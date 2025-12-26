package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.UserDto;
import com.project.inventory_management_system.entity.Users;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface UserService
{
    ResponseEntity<?> createUser(String username, UserDto userDto);

    ResponseEntity<?> getUsers(String username, int offset, int limit);

    ResponseEntity<?> updateUserDetails(String username, Long userId, UserDto userDto);

    ResponseEntity<?> deleteUserDetails(String username, Long userId);
}
