package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrdersController
{
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(Orders orders)
    {
        Orders orders1 = orderService.createOrder(orders);
        if (orders1 != null)
        {
            return ResponseEntity.ok(orders1);
        }
        else
            return ResponseEntity.badRequest().body("Order not Save");
    }
}
