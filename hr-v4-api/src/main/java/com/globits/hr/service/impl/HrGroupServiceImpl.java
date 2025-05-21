package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.HrGroup;
import com.globits.hr.domain.HrGroupStaff;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.HrGroupDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchHrGroupDto;
import com.globits.hr.repository.HrGroupRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.HrGroupService;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HrGroupServiceImpl extends GenericServiceImpl<HrGroup, UUID> implements HrGroupService {

    @Autowired
    private HrGroupRepository hrGroupRepository;
    @Autowired
    private StaffRepository staffRepository;

    @Override
    public HrGroupDto getById(UUID id) {
        HrGroup hrGroup = hrGroupRepository.findById(id).orElse(null);
        if (hrGroup != null) {
            return new HrGroupDto(hrGroup);
        }
        return null;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteHrGroup(UUID id) {
        if (id == null) return false;
        HrGroup hrGroup = hrGroupRepository.findById(id).orElse(null);
        if (hrGroup == null) return false;

        // Remove all associated HrGroupStaff entries
        hrGroup.getHrGroupStaffs().clear();
        hrGroupRepository.save(hrGroup);
        hrGroupRepository.delete(hrGroup);
        return true;
    }

    @Override
    @Transactional
    public HrGroupDto saveHrGroup(HrGroupDto dto) {
        if (dto == null) {
            System.out.println("Dto is null");
            return null;
        }

        HrGroup hrGroup = dto.getId() != null ? hrGroupRepository.findById(dto.getId()).orElse(new HrGroup()) : new HrGroup();
        hrGroup.setName(dto.getName());
        hrGroup.setCode(dto.getCode());
        hrGroup.setDescription(dto.getDescription());
        hrGroup.setGroupType(dto.getGroupType());

        Set<HrGroupStaff> existingHrGroupStaffs = hrGroup.getHrGroupStaffs() != null ? hrGroup.getHrGroupStaffs() : new HashSet<>();
        Set<HrGroupStaff> hrGroupStaffsToRemove = new HashSet<>(existingHrGroupStaffs);

        if (dto.getStaffs() != null && !dto.getStaffs().isEmpty()) {
            for (StaffDto staffDto : dto.getStaffs()) {
                Staff staff = staffRepository.findById(staffDto.getId()).orElse(null);
                if (staff != null) {
                    boolean staffExists = false;
                    for (HrGroupStaff existingHrGroupStaff : existingHrGroupStaffs) {
                        if (existingHrGroupStaff.getStaff().getId().equals(staff.getId())) {
                            staffExists = true;
                            hrGroupStaffsToRemove.remove(existingHrGroupStaff);
                            break;
                        }
                    }
                    if (!staffExists) {
                        HrGroupStaff hrGroupStaff = new HrGroupStaff();
                        hrGroupStaff.setHrGroup(hrGroup);
                        hrGroupStaff.setStaff(staff);
                        existingHrGroupStaffs.add(hrGroupStaff);
                    }
                }
            }
        }

        for (HrGroupStaff hrGroupStaffToRemove : hrGroupStaffsToRemove) {
            hrGroup.getHrGroupStaffs().remove(hrGroupStaffToRemove);
        }
        hrGroup.setHrGroupStaffs(existingHrGroupStaffs);
        hrGroup = hrGroupRepository.save(hrGroup);
        return new HrGroupDto(hrGroup);
    }




    @Override
    public Page<HrGroupDto> pagingHrGroup(SearchHrGroupDto dto) {
        if (dto == null) return null;

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();
        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.createDate ";

        String sqlCount = "select count(distinct entity.id) from HrGroup as entity ";
        String sql = "select distinct new com.globits.hr.dto.HrGroupDto(entity) from HrGroup as entity ";
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }

        if (dto.getHrGroupId() != null) {
            whereClause += " AND ( entity.id = :hrGroupId) ";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, HrGroupDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        if (dto.getHrGroupId() != null) {
            query.setParameter("hrGroupId", dto.getHrGroupId());
            qCount.setParameter("hrGroupId", dto.getHrGroupId());
        }

        long count = (long) qCount.getSingleResult();

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<HrGroupDto> entities = query.getResultList();
        Page<HrGroupDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    @Transactional
    public Boolean deleteMultipleHrGroup(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return false;
        boolean isValid = true;
        for (UUID hrGroupId : ids) {
            boolean deleteRes = this.deleteHrGroup(hrGroupId);
            if (!deleteRes) isValid = false;
        }
        return isValid;
    }
}
