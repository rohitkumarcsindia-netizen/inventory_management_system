package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long>
{

}
