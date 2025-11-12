package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService 
{

    ResponseEntity<?> createOrder(String username, OrdersDto ordersDto);

    Orders updateOreder(Long orderId,Orders orders);

    Orders deleteOrder(Long orderId);

    List<OrdersDto> getOrdersByUserWithLimitOffset(Users user, int offset, int limit);

}
