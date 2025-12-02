package com.project.inventory_management_system.service;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface FinanceOrderService
{
    //Finance Team Method
    ResponseEntity<?> approveOrder(String username, Long orderId, String reason);

    ResponseEntity<?> rejectOrder(String username, Long orderId, String reason);

    ResponseEntity<?> getPendingOrdersForFinance(String username,int offset, int limit);

    ResponseEntity<?> getCompleteOrdersForFinance(String username,int offset, int limit);

    //Pending button Searching Filter Method
    ResponseEntity<?> getOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end,int page,int size);

    ResponseEntity<?> getOrdersSearch(String username, String keyword,int page,int size);

    //Complete button Searching Filter Method
    ResponseEntity<?> getOrdersFilterStatus(String username, String status,int page,int size);

    ResponseEntity<?> getCompleteOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size);

    ResponseEntity<?> getOrdersCompleteSearch(String username, String keyword, int page, int size);
}
