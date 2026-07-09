package uz.bozor.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import uz.bozor.entity.enums.CategoryType;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopResponse {
    private Long id;
    private String name;
    private CategoryType categoryType;
    private String categoryDisplayName;
    private String address;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String workingHours;
    private boolean active;
    private String ownerName;
    private String imageUrl;

    // Location qidiruv natijasida qo'shiladi
    private Double distanceKm;
    private String distanceLabel;
    private String googleMapsUrl;
}
