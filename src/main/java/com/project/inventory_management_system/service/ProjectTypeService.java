package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.ProjectTypeDto;
import org.springframework.http.ResponseEntity;

public interface ProjectTypeService
{
    ResponseEntity<?> addProjectType(String username, ProjectTypeDto projectTypeDto);

    ResponseEntity<?> updateProjectType(String username, Long id, ProjectTypeDto projectTypeDto);

    ResponseEntity<?> deleteProjectType(String username, Long id);

    ResponseEntity<?> getProjectType(String username);
}
