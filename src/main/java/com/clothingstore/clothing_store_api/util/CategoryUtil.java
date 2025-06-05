package com.clothingstore.clothing_store_api.util;


import com.clothingstore.clothing_store_api.entity.Category;

public class CategoryUtil {

    public static Category getParentCategoryFromCategory(Category category) {
        Category current = category;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return current.getId() != null ? current : null;
    }
}