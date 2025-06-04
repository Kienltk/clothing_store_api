package com.clothingstore.clothing_store_api.repository;

import com.clothingstore.clothing_store_api.entity.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColor,Long> {
    ProductColor save(ProductColor productColor);
}
