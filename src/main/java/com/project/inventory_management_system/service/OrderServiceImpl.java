package com.project.inventory_management_system.service;

import com.project.inventory_management_system.controller.UserAndOrderIdController;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.UserAndOrderId;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService
{
    private final OrderRepository orderRepository;
    private final UsersRepository usersRepository;

    @Override
    public Orders createOrder(Orders orders)
    {
        Orders orderId = orderRepository.findById(orders.getOrderId());
        Users userId =  orders.getUsers().getUserId();
        UserAndOrderId userAndOrderId = new UserAndOrderId();
        userAndOrderId.setUsers(userId);
        userAndOrderId.setOrders(orderId);
        Users users = usersRepository.findByUserId(orders.getUsers().getUserId());
        if(users != null)
        {
            Orders saveOrder = orderRepository.save(orders);
            return saveOrder;
        }
        else
            return null;
    }
}
