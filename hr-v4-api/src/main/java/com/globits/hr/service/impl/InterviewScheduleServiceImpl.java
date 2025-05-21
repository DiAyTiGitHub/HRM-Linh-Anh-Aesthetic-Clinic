package com.globits.hr.service.impl;

import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.InterviewScheduleSearchDto;
import com.globits.hr.repository.CandidateRepository;
import com.globits.hr.repository.InterviewScheduleRepository;
import com.globits.hr.repository.RecruitmentRoundRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.InterviewScheduleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InterviewScheduleServiceImpl implements InterviewScheduleService {

    @PersistenceContext
    EntityManager manager;

    @Autowired
    private InterviewScheduleRepository repository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private RecruitmentRoundRepository recrruitmentRoundRepository;

    @Override
    public ApiResponse<InterviewScheduleDto> getById(UUID id) {
        if (id == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Id is empty", null);
        }
        InterviewSchedule entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Can't find with id: " + id, null);
        }
        return new ApiResponse<>(HttpStatus.SC_OK, "Success", toDto(entity));
    }

    @Override
    public ApiResponse<List<InterviewScheduleDto>> getAll() {
        List<InterviewSchedule> entities = repository.findAll();
        List<InterviewScheduleDto> dtos = entities.stream().map(this::toDto).collect(Collectors.toList());
        return new ApiResponse<>(HttpStatus.SC_OK, "Success", dtos);
    }

    @Override
    @Transactional
    public ApiResponse<InterviewScheduleDto> save(InterviewScheduleDto dto) {
        if (dto == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "DTO rỗng", null);
        }

        InterviewSchedule entity = null;

        if (dto.getId() != null) {
            entity = repository.findById(dto.getId()).orElse(null);
            if (entity != null) {
                entity.setStatus(dto.getStatus());
            }
        }

        if (entity == null) {
            entity = new InterviewSchedule();
            entity.setStatus(HrConstants.InterviewScheduleStatus.PENDING_CANDIDATE_CONFIRMATION.getValue());
        }

        if (dto.getCandidate() == null || dto.getCandidate().getId() == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Ứng viên không được bỏ trống", null);
        }

        Candidate candidate = candidateRepository.findById(dto.getCandidate().getId()).orElse(null);

        if (candidate == null) {
            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không tìm thấy ứng viên với id: " + dto.getCandidate().getId(), null);
        }

        if (dto.getStaffInterviewSchedules() == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Danh sách người phỏng vấn không được bỏ trống", null);
        }

        candidate.setStatus(HrConstants.CandidateStatus.PENDING_CANDIDATE_CONFIRMATION.getValue());
        candidateRepository.save(candidate);

        entity.setCandidate(candidate);
        entity.setInterviewTime(dto.getInterviewTime());
        entity.setInterviewLocation(dto.getInterviewLocation());
        entity.setNote(dto.getNote());

        // TODO: xử lý danh sách staffInterviewSchedules nếu có
        if (entity.getStaffInterviewSchedules() != null) {
            entity.getStaffInterviewSchedules().clear();
        } else {
            entity.setStaffInterviewSchedules(new HashSet<>());
        }
        if (dto.getStaffInterviewSchedules() != null && !dto.getStaffInterviewSchedules().isEmpty()) {
            for (StaffInterviewScheduleDto sisDto : dto.getStaffInterviewSchedules()) {
                StaffInterviewSchedule staffInterviewSchedule = new StaffInterviewSchedule();

                if (sisDto.getInterviewer() == null && sisDto.getInterviewer().getId() == null) {
                    return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Người phỏng vấn không được bỏ trống", null);
                }

                Staff staff = staffRepository.findById(sisDto.getInterviewer().getId()).orElse(null);

                if (staff == null) {
                    return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không tìm thấy người phỏng vấn với id: " + sisDto.getInterviewer().getId(), null);
                }
                staffInterviewSchedule.setInterviewer(staff);
                staffInterviewSchedule.setInterviewRole(sisDto.getInterviewRole());
                staffInterviewSchedule.setInterviewSchedule(entity);
                staffInterviewSchedule.setNote(sisDto.getNote());
                entity.getStaffInterviewSchedules().add(staffInterviewSchedule);
            }
        }
        RecruitmentRound recruitmentRound = null;
        if (dto.getRecruitmentRound() != null && dto.getRecruitmentRound().getId() != null) {
            recruitmentRound = recrruitmentRoundRepository.findById(dto.getRecruitmentRound().getId()).orElse(null);
        }
        if (recruitmentRound == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Vòng phỏng vấn không được bỏ trống", null);
        }
        entity.setRecruitmentRound(recruitmentRound);

        InterviewSchedule saved = repository.save(entity);
        return new ApiResponse<>(HttpStatus.SC_OK, "Lưu thành công", toDto(saved));
    }

    @Override
    @Transactional
    public ApiResponse<Integer> saveMultiple(CreateInterviewSchedulesDto dto) {
        if (dto == null || dto.getCandidateIds() == null || dto.getCandidateIds().isEmpty()) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "DTO hoặc danh sách ứng viên rỗng", null);
        }

        int count = 0;
        for (UUID candidateId : dto.getCandidateIds()) {
            InterviewScheduleDto scheduleDto = new InterviewScheduleDto();
            scheduleDto.setInterviewTime(dto.getInterviewTime());
            scheduleDto.setInterviewLocation(dto.getInterviewLocation());
            scheduleDto.setNote(dto.getNote());
            scheduleDto.setStatus(HrConstants.InterviewScheduleStatus.PENDING_CANDIDATE_CONFIRMATION.getValue());

            // Gán candidate
            CandidateDto candidateDto = new CandidateDto();
            candidateDto.setId(candidateId);
            scheduleDto.setCandidate(candidateDto);

            // Gán danh sách người phỏng vấn
            scheduleDto.setStaffInterviewSchedules(dto.getStaffInterviewSchedules());

            scheduleDto.setRecruitmentRound(dto.getRecruitmentRound());
            ApiResponse<InterviewScheduleDto> response = this.save(scheduleDto);

            if (response.getStatus() == HttpStatus.SC_OK) {
                count++;
            } else {
                return new ApiResponse<>(response.getStatus(), "Lỗi khi lưu ứng viên " + candidateId + ": " + response.getMessage(), null);
            }
        }

        return new ApiResponse<>(HttpStatus.SC_OK, "Lưu thành công " + count + " lịch phỏng vấn", count);
    }


    @Override
    public void delete(UUID id) {
        if (id != null) {
            repository.deleteById(id);
        }
    }

    @Override
    public ApiResponse<Boolean> markDeleted(UUID id) {
        if (id != null) {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                return new ApiResponse<>(HttpStatus.SC_OK, "Xoá thành công", true);
            } else {
                return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "ID không hợp lệ " + id, false);
            }
        }
        return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "ID rỗng", null);
    }

    @Override
    public ApiResponse<Page<InterviewScheduleDto>> paging(InterviewScheduleSearchDto dto) {
        if (dto == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Dữ liệu tìm kiếm rỗng", null);
        }

        int pageIndex = dto.getPageIndex() > 0 ? dto.getPageIndex() - 1 : 0;
        int pageSize = dto.getPageSize();

        String whereClause = " WHERE 1=1 ";
        String orderBy = " ORDER BY entity.createDate DESC ";
        String sqlCount = "SELECT count(entity.id) FROM InterviewSchedule AS entity ";
        String sql = "SELECT new com.globits.hr.dto.InterviewScheduleDto(entity) FROM InterviewSchedule AS entity ";

        if (StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.note LIKE :text) ";
        }

        if (dto.getCandidateId() != null) {
            whereClause += " AND (entity.candidate.id = :candidateId) ";
        }

        if (dto.getStatus() != null) {
            whereClause += " AND (entity.status = :status) ";
        }
        if (dto.getRecruitmentRoundId() != null) {
            whereClause += " AND (entity.recruitmentRound.id = :recruitmentRoundId) ";
        }

        if (dto.getFromDate() != null) {
            whereClause += " AND (entity.interviewTime >= :fromDate) ";
        }
        if (dto.getToDate() != null) {
            whereClause += " AND (entity.interviewTime <= :toDate) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, InterviewScheduleDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", "%" + dto.getKeyword().trim() + "%");
            qCount.setParameter("text", "%" + dto.getKeyword().trim() + "%");
        }

        if (dto.getCandidateId() != null) {
            q.setParameter("candidateId", dto.getCandidateId());
            qCount.setParameter("candidateId", dto.getCandidateId());
        }

        if (dto.getStatus() != null) {
            q.setParameter("status", dto.getStatus());
            qCount.setParameter("status", dto.getStatus());
        }
        if (dto.getRecruitmentRoundId() != null) {
            q.setParameter("recruitmentRoundId", dto.getRecruitmentRoundId());
            qCount.setParameter("recruitmentRoundId", dto.getRecruitmentRoundId());
        }

        if (dto.getFromDate() != null) {
            LocalDateTime startOfDay = dto.getFromDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();
            Date fromDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());

            q.setParameter("fromDate", fromDate);
            qCount.setParameter("fromDate", fromDate);
        }
        if (dto.getToDate() != null) {
            LocalDateTime endOfDay = dto.getToDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate().atTime(23, 59, 59);
            Date toDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

            q.setParameter("toDate", toDate);
            qCount.setParameter("toDate", toDate);
        }
        q.setFirstResult(pageIndex * pageSize);
        q.setMaxResults(pageSize);

        List<InterviewScheduleDto> results = q.getResultList();
        long total = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<InterviewScheduleDto> page = new PageImpl<>(results, pageable, total);

        return new ApiResponse<>(HttpStatus.SC_OK, "OK", page);
    }


    private InterviewScheduleDto toDto(InterviewSchedule entity) {
        return new InterviewScheduleDto(entity);
    }
}
