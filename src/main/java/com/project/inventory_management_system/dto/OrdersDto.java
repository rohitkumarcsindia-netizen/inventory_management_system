package com.project.inventory_management_system.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.inventory_management_system.enums.OrderStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;


@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OrdersDto
{
    private Long orderId;
    private LocalDateTime createAt;
    private java.time.LocalDate expectedOrderDate;
    private String project;
    private String initiator;
    private String productType;
    private Integer proposedBuildPlanQty;
    private List<ProductRequestDto> products;
    private String orderType;
    private String reasonForBuildRequest;
    private String status;
    private String pmsRemarks;

    private UserDto users;
}
