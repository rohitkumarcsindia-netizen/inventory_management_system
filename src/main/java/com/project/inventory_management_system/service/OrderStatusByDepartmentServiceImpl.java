package com.project.inventory_management_system.service;



import com.project.inventory_management_system.enums.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderStatusByDepartmentServiceImpl implements OrderStatusByDepartmentService
{
    @Override
    public List<OrderStatus> getStatusesByDepartment(String departmentName)
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
                    OrderStatus.PROJECT_TEAM_FINANCE_PRE_APPROVAL_PENDING,
                    OrderStatus.SCM_FINANCE_POST_APPROVAL_PENDING,
                    OrderStatus.LOGISTIC_FINANCE_CLOSURE_PENDING,
                    OrderStatus.PROJECT_TEAM_FINANCE_CLOSURE_PENDING
//                    "PROJECT TEAM > FINANCE PRE APPROVAL PENDING",
//                    "SCM > FINANCE POST APPROVAL PENDING",
//                    "LOGISTIC > FINANCE CLOSURE PENDING",
//                    "PROJECT TEAM > FINANCE CLOSURE PENDING"
                    );

            case ("SCM")
                    -> List.of
                    (
                            OrderStatus.PROJECT_TEAM_SCM_PENDING,
                    OrderStatus.FINANCE_APPROVED_SCM_PENDING,
                    OrderStatus.CLOUD_CREATED_CERTIFICATE_SCM_PROD_BACK_CREATION_PENDING,
                    OrderStatus.SYRMA_PROD_TEST_DONE_SCM_ACTION_PENDING,
                    OrderStatus.RMA_QC_PASS_SCM_ORDER_RELEASE_PENDING,
                    OrderStatus.SYRMA_RE_PROD_TEST_DONE_SCM_ACTION_PENDING,
                    OrderStatus.PROJECT_TEAM_SCM_READY_FOR_DISPATCH,
                    OrderStatus.PROJECT_TEAM_NOTIFY_SCM_LOCATION_DETAILS,
                    OrderStatus.FINANCE_SCM_PLAN_TO_DISPATCH,
                    OrderStatus.FINANCE_CLOSURE_DONE_SCM_CLOSURE_PENDING
//                    "PROJECT TEAM > SCM PENDING",
//                    "FINANCE APPROVED > SCM PENDING",
//                    "CLOUD CREATED CERTIFICATE > SCM PROD-BACK CREATION PENDING",
//                    "SYRMA PROD/TEST DONE > SCM ACTION PENDING",
//                    "RMA QC PASS > SCM ORDER RELEASE PENDING",
//                    "SYRMA RE-PROD/TEST DONE > SCM ACTION PENDING",
//                    "PROJECT TEAM > SCM READY FOR DISPATCH",
//                    "PROJECT TEAM NOTIFY > SCM LOCATION DETAILS",
//                    "FINANCE > SCM PLAN TO DISPATCH",
//                    "FINANCE CLOSURE DONE > SCM CLOSURE PENDING"
                    );

            case ("CLOUD TEAM")
                    -> List.of
                    (
                            OrderStatus.SCM_CREATED_TICKET_CLOUD_PENDING
//                "SCM CREATED TICKET > CLOUD PENDING"
                    );


            case ("SYRMA")
                    -> List.of
                    (
                            OrderStatus.SCM_JIRA_TICKET_CLOSURE_SYRMA_PENDING,
                    OrderStatus.RMA_QC_PASS_SCM_ORDER_RELEASE_PENDING
//                    "SCM JIRA TICKET CLOSURE > SYRMA PENDING",
//                    "RMA QC FAIL > SYRMA RE-PROD/TEST PENDING"
                    );

            case ("RMA")
                    -> List.of
                    (
                            OrderStatus.SCM_NOTIFY_RMA_QC_PENDING
//                "SCM NOTIFY > RMA QC PENDING"
                    );

            case ("LOGISTIC")
                    -> List.of
                    (
                            OrderStatus.SCM_LOGISTIC_PENDING,
                    OrderStatus.DELIVERY_PENDING
//                    "SCM > LOGISTIC PENDING",
//                    "DELIVERY PENDING"
//                    "PDI PENDING"
                    );

            default -> List.of();
        };
    }

}
