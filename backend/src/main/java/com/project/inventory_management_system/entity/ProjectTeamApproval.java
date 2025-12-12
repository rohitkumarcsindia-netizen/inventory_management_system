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

    private String amispPdiType;
    private LocalDateTime projectTeamActionTime;

    @Column(columnDefinition = "TEXT")
    private String projectTeamComment;

    @Column(columnDefinition = "TEXT")
    private String amispEmailId;

    @ManyToOne
    @JoinColumn(name = "action_by")
    private Users actionBy;

    private String pdiLocation;
    private String serialNumbers;
    private String dispatchDetails;
    private String documentUrl;

    private String locationDetails;
}
