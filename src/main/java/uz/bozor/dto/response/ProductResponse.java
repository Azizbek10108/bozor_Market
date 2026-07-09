package uz.bozor.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import uz.bozor.entity.enums.CategoryType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private CategoryType categoryType;
    private String categoryDisplayName;
    private BigDecimal sellPrice;
    private BigDecimal purchasePrice;
    private String unit;
    private Integer stockQuantity;
    private Integer soldQuantity;
    private String color;
    private String brand;
    private BigDecimal profitMargin;
    private BigDecimal stockValue;
    private String stockStatus;

    // Do'kon ma'lumotlari
    private Long shopId;
    private String shopName;
    private String shopAddress;
    private String shopPhone;
    private Double shopLatitude;
    private Double shopLongitude;

    // Location qidiruv natijasida qo'shiladi
    private Double distanceKm;       // foydalanuvchidan masofa
    private String distanceLabel;    // "1.2 km" yoki "800 m"
    private String googleMapsUrl;    // Do'konga navigatsiya linki

    private LocalDateTime createdAt;
    private String imageUrl;

    // Fikr-mulohaza statistikasi
    private Double averageRating;
    private Long reviewCount;
}
