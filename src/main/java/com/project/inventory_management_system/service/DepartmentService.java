package com.project.inventory_management_system.service;

import org.springframework.http.ResponseEntity;

public interface DepartmentService 
{
    ResponseEntity<?> getDepartments(String username);
}
