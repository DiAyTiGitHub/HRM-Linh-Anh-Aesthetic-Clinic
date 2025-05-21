package com.globits.hr.dto.staff;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.StaffSocialInsurance;
import com.globits.hr.dto.StaffDto;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.dto.SalaryResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class StaffSocialInsuranceDto extends BaseObjectDto {
    private static final Logger logger = LoggerFactory.getLogger(StaffSocialInsuranceDto.class);

    private StaffDto staff;

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

    private Integer paidStatus; // Bảo hiểm này của nhan vien da duoc tra (dong) hay chua. Chi tiet: HrConstants.StaffSocialInsurancePaidStatus

    private Date startDate;//Ngày bắt đầu mức đóng

    private Date endDate;//Ngày kết thúc mức đóng

    // Ghi chú
    private String note;

    private SalaryPeriodDto salaryPeriod; // kỳ lương nào

    private SalaryResultDto salaryResult;

    private UUID staffId;
    private String staffName;
    private String staffCode;
    private String mainOrganization;
    private String mainDepartment;
    private String mainPositionTitle;
    private String mainPosition;


    public StaffSocialInsuranceDto() {
    }

    public StaffSocialInsuranceDto(StaffSocialInsurance entity, Boolean isGetFull) {
        super(entity);
        if (entity != null) {
            this.insuranceSalary = entity.getInsuranceSalary();

            this.staffSocialInsurancePercentage = entity.getStaffSocialInsurancePercentage();
            this.staffHealthInsurancePercentage = entity.getStaffHealthInsurancePercentage();
            this.staffUnemploymentInsurancePercentage = entity.getStaffUnemploymentInsurancePercentage();

            this.orgSocialInsurancePercentage = entity.getOrgSocialInsurancePercentage();
            this.orgHealthInsurancePercentage = entity.getOrgHealthInsurancePercentage();
            this.orgUnemploymentInsurancePercentage = entity.getOrgUnemploymentInsurancePercentage();
            this.startDate = entity.getStartDate();
            this.endDate = entity.getEndDate();
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
            this.note = entity.getNote();
            this.paidStatus = entity.getPaidStatus();
            if (isGetFull) {
                if (entity.getStaff() != null) {
//                    this.staff = new StaffDto(entity.getStaff(), false, false);
                    this.staff = new StaffDto();
                    this.staff.setId(entity.getStaff().getId());
                    this.staff.setStaffCode(entity.getStaff().getStaffCode());
                    this.staff.setDisplayName(entity.getStaff().getDisplayName());
                }
                if (entity.getSalaryPeriod() != null) {
                    this.salaryPeriod = new SalaryPeriodDto(entity.getSalaryPeriod(), false);
                }
                if (entity.getSalaryResult() != null) {
                    this.salaryResult = new SalaryResultDto(entity.getSalaryResult(), false);
                }
            }
        }
    }

    public StaffSocialInsuranceDto(StaffSocialInsurance entity) {
        this(entity, true);
    }


    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getMainOrganization() {
        return mainOrganization;
    }

    public void setMainOrganization(String mainOrganization) {
        this.mainOrganization = mainOrganization;
    }

    public String getMainDepartment() {
        return mainDepartment;
    }

    public void setMainDepartment(String mainDepartment) {
        this.mainDepartment = mainDepartment;
    }

    public String getMainPositionTitle() {
        return mainPositionTitle;
    }

    public void setMainPositionTitle(String mainPositionTitle) {
        this.mainPositionTitle = mainPositionTitle;
    }

    public String getMainPosition() {
        return mainPosition;
    }

    public void setMainPosition(String mainPosition) {
        this.mainPosition = mainPosition;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public Double getTotalInsuranceAmount() {
        return totalInsuranceAmount;
    }

    public void setTotalInsuranceAmount(Double totalInsuranceAmount) {
        this.totalInsuranceAmount = totalInsuranceAmount;
    }

    public SalaryPeriodDto getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(SalaryPeriodDto salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

    public SalaryResultDto getSalaryResult() {
        return salaryResult;
    }

    public void setSalaryResult(SalaryResultDto salaryResult) {
        this.salaryResult = salaryResult;
    }

    public Integer getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(Integer paidStatus) {
        this.paidStatus = paidStatus;
    }
}
