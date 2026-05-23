package com.restaurant.controller;

import com.restaurant.dto.TableDTO;
import com.restaurant.model.RestaurantTable;
import com.restaurant.service.TableService;
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
@RequestMapping("/api/tables")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tables", description = "Restaurant table management")
public class TableController {

    private final TableService tableService;

    @GetMapping
    @Operation(summary = "Get all tables")
    public ResponseEntity<List<TableDTO.Response>> getAllTables() {
        return ResponseEntity.ok(tableService.getAllTables());
    }

    @GetMapping("/available")
    @Operation(summary = "Get available tables")
    public ResponseEntity<List<TableDTO.Response>> getAvailableTables() {
        return ResponseEntity.ok(tableService.getAvailableTables());
    }

    @GetMapping("/available/party-size/{size}")
    @Operation(summary = "Get available tables for a party size")
    public ResponseEntity<List<TableDTO.Response>> getAvailableForParty(@PathVariable Integer size) {
        return ResponseEntity.ok(tableService.getAvailableTablesForParty(size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get table by ID")
    public ResponseEntity<TableDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tableService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new table")
    public ResponseEntity<TableDTO.Response> create(@Valid @RequestBody TableDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableService.createTable(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a table")
    public ResponseEntity<TableDTO.Response> update(@PathVariable Long id,
                                                     @Valid @RequestBody TableDTO.Request request) {
        return ResponseEntity.ok(tableService.updateTable(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update table status")
    public ResponseEntity<TableDTO.Response> updateStatus(@PathVariable Long id,
                                                           @RequestParam RestaurantTable.TableStatus status) {
        return ResponseEntity.ok(tableService.updateTableStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a table")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}
