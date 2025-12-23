package com.project.inventory_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ProjectTeamApproval
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    @Column(name = "pdi_type")
    private String amispPdiType;

    @Column(name = "action_time")
    private LocalDateTime projectTeamActionTime;

    @Column(name = "comments",columnDefinition = "TEXT")
    private String projectTeamComment;

    @Column(name = "email",columnDefinition = "TEXT")
    private String amispEmailId;

    @ManyToOne
    @JoinColumn(name = "action_by")
    private Users actionBy;

    @Column(name = "pdi_location")
    private String pdiLocation;

    @Column(name = "serial_number")
    private String serialNumbers;

    @Column(name = "dispatch_details")
    private String dispatchDetails;

    @Column(name = "document_url")
    private String documentUrl;

    @Column(name = "location")
    private String locationDetails;

    @Column(columnDefinition = "TEXT")
    private String PdiComment;

    @Column(name = "pdi_action",columnDefinition = "TEXT")
    private String pdiAction;
}
