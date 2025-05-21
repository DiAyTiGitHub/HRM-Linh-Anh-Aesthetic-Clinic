package com.globits.salary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;
import com.globits.salary.domain.SalaryPeriod;
import com.globits.salary.domain.SalaryResult;
import com.globits.salary.domain.SalaryResultStaff;
import com.globits.salary.domain.SalaryResultStaffItem;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.domain.SalaryTemplateItem;
import jakarta.persistence.*;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class SalaryResultStaffDto extends BaseObjectDto {
    private Integer displayOrder;
    private String note;
    private UUID salaryResultId;
    private StaffDto staff;
    private SalaryPeriodDto salaryPeriod;
    private SalaryTemplateDto salaryTemplate;
    private List<SalaryResultStaffItemDto> salaryResultStaffItems;
    private Integer paidStatus; // Trạng thái chi trả phiếu lương
    private Boolean isLocked; // Phiếu lương đã bị khóa hay chưa (phụ thuộc vào bảng lương đã bị khóa hay chưa)

    private UUID staffId;
    private String staffName;
    private String staffCode;
    private String mainOrganization;
    private String mainDepartment;
    private String mainPositionTitle;
    private String mainPosition;

    public SalaryResultStaffDto() {

    }

    public SalaryResultStaffDto(SalaryResultStaff entity) {
        super();
        if (entity == null) return;

        this.id = entity.getId();
        this.displayOrder = entity.getDisplayOrder();
        this.note = entity.getNote();
        this.paidStatus = entity.getPaidStatus();

        if (entity.getSalaryTemplate() != null) {
            this.salaryTemplate = new SalaryTemplateDto(entity.getSalaryTemplate());
        }
        if (entity.getSalaryPeriod() != null) {
            this.salaryPeriod = new SalaryPeriodDto(entity.getSalaryPeriod());
        }
        if (entity.getSalaryResult() != null) {
            this.salaryResultId = entity.getSalaryResult().getId();
            this.isLocked = entity.getSalaryResult().getIsLocked();
        }

        if (entity.getStaff() != null) {
            this.staffId = entity.getStaff().getId();
            this.staffName = entity.getStaff().getDisplayName();
            this.staffCode = entity.getStaff().getStaffCode();

            this.staff = new StaffDto(entity.getStaff(), true, true);
        }

        if (entity.getSalaryResultStaffItems() != null && !entity.getSalaryResultStaffItems().isEmpty()) {
            List<SalaryResultStaffItemDto> rowData = new ArrayList<>();

            for (SalaryResultStaffItem cell : entity.getSalaryResultStaffItems()) {
                SalaryResultStaffItemDto cellDto = new SalaryResultStaffItemDto(cell, true);

                rowData.add(cellDto);
            }

            Collections.sort(rowData, new Comparator<SalaryResultStaffItemDto>() {
                @Override
                public int compare(SalaryResultStaffItemDto o1, SalaryResultStaffItemDto o2) {
                    // First, compare by displayOrder
                    if (o1.getReferenceDisplayOrder() == null && o2.getReferenceDisplayOrder() == null) return 0;
                    if (o1.getReferenceDisplayOrder() == null) return 1;
                    if (o2.getReferenceDisplayOrder() == null) return -1;

                    int orderComparison = o1.getReferenceDisplayOrder().compareTo(o2.getReferenceDisplayOrder());
                    if (orderComparison != 0) {
                        return orderComparison;
                    }

                    // If displayOrder is the same, compare by displayName (handling nulls)
                    if (o1.getReferenceName() == null && o2.getReferenceName() == null) return 0;
                    if (o1.getReferenceName() == null) return 1;
                    if (o2.getReferenceName() == null) return -1;
                    return o1.getReferenceName().compareTo(o2.getReferenceName());
                }
            });

            this.salaryResultStaffItems = rowData;
        }

    }

    public SalaryResultStaffDto(SalaryResultStaff entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || isDetail.equals(false))
            return;

        Staff staff = entity.getStaff();
        if (staff != null && staff.getCurrentPositions() != null && !staff.getCurrentPositions().isEmpty()) {
            Position mainPosition = null;

            for (Position position : staff.getCurrentPositions()) {
                if (position != null && position.getIsMain() != null && position.getIsMain().equals(true)) {
                    mainPosition = position;
                    break;
                }
            }

            if (mainPosition != null) {
                this.mainPosition = mainPosition.getName();

                if (mainPosition.getTitle() != null) {
                    this.mainPositionTitle = mainPosition.getTitle().getName();
                }

                if (mainPosition.getDepartment() != null) {
                    this.mainDepartment = mainPosition.getDepartment().getName();
                }

                if (mainPosition.getDepartment() != null && mainPosition.getDepartment().getOrganization() != null) {
                    this.mainOrganization = mainPosition.getDepartment().getOrganization().getName();
                }
            }
        }
    }


    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Integer getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(Integer paidStatus) {
        this.paidStatus = paidStatus;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public UUID getSalaryResultId() {
        return salaryResultId;
    }

    public void setSalaryResultId(UUID salaryResultId) {
        this.salaryResultId = salaryResultId;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public List<SalaryResultStaffItemDto> getSalaryResultStaffItems() {
        return salaryResultStaffItems;
    }

    public void setSalaryResultStaffItems(List<SalaryResultStaffItemDto> salaryResultStaffItems) {
        this.salaryResultStaffItems = salaryResultStaffItems;
    }

    public SalaryPeriodDto getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(SalaryPeriodDto salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

    public SalaryTemplateDto getSalaryTemplate() {
        return salaryTemplate;
    }

    public void setSalaryTemplate(SalaryTemplateDto salaryTemplate) {
        this.salaryTemplate = salaryTemplate;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getMainOrganization() {
        return mainOrganization;
    }

    public void setMainOrganization(String mainOrganization) {
        this.mainOrganization = mainOrganization;
    }

    public String getMainDepartment() {
        return mainDepartment;
    }

    public void setMainDepartment(String mainDepartment) {
        this.mainDepartment = mainDepartment;
    }

    public String getMainPositionTitle() {
        return mainPositionTitle;
    }

    public void setMainPositionTitle(String mainPositionTitle) {
        this.mainPositionTitle = mainPositionTitle;
    }

    public String getMainPosition() {
        return mainPosition;
    }

    public void setMainPosition(String mainPosition) {
        this.mainPosition = mainPosition;
    }
}
