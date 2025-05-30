package com.clothingstore.clothing_store_api.repository;

import com.clothingstore.clothing_store_api.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    @NotNull
    Optional<User> findById(@NotNull Long userId);
    boolean existsByEmail(String email);
}
