package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.LogisticsDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogisticsDetailsRepository extends JpaRepository<LogisticsDetails, Long>
{

    LogisticsDetails findByOrder_OrderId(Long orderId);
}
