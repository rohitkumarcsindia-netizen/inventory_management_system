package com.project.inventory_management_system.service;


import com.project.inventory_management_system.entity.Orders;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderStatusByDepartmentServiceImpl implements OrderStatusByDepartmentService
{
    @Override
    public List<String> getStatusesByDepartment(String departmentName)
    {

        if (departmentName == null)
        {
            return List.of();
        }

        return switch (departmentName.toUpperCase())
        {

            case ("FINANCE")
                    -> List.of
                    (
                    "PROJECT TEAM > FINANCE PRE APPROVAL PENDING",
                    "SCM > FINANCE POST APPROVAL PENDING",
                    "LOGISTIC > FINANCE CLOSURE PENDING"
                    );

            case ("SCM")
                    -> List.of
                    (
                    "PROJECT TEAM > SCM PENDING",
                    "FINANCE APPROVED > SCM PENDING",
                    "CLOUD CREATED CERTIFICATE > SCM PROD-BACK CREATION PENDING",
                    "SYRMA PROD/TEST DONE > SCM ACTION PENDING",
                    "RMA QC PASS > SCM ORDER RELEASE PENDING",
                    "SYRMA RE-PROD/TEST DONE > SCM ACTION PENDING",
                    "PROJECT TEAM > SCM READY FOR DISPATCH",
                    "PROJECT TEAM NOTIFY > SCM LOCATION DETAILS",
                    "FINANCE > SCM PLAN TO DISPATCH",
                    "FINANCE CLOSURE DONE > SCM CLOSURE PENDING"
                    );

            case ("CLOUD TEAM")
                    -> List.of
                    (
                "SCM CREATED TICKET > CLOUD PENDING"
                    );


            case ("SYRMA")
                    -> List.of
                    (
                    "SCM JIRA TICKET CLOSURE > SYRMA PENDING",
                    "RMA QC FAIL > SYRMA RE-PROD/TEST PENDING"
                    );

            case ("RMA")
                    -> List.of
                    (
                "SCM NOTIFY > RMA QC PENDING"
                    );

            case ("LOGISTIC")
                    -> List.of
                    (
                    "SCM > LOGISTIC PENDING",
                    "DELIVERY PENDING",
                    "PDI PENDING"
                    );

            default -> List.of();
        };
    }

//    // user filter (o.users.userId = :userId)
//    public static Specification<Orders> hasUser(Long userId)
//    {
//        return (root, query, cb) ->
//                userId == null
//                        ? cb.conjunction()
//                        : cb.equal(root.get("users").get("userId"), userId);
//    }
//
//    public static Specification<Orders> keywordSearch(String keyword)
//    {
//        return (root, query, cb) ->
//        {
//            if (keyword == null || keyword.isBlank())
//            {
//                return cb.conjunction();
//            }
//
//            String like = "%" + keyword.toLowerCase() + "%";
//
//            return cb.or(
//                    cb.like(cb.lower(root.get("project")), like),
//                    cb.like(cb.lower(root.get("productType")), like),
//                    cb.like(cb.lower(root.get("orderType")), like),
//                    cb.like(cb.lower(root.get("initiator")), like),
//                    cb.like(cb.lower(root.get("reasonForBuildRequest")), like),
//                    cb.like(root.get("orderId").as(String.class), like)
//            );
//        };
//    }
}
