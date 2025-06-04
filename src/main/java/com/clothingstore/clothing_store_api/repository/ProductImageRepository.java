package com.clothingstore.clothing_store_api.repository;

import com.clothingstore.clothing_store_api.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {

}
