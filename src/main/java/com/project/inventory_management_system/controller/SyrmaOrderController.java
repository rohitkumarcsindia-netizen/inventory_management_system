package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.dto.SyrmaOrdersDto;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.service.SyrmaOrderService;
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
@RequestMapping("/api/orders/syrma")
public class SyrmaOrderController
{
    private final SyrmaOrderService syrmaOrderService;
    private final OrderRepository orderRepository;


    @GetMapping("/pending")
    public ResponseEntity<?> getPendingOrdersForSyrma(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return syrmaOrderService.getPendingOrdersForSyrma(userDetails.getUsername(), offset, limit);

    }

    @PostMapping("/production-testing/{orderId}")
    public ResponseEntity<?> productionAndTestingComplete(HttpServletRequest request, @PathVariable Long orderId, @RequestBody SyrmaOrdersDto syrmaComments)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return syrmaOrderService.productionAndTestingComplete(userDetails.getUsername(), orderId, syrmaComments);
    }


    @GetMapping("/complete")
    public ResponseEntity<?> getCompleteOrdersForSyrma(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return syrmaOrderService.getCompleteOrdersForSyrma(userDetails.getUsername(), offset, limit);

    }

    //Search Filter
    @GetMapping("/date-filter")
    public ResponseEntity<?> getSyrmaOrdersFilterDate(
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

        return syrmaOrderService.getSyrmaOrdersFilterDate(userDetails.getUsername(), start, end, page, size);

    }

    @GetMapping("/status-filter")
    public ResponseEntity<?> getSyrmaOrdersFilterStatus(
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

        return syrmaOrderService.getSyrmaOrdersFilterStatus(userDetails.getUsername(), status, page, size);

    }

    //Universal searching
    @GetMapping("/search")
    public ResponseEntity<?> getSyrmaOrdersSearch(
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

        return syrmaOrderService.getSyrmaOrdersSearch(userDetails.getUsername(), keyword, page, size);

    }

    //Search Filter Complete button
    @GetMapping("/complete/date-filter")
    public ResponseEntity<?> getSyrmaCompleteOrdersFilterDate(
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

        return syrmaOrderService.getSyrmaCompleteOrdersFilterDate(userDetails.getUsername(), start, end, page, size);

    }

    @GetMapping("/complete/status-filter")
    public ResponseEntity<?> getSyrmaCompleteOrdersFilterStatus(
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

        return syrmaOrderService.getSyrmaCompleteOrdersFilterStatus(userDetails.getUsername(), status, page, size);

    }

    //Universal searching
    @GetMapping("/complete/search")
    public ResponseEntity<?> getSyrmaCompleteOrdersFilterSearch(
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

        return syrmaOrderService.getSyrmaCompleteOrdersFilterSearch(userDetails.getUsername(), keyword, page, size);

    }

    @PostMapping("/re-production-testing/{orderId}")
    public ResponseEntity<?> reProductionAndTestingComplete(HttpServletRequest request, @PathVariable Long orderId, @RequestBody SyrmaOrdersDto syrmaComments)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return syrmaOrderService.reProductionAndTestingComplete(userDetails.getUsername(), orderId, syrmaComments);
    }
}
