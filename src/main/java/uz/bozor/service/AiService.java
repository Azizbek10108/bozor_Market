package uz.bozor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.bozor.dto.response.AiRecommendationResponse;
import uz.bozor.dto.response.ProductResponse;
import uz.bozor.entity.Product;
import uz.bozor.exception.BozorException;
import uz.bozor.repository.ProductRepository;
import uz.bozor.repository.ReviewRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final RestTemplate restTemplate;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.groq.api-key}")
    private String apiKey;

    @Value("${app.groq.model}")
    private String model;

    @Value("${app.groq.api-url}")
    private String apiUrl;

    private static final int MAX_PRODUCTS_FOR_CONTEXT = 150;

    public AiRecommendationResponse ask(String question) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BozorException(
                "AI xizmati sozlanmagan. Administrator GROQ_API_KEY ni sozlashi kerak");
        }

        List<Product> activeProducts = productRepository.findAll()
                .stream()
                .filter(Product::isActive)
                .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() > 0)
                .limit(MAX_PRODUCTS_FOR_CONTEXT)
                .toList();

        String productsContext = buildProductsContext(activeProducts);
        String systemPrompt = buildSystemPrompt(productsContext);

        String aiAnswer = callGroq(systemPrompt, question);
        List<Long> productIds = extractProductIds(aiAnswer);
        String cleanAnswer = removeProductIdsTag(aiAnswer);

        List<ProductResponse> matchedProducts = productIds.stream()
                .map(id -> activeProducts.stream()
                        .filter(p -> p.getId().equals(id))
                        .findFirst().orElse(null))
                .filter(Objects::nonNull)
                .map(this::toLightResponse)
                .collect(Collectors.toList());

        return AiRecommendationResponse.builder()
                .answer(cleanAnswer)
                .products(matchedProducts)
                .build();
    }

    private String buildProductsContext(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        for (Product p : products) {
            Double avgRating = reviewRepository.findAverageRatingByProductId(p.getId());
            Long reviewCount = reviewRepository.countByProductId(p.getId());

            sb.append("ID:").append(p.getId())
              .append(" | Nomi: ").append(p.getName())
              .append(" | Brend: ").append(p.getBrand() != null ? p.getBrand() : "noma'lum")
              .append(" | Kategoriya: ").append(p.getCategoryType().getDisplayName())
              .append(" | Narx: ").append(p.getSellPrice()).append(" so'm")
              .append(" | Sotilgan: ").append(p.getSoldQuantity()).append(" ").append(p.getUnit())
              .append(" | Baho: ").append(avgRating != null ? String.format("%.1f", avgRating) : "baho yo'q")
              .append(" (").append(reviewCount).append(" fikr)")
              .append(" | Do'kon: ").append(p.getShop().getName())
              .append("\n");
        }
        return sb.toString();
    }

    private String buildSystemPrompt(String productsContext) {
        return """
            Sen Bozor Market savdo platformasining AI yordamchisisan. O'zbek tilida javob ber.
            Foydalanuvchiga quyidagi mavjud mahsulotlar ro'yxati asosida eng mos mahsulotlarni tavsiya qil.
            Mahsulotni tanlashda brend, narx, sotilgan miqdori (mashhurlik) va o'rtacha bahoni hisobga ol.

            MAVJUD MAHSULOTLAR:
            %s

            QOIDALAR:
            1. Javobni qisqa, tushunarli va do'stona o'zbek tilida yoz (2-4 jumla).
            2. Agar mos mahsulot topilsa, javobing oxirida albatta shu formatda ID larni qo'sh:
               [PRODUCT_IDS: 12,45,7]
            3. Agar mos mahsulot topilmasa, [PRODUCT_IDS:] bo'sh qoldir va buni tushuntir.
            4. Faqat yuqoridagi ro'yxatdagi ID lardan foydalan, o'zingdan ID to'qib chiqarma.
            5. Eng ko'p 5 tagacha mahsulot tavsiya qil, eng mosini birinchi qo'y.
            """.formatted(productsContext);
    }

    private String callGroq(String systemPrompt, String userQuestion) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("temperature", 0.4);
        body.put("max_tokens", 600);
        body.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userQuestion)
        ));

        try {
            var response = restTemplate.postForObject(
                    apiUrl, new HttpEntity<>(body, headers), String.class);
            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("Groq API xatosi: {}", e.getMessage());
            throw new BozorException("AI xizmati javob bermadi. Birozdan keyin urinib ko'ring");
        }
    }

    private List<Long> extractProductIds(String aiAnswer) {
        try {
            int start = aiAnswer.indexOf("[PRODUCT_IDS:");
            if (start == -1) return List.of();
            int end = aiAnswer.indexOf("]", start);
            if (end == -1) return List.of();

            String idsStr = aiAnswer.substring(start + "[PRODUCT_IDS:".length(), end).trim();
            if (idsStr.isBlank()) return List.of();

            return Arrays.stream(idsStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    private String removeProductIdsTag(String aiAnswer) {
        int start = aiAnswer.indexOf("[PRODUCT_IDS:");
        if (start == -1) return aiAnswer.trim();
        int end = aiAnswer.indexOf("]", start);
        if (end == -1) return aiAnswer.trim();
        return (aiAnswer.substring(0, start) + aiAnswer.substring(end + 1)).trim();
    }

    private ProductResponse toLightResponse(Product p) {
        Double avgRating = reviewRepository.findAverageRatingByProductId(p.getId());
        Long reviewCount = reviewRepository.countByProductId(p.getId());

        String stockStatus;
        if (p.getStockQuantity() == 0) stockStatus = "TUGAGAN";
        else if (p.getStockQuantity() < 20) stockStatus = "KAM_QOLDI";
        else stockStatus = "MAVJUD";

        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .categoryType(p.getCategoryType())
                .categoryDisplayName(p.getCategoryType().getDisplayName())
                .sellPrice(p.getSellPrice())
                .unit(p.getUnit())
                .stockQuantity(p.getStockQuantity())
                .soldQuantity(p.getSoldQuantity())
                .brand(p.getBrand())
                .stockStatus(stockStatus)
                .shopId(p.getShop().getId())
                .shopName(p.getShop().getName())
                .shopAddress(p.getShop().getAddress())
                .shopPhone(p.getShop().getPhone())
                .imageUrl(p.getImageUrl())
                .averageRating(avgRating)
                .reviewCount(reviewCount)
                .build();
    }
}
