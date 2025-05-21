package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffAnnualLeaveHistory;
import com.globits.hr.domain.StaffMonthlyLeaveHistory;
import com.globits.hr.dto.StaffAnnualLeaveHistoryDto;
import com.globits.hr.dto.StaffMonthlyLeaveHistoryDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.staff.StaffInsuranceHistoryDto;
import com.globits.hr.repository.StaffAnnualLeaveHistoryRepository;
import com.globits.hr.repository.StaffMonthlyLeaveHistoryRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.StaffAnnualLeaveHistoryService;
import com.globits.hr.service.StaffMonthlyLeaveHistoryService;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.domain.SalaryTemplateItem;
import com.globits.salary.domain.SalaryTemplateItemGroup;
import com.globits.salary.dto.SalaryTemplateItemDto;
import jakarta.persistence.Column;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class StaffMonthlyLeaveHistoryServiceImpl extends GenericServiceImpl<StaffMonthlyLeaveHistory, UUID> implements StaffMonthlyLeaveHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(StaffMonthlyLeaveHistoryServiceImpl.class);

    @Autowired
    private StaffAnnualLeaveHistoryRepository staffAnnualLeaveHistoryRepository;

    @Autowired
    private StaffMonthlyLeaveHistoryRepository staffMonthlyLeaveHistoryRepository;

    @Autowired
    private StaffRepository staffRepository;


    @Override
    public void handleSetMonthlyLeaveHistoryForAnnualLeave(StaffAnnualLeaveHistory entity, StaffAnnualLeaveHistoryDto dto) {
        if (entity.getMonthlyLeaveHistories() == null)
            entity.setMonthlyLeaveHistories(new HashSet<>());

        Set<StaffMonthlyLeaveHistory> monthlyLeaveHistories = new HashSet<>();
        if (dto.getMonthlyLeaveHistories() != null && !dto.getMonthlyLeaveHistories().isEmpty()) {
            int monthIndex = 1;
            boolean useMonthIndex = false;

            if (dto.getMonthlyLeaveHistories().size() == 12) {
                useMonthIndex = true;
            }

            for (StaffMonthlyLeaveHistoryDto itemDto : dto.getMonthlyLeaveHistories()) {
                StaffMonthlyLeaveHistory item = null;
                if (itemDto.getId() != null) {
                    item = staffMonthlyLeaveHistoryRepository.findById(itemDto.getId()).orElse(null);
                }
                if (item == null && itemDto.getMonth() != null && entity.getYear() != null && entity.getStaff() != null) {
                    List<StaffMonthlyLeaveHistory> availableResults = staffMonthlyLeaveHistoryRepository.findByStaffIdYearAndMonth(entity.getStaff().getId(), entity.getYear(), itemDto.getMonth());
                    if (availableResults != null && !availableResults.isEmpty()) {
                        item = availableResults.get(0);
                    }
                }
                if (item == null) {
                    item = new StaffMonthlyLeaveHistory();
                    if (itemDto.getId() != null)
                        item.setId(itemDto.getId());
                }

                if (itemDto.getMonth() == null && useMonthIndex) {
                    itemDto.setMonth(monthIndex);
                }
                item.setMonth(itemDto.getMonth()); // Tháng thống kê nghỉ phép
                item.setLeaveDays((itemDto.getLeaveDays())); // Số ngày nhân viên đã nghỉ trong tháng

                item.setAnnualLeaveHistory(entity);

                monthlyLeaveHistories.add(item);
                monthIndex++;
            }
        }

        entity.getMonthlyLeaveHistories().clear();
        entity.getMonthlyLeaveHistories().addAll(monthlyLeaveHistories);
    }
}
