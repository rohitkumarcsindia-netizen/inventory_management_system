package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import com.project.inventory_management_system.service.ProjectOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api/orders/project")
public class ProjectOrdersController
{
    private final ProjectOrderService projectOrderService;
    private final UsersRepository usersRepository;
    private final OrderRepository orderRepository;

    @PostMapping("/create")
    public ResponseEntity<?> addNewOrders(HttpServletRequest request, @RequestBody OrdersDto ordersDto)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

            if (userDetails == null)
            {
                return ResponseEntity.status(401).body("Unauthorized");
            }

        return projectOrderService.createOrder(userDetails.getUsername(), ordersDto);


    }

    @GetMapping("/page")
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
            return ResponseEntity.ok(body);
        }

        Users user = usersRepository.findByUsername(userDetails.getUsername());

        List<OrdersDto> orders = (List<OrdersDto>) body;

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.countByUserId(user.getUserId()),
                "orders", orders
        ));

        }

    @PutMapping("/update/{orderId}")
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
        @DeleteMapping("/delete/{orderId}")
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

    //Search Filter
    @GetMapping("/date-filter")
    public ResponseEntity<?> getOrdersFilterDate(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23,59,59);

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        ResponseEntity<?> serviceResponse = projectOrderService.getOrdersFilterDate(userDetails.getUsername(), start, end);

        Object body = serviceResponse.getBody();

        if (body instanceof String)
        {
            return ResponseEntity.ok(body);
        }

        List<OrdersDto> orders = (List<OrdersDto>) body;

        return ResponseEntity.ok(Map.of(
                "startDate", startDate,
                "endDate", endDate,
                "ordersCount", orders.size(),
                "orders", orders
        ));
    }
    @GetMapping("/status-filter")
    public ResponseEntity<?> getOrdersFilterStatus(
            HttpServletRequest request,
            @RequestParam String status)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        ResponseEntity<?> serviceResponse = projectOrderService.getOrdersFilterStatus(userDetails.getUsername(), status);

        Object body = serviceResponse.getBody();

        if (body instanceof String)
        {
            return ResponseEntity.ok(body);
        }

        List<OrdersDto> orders = (List<OrdersDto>) body;

        return ResponseEntity.ok(Map.of(
                "status", status,
                "ordersCount", orders.size(),
                "orders", orders
        ));
    }
    @GetMapping("/project-filter")
    public ResponseEntity<?> getOrdersFilterProject(
            HttpServletRequest request,
            @RequestParam String project)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        ResponseEntity<?> serviceResponse = projectOrderService.getOrdersFilterProject(userDetails.getUsername(), project);

        Object body = serviceResponse.getBody();

        if (body instanceof String)
        {
            return ResponseEntity.ok(body);
        }

        List<OrdersDto> orders = (List<OrdersDto>) body;

        return ResponseEntity.ok(Map.of(
                "project", project,
                "ordersCount", orders.size(),
                "orders", orders
        ));
    }

    //Universal search bar
    @GetMapping("/search")
    public ResponseEntity<?> getOrdersSearch(
            HttpServletRequest request,
            @RequestParam String keyword)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        ResponseEntity<?> serviceResponse = projectOrderService.getOrdersSearch(userDetails.getUsername(), keyword);

        Object body = serviceResponse.getBody();

        if (body instanceof String)
        {
            return ResponseEntity.ok(body);
        }

        List<OrdersDto> orders = (List<OrdersDto>) body;

        return ResponseEntity.ok(Map.of(
                "search", keyword,
                "ordersCount", orders.size(),
                "orders", orders
        ));
    }
}
