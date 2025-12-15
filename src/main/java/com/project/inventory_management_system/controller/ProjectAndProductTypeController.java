package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.dto.ProjectAndProductTypeDto;
import com.project.inventory_management_system.service.ProjectAndProductTypeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/project-product-types")
public class ProjectAndProductTypeController
{
    private final ProjectAndProductTypeService projectAndProductTypeService;

    @PostMapping
    public ResponseEntity<?> addProjectAndProductType(HttpServletRequest request, @RequestBody ProjectAndProductTypeDto projectAndProductTypeDto)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return projectAndProductTypeService.addProjectAndProductType(userDetails.getUsername(), projectAndProductTypeDto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateProjectAndProductType(HttpServletRequest request, @PathVariable Long id, @RequestBody ProjectAndProductTypeDto projectAndProductTypeDto)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return projectAndProductTypeService.updateProjectAndProductType(userDetails.getUsername(), id, projectAndProductTypeDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProjectAndProductType(HttpServletRequest request, @PathVariable Long id)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return projectAndProductTypeService.deleteProjectAndProductType(userDetails.getUsername(), id);
    }

    @GetMapping
    public ResponseEntity<?> getProjectAndProductType(HttpServletRequest request)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return projectAndProductTypeService.getProjectAndProductType(userDetails.getUsername());
    }
}
