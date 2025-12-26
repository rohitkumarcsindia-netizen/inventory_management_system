package com.project.inventory_management_system.dto;

import lombok.Data;

@Data
public class ProductRequestDto
{
    private String productName;
    private Integer quantity;
}
