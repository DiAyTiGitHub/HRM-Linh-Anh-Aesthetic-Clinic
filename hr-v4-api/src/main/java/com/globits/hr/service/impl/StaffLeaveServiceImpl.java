package com.globits.hr.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import com.globits.hr.HrConstants;
import com.globits.hr.domain.EmployeeStatus;
import com.globits.hr.domain.StaffLeaveHandOverItem;
import com.globits.hr.dto.StaffLeaveHandOverItemDto;
import com.globits.hr.repository.EmployeeStatusRepository;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
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
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffLeave;
import com.globits.hr.dto.StaffLeaveDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.StaffLeaveHandOverItemRepository;
import com.globits.hr.repository.StaffLeaveRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.StaffLeaveHandOverItemService;
import com.globits.hr.service.StaffLeaveService;

@Service
public class StaffLeaveServiceImpl extends GenericServiceImpl<StaffLeave, UUID> implements StaffLeaveService {
    private static final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);

    @Autowired
    private StaffLeaveRepository staffLeaveRepository;

    @Autowired
    private StaffLeaveHandOverItemRepository staffLeaveHandOverItemRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffLeaveHandOverItemService staffLeaveHandOverItemService;

    @Autowired
    private EmployeeStatusRepository employeeStatusRepository;

    @Override
    public Page<StaffLeaveDto> searchByPage(SearchDto dto) {
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
        String orderBy = " ORDER BY entity.leaveDate desc ";

        String sqlCount = "select count(entity.id) from StaffLeave as entity ";
        String sql = "select  new com.globits.hr.dto.StaffLeaveDto(entity) from StaffLeave as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.staff.displayName LIKE :text  or entity.staff.displayCode like :text or entity.decisionNumber like :text) ";
        }

        if (dto.getStaffId() != null) {
            whereClause += " and (entity.staff.id = :staffId) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, StaffLeaveDto.class);
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
        List<StaffLeaveDto> entities = query.getResultList();
        Page<StaffLeaveDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public StaffLeaveDto getById(UUID id) {
        if (id == null)
            return null;
        StaffLeave entity = staffLeaveRepository.findById(id).orElse(null);

        if (entity == null)
            return null;
        StaffLeaveDto response = new StaffLeaveDto(entity, true);

        return response;
    }

    @Override
    @Transactional
    public StaffLeaveDto saveOrUpdate(StaffLeaveDto dto) {
        if (dto == null) {
            return null;
        }

        StaffLeave entity = null;

        if (dto.getId() != null) {
            entity = staffLeaveRepository.findById(dto.getId()).orElse(null);
        }

        if (entity == null) {
            entity = new StaffLeave();
        }

        // Map simple fields
        entity.setDecisionNumber(dto.getDecisionNumber());
        entity.setLeaveDate(dto.getLeaveDate());
        entity.setStillInDebt(dto.getStillInDebt());
        entity.setPaidStatus(dto.getPaidStatus());

        // Set staff
        Staff staff = null;
        if (dto.getStaffId() != null) {
            staff = staffRepository.findById(dto.getStaffId()).orElse(null);
        } else if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        }
        if (staff == null) {
            return null;
        }
        entity.setStaff(staff);
        //set trang thái của nhân viên thành đã nghỉ việc
        List<EmployeeStatus> employees = employeeStatusRepository.findByCode(HrConstants.DismissPositions.DA_NGHI_VIEC.getValue());
        if (employees != null && !employees.isEmpty()) {
            staff.setStatus(employees.get(0));
            staffRepository.save(staff);
        }

        if (entity.getHandleOverItems() == null) {
            entity.setHandleOverItems(new HashSet<>());
        }

        entity.getHandleOverItems().clear();

        if (dto.getHandleOverItems() != null && !dto.getHandleOverItems().isEmpty()) {
            for (StaffLeaveHandOverItemDto item : dto.getHandleOverItems()) {
                StaffLeaveHandOverItem handOverItem = null;
                if (item.getId() != null) {
                    handOverItem = staffLeaveHandOverItemRepository.findById(item.getId()).orElse(null);
                }
                if (handOverItem == null) {
                    handOverItem = new StaffLeaveHandOverItem();
                }
                // Map simple fields
                handOverItem.setName(item.getName());
                handOverItem.setNote(item.getNote());
                handOverItem.setDisplayOrder(item.getDisplayOrder());
                handOverItem.setHandoverDate(item.getHandoverDate());
                handOverItem.setIsHandovered(item.getIsHandovered());

                // Set staff leave
                handOverItem.setStaffLeave(entity);

                entity.getHandleOverItems().add(handOverItem);
            }
        }

        // Save staff leave
        entity = staffLeaveRepository.save(entity);

        return new StaffLeaveDto(entity); // return with full handover items
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id == null)
            return false;

        StaffLeave entity = staffLeaveRepository.findById(id).orElse(null);
        if (entity == null)
            return false;

        staffLeaveRepository.delete(entity);
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
