package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.ScmApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScmApprovalRepository extends JpaRepository<ScmApproval, Long>
{
    //find order orderId in scm entity
    @Query(
            value = "SELECT * FROM scm_approval WHERE order_id = :orderId ORDER BY action_time DESC LIMIT 1",
            nativeQuery = true
    )
    ScmApproval findLatestByOrderId(@Param("orderId") Long orderId);


    // Order Count using Scm Action
    @Query(value = "SELECT COUNT(*) FROM  scm_approval WHERE scm_action IS NOT NULL", nativeQuery = true)
    Long countByScmAction();


    // Order get using Scm Action
    @Query(
            value = "SELECT * FROM scm_approval " +
                    "WHERE scm_action IS NOT NULL " +
                    "ORDER BY action_time DESC " +
                    "LIMIT :limit OFFSET :offset",
            nativeQuery = true
    )
    List<ScmApproval> findByScmActionIsNotNull(@Param("limit")int limit, @Param("offset")int offset);
}
