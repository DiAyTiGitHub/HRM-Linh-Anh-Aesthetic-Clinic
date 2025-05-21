package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.InsurancePackage;
import com.globits.hr.domain.InsurancePackageItem;
import com.globits.hr.dto.InsurancePackageDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.dto.SalaryTemplateDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface InsurancePackageItemService extends GenericService<InsurancePackageItem, UUID> {

    void handleSetItemsForInsurancePackage(InsurancePackage entity, InsurancePackageDto dto);

}
