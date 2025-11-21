package com.project.inventory_management_system.service;


import org.springframework.http.ResponseEntity;

public interface ScmOrderService
{

    //SCM Team Method
    ResponseEntity<?> getApprovedOrdersForScm(String username, int offset, int limit);

    ResponseEntity<?> createJiraTicket(String username, Long orderId);
}
