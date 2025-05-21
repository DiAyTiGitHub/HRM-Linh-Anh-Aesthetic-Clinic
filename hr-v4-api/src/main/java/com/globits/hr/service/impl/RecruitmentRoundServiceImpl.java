package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchRecruitmentDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.CandidateRecruitmentRoundService;
import com.globits.hr.service.EvaluationCandidateRoundService;
import com.globits.hr.service.RecruitmentRoundService;
import com.globits.template.domain.ContentTemplate;
import com.globits.template.repository.ContentTemplateRepository;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class RecruitmentRoundServiceImpl extends GenericServiceImpl<RecruitmentRound, UUID> implements RecruitmentRoundService {
    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private RecruitmentRoundRepository recruitmentRoundRepository;

    @Autowired
    private RecruitmentPlanRepository recruitmentPlanRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private EvaluationTemplateRepository evaluationTemplateRepository;

    @Autowired
    private ContentTemplateRepository contentTemplateRepository;

    @Autowired
    private ContentTemplateExtRepository contentTemplateExtRepository;

    @Autowired
    private CandidateRecruitmentRoundService candidateRecruitmentRoundService;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private EvaluationCandidateRoundService evaluationCandidateRoundService;

    @Override
    public RecruitmentRound getNextRecruitmentRound(UUID currentRoundId) {
        if (currentRoundId == null)
            return null;

        RecruitmentRound currentRoundEntity = recruitmentRoundRepository.findById(currentRoundId).orElse(null);
        if (currentRoundEntity == null || currentRoundEntity.getRecruitment() == null) return null;

        List<RecruitmentRound> allRounds = new ArrayList<>();
        allRounds.addAll(currentRoundEntity.getRecruitment().getRecruitmentRounds());

        Collections.sort(allRounds, new Comparator<RecruitmentRound>() {
            @Override
            public int compare(RecruitmentRound o1, RecruitmentRound o2) {
                if (o1.getRoundOrder() == null && o2.getRoundOrder() == null) {
                    return 0;
                }
                if (o1.getRoundOrder() == null) {
                    return -1;
                }
                if (o2.getRoundOrder() == null) {
                    return 1;
                }
                return o1.getRoundOrder().compareTo(o2.getRoundOrder());
            }
        });

        // Find the current round and return the next one
        boolean foundCurrent = false;
        for (RecruitmentRound round : allRounds) {
            if (foundCurrent) {
                return round; // The next round after the current one
            }
            if (round.getId().equals(currentRoundId)) {
                foundCurrent = true;
            }
        }

        // If no next round is found, return null
        return null;
    }

    @Override
    public RecruitmentRound getFirstRecruitmentRoundOfRecruitment(UUID recruitmentId) {
        if (recruitmentId == null) return null;

        Recruitment recruitment = recruitmentRepository.findById(recruitmentId).orElse(null);
        if (recruitment == null || recruitment.getRecruitmentRounds() == null || recruitment.getRecruitmentRounds().size() == 0)
            return null;

        List<RecruitmentRound> recruitmentRounds = new ArrayList<>(recruitment.getRecruitmentRounds());
        Collections.sort(recruitmentRounds, new Comparator<RecruitmentRound>() {
            @Override
            public int compare(RecruitmentRound o1, RecruitmentRound o2) {
                if (o1.getRoundOrder() == null && o2.getRoundOrder() == null) {
                    return 0;
                }
                if (o1.getRoundOrder() == null) {
                    return -1;
                }
                if (o2.getRoundOrder() == null) {
                    return 1;
                }
                return o1.getRoundOrder().compareTo(o2.getRoundOrder());
            }
        });

        return recruitmentRounds.get(0);
    }

    @Override
    public void deleteByRecruitmentPlanId(UUID recruitmentPlanId) {
        if (recruitmentPlanId == null) return;
        recruitmentRoundRepository.deleteByRecruitmentPlanId(recruitmentPlanId);
    }

    @Override
    public RecruitmentRoundDto saveOrUpdate(RecruitmentRoundDto dto) {
        if (dto != null) {
            RecruitmentRound entity = null;
            if (dto.getId() != null) {
                entity = recruitmentRoundRepository.findById(dto.getId()).orElse(null);
            }
            if (entity == null) {
                entity = new RecruitmentRound();
            }
            if (dto.getRecruitmentPlainId() != null) {
                recruitmentPlanRepository.findById(dto.getRecruitmentPlainId()).ifPresent(entity::setRecruitmentPlan);
            } else {
                entity.setRecruitmentPlan(null);
            }
            EvaluationTemplate template = null;
            if (dto.getEvaluationTemplate() != null && dto.getEvaluationTemplate().getId() != null) {
                template = evaluationTemplateRepository.findById(dto.getEvaluationTemplate().getId()).orElse(null);
            }
            entity.setEvaluationTemplate(template);
            entity.setRoundOrder(dto.getRoundOrder());
            entity.setName(dto.getName());
            entity.setTakePlaceDate(dto.getTakePlaceDate());
            if (dto.getInterviewLocation() != null && dto.getInterviewLocation().getId() != null) {
                entity.setInterviewLocation(workplaceRepository.findById(dto.getInterviewLocation().getId()).orElse(null));
            } else {
                entity.setInterviewLocation(null);
            }
            entity.setDescription(dto.getDescription());
            entity.setRecruitmentType(dto.getRecruitmentType());
            if (dto.getPassTemplate() != null && dto.getPassTemplate().getId() != null) {
                entity.setPassTemplate(contentTemplateRepository.findById(dto.getPassTemplate().getId()).orElse(null));
            }
            if (dto.getFailTemplate() != null && dto.getFailTemplate().getId() != null) {
                entity.setFailTemplate(contentTemplateRepository.findById(dto.getFailTemplate().getId()).orElse(null));
            }
            if (!CollectionUtils.isEmpty(dto.getParticipatingPeople())) {
                if (!CollectionUtils.isEmpty(entity.getParticipatingPeople())) {
                    entity.getParticipatingPeople().clear();
                }
                for (StaffDto staffDto : dto.getParticipatingPeople()) {
                    Staff staff = staffRepository.findById(staffDto.getId()).orElse(null);
                    if (staff != null) {
                        entity.getParticipatingPeople().add(staff);
                    }
                    if (staffDto.getJudgePerson() != null) {
                        if (staffDto.getJudgePerson()) {
                            entity.setJudgePerson(staff);
                        }
                    }
                }
            }

            if (!CollectionUtils.isEmpty(dto.getCandidates())) {
                Set<CandidateRecruitmentRound> updatedRounds = new HashSet<>();
                for (CandidateRecruitmentRoundDto dtoItem : dto.getCandidates()) {
                    dtoItem.setRecruitmentRound(new RecruitmentRoundDto(entity));
                    CandidateRecruitmentRound round = candidateRecruitmentRoundService.convertDtoToEntity(dtoItem);
                    round.setRecruitmentRound(entity);
                    updatedRounds.add(round);
                }
                if (!CollectionUtils.isEmpty(entity.getCandidateRecruitmentRounds())) {
                    for (CandidateRecruitmentRound round : entity.getCandidateRecruitmentRounds()) {
                        if (!CollectionUtils.isEmpty(round.getEvaluationTicket())) {
                            round.getEvaluationTicket().clear();
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(dto.getCandidates())) {
                    for (CandidateRecruitmentRoundDto round : dto.getCandidates()) {
                        for (EvaluationCandidateRoundDto roundDto : round.getEvaluationCandidateRoundDtos()) {
                            evaluationCandidateRoundService.saveOrUpdate(roundDto);
                        }
                    }
                }
                entity.getCandidateRecruitmentRounds().clear();
                entity.getCandidateRecruitmentRounds().addAll(updatedRounds);
            } else {
                if (!CollectionUtils.isEmpty(entity.getCandidateRecruitmentRounds())) {
                    entity.getCandidateRecruitmentRounds().clear();
                }
            }

            recruitmentRoundRepository.save(entity);
            return new RecruitmentRoundDto(entity);
        }
        return null;
    }

    @Override
    public List<RecruitmentRoundDto> getListByRecruitmentPlanId(UUID recruitmentPlanId) {
        return recruitmentRoundRepository.getListRecruitmentRoundByPlanId(recruitmentPlanId);
    }

    @Override
    public Page<RecruitmentRoundDto> pagingRecruitmentRound(SearchRecruitmentDto dto) {
        if (dto == null) {
            return null;
        }

        int pageIndex = dto.getPageIndex() > 0 ? dto.getPageIndex() - 1 : 0;
        int pageSize = dto.getPageSize();

        String whereClause = " WHERE 1=1 ";
        String orderBy = " ORDER BY entity.roundOrder ASC ";
        String sqlCount = "SELECT count(entity.id) FROM RecruitmentRound AS entity ";
        String sql = "SELECT new com.globits.hr.dto.RecruitmentRoundDto(entity) FROM RecruitmentRound AS entity ";

        if (StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.name LIKE :text) ";
        }

        if (dto.getRecruitmentPlanId() != null) {
            whereClause += " AND entity.recruitmentPlan.id = :recruitmentPlanId ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, RecruitmentRoundDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", "%" + dto.getKeyword().trim() + "%");
            qCount.setParameter("text", "%" + dto.getKeyword().trim() + "%");
        }

        if (dto.getRecruitmentPlanId() != null) {
            q.setParameter("recruitmentPlanId", dto.getRecruitmentPlanId());
            qCount.setParameter("recruitmentPlanId", dto.getRecruitmentPlanId());
        }

        q.setFirstResult(pageIndex * pageSize);
        q.setMaxResults(pageSize);

        List<RecruitmentRoundDto> results = q.getResultList();
        long total = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public RecruitmentRound getNextRound(UUID planId, Integer roundOrder) {
        if (planId == null || roundOrder == null) return null;
        return recruitmentRoundRepository.findByRecruitmentPlanAndRoundOrder(planId, roundOrder);
    }

    @Override
    public Integer saveListRecruitmentRound(List<RecruitmentRoundDto> list) {
        if (list == null || list.isEmpty()) return null;
        List<RecruitmentRound> entities = new ArrayList<>();
        for (RecruitmentRoundDto recruitmentRoundDto : list) {
            RecruitmentRound entity = null;

            RecruitmentPlan recruitmentPlan = null;
            if (recruitmentRoundDto.getRecruitmentPlan() != null && recruitmentRoundDto.getRecruitmentPlan().getCode() != null) {
                List<RecruitmentPlan> recruitmentPlanList = recruitmentPlanRepository.findByCode(recruitmentRoundDto.getRecruitmentPlan().getCode());
                if (recruitmentPlanList != null) {
                    recruitmentPlan = recruitmentPlanList.get(0);
                }
            }
            if (recruitmentPlan == null) continue;

            if (recruitmentRoundDto.getRoundOrder() != null) {
                entity = recruitmentRoundRepository.findByRecruitmentPlanAndRoundOrder(recruitmentPlan.getId(), recruitmentRoundDto.getRoundOrder());
            }
            if (entity == null) {
                entity = new RecruitmentRound();
            }
            entity.setRecruitmentPlan(recruitmentPlan);
            entity.setRoundOrder(recruitmentRoundDto.getRoundOrder());
            entity.setName(recruitmentRoundDto.getName());
            entity.setTakePlaceDate(recruitmentRoundDto.getTakePlaceDate());
            if (recruitmentRoundDto.getInterviewLocation() != null) {
                entity.setInterviewLocation(workplaceRepository.findById(recruitmentRoundDto.getInterviewLocation().getId()).orElse(null));
            } else {
                entity.setInterviewLocation(null);
            }
            entity.setRecruitmentType(recruitmentRoundDto.getRecruitmentType());

            if (recruitmentRoundDto.getEvaluationTemplate() != null && recruitmentRoundDto.getEvaluationTemplate().getCode() != null) {
                List<EvaluationTemplate> evaluationTemplateList = evaluationTemplateRepository.findByCode(recruitmentRoundDto.getEvaluationTemplate().getCode());
                if (evaluationTemplateList != null && !evaluationTemplateList.isEmpty()) {
                    entity.setEvaluationTemplate(evaluationTemplateList.get(0));
                }
            }

            if (recruitmentRoundDto.getPassTemplate() != null && recruitmentRoundDto.getPassTemplate().getCode() != null) {
                List<ContentTemplate> contentTemplateList = contentTemplateExtRepository.findByCode(recruitmentRoundDto.getPassTemplate().getCode());
                if (contentTemplateList != null && !contentTemplateList.isEmpty()) {
                    entity.setPassTemplate(contentTemplateList.get(0));
                }
            }

            if (recruitmentRoundDto.getFailTemplate() != null && recruitmentRoundDto.getFailTemplate().getCode() != null) {
                List<ContentTemplate> contentTemplateList = contentTemplateExtRepository.findByCode(recruitmentRoundDto.getFailTemplate().getCode());
                if (contentTemplateList != null && !contentTemplateList.isEmpty()) {
                    entity.setFailTemplate(contentTemplateList.get(0));
                }
            }
            entities.add(entity);
        }

        entities = recruitmentRoundRepository.saveAll(entities);
        return entities.size();
    }
}
