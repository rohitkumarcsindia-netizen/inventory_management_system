package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.FinanceApproval;
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

    @Query(value = """
        SELECT * FROM orders
        WHERE status IN (:statuses)
        ORDER BY 
            CASE 
                WHEN status = 'FINANCE PENDING' THEN 1
                WHEN status = 'CLOUD > SCM RECHECK PENDING' THEN 2
                ELSE 5
            END,
            order_id DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Orders> findByFinanceStatusWithLimitOffset(@Param("statuses") List<String> statuses,
                                                 @Param("offset") int offset,
                                                 @Param("limit") int limit);

    @Query(value = """
        SELECT * FROM orders
        WHERE status IN (:statuses)
        ORDER BY 
            CASE 
                WHEN status = 'AMISP PENDING' THEN 1
                WHEN status = 'SCM > AMISP RECHECK PENDING' THEN 2
                ELSE 5
            END,
            order_id DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Orders> findByStatusListWithLimitOffset(@Param("statuses") List<String> statuses,
                                                 @Param("offset") int offset,
                                                 @Param("limit") int limit);

    @Query(value = """
        SELECT * FROM orders
        WHERE status IN (:statuses)
        ORDER BY 
            CASE 
                WHEN status = 'SCM PENDING' THEN 1
                WHEN status = 'CLOUD > SCM RECHECK PENDING' THEN 2
                WHEN status = 'SYRMA > SCM PRODUCTION STARTED' THEN 3
                WHEN status = 'RMA > SCM RECHECK PENDING' THEN 4
                WHEN status = 'PROJECT TEAM > SCM RECHECK PENDING' THEN 5
                WHEN status = 'PROJECT TEAM > SCM LOCATION SENT' THEN 6
                WHEN status = 'FINANCE > SCM RECHECK PENDING' THEN 7
                ELSE 5
            END,
            order_id DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Orders> findOrdersForScm(@Param("statuses") List<String> statuses,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);



    // Order Count using userId
    @Query(value = "SELECT COUNT(*) FROM orders WHERE user_id = :userId", nativeQuery = true)
    Long countByUserId(@Param("userId") long userId);

    // Order Count using status
    @Query(value = "SELECT COUNT(*) FROM orders WHERE status = :status", nativeQuery = true)
    Long countByStatus(@Param("status") String status);

    @Query(value = "SELECT COUNT(*) FROM orders WHERE status IN (:statuses)", nativeQuery = true)
    long countOrdersForScm(@Param("statuses") List<String> statuses);



    //Search filter Query
    //Date filter
    @Query("""
    SELECT o FROM Orders o
    WHERE o.createAt BETWEEN :start AND :end
      AND o.users.userId = :userId
""")
    Page<Orders> findByOrderDateBetweenAndUser(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Long userId,
            Pageable pageable
    );

    //status filter
    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.users.userId = :userId")
    Page<Orders> findByStatusAndUser(@Param("status") String status, @Param("userId") Long userId, Pageable pageable);



    //Universal search query for project team
    @Query("""
    SELECT o FROM Orders o
    WHERE o.users.userId = :userId
      AND (
            LOWER(o.project) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(o.productType) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(o.orderType) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(o.initiator) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(o.reasonForBuildRequest) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
      )
""")
    Page<Orders> findBySearchOrders(@Param("keyword") String keyword, @Param("userId") Long userId,Pageable pageable);


    //Universal searching query for finance pending button
    @Query("""
    SELECT o FROM Orders o
    WHERE
        o.status = 'FINANCE PENDING' AND
        (
             LOWER(o.project) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.productType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.orderType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.initiator) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.reasonForBuildRequest) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
        )
""")
    Page<Orders> searchFinance(@Param("keyword") String keyword, Pageable pageable);


    //Status searching query for finance pending button
    @Query("""
    SELECT o FROM Orders o
    WHERE o.status = 'FINANCE PENDING'
      AND o.createAt BETWEEN :start AND :end
""")
    Page<Orders> findByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );
}
