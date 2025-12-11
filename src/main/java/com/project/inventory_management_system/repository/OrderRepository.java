package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long>
{
    List<Orders> findByUsers(Users users);

    Orders findByOrderIdAndUsers(Long orderId, Users user);


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
                WHEN status = 'PROJECT TEAM > FINANCE PRE APPROVAL PENDING' THEN 2
                WHEN status = 'SCM > FINANCE APPROVAL SENT' THEN 3
                WHEN status = 'LOGISTIC > FINANCE CLOSURE PENDING' THEN 1
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
                WHEN status = 'SCM > LOGISTIC PENDING' THEN 2
                WHEN status = 'DELIVERY PENDING' THEN 3
                WHEN status = 'PDI PENDING' THEN 1
                ELSE 5
            END,
            order_id DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Orders> findByLogisticStatusWithLimitOffset(@Param("statuses") List<String> statuses,
                                                    @Param("offset") int offset,
                                                    @Param("limit") int limit);

    @Query(value = """
        SELECT * FROM orders
        WHERE status IN (:statuses)
        ORDER BY 
            CASE 
                WHEN status = 'PDI PENDING' THEN 2
                WHEN status = 'SCM > AMISP RECHECK PENDING' THEN 1
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
                WHEN status = 'PROJECT TEAM > SCM PENDING' THEN 7
                WHEN status = 'FINANCE APPROVED > SCM PENDING' THEN 6
                WHEN status = 'SYRMA > SCM PENDING' THEN 5
                WHEN status = 'RMA QC PASS > SCM PENDING' THEN 4
                WHEN status = 'DISPATCH ORDER IS READY' THEN 3
                WHEN status = 'PROJECT TEAM > SCM LOCATION SENT' THEN 2
                WHEN status = 'FINANCE > SCM RECHECK PENDING' THEN 1
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

    // Order Count using status
    @Query(value = "SELECT COUNT(*) FROM orders WHERE status IN (:financeStatuses)", nativeQuery = true)
    Long countByStatusList(@Param("financeStatuses") List<String> financeStatuses);

    // Order Count using status
    @Query(value = "SELECT COUNT(*) FROM orders WHERE status IN (:amispStatuses)", nativeQuery = true)
    Long countByAmispStatusList(@Param("amispStatuses") List<String> amispStatuses);



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
        o.status IN ('FINANCE PRE APPROVAL PENDING', 'LOGISTIC > FINANCE CLOSURE PENDING') AND
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


    //status filter finance pending
    @Query("SELECT o FROM Orders o WHERE o.status = :status ")
    Page<Orders> findByStatusForFinance(@Param("status") String status, Pageable pageable);


    //Status searching query for finance pending button
    @Query("""
    SELECT o FROM Orders o
    WHERE o.status IN ('FINANCE PRE APPROVAL PENDING', 'LOGISTIC TO FINANCE CLOSURE PENDING')
      AND o.createAt BETWEEN :start AND :end
""")
    Page<Orders> findByDateRangeForFinance(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    //Universal searching query for cloud pending button
    @Query("""
    SELECT o FROM Orders o
    WHERE
        o.status = 'CLOUD PENDING' AND
        (
             LOWER(o.project) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.productType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.orderType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.initiator) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.reasonForBuildRequest) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
        )
""")
    Page<Orders> searchCloud(@Param("keyword") String keyword, Pageable pageable);


    //Status searching query for cloud pending button
    @Query("""
    SELECT o FROM Orders o WHERE o.status = 'CLOUD PENDING' AND
    o.createAt BETWEEN :start AND :end
""")
    Page<Orders> findByDateRangeForCloud(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);

    //Universal searching query for syrma pending button
    @Query("""
    SELECT o FROM Orders o
    WHERE
        o.status IN ('SYRMA PENDING', 'RMA QC FAIL > SYRMA PENDING') AND
        (
             LOWER(o.project) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.productType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.orderType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.initiator) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.reasonForBuildRequest) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
        )
""")
    Page<Orders> searchSyrma(@Param("keyword")String keyword, Pageable pageable);

    //Status searching query for syrma pending button
    @Query("""
    SELECT o FROM Orders o
    WHERE o.status IN ('SYRMA PENDING', 'RMA QC FAIL > SYRMA PENDING')
      AND o.createAt BETWEEN :start AND :end
