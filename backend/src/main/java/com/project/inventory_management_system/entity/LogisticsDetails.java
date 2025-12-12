package com.project.inventory_management_system.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class LogisticsDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    private String shippingMode;
    private String courierName;
    private String trackingNumber;
    private java.time.LocalDate dispatchDate;
    private java.time.LocalDate expectedDeliveryDate;
    private java.time.LocalDate actualDeliveryDate;
    private String deliveredStatus;

    @Column(columnDefinition = "TEXT")
    private String serialNumbers;

    @Column(columnDefinition = "TEXT")
    private String logisticsComment;

    @Column(columnDefinition = "TEXT")
    private String logisticsPdiComment;

    @Column(columnDefinition = "TEXT")
    private String pdiAction;

    private String shipmentDocumentUrl;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private Users actionBy;

    private LocalDateTime actionTime;
}
