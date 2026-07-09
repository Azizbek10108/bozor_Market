package uz.bozor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.bozor.dto.request.ReviewRequest;
import uz.bozor.dto.response.ReviewResponse;
import uz.bozor.entity.Order;
import uz.bozor.entity.OrderItem;
import uz.bozor.entity.Product;
import uz.bozor.entity.Review;
import uz.bozor.entity.User;
import uz.bozor.entity.enums.OrderStatus;
import uz.bozor.exception.BozorException;
import uz.bozor.repository.OrderRepository;
import uz.bozor.repository.ProductRepository;
import uz.bozor.repository.ReviewRepository;
import uz.bozor.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    private User currentUser() {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new BozorException("Foydalanuvchi topilmadi"));
    }

    @Transactional
    public ReviewResponse create(ReviewRequest request) {
        User buyer = currentUser();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BozorException("Mahsulot topilmadi"));

        // Agar buyurtma ko'rsatilgan bo'lsa - tekshirish: shu buyer'ga tegishli, COMPLETED, va shu mahsulotni o'z ichiga olgan
        Order order = null;
        if (request.getOrderId() != null) {
            order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new BozorException("Buyurtma topilmadi"));

            if (!order.getBuyer().getId().equals(buyer.getId())) {
                throw new BozorException("Bu buyurtma sizga tegishli emas");
            }
            if (order.getStatus() != OrderStatus.COMPLETED) {
                throw new BozorException("Faqat bajarilgan buyurtmalarga fikr qoldirish mumkin");
            }
            boolean hasProduct = order.getItems().stream()
                    .map(OrderItem::getProduct)
                    .anyMatch(p -> p.getId().equals(product.getId()));
            if (!hasProduct) {
                throw new BozorException("Bu mahsulot ushbu buyurtmada yo'q");
            }
            if (reviewRepository.existsByOrderId(order.getId())) {
                throw new BozorException("Bu buyurtmaga allaqachon fikr qoldirilgan");
            }
        }

        Review review = Review.builder()
                .product(product)
                .buyer(buyer)
                .order(order)
                .rating(request.getRating())
                .comment(request.getComment())
                .imageUrl(request.getImageUrl())
                .build();

        return toResponse(reviewRepository.save(review));
    }

    public List<ReviewResponse> getByProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream().map(this::toResponse).toList();
    }

    public List<ReviewResponse> getMyReviews() {
        User buyer = currentUser();
        return reviewRepository.findByBuyerIdOrderByCreatedAtDesc(buyer.getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public void delete(Long id) {
        User user = currentUser();
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new BozorException("Fikr topilmadi"));
        if (!review.getBuyer().getId().equals(user.getId())) {
            throw new BozorException("Bu fikr sizga tegishli emas");
        }
        reviewRepository.delete(review);
    }

    // Mahsulot uchun o'rtacha baho va son - ProductService tomonidan ishlatiladi
    public Double getAverageRating(Long productId) {
        return reviewRepository.findAverageRatingByProductId(productId);
    }

    public Long getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    private ReviewResponse toResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .productId(r.getProduct().getId())
                .productName(r.getProduct().getName())
                .buyerId(r.getBuyer().getId())
                .buyerName(r.getBuyer().getFullName())
                .rating(r.getRating())
                .comment(r.getComment())
                .imageUrl(r.getImageUrl())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
