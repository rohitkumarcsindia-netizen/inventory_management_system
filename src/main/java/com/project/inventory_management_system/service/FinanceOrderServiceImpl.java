package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.FinanceOrdersHistoryDto;
import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.Department;
import com.project.inventory_management_system.entity.FinanceApproval;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.mapper.OrdersCompleteMapper;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.FinanceApprovalRepository;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class FinanceOrderServiceImpl implements FinanceOrderService
{
    private final UsersRepository usersRepository;
    private final DepartmentRepository departmentRepository;
    private final OrderRepository orderRepository;
    private final OrdersCompleteMapper ordersCompleteMapper;
    private final OrderMapper orderMapper;
    private final EmailService emailService;
    private final FinanceApprovalRepository financeApprovalRepository;


    @Override
    public ResponseEntity<?> getPendingOrdersForFinance(String username,int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("FINANCE"))
        {
            return ResponseEntity.badRequest().body("Only finance team can view pending orders");
        }

        List<Orders> orders = orderRepository.findByStatusWithLimitOffset("FINANCE PENDING", offset, limit);

        if (orders.isEmpty())
        {
            return ResponseEntity.badRequest().body("No Orders found");
        }
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

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("FINANCE"))
        {
            return ResponseEntity.badRequest().body("Only finance team can view complete orders");
        }

        List<FinanceApproval> financeApprovalsOrders = financeApprovalRepository.findFinanceApprovals(limit, offset);

        if (financeApprovalsOrders.isEmpty())
        {
            return ResponseEntity.badRequest().body("No Orders found");
        }
        List<FinanceOrdersHistoryDto> list = financeApprovalsOrders.stream()
                .map(approval -> ordersCompleteMapper.financeOrdersHistoryDto(
                        approval.getOrder(), approval))
                .toList();

        return ResponseEntity.ok(list);

    }



    @Override
    public ResponseEntity<?> approveOrder(String username, Long orderId, String reason)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("FINANCE"))
        {
            return ResponseEntity.badRequest().body("Only finance team can approve orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order == null)
        {
            return ResponseEntity.badRequest().body("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("FINANCE PENDING"))
        {
            return ResponseEntity.badRequest().body("Order is not pending for finance approval");
        }

        //Finance Approval table data save
        FinanceApproval financeApproval = new FinanceApproval();
        financeApproval.setFinanceAction("APPROVED");
        financeApproval.setFinanceActionTime(LocalDateTime.now());
        financeApproval.setFinanceReason(reason);
        financeApproval.setFinanceApprovedBy(user);
        financeApproval.setOrder(order);
        financeApprovalRepository.save(financeApproval);

        //Order table status update
        order.setStatus("SCM PENDING");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("SCM");

        boolean mailsent = emailService.sendMailOrderApprove(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Order Approved Successfully");
    }



    @Override
    public ResponseEntity<?> rejectOrder(String username, Long orderId, String reason)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("FINANCE"))
        {
            return ResponseEntity.badRequest().body("Only finance team can reject orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order == null)
        {
            return ResponseEntity.badRequest().body("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("FINANCE PENDING"))
        {
            return ResponseEntity.badRequest().body("Order is not pending for finance approval");
        }


        //Finance Approval table data save
        FinanceApproval financeApproval = new FinanceApproval();
        financeApproval.setFinanceAction("REJECTED");
        financeApproval.setFinanceActionTime(LocalDateTime.now());
        financeApproval.setFinanceReason(reason);
        financeApproval.setFinanceApprovedBy(user);
        financeApproval.setOrder(order);
        financeApprovalRepository.save(financeApproval);

        //Order table status update
        order.setStatus("FINANCE REJECTED");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("PROJECT TEAM");

        boolean mailsent = emailService.sendMailOrderReject(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Order Reject Successfully");
    }

}
