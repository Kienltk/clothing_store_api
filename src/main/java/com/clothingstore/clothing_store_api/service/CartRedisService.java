package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.dto.CartItemDTO;
import com.clothingstore.clothing_store_api.entity.CartItem;
import com.clothingstore.clothing_store_api.entity.ProductSize;
import com.clothingstore.clothing_store_api.entity.User;
import com.clothingstore.clothing_store_api.mapper.CartItemMapper;
import com.clothingstore.clothing_store_api.repository.CartItemRepository;
import com.clothingstore.clothing_store_api.util.CartItemHandler;
import com.clothingstore.clothing_store_api.util.EntityFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<CartItemDTO> getCartItemsByUserId(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        return cartItems.stream()
                .map(cartItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "cartItems", key = "#userId")
    public List<CartItemDTO> addOrUpdateCartItem(Long userId, List<CartItemDTO> addCartItemDTO) {
        List<CartItemDTO> result = new ArrayList<>();
        User user = entityFinder.findUserById(userId);

        for (CartItemDTO itemDTO : addCartItemDTO) {
            Long productSizeId = productService.mapToProductSizeId(
                    itemDTO.getProductId(),
                    itemDTO.getColor(),
                    itemDTO.getSize()
            );

            ProductSize productSize = entityFinder.findProductSizeById(productSizeId);
            Optional<CartItem> existingCartItem = cartItemRepository.findByUser_IdAndProductSize_Id(userId, productSizeId);
            CartItem cartItem = existingCartItem.orElse(new CartItem());

            cartItem.setUser(user);
            cartItem.setProductSize(productSize);
            cartItem.setQuantity(itemDTO.getQuantity());

            CartItem savedCartItem = cartItemRepository.save(cartItem);
            result.add(cartItemMapper.toDTO(savedCartItem));
        }

        return result;
    }

//    @CacheEvict(value = "cartItems", key = "#userId")
//    public CartItemDTO editCartItem(Long userId, CartItemStoreDTO editCartItemDTO) {
//        Long productSizeId = productService.mapToProductSizeId(editCartItemDTO.getProductId(), editCartItemDTO.getColor(), editCartItemDTO.getSize());
//        ProductSize productSize = entityFinder.findProductSizeById(productSizeId);
//
//        Optional<CartItem> existingCartItem = cartItemRepository.findByUser_IdAndProductSize_Id(userId, productSizeId);
//        if (existingCartItem.isEmpty()) {
//            throw new ValidationException("CartItem not found for userId: " + userId + " and productSizeId: " + productSizeId);
//        }
//
//        if (productSize.getStock() < editCartItemDTO.getQuantity()) {
//            throw new ValidationException("Insufficient stock for ProductSize id: " + productSizeId);
//        }
//
//        CartItem cartItem = existingCartItem.get();
//        cartItem.setQuantity(editCartItemDTO.getQuantity());
//        cartItem = cartItemRepository.save(cartItem);
//
//        return cartItemMapper.toDTO(cartItem);
//    }

//    @CacheEvict(value = "cartItems", key = "#userId")
//    public void deleteCartItem(Long userId, Long productSizeId) {
//        Optional<CartItem> existingCartItem = cartItemRepository.findByUser_IdAndProductSize_Id(userId, productSizeId);
//        if (existingCartItem.isEmpty()) {
//            throw new ValidationException("CartItem not found for userId: " + userId + " and productSizeId: " + productSizeId);
//        }
//
//        cartItemRepository.delete(existingCartItem.get());
//    }
}