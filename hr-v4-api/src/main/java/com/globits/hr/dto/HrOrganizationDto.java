package com.globits.hr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.domain.AdministrativeUnit;
import com.globits.core.domain.Organization;
import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.OrganizationDto;
import com.globits.core.dto.OrganizationUserDto;
import com.globits.hr.domain.HRDepartment;
import com.globits.hr.domain.HrOrganization;
import com.globits.hr.domain.Staff;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class HrOrganizationDto extends BaseObjectDto {
    private UUID id;
    private String name;
    private String code;
    private String website;
    private Integer organizationType;
    private Integer level;
    private OrganizationDto parent;
    private List<HrOrganizationDto> subOrganizations;
    private List<OrganizationUserDto> users;
    private Boolean isActive = true;
    private UUID parentId;
    private List<HRDepartmentDto> departments;//danh sách phòng ban, chi nhánh trực thuộc
    private HrAdministrativeUnitDto administrativeUnit;//địa chỉ của đơn vị
    private HrAdministrativeUnitDto province;
    private HrAdministrativeUnitDto district;
    private Integer sortNumber;
    private String addressDetail; // Chi tiết đơn vị/pháp nhân
    private String taxCode; // Mã số thuế
    private Date foundedDate; // Ngày thành lập
    private StaffDto representative; // Nhân viên đại diện

    public HrOrganizationDto() {
    }

    public HrOrganizationDto(HrOrganization entity, Boolean isGetParent) {
        this(entity, isGetParent, true);

    }

    public HrOrganizationDto(HrOrganization entity, Boolean isGetParent, boolean isDetail) {
        if (entity == null) return;

        this.id = entity.getId();
        this.code = entity.getCode();
        this.name = entity.getName();
        this.organizationType = entity.getOrganizationType();
        this.website = entity.getWebsite();
        this.sortNumber = entity.getSortNumber();
        this.level = entity.getLevel();

        this.addressDetail = entity.getAddressDetail();
        this.foundedDate = entity.getFoundedDate();
        this.taxCode = entity.getTaxCode();

        if (entity.getRepresentative() != null) {
            this.representative = new StaffDto();

            this.representative.setId(entity.getRepresentative().getId());
            this.representative.setStaffCode(entity.getRepresentative().getStaffCode());
            this.representative.setDisplayName(entity.getRepresentative().getDisplayName());
        }

        if (true) {
            if (entity.getParent() != null) {
                this.parent = new OrganizationDto();
                this.parent.setCode(entity.getParent().getCode());
                this.parent.setName(entity.getParent().getName());
                this.parent.setOrganizationType(entity.getParent().getOrganizationType());
                this.parent.setId(entity.getParent().getId());
            }

        }

        if (entity.getSubOrganizations() != null && !entity.getSubOrganizations().isEmpty()) {
            this.subOrganizations = new ArrayList<>();

            for (Organization child : entity.getSubOrganizations()) {
                HrOrganization childHrOrg = (HrOrganization) child;

                HrOrganizationDto childItem = new HrOrganizationDto(childHrOrg, true, false);
//                childItem.setCode(childHrOrg.getCode());
//                childItem.setName(childHrOrg.getName());
//                childItem.setOrganizationType(childHrOrg.getOrganizationType());
//                childItem.setId(childHrOrg.getId());

                this.subOrganizations.add(childItem);
            }
        }

        if (entity.getAdministrativeUnit() != null) {
            AdministrativeUnit administrativeUnit = entity.getAdministrativeUnit();
            HrAdministrativeUnitDto administrativeUnitDto = new HrAdministrativeUnitDto(administrativeUnit, false);
            Integer level = administrativeUnitDto.getLevel();
            if (level != null) {
                if (level == 1) {
                    this.setAdministrativeUnit(administrativeUnitDto);
                    if (administrativeUnit != null && administrativeUnit.getParent() != null) {
                        this.setDistrict(new HrAdministrativeUnitDto(administrativeUnit.getParent(), false));
                        if (administrativeUnit.getParent().getParent() != null) {
                            this.setProvince(new HrAdministrativeUnitDto(administrativeUnit.getParent().getParent(), false));
                        }
                    }
                } else if (level == 2) { // Cấp huyện/quận
                    this.setDistrict(administrativeUnitDto);
                    if (administrativeUnit != null && administrativeUnit.getParent() != null) {
                        this.setProvince(new HrAdministrativeUnitDto(administrativeUnit.getParent(), false));
                    }
                } else if (level == 3) { // Cấp tỉnh/thành phố
                    this.setProvince(administrativeUnitDto);
                }
            }
        }

        if (isDetail) {
            if (entity.getDepartments() != null && !entity.getDepartments().isEmpty()) {
                this.departments = new ArrayList<>();
                for (HRDepartment child : entity.getDepartments()) {
                    this.departments.add(new HRDepartmentDto(child, true, true));
                }
            }
        }
    }


    public StaffDto getRepresentative() {
        return representative;
    }

    public void setRepresentative(StaffDto representative) {
        this.representative = representative;
    }

    public HrOrganizationDto(HrOrganization entity) {
        this(entity, true, true);

    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public Date getFoundedDate() {
        return foundedDate;
    }

    public void setFoundedDate(Date foundedDate) {
        this.foundedDate = foundedDate;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Integer getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(Integer organizationType) {
        this.organizationType = organizationType;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public OrganizationDto getParent() {
        return parent;
    }

    public void setParent(OrganizationDto parent) {
        this.parent = parent;
    }

    public List<HrOrganizationDto> getSubOrganizations() {
        return subOrganizations;
    }

    public void setSubOrganizations(List<HrOrganizationDto> subOrganizations) {
        this.subOrganizations = subOrganizations;
    }

    public List<OrganizationUserDto> getUsers() {
        return users;
    }

    public void setUsers(List<OrganizationUserDto> users) {
        this.users = users;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public List<HRDepartmentDto> getDepartments() {
        return departments;
    }

    public void setDepartments(List<HRDepartmentDto> departments) {
        this.departments = departments;
    }

    public HrAdministrativeUnitDto getAdministrativeUnit() {
        return administrativeUnit;
    }

    public void setAdministrativeUnit(HrAdministrativeUnitDto administrativeUnit) {
        this.administrativeUnit = administrativeUnit;
    }

    public Integer getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(Integer sortNumber) {
        this.sortNumber = sortNumber;
    }

    public HrAdministrativeUnitDto getProvince() {
        return province;
    }

    public void setProvince(HrAdministrativeUnitDto province) {
        this.province = province;
    }

    public HrAdministrativeUnitDto getDistrict() {
        return district;
    }

    public void setDistrict(HrAdministrativeUnitDto district) {
        this.district = district;
    }


}
