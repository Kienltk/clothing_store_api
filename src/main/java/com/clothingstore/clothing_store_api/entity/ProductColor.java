package com.clothingstore.clothing_store_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "product_color")
@Data
public class ProductColor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @OneToMany(mappedBy = "productColor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSize> productSizes;

    @OneToMany(mappedBy = "productColor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages;
}