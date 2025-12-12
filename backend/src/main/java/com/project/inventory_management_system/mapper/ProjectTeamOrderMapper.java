package com.project.inventory_management_system.mapper;


import com.project.inventory_management_system.dto.ProjectTeamOrderDto;
import com.project.inventory_management_system.entity.ProjectTeamApproval;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectTeamOrderMapper
{
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;


    public ProjectTeamOrderDto amispOrdersDto(ProjectTeamApproval projectTeamApproval)
    {
        if (projectTeamApproval == null) return null;

        ProjectTeamOrderDto amispOrdersDto = new ProjectTeamOrderDto();
        amispOrdersDto.setId(projectTeamApproval.getId());
        amispOrdersDto.setAmispPdiType(projectTeamApproval.getAmispPdiType());
        amispOrdersDto.setProjectTeamActionTime(projectTeamApproval.getProjectTeamActionTime());
        amispOrdersDto.setProjectTeamComment(projectTeamApproval.getProjectTeamComment());
        projectTeamApproval.setPdiLocation(projectTeamApproval.getPdiLocation());
        amispOrdersDto.setDispatchDetails(projectTeamApproval.getDispatchDetails());
        amispOrdersDto.setDocumentUrl(projectTeamApproval.getDocumentUrl());
        amispOrdersDto.setSerialNumbers(projectTeamApproval.getSerialNumbers());
        amispOrdersDto.setLocationDetails(projectTeamApproval.getLocationDetails());

        amispOrdersDto.setOrder(orderMapper.toDto(projectTeamApproval.getOrder()));
        amispOrdersDto.setActionBy(userMapper.toDto(projectTeamApproval.getActionBy())); // nested mapping

        return amispOrdersDto;
    }
}
