package com.restaurant.controller;

import com.restaurant.dto.InventoryDTO;
import com.restaurant.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Inventory", description = "Inventory management (Admin/Manager only)")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @Operation(summary = "Get all inventory items")
    public ResponseEntity<List<InventoryDTO.Response>> getAll() {
        return ResponseEntity.ok(inventoryService.getAllItems());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get inventory item by ID")
    public ResponseEntity<InventoryDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getById(id));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock items")
    public ResponseEntity<List<InventoryDTO.Response>> getLowStock() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }

    @PostMapping
    @Operation(summary = "Add new inventory item")
    public ResponseEntity<InventoryDTO.Response> create(@Valid @RequestBody InventoryDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createItem(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update inventory item")
    public ResponseEntity<InventoryDTO.Response> update(@PathVariable Long id,
                                                         @Valid @RequestBody InventoryDTO.Request request) {
        return ResponseEntity.ok(inventoryService.updateItem(id, request));
    }

    @PatchMapping("/{id}/restock")
    @Operation(summary = "Restock an inventory item")
    public ResponseEntity<InventoryDTO.Response> restock(@PathVariable Long id,
                                                          @RequestParam BigDecimal quantity) {
        return ResponseEntity.ok(inventoryService.restockItem(id, quantity));
    }

    @PatchMapping("/{id}/deduct")
    @Operation(summary = "Deduct from inventory")
    public ResponseEntity<InventoryDTO.Response> deduct(@PathVariable Long id,
                                                         @RequestParam BigDecimal quantity) {
        return ResponseEntity.ok(inventoryService.deductItem(id, quantity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete inventory item")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
