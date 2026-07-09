package uz.bozor.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.bozor.entity.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "discounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discount extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    // PERCENT yoki FIXED
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    // Chegirma miqdori: 20 (%) yoki 5000 (so'm)
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    // Asl narx
    @Column(name = "original_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal originalPrice;

    // Chegirmali narx
    @Column(name = "discounted_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountedPrice;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    @Column(name = "is_active")
    private boolean active = true;

    // Aksiya hali davom etayaptimi
    public boolean isOngoing() {
        LocalDateTime now = LocalDateTime.now();
        return active && now.isAfter(startsAt) && now.isBefore(endsAt);
    }

    // Qolgan vaqt (soatlarda)
    public long hoursLeft() {
        return java.time.Duration.between(LocalDateTime.now(), endsAt).toHours();
    }
}
