package com.project.inventory_management_system.service;



import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.dto.UserDto;
import com.project.inventory_management_system.entity.*;
import com.project.inventory_management_system.mapper.UserMapper;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public UserDto createUser(UserDto userDto)
    {

        Department department = departmentRepository.findById(userDto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));


        // Convert Dto → Entity
        Users users = userMapper.toEntity(userDto);
        users.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Set existing department
        users.setDepartment(department);

        //saved from database
        Users savedUser = usersRepository.save(users);

        // Return Dto
        return userMapper.toDto(savedUser);
    }

    @Override
    public Users updateUserData(Users user)
    {
        Optional<Users> findUser = Optional.ofNullable(usersRepository.findByUserId(user.getUserId()));
        if (findUser.isPresent())
        {
            Users existinguser = findUser.get();
            //existinguser.setUserRoles(user.getUserRoles());
            existinguser.setEmail(user.getEmail());
//            existinguser.setDepartmentRole(user.getDepartmentRole());
            existinguser.setPassword(user.getPassword());
            existinguser.setUsername(user.getUsername());
            return existinguser;
        }
        return null;
    }

    @Override
    public Users deleteUser(Users user)
    {
        Users findUser = usersRepository.findByUserId(user.getUserId());
        if (findUser != null)
        {
            usersRepository.deleteById(findUser.getUserId());
            return findUser;
        }
        return null;
    }

    @Override
    public List<Users> findAllUsers()
    {
        return usersRepository.findAll();
    }

    @Override
    public Users findUsers(UserDto userDto)
    {
        Optional<Users> existingRoles = usersRepository.findById(userDto.getDepartmentId());
        if (existingRoles.isPresent())
        {
            Users userDetails = existingRoles.get();
            return userDetails;
        }
        return null;
    }

}
