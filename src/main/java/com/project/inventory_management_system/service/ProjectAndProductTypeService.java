package com.project.inventory_management_system.service;


import com.project.inventory_management_system.dto.ProjectAndProductTypeDto;
import org.springframework.http.ResponseEntity;

public interface ProjectAndProductTypeService 
{
    ResponseEntity<?> addProjectAndProductType(String username, ProjectAndProductTypeDto projectAndProductTypeDto);

    ResponseEntity<?> updateProjectAndProductType(String username, Long id, ProjectAndProductTypeDto projectAndProductTypeDto);

    ResponseEntity<?> deleteProjectAndProductType(String username, Long id);

    ResponseEntity<?> getProjectAndProductType(String username);
}
