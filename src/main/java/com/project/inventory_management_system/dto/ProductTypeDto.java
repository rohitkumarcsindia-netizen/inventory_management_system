package com.project.inventory_management_system.dto;

import lombok.Data;

@Data
public class ProductTypeDto
{
    private Long Id;

    private String productType;

    private UserDto createdBy;
}
