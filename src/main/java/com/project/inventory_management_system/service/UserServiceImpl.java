package com.project.inventory_management_system.service;



import com.project.inventory_management_system.dto.UserDto;
import com.project.inventory_management_system.entity.*;
import com.project.inventory_management_system.mapper.UserMapper;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService
{

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final UserMapper userMapper;


    @Override
    public ResponseEntity<?> createUser(String username, UserDto userDto)
    {
        Users user = usersRepository.findByUsername(username);
        if (user == null)
        {
            return ResponseEntity.status(404).body("Logged-in user not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only Admin add Users");
        }

        Department department = departmentRepository.findByDepartmentName(userDto.getDepartmentName());
        if (department == null)
        {
            return ResponseEntity.status(404).body("Department not Found");
        }

        Users existingUserByUsername = usersRepository.findByUsername(userDto.getUsername());
        if (existingUserByUsername != null)
        {
            return ResponseEntity.status(409).body("Username already exists");
        }

        Users existingUserByEmail = usersRepository.findByEmail(userDto.getEmail());
        if (existingUserByEmail != null)
        {
            return ResponseEntity.status(409).body("Email already exists");
        }

        Users saveUser = userMapper.toEntity(userDto);
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank())
        {
            saveUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        saveUser.setDepartment(department);
        usersRepository.save(saveUser);
        return ResponseEntity.ok("User Details Save Successfully");
    }

    @Override
    public ResponseEntity<?> getUsers(String username, int limit, int offset)
    {
        Users user = usersRepository.findByUsername(username);
        if (user == null)
        {
            return ResponseEntity.status(404).body("Logged-in user not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only ADMIN can view users");
        }

        List<Users> users = usersRepository.findAllWithLimitOffset(limit, offset);
        if (users.isEmpty())
        {
            return ResponseEntity.ok("No Users found");
        }
        List<UserDto> userDtoList = users.stream()
                .map(userMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "usersCount", usersRepository.count(),
                "users", userDtoList
        ));
    }

    @Override
    public ResponseEntity<?> updateUserDetails(String username, Long userId, UserDto userDto)
    {
        Users user = usersRepository.findByUsername(username);
        if (user == null)
        {
            return ResponseEntity.status(404).body("Logged-in user not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only ADMIN can update users");
        }
        Users existsUser = usersRepository.findByUserId(userId);
        if (existsUser == null)
        {
            return ResponseEntity.ok("UserDetails not found");
        }
        Department department = departmentRepository.findByDepartmentName(userDto.getDepartmentName());
        if (department == null)
        {
            return ResponseEntity.status(404).body("Department not Found");
        }
        existsUser.setUsername(userDto.getUsername());
        existsUser.setEmail(userDto.getEmail());
        existsUser.setDepartment(department);
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank())
        {
            existsUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        usersRepository.save(existsUser);

        return ResponseEntity.ok("User Details Updated Successfully");
    }

    @Override
    public ResponseEntity<?> deleteUserDetails(String username, Long userId)
    {
        Users user = usersRepository.findByUsername(username);
        if (user == null)
        {
            return ResponseEntity.status(404).body("Logged-in user not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only ADMIN can delete users");
        }
        Users existsUser = usersRepository.findByUserId(userId);
        if (existsUser == null)
        {
            return ResponseEntity.ok("UserDetails not found");
        }

        usersRepository.delete(existsUser);
        return ResponseEntity.ok("User Details Delete Successfully");
    }

}
