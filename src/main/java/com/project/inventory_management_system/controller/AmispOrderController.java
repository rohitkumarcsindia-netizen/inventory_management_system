package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.dto.AmispOrderDto;
import com.project.inventory_management_system.service.AmispOrderService;
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
@RequestMapping("/api/orders/amisp")
public class AmispOrderController
{
    private final AmispOrderService amispOrderService;


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

        return amispOrderService.getPendingOrdersForAmisp(userDetails.getUsername(), offset, limit);

    }

    @PostMapping("/post-delivery-pdi/{orderId}")
    public ResponseEntity<?> postDeliveryPdiOrder(HttpServletRequest request, @PathVariable Long orderId, @RequestBody AmispOrderDto pdiDetails)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return amispOrderService.postDeliveryPdiOrder(userDetails.getUsername(), orderId, pdiDetails);
    }

    @PostMapping("/pri-delivery-pdi/{orderId}")
    public ResponseEntity<?> priDeliveryPdiOrder(HttpServletRequest request, @PathVariable Long orderId,@RequestBody AmispOrderDto pdiDetails)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return amispOrderService.priDeliveryPdiOrder(userDetails.getUsername(), orderId, pdiDetails);
    }

    @PutMapping("/notify-location-details/{orderId}")
    public ResponseEntity<?> amispNotifyProjectTeamLocationDetails(HttpServletRequest request, @PathVariable Long orderId, @RequestBody AmispOrderDto locationDetails)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return amispOrderService.amispNotifyProjectTeamLocationDetails(userDetails.getUsername(), orderId, locationDetails);
    }

    @GetMapping("/complete")
    public ResponseEntity<?> getCompleteOrdersForAmisp(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return amispOrderService.getCompleteOrdersForAmisp(userDetails.getUsername(), offset, limit);

    }

    //Search Filter
    @GetMapping("/date-filter")
    public ResponseEntity<?> getAmispOrdersFilterDate(
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

        return amispOrderService.getAmispOrdersFilterDate(userDetails.getUsername(), start, end, page, size);

    }

    @GetMapping("/status-filter")
    public ResponseEntity<?> getAmispOrdersFilterStatus(
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

        return amispOrderService.getAmispOrdersFilterStatus(userDetails.getUsername(), status, page, size);

    }

    //Universal searching
    @GetMapping("/search")
    public ResponseEntity<?> getOrdersSearchForAmisp(
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

        return amispOrderService.getOrdersSearchForAmisp(userDetails.getUsername(), keyword, page, size);

    }

    //Search Filter Complete button
    @GetMapping("/complete/date-filter")
    public ResponseEntity<?> getAmispCompleteOrdersFilterDate(
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

        return amispOrderService.getAmispCompleteOrdersFilterDate(userDetails.getUsername(), start, end, page, size);

    }
    //Complete button searching filter
    @GetMapping("/complete/status-filter")
    public ResponseEntity<?> getAmispCompleteOrdersFilterStatus(
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

        return amispOrderService.getAmispCompleteOrdersFilterStatus(userDetails.getUsername(), status, page, size);

    }

    //Universal searching
    @GetMapping("/complete/search")
    public ResponseEntity<?> getAmispCompleteOrdersFilterSearch(
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

        return amispOrderService.getAmispCompleteOrdersFilterSearch(userDetails.getUsername(), keyword, page, size);

    }
}
