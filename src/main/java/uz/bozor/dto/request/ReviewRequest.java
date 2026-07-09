package uz.bozor.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotNull(message = "Mahsulot tanlanishi shart")
    private Long productId;

    private Long orderId;

    @NotNull(message = "Baho kiritilishi shart")
    @Min(value = 1, message = "Baho 1 dan kam bo'lmasligi kerak")
    @Max(value = 5, message = "Baho 5 dan ko'p bo'lmasligi kerak")
    private Integer rating;

    private String comment;

    private String imageUrl;
}
