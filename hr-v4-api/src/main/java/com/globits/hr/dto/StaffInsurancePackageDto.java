package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.StaffInsurancePackage;

import java.util.Date;

public class StaffInsurancePackageDto extends BaseObjectDto {
    private static final long serialVersionUID = 991992518344617174L;

    private StaffDto staff;
    private InsurancePackageDto insurancePackage; // Đóng gói bảo hiểm nào
    private Date startDate; // Ngày bắt đầu đóng bảo hiểm
    private Date endDate; // Ngày kết thúc đóng bảo hiểm
    private Double insuranceAmount; // Mức tham gia bảo hiểm
    private Double compensationAmount; // Mức đền bù bảo hiểm
    private Double staffPercentage; // Tỷ lệ nhân viên đóng bảo hiểm
    private Double staffAmount;
    private Double orgPercentage; // Tỷ lệ công ty đóng bảo hiểm
    private Double orgAmount;
    private Boolean hasFamilyParticipation; // Có đóng cho thân nhân người lao động

    public StaffInsurancePackageDto() {
    }

    public StaffInsurancePackageDto(StaffInsurancePackage entity) {
        super(entity);

        if (entity == null) {
            return;
        }

        if (entity.getStaff() != null) {
            this.staff = new StaffDto();
            this.staff.setId(entity.getStaff().getId());
            this.staff.setStaffCode(entity.getStaff().getStaffCode());
            this.staff.setDisplayName(entity.getStaff().getDisplayName());
        }
        if (entity.getInsurancePackage() != null) {
            this.insurancePackage = new InsurancePackageDto(entity.getInsurancePackage());
        }

        this.startDate = entity.getStartDate(); // Ngày bắt đầu đóng bảo hiểm
        this.endDate = entity.getEndDate(); // Ngày kết thúc đóng bảo hiểm
        this.insuranceAmount = entity.getInsuranceAmount(); // Mức tham gia bảo hiểm
        this.compensationAmount = entity.getCompensationAmount(); // Mức đền bù bảo hiểm
        this.staffPercentage = entity.getStaffPercentage(); // Tỷ lệ nhân viên đóng bảo hiểm
        this.orgPercentage = entity.getOrgPercentage(); // Tỷ lệ công ty đóng bảo hiểm
        this.hasFamilyParticipation = entity.getHasFamilyParticipation(); // Có đóng cho thân nhân người lao động

        if (this.insuranceAmount != null) {
            if (this.staffPercentage != null) {
                this.staffAmount = this.insuranceAmount * this.staffPercentage / 100;
            }

            if (this.orgPercentage != null) {
                this.orgAmount = this.insuranceAmount * this.orgPercentage / 100;
            }
        }

    }

    public StaffInsurancePackageDto(StaffInsurancePackage entity, boolean isDetail) {
        this(entity);

        if (!isDetail) return;


    }


    public Double getStaffAmount() {
        return staffAmount;
    }

    public void setStaffAmount(Double staffAmount) {
        this.staffAmount = staffAmount;
    }

    public Double getOrgAmount() {
        return orgAmount;
    }

    public void setOrgAmount(Double orgAmount) {
        this.orgAmount = orgAmount;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public InsurancePackageDto getInsurancePackage() {
        return insurancePackage;
    }

    public void setInsurancePackage(InsurancePackageDto insurancePackage) {
        this.insurancePackage = insurancePackage;
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

    public Double getInsuranceAmount() {
        return insuranceAmount;
    }

    public void setInsuranceAmount(Double insuranceAmount) {
        this.insuranceAmount = insuranceAmount;
    }

    public Double getCompensationAmount() {
        return compensationAmount;
    }

    public void setCompensationAmount(Double compensationAmount) {
        this.compensationAmount = compensationAmount;
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

    public Boolean getHasFamilyParticipation() {
        return hasFamilyParticipation;
    }

    public void setHasFamilyParticipation(Boolean hasFamilyParticipation) {
        this.hasFamilyParticipation = hasFamilyParticipation;
    }
}
