package com.globits.hr.dto.search;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.globits.hr.domain.ShiftWork;
import com.globits.hr.dto.EmployeeStatusDto;
import com.globits.salary.dto.SalaryPeriodDto;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class SearchStaffDto extends SearchDto {
    //    private UUID staffId;
    private UUID civilServantTypeId; // loại công chức (trước là loại nhân viên)
    private Boolean isExportExcel = false;
    private Integer approvalStatus;
    private Boolean includeVoidedInProject;
    private UUID staffTypeId; // loại nhân viên

    private UUID salaryPeriodId; // kỳ lương
    private UUID salaryTemplateId; // mẫu bảng lương
    private SalaryPeriodDto salaryPeriod; //kỳ lương
    private Integer agreementStatus; //trạng thái hợp đồng

    private Boolean hasSocialIns;
    private Boolean allowExternalIpTimekeeping;
    private Set<UUID> departmentIds;

    private List<Integer> birthMonths;
    //keyword
    //-mã nhân viên, tên nhân viên, email, sđt

    //- Tiêu chí:
    private UUID contractOrganizationId;// Đơn vị ký hợp đồng
    private UUID workOrganizationId;// Đơn vị làm việc
    private UUID employeeStatusId; // Trạng thái nhân viên
    private EmployeeStatusDto employeeStatus;
    // Ngày bắt đầu chính thức (Ngày vào làm)
    private Date fromStartDate;
    private Date toStartDate;

    //Ngày bắt đầu tuyển dụng
    private Date fromRecruitmentDate;
    private Date toRecruitmentDate;

    private UUID workplaceId; // Địa điểm làm việc
    private Integer staffPhase; // Tình trạng nhân viên. Chi tiết: HrConstants.StaffPhase
    private String contractNumber; // Số hợp đồng
    private UUID provinceId; // Tỉnh thường trú
    private UUID districtId; // Huyện thường trú
    private UUID communeId; // Xã thường trú
    private String currentResidence; //tạm trú (nơi ở hiện tại)
    private String birthPlace; // Quê quán
    private String idNumber; // CMND/CCCD
    private Integer maritalStatus; // Tình trạng hôn nhân
    private String taxCode; // Mã số thuế
    private String healthInsuranceNumber; // Mã số bảo hiểm y tế (BHYT)
    private String socialInsuranceNumber; // Mã số BHXH
    private String socialInsuranceNote; // Tình trạng sổ BHXH
    private UUID introducerId; // Nhân viên giới thiệu nhân viên này vào làm
    private UUID recruiterId; // Nhân viên quyết định tuyển dụng nhân viên này vào làm
    private Integer staffDocumentStatus; // Tình trạng hoàn thành hồ sơ của nhân viên. Chi tiết: HrConstants.StaffDocumentStatus
    private Boolean hasSocialInsuranceNumber;
    // Vị trí công tác chính

    private UUID rankTitleId;// Cấp bậc
    private UUID directManagerId; // Vị trí quản lý trực tiếp

    private Integer levelNumber; // Cấp bậc cần lấy dữ liệu
    private Boolean collectInEachLevel; // Có lấy dữ liệu trên mỗi level (từ level của nhân viên hiện tại đến level cần tìm kiếm)

    private List<UUID> listStaffId;

    private Date recruitmentDate;

    private Date fromBirthDate; // Ngày sinh từ
    private Date toBirthDate; // Ngày sinh đến

    private Integer staffWorkShiftType; // Loại làm việc. HrConstants.StaffWorkShiftType
    private UUID fixShiftWorkId; // Ca làm việc cố định nếu loại làm việc của nhân viên là cố định. HrConstants.StaffWorkShiftType.FIX
    private Integer staffLeaveShiftType; // Loại nghỉ làm việc. HrConstants.StaffLeaveShiftType
    private Integer fixLeaveWeekDay; // Ngày nghỉ cố định trong tuần, có giá trị khi loại nghỉ cửa nhân viên là nghỉ cố định. HrConstants.WeekDays
    private Integer year;
    private Date fromMaternityLeave;
    private Date toMaternityLeave;
    private Boolean maternityLeaveEnded;

    private Integer staffPositionType;

    private Date endSocialInsFromDate; // kết thúc đóng BHXH từ ngày
    private Date endSocialInsToDate; // kết thúc đóng BHXH đến ngày
    private Boolean isBasic = false;

    public EmployeeStatusDto getEmployeeStatus() {
        return employeeStatus;
    }

    public void setEmployeeStatus(EmployeeStatusDto employeeStatus) {
        this.employeeStatus = employeeStatus;
    }

    public Date getEndSocialInsFromDate() {
        return endSocialInsFromDate;
    }

    public void setEndSocialInsFromDate(Date endSocialInsFromDate) {
        this.endSocialInsFromDate = endSocialInsFromDate;
    }

    public Date getEndSocialInsToDate() {
        return endSocialInsToDate;
    }

    public void setEndSocialInsToDate(Date endSocialInsToDate) {
        this.endSocialInsToDate = endSocialInsToDate;
    }

    public Integer getStaffPositionType() {
        return staffPositionType;
    }

    public void setStaffPositionType(Integer staffPositionType) {
        this.staffPositionType = staffPositionType;
    }

    public Integer getStaffWorkShiftType() {
        return staffWorkShiftType;
    }

    public void setStaffWorkShiftType(Integer staffWorkShiftType) {
        this.staffWorkShiftType = staffWorkShiftType;
    }

    public UUID getFixShiftWorkId() {
        return fixShiftWorkId;
    }

    public void setFixShiftWorkId(UUID fixShiftWorkId) {
        this.fixShiftWorkId = fixShiftWorkId;
    }

    public Integer getStaffLeaveShiftType() {
        return staffLeaveShiftType;
    }

    public void setStaffLeaveShiftType(Integer staffLeaveShiftType) {
        this.staffLeaveShiftType = staffLeaveShiftType;
    }

    public Integer getFixLeaveWeekDay() {
        return fixLeaveWeekDay;
    }

    public void setFixLeaveWeekDay(Integer fixLeaveWeekDay) {
        this.fixLeaveWeekDay = fixLeaveWeekDay;
    }

    public Set<UUID> getDepartmentIds() {
        return departmentIds;
    }

    public void setDepartmentIds(Set<UUID> departmentIds) {
        this.departmentIds = departmentIds;
    }

    public Date getRecruitmentDate() {
        return recruitmentDate;
    }

    public void setRecruitmentDate(Date recruitmentDate) {
        this.recruitmentDate = recruitmentDate;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public UUID getEmployeeStatusId() {
        return employeeStatusId;
    }

    public void setEmployeeStatusId(UUID employeeStatusId) {
        this.employeeStatusId = employeeStatusId;
    }

    public UUID getCivilServantTypeId() {
        return civilServantTypeId;
    }

    public void setCivilServantTypeId(UUID civilServantTypeId) {
        this.civilServantTypeId = civilServantTypeId;
    }

    public Boolean getIsExportExcel() {
        return isExportExcel;
    }

    public void setIsExportExcel(Boolean isExportExcel) {
        this.isExportExcel = isExportExcel;
    }

    public Boolean getExportExcel() {
        return isExportExcel;
    }

    public void setExportExcel(Boolean exportExcel) {
        isExportExcel = exportExcel;
    }

    public Boolean getIncludeVoidedInProject() {
        return includeVoidedInProject;
    }

    public void setIncludeVoidedInProject(Boolean includeVoidedInProject) {
        this.includeVoidedInProject = includeVoidedInProject;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public UUID getSalaryPeriodId() {
        return salaryPeriodId;
    }

    public void setSalaryPeriodId(UUID salaryPeriodId) {
        this.salaryPeriodId = salaryPeriodId;
    }

    public UUID getSalaryTemplateId() {
        return salaryTemplateId;
    }

    public void setSalaryTemplateId(UUID salaryTemplateId) {
        this.salaryTemplateId = salaryTemplateId;
    }

    public Boolean getHasSocialIns() {
        return hasSocialIns;
    }

    public void setHasSocialIns(Boolean hasSocialIns) {
        this.hasSocialIns = hasSocialIns;
    }

    public UUID getStaffTypeId() {
        return staffTypeId;
    }

    public void setStaffTypeId(UUID staffTypeId) {
        this.staffTypeId = staffTypeId;
    }

    public UUID getContractOrganizationId() {
        return contractOrganizationId;
    }

    public void setContractOrganizationId(UUID contractOrganizationId) {
        this.contractOrganizationId = contractOrganizationId;
    }

    public UUID getWorkOrganizationId() {
        return workOrganizationId;
    }

    public void setWorkOrganizationId(UUID workOrganizationId) {
        this.workOrganizationId = workOrganizationId;
    }

    public SalaryPeriodDto getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(SalaryPeriodDto salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

    public Integer getAgreementStatus() {
        return agreementStatus;
    }

    public void setAgreementStatus(Integer agreementStatus) {
        this.agreementStatus = agreementStatus;
    }

    public Boolean getAllowExternalIpTimekeeping() {
        return allowExternalIpTimekeeping;
    }

    public void setAllowExternalIpTimekeeping(Boolean allowExternalIpTimekeeping) {
        this.allowExternalIpTimekeeping = allowExternalIpTimekeeping;
    }

    public Date getFromStartDate() {
        return fromStartDate;
    }

    public void setFromStartDate(Date fromStartDate) {
        this.fromStartDate = fromStartDate;
    }

    public Date getToStartDate() {
        return toStartDate;
    }

    public void setToStartDate(Date toStartDate) {
        this.toStartDate = toStartDate;
    }

    public UUID getWorkplaceId() {
        return workplaceId;
    }

    public void setWorkplaceId(UUID workplaceId) {
        this.workplaceId = workplaceId;
    }

    public Integer getStaffPhase() {
        return staffPhase;
    }

    public void setStaffPhase(Integer staffPhase) {
        this.staffPhase = staffPhase;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public UUID getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(UUID provinceId) {
        this.provinceId = provinceId;
    }

    public UUID getDistrictId() {
        return districtId;
    }

    public void setDistrictId(UUID districtId) {
        this.districtId = districtId;
    }

    public UUID getCommuneId() {
        return communeId;
    }

    public void setCommuneId(UUID communeId) {
        this.communeId = communeId;
    }

    public String getCurrentResidence() {
        return currentResidence;
    }

    public void setCurrentResidence(String currentResidence) {
        this.currentResidence = currentResidence;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public Integer getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(Integer maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getHealthInsuranceNumber() {
        return healthInsuranceNumber;
    }

    public void setHealthInsuranceNumber(String healthInsuranceNumber) {
        this.healthInsuranceNumber = healthInsuranceNumber;
    }

    public String getSocialInsuranceNumber() {
        return socialInsuranceNumber;
    }

    public void setSocialInsuranceNumber(String socialInsuranceNumber) {
        this.socialInsuranceNumber = socialInsuranceNumber;
    }

    public String getSocialInsuranceNote() {
        return socialInsuranceNote;
    }

    public void setSocialInsuranceNote(String socialInsuranceNote) {
        this.socialInsuranceNote = socialInsuranceNote;
    }

    public UUID getIntroducerId() {
        return introducerId;
    }

    public void setIntroducerId(UUID introducerId) {
        this.introducerId = introducerId;
    }

    public UUID getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(UUID recruiterId) {
        this.recruiterId = recruiterId;
    }

    public Integer getStaffDocumentStatus() {
        return staffDocumentStatus;
    }

    public void setStaffDocumentStatus(Integer staffDocumentStatus) {
        this.staffDocumentStatus = staffDocumentStatus;
    }

    public UUID getRankTitleId() {
        return rankTitleId;
    }

    public void setRankTitleId(UUID rankTitleId) {
        this.rankTitleId = rankTitleId;
    }

    public UUID getDirectManagerId() {
        return directManagerId;
    }

    public void setDirectManagerId(UUID directManagerId) {
        this.directManagerId = directManagerId;
    }

    public List<UUID> getListStaffId() {
        return listStaffId;
    }

    public void setListStaffId(List<UUID> listStaffId) {
        this.listStaffId = listStaffId;
    }

    public Integer getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(Integer levelNumber) {
        this.levelNumber = levelNumber;
    }

    public Boolean getCollectInEachLevel() {
        return collectInEachLevel;
    }

    public void setCollectInEachLevel(Boolean collectInEachLevel) {
        this.collectInEachLevel = collectInEachLevel;
    }

    public Boolean getHasSocialInsuranceNumber() {
        return hasSocialInsuranceNumber;
    }

    public void setHasSocialInsuranceNumber(Boolean hasSocialInsuranceNumber) {
        this.hasSocialInsuranceNumber = hasSocialInsuranceNumber;
    }

    public Date getFromBirthDate() {
        return fromBirthDate;
    }

    public void setFromBirthDate(Date fromBirthDate) {
        this.fromBirthDate = fromBirthDate;
    }

    public Date getToBirthDate() {
        return toBirthDate;
    }

    public void setToBirthDate(Date toBirthDate) {
        this.toBirthDate = toBirthDate;
    }

    public List<Integer> getBirthMonths() {
        return birthMonths;
    }

    public void setBirthMonths(List<Integer> birthMonths) {
        this.birthMonths = birthMonths;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Date getFromMaternityLeave() {
        return fromMaternityLeave;
    }

    public void setFromMaternityLeave(Date fromMaternityLeave) {
        this.fromMaternityLeave = fromMaternityLeave;
    }

    public Date getToMaternityLeave() {
        return toMaternityLeave;
    }

    public void setToMaternityLeave(Date toMaternityLeave) {
        this.toMaternityLeave = toMaternityLeave;
    }

    public Date getFromRecruitmentDate() {
        return fromRecruitmentDate;
    }

    public void setFromRecruitmentDate(Date fromRecruitmentDate) {
        this.fromRecruitmentDate = fromRecruitmentDate;
    }

    public Date getToRecruitmentDate() {
        return toRecruitmentDate;
    }

    public void setToRecruitmentDate(Date toRecruitmentDate) {
        this.toRecruitmentDate = toRecruitmentDate;
    }

    public Boolean getMaternityLeaveEnded() {
        return maternityLeaveEnded;
    }

    public void setMaternityLeaveEnded(Boolean maternityLeaveEnded) {
        this.maternityLeaveEnded = maternityLeaveEnded;
    }

    public Boolean getIsBasic() {
        return isBasic;
    }

    public void setIsBasic(Boolean basic) {
        isBasic = basic;
    }
}
