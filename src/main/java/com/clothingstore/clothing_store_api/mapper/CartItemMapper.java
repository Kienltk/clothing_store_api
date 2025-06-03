package com.clothingstore.clothing_store_api.mapper;

import com.clothingstore.clothing_store_api.dto.CartItemDTO;
import com.clothingstore.clothing_store_api.entity.CartItem;
import com.clothingstore.clothing_store_api.entity.Discount;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class CartItemMapper {
    public CartItemDTO toDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setQuantity(cartItem.getQuantity());
        dto.setStock(cartItem.getProductSize().getStock());
        dto.setSize(cartItem.getProductSize().getSize().getSize());
        dto.setProductId(cartItem.getProductSize().getProductColor().getProduct().getId());
        dto.setProductName(cartItem.getProductSize().getProductColor().getProduct().getProductName());
        dto.setColor(cartItem.getProductSize().getProductColor().getColor().getColor());

        LocalDateTime now = LocalDateTime.now();
        Double discountPercent = 0.0;
        if (cartItem.getProductSize().getProductColor().getProduct().getDiscounts() != null) {
            for (Discount discount : cartItem.getProductSize().getProductColor().getProduct().getDiscounts()) {
                LocalDateTime start = discount.getStartSale() != null
                        ? discount.getStartSale().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                        : LocalDateTime.MIN;
                LocalDateTime end = discount.getEndSale() != null
                        ? discount.getEndSale().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                        : LocalDateTime.MAX;

                if (start.isBefore(now) && end.isAfter(now)) {
                    discountPercent = discount.getDiscountPercent() != null ? discount.getDiscountPercent().doubleValue() : 0.0;
                    break;
                }
            }
        }
        dto.setDiscountPercent(discountPercent);

        if (!cartItem.getProductSize().getProductColor().getProductImages().isEmpty()) {
            dto.setImageUrl(cartItem.getProductSize().getProductColor().getProductImages().get(0).getImageUrl());
        }

        return dto;
    }
}