package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.ProjectType;
import com.project.inventory_management_system.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectTypeRepository extends JpaRepository<ProjectType, Long>
{
    ProjectType findByIdAndUsers(Long id, Users user);
}
