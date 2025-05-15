package com.clothingstore.clothing_store_api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cart_items")
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_size_id", nullable = false)
    private ProductSize productSize;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}