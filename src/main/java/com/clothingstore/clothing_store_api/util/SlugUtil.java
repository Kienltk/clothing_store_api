package com.clothingstore.clothing_store_api.util;

import com.clothingstore.clothing_store_api.entity.Category;
import com.clothingstore.clothing_store_api.repository.CategoryRepository;

import java.text.Normalizer;

public class SlugUtil {

    public static String generateSlug(String name) {
        if (name == null) return "";

        name = name.replace("Đ", "D").replace("đ", "d");

        String[] words = name.split("\\s+");
        StringBuilder slugBuilder = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                String normalizedWord = Normalizer.normalize(word, Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                        .toLowerCase()
                        .replaceAll("[^a-z0-9]", "");
                if (!slugBuilder.isEmpty()) {
                    slugBuilder.append("-");
                }
                slugBuilder.append(normalizedWord);
            }
        }

        String slug = slugBuilder.toString()
                .trim()
                .replaceAll("-+", "-");

        return slug.isEmpty() ? "default" : slug;
    }

    public static String generateUniqueSlugForCategory(String baseSlug, Long parentId, CategoryRepository categoryRepository) {
        String candidateSlug = baseSlug;
        int counter = 1;

        Category parent = null;
        String parentLastSlug = "";

        if (parentId != null) {
            parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));

            String[] parentWords = parent.getCategoryName().split("\\s+");
            String lastWord = parentWords[parentWords.length - 1];
            parentLastSlug = generateSlug(lastWord); // ví dụ: "nu"

            candidateSlug = baseSlug + "-" + parentLastSlug;
        }

        while (categoryRepository.findBySlug(candidateSlug).isPresent()) {
            if (parentId != null) {
                candidateSlug = baseSlug + "-" + parentLastSlug + "-" + counter++;
            } else {
                candidateSlug = baseSlug + "-" + counter++;
            }
        }

        return candidateSlug;
    }


}