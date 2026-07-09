package uz.bozor.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import uz.bozor.entity.User;
import uz.bozor.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Foydalanuvchi topilmadi: " + phone));

        // active tekshiruvini olib tashladik - AuthService da o'zimiz tekshiramiz
        // Shunday qilib Spring Security DisabledException chiqarmaydi
        return new org.springframework.security.core.userdetails.User(
                user.getPhone(),
                user.getPassword(),
                true, // enabled - har doim true
                true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
