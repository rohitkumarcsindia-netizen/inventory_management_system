package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.*;
import com.project.inventory_management_system.entity.*;
import com.project.inventory_management_system.mapper.ProjectTeamOrderMapper;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.mapper.OrdersCompleteMapper;
import com.project.inventory_management_system.repository.ProjectTeamApprovalRepository;
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
    private final ProjectTeamApprovalRepository projectTeamApprovalRepository;
    private final DepartmentRepository departmentRepository;
    private final EmailService emailService;
    private final OrdersCompleteMapper ordersCompleteMapper;
    private final ProjectTeamOrderMapper projectTeamOrderMapper;



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
                "PDI PENDING",
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
                "ordersCount", orderRepository.countByAmispStatusList(amispStatuses),
                "orders", ordersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> postDeliveryPdiOrder(String username, Long orderId, ProjectTeamOrderDto pdiDetails)
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

        if (!order.getStatus().equalsIgnoreCase("PROJECT TEAM > AMISP PDI PENDING"))
        {
            return ResponseEntity.status(403).body("Order is not pending for Amisp approval");
        }

        //Finance Approval table data save
        ProjectTeamApproval projectTeamApproval = new ProjectTeamApproval();
        projectTeamApproval.setAmispPdiType("Post-Delivery PDI");
        projectTeamApproval.setProjectTeamActionTime(LocalDateTime.now());
        projectTeamApproval.setOrder(order);
        projectTeamApproval.setActionBy(user);

        projectTeamApproval.setProjectTeamComment(pdiDetails.getProjectTeamComment());
        projectTeamApproval.setSerialNumbers(pdiDetails.getSerialNumbers());
        projectTeamApproval.setDocumentUrl(pdiDetails.getDocumentUrl());
        projectTeamApproval.setDispatchDetails(pdiDetails.getDispatchDetails());
        projectTeamApproval.setPdiLocation(pdiDetails.getPdiLocation());
        projectTeamApprovalRepository.save(projectTeamApproval);

        //Order table status update
        order.setStatus("AMISP > POST PDI");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("PROJECT TEAM");

        boolean mailsent = emailService.sendMailNotifyAmispToProjectTeam(department.getDepartmentEmail(), order.getOrderId(), projectTeamApproval);

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Notification Sent For Project Team");
    }

    @Override
    public ResponseEntity<?> priDeliveryPdiOrder(String username, Long orderId, ProjectTeamOrderDto pdiDetails)
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

        if (!order.getStatus().equalsIgnoreCase("PROJECT TEAM > AMISP PDI PENDING"))
        {
            return ResponseEntity.status(403).body("Order is not pending for Amisp approval");
        }

        //Finance Approval table data save
        ProjectTeamApproval projectTeamApproval = new ProjectTeamApproval();
        projectTeamApproval.setAmispPdiType("Pri-Delivery PDI");
        projectTeamApproval.setProjectTeamActionTime(LocalDateTime.now());
        projectTeamApproval.setOrder(order);
        projectTeamApproval.setActionBy(user);

        projectTeamApproval.setProjectTeamComment(pdiDetails.getProjectTeamComment());
        projectTeamApproval.setSerialNumbers(pdiDetails.getSerialNumbers());
        projectTeamApproval.setDocumentUrl(pdiDetails.getDocumentUrl());
        projectTeamApproval.setDispatchDetails(pdiDetails.getDispatchDetails());
        projectTeamApproval.setPdiLocation(pdiDetails.getPdiLocation());
        projectTeamApprovalRepository.save(projectTeamApproval);

        //Order table status update
        order.setStatus("AMISP > PRE PDI");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("PROJECT TEAM");

        boolean mailsent = emailService.sendMailNotifyAmispToProjectTeam(department.getDepartmentEmail(), order.getOrderId(), projectTeamApproval);

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Notification Sent For Project Team");
    }

    @Override
    public ResponseEntity<?> amispNotifyProjectTeamLocationDetails(String username, Long orderId, ProjectTeamOrderDto locationDetails)
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
        ProjectTeamApproval projectTeamApproval = projectTeamApprovalRepository.findByOrder_OrderId(order.getOrderId());

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("SCM > AMISP RECHECK PENDING"))
        {
            return ResponseEntity.status(403).body("Notify details can only be submitted when the order is pending for AMISP action");
        }

        //Location details set Db
        projectTeamApproval.setLocationDetails(locationDetails.getLocationDetails());
        projectTeamApprovalRepository.save(projectTeamApproval);


        order.setStatus("AMISP > PROJECT TEAM LOCATION SENT");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("PROJECT TEAM");

        boolean mailsent = emailService.sendMailNotifyAmisoToProjectTeam(department.getDepartmentEmail(), order, projectTeamApproval);

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

        List<ProjectTeamApproval> projectTeamApprovalsOrders = projectTeamApprovalRepository.findByAmispActionIsNotNull(limit, offset);

        if (projectTeamApprovalsOrders.isEmpty())
        {
            return ResponseEntity.ok("No Orders found");
        }
        List<ProjectTeamOrdersHistoryDto> projectTeamOrdersHistoryDtoList = projectTeamApprovalsOrders.stream()
                .map(approval -> ordersCompleteMapper.amispOrdersHistoryDto(
                        approval.getOrder(), approval))
                .toList();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", projectTeamApprovalRepository.countByAmispAction(),
                "orders", projectTeamOrdersHistoryDtoList
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

    @Override
    public ResponseEntity<?> getAmispOrdersFilterStatus(String username, String status, int page, int size)
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
        Page<Orders> ordersPage =  orderRepository.findByStatusForAmisp(status, pageable);

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
    public ResponseEntity<?> getOrdersSearchForAmisp(String username, String keyword, int page, int size)
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
        Page<Orders> ordersPage = orderRepository.searchAmisp(keyword.trim(), pageable);

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
    public ResponseEntity<?> getAmispCompleteOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size)
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

        Pageable pageable = PageRequest.of(page, size, Sort.by("amispActionTime").descending());
        Page<ProjectTeamApproval> amispApprovalPage = projectTeamApprovalRepository.findByDateRange(start, end, pageable);
        if (amispApprovalPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<ProjectTeamOrderDto> projectTeamOrderDtoList = amispApprovalPage.stream()
                .map(projectTeamOrderMapper::amispOrdersDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", amispApprovalPage.getTotalElements(),
                "totalPages", amispApprovalPage.getTotalPages(),
                "page", amispApprovalPage.getNumber(),
                "size", amispApprovalPage.getSize(),
                "records", projectTeamOrderDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getAmispCompleteOrdersFilterStatus(String username, String status, int page, int size)
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

        Pageable pageable = PageRequest.of(page, size, Sort.by("amispActionTime").descending());
        Page<ProjectTeamApproval> amispApprovalpage =  projectTeamApprovalRepository.findByStatusFilter(status, pageable);

        if (amispApprovalpage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<ProjectTeamOrderDto> projectTeamOrderDtoList = amispApprovalpage.stream()
                .map(projectTeamOrderMapper::amispOrdersDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", amispApprovalpage.getTotalElements(),
                "totalPages", amispApprovalpage.getTotalPages(),
                "page", amispApprovalpage.getNumber(),
                "size", amispApprovalpage.getSize(),
                "records", projectTeamOrderDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getAmispCompleteOrdersFilterSearch(String username, String keyword, int page, int size)
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

        Pageable pageable = PageRequest.of(page, size, Sort.by("amispActionTime").descending());
        Page<ProjectTeamApproval> amispApprovalPage = projectTeamApprovalRepository.searchAmispComplete(keyword.trim(), pageable);

        if (amispApprovalPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<ProjectTeamOrderDto> projectTeamOrderDtoList = amispApprovalPage.stream()
                .map(projectTeamOrderMapper::amispOrdersDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", amispApprovalPage.getTotalElements(),
                "totalPages", amispApprovalPage.getTotalPages(),
                "page", amispApprovalPage.getNumber(),
                "size", amispApprovalPage.getSize(),
                "records", projectTeamOrderDtoList
        ));
    }


}
