package com.project.inventory_management_system.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService
{
    String extractUserName(String token);

    boolean validateToken(String token, UserDetails userDetails);

    String generateToken(String username);
}

