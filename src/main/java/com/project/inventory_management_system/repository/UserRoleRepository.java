package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.Roles;
import com.project.inventory_management_system.entity.UserRoles;
import com.project.inventory_management_system.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoles, Long>
{
    List<UserRoles> findByUser_Username(String userName);

}
