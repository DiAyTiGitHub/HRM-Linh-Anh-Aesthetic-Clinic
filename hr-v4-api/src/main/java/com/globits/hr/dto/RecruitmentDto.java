package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.*;
import jakarta.persistence.*;

import java.util.*;

//dot tuyen dung
public class RecruitmentDto extends BaseObjectDto {
    private static final long serialVersionUID = 991992518344617174L;

    // thong tin tuyen dung
    private String code; // ma dot tuyen
    private String name; // ten dot tuyen
    private RecruitmentPlanDto recruitmentPlan; // ke hoach
    private HrOrganizationDto organization;
    private HRDepartmentDto department;
//    private Integer quantity; // so luong tuyen
    private Date startDate; // ngay bat dau
    private Date endDate; // ngay ket thuc
    private String note; // ghi chu = yeu cau
//    private PositionTitleDto positionTitle; // Vị trí tuyển


    // thong tin lien he
    private StaffDto contactStaff; // ho va ten (nhan vien lien lac)
    private PositionDto positionCS; //  vi tri (chuc vu) cong tac nhan vien lien lac
    private HRDepartmentDto hrDepartmentCS; // department of contact staff - phong ban
    private String phoneNumber;
    private String officePhoneNumber;
    private String contactEmail;
    private String contactWebsite;

    //vong tuyen dung
    private List<RecruitmentRoundDto> recruitmentRounds;

    // for display only
    private Long numberAppliedCandidates;

    // vị trí tuyển trong đợt tuyển dụng
    private List<RecruitmentItemDto> recruitmentItems;

    public RecruitmentDto() {
    }

    public RecruitmentDto(Recruitment entity) {
        if (entity == null) return;

        this.id = entity.getId();
        this.name = entity.getName();
        this.code = entity.getCode();
        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();
        this.note = entity.getNote();

        if (entity.getRecruitmentPlan() != null && entity.getRecruitmentPlan().getId() != null) {
            this.recruitmentPlan = new RecruitmentPlanDto();
            this.recruitmentPlan.setId(entity.getRecruitmentPlan().getId());
            this.recruitmentPlan.setCode(entity.getRecruitmentPlan().getCode());
            this.recruitmentPlan.setName(entity.getRecruitmentPlan().getName());
        }
        if (entity.getHrDepartmentCS() != null && entity.getHrDepartmentCS().getId() != null) {
            this.hrDepartmentCS = new HRDepartmentDto();
            this.hrDepartmentCS.setId(entity.getHrDepartmentCS().getId());
            this.hrDepartmentCS.setCode(entity.getHrDepartmentCS().getCode());
            this.hrDepartmentCS.setName(entity.getHrDepartmentCS().getName());
            this.hrDepartmentCS.setShortName(entity.getHrDepartmentCS().getShortName());
            HRDepartment hrDepartmentCS = entity.getHrDepartmentCS();
            if (hrDepartmentCS.getOrganization() != null) {
                HrOrganization hrOrg = entity.getHrDepartmentCS().getOrganization();
                this.hrDepartmentCS.setOrganization(new HrOrganizationDto(hrOrg));
            }
        }
        if (entity.getOrganization() != null) {
            this.organization = new HrOrganizationDto();
            this.organization.setId(entity.getOrganization().getId());
            this.organization.setName(entity.getOrganization().getName());
            this.organization.setCode(entity.getOrganization().getCode());
        }
        if (entity.getDepartment() != null) {
            this.department = new HRDepartmentDto();
            this.department.setId(entity.getDepartment().getId());
            this.department.setName(entity.getDepartment().getName());
            this.department.setCode(entity.getDepartment().getCode());
        }
//        if (entity.getPositionTitle() != null) {
//            this.positionTitle = new PositionTitleDto();
//            this.positionTitle.setId(entity.getPositionTitle().getId());
//            this.positionTitle.setName(entity.getPositionTitle().getName());
//            this.positionTitle.setCode(entity.getPositionTitle().getCode());
//            this.positionTitle.setShortName(entity.getPositionTitle().getShortName());
//            this.positionTitle.setDescription(entity.getPositionTitle().getDescription());
//            this.positionTitle.setType(entity.getPositionTitle().getType());
//            this.positionTitle.setRecruitmentDays(entity.getPositionTitle().getRecruitmentDays());
//
//        }

    }

