package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.LoginRequestDto;
import com.project.inventory_management_system.dto.LoginResponseDto;
import com.project.inventory_management_system.entity.Roles;
import com.project.inventory_management_system.repository.RolesRepository;
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
    private final LoginResponseDto loginResponseDto;
    private final JwtUtil jwtUtil;
    private RolesRepository rolesRepository;


    @Override
    public ResponseEntity<?> loginUser(LoginRequestDto loginRequestDto)
    {
        try
        {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getUsername(),
                            loginRequestDto.getPassword()
                    )
            );
            loginRequestDto.get
            Roles role = rolesRepository.findByRoleName(roleName);


            if (authentication.isAuthenticated())
            {
                String token = jwtUtil.generateToken(Map.of(), loginRequestDto.getUsername());

                LoginResponseDto response = new LoginResponseDto();
//                response.setUsername(loginRequestDto.getUsername());
//                response.setToken(token);

                return ResponseEntity.ok(response);
            }
            else
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid username or password");
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

