package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import com.project.inventory_management_system.service.ProjectOrderService;
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
public class ProjectOrdersController
{
    private final ProjectOrderService projectOrderService;
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

        return projectOrderService.createOrder(userDetails.getUsername(), ordersDto);


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

        ResponseEntity<?> serviceResponse = projectOrderService.getOrdersByUserWithLimitOffset(userDetails.getUsername(), offset, limit);

        Object body = serviceResponse.getBody();

        if (body instanceof String)
        {
            return ResponseEntity.badRequest().body(body);
        }

        Users user = usersRepository.findByUsername(userDetails.getUsername());

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.countByUserId(user.getUserId()),
                "orders", serviceResponse
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
                OrdersDto orderDetailsUpdate = projectOrderService.updateOrderDetails(userDetails.getUsername(), orderId, ordersDto);
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
                String deleteOrder = projectOrderService.deleteOrder(userDetails.getUsername(), orderId);
                return ResponseEntity.ok(deleteOrder);
            }
            catch (RuntimeException e)
            {
                return ResponseEntity.badRequest().body(e.getMessage());
            }

        }

    }
