package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.dto.ScmOrdersDto;
import com.project.inventory_management_system.dto.ScmOrdersHistoryDto;
import com.project.inventory_management_system.entity.*;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.mapper.OrdersCompleteMapper;
import com.project.inventory_management_system.mapper.ScmOrderMapper;
import com.project.inventory_management_system.repository.*;
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
public class ScmOrderServiceImpl implements ScmOrderService
{


    private final OrderRepository orderRepository;
    private final UsersRepository usersRepository;
    private final DepartmentRepository departmentRepository;
    private final EmailService emailService;
    private final OrderMapper orderMapper;
    private final OrdersCompleteMapper ordersCompleteMapper;
    private final ScmApprovalRepository scmApprovalRepository;
    private final ProjectTeamApprovalRepository projectTeamApprovalRepository;
    private final ScmOrderMapper scmOrderMapper;
    private final OrderStatusByDepartmentService orderStatusByDepartmentService;



    @Override
    public ResponseEntity<?> getPendingOrdersForScm(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only SCM team can view approved orders");
        }

        List<String> scmStatuses = orderStatusByDepartmentService.getStatusesByDepartment( user.getDepartment().getDepartmentname());

        List<Orders> ordersList = orderRepository.findByStatusWithLimitOffset(scmStatuses, offset, limit);

        if (ordersList.isEmpty())
        {
            return ResponseEntity.ok("No Orders found");
        }

