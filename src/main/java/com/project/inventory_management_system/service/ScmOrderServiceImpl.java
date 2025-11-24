package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.OrdersCompleteDto;
import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.Department;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.mapper.OrdersCompleteMapper;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ScmOrderServiceImpl implements ScmOrderService
{

    @Value("${jira.url}")
    private String jiraUrl;

    @Value("${jira.email}")
    private String jiraEmail;

    @Value("${jira.token}")
    private String jiraToken;

    @Value("${jira.project.key}")
    private String projectKey;

    private final OrderRepository orderRepository;
    private final UsersRepository usersRepository;
    private final DepartmentRepository departmentRepository;
    private final EmailService emailService;
    private final OrderMapper orderMapper;
    private final OrdersCompleteMapper ordersCompleteMapper;



    @Override
    public ResponseEntity<?> getApprovedOrdersForScm(String username, int offset, int limit)
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

        List<Orders> orders = orderRepository.findByStatusWithLimitOffset("SCM_PENDING", offset, limit);

        List<OrdersDto> list = orders.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(list);
    }



    @Override
    public ResponseEntity<?> createJiraTicket(String username, Long orderId)
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

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.badRequest().body("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("SCM_PENDING"))
        {
            return ResponseEntity.badRequest().body("Finance approval required or Jira ticket already created for this order");
        }

        String summary = "Build Request for Order #" + orderId;

        //ADF FORMAT DESCRIPTION
        Map<String, Object> descriptionADF = Map.of(
                "type", "doc",
                "version", 1,
                "content", java.util.List.of(
                        Map.of(
                                "type", "paragraph",
                                "content", java.util.List.of(
                                        Map.of("type", "text", "text", "Order ID: " + orderId + "\n"),
                                        Map.of("type", "text", "text", "Customer: " + order.getUsers().getUsername() + "\n"),
                                        Map.of("type", "text", "text", "Please process this build task.")
                                )
                        )
                )
        );

        String apiUrl = jiraUrl + "/rest/api/3/issue";

        String auth = jiraEmail + ":" + jiraToken;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + encodedAuth);

        Map<String, Object> body = Map.of(
                "fields", Map.of(
                        "project", Map.of("key", projectKey),
                        "summary", summary,
                        "description", descriptionADF,     //ADF
                        "issuetype", Map.of("name", "Task")
                )
        );

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            Map response = restTemplate.postForObject(apiUrl, requestEntity, Map.class);
            String ticketKey = (String) response.get("key");

            order.setJiraTicket(ticketKey);
            order.setStatus("CLOUD_PENDING");
            order.setScmAction("JIRA_CREATED");
            order.setScmActionTime(LocalDateTime.now());
            orderRepository.save(order);

            Department department = departmentRepository.findByDepartmentname("CLOUD TEAM");

            boolean mailsent = emailService.sendMailCreateJiraTicket(department.getDepartmentEmail(), order.getOrderId());

            if (!mailsent)
            {
                return ResponseEntity.status(500).body("Mail Not Sent");
            }

            return ResponseEntity.ok("Jira Ticket Created: " + ticketKey);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(500).body("Failed: " + e.getMessage());
        }
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
            return ResponseEntity.badRequest().body("Only finance team can view complete orders");
        }

        List<Orders> orders = orderRepository.findByScmActionIsNotNull(offset, limit);

        List<OrdersCompleteDto> list = orders.stream()
                .map(ordersCompleteMapper::toDto)
                .toList();

        return ResponseEntity.ok(list);
    }
}
