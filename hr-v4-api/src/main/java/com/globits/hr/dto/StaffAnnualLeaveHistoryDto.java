package com.globits.hr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.*;
import com.globits.salary.domain.SalaryResultStaff;
import jakarta.persistence.*;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class StaffAnnualLeaveHistoryDto extends BaseObjectDto {

    private StaffDto staff; // Nhân viên nào nghỉ phép
    private UUID staffId;
    private Integer year; // Năm thống kê nghỉ phép

    private Double grantedLeaveDays; // Số ngày nghỉ phép được cấp trong năm
    private String grantedLeaveDaysNote; // ghi chú
    private Double carriedOverLeaveDays; // Số ngày nghỉ phép được chuyển từ năm trước
    private String carriedOverLeaveDaysNote; // ghi chú
    private Double seniorityLeaveDays; // Số ngày nghỉ phép tăng theo thâm niên
    private String seniorityLeaveDaysNote; // ghi chú
    private Double bonusLeaveDays; // Số ngày nghỉ phép được thưởng khác
    private String bonusLeaveDaysNote; // ghi chú
    private Double cancelledLeaveDays; // Số ngày nghỉ phép bị hủy/không được dùng
    private String cancelledLeaveDaysNote; // ghi chú

    // Thống kê số ngày đã nghỉ theo từng tháng
    private List<StaffMonthlyLeaveHistoryDto> monthlyLeaveHistories;

    // BE tự tính trả về
    private Double totalUsedLeaveDays; // Số ngày nghỉ phép đã sử dụng


    public StaffAnnualLeaveHistoryDto() {

    }

    public StaffAnnualLeaveHistoryDto(StaffAnnualLeaveHistory entity) {
        if (entity == null) return;

        this.id = entity.getId();

        if (entity.getStaff() != null) {
            this.staffId = entity.getStaff().getId();

            this.staff = new StaffDto();
            this.staff.setId(entity.getStaff().getId());
            this.staff.setStaffCode(entity.getStaff().getStaffCode());
            this.staff.setDisplayName(entity.getStaff().getDisplayName());

            setMainPositionForStaff(this.staff, entity.getStaff().getCurrentPositions());

        }

        this.year = entity.getYear(); // Năm thống kê nghỉ phép
        this.grantedLeaveDays = entity.getGrantedLeaveDays(); // Số ngày nghỉ phép được cấp trong năm
        this.grantedLeaveDaysNote = entity.getGrantedLeaveDaysNote(); // ghi chú
        this.carriedOverLeaveDays = entity.getCarriedOverLeaveDays(); // Số ngày nghỉ phép được chuyển từ năm trước
        this.carriedOverLeaveDaysNote = entity.getCarriedOverLeaveDaysNote(); // ghi chú
        this.seniorityLeaveDays = entity.getSeniorityLeaveDays(); // Số ngày nghỉ phép tăng theo thâm niên
        this.seniorityLeaveDaysNote = entity.getSeniorityLeaveDaysNote(); // ghi chú
        this.bonusLeaveDays = entity.getBonusLeaveDays(); // Số ngày nghỉ phép được thưởng khác
        this.bonusLeaveDaysNote = entity.getBonusLeaveDaysNote(); // ghi chú
        this.cancelledLeaveDays = entity.getCancelledLeaveDays(); // Số ngày nghỉ phép bị hủy/không được dùng
        this.cancelledLeaveDaysNote = entity.getCancelledLeaveDaysNote(); // ghi chú


        this.totalUsedLeaveDays = 0D; // Số ngày nghỉ phép đã sử dụng

        if (entity.getMonthlyLeaveHistories() != null && !entity.getMonthlyLeaveHistories().isEmpty()) {
            List<StaffMonthlyLeaveHistoryDto> monthlyLeaveHistories = new ArrayList<>();

            for (StaffMonthlyLeaveHistory monthlyLeaveHistory : entity.getMonthlyLeaveHistories()) {
                StaffMonthlyLeaveHistoryDto historyItem = new StaffMonthlyLeaveHistoryDto(monthlyLeaveHistory);

                if (historyItem.getLeaveDays() == null) continue;

                this.totalUsedLeaveDays += historyItem.getLeaveDays();
            }
        }

    }

    public StaffAnnualLeaveHistoryDto(StaffAnnualLeaveHistory entity, boolean isDetail) {
        this(entity);

        if (entity == null || !isDetail) return;

        if (entity.getMonthlyLeaveHistories() != null && !entity.getMonthlyLeaveHistories().isEmpty()) {
            List<StaffMonthlyLeaveHistoryDto> monthlyLeaveHistories = new ArrayList<>();

            for (StaffMonthlyLeaveHistory monthlyLeaveHistory : entity.getMonthlyLeaveHistories()) {
                StaffMonthlyLeaveHistoryDto historyItem = new StaffMonthlyLeaveHistoryDto(monthlyLeaveHistory);

                monthlyLeaveHistories.add(historyItem);
            }

            Collections.sort(monthlyLeaveHistories, new Comparator<StaffMonthlyLeaveHistoryDto>() {
                @Override
                public int compare(StaffMonthlyLeaveHistoryDto o1, StaffMonthlyLeaveHistoryDto o2) {
                    // First, compare by displayOrder
                    if (o1.getMonth() == null && o2.getMonth() == null)
                        return 0;
                    if (o1.getMonth() == null)
                        return 1;
                    if (o2.getMonth() == null)
                        return -1;

                    int orderComparison = o1.getMonth().compareTo(o2.getMonth());
                    return orderComparison;
                }
            });

            this.monthlyLeaveHistories = monthlyLeaveHistories;
        }

    }


    // Lấy dữ liệu theo Organization - Department - CurrentPosition currentPositions (Position có isMain = true)
    private void setMainPositionForStaff(StaffDto staff, Set<Position> currentPositions) {
        if (currentPositions == null || currentPositions.isEmpty()) {
            return;
        }


        for (Position position : currentPositions) {
            if (position.getIsMain() == null || position.getIsMain().equals(false)) continue;
            //lấy vị trí hiện tại
            staff.setCurrentPosition(new PositionDto());

            staff.getCurrentPosition().setName(position.getName());
            staff.getCurrentPosition().setCode(position.getCode());
            staff.getCurrentPosition().setDescription(position.getDescription());

            if (position.getTitle() != null) {
                PositionTitle pt = position.getTitle();

                staff.setPositionTitle(new PositionTitleDto());
                staff.getPositionTitle().setId(pt.getId());
                staff.getPositionTitle().setCode(pt.getCode());
                staff.getPositionTitle().setName(pt.getName());

                if (pt.getRankTitle() != null) {
                    RankTitleDto rankTitle = new RankTitleDto();

                    rankTitle.setId(pt.getRankTitle().getId());
                    rankTitle.setName(pt.getRankTitle().getName());
                    rankTitle.setOtherName(pt.getRankTitle().getOtherName());
                    rankTitle.setShortName(pt.getRankTitle().getShortName());
                    rankTitle.setReferralFeeLevel(pt.getRankTitle().getReferralFeeLevel());

                    staff.getPositionTitle().setRankTitle(rankTitle);
                }
            }

            if (position.getDepartment() != null) {
                staff.setDepartment(new HRDepartmentDto());

                staff.getDepartment().setId(position.getDepartment().getId());
                staff.getDepartment().setCode(position.getDepartment().getCode());
                staff.getDepartment().setName(position.getDepartment().getName());

                if (position.getDepartment().getHrdepartmentType() != null) {
                    DepartmentTypeDto departmentType = new DepartmentTypeDto();
                    departmentType.setCode(position.getDepartment().getHrdepartmentType().getCode());
                    departmentType.setName(position.getDepartment().getHrdepartmentType().getName());

                    staff.getDepartment().setHrDepartmentType(departmentType);
                }

                if (position.getDepartment().getOrganization() != null) {
                    staff.setOrganization(new HrOrganizationDto());

                    staff.getOrganization().setId(position.getDepartment().getOrganization().getId());
                    staff.getOrganization().setCode(position.getDepartment().getOrganization().getCode());
                    staff.getOrganization().setName(position.getDepartment().getOrganization().getName());
                }
            }


            break;

        }
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getGrantedLeaveDays() {
        return grantedLeaveDays;
    }

    public void setGrantedLeaveDays(Double grantedLeaveDays) {
        this.grantedLeaveDays = grantedLeaveDays;
    }

    public String getGrantedLeaveDaysNote() {
        return grantedLeaveDaysNote;
    }

    public void setGrantedLeaveDaysNote(String grantedLeaveDaysNote) {
        this.grantedLeaveDaysNote = grantedLeaveDaysNote;
    }

    public Double getCarriedOverLeaveDays() {
        return carriedOverLeaveDays;
    }

    public void setCarriedOverLeaveDays(Double carriedOverLeaveDays) {
        this.carriedOverLeaveDays = carriedOverLeaveDays;
    }

    public String getCarriedOverLeaveDaysNote() {
        return carriedOverLeaveDaysNote;
    }

    public void setCarriedOverLeaveDaysNote(String carriedOverLeaveDaysNote) {
        this.carriedOverLeaveDaysNote = carriedOverLeaveDaysNote;
    }

    public Double getSeniorityLeaveDays() {
        return seniorityLeaveDays;
    }

    public void setSeniorityLeaveDays(Double seniorityLeaveDays) {
        this.seniorityLeaveDays = seniorityLeaveDays;
    }

    public String getSeniorityLeaveDaysNote() {
        return seniorityLeaveDaysNote;
    }

    public void setSeniorityLeaveDaysNote(String seniorityLeaveDaysNote) {
        this.seniorityLeaveDaysNote = seniorityLeaveDaysNote;
    }

    public Double getBonusLeaveDays() {
        return bonusLeaveDays;
    }

    public void setBonusLeaveDays(Double bonusLeaveDays) {
        this.bonusLeaveDays = bonusLeaveDays;
    }

    public String getBonusLeaveDaysNote() {
        return bonusLeaveDaysNote;
    }

    public void setBonusLeaveDaysNote(String bonusLeaveDaysNote) {
        this.bonusLeaveDaysNote = bonusLeaveDaysNote;
    }

    public Double getCancelledLeaveDays() {
        return cancelledLeaveDays;
    }

    public void setCancelledLeaveDays(Double cancelledLeaveDays) {
        this.cancelledLeaveDays = cancelledLeaveDays;
    }

    public String getCancelledLeaveDaysNote() {
        return cancelledLeaveDaysNote;
    }

    public void setCancelledLeaveDaysNote(String cancelledLeaveDaysNote) {
        this.cancelledLeaveDaysNote = cancelledLeaveDaysNote;
    }

    public List<StaffMonthlyLeaveHistoryDto> getMonthlyLeaveHistories() {
        return monthlyLeaveHistories;
    }

    public void setMonthlyLeaveHistories(List<StaffMonthlyLeaveHistoryDto> monthlyLeaveHistories) {
        this.monthlyLeaveHistories = monthlyLeaveHistories;
    }

    public Double getTotalUsedLeaveDays() {
        return totalUsedLeaveDays;
    }

    public void setTotalUsedLeaveDays(Double totalUsedLeaveDays) {
        this.totalUsedLeaveDays = totalUsedLeaveDays;
    }


}
