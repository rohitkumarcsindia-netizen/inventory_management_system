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

    @Column(name = "serial_number",columnDefinition = "TEXT")
    private String serialNumbers;

    @Column(name = "comments",columnDefinition = "TEXT")
    private String logisticsComment;


    @Column(name = "document_url")
    private String shipmentDocumentUrl;

    @ManyToOne
    @JoinColumn(name = "action_by")
    private Users actionBy;

    @Column(name = "action_time")
    private LocalDateTime actionTime;
}
