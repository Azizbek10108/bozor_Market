package uz.bozor.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import uz.bozor.entity.enums.CategoryType;
import uz.bozor.entity.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountResponse {
    private Long id;

    // Aksiya
    private String title;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;    // 20 (%) yoki 5000 (so'm)
    private String discountLabel;        // "20% chegirma" yoki "5 000 so'm chegirma"

    // Narxlar
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private BigDecimal savedAmount;      // Tejash miqdori

    // Vaqt
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private long hoursLeft;             // Qolgan soat
    private boolean ongoing;

    // Mahsulot
    private Long productId;
    private String productName;
    private String productUnit;
    private String productImageUrl;
    private CategoryType categoryType;
    private String categoryDisplayName;
    private Integer stockQuantity;

    // Do'kon
    private Long shopId;
    private String shopName;
    private String shopAddress;
    private Double shopLatitude;
    private Double shopLongitude;
    private String shopPhone;
    private String shopImageUrl;
}
