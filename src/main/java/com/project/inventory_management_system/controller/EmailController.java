package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.Department;
import com.project.inventory_management_system.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api")
@ResponseBody
public class EmailController
{
    @Autowired
    private EmailService emailService;


    @GetMapping("/test")
    public String testMail(HttpServletRequest request, Department department, OrdersDto ordersDto)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

         emailService.sendMailOrderConfirm(department.getDepartmentEmail(), ordersDto.getOrderId());


//        emailService.sendMailOrderConfirm(10L);
        return "Mail sent!";
    }
}
