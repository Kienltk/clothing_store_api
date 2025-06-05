package com.clothingstore.clothing_store_api.repository;

import com.clothingstore.clothing_store_api.entity.ProductColor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColor,Long> {
    @NotNull
    ProductColor save(@NotNull ProductColor productColor);

    List<ProductColor> findByProductId(Long productId);
}
