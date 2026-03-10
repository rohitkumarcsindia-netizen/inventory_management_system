package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.ProjectTeamApproval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectTeamApprovalRepository extends JpaRepository<ProjectTeamApproval, Long>
{
    ProjectTeamApproval findByOrder_OrderId(Long orderId);

    @Query("""
    SELECT aa FROM ProjectTeamApproval aa
    WHERE aa.actionBy IS NOT NULL
      AND aa.projectTeamActionTime BETWEEN :start AND :end
    ORDER BY aa.projectTeamActionTime DESC
""")
    Page<ProjectTeamApproval> findByDateRange(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);

}
