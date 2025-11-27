package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.dto.FinanceOrdersHistoryDto;
import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.repository.FinanceApprovalRepository;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.service.FinanceOrderService;
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
@RequestMapping("/api/orders")
public class FinanceOrderController
{
    private final FinanceOrderService financeOrderService;
    private final OrderRepository orderRepository;
    private final FinanceApprovalRepository financeApprovalRepository;


    @GetMapping("/finance/pending")
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

        Object body = serviceResponse.getBody();

        if (body instanceof String)
        {
            return ResponseEntity.badRequest().body(body);
        }

        List<OrdersDto> orders = (List<OrdersDto>) serviceResponse.getBody();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.countByStatus("FINANCE PENDING"),
                "orders", orders
        ));
    }



    @GetMapping("/finance/complete")
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

        Object body = serviceResponse.getBody();

        if (body instanceof String)
        {
            return ResponseEntity.badRequest().body(body);
        }

        List<FinanceOrdersHistoryDto> orders = (List<FinanceOrdersHistoryDto>) serviceResponse.getBody();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", financeApprovalRepository.countByAction(),
                "orders", orders
        ));
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

}
