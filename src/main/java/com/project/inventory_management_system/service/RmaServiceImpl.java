package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RmaServiceImpl implements RmaService
{
    private final UsersRepository usersRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public ResponseEntity<?> getPendingOrdersForRma(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("RMA"))
        {
            return ResponseEntity.status(403).body("Only Rma team can view pending orders");
        }

        List<Orders> orders = orderRepository.findByStatusWithLimitOffset("RMA PENDING", offset, limit);

        if (orders.isEmpty())
        {
            return ResponseEntity.ok("No Orders found");
        }
        List<OrdersDto> ordersDtoList = orders.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.countByStatus("RMA PENDING"),
                "orders", ordersDtoList
        ));
    }
}
