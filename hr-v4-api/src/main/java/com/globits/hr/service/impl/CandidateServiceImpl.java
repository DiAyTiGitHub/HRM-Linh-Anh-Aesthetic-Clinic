package com.globits.hr.service.impl;

import com.globits.core.domain.*;
import com.globits.core.dto.AdministrativeUnitDto;
import com.globits.core.dto.PersonAddressDto;
import com.globits.core.repository.*;
import com.globits.core.service.CountryService;
import com.globits.core.service.EthnicsService;
import com.globits.core.service.ReligionService;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.HrConstants.PreScreenStatus;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.importExcel.CandidateImport;
import com.globits.hr.dto.search.*;
import com.globits.hr.dto.staff.StaffSocialInsuranceDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.*;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.hr.utils.ExcelUtils;
import com.globits.salary.repository.SalaryIncrementTypeRepository;
import com.globits.security.domain.User;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.security.repository.RoleRepository;
import com.globits.security.repository.UserRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.Query;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.formula.functions.Count;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
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

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.globits.hr.HrConstants.HR_ASSIGNMENT;
import static com.globits.hr.HrConstants.ROLE_ADMIN;

@Service
public class CandidateServiceImpl extends GenericServiceImpl<Candidate, UUID> implements CandidateService {
    private static final Logger logger = LoggerFactory.getLogger(CandidateDto.class);

    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private RecruitmentRepository recruitmentRepository;
    @Autowired
    private PersonAddressRepository personAddressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ReligionRepository religionRepository;
    @Autowired
    private StaffWorkingHistoryRepository staffWorkingHistoryRepository;
    @Autowired
    private EthnicsRepository ethnicsRepository;
    @Autowired
    private AdministrativeUnitRepository administrativeUnitRepository;
    @Autowired
    private HrOrganizationRepository hrOrganizationRepository;
    @Autowired
    private HRDepartmentRepository hRDepartmentRepository;
    @Autowired
    private PositionTitleRepository positionTitleRepository;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private FileDescriptionRepository fileDescriptionRepository;
    @Autowired
    private PersonCertificateRepository personCertificateRepository;
    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private CandidateEducationHistoryService candidateEducationHistoryService;
    @Autowired
    private CandidateRecruitmentRoundService candidateRecruitmentRoundService;
    @Autowired
    private CandidateWorkingExperienceService candidateWorkingExperienceService;
    @Autowired
    private CandidateAttachmentService candidateAttachmentService;
    @Autowired
    private RecruitmentPlanRepository recruitmentPlanRepository;

    @Autowired
    private RecruitmentRoundRepository recruitmentRoundRepository;

    @Autowired
    private CandidateRecruitmentRoundRepository candidateRecruitmentRoundRepository;

    @Autowired
    private StaffServiceV3 staffServiceV3;

    @Autowired
    private CommonKeyCodeRepository commonKeyCodeRepository;

    @Autowired
    private UserExtService userExtService;

    @Autowired
    private EmployeeStatusRepository employeeStatusRepository;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private PositionRepository positionRepository;

    @Override
    public CandidateDto getById(UUID id) {
        if (id == null)
            return null;
        Candidate entity = candidateRepository.findById(id).orElse(null);

        if (entity == null) {
            return null;
        }
        CandidateDto result = new CandidateDto(entity, true);
        return result;
    }

    private String normalize(String strName) {
        strName = strName.trim();
        while (strName.contains("  ")) {
            strName = strName.replace("  ", " ");
        }
        String[] arrStr = strName.split(" ");
        StringBuilder kq = new StringBuilder();
        for (String s : arrStr) {
            kq.append(s.substring(0, 1).toUpperCase());
            kq.append(s.substring(1).toLowerCase());
            kq.append(" ");
        }
        return kq.toString().trim();
    }

    @Override
    public CandidateDto findByCode(String code) {
        List<Candidate> entities = candidateRepository.findByCode(code);
        if (entities == null || entities.size() == 0)
            return null;
        return new CandidateDto(entities.get(0));
    }

