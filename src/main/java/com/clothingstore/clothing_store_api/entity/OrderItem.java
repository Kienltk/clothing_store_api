package com.clothingstore.clothing_store_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_size_id", nullable = false)
    private ProductSize productSize;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "discount", nullable = false)
    private Integer discount;

    @Column(name = "total", nullable = false)
    private BigDecimal total;
}