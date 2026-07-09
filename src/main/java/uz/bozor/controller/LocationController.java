package uz.bozor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.bozor.dto.request.LocationSearchRequest;
import uz.bozor.dto.response.ApiResponse;
import uz.bozor.dto.response.ProductResponse;
import uz.bozor.dto.response.ShopResponse;
import uz.bozor.entity.enums.CategoryType;
import uz.bozor.service.LocationService;

import java.util.List;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    /**
     * Yaqin do'konlarni topish
     * GET /api/location/shops?lat=41.2995&lon=69.2401&radiusKm=3
     */
    @GetMapping("/shops")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> nearbyShops(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "5.0") double radiusKm,
            @RequestParam(required = false) CategoryType category,
            @RequestParam(defaultValue = "20") int limit) {

        LocationSearchRequest req = new LocationSearchRequest();
        req.setLatitude(lat);
        req.setLongitude(lon);
        req.setRadiusKm(radiusKm);
        req.setCategory(category);
        req.setLimit(limit);

        List<ShopResponse> shops = locationService.findNearbyShops(req);
        return ResponseEntity.ok(ApiResponse.ok(
                shops.size() + " ta do'kon topildi (" + radiusKm + " km ichida)", shops));
    }

    /**
     * Yaqin do'konlardagi mahsulotlarni qidirish
     * GET /api/location/products?lat=41.2995&lon=69.2401&query=sement&radiusKm=5
     */
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> nearbyProducts(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "5.0") double radiusKm,
            @RequestParam(required = false) CategoryType category,
            @RequestParam(defaultValue = "20") int limit) {

        LocationSearchRequest req = new LocationSearchRequest();
        req.setLatitude(lat);
        req.setLongitude(lon);
        req.setQuery(query);
        req.setRadiusKm(radiusKm);
        req.setCategory(category);
        req.setLimit(limit);

        List<ProductResponse> products = locationService.searchNearbyProducts(req);
        return ResponseEntity.ok(ApiResponse.ok(
                products.size() + " ta mahsulot topildi", products));
    }

    /**
     * Kategoriya bo'yicha eng arzon + yaqin mahsulotlar (AI tavsiya uchun)
     * GET /api/location/cheapest?lat=41.2995&lon=69.2401&category=QURILISH_MATERIALLARI
     */
    @GetMapping("/cheapest")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> cheapestNearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam CategoryType category,
            @RequestParam(defaultValue = "10.0") double radiusKm,
            @RequestParam(defaultValue = "10") int limit) {

        List<ProductResponse> products = locationService.findCheapestNearby(
                category, lat, lon, radiusKm, limit);
        return ResponseEntity.ok(ApiResponse.ok(
                "Atrofingizda eng arzon " + category.getDisplayName(), products));
    }

    /**
     * Eng yaqin do'konni topish
     * GET /api/location/nearest-shop?lat=41.2995&lon=69.2401
     */
    @GetMapping("/nearest-shop")
    public ResponseEntity<ApiResponse<ShopResponse>> nearestShop(
            @RequestParam double lat,
            @RequestParam double lon) {

        ShopResponse shop = locationService.findNearestShop(lat, lon);
        return ResponseEntity.ok(ApiResponse.ok(
                "Eng yaqin do'kon: " + shop.getDistanceLabel(), shop));
    }

    /**
     * To'liq qidiruv (body orqali)
     * POST /api/location/search
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchByLocation(
            @Valid @RequestBody LocationSearchRequest request) {

        List<ProductResponse> products = locationService.searchNearbyProducts(request);
        return ResponseEntity.ok(ApiResponse.ok(
                products.size() + " ta mahsulot topildi ("
                        + request.getRadiusKm() + " km ichida)", products));
    }
}
