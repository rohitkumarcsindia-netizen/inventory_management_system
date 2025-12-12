package com.project.inventory_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.auditing.CurrentDateTimeProvider;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class OrderTracking
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt = LocalDateTime.now();


    @PreUpdate
    public void onUpdate()
    {
        this.updatedAt = LocalDateTime.now();
    }
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "orders_id")
    private Orders orders;

    @ManyToOne
    @JoinColumn(name = "previous_department_id")
    private Department perviousDepartmentId;

    @ManyToOne
    @JoinColumn(name = "next_department_id")
    private Department nextDepartmentId;

    @ManyToOne
    @JoinColumn(name = "action_performed_by_id")
    private Users actionPreformedById;

    @Column(name = "remarks")
    private String remarks;

//    @Column(name = "create_at")
//    @DateTimeFormat
//    private CurrentDateTimeProvider createAt;
//
//    @Column(name = "update_at")
//    @DateTimeFormat
//    private CurrentDateTimeProvider updateAT;
}
