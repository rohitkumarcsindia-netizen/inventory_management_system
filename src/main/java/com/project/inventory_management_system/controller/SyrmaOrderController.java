package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.dto.ScmOrdersHistoryDto;
import com.project.inventory_management_system.dto.SyrmaOrdersDto;
import com.project.inventory_management_system.dto.SyrmaOrdersHistoryDto;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.service.SyrmaOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

        ResponseEntity<?> serviceResponse = syrmaOrderService.getPendingOrdersForSyrma(userDetails.getUsername(), offset, limit);

        List<OrdersDto> orders = (List<OrdersDto>) serviceResponse.getBody();

        if (orders.isEmpty())
        {
            return ResponseEntity.ok(Map.of("message", "No orders found"));
        }

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.countByStatus("SYRMA_PENDING"),
                "orders", orders
        ));
    }

    @PutMapping("/{orderId}/start-production")
    public ResponseEntity<?> startProduction(HttpServletRequest request, @PathVariable Long orderId)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return syrmaOrderService.startProduction(userDetails.getUsername(), orderId);
    }

    @GetMapping("/pending/testing")
    public ResponseEntity<?> getPendingTestingOrders(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        ResponseEntity<?> serviceResponse = syrmaOrderService.getPendingTestingOrders(userDetails.getUsername(), offset, limit);

        List<OrdersDto> orders = (List<OrdersDto>) serviceResponse.getBody();

        if (orders.isEmpty())
        {
            return ResponseEntity.ok(Map.of("message", "No orders found"));
        }

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.countByStatus("PRODUCTION STARTED"),
                "orders", orders
        ));
    }

//    @PutMapping("/{orderId}/testing-complete")
//    public ResponseEntity<?> testingComplete(HttpServletRequest request, @PathVariable Long orderId, SyrmaOrdersDto syrmaOrdersDto)
//    {
//        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
//
//        if (userDetails == null)
//        {
//            return ResponseEntity.status(401).body("Unauthorized");
//        }
//
//        return syrmaOrderService.testingComplete(userDetails.getUsername(), orderId, syrmaOrdersDto);
//    }

//    @GetMapping("/complete")
//    public ResponseEntity<?> getCompleteOrdersForSyrma(
//            HttpServletRequest request,
//            @RequestParam(defaultValue = "0") int offset,
//            @RequestParam(defaultValue = "10") int limit)
//    {
//        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
//
//        if (userDetails == null)
//        {
//            return ResponseEntity.status(401).body("Unauthorized");
//        }
//
//        ResponseEntity<?> serviceResponse =
//                syrmaOrderService.getCompleteOrdersForSyrma(userDetails.getUsername(), offset, limit);
//
//        List<SyrmaOrdersHistoryDto> orders = (List<SyrmaOrdersHistoryDto>) serviceResponse.getBody();
//
//        if (orders.isEmpty())
//        {
//            return ResponseEntity.ok(Map.of("message", "No orders found"));
//        }
//
//        return ResponseEntity.ok(Map.of(
//                "offset", offset,
//                "limit", limit,
//                "ordersCount", orderRepository.countByScmAction(),
//                "orders", orders
//        ));
//    }

}
