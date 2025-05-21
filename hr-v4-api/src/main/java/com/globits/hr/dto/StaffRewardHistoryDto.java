package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.FileDescriptionDto;
import com.globits.hr.domain.StaffRewardHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class StaffRewardHistoryDto extends BaseObjectDto {
    private static final Logger logger = LoggerFactory.getLogger(StaffRewardHistoryDto.class);
    private Date rewardDate; // Ngày khen thưong
    private RewardFormDto rewardType; // Hình thức khen thưởng
    private UUID staffId; // Nhân viên được khen thưởng
    private StaffDto staff; // Nhân viên được khen thưởng
    private HrOrganizationDto organization; // Đơn vị khen thưởng
    private HRDepartmentDto department; // Phòng ban khen thưởng
    private String organizationName;
    private FileDescriptionDto file; // Tài liệu đã được lưu

    public StaffRewardHistoryDto() {
    }

    public StaffRewardHistoryDto(StaffRewardHistory entity) {
        if (entity.getId() != null) {
            setId(entity.getId());

            this.organizationName = entity.getOrganizationName();

            if (entity.getStaff() != null) {
                this.staffId = entity.getStaff().getId();
            }

            if (entity.getOrganization() != null) {
                this.organization = new HrOrganizationDto();
                this.organization.setId(entity.getOrganization().getId());
                this.organization.setName(entity.getOrganization().getName());
                this.organization.setCode(entity.getOrganization().getCode());

                this.organizationName = entity.getOrganization().getName();
            }
            if (entity.getDepartment() != null) {
                this.department = new HRDepartmentDto(entity.getDepartment(), false, false);
            }

            if (entity.getRewardType() != null) {
                this.rewardType = new RewardFormDto(entity.getRewardType());
            }
            if (entity.getFile() != null) {
                this.file = new FileDescriptionDto(entity.getFile());
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            try {
                if (entity.getRewardDate() != null) {
                    if (entity.getRewardDate().before(simpleDateFormat.parse("01-01-1900"))
                            || entity.getRewardDate().after(simpleDateFormat.parse("01-01-2100"))) {
                        this.rewardDate = null;
                    } else {
                        this.rewardDate = entity.getRewardDate();
                    }
                }
            } catch (Exception e) {
                logger.error("ERROR : {}", e.getMessage(), e);
            }
        }
    }

    public Date getRewardDate() {
        return rewardDate;
    }

    public void setRewardDate(Date rewardDate) {
        this.rewardDate = rewardDate;
    }

    public RewardFormDto getRewardType() {
        return rewardType;
    }

    public void setRewardType(RewardFormDto rewardType) {
        this.rewardType = rewardType;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public HrOrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganizationDto organization) {
        this.organization = organization;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public FileDescriptionDto getFile() {
        return file;
    }

    public void setFile(FileDescriptionDto file) {
        this.file = file;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }
}
