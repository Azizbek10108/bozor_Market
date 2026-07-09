package uz.bozor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.bozor.dto.response.ApiResponse;
import uz.bozor.entity.enums.CategoryType;
import uz.bozor.service.ShopService;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class PublicController {

    private final ShopService shopService;

    // Barcha kategoriyalar ro'yxati
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getCategories() {
        List<Map<String, String>> categories = Arrays.stream(CategoryType.values())
                .map(c -> Map.of(
                        "key", c.name(),
                        "label", c.getDisplayName()
                ))
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }

    // Ochiq do'konlar ro'yxati
    @GetMapping("/shops/public")
    public ResponseEntity<ApiResponse<?>> getPublicShops() {
        return ResponseEntity.ok(ApiResponse.ok(shopService.getAllShops()));
    }
}
