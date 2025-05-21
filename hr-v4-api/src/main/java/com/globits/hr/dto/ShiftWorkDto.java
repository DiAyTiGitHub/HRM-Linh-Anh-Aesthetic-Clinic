package com.globits.hr.dto;

import com.globits.hr.domain.*;

import java.util.*;

import com.globits.core.dto.BaseObjectDto;

public class ShiftWorkDto extends BaseObjectDto {
    private String code;
    private String name;
    private Double totalHours;
    private Integer shiftWorkType; // Loại ca làm việc. Chi tiết: HrConstants.ShiftWorkType
    private List<ShiftWorkTimePeriodDto> timePeriods;
    private Double convertedWorkingHours; // Số giờ công quy đổi của ca làm việc
    private List<HRDepartmentDto> departments; // các phòng ban áp dụng ca làm việc này
    private String errorMessage; // Thông báo lỗi khi không thể tạo ca làm việc

    public ShiftWorkDto() {
        this.timePeriods = new ArrayList<>();
    }

    public ShiftWorkDto(ShiftWork shiftWork) {
        if (shiftWork == null) return;
        this.id = shiftWork.getId();
        this.code = shiftWork.getCode();
        this.name = shiftWork.getName();
        this.totalHours = shiftWork.getTotalHours();
        this.shiftWorkType = shiftWork.getShiftWorkType();
        this.convertedWorkingHours = shiftWork.getConvertedWorkingHours();

        if (shiftWork.getTimePeriods() != null && !shiftWork.getTimePeriods().isEmpty()) {
            this.timePeriods = new ArrayList<>();

            for (ShiftWorkTimePeriod period : shiftWork.getTimePeriods()) {
                this.timePeriods.add(new ShiftWorkTimePeriodDto(period, true));
            }

            Collections.sort(this.timePeriods, new Comparator<ShiftWorkTimePeriodDto>() {
                @Override
                public int compare(ShiftWorkTimePeriodDto o1, ShiftWorkTimePeriodDto o2) {
                    int start1 = getHourMinute(o1.getStartTime());
                    int start2 = getHourMinute(o2.getStartTime());
                    int end1 = getHourMinute(o1.getEndTime());
                    int end2 = getHourMinute(o2.getEndTime());

                    int result = Integer.compare(start1, start2); // So sánh startTime trước

                    if (result == 0) {
                        return Integer.compare(end1, end2); // Nếu trùng startTime thì so sánh endTime
                    }
                    return result;
                }

                // Hàm lấy Giờ & Phút dưới dạng số nguyên để so sánh
                private int getHourMinute(Date date) {
                    if (date == null) return 0;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                }
            });

        }
    }

    public ShiftWorkDto(ShiftWork shiftWork, boolean isDetail) {
        this(shiftWork);

        if (!isDetail) {
            return;
        }

        this.departments = new ArrayList<>();

        for (HrDepartmentShiftWork departmentShiftWork : shiftWork.getDepartmentShiftWorks()) {
            if (departmentShiftWork.getDepartment() == null) continue;

            HRDepartmentDto department = new HRDepartmentDto();

            department.setId(departmentShiftWork.getDepartment().getId());
            department.setCode(departmentShiftWork.getDepartment().getCode());
            department.setName(departmentShiftWork.getDepartment().getName());
            department.setSortNumber(departmentShiftWork.getDepartment().getSortNumber());

            this.departments.add(department);
        }

        Collections.sort(this.departments,
                Comparator.comparing(HRDepartmentDto::getSortNumber, Comparator.nullsLast(Comparator.naturalOrder())));

        this.departments = new ArrayList<>();
        if (shiftWork.getDepartmentShiftWorks() != null && !shiftWork.getDepartmentShiftWorks().isEmpty()) {
            for (HrDepartmentShiftWork departmentShiftWork : shiftWork.getDepartmentShiftWorks()) {
                if (departmentShiftWork.getDepartment() != null) {
                    HRDepartmentDto department = new HRDepartmentDto();
                    department.setId(departmentShiftWork.getDepartment().getId());
                    department.setCode(departmentShiftWork.getDepartment().getCode());
                    department.setName(departmentShiftWork.getDepartment().getName());
                    department.setDescription(departmentShiftWork.getDepartment().getDescription());

                    this.departments.add(department);
                }
            }
        }

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }

    public List<ShiftWorkTimePeriodDto> getTimePeriods() {
        return timePeriods;
    }

    public void setTimePeriods(List<ShiftWorkTimePeriodDto> timePeriods) {
        this.timePeriods = timePeriods;
    }

    public Integer getShiftWorkType() {
        return shiftWorkType;
    }

    public void setShiftWorkType(Integer shiftWorkType) {
        this.shiftWorkType = shiftWorkType;
    }

    public Double getConvertedWorkingHours() {
        return convertedWorkingHours;
    }

    public void setConvertedWorkingHours(Double convertedWorkingHours) {
        this.convertedWorkingHours = convertedWorkingHours;
    }

    public List<HRDepartmentDto> getDepartments() {
        return departments;
    }

    public void setDepartments(List<HRDepartmentDto> departments) {
        this.departments = departments;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
