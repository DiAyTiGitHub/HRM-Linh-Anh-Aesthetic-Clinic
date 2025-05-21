package com.globits.hr.dto.staff;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffInsuranceHistory;
import com.globits.hr.domain.StaffSocialInsurance;
import com.globits.hr.dto.StaffDto;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.dto.SalaryResultDto;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

/**
 * Quá trình đóng bảo hiểm xã hội
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class StaffInsuranceHistoryDto extends BaseObjectDto {
    private static final Logger logger = LoggerFactory.getLogger(StaffInsuranceHistoryDto.class);
    private static final long serialVersionUID = 1L;

    private StaffDto staff; // Lịch sử của nhân viên nào

    private Date startDate;//Ngày bắt đầu mức đóng

    private Date endDate;//Ngày kết thúc mức đóng

    private String note;//Ghi chú

    private Double insuranceSalary;//Mức lương đóng bảo hiểm xã hội

    private Double staffPercentage;//Tỷ lệ cá nhân đóng bảo hiểm xã hội

    private Double orgPercentage;//Tỷ lệ đơn vị đóng bảo hiểm xã hội

    private Double staffInsuranceAmount;//Số tiền cá nhân đóng

    private Double orgInsuranceAmount;//Số tiền đơn vị đóng

    private String socialInsuranceBookCode;//Số sổ bảo hiểm xã hội

    public StaffInsuranceHistoryDto() {
    }

    public StaffInsuranceHistoryDto(StaffInsuranceHistory entity) {
        super(entity);
        if (entity == null) return;

        this.insuranceSalary = entity.getInsuranceSalary();

        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();

        this.staffPercentage = entity.getStaffPercentage();//Tỷ lệ cá nhân đóng bảo hiểm xã hội
        this.orgPercentage = entity.getOrgPercentage();//Tỷ lệ đơn vị đóng bảo hiểm xã hội
        this.staffInsuranceAmount = entity.getStaffInsuranceAmount();//Số tiền cá nhân đóng
        this.orgInsuranceAmount = entity.getOrgInsuranceAmount();//Số tiền đơn vị đóng
        this.socialInsuranceBookCode = entity.getSocialInsuranceBookCode();//Số sổ bảo hiểm xã hội

        this.note = entity.getNote();
    }

    public StaffInsuranceHistoryDto(StaffInsuranceHistory entity, Boolean isGetFull) {
        this(entity);

        if (isGetFull == null || !isGetFull) return;


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

    public Double getInsuranceSalary() {
        return insuranceSalary;
    }

    public void setInsuranceSalary(Double insuranceSalary) {
        this.insuranceSalary = insuranceSalary;
    }

    public Double getStaffPercentage() {
        return staffPercentage;
    }

    public void setStaffPercentage(Double staffPercentage) {
        this.staffPercentage = staffPercentage;
    }

    public Double getOrgPercentage() {
        return orgPercentage;
    }

    public void setOrgPercentage(Double orgPercentage) {
        this.orgPercentage = orgPercentage;
    }

    public Double getStaffInsuranceAmount() {
        return staffInsuranceAmount;
    }

    public void setStaffInsuranceAmount(Double staffInsuranceAmount) {
        this.staffInsuranceAmount = staffInsuranceAmount;
    }

    public Double getOrgInsuranceAmount() {
        return orgInsuranceAmount;
    }

    public void setOrgInsuranceAmount(Double orgInsuranceAmount) {
        this.orgInsuranceAmount = orgInsuranceAmount;
    }

    public String getSocialInsuranceBookCode() {
        return socialInsuranceBookCode;
    }

    public void setSocialInsuranceBookCode(String socialInsuranceBookCode) {
        this.socialInsuranceBookCode = socialInsuranceBookCode;
    }
}
