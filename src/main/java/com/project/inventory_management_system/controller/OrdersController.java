package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrdersController
{
    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<?> addNewOrders(@RequestBody Orders orders, Authentication authentication)
    {
        authentication.getName();
        Orders saveOrder = orderService.createOrder(orders);
        if (saveOrder != null)
        {
            return ResponseEntity.ok("OrderId :"+(saveOrder.getOrderId())+" Save Successfully");
        }
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order Not Saved: User not found.");
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
    public Orders findOrder()
    {
        return orderService.getOrders();
    }

    @GetMapping("/findall")
    public List<Orders> findAllOrdres()
    {
        return orderService.findAllOrder();
    }
}
