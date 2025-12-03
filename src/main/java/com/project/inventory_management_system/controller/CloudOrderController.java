package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.entity.CloudApproval;
import com.project.inventory_management_system.repository.CloudApprovalRepository;
import com.project.inventory_management_system.service.CloudOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class CloudOrderController
{
    private final CloudOrderService cloudOrderService;
    private final CloudApprovalRepository cloudApprovalRepository;


    @GetMapping("/cloud/pending")
    public ResponseEntity<?> getPendingOrderForCloud(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return cloudOrderService.getOrderPendingForCloud(userDetails.getUsername(), offset, limit);
    }

    @PostMapping("/cloud/update-jira-details/{orderId}")
    public ResponseEntity<?> updateJiraDetails(HttpServletRequest request, @PathVariable Long orderId, @RequestBody CloudApproval jiraDetails)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return cloudOrderService.updateJiraDetails(userDetails.getUsername(), orderId, jiraDetails);
    }

    @GetMapping("/cloud/complete")
    public ResponseEntity<?> getCompleteOrdersForFinance(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return cloudOrderService.getCompleteOrdersForScm(userDetails.getUsername(), offset, limit);

    }

}
