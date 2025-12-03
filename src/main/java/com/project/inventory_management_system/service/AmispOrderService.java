package com.project.inventory_management_system.service;

import org.springframework.http.ResponseEntity;

public interface AmispOrderService
{
    ResponseEntity<?> getPendingOrdersForAmisp(String username, int offset, int limit);
}
