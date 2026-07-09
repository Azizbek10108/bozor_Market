package uz.bozor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.bozor.dto.request.LoginRequest;
import uz.bozor.dto.request.RegisterRequest;
import uz.bozor.dto.response.AuthResponse;
import uz.bozor.dto.response.ShopResponse;
import uz.bozor.entity.User;
import uz.bozor.entity.enums.Role;
import uz.bozor.exception.BozorException;
import uz.bozor.repository.ShopRepository;
import uz.bozor.repository.UserRepository;
import uz.bozor.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BozorException("Bu telefon raqam allaqachon ro'yxatdan o'tgan");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.BUYER;

        User user = User.builder()
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .deliveryAddress(request.getDeliveryAddress())
                .active(true)
                .build();

        userRepository.save(user);

        String accessToken = jwtUtil.generateToken(user.getPhone(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getPhone());

        return AuthResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getPhone(), request.getPassword()));
        } catch (org.springframework.security.authentication.DisabledException e) {
            throw new BozorException("Hisobingiz faol emas. Admin bilan bog'laning");
        } catch (BadCredentialsException e) {
            throw new BozorException("Telefon yoki parol noto'g'ri");
        } catch (Exception e) {
            throw new BozorException("Kirish xatoligi: " + e.getMessage());
        }

        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new BozorException("Foydalanuvchi topilmadi"));

        if (!user.isActive()) {
            throw new BozorException("Hisobingiz faol emas");
        }

        String accessToken = jwtUtil.generateToken(user.getPhone(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getPhone());

        // Sotuvchi bo'lsa do'kon ma'lumotlarini ham qaytaramiz
        ShopResponse shopResponse = null;
        if (user.getRole() == Role.SELLER) {
            shopResponse = shopRepository.findByOwnerId(user.getId())
                    .map(shop -> ShopResponse.builder()
                            .id(shop.getId())
                            .name(shop.getName())
                            .categoryType(shop.getCategoryType())
                            .categoryDisplayName(shop.getCategoryType().getDisplayName())
                            .address(shop.getAddress())
                            .latitude(shop.getLatitude())
                            .longitude(shop.getLongitude())
                            .phone(shop.getPhone())
                            .workingHours(shop.getWorkingHours())
                            .active(shop.isActive())
                            .build())
                    .orElse(null);
        }

        return AuthResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .shop(shopResponse)
                .build();
    }
}
