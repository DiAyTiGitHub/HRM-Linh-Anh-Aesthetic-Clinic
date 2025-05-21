package com.globits.timesheet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.data.types.TimeSheetRegStatus;
import com.globits.hr.domain.TimeSheetStaff;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.StaffWorkScheduleDto;
import com.globits.hr.dto.WorkingStatusDto;
import com.globits.task.dto.HrTaskDto;
import com.globits.timesheet.domain.TimeSheet;
import com.globits.timesheet.domain.TimeSheetDetail;
import com.globits.timesheet.domain.TimeSheetLabel;
import com.globits.timesheet.domain.TimeSheetShiftWorkPeriod;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class TimeSheetDto extends BaseObjectDto {

    private Date workingDate;

    private double totalHours;

    private WorkingStatusDto workingStatus;

    private Date startTime;

    private Date endTime;

    private StaffDto staff;

    private List<StaffDto> timeSheetStaff;

    private List<TimeSheetDetailDto> details;

    private List<TimeSheetShiftWorkPeriodDto> timeSheetShiftWorkPeriod;

    private List<LabelDto> labels;

    private Integer approveStatus;

    private ProjectDto project;

    private ProjectActivityDto activity;

    private String description;

    private Integer priority;

    private LocalDateTime createDate;

    private Integer year;

    private Integer month;

    private Integer day;

    private String displayName;

    private TimeSheetRegStatus regStatus;
    
    public TimeSheetDto() {

    }

    public TimeSheetDto(TimeSheet timeSheet) {
        if (timeSheet != null) {
            this.setId(timeSheet.getId());
            this.workingDate = timeSheet.getWorkingDate();
            this.startTime = timeSheet.getStartTime();
            this.endTime = timeSheet.getEndTime();
            this.totalHours = timeSheet.getTotalHours();
            this.approveStatus = timeSheet.getApproveStatus();
            this.description = timeSheet.getDescription();
            this.createDate = timeSheet.getCreateDate();
            this.priority = timeSheet.getPriority();
            this.year = timeSheet.getYear();
            this.month = timeSheet.getMonth();
            this.day = timeSheet.getDay();
            this.displayName = "";
            if (timeSheet.getStaff() != null) {
                this.displayName += timeSheet.getStaff().getDisplayName();
            }
            if (this.day != null) {
                this.displayName += "-" + this.day;
            }
            if (this.month != null) {
                this.displayName += "/" + (this.month + 1);
            }
            if (this.year != null) {
                this.displayName += "/" + this.year;
            }
            if (this.day == null || this.month == null || this.year == null) {
                if (this.workingDate != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    this.displayName += simpleDateFormat.format(this.workingDate);
                }
            }
            if (timeSheet.getWorkingStatus() != null) {
                this.workingStatus = new WorkingStatusDto(timeSheet.getWorkingStatus());
            }
            if (timeSheet.getProject() != null) {
                this.project = new ProjectDto(timeSheet.getProject(), false);
            }
            if (timeSheet.getStaff() != null) {
                this.staff = new StaffDto(timeSheet.getStaff(), false);
            }
            if (timeSheet.getActivity() != null) {
                this.activity = new ProjectActivityDto(timeSheet.getActivity());
            }
            if (timeSheet.getTimeSheetShiftWorkPeriod() != null) {
                this.timeSheetShiftWorkPeriod = new ArrayList<>();
                for (TimeSheetShiftWorkPeriod timeSheetShiftWorkPeriod : timeSheet.getTimeSheetShiftWorkPeriod()) {
                    this.timeSheetShiftWorkPeriod.add(new TimeSheetShiftWorkPeriodDto(timeSheetShiftWorkPeriod));
                }
            }

            Set<TimeSheetStaff> timeSheetStaffs = timeSheet.getTimeSheetStaffSet();
            if (timeSheetStaffs != null && !timeSheetStaffs.isEmpty()) {
                this.timeSheetStaff = new ArrayList<>();
                for (TimeSheetStaff sheetStaff : timeSheetStaffs) {
                    this.timeSheetStaff.add(new StaffDto(sheetStaff.getStaff(), true));
                }
            }

            if (timeSheet.getDetails() != null && !timeSheet.getDetails().isEmpty()) {
                this.details = new ArrayList<>();
                for (TimeSheetDetail detail : timeSheet.getDetails()) {
                    TimeSheetDetailDto detailDto = new TimeSheetDetailDto();
                    detailDto.setId(detail.getId());
                    detailDto.setDuration(detail.getDuration());
                    detailDto.setEndTime(detail.getEndTime());
                    detailDto.setStartTime(detail.getStartTime());
                    detailDto.setWorkingItemTitle(detail.getWorkingItemTitle());
                    detailDto.setDescription(detail.getDescription());
                    detailDto.setApproveStatus(detail.getApproveStatus());
                    detailDto.setPriority(detail.getPriority());
                    //return basic info of task
                    if (detail.getTask() != null && detail.getTask().getId() != null) {
                        detailDto.setHrTask(new HrTaskDto());
                        detailDto.getHrTask().setId(detail.getTask().getId());
                        detailDto.getHrTask().setName(detail.getTask().getName());
                    }

                    if (detail.getEmployee() != null) {
                        detailDto.setEmployee(new StaffDto(detail.getEmployee(), false));
                    }
                    if (detail.getProject() != null) {
                        detailDto.setProject(new ProjectDto(detail.getProject(), true));
                    }
                    if (detail.getActivity() != null) {
                        detailDto.setProjectActivity(new ProjectActivityDto(detail.getActivity(), false));
                    }
                    detailDto.setPriority(detail.getPriority());
                    if (detail.getWorkingStatus() != null) {
                        detailDto.setWorkingStatus(new WorkingStatusDto(detail.getWorkingStatus()));
                    }
                    if(detail.getStaffWorkSchedule()!=null) {
                    	detailDto.setStaffWorkSchedule(new StaffWorkScheduleDto(detail.getStaffWorkSchedule()));
                    }
                    details.add(detailDto);
                }
            }
//            if (timeSheet.getTimeSheetShiftWorkPeriod() != null && !timeSheet.getTimeSheetShiftWorkPeriod().isEmpty()){
//                this.timeSheetShiftWorkPeriod = new ArrayList<>();
//                for (TimeSheetShiftWorkPeriod timeSheetShiftWorkPeriod : timeSheet.getTimeSheetShiftWorkPeriod()){
//                    TimeSheetShiftWorkPeriodDto timeSheetShiftWorkPeriodDto = new TimeSheetShiftWorkPeriodDto();
//                    timeSheetShiftWorkPeriodDto.setId(timeSheetShiftWorkPeriod.getId());
//                    timeSheetShiftWorkPeriodDto.setNote(timeSheetShiftWorkPeriod.getNote());
//                    timeSheetShiftWorkPeriodDto.setWorkingFormat(timeSheetShiftWorkPeriod.getWorkingFormat());
//                    timeSheetShiftWorkPeriodDto.setShiftWorkTimePeriod(new ShiftWorkTimePeriodDto(timeSheetShiftWorkPeriod.getShiftWorkTimePeriod()));
//                    this.timeSheetShiftWorkPeriod.add(timeSheetShiftWorkPeriodDto);
//                }
//            }

            if (timeSheet.getLabels() != null) {
                this.labels = new ArrayList<>();
                for (TimeSheetLabel timeSheetLabel : timeSheet.getLabels()) {
                    this.labels.add(new LabelDto(timeSheetLabel.getLabel(), false));
                }
            }
            if (timeSheet.getRegStatus() != null) {
                this.regStatus = timeSheet.getRegStatus();
            }
        }
    }

    public TimeSheetDto(Date workingDate) {
        this.workingDate = workingDate;
    }

    public TimeSheetDto(TimeSheet timeSheet, boolean collapse) {
        if (timeSheet != null) {
            this.setId(timeSheet.getId());
            this.workingDate = timeSheet.getWorkingDate();
            this.startTime = timeSheet.getStartTime();
            this.endTime = timeSheet.getEndTime();
            this.totalHours = timeSheet.getTotalHours();
            this.approveStatus = timeSheet.getApproveStatus();
            this.description = timeSheet.getDescription();
            this.createDate = timeSheet.getCreateDate();
            this.priority = timeSheet.getPriority();
            this.year = timeSheet.getYear();
            this.month = timeSheet.getMonth();
            this.day = timeSheet.getDay();
            this.displayName = "";
            if (timeSheet.getStaff() != null) {
                this.displayName += timeSheet.getStaff().getDisplayName();
            }
            if (this.day != null) {
                this.displayName += "-" + this.day;
            }
            if (this.month != null) {
                this.displayName += "/" + (this.month + 1);
            }
            if (this.year != null) {
                this.displayName += "/" + this.year;
            }
            if ((this.day == null || this.month == null || this.year == null) && this.workingDate != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                this.displayName += simpleDateFormat.format(this.workingDate);
            }
            if (timeSheet.getTimeSheetShiftWorkPeriod() != null) {
                this.timeSheetShiftWorkPeriod = new ArrayList<>();
                for (TimeSheetShiftWorkPeriod timeSheetShiftWorkPeriod : timeSheet.getTimeSheetShiftWorkPeriod()) {
                    this.timeSheetShiftWorkPeriod.add(new TimeSheetShiftWorkPeriodDto(timeSheetShiftWorkPeriod, false));
                }
            }
            if (!collapse) {
                if (timeSheet.getActivity() != null) {
                    this.activity = new ProjectActivityDto(timeSheet.getActivity(), false);
                }

                if (timeSheet.getWorkingStatus() != null) {
                    this.workingStatus = new WorkingStatusDto(timeSheet.getWorkingStatus());
                }
                if (timeSheet.getProject() != null) {
                    this.project = new ProjectDto(timeSheet.getProject(), false);
                }
                if (timeSheet.getStaff() != null) {
                    this.staff = new StaffDto(timeSheet.getStaff(), false);
                }

//				Set<TimeSheetStaff> timeSheetStaffs = timeSheet.getTimeSheetStaffSet();
//				if (timeSheetStaffs != null) {
//					this.timeSheetStaff = new ArrayList<>();
//					for (TimeSheetStaff sheetStaff : timeSheetStaffs) {
//						StaffDto staffDto = new StaffDto(sheetStaff.getStaff(), true);
//						this.timeSheetStaff.add(staffDto);
//					}
//				}

                if (timeSheet.getLabels() != null) {
                    this.labels = new ArrayList<>();
                    for (TimeSheetLabel timeSheetLabel : timeSheet.getLabels()) {
                        this.labels.add(new LabelDto(timeSheetLabel.getLabel()));
                    }
                }

                if (timeSheet.getDetails() != null && !timeSheet.getDetails().isEmpty()) {
                    this.details = new ArrayList<>();
                    for (TimeSheetDetail detail : timeSheet.getDetails()) {
                        TimeSheetDetailDto detailDto = new TimeSheetDetailDto();
                        detailDto.setId(detail.getId());
                        detailDto.setDuration(detail.getDuration());
                        detailDto.setEndTime(detail.getEndTime());
                        detailDto.setStartTime(detail.getStartTime());
                        detailDto.setWorkingItemTitle(detail.getWorkingItemTitle());
                        details.add(detailDto);
                    }
                }
                this.regStatus = timeSheet.getRegStatus();
            }
        }
    }

    public List<LabelDto> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelDto> labels) {
        this.labels = labels;
    }

    public Date getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(Date workingDate) {
        this.workingDate = workingDate;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<TimeSheetDetailDto> getDetails() {
        return details;
    }

    public void setDetails(List<TimeSheetDetailDto> details) {
        this.details = details;
    }

    public WorkingStatusDto getWorkingStatus() {
        return workingStatus;
    }

    public void setWorkingStatus(WorkingStatusDto workingStatus) {
        this.workingStatus = workingStatus;
    }

    public Integer getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(Integer approveStatus) {
        this.approveStatus = approveStatus;
    }

    public ProjectDto getProject() {
        return project;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }

    public ProjectActivityDto getActivity() {
        return activity;
    }

    public void setActivity(ProjectActivityDto activity) {
        this.activity = activity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<StaffDto> getTimeSheetStaff() {
        return timeSheetStaff;
    }

    public void setTimeSheetStaff(List<StaffDto> timeSheetStaff) {
        this.timeSheetStaff = timeSheetStaff;
    }

    public List<TimeSheetShiftWorkPeriodDto> getTimeSheetShiftWorkPeriod() {
        return timeSheetShiftWorkPeriod;
    }

    public void setTimeSheetShiftWorkPeriod(List<TimeSheetShiftWorkPeriodDto> timeSheetShiftWorkPeriod) {
        this.timeSheetShiftWorkPeriod = timeSheetShiftWorkPeriod;
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

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public TimeSheetRegStatus getRegStatus() {
        return regStatus;
    }

    public void setRegStatus(TimeSheetRegStatus regStatus) {
        this.regStatus = regStatus;
    }

}
