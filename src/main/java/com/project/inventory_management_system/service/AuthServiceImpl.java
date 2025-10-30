package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.LoginRequestDto;
import com.project.inventory_management_system.dto.LoginResponseDto;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService
{

    private final UsersRepository usersRepository;
    private final AuthenticationManager authenticationManager;
    private final LoginResponseDto loginResponseDto;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<?> loginUser(LoginRequestDto loginRequestDto)
    {
        Authentication authentications = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(), loginRequestDto.getPassword()));
        if (authentications.isAuthenticated())
        {
//             LoginResponseDto responseDto = new LoginResponseDto(loginResponseDto.getId(), loginResponseDto.getEmail());
//             return ResponseEntity.ok(responseDto);
//            return ResponseEntity.ok("Success");
            String genToken = jwtService.generateToken(loginRequestDto.getUsername());
            return ResponseEntity.ok(genToken);
        }

//        else {
//            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    //.body("Invalid username or password");
//            return "usernot found";
//        }

        else {
            return ResponseEntity.ofNullable("Login Failed");
            }

    }

}
