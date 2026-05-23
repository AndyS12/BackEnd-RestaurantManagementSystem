package com.restaurant.dto;

import com.restaurant.model.Reservation;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

public class ReservationDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long customerId;

        @NotNull(message = "Table ID is required")
        private Long tableId;

        @NotNull(message = "Reservation time is required")
        private LocalDateTime reservationTime;

        private LocalDateTime endTime;

        @NotNull
        @Min(value = 1, message = "Party size must be at least 1")
        private Integer partySize;

        @NotBlank(message = "Customer name is required")
        private String customerName;

        private String customerPhone;
        private String specialRequests;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long customerId;
        private String customerName;
        private String customerPhone;
        private Long tableId;
        private Integer tableNumber;
        private LocalDateTime reservationTime;
        private LocalDateTime endTime;
        private Integer partySize;
        private Reservation.ReservationStatus status;
        private String specialRequests;
        private LocalDateTime createdAt;
    }
}
