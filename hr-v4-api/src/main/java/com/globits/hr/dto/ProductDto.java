package com.globits.hr.dto;

import com.globits.hr.domain.Product;
import java.util.Set;
import java.util.stream.Collectors;
import com.globits.core.dto.BaseObjectDto;

public class ProductDto extends BaseObjectDto{
    private String code;
    private String name;
    private String description;
    private String model;
    private String serialNumber;
    private String manufacturer;
    private ProductTypeDto productType;
    private HRDepartmentDto department;
    private Set<ProductAttributeDto> attributes;
    private Double price;

    public ProductDto() {
    }

    public ProductDto(Product product) {
        if (product != null) {
            this.id = product.getId();
            this.code = product.getCode();
            this.name = product.getName();
            this.description = product.getDescription();
            this.model = product.getModel();
            this.serialNumber = product.getSerialNumber();
            this.manufacturer = product.getManufacturer();
            this.productType = product.getProductType() != null ? new ProductTypeDto(product.getProductType()) : null;
            this.department = product.getDepartment() != null ? new HRDepartmentDto(product.getDepartment()) : null;
            if (product.getAttributes() != null && !product.getAttributes().isEmpty()) {
                this.attributes = product.getAttributes().stream().map(e -> new ProductAttributeDto(e, false)).collect(Collectors.toSet());
            }
            this.price = product.getPrice();
        }

    }

    public ProductDto(Product product, Boolean isFull) {
        if (product != null) {
            this.id = product.getId();
            this.code = product.getCode();
            this.name = product.getName();
            this.description = product.getDescription();
            this.model = product.getModel();
            this.serialNumber = product.getSerialNumber();
            this.manufacturer = product.getManufacturer();
            this.productType = product.getProductType() != null ? new ProductTypeDto(product.getProductType()) : null;
            this.department = product.getDepartment() != null ? new HRDepartmentDto(product.getDepartment()) : null;
            if (product.getAttributes() != null && !product.getAttributes().isEmpty() && isFull) {
                this.attributes = product.getAttributes().stream().map(e -> new ProductAttributeDto(e, false)).collect(Collectors.toSet());
            }
            this.price = product.getPrice();
        }
    }

    public ProductDto(String code, String name, String description, String model, String serialNumber, String manufacturer, ProductTypeDto productType, HRDepartmentDto department, Set<ProductAttributeDto> attributes, Double price) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.model = model;
        this.serialNumber = serialNumber;
        this.manufacturer = manufacturer;
        this.productType = productType;
        this.department = department;
        this.attributes = attributes;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public ProductTypeDto getProductType() {
        return productType;
    }

    public void setProductType(ProductTypeDto productType) {
        this.productType = productType;
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    public Set<ProductAttributeDto> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<ProductAttributeDto> attributes) {
        this.attributes = attributes;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
