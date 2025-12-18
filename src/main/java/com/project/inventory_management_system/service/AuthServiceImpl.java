package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.LoginRequestDto;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.repository.UsersRepository;
import com.project.inventory_management_system.utiliities.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService
{

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepository;


    @Override
    public ResponseEntity<?> loginUser(LoginRequestDto loginRequestDto)
    {
        Users user = usersRepository.findByUsername(loginRequestDto.getUsername());

        if (user == null || !user.getUsername().equals(loginRequestDto.getUsername()))    // <-- case-sensitive check
        {
            return ResponseEntity.status(401).body("Incorrect username or password");
        }
        
        try
        {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getUsername(),
                            loginRequestDto.getPassword()
                    )
            );

            if (authentication.isAuthenticated())
            {
                String token = jwtUtil.generateToken(Map.of(), loginRequestDto.getUsername());
//                return ResponseEntity.ok(token);
                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "department", user.getDepartment().getDepartmentName()
                ));
            }
            else
            {
                return ResponseEntity.status(401).body("Invalid credentials");
            }

        }

        catch (BadCredentialsException e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect username or password");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong: " + e.getMessage());
        }
    }
}