    @Override
    public Boolean isValidCode(CandidateDto dto) {
        if (dto == null)
            return false;

        // ID of candidate is null => Create new candidate
        // => Assure that there's no other candidates using this code of new candidate
        // if there was any candidate using new candidate's code, then this new code is
        // invalid => return False
        // else return true
        if (dto.getId() == null) {
            List<Candidate> entities = candidateRepository.findByCode(dto.getCandidateCode());
            if (entities == null || entities.size() == 0) {
                return true;
            }
            return false;

        }
        // ID of candidate is NOT null => Candidate is modified
        // => Assure that the modified code is not same to OTHER any candidates' code
        // if there was any candidate using new candidate's code, then this new code is
        // invalid => return False
        // else return true
        else {
            List<Candidate> entities = candidateRepository.findByCode(dto.getCandidateCode());
            if (entities == null || entities.size() == 0) {
                return true;
            }
            for (Candidate entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public CandidateDto saveCandidate(CandidateDto dto) {
        // xét null cho dto
        if (dto == null) {
            return null;
        }

        Candidate entity = null;
        if (dto.getId() != null) {
            entity = candidateRepository.findById(dto.getId()).orElse(null);
            if (entity == null)
                return null;
        } else {
            entity = new Candidate();
        }

        // set các trường trong class entity
        if (entity.getCandidateCode() == null) {
            String code = autoGenerateCode(HrConstants.CodePrefix.UNG_VIEN.getConfigKey());
            dto.setCandidateCode(code);
        }
        entity.setCandidateCode(dto.getCandidateCode());
        entity.setFirstName(normalize(dto.getFirstName()));
        entity.setLastName(normalize(dto.getLastName()));
        entity.setBirthDate(dto.getBirthDate());
        entity.setBirthPlace(dto.getBirthPlace());
        entity.setGender(dto.getGender());
        entity.setPhoto(dto.getPhoto());
        entity.setDisplayName(normalize(dto.getDisplayName()));
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setMaritalStatus(dto.getMaritalStatus());
        entity.setImagePath(dto.getImagePath());
        entity.setIdNumber(dto.getIdNumber());
        entity.setIdNumberIssueBy(dto.getIdNumberIssueBy());
        entity.setIdNumberIssueDate(dto.getIdNumberIssueDate());
        entity.setPermanentResidence(dto.getPermanentResidence());
        entity.setCurrentResidence(dto.getCurrentResidence());
        entity.setEmail(dto.getEmail());
        entity.setStatus(dto.getStatus());
        entity.setIsEnterdCandidateProfile(dto.getIsEnterdCandidateProfile());
        entity.setPersonalIdentificationNumber(dto.getPersonalIdentificationNumber());
        entity.setPersonalIdentificationIssueDate(dto.getPersonalIdentificationIssueDate());
        entity.setPersonalIdentificationIssuePlace(dto.getPersonalIdentificationIssuePlace());
        if (dto.getAdministrativeUnit() != null) {
            AdministrativeUnit ward = administrativeUnitRepository.findById(dto.getAdministrativeUnit().getId())
                    .orElse(null);
            if (ward == null)
                return null;
            entity.setAdministrativeUnit(ward);
        } else {
            entity.setAdministrativeUnit(null);
        }

        if (dto.getNationality() != null) {
            Country nationality = countryRepository.findById(dto.getNationality().getId()).orElse(null);
            if (nationality == null)
                return null;
            entity.setNationality(nationality);
        } else {
            entity.setNationality(null);
        }

        if (dto.getEthnics() != null) {
            Ethnics ethnics = ethnicsRepository.findById(dto.getEthnics().getId()).orElse(null);
            if (ethnics == null)
                return null;
            entity.setEthnics(ethnics);
        } else {
            entity.setEthnics(null);
        }

        if (dto.getNativeVillage() != null) {
            AdministrativeUnit nativeVillage = administrativeUnitRepository.findById(dto.getNativeVillage().getId())
                    .orElse(null);
            if (nativeVillage == null)
                return null;
            entity.setNativeVillage(nativeVillage);
        } else {
            entity.setNativeVillage(null);
        }

        if (dto.getReligion() != null) {
            Religion religion = religionRepository.findById(dto.getReligion().getId()).orElse(null);
            if (religion == null)
                return null;
            entity.setReligion(religion);
        } else {
            entity.setReligion(null);
        }

        // tab 2
        if (dto.getRecruitment() != null) {
            Recruitment recruitment = recruitmentRepository.findById(dto.getRecruitment().getId()).orElse(null);
            if (recruitment == null)
                return null;
            entity.setRecruitment(recruitment);
        } else {
            entity.setRecruitment(null);
        }

        if (dto.getPositionTitle() != null) {
            PositionTitle positionTitle = positionTitleRepository.findById(dto.getPositionTitle().getId()).orElse(null);
            if (positionTitle == null)
                return null;
            entity.setPositionTitle(positionTitle);
        } else {
            entity.setPositionTitle(null);
        }
        if (dto.getStaff() != null) {
            Staff staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
            if (staff == null)
                return null;
            entity.setStaff(staff);
        } else {
            entity.setStaff(null);
        }

        entity.setSubmissionDate(dto.getSubmissionDate());
        entity.setInterviewDate(dto.getInterviewDate());
        entity.setDesiredPay(dto.getDesiredPay());
        entity.setPossibleWorkingDate(dto.getPossibleWorkingDate());
        entity.setOnboardDate(dto.getOnboardDate());
        if (dto.getOrganization() != null && dto.getOrganization().getId() != null) {
            HrOrganization organization = hrOrganizationRepository.findById(dto.getOrganization().getId()).orElse(null);
            entity.setOrganization(organization);
        } else {
            entity.setOrganization(null);
        }
        if (dto.getDepartment() != null && dto.getDepartment().getId() != null) {
            HRDepartment department = hRDepartmentRepository.findById(dto.getDepartment().getId()).orElse(null);
            entity.setDepartment(department);
        } else {
            entity.setDepartment(null);
        }

        if (dto.getIntroducer() != null && dto.getIntroducer().getId() != null) {
            Staff introducer = staffRepository.findById(dto.getIntroducer().getId()).orElse(null);
            entity.setIntroducer(introducer);
        } else {
            entity.setIntroducer(null);
        }
        if (dto.getRecruitmentPlan() != null) {
            RecruitmentPlan recruitmentPlan = recruitmentPlanRepository.findById(dto.getRecruitmentPlan().getId()).orElse(null);
            entity.setRecruitmentPlan(recruitmentPlan);
            if (recruitmentPlan != null && recruitmentPlan.getRecruitmentRequest() != null) {
                HrOrganization organization = recruitmentPlan.getRecruitmentRequest().getHrOrganization();
                if (entity.getOrganization() == null) {
                    entity.setOrganization(organization);
                }
                if (entity.getDepartment() == null) {
                    entity.setDepartment(recruitmentPlan.getRecruitmentRequest().getHrDepartment());
                }
                if (entity.getPositionTitle() == null) {
                    entity.setPositionTitle(Objects.requireNonNull(recruitmentPlan.getRecruitmentRequest().getRecruitmentRequestItems().stream().findFirst().orElse(null)).getPositionTitle());
                }
            }
        } else {
            entity.setRecruitmentPlan(null);
        }
        // tab 3 - Quá trình đào tạo
        candidateEducationHistoryService.handleSetEducationHistoryList(dto, entity);
        candidateRecruitmentRoundService.handleSetCandidateRecruitmentRoundList(dto, entity);

        // tab 4 - Chứng chỉ
        Set<PersonCertificate> candidateCertificates = new HashSet<>();
        if (dto.getCandidateCertificates() != null && !dto.getCandidateCertificates().isEmpty()) {
            for (PersonCertificateDto itemDto : dto.getCandidateCertificates()) {
                if (itemDto == null)
                    continue;
                PersonCertificate item = null;
                if (itemDto.getId() != null) {
                    item = personCertificateRepository.findById(itemDto.getId()).orElse(null);
                }
                if (item == null)
                    item = new PersonCertificate();

                if (itemDto.getCertificate() != null && itemDto.getCertificate().getId() != null) {
                    Certificate certificate = certificateRepository.findById(itemDto.getCertificate().getId())
                            .orElse(null);
                    item.setCertificate(certificate);
                }
                item.setLevel(itemDto.getLevel());
                item.setIssueDate(itemDto.getIssueDate());
                item.setName(itemDto.getName());
                item.setPerson(entity);

                if (item.getName() == null && item.getLevel() == null && item.getCertificate() == null
                        && item.getIssueDate() == null)
                    continue;

                candidateCertificates.add(item);
            }
        }

        if (entity.getCandidateCertificates() == null)
            entity.setCandidateCertificates(candidateCertificates);
        else {
            entity.getCandidateCertificates().clear();
            entity.getCandidateCertificates().addAll(candidateCertificates);
        }

        // tab 5 - Kinh nghiệm làm việc
        candidateWorkingExperienceService.handleSetWorkingExperienceList(dto, entity);

        // tab 6 - Tệp đính kèm
        candidateAttachmentService.handleSetAttachmentList(dto, entity);

        // Các trường khác
        if (dto.getApprovalStatus() == null) {
            // default approval status is NOT_APPROVED_YET
            entity.setApprovalStatus(HrConstants.CandidateApprovalStatus.NOT_APPROVED_YET.getValue());
        } else {
            entity.setApprovalStatus(dto.getApprovalStatus());
        }

        if (dto.getStatus() == null) {
            // default approval status is NOT_APPROVED_YET
            entity.setStatus(HrConstants.CandidateStatus.NOT_APPROVED_YET.getValue());
        } else {
            entity.setStatus(dto.getStatus());
        }
        entity.setExamStatus(dto.getExamStatus());
        entity.setReceptionStatus(dto.getReceptionStatus());

        entity.setOnboardStatus(dto.getOnboardStatus());
        entity.setPreScreenStatus(dto.getPreScreenStatus());
        entity.setProbationIncome(dto.getProbationIncome());
        entity.setBasicIncome(dto.getBasicIncome());
        entity.setPositionBonus(dto.getPositionBonus());
        entity.setAllowance(dto.getAllowance());
        entity.setOtherBenefit(dto.getOtherBenefit());
        entity = candidateRepository.save(entity);

        CandidateDto result = new CandidateDto(entity, true);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CandidateDto deleteCandidate(UUID id) {
        if (id == null) {
            throw new RuntimeException("Invalid argument!");
        }
        Candidate entity = candidateRepository.findById(id).orElse(null);

        if (entity == null || entity.getId() == null)
            throw new RuntimeException("Invalid argument!");
        entity.setVoided(Boolean.TRUE);
        candidateRepository.save(entity);
        return new CandidateDto(entity);
    }

    @Override
    @Transactional
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null)
            return false;
        for (UUID applicantId : ids) {
            this.deleteCandidate(applicantId);
        }
        return true;
    }

    private void formalizeSearchObject(SearchCandidateDto dto) {
        if (dto.getFromDate() != null) {
            dto.setFromDate(DateTimeUtil.getStartOfDay(dto.getFromDate()));
        }
        if (dto.getToDate() != null) {
            dto.setToDate(DateTimeUtil.getEndOfDay(dto.getToDate()));
        }
        if (dto.getSubmissionDateFrom() != null) {
            dto.setSubmissionDateFrom(DateTimeUtil.getStartOfDay(dto.getSubmissionDateFrom()));
        }
        if (dto.getSubmissionDateTo() != null) {
            dto.setSubmissionDateTo(DateTimeUtil.getEndOfDay(dto.getSubmissionDateTo()));
        }
        if (dto.getInterviewDateFrom() != null) {
            dto.setInterviewDateFrom(DateTimeUtil.getStartOfDay(dto.getInterviewDateFrom()));
        }
        if (dto.getInterviewDateTo() != null) {
            dto.setInterviewDateTo(DateTimeUtil.getEndOfDay(dto.getInterviewDateTo()));
        }
    }

    @Override
    public Page<CandidateDto> pagingCandidates(SearchCandidateDto dto) {
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

        UserExtRoleDto userRoleDto = userExtService.getCurrentRoleUser();
        List<UUID> staffUsers = new ArrayList<UUID>();
        if (userRoleDto != null) {
            if (userRoleDto.isRoleAdmin() || userRoleDto.isRoleHrManager() || userRoleDto.isRoleSuperHr()) {
                //xem duoc all danh sach
            } else if (userRoleDto.getStaffId() != null) {
                staffUsers.add(userRoleDto.getStaffId());
            }
        }

        String sqlCount = "select count(entity.id) from Candidate as entity ";
        String sql = "select new com.globits.hr.dto.CandidateDto(entity) from Candidate as entity ";
        String whereClause = " where (1=1) AND (entity.voided = false OR entity.voided is NULL) ";
        String orderBy = " ORDER BY entity.modifyDate desc ";

        // nếu có tìm kiếm theo vòng tuyn dụng hoặc trạng thái tuyển dụng thì joind vơới CandidateRecruitmenRound
        if (dto.getRecruitmentRoundIds() != null && !dto.getRecruitmentRoundIds().isEmpty() ||
                dto.getRecruitmentRoundStatus() != null) {
            sql += " join CandidateRecruitmentRound cr on cr.candidate.id = entity.id ";
            sqlCount += " join CandidateRecruitmentRound cr on cr.candidate.id = entity.id ";
        }
        if (dto.getRecruitmentRoundIds() != null && !dto.getRecruitmentRoundIds().isEmpty()) {
            whereClause += " and cr.recruitmentRound.id in :roundIds ";
        }
        if (dto.getRecruitmentRoundStatus() != null) {
            whereClause += " and cr.status in :recruitmentRoundStatus ";
        }
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.displayName LIKE :text OR entity.candidateCode LIKE :text ) ";
        }
        if (dto.getStatus() != null) {
            whereClause += " and (entity.status = :status) ";
        }
        if (dto.getApprovalStatus() != null) {
            whereClause += " and (entity.approvalStatus = :approvalStatus) ";
        }

        if (dto.getDepartmentId() != null) {
            whereClause += " and (entity.recruitment.department.id = :departmentId) ";
        }
        if (dto.getOrganizationId() != null) {
            whereClause += " and (entity.recruitment.organization.id = :organizationId) ";
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            whereClause += " and (entity.positionTitle.id = :positionTitleId) ";
        }
        if (dto.getSubmissionDateFrom() != null) {
            whereClause += " and (entity.submissionDate >= :submissionDateFrom) ";
        }
        if (dto.getSubmissionDateTo() != null) {
            whereClause += " and (entity.submissionDate <= :submissionDateTo) ";
        }
        if (dto.getSubmissionDate() != null) {
            whereClause += " and (Date(entity.submissionDate) = :submissionDate) ";
        }
        if (dto.getRecruitmentPlanId() != null) {
            whereClause += " and (entity.recruitmentPlan.id = :planId) ";
        }
        if (dto.getFindNullPlain() != null) {
            if (dto.getFindNullPlain()) {
                whereClause += " and (entity.recruitmentPlan  IS NULL) ";
            }
        }
        if (staffUsers != null && staffUsers.size() > 0) {
            whereClause += " AND ((entity.recruitmentPlan.recruitmentRequest.personInCharge.id IN (:staffUserIds)) " +
                    "OR (entity.recruitmentPlan.personApproveCV.id IN (:staffUserIds)) " +
                    "OR EXISTS (SELECT 1 FROM RecruitmentRound round JOIN round.participatingPeople people WHERE round.recruitmentPlan.id = entity.recruitmentPlan.id AND people.id IN (:staffUserIds)) " +
                    ") ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, CandidateDto.class);
        Query qCount = manager.createQuery(sqlCount);
        if (dto.getRecruitmentRoundIds() != null && !dto.getRecruitmentRoundIds().isEmpty()) {
            query.setParameter("roundIds", dto.getRecruitmentRoundIds());
            qCount.setParameter("roundIds", dto.getRecruitmentRoundIds());
        }
        if (dto.getRecruitmentRoundStatus() != null) {
            query.setParameter("recruitmentRoundStatus", dto.getRecruitmentRoundStatus());
            qCount.setParameter("recruitmentRoundStatus", dto.getRecruitmentRoundStatus());
        }
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getApprovalStatus() != null) {
            query.setParameter("approvalStatus", dto.getApprovalStatus());
            qCount.setParameter("approvalStatus", dto.getApprovalStatus());
        }

        if (dto.getStatus() != null) {
            query.setParameter("status", dto.getStatus());
            qCount.setParameter("status", dto.getStatus());
        }
        if (dto.getRecruitment() != null && dto.getRecruitment().getId() != null) {
            query.setParameter("recruitmentId", dto.getRecruitment().getId());
            qCount.setParameter("recruitmentId", dto.getRecruitment().getId());
        } else if (dto.getRecruitmentId() != null) {
            query.setParameter("recruitmentId", dto.getRecruitmentId());
            qCount.setParameter("recruitmentId", dto.getRecruitmentId());
        }
        if (dto.getDepartmentId() != null) {
            query.setParameter("departmentId", dto.getDepartmentId());
            qCount.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getOrganizationId() != null) {
            query.setParameter("organizationId", dto.getOrganizationId());
            qCount.setParameter("organizationId", dto.getOrganizationId());
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            query.setParameter("positionTitleId", dto.getPositionTitle().getId());
            qCount.setParameter("positionTitleId", dto.getPositionTitle().getId());
        }
        if (dto.getSubmissionDateFrom() != null) {
            query.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
            qCount.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
        }
        if (dto.getSubmissionDateTo() != null) {
            query.setParameter("submissionDateTo", dto.getSubmissionDateTo());
            qCount.setParameter("submissionDateTo", dto.getSubmissionDateTo());
        }
        if (dto.getSubmissionDate() != null) {
            query.setParameter("submissionDate", dto.getSubmissionDate());
            qCount.setParameter("submissionDate", dto.getSubmissionDate());
        }
        if (dto.getRecruitmentPlanId() != null) {
            query.setParameter("planId", dto.getRecruitmentPlanId());
            qCount.setParameter("planId", dto.getRecruitmentPlanId());
        }
        if (staffUsers != null && staffUsers.size() > 0) {
            query.setParameter("staffUserIds", staffUsers);
            qCount.setParameter("staffUserIds", staffUsers);
        }

        long count = (long) qCount.getSingleResult();
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        List<CandidateDto> content = query.getResultList();
        // Nếu cần set currentRound
        List<UUID> candidateIds = content.stream()
                .map(CandidateDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Lấy thông tin vòng hiện tạo của ứng viên
        List<CandidateRecruitmentRound> currentRounds = candidateRecruitmentRoundRepository.getCurrentRoundsOfCandidates(candidateIds);

        Map<UUID, CandidateRecruitmentRound> currentCandidateRoundMap = currentRounds.stream()
                .filter(c -> c.getRecruitmentRound() != null)
                .collect(Collectors.toMap(
                        c -> c.getCandidate().getId(),
                        c -> c
                ));

        for (CandidateDto cDto : content) {
            CandidateRecruitmentRound cRoundDto = currentCandidateRoundMap.get(cDto.getId());
            if (cRoundDto != null) {
                cDto.setCurrentCandidateRound(new CandidateRecruitmentRoundDto((cRoundDto)));
            }
        }

        // result
        return new PageImpl<>(content, pageable, count);
    }

    // danh sach ung vien duoc tham gia buoi phong van/kiem tra
    @Override
    public Page<CandidateDto> pagingExamCandidates(SearchCandidateDto dto) {
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

        String whereClause = " where (1=1) and (entity.approvalStatus = "
                + HrConstants.CandidateApprovalStatus.APPROVED.getValue() + ") ";
        String orderBy = " ORDER BY entity.modifyDate desc, entity.interviewDate ";

        String sqlCount = "select count(distinct entity.id) from Candidate as entity ";
        String sql = "select distinct new com.globits.hr.dto.CandidateDto(entity) from Candidate as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.displayName LIKE :text OR entity.candidateCode LIKE :text ) ";
        }
        if (dto.getExamStatus() != null) {
            whereClause += " and (entity.examStatus = :examStatus) ";
        }
        if (dto.getRecruitment() != null && dto.getRecruitment().getId() != null) {
            whereClause += " and (entity.recruitment.id = :recruitmentId) ";
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            whereClause += " and (entity.positionTitle.id = :positionTitleId) ";
        }
        if (dto.getSubmissionDateFrom() != null) {
            whereClause += " and (entity.submissionDate >= :submissionDateFrom) ";
        }
        if (dto.getSubmissionDateTo() != null) {
            whereClause += " and (entity.submissionDate <= :submissionDateTo) ";
        }
        if (dto.getInterviewDateFrom() != null) {
            whereClause += " and (entity.interviewDate >= :interviewDateFrom) ";
        }
        if (dto.getInterviewDateTo() != null) {
            whereClause += " and (entity.interviewDate <= :interviewDateTo) ";
        }
        if (dto.getSubmissionDate() != null) {
            whereClause += " and (Date(entity.submissionDate) = :submissionDate) ";
        }
        if (dto.getInterviewDate() != null) {
            whereClause += " and (Date(entity.interviewDate) = :interviewDate) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, CandidateDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getExamStatus() != null) {
            query.setParameter("examStatus", dto.getExamStatus());
            qCount.setParameter("examStatus", dto.getExamStatus());
        }
        if (dto.getRecruitment() != null && dto.getRecruitment().getId() != null) {
            query.setParameter("recruitmentId", dto.getRecruitment().getId());
            qCount.setParameter("recruitmentId", dto.getRecruitment().getId());
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            query.setParameter("positionTitleId", dto.getPositionTitle().getId());
            qCount.setParameter("positionTitleId", dto.getPositionTitle().getId());
        }
        if (dto.getSubmissionDateFrom() != null) {
            query.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
            qCount.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
        }
        if (dto.getSubmissionDateTo() != null) {
            query.setParameter("submissionDateTo", dto.getSubmissionDateTo());
            qCount.setParameter("submissionDateTo", dto.getSubmissionDateTo());
        }
        if (dto.getInterviewDateFrom() != null) {
            query.setParameter("interviewDateFrom", dto.getInterviewDateFrom());
            qCount.setParameter("interviewDateFrom", dto.getInterviewDateFrom());
        }
        if (dto.getInterviewDateTo() != null) {
            query.setParameter("interviewDateTo", dto.getInterviewDateTo());
            qCount.setParameter("interviewDateTo", dto.getInterviewDateTo());
        }
        if (dto.getSubmissionDate() != null) {
            query.setParameter("submissionDate", dto.getSubmissionDate());
            qCount.setParameter("submissionDate", dto.getSubmissionDate());
        }
        if (dto.getInterviewDate() != null) {
            query.setParameter("interviewDate", dto.getInterviewDate());
            qCount.setParameter("interviewDate", dto.getInterviewDate());
        }

        long count = (long) qCount.getSingleResult();
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<CandidateDto> entities = query.getResultList();
        Page<CandidateDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    // danh sach ung vien da PASS bai kiem tra/phong van
    @Override
    public Page<CandidateDto> pagingPassedCandidates(SearchCandidateDto dto) {
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

        String whereClause = " where (1=1) " + "and (entity.approvalStatus = "
                + HrConstants.CandidateApprovalStatus.APPROVED.getValue() + ") " + "and (entity.examStatus = "
                + HrConstants.CandidateExamStatus.PASSED.getValue() + ") ";
        String orderBy = " ORDER BY entity.modifyDate desc ";

        String sqlCount = "select count(distinct entity.id) from Candidate as entity ";
        String sql = "select distinct new com.globits.hr.dto.CandidateDto(entity) from Candidate as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.displayName LIKE :text OR entity.candidateCode LIKE :text ) ";
        }
        if (dto.getReceptionStatus() != null) {
            whereClause += " and (entity.receptionStatus = :receptionStatus) ";
        }
        if (dto.getRecruitment() != null && dto.getRecruitment().getId() != null) {
            whereClause += " and (entity.recruitment.id = :recruitmentId) ";
        }
        if (dto.getOrganizationId() != null) {
            whereClause += " and (entity.recruitment.organization.id = :organizationId) ";
        }
        if (dto.getDepartmentId() != null) {
            whereClause += " and (entity.recruitment.department.id = :departmentId) ";
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            whereClause += " and (entity.positionTitle.id = :positionTitleId) ";
        }
        if (dto.getSubmissionDateFrom() != null) {
            whereClause += " and (entity.submissionDate >= :submissionDateFrom) ";
        }
        if (dto.getSubmissionDateTo() != null) {
            whereClause += " and (entity.submissionDate <= :submissionDateTo) ";
        }
        if (dto.getInterviewDateFrom() != null) {
            whereClause += " and (entity.interviewDate >= :interviewDateFrom) ";
        }
        if (dto.getInterviewDateTo() != null) {
            whereClause += " and (entity.interviewDate <= :interviewDateTo) ";
        }
        if (dto.getSubmissionDate() != null) {
            whereClause += " and (Date(entity.submissionDate) = :submissionDate) ";
        }
        if (dto.getInterviewDate() != null) {
            whereClause += " and (Date(entity.interviewDate) = :interviewDate) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, CandidateDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getReceptionStatus() != null) {
            query.setParameter("receptionStatus", dto.getReceptionStatus());
            qCount.setParameter("receptionStatus", dto.getReceptionStatus());
        }
        if (dto.getRecruitment() != null && dto.getRecruitment().getId() != null) {
            query.setParameter("recruitmentId", dto.getRecruitment().getId());
            qCount.setParameter("recruitmentId", dto.getRecruitment().getId());
        }
        if (dto.getOrganizationId() != null) {
            query.setParameter("organizationId", dto.getOrganizationId());
            qCount.setParameter("organizationId", dto.getOrganizationId());
        }
        if (dto.getDepartmentId() != null) {
            query.setParameter("departmentId", dto.getDepartmentId());
            qCount.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            query.setParameter("positionTitleId", dto.getPositionTitle().getId());
            qCount.setParameter("positionTitleId", dto.getPositionTitle().getId());
        }
        if (dto.getSubmissionDateFrom() != null) {
            query.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
            qCount.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
        }
        if (dto.getSubmissionDateTo() != null) {
            query.setParameter("submissionDateTo", dto.getSubmissionDateTo());
            qCount.setParameter("submissionDateTo", dto.getSubmissionDateTo());
        }
        if (dto.getInterviewDateFrom() != null) {
            query.setParameter("interviewDateFrom", dto.getInterviewDateFrom());
            qCount.setParameter("interviewDateFrom", dto.getInterviewDateFrom());
        }
        if (dto.getInterviewDateTo() != null) {
            query.setParameter("interviewDateTo", dto.getInterviewDateTo());
            qCount.setParameter("interviewDateTo", dto.getInterviewDateTo());
        }
        if (dto.getSubmissionDate() != null) {
            query.setParameter("submissionDate", dto.getSubmissionDate());
            qCount.setParameter("submissionDate", dto.getSubmissionDate());
        }
        if (dto.getInterviewDate() != null) {
            query.setParameter("interviewDate", dto.getInterviewDate());
            qCount.setParameter("interviewDate", dto.getInterviewDate());
        }
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        long count = (long) qCount.getSingleResult();
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<CandidateDto> entities = query.getResultList();
        Page<CandidateDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    // danh sách ứng viên Chờ nhận việc
    @Override
    public Page<CandidateDto> pagingWaitingJobCandidates(SearchCandidateDto dto) {
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

        String whereClause = " where (1=1) AND (entity.voided = false OR entity.voided IS NULL) and (entity.status = " + HrConstants.CandidateStatus.PENDING_ASSIGNMENT.getValue() + ") ";
        String orderBy = " ORDER BY entity.modifyDate desc ";

        String sqlCount = "select count(distinct entity.id) from Candidate as entity ";
        String sql = "select distinct new com.globits.hr.dto.CandidateDto(entity) from Candidate as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.displayName LIKE :text OR entity.candidateCode LIKE :text ) ";
        }
        if (dto.getDepartmentId() != null) {
            whereClause += " and (entity.recruitment.recruitmentPlan.recruitmentRequest.hrDepartment.id = :departmentId) ";
        }
        if (dto.getOrganizationId() != null) {
            whereClause += " and (entity.recruitment.organization.id = :organizationId) ";
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            whereClause += " and (entity.positionTitle.id = :positionTitleId) ";
        }
        if (dto.getRecruitmentRequest() != null) {
            whereClause += " and (entity.recruitment.recruitmentPlan.recruitmentRequest.id = :recruitmentRequestId) ";
        }
        if (dto.getRecruitmentPlan() != null) {
            whereClause += " and (entity.recruitment.recruitmentPlan.id = :recruitmentPlanId) ";
        }
        if (dto.getRecruitment() != null) {
            whereClause += " and (entity.recruitment.id = :recruitmentId) ";
        }
        if (dto.getSubmissionDateFrom() != null) {
            whereClause += " and (entity.submissionDate >= :submissionDateFrom) ";
        }
        if (dto.getSubmissionDateTo() != null) {
            whereClause += " and (entity.submissionDate <= :submissionDateTo) ";
        }
        if (dto.getInterviewDateFrom() != null) {
            whereClause += " and (entity.interviewDate >= :interviewDateFrom) ";
        }
        if (dto.getInterviewDateTo() != null) {
            whereClause += " and (entity.interviewDate <= :interviewDateTo) ";
        }
        if (dto.getOnboardDateFrom() != null) {
            whereClause += " and (entity.onboardDate >= :onboardDateFrom) ";
        }
        if (dto.getOnboardDateTo() != null) {
            whereClause += " and (entity.onboardDate <= :onboardDateTo) ";
        }
        if (dto.getOnboardDate() != null) {
            whereClause += " and (Date(entity.onboardDate) = :onboardDate) ";
        }
        if (dto.getSubmissionDate() != null) {
            whereClause += " and (Date(entity.submissionDate) = :submissionDate) ";
        }
        if (dto.getInterviewDate() != null) {
            whereClause += " and (Date(entity.interviewDate) = :interviewDate) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, CandidateDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getDepartmentId() != null) {
            query.setParameter("departmentId", dto.getDepartmentId());
            qCount.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getOrganizationId() != null) {
            query.setParameter("organizationId", dto.getOrganizationId());
            qCount.setParameter("organizationId", dto.getOrganizationId());
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            query.setParameter("positionTitleId", dto.getPositionTitle().getId());
            qCount.setParameter("positionTitleId", dto.getPositionTitle().getId());
        }
        if (dto.getRecruitmentRequest() != null) {
            query.setParameter("recruitmentRequestId", dto.getRecruitmentRequest().getId());
            qCount.setParameter("recruitmentRequestId", dto.getRecruitmentRequest().getId());
        }
        if (dto.getRecruitmentPlan() != null) {
            query.setParameter("recruitmentPlanId", dto.getRecruitmentPlan().getId());
            qCount.setParameter("recruitmentPlanId", dto.getRecruitmentPlan().getId());
        }
        if (dto.getRecruitment() != null) {
            query.setParameter("recruitmentId", dto.getRecruitment().getId());
            qCount.setParameter("recruitmentId", dto.getRecruitment().getId());
        }
        if (dto.getSubmissionDateFrom() != null) {
            query.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
            qCount.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
        }
        if (dto.getSubmissionDateTo() != null) {
            query.setParameter("submissionDateTo", dto.getSubmissionDateTo());
            qCount.setParameter("submissionDateTo", dto.getSubmissionDateTo());
        }
        if (dto.getInterviewDateFrom() != null) {
            query.setParameter("interviewDateFrom", dto.getInterviewDateFrom());
            qCount.setParameter("interviewDateFrom", dto.getInterviewDateFrom());
        }
        if (dto.getInterviewDateTo() != null) {
            query.setParameter("interviewDateTo", dto.getInterviewDateTo());
            qCount.setParameter("interviewDateTo", dto.getInterviewDateTo());
        }
        if (dto.getOnboardDateFrom() != null) {
            query.setParameter("onboardDateFrom", dto.getOnboardDateFrom());
            qCount.setParameter("onboardDateFrom", dto.getOnboardDateFrom());
        }
        if (dto.getOnboardDateTo() != null) {
            query.setParameter("onboardDateTo", dto.getOnboardDateTo());
            qCount.setParameter("onboardDateTo", dto.getOnboardDateTo());
        }
        if (dto.getOnboardDate() != null) {
            query.setParameter("onboardDate", dto.getOnboardDate());
            qCount.setParameter("onboardDate", dto.getOnboardDate());
        }
        if (dto.getSubmissionDate() != null) {
            query.setParameter("submissionDate", dto.getSubmissionDate());
            qCount.setParameter("submissionDate", dto.getSubmissionDate());
        }
        if (dto.getInterviewDate() != null) {
            query.setParameter("interviewDate", dto.getInterviewDate());
            qCount.setParameter("interviewDate", dto.getInterviewDate());
        }

        long count = (long) qCount.getSingleResult();
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<CandidateDto> entities = query.getResultList();
        Page<CandidateDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    // Danh sách ứng viên Không đến nhận việc
    @Override
    public Page<CandidateDto> pagingNotComeCandidates(SearchCandidateDto dto) {
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

        String whereClause = " where (1=1) AND (entity.voided = false OR entity.voided IS NULL) and (entity.status = " + HrConstants.CandidateStatus.DECLINED_ASSIGNMENT.getValue() + ") ";
        String orderBy = " ORDER BY entity.modifyDate desc ";

        String sqlCount = "select count(distinct entity.id) from Candidate as entity ";
        String sql = "select distinct new com.globits.hr.dto.CandidateDto(entity) from Candidate as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.displayName LIKE :text OR entity.candidateCode LIKE :text ) ";
        }
        if (dto.getDepartmentId() != null) {
            whereClause += " and (entity.department.id = :departmentId) ";
        }
        if (dto.getOrganizationId() != null) {
            whereClause += " and (entity.organization.id = :organizationId) ";
        }
        if (dto.getPositionId() != null) {
            whereClause += " and (entity.position.id = :positionId) ";
        }
        if (dto.getRecruitmentRequest() != null) {
            whereClause += " and (entity.recruitment.recruitmentPlan.recruitmentRequest.id = :recruitmentRequestId) ";
        }
        if (dto.getRecruitmentPlan() != null) {
            whereClause += " and (entity.recruitment.recruitmentPlan.id = :recruitmentPlanId) ";
        }
        if (dto.getRecruitment() != null) {
            whereClause += " and (entity.recruitment.id = :recruitmentId) ";
        }
        if (dto.getSubmissionDateFrom() != null) {
            whereClause += " and (entity.submissionDate >= :submissionDateFrom) ";
        }
        if (dto.getSubmissionDateTo() != null) {
            whereClause += " and (entity.submissionDate <= :submissionDateTo) ";
        }
        if (dto.getInterviewDateFrom() != null) {
            whereClause += " and (entity.interviewDate >= :interviewDateFrom) ";
        }
        if (dto.getInterviewDateTo() != null) {
            whereClause += " and (entity.interviewDate <= :interviewDateTo) ";
        }
        if (dto.getOnboardDateFrom() != null) {
            whereClause += " and (entity.onboardDate >= :onboardDateFrom) ";
        }
        if (dto.getOnboardDateTo() != null) {
            whereClause += " and (entity.onboardDate <= :onboardDateTo) ";
        }
        if (dto.getOnboardDate() != null) {
            whereClause += " and (Date(entity.onboardDate) = :onboardDate) ";
        }
        if (dto.getSubmissionDate() != null) {
            whereClause += " and (Date(entity.submissionDate) = :submissionDate) ";
        }
        if (dto.getInterviewDate() != null) {
            whereClause += " and (Date(entity.interviewDate) = :interviewDate) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, CandidateDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getOrganizationId() != null) {
            query.setParameter("organizationId", dto.getOrganizationId());
            qCount.setParameter("organizationId", dto.getOrganizationId());
        }
        if (dto.getDepartmentId() != null) {
            query.setParameter("departmentId", dto.getDepartmentId());
            qCount.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getPositionId() != null) {
            query.setParameter("positionId", dto.getPositionId());
            qCount.setParameter("positionId", dto.getPositionId());
        }
        if (dto.getRecruitmentRequest() != null) {
            query.setParameter("recruitmentRequestId", dto.getRecruitmentRequest().getId());
            qCount.setParameter("recruitmentRequestId", dto.getRecruitmentRequest().getId());
        }
        if (dto.getRecruitmentPlan() != null) {
            query.setParameter("recruitmentPlanId", dto.getRecruitmentPlan().getId());
            qCount.setParameter("recruitmentPlanId", dto.getRecruitmentPlan().getId());
        }
        if (dto.getRecruitment() != null) {
            query.setParameter("recruitmentId", dto.getRecruitment().getId());
            qCount.setParameter("recruitmentId", dto.getRecruitment().getId());
        }
        if (dto.getSubmissionDateFrom() != null) {
            query.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
            qCount.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
        }
        if (dto.getSubmissionDateTo() != null) {
            query.setParameter("submissionDateTo", dto.getSubmissionDateTo());
            qCount.setParameter("submissionDateTo", dto.getSubmissionDateTo());
        }
        if (dto.getInterviewDateFrom() != null) {
            query.setParameter("interviewDateFrom", dto.getInterviewDateFrom());
            qCount.setParameter("interviewDateFrom", dto.getInterviewDateFrom());
        }
        if (dto.getInterviewDateTo() != null) {
            query.setParameter("interviewDateTo", dto.getInterviewDateTo());
            qCount.setParameter("interviewDateTo", dto.getInterviewDateTo());
        }
        if (dto.getOnboardDateFrom() != null) {
            query.setParameter("onboardDateFrom", dto.getOnboardDateFrom());
            qCount.setParameter("onboardDateFrom", dto.getOnboardDateFrom());
        }
        if (dto.getOnboardDateTo() != null) {
            query.setParameter("onboardDateTo", dto.getOnboardDateTo());
            qCount.setParameter("onboardDateTo", dto.getOnboardDateTo());
        }
        if (dto.getOnboardDate() != null) {
            query.setParameter("onboardDate", dto.getOnboardDate());
            qCount.setParameter("onboardDate", dto.getOnboardDate());
        }
        if (dto.getSubmissionDate() != null) {
            query.setParameter("submissionDate", dto.getSubmissionDate());
            qCount.setParameter("submissionDate", dto.getSubmissionDate());
        }
        if (dto.getInterviewDate() != null) {
            query.setParameter("interviewDate", dto.getInterviewDate());
            qCount.setParameter("interviewDate", dto.getInterviewDate());
        }

        long count = (long) qCount.getSingleResult();
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<CandidateDto> entities = query.getResultList();
        Page<CandidateDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    // danh sách ứng viên ĐÃ nhận việc
    @Override
    public Page<CandidateDto> pagingOnboardedCandidates(SearchCandidateDto dto) {
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

        String whereClause = " where (1=1) AND (entity.voided = false OR entity.voided IS NULL) and (entity.status = " + HrConstants.CandidateStatus.ACCEPTED_ASSIGNMENT.getValue() + ")";

        String orderBy = " ORDER BY entity.modifyDate desc ";

        String sqlCount = "select count(distinct entity.id) from Candidate as entity ";
        String sql = "select distinct new com.globits.hr.dto.CandidateDto(entity) from Candidate as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.displayName LIKE :text OR entity.candidateCode LIKE :text ) ";
        }
        if (dto.getOrganizationId() != null) {
            whereClause += " and (entity.recruitment.organization.id = :organizationId) ";
        }
        if (dto.getDepartmentId() != null) {
            whereClause += " and (entity.recruitment.recruitmentPlan.recruitmentRequest.hrDepartment.id = :departmentId) ";
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            whereClause += " and (entity.positionTitle.id = :positionTitleId) ";
        }
        if (dto.getRecruitmentRequest() != null) {
            whereClause += " and (entity.recruitment.recruitmentPlan.recruitmentRequest.id = :recruitmentRequestId) ";
        }
        if (dto.getRecruitmentPlan() != null) {
            whereClause += " and (entity.recruitment.recruitmentPlan.id = :recruitmentPlanId) ";
        }
        if (dto.getRecruitment() != null) {
            whereClause += " and (entity.recruitment.id = :recruitmentId) ";
        }
        if (dto.getSubmissionDateFrom() != null) {
            whereClause += " and (entity.submissionDate >= :submissionDateFrom) ";
        }
        if (dto.getSubmissionDateTo() != null) {
            whereClause += " and (entity.submissionDate <= :submissionDateTo) ";
        }
        if (dto.getInterviewDateFrom() != null) {
            whereClause += " and (entity.interviewDate >= :interviewDateFrom) ";
        }
        if (dto.getInterviewDateTo() != null) {
            whereClause += " and (entity.interviewDate <= :interviewDateTo) ";
        }
        if (dto.getOnboardDateFrom() != null) {
            whereClause += " and (entity.onboardDate >= :onboardDateFrom) ";
        }
        if (dto.getOnboardDateTo() != null) {
            whereClause += " and (entity.onboardDate <= :onboardDateTo) ";
        }
        if (dto.getOnboardDate() != null) {
            whereClause += " and (Date(entity.onboardDate) = :onboardDate) ";
        }
        if (dto.getSubmissionDate() != null) {
            whereClause += " and (Date(entity.submissionDate) = :submissionDate) ";
        }
        if (dto.getInterviewDate() != null) {
            whereClause += " and (Date(entity.interviewDate) = :interviewDate) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, CandidateDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getOrganizationId() != null) {
            query.setParameter("organizationId", dto.getOrganizationId());
            qCount.setParameter("organizationId", dto.getOrganizationId());
        }
        if (dto.getDepartmentId() != null) {
            query.setParameter("departmentId", dto.getDepartmentId());
            qCount.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            query.setParameter("positionTitleId", dto.getPositionTitle().getId());
            qCount.setParameter("positionTitleId", dto.getPositionTitle().getId());
        }
        if (dto.getRecruitmentRequest() != null) {
            query.setParameter("recruitmentRequestId", dto.getRecruitmentRequest().getId());
            qCount.setParameter("recruitmentRequestId", dto.getRecruitmentRequest().getId());
        }
        if (dto.getRecruitmentPlan() != null) {
            query.setParameter("recruitmentPlanId", dto.getRecruitmentPlan().getId());
            qCount.setParameter("recruitmentPlanId", dto.getRecruitmentPlan().getId());
        }
        if (dto.getRecruitment() != null) {
            query.setParameter("recruitmentId", dto.getRecruitment().getId());
            qCount.setParameter("recruitmentId", dto.getRecruitment().getId());
        }
        if (dto.getSubmissionDateFrom() != null) {
            query.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
            qCount.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
        }
        if (dto.getSubmissionDateTo() != null) {
            query.setParameter("submissionDateTo", dto.getSubmissionDateTo());
            qCount.setParameter("submissionDateTo", dto.getSubmissionDateTo());
        }
        if (dto.getInterviewDateFrom() != null) {
            query.setParameter("interviewDateFrom", dto.getInterviewDateFrom());
            qCount.setParameter("interviewDateFrom", dto.getInterviewDateFrom());
        }
        if (dto.getInterviewDateTo() != null) {
            query.setParameter("interviewDateTo", dto.getInterviewDateTo());
            qCount.setParameter("interviewDateTo", dto.getInterviewDateTo());
        }
        if (dto.getOnboardDateFrom() != null) {
            query.setParameter("onboardDateFrom", dto.getOnboardDateFrom());
            qCount.setParameter("onboardDateFrom", dto.getOnboardDateFrom());
        }
        if (dto.getOnboardDateTo() != null) {
            query.setParameter("onboardDateTo", dto.getOnboardDateTo());
            qCount.setParameter("onboardDateTo", dto.getOnboardDateTo());
        }
        if (dto.getOnboardDate() != null) {
            query.setParameter("onboardDate", dto.getOnboardDate());
            qCount.setParameter("onboardDate", dto.getOnboardDate());
        }
        if (dto.getSubmissionDate() != null) {
            query.setParameter("submissionDate", dto.getSubmissionDate());
            qCount.setParameter("submissionDate", dto.getSubmissionDate());
        }
        if (dto.getInterviewDate() != null) {
            query.setParameter("interviewDate", dto.getInterviewDate());
            qCount.setParameter("interviewDate", dto.getInterviewDate());
        }

        long count = (long) qCount.getSingleResult();
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<CandidateDto> entities = query.getResultList();
        Page<CandidateDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    private void deleteErrorCreatedStaff(Candidate entity) {
        if (entity.getStaff() != null) {
            Staff errorCreatedStaff = staffRepository.findById(entity.getStaff().getId()).orElse(null);
            staffRepository.delete(errorCreatedStaff);
            entity.setStaff(null);
        }
    }


    @Override
    @Transactional
    public Boolean updateStatus(SearchCandidateDto dto) throws Exception {
        if (dto == null)
            return false;
        if (dto.getCandidateIds() != null && dto.getCandidateIds().isEmpty())
            return true;

        for (UUID candidateId : dto.getCandidateIds()) {
            Candidate entity = candidateRepository.findById(candidateId).orElse(null);
            if (entity == null) throw new Exception("Candidate is not existed!");
            if (dto.getStatus().equals(HrConstants.CandidateStatus.REJECTED.getValue())) {
                CandidateRecruitmentRound candidateRecruitmentRound = candidateRecruitmentRoundRepository.getCurrentRoundOfCandidate(entity.getId());
                if (candidateRecruitmentRound != null) {
                    candidateRecruitmentRound.setResultStatus(HrConstants.ResultStatus.FAIL);
                    if (candidateRecruitmentRound.getStatus() != null) {
                        candidateRecruitmentRound.setStatus(HrConstants.CandidateRecruitmentRoundStatus.REJECTED);
                    } else {
                        candidateRecruitmentRound.setStatus(HrConstants.CandidateRecruitmentRoundStatus.PARTICIPATED);
                    }
                    candidateRecruitmentRoundRepository.save(candidateRecruitmentRound);
                }
            }
            entity.setStatus(dto.getStatus());
            candidateRepository.save(entity);
        }

        return true;
    }

    @Override
    public Integer saveListCandidate(List<CandidateDto> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<Candidate> candidates = new ArrayList<>();
        for (CandidateDto dto : list) {
            Candidate entity = null;
            if (dto.getCandidateCode() != null) {
                List<Candidate> candidateList = candidateRepository.findByCode(dto.getCandidateCode());
                if (candidateList != null && !candidateList.isEmpty()) {
                    entity = candidateList.get(0);
                }
            } else {
                continue;
            }
            if (entity == null) {
                entity = new Candidate();
                entity.setCandidateCode(dto.getCandidateCode());
            }
            entity.setDisplayName(dto.getDisplayName());
            entity.setFirstName(dto.getFirstName());
            entity.setLastName(dto.getLastName());

            entity.setGender(dto.getGender());
            entity.setBirthDate(dto.getBirthDate());
            entity.setMaritalStatus(dto.getMaritalStatus());

            AdministrativeUnit nativePlace = null;
            if (dto.getNativeVillage() != null && dto.getNativeVillage().getCode() != null) {
                nativePlace = administrativeUnitRepository.findByCode(dto.getNativeVillage().getCode());
            }
            entity.setNativeVillage(nativePlace);

            entity.setPermanentResidence(dto.getPermanentResidence());

            entity.setCurrentResidence(dto.getCurrentResidence());

            AdministrativeUnit province = null;
            AdministrativeUnit district = null;
            AdministrativeUnit commune = null;

            if (dto.getProvince() != null && dto.getProvince().getCode() != null) {
                province = administrativeUnitRepository.findByCode(dto.getProvince().getCode());
            }
            if (dto.getDistrict() != null && dto.getDistrict().getCode() != null) {
                district = administrativeUnitRepository.findByCode(dto.getDistrict().getCode());
            }
            if (dto.getAdministrativeUnit() != null && dto.getAdministrativeUnit().getCode() != null) {
                commune = administrativeUnitRepository.findByCode(dto.getAdministrativeUnit().getCode());
            }
            if (commune != null) {
                entity.setAdministrativeUnit(commune);
            }
            if (commune == null && district != null) {
                entity.setAdministrativeUnit(district);
            }
            if (commune == null && district == null && province != null) {
                entity.setAdministrativeUnit(province);
            }

            entity.setIdNumber(dto.getIdNumber());
            entity.setIdNumberIssueDate(dto.getIdNumberIssueDate());
            entity.setIdNumberIssueBy(dto.getIdNumberIssueBy());

            Country country = null;
            if (dto.getNationality() != null && dto.getNationality().getCode() != null) {
                country = countryRepository.findByCode(dto.getNationality().getCode());
            }
            entity.setNationality(country);

            Ethnics ethnics = null;
            if (dto.getEthnics() != null && dto.getEthnics().getCode() != null) {
                ethnics = ethnicsRepository.findByCode(dto.getEthnics().getCode());
            }
            entity.setEthnics(ethnics);

            Religion religion = null;
            if (dto.getReligion() != null && dto.getReligion().getCode() != null) {
                religion = religionRepository.findByCode(dto.getReligion().getCode());
            }
            entity.setReligion(religion);

            entity.setPhoneNumber(dto.getPhoneNumber());
            entity.setEmail(dto.getEmail());
            entity.setSubmissionDate(dto.getSubmissionDate());

            RecruitmentPlan recruitmentPlan = null;
            if (dto.getRecruitmentPlan() != null && dto.getRecruitmentPlan().getCode() != null) {
                List<RecruitmentPlan> recruitmentPlanList = recruitmentPlanRepository.findByCode(dto.getRecruitmentPlan().getCode());
                if (recruitmentPlanList != null && !recruitmentPlanList.isEmpty()) {
                    recruitmentPlan = recruitmentPlanList.get(0);
                }
            }
            entity.setRecruitmentPlan(recruitmentPlan);

            HrOrganization organization = null;
            if (dto.getOrganization() != null && dto.getOrganization().getCode() != null) {
                List<HrOrganization> hrOrganizationList = hrOrganizationRepository.findByCode(dto.getOrganization().getCode());
                if (hrOrganizationList != null && !hrOrganizationList.isEmpty()) {
                    organization = hrOrganizationList.get(0);
                }
            }
            entity.setOrganization(organization);

            HRDepartment hrDepartment = null;
            if (dto.getDepartment() != null && dto.getDepartment().getCode() != null) {
                List<HRDepartment> hrDepartmentList = hRDepartmentRepository.findByCode(dto.getDepartment().getCode());
                if (hrDepartmentList != null && !hrDepartmentList.isEmpty()) {
                    hrDepartment = hrDepartmentList.get(0);
                }
            }
            entity.setDepartment(hrDepartment);

            PositionTitle positionTitle = null;
            if (dto.getPositionTitle() != null && dto.getPositionTitle().getCode() != null) {
                List<PositionTitle> positionTitleList = positionTitleRepository.findByCode(dto.getPositionTitle().getCode());
                if (positionTitleList != null && !positionTitleList.isEmpty()) {
                    positionTitle = positionTitleList.get(0);
                }
            }
            if (positionTitle == null) continue;
            entity.setPositionTitle(positionTitle);

            entity.setDesiredPay(dto.getDesiredPay());
            entity.setPossibleWorkingDate(dto.getPossibleWorkingDate());

            Staff staff = null;
            if (dto.getStaff() != null && dto.getStaff().getStaffCode() != null) {
                List<Staff> staffList = staffRepository.findByCode(dto.getStaff().getStaffCode());
                if (staffList != null && !staffList.isEmpty()) {
                    staff = staffList.get(0);
                }
            }
            entity.setIntroducer(staff);
            candidates.add(entity);

        }
        candidates = candidateRepository.saveAll(candidates);
        return candidates.size();
    }

    // trang thai ho so cua ung vien
    @Override
    @Transactional
    public Boolean updateApprovalStatus(SearchCandidateDto dto) throws Exception {
        if (dto == null)
            return false;
        if (dto.getCandidateIds() != null && dto.getCandidateIds().size() == 0)
            return true;

        for (UUID candidateId : dto.getCandidateIds()) {
            Candidate entity = candidateRepository.findById(candidateId).orElse(null);
            if (entity == null)
                throw new Exception("Candidate is not existed!");

            // skip if approval status of candidate is not changed
            if (entity.getApprovalStatus() != null && dto.getApprovalStatus() != null
                    && entity.getApprovalStatus().equals(dto.getApprovalStatus()))
                continue;

            entity.setApprovalStatus(dto.getApprovalStatus());

            // candidate in this stage has not received yet
            entity.setExamStatus(null);
            entity.setReceptionStatus(null);
            entity.setOnboardDate(null);
            entity.setRefusalReason(null);
            entity.setInterviewDate(null);
            deleteErrorCreatedStaff(entity);

            if (dto.getApprovalStatus() != null) {
                // if candidate's approval status is changed to APPROVED
                // => the initial state of field exam state of candidate is NOT_TESTED_YET
                if (dto.getApprovalStatus().equals(HrConstants.CandidateApprovalStatus.APPROVED.getValue())) {
                    entity.setExamStatus(HrConstants.CandidateExamStatus.NOT_TESTED_YET.getValue());

                    // save interview date = Ngày ứng viên làm bài phỏng vấn/ thi tuyển
                    entity.setInterviewDate(dto.getInterviewDate());
                }
                // if candidate is rejected
                // => save refusalReason
                else if (dto.getApprovalStatus().equals(HrConstants.CandidateApprovalStatus.REJECTED.getValue())) {
                    entity.setRefusalReason(dto.getRefusalReason());
                }
            }

            candidateRepository.save(entity);
        }

        return true;
    }

    @Override
    @Transactional
    public Boolean updateExamStatus(SearchCandidateDto dto) throws Exception {
        if (dto == null)
            return false;
        if (dto.getCandidateIds() != null && dto.getCandidateIds().isEmpty())
            return true;

        for (UUID candidateId : dto.getCandidateIds()) {
            Candidate entity = candidateRepository.findById(candidateId).orElse(null);
            if (entity == null)
                throw new Exception(
                        "Candidate " + entity.getId() + "(" + entity.getDisplayName() + ")" + " is not existed!");
            if (entity.getApprovalStatus() == null
                    || entity.getApprovalStatus() != HrConstants.CandidateApprovalStatus.APPROVED.getValue())
                throw new Exception(
                        "Candidate " + entity.getId() + "(" + entity.getDisplayName() + ")" + " has not approved yet");

            // skip logic below if exam status of candidate is not changed
            if (entity.getExamStatus() != null && dto.getExamStatus() != null
                    && entity.getExamStatus().equals(dto.getExamStatus()))
                continue;

            entity.setExamStatus(dto.getExamStatus());

            // candidate in this stage has not received yet
            entity.setReceptionStatus(null);
            entity.setOnboardDate(null);
            entity.setRefusalReason(null);
            deleteErrorCreatedStaff(entity);

            if (dto.getExamStatus() != null) {
                // if candidate's exam status is changed to PASSED
                // => the initial state of field receptionStatus of candidate is
                // NOT_RECEPTED_YET
                if (dto.getExamStatus().equals(HrConstants.CandidateExamStatus.PASSED.getValue())) {
                    entity.setReceptionStatus(HrConstants.CandidateReceptionStatus.NOT_RECEPTED_YET.getValue());
                }
                // if candidate is rejected
                // => save refusalReason
                else if (dto.getExamStatus().equals(HrConstants.CandidateExamStatus.REJECTED.getValue())) {
                    entity.setRefusalReason(dto.getRefusalReason());
                }
            }

            candidateRepository.save(entity);
        }

        return true;
    }

    @Override
    public List<CandidateDto> getCandidatesByRecruitmentPlanId(UUID recruitmentPlanId) {
        if (recruitmentPlanId == null) {
            return null;
        }

        return candidateRepository.getByPlan(recruitmentPlanId);
    }

    @Override
    @Transactional
    public Boolean updateReceptionStatus(SearchCandidateDto dto) throws Exception {
        if (dto == null)
            return false;
        if (dto.getCandidateIds() != null && dto.getCandidateIds().size() == 0)
            return true;

        for (UUID candidateId : dto.getCandidateIds()) {
            Candidate entity = candidateRepository.findById(candidateId).orElse(null);
            if (entity == null)
                throw new Exception(
                        "Candidate " + entity.getId() + "(" + entity.getDisplayName() + ")" + " is not existed!");
            if (entity.getApprovalStatus() == null
                    || entity.getApprovalStatus() != HrConstants.CandidateApprovalStatus.APPROVED.getValue())
                throw new Exception(
                        "Candidate " + entity.getId() + "(" + entity.getDisplayName() + ")" + " has not approved yet");
            if (entity.getExamStatus() == null
                    || entity.getExamStatus() != HrConstants.CandidateExamStatus.PASSED.getValue())
                throw new Exception("Candidate " + entity.getId() + "(" + entity.getDisplayName() + ")"
                        + " has not passed the test of recruitment");

            // skip logic below if reception status of candidate is not changed
            if (entity.getReceptionStatus() != null && dto.getReceptionStatus() != null
                    && entity.getReceptionStatus().equals(dto.getReceptionStatus()))
                continue;

            entity.setReceptionStatus(dto.getReceptionStatus());

            // candidate in this stage has not received yet
            entity.setOnboardDate(null);
            entity.setRefusalReason(null);
            deleteErrorCreatedStaff(entity);

            if (dto.getReceptionStatus() != null) {
                // if candidate's reception status is changed to APPROVED
                // => the initial state of field exam state of candidate is WAITING
                if (dto.getReceptionStatus().equals(HrConstants.CandidateReceptionStatus.RECEPTED.getValue())) {
                    entity.setOnboardStatus(HrConstants.CandidateOnboardStatus.WAITING.getValue());

                    // save onboardDate = Ngày ứng viên nhận việc
                    entity.setOnboardDate(dto.getOnboardDate());
                }
                // if candidate is rejected
                // => save refusalReason
                else if (dto.getReceptionStatus().equals(HrConstants.CandidateReceptionStatus.REJECTED.getValue())) {
                    entity.setRefusalReason(dto.getRefusalReason());
                }
            }

            candidateRepository.save(entity);
        }

        return true;
    }

    @Override
    public Boolean convertToWaitingJob(SearchCandidateDto dto) throws Exception {
        if (dto == null)
            return false;
        if (dto.getCandidateIds() != null && dto.getCandidateIds().isEmpty())
            return true;

        for (UUID candidateId : dto.getCandidateIds()) {
            Candidate entity = candidateRepository.findById(candidateId).orElse(null);
            if (entity == null)
                throw new Exception(
                        "Candidate " + entity.getId() + "(" + entity.getDisplayName() + ")" + " is not existed!");
            if (entity.getApprovalStatus() == null
                    || !entity.getApprovalStatus().equals(HrConstants.CandidateApprovalStatus.APPROVED.getValue()))
                throw new Exception(
                        "Candidate " + entity.getId() + "(" + entity.getDisplayName() + ")" + " has not approved yet");
            if (entity.getExamStatus() == null
                    || !entity.getExamStatus().equals(HrConstants.CandidateExamStatus.PASSED.getValue()))
                throw new Exception("Candidate " + entity.getId() + "(" + entity.getDisplayName() + ")"
                        + " has not passed the recruitment's tests yet");

            // skip logic below if exam status of candidate is not changed
            if (entity.getReceptionStatus() != null
                    && entity.getReceptionStatus().equals(HrConstants.CandidateReceptionStatus.RECEPTED.getValue()))
                continue;

            if (entity.getReceptionStatus() != null && !entity.getReceptionStatus()
                    .equals(HrConstants.CandidateReceptionStatus.NOT_RECEPTED_YET.getValue())) {
                throw new Exception("Candidate " + entity.getId() + "(" + entity.getDisplayName() + ")"
                        + " has already processed, can not convert this candidate's state to NOT_RECEPTED_YET anymore!");
            }

            entity.setReceptionStatus(HrConstants.CandidateReceptionStatus.NOT_RECEPTED_YET.getValue());
            // ngày tiếp nhận nhân viên
            entity.setOnboardDate(dto.getOnboardDate());

            // candidate in this stage has not received yet
            deleteErrorCreatedStaff(entity);

            candidateRepository.save(entity);
        }

        return true;
    }

    @Override
    public Boolean convertToNotCome(SearchCandidateDto dto) throws Exception {
        if (dto == null)
            return false;
        if (dto.getCandidateIds() != null && dto.getCandidateIds().isEmpty())
            return true;

        for (UUID candidateId : dto.getCandidateIds()) {
            Candidate entity = candidateRepository.findById(candidateId).orElse(null);
            if (entity != null) {
                // candidate in this stage has not received yet
                deleteErrorCreatedStaff(entity);
                entity.setStatus(HrConstants.CandidateStatus.DECLINED_ASSIGNMENT.getValue());
                // Ly do tu choi ung vien
                entity.setRefusalReason(dto.getRefusalReason());
                // Chuyen trang thai sang: Khong den nhan viec
                entity.setOnboardStatus(HrConstants.CandidateOnboardStatus.NOT_COME.getValue());
                candidateRepository.save(entity);
            }
        }

        return true;
    }

    @Override
    public ApiResponse<StaffDto> convertToReceivedJob(List<CandidateDto> dto) {
        for (CandidateDto candidateDto : dto) {
            if (candidateDto.getId() == null) {
                return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy ứng viên!", null);
            }
            Candidate entity = candidateRepository.findById(candidateDto.getId()).orElse(null);
            if (entity == null) {
                return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy ứng viên!!", null);
            }
            RecruitmentPlan recruitmentPlan = entity.getRecruitmentPlan();
            if (recruitmentPlan != null) {
                RecruitmentRequest request = recruitmentPlan.getRecruitmentRequest();
                if (request != null) {
                    List<RecruitmentRequestItem> items = new ArrayList<>(request.getRecruitmentRequestItems());
                    if (!items.isEmpty()) {
                        RecruitmentRequestItem item = items.get(0);
                        Long count = candidateRepository.countNumberCandidateByStatusAndRecruitmentRequest(request.getId(), HrConstants.CandidateStatus.ACCEPTED_ASSIGNMENT.getValue());
                        if (item.getAnnouncementQuantity() != null) {
                            if (count >= item.getAnnouncementQuantity()) {
                                return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Số lượng tuyển dụng của yêu cầu đã đủ", null);
                            }
                        }
                    }
                    entity.setOnboardDate(candidateDto.getOnboardDate());
                    entity.setStatus(HrConstants.CandidateStatus.ACCEPTED_ASSIGNMENT.getValue());
                    Staff newStaff = generateStaffFromCandidate(entity);
                    entity.setStaff(newStaff);
                    candidateRepository.save(entity);
                    return new ApiResponse<>(HttpStatus.SC_OK, "Tuyển dụng thành công", new StaffDto(newStaff));
                } else {
                    return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy yêu cầu tuyển dụng", null);
                }
            } else {
                return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy kế hoạch tuyển dụng", null);
            }
        }
        return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy kế hoạch tuyển dụng", null);
    }

    private Staff generateStaffFromCandidate(Candidate candidate) {
        if (candidate == null || candidate.getId() == null)
            return null;

//        Staff entity = staffRepository.findById(candidate.getId()).orElse(null);
//        if (entity == null) {
//            entity = new Staff();
//            entity.setId(candidate.getId());
//        }

        Staff entity = new Staff();
        //Sinh mã nhân viên tự động
        String staffCode = staffServiceV3.generateNewStaffCodeV2(null);
        entity.setStaffCode(staffCode);
        entity.setCandidate(candidate);
        entity.setDisplayName(candidate.getDisplayName());
        entity.setLastName(candidate.getLastName());
        entity.setFirstName(candidate.getFirstName());
        // ngay bat dong cong viec
        entity.setStartDate(candidate.getOnboardDate());
        // ngay tuyen dung
        entity.setRecruitmentDate(candidate.getOnboardDate());

        entity.setBirthDate(candidate.getBirthDate());
        entity.setBirthPlace(candidate.getBirthPlace());
        entity.setGender(candidate.getGender());
        entity.setIdNumber(candidate.getIdNumber());
        entity.setIdNumberIssueDate(candidate.getIdNumberIssueDate());
        entity.setIdNumberIssueBy(candidate.getIdNumberIssueBy());
        entity.setPhoneNumber(candidate.getPhoneNumber());
        entity.setEmail(candidate.getEmail());
        entity.setImagePath(candidate.getImagePath());
        entity.setMaritalStatus(candidate.getMaritalStatus());
        entity.setPermanentResidence(candidate.getPermanentResidence());
        entity.setCurrentResidence(candidate.getCurrentResidence());

        if (candidate.getNationality() != null) {
            entity.setNationality(candidate.getNationality());
        }
        if (candidate.getEthnics() != null) {
            entity.setEthnics(candidate.getEthnics());
        }
        if (candidate.getReligion() != null) {
            entity.setReligion(candidate.getReligion());
        }
        if (candidate.getNativeVillage() != null) {
            entity.setNativeVillage(candidate.getNativeVillage());
        }
        if (candidate.getPositionTitle() != null) {
            entity.setJobTitle(candidate.getPositionTitle().getName());
//            entity.setCurrentPosition(candidate.getPositionTitle());
        }

        // fill employee status code default is WORKING
        List<EmployeeStatus> isWorkingEmployeeStatuses = employeeStatusRepository
                .findByCode(HrConstants.EmployeeStatusCodeEnum.WORKING.getValue());
        if (isWorkingEmployeeStatuses != null && !isWorkingEmployeeStatuses.isEmpty()) {
            EmployeeStatus isWorkingStatus = isWorkingEmployeeStatuses.get(0);
            entity.setStatus(isWorkingStatus);
        }

        if (candidate.getRecruitment() != null && candidate.getRecruitment().getHrDepartmentCS() != null) {
            entity.setDepartment(candidate.getRecruitment().getHrDepartmentCS());
        }

        if (candidate.getAdministrativeUnit() != null) {
            entity.setAdministrativeUnit(candidate.getAdministrativeUnit());
        }

        if (candidate.getCandidateCertificates() != null && !candidate.getCandidateCertificates().isEmpty()) {
            if (entity.getPersonCertificate() == null)
                entity.setPersonCertificate(new HashSet<>());

            Set<PersonCertificate> staffCertificates = new HashSet<>();
            for (PersonCertificate personCertificate : candidate.getCandidateCertificates()) {
                PersonCertificate copy = new PersonCertificate();
                copy.setPerson(entity);
                copy.setCertificate(personCertificate.getCertificate());
                copy.setName(personCertificate.getName());
                copy.setLevel(personCertificate.getLevel());
                copy.setIssueDate(personCertificate.getIssueDate());

                staffCertificates.add(copy);
            }

            entity.getPersonCertificate().addAll(staffCertificates);
        }

        entity = staffRepository.save(entity);

        return entity;
    }

    @Override
    public XWPFDocument generateDocx(CandidateDto dto) throws IOException {
        String HDLD = "HĐTV.docx";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(HDLD);

        if (inputStream == null) {
            throw new IOException("Không tìm thấy template HĐTV.docx");
        }

        XWPFDocument document = new XWPFDocument(inputStream);
        inputStream.close();
        CandidateDto candidateDto = candidateRepository.findById(dto.getId())
                .map(CandidateDto::new)
                .orElse(null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedBirthDate = (candidateDto.getBirthDate() != null) ? dateFormat.format(candidateDto.getBirthDate()) : "Không có";
        String formattedIssueDate = (candidateDto.getIdNumberIssueDate() != null) ? dateFormat.format(candidateDto.getIdNumberIssueDate()) : "";
        String formattedStartDate = (candidateDto.getStartDate() != null) ? convertDate(candidateDto.getStartDate()) : convertDate(new Date());

        String formattedEndDate = (candidateDto.getEndDate() != null) ? convertDate(candidateDto.getEndDate()) : "";

        // Chuyển từ Date sang Calendar để lấy năm đúng
        Date onBoad = candidateDto.getOnboardDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(onBoad);

        String formatNum = candidateDto.getCandidateCode() + "/HĐLĐ-Globits-" + calendar.get(Calendar.YEAR);

        String formattedToday = convertDate(onBoad);

        // Hàm thay thế nội dung trong một đoạn văn bản
        Consumer<XWPFParagraph> replacePlaceholders = paragraph -> {
            StringBuilder fullText = new StringBuilder();
            List<XWPFRun> runs = paragraph.getRuns();

            for (XWPFRun run : runs) {
                if (run.getText(0) != null) {
                    fullText.append(run.getText(0));
                }
            }

            String text = fullText.toString();
            if (text.contains("{{")) {
                text = text.replace("{{candidateName}}", candidateDto.getDisplayName());
                text = text.replace("{{candidateBirthDate}}", formattedBirthDate);
                text = text.replace("{{candidateIdCard}}", candidateDto.getIdCitizen() != null ? candidateDto.getIdCitizen() : "");
                text = text.replace("{{candidatePhone}}", candidateDto.getPhoneNumber() != null ? candidateDto.getPhoneNumber() : "Không có");
                text = text.replace("{{candidateAddress}}", candidateDto.getPermanentResidence() != null ? candidateDto.getPermanentResidence() : "Không có");
                text = text.replace("{{candidateIdCardDate}}", formattedIssueDate);
                text = text.replace("{{candidateIdCardPlace}}", candidateDto.getIdNumberIssueBy() != null ? candidateDto.getIdNumberIssueBy() : "");
                text = text.replace("{{ngayBatDau}}", formattedStartDate);
                text = text.replace("{{ngayKetThuc}}", formattedEndDate);
                if (candidateDto.getOrganization() != null) {
                    HrOrganizationDto organization = candidateDto.getOrganization();
                    text = text.replace("{{mst}}", organization.getTaxCode() != null ? organization.getTaxCode() : "");
                    text = text.replace("{{address}}", organization.getAddressDetail() != null ? organization.getAddressDetail() : "");
                    text = text.replace("{{phoneNumber}}", "");
                    text = text.replace("{{directorA}}", "");
                    text = text.replace("{{positionA}}", "");
                    text = text.replace("{{directorB}}", organization.getRepresentative() != null && organization.getRepresentative().getDisplayName() != null ? organization.getRepresentative().getDisplayName() : "");
                    Position position = null;
                    if (organization.getRepresentative() != null && organization.getRepresentative().getMainPositionId() != null) {
                        positionRepository.findById(organization.getRepresentative().getMainPositionId()).orElse(null);
                    }
                    text = text.replace("{{positionB}}", position != null ? position.getName() : "");

                }
                // Xóa các run cũ
                for (int i = runs.size() - 1; i >= 0; i--) {
                    paragraph.removeRun(i);
                }

                // Tạo run mới với nội dung đã thay thế
                XWPFRun newRun = paragraph.createRun();
                newRun.setText(text);


                // Đặt font là Times New Roman
                newRun.setFontFamily("Times New Roman");
            }
        };
        // Thay thế trong tất cả các đoạn văn bản ngoài bảng
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replacePlaceholders.accept(paragraph);
        }

        // Thay thế trong tất cả các bảng
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replacePlaceholders.accept(paragraph);
                    }
                }
            }
        }

        return document;
    }


    public static String convertDate(Date date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd 'tháng' MM 'năm' yyyy");

        return outputFormat.format(date);
    }

    @Override
    public ExistingCandidatesDto existingCandidates(ExistingCandidatesSearchDto search) {
        if (search != null) {
            ExistingCandidatesDto dto = new ExistingCandidatesDto();
            boolean status = false;
            //Kiểm tra sđt và email đã có staff nào có chưa
            List<CandidateDto> listStaff = candidateRepository.checkDuplicate(search.getPhoneNumber(), search.getEmail(), search.getIdNumber());
            if (listStaff != null && listStaff.size() > 0) {
                dto.setListStaff(listStaff);
                status = true;
            }
            dto.setStatus(status);
            return dto;
        }
        return null;
    }

    @Override
    public List<CandidateDto> getExistCandidateProfileOfStaff(UUID staffId) {
        if (staffId == null) return null;
        return candidateRepository.getExistCandidateProfileOfStaff(staffId);
    }

    @Override
    public Workbook exportExcelRecruitmentReports(SearchCandidateDto dto) {
        if (dto == null) {
            return null;
        }

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("Empty.xlsx")) {
            if (fileInputStream == null) {
                throw new IOException("File '" + "Empty.xlsx" + "' không tìm thấy trong classpath");
            }
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            CellStyle dataCellStyle = ExcelUtils.createDataCellStyle(workbook);

            // Tạo style riêng cho header
            CellStyle headerCellStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 13);
            headerFont.setColor(IndexedColors.BLACK.getIndex());
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);

            int pageIndex = 1;
            int rowIndex = 1;
            boolean hasNextPage = true;
            long startTime = System.nanoTime();
            logger.info("Bắt đầu xuất báo cáo tuyển dụng");

            List<String> header = new ArrayList<>(Arrays.asList(
                    "Vị trí tuyển dụng",
                    "Phòng/Cơ sở",
                    "Ngày nhận đề xuất",
                    "Ngày nhận CV",
                    "Họ và tên ứng viên",
                    "Năm sinh",
                    "SĐT",
                    "Email",
                    "Ngày nhận CV",
                    "Nguồn",
                    "Link CV",
                    "Người phụ trách",
                    "Sơ vấn",
                    "Thông tin sơ vấn (nếu có)",
                    "Thời gian sơ vấn"
            ));

            int recruitmentPlanRound = 0;
            if (dto.getRecruitmentPlanId() != null) {
                List<RecruitmentRound> listRecruitmentRound = recruitmentRoundRepository.findAllByRecruitmentPlan(dto.getRecruitmentPlanId());
                if (listRecruitmentRound != null && !listRecruitmentRound.isEmpty()) {
                    recruitmentPlanRound = 4 * listRecruitmentRound.size();
                    for (RecruitmentRound round : listRecruitmentRound) {
                        header.add("Ngày phỏng vấn vòng " + round.getRoundOrder());
                        header.add("Người phỏng vấn vòng " + round.getRoundOrder());
                        header.add("Nhận xét vòng " + round.getRoundOrder());
                        header.add("Kết quả vòng " + round.getRoundOrder());
                    }
                }
            }

            header.add("Ngày đến Nhận việc thực tế");

            Row headerRow = sheet.createRow(0); // Ghi header vào dòng đầu tiên
            for (int colIndex = 0; colIndex < header.size(); colIndex++) {
                Cell cell = headerRow.createCell(colIndex);
                cell.setCellValue(header.get(colIndex));
                cell.setCellStyle(headerCellStyle); // Áp dụng style đã định dạng cho header
            }

            while (hasNextPage) {
                // tạo mới search
                SearchCandidateDto searchCandidateDto = new SearchCandidateDto();
                searchCandidateDto.setPageIndex(pageIndex);
                searchCandidateDto.setPageSize(100);
                searchCandidateDto.setRecruitmentPlanId(dto.getRecruitmentPlanId());

                Page<Candidate> pageCandidate = this.pagingEntity(searchCandidateDto);
                if (pageCandidate == null || pageCandidate.isEmpty()) {
                    break;
                }

                // get Map<Candidate, RecruitmentPlan -> RecruitmentRequest
                // RecruitmentPlan :
                // RecruitmentRequest Ngày nhận đề xuất proposalReceiptDate, Người phụ trách
                // personInCharge (staff)
                List<CandidateRecruitmentReportDto> listCandidateRecruitmentReport = getListCandidateReport(dto);

                Map<UUID, CandidateRecruitmentReportDto> mapCandidateRecruitmentReport = this
                        .buildCandidateReportMap(listCandidateRecruitmentReport);

                for (Candidate candidate : pageCandidate) {
                    if (candidate == null)
                        continue;
                    // bắt đầu dòng tiếp
                    Row dataRow = sheet.createRow(rowIndex);
                    int cellIndex = 0;
                    // Vị trí tuyển dụng
                    if (candidate.getPositionTitle() != null) {
                        ExcelUtils.createCell(dataRow, cellIndex++, candidate.getPositionTitle().getName(),
                                dataCellStyle);
                    } else {
                        cellIndex++;
                    }
                    if (candidate.getDepartment() != null) {
                        ExcelUtils.createCell(dataRow, cellIndex++, candidate.getDepartment().getName(), dataCellStyle);
                    } else {
                        cellIndex++;
                    }
                    // Ngày nhận đề xuất
                    // proposalReceiptDate
                    CandidateRecruitmentReportDto candidateRecruitmentReportDto = mapCandidateRecruitmentReport
                            .get(candidate.getId());
                    if (candidateRecruitmentReportDto != null && candidateRecruitmentReportDto.getProposalReceiptDate() != null) {
                        ExcelUtils.createCell(dataRow, cellIndex++, ExcelUtils.formatDate(candidateRecruitmentReportDto.getProposalReceiptDate()), dataCellStyle);
                    } else {
                        cellIndex++;
                    }
                    // Ngày nhận CV
                    ExcelUtils.createCell(dataRow, cellIndex++, ExcelUtils.formatDate(candidate.getSubmissionDate()),
                            dataCellStyle);

                    // Họ và tên ứng viên
                    ExcelUtils.createCell(dataRow, cellIndex++, candidate.getDisplayName(), dataCellStyle);
                    // Năm sinh
                    ExcelUtils.createCell(dataRow, cellIndex++, ExcelUtils.formatDate(candidate.getBirthDate()),
                            dataCellStyle);
                    // SĐT
                    ExcelUtils.createCell(dataRow, cellIndex++, candidate.getPhoneNumber(), dataCellStyle);
                    // Email
                    ExcelUtils.createCell(dataRow, cellIndex++, candidate.getEmail(), dataCellStyle);
                    // Ngày nhận CV
                    ExcelUtils.createCell(dataRow, cellIndex++, ExcelUtils.formatDate(candidate.getSubmissionDate()),
                            dataCellStyle);

                    cellIndex += 3;
                    // Nguồn
                    // ExcelUtils.createCell(dataRow, cellIndex++, candidate, dataCellStyle);
                    // Link CV
                    // ExcelUtils.createCell(dataRow, cellIndex++, candidate, dataCellStyle);
                    // Người phụ trách
                    // ExcelUtils.createCell(dataRow, cellIndex++, candidate, dataCellStyle);

                    // Kết quả sơ lọc
                    String valuePreScreenStatus = "";
                    if (candidate.getPreScreenStatus() != null) {
                        try {
                            PreScreenStatus status = PreScreenStatus.fromValue(candidate.getPreScreenStatus());
                            valuePreScreenStatus = status.getDescription();
                        } catch (IllegalArgumentException e) {
                            valuePreScreenStatus = "";
                        }
                    }
                    ExcelUtils.createCell(dataRow, cellIndex++, valuePreScreenStatus, dataCellStyle);
                    // Ghi chú sơ lọc
                    cellIndex++;
                    // Thời gian phỏng vấn
                    cellIndex++;

                    // Các vòng tuyển dụng trong RecruitmentPlan là RecruitmentRoand->
                    // CadidateRecruitmentRoand
                    // Ngày phỏng vấn vòng
                    // Người phỏng vấn vòng
                    // Nhận xét vòng
                    // kết quả vòng

                    if (candidateRecruitmentReportDto != null && candidateRecruitmentReportDto.getResults() != null
                            && candidateRecruitmentReportDto.getResults().size() > 0) {
                        for (RecruitmentRoundResultDto recruitmentRoundResultDto : candidateRecruitmentReportDto
                                .getResults()) {
                            if (recruitmentRoundResultDto != null) {
                                ExcelUtils.createCell(dataRow, cellIndex++,
                                        recruitmentRoundResultDto.getTakePlaceDate(), dataCellStyle);
                                ExcelUtils.createCell(dataRow, cellIndex++,
                                        recruitmentRoundResultDto.getJudgePersonDisplayName(), dataCellStyle);
                                ExcelUtils.createCell(dataRow, cellIndex++, recruitmentRoundResultDto.getNote(),
                                        dataCellStyle);
                                ExcelUtils.createCell(dataRow, cellIndex++, recruitmentRoundResultDto.getResultStatus(),
                                        dataCellStyle);
                            } else {
                                cellIndex += 4;
                            }
                        }
                    } else {
                        cellIndex += recruitmentPlanRound;
                    }
                    // Ngày đến Nhận việc thực tế
                    ExcelUtils.createCell(dataRow, cellIndex++, candidate.getOnboardDate(), dataCellStyle);
                    rowIndex++;
                }

                hasNextPage = pageCandidate.hasNext(); // Kiểm tra xem còn trang tiếp theo không
                pageIndex++; // Tăng pageIndex để lấy trang tiếp theo
            }
            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;
            logger.info("Kết thúc xuất báo cáo tuyển dụng - Xử lý mất {} ms ", elapsedTimeMs);
            return workbook;
        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }

    /*
     * Lấy thông tin các vòng phỏng vấn của ứng viên
     */
    public List<CandidateRecruitmentReportDto> getListCandidateReport(SearchCandidateDto dto) {
        if (dto == null)
            return Collections.emptyList();

        formalizeSearchObject(dto);

        StringBuilder baseSelect = new StringBuilder("""
                    select new com.globits.hr.dto.CandidateRecruitmentReportDto(
                        c.id as candidateId,
                        c.candidateCode as candidateCode,
                        c.positionTitle.name as positionTitleName,
                        c.department.name as departmentName,
                        r.proposalReceiptDate as proposalReceiptDate,
                        p.id as planId,
                        p.name as planName,
                        rRound.roundOrder as roundOrder,
                        rRound.takePlaceDate as takePlaceDate,
                        judgePerson.displayName as judgePersonDisplayName,
                        rRound.name as roundName,
                        rRoundCandidate.note as note,
                        rRoundCandidate.resultStatus as resultStatus
                    )
                """);

        String baseFrom = """
                    from Candidate c
                    left join c.recruitmentPlan p
                    left join p.recruitmentRequest r
                    left join RecruitmentRound rRound on rRound.recruitmentPlan.id = p.id
                    left join CandidateRecruitmentRound rRoundCandidate
                             on rRoundCandidate.recruitmentRound.id = rRound.id
                             and rRoundCandidate.candidate.id = c.id
                    left join Person judgePerson on judgePerson.id = rRound.judgePerson.id
                """;

        StringBuilder whereClause = new StringBuilder(" where 1=1 ");

        if (StringUtils.hasText(dto.getKeyword())) {
            whereClause.append(" and (c.candidateCode like :text or c.displayName like :text) ");
        }
        if (dto.getRecruitmentPlanId() != null) {
            whereClause.append(" and p.id = :planId ");
        }
        if (dto.getDepartmentId() != null) {
            whereClause.append(" and c.department.id = :departmentId ");
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            whereClause.append(" and c.positionTitle.id = :positionTitleId ");
        }
        if (dto.getCandidateIds() != null && dto.getCandidateIds().size() > 0) {
            whereClause.append(" and c.id IN ( :candidateIds ) ");
        }

        String orderBy = " order by c.id, rRound.roundOrder ";

        String fullHql = baseSelect + baseFrom + whereClause + orderBy;

        Query query = manager.createQuery(fullHql, CandidateRecruitmentReportDto.class);

        // Set parameters
        if (StringUtils.hasText(dto.getKeyword())) {
            String likeText = "%" + dto.getKeyword().trim() + "%";
            query.setParameter("text", likeText);
        }
        if (dto.getRecruitmentPlanId() != null) {
            query.setParameter("planId", dto.getRecruitmentPlanId());
        }
        if (dto.getDepartmentId() != null) {
            query.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            query.setParameter("positionTitleId", dto.getPositionTitle().getId());
        }
        if (dto.getCandidateIds() != null && dto.getCandidateIds().size() > 0) {
            query.setParameter("candidateIds", dto.getCandidateIds());
        }

        return query.getResultList();
    }

    /*
     * gộp danh sách ứng viên và kết quả các vòng tuyển dụng
     */
    public Map<UUID, CandidateRecruitmentReportDto> buildCandidateReportMap(
            List<CandidateRecruitmentReportDto> rawList) {
        Map<UUID, CandidateRecruitmentReportDto> map = new LinkedHashMap<>();

        if (rawList == null || rawList.isEmpty()) {
            return map;
        }

        for (CandidateRecruitmentReportDto item : rawList) {
            UUID candidateId = item.getCandidateId();
            if (candidateId == null)
                continue;

            CandidateRecruitmentReportDto dto = map.get(candidateId);
            if (dto == null) {
                dto = new CandidateRecruitmentReportDto();
                dto.setCandidateId(candidateId);
                dto.setCandidateCode(item.getCandidateCode());
                dto.setPositionTitleName(item.getPositionTitleName());
                dto.setDepartmentName(item.getDepartmentName());
                dto.setProposalReceiptDate(item.getProposalReceiptDate());
                dto.setPlanId(item.getPlanId());
                dto.setPlanName(item.getPlanName());
                dto.setResults(new ArrayList<>());

                map.put(candidateId, dto);
            }

            // Add vòng kết quả
            RecruitmentRoundResultDto roundResult = new RecruitmentRoundResultDto();
            roundResult.setRoundOrder(item.getRoundOrder());
            roundResult.setRoundName(item.getRoundName());
            roundResult.setTakePlaceDate(item.getTakePlaceDate());
            roundResult.setJudgePersonDisplayName(item.getJudgePersonDisplayName());
            roundResult.setNote(item.getNote());
            roundResult.setResultStatus(item.getResultStatus());

            dto.getResults().add(roundResult);
        }

        // Sort các vòng theo thứ tự
        for (CandidateRecruitmentReportDto dto : map.values()) {
            dto.getResults().sort(Comparator.comparing(RecruitmentRoundResultDto::getRoundOrder,
                    Comparator.nullsLast(Integer::compareTo)));
        }

        return map;
    }

    @Override
    public ApiResponse<Boolean> approveCv(SearchCandidateDto searchDto) {
        if (searchDto != null && !CollectionUtils.isEmpty(searchDto.getCandidateIds())) {
            for (UUID candidateId : searchDto.getCandidateIds()) {
                Candidate entity = candidateRepository.findById(candidateId).orElse(null);
                if (entity != null) {
                    if (entity.getRecruitmentPlan() != null) {
                        RecruitmentRound round = recruitmentRoundRepository
                                .findByRecruitmentPlan(entity.getRecruitmentPlan().getId(), PageRequest.of(0, 1))
                                .stream().findFirst().orElse(null);
                        if (entity.getId() != null && round != null && round.getId() != null) {
                            Boolean isExisted = candidateRecruitmentRoundService.checkExistCandidateRecruitmentRound(entity.getId(), round.getId());
                            if (isExisted != null && !isExisted) {
                                CandidateRecruitmentRound candidateRecruitmentRound = new CandidateRecruitmentRound();
                                candidateRecruitmentRound.setCandidate(entity);
                                candidateRecruitmentRound.setRecruitmentRound(round);
                                entity.getCandidateRecruitmentRounds().add(candidateRecruitmentRound);
                                entity.setStatus(HrConstants.CandidateStatus.APPROVE_CV.getValue());
                                candidateRepository.save(entity);
                                return new ApiResponse<>(HttpStatus.SC_OK, "Gửi CV thành công", true);
                            } else {
                                return new ApiResponse<>(HttpStatus.SC_CONFLICT, "Hồ sơ này đã tồn tại ở vòng đầu tiên của kế hoạch", true);
                            }
                        } else {
                            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy vòng trong kế hoạch", true);
                        }
                    } else {
                        return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy kế hoạch", true);
                    }
                } else {
                    return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy hồ sơ", true);
                }
            }
        }
        return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Có lỗi xảy ra", true);
    }

    /*
     * logic giống pagingCandidates
     */
    private Page<Candidate> pagingEntity(SearchCandidateDto dto) {
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

        UserExtRoleDto userRoleDto = userExtService.getCurrentRoleUser();
        List<UUID> staffUsers = new ArrayList<>();
        if (userRoleDto != null) {
            if (userRoleDto.isRoleAdmin() || userRoleDto.isRoleHrManager() || userRoleDto.isRoleSuperHr()) {
                // xem được all danh sách
            } else if (userRoleDto.getStaffId() != null) {
                staffUsers.add(userRoleDto.getStaffId());
            }
        }

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.modifyDate desc ";

        String sqlCount = "select count(entity.id) from Candidate as entity ";
        String sql = "select entity from Candidate as entity ";

        if ((dto.getRecruitmentRoundIds() != null && !dto.getRecruitmentRoundIds().isEmpty())
                || dto.getRecruitmentRoundStatus() != null) {
            sql += " join CandidateRecruitmentRound cr on cr.candidate.id = entity.id ";
            sqlCount += " join CandidateRecruitmentRound cr on cr.candidate.id = entity.id ";
        }
        if (dto.getRecruitmentRoundIds() != null && !dto.getRecruitmentRoundIds().isEmpty()) {
            whereClause += " and cr.recruitmentRound.id in :roundIds ";
        }
        if (dto.getRecruitmentRoundStatus() != null) {
            whereClause += " and cr.status in :recruitmentRoundStatus ";
        }
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.displayName LIKE :text OR entity.candidateCode LIKE :text ) ";
        }
