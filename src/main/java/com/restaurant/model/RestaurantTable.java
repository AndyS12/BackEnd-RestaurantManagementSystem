package com.restaurant.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurant_tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number", nullable = false, unique = true)
    private Integer tableNumber;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private TableStatus status;

    @Column(name = "location")
    private String location; // e.g., "Indoor", "Outdoor", "Private"

    public enum TableStatus {
        AVAILABLE, OCCUPIED, RESERVED, MAINTENANCE
    }
}
