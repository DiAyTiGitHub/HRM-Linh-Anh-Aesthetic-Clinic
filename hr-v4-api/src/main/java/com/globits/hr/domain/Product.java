package com.globits.hr.domain;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import com.globits.core.domain.BaseObject;


@Table(name = "tbl_product")
@Entity
public class Product extends BaseObject {
    private static final long serialVersionUID = 1L;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "model")
    private String model;// model tài sản

    @Column(name = "serial_number")
    private String serialNumber;// Số serial

    @Column(name = "manufacturer")
    private String manufacturer;// dang san xuat

    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private ProductType productType;// Loại hàng hóa

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "department_id")
    private HRDepartment department;// phong ban

    @OneToMany(mappedBy = "product", cascade= CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createDate asc")
    private Set<ProductAttribute> attributes = new HashSet<>();// thuoc tinh

    @Column(name = "price")
    private Double price;

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

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public HRDepartment getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartment department) {
        this.department = department;
    }

    public Set<ProductAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<ProductAttribute> attributes) {
        this.attributes = attributes;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}
