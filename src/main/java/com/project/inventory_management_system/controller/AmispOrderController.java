package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.service.AmispOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
