package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.core.utils.CoreDateTimeUtil;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.OrganizationChartRelationDto;
import com.globits.hr.dto.PositionRoleDto;
import com.globits.hr.dto.PositionStaffDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.function.PositionTitleStaffDto;
import com.globits.hr.dto.search.PositionStaffSearchDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.PositionStaffService;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service

public class PositionStaffServiceImpl extends GenericServiceImpl<PositionStaff, UUID> implements PositionStaffService {
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    PositionRepository positionRepository;

    @Autowired
    HRDepartmentRepository hRDepartmentRepository;
    @Autowired
    PositionStaffRepository positionStaffRepository;

    @Autowired
    OrganizationChartRepository organizationChartRepository;

    @Override
    public PositionStaffDto saveImportStaffEducationHistory(PositionTitleStaffDto dto) {
        if (dto != null) {
            Staff entity = null;
            PositionStaff positionStaff = new PositionStaff();
            PositionTitle positionTitle = null;
            if (dto.getStaffCode() != null) {
                List<Staff> listStaff = staffRepository.getByCode(dto.getStaffCode());
                if (listStaff != null && listStaff.size() > 0) {
                    entity = listStaff.get(0);
                    positionStaff.setStaff(entity);
                }
            }
            if (entity == null) {
                return null;
            }
//			if(dto.getPositionTitleCode() != null) {
//				List<PositionTitle> listPotitionTitle = positionTitleRepository.findByCode(dto.getPositionTitleCode());
//				if(listPotitionTitle != null && listPotitionTitle.size() >0) {
//					positionTitle = listPotitionTitle.get(0);
//					positionStaff.setPosition(positionTitle);
//				}
//			}
            if (positionTitle == null) {
                return null;
            }
            if (dto.getFromDate() != null) {
                positionStaff.setFromDate(dto.getFromDate());
            }
            if (dto.getToDate() != null) {
                positionStaff.setToDate(dto.getToDate());
            }
            if (dto.getDepartmentCode() != null) {
                List<HRDepartment> depart = hRDepartmentRepository.findByCode(dto.getDepartmentCode());
                if (depart != null && depart.size() > 0) {
                    positionStaff.setHrDepartment(depart.get(0));
                }
            }

            positionStaff = positionStaffRepository.save(positionStaff);
            return new PositionStaffDto(positionStaff);
        }
        return null;
    }


    @Override
    public List<PositionStaff> findPositionStaffByRelation(OrganizationChartRelationDto dto) {
        if (dto == null && dto.getId() == null) {
            return null;
        }
        OrganizationChart source = organizationChartRepository.findById(dto.getSourceOrgId()).orElse(null);
        OrganizationChart target = organizationChartRepository.findById(dto.getTargetOrgId()).orElse(null);

        if (source == null || target == null) {
            return null;
        }

        if (source.getOrgType() == 1 && target.getOrgType() == 1) {
            return null;
        }

        UUID staffId = null;
        UUID supervisorId = null;
        UUID departmentId = null;
        Integer relationType = null;

        if (source.getOrgType() == 0 && target.getOrgType() == 0) {
            supervisorId = source.getObjectId();
            staffId = target.getObjectId();
            if (dto.getRelationType() == 0) {
                relationType = HrConstants.RelationshipType.UNDER_DIRECT_MANAGEMENT.getValue(); // chịu sự quản lý trực tiếp của phòng ban hoặc nhân viên
            } else if (dto.getRelationType() == 1) {
                relationType = HrConstants.RelationshipType.UNDER_INDIRECT_MANAGEMENT.getValue(); // chịu sự quản lý gián tiếp của phòng ban hoặc nhân viên
            }
        }
        if (source.getOrgType() == 0 && target.getOrgType() == 1) {
            staffId = source.getObjectId();
            departmentId = target.getObjectId();
        }
        if (source.getOrgType() == 1 && target.getOrgType() == 0) {
            staffId = target.getObjectId();
            departmentId = source.getObjectId();
        }

        List<PositionStaff> positionStaffList = positionStaffRepository.findByRelation(staffId, supervisorId, departmentId);
        return positionStaffList;
    }

