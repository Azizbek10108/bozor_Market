package uz.bozor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.bozor.entity.Discount;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    // Do'kon aksiyalari
    List<Discount> findByShopIdAndActiveTrueOrderByCreatedAtDesc(Long shopId);

    // Hozir davom etayotgan barcha aksiyalar
    @Query("""
        SELECT d FROM Discount d
        WHERE d.active = true
        AND d.startsAt <= :now
        AND d.endsAt >= :now
        ORDER BY d.discountValue DESC
    """)
    List<Discount> findActiveDiscounts(@Param("now") LocalDateTime now);

    // Kategoriya bo'yicha aksiyalar
    @Query("""
        SELECT d FROM Discount d
        WHERE d.active = true
        AND d.startsAt <= :now
        AND d.endsAt >= :now
        AND d.product.categoryType = :category
        ORDER BY d.discountValue DESC
    """)
    List<Discount> findActiveDiscountsByCategory(
        @Param("now") LocalDateTime now,
        @Param("category") uz.bozor.entity.enums.CategoryType category
    );

    // Qidiruv bo'yicha aksiyalar
    @Query("""
        SELECT d FROM Discount d
        WHERE d.active = true
        AND d.startsAt <= :now
        AND d.endsAt >= :now
        AND (
            LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(d.product.name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(d.shop.name) LIKE LOWER(CONCAT('%', :query, '%'))
        )
        ORDER BY d.discountValue DESC
    """)
    List<Discount> searchActiveDiscounts(
        @Param("now") LocalDateTime now,
        @Param("query") String query
    );

    // Eng katta chegirmalar (top)
    @Query("""
        SELECT d FROM Discount d
        WHERE d.active = true
        AND d.startsAt <= :now
        AND d.endsAt >= :now
        AND d.discountType = 'PERCENT'
        ORDER BY d.discountValue DESC
    """)
    List<Discount> findTopDiscounts(@Param("now") LocalDateTime now,
        org.springframework.data.domain.Pageable pageable);
}
