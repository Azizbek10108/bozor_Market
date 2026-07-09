package uz.bozor.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import uz.bozor.entity.enums.Role;

@Data
public class RegisterRequest {

    @NotBlank(message = "Ism kiritilishi shart")
    private String fullName;

    @NotBlank(message = "Telefon raqam kiritilishi shart")
    @Pattern(regexp = "^\\+998[0-9]{9}$", message = "Telefon format: +998XXXXXXXXX")
    private String phone;

    @NotBlank(message = "Parol kiritilishi shart")
    @Size(min = 6, message = "Parol kamida 6 ta belgidan iborat bo'lishi kerak")
    private String password;

    private Role role;

    // Oluvchi uchun
    private String deliveryAddress;
}
