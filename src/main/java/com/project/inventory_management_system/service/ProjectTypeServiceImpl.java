package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.ProjectTypeDto;
import com.project.inventory_management_system.entity.ProjectType;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.mapper.ProjectTypeMapper;
import com.project.inventory_management_system.repository.ProjectTypeRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectTypeServiceImpl implements ProjectTypeService
{
    private final UsersRepository usersRepository;
    private final ProjectTypeMapper projectTypeMapper;
    private final ProjectTypeRepository projectTypeRepository;


    @Override
    public ResponseEntity<?> addProjectType(String username, ProjectTypeDto projectTypeDto)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if(!user.getDepartment().getDepartmentName().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only Admin can Add Project Type");
        }

        //Dto to Entity
        ProjectType projectType = projectTypeMapper.toEntity(projectTypeDto);
        projectType.setUsers(user);
        projectTypeRepository.save(projectType);

        return ResponseEntity.ok("Saved Successfully");
    }

    @Override
    public ResponseEntity<?> updateProjectType(String username, Long id, ProjectTypeDto projectTypeDto)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if(!user.getDepartment().getDepartmentName().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only Admin can Add Project Type");
        }

        ProjectType projectType = projectTypeRepository.findByIdAndUsers(id, user);

        if (projectType == null)
        {
            return ResponseEntity.ok("Project Type not found");
        }

        projectType.setProjectType(projectTypeDto.getProjectType());

        projectTypeRepository.save(projectType);

        return ResponseEntity.ok("Updated Successfully");
    }

    @Override
    public ResponseEntity<?> deleteProjectType(String username, Long id)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if(!user.getDepartment().getDepartmentName().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only Admin can Delete Project Type");
        }

        ProjectType projectType = projectTypeRepository.findByIdAndUsers(id, user);

        if (projectType == null)
        {
            return ResponseEntity.ok("Project Type not found");
        }

        projectTypeRepository.deleteById(projectType.getId());

        return ResponseEntity.ok("Details Deleted Successfully");
    }

    @Override
    public ResponseEntity<?> getProjectType(String username)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if(!user.getDepartment().getDepartmentName().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only Admin can Delete Project Type");
        }
        List<ProjectType> projectType = projectTypeRepository.findAll();

        if (projectType.isEmpty())
        {
            return ResponseEntity.ok("Project Type not found");
        }

        List<ProjectTypeDto> projectTypeDtoList = projectType.stream()
                .map(projectTypeMapper::toDto)
                .toList();

        return ResponseEntity.ok(projectTypeDtoList);
    }
}
