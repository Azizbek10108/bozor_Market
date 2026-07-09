package uz.bozor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.bozor.dto.response.*;
import uz.bozor.entity.enums.Role;
import uz.bozor.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ── DASHBOARD STATISTIKA ───────────────────────────────
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getStats() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getStats()));
    }

    // ── FOYDALANUVCHILAR ───────────────────────────────────
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(required = false) Role role) {
        List<UserResponse> users = role != null
                ? adminService.getUsersByRole(role)
                : adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @PatchMapping("/users/{id}/toggle")
    public ResponseEntity<ApiResponse<UserResponse>> toggleUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Foydalanuvchi holati o'zgartirildi",
                adminService.toggleUserStatus(id)));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.ok("Foydalanuvchi o'chirildi", null));
    }

    // ── DO'KONLAR ──────────────────────────────────────────
    @GetMapping("/shops")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> getAllShops() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getAllShops()));
    }

    @PatchMapping("/shops/{id}/toggle")
    public ResponseEntity<ApiResponse<ShopResponse>> toggleShop(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Do'kon holati o'zgartirildi",
                adminService.toggleShopStatus(id)));
    }

    @DeleteMapping("/shops/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShop(@PathVariable Long id) {
        adminService.deleteShop(id);
        return ResponseEntity.ok(ApiResponse.ok("Do'kon o'chirildi", null));
    }

    // ── BUYURTMALAR ────────────────────────────────────────
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getAllOrders()));
    }
}
