package com.globits.salary.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.staff.StaffSocialInsuranceDto;
import com.globits.salary.domain.SalaryResultItem;
import com.globits.salary.domain.StaffAdvancePayment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

// Thành phần lương trong bảng lương
public class StaffAdvancePaymentDto extends BaseObjectDto {
    private static final Logger logger = LoggerFactory.getLogger(StaffAdvancePaymentDto.class);

    private StaffDto staff; // Nhân viên xin ứng truước
    private SalaryPeriodDto salaryPeriod; // Kỳ lương xin ứng
    private Date requestDate; // Ngày xin ứng tiền
    private String requestReason; // Lý do tạm ứng tiền
    private Double advancedAmount; // Số tiền ứng trước
    private Integer approvalStatus; // Trạng thái xác nhận. Chi tiết trong: HrConstants.StaffAdvancePaymentApprovalStatus

    public StaffAdvancePaymentDto() {
    }

    public StaffAdvancePaymentDto(StaffAdvancePayment entity, Boolean isDetail) {
        super(entity);
        this.requestDate = entity.getRequestDate();
        this.requestReason = entity.getRequestReason();
        this.advancedAmount = entity.getAdvancedAmount();
        this.approvalStatus = entity.getApprovalStatus();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if (entity.getRequestDate() != null) {
                if (entity.getRequestDate().before(sdf.parse("01-01-1900")) || entity.getRequestDate().after(sdf.parse("01-01-2100"))) {
                    this.requestDate = null;
                } else {
                    this.requestDate = entity.getRequestDate();
                }
            }
        } catch (Exception e) {
            logger.error("ERROR : {}", e.getMessage(), e);
        }
        if (isDetail) {
            if (entity.getStaff() != null) {
                this.staff = new StaffDto(entity.getStaff(), false);
            }
            if (entity.getSalaryPeriod() != null) {
                this.salaryPeriod = new SalaryPeriodDto(entity.getSalaryPeriod(), false);
            }
        }


    }

    public StaffAdvancePaymentDto(StaffAdvancePayment entity) {
        this(entity, true);
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public SalaryPeriodDto getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(SalaryPeriodDto salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public Double getAdvancedAmount() {
        return advancedAmount;
    }

    public void setAdvancedAmount(Double advancedAmount) {
        this.advancedAmount = advancedAmount;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

}
