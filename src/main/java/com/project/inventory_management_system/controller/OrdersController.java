package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.dto.UserDto;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import com.project.inventory_management_system.service.OrderService;
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
@RequestMapping("/api")
public class OrdersController
{
    private final OrderService orderService;
    private final UsersRepository usersRepository;
    private final OrderRepository orderRepository;

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

    @GetMapping("/orders/page")
    public ResponseEntity<?> getOrdersWithLimitOffset(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {

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

        List<OrdersDto> orders = orderService.getOrdersByUserWithLimitOffset(user, offset, limit);

        if (orders.isEmpty())
        {
            return ResponseEntity.ok(Map.of("message", "No orders found"));
        }

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.count(),
                "orders", orders
        ));

        }

        @PutMapping("/order/update/{orderId}")
        public ResponseEntity<?> updateOrderDetails(HttpServletRequest request, @PathVariable Long orderId, @RequestBody OrdersDto ordersDto)
        {

            UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

            if (userDetails == null)
            {
                return ResponseEntity.status(401).body("Unauthorized");
            }

            try
            {
                OrdersDto orderDetailsUpdate = orderService.updateOrderDetails(userDetails.getUsername(), orderId, ordersDto);
                return ResponseEntity.ok(orderDetailsUpdate);
            }
            catch (Exception e)
            {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        @DeleteMapping("/order/delete/{orderId}")
        public ResponseEntity<?> deleteOrderDetails(HttpServletRequest request, @PathVariable Long orderId)
        {
            UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
            if (userDetails == null)
            {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            try
            {
                String deleteOrder = orderService.deleteOrder(userDetails.getUsername(), orderId);
                return ResponseEntity.ok(deleteOrder);
            }
            catch (RuntimeException e)
            {
                return ResponseEntity.badRequest().body(e.getMessage());
            }

        }

    }
