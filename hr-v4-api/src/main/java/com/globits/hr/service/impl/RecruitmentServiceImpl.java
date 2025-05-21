package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchCandidateDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchRecruitmentDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.RecruitmentPlanService;
import com.globits.hr.service.RecruitmentService;
import com.globits.hr.utils.DateTimeUtil;
import jakarta.persistence.Query;
import org.checkerframework.checker.units.qual.A;
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
public class RecruitmentServiceImpl extends GenericServiceImpl<Recruitment, UUID> implements RecruitmentService {
    @Autowired
    private PositionTitleRepository positionTitleRepository;

    @Autowired
    private RecruitmentPlanRepository recruitmentPlanRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private HrOrganizationRepository hrOrganizationRepository;

    @Autowired
    private HRDepartmentRepository hrDepartmentRepository;

    @Autowired
    private RecruitmentRoundRepository recruitmentRoundRepository;

    @Autowired
    private RecruitmentExamTypeRepository recruitmentExamTypeRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private RecruitmentItemRepository recruitmentItemRepository;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Override
    public RecruitmentDto getById(UUID id) {
        Recruitment entity = recruitmentRepository.findById(id).orElse(null);
        if (entity == null)
            return null;
        RecruitmentDto response = new RecruitmentDto(entity, true);
        return response;
    }

