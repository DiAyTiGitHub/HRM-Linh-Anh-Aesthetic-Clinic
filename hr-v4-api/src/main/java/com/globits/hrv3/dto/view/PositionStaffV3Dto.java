package com.globits.hrv3.dto.view;

import java.time.LocalDateTime;
import java.util.Date;
import com.globits.hr.domain.HRDepartment;
import com.globits.hr.domain.PositionStaff;
import com.globits.hr.domain.PositionTitle;
import com.globits.hr.domain.Staff;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.dto.StaffDto;

public class PositionStaffV3Dto extends BaseObjectDto{
	private LocalDateTime fromDate;

	private LocalDateTime toDate;

	private boolean current = false;

	private StaffDto staff;

	private PositionTitleDto position;

	private HRDepartmentDto department;
	
	private Boolean mainPosition;
	
	private String departmentStr;//Vị trí cho đơn vị cụ thể nào đó dạng String--> Dữ liệu lịch sử, dùng để tham khảo
	
	private String decisionCode;
	
	private Date decisionDate;
	
	private Double allowanceCoefficient;//Phụ cấp chức vụ
	
	private String note;
	
	private Boolean connectedAllowanceProcess;//Kết nối quá trình phụ cấp --> Dữ liệu lịch sử, dùng để tham khảo

	public PositionStaffV3Dto() {

	}

	public PositionStaffV3Dto(PositionStaff entity) {
		if (entity == null) {
			return;
		}
		this.setId(entity.getId());
//		this.fromDate = entity.getFromDate();
//		this.toDate = entity.getToDate();
//		if(entity.getCurrent() == null) {
//			this.current = false;
//		}else {
//			this.current = entity.getCurrent();
//		}
//		this.mainPosition = entity.getMainPosition();

		if (entity.getHrDepartment() != null) {
			department = new HRDepartmentDto();
			department.setId(entity.getHrDepartment().getId());
			department.setCode(entity.getHrDepartment().getCode());
			department.setName(entity.getHrDepartment().getName());
			department.setDepartmentType(entity.getHrDepartment().getDepartmentType());
		}

		if (entity.getPosition() != null) {
			position = new PositionTitleDto();
			position.setCode(entity.getPosition().getCode());
			position.setName(entity.getPosition().getName());
		}

		if (entity.getStaff() != null) {
			staff = new StaffDto();
			staff.setId(entity.getStaff().getId());
			staff.setDisplayName(entity.getStaff().getDisplayName());
			staff.setBirthDate(entity.getStaff().getBirthDate());
			staff.setBirthPlace(entity.getStaff().getBirthPlace());
			staff.setEmail(entity.getStaff().getEmail());
			staff.setEndDate(entity.getStaff().getEndDate());
			staff.setFirstName(entity.getStaff().getFirstName());
			staff.setGender(entity.getStaff().getGender());
			staff.setId(entity.getStaff().getId());
			staff.setIdNumber(entity.getStaff().getIdNumber());
			staff.setIdNumberIssueBy(entity.getStaff().getIdNumberIssueBy());
			staff.setIdNumberIssueDate(entity.getStaff().getIdNumberIssueDate());
			staff.setPhoto(entity.getStaff().getPhoto());
			staff.setLastName(entity.getStaff().getLastName());
			staff.setPhoneNumber(entity.getStaff().getPhoneNumber());
		}
	}

	public PositionStaff toEntity() {
		PositionStaff entity = new PositionStaff();

//		entity.setId(this.getId());
//		entity.setFromDate(fromDate);
//		entity.setToDate(toDate);
//		entity.setCurrent(current);

		if (department != null) {
			HRDepartment d = new HRDepartment();
			d.setId(department.getId());
			d.setCode(department.getCode());
			d.setName(department.getName());
			d.setDepartmentType(department.getDepartmentType());

			entity.setHrDepartment(d);
		}

		if (position != null) {
			PositionTitle p = new PositionTitle();
			p.setDescription(position.getDescription());
			p.setName(position.getName());
//			entity.setPosition(p);
		}

		if (entity.getStaff() != null) {
			Staff s = new Staff();
			s.setId(staff.getId());
			s.setDisplayName(staff.getDisplayName());
			s.setBirthDate(staff.getBirthDate());
			s.setBirthPlace(staff.getBirthPlace());
			s.setEmail(staff.getEmail());
			s.setEndDate(staff.getEndDate());
			s.setFirstName(staff.getFirstName());
			s.setGender(staff.getGender());
			s.setId(staff.getId());
			s.setIdNumber(staff.getIdNumber());
			s.setIdNumberIssueBy(staff.getIdNumberIssueBy());
			s.setIdNumberIssueDate(staff.getIdNumberIssueDate());
			s.setPhoto(staff.getPhoto());
			s.setLastName(staff.getLastName());
			s.setPhoneNumber(staff.getPhoneNumber());
			entity.setStaff(s);
		}
		return entity;
	}

	public LocalDateTime getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDateTime fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDateTime getToDate() {
		return toDate;
	}

	public void setToDate(LocalDateTime toDate) {
		this.toDate = toDate;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	public StaffDto getStaff() {
		return staff;
	}

	public void setStaff(StaffDto staff) {
		this.staff = staff;
	}

	public PositionTitleDto getPosition() {
		return position;
	}

	public void setPosition(PositionTitleDto position) {
		this.position = position;
	}

	public HRDepartmentDto getDepartment() {
		return department;
	}

	public void setDepartment(HRDepartmentDto department) {
		this.department = department;
	}

	public Boolean getMainPosition() {
		return mainPosition;
	}

	public void setMainPosition(Boolean mainPosition) {
		this.mainPosition = mainPosition;
	}

	public String getDepartmentStr() {
		return departmentStr;
	}

	public void setDepartmentStr(String departmentStr) {
		this.departmentStr = departmentStr;
	}

	public String getDecisionCode() {
		return decisionCode;
	}

	public void setDecisionCode(String decisionCode) {
		this.decisionCode = decisionCode;
	}

	public Date getDecisionDate() {
		return decisionDate;
	}

	public void setDecisionDate(Date decisionDate) {
		this.decisionDate = decisionDate;
	}

	public Double getAllowanceCoefficient() {
		return allowanceCoefficient;
	}

	public void setAllowanceCoefficient(Double allowanceCoefficient) {
		this.allowanceCoefficient = allowanceCoefficient;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getConnectedAllowanceProcess() {
		return connectedAllowanceProcess;
	}

	public void setConnectedAllowanceProcess(Boolean connectedAllowanceProcess) {
		this.connectedAllowanceProcess = connectedAllowanceProcess;
	}

}
