package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.AmispApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmispApprovalRepository extends JpaRepository<AmispApproval, Long>
{
    AmispApproval findByOrder_OrderId(Long orderId);
}
