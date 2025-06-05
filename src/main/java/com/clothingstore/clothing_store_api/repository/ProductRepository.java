package com.clothingstore.clothing_store_api.repository;

import com.clothingstore.clothing_store_api.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoriesId(Long categoryId);
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    @Query(value = "SELECT * FROM products ORDER BY RAND() LIMIT 16", nativeQuery = true)
    List<Product> findRandomProducts();
    Product findProductById(Long id);
    Product findProductBySlug(String slug);

    Optional<Object> findBySlug(String slug);
}