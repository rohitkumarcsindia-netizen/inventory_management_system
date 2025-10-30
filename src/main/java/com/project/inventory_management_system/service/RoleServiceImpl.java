package com.project.inventory_management_system.service;

import com.project.inventory_management_system.entity.Roles;

import com.project.inventory_management_system.repository.RolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService
{
    private final RolesRepository rollRepository;


    @Override
    public Roles save(Roles role)
    {
       return rollRepository.save(role);
        //return "data inserted";f
        //System.out.println("data inserted");
        //return role;
    }
}
