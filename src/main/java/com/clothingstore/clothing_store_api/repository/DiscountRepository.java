package com.clothingstore.clothing_store_api.repository;

import com.clothingstore.clothing_store_api.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
}