package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.AmispApproval;
import com.project.inventory_management_system.entity.CloudApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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


}
