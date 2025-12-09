package com.project.inventory_management_system.mapper;

import com.project.inventory_management_system.dto.ScmOrdersDto;
import com.project.inventory_management_system.entity.ScmApproval;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScmOrderMapper
{
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;


    public ScmOrdersDto scmOrdersDto(ScmApproval scmApproval)
    {
        if (scmApproval == null) return null;

        ScmOrdersDto scmOrdersDto = new ScmOrdersDto();
        scmOrdersDto.setId(scmApproval.getId());
        scmOrdersDto.setScmAction(scmApproval.getScmAction());
        scmOrdersDto.setActionTime(scmApproval.getActionTime());
        scmOrdersDto.setScmComments(scmApproval.getScmComments());
        scmOrdersDto.setJiraStatus(scmApproval.getJiraStatus());
        scmOrdersDto.setJiraTicketNumber(scmApproval.getJiraTicketNumber());
        scmOrdersDto.setJiraSummary(scmApproval.getJiraSummary());

        scmOrdersDto.setOrder(orderMapper.toDto(scmApproval.getOrder()));
        scmOrdersDto.setApprovedBy(userMapper.toDto(scmApproval.getApprovedBy())); // nested mapping

        return scmOrdersDto;
    }
}
