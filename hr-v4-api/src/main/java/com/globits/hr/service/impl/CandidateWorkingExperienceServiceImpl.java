package com.globits.hr.service.impl;

import com.globits.core.domain.FileDescription;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Candidate;
import com.globits.hr.domain.CandidateAttachment;
import com.globits.hr.domain.CandidateWorkingExperience;
import com.globits.hr.domain.Position;
import com.globits.hr.dto.CandidateAttachmentDto;
import com.globits.hr.dto.CandidateDto;
import com.globits.hr.dto.CandidateWorkingExperienceDto;
import com.globits.hr.repository.CandidateWorkingExperienceRepository;
import com.globits.hr.repository.PositionRepository;
import com.globits.hr.service.CandidateAttachmentService;
import com.globits.hr.service.CandidateWorkingExperienceService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Transactional
@Service
public class CandidateWorkingExperienceServiceImpl extends GenericServiceImpl<CandidateWorkingExperience, UUID>
        implements CandidateWorkingExperienceService {
    @Autowired
    private CandidateWorkingExperienceRepository candidateWorkingExperienceRepository;
    @Autowired
    private PositionRepository positionRepository;

    @Override
    public void handleSetWorkingExperienceList(CandidateDto dto, Candidate entity) {
        Set<CandidateWorkingExperience> experienceList = new HashSet<>();
        if (dto.getCandidateWorkingExperiences() != null && !dto.getCandidateWorkingExperiences().isEmpty()) {
            for (CandidateWorkingExperienceDto workingExperienceDto : dto.getCandidateWorkingExperiences()) {
                CandidateWorkingExperience workingExperienceEntity = null;
                if (workingExperienceDto != null && workingExperienceDto.getId() != null) {
                    workingExperienceEntity = candidateWorkingExperienceRepository.findById(workingExperienceDto.getId()).orElse(null);
                }

                if (workingExperienceEntity == null) {
                    workingExperienceEntity = new CandidateWorkingExperience();
                    workingExperienceEntity.setCandidate(entity);
                }

                if (workingExperienceDto.getPosition() != null) {
                    Position position = positionRepository.findById(workingExperienceDto.getPosition().getId()).orElse(null);
                    workingExperienceEntity.setPosition(position);
                }

                workingExperienceEntity.setCompanyName(workingExperienceDto.getCompanyName());
                workingExperienceEntity.setStartDate(workingExperienceDto.getStartDate());
                workingExperienceEntity.setEndDate(workingExperienceDto.getEndDate());
                workingExperienceEntity.setSalary(workingExperienceDto.getSalary());
                workingExperienceEntity.setLeavingReason(workingExperienceDto.getLeavingReason());
                workingExperienceEntity.setDecription(workingExperienceDto.getDecription());
                workingExperienceEntity.setOldPosition(workingExperienceDto.getOldPosition());
                experienceList.add(workingExperienceEntity);
            }
        }

        if (entity.getCandidateWorkingExperience() == null) {
            entity.setCandidateWorkingExperience(new HashSet<>());
        }
        entity.getCandidateWorkingExperience().clear();
        entity.getCandidateWorkingExperience().addAll(experienceList);
    }
}
