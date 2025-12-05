package com.project.inventory_management_system.dto;

import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FinanceOrderDto
{

    private Long id;
    private OrdersDto order;

    private String financeAction;
    private LocalDateTime financeActionTime;

    private String financeReason;

    private UserDto financeApprovedBy;

    private String financeFinalRemark;

    private String financeApprovalDocumentUrl;
    private String financeClosureStatus;
    private LocalDateTime financeClosureTime;

}
