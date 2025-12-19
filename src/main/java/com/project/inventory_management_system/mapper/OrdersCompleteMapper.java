package com.project.inventory_management_system.mapper;


import com.project.inventory_management_system.dto.*;
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
        financeOrdersHistoryDto.setApprovedByUserName(approvedUser.getUsername());

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
        scmOrdersHistoryDto.setApprovedByUserName(approvedUser.getUsername());

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
        cloudOrdersHistoryDto.setUpdatedByUserName(approvedUser.getUsername());

        return cloudOrdersHistoryDto;
    }

    //Entity → DTO Syrma Orders Action History
    public SyrmaOrdersHistoryDto syrmaOrdersHistoryDto(Orders order, SyrmaApproval syrmaApprovalDetails)
    {
        SyrmaOrdersHistoryDto syrmaOrdersHistoryDto = new SyrmaOrdersHistoryDto();

        syrmaOrdersHistoryDto.setOrderId(order.getOrderId());
        syrmaOrdersHistoryDto.setCreateAt(order.getCreateAt());
        syrmaOrdersHistoryDto.setExpectedOrderDate(order.getExpectedOrderDate());
        syrmaOrdersHistoryDto.setProject(order.getProject());
        syrmaOrdersHistoryDto.setOrderType(order.getOrderType());
        syrmaOrdersHistoryDto.setInitiator(order.getInitiator());
        syrmaOrdersHistoryDto.setProductType(order.getProductType());
        syrmaOrdersHistoryDto.setProposedBuildPlanQty(order.getProposedBuildPlanQty());
        syrmaOrdersHistoryDto.setReasonForBuildRequest(order.getReasonForBuildRequest());
        syrmaOrdersHistoryDto.setPmsRemarks(order.getPmsRemarks());


        syrmaOrdersHistoryDto.setSyrmaAction(syrmaApprovalDetails.getSyrmaAction());
        syrmaOrdersHistoryDto.setActionTime(syrmaApprovalDetails.getActionTime());
        syrmaOrdersHistoryDto.setSyrmaComments(syrmaApprovalDetails.getSyrmaComments());

        Users approvedUser = syrmaApprovalDetails.getActionDoneBy();
        if (approvedUser != null)
        {
            syrmaOrdersHistoryDto.setActionDoneBy(approvedUser.getUserId());
        }

        syrmaOrdersHistoryDto.setUsers(userMapper.toDto(order.getUsers()));
        syrmaOrdersHistoryDto.setActionDoneByUserName(approvedUser.getUsername());

        return syrmaOrdersHistoryDto;
    }

    //Entity → DTO Rma Orders Action History
    public RmaOrdersHistoryDto rmaOrdersHistoryDto(Orders order, RmaApproval rmaApprovalDetails)
    {
        RmaOrdersHistoryDto rmaOrdersHistoryDto = new RmaOrdersHistoryDto();

        rmaOrdersHistoryDto.setOrderId(order.getOrderId());
        rmaOrdersHistoryDto.setCreateAt(order.getCreateAt());
        rmaOrdersHistoryDto.setExpectedOrderDate(order.getExpectedOrderDate());
        rmaOrdersHistoryDto.setProject(order.getProject());
        rmaOrdersHistoryDto.setOrderType(order.getOrderType());
        rmaOrdersHistoryDto.setInitiator(order.getInitiator());
        rmaOrdersHistoryDto.setProductType(order.getProductType());
        rmaOrdersHistoryDto.setProposedBuildPlanQty(order.getProposedBuildPlanQty());
        rmaOrdersHistoryDto.setReasonForBuildRequest(order.getReasonForBuildRequest());
        rmaOrdersHistoryDto.setPmsRemarks(order.getPmsRemarks());

        rmaOrdersHistoryDto.setRmaAction(rmaApprovalDetails.getRmaAction());
        rmaOrdersHistoryDto.setRmaActionTime(rmaApprovalDetails.getRmaActionTime());
        rmaOrdersHistoryDto.setRmaComment(rmaApprovalDetails.getRmaComment());

        Users approvedUser = rmaApprovalDetails.getApprovedBy();
        if (approvedUser != null)
        {
            rmaOrdersHistoryDto.setRmaApprovedBy(approvedUser.getUserId());
        }

        rmaOrdersHistoryDto.setUsers(userMapper.toDto(order.getUsers()));
        rmaOrdersHistoryDto.setRmaApprovedByUserName(approvedUser.getUsername());

        return rmaOrdersHistoryDto;
    }

    //Entity → DTO Amisp Orders Action History
    public ProjectTeamOrdersHistoryDto amispOrdersHistoryDto(Orders order, ProjectTeamApproval projectTeamApprovalDetails)
    {
        ProjectTeamOrdersHistoryDto projectTeamOrdersHistoryDto = new ProjectTeamOrdersHistoryDto();

        projectTeamOrdersHistoryDto.setOrderId(order.getOrderId());
        projectTeamOrdersHistoryDto.setCreateAt(order.getCreateAt());
        projectTeamOrdersHistoryDto.setExpectedOrderDate(order.getExpectedOrderDate());
        projectTeamOrdersHistoryDto.setProject(order.getProject());
        projectTeamOrdersHistoryDto.setOrderType(order.getOrderType());
        projectTeamOrdersHistoryDto.setInitiator(order.getInitiator());
        projectTeamOrdersHistoryDto.setProductType(order.getProductType());
        projectTeamOrdersHistoryDto.setProposedBuildPlanQty(order.getProposedBuildPlanQty());
        projectTeamOrdersHistoryDto.setReasonForBuildRequest(order.getReasonForBuildRequest());
        projectTeamOrdersHistoryDto.setPmsRemarks(order.getPmsRemarks());

        projectTeamOrdersHistoryDto.setAmispAction(projectTeamApprovalDetails.getAmispPdiType());
        projectTeamOrdersHistoryDto.setAmispActionTime(projectTeamApprovalDetails.getProjectTeamActionTime());
        projectTeamOrdersHistoryDto.setAmispComment(projectTeamApprovalDetails.getProjectTeamComment());
        projectTeamOrdersHistoryDto.setPdiLocation(projectTeamApprovalDetails.getPdiLocation());
        projectTeamOrdersHistoryDto.setDispatchDetails(projectTeamApprovalDetails.getDispatchDetails());
        projectTeamOrdersHistoryDto.setDocumentUrl(projectTeamApprovalDetails.getDocumentUrl());
        projectTeamOrdersHistoryDto.setSerialNumbers(projectTeamApprovalDetails.getSerialNumbers());
        projectTeamOrdersHistoryDto.setLocationDetails(projectTeamApprovalDetails.getLocationDetails());

        Users approvedUser = projectTeamApprovalDetails.getActionBy();
        if (approvedUser != null)
        {
            projectTeamOrdersHistoryDto.setAmispApprovedBy(approvedUser.getUserId());
        }

        projectTeamOrdersHistoryDto.setUsers(userMapper.toDto(order.getUsers()));
        projectTeamOrdersHistoryDto.setAmispApprovedByUserName(approvedUser.getUsername());

        return projectTeamOrdersHistoryDto;
    }

    //Entity → DTO Logistic Orders Action History
    public LogisticOrdersHistoryDto logisticOrderHistoryDto(Orders order, LogisticsDetails logisticsDetails)
    {
        LogisticOrdersHistoryDto logisticOrdersHistoryDto = new LogisticOrdersHistoryDto();

        logisticOrdersHistoryDto.setOrderId(order.getOrderId());
        logisticOrdersHistoryDto.setCreateAt(order.getCreateAt());
        logisticOrdersHistoryDto.setExpectedOrderDate(order.getExpectedOrderDate());
        logisticOrdersHistoryDto.setProject(order.getProject());
        logisticOrdersHistoryDto.setOrderType(order.getOrderType());
        logisticOrdersHistoryDto.setInitiator(order.getInitiator());
        logisticOrdersHistoryDto.setProductType(order.getProductType());
        logisticOrdersHistoryDto.setProposedBuildPlanQty(order.getProposedBuildPlanQty());
        logisticOrdersHistoryDto.setReasonForBuildRequest(order.getReasonForBuildRequest());
        logisticOrdersHistoryDto.setPmsRemarks(order.getPmsRemarks());

        logisticOrdersHistoryDto.setLogisticsComment(logisticsDetails.getLogisticsComment());
        logisticOrdersHistoryDto.setLogisticsPdiComment(logisticsDetails.getLogisticsPdiComment());
        logisticOrdersHistoryDto.setActionTime(logisticsDetails.getActionTime());
        logisticOrdersHistoryDto.setCourierName(logisticsDetails.getCourierName());
        logisticOrdersHistoryDto.setDeliveredStatus(logisticsDetails.getDeliveredStatus());
        logisticOrdersHistoryDto.setDispatchDate(logisticsDetails.getDispatchDate());
        logisticOrdersHistoryDto.setSerialNumbers(logisticsDetails.getSerialNumbers());
        logisticOrdersHistoryDto.setActualDeliveryDate(logisticsDetails.getActualDeliveryDate());
        logisticOrdersHistoryDto.setShippingMode(logisticsDetails.getShippingMode());
        logisticOrdersHistoryDto.setExpectedDeliveryDate(logisticsDetails.getExpectedDeliveryDate());
        logisticOrdersHistoryDto.setPdiAction(logisticsDetails.getPdiAction());
        logisticOrdersHistoryDto.setShipmentDocumentUrl(logisticsDetails.getShipmentDocumentUrl());
        logisticOrdersHistoryDto.setTrackingNumber(logisticsDetails.getTrackingNumber());

        Users approvedUser = logisticsDetails.getActionBy();
        if (approvedUser != null)
        {
            logisticOrdersHistoryDto.setActionBy(approvedUser.getUserId());
        }

        logisticOrdersHistoryDto.setUsers(userMapper.toDto(order.getUsers()));
        logisticOrdersHistoryDto.setActionByUserName(approvedUser.getUsername());

        return logisticOrdersHistoryDto;
    }

}
