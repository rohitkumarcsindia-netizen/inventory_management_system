package com.project.inventory_management_system.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ScmApproval
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    private String jiraTicketNumber;
    private String jiraSummary;
    private String jiraStatus;


    @Column(name = "action")
    private String scmAction;

    @Column(name = "action_time")
    private LocalDateTime actionTime;


    @Column(name = "comments",columnDefinition = "TEXT")
    private String scmComments;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Users approvedBy;
}
