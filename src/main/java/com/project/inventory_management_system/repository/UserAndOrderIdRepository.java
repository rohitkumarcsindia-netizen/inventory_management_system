package com.project.inventory_management_system.repository;


import com.project.inventory_management_system.entity.UserAndOrderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAndOrderIdRepository extends JpaRepository<UserAndOrderId, Long>
{

}
