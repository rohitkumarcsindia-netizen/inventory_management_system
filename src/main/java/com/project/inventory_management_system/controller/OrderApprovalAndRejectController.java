package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderApprovalAndRejectController
{
    private final OrderService orderService;


    @GetMapping("/finance/pending")
    public ResponseEntity<?> getPendingOrdersForFinance(HttpServletRequest request)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return orderService.getPendingOrdersForFinance(userDetails.getUsername());
    }



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
