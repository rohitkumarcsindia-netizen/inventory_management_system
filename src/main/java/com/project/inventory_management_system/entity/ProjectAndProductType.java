package com.project.inventory_management_system.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ProjectAndProductType
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "projectType", nullable = false, length = 50)
    private String projectType;

    @Column(name = "productType", nullable = false, length = 50)
    private String productType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;
}
