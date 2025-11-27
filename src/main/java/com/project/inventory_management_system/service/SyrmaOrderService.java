package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.SyrmaOrdersDto;
import org.springframework.http.ResponseEntity;

public interface SyrmaOrderService 
{

    ResponseEntity<?> getPendingOrdersForSyrma(String username, int offset, int limit);

    ResponseEntity<?> startProduction(String username, Long orderId);

    ResponseEntity<?> getPendingTestingOrders(String username, int offset, int limit);

//    ResponseEntity<?> testingComplete(String username, Long orderId, SyrmaOrdersDto syrmaOrdersDto);

   // ResponseEntity<?> getCompleteOrdersForSyrma(String username, int offset, int limit);
}
