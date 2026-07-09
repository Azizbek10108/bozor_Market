package uz.bozor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.bozor.entity.Review;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    List<Review> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);

    Optional<Review> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);

    @Query("SELECT r FROM Review r WHERE r.product.shop.id = :shopId ORDER BY r.createdAt DESC")
    List<Review> findByShopId(@Param("shopId") Long shopId);
}
