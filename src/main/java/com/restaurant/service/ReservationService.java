package com.restaurant.service;

import com.restaurant.dto.ReservationDTO;
import com.restaurant.exception.BusinessException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.model.*;
import com.restaurant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TableRepository tableRepository;
    private final UserRepository userRepository;

    public List<ReservationDTO.Response> getAllReservations() {
        return reservationRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ReservationDTO.Response getById(Long id) {
        return toResponse(findById(id));
    }

    public List<ReservationDTO.Response> getByCustomer(Long customerId) {
        return reservationRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ReservationDTO.Response createReservation(ReservationDTO.Request request) {
        RestaurantTable table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new ResourceNotFoundException("Table", request.getTableId()));

        if (table.getCapacity() < request.getPartySize()) {
            throw new BusinessException("Table capacity (" + table.getCapacity() +
                    ") is less than party size (" + request.getPartySize() + ")");
        }

        LocalDateTime endTime = request.getEndTime() != null
                ? request.getEndTime()
                : request.getReservationTime().plusHours(2);

        // Check for conflicts
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                request.getTableId(), request.getReservationTime(), endTime);

        if (!conflicts.isEmpty()) {
            throw new BusinessException("Table " + table.getTableNumber() +
                    " is already reserved for the requested time slot");
        }

        Reservation reservation = Reservation.builder()
                .table(table)
                .reservationTime(request.getReservationTime())
                .endTime(endTime)
                .partySize(request.getPartySize())
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .specialRequests(request.getSpecialRequests())
                .status(Reservation.ReservationStatus.CONFIRMED)
                .build();

        if (request.getCustomerId() != null) {
            User customer = userRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));
            reservation.setCustomer(customer);
        }

        // Mark table as reserved
        table.setStatus(RestaurantTable.TableStatus.RESERVED);
        tableRepository.save(table);

        return toResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationDTO.Response cancelReservation(Long id) {
        Reservation reservation = findById(id);
        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new BusinessException("Reservation is already cancelled");
        }
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);

        // Free the table
        RestaurantTable table = reservation.getTable();
        table.setStatus(RestaurantTable.TableStatus.AVAILABLE);
        tableRepository.save(table);

        return toResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationDTO.Response completeReservation(Long id) {
        Reservation reservation = findById(id);
        reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
        return toResponse(reservationRepository.save(reservation));
    }

    private Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }

    private ReservationDTO.Response toResponse(Reservation r) {
        return ReservationDTO.Response.builder()
                .id(r.getId())
                .customerId(r.getCustomer() != null ? r.getCustomer().getId() : null)
                .customerName(r.getCustomerName())
                .customerPhone(r.getCustomerPhone())
                .tableId(r.getTable().getId())
                .tableNumber(r.getTable().getTableNumber())
                .reservationTime(r.getReservationTime())
                .endTime(r.getEndTime())
                .partySize(r.getPartySize())
                .status(r.getStatus())
                .specialRequests(r.getSpecialRequests())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
