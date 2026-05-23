package com.restaurant.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "Item name is required")
        private String itemName;

        @NotNull
        @DecimalMin(value = "0.0")
        private BigDecimal quantity;

        @NotBlank(message = "Unit is required")
        private String unit;

        private BigDecimal minimumThreshold;
        private BigDecimal costPerUnit;
        private String supplierName;
        private LocalDateTime expiryDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String itemName;
        private BigDecimal quantity;
        private String unit;
        private BigDecimal minimumThreshold;
        private BigDecimal costPerUnit;
        private String supplierName;
        private LocalDateTime lastRestocked;
        private LocalDateTime expiryDate;
        private boolean lowStock;
    }
}
