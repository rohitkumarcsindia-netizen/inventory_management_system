package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.CloudApproval;
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

    //All order Action count only
    @Query(
            value = "SELECT COUNT(*) FROM cloud_approval " +
                    "WHERE cloud_approval.action IS NOT NULL",
            nativeQuery = true
    )
    Long countByCloudAction();


    //All Complete Order data fetch
    @Query(
            value = "SELECT * FROM cloud_approval " +
                    "WHERE action IS NOT NULL " +
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
    Page<CloudApproval> findByDateRange(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);


    @Query("""
       SELECT c FROM CloudApproval c
       JOIN c.order o
       JOIN c.updatedBy u
       WHERE c.cloudAction IS NOT NULL
       AND (
            CAST(u.userId AS string) LIKE CONCAT('%', :keyword, '%')
         OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
         OR LOWER(COALESCE(o.project, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(COALESCE(o.productType, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(COALESCE(o.initiator, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
       )
""")
    Page<CloudApproval> searchCloudComplete(@Param("keyword") String keyword, Pageable pageable);

    //status filter
    @Query("SELECT c FROM CloudApproval c WHERE c.cloudAction = :status")
    Page<CloudApproval> findByStatusFilterForCloud(@Param("status")String status, Pageable pageable);
}
