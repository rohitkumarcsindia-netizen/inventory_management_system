package com.project.inventory_management_system.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDto
{
    private Long userId;
    private String username;
    private String email;
    private String password;
    private Long departmentId;
    private String departmentName;

}

