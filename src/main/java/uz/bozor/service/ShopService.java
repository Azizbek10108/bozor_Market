package uz.bozor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.bozor.dto.request.ShopRequest;
import uz.bozor.dto.response.ShopResponse;
import uz.bozor.entity.Shop;
import uz.bozor.entity.User;
import uz.bozor.exception.BozorException;
import uz.bozor.repository.ShopRepository;
import uz.bozor.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    @Transactional
    public ShopResponse createShop(ShopRequest request) {
        String phone = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User owner = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BozorException("Foydalanuvchi topilmadi"));

        if (shopRepository.findByOwnerId(owner.getId()).isPresent()) {
            throw new BozorException("Siz allaqachon do'kon occhgansiz");
        }

        Shop shop = Shop.builder()
                .name(request.getName())
                .categoryType(request.getCategoryType())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .phone(request.getPhone())
                .workingHours(request.getWorkingHours())
                .imageUrl(request.getImageUrl())
                .active(true)
                .owner(owner)
                .build();

        return toResponse(shopRepository.save(shop));
    }

    @Transactional
    public ShopResponse updateShop(ShopRequest request) {
        String phone = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User owner = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BozorException("Foydalanuvchi topilmadi"));
        Shop shop = shopRepository.findByOwnerId(owner.getId())
                .orElseThrow(() -> new BozorException("Do'kon topilmadi"));

        shop.setName(request.getName());
        shop.setCategoryType(request.getCategoryType());
        shop.setAddress(request.getAddress());
        shop.setLatitude(request.getLatitude());
        shop.setLongitude(request.getLongitude());
        shop.setPhone(request.getPhone());
        shop.setWorkingHours(request.getWorkingHours());

        return toResponse(shopRepository.save(shop));
    }

    public ShopResponse getMyShop() {
        String phone = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User owner = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BozorException("Foydalanuvchi topilmadi"));
        Shop shop = shopRepository.findByOwnerId(owner.getId())
                .orElseThrow(() -> new BozorException("Do'kon topilmadi"));
        return toResponse(shop);
    }

    public List<ShopResponse> getAllShops() {
        return shopRepository.findByActiveTrue()
                .stream().map(this::toResponse).toList();
    }

    private ShopResponse toResponse(Shop s) {
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
                .build();
    }
}
