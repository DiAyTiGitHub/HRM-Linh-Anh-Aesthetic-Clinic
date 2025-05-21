package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffMaternityHistory;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.LabourAgreementDto;
import com.globits.hr.dto.StaffMaternityHistoryDto;
import com.globits.hr.dto.StaffSignatureDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchStaffSignatureDto;
import com.globits.hr.repository.StaffMaternityHistoryRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.StaffMaternityHistoryService;
import jakarta.persistence.Query;
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
public class StaffMaternityHistoryServiceImpl extends GenericServiceImpl<StaffMaternityHistory, UUID> implements StaffMaternityHistoryService {
    @Autowired
    StaffMaternityHistoryRepository staffMaternityHistoryRepository;
    @Autowired
    StaffRepository staffRepository;

    public static String formatMaternityHistory(StaffMaternityHistory history) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fromDate = history.getStartDate() != null ? sdf.format(history.getStartDate()) : "N/A";
        String toDate = history.getEndDate() != null ? sdf.format(history.getEndDate()) : "N/A";
        return String.format("Loại nghỉ: Thai sản - Từ ngày %s - đến ngày %s", fromDate, toDate);
    }

    @Override
    public Page<StaffMaternityHistoryDto> searchByPage(SearchDto dto) {
        if (dto == null) {
            return Page.empty();
        }
        int pageIndex = dto.getPageIndex() > 0 ? dto.getPageIndex() - 1 : 0;
        int pageSize = dto.getPageSize();

        String sqlCount = "SELECT COUNT(entity.id) FROM StaffMaternityHistory AS entity WHERE (1=1)";
        String sql = "SELECT new com.globits.hr.dto.StaffMaternityHistoryDto(entity) FROM StaffMaternityHistory AS entity WHERE (1=1) ";
        String whereClause = "";
        String orderBy = " ORDER BY entity.id DESC";

        if (dto.getStaffId() != null) {
            whereClause += " AND entity.staff.id = :staffId";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, StaffSignatureDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getStaffId() != null) {
            q.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }

        q.setFirstResult(pageIndex * pageSize);
        q.setMaxResults(pageSize);
        List<StaffMaternityHistoryDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        return new PageImpl<>(entities, PageRequest.of(pageIndex, pageSize), count);
    }

    @Override
    public Page<StaffMaternityHistoryDto> getPage(int pageIndex, int pageSize) {
        if (pageIndex > 1) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return this.staffMaternityHistoryRepository.getPages(pageable);
    }

    public List<StaffMaternityHistoryDto> getAll(UUID id) {
        return this.staffMaternityHistoryRepository.getAll(id);
    }

    @Override
    public StaffMaternityHistoryDto getStaffMaternityHistoryById(UUID id) {
        return this.staffMaternityHistoryRepository.getStaffMaternityHistoryById(id);
    }

    @Override
    public StaffMaternityHistoryDto saveStaffMaternityHistory(StaffMaternityHistoryDto dto) {
        if (dto == null) {
            return null;
        }
        StaffMaternityHistory entity = null;
        if (dto.getId() != null) {
            entity = staffMaternityHistoryRepository.findById(dto.getId()).orElse(null);
        }
        if (entity == null) {
            entity = new StaffMaternityHistory();
        }

        Staff staff = null;
        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        }
        if (staff == null) {
            return null;
        }

        entity.setStaff(staff);
        entity.setNote(dto.getNote());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setBirthNumber(dto.getBirthNumber());
        entity.setMaternityLeaveEndDate(dto.getMaternityLeaveEndDate());
        entity.setMaternityLeaveStartDate(dto.getMaternityLeaveStartDate());


        entity = staffMaternityHistoryRepository.save(entity);
        return new StaffMaternityHistoryDto(entity);

    }

    @Override
    public Boolean removeLists(List<UUID> ids) {
        if (ids != null && ids.size() > 0) {
            for (UUID id : ids) {
                this.staffMaternityHistoryRepository.deleteById(id);
            }
        }
        return false;

    }

    @Override
    public StaffMaternityHistoryDto removeStaffMaternityHistory(UUID id) {
        StaffMaternityHistoryDto staffMaternityHistoryDto = new StaffMaternityHistoryDto(this.staffMaternityHistoryRepository.getOne(id));
        if (staffMaternityHistoryRepository != null) {
            this.staffMaternityHistoryRepository.deleteById(id);
        }
        return staffMaternityHistoryDto;
    }

    @Override
    public void handleSetDuringPregnancyStatus(StaffWorkSchedule staffWorkSchedule) {
        if (staffWorkSchedule == null || staffWorkSchedule.getStaff() == null || staffWorkSchedule.getWorkingDate() == null)
            return;

        List<StaffMaternityHistory> durations = staffMaternityHistoryRepository.findByIncludedDate(staffWorkSchedule.getStaff().getId(), staffWorkSchedule.getWorkingDate());

        if (durations == null || durations.isEmpty()) {
            staffWorkSchedule.setDuringPregnancy(false);
        } else {
            staffWorkSchedule.setDuringPregnancy(true);
        }
    }
}
