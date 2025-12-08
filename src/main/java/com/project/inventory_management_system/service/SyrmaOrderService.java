package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.SyrmaOrdersDto;
import com.project.inventory_management_system.entity.SyrmaApproval;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface SyrmaOrderService 
{

    ResponseEntity<?> getPendingOrdersForSyrma(String username, int offset, int limit);

    ResponseEntity<?> productionAndTestingComplete(String username, Long orderId, SyrmaOrdersDto syrmaComments);

    ResponseEntity<?> getCompleteOrdersForSyrma(String username, int offset, int limit);

    ResponseEntity<?> getSyrmaOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size);

    ResponseEntity<?> getSyrmaOrdersFilterStatus(String username, String status, int page, int size);

    ResponseEntity<?> getSyrmaOrdersSearch(String username, String keyword, int page, int size);

    ResponseEntity<?> getSyrmaCompleteOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size);

    ResponseEntity<?> getSyrmaCompleteOrdersFilterStatus(String username, String status, int page, int size);

    ResponseEntity<?> getSyrmaCompleteOrdersFilterSearch(String username, String keyword, int page, int size);
}
