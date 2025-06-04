package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.dto.OrderDTO;
import com.clothingstore.clothing_store_api.dto.OrderItemDTO;
import com.clothingstore.clothing_store_api.dto.OrderItemRequestDTO;
import com.clothingstore.clothing_store_api.dto.OrderRequestDTO;
import com.clothingstore.clothing_store_api.entity.*;
import com.clothingstore.clothing_store_api.repository.OrderRepository;
import com.clothingstore.clothing_store_api.repository.ProductSizeRepository;
import com.clothingstore.clothing_store_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductSizeRepository productSizeRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductSizeRepository productSizeRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productSizeRepository = productSizeRepository;
        this.productService = productService;
    }

    public OrderDTO addOrder(Long userId, OrderRequestDTO orderRequest) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOpt.get();

        Order order = new Order();
        order.setUser(user);
        order.setPaymentTime(new Date());
        order.setOrderItems(new ArrayList<>());


        BigDecimal orderTotal = BigDecimal.ZERO;
        for (OrderItemRequestDTO itemRequestDTO : orderRequest.getOrderItems()) {
            Long productSizeId = productService.mapToProductSizeId(itemRequestDTO.getProductId(), itemRequestDTO.getColor(), itemRequestDTO.getSize());
            Optional<ProductSize> productSizeOpt = productSizeRepository.findById(productSizeId);
            if (productSizeOpt.isEmpty()) {
                throw new RuntimeException("Product size not found: " + productSizeId);
            }
            ProductSize productSize = productSizeOpt.get();
            ProductColor productColor = productSize.getProductColor();
            Product product = productColor.getProduct();

            if (productSize.getStock() < itemRequestDTO.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product size: " + productSizeId);
            }

            BigDecimal price = product.getPrice();
            BigDecimal quantity = new BigDecimal(itemRequestDTO.getQuantity());
            double discount = itemRequestDTO.getDiscount() != null ? itemRequestDTO.getDiscount() : 0.0;
            BigDecimal discountFactor = BigDecimal.ONE.subtract(BigDecimal.valueOf(discount / 100));
            BigDecimal itemTotal = price.multiply(quantity).multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);

            productSize.setStock(productSize.getStock() - itemRequestDTO.getQuantity());
            productSizeRepository.save(productSize);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductSize(productSize);
            orderItem.setQuantity(itemRequestDTO.getQuantity());
            orderItem.setDiscount((int) discount);
            orderItem.setTotal(itemTotal);
            order.getOrderItems().add(orderItem);

            orderTotal = orderTotal.add(itemTotal);
        }

        order.setTotal(orderTotal);
        order.setStatus("Pending");
        orderRepository.save(order);

        return toOrderDTO(order);
    }

    public List<OrderDTO> getOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (Order order : orders) {
            orderDTOs.add(toOrderDTO(order));
        }
        return orderDTOs;
    }

    public OrderDTO updateStatusOrder(Long orderId, String status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        Order order = orderOpt.get();
        order.setStatus(status);
        orderRepository.save(order);
        return toOrderDTO(order);
    }

    private OrderDTO toOrderDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setUserId(order.getUser().getId());
        orderDTO.setPaymentTime(order.getPaymentTime());
        orderDTO.setTotal(order.getTotal());
        orderDTO.setStatus(order.getStatus());

        List<OrderItemDTO> itemDTOs = new ArrayList<>();
        for (OrderItem item : order.getOrderItems()) {
            ProductSize productSize = item.getProductSize();
            ProductColor productColor = productSize.getProductColor();
            Product product = productColor.getProduct();

            String mainImageUrl = productColor.getProductImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsMainImage()))
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse("");

            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setProductName(product.getProductName());
            itemDTO.setPrice(product.getPrice());
            itemDTO.setImg(mainImageUrl);
            itemDTO.setSlug(product.getSlug());
            itemDTO.setColor(productColor.getColor().getColor());
            itemDTO.setSize(productSize.getSize().getSize());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setDiscount(item.getDiscount().doubleValue());
            itemDTO.setTotal(item.getTotal());
            itemDTOs.add(itemDTO);
        }
        orderDTO.setOrderItems(itemDTOs);

        return orderDTO;
    }
}
