package uz.bozor.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.bozor.entity.enums.CategoryType;

import java.util.List;

@Entity
@Table(name = "shops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_type", nullable = false)
    private CategoryType categoryType;

    @Column(name = "address", nullable = false)
    private String address;

    // Google Maps koordinatalar
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "phone")
    private String phone;

    @Column(name = "working_hours")
    private String workingHours;

    @Column(name = "is_active")
    private boolean active = true;

    // Do'kon banneri / rasmi
    @Column(name = "image_url")
    private String imageUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;
}
