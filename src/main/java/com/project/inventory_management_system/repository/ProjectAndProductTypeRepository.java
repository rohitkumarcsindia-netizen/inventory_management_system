package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.ProjectAndProductType;
import com.project.inventory_management_system.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectAndProductTypeRepository extends JpaRepository<ProjectAndProductType, Long>
{
    ProjectAndProductType findByIdAndUsers(Long id, Users user);
}
