package com.project.inventory_management_system.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class CloudApproval
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    @Column(columnDefinition = "TEXT")
    private String jiraDescription;


    private String priority;

    @Column(columnDefinition = "TEXT")
    private String cloudComments;

    private String cloudAction;
    private LocalDateTime actionTime;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private Users updatedBy;
}
