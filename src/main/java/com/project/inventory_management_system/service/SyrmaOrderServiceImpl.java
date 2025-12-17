package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.dto.SyrmaOrdersDto;
import com.project.inventory_management_system.dto.SyrmaOrdersHistoryDto;
import com.project.inventory_management_system.entity.*;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.mapper.OrdersCompleteMapper;
import com.project.inventory_management_system.mapper.SyrmaOrdersMapper;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.SyrmaApprovalRepository;
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
public class SyrmaOrderServiceImpl implements SyrmaOrderService {

    private final UsersRepository usersRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final DepartmentRepository departmentRepository;
    private final EmailService emailService;
    private final OrdersCompleteMapper ordersCompleteMapper;
    private final SyrmaApprovalRepository syrmaApprovalRepository;
    private final SyrmaOrdersMapper syrmaOrdersMapper;
    private final OrderStatusByDepartmentService orderStatusByDepartmentService;


    @Override
    public ResponseEntity<?> getPendingOrdersForSyrma(String username, int offset, int limit) {
        Users user = usersRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.ok("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA")) {
            return ResponseEntity.status(403).body("Only syrma team can view approved orders");
        }

        List<String> syrmaStatuses = orderStatusByDepartmentService.getStatusesByDepartment(user.getDepartment().getDepartmentname());

        List<Orders> ordersList = orderRepository.findByStatusWithLimitOffset(syrmaStatuses, offset, limit);

        if (ordersList.isEmpty()) {
            return ResponseEntity.ok("No Orders found");
        }

