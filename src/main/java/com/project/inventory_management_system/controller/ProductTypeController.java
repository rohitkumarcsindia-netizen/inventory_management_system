package com.project.inventory_management_system.controller;

import com.project.inventory_management_system.dto.ProductTypeDto;
import com.project.inventory_management_system.service.ProductTypeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/product-types")
public class ProductTypeController 
{
    private final ProductTypeService productTypeService;

    @PostMapping
    public ResponseEntity<?> addProductType(HttpServletRequest request, @RequestBody ProductTypeDto productTypeDto)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return productTypeService.addProductType(userDetails.getUsername(), productTypeDto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateProductType(HttpServletRequest request, @PathVariable Long id, @RequestBody ProductTypeDto productTypeDto)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return productTypeService.updateProductType(userDetails.getUsername(), id, productTypeDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductType(HttpServletRequest request, @PathVariable Long id)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return productTypeService.deleteProductType(userDetails.getUsername(), id);
    }

    @GetMapping
    public ResponseEntity<?> getProductType(HttpServletRequest request)
    {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null)
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return productTypeService.getProductType(userDetails.getUsername());
    }
}
