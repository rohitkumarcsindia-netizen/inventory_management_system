package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.Users;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ProjectOrderService
{

    //Project Team Method
    ResponseEntity<?> createOrder(String username, OrdersDto ordersDto);

    ResponseEntity<?> getOrdersByUserWithLimitOffset(String username, int offset, int limit);

    OrdersDto updateOrderDetails(String username, Long orderId, OrdersDto ordersDto);

    String deleteOrder(String username, Long orderId);

    ResponseEntity<?> getOrdersFilterDate(String username, LocalDateTime startDate, LocalDateTime endDate, int page, int size);

    ResponseEntity<?> getOrdersFilterStatus(String username, String status, int page, int size);

    ResponseEntity<?> getOrdersSearch(String username, String keyword, int page, int size);

    ResponseEntity<?> projectTeamNotifyConveyToAmisp(String username, Long orderId);

    ResponseEntity<?> projectTeamNotifyToScmDispatchOrderIsReady(String username, Long orderId);
}
