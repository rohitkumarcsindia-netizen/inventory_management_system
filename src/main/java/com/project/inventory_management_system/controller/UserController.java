package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController
{

    private final UserService userService;

    @PostMapping("/data")
    public ResponseEntity<?> user(@RequestBody Users user)
    {
         Users users = userService.save(user);
         return ResponseEntity.ok(users);
    }

}
