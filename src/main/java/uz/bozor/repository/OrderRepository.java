package uz.bozor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.bozor.entity.Order;
import uz.bozor.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);
    List<Order> findByShopIdOrderByCreatedAtDesc(Long shopId);
    List<Order> findByShopIdAndStatus(Long shopId, OrderStatus status);
    List<Order> findByStatusAndPickupDeadlineBefore(OrderStatus status, LocalDateTime deadline);

    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o
        WHERE o.shop.id = :shopId
        AND o.status = 'COMPLETED'
        AND o.createdAt BETWEEN :from AND :to
    """)
    BigDecimal getTotalRevenue(@Param("shopId") Long shopId,
                               @Param("from") LocalDateTime from,
                               @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.shop.id = :shopId AND o.status = 'PENDING'")
    Long countPendingOrders(@Param("shopId") Long shopId);

    // Admin uchun
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'COMPLETED'")
    BigDecimal getTotalPlatformRevenue();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :from AND :to")
    Long countOrdersBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
