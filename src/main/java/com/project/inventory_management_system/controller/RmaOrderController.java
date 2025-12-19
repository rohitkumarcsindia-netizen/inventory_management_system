package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.entity.RmaApproval;
import com.project.inventory_management_system.service.RmaService;
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
@RequestMapping("/api/v1/orders/rma")
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
    public ResponseEntity<?> passedOrder(HttpServletRequest request, @PathVariable Long orderId, @RequestBody RmaApproval comments)
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

    //Search Filter
    @GetMapping("/date-filter")
    public ResponseEntity<?> getRmaOrdersFilterDate(
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

        return rmaService.getRmaOrdersFilterDate(userDetails.getUsername(), start, end, page, size);

    }

    @GetMapping("/complete")
    public ResponseEntity<?> getCompleteOrdersForRma(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return rmaService.getCompleteOrdersForRma(userDetails.getUsername(), offset, limit);

    }


    //Universal searching
    @GetMapping("/search")
    public ResponseEntity<?> getRmaOrdersSearch(
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

        return rmaService.getRmaOrdersSearch(userDetails.getUsername(), keyword, page, size);

    }

    //Search Filter
    @GetMapping("/complete/date-filter")
    public ResponseEntity<?> getRmaCompleteOrdersFilterDate(
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

        return rmaService.getRmaCompleteOrdersFilterDate(userDetails.getUsername(), start, end, page, size);

    }

    @GetMapping("/complete/status-filter")
    public ResponseEntity<?> getRmaCompleteOrdersFilterStatus(
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

        return rmaService.getRmaCompleteOrdersFilterStatus(userDetails.getUsername(), status, page, size);

    }

    //Universal searching
    @GetMapping("/complete/search")
    public ResponseEntity<?> getRmaCompleteOrdersFilterSearch(
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

        return rmaService.getRmaCompleteOrdersFilterSearch(userDetails.getUsername(), keyword, page, size);

    }

}
