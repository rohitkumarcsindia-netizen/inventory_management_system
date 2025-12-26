package com.project.inventory_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class RmaApproval
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    @Column(name = "action")
    private String rmaAction;

    @Column(name = "action_time")
    private LocalDateTime rmaActionTime;

    @Column(name = "comments",columnDefinition = "TEXT")
    private String rmaComment;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Users approvedBy;
}
