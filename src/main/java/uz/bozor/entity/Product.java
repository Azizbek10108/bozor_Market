package uz.bozor.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.bozor.entity.enums.CategoryType;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_type", nullable = false)
    private CategoryType categoryType;

    // Sotish narxi
    @Column(name = "sell_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal sellPrice;

    // Xarid narxi (foyda hisoblash uchun)
    @Column(name = "purchase_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal purchasePrice;

    // O'lchov birligi: dona, kg, metr, litr, qop, rulon
    @Column(name = "unit", nullable = false)
    private String unit;

    // Ombordagi umumiy son
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    // Sotilgan miqdor
    @Column(name = "sold_quantity")
    private Integer soldQuantity = 0;

    @Column(name = "color")
    private String color;

    @Column(name = "brand")
    private String brand;

    @Column(name = "is_active")
    private boolean active = true;

    // Rasm URL (local yoki CDN)
    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Review> reviews;

    // Foyda foizi
    public BigDecimal getProfitMargin() {
        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return sellPrice.subtract(purchasePrice)
                .divide(sellPrice, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    // Ombor qiymati
    public BigDecimal getStockValue() {
        return sellPrice.multiply(BigDecimal.valueOf(stockQuantity));
    }
}
