package com.globits.salary.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.StaffDto;
import com.globits.salary.domain.SalaryResult;
import com.globits.salary.domain.SalaryResultStaff;

public class SalaryResultDto extends BaseObjectDto {

    private String code;
    private String name;
    private String description;
    private SalaryTemplateDto salaryTemplate;
    private SalaryPeriodDto salaryPeriod;
    private List<SalaryResultStaffDto> salaryResultStaffs;
    private List<StaffDto> staffs;
    private List<SalaryResultItemGroupDto> resultItemGroups;
    private List<SalaryResultItemDto> resultItems;
    private List<SalaryTemplateItemDto> templateItems;
    private List<SalaryTemplateItemGroupDto> templateItemGroups;

    private Boolean isLocked; // Bảng lương đã bị khóa hay chưa
    private Integer approvalStatus; // Trạng thái duyệt của bảng lương. Chi tiết trong: HrConstants.SalaryResulStaffApprovalStatus


    public SalaryResultDto() {
    }

    public SalaryResultDto(SalaryResult entity) {
        if (entity == null)
            return;
        this.id = entity.getId();
        this.name = entity.getName();
        this.code = entity.getCode();
        this.description = entity.getDescription();
        this.approvalStatus = entity.getApprovalStatus();
        this.isLocked = entity.getIsLocked();

        if (entity.getSalaryPeriod() != null) {
            this.salaryPeriod = new SalaryPeriodDto();

            this.salaryPeriod.setId(entity.getSalaryPeriod().getId());
            this.salaryPeriod.setName(entity.getSalaryPeriod().getName());
            this.salaryPeriod.setCode(entity.getSalaryPeriod().getCode());
            this.salaryPeriod.setFromDate(entity.getSalaryPeriod().getFromDate());
            this.salaryPeriod.setToDate(entity.getSalaryPeriod().getToDate());
        }
        if (entity.getSalaryTemplate() != null) {
            this.salaryTemplate = new SalaryTemplateDto();

            this.salaryTemplate.setId(entity.getSalaryTemplate().getId());
            this.salaryTemplate.setName(entity.getSalaryTemplate().getName());
            this.salaryTemplate.setCode(entity.getSalaryTemplate().getCode());
        }
    }

    public SalaryResultDto(SalaryResult entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || isDetail.equals(false))
            return;

        this.staffs = new ArrayList<>();
        if (entity.getSalaryResultStaffs() != null && !entity.getSalaryResultStaffs().isEmpty()) {
            List<SalaryResultStaff> staffData = new ArrayList<>(entity.getSalaryResultStaffs());

            Collections.sort(staffData, new Comparator<SalaryResultStaff>() {
                @Override
                public int compare(SalaryResultStaff o1, SalaryResultStaff o2) {
                    // First, compare by displayOrder
                    if (o1.getStaff() == null && o2.getStaff() == null)
                        return 0;
                    if (o1.getStaff().getDisplayName() == null)
                        return 1;
                    if (o2.getStaff().getDisplayName() == null)
                        return -1;

                    int orderComparison = o1.getStaff().getDisplayName().compareTo(o2.getStaff().getDisplayName());
                    if (orderComparison != 0) {
                        return orderComparison;
                    }

                    // If displayOrder is the same, compare by displayName (handling nulls)
                    if (o1.getStaff().getFirstName() == null && o2.getStaff().getFirstName() == null)
                        return 0;
                    if (o1.getStaff().getFirstName() == null)
                        return 1;
                    if (o2.getStaff().getFirstName() == null)
                        return -1;
                    return o1.getStaff().getFirstName().compareTo(o2.getStaff().getFirstName());
                }
            });

            List<StaffDto> appliedStaffs = new ArrayList<>();
            for (SalaryResultStaff tableRow : staffData) {
                StaffDto chosenStaff = new StaffDto(tableRow.getStaff(), false);

                appliedStaffs.add(chosenStaff);
            }

            this.staffs.addAll(appliedStaffs);
        }
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean locked) {
        isLocked = locked;
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

    public SalaryTemplateDto getSalaryTemplate() {
        return salaryTemplate;
    }

    public void setSalaryTemplate(SalaryTemplateDto salaryTemplate) {
        this.salaryTemplate = salaryTemplate;
    }

    public SalaryPeriodDto getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(SalaryPeriodDto salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

    public List<SalaryResultStaffDto> getSalaryResultStaffs() {
        return salaryResultStaffs;
    }

    public void setSalaryResultStaffs(List<SalaryResultStaffDto> salaryResultStaffs) {
        this.salaryResultStaffs = salaryResultStaffs;
    }

    public List<StaffDto> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<StaffDto> staffs) {
        this.staffs = staffs;
    }

    public List<SalaryResultItemGroupDto> getResultItemGroups() {
        return resultItemGroups;
    }

    public void setResultItemGroups(List<SalaryResultItemGroupDto> resultItemGroups) {
        this.resultItemGroups = resultItemGroups;
    }

    public List<SalaryResultItemDto> getResultItems() {
        return resultItems;
    }

    public void setResultItems(List<SalaryResultItemDto> resultItems) {
        this.resultItems = resultItems;
    }

    public List<SalaryTemplateItemDto> getTemplateItems() {
        return templateItems;
    }

    public void setTemplateItems(List<SalaryTemplateItemDto> templateItems) {
        this.templateItems = templateItems;
    }

    public List<SalaryTemplateItemGroupDto> getTemplateItemGroups() {
        return templateItemGroups;
    }

    public void setTemplateItemGroups(List<SalaryTemplateItemGroupDto> templateItemGroups) {
        this.templateItemGroups = templateItemGroups;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}
