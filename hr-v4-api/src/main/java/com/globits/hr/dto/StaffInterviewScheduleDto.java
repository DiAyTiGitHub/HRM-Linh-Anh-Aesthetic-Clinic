package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.InterviewSchedule;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffInterviewSchedule;

public class StaffInterviewScheduleDto extends BaseObjectDto {
    private StaffDto interviewer;
    private Integer status;
    private String interviewRole; // Vai trò người phỏng vấn (chủ trì, thư ký...)
    private String note; // Ghi chú
    public StaffInterviewScheduleDto() {
    }

    public StaffInterviewScheduleDto(StaffInterviewSchedule entity) {
        super(entity);
        if(entity.getInterviewer() != null) {
            this.interviewer = new StaffDto(entity.getInterviewer(), false, false);
        }
        this.status = entity.getStatus();
        this.interviewRole = entity.getInterviewRole();
        this.note = entity.getNote();
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public StaffDto getInterviewer() {
        return interviewer;
    }

    public void setInterviewer(StaffDto interviewer) {
        this.interviewer = interviewer;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getInterviewRole() {
        return interviewRole;
    }

    public void setInterviewRole(String interviewRole) {
        this.interviewRole = interviewRole;
    }
}
