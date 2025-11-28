package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.dto.UserDto;
import com.project.inventory_management_system.entity.Department;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectOrderServiceImpl implements ProjectOrderService
{
    private final OrderRepository orderRepository;
    private final UsersRepository usersRepository;
    private final OrderMapper orderMapper;
    private final EmailService emailService;
    private final DepartmentRepository departmentRepository;

    //Project Team Order Created Method
    @Override
    public ResponseEntity<?> createOrder(String username, OrdersDto ordersDto)
    {
        Users user = usersRepository.findByUsername(username);


        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname()
                .equalsIgnoreCase("PROJECT TEAM"))
        {
            return ResponseEntity.badRequest().body("This User not allowed create orders");
        }


            // Set user inside Dto
            UserDto userDto = new UserDto();
            userDto.setUserId(user.getUserId());
            userDto.setUsername(user.getUsername());
            userDto.setEmail(user.getEmail());

            ordersDto.setUsers(userDto);

            // Convert Dto → Entity
            Orders orders = orderMapper.toEntity(ordersDto);
            orders.setUsers(user);

            if (orders.getOrderType().equalsIgnoreCase("PURCHASE"))
            {
                orders.setCreateAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
                orders.setStatus("SCM PENDING");


                Orders saved = orderRepository.save(orders);

                Department financeTeam = departmentRepository.findByDepartmentname("SCM");

                //sending mail
                boolean mailsent = emailService.sendMailOrderConfirm(financeTeam.getDepartmentEmail(), saved.getOrderId());

                if (!mailsent)
                {
                    return ResponseEntity.status(500).body("Mail Not Sent");
                }

                // Return Dto
                OrdersDto saveOrder = orderMapper.toDto(saved);


                return ResponseEntity.ok(saveOrder);
            }


            orders.setCreateAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
            orders.setStatus("FINANCE PENDING");

            Orders saved = orderRepository.save(orders);

            Department financeTeam = departmentRepository.findByDepartmentname("FINANCE");

            //sending mail
            boolean mailsent = emailService.sendMailOrderConfirm(financeTeam.getDepartmentEmail(), saved.getOrderId());

            if (!mailsent)
            {
                return ResponseEntity.status(500).body("Mail Not Sent");
            }

            // Return Dto
            OrdersDto saveOrder = orderMapper.toDto(saved);


            return ResponseEntity.ok(saveOrder);


    }



    //Project Team Order Get Method
    @Override
    public ResponseEntity<?> getOrdersByUserWithLimitOffset(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("PROJECT TEAM"))
        {
            return ResponseEntity.badRequest().body("Only project team can view pending orders");
        }

        List<Orders> orders =  orderRepository.findOrdersByUserWithLimitOffset(user.getUserId(), offset, limit);

        if (orders.isEmpty())
        {
            return ResponseEntity.badRequest().body("No orders found");
        }

        List<OrdersDto> ordersDtoList = orders.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(ordersDtoList);
    }


    //Project Team Order update Method
    @Override
    public OrdersDto updateOrderDetails(String username, Long orderId, OrdersDto ordersDto)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            throw new RuntimeException("User not found");
        }

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));


        if (order.getUsers() == null || order.getUsers().getUserId() == 0 ||
                order.getUsers().getUserId() != user.getUserId())
        {
            throw new RuntimeException("Order not found for this user");
        }

        // 🚀 Update only fields from DTO
        order.setCreateAt(ordersDto.getCreateAt());
        order.setExpectedOrderDate(ordersDto.getExpectedOrderDate());
        order.setProject(ordersDto.getProject());
        order.setProductType(ordersDto.getProductType());
        order.setProposedBuildPlanQty(ordersDto.getProposedBuildPlanQty());
        order.setReasonForBuildRequest(ordersDto.getReasonForBuildRequest());
        order.setInitiator(ordersDto.getInitiator());
        order.setStatus(ordersDto.getStatus());
        order.setPmsRemarks(ordersDto.getPmsRemarks());



        // Save updated order
        Orders updateOrder = orderRepository.save(order);

        // Return Dto
        return orderMapper.toDto(updateOrder);
    }


    //Project Team Order delete Method
    @Override
    public String deleteOrder(String username, Long orderId)
    {
        Users user = usersRepository.findByUsername(username);
         if (user == null)
         {
             throw new RuntimeException("User not found");
         }
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));


        if (order.getUsers() == null || order.getUsers().getUserId() == 0 ||
                order.getUsers().getUserId() != user.getUserId())
        {
            throw new RuntimeException("Order not found for this user");
        }

        //delete order
        orderRepository.deleteById(orderId);

        return "Order deleted successfully";
    }


}


