package uz.bozor.dto.response;

import lombok.Builder;
import lombok.Data;
import uz.bozor.entity.enums.Role;

@Data
@Builder
public class AuthResponse {
    private Long userId;
    private String fullName;
    private String phone;
    private Role role;
    private String accessToken;
    private String refreshToken;
    private ShopResponse shop; // Sotuvchi uchun
}
