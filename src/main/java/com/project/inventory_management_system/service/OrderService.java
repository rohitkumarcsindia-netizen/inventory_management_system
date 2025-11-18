package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService 
{

    ResponseEntity<?> createOrder(String username, OrdersDto ordersDto);


    List<OrdersDto> getOrdersByUserWithLimitOffset(Users user, int offset, int limit);

    OrdersDto updateOrderDetails(String username, Long orderId, OrdersDto ordersDto);

    String deleteOrder(String username, Long orderId);


    //Finance Team Method

    ResponseEntity<?> approveOrder(String username, Long orderId);

    ResponseEntity<?> rejectOrder(String username, Long orderId);

    ResponseEntity<?> getPendingOrdersForFinance(String username);
}
