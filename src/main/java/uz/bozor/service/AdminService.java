package uz.bozor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.bozor.dto.response.*;
import uz.bozor.entity.Shop;
import uz.bozor.entity.User;
import uz.bozor.entity.enums.OrderStatus;
import uz.bozor.entity.enums.Role;
import uz.bozor.exception.BozorException;
import uz.bozor.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    // ── STATISTIKA ─────────────────────────────────────────
    public AdminStatsResponse getStats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        return AdminStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalSellers(userRepository.countByRole(Role.SELLER))
                .totalBuyers(userRepository.countByRole(Role.BUYER))
                .activeUsers(userRepository.countByActiveTrue())
                .totalShops((long) shopRepository.findAll().size())
                .activeShops((long) shopRepository.findByActiveTrue().size())
                .totalProducts(productRepository.count())
                .activeProducts(productRepository.countByActiveTrue())
                .totalOrders(orderRepository.count())
                .pendingOrders(orderRepository.countByStatus(OrderStatus.PENDING))
                .completedOrders(orderRepository.countByStatus(OrderStatus.COMPLETED))
                .cancelledOrders(orderRepository.countByStatus(OrderStatus.CANCELLED))
                .expiredOrders(orderRepository.countByStatus(OrderStatus.EXPIRED))
                .totalPlatformRevenue(orderRepository.getTotalPlatformRevenue())
                .todayOrders(orderRepository.countOrdersBetween(todayStart, LocalDateTime.now()))
                .todayNewUsers(userRepository.countByCreatedAtAfter(todayStart))
                .build();
    }

    // ── FOYDALANUVCHILAR ───────────────────────────────────
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .toList();
    }

    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::toUserResponse)
                .toList();
    }

    @Transactional
    public UserResponse toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BozorException("Foydalanuvchi topilmadi"));
        if (user.getRole() == Role.ADMIN) {
            throw new BozorException("Admin hisobini bloklash mumkin emas");
        }
        user.setActive(!user.isActive());
        return toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BozorException("Foydalanuvchi topilmadi"));
        if (user.getRole() == Role.ADMIN) {
            throw new BozorException("Admin hisobini o'chirish mumkin emas");
        }
        userRepository.delete(user);
    }

    // ── DO'KONLAR ──────────────────────────────────────────
    public List<ShopResponse> getAllShops() {
        return shopRepository.findAll().stream()
                .map(this::toShopResponse)
                .toList();
    }

    @Transactional
    public ShopResponse toggleShopStatus(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new BozorException("Do'kon topilmadi"));
        shop.setActive(!shop.isActive());
        return toShopResponse(shopRepository.save(shop));
    }

    @Transactional
    public void deleteShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new BozorException("Do'kon topilmadi"));
        shopRepository.delete(shop);
    }

    // ── BUYURTMALAR ────────────────────────────────────────
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(o -> {
                    if (o.getItems() == null || o.getItems().isEmpty()) return null;
                    var item = o.getItems().get(0);
                    return OrderResponse.builder()
                            .id(o.getId())
                            .status(o.getStatus())
                            .totalAmount(o.getTotalAmount())
                            .pickupDeadline(o.getPickupDeadline())
                            .createdAt(o.getCreatedAt())
                            .productName(item.getProduct().getName())
                            .quantity(item.getQuantity())
                            .priceAtPurchase(item.getPriceAtPurchase())
                            .shopName(o.getShop().getName())
                            .shopAddress(o.getShop().getAddress())
                            .buyerName(o.getBuyer().getFullName())
                            .buyerPhone(o.getBuyer().getPhone())
                            .build();
                })
                .filter(o -> o != null)
                .toList();
    }

    // ── HELPER METHODLAR ───────────────────────────────────
    private UserResponse toUserResponse(User u) {
        UserResponse.UserResponseBuilder builder = UserResponse.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .phone(u.getPhone())
                .role(u.getRole())
                .active(u.isActive())
                .deliveryAddress(u.getDeliveryAddress())
                .createdAt(u.getCreatedAt());

        if (u.getShop() != null) {
            builder.shopId(u.getShop().getId())
                   .shopName(u.getShop().getName());
        }
        return builder.build();
    }

    private ShopResponse toShopResponse(Shop s) {
        return ShopResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .categoryType(s.getCategoryType())
                .categoryDisplayName(s.getCategoryType().getDisplayName())
                .address(s.getAddress())
                .latitude(s.getLatitude())
                .longitude(s.getLongitude())
                .phone(s.getPhone())
                .workingHours(s.getWorkingHours())
                .active(s.isActive())
                .ownerName(s.getOwner().getFullName())
                .imageUrl(s.getImageUrl())
                .build();
    }
}
