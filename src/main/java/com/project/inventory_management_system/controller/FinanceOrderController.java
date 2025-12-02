package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.dto.FinanceOrderDto;
import com.project.inventory_management_system.dto.FinanceOrdersHistoryDto;
import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.repository.FinanceApprovalRepository;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.service.FinanceOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/orders/finance")
public class FinanceOrderController
{
    private final FinanceOrderService financeOrderService;
    private final OrderRepository orderRepository;
    private final FinanceApprovalRepository financeApprovalRepository;


    @GetMapping("/pending")
    public ResponseEntity<?> getPendingOrdersForFinance(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        ResponseEntity<?> serviceResponse =
                financeOrderService.getPendingOrdersForFinance(userDetails.getUsername(), offset, limit);

        return  serviceResponse;
    }



    @GetMapping("/complete")
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

        ResponseEntity<?> serviceResponse =
                financeOrderService.getCompleteOrdersForFinance(userDetails.getUsername(), offset, limit);

        return serviceResponse;
    }



    @PutMapping("/approve/{orderId}")
    public ResponseEntity<?> approveOrder(HttpServletRequest request, @PathVariable Long orderId, @RequestBody String reason)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return financeOrderService.approveOrder(userDetails.getUsername(), orderId, reason);
    }




    @PutMapping("/reject/{orderId}")
    public ResponseEntity<?> rejectOrder(HttpServletRequest request, @PathVariable Long orderId,@RequestBody String reason)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return financeOrderService.rejectOrder(userDetails.getUsername(), orderId, reason);
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

        ResponseEntity<?> serviceResponse = financeOrderService.getOrdersFilterDate(userDetails.getUsername(), start, end, page, size);

        return serviceResponse;
    }

    //Universal searching
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

        ResponseEntity<?> serviceResponse = financeOrderService.getOrdersSearch(userDetails.getUsername(), keyword, page, size);

       return serviceResponse;
    }
    //Complete button searching filter
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

        ResponseEntity<?> serviceResponse = financeOrderService.getOrdersFilterStatus(userDetails.getUsername(), status, page, size);

       return serviceResponse;
    }

    //Search Filter
    @GetMapping("/complete/date-filter")
    public ResponseEntity<?> getCompleteOrdersFilterDate(
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

        ResponseEntity<?> serviceResponse = financeOrderService.getCompleteOrdersFilterDate(userDetails.getUsername(), start, end, page, size);

        return serviceResponse;
    }

}
