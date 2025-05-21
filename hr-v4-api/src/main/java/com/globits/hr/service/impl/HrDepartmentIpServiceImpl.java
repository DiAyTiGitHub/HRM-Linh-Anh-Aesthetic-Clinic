package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.HRDepartment;
import com.globits.hr.domain.HrDepartmentIp;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.HrDepartmentIpDto;
import com.globits.hr.dto.search.SearchHrDepartmentDto;
import com.globits.hr.repository.HRDepartmentRepository;
import com.globits.hr.repository.HrDepartmentIpRepository;
import com.globits.hr.repository.PositionRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.HrDepartmentIpService;
import jakarta.annotation.Resource;
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

@Service
public class HrDepartmentIpServiceImpl extends GenericServiceImpl<HrDepartmentIp, UUID> implements HrDepartmentIpService {

    @Autowired
    private HRDepartmentRepository hrDepartmentRepository;

    @Autowired
    private HrDepartmentIpRepository hrDepartmentIpRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Override
    @Modifying
    public HrDepartmentIpDto saveHrDepartmentIp(HrDepartmentIpDto dto) {
        if (dto == null || dto.getIpAddress() == null) {
            return null;
        }

        HrDepartmentIp hrDepartmentIp = new HrDepartmentIp();
        if (dto.getId() != null) {
            hrDepartmentIp = hrDepartmentIpRepository.findById(dto.getId()).orElse(null);
            if (hrDepartmentIp == null) return null;
        }
        if (hrDepartmentIp == null) hrDepartmentIp = new HrDepartmentIp();

        dto.setIpAddress(dto.getIpAddress().trim());
        hrDepartmentIp.setIpAddress(dto.getIpAddress());
        hrDepartmentIp.setDescription(dto.getDescription());

        if (dto.getDepartment() != null && dto.getDepartment().getId() != null) {
            HRDepartment hrDepartment = hrDepartmentRepository.findById(dto.getDepartment().getId()).orElse(null);
            if (hrDepartment == null) return null;
            hrDepartmentIp.setDepartment(hrDepartment);
        } else {
            hrDepartmentIp.setDepartment(null);
        }

        hrDepartmentIp = hrDepartmentIpRepository.save(hrDepartmentIp);
        if (hrDepartmentIp == null) return null;

        return new HrDepartmentIpDto(hrDepartmentIp);
    }

    @Override
    public HrDepartmentIpDto getById(UUID id) {
        HrDepartmentIp hrDepartmentIp = hrDepartmentIpRepository.findById(id).orElse(null);
        if (hrDepartmentIp != null) {
            return new HrDepartmentIpDto(hrDepartmentIp);
        }
        return null;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteHrDepartmentIp(UUID id) {
        if (id == null) return false;
        HrDepartmentIp hrDepartmentIp = hrDepartmentIpRepository.findById(id).orElse(null);
        if (hrDepartmentIp == null) {
            return false;
        }
        hrDepartmentIpRepository.delete(hrDepartmentIp);
        return true;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteMultipleHrDepartmentIps(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID hrDepartmentIpId : ids) {
            Boolean deleteRes = this.deleteHrDepartmentIp(hrDepartmentIpId);
            if (!deleteRes) isValid = false;
        }
        return isValid;
    }

    @Override
    public Page<HrDepartmentIpDto> pagingHrDepartmentIp(SearchHrDepartmentDto dto) {
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
        String orderBy = " ORDER BY entity.createDate ";

        String sqlCount = "select count(distinct entity.id) from HrDepartmentIp as entity ";
        String sql = "select distinct new com.globits.hr.dto.HrDepartmentIpDto(entity) from HrDepartmentIp as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.ipAddress LIKE :text OR entity.description LIKE :text or entity.department.code like :text or entity.department.name like :text ) ";
        }
        if (dto.getDepartmentId() != null) {
            whereClause += " AND ( entity.department.id = :deptId) ";
        }
        if (dto.getOrganizationId() != null) {
            whereClause += " AND ( entity.department.organization.id = :organizationId) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, HrDepartmentIpDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getDepartmentId() != null) {
            query.setParameter("deptId", dto.getDepartmentId());
            qCount.setParameter("deptId", dto.getDepartmentId());
        }
        if (dto.getOrganizationId() != null) {
            query.setParameter("organizationId", dto.getOrganizationId());
            qCount.setParameter("organizationId", dto.getOrganizationId());
        }
        long count = (long) qCount.getSingleResult();

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        List<HrDepartmentIpDto> entities = query.getResultList();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        return new PageImpl<>(entities, pageable, count);
    }

    // Kiểm tra địa chỉ IP có hợp lệ hay không
    @Override
    public Boolean isValidTimekeepingIP(UUID staffId, String timekeepingIP) {
        if (staffId == null || timekeepingIP == null) return false;

        // Nhân viên cần chấm công
        Staff staff = staffRepository.findById(staffId).orElse(null);
        if (staff == null) return null;

        //Kiểm tra điề kiện nhân viên có được phép chấm công ngoài ip hay không
        if (staff.getAllowExternalIpTimekeeping() != null && staff.getAllowExternalIpTimekeeping()) return true;

        // Phòng ban hiện thời của nhân viên
//        Set<UUID> departmentIds = new HashSet<>();
//        if (staff.getCurrentPositions() != null && !staff.getCurrentPositions().isEmpty()) {
//            for (Position position : staff.getCurrentPositions()) {
//                if (position.getDepartment() != null) {
//                    departmentIds.add(position.getDepartment().getId());
//                }
//            }
//        }
//
//        List<HrDepartmentIp> validDepIps = hrDepartmentIpRepository.getAllValidDepartmentIps(new ArrayList<>(departmentIds));

        List<HrDepartmentIp> validDepIps = hrDepartmentIpRepository.findAll();


        if (validDepIps != null && !validDepIps.isEmpty()) {
            for (HrDepartmentIp validDepIp : validDepIps) {
                if (timekeepingIP.contains(validDepIp.getIpAddress())) return true;
            }
        }

        return false;
    }
}
