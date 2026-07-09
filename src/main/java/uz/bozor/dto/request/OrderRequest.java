package uz.bozor.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull(message = "Mahsulot ID kiritilishi shart")
    private Long productId;

    @NotNull(message = "Miqdor kiritilishi shart")
    @Min(value = 1, message = "Miqdor 1 dan kichik bo'lmasligi kerak")
    private Integer quantity;

    private String buyerNote;
}
