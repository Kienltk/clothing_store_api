package com.clothingstore.clothing_store_api.repository;

import com.clothingstore.clothing_store_api.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface SizeRepository extends JpaRepository<Size,Long> {
    Optional<Size> findBySize(String size);
}