    @Override
    public PositionStaffDto getPositionStaff(UUID id) {
        if (id == null) {
            return null;
        }
        PositionStaff positionStaff = positionStaffRepository.findById(id).orElse(null);
        if (positionStaff != null) {
            return new PositionStaffDto(positionStaff);
        }
        return null;
    }

    @Override
    public PositionStaffDto saveOrUpdate(PositionStaffDto dto) {
        if (dto == null) {
            return null;
        }

        if (dto.getStaff() == null || dto.getStaff().getId() == null) {
            return null;
        }

        PositionStaff positionStaff = null;
        if (dto.getId() != null) {
            positionStaff = positionStaffRepository.findById(dto.getId()).orElse(null);
        }
        if (positionStaff == null) {
            positionStaff = new PositionStaff();
        }
        positionStaff.setFromDate(dto.getFromDate());
        positionStaff.setToDate(dto.getToDate());
        positionStaff.setMainPosition(dto.getMainPosition());
        positionStaff.setRelationshipType(dto.getRelationshipType());

        if (dto.getPosition() != null && dto.getPosition().getId() != null) {
            Position position = positionRepository.findById(dto.getPosition().getId()).orElse(null);
            positionStaff.setPosition(position);
        }

        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            Staff staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
            if (staff == null) {
                return null;
            }
            positionStaff.setStaff(staff);
        }

        if (dto.getHrDepartment() != null && dto.getHrDepartment().getId() != null) {
            HRDepartment department = hRDepartmentRepository.findById(dto.getHrDepartment().getId()).orElse(null);
            positionStaff.setHrDepartment(department);
        }

        if (dto.getSupervisor() != null && dto.getSupervisor().getId() != null) {
            Staff supervisor = staffRepository.findById(dto.getSupervisor().getId()).orElse(null);
            positionStaff.setSupervisor(supervisor);
        }

        positionStaff = positionStaffRepository.save(positionStaff);

        return new PositionStaffDto(positionStaff);
    }

    @Override
    public Boolean deletePositionStaff(UUID id) {
        if (id == null) return false;
        PositionStaff positionStaff = positionStaffRepository.findById(id).orElse(null);
        if (positionStaff == null) return false;
        positionStaffRepository.delete(positionStaff);
        return true;
    }

    @Override
    public Page<PositionStaffDto> paging(PositionStaffSearchDto dto) {
        if (dto == null) {
            return Page.empty();
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

        String sqlCount = "select count(distinct entity.id) from PositionStaff as entity ";
        String sql = "select distinct new com.globits.hr.dto.PositionStaffDto(entity) from PositionStaff as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.staff.displayName LIKE :text or  entity.supervisor.displayName LIKE :text or  entity.hrDepartment.name LIKE :text  or  entity.position.name LIKE :text ) ";
        }

        if (dto.getStaffId() != null) {
            whereClause += " AND ( entity.staff.id = :staffId) ";
        }
        if (dto.getPositionId() != null) {
            whereClause += " AND ( entity.position.id = :positionId) ";
        }
        if (dto.getDepartmentId() != null) {
            whereClause += " AND ( entity.hrDepartment.id = :departmentId) ";
        }
        if (dto.getSupervisorId() != null) {
            whereClause += " AND ( entity.supervisor.id = :supervisorId) ";
        }
        if (dto.getRelationshipType() != null) {
            whereClause += " AND ( entity.relationshipType = :relationshipType) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, PositionStaffDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        if (dto.getStaffId() != null) {
            query.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }
        if (dto.getPositionId() != null) {
            query.setParameter("positionId", dto.getPositionId());
            qCount.setParameter("positionId", dto.getPositionId());
        }
        if (dto.getDepartmentId() != null) {
            query.setParameter("departmentId", dto.getDepartmentId());
            qCount.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getSupervisorId() != null) {
            query.setParameter("supervisorId", dto.getSupervisorId());
            qCount.setParameter("supervisorId", dto.getSupervisorId());
        }
        if (dto.getRelationshipType() != null) {

            query.setParameter("relationshipType", dto.getRelationshipType());
            qCount.setParameter("relationshipType", dto.getRelationshipType());
        }
        long count = (long) qCount.getSingleResult();

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        List<PositionStaffDto> entities = query.getResultList();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public List<StaffDto> getListStaffUnderManager(Long userId) {
        return positionRepository.getListStaffUnderManager(userId);
    }
}
