package com.project.inventory_management_system.service;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface AdminService
{

    ResponseEntity<?> getOrdersByAdmin(String username, int offset, int limit);

    ResponseEntity<?> getOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size);

    ResponseEntity<?> getOrdersFilterStatus(String username, String status, int page, int size);

    ResponseEntity<?> getOrdersSearch(String username, String keyword, int page, int size);
}
