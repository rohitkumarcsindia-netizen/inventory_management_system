package com.project.inventory_management_system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.inventory_management_system.entity.Users;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScmOrdersHistoryDto
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

    private String scmAction;
    private LocalDateTime scmActionTime;

    private String jiraTicketNumber;
    private String jiraSummary;
    private String jiraStatus;
    private Long approvedBy;
    private String approvedByUserName;

    private UserDto users;
}
