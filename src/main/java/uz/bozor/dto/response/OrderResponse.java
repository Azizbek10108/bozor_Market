package uz.bozor.dto.response;

import lombok.Builder;
import lombok.Data;
import uz.bozor.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime pickupDeadline;
    private String buyerNote;
    private LocalDateTime createdAt;

    // Mahsulot ma'lumotlari
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private String unit;

    // Do'kon ma'lumotlari
    private Long shopId;
    private String shopName;
    private String shopAddress;
    private Double shopLatitude;
    private Double shopLongitude;
    private String shopPhone;

    // Oluvchi ma'lumotlari
    private String buyerName;
    private String buyerPhone;
    private Boolean hasReview;
}
