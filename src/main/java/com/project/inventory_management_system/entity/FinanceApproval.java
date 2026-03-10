package com.project.inventory_management_system.entity;

import com.project.inventory_management_system.enums.ActionStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "finance_action")
public class FinanceApproval
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    private ActionStatus financeAction;

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
