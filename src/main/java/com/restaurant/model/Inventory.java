package com.restaurant.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name", nullable = false, unique = true)
    private String itemName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false)
    private String unit; // kg, liters, pieces, etc.

    @Column(name = "minimum_threshold", precision = 10, scale = 2)
    private BigDecimal minimumThreshold;

    @Column(name = "cost_per_unit", precision = 10, scale = 2)
    private BigDecimal costPerUnit;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "last_restocked")
    private LocalDateTime lastRestocked;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    public boolean isLowStock() {
        return quantity.compareTo(minimumThreshold) <= 0;
    }
}
