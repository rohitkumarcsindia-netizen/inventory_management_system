package com.project.inventory_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "department")
public class Department
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "department_name",unique = true)
    private  String departmentname;

    @Column(name = "department_email", unique = true)
    private String departmentEmail;

    @OneToMany(mappedBy = "department")
    private List<Users> users;


}
