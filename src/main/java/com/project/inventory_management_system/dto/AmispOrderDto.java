package com.project.inventory_management_system.dto;


import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class AmispOrderDto
{
    private Long id;

    private OrdersDto order;

    private String amispAction;
    private LocalDateTime amispActionTime;


    private String amispComment;

    private UserDto amispApprovedBy;

    private String pdiLocation;
    private String serialNumbers;
    private String dispatchDetails;
    private String documentUrl;
}
