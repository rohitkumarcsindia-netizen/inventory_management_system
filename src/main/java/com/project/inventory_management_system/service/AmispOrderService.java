package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.AmispOrderDto;
import org.springframework.http.ResponseEntity;

public interface AmispOrderService
{
    ResponseEntity<?> getPendingOrdersForAmisp(String username, int offset, int limit);

    ResponseEntity<?> postDeliveryPdiOrder(String username, Long orderId, AmispOrderDto pdiDetails);

    ResponseEntity<?> priDeliveryPdiOrder(String username, Long orderId, AmispOrderDto pdiDetails);

    ResponseEntity<?> amispNotifyProjectTeamLocationDetails(String username, Long orderId,AmispOrderDto locationDetails);
}
