package com.project.inventory_management_system.mapper;


import com.project.inventory_management_system.dto.DepartmentDto;
import com.project.inventory_management_system.dto.UserDto;
import com.project.inventory_management_system.entity.Department;
import org.springframework.stereotype.Component;


@Component
public class DepartmentMapper
{
    //Entity to Dto
    public DepartmentDto toDto(Department department)
    {
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setId(department.getId());
        departmentDto.setDepartmentName(department.getDepartmentName());
        departmentDto.setDepartmentEmail(department.getDepartmentEmail());

        return departmentDto;
    }
}
