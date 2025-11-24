package com.project.inventory_management_system.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    private String scmAction;
    private LocalDateTime scmActionTime;

    private UserDto users;
}
