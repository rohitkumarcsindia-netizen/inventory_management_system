package com.project.inventory_management_system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogisticOrdersHistoryDto
{

    private Long orderId;
    private LocalDateTime createAt;
    private java.time.LocalDate expectedOrderDate;
    private String project;
    private String initiator;
    private String productType;
    private String orderType;
    private Integer proposedBuildPlanQty;
    private String aktsComments;
    private String reasonForBuildRequest;
    private String pmsRemarks;


    private String shippingMode;
    private String courierName;
    private String trackingNumber;
    private java.time.LocalDate dispatchDate;
    private java.time.LocalDate expectedDeliveryDate;
    private java.time.LocalDate actualDeliveryDate;
    private String deliveredStatus;
    private String serialNumbers;
    private String logisticsComment;
    private String logisticsPdiComment;
    private String pdiAction;
    private String shipmentDocumentUrl;
    private LocalDateTime actionTime;
    private Long actionBy;

    private UserDto users;
}
