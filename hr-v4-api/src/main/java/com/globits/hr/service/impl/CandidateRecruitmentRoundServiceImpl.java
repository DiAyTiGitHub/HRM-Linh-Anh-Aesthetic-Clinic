package com.globits.hr.service.impl;

import com.globits.core.domain.FileDescription;
import com.globits.core.repository.FileDescriptionRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchCandidateDto;
import com.globits.hr.dto.search.SearchCandidateRecruitmentRoundDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.CandidateRecruitmentRoundService;
import com.globits.hr.service.CandidateService;
import com.globits.hr.service.RecruitmentRoundService;
import com.globits.hr.service.UserExtService;
import com.globits.salary.dto.SalaryResultDto;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import jakarta.persistence.Query;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


import java.util.*;

import static com.globits.hr.HrConstants.*;

@Service
public class CandidateRecruitmentRoundServiceImpl extends GenericServiceImpl<CandidateRecruitmentRound, UUID>
        implements CandidateRecruitmentRoundService {
    private static final Logger logger = LoggerFactory.getLogger(CandidateRecruitmentRoundServiceImpl.class);

    @Autowired
    private CandidateRecruitmentRoundRepository candidateRecruitmentRoundRepository;

    @Autowired
    private RecruitmentRoundRepository recruitmentRoundRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private RecruitmentRoundService recruitmentRoundService;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private UserExtService userExtService;

    @Autowired
    private FileDescriptionRepository filedescriptionRepository;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Override
    public Boolean isValid(CandidateRecruitmentRoundDto dto) {
        if (dto == null || dto.getCandidate() == null || dto.getCandidate().getId() == null
                || dto.getRecruitmentRound() == null || dto.getRecruitmentRound().getId() == null)
            return false;

        List<CandidateRecruitmentRound> availableResults = candidateRecruitmentRoundRepository
                .getByCandidateIdAndRecruitmentRoundId(dto.getCandidate().getId(), dto.getRecruitmentRound().getId());

        if (availableResults == null || availableResults.isEmpty()) return true;

        for (CandidateRecruitmentRound item : availableResults) {
            // Nếu đang cập nhật chính bản ghi này thì bỏ qua
            if (dto.getId() != null && dto.getId().equals(item.getId())) {
                continue;
            }
            // Trùng candidate và vòng tuyển => không hợp lệ
            return false;
        }

        return true;
    }

    @Override
    public void handleSetCandidateRecruitmentRoundList(CandidateDto dto, Candidate entity) {
//        Set<CandidateRecruitmentRound> candidateRecruitmentRounds = new HashSet<>();
//        if (dto.getRecruitmentRounds() != null && !dto.getRecruitmentRounds().isEmpty()) {
//            for (CandidateRecruitmentRoundDto candidateRecruitmentRoundDto : dto.getRecruitmentRounds()) {
//                CandidateRecruitmentRound recruitmentRoundEntity = null;
//                if (candidateRecruitmentRoundDto.getId() != null) {
//                    recruitmentRoundEntity = candidateRecruitmentRoundRepository.findById(candidateRecruitmentRoundDto.getId()).orElse(null);
//                }
//
//                if (recruitmentRoundEntity == null) {
//                    recruitmentRoundEntity = new CandidateRecruitmentRound();
//                    recruitmentRoundEntity.setCandidate(entity);
//                }
//
//                if (candidateRecruitmentRoundDto.getRecruitmentRound() != null) {
//                    RecruitmentRound recruitmentRound = recruitmentRoundRepository.findById(candidateRecruitmentRoundDto.getRecruitmentRound().getId()).orElse(null);
//                    recruitmentRoundEntity.setRecruitmentRound(recruitmentRound);
//                }
//
//                recruitmentRoundEntity.setNote(candidateRecruitmentRoundDto.getNote());
//                recruitmentRoundEntity.setResult(candidateRecruitmentRoundDto.getResult());
//
//                candidateRecruitmentRounds.add(recruitmentRoundEntity);
//            }
//        }
//
//        if (entity.getCandidateRecruitmentRounds() == null) {
//            entity.setCandidateRecruitmentRounds(new HashSet<>());
//        }
//        entity.getCandidateRecruitmentRounds().clear();
//        entity.getCandidateRecruitmentRounds().addAll(candidateRecruitmentRounds);
    }

    @Override
    public CandidateRecruitmentRoundDto saveCandidateRecruitmentRound(CandidateRecruitmentRoundDto dto) {
        if (dto == null || dto.getCandidate() == null || dto.getRecruitmentRound() == null) return null;

        CandidateRecruitmentRound entity = null;
        if (dto.getId() != null) {
            entity = candidateRecruitmentRoundRepository.findById(dto.getId()).orElse(null);
            if (entity == null) return null;
        }

        if (entity == null) {
            entity = new CandidateRecruitmentRound();

            if (dto.getCandidate().getId() != null) {
                Candidate candidate = candidateRepository.findById(dto.getCandidate().getId()).orElse(null);
                entity.setCandidate(candidate);
            }

            if (dto.getRecruitmentRound().getId() != null) {
                RecruitmentRound recruitmentRound = recruitmentRoundRepository.findById(dto.getRecruitmentRound().getId()).orElse(null);
                entity.setRecruitmentRound(recruitmentRound);
            }
        }

        if (entity.getDocuments() == null) {
            entity.setDocuments(new HashSet<>());
        } else {
            entity.getDocuments().clear();
        }

        if (dto.getDocuments() != null && !dto.getDocuments().isEmpty()) {
            for (CandidateRecruitmentRoundDocumentDto documentDto : dto.getDocuments()) {
                FileDescription fileDescription = null;
                if (documentDto.getFile() != null && documentDto.getFile().getId() != null) {
                    fileDescription = filedescriptionRepository.findById(documentDto.getFile().getId()).orElse(null);
                }
                CandidateRecruitmentRoundDocument documentEntity = new CandidateRecruitmentRoundDocument();

                documentEntity.setRound(entity);
                documentEntity.setNote(documentDto.getNote());
                documentEntity.setDisplayOrder(documentDto.getDisplayOrder());

                documentEntity.setFile(fileDescription);

                entity.getDocuments().add(documentEntity);
            }
        }
        Workplace workplace = null;
        if (dto.getWorkplace() != null && dto.getWorkplace().getId() != null) {
            workplace = workplaceRepository.findById(dto.getWorkplace().getId()).orElse(null);
        }
        entity.setWorkplace(workplace);

        entity.setStatus(dto.getStatus());
        entity.setNote(dto.getNote());
        entity.setActualTakePlaceDate(dto.getActualTakePlaceDate());
        entity.setRecruitmentType(dto.getRecruitmentType());
        entity.setResultStatus(dto.getResultStatus());
        entity = candidateRecruitmentRoundRepository.save(entity);
        return new CandidateRecruitmentRoundDto(entity);
    }

    @Override
    public CandidateRecruitmentRound convertDtoToEntity(CandidateRecruitmentRoundDto dto) {
        if (dto == null || dto.getCandidate() == null || dto.getRecruitmentRound() == null) return null;

        CandidateRecruitmentRound entity = null;
        if (dto.getId() != null) {
            entity = candidateRecruitmentRoundRepository.findById(dto.getId()).orElse(null);
            if (entity == null) return null;
        }
        if (entity == null) {
            entity = new CandidateRecruitmentRound();
        }
        if (dto.getRecruitmentRound().getId() != null) {
            RecruitmentRound recruitmentRound = recruitmentRoundRepository.findById(dto.getRecruitmentRound().getId()).orElse(null);
            entity.setRecruitmentRound(recruitmentRound);
        }
        if (dto.getCandidate() != null && dto.getCandidate().getId() != null) {
            Candidate candidate = candidateRepository.findById(dto.getCandidate().getId()).orElse(null);
            if (candidate != null) {
                entity.setCandidate(candidate);
            }
        }
        if (entity.getDocuments() == null) {
            entity.setDocuments(new HashSet<>());
        } else {
            entity.getDocuments().clear();
        }

        if (dto.getDocuments() != null && !dto.getDocuments().isEmpty()) {
            for (CandidateRecruitmentRoundDocumentDto documentDto : dto.getDocuments()) {
                FileDescription fileDescription = null;
                if (documentDto.getFile() != null && documentDto.getFile().getId() != null) {
                    fileDescription = filedescriptionRepository.findById(documentDto.getFile().getId()).orElse(null);
                }
                CandidateRecruitmentRoundDocument documentEntity = new CandidateRecruitmentRoundDocument();
                documentEntity.setRound(entity);
                documentEntity.setNote(documentDto.getNote());
                documentEntity.setDisplayOrder(documentDto.getDisplayOrder());
                documentEntity.setFile(fileDescription);
                entity.getDocuments().add(documentEntity);
            }
        }
        Workplace workplace = null;
        if (dto.getWorkplace() != null && dto.getWorkplace().getId() != null) {
            workplace = workplaceRepository.findById(dto.getWorkplace().getId()).orElse(null);
        }
        entity.setWorkplace(workplace);
        entity.setStatus(dto.getStatus());
        entity.setNote(dto.getNote());
        entity.setActualTakePlaceDate(dto.getActualTakePlaceDate());
        entity.setRecruitmentType(dto.getRecruitmentType());
        entity.setResultStatus(dto.getResultStatus());
        return entity;
    }

    @Override
    public Page<CandidateRecruitmentRoundDto> pagingCandidateRecruitmentRound(SearchCandidateRecruitmentRoundDto dto) {
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
        String orderBy = " ORDER BY entity.modifyDate desc ";

        String sqlCount = "select count(distinct entity.id) from CandidateRecruitmentRound as entity ";
        String sql = "select distinct new com.globits.hr.dto.CandidateRecruitmentRoundDto(entity) from CandidateRecruitmentRound as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.candidate.displayName LIKE :text OR entity.candidate.candidateCode LIKE :text " +
                    "OR entity.candidate.phoneNumber LIKE :text or entity.candidate.firstName LIKE :text or entity.candidate.lastName LIKE :text) ";
        }
        if (dto.getRecruitmentRoundId() != null) {
            whereClause += " and entity.recruitmentRound.id = :recruitmentRoundId ";
        }
        if (dto.getCandidate() != null && dto.getCandidate().getId() != null) {
            whereClause += " and entity.candidate.id = :candidateId ";
        }


        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, SalaryResultDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getRecruitmentRoundId() != null) {
            query.setParameter("recruitmentRoundId", dto.getRecruitmentRoundId());
            qCount.setParameter("recruitmentRoundId", dto.getRecruitmentRoundId());
        }
        if (dto.getCandidate() != null && dto.getCandidate().getId() != null) {
            query.setParameter("candidateId", dto.getCandidate().getId());
            qCount.setParameter("candidateId", dto.getCandidate().getId());
        }

        long count = (long) qCount.getSingleResult();
        int startPosition = pageIndex * pageSize;

        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        List<CandidateRecruitmentRoundDto> entities = query.getResultList();
        Page<CandidateRecruitmentRoundDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public CandidateRecruitmentRoundDto getById(UUID id) {
        CandidateRecruitmentRound entity = candidateRecruitmentRoundRepository.findById(id).orElse(null);
        if (entity == null) return null;

        return new CandidateRecruitmentRoundDto(entity, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean remove(UUID id) {
        if (id == null) {
            throw new RuntimeException("Invalid argument!");
        }

        CandidateRecruitmentRound entity = candidateRecruitmentRoundRepository.findById(id).orElse(null);

        if (entity == null || entity.getId() == null)
            throw new RuntimeException("Invalid argument!");

        candidateRecruitmentRoundRepository.delete(entity);

        return true;
    }

    @Override
    public Boolean removeMultiple(List<UUID> ids) {
        if (ids == null)
            return false;
        for (UUID candidateRecruitmentRoundId : ids) {
            this.remove(candidateRecruitmentRoundId);
        }
        return true;
    }

    @Override
    public Boolean updateCandidateRecruitmentRoundResult(SearchCandidateRecruitmentRoundDto dto) throws Exception {
        if (dto == null || dto.getResult() == null || dto.getChosenRecordIds() == null) return false;
        if (dto.getChosenRecordIds().isEmpty()) return true;

        List<CandidateRecruitmentRound> onUpdatingRecords = new ArrayList<>();
        for (UUID recordId : dto.getChosenRecordIds()) {
            CandidateRecruitmentRound entity = candidateRecruitmentRoundRepository.findById(recordId).orElse(null);

            if (entity == null) {
                throw new Exception("Entity not found");
            }

            entity.setNote(dto.getNote());

            onUpdatingRecords.add(entity);
        }

        onUpdatingRecords = candidateRecruitmentRoundRepository.saveAll(onUpdatingRecords);

        // update exam status of candidate is RECRUITING if round result is updated
        return this.autoUpdateExamStatusOfCandidateByChangingRoundResult(dto, onUpdatingRecords);
    }

    @Override
    public Boolean moveToNextRecruitmentRound(SearchCandidateRecruitmentRoundDto dto) throws Exception {
        if (dto == null || dto.getChosenRecordIds() == null) return false;
        if (dto.getChosenRecordIds().isEmpty()) return true;

        CandidateRecruitmentRound firstCRRItem = candidateRecruitmentRoundRepository.findById(dto.getChosenRecordIds().get(0)).orElse(null);
        if (firstCRRItem == null || firstCRRItem.getRecruitmentRound() == null || firstCRRItem.getRecruitmentRound().getId() == null)
            return false;

        RecruitmentRound nextRound = recruitmentRoundService.getNextRecruitmentRound(firstCRRItem.getRecruitmentRound().getId());
        // if there's no other recruitment round, all chosen candidates will have exam status is PASSED
        if (nextRound == null) {
            return this.updateExamStatusOfCandidateToPassed(dto);
        }

        List<CandidateRecruitmentRound> onCreatingRecords = new ArrayList<>();

        for (UUID recordId : dto.getChosenRecordIds()) {
            CandidateRecruitmentRound entity = candidateRecruitmentRoundRepository.findById(recordId).orElse(null);
            if (entity == null || entity.getCandidate() == null)
                throw new Exception("Invalid chosen record id in moveToNextRecruitmentRound");

            List<CandidateRecruitmentRound> availableRecords = candidateRecruitmentRoundRepository.getByCandidateIdAndRecruitmentRoundId(entity.getCandidate().getId(), nextRound.getId());

            // existed Next round record => skip create new record
            if (availableRecords != null && !availableRecords.isEmpty()) continue;

            CandidateRecruitmentRound nextRoundRecord = new CandidateRecruitmentRound();

            nextRoundRecord.setRecruitmentRound(nextRound);
            nextRoundRecord.setCandidate(entity.getCandidate());

            // ngày dự thi phân bổ cho ứng viên
            nextRoundRecord.setActualTakePlaceDate(dto.getActualTakePlaceDate());
            // địa điểm dự thi phân bổ

            Workplace workplace = null;
            if (dto.getWorkplace() != null && dto.getWorkplace().getId() != null) {
                workplace = workplaceRepository.findById(dto.getWorkplace().getId()).orElse(null);
            }
            if (workplace == null && dto.getWorkplaceId() != null) {
                workplace = workplaceRepository.findById(dto.getWorkplaceId()).orElse(null);
            }
            entity.setWorkplace(workplace);

            onCreatingRecords.add(nextRoundRecord);
        }

        candidateRecruitmentRoundRepository.saveAll(onCreatingRecords);

        return true;
    }


    @Override
    public Boolean distributeCandidatesForFirstRecruitmentRound(SearchCandidateRecruitmentRoundDto dto) throws Exception {
        // chosenRecordIds here is candidateIds
        if (dto == null || dto.getChosenRecordIds() == null) return false;
        if (dto.getChosenRecordIds().isEmpty()) return true;

        List<CandidateRecruitmentRound> cuFirstRecruitmentRounds = new ArrayList<>();

        for (UUID recordId : dto.getChosenRecordIds()) {
            Candidate candidate = candidateRepository.findById(recordId).orElse(null);
            if (candidate == null)
                throw new Exception("Candidate is not existed");

            if (candidate.getRecruitment() == null) continue;
            RecruitmentRound firstRound = recruitmentRoundService.getFirstRecruitmentRoundOfRecruitment(candidate.getRecruitment().getId());
            if (firstRound == null) continue;

            List<CandidateRecruitmentRound> availableRecords = candidateRecruitmentRoundRepository.getByCandidateIdAndRecruitmentRoundId(candidate.getId(), firstRound.getId());

            // existed First round record => skip create new record
            if (availableRecords != null && !availableRecords.isEmpty()) continue;

            CandidateRecruitmentRound firstRoundRecord = new CandidateRecruitmentRound();

            firstRoundRecord.setRecruitmentRound(firstRound);
            firstRoundRecord.setCandidate(candidate);

            // ngày dự thi phân bổ cho ứng viên
            firstRoundRecord.setActualTakePlaceDate(dto.getActualTakePlaceDate());
            // địa điểm dự thi phân bổ
            Workplace workplace = null;
            if (dto.getWorkplace() != null && dto.getWorkplace().getId() != null) {
                workplace = workplaceRepository.findById(dto.getWorkplace().getId()).orElse(null);
            }
            if (workplace == null && dto.getWorkplaceId() != null) {
                workplace = workplaceRepository.findById(dto.getWorkplaceId()).orElse(null);
            }
            firstRoundRecord.setWorkplace(workplace);

            cuFirstRecruitmentRounds.add(firstRoundRecord);
        }

        candidateRecruitmentRoundRepository.saveAll(cuFirstRecruitmentRounds);

        return true;
    }

    @Override
    public Boolean checkExistCandidateRecruitmentRound(UUID candidateId, UUID recruitmentRoundId) {
        if (candidateId == null || recruitmentRoundId == null) return false;
        List<CandidateRecruitmentRound> availableRecords = candidateRecruitmentRoundRepository.getByCandidateIdAndRecruitmentRoundId(candidateId, recruitmentRoundId);
        if (!CollectionUtils.isEmpty(availableRecords)) {
            return true;
        }
        return false;
    }

    @Override
    public ApiResponse<List<CandidateRecruitmentRoundDto>> getListCandiDateByPlainAndRound(UUID planId, UUID roundId) {
        if (planId == null || roundId == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "OK", null);
        } else {
            List<CandidateRecruitmentRoundDto> candidates = candidateRecruitmentRoundRepository.getListCandiDateByPlainAndRound(planId, roundId);
            return new ApiResponse<>(HttpStatus.SC_OK, "OK", candidates);
        }
    }


    private Boolean updateExamStatusOfCandidateToPassed(SearchCandidateRecruitmentRoundDto dto) throws Exception {
        if (dto == null || dto.getChosenRecordIds() == null) return false;
        if (dto.getChosenRecordIds().isEmpty()) return true;

        List<UUID> candidateIds = new ArrayList<>();
        for (UUID recordId : dto.getChosenRecordIds()) {
            CandidateRecruitmentRound entity = candidateRecruitmentRoundRepository.findById(recordId).orElse(null);
            if (entity == null || entity.getCandidate() == null) continue;

            candidateIds.add(entity.getCandidate().getId());
        }

        Integer examStatus = HrConstants.CandidateExamStatus.PASSED.getValue();

        SearchCandidateDto searchCandidateDto = new SearchCandidateDto();
        searchCandidateDto.setCandidateIds(candidateIds);
        searchCandidateDto.setExamStatus(examStatus);

        return candidateService.updateExamStatus(searchCandidateDto);
    }

    private Boolean autoUpdateExamStatusOfCandidateByChangingRoundResult(SearchCandidateRecruitmentRoundDto dto, List<CandidateRecruitmentRound> savedEntities) throws Exception {
        if (savedEntities == null) return false;
        if (savedEntities.isEmpty()) return true;

        List<UUID> candidateIds = new ArrayList<>();
        for (CandidateRecruitmentRound entity : savedEntities) {
            if (entity == null || entity.getCandidate() == null) continue;

            candidateIds.add(entity.getCandidate().getId());
        }

        Integer examStatus = HrConstants.CandidateExamStatus.RECRUITING.getValue();
//        if candidate's round result is convert to FAILED => exam status of candidate is FAILED
        if (dto.getResult().equals(HrConstants.CandidateExamStatus.FAILED.getValue()))
            examStatus = HrConstants.CandidateExamStatus.FAILED.getValue();

        SearchCandidateDto searchCandidateDto = new SearchCandidateDto();
        searchCandidateDto.setCandidateIds(candidateIds);
        searchCandidateDto.setExamStatus(examStatus);

        return candidateService.updateExamStatus(searchCandidateDto);
    }

    @Override
    public ApiResponse<Boolean> confirmInterview(UUID candidateId, HrConstants.CandidateRecruitmentRoundStatus decision) {
        if (candidateId == null) return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không có mã hồ sơ", null);
        CandidateRecruitmentRound candidate = candidateRecruitmentRoundRepository.findById(candidateId).orElse(null);
        if (candidate != null) {
            if (decision != null) {
                if (decision.equals(HrConstants.CandidateRecruitmentRoundStatus.PARTICIPATED)) {
                    candidate.setStatus(HrConstants.CandidateRecruitmentRoundStatus.PARTICIPATED);
                }
                if (decision.equals(HrConstants.CandidateRecruitmentRoundStatus.NOT_PARTICIPATED)) {
                    candidate.setStatus(HrConstants.CandidateRecruitmentRoundStatus.NOT_PARTICIPATED);
                }
                if (decision.equals(HrConstants.CandidateRecruitmentRoundStatus.REJECTED)) {
                    candidate.setStatus(HrConstants.CandidateRecruitmentRoundStatus.REJECTED);
                }

                candidateRecruitmentRoundRepository.save(candidate);
                return new ApiResponse<>(HttpStatus.SC_OK, "Xác nhận thành công", true);
            }
        } else {
            if (candidateId == null) return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không tìm thấy hồ", null);
        }
        return null;
    }

    @Override
    public ApiResponse<List<CandidateRecruitmentRoundDto>> getByIdRecruitmentRound(UUID roundId) {
        if (roundId == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không được bỏ trông ID vòng tuyển dụng", null);
        }
        RecruitmentRound round = recruitmentRoundRepository.findById(roundId).orElse(null);
        if (round == null) {
            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không tìm thấy vòng tuyển dụng", null);
        }

        List<CandidateRecruitmentRound> candidateRecruitmentRounds = candidateRecruitmentRoundRepository.findByRecruitmentRoundId(roundId);

        if (candidateRecruitmentRounds.isEmpty()) {
            return new ApiResponse<>(HttpStatus.SC_OK, "Chưa có ứng viên nào trong vòng tuyển dụng", new ArrayList<>());
        }

        List<CandidateRecruitmentRoundDto> res = new ArrayList<>();
        for (CandidateRecruitmentRound rec : candidateRecruitmentRounds) {
            CandidateRecruitmentRoundDto resDto = new CandidateRecruitmentRoundDto(rec);
            res.add(resDto);
        }
        return new ApiResponse<>(HttpStatus.SC_OK, "OK", res);
    }

    @Override
    public ApiResponse<Boolean> passToNextRound(UUID crrId) {
        if (crrId != null) {
            CandidateRecruitmentRound crr = candidateRecruitmentRoundRepository.findById(crrId).orElse(null);
            if (crr != null) {
                if (crr.getResultStatus() == null || !crr.getResultStatus().equals(HrConstants.ResultStatus.FAIL)) {
                    Candidate candidate = crr.getCandidate();
                    if (candidate != null) {
                        UserDto user = userExtService.getCurrentUser();
                        try {
                            List<String> roleNames = user.getRoles().stream()
                                    .map(RoleDto::getName)
                                    .toList();
                            if (roleNames.contains(HR_ASSIGNMENT) || roleNames.contains(ROLE_ADMIN)) {
                                RecruitmentRound nextRound = recruitmentRoundService.getNextRound(crr.getRecruitmentRound().getRecruitmentPlan().getId(), crr.getRecruitmentRound().getRoundOrder() + 1);
                                if (nextRound == null) {
                                    return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Đã là vòng cuối không thể chuyển sang vòng tiếp theo", false);
                                }
                                CandidateRecruitmentRound candidateRecruitmentRound = candidateRecruitmentRoundRepository.findTheNextRound(crr.getCandidate().getId(), nextRound.getId()).orElse(new CandidateRecruitmentRound());
                                if (candidateRecruitmentRound.getId() != null) {
                                    crr.setStatus(HrConstants.CandidateRecruitmentRoundStatus.PARTICIPATED);
                                    crr.setResultStatus(HrConstants.ResultStatus.PASS);
                                    candidateRecruitmentRound.setCandidate(candidate);
                                    candidateRecruitmentRound.setRecruitmentRound(nextRound);
                                    if (candidate.getStatus() != null &&
                                            (candidate.getStatus().equals(HrConstants.CandidateStatus.ACCEPTED_ASSIGNMENT.getValue())
                                                    || candidate.getStatus().equals(HrConstants.CandidateStatus.PENDING_ASSIGNMENT.getValue())
                                                    || candidate.getStatus().equals(HrConstants.CandidateStatus.DECLINED_ASSIGNMENT.getValue())
                                            )) {
                                        candidateRecruitmentRound.setResultStatus(HrConstants.ResultStatus.PASS);
                                        candidateRecruitmentRound.setStatus(HrConstants.CandidateRecruitmentRoundStatus.PARTICIPATED);
                                    }
                                    candidateRecruitmentRoundRepository.save(candidateRecruitmentRound);
                                    candidateRecruitmentRoundRepository.save(crr);
                                    return new ApiResponse<>(HttpStatus.SC_OK, "Chuyển sang vòng tiếp theo thành công  ", true);
                                } else {
                                    return new ApiResponse<>(HttpStatus.SC_OK, "Đây đã là vòng cuối cùng không thể chuyển sang vòng tiếp theo  ", true);
                                }
                            } else {
                                return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, "Tài khoản ko có quyền tuyển dụng  ", false);
                            }
                        } catch (Exception e) {
                            logger.error("Error when pass to next round: " + e.getMessage());
                            return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, "Tài khoản ko có quyền tuyển dụng!  ", false);
                        }
                    } else {
                        return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy ứng viên", false);
                    }
                } else {
                    return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Ứng viên này không đạt không thể lên vòng tiếp theo", false);
                }
            }
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy ứng viên trong vòng này", false);
        }
        return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Có lỗi xảy ra", false);
    }

    @Override
    public ApiResponse<Boolean> rejectCandidateRound(UUID crrId) {
        if (crrId != null) {
            UserDto user = userExtService.getCurrentUser();
            try {
                List<String> roleNames = user.getRoles().stream()
                        .map(RoleDto::getName)
                        .toList();
                if (roleNames.contains(HR_ASSIGNMENT) || roleNames.contains(ROLE_ADMIN) || roleNames.contains(HR_MANAGER) || roleNames.contains(SUPER_HR)) {
                    CandidateRecruitmentRound crr = candidateRecruitmentRoundRepository.findById(crrId).orElse(null);
                    if (crr != null) {
                        Candidate candidate = crr.getCandidate();
                        if (candidate != null) {
                            return doReject(candidate, crr);
                        }
                    } else {
                        return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy ứng viên", false);
                    }
                } else {
                    return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, "Tài khoản ko có quyền tuyển dụng", false);
                }
            } catch (Exception e) {
                logger.error("Error when pass to next round: " + e.getMessage());
                return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, "Tài khoản ko có quyền tuyển dụng!", false);
            }
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy ứng viên trong vòng này", false);
        }
        return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Có lỗi xảy ra", false);
    }

    public ApiResponse<Boolean> doReject(Candidate candidate, CandidateRecruitmentRound crr) {
        if (!candidate.getStatus().equals(HrConstants.CandidateStatus.PENDING_ASSIGNMENT.getValue())
                && !candidate.getStatus().equals(HrConstants.CandidateStatus.ACCEPTED_ASSIGNMENT.getValue())
                && !candidate.getStatus().equals(HrConstants.CandidateStatus.DECLINED_ASSIGNMENT.getValue())) {
            // Tìm kiếm vòng tiếp theo
            RecruitmentRound nextRound = recruitmentRoundService.getNextRound(crr.getRecruitmentRound().getRecruitmentPlan().getId(), crr.getRecruitmentRound().getRoundOrder() + 1);
            if (nextRound != null) {
                // Tìm kiếm candidate có mặt trên vòng tiếp theo không
                CandidateRecruitmentRound candidateRecruitmentRound = candidateRecruitmentRoundRepository.findTheNextRound(crr.getCandidate().getId(), nextRound.getId()).orElse(null);
                if (candidateRecruitmentRound != null) {
                    return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Đánh trượt thất bại hồ sơ này đã có mặt ở vòng sau", true);
                }
            }
            candidate.setStatus(CandidateStatus.REJECTED.getValue());
            crr.setResultStatus(HrConstants.ResultStatus.FAIL);
            candidateRecruitmentRoundRepository.save(crr);
            candidateRepository.save(candidate);
            return new ApiResponse<>(HttpStatus.SC_OK, "Đánh trượt thành công", true);
        } else {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Đánh trượt thất bại hồ sơ này đã được nhận", true);
        }
    }

    @Override
    public List<CandidateRecruitmentRoundDto> getCandidateRoundByCandidateId(UUID candidateId) {
        if (candidateId == null) return null;

        List<CandidateRecruitmentRoundDto> res = candidateRecruitmentRoundRepository.getCandidateRoundByCandidateId(candidateId);
        CandidateRecruitmentRound currentRound = candidateRecruitmentRoundRepository.getCurrentRoundOfCandidate(candidateId);

        if (currentRound != null) {
            for (CandidateRecruitmentRoundDto dto : res) {
                if (dto.getId().equals(currentRound.getId())) {
                    dto.setCurrent(true); // Giả sử bạn có field `current` trong DTO
                }
            }
        }

        return res;
    }

    @Override
    public ApiResponse<HashMap<UUID, HashMap<String, Object>>> passListToNextRound(List<UUID> crrIds) {
        int success = 0;
        HashMap<UUID, HashMap<String, Object>> message = new HashMap<>();
        for (UUID crrId : crrIds) {
            HashMap<String, Object> res = new HashMap<>();
            ApiResponse<Boolean> response = passToNextRound(crrId);
            if (response.getStatus() == HttpStatus.SC_OK) {
                success++;
            }
            res.put("message", response.getMessage());
            res.put("status", response.getStatus());
            message.put(crrId, res);
        }
        return new ApiResponse<>(HttpStatus.SC_OK, "Thành công " + success + " Thất bại " + (crrIds.size() - success), message);
    }

    @Override
    public ApiResponse<Boolean> rejectListCandidateRound(List<UUID> crrIds) {
        int success = 0;
        for (UUID crrId : crrIds) {
            ApiResponse<Boolean> response = rejectCandidateRound(crrId);
            if (response.getStatus() == HttpStatus.SC_OK) {
                success++;
            }
        }
        return new ApiResponse<>(HttpStatus.SC_OK, "Thành công " + success + " Thất bại " + (crrIds.size() - success), true);
    }

    @Override
    public ApiResponse<Boolean> doActionAssignment(UUID crrId, HrConstants.CandidateStatus status) {
        if (crrId != null && status != null) {
            CandidateRecruitmentRound crr = candidateRecruitmentRoundRepository.findById(crrId).orElse(null);
            if (crr != null) {
                Candidate candidate = crr.getCandidate();
                if (candidate != null) {
                    UserDto user = userExtService.getCurrentUser();
                    try {
                        List<String> roleNames = user.getRoles().stream()
                                .map(RoleDto::getName)
                                .toList();

                        if (roleNames.contains(HR_ASSIGNMENT) || roleNames.contains(ROLE_ADMIN) || roleNames.contains(HR_MANAGER) || roleNames.contains(SUPER_HR)) {
                            if (status.equals(HrConstants.CandidateStatus.PENDING_ASSIGNMENT)) {
                                candidate.setStatus(status.getValue());
                                crr.setResultStatus(HrConstants.ResultStatus.PASS);
                                candidateRecruitmentRoundRepository.save(crr);
                            } else {
                                candidate.setStatus(status.getValue());
                            }
                            candidateRepository.save(candidate);
                            return new ApiResponse<>(HttpStatus.SC_OK, "Tuyển dụng thành công", true);
                        } else {
                            return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, "Tài khoản ko có quyền tuyển dụng", false);
                        }
                    } catch (Exception e) {
                        return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, "Tài khoản ko có quyền tuyển dụng", false);
                    }
                }
                return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy ứng viên", false);
            }
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy ứng viên trong vòng này", false);
        }
        return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Có lỗi xảy ra", false);
    }
}
