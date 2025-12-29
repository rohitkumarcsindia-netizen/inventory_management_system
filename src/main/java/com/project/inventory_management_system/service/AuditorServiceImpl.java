package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.enums.OrderStatus;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuditorServiceImpl implements AuditorService
{
    private final OrderRepository orderRepository;
    private final UsersRepository usersRepository;
    private final OrderMapper orderMapper;

    @Override
    public ResponseEntity<?> getOrdersByAuditor(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("AUDITOR"))
        {
            return ResponseEntity.status(403).body("Only admin team can view pending orders");
        }

        List<Orders> orders =  orderRepository.findAllOrders(offset, limit);

        if (orders.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<OrdersDto> ordersDtoList = orders.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.count(),
                "orders", ordersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getOrdersFilterDate(String username, LocalDateTime startDate, LocalDateTime endDate, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("AUDITOR"))
        {
            return ResponseEntity.status(403).body("Only admin team can view pending orders");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage =  orderRepository.findByOrderDate(startDate, endDate, pageable);

        if (ordersPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<OrdersDto> ordersDtoList = ordersPage.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", ordersPage.getTotalElements(),
                "totalPages", ordersPage.getTotalPages(),
                "page", ordersPage.getNumber(),
                "size", ordersPage.getSize(),
                "records", ordersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getOrdersFilterStatus(String username, String status, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("AUDITOR"))
        {
            return ResponseEntity.status(403).body("Only admin team can view this");
        }

        OrderStatus orderStatus;
        try
        {
            orderStatus = OrderStatus.fromDisplay(status);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body("Invalid status");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage =  orderRepository.findByStatus(orderStatus, pageable);

        if (ordersPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<OrdersDto> ordersDtoList = ordersPage.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", ordersPage.getTotalElements(),
                "totalPages", ordersPage.getTotalPages(),
                "page", ordersPage.getNumber(),
                "size", ordersPage.getSize(),
                "records", ordersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getOrdersSearch(String username, String keyword, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("AUDITOR"))
        {
            return ResponseEntity.status(403).body("Only admin team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage =  orderRepository.findBySearchOrdersForAdmin(keyword, pageable);

        if (ordersPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<OrdersDto> ordersDtoList = ordersPage.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", ordersPage.getTotalElements(),
                "totalPages", ordersPage.getTotalPages(),
                "page", ordersPage.getNumber(),
                "size", ordersPage.getSize(),
                "records", ordersDtoList
        ));
    }

}