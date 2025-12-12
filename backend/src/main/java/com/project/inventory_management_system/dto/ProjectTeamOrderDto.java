package com.project.inventory_management_system.dto;


import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class ProjectTeamOrderDto
{
    private Long id;

    private OrdersDto order;

    private String amispPdiType;
    private LocalDateTime projectTeamActionTime;

    private String amispEmailId;

    private String projectTeamComment;

    private UserDto actionBy;

    private String pdiLocation;
    private String serialNumbers;
    private String dispatchDetails;
    private String documentUrl;
    private String locationDetails;
}
