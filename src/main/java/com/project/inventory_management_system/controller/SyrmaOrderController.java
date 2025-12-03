package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.service.SyrmaOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


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

    @PutMapping("/production-testing/{orderId}")
    public ResponseEntity<?> productionAndTestingComplete(HttpServletRequest request, @PathVariable Long orderId)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return syrmaOrderService.productionAndTestingComplete(userDetails.getUsername(), orderId);
    }


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
