package com.globits.hr.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.core.utils.CoreDateTimeUtil;
import com.globits.hr.domain.HRDepartment;
import com.globits.hr.domain.PositionStaff;
import com.globits.hr.domain.PositionTitle;
import com.globits.hr.domain.Staff;
import com.globits.hr.repository.HRDepartmentRepository;
import com.globits.hr.repository.PositionStaffRepository;
import com.globits.hr.repository.PositionTitleRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.PositionStaffV3Service;
import com.globits.hrv3.dto.view.PositionStaffV3Dto;
import com.globits.security.domain.User;

@Service
public class PositionStaffV3ServiceImpl extends GenericServiceImpl<PositionStaff, UUID>
		implements PositionStaffV3Service {
	@Autowired
	StaffRepository staffRepository;
	@Autowired
	PositionTitleRepository positionTitleRepository;
	@Autowired
	HRDepartmentRepository hRDepartmentRepository;
	@Autowired
	PositionStaffRepository positionStaffRepository;

	@Override
	public PositionStaffV3Dto getById(UUID id) {
		PositionStaff ps = positionStaffRepository.getById(id);
		return new PositionStaffV3Dto(ps);
	}

	@Override
	public PositionStaffV3Dto saveOrUpdate(PositionStaffV3Dto dto, UUID id) {
		if (dto != null) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			User modifiedUser;
			LocalDateTime currentDate = LocalDateTime.now();
			String currentUserName = "Unknown User";
			if (authentication != null) {
				modifiedUser = (User) authentication.getPrincipal();
				currentUserName = modifiedUser.getUsername();
			}
			PositionStaff positionStaff = null;
			if (id != null) {
				positionStaff = positionStaffRepository.getById(id);
			}
			if (positionStaff == null) {
				positionStaff = new PositionStaff();
				positionStaff.setCreateDate(currentDate);
				positionStaff.setCreatedBy(currentUserName);
			}
			positionStaff.setModifiedBy(currentUserName);
			positionStaff.setModifyDate(currentDate);
			if (dto.getStaff() != null) {
				Staff staff;
				if (dto.getStaff().getId() != null) {
					staff = staffRepository.findOneById(dto.getStaff().getId());
					if (staff != null) {
						positionStaff.setStaff(staff);
					}
				} else if (dto.getStaff().getStaffCode() != null) {
					List<Staff> lst = staffRepository.getByCode(dto.getStaff().getStaffCode());
					if (lst != null && lst.size() > 0) {
						staff = lst.get(0);
					}
				}
			}
//			positionStaff.setFromDate(dto.getFromDate());
//			positionStaff.setToDate(dto.getToDate());
//
			PositionTitle positionTitle;
//			if (dto.getPosition() != null) {
//				if (dto.getPosition().getId() != null) {
//					positionTitle = positionTitleRepository.getById(dto.getPosition().getId());
//					positionStaff.setPosition(positionTitle);
//				} else if (dto.getPosition().getCode() != null) {
//					List<PositionTitle> lt = positionTitleRepository.findByCode(dto.getPosition().getCode());
//					if (lt != null && lt.size() > 0) {
//						positionStaff.setPosition(lt.get(0));
//					}
//				}
//			}
			HRDepartment dep = null;
			if (dto.getDepartment() != null) {
				if (dto.getDepartment().getId() != null) {
					dep = hRDepartmentRepository.getById(dto.getDepartment().getId());
					positionStaff.setHrDepartment(dep);
				} else if (dto.getDepartment().getCode() != null) {
					List<HRDepartment> depart = hRDepartmentRepository.findByCode(dto.getDepartment().getCode());
					if (depart != null && depart.size() > 0) {
						positionStaff.setHrDepartment(depart.get(0));
					}
				}
			}
//			if (dto.getDepartmentStr() != null && dto.getDepartmentStr().length() > 0) {
//				positionStaff.setDepartmentStr(dto.getDepartmentStr());
//			} else if (dep != null) {
//				positionStaff.setDepartmentStr(dep.getName());
//			}
//			positionStaff.setDecisionCode(dto.getDecisionCode());
//			positionStaff.setDecisionDate(dto.getDecisionDate());
//			positionStaff.setAllowanceCoefficient(dto.getAllowanceCoefficient());
//			positionStaff.setNote(dto.getNote());
//			positionStaff.setConnectedAllowanceProcess(dto.getConnectedAllowanceProcess());
			positionStaff = positionStaffRepository.save(positionStaff);
			return new PositionStaffV3Dto(positionStaff);
		}

		return null;
	}

	@Override
	public Boolean remove(UUID id) {
		if(id!=null) {
			PositionStaff ps= positionStaffRepository.getById(id);
			positionStaffRepository.delete(ps);
			return true;
		}
		return false;
	}

	@Override
	public Boolean removeList(List<UUID> ids) {
		if(ids!=null && ids.size()>0) {
			List<PositionStaff> list= new ArrayList<>();
			for (UUID uuid : ids) {
				PositionStaff ps= positionStaffRepository.getById(uuid);
				list.add(ps);
			}
			positionStaffRepository.deleteAll(list);
			return true;
		}
		return false;
	}

	@Override
	public List<PositionStaffV3Dto> getListByStaffId(UUID staffId) {
		if(staffId!=null) {
			List<PositionStaffV3Dto> ret = new ArrayList<>();
			List<PositionStaff> list=positionStaffRepository.findPositionStaffByStaff(staffId);
			if(list!=null && list.size()>0) {
				for (PositionStaff positionStaff : list) {
					PositionStaffV3Dto item=new PositionStaffV3Dto(positionStaff);
					ret.add(item);
				}
			}
			return ret;
		}
		return null;
	}

}
