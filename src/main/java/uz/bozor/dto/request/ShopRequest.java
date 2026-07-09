package uz.bozor.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.bozor.entity.enums.CategoryType;

@Data
public class ShopRequest {

    @NotBlank(message = "Do'kon nomi kiritilishi shart")
    private String name;

    @NotNull(message = "Kategoriya tanlanishi shart")
    private CategoryType categoryType;

    @NotBlank(message = "Manzil kiritilishi shart")
    private String address;

    private Double latitude;
    private Double longitude;
    private String phone;
    private String workingHours;
    private String imageUrl;
}
