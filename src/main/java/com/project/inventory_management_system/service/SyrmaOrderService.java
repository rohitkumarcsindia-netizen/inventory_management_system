package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.SyrmaOrdersDto;
import org.springframework.http.ResponseEntity;

public interface SyrmaOrderService 
{

    ResponseEntity<?> getPendingOrdersForSyrma(String username, int offset, int limit);

    ResponseEntity<?> productionAndTestingComplete(String username, Long orderId);

   // ResponseEntity<?> getCompleteOrdersForSyrma(String username, int offset, int limit);
}
