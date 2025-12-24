package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.LogisticOrderDto;
import com.project.inventory_management_system.dto.LogisticOrdersHistoryDto;
import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.entity.*;
import com.project.inventory_management_system.enums.OrderStatus;
import com.project.inventory_management_system.mapper.LogisticOrderMapper;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.mapper.OrdersCompleteMapper;
import com.project.inventory_management_system.repository.ProjectTeamApprovalRepository;
import com.project.inventory_management_system.repository.LogisticsDetailsRepository;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    private final LogisticOrderMapper logisticOrderMapper;
    private final ProjectTeamApprovalRepository projectTeamApprovalRepository;
    private final OrderStatusByDepartmentService orderStatusByDepartmentService;


    @Override
    public ResponseEntity<?> getPendingOrdersForLogistic(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("LOGISTIC"))
        {
            return ResponseEntity.status(403).body("Only logistic team can view pending orders");
        }
        List<OrderStatus> logisticStatuses = orderStatusByDepartmentService.getStatusesByDepartment(user.getDepartment().getDepartmentName());

        List<Orders> orders = orderRepository. findByStatusWithLimitOffset(logisticStatuses, offset, limit);

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
                "ordersCount", orderRepository.countByStatus(logisticStatuses),
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

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("LOGISTIC"))
        {
            return ResponseEntity.status(403).body("Only logistic team can approve orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (order.getStatus() != OrderStatus.SCM_LOGISTIC_PENDING)
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
        order.setStatus(OrderStatus.DELIVERY_PENDING);
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

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("LOGISTIC"))
        {
            return ResponseEntity.status(403).body("Only logistic team can approve orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (order.getStatus() != OrderStatus.DELIVERY_PENDING)
        {
            return ResponseEntity.status(403).body("Order is not pending for logistic approval");
        }

        ProjectTeamApproval findPdiType = projectTeamApprovalRepository.findByOrder_OrderId(order.getOrderId());
        if (findPdiType.getAmispPdiType().equalsIgnoreCase("Pri-Delivery PDI"))
        {
            LogisticsDetails findOrder = logisticsDetailsRepository.findByOrder_OrderId(order.getOrderId());

            //Logistic Details table data update
            findOrder.setDeliveredStatus(deliveryDetails.getDeliveredStatus());
            findOrder.setActionTime(LocalDateTime.now());
            findOrder.setLogisticsComment(deliveryDetails.getLogisticsComment().trim());
            findOrder.setActualDeliveryDate(deliveryDetails.getActualDeliveryDate());
            findOrder.setActionTime(LocalDateTime.now());
            logisticsDetailsRepository.save(findOrder);

            //Order table status update
            order.setStatus(OrderStatus.LOGISTIC_FINANCE_CLOSURE_PENDING);
            orderRepository.save(order);

            return ResponseEntity.ok("Order Delivery Successfully");
        }

        LogisticsDetails findOrder = logisticsDetailsRepository.findByOrder_OrderId(order.getOrderId());

        //Logistic Details table data update
        findOrder.setDeliveredStatus(deliveryDetails.getDeliveredStatus());
        findOrder.setActionTime(LocalDateTime.now());
        findOrder.setLogisticsComment(deliveryDetails.getLogisticsComment().trim());
        findOrder.setActualDeliveryDate(deliveryDetails.getActualDeliveryDate());
        logisticsDetailsRepository.save(findOrder);

        //Order table status update
        order.setStatus(OrderStatus.PDI_PENDING);
        orderRepository.save(order);

        return ResponseEntity.ok("Order Delivery Successfully");
    }

//    @Override
//    public ResponseEntity<?> fillPassPdiDetails(String username, Long orderId, LogisticsDetails pdiComments)
//    {
//        Users user = usersRepository.findByUsername(username);
//
//        if (user == null)
//        {
//            return ResponseEntity.badRequest().body("User not found");
//        }
//
//        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("LOGISTIC"))
//        {
//            return ResponseEntity.status(403).body("Only logistic team can approve orders");
//        }
//
//        Orders order = orderRepository.findById(orderId).orElse(null);
//
//        if (order == null)
//        {
//            return ResponseEntity.ok("Order not found");
//        }
//
//        if (order.getStatus() != OrderStatus.PDI_PENDING)
//        {
//            return ResponseEntity.status(403).body("Order is not pending for logistic approval");
//        }
//
//        LogisticsDetails findOrder = logisticsDetailsRepository.findByOrder_OrderId(order.getOrderId());
//
//        //Logistic Details table data update
//        findOrder.setPdiAction("PDI PASS");
//        findOrder.setActionTime(LocalDateTime.now());
//        findOrder.setLogisticsPdiComment(pdiComments.getLogisticsPdiComment());
//        logisticsDetailsRepository.save(findOrder);
//
//        //Order table status update
//        order.setStatus(OrderStatus.LOGISTIC_FINANCE_CLOSURE_PENDING);
//        orderRepository.save(order);
//
//        return ResponseEntity.ok("PDI Details Submit Successfully");
//    }
//
//    @Override
//    public ResponseEntity<?> fillFailPdiDetails(String username, Long orderId, LogisticsDetails pdiComments)
//    {
//        Users user = usersRepository.findByUsername(username);
//
//        if (user == null)
//        {
//            return ResponseEntity.badRequest().body("User not found");
//        }
//
//        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("LOGISTIC"))
//        {
//            return ResponseEntity.status(403).body("Only logistic team can approve orders");
//        }
//
//        Orders order = orderRepository.findById(orderId).orElse(null);
//
//        if (order == null)
//        {
//            return ResponseEntity.ok("Order not found");
//        }
//
//        if (order.getStatus() != OrderStatus.PDI_PENDING)
//        {
//            return ResponseEntity.status(403).body("Order is not pending for logistic approval");
//        }
//
//        LogisticsDetails findOrder = logisticsDetailsRepository.findByOrder_OrderId(order.getOrderId());
//
//        //Logistic Details table data update
//        findOrder.setPdiAction("PDI FAIL");
//        findOrder.setActionTime(LocalDateTime.now());
//        findOrder.setLogisticsPdiComment(pdiComments.getLogisticsPdiComment());
//        logisticsDetailsRepository.save(findOrder);
//
//        //Order table status update
//        order.setStatus(OrderStatus.POST_PDI_FAIL_RETURN_AMISP);
//        orderRepository.save(order);
//
//        return ResponseEntity.ok("PDI Details Submit Successfully");
//    }

    @Override
    public ResponseEntity<?> getCompleteOrdersForLogistics(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("LOGISTIC"))
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

    @Override
    public ResponseEntity<?> getLogisticOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("LOGISTIC"))
        {
            return ResponseEntity.status(403).body("Only logistic team can view this");
        }

        List<OrderStatus> statuses = orderStatusByDepartmentService.getStatusesByDepartment(user.getDepartment().getDepartmentName());

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage = orderRepository.findByDateRange(start, end, statuses, pageable);
        if (ordersPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<OrdersDto> cloudOrderDtoList = ordersPage.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", ordersPage.getTotalElements(),
                "totalPages", ordersPage.getTotalPages(),
                "page", ordersPage.getNumber(),
                "size", ordersPage.getSize(),
                "records", cloudOrderDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getLogisticOrdersFilterStatus(String username, String status, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("LOGISTIC"))
        {
            return ResponseEntity.status(403).body("Only logistic team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage =  orderRepository.findByStatus(status, pageable);

        if (ordersPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<OrdersDto> ordersDtoList = ordersPage.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", ordersPage.getTotalElements(),
                "totalPages", ordersPage.getTotalPages(),
                "page", ordersPage.getNumber(),
                "size", ordersPage.getSize(),
                "records", ordersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getOrdersSearchForLogistic(String username, String keyword, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("LOGISTIC"))
        {
            return ResponseEntity.status(403).body("Only logistic team can view this");
        }

        List<OrderStatus> departmentNameWiseStatus = orderStatusByDepartmentService.getStatusesByDepartment(user.getDepartment().getDepartmentName());

        Specification<Orders> spec = Specification.allOf(OrderSpecification.statusIn(departmentNameWiseStatus)).and(OrderSpecification.keywordSearch(keyword));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage = orderRepository.findAll(spec, pageable);

        if (ordersPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<OrdersDto> OrderDtoList = ordersPage.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", ordersPage.getTotalElements(),
                "totalPages", ordersPage.getTotalPages(),
                "page", ordersPage.getNumber(),
                "size", ordersPage.getSize(),
                "records", OrderDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getLogisticCompleteOrdersFilterDate(String username, LocalDateTime start, LocalDateTime end, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("LOGISTIC"))
        {
            return ResponseEntity.status(403).body("Only logistic team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("actionTime").descending());
        Page<LogisticsDetails> logisticsDetailsPage = logisticsDetailsRepository.findByDateRange(start, end, pageable);
        if (logisticsDetailsPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<LogisticOrderDto> logisticOrderDtoList = logisticsDetailsPage.stream()
                .map(logisticOrderMapper::logisticOrdersDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", logisticsDetailsPage.getTotalElements(),
                "totalPages", logisticsDetailsPage.getTotalPages(),
                "page", logisticsDetailsPage.getNumber(),
                "size", logisticsDetailsPage.getSize(),
                "records", logisticOrderDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getLogisticCompleteOrdersFilterStatus(String username, String status, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("LOGISTIC"))
        {
            return ResponseEntity.status(403).body("Only logistic team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("actionTime").descending());
        Page<LogisticsDetails> logisticsDetailsPage =  logisticsDetailsRepository.findByStatusFilter(status, pageable);

        if (logisticsDetailsPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<LogisticOrderDto> logisticOrderDtoList = logisticsDetailsPage.stream()
                .map(logisticOrderMapper::logisticOrdersDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", logisticsDetailsPage.getTotalElements(),
                "totalPages", logisticsDetailsPage.getTotalPages(),
                "page", logisticsDetailsPage.getNumber(),
                "size", logisticsDetailsPage.getSize(),
                "records", logisticOrderDtoList
        ));
    }

    @Override
    public ResponseEntity<?> getLogisticCompleteOrdersFilterSearch(String username, String keyword, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("LOGISTIC"))
        {
            return ResponseEntity.status(403).body("Only logistic team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("actionTime").descending());
        Page<LogisticsDetails> logisticsDetailsPage = logisticsDetailsRepository.searchLogisticComplete(keyword.trim(), pageable);

        if (logisticsDetailsPage.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<LogisticOrderDto> logisticOrderDtoList = logisticsDetailsPage.stream()
                .map(logisticOrderMapper::logisticOrdersDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalElements", logisticsDetailsPage.getTotalElements(),
                "totalPages", logisticsDetailsPage.getTotalPages(),
                "page", logisticsDetailsPage.getNumber(),
                "size", logisticsDetailsPage.getSize(),
                "records", logisticOrderDtoList
        ));
    }
}
