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

    public void sendMailOrderConfirm(Long orderId)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        String fixedEmail = "shubhamkumar10510sk@gmail.com";
        message.setTo(fixedEmail);
        message.setSubject("Order Confirmation - Order #" + orderId);
        message.setText("Thank you for your order! Your order ID is " + orderId + ".");

        mailSender.send(message);
        System.out.println("Mail sent successfully to " + fixedEmail);


    }

}
