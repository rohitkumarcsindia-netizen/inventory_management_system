package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.AmispApproval;
import com.project.inventory_management_system.entity.LogisticsDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

}
