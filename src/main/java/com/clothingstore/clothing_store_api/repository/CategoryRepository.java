package com.clothingstore.clothing_store_api.repository;

import com.clothingstore.clothing_store_api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentId(Long parentId);
    Optional<Category> findBySlug(String slug);
    List<Category> findByParentIsNull();
    @Query("SELECT c FROM Category c JOIN c.products p WHERE p.id = :productId")
    Optional<List<Category>> findCategoriesByProductId(Long productId);
}