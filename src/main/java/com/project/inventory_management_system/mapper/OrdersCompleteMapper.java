package com.project.inventory_management_system.mapper;


import com.project.inventory_management_system.dto.CloudOrdersHistoryDto;
import com.project.inventory_management_system.dto.FinanceOrdersHistoryDto;
import com.project.inventory_management_system.dto.ScmOrdersHistoryDto;
import com.project.inventory_management_system.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdersCompleteMapper
{
    private final UserMapper userMapper;

    // Entity → DTO Finance Orders Action History
    public FinanceOrdersHistoryDto financeOrdersHistoryDto(Orders order, FinanceApproval financeApproval)
    {
        if (order == null) return null;

        FinanceOrdersHistoryDto financeOrdersHistoryDto = new FinanceOrdersHistoryDto();

        financeOrdersHistoryDto.setOrderId(order.getOrderId());
        financeOrdersHistoryDto.setCreateAt(order.getCreateAt());
        financeOrdersHistoryDto.setExpectedOrderDate(order.getExpectedOrderDate());
        financeOrdersHistoryDto.setProject(order.getProject());
        financeOrdersHistoryDto.setOrderType(order.getOrderType());
        financeOrdersHistoryDto.setInitiator(order.getInitiator());
        financeOrdersHistoryDto.setProductType(order.getProductType());
        financeOrdersHistoryDto.setProposedBuildPlanQty(order.getProposedBuildPlanQty());
        financeOrdersHistoryDto.setReasonForBuildRequest(order.getReasonForBuildRequest());
        financeOrdersHistoryDto.setPmsRemarks(order.getPmsRemarks());


        financeOrdersHistoryDto.setFinanceAction(financeApproval.getFinanceAction());
        financeOrdersHistoryDto.setFinanceActionTime(financeApproval.getFinanceActionTime());
        financeOrdersHistoryDto.setFinanceReason(financeApproval.getFinanceReason());

        Users approvedUser = financeApproval.getFinanceApprovedBy();
        if (approvedUser != null)
        {
            financeOrdersHistoryDto.setFinanceApprovedBy(approvedUser.getUserId());
        }

        financeOrdersHistoryDto.setUsers(userMapper.toDto(order.getUsers())); // nested mapping

        return financeOrdersHistoryDto;
    }

    // Entity → DTO Scm Orders Action History
    public ScmOrdersHistoryDto scmOrdersHistoryDto(Orders order, ScmApproval jiraDetails)
    {

        if (order == null) return null;

        ScmOrdersHistoryDto scmOrdersHistoryDto = new ScmOrdersHistoryDto();

        scmOrdersHistoryDto.setOrderId(order.getOrderId());
        scmOrdersHistoryDto.setCreateAt(order.getCreateAt());
        scmOrdersHistoryDto.setExpectedOrderDate(order.getExpectedOrderDate());
        scmOrdersHistoryDto.setProject(order.getProject());
        scmOrdersHistoryDto.setOrderType(order.getOrderType());
        scmOrdersHistoryDto.setInitiator(order.getInitiator());
        scmOrdersHistoryDto.setProductType(order.getProductType());
        scmOrdersHistoryDto.setProposedBuildPlanQty(order.getProposedBuildPlanQty());
        scmOrdersHistoryDto.setReasonForBuildRequest(order.getReasonForBuildRequest());
        scmOrdersHistoryDto.setPmsRemarks(order.getPmsRemarks());

        scmOrdersHistoryDto.setJiraTicketNumber(jiraDetails.getJiraTicketNumber());
        scmOrdersHistoryDto.setJiraSummary(jiraDetails.getJiraSummary());

        scmOrdersHistoryDto.setScmAction(jiraDetails.getScmAction());
        scmOrdersHistoryDto.setScmActionTime(jiraDetails.getActionTime());
        scmOrdersHistoryDto.setJiraStatus(jiraDetails.getJiraStatus());

        Users approvedUser = jiraDetails.getApprovedBy();
        if (approvedUser != null)
        {
            scmOrdersHistoryDto.setApprovedBy(approvedUser.getUserId());
        }

        scmOrdersHistoryDto.setUsers(userMapper.toDto(order.getUsers())); // nested mapping

        return scmOrdersHistoryDto;
    }

    //Entity → DTO Cloud Orders Action History
    public CloudOrdersHistoryDto cloudOrdersHistoryDto(Orders order, CloudApproval jiraDetails)
    {
        CloudOrdersHistoryDto cloudOrdersHistoryDto = new CloudOrdersHistoryDto();

        cloudOrdersHistoryDto.setOrderId(order.getOrderId());
        cloudOrdersHistoryDto.setCreateAt(order.getCreateAt());
        cloudOrdersHistoryDto.setExpectedOrderDate(order.getExpectedOrderDate());
        cloudOrdersHistoryDto.setProject(order.getProject());
        cloudOrdersHistoryDto.setOrderType(order.getOrderType());
        cloudOrdersHistoryDto.setInitiator(order.getInitiator());
        cloudOrdersHistoryDto.setProductType(order.getProductType());
        cloudOrdersHistoryDto.setProposedBuildPlanQty(order.getProposedBuildPlanQty());
        cloudOrdersHistoryDto.setReasonForBuildRequest(order.getReasonForBuildRequest());
        cloudOrdersHistoryDto.setPmsRemarks(order.getPmsRemarks());


        cloudOrdersHistoryDto.setJiraDescription(jiraDetails.getJiraDescription());
        cloudOrdersHistoryDto.setPriority(jiraDetails.getPriority());
        cloudOrdersHistoryDto.setCloudComments(jiraDetails.getCloudComments());

        cloudOrdersHistoryDto.setCloudAction(jiraDetails.getCloudAction());
        cloudOrdersHistoryDto.setCloudActionTime(jiraDetails.getActionTime());

        Users approvedUser = jiraDetails.getUpdatedBy();
        if (approvedUser != null)
        {
            cloudOrdersHistoryDto.setUpdatedBy(approvedUser.getUserId());
        }

        cloudOrdersHistoryDto.setUsers(userMapper.toDto(order.getUsers()));

        return cloudOrdersHistoryDto;
    }

}
