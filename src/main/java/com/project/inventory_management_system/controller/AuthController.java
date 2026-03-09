package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.dto.LoginRequestDto;
import com.project.inventory_management_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth/login")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4000")
public class AuthController
{

    private final AuthService loginService;

    @PostMapping()
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) throws Exception
    {
       return loginService.loginUser(loginRequestDto);
    }
}

