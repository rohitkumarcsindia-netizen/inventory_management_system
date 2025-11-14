package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.dto.UserDto;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController
{

    private final UserService userService;

    @PostMapping("/data")
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto)
    {
        try
        {
            UserDto user = userService.createUser(userDto);
            return ResponseEntity.ok(user);
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/dataupdate")
    public ResponseEntity<?> updateUser(@RequestBody Users user)
    {
        Users updateUser = userService.updateUserData(user);
        if (updateUser != null)
        {
            return ResponseEntity.ok("User Details Updated Successfully "+updateUser);
        }
        else
            return ResponseEntity.badRequest().body("User Details not Updated");
    }

    @DeleteMapping("/delete")
    private ResponseEntity<?> deleteUser(@RequestBody Users user)
    {
       Users deleteUser = userService.deleteUser(user);
        if (deleteUser != null)
        {
            return ResponseEntity.ok("User delete Successfully "+deleteUser);
        }
        else
            return ResponseEntity.badRequest().body("User Not Delete");
    }

    @GetMapping("/findall")
    public ResponseEntity<?> findAllUser()
    {
        List<Users> allUsers =  userService.findAllUsers();
        if (allUsers != null)
        {
            return ResponseEntity.ok("User Details "+allUsers);
        }
        else
            return ResponseEntity.badRequest().body("..........");

    }

    @GetMapping("/find")
    public ResponseEntity<?> findUser(@RequestBody UserDto user)
    {
        Users findUser = userService.findUsers(user);
        if (findUser != null)
        {
            return ResponseEntity.ok("User Details "+findUser);
        }
        else
            return ResponseEntity.badRequest().body("User Details Not Found");
    }

}
