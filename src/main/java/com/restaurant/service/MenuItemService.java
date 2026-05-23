package com.restaurant.service;

import com.restaurant.dto.MenuItemDTO;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.model.MenuItem;
import com.restaurant.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    @Cacheable("menuItems")
    public List<MenuItemDTO.Response> getAllMenuItems() {
        return menuItemRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "menuItemsByCategory", key = "#category")
    public List<MenuItemDTO.Response> getByCategory(MenuItem.Category category) {
        return menuItemRepository.findByCategory(category).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<MenuItemDTO.Response> getAvailableItems() {
        return menuItemRepository.findByAvailableTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<MenuItemDTO.Response> searchByName(String keyword) {
        return menuItemRepository.searchByName(keyword).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public MenuItemDTO.Response getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    @CacheEvict(value = {"menuItems", "menuItemsByCategory"}, allEntries = true)
    public MenuItemDTO.Response createMenuItem(MenuItemDTO.Request request) {
        MenuItem item = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .available(request.isAvailable())
                .preparationTimeMinutes(request.getPreparationTimeMinutes())
                .imageUrl(request.getImageUrl())
                .build();
        return toResponse(menuItemRepository.save(item));
    }

    @Transactional
    @CacheEvict(value = {"menuItems", "menuItemsByCategory"}, allEntries = true)
    public MenuItemDTO.Response updateMenuItem(Long id, MenuItemDTO.Request request) {
        MenuItem item = findById(id);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCategory(request.getCategory());
        item.setAvailable(request.isAvailable());
        item.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        item.setImageUrl(request.getImageUrl());
        return toResponse(menuItemRepository.save(item));
    }

    @Transactional
    @CacheEvict(value = {"menuItems", "menuItemsByCategory"}, allEntries = true)
    public void deleteMenuItem(Long id) {
        MenuItem item = findById(id);
        menuItemRepository.delete(item);
    }

    @Transactional
    @CacheEvict(value = {"menuItems", "menuItemsByCategory"}, allEntries = true)
    public MenuItemDTO.Response toggleAvailability(Long id) {
        MenuItem item = findById(id);
        item.setAvailable(!item.isAvailable());
        return toResponse(menuItemRepository.save(item));
    }

    private MenuItem findById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", id));
    }

    private MenuItemDTO.Response toResponse(MenuItem item) {
        return MenuItemDTO.Response.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .category(item.getCategory())
                .available(item.isAvailable())
                .preparationTimeMinutes(item.getPreparationTimeMinutes())
                .imageUrl(item.getImageUrl())
                .build();
    }
}
