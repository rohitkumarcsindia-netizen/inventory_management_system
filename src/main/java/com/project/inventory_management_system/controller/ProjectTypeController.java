package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.dto.ProjectTypeDto;
import com.project.inventory_management_system.service.ProjectTypeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/project-types")
public class ProjectTypeController
{
    private final ProjectTypeService projectTypeService;

    @PostMapping
    public ResponseEntity<?> addProjectType(HttpServletRequest request, @RequestBody ProjectTypeDto projectTypeDto)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return projectTypeService.addProjectType(userDetails.getUsername(), projectTypeDto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateProductType(HttpServletRequest request, @PathVariable Long id, @RequestBody ProjectTypeDto projectTypeDto)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return projectTypeService.updateProjectType(userDetails.getUsername(), id, projectTypeDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductType(HttpServletRequest request, @PathVariable Long id)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return projectTypeService.deleteProjectType(userDetails.getUsername(), id);
    }

    @GetMapping
    public ResponseEntity<?> getProductType(HttpServletRequest request)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return projectTypeService.getProjectType(userDetails.getUsername());
    }
}
