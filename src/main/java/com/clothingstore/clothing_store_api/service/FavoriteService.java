package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.entity.Favorite;
import com.clothingstore.clothing_store_api.entity.Product;
import com.clothingstore.clothing_store_api.entity.User;
import com.clothingstore.clothing_store_api.repository.CategoryRepository;
import com.clothingstore.clothing_store_api.repository.FavoriteRepository;
import com.clothingstore.clothing_store_api.repository.ProductRepository;
import com.clothingstore.clothing_store_api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class FavoriteService {
    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    public FavoriteService(ProductRepository productRepository, FavoriteRepository favoriteRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
    }

    public Map<String, String> addFavorite(Long userId, Long productId) {
        Map<String, String> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            response.put("message", "User not found");
            return response;
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            response.put("message", "Product not found");
            return response;
        }

        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndProductId(userId, productId);
        if (existingFavorite.isPresent()) {
            response.put("message", "Product in favorites");
            return response;
        }

        Favorite favorite = new Favorite();
        favorite.setUser(userOpt.get());
        favorite.setProduct(productOpt.get());
        favoriteRepository.save(favorite);

        response.put("message", "Favorite added successfully");
        return response;
    }

    @Transactional
    public Map<String, String> removeFavorite(Long userId, Long productId) {
        Map<String, String> response = new HashMap<>();
        String checkFavorite = checkFavorite(userId, productId);
        if (checkFavorite != null) {
            response.put("message", checkFavorite);
            return response;
        }

        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndProductId(userId, productId);
        if (existingFavorite.isEmpty()) {
            response.put("message", "Product not in favorites");
            return response;
        }

        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
        response.put("message", "Favorite removed successfully");
        return response;
    }

    private String checkFavorite(Long userId, Long productId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return "User not found";
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return "Product not found";
        }

        return null;
    }
}
