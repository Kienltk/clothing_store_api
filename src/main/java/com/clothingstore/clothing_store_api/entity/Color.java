package com.clothingstore.clothing_store_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "colors")
@Data
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "color", nullable = false)
    private String color;

    @OneToMany(mappedBy = "color", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductColor> productColors;
}