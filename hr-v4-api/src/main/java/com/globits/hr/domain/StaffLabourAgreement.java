package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.salary.domain.SalaryArea;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.domain.SalaryUnit;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/*
 * Hợp đồng lao động với nhân viên
 */
@Table(name = "tbl_staff_labour_agreement")
@Entity
public class StaffLabourAgreement extends BaseObject {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "labour_agreement_type_id")
    private LabourAgreementType labourAgreementType;// loai hop dong

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contract_type_id")
    private ContractType contractType;// loai hop dong - theo thoi gian

    @Column(name = "labour_agreement_number")
    private String labourAgreementNumber;// so hop dong

    @Column(name = "start_date")
    private Date startDate; // ngay bat dau hieu luc

    @Column(name = "end_date")
    private Date endDate;// ngay ap dung cuoi cung

    @Column(name = "duration_months")
    private Integer durationMonths; // số tháng hợp đồng (chỉ áp dụng khi loại hợp đồng là xác định thời hạn)

    @Column(name = "working_hour")
    private Double workingHour;// gio cong chuan 1 ngay

    @Column(name = "working_hour_week_min")
    private Double workingHourWeekMin;// gio cong toi thieu 1 tuan

    @Column(name = "working_hour_week_max")
    private Double workingHourWeekMax;// gio cong toi da 1 tuan

    @Column(name = "working_place")
    private String workingPlace;// noi lam viec

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_area_id")
    private SalaryArea salaryArea;// vung luong

    @Column(name = "salary")
    private Double salary;// muc luong

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_unit_id")
    private SalaryUnit salaryUnit;// don vi tinh luong (gio - thang - ...)

    @Column(name = "signed_date")
    private Date signedDate;// Ngày ký

    // 11/02/2025 meeting
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contract_organization_id")
    private HrOrganization contractOrganization; // Đơn vị ký hợp đồng

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "work_organization_id")
    private HrOrganization workOrganization; // Đơn vị làm việc

    // 14/02/2025 meeting
    @Column(name = "agreement_status")
    private Integer agreementStatus; // Trạng thái hợp đồng. Chi tiết HrConstants.StaffLabourAgreementStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_template_id")
    private SalaryTemplate salaryTemplate; // mẫu bảng lương

    @OneToMany(mappedBy = "staffLabourAgreement", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<StaffLabourAgreementAttachment> attachments = new HashSet<StaffLabourAgreementAttachment>();// file hdld

    //bhxh
    @Column(name = "social_insurance_number", nullable = true)
    private String socialInsuranceNumber;// Số sổ bảo hiểm xã hội

    @Column(name = "has_social_ins")
    private Boolean hasSocialIns; // Có đóng BHXH hay không

    @Column(name = "start_ins_date")
    private Date startInsDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_insurance_unit_id")
    private SalaryUnit salaryInsuranceUnit;// don vi tinh luong bhxh (gio - thang - ...)

    // Mức lương tham gia bảo hiểm xã hội
    @Column(name = "insurance_salary")
    private Double insuranceSalary;

    // Tỷ lệ đóng BHXH của nhân viên
    @Column(name = "staff_si_percentage")
    private Double staffSocialInsurancePercentage;

    // Tỷ lệ đóng BHYT của nhân viên
    @Column(name = "staff_hi_percentage")
    private Double staffHealthInsurancePercentage;

    // Tỷ lệ đóng BHTN của nhân viên
    @Column(name = "staff_ui_percentage")
    private Double staffUnemploymentInsurancePercentage;

    // Tổng tiền bảo hiểm mà nhân viên đóng
    @Column(name = "staff_total_insurance")
    private Double staffTotalInsuranceAmount;

    // Tỷ lệ đóng BHXH của công ty
    @Column(name = "org_si_percentage")
    private Double orgSocialInsurancePercentage;

    // Tỷ lệ đóng BHYT của công ty
    @Column(name = "org_hi_percentage")
    private Double orgHealthInsurancePercentage;

    // Tỷ lệ đóng BHTN của công ty
    @Column(name = "org_ui_percentage")
    private Double orgUnemploymentInsurancePercentage;

    // Tổng tiền bảo hiểm mà công ty đóng
    @Column(name = "org_total_insurance")
    private Double orgTotalInsuranceAmount;

    @Column(name = "paid_status")
    private Integer paidStatus; // Bảo hiểm này của nhan vien da duoc tra (dong) hay chua. Chi tiet: HrConstants.StaffSocialInsurancePaidStatus

    @Column(name = "total_insurance_amount")
    private Double totalInsuranceAmount;


    @Column(name = "insurance_start_date")
    private Date insuranceStartDate;//Ngày bắt đầu mức đóng

    @Column(name = "insurance_end_date")
    private Date insuranceEndDate;//Ngày kết thúc mức đóng
//
//    @Column(name = "insurance_salary_coefficient")
//    private Double insuranceSalaryCoefficient;//Hệ số lương đó bảo hiểm xã hội



    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public LabourAgreementType getLabourAgreementType() {
        return labourAgreementType;
    }

    public void setLabourAgreementType(LabourAgreementType labourAgreementType) {
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


    public Set<StaffLabourAgreementAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<StaffLabourAgreementAttachment> attachments) {
        this.attachments = attachments;
    }

    public StaffLabourAgreement() {
        this.setUuidKey(UUID.randomUUID());
    }

    public String getLabourAgreementNumber() {
        return labourAgreementNumber;
    }

    public void setLabourAgreementNumber(String labourAgreementNumber) {
        this.labourAgreementNumber = labourAgreementNumber;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public Double getWorkingHour() {
        return workingHour;
    }

    public void setWorkingHour(Double workingHour) {
        this.workingHour = workingHour;
    }

    public Double getWorkingHourWeekMin() {
        return workingHourWeekMin;
    }

    public void setWorkingHourWeekMin(Double workingHourWeekMin) {
        this.workingHourWeekMin = workingHourWeekMin;
    }

    public Double getWorkingHourWeekMax() {
        return workingHourWeekMax;
    }

    public void setWorkingHourWeekMax(Double workingHourWeekMax) {
        this.workingHourWeekMax = workingHourWeekMax;
    }

    public String getWorkingPlace() {
        return workingPlace;
    }

    public void setWorkingPlace(String workingPlace) {
        this.workingPlace = workingPlace;
    }

    public SalaryArea getSalaryArea() {
        return salaryArea;
    }

    public void setSalaryArea(SalaryArea salaryArea) {
        this.salaryArea = salaryArea;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public SalaryUnit getSalaryUnit() {
        return salaryUnit;
    }

    public void setSalaryUnit(SalaryUnit salaryUnit) {
        this.salaryUnit = salaryUnit;
    }

    public SalaryUnit getSalaryInsuranceUnit() {
        return salaryInsuranceUnit;
    }

    public void setSalaryInsuranceUnit(SalaryUnit salaryInsuranceUnit) {
        this.salaryInsuranceUnit = salaryInsuranceUnit;
    }

    public SalaryTemplate getSalaryTemplate() {
        return salaryTemplate;
    }

    public void setSalaryTemplate(SalaryTemplate salaryTemplate) {
        this.salaryTemplate = salaryTemplate;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public Boolean getHasSocialIns() {
        return hasSocialIns;
    }

    public void setHasSocialIns(Boolean hasSocialIns) {
        this.hasSocialIns = hasSocialIns;
    }

    public Date getStartInsDate() {
        return startInsDate;
    }

    public void setStartInsDate(Date startInsDate) {
        this.startInsDate = startInsDate;
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

    public Double getStaffHealthInsurancePercentage() {
        return staffHealthInsurancePercentage;
    }

    public void setStaffHealthInsurancePercentage(Double staffHealthInsurancePercentage) {
        this.staffHealthInsurancePercentage = staffHealthInsurancePercentage;
    }

    public Double getStaffUnemploymentInsurancePercentage() {
        return staffUnemploymentInsurancePercentage;
    }

    public void setStaffUnemploymentInsurancePercentage(Double staffUnemploymentInsurancePercentage) {
        this.staffUnemploymentInsurancePercentage = staffUnemploymentInsurancePercentage;
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

    public Double getOrgHealthInsurancePercentage() {
        return orgHealthInsurancePercentage;
    }

    public void setOrgHealthInsurancePercentage(Double orgHealthInsurancePercentage) {
        this.orgHealthInsurancePercentage = orgHealthInsurancePercentage;
    }

    public Double getOrgUnemploymentInsurancePercentage() {
        return orgUnemploymentInsurancePercentage;
    }

    public void setOrgUnemploymentInsurancePercentage(Double orgUnemploymentInsurancePercentage) {
        this.orgUnemploymentInsurancePercentage = orgUnemploymentInsurancePercentage;
    }

    public Double getOrgTotalInsuranceAmount() {
        return orgTotalInsuranceAmount;
    }

    public void setOrgTotalInsuranceAmount(Double orgTotalInsuranceAmount) {
        this.orgTotalInsuranceAmount = orgTotalInsuranceAmount;
    }

    public Integer getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(Integer paidStatus) {
        this.paidStatus = paidStatus;
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

//    public Double getInsuranceSalaryCoefficient() {
//        return insuranceSalaryCoefficient;
//    }
//
//    public void setInsuranceSalaryCoefficient(Double insuranceSalaryCoefficient) {
//        this.insuranceSalaryCoefficient = insuranceSalaryCoefficient;
//    }

    public HrOrganization getContractOrganization() {
        return contractOrganization;
    }

    public void setContractOrganization(HrOrganization contractOrganization) {
        this.contractOrganization = contractOrganization;
    }

    public HrOrganization getWorkOrganization() {
        return workOrganization;
    }

    public void setWorkOrganization(HrOrganization workOrganization) {
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
}
