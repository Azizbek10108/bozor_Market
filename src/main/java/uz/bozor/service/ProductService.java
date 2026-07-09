package uz.bozor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.bozor.dto.request.ProductRequest;
import uz.bozor.dto.response.FinanceResponse;
import uz.bozor.dto.response.ProductResponse;
import uz.bozor.entity.Product;
import uz.bozor.entity.Shop;
import uz.bozor.entity.User;
import uz.bozor.entity.enums.CategoryType;
import uz.bozor.exception.BozorException;
import uz.bozor.repository.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;

    // Joriy sotuvchining do'konini olish
    private Shop getCurrentSellerShop() {
        String phone = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BozorException("Foydalanuvchi topilmadi"));
        return shopRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new BozorException("Do'kon topilmadi. Avval do'kon oching"));
    }

    @Transactional
    public ProductResponse addProduct(ProductRequest request) {
        Shop shop = getCurrentSellerShop();

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .categoryType(request.getCategoryType())
                .sellPrice(request.getSellPrice())
                .purchasePrice(request.getPurchasePrice())
                .unit(request.getUnit())
                .stockQuantity(request.getStockQuantity())
                .soldQuantity(0)
                .color(request.getColor())
                .brand(request.getBrand())
                .imageUrl(request.getImageUrl())
                .active(true)
                .shop(shop)
                .build();

        productRepository.save(product);
        return toResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Shop shop = getCurrentSellerShop();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BozorException("Mahsulot topilmadi"));

        if (!product.getShop().getId().equals(shop.getId())) {
            throw new BozorException("Bu mahsulot sizga tegishli emas");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategoryType(request.getCategoryType());
        product.setSellPrice(request.getSellPrice());
        product.setPurchasePrice(request.getPurchasePrice());
        product.setUnit(request.getUnit());
        product.setStockQuantity(request.getStockQuantity());
        product.setColor(request.getColor());
        product.setBrand(request.getBrand());
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Shop shop = getCurrentSellerShop();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BozorException("Mahsulot topilmadi"));

        if (!product.getShop().getId().equals(shop.getId())) {
            throw new BozorException("Bu mahsulot sizga tegishli emas");
        }
        product.setActive(false);
        productRepository.save(product);
    }

    public List<ProductResponse> getMyProducts() {
        Shop shop = getCurrentSellerShop();
        return productRepository.findByShopIdAndActiveTrue(shop.getId())
                .stream().map(this::toResponse).toList();
    }

    // Moliyaviy hisobot
    public FinanceResponse getFinanceReport() {
        Shop shop = getCurrentSellerShop();
        List<Product> products = productRepository.findByShopIdAndActiveTrue(shop.getId());

        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        BigDecimal totalRevenue = orderRepository.getTotalRevenue(shop.getId(), monthStart, now);
        Long pendingOrders = orderRepository.countPendingOrders(shop.getId());

        // Ombor qiymati va xarid narxi hisob
        BigDecimal stockValue = products.stream()
                .map(Product::getStockValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPurchaseCost = products.stream()
                .map(p -> p.getPurchasePrice()
                        .multiply(BigDecimal.valueOf(p.getSoldQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netProfit = totalRevenue.subtract(totalPurchaseCost);

        BigDecimal profitMarginPercent = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? netProfit.divide(totalRevenue, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        long totalItemsSold = products.stream()
                .mapToLong(Product::getSoldQuantity).sum();

        long lossProducts = products.stream()
                .filter(p -> p.getSellPrice().compareTo(p.getPurchasePrice()) < 0)
                .count();

        String topProduct = products.stream()
                .max(java.util.Comparator.comparingInt(Product::getSoldQuantity))
                .map(Product::getName)
                .orElse("—");

        return FinanceResponse.builder()
                .totalRevenue(totalRevenue)
                .totalPurchaseCost(totalPurchaseCost)
                .netProfit(netProfit)
                .profitMarginPercent(profitMarginPercent)
                .stockValue(stockValue)
                .totalItemsSold(totalItemsSold)
                .pendingOrders(pendingOrders)
                .lossProducts(lossProducts)
                .topProduct(topProduct)
                .build();
    }

    // Oluvchi uchun qidiruv
    public Page<ProductResponse> search(String query, CategoryType category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("soldQuantity").descending());

        if (category != null) {
            return productRepository.searchByCategory(query, category, pageable)
                    .map(this::toResponse);
        }
        if (query != null && !query.isBlank()) {
            return productRepository.searchProducts(query, pageable).map(this::toResponse);
        }
        return productRepository.findAll(pageable).map(this::toResponse);
    }

    private ProductResponse toResponse(Product p) {
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
                .purchasePrice(p.getPurchasePrice())
                .unit(p.getUnit())
                .stockQuantity(p.getStockQuantity())
                .soldQuantity(p.getSoldQuantity())
                .color(p.getColor())
                .brand(p.getBrand())
                .profitMargin(p.getProfitMargin())
                .stockValue(p.getStockValue())
                .stockStatus(stockStatus)
                .shopId(p.getShop().getId())
                .shopName(p.getShop().getName())
                .imageUrl(p.getImageUrl())
                .createdAt(p.getCreatedAt())
                .averageRating(reviewRepository.findAverageRatingByProductId(p.getId()))
                .reviewCount(reviewRepository.countByProductId(p.getId()))
                .build();
    }
}
