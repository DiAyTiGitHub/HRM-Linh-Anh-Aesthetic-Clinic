package com.globits.hr.dto;

import com.globits.budget.dto.BaseNameCodeObjectDto;
import com.globits.core.domain.FileDescription;
import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.FileDescriptionDto;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffSignature;

public class StaffSignatureDto extends BaseNameCodeObjectDto {
    private StaffDto staff;
//    private String signature;

    private FileDescriptionDto file; // Tài liệu đã được lưu

    public StaffSignatureDto() {
    }

    public StaffSignatureDto(StaffSignature entity) {
        super(entity);
        if (entity != null) {
            this.id = entity.getId();
//            this.signature = entity.getSignature();
            if (entity.getFile() != null) {
                this.file = new FileDescriptionDto(entity.getFile());
            }
            if (entity.getStaff() != null) {
                this.staff = new StaffDto(entity.getStaff(), false);
            }
        }
    }


    public StaffSignatureDto(StaffSignature entity, Boolean getAll) {
        super(entity);
        if (entity != null) {
            this.id = entity.getId();
//            this.signature = entity.getSignature();
            if (entity.getFile() != null) {
                this.file = new FileDescriptionDto(entity.getFile());
            }
            if (getAll) {
                if (entity.getStaff() != null) {
                    this.staff = new StaffDto(entity.getStaff(), false, false);
                }
            }
        }
    }

    public FileDescriptionDto getFile() {
        return file;
    }

    public void setFile(FileDescriptionDto file) {
        this.file = file;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

//    public String getSignature() {
//        return signature;
//    }
//
//    public void setSignature(String signature) {
//        this.signature = signature;
//    }
}
