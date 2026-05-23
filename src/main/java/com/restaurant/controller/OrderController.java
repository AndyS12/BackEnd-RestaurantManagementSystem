package com.restaurant.controller;

import com.restaurant.dto.OrderDTO;
import com.restaurant.model.Order;
import com.restaurant.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Orders", description = "Order management")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get all orders")
    public ResponseEntity<List<OrderDTO.Response>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status")
    public ResponseEntity<List<OrderDTO.Response>> getByStatus(@PathVariable Order.OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @GetMapping("/table/{tableId}")
    @Operation(summary = "Get orders by table")
    public ResponseEntity<List<OrderDTO.Response>> getByTable(@PathVariable Long tableId) {
        return ResponseEntity.ok(orderService.getOrdersByTable(tableId));
    }

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<OrderDTO.Response> createOrder(@Valid @RequestBody OrderDTO.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<OrderDTO.Response> updateStatus(@PathVariable Long id,
                                                           @RequestBody OrderDTO.StatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request.getStatus()));
    }

    @PatchMapping("/{id}/discount")
    @Operation(summary = "Apply discount to order")
    public ResponseEntity<OrderDTO.Response> applyDiscount(@PathVariable Long id,
                                                            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(orderService.applyDiscount(id, amount));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get total revenue for a time period")
    public ResponseEntity<Map<String, Double>> getRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Double revenue = orderService.getTotalRevenue(start, end);
        return ResponseEntity.ok(Map.of("totalRevenue", revenue));
    }
}
