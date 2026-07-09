package uz.bozor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.bozor.dto.request.DiscountRequest;
import uz.bozor.dto.response.DiscountResponse;
import uz.bozor.entity.*;
import uz.bozor.entity.enums.CategoryType;
import uz.bozor.exception.BozorException;
import uz.bozor.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final ProductRepository  productRepository;
    private final ShopRepository     shopRepository;
    private final UserRepository     userRepository;

    // ── SOTUVCHI ─────────────────────────────────────────────
    @Transactional
    public DiscountResponse createDiscount(DiscountRequest req) {
        Shop shop = getSellerShop();
        Product product = productRepository.findById(req.getProductId())
            .orElseThrow(() -> new BozorException("Mahsulot topilmadi"));

        if (!product.getShop().getId().equals(shop.getId())) {
            throw new BozorException("Bu mahsulot sizga tegishli emas");
        }
        if (req.getEndsAt().isBefore(req.getStartsAt())) {
            throw new BozorException("Tugash vaqti boshlanish vaqtidan keyin bo'lishi kerak");
        }
        if (req.getEndsAt().isBefore(LocalDateTime.now())) {
            throw new BozorException("Tugash vaqti o'tib ketgan");
        }

        BigDecimal discountedPrice = calcDiscountedPrice(
            product.getSellPrice(), req.getDiscountType().name(), req.getDiscountValue());

        if (discountedPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BozorException("Chegirmali narx 0 dan katta bo'lishi kerak");
        }

        Discount discount = Discount.builder()
            .product(product)
            .shop(shop)
            .title(req.getTitle())
            .description(req.getDescription())
            .discountType(req.getDiscountType())
            .discountValue(req.getDiscountValue())
            .originalPrice(product.getSellPrice())
            .discountedPrice(discountedPrice)
            .startsAt(req.getStartsAt())
            .endsAt(req.getEndsAt())
            .active(true)
            .build();

        return toResponse(discountRepository.save(discount));
    }

    @Transactional
    public void deleteDiscount(Long id) {
        Shop shop = getSellerShop();
        Discount d = discountRepository.findById(id)
            .orElseThrow(() -> new BozorException("Aksiya topilmadi"));
        if (!d.getShop().getId().equals(shop.getId())) {
            throw new BozorException("Bu aksiya sizga tegishli emas");
        }
        d.setActive(false);
        discountRepository.save(d);
    }

    public List<DiscountResponse> getMyDiscounts() {
        Shop shop = getSellerShop();
        return discountRepository
            .findByShopIdAndActiveTrueOrderByCreatedAtDesc(shop.getId())
            .stream().map(this::toResponse).toList();
    }

    // ── OLUVCHI / PUBLIC ─────────────────────────────────────
    public List<DiscountResponse> getActiveDiscounts() {
        return discountRepository.findActiveDiscounts(LocalDateTime.now())
            .stream().map(this::toResponse).toList();
    }

    public List<DiscountResponse> getTopDiscounts() {
        return discountRepository.findTopDiscounts(
            LocalDateTime.now(), PageRequest.of(0, 10))
            .stream().map(this::toResponse).toList();
    }

    public List<DiscountResponse> searchDiscounts(String query) {
        return discountRepository.searchActiveDiscounts(LocalDateTime.now(), query)
            .stream().map(this::toResponse).toList();
    }

    public List<DiscountResponse> getByCategory(CategoryType category) {
        return discountRepository.findActiveDiscountsByCategory(LocalDateTime.now(), category)
            .stream().map(this::toResponse).toList();
    }

    // ── HELPER ──────────────────────────────────────────────
    private Shop getSellerShop() {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new BozorException("Foydalanuvchi topilmadi"));
        return shopRepository.findByOwnerId(user.getId())
            .orElseThrow(() -> new BozorException("Do'kon topilmadi"));
    }

    private BigDecimal calcDiscountedPrice(BigDecimal price, String type, BigDecimal value) {
        if ("PERCENT".equals(type)) {
            BigDecimal discount = price.multiply(value)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            return price.subtract(discount);
        } else {
            return price.subtract(value).max(BigDecimal.ZERO);
        }
    }

    private DiscountResponse toResponse(Discount d) {
        BigDecimal saved = d.getOriginalPrice().subtract(d.getDiscountedPrice());

        String label = d.getDiscountType().name().equals("PERCENT")
            ? d.getDiscountValue().intValue() + "% chegirma"
            : formatPrice(d.getDiscountValue()) + " chegirma";

        Shop s = d.getShop();
        Product p = d.getProduct();

        return DiscountResponse.builder()
            .id(d.getId())
            .title(d.getTitle())
            .description(d.getDescription())
            .discountType(d.getDiscountType())
            .discountValue(d.getDiscountValue())
            .discountLabel(label)
            .originalPrice(d.getOriginalPrice())
            .discountedPrice(d.getDiscountedPrice())
            .savedAmount(saved)
            .startsAt(d.getStartsAt())
            .endsAt(d.getEndsAt())
            .hoursLeft(Math.max(0, d.hoursLeft()))
            .ongoing(d.isOngoing())
            .productId(p.getId())
            .productName(p.getName())
            .productUnit(p.getUnit())
            .productImageUrl(p.getImageUrl())
            .categoryType(p.getCategoryType())
            .categoryDisplayName(p.getCategoryType().getDisplayName())
            .stockQuantity(p.getStockQuantity())
            .shopId(s.getId())
            .shopName(s.getName())
            .shopAddress(s.getAddress())
            .shopLatitude(s.getLatitude())
            .shopLongitude(s.getLongitude())
            .shopPhone(s.getPhone())
            .shopImageUrl(s.getImageUrl())
            .build();
    }

    private String formatPrice(BigDecimal price) {
        return String.format("%,.0f so'm", price).replace(",", " ");
    }
}
