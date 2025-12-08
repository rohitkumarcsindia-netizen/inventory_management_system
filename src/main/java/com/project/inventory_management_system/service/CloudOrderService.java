package com.project.inventory_management_system.service;

import com.project.inventory_management_system.entity.CloudApproval;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface CloudOrderService
{
    //Cloud Team Method
    ResponseEntity<?> getOrderPendingForCloud(String username, int offset, int limit);

    ResponseEntity<?> getCompleteOrdersForScm(String username, int offset, int limit);

    ResponseEntity<?> updateJiraDetails(String username, Long orderId, CloudApproval jiraDetails);

    //pending searching filters method
    ResponseEntity<?> getCloudOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size);

    ResponseEntity<?> getCloudOrdersSearch(String username, String keyword, int page, int size);

    //complete searching filters method
    ResponseEntity<?> getCloudCompleteOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size);

    ResponseEntity<?> getCloudCompleteOrdersFilterStatus(String username, String status, int page, int size);

    ResponseEntity<?> getCloudCompleteOrdersSearch(String username, String keyword, int page, int size);
}
