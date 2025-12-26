package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.ProductType;
import com.project.inventory_management_system.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<ProductType, Long> 
{
    ProductType findByIdAndUsers(Long id, Users user);
}
