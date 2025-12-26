package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.dto.UserDto;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/admin/users")
@RequiredArgsConstructor
public class UserController
{

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(HttpServletRequest request, @RequestBody UserDto userDto)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return userService.createUser(userDetails.getUsername(), userDto);
    }

    @PutMapping("/details-update/{userId}")
    public ResponseEntity<?> updateUserDetails(HttpServletRequest request, @PathVariable Long  userId, @RequestBody UserDto userDto)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return userService.updateUserDetails(userDetails.getUsername(), userId, userDto);
    }

    @DeleteMapping("/delete/{userId}")
    private ResponseEntity<?> deleteUserDetails(HttpServletRequest request,  @PathVariable Long  userId)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return userService.deleteUserDetails(userDetails.getUsername(), userId);
    }

    @GetMapping
    public ResponseEntity<?> getUsers(HttpServletRequest request, @RequestParam(defaultValue = "10") int limit,
                                      @RequestParam(defaultValue = "0") int offset)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return userService.getUsers(userDetails.getUsername(), limit, offset);
    }

}
