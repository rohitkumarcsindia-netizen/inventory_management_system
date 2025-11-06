package com.project.inventory_management_system.service;

import com.project.inventory_management_system.entity.Roles;
import org.springframework.http.ResponseEntity;

import javax.management.relation.Role;
import java.util.List;


public interface RoleService
{
    public Roles save(Roles role);

    Roles updateRole(Roles roles);

    Roles deleteRole(Roles roles);

    List<Roles> findAllRole();

    Roles findRole(Roles roles);
}
