package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.dto.CartItemGetDTO;
import com.clothingstore.clothing_store_api.dto.CartItemStoreDTO;
import com.clothingstore.clothing_store_api.entity.CartItem;
import com.clothingstore.clothing_store_api.entity.ProductSize;
import com.clothingstore.clothing_store_api.entity.User;
import com.clothingstore.clothing_store_api.mapper.CartItemMapper;
import com.clothingstore.clothing_store_api.repository.CartItemRepository;
import com.clothingstore.clothing_store_api.util.CartItemHandler;
import com.clothingstore.clothing_store_api.util.EntityFinder;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartRedisService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private EntityFinder entityFinder;

    @Autowired
    private CartItemHandler cartItemHandler;

    @Autowired
    private ProductService productService;

    @Cacheable(value = "cartItems", key = "#userId")
    public List<CartItemGetDTO> getCartItemsByUserId(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        return cartItems.stream()
                .map(cartItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "cartItems", key = "#userId")
    public CartItemGetDTO addCartItem(Long userId, CartItemStoreDTO addCartItemDTO) {
        User user = entityFinder.findUserById(userId);
        Long productSizeId = productService.mapToProductSizeId(addCartItemDTO.getProductId(), addCartItemDTO.getColor(), addCartItemDTO.getSize());
        ProductSize productSize = entityFinder.findProductSizeById(productSizeId);

        CartItem cartItem = cartItemHandler.addOrUpdateCartItem(user, productSize, addCartItemDTO.getQuantity());

        return cartItemMapper.toDTO(cartItem);
    }

    @CacheEvict(value = "cartItems", key = "#userId")
    public CartItemGetDTO editCartItem(Long userId, CartItemStoreDTO editCartItemDTO) {
        Long productSizeId = productService.mapToProductSizeId(editCartItemDTO.getProductId(), editCartItemDTO.getColor(), editCartItemDTO.getSize());
        ProductSize productSize = entityFinder.findProductSizeById(productSizeId);

        Optional<CartItem> existingCartItem = cartItemRepository.findByUser_IdAndProductSize_Id(userId, productSizeId);
        if (existingCartItem.isEmpty()) {
            throw new ValidationException("CartItem not found for userId: " + userId + " and productSizeId: " + productSizeId);
        }

        if (productSize.getStock() < editCartItemDTO.getQuantity()) {
            throw new ValidationException("Insufficient stock for ProductSize id: " + productSizeId);
        }

        CartItem cartItem = existingCartItem.get();
        cartItem.setQuantity(editCartItemDTO.getQuantity());
        cartItem = cartItemRepository.save(cartItem);

        return cartItemMapper.toDTO(cartItem);
    }

    @CacheEvict(value = "cartItems", key = "#userId")
    public void deleteCartItem(Long userId, Long productSizeId) {
        Optional<CartItem> existingCartItem = cartItemRepository.findByUser_IdAndProductSize_Id(userId, productSizeId);
        if (existingCartItem.isEmpty()) {
            throw new ValidationException("CartItem not found for userId: " + userId + " and productSizeId: " + productSizeId);
        }

        cartItemRepository.delete(existingCartItem.get());
    }
}