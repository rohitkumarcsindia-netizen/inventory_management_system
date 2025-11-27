package com.project.inventory_management_system.dto;

import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SyrmaOrdersDto
{
    private Orders order;
    private String syrmaAction;
    private LocalDateTime actionTime;
    private String syrmaComments;
    private Users actionDoneBy;
}
