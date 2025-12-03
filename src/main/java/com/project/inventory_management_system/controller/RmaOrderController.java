package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.entity.RmaApproval;
import com.project.inventory_management_system.service.RmaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/orders/rma")
public class RmaOrderController
{
    private final RmaService rmaService;


    @GetMapping("/pending")
    public ResponseEntity<?> getPendingOrdersForRma(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return rmaService.getPendingOrdersForRma(userDetails.getUsername(), offset, limit);
    }

    @PostMapping("/passed/{orderId}")
    public ResponseEntity<?> passedeOrder(HttpServletRequest request, @PathVariable Long orderId, @RequestBody RmaApproval comments)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return rmaService.passedOrder(userDetails.getUsername(), orderId, comments);
    }

    @PostMapping("/failed/{orderId}")
    public ResponseEntity<?> failedOrder(HttpServletRequest request, @PathVariable Long orderId,@RequestBody RmaApproval comments)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return rmaService.failedOrder(userDetails.getUsername(), orderId, comments);
    }
}
