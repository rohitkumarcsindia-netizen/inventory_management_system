package com.project.inventory_management_system.service;


import com.project.inventory_management_system.entity.ScmApproval;
import org.springframework.http.ResponseEntity;

public interface ScmOrderService
{

    //SCM Team Method
    ResponseEntity<?> getPendingOrdersForScm(String username, int offset, int limit);


    ResponseEntity<?> getCompleteOrdersForScm(String username, int offset, int limit);

    ResponseEntity<?> fillJiraTicketDetail(String username, Long orderId, ScmApproval jiraDetails);

    ResponseEntity<?> prodbackGenerateAndJiraTicketClosure(String username, Long orderId, ScmApproval jiraDetails);

    ResponseEntity<?> getScmRecheckOrderPending(String username, int offset, int limit);

    ResponseEntity<?> fillJiraTicketDetailOldOrder(String username, Long orderId, ScmApproval jiraDetails);
}
