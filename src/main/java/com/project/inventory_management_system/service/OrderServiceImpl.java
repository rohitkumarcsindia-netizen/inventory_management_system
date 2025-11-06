package com.project.inventory_management_system.service;

//import com.project.inventory_management_system.controller.UserAndOrderIdController;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UsersRepository usersRepository;

    @Override
    public Orders createOrder(Orders orders) {

        Users existingUser = usersRepository.findByUserId(orders.getUsers().getUserId());

        if (existingUser != null) {
            orders.setUsers(existingUser);
            return orderRepository.save(orders);
        }
        else
        {
            return null;
        }
    }

    @Override
    public Orders updateOreder(Long orderId,Orders orders)
    {
        Optional<Orders> findOrder = orderRepository.findById(orderId);
        if (findOrder.isPresent())
        {
            Orders existingOrder = findOrder.get();
            existingOrder.setProject(orders.getProject());
            existingOrder.setInitiator(orders.getInitiator());
            existingOrder.setAktsComments(orders.getAktsComments());
            existingOrder.setProductType(orders.getProductType());
            existingOrder.setProposedBuildPlanQty(orders.getProposedBuildPlanQty());
            existingOrder.setReasonForBuildRequest(orders.getReasonForBuildRequest());
            return orderRepository.save(existingOrder);
        }
        else
            return null;
    }

    @Override
    public Orders deleteOrder(Long orderId, Orders orders)
    {
        Optional<Orders> findOrder = orderRepository.findById(orderId);
        if (findOrder.isPresent())
        {
            Orders existingOrder = findOrder.get();
            orderRepository.deleteById(existingOrder.getOrderId());
            return existingOrder;
        }
        else
            return null;
    }

    @Override
    public Orders getOrders()
    {
        Optional<Orders> findOrder = orderRepository.findById(orders.getOrderId());
        if (findOrder.isPresent())
        {
            Orders existingOrder = findOrder.get();
            return existingOrder;
        }
        return null;
    }

    @Override
    public List<Orders> findAllOrder()
    {
        return orderRepository.findAll();
    }

}
