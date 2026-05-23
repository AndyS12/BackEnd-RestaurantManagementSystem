package com.restaurant.repository;

import com.restaurant.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByTableId(Long tableId);
    List<Order> findByStatus(Order.OrderStatus status);
    List<Order> findByWaiterId(Long waiterId);
    List<Order> findByCustomerId(Long customerId);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    List<Order> findOrdersBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.status = 'PAID' AND o.createdAt BETWEEN :start AND :end")
    Double getTotalRevenueForPeriod(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(Order.OrderStatus status);
}
