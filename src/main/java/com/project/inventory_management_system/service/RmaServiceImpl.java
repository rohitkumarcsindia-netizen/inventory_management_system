package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.*;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.RmaApprovalRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RmaServiceImpl implements RmaService
{
    private final UsersRepository usersRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final RmaApprovalRepository rmaApprovalRepository;
    private final DepartmentRepository departmentRepository;
    private final EmailService emailService;

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

    @Override
    public ResponseEntity<?> passedOrder(String username, Long orderId, RmaApproval comments)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("RMA"))
        {
            return ResponseEntity.status(403).body("Only Rma team can approve orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("Rma PENDING"))
        {
            return ResponseEntity.status(403).body("Order is not pending for Rma approval");
        }

        //Rma Approval table data save
        RmaApproval rmaApproval = new RmaApproval();
        rmaApproval.setRmaAction("PASSED");
        rmaApproval.setRmaActionTime(LocalDateTime.now());
        rmaApproval.setRmaComment(comments.getRmaComment().trim());
        rmaApproval.setRmaApprovedBy(user);
        rmaApproval.setOrder(order);
        rmaApprovalRepository.save(rmaApproval);

        //Order table status update
        order.setStatus("RMA > SCM RECHECK PENDING");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("SCM");

        boolean mailsent = emailService.sendMailNotifyScm(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Scm Notify Successfully");
    }

    @Override
    public ResponseEntity<?> failedOrder(String username, Long orderId, RmaApproval comments)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("Rma"))
        {
            return ResponseEntity.status(403).body("Only Rma team can reject orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("Rma PENDING"))
        {
            return ResponseEntity.status(403).body("Order is not pending for Rma approval");
        }


        //Rma Approval table data save
        RmaApproval rmaApproval = new RmaApproval();
        rmaApproval.setRmaAction("FAILED");
        rmaApproval.setRmaActionTime(LocalDateTime.now());
        rmaApproval.setRmaComment(comments.getRmaComment().trim());
        rmaApproval.setRmaApprovedBy(user);
        rmaApproval.setOrder(order);
        rmaApprovalRepository.save(rmaApproval);

        //Order table status update
        order.setStatus("RMA > SYRMA RE WORK PENDING");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("SYRMA");

        boolean mailsent = emailService.sendMailNotifySyrma(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Scm Notify Successfully");
    }
}
