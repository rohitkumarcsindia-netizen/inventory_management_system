package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.entity.Roles;
import com.project.inventory_management_system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RoleController
{
    private final RoleService roleService;

    @PostMapping("/roles")
    public ResponseEntity<?> addNewRole(@RequestBody Roles role)
    {
        Roles createRole =  roleService.save(role);
        if (createRole != null)
        {
            return ResponseEntity.ok("Role Save Successfully "+createRole);
        }
        else
            return ResponseEntity.badRequest().body("Role Not Save");

    }


    @PutMapping("/roles/{roleId}")
    public  ResponseEntity<?> updateRoleDetails(@RequestBody Roles roles)
    {
        Roles updateRole =  roleService.updateRole(roles);
        if (updateRole != null)
        {
            return ResponseEntity.ok("Role Update Successfully "+updateRole);
        }
        else
            return ResponseEntity.badRequest().body("Role Not update");
    }

    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<?> deleteRoleDetails(@RequestBody Roles roles)
    {
        Roles deleteRole = roleService.deleteRole(roles);
        if (deleteRole != null)
        {
            return ResponseEntity.ok("Role delete Successfully "+deleteRole);
        }
        else
            return ResponseEntity.badRequest().body("Role Not Delete");
    }

    @GetMapping("/roles")
    public List<Roles> getRoles()
    {
        List<Roles> roles =  roleService.findAllRole();
        if (roles != null)
        {
            return roles;
        }
        return null;
    }

    @GetMapping("/roles/{rolesId}")
    public ResponseEntity<?> getrole(@RequestBody Roles roles)
    {
        Roles findRole = roleService.findRole(roles);
        if (findRole != null)
        {
            return ResponseEntity.ok("Role Find Successfully "+findRole);
        }
        else
            return ResponseEntity.badRequest().body("Role Not Found");
    }
}
