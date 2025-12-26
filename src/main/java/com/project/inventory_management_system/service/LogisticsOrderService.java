package com.project.inventory_management_system.service;

import com.project.inventory_management_system.entity.LogisticsDetails;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface LogisticsOrderService 
{
    ResponseEntity<?> getPendingOrdersForLogistic(String username, int offset, int limit);

    ResponseEntity<?> fillShippingDetails(String username, Long orderId, LogisticsDetails shippingDetails);

    ResponseEntity<?> fillDeliveryDetails(String username, Long orderId, LogisticsDetails deliveryDetails);

    ResponseEntity<?> getCompleteOrdersForLogistics(String username, int offset, int limit);

    ResponseEntity<?> getLogisticOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size);

    ResponseEntity<?> getLogisticOrdersFilterStatus(String username, String status, int page, int size);

    ResponseEntity<?> getOrdersSearchForLogistic(String username, String keyword, int page, int size);

    ResponseEntity<?> getLogisticCompleteOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size);

    ResponseEntity<?> getLogisticCompleteOrdersFilterStatus(String username, String status, int page, int size);

    ResponseEntity<?> getLogisticCompleteOrdersFilterSearch(String username, String keyword, int page, int size);
}
