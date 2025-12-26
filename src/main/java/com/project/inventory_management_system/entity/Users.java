package com.project.inventory_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table (name = "Users")
public class Users
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    @Column(name = "email",nullable = false,unique = true)
    private String email;

    @Column(name = "user_name",nullable = false,unique = true)
    private  String username;

    @Column(name = "password",nullable = false)
    private  String password;


    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

}
