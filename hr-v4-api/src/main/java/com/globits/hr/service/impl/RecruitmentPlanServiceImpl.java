package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchCandidateDto;
import com.globits.hr.dto.search.SearchRecruitmentDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.*;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.hr.utils.RoleUtils;
import com.globits.security.dto.UserDto;
import com.globits.security.service.RoleService;
import com.globits.template.repository.ContentTemplateRepository;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecruitmentPlanServiceImpl extends GenericServiceImpl<RecruitmentPlan, UUID> implements RecruitmentPlanService {
    @Autowired
    private RecruitmentRequestRepository recruitmentRequestRepository;

    @Autowired
    private HrOrganizationRepository hrOrganizationRepository;
    @Autowired
    private HRDepartmentRepository hrDepartmentRepository;
    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private RecruitmentPlanRepository recruitmentPlanRepository;
    @Autowired
    private PositionTitleRepository positionTitleRepository;
    @Autowired
    private RecruitmentPlanItemRepository recruitmentPlanItemRepository;
    @Autowired
    private RecruitmentRoundService recruitmentRoundService;
    @Autowired
    private RecruitmentRoundRepository recruitmentRoundRepository;

    @Autowired
    private ContentTemplateRepository contentTemplateRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private HrRoleService hrRoleService;
    @Autowired
    private UserExtService userExtService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public RecruitmentPlanDto getById(UUID id) {
        RecruitmentPlan entity = recruitmentPlanRepository.findById(id).orElse(null);
        if (entity == null)
            return null;
        List<RecruitmentRoundDto> items = recruitmentRoundService.getListByRecruitmentPlanId(entity.getId());
        return new RecruitmentPlanDto(entity, items);
    }

    @Override
    public Boolean isValidCode(RecruitmentPlanDto dto) {
        if (dto == null)
            return false;

        // ID of RecruitmentPlan is null => Create new RecruitmentPlan
        // => Assure that there's no other RecruitmentPlans using this code of new RecruitmentPlan
        // if there was any RecruitmentPlan using new RecruitmentPlan code, then this new code is
        // invalid => return False
        // else return true
        if (dto.getId() == null) {
            List<RecruitmentPlan> entities = recruitmentPlanRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        }
        // ID of RecruitmentPlan is NOT null => RecruitmentPlan is modified
        // => Assure that the modified code is not same to OTHER any RecruitmentPlan's code
        // if there was any RecruitmentPlan using new RecruitmentPlan code, then this new code is
        // invalid => return False
        // else return true
        else {
            List<RecruitmentPlan> entities = recruitmentPlanRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (RecruitmentPlan entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public Integer saveListRecruitmentPlan(List<RecruitmentPlanDto> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<RecruitmentPlan> recruitmentPlans = new ArrayList<>();
        for (RecruitmentPlanDto dto : list) {
            RecruitmentPlan entity = null;
            if (dto.getCode() != null) {
                List<RecruitmentPlan> recruitmentPlanList = recruitmentPlanRepository.findByCode(dto.getCode());
                if (recruitmentPlanList != null && !recruitmentPlanList.isEmpty()) {
                    entity = recruitmentPlanList.get(0);
                }
            }
            if (entity == null) {
                entity = new RecruitmentPlan();
                entity.setCode(dto.getCode());
            }
            entity.setName(dto.getName());

            if (dto.getRecruitmentRequest() != null && dto.getRecruitmentRequest().getCode() != null) {
                List<RecruitmentRequest> recruitmentRequestList = recruitmentRequestRepository.findByCode(dto.getRecruitmentRequest().getCode());
                if (recruitmentRequestList != null && !recruitmentRequestList.isEmpty()) {
                    entity.setRecruitmentRequest(recruitmentRequestList.get(0));
                } else {
                    entity.setRecruitmentRequest(null);
                }
            } else {
                entity.setRecruitmentRequest(null);
            }

            entity.setStatus(dto.getStatus());
            entity.setEstimatedTimeFrom(dto.getEstimatedTimeFrom());
            entity.setEstimatedTimeTo(dto.getEstimatedTimeTo());
            entity.setDescription(dto.getDescription());
            entity.setVoided(false);
            recruitmentPlans.add(entity);

        }
        recruitmentPlans = recruitmentPlanRepository.saveAll(recruitmentPlans);
        return recruitmentPlans.size();
    }

    @Override
    public String autoGenerateCode(String configKey) {
        SystemConfig systemConfig = systemConfigService.getConfigByConfigValue(configKey);
        if (systemConfig == null) {
            return "";
        } else {
            String maxCode = recruitmentPlanRepository.findMaxCodeByPrefix(systemConfig.getConfigValue(), systemConfig.getNumberOfZero());
            return systemConfigService.generateNextCode(systemConfig.getConfigValue(), systemConfig.getNumberOfZero(), maxCode);
        }
    }

    @Override
    @Modifying
    @Transactional
    public RecruitmentPlanDto saveRecruitmentPlan(RecruitmentPlanDto dto) {
        if (dto == null) {
            return null;
        }

        RecruitmentPlan entity = new RecruitmentPlan();
        if (dto.getId() != null) entity = recruitmentPlanRepository.findById(dto.getId()).orElse(null);
        if (entity == null) {
            entity = new RecruitmentPlan();
        }

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());

        if (dto.getRecruitmentRequest() != null && dto.getRecruitmentRequest().getId() != null) {
            RecruitmentRequest recruitmentRequest = recruitmentRequestRepository.findById(dto.getRecruitmentRequest().getId()).orElse(null);
            if (recruitmentRequest == null) return null;
            entity.setRecruitmentRequest(recruitmentRequest);
        } else {
            entity.setRecruitmentRequest(null);
        }
        entity.setStatus(dto.getStatus());
        entity.setEstimatedTimeFrom(dto.getEstimatedTimeFrom());
        entity.setEstimatedTimeTo(dto.getEstimatedTimeTo());
        entity = recruitmentPlanRepository.saveAndFlush(entity);

        // === Handle RecruitmentRounds ===
        List<RecruitmentRoundDto> roundDtos = dto.getRecruitmentRounds();
        List<UUID> dtoRoundIds = roundDtos != null
                ? roundDtos.stream().filter(r -> r.getId() != null).map(RecruitmentRoundDto::getId).collect(Collectors.toList())
                : List.of();

        // Xóa những round không có trong DTO
        List<RecruitmentRoundDto> existingRounds = recruitmentRoundRepository.getListRecruitmentRoundByPlanId(entity.getId());
        for (RecruitmentRoundDto existingRound : existingRounds) {
            if (!dtoRoundIds.contains(existingRound.getId())) {
                recruitmentRoundRepository.deleteById(existingRound.getId());
            }
        }

        // Lưu lại hoặc thêm mới các round
        if (!CollectionUtils.isEmpty(roundDtos)) {
            for (RecruitmentRoundDto roundDto : roundDtos) {
                if (roundDto == null) continue;
                roundDto.setRecruitmentPlainId(entity.getId());
                recruitmentRoundService.saveOrUpdate(roundDto);
            }
        }
        return new RecruitmentPlanDto(entity);
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteRecruitmentPlan(UUID id) {
        if (id == null) return false;

        RecruitmentPlan entity = recruitmentPlanRepository.findById(id).orElse(null);
        if (entity == null) return false;
        entity.setVoided(true);

        recruitmentPlanRepository.save(entity);
        return true;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteMultipleRecruitmentPlan(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID id : ids) {
            boolean deleteRes = this.deleteRecruitmentPlan(id);
            if (!deleteRes) isValid = false;
        }
        return isValid;
    }

    private void formalizeSearchObject(SearchRecruitmentDto dto) {
        if (dto.getFromDate() != null) {
            dto.setFromDate(DateTimeUtil.getStartOfDay(dto.getFromDate()));
        }
        if (dto.getToDate() != null) {
            dto.setToDate(DateTimeUtil.getEndOfDay(dto.getToDate()));
        }
    }

    @Override
    public Page<RecruitmentPlanDto> pagingRecruitmentPlan(SearchRecruitmentDto dto) {
        if (dto == null) {
            return null;
        }
        formalizeSearchObject(dto);

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        UserDto userDto = userExtService.getCurrentUser();
        StaffDto staffDto = userExtService.getCurrentStaff();
        boolean isAdmin = RoleUtils.hasRole(userDto, HrConstants.ROLE_ADMIN);
        boolean isManager = RoleUtils.hasRole(userDto, HrConstants.HR_MANAGER);
        boolean isSuperHR = RoleUtils.hasRole(userDto, HrConstants.SUPER_HR);
        UUID staffId = null;
        if (!isAdmin && !isManager && !isSuperHR) {
            staffId = staffDto != null ? staffDto.getId() : null;
        }

        String sqlCount = "select count(entity.id) from RecruitmentPlan as entity ";
        String sql = "select new com.globits.hr.dto.RecruitmentPlanDto(entity) from RecruitmentPlan as entity ";

        String whereClause = " where (1=1) and (entity.voided = false or entity.voided is null)  ";
        String orderBy = " ORDER BY entity.createDate desc ";

        if(staffId != null) {
            whereClause += " AND (" +
                    // người phụ trách
                    "(entity.recruitmentRequest.personInCharge.id = :staffId) " +
                    // người duyệt cv
                    "OR entity.personApproveCV.id = :staffId " +
                    // người tham gia vòng phỏng vấn thuộc kế hoạch
                    "OR EXISTS (SELECT 1 FROM RecruitmentRound round JOIN round.participatingPeople people WHERE round.recruitmentPlan.id = entity.id AND people.id = :staffId)" +
                    ") ";
        }
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.code LIKE :text OR entity.name LIKE :text ) ";
        }
        if (dto.getStatus() != null) {
            whereClause += " and entity.status = :status ";
        }
        if (dto.getRecruitmentRequestId() != null) {
            whereClause += " and entity.recruitmentRequest.id = :recruitmentRequestId ";
        }
        if (dto.getOrganizationId() != null) {
            whereClause += " and entity.recruitmentRequest.hrOrganization.id = :organizationId ";
        }
        if (dto.getDepartmentId() != null) {
            whereClause += " and entity.recruitmentRequest.hrDepartment.id = :departmentId ";
        }
        if (dto.getPositionTitleId() != null) {
            whereClause += " and EXISTS (SELECT 1 FROM RecruitmentRequestItem item WHERE item.recruitmentRequest.id = entity.recruitmentRequest.id AND item.positionTitle.id = :positionTitleId) ";
        }
        if (dto.getFromDate() != null) {
            whereClause += " and entity.estimatedTimeFrom >= :fromDate ";
        }
        if (dto.getToDate() != null) {
            whereClause += " and entity.estimatedTimeTo <= :toDate ";
        }
        if (dto.getPersonInCharge() != null && dto.getPersonInCharge()) {
            whereClause += " AND entity.recruitmentRequest.personInCharge IS NOT NULL ";
        }
        if(!CollectionUtils.isEmpty(dto.getRecruitmentRequestStatus())){
            whereClause += " AND entity.recruitmentRequest.status IN (:recruitmentRequestStatus) ";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, RecruitmentPlanDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getStatus() != null) {
            query.setParameter("status", dto.getStatus());
            qCount.setParameter("status", dto.getStatus());
        }
        if (dto.getRecruitmentRequestId() != null) {
            query.setParameter("recruitmentRequestId", dto.getRecruitmentRequestId());
            qCount.setParameter("recruitmentRequestId", dto.getRecruitmentRequestId());
        }
        if (dto.getOrganizationId() != null) {
            query.setParameter("organizationId", dto.getOrganizationId());
            qCount.setParameter("organizationId", dto.getOrganizationId());
        }
        if (dto.getDepartmentId() != null) {
            query.setParameter("departmentId", dto.getDepartmentId());
            qCount.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getPositionTitleId() != null) {
            query.setParameter("positionTitleId", dto.getPositionTitleId());
            qCount.setParameter("positionTitleId", dto.getPositionId());
        }
        if (dto.getFromDate() != null) {
            query.setParameter("fromDate", dto.getFromDate());
            qCount.setParameter("fromDate", dto.getFromDate());
        }
        if (dto.getToDate() != null) {
            query.setParameter("toDate", dto.getToDate());
            qCount.setParameter("toDate", dto.getToDate());
        }
        if(staffId != null) {
            query.setParameter("staffId", staffId);
            qCount.setParameter("staffId", staffId);
        }
        if(!CollectionUtils.isEmpty(dto.getRecruitmentRequestStatus())){
            List<Integer> recruitmentRequestStatus = dto.getRecruitmentRequestStatus().stream().map(HrConstants.RecruitmentRequestStatus::getValue).toList();
            query.setParameter("recruitmentRequestStatus", recruitmentRequestStatus);
            qCount.setParameter("recruitmentRequestStatus", recruitmentRequestStatus);
        }
        List<RecruitmentPlanDto> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();
        Page<RecruitmentPlanDto> result;

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        entities = query.getResultList();
        result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    @Transactional
    @Modifying
    public List<UUID> updatePlansStatus(SearchRecruitmentDto dto) {
        if (dto == null) return null;
        if (dto.getStatus() == null || dto.getChosenIds() == null || dto.getChosenIds().size() == 0)
            return null;

        //save all need update plans at the same time instead of save each item
        List<RecruitmentPlan> onSavePlans = new ArrayList<>();
        for (UUID planId : dto.getChosenIds()) {
            if (planId == null) return null;

            RecruitmentPlan plan = recruitmentPlanRepository.findById(planId).orElse(null);
            if (plan == null) return null;
            plan.setStatus(dto.getStatus());

            onSavePlans.add(plan);
        }

        if (onSavePlans == null || onSavePlans.size() == 0) return null;
        List<RecruitmentPlan> savedPlans = recruitmentPlanRepository.saveAll(onSavePlans);
        if (savedPlans == null) return null;

        //return ids of updated request
        List<UUID> updatedPlanIds = new ArrayList<>();
        for (RecruitmentPlan plan : savedPlans) {
            updatedPlanIds.add(plan.getId());
        }

        return updatedPlanIds;
    }
}
