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
@Table(name = "cloud_action")
public class CloudApproval
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    @Column(columnDefinition = "TEXT")
    private String ticketDescription;


    private String priority;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String cloudComments;

    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    private ActionStatus cloudAction;

    @Column(name = "action_time")
    private LocalDateTime actionTime;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private Users updatedBy;
}
