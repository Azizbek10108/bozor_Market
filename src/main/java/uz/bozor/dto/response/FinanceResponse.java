package uz.bozor.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FinanceResponse {
    // Kirim
    private BigDecimal totalRevenue;
    // Xarid xarajatlari
    private BigDecimal totalPurchaseCost;
    // Sof foyda
    private BigDecimal netProfit;
    // Foyda foizi
    private BigDecimal profitMarginPercent;
    // Ombor qiymati
    private BigDecimal stockValue;
    // Sotilgan mahsulotlar soni
    private Long totalItemsSold;
    // Buyurtmalar soni
    private Long totalOrders;
    private Long pendingOrders;
    // Zarar ko'rgan mahsulotlar
    private Long lossProducts;
    // Eng ko'p sotiladigan mahsulot
    private String topProduct;
}
