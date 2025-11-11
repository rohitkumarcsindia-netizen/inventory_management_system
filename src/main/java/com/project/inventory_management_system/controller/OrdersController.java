package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.repository.UsersRepository;
import com.project.inventory_management_system.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrdersController
{
    private final OrderService orderService;
    private final UsersRepository usersRepository;

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

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return orderService.getAllOrders(userDetails.getUsername());
    }


@GetMapping("/limit-offset")
public ResponseEntity<?> getOrdersWithLimitOffset(
        HttpServletRequest request,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

    UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

    if (userDetails == null)
    {
        return ResponseEntity.status(401).body("Unauthorized");
    }

    Users user = usersRepository.findByUsername(userDetails.getUsername());
    if (user == null)
    {
        return ResponseEntity.status(404).body("User not found");
    }

    List<OrdersDto> orders = orderService.getOrdersByUserWithLimitOffset(user, page, size);

    if (orders.isEmpty())
    {
        return ResponseEntity.ok(Map.of("message", "No orders found"));
    }

    return ResponseEntity.ok(Map.of(
            "page", page,
            "size", size,
            "ordersCount", orders.size(),
            "orders", orders
    ));

    }
}
