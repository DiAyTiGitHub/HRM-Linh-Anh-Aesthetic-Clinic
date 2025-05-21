package com.globits.hr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.PositionRelationShip;
import com.globits.hr.domain.Staff;
import jakarta.persistence.Column;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PositionDto extends BaseObjectDto {
    private String code;
    private String name;
    private String description;
    private int status; // 0 - Khong duoc su dung; 1 - Dang duoc su dung
    private PositionTitleDto title;  // Chức danh
    private HRDepartmentDto department;
    private StaffDto staff;

    Boolean isDepartmentManager = false;
    /*
     * Là position chính của Staff này
     */
    private Boolean isMain;
    private Boolean isConcurrent;
    private Boolean isTemporary; // Là tạm thời = tuyển lọc

    private List<PositionRelationshipDto> relationships;
    private StaffDto previousStaff;
    private String errorMessage;

    public PositionDto() {

    }

    public PositionDto(Position entity) {
        this.id = entity.getId();
        this.code = entity.getCode();
        this.description = entity.getDescription();
        this.name = entity.getName();
        this.status = entity.getStatus();
        this.isMain = entity.getIsMain();
        this.isConcurrent = entity.getIsConcurrent();
        this.isTemporary = entity.getIsTemporary();


        if (entity.getStaff() != null) {
            this.staff = new StaffDto();
            this.staff.setId(entity.getStaff().getId());
            this.staff.setStaffCode(entity.getStaff().getStaffCode());
            this.staff.setDisplayName(entity.getStaff().getDisplayName());
        }

        if(entity.getPreviousStaff() != null) {
            this.previousStaff = StaffDto.getSimpleStaff(entity.getPreviousStaff());
        }
        if (entity.getDepartment() != null && entity.getDepartment().getId() != null) {
            this.department = new HRDepartmentDto();
            this.department.setId(entity.getDepartment().getId());
            this.department.setName(entity.getDepartment().getName());
            this.department.setCode(entity.getDepartment().getCode());

            HrOrganizationDto organization = new HrOrganizationDto(entity.getDepartment().getOrganization(), false, false);
            this.department.setOrganization(organization);

            if (entity.getDepartment().getPositionManager() != null) {
                if (entity.getDepartment().getPositionManager().getId().equals(entity.getId())) {
                    this.isDepartmentManager = true;
                }
            }
        }

        if (entity.getTitle() != null && entity.getTitle().getId() != null) {
            this.title = new PositionTitleDto();
            this.title.setId(entity.getTitle().getId());
            this.title.setCode(entity.getTitle().getCode());
            this.title.setName(entity.getTitle().getName());
        }

    }

    public PositionDto(Position entity, Boolean isDetail) {
        this(entity);

        if (isDetail != null && isDetail.equals(true)) {

            if (entity.getTitle() != null && entity.getTitle().getId() != null) {
                this.title = new PositionTitleDto();
                this.title.setId(entity.getTitle().getId());
                this.title.setCode(entity.getTitle().getCode());
                this.title.setName(entity.getTitle().getName());
                this.title.setDescription(entity.getTitle().getDescription());

                if (entity.getTitle().getRankTitle() != null && entity.getTitle().getRankTitle().getName() != null) {
                    RankTitleDto rankTitleDto = new RankTitleDto();
                    rankTitleDto.setName(entity.getTitle().getRankTitle().getName());
                    this.title.setRankTitle(rankTitleDto);
                }

            }


            if (entity.getRelationships() != null && !entity.getRelationships().isEmpty()) {
                this.relationships = new ArrayList<>();
                for (PositionRelationShip relationShipEntity : entity.getRelationships()) {
                    PositionRelationshipDto relationshipDto = new PositionRelationshipDto(relationShipEntity);

                    this.relationships.add(relationshipDto);
                }

//                Collections.sort(this.relationships, new Comparator<PositionRelationshipDto>() {
//                    @Override
//                    public int compare(PositionRelationshipDto o1, PositionRelationshipDto o2) {
//                        // First, compare by displayOrder
//                        if (o1.getDisplayOrder() == null && o2.getDisplayOrder() == null)
//                            return 0;
//                        if (o1.getDisplayOrder() == null)
//                            return 1;
//                        if (o2.getDisplayOrder() == null)
//                            return -1;
//
//                        int orderComparison = o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
//                        if (orderComparison != 0) {
//                            return orderComparison;
//                        }
//
//                        // If displayOrder is the same, compare by displayName (handling nulls)
//                        if (o1.getStaff().getFirstName() == null && o2.getStaff().getFirstName() == null)
//                            return 0;
//                        if (o1.getStaff().getFirstName() == null)
//                            return 1;
//                        if (o2.getStaff().getFirstName() == null)
//                            return -1;
//                        return o1.getStaff().getFirstName().compareTo(o2.getStaff().getFirstName());
//                    }
//                });
            }
        }
    }

    // simple
    public PositionDto(Position entity, Boolean isDetail, Boolean isSimple) {
        super(entity);
        this.name = entity.getName();
        if (entity.getStaff() != null) {
            StaffDto staffDto = new StaffDto();
            staffDto.setId(entity.getStaff().getId());
            staffDto.setDisplayName(entity.getStaff().getDisplayName());
            staffDto.setStaffCode(entity.getStaff().getStaffCode());
            this.staff = staffDto;
        }
    }

    public PositionDto(String code, String name, String description, int status, String titleCode, String departmentCode) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.status = status;

        this.title = new PositionTitleDto();
        this.title.setCode(titleCode);

        this.department = new HRDepartmentDto();
        this.department.setCode(departmentCode);
    }

    public StaffDto getPreviousStaff() {
        return previousStaff;
    }

    public void setPreviousStaff(StaffDto previousStaff) {
        this.previousStaff = previousStaff;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public PositionTitleDto getTitle() {
        return title;
    }

    public void setTitle(PositionTitleDto title) {
        this.title = title;
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getIsMain() {
        return isMain;
    }

    public void setIsMain(Boolean isMain) {
        this.isMain = isMain;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public List<PositionRelationshipDto> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<PositionRelationshipDto> relationships) {
        this.relationships = relationships;
    }

    public Boolean getIsTemporary() {
        return isTemporary;
    }

    public void setIsTemporary(Boolean isTemporary) {
        this.isTemporary = isTemporary;
    }

    public Boolean getIsConcurrent() {
        return isConcurrent;
    }

    public void setIsConcurrent(Boolean isConcurrent) {
        this.isConcurrent = isConcurrent;
    }

    public Boolean isDepartmentManager() {
        return isDepartmentManager;
    }

    public void setDepartmentManager(Boolean departmentManager) {
        isDepartmentManager = departmentManager;
    }

    public Boolean getDepartmentManager() {
        return isDepartmentManager;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
