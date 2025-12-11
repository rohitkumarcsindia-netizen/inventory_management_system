package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.ProjectTeamApproval;
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

    ResponseEntity<?> updateOrderDetails(String username, Long orderId, OrdersDto ordersDto);

    ResponseEntity<?> deleteOrder(String username, Long orderId);

    ResponseEntity<?> getOrdersFilterDate(String username, LocalDateTime startDate, LocalDateTime endDate, int page, int size);

    ResponseEntity<?> getOrdersFilterStatus(String username, String status, int page, int size);

    ResponseEntity<?> getOrdersSearch(String username, String keyword, int page, int size);

    ResponseEntity<?> projectTeamNotifyConveyToAmisp(String username, Long orderId, ProjectTeamApproval projectTeamApproval);

    ResponseEntity<?> projectTeamNotifyToScmDispatchOrderIsReady(String username, Long orderId);

    ResponseEntity<?> projectTeamNotifyToScmLocationDetails(String username, Long orderId);

    ResponseEntity<?> saveOrders(String username, OrdersDto ordersDto);

    ResponseEntity<?> submitOrders(String username, Long orderId, OrdersDto ordersDto);
}
