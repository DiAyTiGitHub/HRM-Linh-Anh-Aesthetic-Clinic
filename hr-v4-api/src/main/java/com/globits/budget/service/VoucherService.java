package com.globits.budget.service;


import com.globits.budget.dto.budget.BudgetSummaryDto;
import com.globits.core.service.GenericService;
import com.globits.budget.domain.Voucher;
import com.globits.budget.dto.budget.BudgetSearchDto;
import com.globits.budget.dto.VoucherDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface VoucherService extends GenericService<Voucher, UUID> {
    Page<VoucherDto> pagingVoucherDto(BudgetSearchDto dto);

    VoucherDto saveOrUpdate(VoucherDto dto);

    VoucherDto getById(UUID id);

    Boolean deleteById(UUID id);

    List<VoucherDto> getAll(BudgetSummaryDto dto);

    Integer deleteMultiple(List<UUID> ids);
}
