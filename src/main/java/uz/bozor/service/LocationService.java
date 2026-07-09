package uz.bozor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.bozor.dto.request.LocationSearchRequest;
import uz.bozor.dto.response.ProductResponse;
import uz.bozor.dto.response.ShopResponse;
import uz.bozor.entity.Product;
import uz.bozor.entity.Shop;
import uz.bozor.entity.enums.CategoryType;
import uz.bozor.exception.BozorException;
import uz.bozor.repository.ProductRepository;
import uz.bozor.repository.ShopRepository;
import uz.bozor.util.LocationUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    // 1. Yaqin do'konlarni topish
    public List<ShopResponse> findNearbyShops(LocationSearchRequest req) {
        validateCoords(req.getLatitude(), req.getLongitude());

        double radius = req.getRadiusKm() != null ? req.getRadiusKm() : 5.0;
        int limit = req.getLimit() != null ? Math.min(req.getLimit(), 50) : 20;

        List<Shop> shops;
        if (req.getCategory() != null) {
            shops = shopRepository.findNearbyShopsByCategory(
                    req.getLatitude(), req.getLongitude(),
                    radius, req.getCategory().name());
        } else {
            shops = shopRepository.findNearbyShops(
                    req.getLatitude(), req.getLongitude(), radius, limit);
        }

        return shops.stream()
                .map(s -> toShopResponse(s, req.getLatitude(), req.getLongitude()))
                .toList();
    }

    // 2. Yaqin do'konlardagi mahsulotlarni qidirish
    public List<ProductResponse> searchNearbyProducts(LocationSearchRequest req) {
        validateCoords(req.getLatitude(), req.getLongitude());

        double radius = req.getRadiusKm() != null ? req.getRadiusKm() : 5.0;
        int limit = req.getLimit() != null ? Math.min(req.getLimit(), 50) : 20;
        String query = req.getQuery() != null ? req.getQuery() : "";

        List<Product> products = productRepository.searchNearbyProducts(
                query, req.getLatitude(), req.getLongitude(), radius, limit);

        return products.stream()
                .map(p -> toProductResponse(p, req.getLatitude(), req.getLongitude()))
                .toList();
    }

    // 3. Kategoriya bo'yicha eng arzon + yaqin mahsulotlar (AI tavsiya uchun)
    public List<ProductResponse> findCheapestNearby(
            CategoryType category, double lat, double lon,
            double radiusKm, int limit) {
        validateCoords(lat, lon);

        List<Product> products = productRepository.findCheapestNearby(
                category.name(), lat, lon, radiusKm, limit);

        return products.stream()
                .map(p -> toProductResponse(p, lat, lon))
                .toList();
    }

    // 4. Eng yaqin do'konni topish
    public ShopResponse findNearestShop(double lat, double lon) {
        validateCoords(lat, lon);

        Shop shop = shopRepository.findNearestShop(lat, lon)
                .orElseThrow(() -> new BozorException(
                        "Atrofingizda hech qanday do'kon topilmadi"));

        return toShopResponse(shop, lat, lon);
    }

    // ──────────────────────────────────────────────────────────
    private void validateCoords(Double lat, Double lon) {
        if (!LocationUtil.isValid(lat, lon)) {
            throw new BozorException(
                    "Noto'g'ri koordinatlar. Latitude: -90..90, Longitude: -180..180");
        }
    }

    private ShopResponse toShopResponse(Shop s, double userLat, double userLon) {
        double dist = 0;
        String mapsUrl = null;

        if (LocationUtil.isValid(s.getLatitude(), s.getLongitude())) {
            dist = LocationUtil.calculateDistance(
                    userLat, userLon, s.getLatitude(), s.getLongitude());
            mapsUrl = LocationUtil.googleMapsNavUrl(s.getLatitude(), s.getLongitude());
        }

        return ShopResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .categoryType(s.getCategoryType())
                .categoryDisplayName(s.getCategoryType().getDisplayName())
                .address(s.getAddress())
                .latitude(s.getLatitude())
                .longitude(s.getLongitude())
                .phone(s.getPhone())
                .workingHours(s.getWorkingHours())
                .active(s.isActive())
                .ownerName(s.getOwner().getFullName())
                .imageUrl(s.getImageUrl())
                .distanceKm(Math.round(dist * 100.0) / 100.0)
                .distanceLabel(LocationUtil.formatDistance(dist))
                .googleMapsUrl(mapsUrl)
                .build();
    }

    private ProductResponse toProductResponse(Product p, double userLat, double userLon) {
        Shop s = p.getShop();
        double dist = 0;
        String mapsUrl = null;

        if (LocationUtil.isValid(s.getLatitude(), s.getLongitude())) {
            dist = LocationUtil.calculateDistance(
                    userLat, userLon, s.getLatitude(), s.getLongitude());
            mapsUrl = LocationUtil.googleMapsNavUrl(s.getLatitude(), s.getLongitude());
        }

        String stockStatus;
        if (p.getStockQuantity() == 0) stockStatus = "TUGAGAN";
        else if (p.getStockQuantity() < 20) stockStatus = "KAM_QOLDI";
        else stockStatus = "MAVJUD";

        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .categoryType(p.getCategoryType())
                .categoryDisplayName(p.getCategoryType().getDisplayName())
                .sellPrice(p.getSellPrice())
                .unit(p.getUnit())
                .stockQuantity(p.getStockQuantity())
                .soldQuantity(p.getSoldQuantity())
                .color(p.getColor())
                .brand(p.getBrand())
                .stockStatus(stockStatus)
                .shopId(s.getId())
                .shopName(s.getName())
                .shopAddress(s.getAddress())
                .shopPhone(s.getPhone())
                .shopLatitude(s.getLatitude())
                .shopLongitude(s.getLongitude())
                .distanceKm(Math.round(dist * 100.0) / 100.0)
                .distanceLabel(LocationUtil.formatDistance(dist))
                .googleMapsUrl(mapsUrl)
                .createdAt(p.getCreatedAt())
                .build();
    }
}
