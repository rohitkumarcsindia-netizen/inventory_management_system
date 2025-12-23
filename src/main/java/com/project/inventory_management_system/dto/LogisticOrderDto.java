package com.project.inventory_management_system.dto;

import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogisticOrderDto
{
    private Long id;

    private OrdersDto order;

    private String shippingMode;
    private String courierName;
    private String trackingNumber;
    private java.time.LocalDate dispatchDate;
    private java.time.LocalDate expectedDeliveryDate;
    private java.time.LocalDate actualDeliveryDate;
    private String deliveredStatus;
    private String serialNumbers;
    private String logisticsComment;
//    private String logisticsPdiComment;
//    private String pdiAction;
    private String shipmentDocumentUrl;

    private UserDto actionBy;

    private LocalDateTime actionTime;
}
