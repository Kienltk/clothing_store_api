package com.clothingstore.clothing_store_api.repository;

import com.clothingstore.clothing_store_api.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    @Query("SELECT p, SUM(oi.quantity) as totalQuantity " +
            "FROM OrderItem oi " +
            "JOIN oi.productSize ps " +
            "JOIN ps.productColor pc " +
            "JOIN pc.product p " +
            "JOIN p.categories c " +
            "WHERE c.id IN :categoryIds " +
            "GROUP BY p " +
            "ORDER BY totalQuantity DESC " +
            "LIMIT :limit")
    List<Object[]> findTopProductsByCategoryIds(List<Long> categoryIds, int limit);

    @Query("SELECT FUNCTION('DATE_FORMAT', o.paymentTime, '%Y-%m-%d'), SUM(o.total) " +
            "FROM Order o " +
            "WHERE o.paymentTime IS NOT NULL AND o.status <> 'Cancelled' " +
            "GROUP BY FUNCTION('DATE_FORMAT', o.paymentTime, '%Y-%m-%d') " +
            "ORDER BY FUNCTION('DATE_FORMAT', o.paymentTime, '%Y-%m-%d')")
    List<Object[]> findRevenueByDate();
}
