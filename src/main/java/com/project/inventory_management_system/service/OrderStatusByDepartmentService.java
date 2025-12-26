package com.project.inventory_management_system.service;

import com.project.inventory_management_system.enums.OrderStatus;

import java.util.List;

public interface OrderStatusByDepartmentService
{

    List<OrderStatus> getStatusesByDepartment(String departmentName);
}
