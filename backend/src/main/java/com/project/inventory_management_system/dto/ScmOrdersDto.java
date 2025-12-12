package com.project.inventory_management_system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScmOrdersDto
{
    private Long id;
    private OrdersDto order;

    private String jiraTicketNumber;
    private String jiraSummary;
    private String jiraStatus;
    private String scmAction;
    private LocalDateTime actionTime;
    private String scmComments;

    private UserDto approvedBy;
}
