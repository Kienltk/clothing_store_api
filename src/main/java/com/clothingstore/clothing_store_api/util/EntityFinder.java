package com.clothingstore.clothing_store_api.util;

import com.clothingstore.clothing_store_api.entity.ProductSize;
import com.clothingstore.clothing_store_api.entity.User;
import com.clothingstore.clothing_store_api.repository.ProductSizeRepository;
import com.clothingstore.clothing_store_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntityFinder {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductSizeRepository productSizeRepository;

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    public ProductSize findProductSizeById(Long productSizeId) {
        return productSizeRepository.findById(productSizeId)
                .orElseThrow(() -> new EntityNotFoundException("ProductSize not found with id: " + productSizeId));
    }
}
