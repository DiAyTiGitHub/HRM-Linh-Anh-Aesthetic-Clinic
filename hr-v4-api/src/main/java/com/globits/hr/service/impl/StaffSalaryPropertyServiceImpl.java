package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.StaffSalaryProperty;
import com.globits.hr.dto.StaffSalaryPropertyDto;
import com.globits.hr.repository.StaffSalaryPropertyRepository;
import com.globits.hr.service.StaffSalaryPropertyService;
import com.globits.salary.domain.SalaryItem;
import com.globits.security.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Transactional
@Service
public class StaffSalaryPropertyServiceImpl extends GenericServiceImpl<SalaryItem, UUID>  implements StaffSalaryPropertyService{

	@Autowired
	StaffSalaryPropertyRepository staffSalaryPropertyRepository;

	@Override
	public StaffSalaryPropertyDto saveStaffSalaryProperty(StaffSalaryPropertyDto dto) {
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		 User modifiedUser;
		 LocalDateTime currentDate = LocalDateTime.now();
		 String currentUserName = "Unknown User";
		 if (authentication != null) {
			 modifiedUser = (User) authentication.getPrincipal();
			 currentUserName = modifiedUser.getUsername();
		 }
		 StaffSalaryProperty staffSalaryProperty = null;
		if(dto!=null) {
			if(dto.getId()!=null)
				staffSalaryProperty = staffSalaryPropertyRepository.getOne(dto.getId());
			if(staffSalaryProperty==null) {//Nếu không tìm thấy thì tạo mới 1 đối tượng
				staffSalaryProperty = new StaffSalaryProperty();
				staffSalaryProperty.setCreateDate(currentDate);
				staffSalaryProperty.setCreatedBy(currentUserName);
			}
			if(dto.getCode()!=null) {
				staffSalaryProperty.setCode(dto.getCode());
			}
			staffSalaryProperty.setName(dto.getName());
			staffSalaryProperty.setModifyDate(currentDate);
			staffSalaryProperty.setModifiedBy(currentUserName);
			staffSalaryProperty = staffSalaryPropertyRepository.save(staffSalaryProperty);
			return new StaffSalaryPropertyDto(staffSalaryProperty);
		}
		return null;
	}

	@Override
	public Boolean deleteStaffSalaryProperty(UUID id) {
		StaffSalaryProperty staffSalaryProperty = null;
		Optional<StaffSalaryProperty> optional = staffSalaryPropertyRepository.findById(id);
		if (optional.isPresent()) {
			staffSalaryProperty = optional.get();
		}
		if(staffSalaryProperty!=null) {
			staffSalaryPropertyRepository.deleteById(id);
			return true;
		}
		return false;
	}

	@Override
	public StaffSalaryPropertyDto getStaffSalaryProperty(UUID id) {
		StaffSalaryProperty staffSalaryProperty = null;
		Optional<StaffSalaryProperty> optional = staffSalaryPropertyRepository.findById(id);
		if (optional.isPresent()) {
			staffSalaryProperty = optional.get();
		}
		if(staffSalaryProperty!=null) {
			return new StaffSalaryPropertyDto(staffSalaryProperty);
		}
		return null;
	}

	@Override
	public int deleteStaffSalaryPropertyList(List<StaffSalaryPropertyDto> dtos) {
		if(dtos==null) {
			return 0;
		}
		int ret = 0;
		for(StaffSalaryPropertyDto dto:dtos) {
			if(dto.getId()!=null) {
				staffSalaryPropertyRepository.deleteById(dto.getId());
			}
			ret++;
		}
		return ret;
	}

	@Override
	public Page<StaffSalaryPropertyDto> getPage(int pageSize, int pageIndex) {
		Pageable pageable = PageRequest.of(pageIndex-1, pageSize);
		return staffSalaryPropertyRepository.getListPage(pageable);
	}

}
