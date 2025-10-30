package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long>
{
    Roles findByRoleName(String roleName);
}
