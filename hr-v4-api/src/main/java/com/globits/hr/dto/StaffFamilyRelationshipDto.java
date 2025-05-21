package com.globits.hr.dto;

import com.globits.core.dto.ProfessionDto;
import com.globits.hr.domain.StaffFamilyRelationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class StaffFamilyRelationshipDto {
    private static final Logger logger = LoggerFactory.getLogger(StaffFamilyRelationshipDto.class);
    private UUID id;
    private StaffDto staff;
    private FamilyRelationshipDto familyRelationship;
    private String fullName;
    private Date birthDate;
    private ProfessionDto profession;
    private String address;
    private String description;
    private String workingPlace;
    private Boolean isDependent; // là người phụ thuộc => Dùng để tính người phụ thuộc trong bảng lương
    private String taxCode; // Mã số thuế
    private Date dependentDeductionFromDate;
    private Date dependentDeductionToDate;
    
    public StaffFamilyRelationshipDto(StaffFamilyRelationship staffFamilyRelationship) {
        super();
        if (staffFamilyRelationship == null)
            return;

        this.id = staffFamilyRelationship.getId();
        if (staffFamilyRelationship.getStaff() != null) {
            this.staff = new StaffDto(staffFamilyRelationship.getStaff(), false);
        }

        if (staffFamilyRelationship.getFamilyRelationship() != null) {
            this.familyRelationship = new FamilyRelationshipDto(staffFamilyRelationship.getFamilyRelationship());
        }

        if (staffFamilyRelationship.getProfession() != null) {
            this.profession = new ProfessionDto(staffFamilyRelationship.getProfession());
        }

        this.fullName = staffFamilyRelationship.getFullName();
        this.address = staffFamilyRelationship.getAddress();
        this.description = staffFamilyRelationship.getDescription();
        this.workingPlace = staffFamilyRelationship.getWorkingPlace();
        this.isDependent = staffFamilyRelationship.getIsDependent();
        this.taxCode = staffFamilyRelationship.getTaxCode();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if (staffFamilyRelationship.getBirthDate() != null) {
                if (staffFamilyRelationship.getBirthDate().before(sdf.parse("01-01-1900"))
                        || staffFamilyRelationship.getBirthDate().after(sdf.parse("01-01-2100"))) {
                    this.birthDate = null;
                } else {
                    this.birthDate = staffFamilyRelationship.getBirthDate();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        this.dependentDeductionFromDate = staffFamilyRelationship.getDependentDeductionFromDate();
        this.dependentDeductionToDate = staffFamilyRelationship.getDependentDeductionToDate();
    }

    public StaffFamilyRelationshipDto(StaffFamilyRelationship staffFamilyRelationship, Boolean getStaff) {
        super();
        if (familyRelationship == null)
            return;
        this.id = familyRelationship.getId();
        this.fullName = staffFamilyRelationship.getFullName();
        this.birthDate = staffFamilyRelationship.getBirthDate();
        this.address = staffFamilyRelationship.getAddress();
        this.description = staffFamilyRelationship.getDescription();
        this.workingPlace = staffFamilyRelationship.getWorkingPlace();
        this.isDependent = staffFamilyRelationship.getIsDependent();

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StaffFamilyRelationshipDto() {
        super();
    }

    public FamilyRelationshipDto getFamilyRelationship() {
        return familyRelationship;
    }

    public void setFamilyRelationship(FamilyRelationshipDto familyRelationship) {
        this.familyRelationship = familyRelationship;
    }

    public String getWorkingPlace() {
        return workingPlace;
    }

    public void setWorkingPlace(String workingPlace) {
        this.workingPlace = workingPlace;
    }

    public ProfessionDto getProfession() {
        return profession;
    }

    public void setProfession(ProfessionDto profession) {
        this.profession = profession;
    }

    public Boolean getIsDependent() {
        return isDependent;
    }

    public void setIsDependent(Boolean isDependent) {
        this.isDependent = isDependent;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public Boolean getDependent() {
        return isDependent;
    }

    public void setDependent(Boolean dependent) {
        isDependent = dependent;
    }

	public Date getDependentDeductionFromDate() {
		return dependentDeductionFromDate;
	}

	public void setDependentDeductionFromDate(Date dependentDeductionFromDate) {
		this.dependentDeductionFromDate = dependentDeductionFromDate;
	}

	public Date getDependentDeductionToDate() {
		return dependentDeductionToDate;
	}

	public void setDependentDeductionToDate(Date dependentDeductionToDate) {
		this.dependentDeductionToDate = dependentDeductionToDate;
	}
    
    
}
