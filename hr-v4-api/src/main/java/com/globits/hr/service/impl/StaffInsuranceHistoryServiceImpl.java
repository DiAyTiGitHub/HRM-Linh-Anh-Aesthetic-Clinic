package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.StaffLeaveDto;
import com.globits.hr.dto.StaffLeaveHandOverItemDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.staff.StaffInsuranceHistoryDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.StaffInsuranceHistoryService;
import com.globits.hr.service.StaffLeaveHandOverItemService;
import com.globits.hr.service.StaffLeaveService;
import jakarta.persistence.Query;
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

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class StaffInsuranceHistoryServiceImpl extends GenericServiceImpl<StaffInsuranceHistory, UUID> implements StaffInsuranceHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(StaffInsuranceHistoryServiceImpl.class);

    @Autowired
    private StaffInsuranceHistoryRepository staffInsuranceHistoryRepository;

    @Autowired
    private StaffRepository staffRepository;


    @Override
    public Page<StaffInsuranceHistoryDto> searchByPage(SearchDto dto) {
        if (dto == null) {
            return null;
        }

        int pageIndex = dto.getPageIndex() > 0 ? dto.getPageIndex() - 1 : 0;
        int pageSize = dto.getPageSize();

        String whereClause = " WHERE (1=1) ";
        String orderBy = " ORDER BY entity.startDate DESC, entity.endDate desc ";

        String sqlCount = "SELECT COUNT(entity.id) FROM StaffInsuranceHistory entity ";
        String sql = "SELECT new com.globits.hr.dto.staff.StaffInsuranceHistoryDto(entity) FROM StaffInsuranceHistory entity ";

        if (StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.staff.displayName LIKE :text " +
                    " OR entity.staff.displayName LIKE :text " +
                    " OR entity.staff.staffCode LIKE :text) ";
        }

        if (dto.getStaffId() != null) {
            whereClause += " AND entity.staff.id = :staffId ";
        }

        if (dto.getFromDate() != null) {
            whereClause += " AND date(entity.startDate) >= date(:fromDate) ";
        }

        if (dto.getToDate() != null) {
            whereClause += " AND date(entity.endDate) <= date(:toDate) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, StaffInsuranceHistoryDto.class);
        Query countQuery = manager.createQuery(sqlCount);

        if (StringUtils.hasText(dto.getKeyword())) {
            String keyword = "%" + dto.getKeyword().trim() + "%";
            query.setParameter("text", keyword);
            countQuery.setParameter("text", keyword);
        }

        if (dto.getStaffId() != null) {
            query.setParameter("staffId", dto.getStaffId());
            countQuery.setParameter("staffId", dto.getStaffId());
        }

        if (dto.getFromDate() != null) {
            query.setParameter("fromDate", dto.getFromDate());
            countQuery.setParameter("fromDate", dto.getFromDate());
        }

        if (dto.getToDate() != null) {
            query.setParameter("toDate", dto.getToDate());
            countQuery.setParameter("toDate", dto.getToDate());
        }

        long total = (long) countQuery.getSingleResult();

        query.setFirstResult(pageIndex * pageSize);
        query.setMaxResults(pageSize);

        List<StaffInsuranceHistoryDto> content = query.getResultList();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        return new PageImpl<>(content, pageable, total);
    }


    @Override
    public StaffInsuranceHistoryDto getById(UUID id) {
        if (id == null)
            return null;
        StaffInsuranceHistory entity = staffInsuranceHistoryRepository.findById(id).orElse(null);

        if (entity == null)
            return null;
        StaffInsuranceHistoryDto response = new StaffInsuranceHistoryDto(entity, true);

        return response;
    }

    @Override
    public StaffInsuranceHistoryDto saveOrUpdate(StaffInsuranceHistoryDto dto) {
        if (dto == null) {
            return null;
        }

        StaffInsuranceHistory entity = null;

        // 1. Nếu có ID thì tìm để update, không thì tạo mới
        if (dto.getId() != null) {
            entity = staffInsuranceHistoryRepository.findById(dto.getId()).orElse(null);
        }
        if (entity == null) {
            entity = new StaffInsuranceHistory();
        }

        // 2. Gán các trường đơn từ DTO
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setNote(dto.getNote());
        entity.setInsuranceSalary(dto.getInsuranceSalary());
        entity.setStaffPercentage(dto.getStaffPercentage());
        entity.setOrgPercentage(dto.getOrgPercentage());
        entity.setStaffInsuranceAmount(dto.getStaffInsuranceAmount());
        entity.setOrgInsuranceAmount(dto.getOrgInsuranceAmount());
        entity.setSocialInsuranceBookCode(dto.getSocialInsuranceBookCode());

        // 3. Gán Staff
        Staff staff = null;
        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        }
        if (staff == null) {
            return null; // hoặc throw exception nếu cần thông báo lỗi rõ hơn
        }
        entity.setStaff(staff);

        // 4. Lưu entity
        entity = staffInsuranceHistoryRepository.save(entity);

        // 5. Trả về DTO
        return new StaffInsuranceHistoryDto(entity);
    }


    @Override
    public Boolean deleteById(UUID id) {
        if (id == null)
            return false;

        StaffInsuranceHistory entity = staffInsuranceHistoryRepository.findById(id).orElse(null);
        if (entity == null)
            return false;

        staffInsuranceHistoryRepository.delete(entity);
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
