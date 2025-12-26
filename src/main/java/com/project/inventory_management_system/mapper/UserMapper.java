package com.project.inventory_management_system.mapper;

import com.project.inventory_management_system.dto.UserDto;
import com.project.inventory_management_system.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper
{

    //Entity → DTO
    public UserDto toDto(Users user)
    {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setDepartmentId(user.getDepartment().getId());
        dto.setDepartmentName(user.getDepartment().getDepartmentName());

        return dto;
    }

    //DTO → Entity
    public Users toEntity(UserDto dto)
    {
        if (dto == null) return null;

        Users user = new Users();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());

        return user;
    }
}
