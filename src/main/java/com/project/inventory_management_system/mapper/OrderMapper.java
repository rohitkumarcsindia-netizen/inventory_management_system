package com.project.inventory_management_system.mapper;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final UserMapper userMapper;

    //Entity → DTO
    public OrdersDto toDto(Orders orders)
    {
        if (orders == null) return null;

        OrdersDto dto = new OrdersDto();
        dto.setOrderId(orders.getOrderId());
        dto.setProject(orders.getProject());
        dto.setInitiator(orders.getInitiator());
        dto.setProductType(orders.getProductType());
        dto.setProposedBuildPlanQty(orders.getProposedBuildPlanQty());
        dto.setAktsComments(orders.getAktsComments());
        dto.setReasonForBuildRequest(orders.getReasonForBuildRequest());

        dto.setUsers(userMapper.toDto(orders.getUsers())); // nested mapping

        return dto;
    }

    //DTO → Entity
    public Orders toEntity(OrdersDto dto)
    {
        if (dto == null) return null;

        Orders orders = new Orders();
        orders.setOrderId(dto.getOrderId());
        orders.setProject(dto.getProject());
        orders.setInitiator(dto.getInitiator());
        orders.setProductType(dto.getProductType());
        orders.setProposedBuildPlanQty(dto.getProposedBuildPlanQty());
        orders.setAktsComments(dto.getAktsComments());
        orders.setReasonForBuildRequest(dto.getReasonForBuildRequest());

        // usersDTO → users
        Users user = userMapper.toEntity(dto.getUsers());
        orders.setUsers(user);

        return orders;
    }
}
