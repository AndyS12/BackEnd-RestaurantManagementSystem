package com.restaurant.service;

import com.restaurant.dto.OrderDTO;
import com.restaurant.exception.BusinessException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.model.*;
import com.restaurant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10% tax

    @Transactional
    public OrderDTO.Response createOrder(OrderDTO.CreateRequest request) {
        RestaurantTable table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new ResourceNotFoundException("Table", request.getTableId()));

        if (table.getStatus() == RestaurantTable.TableStatus.MAINTENANCE) {
            throw new BusinessException("Table " + table.getTableNumber() + " is under maintenance");
        }

        Order order = Order.builder()
                .table(table)
                .status(Order.OrderStatus.PENDING)
                .specialInstructions(request.getSpecialInstructions())
                .discountAmount(BigDecimal.ZERO)
                .orderItems(new ArrayList<>())
                .build();

        if (request.getWaiterId() != null) {
            User waiter = userRepository.findById(request.getWaiterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Waiter", request.getWaiterId()));
            order.setWaiter(waiter);
        }
        if (request.getCustomerId() != null) {
            User customer = userRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));
            order.setCustomer(customer);
        }

        // Save order first to get ID
        order = orderRepository.save(order);

        // Build order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderDTO.OrderItemRequest itemReq : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemReq.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("MenuItem", itemReq.getMenuItemId()));

            if (!menuItem.isAvailable()) {
                throw new BusinessException("Menu item '" + menuItem.getName() + "' is currently unavailable");
            }

            BigDecimal unitPrice = menuItem.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItem(menuItem)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(menuItem.getPrice())
                    .subtotal(subtotal)
                    .specialRequest(itemReq.getSpecialRequest())
                    .build();
            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }

        BigDecimal taxAmount = totalAmount.multiply(TAX_RATE);
        BigDecimal finalAmount = totalAmount.add(taxAmount).subtract(order.getDiscountAmount());

        order.setTotalAmount(totalAmount);
        order.setTaxAmount(taxAmount);
        order.setFinalAmount(finalAmount);

        // Update table status
        table.setStatus(RestaurantTable.TableStatus.OCCUPIED);
        tableRepository.save(table);

        return toResponse(orderRepository.save(order));
    }

    public List<OrderDTO.Response> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public OrderDTO.Response getOrderById(Long id) {
        return toResponse(findById(id));
    }

    public List<OrderDTO.Response> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<OrderDTO.Response> getOrdersByTable(Long tableId) {
        return orderRepository.findByTableId(tableId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO.Response updateOrderStatus(Long id, Order.OrderStatus newStatus) {
        Order order = findById(id);

        validateStatusTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);

        // Free table when order is completed
        if (newStatus == Order.OrderStatus.PAID || newStatus == Order.OrderStatus.CANCELLED) {
            if (order.getTable() != null) {
                order.getTable().setStatus(RestaurantTable.TableStatus.AVAILABLE);
                tableRepository.save(order.getTable());
            }
        }

        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderDTO.Response applyDiscount(Long id, BigDecimal discountAmount) {
        Order order = findById(id);
        if (order.getStatus() == Order.OrderStatus.PAID) {
            throw new BusinessException("Cannot apply discount to a paid order");
        }
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(order.getTotalAmount().add(order.getTaxAmount()).subtract(discountAmount));
        return toResponse(orderRepository.save(order));
    }

    public Double getTotalRevenue(LocalDateTime start, LocalDateTime end) {
        Double revenue = orderRepository.getTotalRevenueForPeriod(start, end);
        return revenue != null ? revenue : 0.0;
    }

    private void validateStatusTransition(Order.OrderStatus current, Order.OrderStatus next) {
        boolean valid = switch (current) {
            case PENDING -> next == Order.OrderStatus.CONFIRMED || next == Order.OrderStatus.CANCELLED;
            case CONFIRMED -> next == Order.OrderStatus.PREPARING || next == Order.OrderStatus.CANCELLED;
            case PREPARING -> next == Order.OrderStatus.READY;
            case READY -> next == Order.OrderStatus.SERVED;
            case SERVED -> next == Order.OrderStatus.PAID;
            default -> false;
        };
        if (!valid) {
            throw new BusinessException("Invalid status transition from " + current + " to " + next);
        }
    }

    private Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    private OrderDTO.Response toResponse(Order order) {
        List<OrderDTO.OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> OrderDTO.OrderItemResponse.builder()
                        .id(item.getId())
                        .menuItemId(item.getMenuItem().getId())
                        .menuItemName(item.getMenuItem().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .specialRequest(item.getSpecialRequest())
                        .build())
                .collect(Collectors.toList());

        return OrderDTO.Response.builder()
                .id(order.getId())
                .tableId(order.getTable() != null ? order.getTable().getId() : null)
                .tableNumber(order.getTable() != null ? order.getTable().getTableNumber() : null)
                .waiterName(order.getWaiter() != null ? order.getWaiter().getUsername() : null)
                .customerName(order.getCustomer() != null ? order.getCustomer().getUsername() : null)
                .items(itemResponses)
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .taxAmount(order.getTaxAmount())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .specialInstructions(order.getSpecialInstructions())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
