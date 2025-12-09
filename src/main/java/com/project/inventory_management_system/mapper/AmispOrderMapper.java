package com.project.inventory_management_system.mapper;


import com.project.inventory_management_system.dto.AmispOrderDto;
import com.project.inventory_management_system.entity.AmispApproval;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AmispOrderMapper
{
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;


    public AmispOrderDto amispOrdersDto(AmispApproval amispApproval)
    {
        if (amispApproval == null) return null;

        AmispOrderDto amispOrdersDto = new AmispOrderDto();
        amispOrdersDto.setId(amispApproval.getId());
        amispOrdersDto.setAmispAction(amispApproval.getAmispAction());
        amispOrdersDto.setAmispActionTime(amispApproval.getAmispActionTime());
        amispOrdersDto.setAmispComment(amispApproval.getAmispComment());
        amispApproval.setPdiLocation(amispApproval.getPdiLocation());
        amispOrdersDto.setDispatchDetails(amispApproval.getDispatchDetails());
        amispOrdersDto.setDocumentUrl(amispApproval.getDocumentUrl());
        amispOrdersDto.setSerialNumbers(amispApproval.getSerialNumbers());

        amispOrdersDto.setOrder(orderMapper.toDto(amispApproval.getOrder()));
        amispOrdersDto.setAmispApprovedBy(userMapper.toDto(amispApproval.getApprovedBy())); // nested mapping

        return amispOrdersDto;
    }
}
