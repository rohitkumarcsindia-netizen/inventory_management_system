package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.dto.UserDto;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.mapper.OrderMapper;
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

    @Override
    public ResponseEntity<?> createOrder(String username, OrdersDto ordersDto)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
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

            //sending mail
            emailService.sendMailOrderConfirm(saved.getOrderId());

            // Return Dto
            OrdersDto saveOrder =  orderMapper.toDto(saved);


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


}


