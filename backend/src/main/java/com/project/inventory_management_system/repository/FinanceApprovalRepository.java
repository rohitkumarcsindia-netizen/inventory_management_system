package com.project.inventory_management_system.repository;

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
public interface FinanceApprovalRepository extends JpaRepository<FinanceApproval, Long>
{

    @Query(
            value = "SELECT fa.* FROM finance_approval fa " +
                    "JOIN orders o ON o.order_id = fa.order_id " +
                    "WHERE fa.finance_action IS NOT NULL " +
                    "ORDER BY fa.finance_action_time DESC " +
                    "LIMIT :limit OFFSET :offset",
            nativeQuery = true
    )
    List<FinanceApproval> findFinanceApprovals(@Param("limit") int limit, @Param("offset") int offset);


    //All order Action count only
    @Query(
            value = "SELECT COUNT(*) FROM finance_approval " +
                    "WHERE finance_approval.finance_action IS NOT NULL",
            nativeQuery = true
    )
    Long countByAction();


    //status filter
    @Query("SELECT o FROM FinanceApproval o WHERE o.financeAction = :status")
    Page<FinanceApproval> findByStatusFilter(@Param("status") String status, Pageable pageable);



    @Query("""
    SELECT fa FROM FinanceApproval fa
    WHERE fa.financeAction IS NOT NULL
      AND fa.financeActionTime BETWEEN :start AND :end
    ORDER BY fa.financeActionTime DESC
""")
    Page<FinanceApproval> findByDateRange(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);

    @Query("""
       SELECT f FROM FinanceApproval f
       JOIN f.order o
       JOIN f.financeApprovedBy u
       WHERE f.financeAction IS NOT NULL
       AND (
            CAST(u.userId AS string) LIKE CONCAT('%', :keyword, '%')
         OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
         OR LOWER(COALESCE(o.project, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(COALESCE(o.productType, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(COALESCE(o.initiator, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
       )
""")
    Page<FinanceApproval> searchFinanceComplete(@Param("keyword") String keyword, Pageable pageable);

    FinanceApproval findByOrder_OrderId(Long orderId);
}
