package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.dto.ScmOrdersHistoryDto;
import com.project.inventory_management_system.entity.ScmApproval;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.ScmApprovalRepository;
import com.project.inventory_management_system.service.ScmOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class ScmController
{
    private final ScmOrderService scmOrderService;
    private final OrderRepository orderRepository;
    private final ScmApprovalRepository scmApprovalRepository;


    @GetMapping("/scm/pending")
    public ResponseEntity<?> getPendingOrdersForScm(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        ResponseEntity<?> serviceResponse = scmOrderService.getPendingOrdersForScm(userDetails.getUsername(), offset, limit);

        Object body = serviceResponse.getBody();

        if (body instanceof String)
        {
            return ResponseEntity.ok(body);
        }

        List<OrdersDto> orders = (List<OrdersDto>) serviceResponse.getBody();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.countByStatus("SCM PENDING"),
                "orders", orders
        ));
    }


    @GetMapping("/scm/complete")
    public ResponseEntity<?> getCompleteOrdersForScm(
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
                scmOrderService.getCompleteOrdersForScm(userDetails.getUsername(), offset, limit);

        Object body = serviceResponse.getBody();

        if (body instanceof String)
        {
            return ResponseEntity.ok(body);
        }

        List<ScmOrdersHistoryDto> orders = (List<ScmOrdersHistoryDto>) serviceResponse.getBody();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", scmApprovalRepository.countByScmAction(),
                "orders", orders
        ));
    }


    @PostMapping("/scm/jira/details/{orderId}")
    public ResponseEntity<?> fillJiraTicketDetail(HttpServletRequest request, @PathVariable Long orderId, @RequestBody ScmApproval jiraDetails)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return scmOrderService.fillJiraTicketDetail(userDetails.getUsername(),orderId, jiraDetails);
    }

    @PutMapping("/scm/jira-ticket-closure/{orderId}")
    public ResponseEntity<?>  prodbackGenerateAndJiraTicketClosure(HttpServletRequest request, @PathVariable Long orderId, @RequestBody ScmApproval jiraDetails)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return scmOrderService.prodbackGenerateAndJiraTicketClosure(userDetails.getUsername(), orderId, jiraDetails);
    }

    // old button method
    @PostMapping("/old/scm/jira/details/{orderId}")
    public ResponseEntity<?> fillJiraTicketDetailOldOrder(HttpServletRequest request, @PathVariable Long orderId, @RequestBody ScmApproval jiraDetails)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return scmOrderService.fillJiraTicketDetailOldOrder(userDetails.getUsername(),orderId, jiraDetails);
    }


    // Scm Recheck Pending And Jira Closure Method
    @GetMapping("/scm/recheck")
    public ResponseEntity<?> getScmRecheckOrderPending(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        ResponseEntity<?> serviceResponse = scmOrderService.getScmRecheckOrderPending(userDetails.getUsername(), offset, limit);

        List<OrdersDto> orders = (List<OrdersDto>) serviceResponse.getBody();
        Object body = serviceResponse.getBody();

        if (body instanceof String)
        {
            return ResponseEntity.badRequest().body(body);
        }

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.countByStatus("SCM RECHECK PENDING"),
                "orders", orders
        ));
    }
}
