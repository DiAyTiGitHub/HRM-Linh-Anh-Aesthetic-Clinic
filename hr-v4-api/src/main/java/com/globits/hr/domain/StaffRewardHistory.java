package com.globits.hr.domain;

import java.util.Date;

import com.globits.core.domain.FileDescription;
import jakarta.persistence.*;


import com.globits.core.domain.BaseObject;
import com.globits.core.domain.Organization;

// Qúa trình khen thưởng
@Table(name = "tbl_staff_reward_history")
@Entity
public class StaffRewardHistory extends BaseObject {
    private static final long serialVersionUID = 795781122770892206L;

    @Column(name = "reward_date")
    private Date rewardDate; // Ngày khen thưong

    @ManyToOne
    @JoinColumn(name = "reward_from_id")
    private RewardForm rewardType; // Hình thức khen thưởng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Staff staff; // Nhân viên được khen thưởng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private HrOrganization organization; // Đơn vị khen thưởng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private HRDepartment department; // Phòng ban khen thưởng

    @Column(name = "organization_name")
    private String organizationName;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileDescription file; // Tài liệu đã được lưu


    public void setOrganization(HrOrganization organization) {
        this.organization = organization;
    }

    public FileDescription getFile() {
        return file;
    }

    public void setFile(FileDescription file) {
        this.file = file;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Organization getOrganization() {
        return organization;
    }


    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Date getRewardDate() {
        return rewardDate;
    }

    public void setRewardDate(Date rewardDate) {
        this.rewardDate = rewardDate;
    }

    public RewardForm getRewardType() {
        return rewardType;
    }

    public void setRewardType(RewardForm rewardType) {
        this.rewardType = rewardType;
    }

    public HRDepartment getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartment department) {
        this.department = department;
    }
}
