package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.Department;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FinanceOrderServiceImpl implements FinanceOrderService
{
    private final UsersRepository usersRepository;
    private final DepartmentRepository departmentRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final EmailService emailService;


    @Override
    public ResponseEntity<?> getPendingOrdersForFinance(String username,int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("finance"))
        {
            return ResponseEntity.badRequest().body("Only finance team can view pending orders");
        }

        List<Orders> orders = orderRepository.findByStatusWithLimitOffset("finance", offset, limit);

        List<OrdersDto> list = orders.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<?> getCompleteOrdersForFinance(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("finance"))
        {
            return ResponseEntity.badRequest().body("Only finance team can view complete orders");
        }

        List<Orders> orders = orderRepository.findByOrderTypeWithLimitOffset("Free of Cost", offset, limit);

        List<OrdersDto> list = orders.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(list);
    }



    @Override
    public ResponseEntity<?> approveOrder(String username, Long orderId)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("finance"))
        {
            return ResponseEntity.badRequest().body("Only finance team can approve orders");
        }

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus("SCM");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("scm");

        boolean mailsent = emailService.sendMailOrderApprove(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Order Approved Successfully");
    }



    @Override
    public ResponseEntity<?> rejectOrder(String username, Long orderId)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("finance"))
        {
            return ResponseEntity.badRequest().body("Only finance team can reject orders");
        }

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus("Rejected from Finance Team");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("project team");

        boolean mailsent = emailService.sendMailOrderReject(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Order Reject Successfully");
    }

}
