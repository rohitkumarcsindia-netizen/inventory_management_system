package com.project.inventory_management_system.service;

import com.project.inventory_management_system.entity.Department;
import com.project.inventory_management_system.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService
{
    private final DepartmentRepository departmentRepository;


    @Override
    public Department addDepartment(Department department)
    {
        return departmentRepository.save(department);
    }
}