""")
    Page<Orders> findByDateRangeForSyrma(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);

    //status filter syrma pending
    @Query("SELECT o FROM Orders o WHERE o.status = :status ")
    Page<Orders> findByStatusForSyrma(@Param("status") String status, Pageable pageable);

    //Status searching query for rma pending button
    @Query("""
    SELECT o FROM Orders o WHERE o.status = 'RMA PENDING' AND
    o.createAt BETWEEN :start AND :end
""")
    Page<Orders> findByDateRangeForRma(@Param("start")LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);


    //Universal searching query for rma pending button
    @Query("""
    SELECT o FROM Orders o
    WHERE
        o.status = 'RMA QC PENDING' AND
        (
             LOWER(o.project) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.productType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.orderType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.initiator) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.reasonForBuildRequest) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
        )
""")
    Page<Orders> searchRma(@Param("keyword") String keyword, Pageable pageable);


    //Status searching query for amisp pending button
    @Query("""
    SELECT o FROM Orders o
    WHERE o.status IN ('AMISP PENDING', 'SCM > AMISP RECHECK PENDING')
      AND o.createAt BETWEEN :start AND :end
""")
    Page<Orders> findByDateRangeForAmisp(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);


    //status filter amisp pending
    @Query("SELECT o FROM Orders o WHERE o.status = :status ")
    Page<Orders> findByStatusForAmisp(@Param("status") String status, Pageable pageable);

    //Universal searching query for amisp pending button
    @Query("""
    SELECT o FROM Orders o
    WHERE
        o.status IN ('AMISP PENDING', 'SCM > AMISP RECHECK PENDING') AND
        (
             LOWER(o.project) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.productType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.orderType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.initiator) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.reasonForBuildRequest) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
        )
""")
    Page<Orders> searchAmisp(@Param("keyword") String keyword, Pageable pageable);

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


    //Universal searching query for logistic pending button
    @Query("""
    SELECT o FROM Orders o
    WHERE
        o.status IN ('SCM > LOGISTIC PENDING', 'DELIVERY PENDING', 'PDI PENDING') AND
        (
             LOWER(o.project) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.productType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.orderType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.initiator) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.reasonForBuildRequest) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
        )
""")
    Page<Orders> searchLogistic(@Param("keyword") String keyword, Pageable pageable);


    //Status searching query for scm pending button
    @Query("""
    SELECT o FROM Orders o
    WHERE o.status IN ('SCM PENDING', 'CLOUD > SCM RECHECK PENDING','SYRMA > SCM RECHECK PENDING',
    'RMA > SCM RECHECK PENDING','PROJECT TEAM > SCM RECHECK PENDING','PROJECT TEAM > SCM LOCATION SENT',
    'FINANCE > SCM RECHECK PENDING')
      AND o.createAt BETWEEN :start AND :end
""")
    Page<Orders> findByDateRangeForScm(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end, Pageable pageable);


    //status filter scm pending
    @Query("SELECT o FROM Orders o WHERE o.status = :status ")
    Page<Orders> findByStatusForScm(@Param("status") String status, Pageable pageable);


    //Universal searching query for amisp pending button
    @Query("""
    SELECT o FROM Orders o
    WHERE
        o.status IN ('SCM PENDING', 'CLOUD > SCM RECHECK PENDING','SYRMA > SCM RECHECK PENDING',
    'RMA > SCM RECHECK PENDING','PROJECT TEAM > SCM RECHECK PENDING','PROJECT TEAM > SCM LOCATION SENT',
    'FINANCE > SCM RECHECK PENDING') AND
        (
             LOWER(o.project) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.productType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.orderType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.initiator) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(o.reasonForBuildRequest) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%')
        )
""")
    Page<Orders> searchScm(@Param("keyword") String keyword, Pageable pageable);

    //syrma pending data fetch
    @Query(value = """
        SELECT * FROM orders
        WHERE status IN (:statuses)
        ORDER BY 
            CASE 
                WHEN status = 'SYRMA PENDING' THEN 1
                WHEN status = 'RMA QC FAIL > SYRMA PENDING' THEN 2
                ELSE 5
            END,
            order_id DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Orders> findBySyrmaStatusWithLimitOffset(@Param("statuses") List<String> statuses,
                                                    @Param("offset") int offset,
                                                    @Param("limit") int limit);


    // Order Count using status for syrma
    @Query(value = "SELECT COUNT(*) FROM orders WHERE status IN (:syrmaStatuses)", nativeQuery = true)
    Long countBySyrmaStatusList(@Param("syrmaStatuses") List<String> syrmaStatuses);


}
