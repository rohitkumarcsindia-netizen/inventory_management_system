package com.project.inventory_management_system.dto;

import lombok.Data;

@Data
public class ProjectTypeDto
{

    private Long Id;

    private String projectType;

    private UserDto createdBy;
}
