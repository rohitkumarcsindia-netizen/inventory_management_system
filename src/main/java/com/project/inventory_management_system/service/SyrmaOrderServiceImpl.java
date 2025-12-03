package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.dto.SyrmaOrdersDto;
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
import java.util.Map;

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
            return ResponseEntity.ok("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA"))
        {
            return ResponseEntity.status(403).body("Only syrma team can view approved orders");
        }

        List<Orders> ordersList = orderRepository.findByStatusWithLimitOffset("SYRMA PENDING", offset, limit);

        if (ordersList.isEmpty())
        {
            return ResponseEntity.ok("No Orders found");
        }

        List<OrdersDto> ordersDtoList = ordersList.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.countByStatus("SYRMA PENDING"),
                "orders", ordersDtoList
        ));
    }

    @Override
    public ResponseEntity<?> productionAndTestingComplete(String username, Long orderId, SyrmaOrdersDto syrmaComments)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentname().equalsIgnoreCase("SYRMA"))
        {
            return ResponseEntity.status(403).body("Only syrma team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        if (!order.getStatus().equalsIgnoreCase("SYRMA PENDING"))
        {
            return ResponseEntity.status(403).body("Order is not ready for production start");
        }

        SyrmaApproval syrmaApproval = new SyrmaApproval();
        syrmaApproval.setOrder(order);
        syrmaApproval.setSyrmaAction("Completed");
        syrmaApproval.setActionTime(LocalDateTime.now());
        syrmaApproval.setActionDoneBy(user);
        syrmaApproval.setSyrmaComments(syrmaComments.getSyrmaComments().trim());

        //Order table status update
        order.setStatus("SYRMA > SCM RECHECK PENDING");
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentname("SCM");

        boolean mailsent = emailService.sendMailProductionAndTestingComplete(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.status(500).body("Mail Not Sent");
        }

        return ResponseEntity.ok("Production and Testing successfully");
    }

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
