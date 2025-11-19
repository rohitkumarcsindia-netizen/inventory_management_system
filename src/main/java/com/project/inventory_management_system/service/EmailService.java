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
    public boolean sendMailOrderConfirm(String departmentEmail, Long orderId)
    {

        try
        {
            SimpleMailMessage message = new SimpleMailMessage();


            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Order Confirmation - Order #" + orderId);
            message.setText("Thank you for your order! Your order ID is " + orderId + ".");

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

    //Order approved email

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

    public boolean sendMailOrderReject(String departmentEmail, Long orderId)
    {

        try
        {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(SENDERMAIL);
            message.setTo(departmentEmail);
            message.setSubject("Order Rejected - Order #" + orderId);
            message.setText("Your Order has been Rejected " + orderId + ".");

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

}
