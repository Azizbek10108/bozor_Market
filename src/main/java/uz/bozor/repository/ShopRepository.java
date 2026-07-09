package uz.bozor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.bozor.entity.Shop;
import uz.bozor.entity.enums.CategoryType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    Optional<Shop> findByOwnerId(Long ownerId);
    List<Shop> findByCategoryTypeAndActiveTrue(CategoryType categoryType);
    List<Shop> findByActiveTrue();

    // Haversine formula: berilgan nuqtadan radius (km) ichidagi do'konlar
    // 6371 = Yer radiusi (km)
    @Query(value = """
        SELECT s.*, (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(s.latitude))
                * cos(radians(s.longitude) - radians(:lon))
                + sin(radians(:lat)) * sin(radians(s.latitude))
            )
        ) AS distance
        FROM shops s
        WHERE s.is_active = true
          AND s.latitude IS NOT NULL
          AND s.longitude IS NOT NULL
        HAVING distance <= :radiusKm
        ORDER BY distance ASC
        LIMIT :limitCount
        """, nativeQuery = true)
    List<Shop> findNearbyShops(
            @Param("lat") double latitude,
            @Param("lon") double longitude,
            @Param("radiusKm") double radiusKm,
            @Param("limitCount") int limit
    );

    // Kategoriya + radius bo'yicha
    @Query(value = """
        SELECT s.*, (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(s.latitude))
                * cos(radians(s.longitude) - radians(:lon))
                + sin(radians(:lat)) * sin(radians(s.latitude))
            )
        ) AS distance
        FROM shops s
        WHERE s.is_active = true
          AND s.category_type = :category
          AND s.latitude IS NOT NULL
          AND s.longitude IS NOT NULL
        HAVING distance <= :radiusKm
        ORDER BY distance ASC
        """, nativeQuery = true)
    List<Shop> findNearbyShopsByCategory(
            @Param("lat") double latitude,
            @Param("lon") double longitude,
            @Param("radiusKm") double radiusKm,
            @Param("category") String category
    );

    // Eng yaqin do'konni topish
    @Query(value = """
        SELECT s.*, (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(s.latitude))
                * cos(radians(s.longitude) - radians(:lon))
                + sin(radians(:lat)) * sin(radians(s.latitude))
            )
        ) AS distance
        FROM shops s
        WHERE s.is_active = true
          AND s.latitude IS NOT NULL
          AND s.longitude IS NOT NULL
        ORDER BY distance ASC
        LIMIT 1
        """, nativeQuery = true)
    Optional<Shop> findNearestShop(
            @Param("lat") double latitude,
            @Param("lon") double longitude
    );
}
