package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.FileDescriptionDto;
import com.globits.hr.domain.StaffDisciplineHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class StaffDisciplineHistoryDto extends BaseObjectDto {
    private static final Logger logger = LoggerFactory.getLogger(StaffDisciplineHistoryDto.class);

    private Date disciplineDate; // Ngày quyết định kỷ luật
    private HRDisciplineDto discipline; // Hình thức kỷ luật
    private StaffDto staff; // Nhân viên bị kỷ luật
    private UUID staffId; // Nhân viên bị kỷ luật
    private HrOrganizationDto organization; // Đơn vị kỷ luật
    private HRDepartmentDto department; // Phòng ban kỷ luật
    private FileDescriptionDto file; // Tệp đính kèm

    public StaffDisciplineHistoryDto() {
    }

    public StaffDisciplineHistoryDto(StaffDisciplineHistory entity) {
        if (entity == null) return;

        if (entity.getId() != null) {
            setId(entity.getId());

            if (entity.getStaff() != null) {
                this.staffId = entity.getStaff().getId();
            }

            if (entity.getStaff() != null) {
                this.staffId = entity.getStaff().getId();
            }

            if (entity.getOrganization() != null) {
                this.organization = new HrOrganizationDto();

                this.organization.setId(entity.getOrganization().getId());
                this.organization.setCode(entity.getOrganization().getCode());
                this.organization.setName(entity.getOrganization().getName());
            }

            if (entity.getDepartment() != null) {
                this.department = new HRDepartmentDto();

                this.department.setId(entity.getDepartment().getId());
                this.department.setCode(entity.getDepartment().getCode());
                this.department.setName(entity.getDepartment().getName());
            }

            if (entity.getDiscipline() != null) {
                this.discipline = new HRDisciplineDto();

                this.discipline.setId(entity.getDiscipline().getId());
                this.discipline.setCode(entity.getDiscipline().getCode());
                this.discipline.setName(entity.getDiscipline().getName());
                this.discipline.setLevel(entity.getDiscipline().getLevel());
            }

            if (entity.getFile() != null) {
                this.file = new FileDescriptionDto(entity.getFile());
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            try {
                if (entity.getDisciplineDate() != null) {
                    if (entity.getDisciplineDate().before(simpleDateFormat.parse("01-01-1900"))
                            || entity.getDisciplineDate().after(simpleDateFormat.parse("01-01-2100"))) {
                        this.disciplineDate = null;
                    } else {
                        this.disciplineDate = entity.getDisciplineDate();
                    }
                }
            } catch (Exception e) {
                logger.error("ERROR : {}", e.getMessage(), e);
            }
        }
    }

    public StaffDisciplineHistoryDto(StaffDisciplineHistory entity, boolean isDetail) {
        this(entity);

        if (!isDetail) return;

        if (entity.getStaff() != null) {
            this.staff = new StaffDto();
            this.staff.setId(entity.getStaff().getId());
            this.staff.setStaffCode(entity.getStaff().getStaffCode());
            this.staff.setDisplayName(entity.getStaff().getDisplayName());
        }

    }

    public Date getDisciplineDate() {
        return disciplineDate;
    }

    public void setDisciplineDate(Date disciplineDate) {
        this.disciplineDate = disciplineDate;
    }

    public HRDisciplineDto getDiscipline() {
        return discipline;
    }

    public void setDiscipline(HRDisciplineDto discipline) {
        this.discipline = discipline;
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

    public HrOrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganizationDto organization) {
        this.organization = organization;
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    public FileDescriptionDto getFile() {
        return file;
    }

    public void setFile(FileDescriptionDto file) {
        this.file = file;
    }
}
