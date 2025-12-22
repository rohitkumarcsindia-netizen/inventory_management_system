package com.project.inventory_management_system.mapper;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.ScmApproval;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper
{

    private final UserMapper userMapper;

    //Entity → DTO
    public OrdersDto toDto(Orders order)
    {
        if (order == null) return null;

        OrdersDto dto = new OrdersDto();
        dto.setOrderId(order.getOrderId());
        dto.setCreateAt(order.getCreateAt());
        dto.setExpectedOrderDate(order.getExpectedOrderDate());
        dto.setProject(order.getProject());
        dto.setOrderType(order.getOrderType());
        dto.setInitiator(order.getInitiator());
        dto.setProductType(order.getProductType());
        dto.setProposedBuildPlanQty(order.getProposedBuildPlanQty());
        dto.setReasonForBuildRequest(order.getReasonForBuildRequest());
        dto.setStatus(order.getStatus().toDisplay());
        dto.setPmsRemarks(order.getPmsRemarks());

        dto.setUsers(userMapper.toDto(order.getUsers())); // nested mapping

        return dto;
    }

    //DTO → Entity
    public Orders toEntity(OrdersDto dto)
    {
        if (dto == null) return null;

        Orders order = new Orders();
        order.setOrderId(dto.getOrderId());
        order.setCreateAt(dto.getCreateAt());
        order.setExpectedOrderDate(dto.getExpectedOrderDate());
        order.setProject(dto.getProject());
        order.setOrderType(dto.getOrderType());
        order.setInitiator(dto.getUsers().getUsername());
        order.setProductType(dto.getProductType());
        order.setProposedBuildPlanQty(dto.getProposedBuildPlanQty());
        order.setReasonForBuildRequest(dto.getReasonForBuildRequest());
        order.setPmsRemarks(dto.getPmsRemarks());

        // usersDTO → users
        Users user = userMapper.toEntity(dto.getUsers());
        order.setUsers(user);

        return order;
    }
}
