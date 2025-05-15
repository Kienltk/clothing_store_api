package com.clothingstore.clothing_store_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "product_sizes")
@Data
public class ProductSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_color_id", nullable = false)
    private ProductColor productColor;

    @ManyToOne
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @OneToMany(mappedBy = "productSize", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "productSize", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
}