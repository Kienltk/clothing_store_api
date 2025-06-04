package com.clothingstore.clothing_store_api.repository;

import com.clothingstore.clothing_store_api.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color,Long> {
    Optional<Color> findByColor(String color);
}
