package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.PersonBankAccount;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffLeave;
import com.globits.hr.domain.StaffLeaveHandOverItem;
import com.globits.hr.dto.PersonBankAccountDto;
import com.globits.hr.dto.StaffLeaveDto;
import com.globits.hr.dto.importExcel.StaffBankAccountImport;
import com.globits.hr.dto.search.PersonBankAccountSearchDto;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StaffLeaveHandOverItemService extends GenericService<StaffLeaveHandOverItem, UUID> {
    void handleSetHandOverItems(StaffLeave entity, StaffLeaveDto dto);

}
