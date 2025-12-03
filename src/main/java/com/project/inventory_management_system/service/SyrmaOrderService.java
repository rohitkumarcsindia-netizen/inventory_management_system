package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.SyrmaOrdersDto;
import com.project.inventory_management_system.entity.SyrmaApproval;
import org.springframework.http.ResponseEntity;

public interface SyrmaOrderService 
{

    ResponseEntity<?> getPendingOrdersForSyrma(String username, int offset, int limit);

    ResponseEntity<?> productionAndTestingComplete(String username, Long orderId, SyrmaOrdersDto syrmaComments);

   // ResponseEntity<?> getCompleteOrdersForSyrma(String username, int offset, int limit);
}
