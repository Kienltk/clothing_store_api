package com.clothingstore.clothing_store_api.repository;

import com.clothingstore.clothing_store_api.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByUser_IdAndProductSize_Id(Long userId, Long productSizeId);
}
