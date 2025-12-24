package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders>
{

    Orders findByOrderIdAndUsers(Long orderId, Users user);


    //Orders find using UserId
    @Query(value = "SELECT * FROM orders WHERE user_id = :userId ORDER BY order_id DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Orders> findOrdersByUserWithLimitOffset(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

    //Search filter Query
    //Date filter using userId
    @Query("SELECT o FROM Orders o WHERE o.createAt BETWEEN :startDate AND :endDate AND o.users.userId = :userId")
    Page<Orders> findByOrderDateBetweenAndUser(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Long userId,
            Pageable pageable
    );

    // Order Count using userId
    @Query(value = "SELECT COUNT(*) FROM orders WHERE user_id = :userId", nativeQuery = true)
    Long countByUserId(@Param("userId") long userId);


    //status filter using userId
    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.users.userId = :userId")
    Page<Orders> findByStatusAndUser(@Param("status") String status, @Param("userId") Long userId, Pageable pageable);


    // Order filter using status
    @Query(value = "SELECT * FROM orders WHERE status IN (:statuses) ORDER BY order_id DESC  LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Orders> findByStatusWithLimitOffset(@Param("statuses") List<OrderStatus> statuses, @Param("offset") int offset, @Param("limit") int limit);

    // Order Count using status
    @Query(value = "SELECT COUNT(*) FROM orders WHERE status IN (:statuses)", nativeQuery = true)
    long countByStatus(@Param("statuses") List<OrderStatus> statuses);


    //status filter using status
    @Query("SELECT o FROM Orders o WHERE o.status = :status ")
    Page<Orders> findByStatus(@Param("status") String status, Pageable pageable);


    //Status searching query for finance pending button
    @Query("SELECT o FROM Orders o WHERE o.status IN (:statuses) AND o.createAt BETWEEN :start AND :end")
    Page<Orders> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
            List<OrderStatus> statuses,
            Pageable pageable
    );

    //All orders data fetch query
    @Query(value = "SELECT * FROM orders ORDER BY order_id DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Orders> findAllOrders(@Param("offset") int offset, @Param("limit") int limit);

    //All orders data fetch query using date
    @Query("SELECT o FROM Orders o WHERE o.createAt BETWEEN :startDate AND :endDate ORDER BY o.createAt DESC")
    Page<Orders> findByOrderDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("""
       SELECT o FROM Orders o
       WHERE 
           LOWER(o.project) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(o.productType) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(o.orderType) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(o.initiator) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(o.reasonForBuildRequest) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
       """)
    Page<Orders> findBySearchOrdersForAdmin(@Param("keyword") String keyword, Pageable pageable);
}
