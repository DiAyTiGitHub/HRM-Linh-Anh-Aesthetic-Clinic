package com.globits.timesheet.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Staff;
import com.globits.hr.service.StaffService;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.timesheet.domain.Journal;
import com.globits.timesheet.dto.JournalDto;
import com.globits.timesheet.dto.search.SearchJournalDto;
import com.globits.timesheet.repository.JournalRepository;
import com.globits.timesheet.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JournalServiceImpl extends GenericServiceImpl<Journal, UUID> implements JournalService {
    @Autowired
    JournalRepository journalRepository;

    @Autowired
    StaffService staffService;

    @Override
    public JournalDto getJournal(UUID id) {
        Journal journal = this.getEntityById(id);
        if (journal != null) {
            return new JournalDto(journal);
        }
        return null;
    }

    @Override
    public Journal getEntityById(UUID id) {
        Journal entity = null;
        Optional<Journal> optional = journalRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        return entity;
    }

    @Override
    public Boolean deleteById(UUID id) {
        try {
            journalRepository.deleteById(id);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public JournalDto saveOrUpdate(UUID id, JournalDto dto) {
        if (dto == null) {
            return null;
        }

        Journal journal = null;
        if (id != null) {
            journal = this.getEntityById(id);
        }

        if (journal == null && dto.getId() != null) {
            journal = this.getEntityById(dto.getId());
        }

        if (journal == null) {
            journal = new Journal();
        }
        if (dto.getFromDate() != null && dto.getToDate() != null && dto.getToDate().before(dto.getFromDate())) {
            return null;
        }
        if (dto.getName() != null) {
            journal.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            journal.setDescription(dto.getDescription());
        }
        if (dto.getJournalDate() != null) {
            journal.setJournalDate(dto.getJournalDate());
        }
        if (dto.getFromDate() != null) {
            journal.setFromDate(dto.getFromDate());
        }
        if (dto.getToDate() != null) {
            journal.setToDate(dto.getToDate());
        }
        if (dto.getLocation() != null) {
            journal.setLocation(dto.getLocation());
        }
        if (dto.getType() != null) {
            journal.setType(dto.getType());
        }
        if (dto.getStaffId() != null) {
            Staff staff = staffService.getEntityById(dto.getStaffId());
            journal.setStaff(staff);
        }
        journal = journalRepository.save(journal);
        return new JournalDto(journal);
    }

    @Override
    public Page<JournalDto> pagingJournals(SearchJournalDto dto) {
        if (dto == null) {
            return null;
        }
        if (dto.getFromDate() != null && dto.getToDate() != null && dto.getToDate().before(dto.getFromDate())) {
            return null;
        }
        if (dto.getFromDate() != null) {
            dto.setFromDate(DateTimeUtil.getStartOfDay(dto.getFromDate()));
        }
        if (dto.getToDate() != null) {
            dto.setToDate(DateTimeUtil.getEndOfDay(dto.getToDate()));
        }
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();
        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        String whereClause = "";
        String sql = "select new com.globits.timesheet.dto.JournalDto(entity) from Journal entity where (1=1) ";
        String sqlCount = "select count(entity.id) from Journal as entity where (1=1) ";
        if (dto.getType() != null) {
            whereClause += " AND entity.type=:typeTmp";
        }
        sql += whereClause;
        sqlCount += whereClause;
        Query query = manager.createQuery(sql, JournalDto.class);
        Query qCount = manager.createQuery(sqlCount);
        if (dto.getType() != null) {
            query.setParameter("typeTmp", dto.getType());
            qCount.setParameter("typeTmp", dto.getType());
        }
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        List<JournalDto> entities = query.getResultList();
        long count = (long) qCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public List<JournalDto> getListJournal(SearchJournalDto dto) {
        if (dto == null) {
            return null;
        }
        if (dto.getFromDate() != null && dto.getToDate() != null && dto.getToDate().before(dto.getFromDate())) {
            return null;
        }
        if (dto.getFromDate() != null) {
            dto.setFromDate(DateTimeUtil.getStartOfDay(dto.getFromDate()));
        }
        if (dto.getToDate() != null) {
            dto.setToDate(DateTimeUtil.getEndOfDay(dto.getToDate()));
        }

        String whereClause = "";
        String sql = "select new com.globits.timesheet.dto.JournalDto(entity) from Journal entity where (1=1) ";
        if (dto.getType() != null) {
            whereClause += " AND entity.type = :typeTmp";
        }
        if (dto.getStaffId() != null) {
            whereClause += " AND entity.staff.id = :staffId";
        }
        if (dto.getYearReport() != null && dto.getMonthReport() != null) {
            whereClause += " AND MONTH(entity.journalDate) = :monthReport AND YEAR(entity.journalDate) = :yearReport";
        }
        sql += whereClause;
        Query query = manager.createQuery(sql, JournalDto.class);
        if (dto.getType() != null) {
            query.setParameter("typeTmp", dto.getType());
        }
        if (dto.getStaffId() != null) {
            query.setParameter("staffId", dto.getStaffId());
        }
        if (dto.getYearReport() != null && dto.getMonthReport() != null) {
            query.setParameter("monthReport", dto.getMonthReport());
            query.setParameter("yearReport", dto.getYearReport());
        }
        List<JournalDto> entities = query.getResultList();
        return entities;
    }
}
