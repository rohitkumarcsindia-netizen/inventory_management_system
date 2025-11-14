package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.UserDto;
import com.project.inventory_management_system.entity.Users;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface UserService
{
    public UserDto createUser(UserDto userDto);

    Users updateUserData(Users user);

    Users deleteUser(Users user);

    List<Users> findAllUsers();

    Users findUsers(UserDto userDto);
}
