package com.restaurant.controller;

import com.restaurant.dto.MenuItemDTO;
import com.restaurant.model.MenuItem;
import com.restaurant.service.MenuItemService;
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
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Tag(name = "Menu", description = "Menu item management")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping
    @Operation(summary = "Get all menu items")
    public ResponseEntity<List<MenuItemDTO.Response>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemService.getAllMenuItems());
    }

    @GetMapping("/available")
    @Operation(summary = "Get all available menu items")
    public ResponseEntity<List<MenuItemDTO.Response>> getAvailableItems() {
        return ResponseEntity.ok(menuItemService.getAvailableItems());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get menu items by category")
    public ResponseEntity<List<MenuItemDTO.Response>> getByCategory(@PathVariable MenuItem.Category category) {
        return ResponseEntity.ok(menuItemService.getByCategory(category));
    }

    @GetMapping("/search")
    @Operation(summary = "Search menu items by name")
    public ResponseEntity<List<MenuItemDTO.Response>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(menuItemService.searchByName(keyword));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get menu item by ID")
    public ResponseEntity<MenuItemDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create menu item", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MenuItemDTO.Response> create(@Valid @RequestBody MenuItemDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuItemService.createMenuItem(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update menu item", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MenuItemDTO.Response> update(@PathVariable Long id,
                                                        @Valid @RequestBody MenuItemDTO.Request request) {
        return ResponseEntity.ok(menuItemService.updateMenuItem(id, request));
    }

    @PatchMapping("/{id}/toggle-availability")
    @Operation(summary = "Toggle menu item availability", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MenuItemDTO.Response> toggleAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.toggleAvailability(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete menu item", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
