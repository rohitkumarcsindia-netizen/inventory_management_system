package com.project.inventory_management_system.service;

import com.project.inventory_management_system.entity.LogisticsDetails;
import org.springframework.http.ResponseEntity;

public interface LogisticsOrderService 
{
    ResponseEntity<?> getPendingOrdersForLogistic(String username, int offset, int limit);

    ResponseEntity<?> fillShippingDetails(String username, Long orderId, LogisticsDetails shippingDetails);

    ResponseEntity<?> fillDeliveryDetails(String username, Long orderId, LogisticsDetails deliveryDetails);

    ResponseEntity<?> fillPassPdiDetails(String username, Long orderId, LogisticsDetails pdiComments);

    ResponseEntity<?> fillFailPdiDetails(String username, Long orderId, LogisticsDetails pdiComments);

    ResponseEntity<?> getCompleteOrdersForLogistics(String username, int offset, int limit);
}
