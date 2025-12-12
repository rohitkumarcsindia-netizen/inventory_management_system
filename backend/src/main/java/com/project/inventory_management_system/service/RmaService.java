package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.FinanceOrderDto;
import com.project.inventory_management_system.entity.RmaApproval;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface RmaService
{

    ResponseEntity<?> getPendingOrdersForRma(String username, int offset, int limit);

    ResponseEntity<?> passedOrder(String username, Long orderId, RmaApproval comments);

    ResponseEntity<?> failedOrder(String username, Long orderId, RmaApproval comments);

    ResponseEntity<?> getCompleteOrdersForRma(String username, int offset, int limit);

    ResponseEntity<?> getRmaOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size);

    ResponseEntity<?> getRmaOrdersSearch(String username, String keyword, int page, int size);

    ResponseEntity<?> getRmaCompleteOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size);

    ResponseEntity<?> getRmaCompleteOrdersFilterStatus(String username, String status, int page, int size);

    ResponseEntity<?> getRmaCompleteOrdersFilterSearch(String username, String keyword, int page, int size);
}
