package com.project.inventory_management_system.service;

import com.project.inventory_management_system.entity.Orders;

import java.util.List;

public interface OrderService 
{

    Orders createOrder(Orders orders);

    Orders updateOreder(Long orderId,Orders orders);

    Orders deleteOrder(Long orderId, Orders orders);

    Orders getOrders();


    List<Orders> findAllOrder();
}
