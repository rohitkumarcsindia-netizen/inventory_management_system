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

    //All order Action count only
    @Query(
            value = "SELECT COUNT(*) FROM project_team_approval " +
                    "WHERE project_team_approval.project_team_action_time IS NOT NULL",
            nativeQuery = true
    )
    Long countByAmispAction();


    //All Complete Order data fetch
    @Query(
            value = "SELECT * FROM project_team_approval " +
                    "WHERE project_team_action_time IS NOT NULL " +
                    "ORDER BY project_team_action_time DESC " +
                    "LIMIT :limit OFFSET :offset",
            nativeQuery = true
    )
    List<ProjectTeamApproval> findByAmispActionIsNotNull(@Param("limit") int limit, @Param("offset") int offset);


    @Query("""
    SELECT aa FROM ProjectTeamApproval aa
    WHERE aa.projectTeamActionTime IS NOT NULL
      AND aa.projectTeamActionTime BETWEEN :start AND :end
    ORDER BY aa.projectTeamActionTime DESC
""")
    Page<ProjectTeamApproval> findByDateRange(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);


    //status filter
    @Query("SELECT o FROM ProjectTeamApproval o WHERE o.amispPdiType = :status")
    Page<ProjectTeamApproval> findByStatusFilter(@Param("status") String status, Pageable pageable);


    @Query("""
       SELECT a FROM ProjectTeamApproval a
       JOIN a.order o
       JOIN a.actionBy u
       WHERE a.projectTeamActionTime IS NOT NULL
       AND (
            CAST(u.userId AS string) LIKE CONCAT('%', :keyword, '%')
         OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
         OR LOWER(COALESCE(o.project, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(COALESCE(o.productType, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(COALESCE(o.initiator, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
       )
""")
    Page<ProjectTeamApproval> searchAmispComplete(@Param("keyword") String keyword, Pageable pageable);

}
