package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.dto.DiscountDTO;
import com.clothingstore.clothing_store_api.dto.DiscountResponseDTO;
import com.clothingstore.clothing_store_api.entity.Discount;
import com.clothingstore.clothing_store_api.entity.Product;
import com.clothingstore.clothing_store_api.repository.DiscountRepository;
import com.clothingstore.clothing_store_api.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public DiscountResponseDTO addDiscount(DiscountDTO dto) {
        Discount discount = new Discount();
        discount.setDiscountPercent(dto.getDiscountPercent());
        discount.setStartSale(Date.from(dto.getStartSale().toInstant()));
        discount.setEndSale(Date.from(dto.getEndSale().toInstant()));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        discount.setProduct(product);

        discount = discountRepository.save(discount);

        DiscountResponseDTO response = new DiscountResponseDTO();
        response.setId(discount.getId());
        response.setDiscountPercent(discount.getDiscountPercent());
        response.setStartSale(discount.getStartSale().toInstant().atOffset(OffsetDateTime.now().getOffset()));
        response.setEndSale(discount.getEndSale().toInstant().atOffset(OffsetDateTime.now().getOffset()));
        response.setProductId(product.getId());
        response.setProductName(product.getProductName());

        return response;
    }

    @Transactional
    public Discount updateDiscount(Long id, DiscountDTO dto) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));

        discount.setDiscountPercent(dto.getDiscountPercent());

        if (dto.getStartSale() != null) {
            discount.setStartSale(Date.from(dto.getStartSale().toInstant()));
        }

        if (dto.getEndSale() != null) {
            discount.setEndSale(Date.from(dto.getEndSale().toInstant()));
        }

        if (!discount.getProduct().getId().equals(dto.getProductId())) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            discount.setProduct(product);
        }

        return discountRepository.save(discount);
    }

    @Transactional
    public boolean deleteDiscount(Long id) {
        if (!discountRepository.existsById(id)) {
            return false;
        }
        discountRepository.deleteById(id);
        return true;
    }

    public Discount getDiscountById(Long id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
    }

    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }
}