        List<OrdersDto> ordersDtoList = ordersList.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.countByStatus(scmStatuses),
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

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only Scm team can view complete orders");
        }

        List<ScmApproval> scmApprovalsOrders = scmApprovalRepository.findByScmActionIsNotNull(limit, offset);

        if (scmApprovalsOrders.isEmpty())
        {
            return ResponseEntity.ok("No Orders found");
        }
        List<ScmOrdersHistoryDto> scmOrdersHistoryDtoList = scmApprovalsOrders.stream()
                .map(approval -> ordersCompleteMapper.scmOrdersHistoryDto(
                        approval.getOrder(), approval))
                .toList();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", scmApprovalRepository.countByScmAction(),
                "orders", scmOrdersHistoryDtoList
        ));
    }

    @Override
    public ResponseEntity<?> fillJiraTicketDetail(String username, Long orderId, ScmApproval jiraDetails)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only Scm team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        String status = order.getStatus();
        boolean allowed = status.equalsIgnoreCase("FINANCE APPROVED > SCM PENDING") || status.equalsIgnoreCase("PROJECT TEAM > SCM PENDING");

        if (!allowed)
        {
            return ResponseEntity.status(403).body("Jira details can only be submitted when the order is pending for SCM action");
        }

        //ScmApproval Table data insert
        ScmApproval scmApproval = new ScmApproval();
        scmApproval.setOrder(order);
        scmApproval.setJiraTicketNumber(jiraDetails.getJiraTicketNumber());
        scmApproval.setJiraSummary(jiraDetails.getJiraSummary());
        scmApproval.setJiraStatus(jiraDetails.getJiraStatus());
        scmApproval.setScmAction("JIRA FILLED");
        scmApproval.setActionTime(LocalDateTime.now());
        scmApproval.setApprovedBy(user);
        scmApproval.setScmComments(jiraDetails.getScmComments());
        scmApprovalRepository.save(scmApproval);


        order.setStatus("SCM CREATED TICKET > CLOUD PENDING");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("CLOUD TEAM");

        boolean mailsent = emailService.sendMailCreateJiraTicket(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }


        return ResponseEntity.ok("Jira details filled successfully");
    }



    // Scm Recheck Method
    @Override
    public ResponseEntity<?> prodbackGenerateAndJiraTicketClosure(String username, Long orderId, ScmApproval jiraDetails)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only Scm team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("CLOUD CREATED CERTIFICATE > SCM PROD-BACK CREATION PENDING"))
        {
            return ResponseEntity.status(403).body("Jira details can only be submitted when the order is pending for SCM action");
        }

        ScmApproval jiraDetailsUpdate = scmApprovalRepository.findLatestByOrderId(order.getOrderId());

        if (jiraDetailsUpdate == null)
        {
            return ResponseEntity.status(404).body("SCM approval record not found for update");
        }

        jiraDetailsUpdate.setJiraStatus(jiraDetails.getJiraStatus());
        jiraDetailsUpdate.setScmComments(jiraDetails.getScmComments());
        jiraDetailsUpdate.setScmAction("JIRA_VERIFIED");
        jiraDetailsUpdate.setActionTime(LocalDateTime.now());
        jiraDetailsUpdate.setApprovedBy(user);
        scmApprovalRepository.save(jiraDetailsUpdate);


        order.setStatus("SCM JIRA TICKET CLOSURE > SYRMA PENDING");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("SYRMA");

        boolean mailsent = emailService.sendMailProdbackGenerate(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Jira details filled successfully");
    }

    //old button method
    @Override
    public ResponseEntity<?> fillJiraTicketDetailOldOrder(String username, Long orderId, ScmApproval jiraDetails)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only Scm team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        String status = order.getStatus();
        boolean allowed = status.equalsIgnoreCase("FINANCE APPROVED > SCM PENDING") || status.equalsIgnoreCase("PROJECT TEAM > SCM PENDING");

        if (!allowed)
        {
            return ResponseEntity.status(403).body("Jira details can only be submitted when the order is pending for SCM action");
        }

        //ScmApproval Table data insert
        ScmApproval scmApproval = new ScmApproval();
        scmApproval.setOrder(order);
        scmApproval.setJiraTicketNumber(jiraDetails.getJiraTicketNumber());
        scmApproval.setJiraSummary(jiraDetails.getJiraSummary());
        scmApproval.setJiraStatus(jiraDetails.getJiraStatus());
        scmApproval.setScmAction("JIRA FILLED");
        scmApproval.setActionTime(LocalDateTime.now());
        scmApproval.setScmComments(jiraDetails.getScmComments());
        scmApproval.setApprovedBy(user);
        scmApprovalRepository.save(scmApproval);


        order.setStatus("SCM JIRA TICKET CLOSURE > SYRMA PENDING");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("CLOUD TEAM");

        boolean mailsent = emailService.sendMailOrderApprove(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }


        return ResponseEntity.ok("Jira details filled successfully");
    }

    @Override
    public ResponseEntity<?> scmNotifyRma(String username, Long orderId)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only Scm team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        String status = order.getStatus();
        boolean allowed = status.equalsIgnoreCase("SYRMA PROD/TEST DONE > SCM ACTION PENDING") || status.equalsIgnoreCase("SYRMA RE-PROD/TEST DONE > SCM ACTION PENDING");

        if (!allowed)
        {
            return ResponseEntity.status(403).body("Notify details can only be submitted when the order is pending for SCM action");
        }

        order.setStatus("SCM NOTIFY > RMA QC PENDING");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("RMA");

        boolean mailsent = emailService.sendMailNotifyRma(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Notification sent for RMA");
    }

    @Override
    public ResponseEntity<?> scmNotifyProjectTeam(String username, Long orderId)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only Scm team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("RMA QC PASS > SCM ORDER RELEASE PENDING"))
        {
            return ResponseEntity.status(403).body("Notify details can only be submitted when the order is pending for SCM action");
        }

        order.setStatus("SCM NOTIFY > PROJECT TEAM BUILD IS READY");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("PROJECT TEAM");

        boolean mailsent = emailService.sendMailNotifyProjectTeam(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Notification sent for PROJECT TEAM");
    }

    @Override
    public ResponseEntity<?> scmNotifyAmisp(String username, Long orderId)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only Scm team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        ProjectTeamApproval projectTeamApproval = projectTeamApprovalRepository.findByOrder_OrderId(order.getOrderId());

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("PROJECT TEAM > SCM READY FOR DISPATCH"))
        {
            return ResponseEntity.status(403).body("Notify details can only be submitted when the order is pending for SCM action");
        }

        order.setStatus("SCM NOTIFY > AMISP READY FOR DISPATCH");
        orderRepository.save(order);


        ProjectTeamApproval amispEmailId = projectTeamApprovalRepository.findByOrder_OrderId(order.getOrderId());

        boolean mailsent = emailService.sendMailNotifyScmToAmisp(amispEmailId.getAmispEmailId(), order, projectTeamApproval);

        if (!mailsent)
        {
            return ResponseEntity.ok("Mail Not Sent");
        }

        return ResponseEntity.ok("Notification sent for AMISP");
    }

    @Override
    public ResponseEntity<?> scmApprovalRequestForFinance(String username, Long orderId)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only Scm team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);
        ProjectTeamApproval projectTeamApproval = projectTeamApprovalRepository.findByOrder_OrderId(order.getOrderId());

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("PROJECT TEAM NOTIFY > SCM LOCATION DETAILS"))
        {
            return ResponseEntity.status(403).body("Notify details can only be submitted when the order is pending for Scm action");
        }

        order.setStatus("SCM > FINANCE POST APPROVAL PENDING");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("FINANCE");


        boolean mailsent = emailService.sendMailScmToFinanceApproval(department.getDepartmentEmail(), order, projectTeamApproval);

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Notification sent for FINANCE TEAM");
    }

    @Override
    public ResponseEntity<?> scmPlanDispatchAndEmailLogistic(String username, Long orderId)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only Scm team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);
        ProjectTeamApproval projectTeamApproval = projectTeamApprovalRepository.findByOrder_OrderId(order.getOrderId());

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("FINANCE > SCM PLAN TO DISPATCH"))
        {
            return ResponseEntity.status(403).body("Notify details can only be submitted when the order is pending for Scm action");
        }

        order.setStatus("SCM > LOGISTIC PENDING");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("LOGISTIC");


        boolean mailsent = emailService.sendMailScmToLogisticTeam(department.getDepartmentEmail(), order, projectTeamApproval);

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Notification sent for LOGISTIC TEAM");
    }

    @Override
    public ResponseEntity<?> getScmOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only scm team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage = orderRepository.findByDateRangeForScm(start, end, pageable);
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
    public ResponseEntity<?> getScmpOrdersFilterStatus(String username, String status, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only scm team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage =  orderRepository.findByStatusForScm(status, pageable);

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
    public ResponseEntity<?> getOrdersSearchForScm(String username, String keyword, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only scm team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage = orderRepository.searchScm(keyword.trim(), pageable);

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
    public ResponseEntity<?> getScmCompleteOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only scm team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("actionTime").descending());
        Page<ScmApproval> scmApprovalPage = scmApprovalRepository.findByDateRange(start, end, pageable);
        if (scmApprovalPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<ScmOrdersDto> scmOrdersDtoList = scmApprovalPage.stream()
                .map(scmOrderMapper::scmOrdersDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", scmApprovalPage.getTotalElements(),
                "totalPages", scmApprovalPage.getTotalPages(),
                "page", scmApprovalPage.getNumber(),
                "size", scmApprovalPage.getSize(),
                "records", scmOrdersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getScmCompleteOrdersFilterStatus(String username, String status, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only scm team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("actionTime").descending());
        Page<ScmApproval> scmApprovalPage =  scmApprovalRepository.findByStatusFilter(status, pageable);

        if (scmApprovalPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<ScmOrdersDto> scmOrdersDtoList = scmApprovalPage.stream()
                .map(scmOrderMapper::scmOrdersDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", scmApprovalPage.getTotalElements(),
                "totalPages", scmApprovalPage.getTotalPages(),
                "page", scmApprovalPage.getNumber(),
                "size", scmApprovalPage.getSize(),
                "records", scmOrdersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getScmCompleteOrdersFilterSearch(String username, String keyword, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only scm team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("actionTime").descending());
        Page<ScmApproval> scmApprovalPage = scmApprovalRepository.searchScmComplete(keyword.trim(), pageable);

        if (scmApprovalPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<ScmOrdersDto> scmOrdersDtoList = scmApprovalPage.stream()
                .map(scmOrderMapper::scmOrdersDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", scmApprovalPage.getTotalElements(),
                "totalPages", scmApprovalPage.getTotalPages(),
                "page", scmApprovalPage.getNumber(),
                "size", scmApprovalPage.getSize(),
                "records", scmOrdersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> scmOrderCompleted(String username, Long orderId)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.status(403).body("Only Scm team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("FINANCE CLOSURE DONE > SCM CLOSURE PENDING"))
        {
            return ResponseEntity.status(403).body("Notify details can only be submitted when the order is pending for SCM action");
        }

        order.setStatus("COMPLETED");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("PROJECT TEAM");

        boolean mailsent = emailService.sendMailOrderCompletedForProjectTeam(department.getDepartmentEmail(), order);

        if (!mailsent)
        {
            return ResponseEntity.ok("Mail Not Sent");
        }

        return ResponseEntity.ok("Notification sent for Project Team");
    }
}
