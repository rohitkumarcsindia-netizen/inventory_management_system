package com.project.inventory_management_system.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrdersCompleteDto
{

    private Long orderId;
    private java.time.LocalDate orderDate;
    private String project;
    private String initiator;
    private String productType;
    private String orderType;
    private Integer proposedBuildPlanQty;
    private String aktsComments;
    private String reasonForBuildRequest;
    private String pmsRemarks;
    private String financeAction;
    private LocalDateTime financeActionTime;

    private UserDto users;
}