    public RecruitmentDto(Recruitment entity, Boolean isDetail) {
        this(entity);

        //thong tin tuyen dung
        if (isDetail == null || isDetail.equals(false)) return;

        //thong tin lien he
        if (entity.getContactStaff() != null && entity.getContactStaff().getId() != null) {
            this.contactStaff = new StaffDto();
            this.contactStaff.setId(entity.getContactStaff().getId());
            this.contactStaff.setStaffCode(entity.getContactStaff().getStaffCode());
            this.contactStaff.setDisplayName(entity.getContactStaff().getDisplayName());
        }

        if (entity.getPositionCS() != null && entity.getPositionCS().getId() != null) {
            this.positionCS = new PositionDto();
            this.positionCS.setId(entity.getPositionCS().getId());
            this.positionCS.setCode(entity.getPositionCS().getCode());
            this.positionCS.setName(entity.getPositionCS().getName());
        }

        this.phoneNumber = entity.getPhoneNumber();
        this.officePhoneNumber = entity.getOfficePhoneNumber();
        this.contactEmail = entity.getContactEmail();
        this.contactWebsite = entity.getContactWebsite();

        //vong tuyen dung
        this.recruitmentRounds = new ArrayList<>();
        if (entity.getRecruitmentRounds() != null && entity.getRecruitmentRounds().size() > 0) {
            for (RecruitmentRound round : entity.getRecruitmentRounds()) {
                this.recruitmentRounds.add(new RecruitmentRoundDto(round));
            }
        }
        Collections.sort(this.recruitmentRounds, new Comparator<RecruitmentRoundDto>() {
            @Override
            public int compare(RecruitmentRoundDto o1, RecruitmentRoundDto o2) {
                if (o1.getRoundOrder() == null && o2.getRoundOrder() == null) {
                    return 0;
                }
                if (o1.getRoundOrder() == null) {
                    return -1;
                }
                if (o2.getRoundOrder() == null) {
                    return 1;
                }
                return o1.getRoundOrder().compareTo(o2.getRoundOrder());
            }
        });

        if (entity.getDepartment() != null) {
            this.department = new HRDepartmentDto();
            this.department.setId(entity.getDepartment().getId());
            this.department.setName(entity.getDepartment().getName());
            this.department.setCode(entity.getDepartment().getCode());
        }
        if (entity.getOrganization() != null) {
            this.organization = new HrOrganizationDto();
            this.organization.setId(entity.getOrganization().getId());
            this.organization.setName(entity.getOrganization().getName());
            this.organization.setCode(entity.getOrganization().getCode());
        }
        if (entity.getRecruitmentItems() != null && !entity.getRecruitmentItems().isEmpty()) {
            this.recruitmentItems = new ArrayList<>();
            for (RecruitmentItem item : entity.getRecruitmentItems()) {
                this.recruitmentItems.add(new RecruitmentItemDto(item, false));
            }
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RecruitmentPlanDto getRecruitmentPlan() {
        return recruitmentPlan;
    }

    public void setRecruitmentPlan(RecruitmentPlanDto recruitmentPlan) {
        this.recruitmentPlan = recruitmentPlan;
    }
//
//    public Integer getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(Integer quantity) {
//        this.quantity = quantity;
//    }

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public StaffDto getContactStaff() {
        return contactStaff;
    }

    public void setContactStaff(StaffDto contactStaff) {
        this.contactStaff = contactStaff;
    }

    public HRDepartmentDto getHrDepartmentCS() {
        return hrDepartmentCS;
    }

    public void setHrDepartmentCS(HRDepartmentDto hrDepartmentCS) {
        this.hrDepartmentCS = hrDepartmentCS;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOfficePhoneNumber() {
        return officePhoneNumber;
    }

    public void setOfficePhoneNumber(String officePhoneNumber) {
        this.officePhoneNumber = officePhoneNumber;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactWebsite() {
        return contactWebsite;
    }

    public void setContactWebsite(String contactWebsite) {
        this.contactWebsite = contactWebsite;
    }

    public List<RecruitmentRoundDto> getRecruitmentRounds() {
        return recruitmentRounds;
    }

    public void setRecruitmentRounds(List<RecruitmentRoundDto> recruitmentRounds) {
        this.recruitmentRounds = recruitmentRounds;
    }

    public PositionDto getPositionCS() {
        return positionCS;
    }

    public void setPositionCS(PositionDto positionCS) {
        this.positionCS = positionCS;
    }

    public Long getNumberAppliedCandidates() {
        return numberAppliedCandidates;
    }

    public void setNumberAppliedCandidates(Long numberAppliedCandidates) {
        this.numberAppliedCandidates = numberAppliedCandidates;
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

//    public PositionTitleDto getPositionTitle() {
//        return positionTitle;
//    }
//
//    public void setPositionTitle(PositionTitleDto positionTitle) {
//        this.positionTitle = positionTitle;
//    }

    public List<RecruitmentItemDto> getRecruitmentItems() {
        return recruitmentItems;
    }

    public void setRecruitmentItems(List<RecruitmentItemDto> recruitmentItems) {
        this.recruitmentItems = recruitmentItems;
    }
}
