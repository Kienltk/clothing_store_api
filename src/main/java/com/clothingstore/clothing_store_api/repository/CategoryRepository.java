package com.clothingstore.clothing_store_api.repository;

import com.clothingstore.clothing_store_api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentId(Long parentId);
}