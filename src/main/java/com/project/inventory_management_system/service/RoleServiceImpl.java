package com.project.inventory_management_system.service;

import com.project.inventory_management_system.entity.Roles;

import com.project.inventory_management_system.repository.RolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService
{
    private final RolesRepository rolesRepository;


    @Override
    public Roles save(Roles role)
    {
       Roles saveRole = rolesRepository.save(role);
       if (saveRole != null)
       {
           return saveRole;
       }
       return null;

    }

    @Override
    public Roles updateRole(Roles roles)
    {
        Optional<Roles> findrole = rolesRepository.findById(roles.getRoleId());

        if (findrole.isPresent())
        {
            Roles existingRoles = findrole.get();
            existingRoles.setRoleName(roles.getRoleName());
            return rolesRepository.save(existingRoles);
        }
        else
            return null;
    }

    @Override
    public Roles deleteRole(Roles roles)
    {
        Optional<Roles> findRoles = rolesRepository.findById(roles.getRoleId());
        if (findRoles.isPresent())
        {
            Roles existingRoles = findRoles.get();
            rolesRepository.deleteById( existingRoles.getRoleId());
            return existingRoles;
        }
        return null;
    }

    @Override
    public List<Roles> findAllRole()
    {
       return rolesRepository.findAll();
    }

    @Override
    public Roles findRole(Roles roles)
    {
        Optional<Roles> existingRoles = rolesRepository.findById(roles.getRoleId());
        if (existingRoles.isPresent())
        {
            Roles roles1 = existingRoles.get();
            return roles1;
        }
        else
            return null;
    }
}
