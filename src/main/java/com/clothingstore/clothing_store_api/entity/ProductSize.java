package com.clothingstore.clothing_store_api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_sizes")
@Data
public class ProductSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    @Column(name = "stock", nullable = false)
    private Integer stock;
}