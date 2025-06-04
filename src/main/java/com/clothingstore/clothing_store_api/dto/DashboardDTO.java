package com.clothingstore.clothing_store_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {
    private BigDecimal totalIncome;
    private Long totalProduct;
    private Long totalOrder;
    private Map<String, List<ProductDTO>> topProductsByParentCategory;
    private List<RevenueDTO> revenueChart;
}
