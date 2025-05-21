package com.globits.hr.dto;

import com.globits.hr.domain.StaffAllowanceHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globits.core.dto.BaseObjectDto;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StaffAllowanceHistoryDto extends BaseObjectDto {
    private static final Logger logger = LoggerFactory.getLogger(StaffAllowanceHistoryDto.class);
    private Date startDate;
    private Date endDate;
    private StaffDto staff;
    private AllowanceTypeDto allowanceType;
    private String note;
    private Double coefficient;

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

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public AllowanceTypeDto getAllowanceType() {
        return allowanceType;
    }

    public void setAllowanceType(AllowanceTypeDto allowanceType) {
        this.allowanceType = allowanceType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }
    public StaffAllowanceHistoryDto(){
    }
    public  StaffAllowanceHistoryDto (StaffAllowanceHistory entity){
        if (entity!= null){
            setId(entity.getId());
            this.note = entity.getNote();
            this.coefficient = entity.getCoefficient();
            if (entity.getStaff() != null){
                this.staff = new StaffDto(entity.getStaff(), false);
            }
            if (entity.getAllowanceType()!= null){
                this.allowanceType = new AllowanceTypeDto(entity.getAllowanceType());
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            try {
                if (entity.getStartDate() != null) {
                    if (entity.getStartDate().before(simpleDateFormat.parse("01-01-1900")) || entity.getStartDate().after(simpleDateFormat.parse("01-01-2100"))) {
                        this.startDate = null;
                    } else {
                        this.startDate = entity.getStartDate();
                    }
                }
                if (entity.getEndDate() != null) {
                    if (entity.getEndDate().before(simpleDateFormat.parse("01-01-1900")) || entity.getEndDate().after(simpleDateFormat.parse("01-01-2100"))) {
                        this.endDate = null;
                    } else {
                        this.endDate = entity.getEndDate();
                    }
                }
            }
            catch (Exception e){
                logger.error("ERROR : {}", e.getMessage(), e);
            }
        }
    }
}
