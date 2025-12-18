package com.project.inventory_management_system.mapper;

import com.project.inventory_management_system.dto.ProductTypeDto;
import com.project.inventory_management_system.entity.ProductType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductTypeMapper 
{
    private final UserMapper userMapper;

    //Dto to Entity
    public ProductType toEntity(ProductTypeDto productTypeDto)
    {
        if (productTypeDto == null) return null;

        ProductType prodcutType = new ProductType();
        prodcutType.setProductType(productTypeDto.getProductType().toUpperCase());
        prodcutType.setUsers(userMapper.toEntity(productTypeDto.getCreatedBy()));

        return prodcutType;
    }

    //Entity to Dto
    public ProductTypeDto toDto(ProductType productType)
    {
        if (productType == null) return null;

        ProductTypeDto prodcutTypeDto = new ProductTypeDto();
        prodcutTypeDto.setId(productType.getId());
        prodcutTypeDto.setProductType(productType.getProductType());
        prodcutTypeDto.setCreatedBy(userMapper.toDto(productType.getUsers()));

        return prodcutTypeDto;
    }
}
