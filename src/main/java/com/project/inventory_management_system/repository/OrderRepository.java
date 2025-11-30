package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long>
{
    List<Orders> findByUsers(Users users);


    //Orders find using UserId
    @Query(
            value = "SELECT * FROM orders WHERE user_id = :userId ORDER BY order_id DESC LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<Orders> findOrdersByUserWithLimitOffset(
            @Param("userId") Long userId,
            @Param("offset") int offset,
            @Param("limit") int limit);


    // Orders find Using status
    @Query(
            value = "SELECT * FROM orders WHERE status = :status ORDER BY order_id DESC LIMIT :limit OFFSET :offset",
            nativeQuery = true
    )
    List<Orders> findByStatusWithLimitOffset(
            @Param("status")String status,
            @Param("offset") int offset,
            @Param("limit") int limit);



    // Order Count using userId
    @Query(value = "SELECT COUNT(*) FROM orders WHERE user_id = :userId", nativeQuery = true)
    Long countByUserId(@Param("userId") long userId);

    // Order Count using status
    @Query(value = "SELECT COUNT(*) FROM orders WHERE status = :status", nativeQuery = true)
    Long countByStatus(@Param("status") String status);



    //Search filter Query
    //Date filter
    @Query("SELECT o FROM Orders o WHERE o.createAt BETWEEN :start AND :end AND o.users.userId = :userId")
    List<Orders> findByOrderDateBetweenAndUser(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("userId") Long userId
    );

    //status filter
    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.users.userId = :userId")
    List<Orders> findByStatusAndUser(@Param("status") String status, @Param("userId") Long userId);


    //project filter
    @Query("SELECT o FROM Orders o WHERE o.project = :project AND o.users.userId = :userId")
    List<Orders> findByProjectAndUser(String project, long userId);
}
