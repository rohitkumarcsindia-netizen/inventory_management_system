package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.entity.CloudApproval;
import com.project.inventory_management_system.repository.CloudApprovalRepository;
import com.project.inventory_management_system.service.CloudOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api/orders/cloud")
public class CloudOrderController
{
    private final CloudOrderService cloudOrderService;
    private final CloudApprovalRepository cloudApprovalRepository;


    @GetMapping("/pending")
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

    @PostMapping("/update-jira-details/{orderId}")
    public ResponseEntity<?> updateJiraDetails(HttpServletRequest request, @PathVariable Long orderId, @RequestBody CloudApproval jiraDetails)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return cloudOrderService.updateJiraDetails(userDetails.getUsername(), orderId, jiraDetails);
    }

    @GetMapping("/complete")
    public ResponseEntity<?> getCompleteOrdersForCloud(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return cloudOrderService.getCompleteOrdersForCloud(userDetails.getUsername(), offset, limit);

    }


    //Search Filter
    @GetMapping("/date-filter")
    public ResponseEntity<?> getCloudOrdersFilterDate(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23,59,59);

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return cloudOrderService.getCloudOrdersFilterDate(userDetails.getUsername(), start, end, page, size);

    }

    //Universal searching
    @GetMapping("/search")
    public ResponseEntity<?> getCloudOrdersSearch(
            HttpServletRequest request,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return cloudOrderService.getCloudOrdersSearch(userDetails.getUsername(), keyword, page, size);

    }

    //Search Filter
    @GetMapping("/complete/date-filter")
    public ResponseEntity<?> getCloudCompleteOrdersFilterDate(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23,59,59);

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return cloudOrderService.getCloudCompleteOrdersFilterDate(userDetails.getUsername(), start, end, page, size);

    }

    @GetMapping("/complete/status-filter")
    public ResponseEntity<?> getCloudCompleteOrdersFilterStatus(
            HttpServletRequest request,
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return cloudOrderService.getCloudCompleteOrdersFilterStatus(userDetails.getUsername(), status, page, size);

    }

    //Universal searching
    @GetMapping("/complete/search")
    public ResponseEntity<?> getCloudCompleteOrdersFilterSearch(
            HttpServletRequest request,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return cloudOrderService.getCloudCompleteOrdersSearch(userDetails.getUsername(), keyword, page, size);

    }

}
