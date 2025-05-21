package com.globits.hr.dto.search;

import java.util.Date;

import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.PositionDto;
import com.globits.hr.dto.StaffDto;

public class SearchStaffLabourAgreementDto extends SearchDto {
    private StaffDto staff;
    private Date insuranceStartDate;
    private Date insuranceEndDate;
    private HrOrganizationDto contractOrganization;
    private HrOrganizationDto workOrganization;

    private HrOrganizationDto staffOrganization;
    private HRDepartmentDto staffDepartment;
    private PositionDto staffPosition;
    private Integer exportType;
    private Integer agreementStatus;
    
    private Boolean isOverdueContract = false;
    private Integer contractPreExpiryDays;

    public SearchStaffLabourAgreementDto() {
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public Date getInsuranceStartDate() {
        return insuranceStartDate;
    }

    public void setInsuranceStartDate(Date insuranceStartDate) {
        this.insuranceStartDate = insuranceStartDate;
    }

    public Date getInsuranceEndDate() {
        return insuranceEndDate;
    }

    public void setInsuranceEndDate(Date insuranceEndDate) {
        this.insuranceEndDate = insuranceEndDate;
    }

    public HrOrganizationDto getContractOrganization() {
        return contractOrganization;
    }

    public void setContractOrganization(HrOrganizationDto contractOrganization) {
        this.contractOrganization = contractOrganization;
    }

    public HrOrganizationDto getWorkOrganization() {
        return workOrganization;
    }

    public void setWorkOrganization(HrOrganizationDto workOrganization) {
        this.workOrganization = workOrganization;
    }

    public HrOrganizationDto getStaffOrganization() {
        return staffOrganization;
    }

    public void setStaffOrganization(HrOrganizationDto staffOrganization) {
        this.staffOrganization = staffOrganization;
    }

    public HRDepartmentDto getStaffDepartment() {
        return staffDepartment;
    }

    public void setStaffDepartment(HRDepartmentDto staffDepartment) {
        this.staffDepartment = staffDepartment;
    }

    public PositionDto getStaffPosition() {
        return staffPosition;
    }

    public void setStaffPosition(PositionDto staffPosition) {
        this.staffPosition = staffPosition;
    }

    public Integer getExportType() {
        return exportType;
    }

    public void setExportType(Integer exportType) {
        this.exportType = exportType;
    }

    public Integer getAgreementStatus() {
        return agreementStatus;
    }

    public void setAgreementStatus(Integer agreementStatus) {
        this.agreementStatus = agreementStatus;
    }

	public Boolean getIsOverdueContract() {
		return isOverdueContract;
	}

	public void setIsOverdueContract(Boolean isOverdueContract) {
		this.isOverdueContract = isOverdueContract;
	}

	public Integer getContractPreExpiryDays() {
		return contractPreExpiryDays;
	}

	public void setContractPreExpiryDays(Integer contractPreExpiryDays) {
		this.contractPreExpiryDays = contractPreExpiryDays;
	}
    
}
	
