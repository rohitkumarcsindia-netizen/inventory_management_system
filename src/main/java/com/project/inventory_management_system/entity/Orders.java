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

    @Column(name = "order_date")
    private java.time.LocalDate orderDate;

    @Column(name = "project", nullable = false, length = 100)
    private String project;

    @Column(name = "product_type", nullable = false, length = 100)
    private String productType;

    @Column(name = "order_type", nullable = false, length = 100)
    private String orderType;

    @Column(name = "proposed_build_plan_qty")
    private Integer proposedBuildPlanQty;

    @Column(name = "reason_for_build_request", columnDefinition = "TEXT")
    private String reasonForBuildRequest;

    @Column(name = "initiator", length = 100)
    private String initiator;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "akts_comments", columnDefinition = "TEXT")
    private String aktsComments;

    @Column(name = "pms_remarks", columnDefinition = "TEXT")
    private String pmsRemarks;

    @OneToMany(mappedBy = "order")
    private List<FinanceApproval> financeApprovalList;

    @OneToMany(mappedBy = "order")
    private List<CloudApproval> cloudApprovalList;

    @OneToMany(mappedBy = "order")
    private List<ScmApproval> scmApprovalList;

}
