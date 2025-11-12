package com.project.inventory_management_system.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrdersDto
{
    private Long orderId;
    private java.time.LocalDate orderDate;
    private String project;
    private String initiator;
    private String productType;
    private Integer proposedBuildPlanQty;
    private String aktsComments;
    private String reasonForBuildRequest;
    private String status;
    private String pmsRemarks;

    private UserDto users;
}
