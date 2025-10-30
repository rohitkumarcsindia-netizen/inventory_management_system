package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.entity.Department;
import com.project.inventory_management_system.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentController
{
    private final DepartmentService departmentService;


    @PostMapping("/createdepartment")
    public ResponseEntity<?> addDepartment(@RequestBody Department department)
    {
        Department newDepartment = departmentService.addDepartment(department);
        return ResponseEntity.ok(newDepartment);
    }
}
