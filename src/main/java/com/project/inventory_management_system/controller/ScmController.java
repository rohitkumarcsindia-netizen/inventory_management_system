package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.entity.ScmApproval;
import com.project.inventory_management_system.service.ScmOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/api/orders/scm")
@RequiredArgsConstructor
public class ScmController
{
    private final ScmOrderService scmOrderService;


    @GetMapping("/pending")
    public ResponseEntity<?> getPendingOrdersForScm(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

       return scmOrderService.getPendingOrdersForScm(userDetails.getUsername(), offset, limit);

    }


    @GetMapping("/complete")
    public ResponseEntity<?> getCompleteOrdersForScm(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return  scmOrderService.getCompleteOrdersForScm(userDetails.getUsername(), offset, limit);

    }


    @PostMapping("/jira/details/{orderId}")
    public ResponseEntity<?> fillJiraTicketDetail(HttpServletRequest request, @PathVariable Long orderId, @RequestBody ScmApproval jiraDetails)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return scmOrderService.fillJiraTicketDetail(userDetails.getUsername(),orderId, jiraDetails);
    }

    @PutMapping("/jira-ticket-closure/{orderId}")
    public ResponseEntity<?>  prodbackGenerateAndJiraTicketClosure(HttpServletRequest request, @PathVariable Long orderId, @RequestBody ScmApproval jiraDetails)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return scmOrderService.prodbackGenerateAndJiraTicketClosure(userDetails.getUsername(), orderId, jiraDetails);
    }

    // old button method
    @PostMapping("/old/jira/details/{orderId}")
    public ResponseEntity<?> fillJiraTicketDetailOldOrder(HttpServletRequest request, @PathVariable Long orderId, @RequestBody ScmApproval jiraDetails)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return scmOrderService.fillJiraTicketDetailOldOrder(userDetails.getUsername(),orderId, jiraDetails);
    }


//    // Scm Recheck Pending And Jira Closure Method
//    @GetMapping("/recheck")
//    public ResponseEntity<?> getScmRecheckOrderPending(
//            HttpServletRequest request,
//            @RequestParam(defaultValue = "0") int offset,
//            @RequestParam(defaultValue = "10") int limit)
//    {
//        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
//
//        if (userDetails == null)
//        {
//            return ResponseEntity.status(401).body("Unauthorized");
//        }
//
//       return  scmOrderService.getScmRecheckOrderPending(userDetails.getUsername(), offset, limit);
//
//    }
}
