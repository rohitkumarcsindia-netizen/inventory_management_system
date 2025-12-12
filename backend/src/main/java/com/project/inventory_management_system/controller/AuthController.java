package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.dto.LoginRequestDto;
import com.project.inventory_management_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4000")
public class AuthController
{

    private final AuthService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) throws Exception
    {
       return loginService.loginUser(loginRequestDto);
    }
}

