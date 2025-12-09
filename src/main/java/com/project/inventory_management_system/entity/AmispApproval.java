package com.project.inventory_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class AmispApproval
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    private String amispAction;
    private LocalDateTime amispActionTime;

    @Column(columnDefinition = "TEXT")
    private String amispComment;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Users approvedBy;

    private String pdiLocation;
    private String serialNumbers;
    private String dispatchDetails;
    private String documentUrl;

    private String locationDetails;
}
