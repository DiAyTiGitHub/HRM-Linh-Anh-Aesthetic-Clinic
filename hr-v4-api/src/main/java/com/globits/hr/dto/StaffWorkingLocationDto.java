package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.StaffWorkingLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class StaffWorkingLocationDto extends BaseObjectDto {

    private StaffDto staff; // Nhân viên có địa điêm làm việc
    private UUID staffId; // Nhân viên có địa điêm làm việc
    private Boolean isMainLocation; // Là địa điểm làm việc chính
    private WorkplaceDto workplace;
    private UUID workplaceId;
    private String workingLocation; // workplace.name

    public StaffWorkingLocationDto() {
    }

    public StaffWorkingLocationDto(StaffWorkingLocation entity) {
        super(entity);

        if (entity == null) return;

        if (entity.getStaff() != null) {
            StaffDto dto = new StaffDto();
            dto.setId(entity.getStaff().getId());
            dto.setDisplayName(entity.getStaff().getDisplayName());
            dto.setStaffCode(entity.getStaff().getStaffCode());
            this.staff = dto;
            this.staffId = entity.getStaff().getId();
        }

        this.isMainLocation = entity.getIsMainLocation();
        this.workingLocation = entity.getWorkingLocation();
        if (entity.getWorkplace() != null) {
            WorkplaceDto dto = new WorkplaceDto(entity.getWorkplace());
            this.workplace = dto;
            this.workplaceId = entity.getWorkplace().getId();
        }
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public Boolean getIsMainLocation() {
        return isMainLocation;
    }

    public void setIsMainLocation(Boolean mainLocation) {
        isMainLocation = mainLocation;
    }

    public WorkplaceDto getWorkplace() {
        return workplace;
    }

    public void setWorkplace(WorkplaceDto workplace) {
        this.workplace = workplace;
    }

    public UUID getWorkplaceId() {
        return workplaceId;
    }

    public void setWorkplaceId(UUID workplaceId) {
        this.workplaceId = workplaceId;
    }

    public String getWorkingLocation() {
        return workingLocation;
    }

    public void setWorkingLocation(String workingLocation) {
        this.workingLocation = workingLocation;
    }


}
