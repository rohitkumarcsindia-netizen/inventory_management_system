package com.project.inventory_management_system.mapper;


import com.project.inventory_management_system.dto.SyrmaOrdersDto;
import com.project.inventory_management_system.entity.SyrmaApproval;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SyrmaOrdersMapper
{
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;


    public SyrmaOrdersDto syrmaOrdersDto(SyrmaApproval syrmaApproval)
    {
        if (syrmaApproval == null) return null;

        SyrmaOrdersDto syrmaOrdersDto = new SyrmaOrdersDto();
        syrmaOrdersDto.setId(syrmaApproval.getId());
        syrmaOrdersDto.setSyrmaAction(syrmaApproval.getSyrmaAction());
        syrmaOrdersDto.setActionTime(syrmaApproval.getActionTime());
        syrmaOrdersDto.setSyrmaComments(syrmaApproval.getSyrmaComments());

        syrmaOrdersDto.setOrder(orderMapper.toDto(syrmaApproval.getOrder()));
        syrmaOrdersDto.setActionDoneBy(userMapper.toDto(syrmaApproval.getActionDoneBy())); // nested mapping

        return syrmaOrdersDto;
    }
}
