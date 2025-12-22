package com.project.inventory_management_system.repository;


import com.project.inventory_management_system.entity.ScmApproval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScmApprovalRepository extends JpaRepository<ScmApproval, Long>
{
    //find order orderId in scm entity
    @Query(
            value = "SELECT * FROM scm_approval WHERE order_id = :orderId ORDER BY action_time DESC LIMIT 1",
            nativeQuery = true
    )
    ScmApproval findLatestByOrderId(@Param("orderId") Long orderId);


    // Order Count using Scm Action
    @Query(value = "SELECT COUNT(*) FROM  scm_approval WHERE action IS NOT NULL", nativeQuery = true)
    Long countByScmAction();


    // Order get using Scm Action
    @Query(
            value = "SELECT * FROM scm_approval " +
                    "WHERE action IS NOT NULL " +
                    "ORDER BY action_time DESC " +
                    "LIMIT :limit OFFSET :offset",
            nativeQuery = true
    )
    List<ScmApproval> findByScmActionIsNotNull(@Param("limit")int limit, @Param("offset")int offset);


    @Query("""
    SELECT sa FROM ScmApproval sa
    WHERE sa.scmAction IS NOT NULL
      AND sa.actionTime BETWEEN :start AND :end
    ORDER BY sa.actionTime DESC
""")
    Page<ScmApproval> findByDateRange(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);


    //status filter
    @Query("SELECT o FROM ScmApproval o WHERE o.scmAction = :status")
    Page<ScmApproval> findByStatusFilter(@Param("status") String status, Pageable pageable);

    @Query("""
       SELECT sa FROM ScmApproval sa
       JOIN sa.order o
       JOIN sa.approvedBy u
       WHERE sa.scmAction IS NOT NULL
       AND (
            CAST(u.userId AS string) LIKE CONCAT('%', :keyword, '%')
         OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
         OR LOWER(COALESCE(o.project, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(COALESCE(o.productType, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(COALESCE(o.initiator, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
       )
""")
    Page<ScmApproval> searchScmComplete(@Param("keyword") String keyword, Pageable pageable);

}
