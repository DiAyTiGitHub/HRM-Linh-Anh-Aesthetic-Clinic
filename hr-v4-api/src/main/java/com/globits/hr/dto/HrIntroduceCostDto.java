package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.*;
import com.globits.hr.utils.Const;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.timesheet.domain.AbsenceRequest;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

public class HrIntroduceCostDto extends BaseObjectDto {
    private Integer periodOrder; // thứ tự hiển thị cột tháng tính giới thiệu đợt

    private StaffDto staff; // Nhân viên giới thiệu
    private RankTitleDto rankTitle; // Cấp bậc hiện tại của nhân viên giới thiệu
    private Date startDate; // Ngày bắt đầu làm việc của nhân viên giới thiệu
    private Double referralFeeLevel; // Tổng chi phí giới thiệu theo level của người giới thiệu

    private StaffDto introducedStaff; // Nhân viên được giới thiệu
    private Date introStaffStartDate; // Ngày vào làm việc của nhân viên được giới thiệu
    private Date officialDate; // Ngày nhân viên đó bắt đầu chính thức
    private Date sixMonthsWorking; // 6 tháng làm việc của nhân viên được giới thiệu

    private Date introducePeriod; // Ngày tính giới thiệu đợt 1
    private Double cost; // Chi phí nhân viên giới thiệu được hưởng đợt 1

    private Date introducePeriod2; // Ngày tính giới thiệu đợt 2
    private Double cost2; // Chi phí nhân viên giới thiệu được hưởng đợt 2

    private Date introducePeriod3; // Ngày tính giới thiệu đợt 3
    private Double cost3; // Chi phí nhân viên giới thiệu được hưởng đợt 3

    private String note; // Ghi chú


    public HrIntroduceCostDto(HrIntroduceCost entity, Boolean isGetFull) {
        this(entity);

        if (isGetFull) {

        }
    }

    public HrIntroduceCostDto(HrIntroduceCost entity) {
        super(entity);

        if (entity == null) return;
        this.periodOrder = entity.getPeriodOrder(); // thứ tự hiển thị cột tháng tính giới thiệu đợt

        // Nhân viên giới thiệu
        if (entity.getStaff() != null) {
            this.staff = new StaffDto();
            this.staff.setId(entity.getStaff().getId());
            this.staff.setStaffCode(entity.getStaff().getStaffCode());
            this.staff.setDisplayName(entity.getStaff().getDisplayName());
        }


        StaffDto detailStaff = null;
        if (entity.getStaff() != null) {
            detailStaff = new StaffDto(entity.getStaff(), false);
        }

        StaffDto detailIntroducedStaff = null;
        if (entity.getIntroducedStaff() != null) {
            detailIntroducedStaff = new StaffDto(entity.getIntroducedStaff());
        }


        this.referralFeeLevel = 0D; // Tổng chi phí giới thiệu theo level của người giới thiệu
        if (detailIntroducedStaff != null && detailIntroducedStaff.getPositionTitle() != null && detailIntroducedStaff.getPositionTitle().getRankTitle() != null) {
            this.rankTitle = new RankTitleDto();
            this.rankTitle.setId(detailIntroducedStaff.getPositionTitle().getRankTitle().getId());
            this.rankTitle.setName(detailIntroducedStaff.getPositionTitle().getRankTitle().getName());
            this.rankTitle.setShortName(detailIntroducedStaff.getPositionTitle().getRankTitle().getShortName());
            this.rankTitle.setDescription(detailIntroducedStaff.getPositionTitle().getRankTitle().getDescription());

            this.referralFeeLevel = detailIntroducedStaff.getPositionTitle().getRankTitle().getReferralFeeLevel();

        }

        if (detailStaff != null)
            this.startDate = detailStaff.getStartDate(); // Ngày bắt đầu làm việc của nhân viên giới thiệu


        if (entity.getIntroducedStaff() != null) {
            this.introducedStaff = new StaffDto(); // Nhân viên được giới thiệu

            this.introducedStaff.setId(entity.getIntroducedStaff().getId());
            this.introducedStaff.setStaffCode(entity.getIntroducedStaff().getStaffCode());
            this.introducedStaff.setDisplayName(entity.getIntroducedStaff().getDisplayName());
            if (entity.getIntroducedStaff() != null && entity.getIntroducedStaff().getDepartment() != null) {
                HRDepartmentDto departmentDto = new HRDepartmentDto();
                departmentDto.setId(entity.getIntroducedStaff().getDepartment().getId());
                departmentDto.setName(entity.getIntroducedStaff().getDepartment().getName());
                departmentDto.setCode(entity.getIntroducedStaff().getDepartment().getCode());
                this.introducedStaff.setDepartment(departmentDto);
            }
        }
        if (detailIntroducedStaff != null) {
            this.introStaffStartDate = detailIntroducedStaff.getRecruitmentDate(); // Ngày vào làm việc của nhân viên được giới thiệu
            this.officialDate = detailIntroducedStaff.getStartDate(); // Ngày bắt đầu chính thức
            this.sixMonthsWorking = DateTimeUtil.getDateAfterSixMonths(this.introStaffStartDate); // Ngày sau 6 tháng làm việc
        }

        this.introducePeriod = entity.getIntroducePeriod(); // Ngày tính giới thiệu đợt 1
        this.cost = entity.getCost(); // Chi phí nhân viên giới thiệu được hưởng đợt 1

        this.introducePeriod2 = entity.getIntroducePeriod2(); // Ngày tính giới thiệu đợt 2
        this.cost2 = entity.getCost2(); // Chi phí nhân viên giới thiệu được hưởng đợt 2

        this.introducePeriod3 = entity.getIntroducePeriod3(); // Ngày tính giới thiệu đợt 3
        this.cost3 = entity.getCost3(); // Chi phí nhân viên giới thiệu được hưởng đợt 3

        this.note = entity.getNote(); // Ghi chú
    }

