package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.entity.*;
import com.clothingstore.clothing_store_api.repository.CategoryRepository;
import com.clothingstore.clothing_store_api.repository.FavoriteRepository;
import com.clothingstore.clothing_store_api.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository, FavoriteRepository favoriteRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.favoriteRepository = favoriteRepository;
    }

    public Map<String, List<Map<String, Object>>> getProductsByParentCategory(Long userId) {
        List<Category> parentCategories = categoryRepository.findByParentId(null);
        return getProductsForCategories(parentCategories, userId);
    }

    public Map<String, List<Map<String, Object>>> getProductsBySubCategory(Long categoryId, Long userId) {
        List<Category> subCategories = categoryRepository.findByParentId(categoryId);
        return getProductsForCategories(subCategories, userId);
    }

    private Map<String, List<Map<String, Object>>> getProductsForCategories(List<Category> categories, Long userId) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        List<Favorite> favorites = (userId != null) ? favoriteRepository.findByUserId(userId) : new ArrayList<>();
        Date currentDate = new Date();

        for (Category category : categories) {
            List<Product> products = productRepository.findByCategoriesId(category.getId());
            List<Map<String, Object>> productList = products.stream().map(product -> {
                Map<String, Object> productMap = new HashMap<>();

                BigDecimal discount = BigDecimal.ZERO;
                if (!product.getDiscounts().isEmpty()) {
                    discount = product.getDiscounts().stream()
                            .filter(d -> d.getStartSale().before(currentDate) && d.getEndSale().after(currentDate))
                            .map(Discount::getDiscountPercent)
                            .findFirst()
                            .orElse(BigDecimal.ZERO);
                }

                String imageUrl = "";
                if (!product.getProductColors().isEmpty()) {
                    imageUrl = product.getProductColors().stream()
                            .flatMap(pc -> pc.getProductImages().stream())
                            .filter(img -> Boolean.TRUE.equals(img.getIsMainImage()))
                            .map(ProductImage::getImageUrl)
                            .findFirst()
                            .orElse("");
                }

                productMap.put("id", product.getId());
                productMap.put("product name", product.getProductName());
                productMap.put("price", product.getPrice());
                productMap.put("discount", discount);
                productMap.put("status", product.getStatus());
                productMap.put("url_img", imageUrl);
                boolean isFavorite = (userId != null) && favorites.stream().anyMatch(fav -> fav.getProduct().getId().equals(product.getId()));
                productMap.put("isFavorite", isFavorite);

                return productMap;
            }).collect(Collectors.toList());

            result.put(category.getCategoryName(), productList);
        }

        return result;
    }
}