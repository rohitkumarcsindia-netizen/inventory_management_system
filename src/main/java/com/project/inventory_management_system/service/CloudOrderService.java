package com.project.inventory_management_system.service;

import com.project.inventory_management_system.entity.CloudApproval;
import org.springframework.http.ResponseEntity;

public interface CloudOrderService
{
    //Cloud Team Method
    ResponseEntity<?> getOrderPendingForCloud(String username, int offset, int limit);

    ResponseEntity<?> getCompleteOrdersForScm(String username, int offset, int limit);

    ResponseEntity<?> updateJiraDetails(String username, Long orderId, CloudApproval jiraDetails);
}
