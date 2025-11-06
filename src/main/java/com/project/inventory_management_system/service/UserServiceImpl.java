package com.project.inventory_management_system.service;



import com.project.inventory_management_system.entity.*;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.RolesRepository;
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
    private final RolesRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;

    @Override
    public Users save(Users user)
    {

        for (UserRoles userRoles : user.getUserRoles())
        {
            Roles roles = roleRepository.findByRoleName(userRoles.getRole().getRoleName());

            userRoles.setUser(user);
            userRoles.setRole(roles);

        }



        for (DepartmentRole departmentRole : user.getDepartmentRole())
        {
            Department department = departmentRepository.findByDepartmentname(departmentRole.getDepartment().getDepartmentname());
            Roles role = roleRepository.findByRoleName(departmentRole.getRole().getRoleName());

            departmentRole.setUser(user);
            departmentRole.setRole(role);
            departmentRole.setDepartment(department);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }

    @Override
    public Users updateUserData(Users user)
    {
        Optional<Users> findUser = Optional.ofNullable(usersRepository.findByUserId(user.getUserId()));
        if (findUser.isPresent())
        {
            Users existinguser = findUser.get();
            existinguser.setUserRoles(user.getUserRoles());
            existinguser.setEmail(user.getEmail());
            existinguser.setDepartmentRole(user.getDepartmentRole());
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
    public Users findUsers(Users user)
    {
        Optional<Users> existingRoles = usersRepository.findById(user.getUserId());
        if (existingRoles.isPresent())
        {
            Users userDetails = existingRoles.get();
            return userDetails;
        }
        return null;
    }

}
