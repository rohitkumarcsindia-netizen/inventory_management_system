package com.project.inventory_management_system.repository;

import com.project.inventory_management_system.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long>
{
    Users findByUsername(String username);

    Users findByUserId(Long userId);

    Users findByEmail(String email);

    @Query(value = "SELECT * FROM users ORDER BY user_id DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Users> findAllWithLimitOffset(@Param("limit") int limit, @Param("offset") int offset);

}
