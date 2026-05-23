package com.restaurant;

import com.restaurant.exception.BusinessException;
import com.restaurant.model.*;
import com.restaurant.repository.*;
import com.restaurant.service.OrderService;
import com.restaurant.dto.OrderDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private TableRepository tableRepository;
    @Mock private UserRepository userRepository;
    @Mock private MenuItemRepository menuItemRepository;

    @InjectMocks private OrderService orderService;

    private RestaurantTable table;
    private MenuItem menuItem;

    @BeforeEach
    void setUp() {
        table = RestaurantTable.builder()
                .id(1L).tableNumber(1).capacity(4)
                .status(RestaurantTable.TableStatus.AVAILABLE).build();

        menuItem = MenuItem.builder()
                .id(1L).name("Butter Chicken")
                .price(new BigDecimal("380.00")).available(true).build();
    }

    @Test
    void createOrder_shouldSucceed() {
        OrderDTO.OrderItemRequest itemReq = new OrderDTO.OrderItemRequest();
        itemReq.setMenuItemId(1L);
        itemReq.setQuantity(2);

        OrderDTO.CreateRequest request = OrderDTO.CreateRequest.builder()
                .tableId(1L).items(List.of(itemReq)).build();

        Order savedOrder = Order.builder()
                .id(1L).table(table).status(Order.OrderStatus.PENDING)
                .orderItems(new ArrayList<>()).discountAmount(BigDecimal.ZERO).build();

        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderDTO.Response response = orderService.createOrder(request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        verify(tableRepository).save(any(RestaurantTable.class));
    }

    @Test
    void createOrder_withMaintenanceTable_shouldThrow() {
        table.setStatus(RestaurantTable.TableStatus.MAINTENANCE);
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));

        OrderDTO.CreateRequest request = OrderDTO.CreateRequest.builder()
                .tableId(1L).items(List.of()).build();

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("maintenance");
    }

    @Test
    void updateStatus_validTransition_shouldSucceed() {
        Order order = Order.builder()
                .id(1L).table(table).status(Order.OrderStatus.PENDING)
                .orderItems(new ArrayList<>()).discountAmount(BigDecimal.ZERO).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        OrderDTO.Response response = orderService.updateOrderStatus(1L, Order.OrderStatus.CONFIRMED);
        assertThat(response.getStatus()).isEqualTo(Order.OrderStatus.CONFIRMED);
    }

    @Test
    void updateStatus_invalidTransition_shouldThrow() {
        Order order = Order.builder()
                .id(1L).table(table).status(Order.OrderStatus.PENDING)
                .orderItems(new ArrayList<>()).discountAmount(BigDecimal.ZERO).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(1L, Order.OrderStatus.PAID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid status transition");
    }
}
