package com.restaurant.dto;

import com.restaurant.model.Order;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotNull(message = "Table ID is required")
        private Long tableId;

        private Long waiterId;
        private Long customerId;

        @NotEmpty(message = "Order must have at least one item")
        private List<OrderItemRequest> items;

        private String specialInstructions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        @NotNull(message = "Menu item ID is required")
        private Long menuItemId;

        @NotNull
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        private String specialRequest;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long tableId;
        private Integer tableNumber;
        private String waiterName;
        private String customerName;
        private List<OrderItemResponse> items;
        private Order.OrderStatus status;
        private BigDecimal totalAmount;
        private BigDecimal taxAmount;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private String specialInstructions;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private Long id;
        private Long menuItemId;
        private String menuItemName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
        private String specialRequest;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusUpdateRequest {
        @NotNull(message = "Status is required")
        private Order.OrderStatus status;
    }
}
