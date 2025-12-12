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

    private String rmaAction;
    private LocalDateTime rmaActionTime;

    @Column(columnDefinition = "TEXT")
    private String rmaComment;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Users approvedBy;
}
