package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.LogisticOrderDto;
import com.project.inventory_management_system.dto.LogisticOrdersHistoryDto;
import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.*;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.mapper.OrdersCompleteMapper;
import com.project.inventory_management_system.repository.LogisticsDetailsRepository;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LogisticsOrderServiceImpl implements LogisticsOrderService
{
    private final UsersRepository usersRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final LogisticsDetailsRepository logisticsDetailsRepository;
    private final OrdersCompleteMapper ordersCompleteMapper;


    @Override
    public ResponseEntity<?> getPendingOrdersForLogistic(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("LOGISTIC"))
        {
            return ResponseEntity.status(403).body("Only logistic team can view pending orders");
        }

        // Allowed Finance statuses (priority order)
        List<String> logisticStatuses = List.of(
                "SCM > LOGISTIC PENDING",
                "DELIVERY PENDING",
                "PDI PENDING"
        );

        List<Orders> orders = orderRepository. findByLogisticStatusWithLimitOffset(logisticStatuses, offset, limit);

        if (orders.isEmpty())
        {
            return ResponseEntity.ok("No Orders found");
        }
        List<OrdersDto> ordersDtoList = orders.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.countByStatusList(logisticStatuses),
                "orders", ordersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> fillShippingDetails(String username, Long orderId, LogisticsDetails shippingDetails)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("LOGISTIC"))
        {
            return ResponseEntity.status(403).body("Only logistic team can approve orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("SCM > LOGISTIC PENDING"))
        {
            return ResponseEntity.status(403).body("Order is not pending for logistic approval");
        }

        //Logistic Details table data save
        LogisticsDetails logisticsDetails = new LogisticsDetails();
        logisticsDetails.setOrder(order);
        logisticsDetails.setDispatchDate(shippingDetails.getDispatchDate());
        logisticsDetails.setShippingMode(shippingDetails.getShippingMode());
        logisticsDetails.setLogisticsComment(shippingDetails.getLogisticsComment().trim());
        logisticsDetails.setDeliveredStatus(shippingDetails.getDeliveredStatus());
        logisticsDetails.setCourierName(shippingDetails.getCourierName());
        logisticsDetails.setSerialNumbers(shippingDetails.getSerialNumbers());
        logisticsDetails.setTrackingNumber(shippingDetails.getTrackingNumber());
        logisticsDetails.setExpectedDeliveryDate(shippingDetails.getExpectedDeliveryDate());
        logisticsDetails.setShipmentDocumentUrl(shippingDetails.getShipmentDocumentUrl());
        logisticsDetails.setActionTime(LocalDateTime.now());
        logisticsDetails.setActionBy(user);
        logisticsDetailsRepository.save(logisticsDetails);


        //Order table status update
        order.setStatus("DELIVERY PENDING");
        orderRepository.save(order);

        return ResponseEntity.ok("Order Shipping Successfully");
    }

    @Override
    public ResponseEntity<?> fillDeliveryDetails(String username, Long orderId, LogisticsDetails deliveryDetails)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("LOGISTIC"))
        {
            return ResponseEntity.status(403).body("Only logistic team can approve orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("DELIVERY PENDING"))
        {
            return ResponseEntity.status(403).body("Order is not pending for logistic approval");
        }

        LogisticsDetails findOrder = logisticsDetailsRepository.findByOrder_OrderId(order.getOrderId());

        //Logistic Details table data update
        findOrder.setDeliveredStatus(deliveryDetails.getDeliveredStatus());
        findOrder.setActionTime(LocalDateTime.now());
        findOrder.setLogisticsComment(deliveryDetails.getLogisticsComment().trim());
        findOrder.setActualDeliveryDate(deliveryDetails.getActualDeliveryDate());
        logisticsDetailsRepository.save(findOrder);

        //Order table status update
        order.setStatus("PDI PENDING");
        orderRepository.save(order);

        return ResponseEntity.ok("Order Delivery Successfully");
    }

    @Override
    public ResponseEntity<?> fillPassPdiDetails(String username, Long orderId, LogisticsDetails pdiComments)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("LOGISTIC"))
        {
            return ResponseEntity.status(403).body("Only logistic team can approve orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("PDI PENDING"))
        {
            return ResponseEntity.status(403).body("Order is not pending for logistic approval");
        }

        LogisticsDetails findOrder = logisticsDetailsRepository.findByOrder_OrderId(order.getOrderId());

        //Logistic Details table data update
        findOrder.setPdiAction("PDI PASS");
        findOrder.setActionTime(LocalDateTime.now());
        findOrder.setLogisticsPdiComment(pdiComments.getLogisticsPdiComment());
        logisticsDetailsRepository.save(findOrder);

        //Order table status update
        order.setStatus("LOGISTIC > FINANCE CLOSURE PENDING");
        orderRepository.save(order);

        return ResponseEntity.ok("PDI Details Submit Successfully");
    }

    @Override
    public ResponseEntity<?> fillFailPdiDetails(String username, Long orderId, LogisticsDetails pdiComments)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("LOGISTIC"))
        {
            return ResponseEntity.status(403).body("Only logistic team can approve orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("PDI PENDING"))
        {
            return ResponseEntity.status(403).body("Order is not pending for logistic approval");
        }

        LogisticsDetails findOrder = logisticsDetailsRepository.findByOrder_OrderId(order.getOrderId());

        //Logistic Details table data update
        findOrder.setPdiAction("PDI FAIL");
        findOrder.setActionTime(LocalDateTime.now());
        findOrder.setLogisticsPdiComment(pdiComments.getLogisticsPdiComment());
        logisticsDetailsRepository.save(findOrder);

        //Order table status update
        order.setStatus("POST PDI FAIL RETURN AMISP");
        orderRepository.save(order);

        return ResponseEntity.ok("PDI Details Submit Successfully");
    }

    @Override
    public ResponseEntity<?> getCompleteOrdersForLogistics(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("LOGISTIC"))
        {
            return ResponseEntity.status(403).body("Only logistic team can view complete orders");
        }

        List<LogisticsDetails> logisticsDetailsList = logisticsDetailsRepository.findByLogisticActionIsNotNull(limit, offset);

        if (logisticsDetailsList.isEmpty())
        {
            return ResponseEntity.ok("No Orders found");
        }
        List<LogisticOrdersHistoryDto> logisticOrdersHistoryDtoList = logisticsDetailsList.stream()
                .map(approval -> ordersCompleteMapper.logisticOrderHistoryDto(
                        approval.getOrder(), approval))
                .toList();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", logisticsDetailsRepository.countByLogisticAction(),
                "orders", logisticOrdersHistoryDtoList
        ));
    }
}
