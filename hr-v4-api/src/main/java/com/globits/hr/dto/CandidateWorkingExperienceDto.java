package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.DepartmentDto;
import com.globits.hr.domain.Candidate;
import com.globits.hr.domain.CandidateWorkingExperience;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.StaffWorkingHistory;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

// Kinh nghiệm làm việc của ứng viên ở các công ty/ tổ chức cũ
public class CandidateWorkingExperienceDto extends BaseObjectDto {
    private static final Logger logger = LoggerFactory.getLogger(PositionStaffDto.class);
    private UUID candidateId;
    private String companyName;

    // ngày bắt đầu làm việc
    private Date startDate;

    // ngày kết thúc làm việc
    private Date endDate;

    // vị trí làm việc
    private PositionDto position;

    // Mức lương trước khi nghỉ việc
    private Double salary;

    // Lý do nghỉ việc
    private String leavingReason;

    // Mô tả công việc
    private String decription;
    protected String oldPosition;

    public CandidateWorkingExperienceDto() {
    }

    public CandidateWorkingExperienceDto(CandidateWorkingExperience entity) {
        if (entity == null) return;

        setId(entity.getId());

        if (entity.getCandidate() != null) {
            this.candidateId = entity.getCandidate().getId();
        }

        this.companyName = entity.getCompanyName();
        if (entity.getPosition() != null) {
            this.position = new PositionDto(entity.getPosition());
        }

        this.salary = entity.getSalary();
        this.leavingReason = entity.getLeavingReason();
        this.decription = entity.getDecription();
        this.oldPosition = entity.getOldPosition();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if (entity.getStartDate().before(simpleDateFormat.parse("01-01-1900"))
                    || entity.getStartDate().after(simpleDateFormat.parse("01-01-2100"))) {
                this.startDate = null;
            } else {
                this.startDate = entity.getStartDate();
            }
            if (entity.getEndDate().before(simpleDateFormat.parse("01-01-1900"))
                    || entity.getEndDate().after(simpleDateFormat.parse("01-01-2100"))) {
                this.endDate = null;
            } else {
                this.endDate = entity.getEndDate();
            }
        } catch (Exception e) {
            logger.error("ERROR : {}", e.getMessage(), e);
        }
    }

    public UUID getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(UUID candidateId) {
        this.candidateId = candidateId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public PositionDto getPosition() {
        return position;
    }

    public void setPosition(PositionDto position) {
        this.position = position;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getLeavingReason() {
        return leavingReason;
    }

    public void setLeavingReason(String leavingReason) {
        this.leavingReason = leavingReason;
    }

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    public String getOldPosition() {
        return oldPosition;
    }

    public void setOldPosition(String oldPosition) {
        this.oldPosition = oldPosition;
    }
}
