package com.globits.hr.service.impl;

import com.globits.core.repository.PersonRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.*;
import com.globits.hr.dto.PersonBankAccountDto;
import com.globits.hr.dto.StaffLeaveDto;
import com.globits.hr.dto.StaffLeaveHandOverItemDto;
import com.globits.hr.dto.importExcel.StaffBankAccountImport;
import com.globits.hr.dto.search.PersonBankAccountSearchDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.PersonBankAccountService;
import com.globits.hr.service.StaffLeaveHandOverItemService;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.domain.SalaryTemplateItem;
import com.globits.salary.domain.SalaryTemplateItemGroup;
import com.globits.salary.dto.SalaryTemplateItemDto;
import jakarta.persistence.Query;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StaffLeaveHandOverItemServiceImpl extends GenericServiceImpl<StaffLeaveHandOverItem, UUID>
        implements StaffLeaveHandOverItemService {
    private static final Logger logger = LoggerFactory.getLogger(StaffLeaveHandOverItemServiceImpl.class);

    @Autowired
    private StaffLeaveHandOverItemRepository staffLeaveHandOverItemRepository;

    @Autowired
    private StaffLeaveRepository staffLeaveRepository;

    @Override
    public void handleSetHandOverItems(StaffLeave entity, StaffLeaveDto dto) {
        if (entity.getHandleOverItems() == null) {
            entity.setHandleOverItems(new HashSet<>());
        }
        entity.getHandleOverItems().clear();

        if (dto.getHandleOverItems() != null && !dto.getHandleOverItems().isEmpty()) {
            for (StaffLeaveHandOverItemDto itemDto : dto.getHandleOverItems()) {
                StaffLeaveHandOverItem item = null;

                if (itemDto.getId() != null) {
                    item = staffLeaveHandOverItemRepository.findById(itemDto.getId()).orElse(null);
                }
                if (item == null) {
                    item = new StaffLeaveHandOverItem();
                }

                item.setStaffLeave(entity);
                item.setDisplayOrder(itemDto.getDisplayOrder());
                item.setName(itemDto.getName());
                item.setNote(itemDto.getNote());
                item.setHandoverDate(itemDto.getHandoverDate());
                item.setIsHandovered(itemDto.getIsHandovered());

                entity.getHandleOverItems().add(item);
            }
        }
    }

}
