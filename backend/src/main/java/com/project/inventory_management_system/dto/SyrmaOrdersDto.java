package com.project.inventory_management_system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SyrmaOrdersDto
{
    private Long id;
    private OrdersDto order;
    private String syrmaAction;
    private LocalDateTime actionTime;
    private String syrmaComments;
    private UserDto actionDoneBy;
}
