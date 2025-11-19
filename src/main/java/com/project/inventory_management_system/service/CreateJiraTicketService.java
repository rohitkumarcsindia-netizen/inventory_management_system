package com.project.inventory_management_system.service;


import org.springframework.http.ResponseEntity;

public interface CreateJiraTicketService
{
    ResponseEntity<?> createJiraTicket(String username, Long orderId);
}
