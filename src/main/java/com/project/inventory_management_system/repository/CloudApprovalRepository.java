package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.CloudApproval;
import com.project.inventory_management_system.entity.FinanceApproval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CloudApprovalRepository extends JpaRepository<CloudApproval, Long>
{

    // Order Count using status
    @Query(value = "SELECT COUNT(*) FROM orders WHERE status = :status", nativeQuery = true)
    Long countByStatus(@Param("status") String status);


    //All order Action count only
    @Query(
            value = "SELECT COUNT(*) FROM cloud_approval " +
                    "WHERE cloud_approval.cloud_action IS NOT NULL",
            nativeQuery = true
    )
    Long countByCloudAction();


    //All Complete Order data fetch
    @Query(
            value = "SELECT * FROM cloud_approval " +
                    "WHERE cloud_action IS NOT NULL " +
                    "ORDER BY action_time DESC " +
                    "LIMIT :limit OFFSET :offset",
            nativeQuery = true
    )
    List<CloudApproval> findByCloudActionIsNotNull(@Param("limit") int limit, @Param("offset") int offset);


    @Query("""
    SELECT ca FROM CloudApproval ca
    WHERE ca.cloudAction IS NOT NULL
      AND ca.actionTime BETWEEN :start AND :end
    ORDER BY ca.actionTime DESC
""")
    Page<CloudApproval> findByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
