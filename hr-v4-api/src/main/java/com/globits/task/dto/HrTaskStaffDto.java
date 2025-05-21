package com.globits.task.dto;

import com.globits.hr.data.types.HrTaskStatus;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.StaffDto;
import com.globits.task.domain.HrTaskStaff;

import java.util.Date;

public class HrTaskStaffDto extends BaseObjectDto{
    private StaffDto staffDto;
    private HrTaskDto taskDto;
    private HrTaskStatus status;//Trạng thái: mới tại, đã nhận việc, đang làm việc, đã hoàn thành, đã hủy bỏ
    private Date dateAssign;//Ngày giao việc
    private Date dateFinished;//Ngày kết thúc việc
    private String role;//Vai trò trong công việc: chỉ đạo chung, hỗ trợ, test, làm chính...

    public HrTaskStaffDto() {
    }

    public HrTaskStaffDto(HrTaskStaff entity) {
        this.id = entity.getId();
        if(entity.getStaff() != null){
            this.staffDto = new StaffDto(entity.getStaff(),true);
        }
        if(entity.getTask() != null){
            HrTaskDto taskDto = new HrTaskDto();
            taskDto.setId(entity.getTask().getId());
            taskDto.setName(entity.getTask().getName());
            this.taskDto = taskDto;
        }
        if(entity.getStatus()!=null){
            this.status = entity.getStatus();
        }
        this.dateAssign = entity.getDateAssign();
        this.dateFinished = entity.getDateFinished();
        this.role = entity.getRole();
    }

    public StaffDto getStaffDto() {
        return staffDto;
    }

    public void setStaffDto(StaffDto staffDto) {
        this.staffDto = staffDto;
    }

    public HrTaskDto getTaskDto() {
        return taskDto;
    }

    public void setTaskDto(HrTaskDto taskDto) {
        this.taskDto = taskDto;
    }

    public HrTaskStatus getStatus() {
        return status;
    }

    public void setStatus(HrTaskStatus status) {
        this.status = status;
    }

    public Date getDateAssign() {
        return dateAssign;
    }

    public void setDateAssign(Date dateAssign) {
        this.dateAssign = dateAssign;
    }

    public Date getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(Date dateFinished) {
        this.dateFinished = dateFinished;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
