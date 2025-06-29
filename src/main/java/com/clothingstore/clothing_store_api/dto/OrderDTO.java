package com.clothingstore.clothing_store_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private Long userId;
    private String Name;
    private Date paymentTime;
    private BigDecimal total;
    private String status;
    private List<OrderItemDTO> orderItems;
}

