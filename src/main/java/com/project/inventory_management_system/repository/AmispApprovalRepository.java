package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.AmispApproval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AmispApprovalRepository extends JpaRepository<AmispApproval, Long>
{
    AmispApproval findByOrder_OrderId(Long orderId);

    //All order Action count only
    @Query(
            value = "SELECT COUNT(*) FROM amisp_approval " +
                    "WHERE amisp_approval.amisp_action IS NOT NULL",
            nativeQuery = true
    )
    Long countByAmispAction();


    //All Complete Order data fetch
    @Query(
            value = "SELECT * FROM amisp_approval " +
                    "WHERE amisp_action IS NOT NULL " +
                    "ORDER BY amisp_action_time DESC " +
                    "LIMIT :limit OFFSET :offset",
            nativeQuery = true
    )
    List<AmispApproval> findByAmispActionIsNotNull(@Param("limit") int limit, @Param("offset") int offset);


    @Query("""
    SELECT aa FROM AmispApproval aa
    WHERE aa.amispAction IS NOT NULL
      AND aa.amispActionTime BETWEEN :start AND :end
    ORDER BY aa.amispActionTime DESC
""")
    Page<AmispApproval> findByDateRange(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);


    //status filter
    @Query("SELECT o FROM AmispApproval o WHERE o.amispAction = :status")
    Page<AmispApproval> findByStatusFilter(@Param("status") String status, Pageable pageable);


    @Query("""
       SELECT a FROM AmispApproval a
       JOIN a.order o
       JOIN a.approvedBy u
       WHERE a.amispAction IS NOT NULL
       AND (
            CAST(u.userId AS string) LIKE CONCAT('%', :keyword, '%')
         OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
         OR LOWER(COALESCE(o.project, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(COALESCE(o.productType, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(COALESCE(o.initiator, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
       )
""")
    Page<AmispApproval> searchAmispComplete(@Param("keyword") String keyword, Pageable pageable);

}
