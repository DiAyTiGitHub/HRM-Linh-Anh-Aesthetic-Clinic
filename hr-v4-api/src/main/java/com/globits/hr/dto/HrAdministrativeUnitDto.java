package com.globits.hr.dto;

import com.globits.core.domain.AdministrativeUnit;

import java.util.*;

public class HrAdministrativeUnitDto {
    private UUID id;
    private String name;
    private String code;
    private Integer level;
    private String parentCode; // import

    private UUID parentId;
    private HrAdministrativeUnitDto parent;

    private HrAdministrativeUnitDto province;
    private Set<HrAdministrativeUnitDto> subAdministrativeUnits;
    private List<HrAdministrativeUnitDto> children;
    private String description;

    public AdministrativeUnit toEntity() {
        AdministrativeUnit entity = new AdministrativeUnit();
        entity.setId(this.id);
        entity.setName(this.name);
        entity.setCode(this.code);
        entity.setLevel(this.level);
        if (this.parent != null) {
            AdministrativeUnit parent = new AdministrativeUnit();
            parent.setId(parent.getId());
            parent.setName(parent.getName());
            parent.setCode(parent.getCode());
            parent.setLevel(this.level);
            entity.setParent(parent);
        }
        if (this.subAdministrativeUnits != null) {
            Set<AdministrativeUnit> subs = new HashSet<>();
            Iterator var3 = this.subAdministrativeUnits.iterator();

            while (var3.hasNext()) {
                HrAdministrativeUnitDto dto = (HrAdministrativeUnitDto) var3.next();
                AdministrativeUnit sub = new AdministrativeUnit();
                sub.setId(dto.getId());
                sub.setName(dto.getName());
                sub.setCode(dto.getCode());
                sub.setLevel(dto.getLevel());
                subs.add(sub);
            }

            entity.getSubAdministrativeUnits().addAll(subs);
        }

        return entity;
    }

    public HrAdministrativeUnitDto() {
    }

    public HrAdministrativeUnitDto(AdministrativeUnit unit, boolean getFull) {
        this.code = unit.getCode();
        this.id = unit.getId();
        this.level = unit.getLevel();
        this.name = unit.getName();
        if (unit.getParent() != null) {
            this.parent = new HrAdministrativeUnitDto();
            this.parent.setCode(unit.getParent().getCode());
            this.parent.setId(unit.getParent().getId());
            this.parent.setName(unit.getParent().getName());
            this.parent.setLevel(unit.getParent().getLevel());
        }
        if (getFull) {
            if (unit.getParent() != null) {
                this.parent = new HrAdministrativeUnitDto();
                this.parent.setCode(unit.getParent().getCode());
                this.parent.setId(unit.getParent().getId());
                this.parent.setName(unit.getParent().getName());
                this.parent.setLevel(unit.getParent().getLevel());
                if (unit.getParent().getParent() != null) {
                    this.province = new HrAdministrativeUnitDto();
                    this.province.setCode(unit.getParent().getParent().getCode());
                    this.province.setId(unit.getParent().getParent().getId());
                    this.province.setName(unit.getParent().getParent().getName());
                    this.province.setLevel(unit.getParent().getParent().getLevel());
                }
            }

            if (unit.getSubAdministrativeUnits() != null && unit.getSubAdministrativeUnits().size() > 0) {
                this.subAdministrativeUnits = new HashSet<>();
                Iterator var3 = unit.getSubAdministrativeUnits().iterator();

                while (var3.hasNext()) {
                    AdministrativeUnit c = (AdministrativeUnit) var3.next();
                    HrAdministrativeUnitDto cDto = new HrAdministrativeUnitDto();
                    cDto.setId(c.getId());
                    cDto.setCode(c.getCode());
                    cDto.setName(c.getName());
                    cDto.setLevel(c.getLevel());
                    this.subAdministrativeUnits.add(cDto);
                }
            }

            this.setChildren(this.getListChildren(unit));
        }

    }

    public HrAdministrativeUnitDto(AdministrativeUnit unit) {
        this(unit, true);
    }

    private List<HrAdministrativeUnitDto> getListChildren(AdministrativeUnit unit) {
        List<HrAdministrativeUnitDto> ret = new ArrayList<>();
        if (unit.getSubAdministrativeUnits() != null && unit.getSubAdministrativeUnits().size() > 0) {
            Iterator var3 = unit.getSubAdministrativeUnits().iterator();

            while (var3.hasNext()) {
                AdministrativeUnit s = (AdministrativeUnit) var3.next();
                HrAdministrativeUnitDto sDto = new HrAdministrativeUnitDto();
                sDto.setId(s.getId());
                sDto.setCode(s.getCode());
                sDto.setName(s.getName());
                sDto.setLevel(s.getLevel());
                sDto.setChildren(this.getListChildren(s));
                ret.add(sDto);
            }
        }

        return ret;
    }

    public UUID getId() {
        return id;
    }

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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public HrAdministrativeUnitDto getParent() {
        return parent;
    }

    public void setParent(HrAdministrativeUnitDto parent) {
        this.parent = parent;
    }

    public Set<HrAdministrativeUnitDto> getSubAdministrativeUnits() {
        return subAdministrativeUnits;
    }

    public void setSubAdministrativeUnits(Set<HrAdministrativeUnitDto> subAdministrativeUnits) {
        this.subAdministrativeUnits = subAdministrativeUnits;
    }

    public List<HrAdministrativeUnitDto> getChildren() {
        return children;
    }

    public void setChildren(List<HrAdministrativeUnitDto> children) {
        this.children = children;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public HrAdministrativeUnitDto getProvince() {
        return province;
    }

    public void setProvince(HrAdministrativeUnitDto province) {
        this.province = province;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
