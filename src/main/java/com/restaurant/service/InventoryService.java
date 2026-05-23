package com.restaurant.service;

import com.restaurant.dto.InventoryDTO;
import com.restaurant.exception.BusinessException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.model.Inventory;
import com.restaurant.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public List<InventoryDTO.Response> getAllItems() {
        return inventoryRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public InventoryDTO.Response getById(Long id) {
        return toResponse(findById(id));
    }

    public List<InventoryDTO.Response> getLowStockItems() {
        return inventoryRepository.findLowStockItems().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public InventoryDTO.Response createItem(InventoryDTO.Request request) {
        Inventory item = Inventory.builder()
                .itemName(request.getItemName())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .minimumThreshold(request.getMinimumThreshold() != null ? request.getMinimumThreshold() : BigDecimal.ZERO)
                .costPerUnit(request.getCostPerUnit())
                .supplierName(request.getSupplierName())
                .expiryDate(request.getExpiryDate())
                .lastRestocked(LocalDateTime.now())
                .build();
        return toResponse(inventoryRepository.save(item));
    }

    @Transactional
    public InventoryDTO.Response updateItem(Long id, InventoryDTO.Request request) {
        Inventory item = findById(id);
        item.setItemName(request.getItemName());
        item.setQuantity(request.getQuantity());
        item.setUnit(request.getUnit());
        if (request.getMinimumThreshold() != null) item.setMinimumThreshold(request.getMinimumThreshold());
        if (request.getCostPerUnit() != null) item.setCostPerUnit(request.getCostPerUnit());
        if (request.getSupplierName() != null) item.setSupplierName(request.getSupplierName());
        if (request.getExpiryDate() != null) item.setExpiryDate(request.getExpiryDate());
        return toResponse(inventoryRepository.save(item));
    }

    @Transactional
    public InventoryDTO.Response restockItem(Long id, BigDecimal quantity) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Restock quantity must be positive");
        }
        Inventory item = findById(id);
        item.setQuantity(item.getQuantity().add(quantity));
        item.setLastRestocked(LocalDateTime.now());
        return toResponse(inventoryRepository.save(item));
    }

    @Transactional
    public InventoryDTO.Response deductItem(Long id, BigDecimal quantity) {
        Inventory item = findById(id);
        if (item.getQuantity().compareTo(quantity) < 0) {
            throw new BusinessException("Insufficient stock for: " + item.getItemName());
        }
        item.setQuantity(item.getQuantity().subtract(quantity));
        return toResponse(inventoryRepository.save(item));
    }

    @Transactional
    public void deleteItem(Long id) {
        inventoryRepository.delete(findById(id));
    }

    private Inventory findById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item", id));
    }

    private InventoryDTO.Response toResponse(Inventory i) {
        return InventoryDTO.Response.builder()
                .id(i.getId())
                .itemName(i.getItemName())
                .quantity(i.getQuantity())
                .unit(i.getUnit())
                .minimumThreshold(i.getMinimumThreshold())
                .costPerUnit(i.getCostPerUnit())
                .supplierName(i.getSupplierName())
                .lastRestocked(i.getLastRestocked())
                .expiryDate(i.getExpiryDate())
                .lowStock(i.isLowStock())
                .build();
    }
}
