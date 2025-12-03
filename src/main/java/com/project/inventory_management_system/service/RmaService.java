package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.FinanceOrderDto;
import com.project.inventory_management_system.entity.RmaApproval;
import org.springframework.http.ResponseEntity;

public interface RmaService
{

    ResponseEntity<?> getPendingOrdersForRma(String username, int offset, int limit);

    ResponseEntity<?> passedOrder(String username, Long orderId, RmaApproval comments);

    ResponseEntity<?> failedOrder(String username, Long orderId, RmaApproval comments);
}
