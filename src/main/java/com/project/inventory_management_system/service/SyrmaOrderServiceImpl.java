package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.dto.ScmOrdersHistoryDto;
import com.project.inventory_management_system.dto.SyrmaOrdersDto;
import com.project.inventory_management_system.dto.SyrmaOrdersHistoryDto;
import com.project.inventory_management_system.entity.Department;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.SyrmaApproval;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.mapper.OrdersCompleteMapper;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SyrmaOrderServiceImpl implements SyrmaOrderService
{

    private final UsersRepository usersRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final DepartmentRepository departmentRepository;
    private final EmailService emailService;
    private final OrdersCompleteMapper ordersCompleteMapper;


    @Override
    public ResponseEntity<?> getPendingOrdersForSyrma(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA"))
        {
            return ResponseEntity.badRequest().body("Only syrma team can view approved orders");
        }

        List<Orders> orders = orderRepository.findByStatusWithLimitOffset("SYRMA_PENDING", offset, limit);

        List<OrdersDto> list = orders.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<?> startProduction(String username, Long orderId)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA"))
        {
            return ResponseEntity.badRequest().body("Only syrma team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.badRequest().body("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("SYRMA_PENDING"))
        {
            return ResponseEntity.badRequest().body("Order is not ready for production start");
        }

        order.setStatus("PRODUCTION STARTED");
        orderRepository.save(order);

        return ResponseEntity.ok("Production started successfully");
    }

    @Override
    public ResponseEntity<?> getPendingTestingOrders(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA"))
        {
            return ResponseEntity.badRequest().body("Only SCM team can view approved orders");
        }

        List<Orders> orders = orderRepository.findByStatusWithLimitOffset("PRODUCTION STARTED", offset, limit);

        List<OrdersDto> list = orders.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(list);
    }

//    @Override
//    public ResponseEntity<?> testingComplete(String username, Long orderId, SyrmaOrdersDto syrmaOrdersDto)
//    {
//        Users user = usersRepository.findByUsername(username);
//
//        if (user == null)
//        {
//            return ResponseEntity.badRequest().body("User not found");
//        }
//
//        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA"))
//        {
//            return ResponseEntity.badRequest().body("Only syrma team can view complete orders");
//        }
//
//        Orders order = orderRepository.findById(orderId).orElse(null);
//
//        if (order == null)
//        {
//            return ResponseEntity.badRequest().body("Order not found");
//        }
//
//        if (!order.getStatus().equalsIgnoreCase("PRODUCTION STARTED"))
//        {
//            return ResponseEntity.badRequest().body("Jira details can only be submitted when the order is pending for SCM action");
//        }
//
//        SyrmaApproval syrmaApproval = new SyrmaApproval();
//        syrmaApproval.setSyrmaAction(syrmaOrdersDto.getSyrmaAction());
//        syrmaApproval.setActionTime(LocalDateTime.now());
//        syrmaApproval.setSyrmaComments(syrmaOrdersDto.getSyrmaComments());
//        syrmaApproval.setActionDoneBy(user.getUserId());
//        order.setStatus("TESTING_COMPLETED");
//        orderRepository.save(order);
//
//        Department department = departmentRepository.findByDepartmentname("SCM");
//
//        boolean mailsent = emailService.sendMailOrderApprove(department.getDepartmentEmail(), order.getOrderId());
//
//        if (!mailsent)
//        {
//            return ResponseEntity.status(500).body("Mail Not Sent");
//        }
//
//
//        return ResponseEntity.ok("Testing Completed successfully");
//
//    }
//
//    @Override
//    public ResponseEntity<?> getCompleteOrdersForSyrma(String username, int offset, int limit)
//    {
//        Users user = usersRepository.findByUsername(username);
//
//        if (user == null)
//        {
//            return ResponseEntity.badRequest().body("User not found");
//        }
//
//        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA"))
//        {
//            return ResponseEntity.badRequest().body("Only Syrma team can view complete orders");
//        }
//
//        List<Orders> orders = orderRepository.findByScmActionIsNotNull(offset, limit);
//
//        List<ScmOrdersHistoryDto> list = orders.stream()
//                .map(ordersCompleteMapper::scmOrdersHistoryDto)
//                .toList();
//
//        return ResponseEntity.ok(list);
//    }
}
