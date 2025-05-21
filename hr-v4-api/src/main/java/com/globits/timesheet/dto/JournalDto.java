package com.globits.timesheet.dto;

import java.util.Date;
import java.util.UUID;

import com.globits.timesheet.domain.Journal;

public class JournalDto {
    private UUID id;
    private UUID staffId;
    private String name;
    private String description;
    private Date journalDate;
    private Date fromDate;
    private Date toDate;
    private String location;
    private Integer type;

    public JournalDto() {
    }

    public JournalDto(Journal entity) {
        if(entity!=null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.description = entity.getDescription();
            this.journalDate = entity.getJournalDate();
            if(entity.getStaff()!=null){
                this.staffId = entity.getStaff().getId();
            }
            if(entity.getType()!=null){
                if(entity.getType()==1) {
                    this.fromDate = entity.getFromDate();
                    this.toDate = entity.getToDate();
                    this.location = entity.getLocation();
                }
                this.type = entity.getType();
            }
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getJournalDate() {
        return journalDate;
    }

    public void setJournalDate(Date journalDate) {
        this.journalDate = journalDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }
}
