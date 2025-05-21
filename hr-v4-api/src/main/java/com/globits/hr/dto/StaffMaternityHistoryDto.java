package com.globits.hr.dto;

import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffMaternityHistory;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globits.core.dto.BaseObjectDto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class StaffMaternityHistoryDto extends BaseObjectDto {
    private static final Logger logger = LoggerFactory.getLogger(StaffMaternityHistoryDto.class);

    private UUID staffId;
    private StaffDto staff;
    // Ngày bắt đầu hưởng chế độ thai sản
    private Date startDate;
    // Ngày kết thúc hưởng chế độ thai san
    private Date endDate;
    // Ngày bắt đầu nghỉ thai sản
    private Date maternityLeaveStartDate;
    // Ngày kết thúc nghỉ thai sản
    private Date maternityLeaveEndDate;
    private Integer birthNumber;
    private String note;

    public StaffMaternityHistoryDto() {
    }

    public StaffMaternityHistoryDto(StaffMaternityHistory entity) {
        super(entity);

        if (entity == null) {
            return;
        }

        this.maternityLeaveStartDate = entity.getMaternityLeaveStartDate();
        this.maternityLeaveEndDate = entity.getMaternityLeaveEndDate();

        this.note = entity.getNote();
        this.birthNumber = entity.getBirthNumber();

        if (entity.getStaff() != null) {
            this.staff = new StaffDto(entity.getStaff(), false, false);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if (entity.getStartDate() != null) {
                if (entity.getStartDate().before(simpleDateFormat.parse("01-01-1900"))
                        || entity.getStartDate().after(simpleDateFormat.parse("01-01-2100"))) {
                    this.startDate = null;
                } else {
                    this.startDate = entity.getStartDate();
                }
            }
            if (entity.getEndDate() != null) {
                if (entity.getEndDate().before(simpleDateFormat.parse("01-01-1900"))
                        || entity.getEndDate().after(simpleDateFormat.parse("01-01-2100"))) {
                    this.endDate = null;
                } else {
                    this.endDate = entity.getEndDate();
                }
            }
        } catch (Exception e) {
            logger.error("ERROR : {}", e.getMessage(), e);
        }
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public Date getMaternityLeaveStartDate() {
        return maternityLeaveStartDate;
    }

    public void setMaternityLeaveStartDate(Date maternityLeaveStartDate) {
        this.maternityLeaveStartDate = maternityLeaveStartDate;
    }

    public Date getMaternityLeaveEndDate() {
        return maternityLeaveEndDate;
    }

    public void setMaternityLeaveEndDate(Date maternityLeaveEndDate) {
        this.maternityLeaveEndDate = maternityLeaveEndDate;
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

    public Integer getBirthNumber() {
        return birthNumber;
    }

    public void setBirthNumber(Integer birthNumber) {
        this.birthNumber = birthNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
