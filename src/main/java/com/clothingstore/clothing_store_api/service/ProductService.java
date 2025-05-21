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
    private static final int MAX_RELATED_PRODUCTS = 16;

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository, FavoriteRepository favoriteRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.favoriteRepository = favoriteRepository;
    }

    public Map<String, List<Map<String, Object>>> getProductsByCategory(Long userId, Long categoryId, boolean isParent) {
        List<Category> categories = isParent ?
                (categoryId == null ? categoryRepository.findByParentId(null) : categoryRepository.findByParentId(categoryId))
                : categoryRepository.findByParentId(categoryId);
        return getProductsGroupedByCategories(categories, userId);
    }

    public Map<String, Object> searchProducts(String productName, Long userId) {
        List<Product> products = productRepository.findByProductNameContainingIgnoreCase(productName.trim());
        Map<String, Object> result = new HashMap<>();
        String message;
        List<Map<String, Object>> productDetailsList;

        if (products.isEmpty()) {
            message = "Not found";
            products = productRepository.findRandomProducts();
            productDetailsList = mapProductsToList(products, userId);
        } else {
            int productCount = products.size();
            message = "Found " + productCount + " products";
            productDetailsList = mapProductsToList(products, userId);
            result.put("total", productCount);
        }

        result.put("products", productDetailsList);
        return new HashMap<String, Object>() {{
            put("message", message);
            put("data", result);
        }};
    }

    public Map<String, Object> getProductDetails(Long productId, Long userId) {
        Product product = productRepository.findProductById(productId);
        if (product == null) {
            return null;
        }

        Map<String, Object> productDetails = mapProductToDetails(product, userId);
        productDetails.put("stockDetail", getStockDetails(product));

        Category parentCategory = getParentCategory(product);
        Map<String, List<Map<String, Object>>> relatedProductsMap = getProductsByCategory(userId, parentCategory.getId(), true);
        List<Map<String, Object>> relatedProducts = relatedProductsMap.values().stream()
                .flatMap(List::stream)
                .filter(p -> !p.get("id").equals(productId))
                .limit(MAX_RELATED_PRODUCTS)
                .collect(Collectors.toList());

        productDetails.put("relatedProducts", relatedProducts);
        return productDetails;
    }

    private Map<String, List<Map<String, Object>>> getProductsGroupedByCategories(List<Category> categories, Long userId) {
        Map<String, List<Map<String, Object>>> categoryProductsMap = new HashMap<>();
        for (Category category : categories) {
            List<Product> products = productRepository.findByCategoriesId(category.getId());
            categoryProductsMap.put(category.getCategoryName(), mapProductsToList(products, userId));
        }
        return categoryProductsMap;
    }

    private List<Map<String, Object>> mapProductsToList(List<Product> products, Long userId) {
        List<Favorite> favorites = userId != null ? favoriteRepository.findByUserId(userId) : Collections.emptyList();
        return products.stream()
                .map(product -> mapProductToDetails(product, userId, favorites))
                .collect(Collectors.toList());
    }

    private Map<String, Object> mapProductToDetails(Product product, Long userId) {
        return mapProductToDetails(product, userId, userId != null ? favoriteRepository.findByUserId(userId) : Collections.emptyList());
    }

    private Map<String, Object> mapProductToDetails(Product product, Long userId, List<Favorite> favorites) {
        Date currentDate = new Date();
        BigDecimal discount = product.getDiscounts().stream()
                .filter(d -> d.getStartSale().before(currentDate) && d.getEndSale().after(currentDate))
                .map(Discount::getDiscountPercent)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        String mainImageUrl = product.getProductColors().stream()
                .flatMap(pc -> pc.getProductImages().stream())
                .filter(img -> Boolean.TRUE.equals(img.getIsMainImage()))
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse("");

        List<String> colors = product.getProductColors().stream()
                .map(pc -> pc.getColor().getColor())
                .distinct()
                .collect(Collectors.toList());

        return new HashMap<String, Object>() {{
            put("id", product.getId());
            put("product name", product.getProductName());
            put("price", product.getPrice());
            put("discount", discount);
            put("status", product.getStatus());
            put("url_img", mainImageUrl);
            put("colors", colors);
            put("isFavorite", userId != null && favorites.stream().anyMatch(fav -> fav.getProduct().getId().equals(product.getId())));
        }};
    }

    private List<Map<String, Object>> getStockDetails(Product product) {
        return product.getProductColors().stream()
                .map(pc -> new HashMap<String, Object>() {{
                    put("color", pc.getColor().getColor());
                    put("url", pc.getProductImages().stream()
                            .filter(img -> Boolean.TRUE.equals(img.getIsMainImage()))
                            .map(ProductImage::getImageUrl)
                            .findFirst()
                            .orElse(""));
                    put("sizes", pc.getProductSizes().stream()
                            .map(ps -> new HashMap<String, Object>() {{
                                put("size", ps.getSize().getSize());
                                put("stock", ps.getStock());
                            }})
                            .collect(Collectors.toList()));
                }})
                .collect(Collectors.toList());
    }

    private Category getParentCategory(Product product) {
        Category category = product.getCategories().get(0);
        while (category.getParent() != null) {
            category = category.getParent();
        }
        return category;
    }
}