package com.project.inventory_management_system.dto;


import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CloudOrdersDto
{
    private Long id;
    private OrdersDto order;

    private String jiraDescription;
    private String priority;
    private String cloudComments;

    private String cloudAction;
    private LocalDateTime actionTime;

    private UserDto updatedBy;
}
