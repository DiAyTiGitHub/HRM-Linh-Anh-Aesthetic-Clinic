package com.globits.hr.dto;

import com.globits.core.dto.CountryDto;
import com.globits.hr.domain.StaffOverseasWorkHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globits.core.dto.BaseObjectDto;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StaffOverseasWorkHistoryDto extends BaseObjectDto {
    private static final Logger logger = LoggerFactory.getLogger(StaffOverseasWorkHistoryDto.class);
    private Date startDate;
    private Date endDate;
    private Date decisionDate;
    private String decisionNumber;
    private String purpose;
    private StaffDto staff;
    private CountryDto country;
    private String companyName;

    public StaffOverseasWorkHistoryDto() {

    }

    public StaffOverseasWorkHistoryDto(StaffOverseasWorkHistory entity) {
        if (entity != null) {
            setId(entity.getId());
            this.decisionNumber = entity.getDecisionNumber();
            this.purpose = entity.getPurpose();
            this.companyName = entity.getCompanyName();
            if (entity.getStaff() != null) {
                this.staff = new StaffDto(entity.getStaff(), false);
            }
            if (entity.getCountry() != null) {
                this.country = new CountryDto(entity.getCountry());
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            try {
                if (entity.getStartDate() != null) {
                    if (entity.getStartDate().before(simpleDateFormat.parse("01-01-1900"))
                            || entity.getStartDate().after(simpleDateFormat.parse("01-01-2100"))) {
                        this.startDate = null;
                    } else {
                        this.startDate = entity.getStartDate();
                    }
                }
                if (entity.getEndDate() != null) {
                    if (entity.getEndDate().before(simpleDateFormat.parse("01-01-1900"))
                            || entity.getStartDate().after(simpleDateFormat.parse("01-01-2100"))) {
                        this.endDate = null;
                    } else {
                        this.endDate = entity.getEndDate();
                    }
                }
                if (entity.getDecisionDate() != null) {
                    if (entity.getDecisionDate().before(simpleDateFormat.parse("01-01-1900"))
                            || entity.getDecisionDate().after(simpleDateFormat.parse("01-01-2100"))) {
                        this.decisionDate = null;
                    } else {
                        this.decisionDate = entity.getDecisionDate();
                    }
                }
            } catch (Exception e) {
                logger.error("ERROR : {}", e.getMessage(), e);
            }
        }
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

    public Date getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
        this.decisionDate = decisionDate;
    }

    public String getDecisionNumber() {
        return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
        this.decisionNumber = decisionNumber;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public CountryDto getCountry() {
        return country;
    }

    public void setCountry(CountryDto country) {
        this.country = country;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

}
