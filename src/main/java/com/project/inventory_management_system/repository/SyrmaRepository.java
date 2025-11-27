package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.SyrmaApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyrmaRepository extends JpaRepository<SyrmaApproval, Long>
{
}
