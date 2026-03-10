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

@Entity
@Getter
@Setter
@Table(name = "syrma_action")
public class SyrmaApproval
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    private ActionStatus syrmaAction;

    @Column(name = "action_time")
    private LocalDateTime actionTime;

    @Column(name = "comments",columnDefinition = "TEXT")
    private String syrmaComments;

    @ManyToOne
    @JoinColumn(name = "action_by")
    private Users actionDoneBy;
}
