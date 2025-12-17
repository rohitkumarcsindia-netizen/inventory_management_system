package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.FinanceOrderDto;
import com.project.inventory_management_system.dto.FinanceOrdersHistoryDto;
import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.Department;
import com.project.inventory_management_system.entity.FinanceApproval;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.mapper.FinanceOrderMapper;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.mapper.OrdersCompleteMapper;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.FinanceApprovalRepository;
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
public class FinanceOrderServiceImpl implements FinanceOrderService
{
    private final UsersRepository usersRepository;
    private final DepartmentRepository departmentRepository;
    private final OrderRepository orderRepository;
    private final OrdersCompleteMapper ordersCompleteMapper;
    private final OrderMapper orderMapper;
    private final EmailService emailService;
    private final FinanceApprovalRepository financeApprovalRepository;
    private final FinanceOrderMapper financeOrderMapper;
    private final OrderStatusByDepartmentService orderStatusByDepartmentService;


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
            return ResponseEntity.status(403).body("Only finance team can view pending orders");
        }

        List<String> financeStatuses = orderStatusByDepartmentService.getStatusesByDepartment( user.getDepartment().getDepartmentname());

        List<Orders> orders = orderRepository.findByStatusWithLimitOffset(financeStatuses, offset, limit);

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
                "ordersCount", orderRepository.countByStatus(financeStatuses),
                "orders", ordersDtoList
        ));
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
            return ResponseEntity.status(403).body("Only finance team can view complete orders");
        }

        List<FinanceApproval> financeApprovalsOrders = financeApprovalRepository.findFinanceApprovals(limit, offset);

        if (financeApprovalsOrders.isEmpty())
        {
            return ResponseEntity.ok("No Orders found");
        }
        List<FinanceOrdersHistoryDto> financeOrdersHistoryDtoList = financeApprovalsOrders.stream()
                .map(approval -> ordersCompleteMapper.financeOrdersHistoryDto(
                        approval.getOrder(), approval))
                .toList();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", financeApprovalRepository.countByAction(),
                "orders", financeOrdersHistoryDtoList
        ));

    }


    @Override
    public ResponseEntity<?> approveOrder(String username, Long orderId, FinanceOrderDto reason)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("FINANCE"))
        {
            return ResponseEntity.status(403).body("Only finance team can approve orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("PROJECT TEAM > FINANCE PRE APPROVAL PENDING"))
        {
            return ResponseEntity.status(403).body("Order is not pending for finance approval");
        }

        //Finance Approval table data save
        FinanceApproval financeApproval = new FinanceApproval();
        financeApproval.setFinanceAction("APPROVED");
        financeApproval.setFinanceActionTime(LocalDateTime.now());
        financeApproval.setFinanceReason(reason.getFinanceReason().trim());
        financeApproval.setFinanceApprovedBy(user);
        financeApproval.setOrder(order);
        financeApprovalRepository.save(financeApproval);

        //Order table status update
        order.setStatus("FINANCE APPROVED > SCM PENDING");
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
    public ResponseEntity<?> rejectOrder(String username, Long orderId, FinanceOrderDto reason)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("FINANCE"))
        {
            return ResponseEntity.status(403).body("Only finance team can reject orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("PROJECT TEAM > FINANCE PRE APPROVAL PENDING"))
        {
            return ResponseEntity.status(403).body("Order is not pending for finance approval");
        }


        //Finance Approval table data save
        FinanceApproval financeApproval = new FinanceApproval();
        financeApproval.setFinanceAction("REJECTED");
        financeApproval.setFinanceFinalRemark("REJECTED");
        financeApproval.setFinanceActionTime(LocalDateTime.now());
        financeApproval.setFinanceReason(reason.getFinanceReason().trim());
        financeApproval.setFinanceApprovedBy(user);
        financeApproval.setOrder(order);
        financeApprovalRepository.save(financeApproval);

        //Order table status update
        order.setStatus("FINANCE TEAM REJECTED");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("PROJECT TEAM");

        boolean mailsent = emailService.sendMailOrderReject(financeApproval.getFinanceReason(), department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Order Reject Successfully");
    }

    //Search filter method
    @Override
    public ResponseEntity<?> getOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end,int page,int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("FINANCE"))
        {
            return ResponseEntity.status(403).body("Only finance team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage = orderRepository.findByDateRangeForFinance(start, end, pageable);
        if (ordersPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<OrdersDto> financeOrderDtoList = ordersPage.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", ordersPage.getTotalElements(),
                "totalPages", ordersPage.getTotalPages(),
                "page", ordersPage.getNumber(),
                "size", ordersPage.getSize(),
                "records", financeOrderDtoList
        ));
    }

    //Universal Search method
    @Override
    public ResponseEntity<?> getOrdersSearch(String username, String keyword, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("FINANCE"))
        {
            return ResponseEntity.status(403).body("Only finance team can view this");
        }

        List<String> departmentNameWiseStatus = orderStatusByDepartmentService.getStatusesByDepartment(user.getDepartment().getDepartmentname());

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
    public ResponseEntity<?> getOrdersFilterStatus(String username, String status, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("FINANCE"))
        {
            return ResponseEntity.status(403).body("Only finance team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("financeActionTime").descending());
        Page<FinanceApproval> financeApprovalpage =  financeApprovalRepository.findByStatusFilter(status, pageable);

        if (financeApprovalpage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<FinanceOrderDto> financeOrderDtoList = financeApprovalpage.stream()
                .map(financeOrderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", financeApprovalpage.getTotalElements(),
                "totalPages", financeApprovalpage.getTotalPages(),
                "page", financeApprovalpage.getNumber(),
                "size", financeApprovalpage.getSize(),
                "records", financeOrderDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getCompleteOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("FINANCE"))
        {
            return ResponseEntity.status(403).body("Only finance team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("financeActionTime").descending());
        Page<FinanceApproval> financeApprovalPage = financeApprovalRepository.findByDateRange(start, end, pageable);
        if (financeApprovalPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<FinanceOrderDto> financeOrderDtoList = financeApprovalPage.stream()
                .map(financeOrderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", financeApprovalPage.getTotalElements(),
                "totalPages", financeApprovalPage.getTotalPages(),
                "page", financeApprovalPage.getNumber(),
                "size", financeApprovalPage.getSize(),
                "records", financeOrderDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getOrdersCompleteSearch(String username, String keyword, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("FINANCE"))
        {
            return ResponseEntity.status(403).body("Only finance team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("financeActionTime").descending());
        Page<FinanceApproval> financeApprovalPage = financeApprovalRepository.searchFinanceComplete(keyword.trim(), pageable);

        if (financeApprovalPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<FinanceOrderDto> financeOrderDtoList = financeApprovalPage.stream()
                .map(financeOrderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", financeApprovalPage.getTotalElements(),
                "totalPages", financeApprovalPage.getTotalPages(),
                "page", financeApprovalPage.getNumber(),
                "size", financeApprovalPage.getSize(),
                "records", financeOrderDtoList
        ));
    }

    @Override
    public ResponseEntity<?> finalApprovedOrder(String username, Long orderId, FinanceOrderDto finalReason)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("FINANCE"))
        {
            return ResponseEntity.status(403).body("Only finance team can approve orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("SCM > FINANCE POST APPROVAL PENDING"))
        {
            return ResponseEntity.status(403).body("Order is not pending for finance approval");
        }

        if (order.getOrderType().equalsIgnoreCase("Free of Cost"))
        {
            FinanceApproval findOrder = financeApprovalRepository.findByOrder_OrderId(order.getOrderId());

            //Finance Approval table data update
            findOrder.setFinanceAction("APPROVED");
            findOrder.setFinanceActionTime(LocalDateTime.now());
            findOrder.setFinanceFinalRemark(finalReason.getFinanceFinalRemark().trim());
            findOrder.setFinanceApprovedBy(user);
            financeApprovalRepository.save(findOrder);

            //Order table status update
            order.setStatus("FINANCE > SCM PLAN TO DISPATCH");
            orderRepository.save(order);


            Department department = departmentRepository.findByDepartmentname("SCM");

            boolean mailsent = emailService.sendFinanceApprovalMailToSCM(department.getDepartmentEmail(), order, findOrder);

            if (!mailsent)
            {
                return ResponseEntity.status(500).body("Mail Not Sent");
            }
        }

        if (order.getOrderType().equalsIgnoreCase("Purchase"))
        {
            //Finance Approval table data save
            FinanceApproval financeApproval = new FinanceApproval();
            financeApproval.setFinanceAction("APPROVED");
            financeApproval.setFinanceActionTime(LocalDateTime.now());
            financeApproval.setFinanceReason(finalReason.getFinanceReason().trim());
            financeApproval.setFinanceApprovedBy(user);
            financeApproval.setOrder(order);
            financeApproval.setFinanceFinalRemark(finalReason.getFinanceFinalRemark().trim());
            financeApprovalRepository.save(financeApproval);

            //Order table status update
            order.setStatus("FINANCE > SCM PLAN TO DISPATCH");
            orderRepository.save(order);

            Department department = departmentRepository.findByDepartmentname("SCM");

            boolean mailsent = emailService.sendFinanceApprovalMailToSCM(department.getDepartmentEmail(), order, financeApproval);

            if (!mailsent)
            {
                return ResponseEntity.ok("Mail Not Sent");
            }
        }

        return ResponseEntity.ok("Order Final Approved Successfully");
    }

    @Override
    public ResponseEntity<?> finalRejectOrder(String username, Long orderId, FinanceOrderDto finalReason)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("FINANCE"))
        {
            return ResponseEntity.status(403).body("Only finance team can approve orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("SCM > FINANCE POST APPROVAL PENDING"))
        {
            return ResponseEntity.status(403).body("Order is not pending for finance approval");
        }

        if (order.getOrderType().equalsIgnoreCase("Free of Cost"))
        {
            FinanceApproval findOrder = financeApprovalRepository.findByOrder_OrderId(order.getOrderId());

            //Finance Approval table data update
            findOrder.setFinanceAction("REJECTED");
            findOrder.setFinanceActionTime(LocalDateTime.now());
            findOrder.setFinanceFinalRemark(finalReason.getFinanceFinalRemark().trim());
            findOrder.setFinanceApprovedBy(user);
            financeApprovalRepository.save(findOrder);

            //Order table status update
            order.setStatus("FINANCE TEAM REJECTED");
            orderRepository.save(order);

            Department department = departmentRepository.findByDepartmentname("PROJECT TEAM");

            boolean mailsent = emailService.sendFinanceRejectedMailToSCM(department.getDepartmentEmail(), order, findOrder);

            if (!mailsent)
            {
                return ResponseEntity.status(500).body("Mail Not Sent");
            }
        }

        if (order.getOrderType().equalsIgnoreCase("Purchase"))
        {
            //Finance Approval table data save
            FinanceApproval financeApproval = new FinanceApproval();
            financeApproval.setFinanceAction("REJECTED");
            financeApproval.setFinanceActionTime(LocalDateTime.now());
            financeApproval.setFinanceReason(finalReason.getFinanceReason().trim());
            financeApproval.setFinanceApprovedBy(user);
            financeApproval.setOrder(order);
            financeApproval.setFinanceFinalRemark(finalReason.getFinanceFinalRemark().trim());
            financeApprovalRepository.save(financeApproval);

            //Order table status update
            order.setStatus("FINANCE TEAM REJECTED");
            orderRepository.save(order);

            Department department = departmentRepository.findByDepartmentname("PROJECT TEAM");

            boolean mailsent = emailService.sendFinanceRejectedMailToSCM(department.getDepartmentEmail(), order, financeApproval);

            if (!mailsent)
            {
                return ResponseEntity.status(500).body("Mail Not Sent");
            }
        }

        return ResponseEntity.ok("Order Final Rejected Successfully");
    }

    @Override
    public ResponseEntity<?> fillOrderClosureDocument(String username, Long orderId, FinanceOrderDto closureDetails)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("FINANCE"))
        {
            return ResponseEntity.status(403).body("Only finance team can approve orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("LOGISTIC > FINANCE CLOSURE PENDING"))
        {
            return ResponseEntity.status(403).body("Order is not pending for finance approval");
        }

            FinanceApproval findOrder = financeApprovalRepository.findByOrder_OrderId(order.getOrderId());

            //Finance Approval table data update
            findOrder.setFinanceApprovalDocumentUrl(closureDetails.getFinanceApprovalDocumentUrl());
            findOrder.setFinanceClosureTime(LocalDateTime.now());
            findOrder.setFinanceClosureStatus(closureDetails.getFinanceClosureStatus());
            financeApprovalRepository.save(findOrder);

            //Order table status update
            order.setStatus("FINANCE CLOSURE DONE > SCM CLOSURE PENDING");
            orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("SCM");

        boolean mailsent = emailService.sendMailDocumentCloseForScm(department.getDepartmentEmail(), order);

        if (!mailsent)
        {
            return ResponseEntity.ok("Mail Not Sent");
        }

        return ResponseEntity.ok("Order Document Closure Successfully");
    }

    @Override
    public ResponseEntity<?> getFinanceOrdersFilterStatus(String username, String status, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("FINANCE"))
        {
            return ResponseEntity.status(403).body("Only finance team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage =  orderRepository.findByStatusForFinance(status, pageable);

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
