package com.globits.salary.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.domain.SalaryPeriod;

public class SalaryPeriodDto extends BaseObjectDto {

    private String code;        // ky cong/ky luong
    private String name;        // ma ky cong/ky luong
    private String description; // mo ta them = mo ta ky luong
    private Date fromDate;
    private Date toDate;
    private SalaryPeriodDto parentPeriod; // thuộc kỳ lương nào. VD: tháng 9 chi trả 2 kì lương => có 2 kì lương con
    private List<SalaryPeriodDto> subPeriods; // các kỳ lương con thuộc kì lương này

    public SalaryPeriodDto() {
    }

    public SalaryPeriodDto(SalaryPeriod entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.code = entity.getCode();
            this.description = entity.getDescription();
            this.fromDate = entity.getFromDate();
            this.toDate = entity.getToDate();
        }
    }

    public SalaryPeriodDto(SalaryPeriod entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || isDetail.equals(false)) return;

        if (entity.getParentPeriod() != null) {
            this.parentPeriod = new SalaryPeriodDto();
            this.parentPeriod.setId(entity.getParentPeriod().getId());
            this.parentPeriod.setCode(entity.getParentPeriod().getCode());
            this.parentPeriod.setName(entity.getParentPeriod().getName());
        }

        if (entity.getSubPeriods() != null && entity.getSubPeriods().size() > 0) {
            List<SalaryPeriodDto> subPeriods = new ArrayList<>();

            for (SalaryPeriod period : entity.getSubPeriods()) {
                subPeriods.add(new SalaryPeriodDto(period));
            }

            Collections.sort(subPeriods, new Comparator<SalaryPeriodDto>() {
                @Override
                public int compare(SalaryPeriodDto o1, SalaryPeriodDto o2) {
                    // Compare by fromDate
                    int fromDateComparison = o1.getFromDate().compareTo(o2.getFromDate());
                    if (fromDateComparison != 0) {
                        return fromDateComparison;
                    }

                    // Compare by toDate
                    int toDateComparison = o1.getToDate().compareTo(o2.getToDate());
                    if (toDateComparison != 0) {
                        return toDateComparison;
                    }

                    // Compare by name
                    return o1.getName().compareTo(o2.getName());
                }
            });

            this.subPeriods = subPeriods;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public SalaryPeriodDto getParentPeriod() {
        return parentPeriod;
    }

    public void setParentPeriod(SalaryPeriodDto parentPeriod) {
        this.parentPeriod = parentPeriod;
    }

    public List<SalaryPeriodDto> getSubPeriods() {
        return subPeriods;
    }

    public void setSubPeriods(List<SalaryPeriodDto> subPeriods) {
        this.subPeriods = subPeriods;
    }
}
