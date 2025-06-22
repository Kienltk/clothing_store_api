package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.dto.DiscountDTO;
import com.clothingstore.clothing_store_api.dto.DiscountResponseDTO;
import com.clothingstore.clothing_store_api.entity.Discount;
import com.clothingstore.clothing_store_api.entity.Product;
import com.clothingstore.clothing_store_api.repository.DiscountRepository;
import com.clothingstore.clothing_store_api.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    @CacheEvict(value = {"discounts", "productsByCategory", "productDetails", "searchResults"}, allEntries = true)
    public DiscountResponseDTO addDiscount(DiscountDTO dto) {
        Discount discount = new Discount();
        discount.setDiscountPercent(dto.getDiscountPercent());
        discount.setStartSale(Date.from(dto.getStartSale().toInstant()));
        discount.setEndSale(Date.from(dto.getEndSale().toInstant()));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + dto.getProductId()));
        discount.setProduct(product);

        discount = discountRepository.save(discount);

        return getDiscountResponseDTO(discount);
    }

    @Transactional
    @CachePut(value = "discounts", key = "#id")
    @CacheEvict(value = {"productsByCategory", "productDetails", "searchResults"}, allEntries = true)
    public Discount updateDiscount(Long id, DiscountDTO dto) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found with id: " + id));

        discount.setDiscountPercent(dto.getDiscountPercent());

        if (dto.getStartSale() != null) {
            discount.setStartSale(Date.from(dto.getStartSale().toInstant()));
        }

        if (dto.getEndSale() != null) {
            discount.setEndSale(Date.from(dto.getEndSale().toInstant()));
        }

        if (!discount.getProduct().getId().equals(dto.getProductId())) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + dto.getProductId()));
            discount.setProduct(product);
        }

        return discountRepository.save(discount);
    }

    @Transactional
    @CacheEvict(value = {"discounts", "productsByCategory", "productDetails", "searchResults"}, allEntries = true)
    public boolean deleteDiscount(Long id) {
        if (!discountRepository.existsById(id)) {
            throw new RuntimeException("Discount not found with id: " + id);
        }
        discountRepository.deleteById(id);
        return true;
    }

    @Cacheable(value = "discounts", key = "#id")
    public DiscountResponseDTO getDiscountById(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found with id: " + id));
        return getDiscountResponseDTO(discount);
    }

    @NotNull
    private DiscountResponseDTO getDiscountResponseDTO(Discount discount) {
        DiscountResponseDTO response = new DiscountResponseDTO();
        response.setId(discount.getId());
        response.setDiscountPercent(discount.getDiscountPercent());
        response.setStartSale(discount.getStartSale().toInstant().atOffset(OffsetDateTime.now().getOffset()));
        response.setEndSale(discount.getEndSale().toInstant().atOffset(OffsetDateTime.now().getOffset()));
        response.setProductId(discount.getProduct() != null ? discount.getProduct().getId() : null);
        response.setProductName(discount.getProduct() != null ? discount.getProduct().getProductName() : null);

        return response;
    }

    @Cacheable(value = "discounts", key = "'all'")
    public List<DiscountResponseDTO> getAllDiscounts() {
        return discountRepository.findAll().stream()
                .map(this::getDiscountResponseDTO)
                .collect(Collectors.toList());
    }
}
