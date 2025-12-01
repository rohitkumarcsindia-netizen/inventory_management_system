package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.FinanceApproval;
import com.project.inventory_management_system.entity.Orders;
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
            value = "SELECT finance_approval.* FROM finance_approval " +
                    "JOIN orders ON orders.order_id = finance_approval.order_id " +
                    "ORDER BY finance_approval.finance_action DESC " +
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


    @Query("""
    SELECT fa FROM FinanceApproval fa
    WHERE fa.financeActionTime BETWEEN :start AND :end
""")
    List<FinanceApproval> findByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    //status filter
    @Query("SELECT o FROM FinanceApproval o WHERE o.financeAction = :status")
    List<FinanceApproval> findByStatusFilter(@Param("status") String status);


}
