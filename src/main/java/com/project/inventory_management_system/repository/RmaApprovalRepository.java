package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.RmaApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RmaApprovalRepository extends JpaRepository<RmaApproval, Long>
{

}
