package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.DepartmentRole;
import com.project.inventory_management_system.entity.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRoleRepository extends JpaRepository<DepartmentRole, Long>
{

}
