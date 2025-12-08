package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.RmaApproval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RmaApprovalRepository extends JpaRepository<RmaApproval, Long>
{
    //All order Action count only
    @Query(
            value = "SELECT COUNT(*) FROM rma_approval " +
                    "WHERE rma_approval.rma_action IS NOT NULL",
            nativeQuery = true
    )
    Long countByRmaAction();


    //All Complete Order data fetch
    @Query(
            value = "SELECT * FROM rma_approval " +
                    "WHERE rma_action IS NOT NULL " +
                    "ORDER BY rma_action_time DESC " +
                    "LIMIT :limit OFFSET :offset",
            nativeQuery = true
    )
    List<RmaApproval> findByRmaActionIsNotNull(@Param("limit") int limit, @Param("offset") int offset);


    @Query("""
    SELECT ra FROM RmaApproval ra
    WHERE ra.rmaAction IS NOT NULL
      AND ra.rmaActionTime BETWEEN :start AND :end
    ORDER BY ra.rmaActionTime DESC
""")
    Page<RmaApproval> findByDateRange(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);

    //status filter
    @Query("SELECT r FROM RmaApproval r WHERE r.rmaAction = :status")
    Page<RmaApproval> findByStatusFilterForRma(@Param("status")String status, Pageable pageable);

    @Query("""
       SELECT r FROM RmaApproval r
       JOIN r.order o
       JOIN r.approvedBy u
       WHERE r.rmaAction IS NOT NULL
       AND (
            CAST(u.userId AS string) LIKE CONCAT('%', :keyword, '%')
         OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
         OR LOWER(COALESCE(o.project, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(COALESCE(o.productType, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(COALESCE(o.initiator, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
       )
""")
    Page<RmaApproval> searchRmaComplete(@Param("keyword") String keyword, Pageable pageable);

}
