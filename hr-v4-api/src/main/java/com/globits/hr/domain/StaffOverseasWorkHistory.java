package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.Country;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "tbl_staff_overseasWork_history")

public class StaffOverseasWorkHistory  extends BaseObject {
    @Column(name = "start_date")
    private Date startDate;
    @Column(name ="end_date")
    private Date endDate;
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "decision_number")
    private String decisionNumber;
    @Column(name = "decision_date")
    private Date decisionDate;
    @Column(name = "purpose")
    private String purpose;
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
    @ManyToOne
    @JoinColumn(name ="staff_id")
    private Staff staff;

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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDecisionNumber() {
        return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
        this.decisionNumber = decisionNumber;
    }

    public Date getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
        this.decisionDate = decisionDate;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
