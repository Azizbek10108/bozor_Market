package uz.bozor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.bozor.dto.request.DiscountRequest;
import uz.bozor.dto.response.ApiResponse;
import uz.bozor.dto.response.DiscountResponse;
import uz.bozor.entity.enums.CategoryType;
import uz.bozor.service.DiscountService;

import java.util.List;

@RestController
@RequestMapping("/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    // ── SOTUVCHI (SELLER) ─────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<DiscountResponse>> create(
            @Valid @RequestBody DiscountRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
            "Aksiya yaratildi", discountService.createDiscount(request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.ok(ApiResponse.ok("Aksiya o'chirildi", null));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<List<DiscountResponse>>> getMyDiscounts() {
        return ResponseEntity.ok(ApiResponse.ok(discountService.getMyDiscounts()));
    }

    // ── OCHIQ (PUBLIC) ────────────────────────────────────
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DiscountResponse>>> getActive() {
        return ResponseEntity.ok(ApiResponse.ok(discountService.getActiveDiscounts()));
    }

    @GetMapping("/top")
    public ResponseEntity<ApiResponse<List<DiscountResponse>>> getTop() {
        return ResponseEntity.ok(ApiResponse.ok(discountService.getTopDiscounts()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DiscountResponse>>> search(
            @RequestParam String query) {
        return ResponseEntity.ok(ApiResponse.ok(discountService.searchDiscounts(query)));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<DiscountResponse>>> byCategory(
            @PathVariable CategoryType category) {
        return ResponseEntity.ok(ApiResponse.ok(discountService.getByCategory(category)));
    }
}
