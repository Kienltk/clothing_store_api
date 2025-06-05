package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.dto.*;
import com.clothingstore.clothing_store_api.entity.*;
import com.clothingstore.clothing_store_api.repository.*;
import com.clothingstore.clothing_store_api.util.SlugUtil;
import com.clothingstore.clothing_store_api.util.CategoryUtil;
import jakarta.transaction.Transactional;
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
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final ProductColorRepository productColorRepository;
    private final ProductImageRepository productImageRepository;

    public ProductService(ProductImageRepository productImageRepository, ProductColorRepository productColorRepository, SizeRepository sizeRepository, ColorRepository colorRepository, CategoryRepository categoryRepository, ProductRepository productRepository, FavoriteRepository favoriteRepository, ProductSizeRepository productSizeRepository) {
        this.productColorRepository = productColorRepository;
        this.productImageRepository = productImageRepository;
        this.categoryRepository = categoryRepository;
        this.sizeRepository = sizeRepository;
        this.colorRepository = colorRepository;
        this.productRepository = productRepository;
        this.favoriteRepository = favoriteRepository;
        this.productSizeRepository = productSizeRepository;
    }

    public Map<String, List<ProductDTO>> getProductsByCategory(Long userId, String slug) {
        Long categoryId = null;
        if (slug != null) {
            Optional<Category> category = categoryRepository.findBySlug(slug);
            if (category.isPresent()) {
                categoryId = category.get().getId();
            }
        }
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

    private List<ProductDTO> mapProductsToList(List<Product> products, Long userId) {
        List<Favorite> favorites = userId != null ? favoriteRepository.findByUserId(userId) : Collections.emptyList();
        return products.stream()
                .map(product -> mapProductToDetails(product, userId, favorites))
                .collect(Collectors.toList());
    }

    ProductDTO mapProductToDetails(Product product, Long userId, List<Favorite> favorites) {
        Date currentDate = new Date();
        BigDecimal discount = product.getDiscounts() != null ? product.getDiscounts().stream()
                .filter(d -> d.getStartSale().before(currentDate) && d.getEndSale().after(currentDate))
                .map(Discount::getDiscountPercent)
                .findFirst()
                .orElse(BigDecimal.ZERO) : BigDecimal.ZERO;

        String mainImageUrl = product.getImg();

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

    @Transactional
    public ProductDTO addNewProduct(CreateProductDTO dto, Long userId) {
        Product product = new Product();
        setAttribute(dto, product);
        product.setCreated(new Date());

        List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
        product.setCategories(categories);

        product = productRepository.save(product);

        List<ProductColor> newColors = handleVariants(dto.getVariants(), product);
        product.setProductColors(newColors);

        product = productRepository.findById(product.getId())
                .orElseThrow(() -> new RuntimeException("Product not found after save"));

        return mapProductToDetails(product, userId, Collections.emptyList());
    }

    @Transactional
    public ProductDTO editProduct(Long productId, CreateProductDTO dto, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        setAttribute(dto, product);

        List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
        product.setCategories(categories);

        // Xóa các productColor cũ và dữ liệu liên quan
        List<ProductColor> oldColors = productColorRepository.findByProductId(productId);
        for (ProductColor pc : oldColors) {
            productSizeRepository.deleteAll(pc.getProductSizes());
            productImageRepository.deleteAll(pc.getProductImages());
        }
        productColorRepository.deleteAll(oldColors);

        product = productRepository.save(product);

        handleVariants(dto.getVariants(), product);

        return mapProductToDetails(product, userId, Collections.emptyList());
    }

    private void setAttribute(CreateProductDTO dto, Product product) {
        product.setProductName(dto.getProductName());
        product.setImg(dto.getImgMain());
        product.setPrice(dto.getPrice());
        product.setStatus(dto.getStatus());

        String finalSlug = dto.getSlug() != null && !dto.getSlug().isEmpty()
                ? dto.getSlug()
                : SlugUtil.generateSlug(dto.getProductName());
        product.setSlug(finalSlug);
    }

    private List<ProductColor> handleVariants(List<StockDetailDTO> variants, Product product) {
        List<ProductColor> newColors = new ArrayList<>();
        if (variants == null || variants.isEmpty()) return newColors;

        for (StockDetailDTO variant : variants) {
            Color color = colorRepository.findByColor(variant.getColor())
                    .orElseThrow(() -> new RuntimeException("Color not found: " + variant.getColor()));

            ProductColor productColor = new ProductColor();
            productColor.setProduct(product);
            productColor.setColor(color);
            productColor = productColorRepository.save(productColor);

            if (variant.getImg() != null && !variant.getImg().isEmpty()) {
                ProductImage image = new ProductImage();
                image.setImageUrl(variant.getImg());
                image.setProductColor(productColor);
                productImageRepository.save(image);
            }

            if (variant.getSizes() != null) {
                for (SizeStockDTO sizeDTO : variant.getSizes()) {
                    Size size = sizeRepository.findBySize(sizeDTO.getSize())
                            .orElseThrow(() -> new RuntimeException("Size not found: " + sizeDTO.getSize()));

                    ProductSize productSize = new ProductSize();
                    productSize.setProductColor(productColor);
                    productSize.setSize(size);
                    productSize.setStock(sizeDTO.getStock());
                    productSizeRepository.save(productSize);
                }
            }

            newColors.add(productColor);
        }
        return newColors;
    }

    private List<StockDetailDTO> getStockDetails(Product product) {
        if (product.getProductColors() == null || product.getProductColors().isEmpty()) {
            return Collections.emptyList();
        }
        return product.getProductColors().stream()
                .map(pc -> new StockDetailDTO(
                        pc.getColor() != null ? pc.getColor().getColor() : "",
                        pc.getProductImages() != null ? pc.getProductImages().stream()
                                .map(ProductImage::getImageUrl)
                                .findFirst()
                                .orElse("") : "",
                        pc.getProductSizes() != null ? pc.getProductSizes().stream()
                                .map(ps -> new SizeStockDTO(
                                        ps.getSize() != null ? ps.getSize().getSize() : "",
                                        ps.getStock()
                                ))
                                .collect(Collectors.toList()) : Collections.emptyList()
                ))
                .collect(Collectors.toList());
    }

    private Category getParentCategory(Product product) {
        Category category = product.getCategories().get(0);
        return CategoryUtil.getParentCategoryFromCategory(category);
    }

    Long mapToProductSizeId(Long productId, String color, String sizeName) {
        Optional<ProductSize> productSizeOpt = productSizeRepository
                .findByProductColorProductIdAndProductColorColorColorAndSizeSize(productId, color, sizeName);

        if (productSizeOpt.isEmpty()) {
            throw new RuntimeException("Not found with product id " + productId +
                    ", color=" + color + ", size=" + sizeName);
        }

        return productSizeOpt.get().getId();
    }
}