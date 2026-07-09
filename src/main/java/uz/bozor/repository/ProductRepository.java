package uz.bozor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.bozor.entity.Product;
import uz.bozor.entity.enums.CategoryType;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByShopIdAndActiveTrue(Long shopId);
    Page<Product> findByShopIdAndActiveTrue(Long shopId, Pageable pageable);

    // Matn bo'yicha qidiruv
    @Query("""
        SELECT p FROM Product p
        WHERE p.active = true
        AND (
            LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(p.color) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%'))
        )
        ORDER BY p.soldQuantity DESC
    """)
    Page<Product> searchProducts(@Param("query") String query, Pageable pageable);

    // Kategoriya + matn qidiruv
    @Query("""
        SELECT p FROM Product p
        WHERE p.active = true
        AND p.categoryType = :category
        AND (
            LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))
        )
        ORDER BY p.sellPrice ASC
    """)
    Page<Product> searchByCategory(
            @Param("query") String query,
            @Param("category") CategoryType category,
            Pageable pageable
    );

    // Location bo'yicha qidiruv: yaqin do'konlardagi mahsulotlar, narx bo'yicha tartiblash
    @Query(value = """
        SELECT p.* FROM products p
        JOIN shops s ON p.shop_id = s.id
        WHERE p.is_active = true
          AND p.stock_quantity > 0
          AND s.is_active = true
          AND s.latitude IS NOT NULL
          AND s.longitude IS NOT NULL
          AND (
              LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(p.color) LIKE LOWER(CONCAT('%', :query, '%'))
          )
          AND (
              6371 * acos(
                  cos(radians(:lat)) * cos(radians(s.latitude))
                  * cos(radians(s.longitude) - radians(:lon))
                  + sin(radians(:lat)) * sin(radians(s.latitude))
              )
          ) <= :radiusKm
        ORDER BY p.sell_price ASC
        LIMIT :limitCount
        """, nativeQuery = true)
    List<Product> searchNearbyProducts(
            @Param("query") String query,
            @Param("lat") double latitude,
            @Param("lon") double longitude,
            @Param("radiusKm") double radiusKm,
            @Param("limitCount") int limit
    );

    // Kategoriya + location bo'yicha eng arzon mahsulot topish
    @Query(value = """
        SELECT p.* FROM products p
        JOIN shops s ON p.shop_id = s.id
        WHERE p.is_active = true
          AND p.stock_quantity > 0
          AND p.category_type = :category
          AND s.is_active = true
          AND s.latitude IS NOT NULL
          AND s.longitude IS NOT NULL
          AND (
              6371 * acos(
                  cos(radians(:lat)) * cos(radians(s.latitude))
                  * cos(radians(s.longitude) - radians(:lon))
                  + sin(radians(:lat)) * sin(radians(s.latitude))
              )
          ) <= :radiusKm
        ORDER BY p.sell_price ASC
        LIMIT :limitCount
        """, nativeQuery = true)
    List<Product> findCheapestNearby(
            @Param("category") String category,
            @Param("lat") double latitude,
            @Param("lon") double longitude,
            @Param("radiusKm") double radiusKm,
            @Param("limitCount") int limit
    );

    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.soldQuantity DESC")
    List<Product> findTopSelling(Pageable pageable);

    long countByActiveTrue();

    Page<Product> findByCategoryTypeAndActiveTrue(CategoryType categoryType, Pageable pageable);
}
