package com.project.inventory_management_system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectTeamOrdersHistoryDto
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


    private String amispAction;
    private LocalDateTime amispActionTime;
    private String amispComment;
    private String pdiLocation;
    private String serialNumbers;
    private String dispatchDetails;
    private String documentUrl;
    private String locationDetails;

    private Long amispApprovedBy;

    private UserDto users;
}