        List<OrdersDto> ordersDtoList = ordersList.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.countBySyrmaStatusList(syrmaStatuses),
                "orders", ordersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> productionAndTestingComplete(String username, Long orderId, SyrmaOrdersDto syrmaComments) {
        Users user = usersRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA")) {
            return ResponseEntity.status(403).body("Only syrma team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null) {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("SCM JIRA TICKET CLOSURE > SYRMA PENDING")) {
            return ResponseEntity.status(403).body("Order is not ready for production start");
        }

        SyrmaApproval syrmaApproval = new SyrmaApproval();
        syrmaApproval.setOrder(order);
        syrmaApproval.setSyrmaAction("Completed");
        syrmaApproval.setActionTime(LocalDateTime.now());
        syrmaApproval.setActionDoneBy(user);
        syrmaApproval.setSyrmaComments(syrmaComments.getSyrmaComments().trim());
        syrmaApprovalRepository.save(syrmaApproval);

        //Order table status update
        order.setStatus("SYRMA PROD/TEST DONE > SCM ACTION PENDING");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("SCM");

        boolean mailsent = emailService.sendMailProductionAndTestingComplete(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent) {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Production and Testing successfully");
    }


    @Override
    public ResponseEntity<?> getCompleteOrdersForSyrma(String username, int offset, int limit) {
        Users user = usersRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA")) {
            return ResponseEntity.status(403).body("Only Syrma team can view complete orders");
        }

        List<SyrmaApproval> syrmaApprovalList = syrmaApprovalRepository.findSyrmaApprovals(limit, offset);

        if (syrmaApprovalList.isEmpty()) {
            return ResponseEntity.ok("No Orders found");
        }
        List<SyrmaOrdersHistoryDto> syrmaOrdersHistoryDtoList = syrmaApprovalList.stream()
                .map(approval -> ordersCompleteMapper.syrmaOrdersHistoryDto(
                        approval.getOrder(), approval))
                .toList();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", syrmaApprovalRepository.countByAction(),
                "orders", syrmaOrdersHistoryDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getSyrmaOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size) {
        Users user = usersRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA")) {
            return ResponseEntity.status(403).body("Only syrma team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage = orderRepository.findByDateRangeForSyrma(start, end, pageable);
        if (ordersPage.isEmpty()) {
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
    public ResponseEntity<?> getSyrmaOrdersFilterStatus(String username, String status, int page, int size) {
        Users user = usersRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA")) {
            return ResponseEntity.status(403).body("Only syrma team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage = orderRepository.findByStatusForSyrma(status, pageable);

        if (ordersPage.isEmpty()) {
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
    public ResponseEntity<?> getSyrmaOrdersSearch(String username, String keyword, int page, int size) {
        Users user = usersRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA")) {
            return ResponseEntity.status(403).body("Only syrma team can view this");
        }

        List<String> departmentNameWiseStatus = orderStatusByDepartmentService.getStatusesByDepartment(user.getDepartment().getDepartmentname());

        Specification<Orders> spec = Specification.allOf(OrderSpecification.statusIn(departmentNameWiseStatus)).and(OrderSpecification.keywordSearch(keyword));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage = orderRepository.findAll(spec, pageable);

        if (ordersPage.isEmpty()) {
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
    public ResponseEntity<?> getSyrmaCompleteOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size) {
        Users user = usersRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA")) {
            return ResponseEntity.status(403).body("Only syrma team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("actionTime").descending());
        Page<SyrmaApproval> syrmaApprovalPage = syrmaApprovalRepository.findByDateRangeForSyrma(start, end, pageable);
        if (syrmaApprovalPage.isEmpty()) {
            return ResponseEntity.ok("No orders found");
        }

        List<SyrmaOrdersDto> syrmaOrdersDtoList = syrmaApprovalPage.stream()
                .map(syrmaOrdersMapper::syrmaOrdersDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", syrmaApprovalPage.getTotalElements(),
                "totalPages", syrmaApprovalPage.getTotalPages(),
                "page", syrmaApprovalPage.getNumber(),
                "size", syrmaApprovalPage.getSize(),
                "records", syrmaOrdersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getSyrmaCompleteOrdersFilterStatus(String username, String status, int page, int size) {
        Users user = usersRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA")) {
            return ResponseEntity.status(403).body("Only syrma team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("actionTime").descending());
        Page<SyrmaApproval> syrmaApprovalPage = syrmaApprovalRepository.findByStatusFilterForSyrma(status, pageable);

        if (syrmaApprovalPage.isEmpty()) {
            return ResponseEntity.ok("No orders found");
        }

        List<SyrmaOrdersDto> syrmaOrderDtoList = syrmaApprovalPage.stream()
                .map(syrmaOrdersMapper::syrmaOrdersDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", syrmaApprovalPage.getTotalElements(),
                "totalPages", syrmaApprovalPage.getTotalPages(),
                "page", syrmaApprovalPage.getNumber(),
                "size", syrmaApprovalPage.getSize(),
                "records", syrmaOrderDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getSyrmaCompleteOrdersFilterSearch(String username, String keyword, int page, int size) {

        Users user = usersRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA")) {
            return ResponseEntity.status(403).body("Only syrma team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("actionTime").descending());
        Page<SyrmaApproval> syrmaApprovalPage = syrmaApprovalRepository.searchSyrmaComplete(keyword.trim(), pageable);

        if (syrmaApprovalPage.isEmpty()) {
            return ResponseEntity.ok("No orders found");
        }

        List<SyrmaOrdersDto> syrmaOrdersDtoList = syrmaApprovalPage.stream()
                .map(syrmaOrdersMapper::syrmaOrdersDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", syrmaApprovalPage.getTotalElements(),
                "totalPages", syrmaApprovalPage.getTotalPages(),
                "page", syrmaApprovalPage.getNumber(),
                "size", syrmaApprovalPage.getSize(),
                "records", syrmaOrdersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> reProductionAndTestingComplete(String username, Long orderId, SyrmaOrdersDto syrmaComments)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA"))
        {
            return ResponseEntity.status(403).body("Only syrma team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("RMA QC FAIL > SYRMA RE-PROD/TEST PENDING")) {
            return ResponseEntity.status(403).body("Order is not ready for production start");
        }

        SyrmaApproval syrmaApproval = syrmaApprovalRepository.findByOrder_OrderId(order.getOrderId());

        syrmaApproval.setOrder(order);
        syrmaApproval.setSyrmaAction("RE-PROD/TEST-Completed");
        syrmaApproval.setActionTime(LocalDateTime.now());
        syrmaApproval.setActionDoneBy(user);
        syrmaApproval.setSyrmaComments(syrmaComments.getSyrmaComments().trim());
        syrmaApprovalRepository.save(syrmaApproval);

        //Order table status update
        order.setStatus("SYRMA RE-PROD/TEST DONE > SCM ACTION PENDING");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("SCM");

        boolean mailsent = emailService.sendMailProductionAndTestingComplete(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent) {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Re Production and Testing successfully");
    }
}
