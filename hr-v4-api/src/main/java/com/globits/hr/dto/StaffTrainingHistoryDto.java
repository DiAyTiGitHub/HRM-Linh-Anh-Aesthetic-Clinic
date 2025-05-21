package com.globits.hr.dto;

import com.globits.core.dto.CountryDto;
import com.globits.hr.domain.StaffTrainingHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globits.core.dto.BaseObjectDto;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StaffTrainingHistoryDto extends BaseObjectDto {
    private static final Logger logger = LoggerFactory.getLogger(StaffTrainingHistoryDto.class);
    private Date startDate;
    private Date endDate;
    private StaffDto staff;
    private CertificateDto certificate;
    private CountryDto trainingCountry;
    private String trainingPlace;
    private String trainingContent;

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

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public CertificateDto getCertificate() {
        return certificate;
    }

    public void setCertificate(CertificateDto certificate) {
        this.certificate = certificate;
    }

    public CountryDto getTrainingCountry() {
        return trainingCountry;
    }

    public void setTrainingCountry(CountryDto trainingCountry) {
        this.trainingCountry = trainingCountry;
    }

    public String getTrainingPlace() {
        return trainingPlace;
    }

    public void setTrainingPlace(String trainingPlace) {
        this.trainingPlace = trainingPlace;
    }

    public String getTrainingContent() {
        return trainingContent;
    }

    public void setTrainingContent(String trainingContent) {
        this.trainingContent = trainingContent;
    }

    public StaffTrainingHistoryDto() {
    }

    public StaffTrainingHistoryDto(StaffTrainingHistory entity) {
        if (entity != null) {
            setId(entity.getId());
            this.trainingContent = entity.getTrainingContent();
            this.trainingPlace = entity.getTrainingPlace();
            if (entity.getStaff() != null) {
                this.staff = new StaffDto(entity.getStaff(), false);
            }
            if (entity.getTrainingCountry() != null) {
                this.trainingCountry = new CountryDto(entity.getTrainingCountry());
            }
            if (entity.getCertificate() != null) {
                this.certificate = new CertificateDto(entity.getCertificate());
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            try {
                if (entity.getStartDate() != null) {
                    if (entity.getStartDate().before(simpleDateFormat.parse("01-01-1900")) || entity.getStartDate().after(simpleDateFormat.parse("01-01-2100"))) {
                        this.startDate = null;
                    } else {
                        this.startDate = entity.getStartDate();
                    }
                }
                if (entity.getEndDate() != null) {
                    if (entity.getEndDate().before(simpleDateFormat.parse("01-01-1900")) || entity.getEndDate().after(simpleDateFormat.parse("01-01-2100"))) {
                        this.endDate = null;
                    } else {
                        this.endDate = entity.getEndDate();
                    }
                }
            } catch (Exception e) {
                logger.error("ERROR : {}", e.getMessage(), e);
            }
        }
    }
}

