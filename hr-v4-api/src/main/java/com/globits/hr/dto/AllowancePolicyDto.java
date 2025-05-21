package com.globits.hr.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.DepartmentDto;
import com.globits.core.dto.OrganizationDto;
import com.globits.hr.domain.AllowancePolicy;
import com.globits.hr.domain.StaffAllowance;
import com.globits.salary.domain.SalaryResultStaff;

public class AllowancePolicyDto extends BaseObjectDto {
    
	private String code;
	private String name;
	private String description;
	
	private UUID organizationId;
	private OrganizationDto organization;
	private UUID departmentId;
	private DepartmentDto department;
	private UUID positionId;
	private PositionDto position;
	private UUID allowanceId;
	private AllowanceDto allowance;
	
	private String formula;
	private Date startDate;
	private Date endDate;
	
	private List<StaffDto> staffs;
	
	public AllowancePolicyDto() {
		
	}
	
	public AllowancePolicyDto(AllowancePolicy entity) {
		this.id = entity.getId();
		this.code = entity.getCode();
		this.name = entity.getName();
		this.description = entity.getDescription();
		this.formula = entity.getFormula();
		this.startDate = entity.getStartDate();
		this.endDate = entity.getEndDate();
		if (entity.getOrganization() != null) {
			this.organization = new OrganizationDto(entity.getOrganization());
			if (organization != null && organization.getId() != null) {
				this.organizationId = organization.getId();
			}
		}
		if (entity.getDepartment() != null) {
			this.department = new DepartmentDto(entity.getDepartment());
			if (department != null && department.getId() != null) {
				this.departmentId = department.getId();
			}
		}
		if (entity.getPosition() != null) {
			this.position = new PositionDto(entity.getPosition());
			if (position != null && position.getId() != null) {
				this.positionId = position.getId();
			}
		}
		if (entity.getAllowance() != null) {
			this.allowance = new AllowanceDto(entity.getAllowance());
			if (allowance != null && allowance.getId() != null) {
				this.allowanceId = allowance.getId();
			}
		}
		
	}
	
	public AllowancePolicyDto(AllowancePolicy entity, Boolean isGetStaffs) {
		this(entity); 
		if (isGetStaffs == true) {
			if (entity.getStaffAllowances() != null && entity.getStaffAllowances().size() > 0) {
				this.staffs = new ArrayList<>();
				if (entity.getStaffAllowances() != null && entity.getStaffAllowances().size() > 0) {
					List<StaffAllowance> listStaff = new ArrayList<>(entity.getStaffAllowances());

					Collections.sort(listStaff, new Comparator<StaffAllowance>() {
						@Override
						public int compare(StaffAllowance o1, StaffAllowance o2) {
							if (o1.getStaff().getFirstName() == null && o2.getStaff().getFirstName() == null)
								return 0;
							if (o1.getStaff().getFirstName() == null)
								return 1;
							if (o2.getStaff().getFirstName() == null)
								return -1;
							return o1.getStaff().getFirstName().compareTo(o2.getStaff().getFirstName());
						}
					});
					List<StaffDto> listStaffDto = new ArrayList<>();
					for (StaffAllowance staffAllowance : listStaff) {
						StaffDto staffDto = new StaffDto(staffAllowance.getStaff(), false);
						listStaffDto.add(staffDto);
					}
					this.staffs.addAll(listStaffDto);
				}
				
			}
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

	public UUID getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(UUID organizationId) {
		this.organizationId = organizationId;
	}

	public OrganizationDto getOrganization() {
		return organization;
	}

	public void setOrganization(OrganizationDto organization) {
		this.organization = organization;
	}

	public UUID getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(UUID departmentId) {
		this.departmentId = departmentId;
	}

	public DepartmentDto getDepartment() {
		return department;
	}

	public void setDepartment(DepartmentDto department) {
		this.department = department;
	}

	public UUID getPositionId() {
		return positionId;
	}

	public void setPositionId(UUID positionId) {
		this.positionId = positionId;
	}

	public PositionDto getPosition() {
		return position;
	}

	public void setPosition(PositionDto position) {
		this.position = position;
	}

	public UUID getAllowanceId() {
		return allowanceId;
	}

	public void setAllowanceId(UUID allowanceId) {
		this.allowanceId = allowanceId;
	}

	public AllowanceDto getAllowance() {
		return allowance;
	}

	public void setAllowance(AllowanceDto allowance) {
		this.allowance = allowance;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

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

	public List<StaffDto> getStaffs() {
		return staffs;
	}

	public void setStaffs(List<StaffDto> staffs) {
		this.staffs = staffs;
	}
	
}
