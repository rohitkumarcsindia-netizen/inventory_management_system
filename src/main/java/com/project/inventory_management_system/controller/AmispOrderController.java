package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.dto.AmispOrderDto;
import com.project.inventory_management_system.dto.FinanceOrderDto;
import com.project.inventory_management_system.service.AmispOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
}
