package com.globits.hr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.domain.Department;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.*;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class HRDepartmentDto extends BaseObjectDto {
    private String staffCodeManager;
    private String name;
    private String code;
    private HRDepartmentDto parent;
    private List<HRDepartmentDto> subDepartment;
    private String displayOrder;
    private UUID parentId;
    private List<HRDepartmentDto> children = new ArrayList<HRDepartmentDto>();
    private String description;
    private String func;
    private String industryBlock;
    private Date foundedDate;
    private String foundedNumber;
    private String departmentDisplayCode;//Số hiệu phòng ban
    private String establishDecisionCode;//Số quyết định thành lập
    private Date establishDecisionDate;//Ngày quyết định thành lập
    private String shortName;
    private Integer sortNumber;
    private String parentCode;
    private Integer departmentType;
    private PositionTitleDto positionTitleManager; //chuc danh quan ly
    private DepartmentTypeDto hrDepartmentType; // loai phong ban
    private DepartmentGroupDto departmentGroup;// nhom phong ban
    private String timezone; // mui gio
    private Integer weight;
    // cac chuc danh trong phong ban
    private List<PositionTitleDto> positionTitles;
    private Set<PositionDto> positions;// cac chuc danh trong phong ban


    private HrOrganizationDto organization;// Đơn vị trực
    private Integer numberOfPositions;// số lượng chức vụ thuộc deparmtnet này

    private PositionDto positionManager; // vị trí quản lý

    // Các ca làm việc của phòng ban
    private List<ShiftWorkDto> shiftWorks;

    private String errorMessage;
    private Boolean isUpdate = false;

    public HRDepartmentDto(HRDepartment entity, boolean isGetParent, boolean isDetail) {
        if (entity == null) return;
        this.setId(entity.getId());
        this.name = entity.getName();
        this.code = entity.getCode();
        this.displayOrder = entity.getDisplayOrder();
        this.func = entity.getFunction();
        this.industryBlock = entity.getIndustryBlock();
        this.foundedDate = entity.getFoundedDate();
        this.foundedNumber = entity.getFoundedNumber();
        this.displayOrder = entity.getDisplayOrder();
        this.description = entity.getDescription();
        this.establishDecisionDate = entity.getEstablishDecisionDate();
        this.establishDecisionCode = entity.getEstablishDecisionCode();
        this.departmentDisplayCode = entity.getDepartmentDisplayCode();
        this.departmentType = entity.getDepartmentType();
        this.shortName = entity.getShortName();
        this.sortNumber = entity.getSortNumber();
        this.timezone = entity.getTimezone();

        if (entity.getPositionManager() != null) {
            if (entity.getPositionManager().getStaff() != null) {
                this.staffCodeManager = entity.getPositionManager().getStaff().getStaffCode();
            }
        }

        if (entity.getHrdepartmentType() != null) {
            this.setHrDepartmentType(new DepartmentTypeDto(entity.getHrdepartmentType()));
        }
        if (entity.getHrdepartmentType() != null) {
            this.weight = entity.getHrdepartmentType().getSortNumber();
        }

        this.shiftWorks = new ArrayList<>();

        if (entity.getParent() != null) {
            if (isGetParent) {
                this.parent = new HRDepartmentDto();
                this.parent.setId(entity.getParent().getId());
                this.parent.setName(entity.getParent().getName());
                this.parent.setCode(entity.getParent().getCode());
                this.parent.setDisplayOrder(entity.getDisplayOrder());
            }
            this.setParentId(entity.getParent().getId());

        }

        if (entity.getPositions() != null) {
            this.numberOfPositions = entity.getPositions().size();
        } else {
            this.numberOfPositions = 0;
        }

        this.children = new ArrayList<>();
        if (entity.getSubDepartments() != null && !entity.getSubDepartments().isEmpty() && isDetail) {
            for (Department child : entity.getSubDepartments()) {

                HRDepartment hRDepartment = (HRDepartment) child;
                this.children.add(new HRDepartmentDto(hRDepartment, true, true));
            }
        }

        List<PositionTitleDto> positionTitles = new ArrayList<>();
        if (entity.getDepartmentPositions() != null && !entity.getDepartmentPositions().isEmpty() && isDetail) {
            for (HRDepartmentPosition hrDepartmentPosition : entity.getDepartmentPositions()) {
                PositionTitleDto positionTitle = new PositionTitleDto(hrDepartmentPosition.getPositionTitle());
                positionTitles.add(positionTitle);
            }

        }
        this.setPositionTitles(positionTitles);

        if (entity.getOrganization() != null) {
            this.organization = new HrOrganizationDto();
            this.organization.setId(entity.getOrganization().getId());
            this.organization.setName(entity.getOrganization().getName());
            this.organization.setCode(entity.getOrganization().getCode());
        }
        if (entity.getPositionTitleManager() != null) {
            this.positionTitleManager = new PositionTitleDto();
            this.positionTitleManager.setId(entity.getPositionTitleManager().getId());
            this.positionTitleManager.setName(entity.getPositionTitleManager().getName());
            this.positionTitleManager.setCode(entity.getPositionTitleManager().getCode());
        }
        if (entity.getPositionManager() != null) {
            this.positionManager = new PositionDto();
            this.positionManager.setId(entity.getPositionManager().getId());
            this.positionManager.setName(entity.getPositionManager().getName());
            this.positionManager.setCode(entity.getPositionManager().getCode());
            StaffDto staffPositionManager = new StaffDto();
            if (entity.getPositionManager().getStaff() != null) {
                Staff staff = entity.getPositionManager().getStaff();
                staffPositionManager.setId(staff.getId());
                staffPositionManager.setDisplayName(staff.getDisplayName());
                staffPositionManager.setStaffCode(staff.getStaffCode());
                this.positionManager.setStaff(staffPositionManager);
            }
        }


        if (entity.getDepartmentGroup() != null && isDetail) {
            this.setDepartmentGroup(new DepartmentGroupDto(entity.getDepartmentGroup()));
        }
        this.shiftWorks = new ArrayList<>();
        if (entity.getDepartmentShiftWorks() != null && !entity.getDepartmentShiftWorks().isEmpty() && isDetail) {
            for (HrDepartmentShiftWork item : entity.getDepartmentShiftWorks()) {
                ShiftWorkDto shiftWorkDto = new ShiftWorkDto();
                if (item != null && item.getShiftWork() != null) {
                    shiftWorkDto.setId(item.getShiftWork().getId());
                    shiftWorkDto.setName(item.getShiftWork().getName());
                    shiftWorkDto.setCode(item.getShiftWork().getCode());
                    this.shiftWorks.add(shiftWorkDto);
                }
            }
        }
    }

    public HRDepartmentDto(HRDepartment entity, boolean isDetail) {
        this(entity, true, isDetail);
    }

    public HRDepartmentDto(HRDepartment entity) {
        this(entity, true, true);
    }

    public HRDepartmentDto(String name, String code, HRDepartmentDto parent, List<HRDepartmentDto> subDepartment, String displayOrder,
                           UUID parentId, List<HRDepartmentDto> children, String description, String func, String industryBlock,
                           Date foundedDate, String foundedNumber, String departmentDisplayCode, String establishDecisionCode, Date establishDecisionDate,
                           String shortName, Integer sortNumber, String parentCode, Integer departmentType, PositionTitleDto positionTitleManager,
                           DepartmentTypeDto hrDepartmentType, DepartmentGroupDto departmentGroup, List<PositionTitleDto> positionTitles, String timezone
    ) {
        this.name = name;
        this.code = code;
        this.parent = parent;
        this.subDepartment = subDepartment;
        this.displayOrder = displayOrder;
        this.parentId = parentId;
        this.children = children;
        this.description = description;
        this.func = func;
        this.industryBlock = industryBlock;
        this.foundedDate = foundedDate;
        this.foundedNumber = foundedNumber;
        this.departmentDisplayCode = departmentDisplayCode;
        this.establishDecisionCode = establishDecisionCode;
        this.establishDecisionDate = establishDecisionDate;
        this.shortName = shortName;
        this.sortNumber = sortNumber;
        this.parentCode = parentCode;
        this.departmentType = departmentType;
        this.positionTitleManager = positionTitleManager;
        this.hrDepartmentType = hrDepartmentType;
        this.departmentGroup = departmentGroup;
        this.positionTitles = positionTitles;
        this.timezone = timezone;

    }

    public String getStaffCodeManager() {
        return staffCodeManager;
    }

    public void setStaffCodeManager(String staffCodeManager) {
        this.staffCodeManager = staffCodeManager;
    }

    public Integer getNumberOfPositions() {
        return numberOfPositions;
    }

    public void setNumberOfPositions(Integer numberOfPositions) {
        this.numberOfPositions = numberOfPositions;
    }

    public List<PositionTitleDto> getPositionTitles() {
        return positionTitles;
    }

    public void setPositionTitles(List<PositionTitleDto> positionTitles) {
        this.positionTitles = positionTitles;
    }

    public PositionTitleDto getPositionTitleManager() {
        return positionTitleManager;
    }

    public void setPositionTitleManager(PositionTitleDto positionTitleManager) {
        this.positionTitleManager = positionTitleManager;
    }

    public DepartmentTypeDto getHrDepartmentType() {
        return hrDepartmentType;
    }

    public void setHrDepartmentType(DepartmentTypeDto hrDepartmentType) {
        this.hrDepartmentType = hrDepartmentType;
    }

    public DepartmentGroupDto getDepartmentGroup() {
        return departmentGroup;
    }

    public void setDepartmentGroup(DepartmentGroupDto departmentGroup) {
        this.departmentGroup = departmentGroup;
    }

    public String getDisplayOrder() {
        return displayOrder;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public void setDisplayOrder(String displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Set<PositionDto> getPositions() {
        return positions;
    }

    public void setPositions(Set<PositionDto> positions) {
        this.positions = positions;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getIndustryBlock() {
        return industryBlock;
    }


    public void setIndustryBlock(String industryBlock) {
        this.industryBlock = industryBlock;
    }

    public Date getFoundedDate() {
        return foundedDate;
    }

    public void setFoundedDate(Date foundedDate) {
        this.foundedDate = foundedDate;
    }

    public String getFoundedNumber() {
        return foundedNumber;
    }

    public void setFoundedNumber(String foundedNumber) {
        this.foundedNumber = foundedNumber;
    }

    public HRDepartmentDto() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public HRDepartmentDto getParent() {
        return parent;
    }

    public void setParent(HRDepartmentDto parent) {
        this.parent = parent;
    }

    public List<HRDepartmentDto> getSubDepartment() {
        return subDepartment;
    }

    public void setSubDepartment(List<HRDepartmentDto> subDepartment) {
        this.subDepartment = subDepartment;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public List<HRDepartmentDto> getChildren() {
        return children;
    }

    public void setChildren(List<HRDepartmentDto> children) {
        this.children = children;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartmentDisplayCode() {
        return departmentDisplayCode;
    }

    public void setDepartmentDisplayCode(String departmentDisplayCode) {
        this.departmentDisplayCode = departmentDisplayCode;
    }

    public String getEstablishDecisionCode() {
        return establishDecisionCode;
    }

    public void setEstablishDecisionCode(String establishDecisionCode) {
        this.establishDecisionCode = establishDecisionCode;
    }

    public Date getEstablishDecisionDate() {
        return establishDecisionDate;
    }

    public void setEstablishDecisionDate(Date establishDecisionDate) {
        this.establishDecisionDate = establishDecisionDate;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Integer getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(Integer departmentType) {
        this.departmentType = departmentType;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Integer getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(Integer sortNumber) {
        this.sortNumber = sortNumber;
    }

    public HrOrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganizationDto organization) {
        this.organization = organization;
    }

    public PositionDto getPositionManager() {
        return positionManager;
    }

    public void setPositionManager(PositionDto positionManager) {
        this.positionManager = positionManager;
    }

    public List<ShiftWorkDto> getShiftWorks() {
        return shiftWorks;
    }

    public void setShiftWorks(List<ShiftWorkDto> shiftWorks) {
        this.shiftWorks = shiftWorks;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Boolean getUpdate() {
        return isUpdate;
    }

    public void setUpdate(Boolean update) {
        isUpdate = update;
    }
}
