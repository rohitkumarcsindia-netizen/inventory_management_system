package com.project.inventory_management_system.dto;


import lombok.Data;

@Data
public class OrdersDto
{
    private Long orderId;
    private String project;
    private String initiator;
    private String productType;
    private Integer proposedBuildPlanQty;
    private String aktsComments;
    private String reasonForBuildRequest;

    private UserDto users;
}
