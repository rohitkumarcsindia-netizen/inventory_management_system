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


    public ProjectTeamOrderDto projectTeamOrderDto(ProjectTeamApproval projectTeamApproval)
    {
        if (projectTeamApproval == null) return null;

        ProjectTeamOrderDto projectTeamOrderDto = new ProjectTeamOrderDto();
        projectTeamOrderDto.setId(projectTeamApproval.getId());
        projectTeamOrderDto.setAmispPdiType(projectTeamApproval.getAmispPdiType());
        projectTeamOrderDto.setProjectTeamActionTime(projectTeamApproval.getProjectTeamActionTime());
        projectTeamOrderDto.setProjectTeamComment(projectTeamApproval.getProjectTeamComment());
        projectTeamOrderDto.setPdiLocation(projectTeamApproval.getPdiLocation());
        projectTeamOrderDto.setDispatchDetails(projectTeamApproval.getDispatchDetails());
        projectTeamOrderDto.setDocumentUrl(projectTeamApproval.getDocumentUrl());
        projectTeamOrderDto.setSerialNumbers(projectTeamApproval.getSerialNumbers());
        projectTeamOrderDto.setLocationDetails(projectTeamApproval.getLocationDetails());
        projectTeamOrderDto.setPdiAction(projectTeamApproval.getPdiAction());
        projectTeamOrderDto.setPdiComment(projectTeamApproval.getPdiComment());


        projectTeamOrderDto.setOrder(orderMapper.toDto(projectTeamApproval.getOrder()));
        projectTeamOrderDto.setActionBy(userMapper.toDto(projectTeamApproval.getActionBy())); // nested mapping

        return projectTeamOrderDto;
    }
}
