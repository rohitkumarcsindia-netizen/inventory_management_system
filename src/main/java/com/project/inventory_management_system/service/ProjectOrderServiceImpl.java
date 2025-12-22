package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.dto.ProjectTeamOrderDto;
import com.project.inventory_management_system.dto.UserDto;
import com.project.inventory_management_system.entity.ProjectTeamApproval;
import com.project.inventory_management_system.entity.Department;
import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.enums.OrderStatus;
import com.project.inventory_management_system.mapper.OrderMapper;
import com.project.inventory_management_system.repository.ProjectTeamApprovalRepository;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectOrderServiceImpl implements ProjectOrderService
{
    private final OrderRepository orderRepository;
    private final UsersRepository usersRepository;
    private final OrderMapper orderMapper;
    private final EmailService emailService;
    private final DepartmentRepository departmentRepository;
    private final ProjectTeamApprovalRepository projectTeamApprovalRepository;

    //Project Team Order Created Method
    @Override
    public ResponseEntity<?> createOrder(String username, OrdersDto ordersDto)
    {
        Users user = usersRepository.findByUsername(username);


        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName()
                .equalsIgnoreCase("PROJECT TEAM"))
        {
            return ResponseEntity.status(403).body("This User not allowed create orders");
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

            if (orders.getOrderType().equalsIgnoreCase("PURCHASE"))
            {
                orders.setCreateAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
                orders.setStatus(OrderStatus.PROJECT_TEAM_SCM_PENDING);


                Orders saved = orderRepository.save(orders);

                Department scmTeam = departmentRepository.findByDepartmentName("SCM");

                //sending mail
                boolean mailSent = emailService.sendMailNextDepartmentOrderCreate(scmTeam.getDepartmentEmail(), saved.getOrderId());

                boolean mailSentPM = emailService.sendMailOrderConfirm(user.getDepartment().getDepartmentEmail(),saved.getOrderId());

                if (!mailSent && !mailSentPM)
                {
                    return ResponseEntity.ok("Order submitted but mail failed to send");
                }

                return ResponseEntity.ok("Order Created Successfully Submit");
            }


            orders.setCreateAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
            orders.setStatus(OrderStatus.PROJECT_TEAM_FINANCE_PRE_APPROVAL_PENDING);
            Orders saved = orderRepository.save(orders);

            Department financeTeam = departmentRepository.findByDepartmentName("FINANCE");

            //sending mail
            boolean mailSent = emailService.sendMailNextDepartmentOrderCreate(financeTeam.getDepartmentEmail(), saved.getOrderId());

            boolean mailSentPM = emailService.sendMailOrderConfirm(user.getDepartment().getDepartmentEmail(),saved.getOrderId());

            if (!mailSent && !mailSentPM)
            {
                return ResponseEntity.ok("Order submitted but mail failed to send");
            }

        return ResponseEntity.ok("Order Created Successfully Submit");
    }



    //Project Team Order Get Method
    @Override
    public ResponseEntity<?> getOrdersByUserWithLimitOffset(String username, int offset, int limit)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("PROJECT TEAM"))
        {
            return ResponseEntity.status(403).body("Only project team can view pending orders");
        }

        List<Orders> orders =  orderRepository.findOrdersByUserWithLimitOffset(user.getUserId(), offset, limit);

        if (orders.isEmpty())
        {
            return ResponseEntity.ok("No orders found");
        }

        List<OrdersDto> ordersDtoList = orders.stream()
                .map(orderMapper::toDto)
                .toList();

        return ResponseEntity.ok(Map.of(
                "offset", offset,
                "limit", limit,
                "ordersCount", orderRepository.countByUserId(user.getUserId()),
                "orders", ordersDtoList
        ));
    }


    //Project Team Order update Method
    @Override
    public ResponseEntity<?> updateOrderDetails(String username, Long orderId, OrdersDto ordersDto)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("PROJECT TEAM"))
        {
            return ResponseEntity.status(403).body("Only project team can update orders");
        }

        Orders order = orderRepository.findByOrderIdAndUsers(orderId, user);
        if (order == null)
        {
            return ResponseEntity.ok("Order not found or This user is not allowed to update order");
        }

        if (order.getStatus() != OrderStatus.PROJECT_TEAM_PENDING)
        {
            return ResponseEntity.status(403).body("Order is not pending for project team update");
        }

        // 🚀 Update only fields from DTO
        order.setCreateAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        order.setExpectedOrderDate(ordersDto.getExpectedOrderDate());
        order.setProject(ordersDto.getProject());
        order.setProductType(String.valueOf(ordersDto.getProductType()));
        order.setOrderType(ordersDto.getOrderType());
        order.setProposedBuildPlanQty(ordersDto.getProposedBuildPlanQty());
        order.setReasonForBuildRequest(ordersDto.getReasonForBuildRequest());
        order.setInitiator(ordersDto.getInitiator());
        order.setPmsRemarks(ordersDto.getPmsRemarks());
        order.setStatus(OrderStatus.PROJECT_TEAM_PENDING);

        // Save updated order
         orderRepository.save(order);

         return ResponseEntity.ok("Orders Details Update Successfully");
    }


    //Project Team Order delete Method
    @Override
    public ResponseEntity<?> deleteOrder(String username, Long orderId)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("PROJECT TEAM"))
        {
            return ResponseEntity.status(403).body("Only project team can delete orders");
        }

        Orders order = orderRepository.findByOrderIdAndUsers(orderId, user);
        if (order == null)
        {
            return ResponseEntity.ok("Order not found or This user is not allowed to delete order");
        }


        if (order.getStatus() != OrderStatus.PROJECT_TEAM_PENDING)
        {
            return ResponseEntity.status(403).body("This order cannot be deleted because it has already moved to the next department");
        }
        orderRepository.delete(order);

        return ResponseEntity.ok("Order deleted successfully");
    }

    @Override
    public ResponseEntity<?> getOrdersFilterDate(String username, LocalDateTime startDate, LocalDateTime endDate, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("PROJECT TEAM"))
        {
            return ResponseEntity.status(403).body("Only project team can view pending orders");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage =  orderRepository.findByOrderDateBetweenAndUser(startDate, endDate, user.getUserId(), pageable);

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
    public ResponseEntity<?> getOrdersFilterStatus(String username, String status,int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("PROJECT TEAM"))
        {
            return ResponseEntity.status(403).body("Only project team can view this");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage =  orderRepository.findByStatusAndUser(status, user.getUserId(),pageable);

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
    public ResponseEntity<?> getOrdersSearch(String username, String keyword, int page, int size)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("PROJECT TEAM"))
        {
            return ResponseEntity.status(403).body("Only project team can view this");
        }

        Specification<Orders> spec = Specification.allOf(OrderSpecification.hasUser(user.getUserId())).and(OrderSpecification.keywordSearch(keyword));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Orders> ordersPage =  orderRepository.findAll(spec,pageable);

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
    public ResponseEntity<?> projectTeamNotifyConveyToAmisp(String username, Long orderId, ProjectTeamApproval projectTeamApproval)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("PROJECT TEAM"))
        {
            return ResponseEntity.status(403).body("Only Project team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }


        if (order.getStatus() != OrderStatus.SCM_NOTIFY_PROJECT_TEAM_BUILD_IS_READY)
        {
            return ResponseEntity.status(403).body("Notify details can only be submitted when the order is pending for Project team action");
        }

        order.setStatus(OrderStatus.PROJECT_TEAM_NOTIFY_AMISP_PDI_TYPE_PENDING);
        orderRepository.save(order);

        ProjectTeamApproval amispEmailId = new ProjectTeamApproval();
        amispEmailId.setOrder(order);
        amispEmailId.setActionBy(user);
        amispEmailId.setProjectTeamActionTime(LocalDateTime.now());
        amispEmailId.setAmispEmailId(projectTeamApproval.getAmispEmailId());
        projectTeamApprovalRepository.save(amispEmailId);

        boolean mailsent = emailService.sendMailNotifyAmispPdiType(amispEmailId.getAmispEmailId(), order);

        if (!mailsent)
        {
            return ResponseEntity.ok("Mail Not Sent");
        }

        return ResponseEntity.ok("Notification sent for Amisp");
    }

    @Override
    public ResponseEntity<?> projectTeamNotifyToScmDispatchOrderIsReady(String username, Long orderId)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("PROJECT TEAM"))
        {
            return ResponseEntity.status(403).body("Only Project team can view complete orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }


        if (order.getStatus() != OrderStatus.PROJECT_TEAM_PROJECT_TEAM_READY_FOR_DISPATCH)
        {
            return ResponseEntity.status(403).body("Notify details can only be submitted when the order is pending for Project team action");
        }

        order.setStatus(OrderStatus.PROJECT_TEAM_SCM_READY_FOR_DISPATCH);
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentName("SCM");

        boolean mailsent = emailService.sendMailNotifyToScmDispatchOrderIsReady(department.getDepartmentEmail(), order.getOrderId());

        if (!mailsent)
        {
            return ResponseEntity.ok("Mail Not Sent");
        }

        return ResponseEntity.ok("Notification sent for SCM");
    }

    @Override
    public ResponseEntity<?> projectTeamNotifyToScmLocationDetails(String username, Long orderId, ProjectTeamApproval locationDetails)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("PROJECT TEAM"))
        {
            return ResponseEntity.status(403).body("Only Project Team can send location details");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);

        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }

        ProjectTeamApproval projectTeamApproval = projectTeamApprovalRepository.findByOrder_OrderId(order.getOrderId());

        if (projectTeamApproval == null)
        {
            return ResponseEntity.ok("Approval details not found for this order");
        }


        if (order.getStatus() != OrderStatus.SCM_NOTIFY_AMISP_READY_FOR_DISPATCH)
        {
            return ResponseEntity.status(403).body("Location details can only be submitted when the order is pending for SCM location update");
        }


        //Location details set Db
        projectTeamApproval.setLocationDetails(locationDetails.getLocationDetails());
        projectTeamApprovalRepository.save(projectTeamApproval);

        order.setStatus(OrderStatus.PROJECT_TEAM_NOTIFY_SCM_LOCATION_DETAILS);
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentName("SCM");

        boolean mailsent = emailService.sendMailNotifyProjectTeamSentLocationForScm(department.getDepartmentEmail(), order, projectTeamApproval);

        if (!mailsent)
        {
            return ResponseEntity.ok("Mail Not Sent");
        }

        return ResponseEntity.ok("Location details sent to SCM successfully");
    }

    @Override
    public ResponseEntity<?> saveOrders(String username, OrdersDto ordersDto)
    {
        Users user = usersRepository.findByUsername(username);


        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("PROJECT TEAM"))
        {
            return ResponseEntity.status(403).body("This User not allowed save orders");
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

        orders.setCreateAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        orders.setStatus(OrderStatus.PROJECT_TEAM_PENDING);

        orderRepository.save(orders);

        return ResponseEntity.ok("Order Saved Successfully");
    }

    @Override
    public ResponseEntity<?> submitOrders(String username, Long orderId, OrdersDto ordersDto)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("PROJECT TEAM"))
        {
            return ResponseEntity.status(403).body("Only project team can submit orders");
        }

        Orders order = orderRepository.findByOrderIdAndUsers(orderId, user);
        if (order == null)
        {
            return ResponseEntity.ok("Order not found or This user is not allowed to update order");
        }


        if (order.getStatus() != OrderStatus.PROJECT_TEAM_PENDING)
        {
            return ResponseEntity.status(403).body("Order is not pending for project team update");
        }

        if (order.getOrderType().equalsIgnoreCase("PURCHASE"))
        {
            order.setCreateAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
            order.setExpectedOrderDate(ordersDto.getExpectedOrderDate());
            order.setProject(ordersDto.getProject());
            order.setProductType(String.valueOf(ordersDto.getProductType()));
            order.setOrderType(ordersDto.getOrderType());
            order.setProposedBuildPlanQty(ordersDto.getProposedBuildPlanQty());
            order.setReasonForBuildRequest(ordersDto.getReasonForBuildRequest());
            order.setInitiator(user.getUsername());
            order.setPmsRemarks(ordersDto.getPmsRemarks());
            order.setStatus(OrderStatus.PROJECT_TEAM_SCM_PENDING);
//            order.setStatus("PROJECT TEAM > SCM PENDING");

            Orders saved = orderRepository.save(order);

            Department scmTeam = departmentRepository.findByDepartmentName("SCM");

            //sending mail
            boolean mailSent = emailService.sendMailNextDepartmentOrderCreate(scmTeam.getDepartmentEmail(), saved.getOrderId());

            boolean mailSentPM = emailService.sendMailOrderConfirm(user.getDepartment().getDepartmentEmail(),saved.getOrderId());

            if (!mailSent && !mailSentPM)
            {
                return ResponseEntity.ok("Order submitted but mail failed to send");
            }

            return ResponseEntity.ok("Order Submit Successfully");
        }

        order.setCreateAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        order.setExpectedOrderDate(ordersDto.getExpectedOrderDate());
        order.setProject(ordersDto.getProject());
        order.setProductType(String.valueOf(ordersDto.getProductType()));
        order.setOrderType(ordersDto.getOrderType());
        order.setProposedBuildPlanQty(ordersDto.getProposedBuildPlanQty());
        order.setReasonForBuildRequest(ordersDto.getReasonForBuildRequest());
        order.setInitiator(user.getUsername());
        order.setPmsRemarks(ordersDto.getPmsRemarks());
        order.setStatus(OrderStatus.PROJECT_TEAM_FINANCE_PRE_APPROVAL_PENDING);

        Orders saved = orderRepository.save(order);

        Department financeTeam = departmentRepository.findByDepartmentName("FINANCE");

        //sending mail
        boolean mailSent = emailService.sendMailNextDepartmentOrderCreate(financeTeam.getDepartmentEmail(), saved.getOrderId());

        boolean mailSentPM = emailService.sendMailOrderConfirm(user.getDepartment().getDepartmentEmail(),saved.getOrderId());

        if (!mailSent && !mailSentPM)
        {
            return ResponseEntity.ok("Order submitted but mail failed to send");
        }

        return ResponseEntity.ok("Order Submit Successfully");
    }

    @Override
    public ResponseEntity<?> postDeliveryPdiOrder(String username, Long orderId, ProjectTeamOrderDto pdiDetails)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("PROJECT TEAM"))
        {
            return ResponseEntity.status(403).body("Only project team can approve orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }


        if (order.getStatus() != OrderStatus.PROJECT_TEAM_NOTIFY_AMISP_PDI_TYPE_PENDING)
        {
            return ResponseEntity.status(403).body("Order is not pending for project approval");
        }

        //project team table update
        ProjectTeamApproval projectTeamApproval = projectTeamApprovalRepository.findByOrder_OrderId(order.getOrderId());

        projectTeamApproval.setAmispPdiType("Post-Delivery PDI");
        projectTeamApproval.setProjectTeamActionTime(LocalDateTime.now());
        projectTeamApproval.setProjectTeamComment(pdiDetails.getProjectTeamComment());
        projectTeamApproval.setSerialNumbers(pdiDetails.getSerialNumbers());
        projectTeamApproval.setDocumentUrl(pdiDetails.getDocumentUrl());
        projectTeamApproval.setDispatchDetails(pdiDetails.getDispatchDetails());
        projectTeamApproval.setPdiLocation(pdiDetails.getPdiLocation());
        projectTeamApprovalRepository.save(projectTeamApproval);

        //Order table status update
        order.setStatus(OrderStatus.PROJECT_TEAM_PROJECT_TEAM_READY_FOR_DISPATCH);
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentName("PROJECT TEAM");

        boolean mailsent = emailService.sendMailNotifyAmispToProjectTeam(department.getDepartmentEmail(), order.getOrderId(), projectTeamApproval);

        if (!mailsent)
        {
            return ResponseEntity.ok("Mail Not Sent");
        }

        return ResponseEntity.ok("Notification Sent For Project Team");
    }



    @Override
    public ResponseEntity<?> priDeliveryPdiOrder(String username, Long orderId, ProjectTeamOrderDto pdiDetails)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!user.getDepartment().getDepartmentName().equalsIgnoreCase("PROJECT TEAM"))
        {
            return ResponseEntity.status(403).body("Only project team can approve orders");
        }

        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order == null)
        {
            return ResponseEntity.ok("Order not found");
        }


        if (order.getStatus() != OrderStatus.PROJECT_TEAM_NOTIFY_AMISP_PDI_TYPE_PENDING)
        {
            return ResponseEntity.status(403).body("Order is not pending for project approval");
        }

        //project team table update
        ProjectTeamApproval projectTeamApproval = projectTeamApprovalRepository.findByOrder_OrderId(order.getOrderId());

        projectTeamApproval.setAmispPdiType("Pri-Delivery PDI");
        projectTeamApproval.setProjectTeamActionTime(LocalDateTime.now());
        projectTeamApproval.setProjectTeamComment(pdiDetails.getProjectTeamComment());
        projectTeamApproval.setSerialNumbers(pdiDetails.getSerialNumbers());
        projectTeamApproval.setDocumentUrl(pdiDetails.getDocumentUrl());
        projectTeamApproval.setDispatchDetails(pdiDetails.getDispatchDetails());
        projectTeamApproval.setPdiLocation(pdiDetails.getPdiLocation());
        projectTeamApprovalRepository.save(projectTeamApproval);

        //Order table status update
        order.setStatus(OrderStatus.PROJECT_TEAM_PROJECT_TEAM_READY_FOR_DISPATCH);
        orderRepository.save(order);

        Department department = departmentRepository.findByDepartmentName("PROJECT TEAM");

        boolean mailsent = emailService.sendMailNotifyAmispToProjectTeam(department.getDepartmentEmail(), order.getOrderId(), projectTeamApproval);

        if (!mailsent)
        {
            return ResponseEntity.ok("Mail Not Sent");
        }

        return ResponseEntity.ok("Notification Sent For Project Team");
    }

}


