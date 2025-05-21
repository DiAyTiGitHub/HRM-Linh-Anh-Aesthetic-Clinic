package com.globits.hr.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.PositionStaff;
import com.globits.hrv3.dto.view.PositionStaffV3Dto;

@Service
public interface PositionStaffV3Service extends GenericService<PositionStaff, UUID> {
	PositionStaffV3Dto getById(UUID id);
	List<PositionStaffV3Dto> getListByStaffId(UUID staffId);
	PositionStaffV3Dto saveOrUpdate(PositionStaffV3Dto dto,UUID id);
	Boolean remove(UUID id);
	Boolean removeList(List<UUID> ids);
}
