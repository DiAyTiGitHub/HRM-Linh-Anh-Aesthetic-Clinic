package com.globits.hr.dto;


import com.globits.hr.domain.Asset;

import java.util.UUID;
import java.util.Date;

public class AssetDto {
    private UUID id;
    private ProductDto product;
    private StaffDto staff;
    private Date startDate;
    private Date endDate;
    private String note;
    private String errorMessage;

    public AssetDto() {

    }

    public AssetDto(Asset entity) {
        this.id = entity.getId();
        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();
        this.note = entity.getNote();
        if (entity.getStaff() != null) {
            this.staff = new StaffDto(
                    entity.getStaff().getId(),
                    entity.getStaff().getStaffCode(),
                    entity.getStaff().getDisplayName(),
                    entity.getStaff().getGender()
            );
        }
        if (entity.getProduct() != null) {
            this.product = new ProductDto(entity.getProduct(), false);
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
