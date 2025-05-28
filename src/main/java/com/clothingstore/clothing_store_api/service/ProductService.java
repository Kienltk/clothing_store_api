package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.dto.*;
import com.clothingstore.clothing_store_api.entity.*;
import com.clothingstore.clothing_store_api.repository.CategoryRepository;
import com.clothingstore.clothing_store_api.repository.FavoriteRepository;
import com.clothingstore.clothing_store_api.repository.ProductRepository;
import com.clothingstore.clothing_store_api.repository.ProductSizeRepository;
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
    private final ProductSizeRepository productSizeRepository;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository, FavoriteRepository favoriteRepository, ProductSizeRepository productSizeRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.favoriteRepository = favoriteRepository;
        this.productSizeRepository = productSizeRepository;
    }

    public Map<String, List<ProductDTO>> getProductsByCategory(Long userId, String slug) {
        Long categoryId = slug == null ? null : categoryRepository.findBySlug(slug).getId();
        List<Category> categories = categoryId == null ? categoryRepository.findByParentId(null) : categoryRepository.findByParentId(categoryId);
        Map<String, List<ProductDTO>> categoryProducts = new HashMap<>();
        for (Category category : categories) {
            List<Product> products = productRepository.findByCategoriesId(category.getId());
            categoryProducts.put(category.getCategoryName(), mapProductsToList(products, userId));
        }
        return categoryProducts;
    }

    public SearchProductDTO searchProducts(String productName, Long userId) {
        List<Product> products = productRepository.findByProductNameContainingIgnoreCase(productName.trim());
        Map<String, Object> data = new HashMap<>();
        String message;
        List<ProductDTO> productDetailsList;

        if (products.isEmpty()) {
            message = "Not found";
            products = productRepository.findRandomProducts();
            productDetailsList = mapProductsToList(products, userId);
        } else {
            int productCount = products.size();
            message = "Found " + productCount + " products";
            productDetailsList = mapProductsToList(products, userId);
            data.put("total", productCount);
        }
        data.put("products", productDetailsList);

        return new SearchProductDTO(message, data);
    }

    public ProductDetailDTO getProductDetails(String slug, Long userId) {
        Long productId = productRepository.findProductBySlug(slug).getId();
        Product product = productRepository.findProductById(productId);
        if (product == null) {
            return null;
        }

        ProductDTO productDetails = mapProductToDetails(product, userId, Collections.emptyList());
        String parentCategory = getParentCategory(product).getSlug();
        Map<String, List<ProductDTO>> relatedProductsMap = getProductsByCategory(userId, parentCategory);
        List<ProductDTO> relatedProducts = relatedProductsMap.values().stream()
                .flatMap(List::stream)
                .filter(p -> !p.getId().equals(productId))
                .limit(MAX_RELATED_PRODUCTS)
                .collect(Collectors.toList());

        return new ProductDetailDTO(productDetails, relatedProducts);
    }

    public List<ProductDTO> getFavoriteProducts(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        List<ProductDTO> favoriteProducts = new ArrayList<>();
        for (Favorite favorite : favorites) {
            ProductDTO product = mapProductToDetails(favorite.getProduct(), userId, favorites);
            favoriteProducts.add(product);
        }

        return favoriteProducts;
    }

    private List<ProductDTO> mapProductsToList(List<Product> products, Long userId) {
        List<Favorite> favorites = userId != null ? favoriteRepository.findByUserId(userId) : Collections.emptyList();
        return products.stream()
                .map(product -> mapProductToDetails(product, userId, favorites))
                .collect(Collectors.toList());
    }

    private ProductDTO mapProductToDetails(Product product, Long userId, List<Favorite> favorites) {
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

        Long id = product.getId();
        String productName = product.getProductName();
        BigDecimal price = product.getPrice();
        String status = product.getStatus();
        List<StockDetailDTO> stockDetailDTO = getStockDetails(product);
        Boolean isFavorite = userId != null &&
                favorites.stream().anyMatch(fav -> fav.getProduct().getId().equals(product.getId()));
        String slug = product.getSlug();

        return new ProductDTO(id, productName, price, discount, status, mainImageUrl, stockDetailDTO, isFavorite, slug);
    }

    private List<StockDetailDTO> getStockDetails(Product product) {
        return product.getProductColors().stream()
                .map(pc -> new StockDetailDTO(
                        pc.getColor().getColor(),
                        pc.getProductImages().stream()
                                .map(ProductImage::getImageUrl)
                                .findFirst()
                                .orElse(""),
                        pc.getProductSizes().stream()
                                .map(ps -> new SizeStockDTO(
                                        ps.getSize().getSize(),
                                        ps.getStock()
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    private Category getParentCategory(Product product) {
        Category category = product.getCategories().get(0);
        while (category.getParent() != null) {
            category = category.getParent();
        }
        return category;
    }

    public Long mapToProductSizeId(Long productId, String color, String sizeName) {
        Optional<ProductSize> productSizeOpt = productSizeRepository
                .findByProductColorProductIdAndProductColorColorColorAndSizeSize(productId, color, sizeName);

        if (productSizeOpt.isEmpty()) {
            throw new RuntimeException("Not found with product id " + productId +
                    ", color=" + color + ", size=" + sizeName);
        }

        return productSizeOpt.get().getId();
    }
}