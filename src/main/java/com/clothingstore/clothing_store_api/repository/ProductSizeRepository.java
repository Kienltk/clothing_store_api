package com.clothingstore.clothing_store_api.repository;

import com.clothingstore.clothing_store_api.entity.ProductSize;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize,Long> {
    Optional<ProductSize> findByProductColorProductIdAndProductColorColorColorAndSizeSize(
            Long productId, String color, String size);
}
