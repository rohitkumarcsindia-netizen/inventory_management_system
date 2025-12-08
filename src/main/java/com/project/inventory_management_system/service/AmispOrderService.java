package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.AmispOrderDto;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface AmispOrderService
{
    ResponseEntity<?> getPendingOrdersForAmisp(String username, int offset, int limit);

    ResponseEntity<?> postDeliveryPdiOrder(String username, Long orderId, AmispOrderDto pdiDetails);

    ResponseEntity<?> priDeliveryPdiOrder(String username, Long orderId, AmispOrderDto pdiDetails);

    ResponseEntity<?> amispNotifyProjectTeamLocationDetails(String username, Long orderId,AmispOrderDto locationDetails);

    ResponseEntity<?> getCompleteOrdersForAmisp(String username, int offset, int limit);

    ResponseEntity<?> getAmispOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size);
}
