package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/orders/admin")
public class AdminController
{
    private final AdminService adminService;


    @GetMapping("/all-orders")
    public ResponseEntity<?> getOrdersWithLimitOffset(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {

        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");


        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return adminService.getOrdersByAdmin(userDetails.getUsername(), offset, limit);


    }

    //Search Filter
    @GetMapping("/date-filter")
    public ResponseEntity<?> getOrdersFilterDate(
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

        return adminService.getOrdersFilterDate(userDetails.getUsername(), start, end,page,size);

    }

    @GetMapping("/status-filter")
    public ResponseEntity<?> getOrdersFilterStatus(
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

        return adminService.getOrdersFilterStatus(userDetails.getUsername(), status, page, size);

    }

    //Universal search bar
    @GetMapping("/search")
    public ResponseEntity<?> getOrdersSearch(
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

        return adminService.getOrdersSearch(userDetails.getUsername(), keyword, page, size);

    }
}
