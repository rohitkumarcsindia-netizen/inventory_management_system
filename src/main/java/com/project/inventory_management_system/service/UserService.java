package com.project.inventory_management_system.service;

import com.project.inventory_management_system.entity.Users;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface UserService
{
    public Users save(Users user);

    Users updateUserData(Users user);

    Users deleteUser(Users user);

    List<Users> findAllUsers();

    Users findUsers(Users user);
}
