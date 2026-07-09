package uz.bozor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.bozor.dto.request.OrderRequest;
import uz.bozor.dto.response.OrderResponse;
import uz.bozor.entity.*;
import uz.bozor.entity.enums.OrderStatus;
import uz.bozor.exception.BozorException;
import uz.bozor.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        String phone = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User buyer = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BozorException("Foydalanuvchi topilmadi"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BozorException("Mahsulot topilmadi"));

        if (!product.isActive()) {
            throw new BozorException("Bu mahsulot mavjud emas");
        }
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BozorException("Omborda yetarli mahsulot yo'q. Mavjud: "
                    + product.getStockQuantity() + " " + product.getUnit());
        }

        BigDecimal totalAmount = product.getSellPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        // 48 soatlik muddat
        LocalDateTime deadline = LocalDateTime.now().plusHours(48);

        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(request.getQuantity())
                .priceAtPurchase(product.getSellPrice())
                .build();

        Order order = Order.builder()
                .buyer(buyer)
                .shop(product.getShop())
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .pickupDeadline(deadline)
                .buyerNote(request.getBuyerNote())
                .build();

        order = orderRepository.save(order);
        item.setOrder(order);
        order.setItems(List.of(item));

        // Omborda miqdorni kamaytirish (rezerv)
        product.setStockQuantity(product.getStockQuantity() - request.getQuantity());
        productRepository.save(product);

        return toResponse(order, item, product);
    }

    @Transactional
    public OrderResponse confirmOrder(Long orderId) {
        Order order = getOrderForSeller(orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BozorException("Bu buyurtma allaqachon " + order.getStatus());
        }
        order.setStatus(OrderStatus.CONFIRMED);

        // Sotilgan miqdorni oshirish
        order.getItems().forEach(item -> {
            Product p = item.getProduct();
            p.setSoldQuantity(p.getSoldQuantity() + item.getQuantity());
            productRepository.save(p);
        });

        return toResponse(orderRepository.save(order),
                order.getItems().get(0),
                order.getItems().get(0).getProduct());
    }

    @Transactional
    public OrderResponse completeOrder(Long orderId) {
        Order order = getOrderForSeller(orderId);
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BozorException("Buyurtma avval tasdiqlanishi kerak");
        }
        order.setStatus(OrderStatus.COMPLETED);
        return toResponse(orderRepository.save(order),
                order.getItems().get(0),
                order.getItems().get(0).getProduct());
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BozorException("Buyurtma topilmadi"));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new BozorException("Bajarilgan buyurtmani bekor qilib bo'lmaydi");
        }

        // Ombonga qaytarish
        order.getItems().forEach(item -> {
            Product p = item.getProduct();
            p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
            productRepository.save(p);
        });

        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order),
                order.getItems().get(0),
                order.getItems().get(0).getProduct());
    }

    // Har soatda 48 soat o'tgan buyurtmalarni EXPIRED qilish
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void expireOldOrders() {
        List<Order> expired = orderRepository.findByStatusAndPickupDeadlineBefore(
                OrderStatus.PENDING, LocalDateTime.now());

        expired.forEach(order -> {
            order.setStatus(OrderStatus.EXPIRED);
            // Ombonga qaytarish
            order.getItems().forEach(item -> {
                Product p = item.getProduct();
                p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
                productRepository.save(p);
            });
            orderRepository.save(order);
        });
    }

    public List<OrderResponse> getMyOrders() {
        String phone = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User buyer = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BozorException("Foydalanuvchi topilmadi"));

        return orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyer.getId())
                .stream()
                .map(o -> toResponse(o, o.getItems().get(0), o.getItems().get(0).getProduct()))
                .toList();
    }

    public List<OrderResponse> getShopOrders(Long shopId) {
        return orderRepository.findByShopIdOrderByCreatedAtDesc(shopId)
                .stream()
                .map(o -> toResponse(o, o.getItems().get(0), o.getItems().get(0).getProduct()))
                .toList();
    }

    private Order getOrderForSeller(Long orderId) {
        String phone = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User seller = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BozorException("Foydalanuvchi topilmadi"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BozorException("Buyurtma topilmadi"));
        if (!order.getShop().getOwner().getId().equals(seller.getId())) {
            throw new BozorException("Bu buyurtma sizning do'koningizga tegishli emas");
        }
        return order;
    }

    private OrderResponse toResponse(Order o, OrderItem item, Product product) {
        return OrderResponse.builder()
                .id(o.getId())
                .status(o.getStatus())
                .totalAmount(o.getTotalAmount())
                .pickupDeadline(o.getPickupDeadline())
                .buyerNote(o.getBuyerNote())
                .createdAt(o.getCreatedAt())
                .productId(product.getId())
                .productName(product.getName())
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .unit(product.getUnit())
                .shopId(o.getShop().getId())
                .shopName(o.getShop().getName())
                .shopAddress(o.getShop().getAddress())
                .shopLatitude(o.getShop().getLatitude())
                .shopLongitude(o.getShop().getLongitude())
                .shopPhone(o.getShop().getPhone())
                .buyerName(o.getBuyer().getFullName())
                .buyerPhone(o.getBuyer().getPhone())
                .hasReview(reviewRepository.existsByOrderId(o.getId()))
                .build();
    }
}
