package com.globits.hrv3.dto.view;

import java.util.Date;

import com.globits.core.dto.ProfessionDto;
import com.globits.hr.domain.Staff;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.CivilServantCategoryDto;
import com.globits.hr.dto.CivilServantGradeDto;
import com.globits.hr.dto.CivilServantTypeDto;
import com.globits.hr.dto.EmployeeStatusDto;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.LabourAgreementTypeDto;

public class ProfileInformationDto extends BaseObjectDto {
    private HRDepartmentDto department;
    private Integer currentWorkingStatus;//Trạng thái công việc hiện tại
    private String salaryCoefficient;//Hệ số lương
    private String socialInsuranceNumber;//Số sổ bảo hiểm xã hội
    private String jobTitle;//Tên công việc hiện tại
    private String staffCode;
    private EmployeeStatusDto status; //Trạng thái nhân viên
    private CivilServantTypeDto civilServantType;
    private CivilServantCategoryDto civilServantCategory;//Ngạch công chức
    private CivilServantGradeDto grade;//Bậc công chức
    private LabourAgreementTypeDto labourAgreementType; //Loại hợp đồng
    private Date contractDate;
    private Date recruitmentDate;//Ngày tuyển dụng
    private String professionalTitles;//Chức danh chuyên môn
    private ProfessionDto profession;//Công việc được giao
    private String highestPosition;//Chức vụ cao nhất
    private Date dateOfReceivingPosition;//Ngày nhận Chức vụ cao nhất
    private String positionDecisionNumber;//Số quyết định chức vụ
    private Date startDate;//Ngày bắt đầu công việc
    private String allowanceCoefficient;//Hệ số phụ cấp
    private Date dateOfReceivingAllowance;//Ngày hưởng phụ cấp
    private String salaryLeve;//Bậc lương
    private Date salaryStartDate;//Ngày hưởng bậc lương hiện thời

    public ProfileInformationDto() {
        super();
    }

    public ProfileInformationDto(Staff entity) {
        if (entity == null) {
            return;
        }
        id = entity.getId();
        staffCode = entity.getStaffCode();
        contractDate = entity.getContractDate();
        recruitmentDate = entity.getRecruitmentDate();
        startDate = entity.getStartDate();
        jobTitle = entity.getJobTitle();
        if (entity.getCivilServantType() != null) {
            civilServantType = new CivilServantTypeDto(entity.getCivilServantType());
        }
        currentWorkingStatus = entity.getCurrentWorkingStatus();
        salaryCoefficient = entity.getSalaryCoefficient();
        socialInsuranceNumber = entity.getSocialInsuranceNumber();
        salaryStartDate = entity.getSalaryStartDate();
        highestPosition = entity.getHighestPosition();
        dateOfReceivingPosition = entity.getDateOfReceivingPosition();
        professionalTitles = entity.getProfessionalTitles();
        allowanceCoefficient = entity.getAllowanceCoefficient();
        dateOfReceivingAllowance = entity.getDateOfReceivingAllowance();
        salaryLeve = entity.getSalaryLeve();
        positionDecisionNumber = entity.getPositionDecisionNumber();

        if (entity.getStatus() != null) {
            status = new EmployeeStatusDto(entity.getStatus());
        }

//        if (entity.getCivilServantCategory() != null) {
//            civilServantCategory = new CivilServantCategoryDto(entity.getCivilServantCategory());
//        }
//        if (entity.getGrade() != null) {
//            grade = new CivilServantGradeDto(entity.getGrade());
//        }
//        if (entity.getProfession() != null) {
//            profession = new ProfessionDto(entity.getProfession());
//        }

        if (entity.getDepartment() != null) {
            this.setDepartment(new HRDepartmentDto(entity.getDepartment()));
        }
        if (entity.getLabourAgreementType() != null) {
            labourAgreementType = new LabourAgreementTypeDto(entity.getLabourAgreementType());
        }
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    public Integer getCurrentWorkingStatus() {
        return currentWorkingStatus;
    }

    public void setCurrentWorkingStatus(Integer currentWorkingStatus) {
        this.currentWorkingStatus = currentWorkingStatus;
    }

    public String getSalaryCoefficient() {
        return salaryCoefficient;
    }

    public void setSalaryCoefficient(String salaryCoefficient) {
        this.salaryCoefficient = salaryCoefficient;
    }

    public String getSocialInsuranceNumber() {
        return socialInsuranceNumber;
    }

    public void setSocialInsuranceNumber(String socialInsuranceNumber) {
        this.socialInsuranceNumber = socialInsuranceNumber;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public EmployeeStatusDto getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatusDto status) {
        this.status = status;
    }

    public CivilServantTypeDto getCivilServantType() {
        return civilServantType;
    }

    public void setCivilServantType(CivilServantTypeDto civilServantType) {
        this.civilServantType = civilServantType;
    }

    public CivilServantCategoryDto getCivilServantCategory() {
        return civilServantCategory;
    }

    public void setCivilServantCategory(CivilServantCategoryDto civilServantCategory) {
        this.civilServantCategory = civilServantCategory;
    }

    public CivilServantGradeDto getGrade() {
        return grade;
    }

    public void setGrade(CivilServantGradeDto grade) {
        this.grade = grade;
    }

    public LabourAgreementTypeDto getLabourAgreementType() {
        return labourAgreementType;
    }

    public void setLabourAgreementType(LabourAgreementTypeDto labourAgreementType) {
        this.labourAgreementType = labourAgreementType;
    }

    public Date getContractDate() {
        return contractDate;
    }

    public void setContractDate(Date contractDate) {
        this.contractDate = contractDate;
    }

    public Date getRecruitmentDate() {
        return recruitmentDate;
    }

    public void setRecruitmentDate(Date recruitmentDate) {
        this.recruitmentDate = recruitmentDate;
    }

    public String getProfessionalTitles() {
        return professionalTitles;
    }

    public void setProfessionalTitles(String professionalTitles) {
        this.professionalTitles = professionalTitles;
    }

    public ProfessionDto getProfession() {
        return profession;
    }

    public void setProfession(ProfessionDto profession) {
        this.profession = profession;
    }

    public String getHighestPosition() {
        return highestPosition;
    }

    public void setHighestPosition(String highestPosition) {
        this.highestPosition = highestPosition;
    }

    public Date getDateOfReceivingPosition() {
        return dateOfReceivingPosition;
    }

    public void setDateOfReceivingPosition(Date dateOfReceivingPosition) {
        this.dateOfReceivingPosition = dateOfReceivingPosition;
    }

    public String getPositionDecisionNumber() {
        return positionDecisionNumber;
    }

    public void setPositionDecisionNumber(String positionDecisionNumber) {
        this.positionDecisionNumber = positionDecisionNumber;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getAllowanceCoefficient() {
        return allowanceCoefficient;
    }

    public void setAllowanceCoefficient(String allowanceCoefficient) {
        this.allowanceCoefficient = allowanceCoefficient;
    }

    public Date getDateOfReceivingAllowance() {
        return dateOfReceivingAllowance;
    }

    public void setDateOfReceivingAllowance(Date dateOfReceivingAllowance) {
        this.dateOfReceivingAllowance = dateOfReceivingAllowance;
    }

    public String getSalaryLeve() {
        return salaryLeve;
    }

    public void setSalaryLeve(String salaryLeve) {
        this.salaryLeve = salaryLeve;
    }

    public Date getSalaryStartDate() {
        return salaryStartDate;
    }

    public void setSalaryStartDate(Date salaryStartDate) {
        this.salaryStartDate = salaryStartDate;
    }
}
