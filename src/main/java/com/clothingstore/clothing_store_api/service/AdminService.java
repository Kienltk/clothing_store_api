package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.dto.DashboardDTO;
import com.clothingstore.clothing_store_api.dto.ProductDTO;
import com.clothingstore.clothing_store_api.dto.RevenueDTO;
import com.clothingstore.clothing_store_api.entity.Category;
import com.clothingstore.clothing_store_api.entity.Product;
import com.clothingstore.clothing_store_api.repository.CategoryRepository;
import com.clothingstore.clothing_store_api.repository.OrderRepository;
import com.clothingstore.clothing_store_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductService productService;

    public DashboardDTO getDashboardData() {
        DashboardDTO dashboard = new DashboardDTO();

        dashboard.setTotalIncome(orderRepository.findAll().stream()
                .map(order -> order.getTotal() != null ? order.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        dashboard.setTotalOrder(orderRepository.count());

        Map<String, List<ProductDTO>> topProductsByParentCategory = getTopProductsByParentCategory();
        dashboard.setTopProductsByParentCategory(topProductsByParentCategory);

        List<RevenueDTO> revenueChart = getRevenueChart();
        dashboard.setRevenueChart(revenueChart);

        return dashboard;
    }

    private Map<String, List<ProductDTO>> getTopProductsByParentCategory() {
        List<Category> parentCategories = categoryRepository.findByParentIsNull();

        Map<String, List<ProductDTO>> result = new HashMap<>();

        for (Category parent : parentCategories) {
            List<Long> categoryIds = getAllCategoryIds(parent);

            List<Object[]> topProducts = orderRepository.findTopProductsByCategoryIds(categoryIds, 3);

            List<ProductDTO> productDTOs = topProducts.stream()
                    .map(obj -> productService.mapProductToDetails((Product) obj[0], null, null))
                    .collect(Collectors.toList());

            result.put(parent.getCategoryName(), productDTOs);
        }

        return result;
    }

    private List<Long> getAllCategoryIds(Category parent) {
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(parent.getId());
        for (Category child : parent.getChildren()) {
            categoryIds.addAll(getAllCategoryIds(child));
        }
        return categoryIds;
    }

    private List<RevenueDTO> getRevenueChart() {
        List<Object[]> revenueData = orderRepository.findRevenueByDate();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

        return revenueData.stream().map(obj -> {
            String inputDate = (String) obj[0];
            BigDecimal total = (BigDecimal) obj[1];
            try {
                Date date = inputFormat.parse(inputDate);
                return new RevenueDTO(outputFormat.format(date), total);
            } catch (ParseException e) {
                throw new RuntimeException("Failed to parse date: " + inputDate, e);
            }
        }).collect(Collectors.toList());
    }
}
