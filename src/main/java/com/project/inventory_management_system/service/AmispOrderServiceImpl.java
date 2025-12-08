package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.AmispOrderDto;
import com.project.inventory_management_system.dto.AmispOrdersHistoryDto;
import com.project.inventory_management_system.dto.CloudOrdersHistoryDto;
import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.*;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.mapper.OrdersCompleteMapper;
import com.project.inventory_management_system.repository.AmispApprovalRepository;
import com.project.inventory_management_system.repository.DepartmentRepository;
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
public class AmispOrderServiceImpl implements AmispOrderService
{

    private final UsersRepository usersRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final AmispApprovalRepository amispApprovalRepository;
    private final DepartmentRepository departmentRepository;
    private final EmailService emailService;
    private final OrdersCompleteMapper ordersCompleteMapper;



    @Override
    public ResponseEntity<?> getPendingOrdersForAmisp(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("AMISP"))
        {
            return ResponseEntity.status(403).body("Only Amisp team can view pending orders");
        }

        // Allowed Amisp statuses (priority order)
        List<String> amispStatuses = List.of(
                "AMISP PENDING",
                "SCM > AMISP RECHECK PENDING"
        );

        List<Orders> orders = orderRepository.findByStatusListWithLimitOffset(amispStatuses, offset, limit);

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
                "ordersCount", orderRepository.countByStatus("PROJECT TEAM > AMISP PENDING"),
                "orders", ordersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> postDeliveryPdiOrder(String username, Long orderId, AmispOrderDto pdiDetails)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("AMISP"))
        {
            return ResponseEntity.status(403).body("Only Amisp team can approve orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("AMISP PENDING"))
        {
            return ResponseEntity.status(403).body("Order is not pending for Amisp approval");
        }

        //Finance Approval table data save
        AmispApproval amispApproval = new AmispApproval();
        amispApproval.setAmispAction(" Post-Delivery PDI");
        amispApproval.setAmispActionTime(LocalDateTime.now());
        amispApproval.setOrder(order);
        amispApproval.setAmispApprovedBy(user);

        amispApproval.setAmispComment(pdiDetails.getAmispComment());
        amispApproval.setSerialNumbers(pdiDetails.getSerialNumbers());
        amispApproval.setDocumentUrl(pdiDetails.getDocumentUrl());
        amispApproval.setDispatchDetails(pdiDetails.getDispatchDetails());
        amispApprovalRepository.save(amispApproval);

        //Order table status update
        order.setStatus("AMISP > PROJECT TEAM RECHECK PENDING");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("PROJECT TEAM");

        boolean mailsent = emailService.sendMailNotifyAmispToProjectTeam(department.getDepartmentEmail(), order.getOrderId(),amispApproval);

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Notification Sent For Project Team");
    }

    @Override
    public ResponseEntity<?> priDeliveryPdiOrder(String username, Long orderId, AmispOrderDto pdiDetails)
    {
        return null;
    }

    @Override
    public ResponseEntity<?> amispNotifyProjectTeamLocationDetails(String username, Long orderId,AmispOrderDto locationDetails)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("AMISP"))
        {
            return ResponseEntity.status(403).body("Only Amisp team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);
        AmispApproval amispApproval = amispApprovalRepository.findByOrder_OrderId(order.getOrderId());

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("SCM > AMISP RECHECK PENDING"))
        {
            return ResponseEntity.status(403).body("Notify details can only be submitted when the order is pending for AMISP action");
        }

        //Location details set Db
        amispApproval.setPdiLocation(locationDetails.getPdiLocation());
        amispApprovalRepository.save(amispApproval);


        order.setStatus("AMISP > PROJECT TEAM LOCATION SENT");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("PROJECT TEAM");

        boolean mailsent = emailService.sendMailNotifyAmisoToProjectTeam(department.getDepartmentEmail(), order, amispApproval);

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Notification sent for PROJECT TEAM");
    }

    @Override
    public ResponseEntity<?> getCompleteOrdersForAmisp(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("AMISP"))
        {
            return ResponseEntity.status(403).body("Only amisp team can view complete orders");
        }

        List<AmispApproval> amispApprovalsOrders = amispApprovalRepository.findByAmispActionIsNotNull(limit, offset);

        if (amispApprovalsOrders.isEmpty())
        {
            return ResponseEntity.ok("No Orders found");
        }
        List<AmispOrdersHistoryDto> amispOrdersHistoryDtoList = amispApprovalsOrders.stream()
                .map(approval -> ordersCompleteMapper.amispOrdersHistoryDto(
                        approval.getOrder(), approval))
                .toList();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", amispApprovalRepository.countByAmispAction(),
                "orders", amispOrdersHistoryDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getAmispOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("AMISP"))
        {
            return ResponseEntity.status(403).body("Only amisp team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage = orderRepository.findByDateRangeForAmisp(start, end, pageable);
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
}
