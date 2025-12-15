package com.project.inventory_management_system.dto;

import lombok.Data;

@Data
public class ProjectAndProductTypeDto
{

    private Long Id;

    private String projectType;

    private String productType;

    private UserDto createdBy;
}
