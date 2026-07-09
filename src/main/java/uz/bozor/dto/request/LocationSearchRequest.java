package uz.bozor.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.bozor.entity.enums.CategoryType;

@Data
public class LocationSearchRequest {

    @NotNull(message = "Kenglik (latitude) kiritilishi shart")
    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private Double latitude;

    @NotNull(message = "Uzunlik (longitude) kiritilishi shart")
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private Double longitude;

    // Qidiruv radiusi (km), default 5 km
    private Double radiusKm = 5.0;

    // Mahsulot nomi bo'yicha qidiruv
    private String query;

    // Kategoriya bo'yicha filter
    private CategoryType category;

    // Natijalar soni (max 50)
    private Integer limit = 20;
}
