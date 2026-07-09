package uz.bozor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.bozor.dto.request.OrderRequest;
import uz.bozor.dto.response.*;
import uz.bozor.entity.enums.CategoryType;
import uz.bozor.service.OrderService;
import uz.bozor.service.ProductService;
import uz.bozor.service.ShopService;

import java.util.List;

@RestController
@RequestMapping("/buyer")
@PreAuthorize("hasRole('BUYER')")
@RequiredArgsConstructor
public class BuyerController {

    private final ProductService productService;
    private final OrderService orderService;
    private final ShopService shopService;

    // Mahsulot qidirish
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) CategoryType category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                productService.search(query, category, page, size)));
    }

    // Buyurtma berish
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Buyurtma qabul qilindi! 48 soat ichida oling.",
                orderService.createOrder(request)));
    }

    // Mening buyurtmalarim
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders() {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getMyOrders()));
    }

    // Buyurtmani bekor qilish
    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Buyurtma bekor qilindi",
                orderService.cancelOrder(id)));
    }

    // Barcha do'konlar (joylashuv uchun)
    @GetMapping("/shops")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> getAllShops() {
        return ResponseEntity.ok(ApiResponse.ok(shopService.getAllShops()));
    }
}
