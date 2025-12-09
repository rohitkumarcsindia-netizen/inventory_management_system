package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.entity.LogisticsDetails;
import com.project.inventory_management_system.service.LogisticsOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
}
