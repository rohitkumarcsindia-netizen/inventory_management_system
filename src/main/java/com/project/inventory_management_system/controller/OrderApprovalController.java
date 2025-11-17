package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderApprovalController
{
    private final OrderService orderService;


    @PutMapping("/{orderId}/approve")
    public ResponseEntity<?> approveOrder(HttpServletRequest request, @PathVariable Long orderId)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return orderService.approveOrder(userDetails.getUsername(), orderId);
    }

    @PutMapping("/{orderId}/reject")
    public ResponseEntity<?> rejectOrder(HttpServletRequest request, @PathVariable Long orderId)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return orderService.rejectOrder(userDetails.getUsername(), orderId);
    }

}
