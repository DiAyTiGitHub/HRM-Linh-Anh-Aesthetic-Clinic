package com.globits.hr.dto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.FileDescriptionDto;
import com.globits.hr.domain.StaffLabourAgreement;
import com.globits.hr.domain.StaffLabourAgreementAttachment;
import com.globits.salary.dto.SalaryAreaDto;
import com.globits.salary.dto.SalaryTemplateDto;
import com.globits.salary.dto.SalaryUnitDto;
import jakarta.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaffLabourAgreementDto extends BaseObjectDto {
    private static final Logger logger = LoggerFactory.getLogger(StaffLabourAgreementDto.class);
    private StaffDto staff;
    private Date startDate;
    private Date endDate;
    private Date signedDate;
    private LabourAgreementTypeDto labourAgreementType;
    private ContractTypeDto contractType; // loại hợp đồng nhưng theo thời gian (ngày, tháng, năm). VD: Hợp đồng 1 năm
    private List<StaffLabourAgreementAttachmentDto> attachments = new ArrayList<>();
    private Boolean isCurrent;
    private String staffCode;
    private String contractTypeCode;
    private Date recruitmentDate;
    private Date contractDate;
    private String labourAgreementNumber;
    private Double workingHour;
    private Double workingHourWeekMin;
    private Double workingHourWeekMax;
    private String workingPlace;
    private SalaryAreaDto salaryArea;
    private Double salary;
    private SalaryUnitDto salaryUnit;
    private SalaryUnitDto salaryInsuranceUnit;
    private Integer durationMonths; // số tháng hợp đồng (chỉ áp dụng khi loại hợp đồng là xác định thời hạn)
    //return file/attachments of agreement only, not return relationship like attachments field above
    private List<FileDescriptionDto> files;

    private SalaryTemplateDto salaryTemplate; // mau bang luong

    //bhxh
    private String socialInsuranceNumber;// Số sổ bảo hiểm xã hội
    private Boolean hasSocialIns; // Có đóng BHXH hay không

    // Mức lương tham gia bảo hiểm xã hội
    private Double insuranceSalary;


    // ====== Nhân viên đóng ======
    // Tỷ lệ đóng BHXH của nhân viên
    private Double staffSocialInsurancePercentage;
    // Số tiền BHXH nhân viên đóng
    private Double staffSocialInsuranceAmount;

    // Tỷ lệ đóng BHYT của nhân viên
    private Double staffHealthInsurancePercentage;
    // Số tiền BHYT nhân viên đóng
    private Double staffHealthInsuranceAmount;

    // Tỷ lệ đóng BHTN của nhân viên
    private Double staffUnemploymentInsurancePercentage;
    // Số tiền BHTN nhân viên đóng
    private Double staffUnemploymentInsuranceAmount;

    // Tổng tiền bảo hiểm mà nhân viên đóng
    private Double staffTotalInsuranceAmount;

    // ====== Công ty đóng ======
    // Tỷ lệ đóng BHXH của công ty
    private Double orgSocialInsurancePercentage;
    // Số tiền BHXH công ty đóng
    private Double orgSocialInsuranceAmount;

    // Tỷ lệ đóng BHYT của công ty
    private Double orgHealthInsurancePercentage;
    // Số tiền BHYT công ty đóng
    private Double orgHealthInsuranceAmount;

    // Tỷ lệ đóng BHTN của công ty
    private Double orgUnemploymentInsurancePercentage;
    // Số tiền BHTN công ty đóng
    private Double orgUnemploymentInsuranceAmount;

    // Tổng tiền bảo hiểm mà công ty đóng
    private Double orgTotalInsuranceAmount;

    private Double totalInsuranceAmount; //Tổng tiền bảo hiểm mà nhân viên và công ty đóng

    private Date insuranceStartDate;//Ngày bắt đầu mức đóng
    private Date insuranceEndDate;//Ngày kết thúc mức đóng
    private Integer paidStatus; // Bảo hiểm này của nhan vien da duoc tra (dong) hay chua. Chi tiet: HrConstants.StaffSocialInsurancePaidStatus

    // noi dang ky, noi lam viec
    private HrOrganizationDto contractOrganization;
    private HrOrganizationDto workOrganization;
    private Integer agreementStatus; // Trạng thái hợp đồng. Chi tiết HrConstants.StaffLabourAgreementStatus;
    private String errorMessage;

    public StaffLabourAgreementDto() {

    }

    public StaffLabourAgreementDto(StaffLabourAgreement agreement) {
        if (agreement == null) {
            return;
        }
        this.setId(agreement.getId());
        this.setLabourAgreementNumber(agreement.getLabourAgreementNumber());
        this.setDurationMonths(agreement.getDurationMonths());
        this.setWorkingPlace(agreement.getWorkingPlace());
        this.setSalary(agreement.getSalary());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if (agreement.getStartDate() != null) {
                if (agreement.getStartDate().before(sdf.parse("01-01-1900")) || agreement.getStartDate().after(sdf.parse("01-01-2100"))) {
                    this.startDate = null;
                } else {
                    this.startDate = agreement.getStartDate();
                }
            }
            if (agreement.getEndDate() != null) {
                if (agreement.getEndDate().before(sdf.parse("01-01-1900")) || agreement.getEndDate().after(sdf.parse("01-01-2100"))) {
                    this.endDate = null;
                } else {
                    this.endDate = agreement.getEndDate();
                }
            }
            if (agreement.getSignedDate() != null) {
                if (agreement.getSignedDate().before(sdf.parse("01-01-1900")) || agreement.getSignedDate().after(sdf.parse("01-01-2100"))) {
                    this.signedDate = null;
                } else {
                    this.signedDate = agreement.getSignedDate();
                }
            }
        } catch (Exception e) {
            logger.error("ERROR : {}", e.getMessage(), e);
        }
        if (agreement.getStaff() != null) {
            this.staff = new StaffDto();

            this.staff.setId(agreement.getStaff().getId());
            this.staff.setStaffCode(agreement.getStaff().getStaffCode());
            this.staff.setDisplayName(agreement.getStaff().getDisplayName());

        }
        if (agreement.getLabourAgreementType() != null) {
            this.labourAgreementType = new LabourAgreementTypeDto();
            this.labourAgreementType.setId(agreement.getLabourAgreementType().getId());
            this.labourAgreementType.setName(agreement.getLabourAgreementType().getName());
            this.labourAgreementType.setCode(agreement.getLabourAgreementType().getCode());
//                labourAgreementType = new LabourAgreementTypeDto(agreement.getLabourAgreementType());
        }

        if (agreement.getContractType() != null) {
//            this.contractType = new ContractTypeDto(agreement.getContractType());

            this.contractType = new ContractTypeDto();
            this.contractType.setId(agreement.getContractType().getId());
            this.contractType.setCode(agreement.getContractType().getCode());
            this.contractType.setName(agreement.getContractType().getName());
        }

        this.durationMonths = agreement.getDurationMonths();

        this.hasSocialIns = agreement.getHasSocialIns(); // Có đóng BHXH hay không

        this.insuranceSalary = agreement.getInsuranceSalary();

        this.staffSocialInsurancePercentage = agreement.getStaffSocialInsurancePercentage();
        this.staffHealthInsurancePercentage = agreement.getStaffHealthInsurancePercentage();
        this.staffUnemploymentInsurancePercentage = agreement.getStaffUnemploymentInsurancePercentage();

        this.orgSocialInsurancePercentage = agreement.getOrgSocialInsurancePercentage();
        this.orgHealthInsurancePercentage = agreement.getOrgHealthInsurancePercentage();
        this.orgUnemploymentInsurancePercentage = agreement.getOrgUnemploymentInsurancePercentage();

        this.insuranceStartDate = agreement.getInsuranceStartDate(); // Ngày bắt đầu mức đóng
        this.insuranceEndDate = agreement.getInsuranceEndDate(); // Ngày kết thúc mức đóng
        this.paidStatus = agreement.getPaidStatus();
        if (this.insuranceSalary != null) {
            // ===== Nhân viên đóng =====
            if (this.staffSocialInsurancePercentage != null) {
                this.staffSocialInsuranceAmount = this.insuranceSalary * this.staffSocialInsurancePercentage / 100;
            } else {
                this.staffSocialInsuranceAmount = 0.0;
            }

            if (this.staffHealthInsurancePercentage != null) {
                this.staffHealthInsuranceAmount = this.insuranceSalary * this.staffHealthInsurancePercentage / 100;
            } else {
                this.staffHealthInsuranceAmount = 0.0;
            }

            if (this.staffUnemploymentInsurancePercentage != null) {
                this.staffUnemploymentInsuranceAmount = this.insuranceSalary * this.staffUnemploymentInsurancePercentage / 100;
            } else {
                this.staffUnemploymentInsuranceAmount = 0.0;
            }

            this.staffTotalInsuranceAmount = this.staffSocialInsuranceAmount
                    + this.staffHealthInsuranceAmount
                    + this.staffUnemploymentInsuranceAmount;

            // ===== Công ty đóng =====
            if (this.orgSocialInsurancePercentage != null) {
                this.orgSocialInsuranceAmount = this.insuranceSalary * this.orgSocialInsurancePercentage / 100;
            } else {
                this.orgSocialInsuranceAmount = 0.0;
            }

            if (this.orgHealthInsurancePercentage != null) {
                this.orgHealthInsuranceAmount = this.insuranceSalary * this.orgHealthInsurancePercentage / 100;
            } else {
                this.orgHealthInsuranceAmount = 0.0;
            }

            if (this.orgUnemploymentInsurancePercentage != null) {
                this.orgUnemploymentInsuranceAmount = this.insuranceSalary * this.orgUnemploymentInsurancePercentage / 100;
            } else {
                this.orgUnemploymentInsuranceAmount = 0.0;
            }

            this.orgTotalInsuranceAmount = this.orgSocialInsuranceAmount
                    + this.orgHealthInsuranceAmount
                    + this.orgUnemploymentInsuranceAmount;

            // Tổng tiền bảo hiểm mà nhân viên và công ty đóng
            this.totalInsuranceAmount = this.staffTotalInsuranceAmount + this.orgTotalInsuranceAmount;
        }

        this.agreementStatus = agreement.getAgreementStatus(); //trạng thái hợp đồng
        this.socialInsuranceNumber = agreement.getSocialInsuranceNumber();

        if (agreement.getContractOrganization() != null){
//            this.contractOrganization = new HrOrganizationDto(agreement.getContractOrganization(), false, false);

            this.contractOrganization = new HrOrganizationDto();
            this.contractOrganization.setId(agreement.getContractOrganization().getId());
            this.contractOrganization.setCode(agreement.getContractOrganization().getCode());
            this.contractOrganization.setName(agreement.getContractOrganization().getName());
        }

        if (agreement.getWorkOrganization() != null){
//            this.workOrganization = new HrOrganizationDto(agreement.getWorkOrganization(), false, false);

            this.workOrganization = new HrOrganizationDto();
            this.workOrganization.setId(agreement.getWorkOrganization().getId());
            this.workOrganization.setCode(agreement.getWorkOrganization().getCode());
            this.workOrganization.setName(agreement.getWorkOrganization().getName());
        }

        if (agreement.getSalaryTemplate() != null) {
//            this.salaryTemplate = new SalaryTemplateDto(agreement.getSalaryTemplate(), false);

            this.salaryTemplate = new SalaryTemplateDto();
            this.salaryTemplate.setId(agreement.getSalaryTemplate().getId());
            this.salaryTemplate.setCode(agreement.getSalaryTemplate().getCode());
            this.salaryTemplate.setName(agreement.getSalaryTemplate().getName());
        }
    }

    public StaffLabourAgreementDto(StaffLabourAgreement agreement, boolean isGetDetail) {
        this(agreement);

        if (!isGetDetail) return;

        if (agreement.getSalaryArea() != null) {
            this.salaryArea = new SalaryAreaDto(agreement.getSalaryArea());
        }
        if (agreement.getSalaryUnit() != null) {
            this.salaryUnit = new SalaryUnitDto(agreement.getSalaryUnit());
        }
        if (agreement.getSalaryInsuranceUnit() != null) {
            this.salaryInsuranceUnit = new SalaryUnitDto(agreement.getSalaryInsuranceUnit());
        }
        this.insuranceSalary = agreement.getInsuranceSalary();
        this.salary = agreement.getSalary();
        this.workingHourWeekMax = agreement.getWorkingHourWeekMax();
        this.workingHourWeekMin = agreement.getWorkingHourWeekMin();
        this.labourAgreementNumber = agreement.getLabourAgreementNumber();
        this.workingHour = agreement.getWorkingHour();
        this.workingPlace = agreement.getWorkingPlace();

        //attachments in agreements
        if (agreement.getAttachments() != null && !agreement.getAttachments().isEmpty()) {
            List<FileDescriptionDto> attachments = new ArrayList<>();
            for (StaffLabourAgreementAttachment slaa : agreement.getAttachments()) {
                FileDescriptionDto file = new FileDescriptionDto(slaa.getFile());
                attachments.add(file);
            }

            this.setFiles(attachments);
        }

        if (agreement.getSalaryTemplate() != null) {
            this.salaryTemplate = new SalaryTemplateDto(agreement.getSalaryTemplate(), false);
        }


    }

    public StaffLabourAgreementDto(Double staffTotalInsuranceAmount, Double orgInsuranceAmount,
                                   Double totalSalaryInsurance) {
        this.staffTotalInsuranceAmount = staffTotalInsuranceAmount;
        this.orgTotalInsuranceAmount = orgInsuranceAmount;

        if (totalSalaryInsurance != null) {
            this.insuranceSalary = totalSalaryInsurance;
        }

        double total = 0.0;
        if (orgInsuranceAmount != null) {
            total += orgInsuranceAmount;
        }
        if (staffTotalInsuranceAmount != null) {
            total += staffTotalInsuranceAmount;
        }
        this.totalInsuranceAmount = total;
    }


    public String getLabourAgreementNumber() {
        return labourAgreementNumber;
    }

    public void setLabourAgreementNumber(String labourAgreementNumber) {
        this.labourAgreementNumber = labourAgreementNumber;
    }

    public void setWorkingHour(Double workingHour) {
        this.workingHour = workingHour;
    }

    public void setWorkingHourWeekMin(Double workingHourWeekMin) {
        this.workingHourWeekMin = workingHourWeekMin;
    }

    public void setWorkingHourWeekMax(Double workingHourWeekMax) {
        this.workingHourWeekMax = workingHourWeekMax;
    }

    public void setWorkingPlace(String workingPlace) {
        this.workingPlace = workingPlace;
    }


    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Double getWorkingHour() {
        return workingHour;
    }

    public Double getWorkingHourWeekMin() {
        return workingHourWeekMin;
    }

    public Double getWorkingHourWeekMax() {
        return workingHourWeekMax;
    }

    public String getWorkingPlace() {
        return workingPlace;
    }


    public Double getSalary() {
        return salary;
    }


    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public LabourAgreementTypeDto getLabourAgreementType() {
        return labourAgreementType;
    }

    public void setLabourAgreementType(LabourAgreementTypeDto labourAgreementType) {
        this.labourAgreementType = labourAgreementType;
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

    public Date getSignedDate() {
        return signedDate;
    }

    public void setSignedDate(Date signedDate) {
        this.signedDate = signedDate;
    }

    public List<StaffLabourAgreementAttachmentDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<StaffLabourAgreementAttachmentDto> attachments) {
        this.attachments = attachments;
    }

    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public Boolean getCurrent() {
        return isCurrent;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public void setCurrent(Boolean current) {
        isCurrent = current;
    }

    public String getContractTypeCode() {
        return contractTypeCode;
    }

    public void setContractTypeCode(String contractTypeCode) {
        this.contractTypeCode = contractTypeCode;
    }

    public Date getRecruitmentDate() {
        return recruitmentDate;
    }

    public void setRecruitmentDate(Date recruitmentDate) {
        this.recruitmentDate = recruitmentDate;
    }

    public Date getContractDate() {
        return contractDate;
    }

    public void setContractDate(Date contractDate) {
        this.contractDate = contractDate;
    }

    public SalaryAreaDto getSalaryArea() {
        return salaryArea;
    }

    public void setSalaryArea(SalaryAreaDto salaryArea) {
        this.salaryArea = salaryArea;
    }

    public SalaryUnitDto getSalaryUnit() {
        return salaryUnit;
    }

    public void setSalaryUnit(SalaryUnitDto salaryUnit) {
        this.salaryUnit = salaryUnit;
    }

    public SalaryUnitDto getSalaryInsuranceUnit() {
        return salaryInsuranceUnit;
    }

    public void setSalaryInsuranceUnit(SalaryUnitDto salaryInsuranceUnit) {
        this.salaryInsuranceUnit = salaryInsuranceUnit;
    }

    public List<FileDescriptionDto> getFiles() {
        return files;
    }

    public void setFiles(List<FileDescriptionDto> files) {
        this.files = files;
    }

    public SalaryTemplateDto getSalaryTemplate() {
        return salaryTemplate;
    }

    public void setSalaryTemplate(SalaryTemplateDto salaryTemplate) {
        this.salaryTemplate = salaryTemplate;
    }

    public ContractTypeDto getContractType() {
        return contractType;
    }

    public void setContractType(ContractTypeDto contractType) {
        this.contractType = contractType;
    }

    public Boolean getHasSocialIns() {
        return hasSocialIns;
    }

    public void setHasSocialIns(Boolean hasSocialIns) {
        this.hasSocialIns = hasSocialIns;
    }

    public Double getTotalInsuranceAmount() {
        return totalInsuranceAmount;
    }

    public void setTotalInsuranceAmount(Double totalInsuranceAmount) {
        this.totalInsuranceAmount = totalInsuranceAmount;
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

    public Integer getAgreementStatus() {
        return agreementStatus;
    }

    public void setAgreementStatus(Integer agreementStatus) {
        this.agreementStatus = agreementStatus;
    }

    public String getSocialInsuranceNumber() {
        return socialInsuranceNumber;
    }

    public void setSocialInsuranceNumber(String socialInsuranceNumber) {
        this.socialInsuranceNumber = socialInsuranceNumber;
    }

    public Integer getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(Integer paidStatus) {
        this.paidStatus = paidStatus;
    }

    public Double getInsuranceSalary() {
        return insuranceSalary;
    }

    public void setInsuranceSalary(Double insuranceSalary) {
        this.insuranceSalary = insuranceSalary;
    }

    public Double getStaffSocialInsurancePercentage() {
        return staffSocialInsurancePercentage;
    }

    public void setStaffSocialInsurancePercentage(Double staffSocialInsurancePercentage) {
        this.staffSocialInsurancePercentage = staffSocialInsurancePercentage;
    }

    public Double getStaffSocialInsuranceAmount() {
        return staffSocialInsuranceAmount;
    }

    public void setStaffSocialInsuranceAmount(Double staffSocialInsuranceAmount) {
        this.staffSocialInsuranceAmount = staffSocialInsuranceAmount;
    }

    public Double getStaffHealthInsurancePercentage() {
        return staffHealthInsurancePercentage;
    }

    public void setStaffHealthInsurancePercentage(Double staffHealthInsurancePercentage) {
        this.staffHealthInsurancePercentage = staffHealthInsurancePercentage;
    }

    public Double getStaffHealthInsuranceAmount() {
        return staffHealthInsuranceAmount;
    }

    public void setStaffHealthInsuranceAmount(Double staffHealthInsuranceAmount) {
        this.staffHealthInsuranceAmount = staffHealthInsuranceAmount;
    }

    public Double getStaffUnemploymentInsurancePercentage() {
        return staffUnemploymentInsurancePercentage;
    }

    public void setStaffUnemploymentInsurancePercentage(Double staffUnemploymentInsurancePercentage) {
        this.staffUnemploymentInsurancePercentage = staffUnemploymentInsurancePercentage;
    }

    public Double getStaffUnemploymentInsuranceAmount() {
        return staffUnemploymentInsuranceAmount;
    }

    public void setStaffUnemploymentInsuranceAmount(Double staffUnemploymentInsuranceAmount) {
        this.staffUnemploymentInsuranceAmount = staffUnemploymentInsuranceAmount;
    }

    public Double getStaffTotalInsuranceAmount() {
        return staffTotalInsuranceAmount;
    }

    public void setStaffTotalInsuranceAmount(Double staffTotalInsuranceAmount) {
        this.staffTotalInsuranceAmount = staffTotalInsuranceAmount;
    }

    public Double getOrgSocialInsurancePercentage() {
        return orgSocialInsurancePercentage;
    }

    public void setOrgSocialInsurancePercentage(Double orgSocialInsurancePercentage) {
        this.orgSocialInsurancePercentage = orgSocialInsurancePercentage;
    }

    public Double getOrgSocialInsuranceAmount() {
        return orgSocialInsuranceAmount;
    }

    public void setOrgSocialInsuranceAmount(Double orgSocialInsuranceAmount) {
        this.orgSocialInsuranceAmount = orgSocialInsuranceAmount;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public Double getOrgHealthInsurancePercentage() {
        return orgHealthInsurancePercentage;
    }

    public void setOrgHealthInsurancePercentage(Double orgHealthInsurancePercentage) {
        this.orgHealthInsurancePercentage = orgHealthInsurancePercentage;
    }

    public Double getOrgHealthInsuranceAmount() {
        return orgHealthInsuranceAmount;
    }

    public void setOrgHealthInsuranceAmount(Double orgHealthInsuranceAmount) {
        this.orgHealthInsuranceAmount = orgHealthInsuranceAmount;
    }

    public Double getOrgUnemploymentInsurancePercentage() {
        return orgUnemploymentInsurancePercentage;
    }

    public void setOrgUnemploymentInsurancePercentage(Double orgUnemploymentInsurancePercentage) {
        this.orgUnemploymentInsurancePercentage = orgUnemploymentInsurancePercentage;
    }

    public Double getOrgUnemploymentInsuranceAmount() {
        return orgUnemploymentInsuranceAmount;
    }

    public void setOrgUnemploymentInsuranceAmount(Double orgUnemploymentInsuranceAmount) {
        this.orgUnemploymentInsuranceAmount = orgUnemploymentInsuranceAmount;
    }

    public Double getOrgTotalInsuranceAmount() {
        return orgTotalInsuranceAmount;
    }

    public void setOrgTotalInsuranceAmount(Double orgTotalInsuranceAmount) {
        this.orgTotalInsuranceAmount = orgTotalInsuranceAmount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