//        if (dto.getStatus() != null) {
//            whereClause += " and (entity.status = :status) ";
//        }
        if (dto.getApprovalStatus() != null) {
            whereClause += " and (entity.approvalStatus = :approvalStatus) ";
        }
        if (dto.getDepartmentId() != null) {
            whereClause += " and (entity.recruitment.department.id = :departmentId) ";
        }
        if (dto.getOrganizationId() != null) {
            whereClause += " and (entity.recruitment.organization.id = :organizationId) ";
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            whereClause += " and (entity.positionTitle.id = :positionTitleId) ";
        }
        if (dto.getSubmissionDateFrom() != null) {
            whereClause += " and (entity.submissionDate >= :submissionDateFrom) ";
        }
        if (dto.getSubmissionDateTo() != null) {
            whereClause += " and (entity.submissionDate <= :submissionDateTo) ";
        }
        if (dto.getSubmissionDate() != null) {
            whereClause += " and (Date(entity.submissionDate) = :submissionDate) ";
        }
        if (dto.getRecruitmentPlanId() != null) {
            whereClause += " and (entity.recruitmentPlan.id = :planId) ";
        }
        if (staffUsers != null && !staffUsers.isEmpty()) {
            whereClause += " AND ((entity.recruitmentPlan.recruitmentRequest.personInCharge.id IN (:staffUserIds)) " +
                    "OR (entity.recruitmentPlan.personApproveCV.id IN (:staffUserIds)) " +
                    "OR EXISTS (SELECT 1 FROM RecruitmentRound round JOIN round.participatingPeople people WHERE round.recruitmentPlan.id = entity.recruitmentPlan.id AND people.id IN (:staffUserIds)) " +
                    ") ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, Candidate.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getRecruitmentRoundIds() != null && !dto.getRecruitmentRoundIds().isEmpty()) {
            query.setParameter("roundIds", dto.getRecruitmentRoundIds());
            qCount.setParameter("roundIds", dto.getRecruitmentRoundIds());
        }
        if (dto.getRecruitmentRoundStatus() != null) {
            query.setParameter("recruitmentRoundStatus", dto.getRecruitmentRoundStatus());
            qCount.setParameter("recruitmentRoundStatus", dto.getRecruitmentRoundStatus());
        }
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getApprovalStatus() != null) {
            query.setParameter("approvalStatus", dto.getApprovalStatus());
            qCount.setParameter("approvalStatus", dto.getApprovalStatus());
        }
        if (dto.getStatus() != null) {
            query.setParameter("status", dto.getStatus());
            qCount.setParameter("status", dto.getStatus());
        }
        if (dto.getRecruitment() != null && dto.getRecruitment().getId() != null) {
            query.setParameter("recruitmentId", dto.getRecruitment().getId());
            qCount.setParameter("recruitmentId", dto.getRecruitment().getId());
        } else if (dto.getRecruitmentId() != null) {
            query.setParameter("recruitmentId", dto.getRecruitmentId());
            qCount.setParameter("recruitmentId", dto.getRecruitmentId());
        }
        if (dto.getDepartmentId() != null) {
            query.setParameter("departmentId", dto.getDepartmentId());
            qCount.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getOrganizationId() != null) {
            query.setParameter("organizationId", dto.getOrganizationId());
            qCount.setParameter("organizationId", dto.getOrganizationId());
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            query.setParameter("positionTitleId", dto.getPositionTitle().getId());
            qCount.setParameter("positionTitleId", dto.getPositionTitle().getId());
        }
        if (dto.getSubmissionDateFrom() != null) {
            query.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
            qCount.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
        }
        if (dto.getSubmissionDateTo() != null) {
            query.setParameter("submissionDateTo", dto.getSubmissionDateTo());
            qCount.setParameter("submissionDateTo", dto.getSubmissionDateTo());
        }
        if (dto.getSubmissionDate() != null) {
            query.setParameter("submissionDate", dto.getSubmissionDate());
            qCount.setParameter("submissionDate", dto.getSubmissionDate());
        }
        if (dto.getRecruitmentPlanId() != null) {
            query.setParameter("planId", dto.getRecruitmentPlanId());
            qCount.setParameter("planId", dto.getRecruitmentPlanId());
        }
        if (staffUsers != null && !staffUsers.isEmpty()) {
            query.setParameter("staffUserIds", staffUsers);
            qCount.setParameter("staffUserIds", staffUsers);
        }

        long count = (long) qCount.getSingleResult();
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<Candidate> content = query.getResultList();

        return new PageImpl<>(content, pageable, count);
    }

    @Override
