package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.ProductTypeDto;
import org.springframework.http.ResponseEntity;

public interface ProductTypeService
{
    ResponseEntity<?> addProductType(String username, ProductTypeDto productTypeDto);

    ResponseEntity<?> updateProductType(String username, Long id, ProductTypeDto productTypeDto);

    ResponseEntity<?> deleteProductType(String username, Long id);

    ResponseEntity<?> getProductType(String username);
}
