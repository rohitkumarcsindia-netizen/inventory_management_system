package com.project.inventory_management_system.service;

import com.project.inventory_management_system.entity.ProjectTeamApproval;
import com.project.inventory_management_system.entity.FinanceApproval;
import com.project.inventory_management_system.entity.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService
{
    @Autowired
    private JavaMailSender mailSender;

    private static final String SENDERMAIL = "shubhamkumar10510sk@gmail.com";

    //Order Create Mail Method
    public boolean sendMailNextDepartmentOrderCreate(String departmentEmail, Long orderId)
    {

        try
        {
            SimpleMailMessage message = new SimpleMailMessage();


            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Order Confirmation - Order #" + orderId);
            message.setText("A new order has been created and is waiting for your action. Order ID: "+ orderId);

            mailSender.send(message);

            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;

        }
        catch (Exception e)
        {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }


    }


    public boolean sendMailOrderConfirm(String departmentEmail, Long orderId)
    {

        try
        {
            SimpleMailMessage message = new SimpleMailMessage();


            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Order Confirmation - Order #" + orderId);
            message.setText("Your order has been successfully placed. Order ID: " + orderId + ".");

            mailSender.send(message);

            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;

        }
        catch (Exception e)
        {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }


    }

    //Finance Order approved email

    public boolean sendMailOrderApprove(String departmentEmail, Long orderId)
    {

        try
        {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Order Approved - Order #" + orderId);
            message.setText("Create Jira Ticket for this OrderId " + orderId + ".");

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);

            return true;

        }
        catch (Exception e)
        {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }

    }

    //Order Rejected email

    public boolean sendMailOrderReject(String reason, String departmentEmail, Long orderId)
    {

        try
        {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Order Rejected - Order #" + orderId);
            message.setText(reason + " " + orderId + ".");

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        }
        catch (Exception e)
        {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }

    }


    //Create Jira Ticket Mail send Method
    public boolean sendMailCreateJiraTicket(String departmentEmail, Long orderId)
    {

        try
        {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Jira Ticket Created #" + orderId);
            message.setText("This OrderId Jira Ticket has been Generated " + orderId + ".");

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        }
        catch (Exception e)
        {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }

    }
    //Cloud Certificate Generate Mail send Method
    public boolean sendMailCertificateGenerate(String departmentEmail, Long orderId)
    {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Order #" + orderId + " – Certificate & Product Details Submitted for SCM Review");
            String mailBody = ""
                    + "Dear SCM Team,\n\n"
                    + "The following order has been successfully processed by the Cloud department and is now forwarded for your review and recheck.\n\n"
                    + "ORDER DETAILS\n"
                    + "-------------\n"
                    + "Order ID     : " + orderId + "\n"
                    + "Certificate Status : Generated & Uploaded\n\n"
                    + "Kindly review the details and proceed with the necessary SCM action.\n"
                    + "Please update the status in IMS after completion to maintain workflow tracking.\n\n"
                    + "If any clarification is needed, feel free to contact the Cloud team.\n\n"
                    + "Thanks & Regards,\n"
                    + "Cloud Team\n"
                    + "IMS Portal";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        } catch (Exception e) {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }

    //SCM Prodback Generate Mail send Method
    public boolean sendMailProdbackGenerate(String departmentEmail, Long orderId)
    {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Order #" + orderId + " – JIRA Ticket Closed & Ready for Production");
            String mailBody = ""
                    + "Dear Syrma Production Team,\n\n"
                    + "This is to inform you that the JIRA ticket for the below-mentioned order has been successfully closed by the Cloud department and the order is now ready for Production.\n\n"
                    + "ORDER DETAILS\n"
                    + "-------------\n"
                    + "Order ID     : " + orderId + "\n"
                    + "Certificate Status : Completed & Verified\n\n"
                    + "Kindly start the Production process as per the project workflow.\n"
                    + "After completion, please update the status in IMS and proceed to the Testing stage.\n\n"
                    + "If any clarification is needed, feel free to contact the Cloud team.\n\n"
                    + "Thanks & Regards,\n"
                    + "Scm Team\n"
                    + "IMS Portal";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        } catch (Exception e) {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }

    //Syrma Production and Testing Complete Generate Mail send Method
    public boolean sendMailProductionAndTestingComplete(String departmentEmail, Long orderId)
    {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Order #" + orderId + " – Production & Testing Completed (Ready for SCM Action)");

            String mailBody = ""
                    + "Dear SCM Team,\n\n"
                    + "This is to inform you that the Production and Testing processes for the below-mentioned order "
                    + "have been successfully completed by the Syrma team.\n\n"
                    + "ORDER DETAILS\n"
                    + "-------------\n"
                    + "Order ID       : " + orderId + "\n"
                    + "Production     : Completed\n"
                    + "Testing        : Completed\n"
                    + "Current Status : Awaiting SCM Action\n\n"
                    + "Kindly proceed with the next SCM process as per the workflow.\n"
                    + "After completion, please update the status in IMS.\n\n"
                    + "If any clarification is required, feel free to contact the Syrma Production team.\n\n"
                    + "Thanks & Regards,\n"
                    + "Syrma Production Team\n"
                    + "IMS Portal";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        } catch (Exception e) {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }

    //Scm Rma notify Mail send Method
    public boolean sendMailNotifyRma(String departmentEmail, Long orderId)
    {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Order #" + orderId + " – Request for RMA Processing (Production & Testing Completed)");

            String mailBody = ""
                    + "Dear RMA Team,\n\n"
                    + "This is to notify you that the below-mentioned order has successfully completed Production and Testing "
                    + "and is now ready for RMA processing.\n\n"
                    + "ORDER DETAILS\n"
                    + "-------------\n"
                    + "Order ID       : " + orderId + "\n"
                    + "Completion Status : Production & Testing Completed\n\n"
                    + "Kindly proceed with the RMA process as per the workflow.\n"
                    + "Once completed, please update the status in IMS.\n\n"
                    + "If any clarification is required, feel free to contact the SCM team.\n\n"
                    + "Thanks & Regards,\n"
                    + "SCM Team\n"
                    + "IMS Portal";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        } catch (Exception e) {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }

    //Rma notify Scm Mail send Method
    public boolean sendMailNotifyScm(String departmentEmail, Long orderId)
    {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Order #" + orderId + " – QC Inspection Passed & Ready for Release");

            String mailBody =  ""
                    + "Dear SCM Team,\n\n"
                    + "This is to notify you that the QC Inspection for the below-mentioned order has been successfully "
                    + "completed and the product has passed all Quality parameters.\n\n"
                    + "ORDER DETAILS\n"
                    + "-------------\n"
                    + "Order ID           : " + orderId + "\n"
                    + "QC Status          : Passed\n"
                    + "RMA Status         : Completed\n"
                    + "Current State      : Ready for Release\n\n"
                    + "Kindly proceed with the next step — arranging release and dispatch planning.\n"
                    + "Once dispatch arrangements are completed, please update the status in IMS.\n\n"
                    + "If any clarification is required, feel free to contact the RMA team.\n\n"
                    + "Thanks & Regards,\n"
                    + "RMA Team\n"
                    + "IMS Portal";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        } catch (Exception e) {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }

    //Rma notify Syrma Mail send Method
    public boolean sendMailNotifySyrma(String departmentEmail, Long orderId)
    {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Order #" + orderId + " – QC Inspection Failed (Re-Work Required)");

            String mailBody =   ""
                    + "Dear Syrma Production Team,\n\n"
                    + "This is to inform you that the QC Inspection for the below-mentioned order has failed, and the product "
                    + "requires re-work as per the inspection report.\n\n"
                    + "ORDER DETAILS\n"
                    + "-------------\n"
                    + "Order ID           : " + orderId + "\n"
                    + "QC Status          : Failed\n"
                    + "Kindly initiate the re-work process at the earliest and re-submit the order for QC inspection.\n"
                    + "Once re-work is completed, please update the status in IMS.\n\n"
                    + "If any clarification is required, feel free to contact the RMA team.\n\n"
                    + "Thanks & Regards,\n"
                    + "RMA Team\n"
                    + "IMS Portal";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        } catch (Exception e) {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }

    //Scm notify Project Team Mail send Method
    public boolean sendMailNotifyProjectTeam(String departmentEmail, Long orderId)
    {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Order #" + orderId + " – Build Ready & Awaiting Dispatch Plan");

            String mailBody = ""
                    + "Dear Project Team,\n\n"
                    + "This is to inform you that the build for the below-mentioned order is now ready and has successfully "
                    + "cleared all internal processes (Production, Testing, QC, and RMA). The order is currently marked as "
                    + "\"Ready to Release\".\n\n"
                    + "ORDER DETAILS\n"
                    + "-------------\n"
                    + "Order ID       : " + orderId + "\n"
                    + "Current Status : Build Ready / Ready to Release\n\n"
                    + "Kindly review the order and share the dispatch plan or any further instructions, if required.\n"
                    + "Once the dispatch details are finalized, the SCM team will proceed with shipment arrangements and "
                    + "update the status in IMS.\n\n"
                    + "If you need any additional information, please feel free to contact the SCM team.\n\n"
                    + "Thanks & Regards,\n"
                    + "SCM Team\n"
                    + "IMS Portal";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        } catch (Exception e) {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }

    //Project notify Amisp Team Mail send Method
    public boolean sendMailNotifyAmisp(String departmentEmail, Long orderId)
    {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Order #" + orderId + " – Build Ready (Request for AMISP Dispatch Coordination)");

            String mailBody = ""
                    + "Dear AMISP Team,\n\n"
                    + "This is to inform you that the build for the below-mentioned order has been completed and is now "
                    + "ready for dispatch. All internal processes including Production, Testing, QC, RMA closure, and "
                    + "Project Team confirmation have been successfully completed.\n\n"
                    + "ORDER DETAILS\n"
                    + "-------------\n"
                    + "Order ID         : " + orderId + "\n"
                    + "Current Status   : Ready for Dispatch\n\n"
                    + "Kindly proceed with AMISP-level coordination including shipment readiness, dispatch planning, "
                    + "and serial number documentation.\n"
                    + "Once dispatch arrangements are finalized, please share the details so SCM can execute shipment "
                    + "and update IMS.\n\n"
                    + "Thanks & Regards,\n"
                    + "Project Team\n"
                    + "IMS Portal";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        } catch (Exception e) {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }


    //Project notify Amisp Team Mail send Method
    public boolean sendMailNotifyAmispPdiType(String departmentEmail, Orders order)
    {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("PDI Type Selection Required for Order ID: " + order.getOrderId());

            String mailBody =
                    "Dear AMISP Team,\n\n" +
                    "The Project Team has completed the required build and requests your assistance in determining the PDI (Pre-Dispatch Inspection) type for the following order:\n\n" +
                    "• Order ID: " + order.getOrderId() + "\n" +
                    "• Product Type: " + order.getProductType() + "\n" +
                    "• Project Name: " + order.getProject() + "\n" +
                    "• Proposed Quantity: " + order.getProposedBuildPlanQty() + "\n\n" +
                    "Kindly review the order details and confirm the appropriate PDI type (Internal / Customer Site).\n\n" +
                    "Please let us know if any additional information is required.\n\n" +
                    "Regards,\n" +
                    "Project Team";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        } catch (Exception e) {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }

    public boolean sendMailNotifyAmispToProjectTeam(String departmentEmail, Long orderId, ProjectTeamApproval projectTeamApproval)
    {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("[IMS Notification] AMISP Status Update – " + projectTeamApproval.getAmispPdiType() + " (Order No: " + orderId + ")");

            String mailBody = "Dear Project Team,\n\n" +
                    "This is to inform you that AMISP has updated the approval status for the below order in the IMS system.\n\n" +
                    "Order ID          : " + orderId + "\n" +
                    "Current AMISP Action : " + projectTeamApproval.getAmispPdiType() + "\n" +
                    "Action Time       : " + projectTeamApproval.getProjectTeamActionTime() + "\n" +
                    "PDI Location      : " + projectTeamApproval.getPdiLocation() + "\n" +
                    "Serial Numbers    : " + projectTeamApproval.getSerialNumbers() + "\n" +
                    "Dispatch Details  : " + projectTeamApproval.getDispatchDetails() + "\n" +
                    "Comments          : " + projectTeamApproval.getProjectTeamComment() + "\n\n" +
                    (projectTeamApproval.getDocumentUrl() != null && !projectTeamApproval.getDocumentUrl().isEmpty()
                            ? "Attachment / Document : " + projectTeamApproval.getDocumentUrl() + "\n\n"
                            : "") +
                    "Next Action (If Any): Please review the updated order and proceed accordingly.\n\n" +
                    "You can review the complete details in IMS Portal:\n" +
                    "Regards,\n" +
                    "AMISP Team";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        }
        catch (Exception e)
        {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }

    public boolean sendMailNotifyToScmDispatchOrderIsReady(String departmentEmail, Long orderId)
    {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("[IMS Notification] Dispatch Order Is Ready – Order No: " + orderId);

            String mailBody =
                    "Dear SCM Team,\n\n" +
                    "This is to inform you that the project team has marked the following order as \"Dispatch Order Is Ready\" in the IMS system.\n\n" +
                    "Order ID        : " + orderId + "\n" +
                    "Next Action Required:\n" +
                    "Kindly plan for dispatch and proceed with shipment arrangements.\n\n" +
                    "You can review the full details in the IMS Portal:\n" +
                    "Regards,\n" +
                    "Project Team";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        } catch (Exception e) {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }

    public boolean sendMailNotifyScmToAmisp(String departmentEmail, Orders order, ProjectTeamApproval projectTeamApproval)
    {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("[IMS Notification] Dispatch Details Shared – Order No: " + order.getOrderId());

            String mailBody = "Dear AMISP Team,\n\n" +
                    "This is to inform you that SCM has shared the dispatch details for the following order in the IMS system.\n\n" +
                    "Order ID              : " + order.getOrderId() + "\n" +
                    "Project Name          : " + order.getProject() + "\n" +
                    "Dispatch Details      : " + projectTeamApproval.getDispatchDetails() + "\n" +
                    "Serial Numbers        : " + projectTeamApproval.getSerialNumbers() + "\n" +
                    "Next Action:\n" +
                    "Kindly proceed with post-delivery PDI and further workflow.\n\n" +
                    "You can review the complete details in the IMS Portal:\n" +
                    "Regards,\n" +
                    "SCM Team";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        }
        catch (Exception e)
        {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }

    public boolean sendMailNotifyAmisoToProjectTeam(String departmentEmail, Orders order, ProjectTeamApproval projectTeamApproval)
    {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("[IMS Notification] Location Details Shared – Order No: " + order.getOrderId());

            String mailBody =
                    "Dear Project Team,\n\n" +
                    "This is to inform you that AMISP has updated the location details for the following order in the IMS system.\n\n" +
                    "Order ID          : " + order.getOrderId() + "\n" +
                    "Project Name      : " + order.getProject() + "\n" +
                    "Pdi Location Details  : " + projectTeamApproval.getPdiLocation() + "\n" +
                    "Location Details  : " + projectTeamApproval.getLocationDetails() + "\n" +
                    "Next Action:\n" +
                    "Kindly ensure dispatch readiness and continue further workflow.\n\n" +
                    "You can review the complete details in the IMS Portal:\n" +
                    "Regards,\n" +
                    "AMISP Team";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        }
        catch (Exception e)
        {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }

    public boolean sendMailNotifyProjectTeamSentLocationForScm(String departmentEmail, Orders order, ProjectTeamApproval projectTeamApproval)
    {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("[IMS Notification] Location Details Shared – Order No: " + order.getOrderId());

            String mailBody =
                    "Dear Scm Team,\n\n" +
                            "This is to inform you that Project Team has updated the location details for the following order in the IMS system.\n\n" +
                            "Order ID          : " + order.getOrderId() + "\n" +
                            "Project Name      : " + order.getProject() + "\n" +
                            "Location Details  : " + projectTeamApproval.getPdiLocation() + "\n" +
                            "Next Action:\n" +
                            "Kindly ensure dispatch readiness and continue further workflow.\n\n" +
                            "You can review the complete details in the IMS Portal:\n" +
                            "Regards,\n" +
                            "Project Team";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        }
        catch (Exception e)
        {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }

    public boolean sendMailScmToFinanceApproval(String departmentEmail, Orders order, ProjectTeamApproval projectTeamApproval)
    {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("[IMS Approval Request] Dispatch Approval Required – Order No: " + order.getOrderId());

            String mailBody =
                    "Dear Finance Team,\n\n" +
                    "This is to inform you that SCM has submitted an approval request for the following order in the IMS system.\n\n" +
                    "Order ID          : " + order.getOrderId() + "\n" +
                    "Project Name      : " + order.getProject() + "\n" +
                    "Location Details  : " + projectTeamApproval.getPdiLocation() + "\n" +
                    "Action Required:\n" +
                    "Kindly review the dispatch details and provide the financial approval at the earliest to proceed further.\n\n" +
                    "IMS Link for Review:\n" +
                    "Regards,\n" +
                    "SCM Team";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        }
        catch (Exception e)
        {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }

    public boolean sendFinanceApprovalMailToSCM(String departmentEmail, Orders order, FinanceApproval findOrder)
    {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Order Finance Approval Received – Ready for Dispatch Planning (Order ID: " + order.getOrderId() + ")");

            String mailBody =
                    "Dear SCM Team,\n\n" +
                    "Finance approval has been successfully completed for the below order:\n\n" +
                    "Order ID       : " + order.getOrderId() + "\n" +
                    "Project Name   : " + order.getProject() + "\n" +
                    "Approval Type  : " + order.getOrderType() + "\n" +   // FOC / GST
                    "Approval Status: APPROVED\n" +
                    "Final Remark   : " + findOrder.getFinanceFinalRemark() + "\n" +
                    "Approved By    : " + findOrder.getFinanceApprovedBy().getUsername() + "\n" +
                    "Approval Date  : " + findOrder.getFinanceActionTime() + "\n\n" +
                    "Kindly proceed further with dispatch planning at the earliest.\n\n" +
                    "Regards,\n" +
                    "Finance Team\n\n" +
                    "*** This is an auto-generated email from IMS Workflow System ***";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        }
        catch (Exception e)
        {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }

    public boolean sendFinanceRejectedMailToSCM(String departmentEmail, Orders order, FinanceApproval findOrder)
    {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Order Rejected by Finance – Action Required (Order ID: " + order.getOrderId() + ")");

            String mailBody =
                    "Dear Project Team,\n\n" +
                    "The following order has been rejected by the Finance Department:\n\n" +
                    "Order ID       : " + order.getOrderId() + "\n" +
                    "Project Name   : " + order.getProject() + "\n" +
                    "Approval Type  : " + order.getOrderType() + "\n" +   // FOC / GST
                    "Approval Status: APPROVED\n" +
                    "Final Remark   : " + findOrder.getFinanceFinalRemark() + "\n" +
                    "Rejected By    : " + findOrder.getFinanceApprovedBy().getUsername() + "\n" +
                    "Approval Date  : " + findOrder.getFinanceActionTime() + "\n\n" +
                    "You are requested to review the remarks and take necessary action for re-submission or correction.\n\n" +
                    "Regards,\n" +
                    "Finance Team\n\n" +
                    "*** This is an auto-generated email from IMS Workflow System ***";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        }
        catch (Exception e)
        {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }

    public boolean sendMailScmToLogisticTeam(String departmentEmail, Orders order, ProjectTeamApproval projectTeamApproval)
    {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Dispatch Plan Received – Arrange Shipping for Order (Order ID: " + order.getOrderId() + ")");

            String mailBody =
                    "Dear Logistics Team,\n\n" +
                            "Dispatch planning has been completed by the SCM team for the following order:\n\n" +
                            "Order ID            : " + order.getOrderId() + "\n" +
                            "Project Name        : " + order.getProject() + "\n" +
                            "Dispatch Location   : " + projectTeamApproval.getPdiLocation() + "\n" +
                            "PDI Type            : " + projectTeamApproval.getAmispPdiType() + "\n\n" +
                            "Kindly arrange shipping and initiate logistics activities accordingly.\n\n" +
                            "Regards,\n" +
                            "SCM Team\n\n" +
                            "*** This is an auto-generated email from IMS Workflow System ***";

            message.setText(mailBody);

            mailSender.send(message);
            System.out.println("Mail sent successfully to " + departmentEmail);
            return true;
        }
        catch (Exception e)
        {
            System.out.println("Mail sending failed: " + e.getMessage());
            return false;
        }
    }
}
