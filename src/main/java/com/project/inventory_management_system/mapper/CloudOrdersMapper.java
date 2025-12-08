package com.project.inventory_management_system.mapper;

import com.project.inventory_management_system.dto.CloudOrdersDto;
import com.project.inventory_management_system.entity.CloudApproval;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CloudOrdersMapper
{
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    //Entity to Dto
    public CloudOrdersDto cloudOrdersDto(CloudApproval cloudApproval)
    {
        if (cloudApproval == null) return null;

        CloudOrdersDto cloudOrdersDto = new CloudOrdersDto();
         cloudOrdersDto.setId(cloudApproval.getId());
         cloudOrdersDto.setCloudAction(cloudApproval.getCloudAction());
         cloudOrdersDto.setActionTime(cloudApproval.getActionTime());
         cloudOrdersDto.setPriority(cloudApproval.getPriority());
         cloudOrdersDto.setCloudComments(cloudOrdersDto.getCloudComments());
         cloudOrdersDto.setJiraDescription(cloudOrdersDto.getJiraDescription());

        cloudOrdersDto.setOrder(orderMapper.toDto(cloudApproval.getOrder()));
        cloudOrdersDto.setUpdatedBy(userMapper.toDto(cloudApproval.getUpdatedBy())); // nested mapping

        return cloudOrdersDto;
    }
}
