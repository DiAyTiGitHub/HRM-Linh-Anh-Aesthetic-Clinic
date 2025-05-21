package com.globits.hr.service.impl;

import com.globits.core.domain.Country;
import com.globits.core.repository.CountryRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.*;
import com.globits.hr.dto.CandidateDto;
import com.globits.hr.dto.CandidateEducationHistoryDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.CandidateEducationHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Transactional
@Service
public class CandidateEducationHistoryServiceImpl extends GenericServiceImpl<CandidateEducationHistory, UUID>
        implements CandidateEducationHistoryService {
    @Autowired
    private CandidateEducationHistoryRepository candidateEducationHistoryRepository;
    @Autowired
    private EducationalInstitutionRepository educationalInstitutionRepository;
    @Autowired
    private HrEducationTypeRepository hrEducationTypeRepository;
    @Autowired
    private HrSpecialityRepository hrSpecialityRepository;
    @Autowired
    private EducationDegreeRepository educationDegreeRepository;
    @Autowired
    private CountryRepository countryRepository;

    @Override
    public void handleSetEducationHistoryList(CandidateDto dto, Candidate entity) {
        Set<CandidateEducationHistory> educationHistories = new HashSet<>();
        if (dto.getCandidateEducationalHistories() != null && !dto.getCandidateEducationalHistories().isEmpty()) {
            for (CandidateEducationHistoryDto eduHistory : dto.getCandidateEducationalHistories()) {
                CandidateEducationHistory eduEntity = null;
                if (eduHistory.getId() != null) {
                    eduEntity = candidateEducationHistoryRepository.findById(eduHistory.getId()).orElse(null);
                }

                if (eduEntity == null) {
                    eduEntity = new CandidateEducationHistory();
                    eduEntity.setCandidate(entity);
                }

                if (eduHistory.getCountry() != null) {
                    Country country = countryRepository.findById(eduHistory.getCountry().getId()).orElse(null);
                    eduEntity.setCountry(country);
                }
                if (eduHistory.getSpeciality() != null) {
                    HrSpeciality hrSpeciality = hrSpecialityRepository
                            .findById(eduHistory.getSpeciality().getId()).orElse(null);
                    eduEntity.setSpeciality(hrSpeciality);
                }
                if (eduHistory.getMajor() != null) {
                    HrSpeciality hrSpeciality = hrSpecialityRepository
                            .findById(eduHistory.getMajor().getId()).orElse(null);
                    eduEntity.setMajor(hrSpeciality);
                }

                if (eduHistory.getEducationType() != null) {
                    HrEducationType hrEducationType = hrEducationTypeRepository
                            .findById(eduHistory.getEducationType().getId()).orElse(null);
                    eduEntity.setEducationType(hrEducationType);
                }

                if (eduHistory.getEducationalInstitution() != null) {
                    EducationalInstitution educationalInstitution = educationalInstitutionRepository
                            .findById(eduHistory.getEducationalInstitution().getId()).orElse(null);
                    eduEntity.setEducationalInstitution(educationalInstitution);
                }

                if (eduHistory.getEducationDegree() != null) {
                    EducationDegree educationDegree = educationDegreeRepository
                            .findById(eduHistory.getEducationDegree().getId()).orElse(null);
                    eduEntity.setEducationDegree(educationDegree);
                }

                eduEntity.setStartDate(eduHistory.getStartDate());
                eduEntity.setEndDate(eduHistory.getEndDate());
                eduEntity.setStatus(eduHistory.getStatus());
                eduEntity.setSchoolName(eduHistory.getSchoolName());
                eduEntity.setDescription(eduHistory.getDescription());

                educationHistories.add(eduEntity);
            }
        }

        if (entity.getCandidateEducationHistory() == null) {
            entity.setCandidateEducationHistory(new HashSet<>());
        }
        entity.getCandidateEducationHistory().clear();
        entity.getCandidateEducationHistory().addAll(educationHistories);
    }
}
