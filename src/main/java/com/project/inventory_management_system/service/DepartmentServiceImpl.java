package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.DepartmentDto;
import com.project.inventory_management_system.dto.ProjectTypeDto;
import com.project.inventory_management_system.entity.Department;
import com.project.inventory_management_system.entity.ProjectType;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.mapper.DepartmentMapper;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService
{
    private final DepartmentRepository departmentRepository;
    private final UsersRepository usersRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    public ResponseEntity<?> getDepartments(String username)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if(!user.getDepartment().getDepartmentName().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only Admin can view Department");
        }
        List<Department> departmentList = departmentRepository.findAll();

        if (departmentList.isEmpty())
        {
            return ResponseEntity.ok("Department not found");
        }

        List<DepartmentDto> departmentDtoList = departmentList.stream()
                .map(departmentMapper::toDto)
                .toList();

        return ResponseEntity.ok(departmentDtoList);
    }
}
