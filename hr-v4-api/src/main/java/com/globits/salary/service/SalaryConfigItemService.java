package com.globits.salary.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import com.globits.core.service.GenericService;
import com.globits.salary.domain.SalaryConfigItem;
import com.globits.salary.dto.SalaryConfigItemDto;

public interface SalaryConfigItemService extends GenericService<SalaryConfigItem, UUID> {
	public Page<SalaryConfigItemDto> getPageBySalaryConfigId(UUID salaryConfigId, int pageSize, int pageIndex);

	public SalaryConfigItemDto saveSalaryConfigItem(SalaryConfigItemDto dto);

	public SalaryConfigItemDto getSalaryConfigItem(UUID id);

}
