package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.service.ScmOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class ScmController
{
    private final ScmOrderService scmOrderService;
    private final OrderRepository orderRepository;


    @GetMapping("/scm/pending")
    public ResponseEntity<?> getApprovedOrdersForScm(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        ResponseEntity<?> serviceResponse = scmOrderService.getApprovedOrdersForScm(userDetails.getUsername(), offset, limit);

        List<OrdersDto> orders = (List<OrdersDto>) serviceResponse.getBody();

        if (orders.isEmpty())
        {
            return ResponseEntity.ok(Map.of("message", "No orders found"));
        }

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.countByStatus("SCM_PENDING"),
                "orders", orders
        ));
    }

    @PostMapping("/scm/jira/create/{orderId}")
    public ResponseEntity<?> createJira(HttpServletRequest request, @PathVariable Long orderId)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return scmOrderService.createJiraTicket(userDetails.getUsername(),orderId);
    }
}
