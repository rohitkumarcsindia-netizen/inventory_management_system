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
                    );

            case ("CLOUD TEAM")
                    -> List.of
                    (
                    OrderStatus.SCM_CREATED_TICKET_CLOUD_PENDING
                    );


            case ("SYRMA")
                    -> List.of
                    (
                    OrderStatus.SCM_JIRA_TICKET_CLOSURE_SYRMA_PENDING,
                    OrderStatus.RMA_QC_FAIL_SYRMA_RE_PROD_TEST_PENDING
                    );

            case ("RMA")
                    -> List.of
                    (
                    OrderStatus.SCM_NOTIFY_RMA_QC_PENDING
                    );

            case ("LOGISTIC")
                    -> List.of
                    (
                    OrderStatus.SCM_LOGISTIC_PENDING,
                    OrderStatus.DELIVERY_PENDING
                    );

            default -> List.of();
        };
    }

}
