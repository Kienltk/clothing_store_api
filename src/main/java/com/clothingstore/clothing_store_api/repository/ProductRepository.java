package com.clothingstore.clothing_store_api.repository;

import com.clothingstore.clothing_store_api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoriesId(Long categoryId);
}