package uz.bozor.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.bozor.entity.User;
import uz.bozor.entity.enums.Role;
import uz.bozor.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Admin mavjud bo'lmasa yaratish
        if (!userRepository.existsByPhone("+998900000000")) {
            User admin = User.builder()
                    .fullName("Super Admin")
                    .phone("+998900000000")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
            log.info("✅ Admin yaratildi: +998900000000 / admin123");
        } else {
            // Mavjud adminni faollashtirish (bloklangan bo'lishi mumkin)
            User existingAdmin = userRepository.findByPhone("+998900000000").orElse(null);
            if (existingAdmin != null && !existingAdmin.isActive()) {
                existingAdmin.setActive(true);
                userRepository.save(existingAdmin);
                log.info("✅ Admin faollashtirildi");
            } else {
                log.info("ℹ️ Admin allaqachon mavjud va faol");
            }
        }
    }
}
