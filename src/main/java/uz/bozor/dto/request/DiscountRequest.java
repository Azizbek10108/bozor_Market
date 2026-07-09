package uz.bozor.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import uz.bozor.entity.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountRequest {

    @NotNull(message = "Mahsulot ID kiritilishi shart")
    private Long productId;

    @NotBlank(message = "Aksiya nomi kiritilishi shart")
    private String title;

    private String description;

    @NotNull(message = "Chegirma turi tanlanishi shart")
    private DiscountType discountType;

    @NotNull(message = "Chegirma miqdori kiritilishi shart")
    @DecimalMin(value = "0.1", message = "Chegirma 0 dan katta bo'lishi kerak")
    private BigDecimal discountValue;

    @NotNull(message = "Boshlanish vaqti kiritilishi shart")
    private LocalDateTime startsAt;

    @NotNull(message = "Tugash vaqti kiritilishi shart")
    private LocalDateTime endsAt;
}
