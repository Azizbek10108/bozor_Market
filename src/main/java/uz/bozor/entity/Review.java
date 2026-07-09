package uz.bozor.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    // Faqat shu buyurtmaga fikr qoldirilganini bilish uchun (1 buyurtma = 1 fikr)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "rating", nullable = false)
    private Integer rating; // 1-5

    @Column(name = "comment", length = 1000)
    private String comment;

    @Column(name = "image_url")
    private String imageUrl;
}
