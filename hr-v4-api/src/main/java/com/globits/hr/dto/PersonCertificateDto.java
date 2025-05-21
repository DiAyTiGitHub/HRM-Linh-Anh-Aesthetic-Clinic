package com.globits.hr.dto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import com.globits.core.dto.FileDescriptionDto;
import com.globits.core.dto.PersonDto;
import com.globits.hr.domain.*;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.utils.Const;

public class PersonCertificateDto extends BaseObjectDto {
    private PersonDto person;
    private StaffDto staff;
    private CertificateDto certificate;
    private Date issueDate;
    private String level;
    private String name;
    private String personCode;
    private String certificateType;
    private FileDescriptionDto certificateFile;

    public PersonCertificateDto() {

    }

    public PersonCertificateDto(PersonCertificate entity) {
        super(entity);

        if (entity == null) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if (entity.getIssueDate() != null) {
                if (entity.getIssueDate().before(sdf.parse("01-01-1900")) || entity.getIssueDate().after(sdf.parse("01-01-2100"))) {
                    this.issueDate = null;
                } else {
                    this.issueDate = entity.getIssueDate();
                }
            }
        } catch (Exception e) {
        }

        this.level = entity.getLevel();
        this.name = entity.getName();
        if (entity.getPerson() != null) {
            this.person = new PersonDto(entity.getPerson());
        }
        if (entity.getCertificate() != null) {
            this.certificate = new CertificateDto(entity.getCertificate());
        }
        if (entity.getCertificateFile() != null) {
            this.certificateFile = new FileDescriptionDto(entity.getCertificateFile());
        }
    }

    public PersonCertificateDto(PersonCertificate entity, Staff staff) {
        super(entity);

        if (entity == null) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if (entity.getIssueDate() != null) {
                if (entity.getIssueDate().before(sdf.parse("01-01-1900")) || entity.getIssueDate().after(sdf.parse("01-01-2100"))) {
                    this.issueDate = null;
                } else {
                    this.issueDate = entity.getIssueDate();
                }
            }
        } catch (Exception e) {
        }

        this.level = entity.getLevel();
        this.name = entity.getName();
        if (entity.getPerson() != null) {
            this.person = new PersonDto(entity.getPerson());
        }
        if (entity.getCertificate() != null) {
            this.certificate = new CertificateDto(entity.getCertificate());
        }
        if (entity.getCertificateFile() != null) {
            this.certificateFile = new FileDescriptionDto(entity.getCertificateFile());
        }

        if (staff == null) return;

        this.staff = new StaffDto();
        this.staff.setId(staff.getId());
        this.staff.setStaffCode(staff.getStaffCode());
        this.staff.setDisplayName(staff.getDisplayName());

        setMainPositionForStaff(this.staff, staff.getCurrentPositions());
    }


    // Lấy dữ liệu theo Organization - Department - CurrentPosition currentPositions (Position có isMain = true)
    private void setMainPositionForStaff(StaffDto staff, Set<Position> currentPositions) {
        if (currentPositions == null || currentPositions.isEmpty()) {
            return;
        }


        for (Position position : currentPositions) {
            if (position.getIsMain() == null || position.getIsMain().equals(false)) continue;
            //lấy vị trí hiện tại
            staff.setCurrentPosition(new PositionDto());

            staff.getCurrentPosition().setName(position.getName());
            staff.getCurrentPosition().setCode(position.getCode());
            staff.getCurrentPosition().setDescription(position.getDescription());

            if (position.getTitle() != null) {
                PositionTitle pt = position.getTitle();

                staff.setPositionTitle(new PositionTitleDto());
                staff.getPositionTitle().setId(pt.getId());
                staff.getPositionTitle().setCode(pt.getCode());
                staff.getPositionTitle().setName(pt.getName());

                if (pt.getRankTitle() != null) {
                    RankTitleDto rankTitle = new RankTitleDto();

                    rankTitle.setId(pt.getRankTitle().getId());
                    rankTitle.setName(pt.getRankTitle().getName());
                    rankTitle.setOtherName(pt.getRankTitle().getOtherName());
                    rankTitle.setShortName(pt.getRankTitle().getShortName());
                    rankTitle.setReferralFeeLevel(pt.getRankTitle().getReferralFeeLevel());

                    staff.getPositionTitle().setRankTitle(rankTitle);
                }
            }

            if (position.getDepartment() != null) {
                staff.setDepartment(new HRDepartmentDto());

                staff.getDepartment().setId(position.getDepartment().getId());
                staff.getDepartment().setCode(position.getDepartment().getCode());
                staff.getDepartment().setName(position.getDepartment().getName());

                if (position.getDepartment().getHrdepartmentType() != null) {
                    DepartmentTypeDto departmentType = new DepartmentTypeDto();
                    departmentType.setCode(position.getDepartment().getHrdepartmentType().getCode());
                    departmentType.setName(position.getDepartment().getHrdepartmentType().getName());

                    staff.getDepartment().setHrDepartmentType(departmentType);
                }

                if (position.getDepartment().getOrganization() != null) {
                    staff.setOrganization(new HrOrganizationDto());

                    staff.getOrganization().setId(position.getDepartment().getOrganization().getId());
                    staff.getOrganization().setCode(position.getDepartment().getOrganization().getCode());
                    staff.getOrganization().setName(position.getDepartment().getOrganization().getName());
                }
            }


            break;

        }
    }


    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public PersonDto getPerson() {
        return person;
    }

    public void setPerson(PersonDto person) {
        this.person = person;
    }

    public CertificateDto getCertificate() {
        return certificate;
    }

    public void setCertificate(CertificateDto certificate) {
        this.certificate = certificate;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPersonCode() {
        return personCode;
    }

    public void setPersonCode(String personCode) {
        this.personCode = personCode;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileDescriptionDto getCertificateFile() {
        return certificateFile;
    }

    public void setCertificateFile(FileDescriptionDto certificateFile) {
        this.certificateFile = certificateFile;
    }
}
