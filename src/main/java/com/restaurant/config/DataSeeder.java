package com.restaurant.config;

import com.restaurant.model.*;
import com.restaurant.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final TableRepository tableRepository;
    private final InventoryRepository inventoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded, skipping...");
            return;
        }

        log.info("Seeding database with initial data...");

        // Create users
        userRepository.save(User.builder().username("admin").email("admin@restaurant.com")
                .password(passwordEncoder.encode("admin123")).role(User.Role.ADMIN).build());
        userRepository.save(User.builder().username("manager").email("manager@restaurant.com")
                .password(passwordEncoder.encode("manager123")).role(User.Role.MANAGER).build());
        userRepository.save(User.builder().username("waiter1").email("waiter1@restaurant.com")
                .password(passwordEncoder.encode("waiter123")).role(User.Role.WAITER).build());
        userRepository.save(User.builder().username("customer1").email("customer1@restaurant.com")
                .password(passwordEncoder.encode("cust123")).role(User.Role.CUSTOMER).build());

        // Create tables
        for (int i = 1; i <= 10; i++) {
            int capacity = (i <= 4) ? 2 : (i <= 8) ? 4 : 8;
            String location = (i <= 5) ? "Indoor" : "Outdoor";
            tableRepository.save(RestaurantTable.builder()
                    .tableNumber(i).capacity(capacity)
                    .status(RestaurantTable.TableStatus.AVAILABLE).location(location).build());
        }

        // Create menu items
        menuItemRepository.save(MenuItem.builder().name("Tomato Soup").description("Fresh tomato soup with cream")
                .price(new BigDecimal("120.00")).category(MenuItem.Category.SOUP).available(true).preparationTimeMinutes(10).build());
        menuItemRepository.save(MenuItem.builder().name("Caesar Salad").description("Classic caesar salad")
                .price(new BigDecimal("180.00")).category(MenuItem.Category.SALAD).available(true).preparationTimeMinutes(8).build());
        menuItemRepository.save(MenuItem.builder().name("Paneer Tikka").description("Grilled cottage cheese with spices")
                .price(new BigDecimal("280.00")).category(MenuItem.Category.APPETIZER).available(true).preparationTimeMinutes(15).build());
        menuItemRepository.save(MenuItem.builder().name("Butter Chicken").description("Creamy tomato-based chicken curry")
                .price(new BigDecimal("380.00")).category(MenuItem.Category.MAIN_COURSE).available(true).preparationTimeMinutes(20).build());
        menuItemRepository.save(MenuItem.builder().name("Dal Makhani").description("Slow cooked black lentils")
                .price(new BigDecimal("280.00")).category(MenuItem.Category.MAIN_COURSE).available(true).preparationTimeMinutes(15).build());
        menuItemRepository.save(MenuItem.builder().name("Veg Biryani").description("Fragrant basmati rice with vegetables")
                .price(new BigDecimal("320.00")).category(MenuItem.Category.MAIN_COURSE).available(true).preparationTimeMinutes(25).build());
        menuItemRepository.save(MenuItem.builder().name("Gulab Jamun").description("Soft milk solids in sugar syrup")
                .price(new BigDecimal("120.00")).category(MenuItem.Category.DESSERT).available(true).preparationTimeMinutes(5).build());
        menuItemRepository.save(MenuItem.builder().name("Mango Lassi").description("Sweet mango yogurt drink")
                .price(new BigDecimal("80.00")).category(MenuItem.Category.BEVERAGE).available(true).preparationTimeMinutes(5).build());
        menuItemRepository.save(MenuItem.builder().name("Chef Special Thali").description("Complete meal with variety of dishes")
                .price(new BigDecimal("550.00")).category(MenuItem.Category.SPECIAL).available(true).preparationTimeMinutes(30).build());

        // Create inventory
        inventoryRepository.save(Inventory.builder().itemName("Chicken").quantity(new BigDecimal("50"))
                .unit("kg").minimumThreshold(new BigDecimal("10")).costPerUnit(new BigDecimal("200")).supplierName("Fresh Farms").build());
        inventoryRepository.save(Inventory.builder().itemName("Tomatoes").quantity(new BigDecimal("30"))
                .unit("kg").minimumThreshold(new BigDecimal("5")).costPerUnit(new BigDecimal("40")).supplierName("Green Veggies").build());
        inventoryRepository.save(Inventory.builder().itemName("Basmati Rice").quantity(new BigDecimal("100"))
                .unit("kg").minimumThreshold(new BigDecimal("20")).costPerUnit(new BigDecimal("80")).supplierName("Rice Mills").build());
        inventoryRepository.save(Inventory.builder().itemName("Paneer").quantity(new BigDecimal("20"))
                .unit("kg").minimumThreshold(new BigDecimal("5")).costPerUnit(new BigDecimal("300")).supplierName("Dairy Direct").build());
        inventoryRepository.save(Inventory.builder().itemName("Cooking Oil").quantity(new BigDecimal("40"))
                .unit("liters").minimumThreshold(new BigDecimal("10")).costPerUnit(new BigDecimal("150")).supplierName("Oil Depot").build());

        log.info("Database seeding complete!");
    }
}
