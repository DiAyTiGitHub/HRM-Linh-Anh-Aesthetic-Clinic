package com.globits.hr.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.StaffSalaryHistory;
import com.globits.salary.dto.SalaryIncrementTypeDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaffSalaryHistoryDto extends BaseObjectDto {
    private static final Logger logger = LoggerFactory.getLogger(StaffSalaryHistoryDto.class);
    private StaffDto staff;
    private Double coefficient;
    private String staffTypeCode;
    private Double coefficientOverLevel;
    private Double percentage;
    private Date decisionDate;
    private String decisionCode;
    private SalaryIncrementTypeDto salaryIncrementType;
    private Date startDate;// Ngày bắt đầu thụ hưởng
    private Date nextSalaryIncrementDate;// Ngày dự kiến tăng lương tiếp theo
    private String note;// Ghi chú
    private Date startStaffTypeCodeDate;// Ngày bắt đầu mã ngạch công chức tương ứng với mức lương --> Dữ liệu mang tính
                                        // lịch sử
    // import
    private String staffCode;
    private String salaryIncrementTypeCode;

    public StaffSalaryHistoryDto() {

    }

    public StaffSalaryHistoryDto(StaffSalaryHistory entity) {
        if (entity != null) {
            this.setId(entity.getId());
            if (entity.getStaff() != null) {
                this.staff = new StaffDto(entity.getStaff(), false);
            }
            this.setCoefficient(entity.getCoefficient());
            this.setStaffTypeCode(entity.getStaffTypeCode());
            this.setCoefficientOverLevel(entity.getCoefficientOverLevel());
            this.setPercentage(entity.getPercentage());
            this.setDecisionCode(entity.getDecisionCode());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            try {
                if (entity.getDecisionDate() != null) {
                    if (entity.getDecisionDate().before(sdf.parse("01-01-1900"))
                            || entity.getDecisionDate().after(sdf.parse("01-01-2100"))) {
                        this.decisionDate = null;
                    } else {
                        this.setDecisionDate(entity.getDecisionDate());
                    }
                }
            } catch (Exception e) {
                logger.error("ERROR : {}", e.getMessage(), e);
            }
            if (entity.getSalaryIncrementType() != null) {
                this.salaryIncrementType = new SalaryIncrementTypeDto(entity.getSalaryIncrementType());
            }
        }
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getNextSalaryIncrementDate() {
        return nextSalaryIncrementDate;
    }

    public void setNextSalaryIncrementDate(Date nextSalaryIncrementDate) {
        this.nextSalaryIncrementDate = nextSalaryIncrementDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getStartStaffTypeCodeDate() {
        return startStaffTypeCodeDate;
    }

    public void setStartStaffTypeCodeDate(Date startStaffTypeCodeDate) {
        this.startStaffTypeCodeDate = startStaffTypeCodeDate;
    }

    public String getSalaryIncrementTypeCode() {
        return salaryIncrementTypeCode;
    }

    public void setSalaryIncrementTypeCode(String salaryIncrementTypeCode) {
        this.salaryIncrementTypeCode = salaryIncrementTypeCode;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public String getStaffTypeCode() {
        return staffTypeCode;
    }

    public void setStaffTypeCode(String staffTypeCode) {
        this.staffTypeCode = staffTypeCode;
    }

    public Double getCoefficientOverLevel() {
        return coefficientOverLevel;
    }

    public void setCoefficientOverLevel(Double coefficientOverLevel) {
        this.coefficientOverLevel = coefficientOverLevel;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    public Date getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
        this.decisionDate = decisionDate;
    }

    public String getDecisionCode() {
        return decisionCode;
    }

    public void setDecisionCode(String decisionCode) {
        this.decisionCode = decisionCode;
    }

    public SalaryIncrementTypeDto getSalaryIncrementType() {
        return salaryIncrementType;
    }

    public void setSalaryIncrementType(SalaryIncrementTypeDto salaryIncrementType) {
        this.salaryIncrementType = salaryIncrementType;
    }

}
