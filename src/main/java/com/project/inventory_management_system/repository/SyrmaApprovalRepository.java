package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.FinanceApproval;
import com.project.inventory_management_system.entity.SyrmaApproval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SyrmaApprovalRepository extends JpaRepository<SyrmaApproval, Long>
{
    @Query(
            value = "SELECT sa.* FROM syrma_approval sa " +
                    "JOIN orders o ON o.order_id = sa.order_id " +
                    "WHERE sa.syrma_action IS NOT NULL " +
                    "ORDER BY sa.action_time DESC " +
                    "LIMIT :offset, :limit",
            nativeQuery = true
    )
    List<SyrmaApproval> findSyrmaApprovals(@Param("limit") int limit, @Param("offset") int offset);

    //All order Action count only
    @Query(
            value = "SELECT COUNT(*) FROM syrma_approval " +
                    "WHERE syrma_approval.syrma_action IS NOT NULL",
            nativeQuery = true
    )
    Long countByAction();


    @Query("""
    SELECT sa FROM SyrmaApproval sa
    WHERE sa.syrmaAction IS NOT NULL
      AND sa.actionTime BETWEEN :start AND :end
    ORDER BY sa.actionTime DESC
""")
    Page<SyrmaApproval> findByDateRangeForSyrma(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);

    //status filter
    @Query("SELECT o FROM SyrmaApproval o WHERE o.syrmaAction = :status")
    Page<SyrmaApproval> findByStatusFilterForSyrma(@Param("status")String status, Pageable pageable);


    @Query("""
       SELECT s FROM SyrmaApproval s
       JOIN s.order o
       JOIN s.actionDoneBy u
       WHERE s.syrmaAction IS NOT NULL
       AND (
            CAST(u.userId AS string) LIKE CONCAT('%', :keyword, '%')
         OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
         OR LOWER(COALESCE(o.project, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(COALESCE(o.productType, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(COALESCE(o.initiator, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
       )
""")
    Page<SyrmaApproval> searchSyrmaComplete(@Param("keyword")String keyword, Pageable pageable);
}
