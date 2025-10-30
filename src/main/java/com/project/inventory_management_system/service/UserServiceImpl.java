package com.project.inventory_management_system.service;



import com.project.inventory_management_system.entity.*;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.RolesRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


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
//        if (user.getDepartment() !=null)
//        {
//            Department department;
//            if (user.getDepartment().getId() != null)
//            {
//                department = departmentRepository.findById(user.getDepartment().getId())
//                        .orElseThrow(() -> new RuntimeExc
//            {
//                department = departmentRepoeption("Department not found"));
////            }
////            elsesitory.save(user.getDepartment());
//            }
//            user.setDepartment(department);
//        }
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

        //        for (DepartmentRole departmentRole : user.getDepartmentRole())
//        {
//            Department department = departmentRepository.findByDepartmentName(departmentRepository.)
//        }


        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }

}
