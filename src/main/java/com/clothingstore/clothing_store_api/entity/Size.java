package com.clothingstore.clothing_store_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "sizes")
@Data
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "size", nullable = false, unique = true)
    private String size;

    @OneToMany(mappedBy = "size", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSize> productSizes;
}