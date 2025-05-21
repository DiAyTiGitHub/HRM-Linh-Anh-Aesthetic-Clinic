package com.globits.hr.dto;

import com.globits.hr.domain.ProductAttribute;
import com.globits.core.dto.BaseObjectDto;

public class ProductAttributeDto extends BaseObjectDto{
    private ProductDto product;
    private String name;
    private String description;

    public ProductAttributeDto() {
    }

    public ProductAttributeDto(ProductAttribute entity) {
        if(entity != null) {
            this.id = entity.getId();
            this.product = new ProductDto(entity.getProduct(), false);
            this.name = entity.getName();
            this.description = entity.getDescription();
        }
    }

    public ProductAttributeDto(ProductAttribute entity, boolean isFull){
        if(entity != null) {
            this.id = entity.getId();
            this.product = isFull ? new ProductDto(entity.getProduct(), false) : null;
            this.name = entity.getName();
            this.description = entity.getDescription();
        }
    }

    public ProductAttributeDto(ProductDto product, String name, String description) {
        this.product = product;
        this.name = name;
        this.description = description;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
