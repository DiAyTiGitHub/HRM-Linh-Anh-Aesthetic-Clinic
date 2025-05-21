package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.task.domain.HrTaskLabel;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

// dot tuyen dung
@Table(name = "tbl_recruitment")
@Entity
public class Recruitment extends BaseObject {
    private static final long serialVersionUID = 991992518344617174L;

    // thong tin tuyen dung
    private String code; // ma dot tuyen
    private String name; // ten dot tuyen

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recruitment_plan_id")
    private RecruitmentPlan recruitmentPlan; // ke hoach
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private HrOrganization organization; // Đơn vị

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private HRDepartment department; // phong ban
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "position_title_id")
//    private PositionTitle positionTitle; // Vị trí tuyển

//    private Integer quantity; // so luong tuyen

    @OneToMany(mappedBy = "recruitment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecruitmentItem> recruitmentItems; // Các vị trí cần tuyển trong đợt


    private Date startDate; // ngay bat dau
    private Date endDate; // ngay ket thuc

    @Column(name = "note", columnDefinition = "MEDIUMTEXT")
    private String note; // ghi chu

    // thong tin lien he
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contact_staff_id")
    private Staff contactStaff; // ho va ten (nhan vien lien lac)

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_cs_id")
    private Position positionCS; //  vi tri (chuc vu) cong tac nhan vien lien lac

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_cs_id")
    private HRDepartment hrDepartmentCS; // department of contact staff - phong ban

    private String phoneNumber;
    private String officePhoneNumber;
    private String contactEmail;
    private String contactWebsite;

    //vong tuyen dung
    @OneToMany(mappedBy = "recruitment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecruitmentRound> recruitmentRounds;

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

    public RecruitmentPlan getRecruitmentPlan() {
        return recruitmentPlan;
    }

    public void setRecruitmentPlan(RecruitmentPlan recruitmentPlan) {
        this.recruitmentPlan = recruitmentPlan;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Staff getContactStaff() {
        return contactStaff;
    }

    public void setContactStaff(Staff contactStaff) {
        this.contactStaff = contactStaff;
    }

    public HRDepartment getHrDepartmentCS() {
        return hrDepartmentCS;
    }

    public void setHrDepartmentCS(HRDepartment hrDepartmentCS) {
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

    public Set<RecruitmentRound> getRecruitmentRounds() {
        return recruitmentRounds;
    }

    public void setRecruitmentRounds(Set<RecruitmentRound> recruitmentRounds) {
        this.recruitmentRounds = recruitmentRounds;
    }

    public Position getPositionCS() {
        return positionCS;
    }

    public void setPositionCS(Position positionCS) {
        this.positionCS = positionCS;
    }

	public HrOrganization getOrganization() {
		return organization;
	}

	public void setOrganization(HrOrganization organization) {
		this.organization = organization;
	}

	public HRDepartment getDepartment() {
		return department;
	}

	public void setDepartment(HRDepartment department) {
		this.department = department;
	}

    public Set<RecruitmentItem> getRecruitmentItems() {
        return recruitmentItems;
    }

    public void setRecruitmentItems(Set<RecruitmentItem> recruitmentItems) {
        this.recruitmentItems = recruitmentItems;
    }
}

