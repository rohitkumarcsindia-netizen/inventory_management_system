package com.project.inventory_management_system.dto;


import lombok.Data;
import java.time.LocalDateTime;


@Data
public class OrdersDto
{
    private Long orderId;
    private LocalDateTime createAt;
    private java.time.LocalDate expectedOrderDate;
    private String project;
    private String initiator;
    private String productType;
    private String orderType;
    private Integer proposedBuildPlanQty;
    private String reasonForBuildRequest;
    private String status;
    private String pmsRemarks;

    private UserDto users;
}
