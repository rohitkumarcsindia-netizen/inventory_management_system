package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.dto.ScmOrdersHistoryDto;
import com.project.inventory_management_system.entity.*;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.mapper.OrdersCompleteMapper;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.ScmApprovalRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
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

        // Allowed SCM statuses (priority order)
        List<String> scmStatuses = List.of(
                "SCM PENDING",
                "CLOUD > SCM RECHECK PENDING",
                "SYRMA > SCM RECHECK PENDING",
                "RMA > SCM RECHECK PENDING"
        );

        List<Orders> ordersList = orderRepository.findOrdersForScm(scmStatuses, offset, limit);

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
                "ordersCount", orderRepository.countOrdersForScm(scmStatuses),
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

        if (!order.getStatus().equalsIgnoreCase("SCM PENDING"))
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


        order.setStatus("CLOUD PENDING");
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

        if (!order.getStatus().equalsIgnoreCase("CLOUD > SCM RECHECK PENDING"))
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


        order.setStatus("SYRMA PENDING");
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

        if (!order.getStatus().equalsIgnoreCase("SCM PENDING"))
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


        order.setStatus("SYRMA PENDING");
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

        if (!order.getStatus().equalsIgnoreCase("SYRMA > SCM RECHECK PENDING"))
        {
            return ResponseEntity.status(403).body("Notify details can only be submitted when the order is pending for SCM action");
        }

        order.setStatus("RMA PENDING");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("RMA");

        boolean mailsent = emailService.sendMailNotifyRma(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Notification sent for Rma");
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

        if (!order.getStatus().equalsIgnoreCase("RMA > SCM RECHECK PENDING"))
        {
            return ResponseEntity.status(403).body("Notify details can only be submitted when the order is pending for SCM action");
        }

        order.setStatus("SCM > PROJECT TEAM PENDING");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("PROJECT TEAM");

        boolean mailsent = emailService.sendMailNotifyProjectTeam(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Notification sent for Rma");
    }
}
