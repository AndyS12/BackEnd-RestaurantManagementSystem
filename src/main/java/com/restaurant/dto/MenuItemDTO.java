package com.restaurant.dto;

import com.restaurant.model.MenuItem;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

public class MenuItemDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "Name is required")
        private String name;

        private String description;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be positive")
        private BigDecimal price;

        @NotNull(message = "Category is required")
        private MenuItem.Category category;

        private boolean available = true;
        private Integer preparationTimeMinutes;
        private String imageUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private MenuItem.Category category;
        private boolean available;
        private Integer preparationTimeMinutes;
        private String imageUrl;
    }
}
