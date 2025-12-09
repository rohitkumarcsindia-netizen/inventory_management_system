package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.AmispApproval;
import com.project.inventory_management_system.entity.LogisticsDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogisticsDetailsRepository extends JpaRepository<LogisticsDetails, Long>
{

    LogisticsDetails findByOrder_OrderId(Long orderId);

    //All order Action count only
    @Query(
            value = "SELECT COUNT(*) FROM logistic_details " +
                    "WHERE logistic_details.action_time IS NOT NULL",
            nativeQuery = true
    )
    Long countByLogisticAction();


    //All Complete Order data fetch
    @Query(
            value = "SELECT * FROM logistic_details " +
                    "WHERE action_time IS NOT NULL " +
                    "ORDER BY action_time DESC " +
                    "LIMIT :limit OFFSET :offset",
            nativeQuery = true
    )
    List<LogisticsDetails> findByLogisticActionIsNotNull(@Param("limit") int limit, @Param("offset") int offset);


    @Query("""
    SELECT ld FROM LogisticsDetails ld
    WHERE ld.actionTime IS NOT NULL
      AND ld.actionTime BETWEEN :start AND :end
    ORDER BY ld.actionTime DESC
""")
    Page<LogisticsDetails> findByDateRange(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);


    //status filter
    @Query("SELECT o FROM LogisticsDetails o WHERE o.pdiAction = :status")
    Page<LogisticsDetails> findByStatusFilter(@Param("status") String status, Pageable pageable);


    @Query("""
       SELECT ld FROM LogisticsDetails ld
       JOIN ld.order o
       JOIN ld.actionBy u
       WHERE ld.actionTime IS NOT NULL
       AND (
            CAST(u.userId AS string) LIKE CONCAT('%', :keyword, '%')
         OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
         OR LOWER(COALESCE(o.project, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(COALESCE(o.productType, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(COALESCE(o.initiator, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
       )
""")
    Page<LogisticsDetails> searchLogisticComplete(@Param("keyword") String keyword, Pageable pageable);

}
