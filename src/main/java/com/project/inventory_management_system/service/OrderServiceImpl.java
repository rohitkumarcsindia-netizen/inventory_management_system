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

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService
{
    private final OrderRepository orderRepository;
    private final UsersRepository usersRepository;
    private final OrderMapper orderMapper;
    private final EmailService emailService;
    private final DepartmentRepository departmentRepository;

    @Override
    public ResponseEntity<?> createOrder(String username, OrdersDto ordersDto)
    {
        Users user = usersRepository.findByUsername(username);


        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname()
                .equalsIgnoreCase("project team"))
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

            Orders saved = orderRepository.save(orders);

            Department financeTeam = departmentRepository.findByDepartmentname("finance");

            //sending mail
            boolean mailsent = emailService.sendMailOrderConfirm(user.getEmail(),
                    financeTeam.getDepartmentEmail(), saved.getOrderId());

            if (!mailsent)
            {
                return ResponseEntity.status(500).body("Mail Not Sent");
            }

            // Return Dto
            OrdersDto saveOrder = orderMapper.toDto(saved);


            return ResponseEntity.ok(saveOrder);


    }


    @Override
    public List<OrdersDto> getOrdersByUserWithLimitOffset(Users user, int offset, int limit)
    {
        List<Orders> orders =  orderRepository.findOrdersByUserWithLimitOffset(user.getUserId(), limit, offset);

        List<OrdersDto> ordersDtos = orders.stream()
                .map(orderMapper::toDto)
                .toList();

        return ordersDtos;
    }

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
        order.setOrderDate(ordersDto.getOrderDate());
        order.setProject(ordersDto.getProject());
        order.setProductType(ordersDto.getProductType());
        order.setProposedBuildPlanQty(ordersDto.getProposedBuildPlanQty());
        order.setReasonForBuildRequest(ordersDto.getReasonForBuildRequest());
        order.setInitiator(ordersDto.getInitiator());
        order.setStatus(ordersDto.getStatus());
        order.setAktsComments(ordersDto.getAktsComments());
        order.setPmsRemarks(ordersDto.getPmsRemarks());

        // Save updated order
        Orders updateOrder = orderRepository.save(order);

        // Return Dto
        return orderMapper.toDto(updateOrder);
    }

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

        return "Order deleted successfull";
    }

    // Order Approve and Reject Method

    @Override
    public ResponseEntity<?> approveOrder(String username, Long orderId)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("finance"))
        {
            return ResponseEntity.badRequest().body("Only finance team can approve orders");
        }

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus("Approved");
        orderRepository.save(order);

        Department cloudTeam = departmentRepository.findByDepartmentname("cloud team");

       boolean mailsent = emailService.sendMailOrderApprove(user.getEmail(), cloudTeam.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Order Approved Successfully");
    }

    @Override
    public ResponseEntity<?> rejectOrder(String username, Long orderId)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("finance"))
        {
            return ResponseEntity.badRequest().body("Only finance team can reject orders");
        }

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus("Rejected");
        orderRepository.save(order);

        Department cloudTeam = departmentRepository.findByDepartmentname("project team");

        boolean mailsent = emailService.sendMailOrderReject(user.getEmail(), cloudTeam.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Order Reject Successfully");
    }


    // finance Team getOrders Method

    @Override
    public ResponseEntity<?> getPendingOrdersForFinance(String username)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("finance"))
        {
            return ResponseEntity.badRequest().body("Only finance team can view pending orders");
        }

        List<Orders> orders = orderRepository.findByStatus("Project");

        List<OrdersDto> list = orders.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(list);
    }


}


