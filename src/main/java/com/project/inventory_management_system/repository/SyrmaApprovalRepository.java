package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.FinanceApproval;
import com.project.inventory_management_system.entity.SyrmaApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

}
