package uz.bozor.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long buyerId;
    private String buyerName;
    private Integer rating;
    private String comment;
    private String imageUrl;
    private LocalDateTime createdAt;
}
