package uz.bozor.dto.response;

import lombok.Builder;
import lombok.Data;
import uz.bozor.entity.enums.Role;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String phone;
    private Role role;
    private boolean active;
    private String deliveryAddress;
    private LocalDateTime createdAt;

    // Sotuvchi bo'lsa do'kon ma'lumotlari
    private Long shopId;
    private String shopName;
}
