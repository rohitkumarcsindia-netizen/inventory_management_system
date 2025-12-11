package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.ProjectTeamOrderDto;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface AmispOrderService
{
    ResponseEntity<?> getPendingOrdersForAmisp(String username, int offset, int limit);

    ResponseEntity<?> postDeliveryPdiOrder(String username, Long orderId, ProjectTeamOrderDto pdiDetails);

    ResponseEntity<?> priDeliveryPdiOrder(String username, Long orderId, ProjectTeamOrderDto pdiDetails);

    ResponseEntity<?> amispNotifyProjectTeamLocationDetails(String username, Long orderId, ProjectTeamOrderDto locationDetails);

    ResponseEntity<?> getCompleteOrdersForAmisp(String username, int offset, int limit);

    ResponseEntity<?> getAmispOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size);

    ResponseEntity<?> getAmispOrdersFilterStatus(String username, String status, int page, int size);

    ResponseEntity<?> getOrdersSearchForAmisp(String username, String keyword, int page, int size);

    ResponseEntity<?> getAmispCompleteOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size);

    ResponseEntity<?> getAmispCompleteOrdersFilterStatus(String username, String status, int page, int size);

    ResponseEntity<?> getAmispCompleteOrdersFilterSearch(String username, String keyword, int page, int size);
}
