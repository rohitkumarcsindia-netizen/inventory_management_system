package com.project.inventory_management_system.service;

import org.springframework.http.ResponseEntity;

public interface FinanceOrderService
{
    //Finance Team Method
    ResponseEntity<?> approveOrder(String username, Long orderId);

    ResponseEntity<?> rejectOrder(String username, Long orderId);

    ResponseEntity<?> getPendingOrdersForFinance(String username,int offset, int limit);

    ResponseEntity<?> getCompleteOrdersForFinance(String username,int offset, int limit);
}
