package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrdersController
{
    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<?> addNewOrders(HttpServletRequest request, @RequestBody OrdersDto ordersDto)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

            if (userDetails == null)
            {
                return ResponseEntity.status(401).body("Unauthorized");
            }

        return orderService.createOrder(userDetails.getUsername(), ordersDto);


    }

    @PutMapping("/orders/{orderId}")
    public ResponseEntity<?> updateOrderDetails(@PathVariable Long orderId, @RequestBody Orders orders)
    {
        Orders updateOrder = orderService.updateOreder(orderId, orders);
        if (updateOrder != null)
        {
            return ResponseEntity.ok("OrderId: "+(updateOrder.getOrderId())+" Updated Successfully");
        }
        else
            return ResponseEntity.badRequest().body("Order Record not Update");
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId, @RequestBody Orders orders)
    {
        Orders deleteOrder = orderService.deleteOrder(orderId, orders);
        if (deleteOrder != null)
        {
            return ResponseEntity.ok("Order: "+(deleteOrder.getOrderId())+" delete Successfully");
        }
        else
            return ResponseEntity.badRequest().body("Order Not Deleted");
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(HttpServletRequest request)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

//        if (userDetails == null)
//        {
//            return ResponseEntity.status(401).body("Unauthorized");
//        }

       return orderService.getAllOrders(userDetails.getUsername());
    }

}
