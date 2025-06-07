package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.config.CustomUserDetails;
import com.clothingstore.clothing_store_api.dto.OrderDTO;
import com.clothingstore.clothing_store_api.dto.OrderPutDTO;
import com.clothingstore.clothing_store_api.dto.OrderRequestDTO;
import com.clothingstore.clothing_store_api.response.ResponseObject;
import com.clothingstore.clothing_store_api.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ResponseObject<OrderDTO>> addOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody OrderRequestDTO orderRequest) {
        if (userDetails == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject<>(401, "User not authenticated", null));
        }
        Long userId = userDetails.getUser().getId();
        OrderDTO orderDTO = orderService.addOrder(userId, orderRequest);
        ResponseObject<OrderDTO> response = new ResponseObject<>(
                HttpStatus.OK.value(),
                "Order created successfully",
                orderDTO
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<OrderDTO>>> getOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject<>(401, "User not authenticated", null));
        }

        Long userId = userDetails.getUser().getRole().equals("ADMIN") ? null : userDetails.getUser().getId();
        System.out.println("Authenticated userId: " + userId + ", username: " + userDetails.getUsername());

        List<OrderDTO> orders = orderService.getOrders(userId);
        ResponseObject<List<OrderDTO>> response = new ResponseObject<>(
                HttpStatus.OK.value(),
                "Orders retrieved successfully",
                orders
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<ResponseObject<OrderDTO>> updateStatusOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody OrderPutDTO orderUpdate
    ) {
        if (userDetails == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject<>(401, "User not authenticated", null));
        }
        OrderDTO order = orderService.updateStatusOrder(orderUpdate.getId(), orderUpdate.getStatus());
        ResponseObject<OrderDTO> response = new ResponseObject<>(
                HttpStatus.OK.value(),
                "Orders retrieved successfully",
                order
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}