package com.project.inventory_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Setter
public class Orders
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "expected_order_date")
    private java.time.LocalDate expectedOrderDate;

    @Column(name = "project", nullable = false, length = 100)
    private String project;

    @Column(name = "product_type", nullable = false, length = 100)
    private String productType;

    @Column(name = "order_type", nullable = false, length = 100)
    private String orderType;

    @Column(name = "quantity")
    private Integer proposedBuildPlanQty;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reasonForBuildRequest;

    @Column(name = "initiator", length = 100)
    private String initiator;

    @Column(name = "status", length = 225)
    private String status;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String pmsRemarks;

    @OneToMany(mappedBy = "order")
    private List<FinanceApproval> financeApprovalList;

    @OneToMany(mappedBy = "order")
    private List<CloudApproval> cloudApprovalList;

    @OneToMany(mappedBy = "order")
    private List<ScmApproval> scmApprovalList;

    @OneToMany(mappedBy = "order")
    private List<RmaApproval> rmaApprovalList;

    @OneToMany(mappedBy = "order")
    private List<ProjectTeamApproval> projectTeamApprovalList;

}
