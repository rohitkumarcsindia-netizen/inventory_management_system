package com.project.inventory_management_system.mapper;


import com.project.inventory_management_system.dto.CloudOrdersHistoryDto;
import com.project.inventory_management_system.dto.FinanceOrdersHistoryDto;
import com.project.inventory_management_system.dto.RmaOrdersHistoryDto;
import com.project.inventory_management_system.dto.SyrmaOrdersHistoryDto;
import com.project.inventory_management_system.dto.ScmOrdersHistoryDto;
import com.project.inventory_management_system.dto.LogisticOrdersHistoryDto;
import com.project.inventory_management_system.entity.FinanceApproval;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.ScmApproval;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.entity.CloudApproval;
import com.project.inventory_management_system.entity.SyrmaApproval;
import com.project.inventory_management_system.entity.RmaApproval;
import com.project.inventory_management_system.entity.LogisticsDetails;
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


        financeOrdersHistoryDto.setFinanceAction(financeApproval.getFinanceAction().toDisplay());
        financeOrdersHistoryDto.setFinanceActionTime(financeApproval.getFinanceActionTime());
        financeOrdersHistoryDto.setFinanceReason(financeApproval.getFinanceReason());

        Users approvedUser = financeApproval.getFinanceApprovedBy();
        if (approvedUser != null)
        {
            financeOrdersHistoryDto.setFinanceApprovedBy(approvedUser.getUserId());
        }

        financeOrdersHistoryDto.setUsers(userMapper.toDto(order.getUsers()));
        financeOrdersHistoryDto.setApprovedByUserName(approvedUser.getUsername());

        return financeOrdersHistoryDto;
    }

    // Entity → DTO Scm Orders Action History
    public ScmOrdersHistoryDto scmOrdersHistoryDto(Orders order, ScmApproval ticketDetails)
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

        scmOrdersHistoryDto.setJiraTicketNumber(ticketDetails.getTicketNumber());
        scmOrdersHistoryDto.setJiraSummary(ticketDetails.getTicketSummary());

        scmOrdersHistoryDto.setScmAction(ticketDetails.getScmAction().toDisplay());
        scmOrdersHistoryDto.setScmActionTime(ticketDetails.getActionTime());
        scmOrdersHistoryDto.setJiraStatus(ticketDetails.getTicketStatus());

        Users approvedUser = ticketDetails.getApprovedBy();
        if (approvedUser != null)
        {
            scmOrdersHistoryDto.setApprovedBy(approvedUser.getUserId());
        }

        scmOrdersHistoryDto.setUsers(userMapper.toDto(order.getUsers())); // nested mapping
        scmOrdersHistoryDto.setApprovedByUserName(approvedUser.getUsername());

        return scmOrdersHistoryDto;
    }

    //Entity → DTO Cloud Orders Action History
    public CloudOrdersHistoryDto cloudOrdersHistoryDto(Orders order, CloudApproval ticketDetails)
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


        cloudOrdersHistoryDto.setJiraDescription(ticketDetails.getTicketDescription());
        cloudOrdersHistoryDto.setPriority(ticketDetails.getPriority());
        cloudOrdersHistoryDto.setCloudComments(ticketDetails.getCloudComments());

        cloudOrdersHistoryDto.setCloudAction(ticketDetails.getCloudAction().toDisplay());
        cloudOrdersHistoryDto.setCloudActionTime(ticketDetails.getActionTime());

        Users approvedUser = ticketDetails.getUpdatedBy();
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


        syrmaOrdersHistoryDto.setSyrmaAction(syrmaApprovalDetails.getSyrmaAction().toDisplay());
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

        rmaOrdersHistoryDto.setRmaAction(String.valueOf(rmaApprovalDetails.getRmaAction()));
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
        logisticOrdersHistoryDto.setActionTime(logisticsDetails.getActionTime());
        logisticOrdersHistoryDto.setCourierName(logisticsDetails.getCourierName());
        logisticOrdersHistoryDto.setDeliveredStatus(logisticsDetails.getDeliveredStatus());
        logisticOrdersHistoryDto.setDispatchDate(logisticsDetails.getDispatchDate());
        logisticOrdersHistoryDto.setSerialNumbers(logisticsDetails.getSerialNumbers());
        logisticOrdersHistoryDto.setActualDeliveryDate(logisticsDetails.getActualDeliveryDate());
        logisticOrdersHistoryDto.setShippingMode(logisticsDetails.getShippingMode());
        logisticOrdersHistoryDto.setExpectedDeliveryDate(logisticsDetails.getExpectedDeliveryDate());
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
