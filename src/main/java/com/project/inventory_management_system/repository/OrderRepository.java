package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long>
{
    List<Orders> findByUsers(Users users);

    @Query(
            value = "SELECT * FROM orders WHERE user_id = :userId LIMIT :limit OFFSET :offset",
            nativeQuery = true
    )
    List<Orders> findOrdersByUserWithLimitOffset(
            @Param("userId") Long userId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

}
