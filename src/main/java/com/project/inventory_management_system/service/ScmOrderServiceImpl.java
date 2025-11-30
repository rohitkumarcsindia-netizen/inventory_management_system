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
            return ResponseEntity.badRequest().body("Only SCM team can view approved orders");
        }

        List<Orders> orders = orderRepository.findByStatusWithLimitOffset("SCM PENDING", offset, limit);

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
    public ResponseEntity<?> getCompleteOrdersForScm(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.badRequest().body("Only Scm team can view complete orders");
        }

        List<ScmApproval> scmApprovalsOrders = scmApprovalRepository.findByScmActionIsNotNull(limit, offset);

        if (scmApprovalsOrders.isEmpty())
        {
            return ResponseEntity.badRequest().body("No Orders found");
        }
        List<ScmOrdersHistoryDto> list = scmApprovalsOrders.stream()
                .map(approval -> ordersCompleteMapper.scmOrdersHistoryDto(
                        approval.getOrder(), approval))
                .toList();

        return ResponseEntity.ok(list);
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
            return ResponseEntity.badRequest().body("Only Scm team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.badRequest().body("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("SCM PENDING"))
        {
            return ResponseEntity.badRequest().body("Jira details can only be submitted when the order is pending for SCM action");
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

        boolean mailsent = emailService.sendMailOrderApprove(department.getDepartmentEmail(), order.getOrderId());

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
            return ResponseEntity.badRequest().body("Only Scm team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.badRequest().body("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("SCM RECHECK PENDING"))
        {
            return ResponseEntity.badRequest().body("Jira details can only be submitted when the order is pending for SCM action");
        }

        ScmApproval jiraDetailsUpdate = scmApprovalRepository.findLatestByOrderId(order.getOrderId());

        if (jiraDetailsUpdate == null)
        {
            return ResponseEntity.badRequest().body("SCM approval record not found for update");
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

        boolean mailsent = emailService.sendMailOrderApprove(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Jira details filled successfully");
    }

    @Override
    public ResponseEntity<?> getScmRecheckOrderPending(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SCM"))
        {
            return ResponseEntity.badRequest().body("Only SCM team can view approved orders");
        }

        List<Orders> orders = orderRepository.findByStatusWithLimitOffset("SCM RECHECK PENDING", offset, limit);

        if (orders.isEmpty())
        {
            return ResponseEntity.badRequest().body("No orders found");
        }

        List<OrdersDto> list = orders.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(list);
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
            return ResponseEntity.badRequest().body("Only Scm team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.badRequest().body("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("SCM PENDING"))
        {
            return ResponseEntity.badRequest().body("Jira details can only be submitted when the order is pending for SCM action");
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
}