    public HrIntroduceCostDto() {
    }


    public Date getOfficialDate() {
        return officialDate;
    }

    public void setOfficialDate(Date officialDate) {
        this.officialDate = officialDate;
    }

    public Date getSixMonthsWorking() {
        return sixMonthsWorking;
    }

    public void setSixMonthsWorking(Date sixMonthsWorking) {
        this.sixMonthsWorking = sixMonthsWorking;
    }

    public RankTitleDto getRankTitle() {
        return rankTitle;
    }

    public void setRankTitle(RankTitleDto rankTitle) {
        this.rankTitle = rankTitle;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Double getReferralFeeLevel() {
        return referralFeeLevel;
    }

    public void setReferralFeeLevel(Double referralFeeLevel) {
        this.referralFeeLevel = referralFeeLevel;
    }

    public StaffDto getIntroducedStaff() {
        return introducedStaff;
    }

    public void setIntroducedStaff(StaffDto introducedStaff) {
        this.introducedStaff = introducedStaff;
    }

    public Date getIntroStaffStartDate() {
        return introStaffStartDate;
    }

    public void setIntroStaffStartDate(Date introStaffStartDate) {
        this.introStaffStartDate = introStaffStartDate;
    }

    public Date getIntroducePeriod2() {
        return introducePeriod2;
    }

    public void setIntroducePeriod2(Date introducePeriod2) {
        this.introducePeriod2 = introducePeriod2;
    }

    public Double getCost2() {
        return cost2;
    }

    public void setCost2(Double cost2) {
        this.cost2 = cost2;
    }

    public Date getIntroducePeriod3() {
        return introducePeriod3;
    }

    public void setIntroducePeriod3(Date introducePeriod3) {
        this.introducePeriod3 = introducePeriod3;
    }

    public Double getCost3() {
        return cost3;
    }

    public void setCost3(Double cost3) {
        this.cost3 = cost3;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public Integer getPeriodOrder() {
        return periodOrder;
    }

    public void setPeriodOrder(Integer periodOrder) {
        this.periodOrder = periodOrder;
    }

    public Date getIntroducePeriod() {
        return introducePeriod;
    }

    public void setIntroducePeriod(Date introducePeriod) {
        this.introducePeriod = introducePeriod;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
