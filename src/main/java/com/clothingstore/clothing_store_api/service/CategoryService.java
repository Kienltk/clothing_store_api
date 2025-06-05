package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.dto.CategoryDTO;
import com.clothingstore.clothing_store_api.entity.Category;
import com.clothingstore.clothing_store_api.repository.CategoryRepository;
import com.clothingstore.clothing_store_api.util.SlugUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setCategoryName(categoryDTO.getCategoryName());

        String finalSlug = SlugUtil.generateUniqueSlugForCategory(
                SlugUtil.generateSlug(categoryDTO.getCategoryName()),
                categoryDTO.getParentId(),
                categoryRepository
        );
        category.setSlug(finalSlug);

        // Xử lý parent nếu có
        if (categoryDTO.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return mapToDTO(savedCategory);
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Category not found"));
        return mapToDTO(category);
    }
    public List<CategoryDTO> getCategoriesByParentId(Long parentId) {
        List<Category> categories;
        if (parentId != null) {
            categories = categoryRepository.findByParentId(parentId);
        } else {
            categories = categoryRepository.findByParentId(null);
        }
        return categories.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Category not found"));

        category.setCategoryName(categoryDTO.getCategoryName());
        category.setSlug(generateSlug(categoryDTO.getCategoryName()));

        if (categoryDTO.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new ValidationException("Parent category not found"));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        return mapToDTO(updatedCategory);
    }

    public boolean deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            return false;
        }
        categoryRepository.deleteById(id);
        return true;
    }
    public List<CategoryDTO> getCategoriesByProductId(Long productId) {
        List<Category> categories = categoryRepository.findCategoriesByProductId(productId)
                .orElseThrow(() -> new ValidationException("No categories found for product id: " + productId));
        return categories.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    private CategoryDTO mapToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setCategoryName(category.getCategoryName());
        dto.setSlug(category.getSlug());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        return dto;
    }

    private String generateSlug(String categoryName) {
        if (categoryName == null) return "";
        return categoryName.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }
}
