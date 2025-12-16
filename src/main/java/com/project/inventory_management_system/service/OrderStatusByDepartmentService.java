package com.project.inventory_management_system.service;

import java.util.List;

public interface OrderStatusByDepartmentService
{

    List<String> getStatusesByDepartment(String departmentName);
}
