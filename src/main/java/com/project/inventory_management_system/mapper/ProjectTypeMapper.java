package com.project.inventory_management_system.mapper;

import com.project.inventory_management_system.dto.ProjectTypeDto;
import com.project.inventory_management_system.entity.ProjectType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectTypeMapper
{
    private final UserMapper userMapper;

    //Dto to Entity
    public ProjectType toEntity(ProjectTypeDto projectTypeDto)
    {
        if (projectTypeDto == null) return null;

        ProjectType projectType = new ProjectType();
        projectType.setProjectType(projectTypeDto.getProjectType().toUpperCase());
        projectType.setUsers(userMapper.toEntity(projectTypeDto.getCreatedBy()));

        return projectType;
    }

    //Entity to Dto
    public ProjectTypeDto toDto(ProjectType projectType)
    {
        if (projectType == null) return null;

        ProjectTypeDto projectTypeDto = new ProjectTypeDto();
        projectTypeDto.setId(projectType.getId());
        projectTypeDto.setProjectType(projectType.getProjectType());
        projectTypeDto.setCreatedBy(userMapper.toDto(projectType.getUsers()));

        return projectTypeDto;
    }
}
