package com.project.inventory_management_system.service;

import com.project.inventory_management_system.dto.ProductTypeDto;
import com.project.inventory_management_system.entity.ProductType;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.mapper.ProductTypeMapper;
import com.project.inventory_management_system.repository.ProductTypeRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductTypeServiceImpl implements ProductTypeService
{
    private final UsersRepository usersRepository;
    private final ProductTypeMapper productTypeMapper;
    private final ProductTypeRepository productTypeRepository;

    @Override
    public ResponseEntity<?> addProductType(String username, ProductTypeDto productTypeDto)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if(!user.getDepartment().getDepartmentName().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only Admin can Add Product Type");
        }

        //Dto to Entity
        ProductType productType = productTypeMapper.toEntity(productTypeDto);
        productType.setUsers(user);
        productTypeRepository.save(productType);

        return ResponseEntity.ok("Saved Successfully");
    }

    @Override
    public ResponseEntity<?> updateProductType(String username, Long id, ProductTypeDto productTypeDto)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if(!user.getDepartment().getDepartmentName().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only Admin can Add Product Type");
        }

        ProductType productType = productTypeRepository.findByIdAndUsers(id, user);

        if (productType == null)
        {
            return ResponseEntity.ok("Product Type not found");
        }

        productType.setProductType(productTypeDto.getProductType());

        productTypeRepository.save(productType);

        return ResponseEntity.ok("Updated Successfully");
    }

    @Override
    public ResponseEntity<?> deleteProductType(String username, Long id)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if(!user.getDepartment().getDepartmentName().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only Admin can Delete Product Type");
        }

        ProductType productType = productTypeRepository.findByIdAndUsers(id, user);

        if (productType == null)
        {
            return ResponseEntity.ok("Product Type not found");
        }

        productTypeRepository.deleteById(productType.getId());

        return ResponseEntity.ok("Details Deleted Successfully");
    }

    @Override
    public ResponseEntity<?> getProductType(String username)
    {
        Users user = usersRepository.findByUsername(username);

        if (user == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }

        if(!user.getDepartment().getDepartmentName().equalsIgnoreCase("ADMIN"))
        {
            return ResponseEntity.status(403).body("Only Admin can Delete Product Type");
        }
        List<ProductType> productTypes = productTypeRepository.findAll();

        if (productTypes.isEmpty())
        {
            return ResponseEntity.ok("Product Type not found");
        }

        List<ProductTypeDto> productTypeDtoList = productTypes.stream()
                .map(productTypeMapper::toDto)
                .toList();

        return ResponseEntity.ok(productTypeDtoList);
    }
}
