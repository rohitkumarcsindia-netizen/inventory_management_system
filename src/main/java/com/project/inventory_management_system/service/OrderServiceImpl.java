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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService
{
    private final OrderRepository orderRepository;
    private final UsersRepository usersRepository;
    private final OrderMapper orderMapper;

    @Override
    public ResponseEntity<?> createOrder(String username, OrdersDto ordersDto)
    {
        Users user = usersRepository.findByUsername(username);

        if (user != null)
        {
            // Set user inside DTO
            UserDto userDto = new UserDto();
            userDto.setUserId(user.getUserId());
            userDto.setUsername(user.getUsername());
            userDto.setEmail(user.getEmail());

            ordersDto.setUsers(userDto);

            // Convert DTO → Entity
            Orders orders = orderMapper.toEntity(ordersDto);
            orders.setUsers(user);

            Orders saved = orderRepository.save(orders);

            // Return DTO
            OrdersDto saveOrder =  orderMapper.toDto(saved);

            return ResponseEntity.ok(saveOrder);
        }
        return ResponseEntity.badRequest().body("User not found");
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
    public ResponseEntity<?> getAllOrders(String username)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User Not Found");
        }

        List<Orders> orders = orderRepository.findByUsers(user);

        List<OrdersDto> ordersDtos = orders.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(ordersDtos);
    }
    @Override
    public List<OrdersDto> getOrdersByUserWithLimitOffset(Users user, int page, int size)
    {
        int offset = page * size; // calculate offset
        List<Orders> orders =  orderRepository.findOrdersByUserWithLimitOffset(user.getUserId(), size, offset);

        List<OrdersDto> ordersDtos = orders.stream()
                .map(orderMapper::toDto)
                .toList();

        return ordersDtos;
    }

}


