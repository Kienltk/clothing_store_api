package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.dto.CategoryDTO;
import com.clothingstore.clothing_store_api.response.ResponseObject;
import com.clothingstore.clothing_store_api.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/categories")
@PreAuthorize("hasRole('ADMIN')")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ResponseObject<CategoryDTO>> createCategory(
          @Valid  @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.CREATED.value(), "Category created successfully", createdCategory),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<CategoryDTO>>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.OK.value(), "Categories retrieved successfully", categories),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<CategoryDTO>> getCategoryById(
            @PathVariable Long id) {
        Optional<CategoryDTO> categoryDTO = Optional.ofNullable(categoryService.getCategoryById(id));
        return categoryDTO.map(dto -> new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.OK.value(), "Category retrieved successfully", dto),
                HttpStatus.OK
        )).orElseGet(() -> new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Category not found", null),
                HttpStatus.NOT_FOUND
        ));
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<ResponseObject<List<CategoryDTO>>> getCategoriesByParentId(
            @PathVariable Long parentId) {
        List<CategoryDTO> categories = categoryService.getCategoriesByParentId(parentId);
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.OK.value(), "Categories retrieved successfully", categories),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<CategoryDTO>> updateCategory(
            @PathVariable Long id,
           @Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        if (updatedCategory == null) {
            return new ResponseEntity<>(
                    new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Category not found", null),
                    HttpStatus.NOT_FOUND
            );
        }
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.OK.value(), "Category updated successfully", updatedCategory),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<String>> deleteCategory(
            @PathVariable Long id) {
        boolean deleted = categoryService.deleteCategory(id);
        if (!deleted) {
            return new ResponseEntity<>(
                    new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Category not found", null),
                    HttpStatus.NOT_FOUND
            );
        }
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.OK.value(), "Category deleted successfully", "Deleted category with id = " + id),
                HttpStatus.OK
        );
    }
}
