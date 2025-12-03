package com.project.inventory_management_system.service;


import org.springframework.http.ResponseEntity;

public interface RmaService
{

    ResponseEntity<?> getPendingOrdersForRma(String username, int offset, int limit);
}
