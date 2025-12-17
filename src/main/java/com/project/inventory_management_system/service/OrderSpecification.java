package com.project.inventory_management_system.service;

import com.project.inventory_management_system.entity.Orders;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class OrderSpecification
{
    public static Specification<Orders> hasUser(Long userId)
    {
        return (root, query, cb) ->
                userId == null
                        ? cb.conjunction()
                        : cb.equal(root.get("users").get("userId"), userId);
    }


    public static Specification<Orders> statusIn(List<String> statuses)
    {
        return (root, query, cb) ->
                statuses == null || statuses.isEmpty()
                        ? cb.conjunction()
                        : root.get("status").in(statuses);
    }


    public static Specification<Orders> keywordSearch(String keyword)
    {
        return (root, query, cb) ->
        {

            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String like = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("project")), like),
                    cb.like(cb.lower(root.get("productType")), like),
                    cb.like(cb.lower(root.get("orderType")), like),
                    cb.like(cb.lower(root.get("initiator")), like),
                    cb.like(cb.lower(root.get("reasonForBuildRequest")), like),
                    cb.like(root.get("orderId").as(String.class), like)
            );
        };
    }
}
