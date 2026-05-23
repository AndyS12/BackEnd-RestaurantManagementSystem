package com.restaurant.repository;

import com.restaurant.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCustomerId(Long customerId);
    List<Reservation> findByTableId(Long tableId);
    List<Reservation> findByStatus(Reservation.ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.table.id = :tableId AND r.status = 'CONFIRMED' " +
            "AND r.reservationTime < :endTime AND r.endTime > :startTime")
    List<Reservation> findConflictingReservations(Long tableId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT r FROM Reservation r WHERE r.reservationTime BETWEEN :start AND :end")
    List<Reservation> findReservationsForPeriod(LocalDateTime start, LocalDateTime end);
}
