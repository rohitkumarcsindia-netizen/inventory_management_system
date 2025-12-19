package com.project.inventory_management_system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RmaOrdersHistoryDto
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

    private String rmaAction;
    private LocalDateTime rmaActionTime;
    private String rmaComment;

    private Long rmaApprovedBy;
    private String rmaApprovedByUserName;

    private UserDto users;
}
