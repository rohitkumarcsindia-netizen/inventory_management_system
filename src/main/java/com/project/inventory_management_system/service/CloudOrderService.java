package com.project.inventory_management_system.service;

import org.springframework.http.ResponseEntity;

public interface CloudOrderService
{
    //Cloud Team Method
    ResponseEntity<?> getOrderCreateTicketForCloud(String username, int offset, int limit);
}
