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

    private String financeAction;
    private LocalDateTime financeActionTime;

    @Column(columnDefinition = "TEXT")
    private String financeReason;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Users financeApprovedBy;

    @Column(columnDefinition = "TEXT")
    private String financeFinalRemark;
}
