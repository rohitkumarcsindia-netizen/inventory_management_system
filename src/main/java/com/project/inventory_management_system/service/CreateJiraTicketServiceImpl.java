package com.project.inventory_management_system.service;


import com.project.inventory_management_system.entity.Department;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
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

import java.util.Base64;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class CreateJiraTicketServiceImpl implements CreateJiraTicketService {

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

    @Override
    public ResponseEntity<?> createJiraTicket(String username, Long orderId)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().equalsIgnoreCase("scm"))
        {
            return ResponseEntity.badRequest().body("Order must be approved for finance before Jira ticket creation");
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
            order.setStatus("cloud team");
            orderRepository.save(order);

            Department department = departmentRepository.findByDepartmentname("cloud team");

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
}
