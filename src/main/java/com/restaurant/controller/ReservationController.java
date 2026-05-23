package com.restaurant.controller;

import com.restaurant.dto.ReservationDTO;
import com.restaurant.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reservations", description = "Table reservation management")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @Operation(summary = "Get all reservations")
    public ResponseEntity<List<ReservationDTO.Response>> getAll() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by ID")
    public ResponseEntity<ReservationDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getById(id));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get reservations by customer")
    public ResponseEntity<List<ReservationDTO.Response>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(reservationService.getByCustomer(customerId));
    }

    @PostMapping
    @Operation(summary = "Create a new reservation")
    public ResponseEntity<ReservationDTO.Response> create(@Valid @RequestBody ReservationDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(request));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a reservation")
    public ResponseEntity<ReservationDTO.Response> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark reservation as completed")
    public ResponseEntity<ReservationDTO.Response> complete(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.completeReservation(id));
    }
}
