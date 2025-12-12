package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.LoginRequestDto;
import org.springframework.http.ResponseEntity;

public interface AuthService
{

    public ResponseEntity<?> loginUser(LoginRequestDto loginRequestDto);
}
