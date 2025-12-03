package com.project.inventory_management_system.service;

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

}
