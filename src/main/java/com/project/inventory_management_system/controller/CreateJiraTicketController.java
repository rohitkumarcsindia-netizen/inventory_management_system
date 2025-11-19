package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.service.CreateJiraTicketService;
import com.project.inventory_management_system.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class CreateJiraTicketController
{
    private final CreateJiraTicketService jiraService;
    private final OrderService orderService;


    @GetMapping("/scm/jira-ticket")
    public ResponseEntity<?> getApprovedOrdersForScm(HttpServletRequest request)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return orderService.getApprovedOrdersForScm(userDetails.getUsername());
    }

    @PostMapping("/scm/jira/create/{orderId}")
    public ResponseEntity<?> createJira(HttpServletRequest request, @PathVariable Long orderId)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return jiraService.createJiraTicket(userDetails.getUsername(),orderId);
    }
}
