package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.service.CloudOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class CloudOrderController
{
    private final CloudOrderService cloudOrderService;
    private final OrderRepository orderRepository;


    @GetMapping("/cloud/pending")
    public ResponseEntity<?> getOrderCreateTicketForCloud(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        ResponseEntity<?> serviceResponse =
                cloudOrderService.getOrderCreateTicketForCloud(userDetails.getUsername(), offset, limit);

        List<OrdersDto> orders = (List<OrdersDto>) serviceResponse.getBody();


        if (orders.isEmpty())
        {
            return ResponseEntity.ok(Map.of("message", "No orders found"));
        }

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.countByStatus("CLOUD_PENDING"),
                "orders", orders
        ));
    }
}
