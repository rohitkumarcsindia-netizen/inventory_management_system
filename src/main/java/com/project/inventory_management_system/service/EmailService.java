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

    public void sendMailOrderConfirm(String userEmail, String departmentEmail, Long orderId)
    {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(userEmail);
        message.setTo(departmentEmail);
        message.setSubject("Order Confirmation - Order #" + orderId);
        message.setText("Thank you for your order! Your order ID is " + orderId + ".");

        mailSender.send(message);
        System.out.println("Mail sent successfully to " + departmentEmail);


    }

    //Order approved email

    public void sendMailOrderApprove(String userEmail, String departmentEmail, Long orderId)
    {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(userEmail);
        message.setTo(departmentEmail);
        message.setSubject("Order Approved - Order #" + orderId);
        message.setText("Create Jira Ticket for this OrderId " + orderId + ".");

        mailSender.send(message);
        System.out.println("Mail sent successfully to " + departmentEmail);


    }

    //Order Rejected email

    public void sendMailOrderReject(String userEmail, String departmentEmail, Long orderId)
    {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(userEmail);
        message.setTo(departmentEmail);
        message.setSubject("Order Rejected - Order #" + orderId);
        message.setText("Your Order has been Rejected " + orderId + ".");

        mailSender.send(message);
        System.out.println("Mail sent successfully to " + departmentEmail);


    }

}