//	@javax.transaction.Transactional()
    public String syncCandidateCode() {
        String hardCode = "";
        String format = HrConstants.STRING_FORMAT_CANDIDATE_CODE;
        int type = HrConstants.CommonKeyCodeTypeEnum.candidateCode.getValue();
        String objectId = null;
        String batchCode = "";
        int length = 6;
        CommonKeyCode commonKeyCode = commonKeyCodeRepository.getByType(type);
        if (commonKeyCode == null || commonKeyCode.getId() == null) {
            commonKeyCode = new CommonKeyCode();
            commonKeyCode.setObjectId(objectId);
            commonKeyCode.setType(type);
            commonKeyCode.setCurrentIndex(0);
        }
        Integer max = commonKeyCode.getCurrentIndex();// + 1;
        Long checkCode;
        do {
            max += 1;
            if (max >= Math.pow(10, length)) {//TH vuot qua 6 so
                length = String.valueOf(max).length(); // tăng độ dài theo số
                format = "%0" + length + "d";
            }

            batchCode = hardCode + String.format(format, max);
            checkCode = candidateRepository.checkCode(batchCode);
        } while (checkCode != null && checkCode > 0);

        commonKeyCode.setCurrentIndex(max);
        commonKeyCodeRepository.save(commonKeyCode);
        return batchCode;
    }

    @Override
    public String autoGenerateCode(String configKey) {
        SystemConfig systemConfig = systemConfigService.getConfigByConfigValue(configKey);
        if (systemConfig == null) {
            return "";
        } else {
            String maxCode = candidateRepository.findMaxCodeByPrefix(systemConfig.getConfigValue(), systemConfig.getNumberOfZero());
            return systemConfigService.generateNextCode(systemConfig.getConfigValue(), systemConfig.getNumberOfZero(), maxCode);
        }
    }

    @Override
    public Boolean resignMultiple(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        for (UUID id : ids) {
            Candidate candidate = candidateRepository.findById(id).orElse(null);
            if (candidate != null) {
                candidate.setStatus(HrConstants.CandidateStatus.RESIGN.getValue());
                candidateRepository.save(candidate);
            }
        }
        return true;
    }

    @Override
    public Workbook exportExcelCandidateReports(CandidateImport list) {
        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("ImportCandidateReports.xlsx")) {
            if (fileInputStream == null) {
                throw new IOException("File '" + "ImportCandidateReports.xlsx" + "' không tìm thấy trong classpath");
            }
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            CellStyle dataCellStyle = ExcelUtils.createDataCellStyle(workbook);
            long startTime = System.nanoTime();

            if (list.getErrors() != null && !list.getErrors().isEmpty()) {
                int rowIndex = 0;
                for (String err : list.getErrors()) {
                    rowIndex++;
                    Row dataRow = sheet.createRow(rowIndex);

                    ExcelUtils.createCell(dataRow, 0, rowIndex, dataCellStyle);
                    ExcelUtils.createCell(dataRow, 1, err, dataCellStyle);
                }

            } else {
                Row dataRow = sheet.createRow(1);
                ExcelUtils.createCell(dataRow, 0, 1, dataCellStyle);
                ExcelUtils.createCell(dataRow, 1, "Không có lỗi nào", dataCellStyle);
            }
            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;
            logger.info("Kết thúc xuất báo cáo tuyển dụng - Xử lý mất {} ms ", elapsedTimeMs);
            return workbook;
        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }
}
