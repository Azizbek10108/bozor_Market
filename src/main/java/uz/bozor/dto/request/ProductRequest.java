package uz.bozor.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import uz.bozor.entity.enums.CategoryType;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "Mahsulot nomi kiritilishi shart")
    private String name;

    private String description;

    @NotNull(message = "Kategoriya tanlanishi shart")
    private CategoryType categoryType;

    @NotNull(message = "Sotish narxi kiritilishi shart")
    @DecimalMin(value = "0.0", inclusive = false, message = "Narx 0 dan katta bo'lishi kerak")
    private BigDecimal sellPrice;

    @NotNull(message = "Xarid narxi kiritilishi shart")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal purchasePrice;

    @NotBlank(message = "O'lchov birligi kiritilishi shart")
    private String unit;

    @NotNull(message = "Miqdor kiritilishi shart")
    @Min(value = 0, message = "Miqdor 0 dan kichik bo'lmasligi kerak")
    private Integer stockQuantity;

    private String color;
    private String brand;

    // Rasm URL (yuklangandan keyin to'ldiriladi)
    private String imageUrl;
}
