package com.project.inventory_management_system.service;

import com.project.inventory_management_system.entity.Department;
import com.project.inventory_management_system.repository.DepartmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService
{
    private final DepartmentRepository departmentRepository;
//    private final UsersRepository usersRepository;
//    private final Department departments;


    @Override
    public Department   addDepartment(Department department) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        Users findUser = usersRepository.findByUsername(username);
//        try
//        {
//            return departmentRepository.save(department);
//        }
//        catch (RuntimeException e)
//        {
//            throw new RuntimeException(e);
//        }

        System.out.println("Received department: " + department.getDepartmentname());
        Department saved = departmentRepository.save(department);
        System.out.println("Saved department ID: " + saved.getId());
        return saved;
    }

//    @Override
//    public Department updateDepartment(Department department)
//    {
//        Optional<Department> findDepartment = departmentRepository.findById(department.getId());
//        if (findDepartment.isPresent())
//        {
//            Department updateDepartment = findDepartment.get();
//            updateDepartment.setDepartmentname(department.getDepartmentname());
//            return  departmentRepository.save(updateDepartment);
//        }
//        else
//             return null;
//    }
}
