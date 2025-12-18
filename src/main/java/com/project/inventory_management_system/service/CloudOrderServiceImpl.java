package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.CloudOrdersDto;
import com.project.inventory_management_system.dto.CloudOrdersHistoryDto;
import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.*;
import com.project.inventory_management_system.mapper.CloudOrderMapper;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.mapper.OrdersCompleteMapper;
import com.project.inventory_management_system.repository.CloudApprovalRepository;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    private final CloudOrderMapper cloudOrderMapper;
    private final OrderStatusByDepartmentService orderStatusByDepartmentService;

    //Cloud Team getOrders Method
    @Override
    public ResponseEntity<?> getOrderPendingForCloud(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("CLOUD TEAM"))
        {
            return ResponseEntity.status(403).body("Only Cloud team can view approved orders");
        }

        List<String> cloudStatuses = orderStatusByDepartmentService.getStatusesByDepartment(user.getDepartment().getDepartmentName());

        List<Orders> ordersList = orderRepository.findByStatusWithLimitOffset(cloudStatuses, offset, limit);

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
                "ordersCount", orderRepository.countByStatus(cloudStatuses),
                "orders", ordersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getCompleteOrdersForCloud(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("CLOUD TEAM"))
        {
            return ResponseEntity.status(403).body("Only cloud team can view complete orders");
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

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("CLOUD TEAM"))
        {
            return ResponseEntity.status(403).body("Only Cloud team can update jira ticket details");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("SCM CREATED TICKET > CLOUD PENDING"))
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

        order.setStatus("CLOUD CREATED CERTIFICATE > SCM PROD-BACK CREATION PENDING");
        orderRepository.save(order);


        Department department = departmentRepository.findByDepartmentName("SCM");

        boolean mailsent = emailService.sendMailCertificateGenerate(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Jira details submitted successfully and sent back to SCM for recheck");
    }


    //Searching Filters
    @Override
    public ResponseEntity<?> getCloudOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("CLOUD TEAM"))
        {
            return ResponseEntity.status(403).body("Only cloud team can view this");
        }

        List<String> statuses = orderStatusByDepartmentService.getStatusesByDepartment(user.getDepartment().getDepartmentName());

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage = orderRepository.findByDateRange(start, end, statuses, pageable);
        if (ordersPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<OrdersDto> cloudOrderDtoList = ordersPage.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", ordersPage.getTotalElements(),
                "totalPages", ordersPage.getTotalPages(),
                "page", ordersPage.getNumber(),
                "size", ordersPage.getSize(),
                "records", cloudOrderDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getCloudOrdersSearch(String username, String keyword, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("CLOUD TEAM"))
        {
            return ResponseEntity.status(403).body("Only cloud team can view this");
        }


        List<String> departmentNameWiseStatus = orderStatusByDepartmentService.getStatusesByDepartment(user.getDepartment().getDepartmentName());

        Specification<Orders> spec = Specification.allOf(OrderSpecification.statusIn(departmentNameWiseStatus)).and(OrderSpecification.keywordSearch(keyword));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage = orderRepository.findAll(spec, pageable);

        if (ordersPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<OrdersDto> OrderDtoList = ordersPage.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", ordersPage.getTotalElements(),
                "totalPages", ordersPage.getTotalPages(),
                "page", ordersPage.getNumber(),
                "size", ordersPage.getSize(),
                "records", OrderDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getCloudCompleteOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("CLOUD TEAM"))
        {
            return ResponseEntity.status(403).body("Only cloud team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("actionTime").descending());
        Page<CloudApproval> cloudApprovalPage = cloudApprovalRepository.findByDateRange(start, end, pageable);
        if (cloudApprovalPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<CloudOrdersDto> cloudOrdersDtoList = cloudApprovalPage.stream()
                .map(cloudOrderMapper::cloudOrdersDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", cloudApprovalPage.getTotalElements(),
                "totalPages", cloudApprovalPage.getTotalPages(),
                "page", cloudApprovalPage.getNumber(),
                "size", cloudApprovalPage.getSize(),
                "records", cloudOrdersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getCloudCompleteOrdersFilterStatus(String username, String status, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("FINANCE"))
        {
            return ResponseEntity.status(403).body("Only finance team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("financeActionTime").descending());
        Page<CloudApproval> cloudApprovalPage =  cloudApprovalRepository.findByStatusFilterForCloud(status, pageable);

        if (cloudApprovalPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<CloudOrdersDto> cloudOrderDtoList = cloudApprovalPage.stream()
                .map(cloudOrderMapper::cloudOrdersDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", cloudApprovalPage.getTotalElements(),
                "totalPages", cloudApprovalPage.getTotalPages(),
                "page", cloudApprovalPage.getNumber(),
                "size", cloudApprovalPage.getSize(),
                "records", cloudOrderDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getCloudCompleteOrdersSearch(String username, String keyword, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("CLOUD TEAM"))
        {
            return ResponseEntity.status(403).body("Only cloud team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("actionTime").descending());
        Page<CloudApproval> cloudApprovalPage = cloudApprovalRepository.searchCloudComplete(keyword.trim(), pageable);

        if (cloudApprovalPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<CloudOrdersDto> cloudOrderDtoList = cloudApprovalPage.stream()
                .map(cloudOrderMapper::cloudOrdersDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", cloudApprovalPage.getTotalElements(),
                "totalPages", cloudApprovalPage.getTotalPages(),
                "page", cloudApprovalPage.getNumber(),
                "size", cloudApprovalPage.getSize(),
                "records", cloudOrderDtoList
        ));
    }
}
