package com.globits.timesheet.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.dto.ShiftWorkTimePeriodDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.StaffWorkScheduleDto;
import com.globits.hr.dto.WorkingStatusDto;
import com.globits.task.dto.HrTaskDto;
import com.globits.timesheet.domain.TimeSheet;
import com.globits.timesheet.domain.TimeSheetDetail;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class TimeSheetDetailDto extends BaseObjectDto {
    private TimeSheetDto timeSheet;
    private Date startTime;
    private Date endTime;
    private double duration;
    private String workingItemTitle;
    private StaffDto employee;
    private ProjectActivityDto projectActivity;
    private ProjectDto project;
    private String description;
    private Integer approveStatus;
    private Integer priority;
    private WorkingStatusDto workingStatus;
    private HrTaskDto hrTask;
    private TimeSheetShiftWorkPeriodDto timeSheetShiftWorkPeriodDto;
    private String note;
    private StaffWorkScheduleDto staffWorkSchedule;
    private String addressIPCheckIn;
    private String addressIPCheckOut;
    private ShiftWorkTimePeriodDto shiftWorkTimePeriod;

    private String indexRowExcel;
    private String staffCode;
    private Date workingDate;
    private String shiftWorkCode;
    private String shiftWorkTimePeriodCode;
    private Boolean isSync;// đánh dấu là đồng bộ từ máy chấm công
    private String timekeepingCode ;//machamcong
    public TimeSheetDetailDto() {

    }

    public TimeSheetDetailDto(TimeSheetDetail timeSheetDetail) {
        if (timeSheetDetail != null) {
            this.setId(timeSheetDetail.getId());
            this.startTime = timeSheetDetail.getStartTime();
            this.endTime = timeSheetDetail.getEndTime();
            this.duration = timeSheetDetail.getDuration();
            this.description = timeSheetDetail.getDescription();
            this.workingItemTitle = timeSheetDetail.getWorkingItemTitle();
            this.approveStatus = timeSheetDetail.getApproveStatus();
            this.priority = timeSheetDetail.getPriority();
            this.note = timeSheetDetail.getNote();
            this.isSync=timeSheetDetail.getIsSync();
            this.timekeepingCode=timeSheetDetail.getTimekeepingCode();
            if (timeSheetDetail.getEmployee() != null) {
                //this.employee = new StaffDto(timeSheetDetail.getEmployee(),false);
                this.employee = new StaffDto();
                this.employee.setId(timeSheetDetail.getEmployee().getId());
                this.employee.setDisplayName(timeSheetDetail.getEmployee().getDisplayName());
                this.employee.setStaffCode(timeSheetDetail.getEmployee().getStaffCode());
                this.employee.setMainPosition(timeSheetDetail.getEmployee().getCurrentPositions());
            }
            if (timeSheetDetail.getActivity() != null) {
                this.projectActivity = new ProjectActivityDto(timeSheetDetail.getActivity(), false);
            }
            if (timeSheetDetail.getProject() != null) {
                this.project = new ProjectDto(timeSheetDetail.getProject(), false);
            }
            if (timeSheetDetail.getWorkingStatus() != null) {
                this.workingStatus = new WorkingStatusDto(timeSheetDetail.getWorkingStatus());
            }
            if (timeSheetDetail.getTimeSheetShiftWorkPeriod() != null) {
                this.timeSheetShiftWorkPeriodDto = new TimeSheetShiftWorkPeriodDto(timeSheetDetail.getTimeSheetShiftWorkPeriod());
            }
            if (timeSheetDetail.getTask() != null) {
                HrTaskDto task = new HrTaskDto();
                task.setId(timeSheetDetail.getTask().getId());
                task.setName(timeSheetDetail.getTask().getName());
                this.hrTask = task;
            }

            if (timeSheetDetail.getTimeSheet() != null) {
                TimeSheet sheet = timeSheetDetail.getTimeSheet();
                if (sheet != null) {
                    TimeSheetDto timeSheetDto = new TimeSheetDto();
                    timeSheetDto.setId(sheet.getId());
                    timeSheetDto.setStartTime(sheet.getStartTime());
                    timeSheetDto.setEndTime(sheet.getEndTime());
                    timeSheetDto.setWorkingDate(sheet.getWorkingDate());
                    if (sheet.getStaff() != null) {
                        StaffDto staff = new StaffDto();
                        staff.setId(timeSheetDetail.getEmployee().getId());
                        staff.setDisplayName(timeSheetDetail.getEmployee().getDisplayName());
                        staff.setStaffCode(timeSheetDetail.getEmployee().getStaffCode());
                        staff.setMainPosition(timeSheetDetail.getEmployee().getCurrentPositions());
                        timeSheetDto.setStaff(staff);
                        if (sheet.getYear() != null && sheet.getMonth() != null && sheet.getYear() != null) {
                            timeSheetDto.setDisplayName(sheet.getStaff().getDisplayName() + "-" + sheet.getDay() + "/" + (sheet.getMonth() + 1) + "/" + sheet.getYear());
                        }
                    }
                    this.timeSheet = timeSheetDto;
                    this.workingDate = timeSheet.getWorkingDate();
                }
            }
            if (timeSheetDetail.getStaffWorkSchedule() != null) {
                this.staffWorkSchedule = new StaffWorkScheduleDto(timeSheetDetail.getStaffWorkSchedule());
                this.staffWorkSchedule.setTimeSheetDetails(new ArrayList<>());
            }
            if (timeSheetDetail.getShiftWorkTimePeriod() != null) {
                this.shiftWorkTimePeriod = new ShiftWorkTimePeriodDto(timeSheetDetail.getShiftWorkTimePeriod());
            }
            this.addressIPCheckIn = timeSheetDetail.getAddressIPCheckIn();
            this.addressIPCheckOut = timeSheetDetail.getAddressIPCheckOut();
        }
    }

    public TimeSheetDetailDto(TimeSheetDetail timeSheetDetail, Boolean isFull) {
        if (timeSheetDetail != null) {
            this.setId(timeSheetDetail.getId());
            this.startTime = timeSheetDetail.getStartTime();
            this.endTime = timeSheetDetail.getEndTime();
            this.duration = timeSheetDetail.getDuration();
            this.description = timeSheetDetail.getDescription();
            this.workingItemTitle = timeSheetDetail.getWorkingItemTitle();
            this.approveStatus = timeSheetDetail.getApproveStatus();
            this.priority = timeSheetDetail.getPriority();
            this.note = timeSheetDetail.getNote();
            this.addressIPCheckIn = timeSheetDetail.getAddressIPCheckIn();
            this.addressIPCheckOut = timeSheetDetail.getAddressIPCheckOut();
            this.isSync=timeSheetDetail.getIsSync();
            this.timekeepingCode=timeSheetDetail.getTimekeepingCode();
            if (isFull) {
                if (timeSheetDetail.getEmployee() != null) {
                    //this.employee = new StaffDto(timeSheetDetail.getEmployee(),false);
                    this.employee = new StaffDto();
                    this.employee.setId(timeSheetDetail.getEmployee().getId());
                    this.employee.setDisplayName(timeSheetDetail.getEmployee().getDisplayName());
                    this.employee.setStaffCode(timeSheetDetail.getEmployee().getStaffCode());
                    this.employee.setMainPosition(timeSheetDetail.getEmployee().getCurrentPositions());
                }
                if (timeSheetDetail.getActivity() != null) {
                    this.projectActivity = new ProjectActivityDto(timeSheetDetail.getActivity(), false);
                }
                if (timeSheetDetail.getProject() != null) {
                    this.project = new ProjectDto(timeSheetDetail.getProject(), false);
                }
                if (timeSheetDetail.getWorkingStatus() != null) {
                    this.workingStatus = new WorkingStatusDto(timeSheetDetail.getWorkingStatus());
                }
                if (timeSheetDetail.getTimeSheetShiftWorkPeriod() != null) {
                    this.timeSheetShiftWorkPeriodDto = new TimeSheetShiftWorkPeriodDto(timeSheetDetail.getTimeSheetShiftWorkPeriod());
                }
                if (timeSheetDetail.getTask() != null) {
                    HrTaskDto task = new HrTaskDto();
                    task.setId(timeSheetDetail.getTask().getId());
                    task.setName(timeSheetDetail.getTask().getName());
                    this.hrTask = task;
                }

                if (timeSheetDetail.getTimeSheet() != null) {
                    TimeSheet sheet = timeSheetDetail.getTimeSheet();
                    if (sheet != null) {
                        TimeSheetDto timeSheetDto = new TimeSheetDto();
                        timeSheetDto.setId(sheet.getId());
                        timeSheetDto.setStartTime(sheet.getStartTime());
                        timeSheetDto.setEndTime(sheet.getEndTime());
                        timeSheetDto.setWorkingDate(sheet.getWorkingDate());
                        if (sheet.getStaff() != null) {
                            StaffDto staff = new StaffDto();
                            staff.setId(timeSheetDetail.getEmployee().getId());
                            staff.setDisplayName(timeSheetDetail.getEmployee().getDisplayName());
                            staff.setStaffCode(timeSheetDetail.getEmployee().getStaffCode());
                            staff.setMainPosition(timeSheetDetail.getEmployee().getCurrentPositions());
                            timeSheetDto.setStaff(staff);
                            if (sheet.getYear() != null && sheet.getMonth() != null && sheet.getYear() != null) {
                                timeSheetDto.setDisplayName(sheet.getStaff().getDisplayName() + "-" + sheet.getDay() + "/" + (sheet.getMonth() + 1) + "/" + sheet.getYear());
                            }
                        }
                        this.timeSheet = timeSheetDto;
                    }
                }
                if (timeSheetDetail.getStaffWorkSchedule() != null) {
                    this.staffWorkSchedule = new StaffWorkScheduleDto(timeSheetDetail.getStaffWorkSchedule());
                }
            }
        }
    }


    public TimeSheetDto getTimeSheet() {
        return timeSheet;
    }

    public void setTimeSheet(TimeSheetDto timeSheet) {
        this.timeSheet = timeSheet;
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

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getWorkingItemTitle() {
        return workingItemTitle;
    }

    public void setWorkingItemTitle(String workingItemTitle) {
        this.workingItemTitle = workingItemTitle;
    }

    public StaffDto getEmployee() {
        return employee;
    }

    public void setEmployee(StaffDto employee) {
        this.employee = employee;
    }

    public ProjectActivityDto getProjectActivity() {
        return projectActivity;
    }

    public void setProjectActivity(ProjectActivityDto projectActivity) {
        this.projectActivity = projectActivity;
    }

    public ProjectDto getProject() {
        return project;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(Integer approveStatus) {
        this.approveStatus = approveStatus;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public WorkingStatusDto getWorkingStatus() {
        return workingStatus;
    }

    public void setWorkingStatus(WorkingStatusDto workingStatus) {
        this.workingStatus = workingStatus;
    }

    public TimeSheetShiftWorkPeriodDto getTimeSheetShiftWorkPeriodDto() {
        return timeSheetShiftWorkPeriodDto;
    }

    public void setTimeSheetShiftWorkPeriodDto(TimeSheetShiftWorkPeriodDto timeSheetShiftWorkPeriodDto) {
        this.timeSheetShiftWorkPeriodDto = timeSheetShiftWorkPeriodDto;
    }

    public HrTaskDto getHrTask() {
        return hrTask;
    }

    public void setHrTask(HrTaskDto hrTask) {
        this.hrTask = hrTask;
    }

    public TimeSheetDetail toEntity(TimeSheetDetailDto dto, TimeSheetDetail entity) {
        entity.setId(dto.getId());
        entity.setWorkingItemTitle(dto.getWorkingItemTitle());
        entity.setDuration(dto.getDuration());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        return entity;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public StaffWorkScheduleDto getStaffWorkSchedule() {
        return staffWorkSchedule;
    }

    public void setStaffWorkSchedule(StaffWorkScheduleDto staffWorkSchedule) {
        this.staffWorkSchedule = staffWorkSchedule;
    }

    public String getAddressIPCheckIn() {
        return addressIPCheckIn;
    }

    public void setAddressIPCheckIn(String addressIPCheckIn) {
        this.addressIPCheckIn = addressIPCheckIn;
    }

    public String getAddressIPCheckOut() {
        return addressIPCheckOut;
    }

    public void setAddressIPCheckOut(String addressIPCheckOut) {
        this.addressIPCheckOut = addressIPCheckOut;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public Date getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(Date workingDate) {
        this.workingDate = workingDate;
    }

    public String getShiftWorkCode() {
        return shiftWorkCode;
    }

    public void setShiftWorkCode(String shiftWorkCode) {
        this.shiftWorkCode = shiftWorkCode;
    }

    public String getShiftWorkTimePeriodCode() {
        return shiftWorkTimePeriodCode;
    }

    public void setShiftWorkTimePeriodCode(String shiftWorkTimePeriodCode) {
        this.shiftWorkTimePeriodCode = shiftWorkTimePeriodCode;
    }

    public ShiftWorkTimePeriodDto getShiftWorkTimePeriod() {
        return shiftWorkTimePeriod;
    }

    public void setShiftWorkTimePeriod(ShiftWorkTimePeriodDto shiftWorkTimePeriod) {
        this.shiftWorkTimePeriod = shiftWorkTimePeriod;
    }

    public String getIndexRowExcel() {
        return indexRowExcel;
    }

    public void setIndexRowExcel(String indexRowExcel) {
        this.indexRowExcel = indexRowExcel;
    }

	public Boolean getIsSync() {
		return isSync;
	}

	public void setIsSync(Boolean isSync) {
		this.isSync = isSync;
	}

	public String getTimekeepingCode() {
		return timekeepingCode;
	}

	public void setTimekeepingCode(String timekeepingCode) {
		this.timekeepingCode = timekeepingCode;
	}

}
