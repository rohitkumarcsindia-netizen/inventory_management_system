package com.project.inventory_management_system.entity;

import com.project.inventory_management_system.enums.ActionStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "rma_action")
public class RmaApproval
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    private ActionStatus rmaAction;

    @Column(name = "action_time")
    private LocalDateTime rmaActionTime;

    @Column(name = "comments",columnDefinition = "TEXT")
    private String rmaComment;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Users approvedBy;
}
