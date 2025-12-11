package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.dto.OrdersDto;
import com.project.inventory_management_system.dto.ProjectTeamOrderDto;
import com.project.inventory_management_system.entity.ProjectTeamApproval;
import com.project.inventory_management_system.repository.OrderRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import com.project.inventory_management_system.service.ProjectOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api/orders/project")
public class ProjectOrdersController
{
    private final ProjectOrderService projectOrderService;
    private final UsersRepository usersRepository;
    private final OrderRepository orderRepository;

    @PostMapping("/create")
    public ResponseEntity<?> addNewOrders(HttpServletRequest request, @RequestBody OrdersDto ordersDto)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

            if (userDetails == null)
            {
                return ResponseEntity.status(401).body("Unauthorized");
            }

        return projectOrderService.createOrder(userDetails.getUsername(), ordersDto);


    }

    @GetMapping("/page")
    public ResponseEntity<?> getOrdersWithLimitOffset(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit)
    {

        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");


        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return projectOrderService.getOrdersByUserWithLimitOffset(userDetails.getUsername(), offset, limit);


        }

    @PutMapping("/update/{orderId}")
    public ResponseEntity<?> updateOrderDetails(HttpServletRequest request, @PathVariable Long orderId, @RequestBody OrdersDto ordersDto)
    {

        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return  projectOrderService.updateOrderDetails(userDetails.getUsername(), orderId, ordersDto);

    }
        @DeleteMapping("/delete/{orderId}")
        public ResponseEntity<?> deleteOrderDetails(HttpServletRequest request, @PathVariable Long orderId)
        {
            UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
            if (userDetails == null)
            {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            return projectOrderService.deleteOrder(userDetails.getUsername(), orderId);

        }

    //Search Filter
    @GetMapping("/date-filter")
    public ResponseEntity<?> getOrdersFilterDate(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23,59,59);

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        ResponseEntity<?> serviceResponse = projectOrderService.getOrdersFilterDate(userDetails.getUsername(), start, end,page,size);

       return serviceResponse;
    }
    @GetMapping("/status-filter")
    public ResponseEntity<?> getOrdersFilterStatus(
            HttpServletRequest request,
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return projectOrderService.getOrdersFilterStatus(userDetails.getUsername(), status, page, size);

    }

    //Universal search bar
    @GetMapping("/search")
    public ResponseEntity<?> getOrdersSearch(
            HttpServletRequest request,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return projectOrderService.getOrdersSearch(userDetails.getUsername(), keyword, page, size);

    }

    @PostMapping("/convey-amisp/{orderId}")
    public ResponseEntity<?> projectTeamNotifyConveyToAmisp(HttpServletRequest request, @PathVariable Long orderId, @RequestBody ProjectTeamApproval projectTeamApproval)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return projectOrderService.projectTeamNotifyConveyToAmisp(userDetails.getUsername(), orderId, projectTeamApproval);
    }

    @PutMapping("/post-delivery-pdi/{orderId}")
    public ResponseEntity<?> postDeliveryPdiOrder(HttpServletRequest request, @PathVariable Long orderId, @RequestBody ProjectTeamOrderDto pdiDetails)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return projectOrderService.postDeliveryPdiOrder(userDetails.getUsername(), orderId, pdiDetails);
    }

    @PutMapping("/pri-delivery-pdi/{orderId}")
    public ResponseEntity<?> priDeliveryPdiOrder(HttpServletRequest request, @PathVariable Long orderId,@RequestBody ProjectTeamOrderDto pdiDetails)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return projectOrderService.priDeliveryPdiOrder(userDetails.getUsername(), orderId, pdiDetails);
    }

    @PutMapping("/notify-scm/{orderId}")
    public ResponseEntity<?> projectTeamNotifyToScmDispatchOrderIsReady(HttpServletRequest request, @PathVariable Long orderId)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return projectOrderService.projectTeamNotifyToScmDispatchOrderIsReady(userDetails.getUsername(), orderId);
    }

    @PutMapping("/notify-scm-location-details/{orderId}")
    public ResponseEntity<?> projectTeamNotifyToScmLocationDetails(HttpServletRequest request, @PathVariable Long orderId, @RequestBody ProjectTeamApproval locationDetails)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return projectOrderService.projectTeamNotifyToScmLocationDetails(userDetails.getUsername(), orderId,locationDetails);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveOrders(HttpServletRequest request, @RequestBody OrdersDto ordersDto)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return projectOrderService.saveOrders(userDetails.getUsername(), ordersDto);

    }

    @PutMapping("/submit/{orderId}")
    public ResponseEntity<?> submitOrders(HttpServletRequest request, @PathVariable Long orderId, @RequestBody OrdersDto ordersDto)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return projectOrderService.submitOrders(userDetails.getUsername(), orderId, ordersDto);

    }
}
