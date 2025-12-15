package com.project.inventory_management_system.mapper;

import com.project.inventory_management_system.dto.ProjectAndProductTypeDto;
import com.project.inventory_management_system.entity.ProjectAndProductType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectAndProductTypeMappers
{
    //Dto to Entity
    public ProjectAndProductType toEntity(ProjectAndProductTypeDto projectAndProductTypeDto)
    {
        if (projectAndProductTypeDto == null) return null;

        ProjectAndProductType projectAndProductType = new ProjectAndProductType();
        projectAndProductType.setProjectType(projectAndProductTypeDto.getProjectType().toUpperCase());
        projectAndProductType.setProductType(projectAndProductTypeDto.getProductType().toUpperCase());

        return projectAndProductType;
    }

    //Entity to Dto
    public ProjectAndProductTypeDto toDto(ProjectAndProductType projectAndProductType)
    {
        if (projectAndProductType == null) return null;

        ProjectAndProductTypeDto projectAndProductTypeDto = new ProjectAndProductTypeDto();
        projectAndProductTypeDto.setId(projectAndProductType.getId());
        projectAndProductTypeDto.setProjectType(projectAndProductType.getProjectType());
        projectAndProductTypeDto.setProductType(projectAndProductType.getProductType());

        return projectAndProductTypeDto;
    }
}
