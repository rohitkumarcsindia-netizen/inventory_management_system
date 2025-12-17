package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
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
    List<Orders> findOrdersByUserWithLimitOffset(
            @Param("userId") Long userId,
            @Param("offset") int offset,
            @Param("limit") int limit);


    @Query(value = "SELECT * FROM orders WHERE status IN (:statuses) ORDER BY order_id DESC  LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Orders> findByStatusWithLimitOffset(@Param("statuses") List<String> statuses,
                                                 @Param("offset") int offset,
                                                 @Param("limit") int limit);


    // Order Count using userId
    @Query(value = "SELECT COUNT(*) FROM orders WHERE user_id = :userId", nativeQuery = true)
    Long countByUserId(@Param("userId") long userId);

    // Order Count using status
    @Query(value = "SELECT COUNT(*) FROM orders WHERE status IN (:statuses)", nativeQuery = true)
    long countByStatus(@Param("statuses") List<String> statuses);


    //Search filter Query
    //Date filter
    @Query("SELECT o FROM Orders o WHERE o.createAt BETWEEN :startDate AND :endDate AND o.users.userId = :userId")
    Page<Orders> findByOrderDateBetweenAndUser(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Long userId,
            Pageable pageable
    );

    //status filter
    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.users.userId = :userId")
    Page<Orders> findByStatusAndUser(@Param("status") String status, @Param("userId") Long userId, Pageable pageable);


    //status filter finance pending
    @Query("SELECT o FROM Orders o WHERE o.status = :status ")
    Page<Orders> findByStatusForFinance(@Param("status") String status, Pageable pageable);


    //Status searching query for finance pending button
    @Query("""
    SELECT o FROM Orders o
    WHERE o.status IN ('PROJECT TEAM > FINANCE PRE APPROVAL PENDING', 'SCM > FINANCE POST APPROVAL PENDING','LOGISTIC > FINANCE CLOSURE PENDING')
      AND o.createAt BETWEEN :start AND :end
""")
    Page<Orders> findByDateRangeForFinance(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );


    //Status searching query for cloud pending button
    @Query("""
    SELECT o FROM Orders o WHERE o.status = 'SCM CREATED TICKET > CLOUD PENDING' AND
    o.createAt BETWEEN :start AND :end
""")
    Page<Orders> findByDateRangeForCloud(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);


    //Status searching query for syrma pending button
    @Query("""
    SELECT o FROM Orders o
    WHERE o.status IN ('SCM JIRA TICKET CLOSURE > SYRMA PENDING', 'RMA QC FAIL > SYRMA RE-PROD/TEST PENDING')
      AND o.createAt BETWEEN :start AND :end
""")
    Page<Orders> findByDateRangeForSyrma(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);

    //status filter syrma pending
    @Query("SELECT o FROM Orders o WHERE o.status = :status ")
    Page<Orders> findByStatusForSyrma(@Param("status") String status, Pageable pageable);

    //Status searching query for rma pending button
    @Query("""
    SELECT o FROM Orders o WHERE o.status = 'SCM NOTIFY > RMA QC PENDING' AND
    o.createAt BETWEEN :start AND :end
""")
    Page<Orders> findByDateRangeForRma(@Param("start")LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);


    //Status searching query for logistic pending button
    @Query("""
    SELECT o FROM Orders o
    WHERE o.status IN ('SCM > LOGISTIC PENDING', 'DELIVERY PENDING', 'PDI PENDING')
      AND o.createAt BETWEEN :start AND :end
""")
    Page<Orders> findByDateRangeForLogistic(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);


    //status filter logistic pending
    @Query("SELECT o FROM Orders o WHERE o.status = :status ")
    Page<Orders> findByStatusForLogistic(@Param("status") String status, Pageable pageable);


    //Status searching query for scm pending button
    @Query("""
    SELECT o FROM Orders o
    WHERE o.status IN ('PROJECT TEAM > SCM PENDING','FINANCE APPROVED > SCM PENDING',
    'CLOUD CREATED CERTIFICATE > SCM PROD-BACK CREATION PENDING',
    'SYRMA PROD/TEST DONE > SCM ACTION PENDING','RMA QC PASS > SCM ORDER RELEASE PENDING',
    'SYRMA RE-PROD/TEST DONE > SCM ACTION PENDING','PROJECT TEAM > SCM READY FOR DISPATCH',
    'PROJECT TEAM NOTIFY > SCM LOCATION DETAILS',
    'FINANCE > SCM PLAN TO DISPATCH','FINANCE CLOSURE DONE > SCM CLOSURE PENDING')
      AND o.createAt BETWEEN :start AND :end
""")
    Page<Orders> findByDateRangeForScm(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);


    //status filter scm pending
    @Query("SELECT o FROM Orders o WHERE o.status = :status ")
    Page<Orders> findByStatusForScm(@Param("status") String status, Pageable pageable);


    // Order Count using status for syrma
    @Query(value = "SELECT COUNT(*) FROM orders WHERE status IN (:syrmaStatuses)", nativeQuery = true)
    Long countBySyrmaStatusList(@Param("syrmaStatuses") List<String> syrmaStatuses);


    //Admin all data fetch query
    @Query(value = """
        SELECT * FROM orders
        ORDER BY order_id DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Orders> findAllOrders(@Param("offset") int offset,
                               @Param("limit") int limit);


    @Query("""
       SELECT o FROM Orders o
       WHERE o.createAt BETWEEN :startDate AND :endDate
       ORDER BY o.createAt DESC
       """)
    Page<Orders> findByOrderDate(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate,
                                 Pageable pageable);


    @Query("""
       SELECT o FROM Orders o
       WHERE o.status = :status
       """)
    Page<Orders> findByStatus(@Param("status") String status, Pageable pageable);

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
