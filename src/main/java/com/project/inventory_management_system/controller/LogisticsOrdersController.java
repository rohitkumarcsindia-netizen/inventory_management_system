package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.entity.LogisticsDetails;
import com.project.inventory_management_system.service.LogisticsOrderService;
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
@RequestMapping("/api/orders/logistic")
public class LogisticsOrdersController
{

    private final LogisticsOrderService logisticsOrderService;

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingOrdersForLogistic(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return logisticsOrderService.getPendingOrdersForLogistic(userDetails.getUsername(), offset, limit);

    }

    @PostMapping("/shipping-details/{orderId}")
    public ResponseEntity<?> fillShippingDetails(HttpServletRequest request, @PathVariable Long orderId, @RequestBody LogisticsDetails shippingDetails)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return logisticsOrderService.fillShippingDetails(userDetails.getUsername(), orderId, shippingDetails);
    }

    @PutMapping("/delivery-details/{orderId}")
    public ResponseEntity<?> fillDeliveryDetails(HttpServletRequest request, @PathVariable Long orderId, @RequestBody LogisticsDetails deliveryDetails)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return logisticsOrderService.fillDeliveryDetails(userDetails.getUsername(), orderId, deliveryDetails);
    }

    @PutMapping("/pdi-pass/{orderId}")
    public ResponseEntity<?> fillPassPdiDetails(HttpServletRequest request, @PathVariable Long orderId, @RequestBody LogisticsDetails pdiComments)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return logisticsOrderService.fillPassPdiDetails(userDetails.getUsername(), orderId, pdiComments);
    }

    @PutMapping("/pdi-fail/{orderId}")
    public ResponseEntity<?> fillFailPdiDetails(HttpServletRequest request, @PathVariable Long orderId, @RequestBody LogisticsDetails pdiComments)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return logisticsOrderService.fillFailPdiDetails(userDetails.getUsername(), orderId, pdiComments);
    }

    @GetMapping("/complete")
    public ResponseEntity<?> getCompleteOrdersForLogistics(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return logisticsOrderService.getCompleteOrdersForLogistics(userDetails.getUsername(), offset, limit);

    }

    //Search Filter
    @GetMapping("/date-filter")
    public ResponseEntity<?> getLogisticOrdersFilterDate(
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

        return logisticsOrderService.getLogisticOrdersFilterDate(userDetails.getUsername(), start, end, page, size);

    }

    @GetMapping("/status-filter")
    public ResponseEntity<?> getLogisticOrdersFilterStatus(
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

        return logisticsOrderService.getLogisticOrdersFilterStatus(userDetails.getUsername(), status, page, size);

    }

    //Universal searching
    @GetMapping("/search")
    public ResponseEntity<?> getOrdersSearchForLogistic(
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

        return logisticsOrderService.getOrdersSearchForLogistic(userDetails.getUsername(), keyword, page, size);

    }

    //Search Filter Complete button
    @GetMapping("/complete/date-filter")
    public ResponseEntity<?> getLogisticCompleteOrdersFilterDate(
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

        return logisticsOrderService.getLogisticCompleteOrdersFilterDate(userDetails.getUsername(), start, end, page, size);

    }

    //Complete button searching filter
    @GetMapping("/complete/status-filter")
    public ResponseEntity<?> getLogisticCompleteOrdersFilterStatus(
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

        return logisticsOrderService.getLogisticCompleteOrdersFilterStatus(userDetails.getUsername(), status, page, size);

    }

    //Universal searching
    @GetMapping("/complete/search")
    public ResponseEntity<?> getLogisticCompleteOrdersFilterSearch(
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

        return logisticsOrderService.getLogisticCompleteOrdersFilterSearch(userDetails.getUsername(), keyword, page, size);

    }

}
