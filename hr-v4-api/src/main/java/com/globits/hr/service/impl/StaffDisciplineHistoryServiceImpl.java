package com.globits.hr.service.impl;

import com.globits.core.domain.FileDescription;
import com.globits.core.repository.FileDescriptionRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.*;
import com.globits.hr.dto.StaffDisciplineHistoryDto;
import com.globits.hr.dto.search.StaffDisciplineHistorySearchDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.StaffDisciplineHistoryService;
import jakarta.persistence.Query;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class StaffDisciplineHistoryServiceImpl extends GenericServiceImpl<StaffDisciplineHistory, UUID> implements StaffDisciplineHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(StaffDisciplineHistoryServiceImpl.class);

    @Autowired
    private StaffDisciplineHistoryRepository staffDisciplineHistoryRepository;

    @Autowired
    private HRDisciplineRepository hrDisciplineRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private HRDepartmentRepository hrDepartmentRepository;

    @Autowired
    private HrOrganizationRepository hrOrganizationRepository;

    @Autowired
    private FileDescriptionRepository fileDescriptionRepository;


    @Override
    public Page<StaffDisciplineHistoryDto> searchByPage(StaffDisciplineHistorySearchDto dto) {
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
        String orderBy = " ORDER BY entity.disciplineDate ";

        String sqlCount = "select count( entity.id) from StaffDisciplineHistory as entity ";
        String sql = "select  new com.globits.hr.dto.StaffDisciplineHistoryDto(entity) from StaffDisciplineHistory as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.staff.displayName LIKE :text  or entity.staff.staffCode like :text ) ";
        }

        if (dto.getStaffId() != null) {
            whereClause += " and (entity.staff.id = :staffId) ";
        }

        if (dto.getDisciplineId() != null) {
            whereClause += " and (entity.bank.id = :disciplineId) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, StaffDisciplineHistoryDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", dto.getKeyword());
            qCount.setParameter("staffId", dto.getKeyword());
        }

        if (dto.getStaffId() != null) {
            query.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }

        if (dto.getDisciplineId() != null) {
            query.setParameter("disciplineId", dto.getDisciplineId());
            qCount.setParameter("disciplineId", dto.getDisciplineId());
        }

        long count = (long) qCount.getSingleResult();

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<StaffDisciplineHistoryDto> entities = query.getResultList();
        Page<StaffDisciplineHistoryDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public StaffDisciplineHistoryDto getById(UUID id) {
        if (id == null) return null;
        StaffDisciplineHistory entity = staffDisciplineHistoryRepository.findById(id).orElse(null);

        if (entity == null) return null;
        StaffDisciplineHistoryDto response = new StaffDisciplineHistoryDto(entity, true);

        return response;
    }

    @Override
    public StaffDisciplineHistoryDto saveOrUpdate(StaffDisciplineHistoryDto dto) {
        if (dto == null) {
            return null;
        }

        StaffDisciplineHistory entity = new StaffDisciplineHistory();
        if (dto.getId() != null) entity = staffDisciplineHistoryRepository.findById(dto.getId()).orElse(null);
        if (entity == null) entity = new StaffDisciplineHistory();

        if (dto.getStaffId() != null) {
            Staff staff = staffRepository.findById(dto.getStaffId()).orElse(null);
            entity.setStaff(staff);
        }

        if (entity.getStaff() == null && dto.getStaff() != null) {
            Staff staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
            entity.setStaff(staff);
        }

        if (entity.getStaff() == null) return null;

        if (dto.getDiscipline() != null) {
            HRDiscipline discipline = hrDisciplineRepository.findById(dto.getDiscipline().getId()).orElse(null);
            if (discipline == null) return null;

            entity.setDiscipline(discipline);
        }

        if (entity.getDiscipline() == null) return null;

        entity.setDisciplineDate(dto.getDisciplineDate());

        if (dto.getOrganization() != null) {
            HrOrganization organization = hrOrganizationRepository.findById(dto.getOrganization().getId()).orElse(null);

            entity.setOrganization(organization);
        }

        if (dto.getDepartment() != null) {
            HRDepartment department = hrDepartmentRepository.findById(dto.getDepartment().getId()).orElse(null);

            entity.setDepartment(department);
        }

        if (dto.getFile() != null) {
            FileDescription fileDescription = fileDescriptionRepository.findById(dto.getFile().getId()).orElse(null);

            entity.setFile(fileDescription);
        }

        entity = staffDisciplineHistoryRepository.saveAndFlush(entity);

        return new StaffDisciplineHistoryDto(entity);
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id == null) return false;

        StaffDisciplineHistory entity = staffDisciplineHistoryRepository.findById(id).orElse(null);
        if (entity == null) return false;

        staffDisciplineHistoryRepository.delete(entity);
        return true;
    }

    @Override
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID itemId : ids) {
            boolean deleteRes = this.deleteById(itemId);
            if (!deleteRes) isValid = false;
        }
        return isValid;
    }

    private void createCell(Row row, int cellIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }
        cell.setCellStyle(style);
    }
}
