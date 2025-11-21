package com.project.inventory_management_system.mapper;


import com.project.inventory_management_system.dto.OrdersCompleteDto;
import com.project.inventory_management_system.entity.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdersCompleteMapper
{
    private final UserMapper userMapper;

    //Entity → DTO
    public OrdersCompleteDto toDto(Orders order)
    {
        if (order == null) return null;

        OrdersCompleteDto OrdersCompleteDto = new OrdersCompleteDto();

        // Finance Orders Action History
        if (order.getFinanceAction() != null)
        {
            OrdersCompleteDto.setOrderId(order.getOrderId());
            OrdersCompleteDto.setOrderDate(order.getOrderDate());
            OrdersCompleteDto.setProject(order.getProject());
            OrdersCompleteDto.setOrderType(order.getOrderType());
            OrdersCompleteDto.setInitiator(order.getInitiator());
            OrdersCompleteDto.setProductType(order.getProductType());
            OrdersCompleteDto.setProposedBuildPlanQty(order.getProposedBuildPlanQty());
            OrdersCompleteDto.setAktsComments(order.getAktsComments());
            OrdersCompleteDto.setReasonForBuildRequest(order.getReasonForBuildRequest());
            OrdersCompleteDto.setPmsRemarks(order.getPmsRemarks());
            OrdersCompleteDto.setFinanceAction(order.getFinanceAction());
            OrdersCompleteDto.setFinanceActionTime(order.getFinanceActionTime());

            OrdersCompleteDto.setUsers(userMapper.toDto(order.getUsers())); // nested mapping

        }
        return OrdersCompleteDto;
    }
}
