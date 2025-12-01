package com.project.inventory_management_system.mapper;

import com.project.inventory_management_system.dto.FinanceOrderDto;
import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.FinanceApproval;
import com.project.inventory_management_system.entity.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinanceOrderMapper
{

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    //Entity → DTO
    public FinanceOrderDto toDto(FinanceApproval financeApproval)
    {
        if (financeApproval == null) return null;


        FinanceOrderDto dto = new FinanceOrderDto();
        dto.setId(financeApproval.getId());
        dto.setFinanceAction(financeApproval.getFinanceAction());
        dto.setFinanceActionTime(financeApproval.getFinanceActionTime());
        dto.setFinanceReason(financeApproval.getFinanceReason());

        dto.setOrder(orderMapper.toDto(financeApproval.getOrder()));
        dto.setFinanceApprovedBy(userMapper.toDto(financeApproval.getFinanceApprovedBy())); // nested mapping

        return dto;
    }
}
