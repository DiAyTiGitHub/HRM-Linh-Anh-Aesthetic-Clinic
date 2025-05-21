package com.globits.hr.dto;
import com.globits.hr.domain.AllowanceSeniorityHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.globits.core.dto.BaseObjectDto;
public class AllowanceSeniorityHistoryDto extends BaseObjectDto {
    private static final Logger logger = LoggerFactory.getLogger(PositionStaffDto.class);
    private StaffDto staff;
    private Date startDate;
    private CivilServantCategoryDto quotaCode;
    private String note;
    private Double percentReceived;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public CivilServantCategoryDto getQuotaCode() {
        return quotaCode;
    }

    public void setQuotaCode(CivilServantCategoryDto quotaCode) {
        this.quotaCode = quotaCode;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getPercentReceived() {
        return percentReceived;
    }

    public void setPercentReceived(Double percentReceived) {
        this.percentReceived = percentReceived;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public AllowanceSeniorityHistoryDto(){
    }
    public AllowanceSeniorityHistoryDto(AllowanceSeniorityHistory entity){
        if (entity != null){
            setId(entity.getId());
            this.note = entity.getNote();
            this.percentReceived = entity.getPercentReceived();
            if (entity.getStaff() != null) {
                this.staff = new StaffDto(entity.getStaff(), false, false);
            }
            if (entity.getQuotaCode() != null){
                this.quotaCode = new CivilServantCategoryDto(entity.getQuotaCode());
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
            }
            catch (Exception e) {
                logger.error("ERROR : {}", e.getMessage(), e);
            }
        }
    }
}
