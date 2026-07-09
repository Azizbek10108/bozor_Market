package uz.bozor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.bozor.entity.User;
import uz.bozor.entity.enums.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);

    List<User> findByRole(Role role);
    List<User> findByActiveTrue();

    long countByRole(Role role);
    long countByActiveTrue();
    long countByCreatedAtAfter(LocalDateTime after);
}