    @Override
    public Boolean isValidCode(RecruitmentDto dto) {
        if (dto == null)
            return false;

        // ID of Recruitment is null => Create new Recruitment
        // => Assure that there's no other Recruitment using this code of new Recruitment
        // if there was any Recruitment using new Recruitment code, then this new code is
        // invalid => return False
        // else return true
        if (dto.getId() == null) {
            List<Recruitment> entities = recruitmentRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        }
        // ID of Recruitment is NOT null => Recruitment is modified
        // => Assure that the modified code is not same to OTHER any Recruitment's code
        // if there was any Recruitment using new Recruitment code, then this new code is
        // invalid => return False
        // else return true
        else {
            List<Recruitment> entities = recruitmentRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (Recruitment entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }

    @Override
    @Modifying
    @Transactional
    public RecruitmentDto saveRecruitment(RecruitmentDto dto) {
        if (dto == null) {
            return null;
        }

        Recruitment entity = new Recruitment();
        if (dto.getId() != null) entity = recruitmentRepository.findById(dto.getId()).orElse(null);
        if (entity == null) {
            entity = new Recruitment();
        }

        //thong tin tuyen dung
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
//        entity.setQuantity(dto.getQuantity());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setNote(dto.getNote());

        if (dto.getRecruitmentPlan() != null && dto.getRecruitmentPlan().getId() != null) {
            RecruitmentPlan recruitmentPlan = recruitmentPlanRepository.findById(dto.getRecruitmentPlan().getId()).orElse(null);
            if (recruitmentPlan == null) return null;
            entity.setRecruitmentPlan(recruitmentPlan);
        } else {
            entity.setRecruitmentPlan(null);
        }

        if (dto.getOrganization() != null && dto.getOrganization().getId() != null) {
            HrOrganization organization = hrOrganizationRepository.findById(dto.getOrganization().getId()).orElse(null);
            if (organization == null)
                return null;
            entity.setOrganization(organization);
        } else {
            entity.setOrganization(null);
        }

        if (dto.getDepartment() != null && dto.getDepartment().getId() != null) {
            HRDepartment hrDepartment = hrDepartmentRepository.findById(dto.getDepartment().getId()).orElse(null);
            if (hrDepartment == null)
                return null;
            entity.setDepartment(hrDepartment);
        } else {
            entity.setDepartment(null);
        }

//        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
//            PositionTitle positionTitle = positionTitleRepository.findById(dto.getPositionTitle().getId()).orElse(null);
//            entity.setPositionTitle(positionTitle);
//        } else {
//            entity.setPositionTitle(null);
//        }

        //thong tin lien he
        if (dto.getContactStaff() != null && dto.getContactStaff().getId() != null) {
            Staff contactStaff = staffRepository.findById(dto.getContactStaff().getId()).orElse(null);
            if (contactStaff == null) return null;
            entity.setContactStaff(contactStaff);
        } else {
            entity.setContactStaff(null);
        }

        if (dto.getPositionCS() != null && dto.getPositionCS().getId() != null) {
            Position position = positionRepository.findById(dto.getPositionCS().getId()).orElse(null);
            if (position == null) return null;
            entity.setPositionCS(position);
        } else {
            entity.setPositionCS(null);
        }

        if (dto.getHrDepartmentCS() != null && dto.getHrDepartmentCS().getId() != null) {
            HRDepartment hrDepartmentCS = hrDepartmentRepository.findById(dto.getHrDepartmentCS().getId()).orElse(null);
            if (hrDepartmentCS == null) return null;
            entity.setHrDepartmentCS(hrDepartmentCS);
        } else {
            entity.setHrDepartmentCS(null);
        }

        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setOfficePhoneNumber(dto.getOfficePhoneNumber());
        entity.setContactEmail(dto.getContactEmail());
        entity.setContactWebsite(dto.getContactWebsite());

        //vong tuyen dung
        if (entity.getRecruitmentRounds() == null) entity.setRecruitmentRounds(new HashSet<>());
        if (dto.getRecruitmentRounds() != null && !dto.getRecruitmentRounds().isEmpty()) {
            Set<RecruitmentRound> recruitmentRounds = new HashSet<>();

            for (RecruitmentRoundDto roundDto : dto.getRecruitmentRounds()) {
                RecruitmentRound round = new RecruitmentRound();

                if (roundDto != null && roundDto.getId() != null) {
                    round = recruitmentRoundRepository.findById(roundDto.getId()).orElse(null);
                    if (round == null) return null;
                }
                if (round == null) round = new RecruitmentRound();

                round.setRecruitment(entity);
                round.setName(roundDto.getName());
                round.setRoundOrder(roundDto.getRoundOrder());
                round.setTakePlaceDate(roundDto.getTakePlaceDate());
                if(roundDto.getInterviewLocation() != null){
                    round.setInterviewLocation(workplaceRepository.findById(roundDto.getInterviewLocation().getId()).orElse(null));
                }else {
                    round.setInterviewLocation(null);
                }
                round.setDescription(roundDto.getDescription());

                if (roundDto.getExamType() != null && roundDto.getExamType().getId() != null) {
                    RecruitmentExamType examType = recruitmentExamTypeRepository.findById(roundDto.getExamType().getId()).orElse(null);
                    if (examType == null) return null;
                    round.setExamType(examType);
                } else {
                    round.setExamType(null);
                }

                if (round.getName() == null && round.getExamType() == null && round.getRoundOrder() == null
                        && round.getTakePlaceDate() == null && round.getDescription() == null)
                    continue;
                recruitmentRounds.add(round);
            }

            entity.getRecruitmentRounds().addAll(recruitmentRounds);
        } else {
            entity.getRecruitmentRounds().clear();
        }
        if (entity.getRecruitmentItems() == null) {
            entity.setRecruitmentItems(new HashSet<>());
        }

        if (entity.getRecruitmentItems() != null && !entity.getRecruitmentItems().isEmpty()) {
            entity.getRecruitmentItems().clear();
        }

        if (dto.getRecruitmentItems() != null && !dto.getRecruitmentItems().isEmpty()) {
            for (RecruitmentItemDto item : dto.getRecruitmentItems()) {
                RecruitmentItem recruitmentItem = null;
                if (item.getId() != null) {
                    recruitmentItem = recruitmentItemRepository.findById(item.getId()).orElse(null);
                }
                if (recruitmentItem == null) {
                    recruitmentItem = new RecruitmentItem();
                }
                recruitmentItem.setQuantity(item.getQuantity());
                if (item.getPositionTitle() != null && item.getPositionTitle().getId() != null) {
                    PositionTitle positionT = positionTitleRepository.findById(item.getPositionTitle().getId()).orElse(null);
                    recruitmentItem.setPositionTitle(positionT);
                } else {
                    recruitmentItem.setPositionTitle(null);
                }
                recruitmentItem.setRecruitment(entity);
                entity.getRecruitmentItems().add(recruitmentItem);
            }
        }

        entity = recruitmentRepository.save(entity);

        return new RecruitmentDto(entity);
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteRecruitment(UUID id) {
        if (id == null) return false;

        Recruitment entity = recruitmentRepository.findById(id).orElse(null);
        if (entity == null) return false;
        entity.setVoided(true);

        recruitmentRepository.save(entity);
        return true;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteMultipleRecruitment(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID id : ids) {
            boolean deleteRes = this.deleteRecruitment(id);
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
    public Page<RecruitmentDto> pagingRecruitment(SearchRecruitmentDto dto) {
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

        String whereClause = " where (1=1) and (entity.voided = false or entity.voided is null)  ";
        String orderBy = " ORDER BY entity.createDate desc ";

        String sqlCount = "select count(distinct entity.id) from Recruitment as entity ";
        String sql = "select distinct new com.globits.hr.dto.RecruitmentDto(entity) from Recruitment as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.code LIKE :text OR entity.name LIKE :text ) ";
        }
        if (dto.getRecruitmentRequestId() != null) {
            whereClause += " and entity.recruitmentPlan.recruitmentRequest.id = :recruitmentRequestId ";
        }
        if (dto.getRecruitmentPlanId() != null) {
            whereClause += " and entity.recruitmentPlan.id = :recruitmentPlanId ";
        }
        if (dto.getOrganizationId() != null) {
            whereClause += " and entity.organization.id = :organizationId ";
        }
        if (dto.getDepartmentId() != null) {
            whereClause += " and entity.department.id = :departmentId ";
        }
        if (dto.getPositionTitleId() != null) {
            whereClause += " and entity.positionTitle.id = :positionTitleId ";
        }
        if (dto.getFromDate() != null) {
            whereClause += " and entity.startDate >= :fromDate ";
        }
        if (dto.getToDate() != null) {
            whereClause += " and entity.endDate <= :toDate ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, RecruitmentDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getRecruitmentRequestId() != null) {
            query.setParameter("recruitmentRequestId", dto.getRecruitmentRequestId());
            qCount.setParameter("recruitmentRequestId", dto.getRecruitmentRequestId());
        }
        if (dto.getRecruitmentPlanId() != null) {
            query.setParameter("recruitmentPlanId", dto.getRecruitmentPlanId());
            qCount.setParameter("recruitmentPlanId", dto.getRecruitmentPlanId());
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
            qCount.setParameter("positionTitleId", dto.getPositionTitleId());
        }
        if (dto.getFromDate() != null) {
            query.setParameter("fromDate", dto.getFromDate());
            qCount.setParameter("fromDate", dto.getFromDate());
        }
        if (dto.getToDate() != null) {
            query.setParameter("toDate", dto.getToDate());
            qCount.setParameter("toDate", dto.getToDate());
        }

        long count = (long) qCount.getSingleResult();

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<RecruitmentDto> responseData = query.getResultList();

        // add number of applicants applied for display

        for (RecruitmentDto record : responseData) {
            Long numberOfCandidates = recruitmentRepository.countCandidatesByRecruitmentId(record.getId());

            record.setNumberAppliedCandidates(numberOfCandidates);
        }

        Page<RecruitmentDto> result = new PageImpl<>(responseData, pageable, count);

        return result;
    }
}