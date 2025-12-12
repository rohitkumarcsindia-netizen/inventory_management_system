package com.project.inventory_management_system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RmaOrdersDto
{
    private Long id;
    private OrdersDto order;

    private String rmaAction;
    private LocalDateTime rmaActionTime;

    private String rmaComment;

    private UserDto rmaApprovedBy;
}
