package com.globits.hr.service.impl;

import java.util.*;

import com.globits.hr.domain.*;
import com.globits.hr.dto.PositionDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchPositionDto;
import com.globits.hr.repository.*;
import com.globits.security.dto.UserDto;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.globits.core.domain.Country;
import com.globits.core.repository.CountryRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.dto.StaffEducationHistoryDto;
import com.globits.hr.service.StaffEducationHistoryService;

@Transactional
@Service
public class StaffEducationHistoryServiceImpl extends GenericServiceImpl<StaffEducationHistory, UUID>
        implements StaffEducationHistoryService {
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private StaffEducationHistoryRepository educationHistoryRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private HrEducationTypeRepository hrEducationTypeRepository;
    @Autowired
    private HrSpecialityRepository hrSpecialityRepository;
    @Autowired
    private EducationDegreeRepository educationDegreeRepository;
    @Autowired
    private EducationalInstitutionRepository educationalInstitutionRepository;
    @Autowired
    private EducationDegreeRepository edegreeRepository;

    @Override
    public Page<StaffEducationHistoryDto> getPages(int pageIndex, int pageSize) {
        if (pageIndex > 1) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return this.educationHistoryRepository.getPages(pageable);
    }

    @Override
    public Page<StaffEducationHistoryDto> pagingEducationHistory(SearchDto dto) {
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
        String orderBy = " ORDER BY entity.createDate desc ";

        String sqlCount = "select count(distinct entity.id) from Position as entity ";
        String sql = "select distinct new com.globits.hr.dto.StaffEducationHistoryDto(entity) from StaffEducationHistory as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text " +
                    "OR entity.description LIKE :text " +
                    "OR entity.staff.displayName LIKE :text " +
                    "OR entity.title.name LIKE :text ) ";
        }

        if (dto.getStaffId() != null) {
            whereClause += " and (entity.staff.id = :staffId) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, PositionDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        if (dto.getStaffId() != null) {
            query.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }

        long count = (long) qCount.getSingleResult();

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<StaffEducationHistoryDto> entities = query.getResultList();
        Page<StaffEducationHistoryDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }


    @Override
    public List<StaffEducationHistoryDto> getAll(UUID id) {
        // TODO Auto-generated method stub
        return this.educationHistoryRepository.getAll(id);
    }

    @Override
    public StaffEducationHistoryDto getEducationById(UUID id) {
        return this.educationHistoryRepository.getEducationById(id);
    }

    @Override
    public StaffEducationHistoryDto saveEducation(StaffEducationHistoryDto educationDto, UUID id) {
        if (educationDto == null) return null;
        Staff staff = null;
        if (educationDto != null && educationDto.getStaff() != null && educationDto.getStaff().getId() != null) {
            staff = this.staffRepository.getOne(educationDto.getStaff().getId());
        }
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User modifiedUser;
//        LocalDateTime currentDate = LocalDateTime.now();
//        String currentUserName = "Unknown User";
//        if (authentication != null) {
//            modifiedUser = (User) authentication.getPrincipal();
//            currentUserName = modifiedUser.getUsername();
//        }
        StaffEducationHistory educationHistory = new StaffEducationHistory();
        if (id != null) {
            Optional<StaffEducationHistory> optional = educationHistoryRepository.findById(id);
            if (optional.isPresent()) {
                educationHistory = optional.get();
            }
        } else {
            if (educationDto != null && educationDto.getId() != null) {
                Optional<StaffEducationHistory> optional = educationHistoryRepository.findById(educationDto.getId());
                if (optional.isPresent()) {
                    educationHistory = optional.get();
                }
            }
        }
        if (educationDto.getSpeciality() != null && educationDto.getSpeciality().getId() != null) {
            HrSpeciality speciality = hrSpecialityRepository.findById(educationDto.getSpeciality().getId()).orElse(null);
            educationHistory.setSpeciality(speciality);
        }
        if (educationDto.getMajor() != null && educationDto.getMajor().getId() != null) {
            HrSpeciality major = hrSpecialityRepository.findById(educationDto.getMajor().getId()).orElse(null);
            educationHistory.setMajor(major);
        }
        if (educationDto.getEducationType() != null && educationDto.getEducationType().getId() != null) {
            HrEducationType educationType = hrEducationTypeRepository.findById(educationDto.getEducationType().getId()).orElse(null);
            educationHistory.setEducationType(educationType);
        }

        if (educationDto.getEducationalInstitution() != null && educationDto.getEducationalInstitution().getId() != null) {
            EducationalInstitution educationalInstitution = educationalInstitutionRepository.findById(educationDto.getEducationalInstitution().getId()).orElse(null);
            educationHistory.setEducationalInstitution(educationalInstitution);
        }

        if (educationDto.getEducationDegree() != null && educationDto.getEducationDegree().getId() != null) {
            EducationDegree educationDegree = educationDegreeRepository.findById(educationDto.getEducationDegree().getId()).orElse(null);
            educationHistory.setEducationDegree(educationDegree);
        }
        if (educationDto.getCountry() != null && educationDto.getCountry().getId() != null) {
            Country country = countryRepository.findById(educationDto.getCountry().getId()).orElse(null);
            educationHistory.setCountry(country);
        }
        educationHistory.setStartDate(educationDto.getStartDate());
        educationHistory.setEndDate(educationDto.getEndDate());
        educationHistory.setSchoolName(educationDto.getSchoolName());
        educationHistory.setDescription(educationDto.getDescription());
        educationHistory.setStatus(educationDto.getStatus());
        educationHistory.setIsCurrent(educationDto.getIsCurrent());
        educationHistory.setPlace(educationDto.getPlace());
        educationHistory.setDescription(educationDto.getDescription());
        educationHistory.setFundingSource(educationDto.getFundingSource());
        educationHistory.setIsCountedForSeniority(educationDto.getIsCountedForSeniority());
        educationHistory.setIsConfirmation(educationDto.getIsConfirmation());
        educationHistory.setNote(educationDto.getNote());
        educationHistory.setBasis(educationDto.getBasis());
        educationHistory.setDecisionDate(educationDto.getDecisionDate());
        educationHistory.setDecisionCode(educationDto.getDecisionCode());
        educationHistory.setReturnDate(educationDto.getReturnDate());
        educationHistory.setNotFinish(educationDto.getNotFinish());
        educationHistory.setActualGraduationYear(educationDto.getActualGraduationYear());
        educationHistory.setFinishDateByDecision(educationDto.getFinishDateByDecision());
        educationHistory.setIsExtended(educationDto.getIsExtended());
        educationHistory.setExtendDecisionDate(educationDto.getExtendDecisionDate());
        educationHistory.setExtendDecisionCode(educationDto.getExtendDecisionCode());
        educationHistory.setExtendDateByDecision(educationDto.getExtendDateByDecision());

        educationHistory.setStaff(staff);

        StaffEducationHistory response = this.educationHistoryRepository.save(educationHistory);

        return new StaffEducationHistoryDto(response);
    }

    @Override
    public Boolean removeLists(List<UUID> ids) {
        if (ids != null && ids.size() > 0) {
            for (UUID id : ids) {
                this.educationHistoryRepository.deleteById(id);
            }
        }
        return false;

    }

    @Override
    public StaffEducationHistoryDto removeEducation(UUID id) {
        StaffEducationHistoryDto educationDto = new StaffEducationHistoryDto(this.educationHistoryRepository.getOne(id));
        if (educationHistoryRepository != null) {
            this.educationHistoryRepository.deleteById(id);
        }
        return educationDto;
    }

    @Override
    public StaffEducationHistoryDto saveImportStaffEducationHistory(StaffEducationHistoryDto dto) {
        if (dto != null) {
            StaffEducationHistory staffEducationHistory = new StaffEducationHistory();
            Staff entity = null;
            if (dto.getStaffCode() != null) {
                List<Staff> listStaff = staffRepository.getByCode(dto.getStaffCode());
                if (listStaff != null && listStaff.size() > 0) {
                    entity = listStaff.get(0);
                    staffEducationHistory.setStaff(entity);
                }
            }
            if (entity == null) {
                return null;
            }
            if (dto.getCountryCode() != null && StringUtils.hasText(dto.getCountryCode())) {
                Country country = countryRepository.findByCode(dto.getCountryCode());
                if (country != null) {
                    staffEducationHistory.setCountry(country);
                }
            }
            if (dto.getSpecialityCode() != null && StringUtils.hasText(dto.getSpecialityCode())) {
                List<HrSpeciality> listHrSpeciality = hrSpecialityRepository.findByCode(dto.getSpecialityCode());
                if (listHrSpeciality != null && listHrSpeciality.size() > 0) {
                    staffEducationHistory.setSpeciality(listHrSpeciality.get(0));
                }
            }
            if (dto.getEducationTypeCode() != null && StringUtils.hasText(dto.getEducationTypeCode())) {
                List<HrEducationType> listData = hrEducationTypeRepository.findByCode(dto.getEducationTypeCode());
                if (listData != null && listData.size() > 0) {
                    staffEducationHistory.setEducationType(listData.get(0));
                }
            }
            if (dto.getEducationDegreeCode() != null && StringUtils.hasText(dto.getEducationDegreeCode())) {
                List<EducationDegree> listData = educationDegreeRepository.findByCode(dto.getEducationDegreeCode());
                if (listData != null && listData.size() > 0) {
                    staffEducationHistory.setEducationDegree(listData.get(0));
                }
            }
            if (dto.getMajorCode() != null && StringUtils.hasText(dto.getMajorCode())) {
                List<HrSpeciality> listData = hrSpecialityRepository.findByCode(dto.getMajorCode());
                if (listData != null && listData.size() > 0) {
                    staffEducationHistory.setMajor(listData.get(0));
                }
            }
            staffEducationHistory.setStartDate(dto.getStartDate());
            staffEducationHistory.setEndDate(dto.getEndDate());
            staffEducationHistory.setSchoolName(dto.getSchoolName());
            staffEducationHistory.setPlace(dto.getPlace());
            staffEducationHistory.setIsCurrent(dto.getIsCurrent());
            staffEducationHistory.setFundingSource(dto.getFundingSource());
            staffEducationHistory.setDecisionCode(dto.getDecisionCode());
            staffEducationHistory.setNote(dto.getNote());
            staffEducationHistory.setIsConfirmation(dto.getIsConfirmation());
            staffEducationHistory.setIsCountedForSeniority(dto.getIsCountedForSeniority());
            staffEducationHistory.setBasis(dto.getBasis());
            staffEducationHistory.setDecisionDate(dto.getDecisionDate());
            staffEducationHistory.setReturnDate(dto.getReturnDate());
            staffEducationHistory.setNotFinish(dto.getNotFinish());
            staffEducationHistory.setFinishDateByDecision(dto.getFinishDateByDecision());
            staffEducationHistory.setExtendDateByDecision(dto.getExtendDateByDecision());
            staffEducationHistory.setExtendDecisionDate(dto.getExtendDecisionDate());
            staffEducationHistory.setIsExtended(dto.getIsExtended());
            staffEducationHistory.setExtendDecisionCode(dto.getExtendDecisionCode());
            staffEducationHistory = educationHistoryRepository.save(staffEducationHistory);
            return new StaffEducationHistoryDto(staffEducationHistory);
        }
        return null;
    }
}
