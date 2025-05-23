package com.clothingstore.clothing_store_api.util;

import com.clothingstore.clothing_store_api.entity.CartItem;
import com.clothingstore.clothing_store_api.entity.ProductSize;
import com.clothingstore.clothing_store_api.entity.User;
import com.clothingstore.clothing_store_api.repository.CartItemRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CartItemHandler {
    @Autowired
    private CartItemRepository cartItemRepository;

    public CartItem addOrUpdateCartItem(User user, ProductSize productSize, Integer quantity) {
        if (productSize.getStock() < quantity) {
            throw new ValidationException("Insufficient stock for ProductSize id: " + productSize.getId());
        }

        Optional<CartItem> existingCartItem = cartItemRepository.findByUser_IdAndProductSize_Id(
                user.getId(), productSize.getId());

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = CartItem.builder()
                    .user(user)
                    .productSize(productSize)
                    .quantity(quantity)
                    .build();
        }

        return cartItemRepository.save(cartItem);
    }
}
