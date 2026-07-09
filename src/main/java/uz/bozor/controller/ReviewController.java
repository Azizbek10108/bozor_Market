package uz.bozor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.bozor.dto.request.ReviewRequest;
import uz.bozor.dto.response.ApiResponse;
import uz.bozor.dto.response.ReviewResponse;
import uz.bozor.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // ── BUYER: fikr qoldirish ──────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> create(
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Fikr qoldirildi", reviewService.create(request)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> myReviews() {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.getMyReviews()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Fikr o'chirildi", null));
    }

    // ── OCHIQ: mahsulot fikrlarini ko'rish (hamma uchun) ──
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> byProduct(
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.getByProduct(productId)));
    }
}
