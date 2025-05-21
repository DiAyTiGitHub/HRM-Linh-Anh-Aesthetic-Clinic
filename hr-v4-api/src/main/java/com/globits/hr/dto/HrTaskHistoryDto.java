package com.globits.hr.dto;

import com.globits.hr.domain.HrTaskHistory;
import com.globits.task.dto.HrTaskDto;


import com.globits.core.dto.BaseObjectDto;
import com.globits.core.utils.CoreDateTimeUtil;

import java.time.LocalDateTime;
import java.util.Date;

public class HrTaskHistoryDto extends BaseObjectDto {
    private String code;
    private HrTaskDto task;
    private StaffDto modifier;
    private Object event;

    private LocalDateTime createDate;

    private Date date;

    public HrTaskHistoryDto() {
    }

    public HrTaskHistoryDto(HrTaskHistory entity) {
        this.setId(entity.getId());
        this.code = entity.getCode();

        if (entity.getModifier() != null)
            this.modifier = new StaffDto(entity.getModifier());

        if (entity.getTask() != null) {
            this.task = new HrTaskDto();
            this.task.setName(entity.getTask().getName());
            this.task.setCode(entity.getTask().getCode());
            this.task.setId(entity.getTask().getId());
        }

//        this.task = new HrTaskDto(entity.getTask());

        this.createDate = entity.getCreateDate();
        this.date = CoreDateTimeUtil.convertToDateViaInstant(entity.getCreateDate());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getEvent() {
        return event;
    }

    public void setEvent(Object event) {
        this.event = event;
    }

    public HrTaskDto getTask() {
        return task;
    }

    public void setTask(HrTaskDto task) {
        this.task = task;
    }

    public StaffDto getModifier() {
        return modifier;
    }

    public void setModifier(StaffDto modifier) {
        this.modifier = modifier;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
