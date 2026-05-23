package com.restaurant.dto;

import com.restaurant.model.RestaurantTable;
import jakarta.validation.constraints.*;
import lombok.*;

public class TableDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "Table number is required")
        private Integer tableNumber;

        @NotNull
        @Min(value = 1, message = "Capacity must be at least 1")
        private Integer capacity;

        private RestaurantTable.TableStatus status;
        private String location;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Integer tableNumber;
        private Integer capacity;
        private RestaurantTable.TableStatus status;
        private String location;
    }
}
