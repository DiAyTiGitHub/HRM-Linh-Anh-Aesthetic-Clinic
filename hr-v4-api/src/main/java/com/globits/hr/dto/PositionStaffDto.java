package com.globits.hr.dto;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import com.globits.hr.domain.HRDepartment;
import com.globits.hr.domain.PositionStaff;
import com.globits.hr.domain.PositionTitle;
import com.globits.hr.domain.Staff;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globits.core.dto.BaseObjectDto;

public class PositionStaffDto extends BaseObjectDto {
    private static final Logger logger = LoggerFactory.getLogger(PositionStaffDto.class);
    private Date fromDate;
    private Date toDate;

    private StaffDto staff;
    private PositionDto position;

    /*
     * Vị trí cho đơn vị cụ thể nào đó - nếu chỉ có vị trí nhưng không thuộc đơn vị nào
     * thì trường này bằng null
     */
    private HRDepartmentDto hrDepartment;//phòng ban
    private StaffDto supervisor;//người quản lý
    private Integer relationshipType;
    private Boolean mainPosition;

    public PositionStaffDto() {

    }

    public PositionStaffDto(PositionStaff entity) {
        if (entity == null) {
            return;
        }
        this.mainPosition = entity.getMainPosition();
        this.relationshipType = entity.getRelationshipType();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        if (entity.getFromDate() != null) {
            this.fromDate = entity.getFromDate();
        }
        if (entity.getToDate() != null) {
            this.toDate = entity.getToDate();
        }
        this.setId(entity.getId());

        if (entity.getHrDepartment() != null) {
            this.hrDepartment = new HRDepartmentDto(entity.getHrDepartment());
        }

        if (entity.getPosition() != null) {
            this.position = new PositionDto(entity.getPosition());
        }

        if (entity.getStaff() != null) {
            this.staff = new StaffDto(entity.getStaff(), false, false);
        }
        if (entity.getSupervisor() != null) {
            this.supervisor = new StaffDto(entity.getSupervisor(), false, false);
        }
    }

    public Boolean getMainPosition() {
        return mainPosition;
    }

    public void setMainPosition(Boolean mainPosition) {
        this.mainPosition = mainPosition;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public PositionDto getPosition() {
        return position;
    }

    public void setPosition(PositionDto position) {
        this.position = position;
    }

    public HRDepartmentDto getHrDepartment() {
        return hrDepartment;
    }

    public void setHrDepartment(HRDepartmentDto hrDepartment) {
        this.hrDepartment = hrDepartment;
    }

    public StaffDto getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(StaffDto supervisor) {
        this.supervisor = supervisor;
    }

    public Integer getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(Integer relationshipType) {
        this.relationshipType = relationshipType;
    }
}
