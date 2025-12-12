package com.project.inventory_management_system.mapper;

import com.project.inventory_management_system.dto.CloudOrdersDto;
import com.project.inventory_management_system.dto.RmaOrdersDto;
import com.project.inventory_management_system.entity.CloudApproval;
import com.project.inventory_management_system.entity.RmaApproval;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RmaOrdersMapper
{

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    //Entity to Dto
    public RmaOrdersDto rmaOrdersDto(RmaApproval rmaApproval)
    {
        if (rmaApproval == null) return null;

        RmaOrdersDto rmaOrdersDto = new RmaOrdersDto();
        rmaOrdersDto.setId(rmaApproval.getId());
        rmaOrdersDto.setRmaAction(rmaApproval.getRmaAction());
        rmaOrdersDto.setRmaActionTime(rmaApproval.getRmaActionTime());
        rmaOrdersDto.setRmaComment(rmaApproval.getRmaComment());

        rmaOrdersDto.setOrder(orderMapper.toDto(rmaApproval.getOrder()));
        rmaOrdersDto.setRmaApprovedBy(userMapper.toDto(rmaApproval.getApprovedBy())); // nested mapping

        return rmaOrdersDto;
    }
}
