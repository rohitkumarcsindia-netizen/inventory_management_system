package com.project.inventory_management_system.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.inventory_management_system.entity.Users;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudOrdersHistoryDto
{
    private Long orderId;
    private LocalDateTime createAt;
    private java.time.LocalDate expectedOrderDate;
    private String project;
    private String initiator;
    private String productType;
    private String orderType;
    private Integer proposedBuildPlanQty;
    private String aktsComments;
    private String reasonForBuildRequest;
    private String pmsRemarks;

    private String cloudAction;
    private LocalDateTime cloudActionTime;

    private String jiraDescription;
    private String priority;
    private String cloudComments;
    private Long updatedBy;

    private UserDto users;
}
