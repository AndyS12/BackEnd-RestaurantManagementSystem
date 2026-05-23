package com.restaurant.service;

import com.restaurant.dto.TableDTO;
import com.restaurant.exception.BusinessException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.model.RestaurantTable;
import com.restaurant.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;

    public List<TableDTO.Response> getAllTables() {
        return tableRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TableDTO.Response> getAvailableTables() {
        return tableRepository.findByStatus(RestaurantTable.TableStatus.AVAILABLE)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TableDTO.Response> getAvailableTablesForParty(Integer partySize) {
        return tableRepository.findByCapacityGreaterThanEqual(partySize).stream()
                .filter(t -> t.getStatus() == RestaurantTable.TableStatus.AVAILABLE)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TableDTO.Response getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public TableDTO.Response createTable(TableDTO.Request request) {
        if (tableRepository.findByTableNumber(request.getTableNumber()).isPresent()) {
            throw new BusinessException("Table number " + request.getTableNumber() + " already exists");
        }
        RestaurantTable table = RestaurantTable.builder()
                .tableNumber(request.getTableNumber())
                .capacity(request.getCapacity())
                .status(request.getStatus() != null ? request.getStatus() : RestaurantTable.TableStatus.AVAILABLE)
                .location(request.getLocation())
                .build();
        return toResponse(tableRepository.save(table));
    }

    @Transactional
    public TableDTO.Response updateTable(Long id, TableDTO.Request request) {
        RestaurantTable table = findById(id);
        table.setCapacity(request.getCapacity());
        table.setLocation(request.getLocation());
        if (request.getStatus() != null) table.setStatus(request.getStatus());
        return toResponse(tableRepository.save(table));
    }

    @Transactional
    public TableDTO.Response updateTableStatus(Long id, RestaurantTable.TableStatus status) {
        RestaurantTable table = findById(id);
        table.setStatus(status);
        return toResponse(tableRepository.save(table));
    }

    @Transactional
    public void deleteTable(Long id) {
        RestaurantTable table = findById(id);
        if (table.getStatus() == RestaurantTable.TableStatus.OCCUPIED) {
            throw new BusinessException("Cannot delete an occupied table");
        }
        tableRepository.delete(table);
    }

    private RestaurantTable findById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table", id));
    }

    private TableDTO.Response toResponse(RestaurantTable t) {
        return TableDTO.Response.builder()
                .id(t.getId())
                .tableNumber(t.getTableNumber())
                .capacity(t.getCapacity())
                .status(t.getStatus())
                .location(t.getLocation())
                .build();
    }
}
