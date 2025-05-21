package com.globits.hr.service.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.InsurancePackage;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffInsurancePackage;
import com.globits.hr.dto.StaffInsurancePackageDto;
import com.globits.hr.dto.search.SearchStaffInsurancePackageDto;
import com.globits.hr.repository.InsurancePackageRepository;
import com.globits.hr.repository.StaffInsurancePackageRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.StaffInsurancePackageService;

import jakarta.persistence.Query;

@Service
public class StaffInsurancePackageServiceImpl extends GenericServiceImpl<StaffInsurancePackage, UUID>
		implements StaffInsurancePackageService {
	private static final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);

	@Autowired
	private StaffInsurancePackageRepository staffInsurancePackageRepository;

	@Autowired
	private StaffRepository staffRepository;

	@Autowired
	private InsurancePackageRepository insurancePackageRepository;

	@Override
	public Page<StaffInsurancePackageDto> searchByPage(SearchStaffInsurancePackageDto dto) {
		if (dto == null) {
			return null;
		}
		int pageIndex = dto.getPageIndex();
		int pageSize = dto.getPageSize();

		if (pageIndex > 0) {
			pageIndex--;
		} else {
			pageIndex = 0;
		}

		String whereClause = " where (1=1) ";
		String orderBy = " ORDER BY entity.startDate desc, entity.endDate desc ";

		String sqlCount = "select count( entity.id) from StaffInsurancePackage as entity ";
		String sql = "select  new com.globits.hr.dto.StaffInsurancePackageDto(entity) from StaffInsurancePackage as entity ";

		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			whereClause += " AND (entity.staff.displayName LIKE :text  or entity.staff.staffCode LIKE :text) ";
		}

		if (dto.getStaffId() != null) {
			whereClause += " and (entity.staff.id = :staffId) ";
		}

		if (dto.getInsurancePackageId() != null) {
			whereClause += " and (entity.insurancePackage.id = :insurancePackageId) ";
		}

		sql += whereClause + orderBy;
		sqlCount += whereClause;

		Query query = manager.createQuery(sql, StaffInsurancePackageDto.class);
		Query qCount = manager.createQuery(sqlCount);

		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			query.setParameter("text", '%' + dto.getKeyword() + '%');
			qCount.setParameter("text", '%' + dto.getKeyword() + '%');
		}

		if (dto.getStaffId() != null) {
			query.setParameter("staffId", dto.getStaffId());
			qCount.setParameter("staffId", dto.getStaffId());
		}

		long count = (long) qCount.getSingleResult();

		int startPosition = pageIndex * pageSize;
		query.setFirstResult(startPosition);
		query.setMaxResults(pageSize);

		Pageable pageable = PageRequest.of(pageIndex, pageSize);
		List<StaffInsurancePackageDto> entities = query.getResultList();
		Page<StaffInsurancePackageDto> result = new PageImpl<>(entities, pageable, count);

		return result;
	}

	@Override
	public StaffInsurancePackageDto getById(UUID id) {
		if (id == null)
			return null;
		StaffInsurancePackage entity = staffInsurancePackageRepository.findById(id).orElse(null);

		if (entity == null)
			return null;
		StaffInsurancePackageDto response = new StaffInsurancePackageDto(entity, true);

		return response;
	}

	@Override
	public StaffInsurancePackageDto saveOrUpdate(StaffInsurancePackageDto dto) {
		if (dto == null) {
			return null;
		}

		StaffInsurancePackage entity = null;
		if (dto.getId() != null) {
			entity = staffInsurancePackageRepository.findById(dto.getId()).orElse(null);
		}
		if (entity == null) {
			entity = new StaffInsurancePackage();
		}

		Staff staff = null;
		if (dto.getStaff() != null && dto.getStaff().getId() != null) {
			staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
		}
		entity.setStaff(staff);

		InsurancePackage insurancePackage = null;
		if (dto.getInsurancePackage() != null && dto.getInsurancePackage().getId() != null) {
			insurancePackage = insurancePackageRepository.findById(dto.getInsurancePackage().getId()).orElse(null);
		}
		entity.setInsurancePackage(insurancePackage);

		entity.setStartDate(dto.getStartDate());
		entity.setEndDate(dto.getEndDate());
		entity.setInsuranceAmount(dto.getInsuranceAmount());
		entity.setCompensationAmount(dto.getCompensationAmount());
		entity.setStaffPercentage(dto.getStaffPercentage());
		entity.setOrgPercentage(dto.getOrgPercentage());
		entity.setHasFamilyParticipation(dto.getHasFamilyParticipation());

		// Lưu entity
		entity = staffInsurancePackageRepository.saveAndFlush(entity);

		return new StaffInsurancePackageDto(entity);
	}

	@Override
	public Boolean deleteById(UUID id) {
		if (id == null)
			return false;

		StaffInsurancePackage entity = staffInsurancePackageRepository.findById(id).orElse(null);
		if (entity == null)
			return false;

		staffInsurancePackageRepository.delete(entity);
		return true;
	}

	@Override
	public Boolean deleteMultiple(List<UUID> ids) {
		if (ids == null)
			return false;
		boolean isValid = true;
		for (UUID itemId : ids) {
			boolean deleteRes = this.deleteById(itemId);
			if (!deleteRes)
				isValid = false;
		}
		return isValid;
	}
}
