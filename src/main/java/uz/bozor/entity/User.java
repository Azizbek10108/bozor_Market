package uz.bozor.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.bozor.entity.enums.Role;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "is_active")
    private boolean active = true;

    // Oluvchi uchun yetkazib berish manzili
    @Column(name = "delivery_address")
    private String deliveryAddress;

    // Sotuvchi uchun bog'liq do'kon
    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Shop shop;

    // Oluvchi buyurtmalari
    @OneToMany(mappedBy = "buyer", fetch = FetchType.LAZY)
    private List<Order> orders;
}
