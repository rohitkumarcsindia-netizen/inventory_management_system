package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.CloudOrdersHistoryDto;
import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.*;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.mapper.OrdersCompleteMapper;
import com.project.inventory_management_system.repository.CloudApprovalRepository;
import com.project.inventory_management_system.repository.DepartmentRepository;
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
public class CloudOrderServiceImpl implements CloudOrderService
{

    private final UsersRepository usersRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrdersCompleteMapper ordersCompleteMapper;
    private final DepartmentRepository departmentRepository;
    private final EmailService emailService;
    private final CloudApprovalRepository cloudApprovalRepository;

    //Cloud Team getOrders Method
    @Override
    public ResponseEntity<?> getOrderPendingForCloud(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("CLOUD TEAM"))
        {
            return ResponseEntity.status(403).body("Only Cloud team can view approved orders");
        }

        List<Orders> ordersList = orderRepository.findByStatusWithLimitOffset("CLOUD PENDING", offset, limit);

        if (ordersList.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<OrdersDto> ordersDtoList = ordersList.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", cloudApprovalRepository.countByStatus("CLOUD PENDING"),
                "orders", ordersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getCompleteOrdersForScm(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("CLOUD TEAM"))
        {
            return ResponseEntity.status(403).body("Only Scm team can view complete orders");
        }

        List<CloudApproval> cloudApprovalsOrders = cloudApprovalRepository.findByCloudActionIsNotNull(limit, offset);

        if (cloudApprovalsOrders.isEmpty())
        {
            return ResponseEntity.ok("No Orders found");
        }
        List<CloudOrdersHistoryDto> cloudOrdersHistoryDtoList = cloudApprovalsOrders.stream()
                .map(approval -> ordersCompleteMapper.cloudOrdersHistoryDto(
                        approval.getOrder(), approval))
                .toList();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", cloudApprovalRepository.countByCloudAction(),
                "orders", cloudOrdersHistoryDtoList
        ));
    }

    @Override
    public ResponseEntity<?> updateJiraDetails(String username, Long orderId, CloudApproval jiraDetails)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("CLOUD TEAM"))
        {
            return ResponseEntity.status(403).body("Only Cloud team can update jira ticket details");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("CLOUD PENDING"))
        {
            return ResponseEntity.status(403).body("Jira details can only be submitted when the order is pending for SCM action");
        }

        CloudApproval jiraDetailsUpdate = new CloudApproval();
        jiraDetailsUpdate.setOrder(order);
        jiraDetailsUpdate.setJiraDescription(jiraDetails.getJiraDescription());
        jiraDetailsUpdate.setPriority(jiraDetails.getPriority());
        jiraDetailsUpdate.setCloudComments(jiraDetails.getCloudComments());
        jiraDetailsUpdate.setCloudAction("CLOUD UPDATED");
        jiraDetailsUpdate.setActionTime(LocalDateTime.now());
        jiraDetailsUpdate.setUpdatedBy(user);
        cloudApprovalRepository.save(jiraDetailsUpdate);

        order.setStatus("CLOUD > SCM RECHECK PENDING");
        orderRepository.save(order);


        Department department = departmentRepository.findByDepartmentname("SCM");

        boolean mailsent = emailService.sendMailCertificateGenerate(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Jira details submitted successfully and sent back to SCM for recheck");
    }
}
