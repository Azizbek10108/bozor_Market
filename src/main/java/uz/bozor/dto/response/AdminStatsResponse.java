package uz.bozor.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AdminStatsResponse {
    // Foydalanuvchilar
    private Long totalUsers;
    private Long totalSellers;
    private Long totalBuyers;
    private Long activeUsers;

    // Do'konlar
    private Long totalShops;
    private Long activeShops;

    // Mahsulotlar
    private Long totalProducts;
    private Long activeProducts;

    // Buyurtmalar
    private Long totalOrders;
    private Long pendingOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    private Long expiredOrders;

    // Moliya
    private BigDecimal totalPlatformRevenue;

    // Bugungi
    private Long todayOrders;
    private Long todayNewUsers;
}
