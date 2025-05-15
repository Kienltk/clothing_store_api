package com.clothingstore.clothing_store_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "discounts")
@Data
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "discount_percent", nullable = false)
    private BigDecimal discountPercent;

    @Column(name = "start_sale", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startSale;

    @Column(name = "end_sale", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endSale;
}