package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.entity.Roles;
import com.project.inventory_management_system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController
{
    private final RoleService roleService;

    @PostMapping("/data")
    public Roles role(@RequestBody Roles role)
    {
        return roleService.save(role);
    }
}
