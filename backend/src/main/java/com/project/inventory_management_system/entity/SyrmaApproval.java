package com.project.inventory_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class SyrmaApproval
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    private String syrmaAction;
    private LocalDateTime actionTime;

    @Column(columnDefinition = "TEXT")
    private String syrmaComments;

    @ManyToOne
    @JoinColumn(name = "action_done_by")
    private Users actionDoneBy;
}
