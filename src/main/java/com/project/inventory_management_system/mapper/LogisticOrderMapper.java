package com.project.inventory_management_system.mapper;

import com.project.inventory_management_system.dto.LogisticOrderDto;
import com.project.inventory_management_system.entity.LogisticsDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogisticOrderMapper
{
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    public LogisticOrderDto logisticOrdersDto(LogisticsDetails logisticsDetails) {
        if (logisticsDetails == null) return null;

        LogisticOrderDto logisticOrderDto = new LogisticOrderDto();
        logisticOrderDto.setId(logisticsDetails.getId());
        logisticOrderDto.setLogisticsComment(logisticsDetails.getLogisticsComment());
        logisticOrderDto.setActionTime(logisticsDetails.getActionTime());
        logisticOrderDto.setCourierName(logisticsDetails.getCourierName());
        logisticOrderDto.setDeliveredStatus(logisticsDetails.getDeliveredStatus());
        logisticOrderDto.setDispatchDate(logisticsDetails.getDispatchDate());
        logisticOrderDto.setSerialNumbers(logisticsDetails.getSerialNumbers());
        logisticOrderDto.setActualDeliveryDate(logisticsDetails.getActualDeliveryDate());
        logisticOrderDto.setShippingMode(logisticsDetails.getShippingMode());
        logisticOrderDto.setExpectedDeliveryDate(logisticsDetails.getExpectedDeliveryDate());
        logisticOrderDto.setShipmentDocumentUrl(logisticsDetails.getShipmentDocumentUrl());
        logisticOrderDto.setTrackingNumber(logisticsDetails.getTrackingNumber());

        logisticOrderDto.setOrder(orderMapper.toDto(logisticsDetails.getOrder()));
        logisticOrderDto.setActionBy(userMapper.toDto(logisticsDetails.getActionBy())); // nested mapping

        return logisticOrderDto;
    }
}
