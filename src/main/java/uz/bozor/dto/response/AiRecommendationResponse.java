package uz.bozor.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiRecommendationResponse {
    // AI ning erkin matnli javobi (tushuntirish)
    private String answer;

    // AI tavsiya etgan mahsulotlar (mavjud bo'lsa)
    private List<ProductResponse> products;
}
