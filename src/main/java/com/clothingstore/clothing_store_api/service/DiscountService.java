package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.dto.DiscountDTO;
import com.clothingstore.clothing_store_api.entity.Discount;
import com.clothingstore.clothing_store_api.entity.Product;
import com.clothingstore.clothing_store_api.repository.DiscountRepository;
import com.clothingstore.clothing_store_api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class DiscountService {
    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductRepository productRepository;

    public Discount createDiscount(DiscountDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Discount discount = new Discount();
        discount.setDiscountPercent(dto.getDiscountPercent());
        discount.setStartSale(Date.from(dto.getStartSale().atZone(ZoneId.systemDefault()).toInstant()));
        discount.setEndSale(Date.from(dto.getEndSale().atZone(ZoneId.systemDefault()).toInstant()));
        discount.setProduct(product);

        return discountRepository.save(discount);
    }

    public Discount updateDiscount(Long id, DiscountDTO dto) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));

        discount.setDiscountPercent(dto.getDiscountPercent());
        discount.setStartSale(Date.from(dto.getStartSale().atZone(ZoneId.systemDefault()).toInstant()));
        discount.setEndSale(Date.from(dto.getEndSale().atZone(ZoneId.systemDefault()).toInstant()));

        if (!discount.getProduct().getId().equals(dto.getProductId())) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            discount.setProduct(product);
        }

        return discountRepository.save(discount);
    }

    public boolean deleteDiscount(Long id) {
        discountRepository.deleteById(id);
        return false;
    }

    public Discount getDiscountById(Long id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
    }

    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }
}
