package com.project.inventory_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class FinanceApproval
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    @Column(name = "action")
    private String financeAction;

    @Column(name = "action_time")
    private LocalDateTime financeActionTime;

    @Column(name = "reason",columnDefinition = "TEXT")
    private String financeReason;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Users financeApprovedBy;

    @Column(name = "remark",columnDefinition = "TEXT")
    private String financeFinalRemark;

    //Finance Final Closure Details
    @Column(name = "document_url")
    private String financeApprovalDocumentUrl;

    @Column(name = "closure_status")
    private String financeClosureStatus;

    @Column(name = "closure_time")
    private LocalDateTime financeClosureTime;
}
