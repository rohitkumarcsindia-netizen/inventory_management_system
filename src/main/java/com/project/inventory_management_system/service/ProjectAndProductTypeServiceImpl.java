package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.ProjectAndProductTypeDto;
import com.project.inventory_management_system.entity.ProjectAndProductType;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.mapper.ProjectAndProductTypeMappers;
import com.project.inventory_management_system.repository.ProjectAndProductTypeRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectAndProductTypeServiceImpl implements ProjectAndProductTypeService
{
    private final UsersRepository usersRepository;
    private final ProjectAndProductTypeMappers projectAndProductTypeMappers;
    private final ProjectAndProductTypeRepository projectAndProductTypeRepository;


    @Override
    public ResponseEntity<?> addProjectAndProductType(String username, ProjectAndProductTypeDto projectAndProductTypeDto)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if(!user.getDepartment().getDepartmentname().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only Admin can Add Product and Project Type");
        }

        //Dto to Entity
        ProjectAndProductType projectAndProductType = projectAndProductTypeMappers.toEntity(projectAndProductTypeDto);
        projectAndProductType.setUsers(user);
        projectAndProductTypeRepository.save(projectAndProductType);

        return ResponseEntity.ok("Saved Successfully");
    }

    @Override
    public ResponseEntity<?> updateProjectAndProductType(String username, Long id, ProjectAndProductTypeDto projectAndProductTypeDto)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if(!user.getDepartment().getDepartmentname().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only Admin can Add Product and Project Type");
        }

        ProjectAndProductType projectAndProductType = projectAndProductTypeRepository.findByIdAndUsers(id, user);

        if (projectAndProductType == null)
        {
            return ResponseEntity.ok("Project/Product Type not found");
        }

        projectAndProductType.setProjectType(projectAndProductTypeDto.getProjectType());
        projectAndProductType.setProductType(projectAndProductTypeDto.getProductType());

        projectAndProductTypeRepository.save(projectAndProductType);

        return ResponseEntity.ok("Updated Successfully");
    }

    @Override
    public ResponseEntity<?> deleteProjectAndProductType(String username, Long id)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if(!user.getDepartment().getDepartmentname().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only Admin can Delete Product and Project Type");
        }

        ProjectAndProductType projectAndProductType = projectAndProductTypeRepository.findByIdAndUsers(id, user);

        if (projectAndProductType == null)
        {
            return ResponseEntity.ok("Project/Product Type not found");
        }

        projectAndProductTypeRepository.deleteById(projectAndProductType.getId());

        return ResponseEntity.ok("Details Deleted Successfully");
    }

    @Override
    public ResponseEntity<?> getProjectAndProductType(String username)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if(!user.getDepartment().getDepartmentname().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only Admin can Delete Product and Project Type");
        }
        List<ProjectAndProductType> projectAndProductType = projectAndProductTypeRepository.findAll();

        if (projectAndProductType.isEmpty())
        {
            return ResponseEntity.ok("Project/Product Type not found");
        }

        List<ProjectAndProductTypeDto> projectAndProductTypeDtoList = projectAndProductType.stream()
                .map(projectAndProductTypeMappers::toDto)
                .toList();

        return ResponseEntity.ok(projectAndProductTypeDtoList);
    }
}
