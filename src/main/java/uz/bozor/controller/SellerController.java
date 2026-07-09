package uz.bozor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.bozor.dto.request.ProductRequest;
import uz.bozor.dto.request.ShopRequest;
import uz.bozor.dto.response.*;
import uz.bozor.service.OrderService;
import uz.bozor.service.ProductService;
import uz.bozor.service.ShopService;

import java.util.List;

@RestController
@RequestMapping("/seller")
@PreAuthorize("hasRole('SELLER')")
@RequiredArgsConstructor
public class SellerController {

    private final ShopService shopService;
    private final ProductService productService;
    private final OrderService orderService;

    // ─── DO'KON ───────────────────────────────────────────
    @PostMapping("/shop")
    public ResponseEntity<ApiResponse<ShopResponse>> createShop(
            @Valid @RequestBody ShopRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(shopService.createShop(request)));
    }

    @PutMapping("/shop")
    public ResponseEntity<ApiResponse<ShopResponse>> updateShop(
            @Valid @RequestBody ShopRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(shopService.updateShop(request)));
    }

    @GetMapping("/shop")
    public ResponseEntity<ApiResponse<ShopResponse>> getMyShop() {
        return ResponseEntity.ok(ApiResponse.ok(shopService.getMyShop()));
    }

    // ─── MAHSULOTLAR ───────────────────────────────────────
    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Mahsulot qo'shildi", productService.addProduct(request)));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Mahsulot yangilandi", productService.updateProduct(id, request)));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok("Mahsulot o'chirildi", null));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getMyProducts() {
        return ResponseEntity.ok(ApiResponse.ok(productService.getMyProducts()));
    }

    // ─── MOLIYAVIY HISOBOT ────────────────────────────────
    @GetMapping("/finance")
    public ResponseEntity<ApiResponse<FinanceResponse>> getFinanceReport() {
        return ResponseEntity.ok(ApiResponse.ok(productService.getFinanceReport()));
    }

    // ─── BUYURTMALAR ─────────────────────────────────────
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders(
            @RequestParam Long shopId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getShopOrders(shopId)));
    }

    @PostMapping("/orders/{id}/confirm")
    public ResponseEntity<ApiResponse<OrderResponse>> confirmOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Buyurtma tasdiqlandi", orderService.confirmOrder(id)));
    }

    @PostMapping("/orders/{id}/complete")
    public ResponseEntity<ApiResponse<OrderResponse>> completeOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Buyurtma bajarildi", orderService.completeOrder(id)));
    }

    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Buyurtma bekor qilindi", orderService.cancelOrder(id)));
    }
}
