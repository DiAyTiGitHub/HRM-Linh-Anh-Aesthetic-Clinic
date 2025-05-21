package com.globits.hr.service.impl;


import com.globits.core.domain.*;
import com.globits.core.dto.PersonAddressDto;
import com.globits.core.repository.*;
import com.globits.core.service.CountryService;
import com.globits.core.service.EthnicsService;
import com.globits.core.service.ReligionService;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.core.utils.SecurityUtils;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.function.ImportStaffDto;
import com.globits.hr.dto.importExcel.StaffBankAccountImport;
import com.globits.hr.dto.importExcel.StaffFamilyRelationshipImport;
import com.globits.hr.dto.importExcel.StaffLAImport;
import com.globits.hr.dto.importExcel.StaffWorkingLocationImport;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.dto.staff.StaffLabourManagementDto;
import com.globits.hr.dto.staff.StaffLabourUtilReportDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.*;
import com.globits.hr.utils.Const;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.hr.utils.ExportExcelUtil;
import com.globits.hr.utils.RoleUtils;
import com.globits.hrv3.dto.view.GeneralInformationDto;
import com.globits.hrv3.dto.view.ProfileInformationDto;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.domain.SalaryPeriod;
import com.globits.salary.domain.StaffSalaryItemValue;
import com.globits.salary.repository.SalaryItemRepository;
import com.globits.salary.repository.SalaryPeriodRepository;
import com.globits.salary.repository.StaffSalaryItemValueRepository;
import com.globits.salary.service.impl.CommissionPayrollServiceImpl;
import com.globits.security.domain.Role;
import com.globits.security.domain.User;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.security.repository.RoleRepository;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StaffServiceV3Impl extends GenericServiceImpl<Staff, UUID> implements StaffServiceV3 {
    private static final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffInsuranceHistoryRepository staffInsuranceHistoryRepository;

    @Autowired
    private StaffService staffService;

    @Autowired
    private StaffLabourAgreementService staffLabourAgreementService;

    @Autowired
    private StaffWorkingLocationRepository staffWorkingLocationRepository;

    @Autowired
    PersonAddressRepository personAddressRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ReligionRepository religionRepository;

    @Autowired
    private HrDocumentTemplateService hrDocumentTemplateService;

    @Autowired
    private EthnicsRepository ethnicsRepository;
    @Autowired
    AdministrativeUnitRepository administrativeUnitRepository;
    @Autowired
    HrAdministrativeUnitService hrAdministrativeUnitService;
    @Autowired
    HRDepartmentRepository hRDepartmentRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ContractTypeRepository contractTypeRepository;

    @Autowired
    StaffLabourAgreementService staffLabourAgreement;

    @Autowired
    LabourAgreementTypeService labourAgreementTypeService;

    @Autowired
    LabourAgreementTypeRepository labourAgreementTypeRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    StaffFamilyRelationshipRepository staffFamilyRelationshipRepository;

    @Autowired
    StaffLabourAgreementRepository labourAgreementRepository;

    @Autowired
    StaffEducationHistoryRepository staffEducationHistoryRepository;

    @Autowired
    StaffSalaryHistoryRepository staffSalaryHistoryRepository;

    @Autowired
    ProfessionalDegreeRepository professionalDegreeRepository;

    @Autowired
    InformaticDegreeRepository informaticDegreeRepository;

    @Autowired
    PoliticalTheoryLevelRepository politicalTheoryLevelRepository;

    @Autowired
    ProfessionRepository professionRepository;

    @Autowired
    AcademicTitleRepository academicTitleRepository;

    @Autowired
    EducationDegreeRepository educationDegreeRepository;

    @Autowired
    EmployeeStatusRepository employeeStatusRepository;

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    CivilServantTypeRepository civilServantTypeRepository;

    @Autowired
    CivilServantCategoryRepository civilServantCategoryRepository;

    @Autowired
    CivilServantGradeRepository civilServantGradeRepository;

    @Autowired
    HrSpecialityRepository hrSpecialityRepository;

    @Autowired
    HrEducationTypeRepository hrEducationTypeRepository;

    @Autowired
    CountryService countryService;

    @Autowired
    EthnicsService ethnicsService;

    @Autowired
    ReligionService religionService;

    @Autowired
    FamilyRelationshipRepository familyRelationshipRepository;

    @Autowired
    PersonCertificateRepository personCertificateRepository;

    @Autowired
    private StaffWorkingHistoryRepository staffWorkingHistoryRepository;

    @Autowired
    private HrDocumentTemplateRepository hrDocumentTemplateRepository;

    @Autowired
    private PositionTitleRepository positionTitleRepository;

    @Autowired
    private RankTitleRepository rankTitleRepository;

    @Autowired
    private HRDepartmentPositionRepository hrDepartmentPositionRepository;

    @Autowired
    private StaffDocumentItemRepository staffDocumentItemRepository;

    @Autowired
    private HrOrganizationRepository hrOrganizationRepository;

    @Autowired
    private StaffLabourAgreementRepository staffLabourAgreementRepository;

    @Autowired
    private PositionRelationShipRepository positionRelationShipRepository;

    @Autowired
    private StaffWorkScheduleService staffWorkScheduleService;

    @Autowired
    private UserExtService userExtService;

    @Autowired
    private StaffHierarchyService staffHierarchyService;

    @Autowired
    private SalaryPeriodRepository salaryPeriodRepository;

    @Autowired
    private StaffMaternityHistoryRepository staffMaternityHistoryRepository;

    @Autowired
    private ShiftWorkRepository shiftWorkRepository;

    @Autowired
    private StaffWorkScheduleRepository staffWorkScheduleRepository;

    @Autowired
    private StaffSalaryItemValueRepository staffSalaryItemValueRepository;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SalaryItemRepository salaryItemRepository;


    @Override
    public GeneralInformationDto saveOrUpdateGeneralInformation(UUID id, GeneralInformationDto staffDto) {
        if (staffDto == null) {
            return null;
        }
        Staff staff = null;
        if (id != null) {
            if (staffDto.getId() != null && !staffDto.getId().equals(id)) {
                return null;
            }
            Optional<Staff> optional = staffRepository.findById(id);
            if (optional.isPresent()) {
                staff = optional.get();
            }
        }
        if (staff == null) {
            staff = new Staff();
        }
        if (staffDto.getFirstName() != null) {
            staff.setFirstName(staffDto.getFirstName());
        }
        if (staffDto.getLastName() != null) {
            staff.setLastName(staffDto.getLastName());
        }
        if (staffDto.getBirthDate() != null) {
            staff.setBirthDate(staffDto.getBirthDate());
        }
        if (staffDto.getBirthPlace() != null) {
            staff.setBirthPlace(staffDto.getBirthPlace());
        }
        if (staffDto.getGender() != null) {
            staff.setGender(staffDto.getGender());
        }
        if (staffDto.getPhoto() != null) {
            staff.setPhoto(staffDto.getPhoto());
        }
        if (staffDto.getDisplayName() != null) {
            staff.setDisplayName(staffDto.getDisplayName());
        }
        if (staffDto.getPhoneNumber() != null) {
            staff.setPhoneNumber(staffDto.getPhoneNumber());
        }
        if (staffDto.getMaritalStatus() != null) {
            staff.setMaritalStatus(staffDto.getMaritalStatus());
        }
        staff.setImagePath(staffDto.getImagePath());
        staff.setPermanentResidence(staffDto.getPermanentResidence());
        staff.setCurrentResidence(staffDto.getCurrentResidence());
        if (staffDto.getEthnics() != null) {
            Ethnics ethnics = null;
            Optional<Ethnics> optional = ethnicsRepository.findById(staffDto.getEthnics().getId());
            if (optional.isPresent()) {
                ethnics = optional.get();
            }
            staff.setEthnics(ethnics);
        }
        if (staffDto.getNationality() != null) {
            Country nationality = null;
            Optional<Country> optional = countryRepository.findById(staffDto.getNationality().getId());
            if (optional.isPresent()) {
                nationality = optional.get();
            }
            staff.setNationality(nationality);
        }
        if (staffDto.getEmail() != null) {
            staff.setEmail(staffDto.getEmail());
        }
        if (staffDto.getReligion() != null && staffDto.getReligion().getId() != null) {
            Religion religion = null;
            Optional<Religion> optional = religionRepository.findById(staffDto.getReligion().getId());
            if (optional.isPresent()) {
                religion = optional.get();
            }
            staff.setReligion(religion);
        }
        if (staffDto.getIdNumber() != null)
            staff.setIdNumber(staffDto.getIdNumber());
        if (staffDto.getIdNumberIssueBy() != null)
            staff.setIdNumberIssueBy(staffDto.getIdNumberIssueBy());
        if (staffDto.getIdNumberIssueDate() != null)
            staff.setIdNumberIssueDate(staffDto.getIdNumberIssueDate());
        if (staffDto.getMaritalStatus() != null) {
            staff.setMaritalStatus(staffDto.getMaritalStatus());
        }
        User user = new User();
        if (staffDto.getUser() != null) {
            user.setUsername(staffDto.getUser().getUsername());
            LocalDateTime currentDate = LocalDateTime.now();
            String password = SecurityUtils.getHashPassword(staffDto.getUser().getPassword());
            user.setPassword(password);

            user.setPassword(password);
            user.setCreateDate(currentDate);
            user.setEmail(staffDto.getUser().getEmail());
            if (staffDto.getUser() != null && staffDto.getUser().getRoles() != null) {
                HashSet<Role> roles = new HashSet<>();
                if (user.getRoles() == null) {
                    user.setRoles(new HashSet<>());
                }
                for (RoleDto rDto : staffDto.getUser().getRoles()) {
                    Role role = null;
                    Optional<Role> optional = roleRepository.findById(rDto.getId());
                    if (optional.isPresent()) {
                        role = optional.get();
                    }
                    if (role != null) {
                        roles.add(role);
                    }
                }
                user.getRoles().clear();
                user.getRoles().addAll(roles);
            }

            user.setPerson(staff);
            staff.setUser(user);
        }
        if (staffDto.getAddress() != null && staffDto.getAddress().size() > 0) {
            HashSet<PersonAddress> temp = new HashSet<>();
            for (PersonAddressDto paDto : staffDto.getAddress()) {
                PersonAddress pa = null;
                if (paDto != null && paDto.getId() != null) {
                    Optional<PersonAddress> optional = personAddressRepository.findById(paDto.getId());
                    if (optional.isPresent()) {
                        pa = optional.get();
                    }
                }
                if (pa == null) {
                    pa = new PersonAddress();
                }
                if (paDto != null) {
                    pa.setType(paDto.getType());
                    pa.setAddress(paDto.getAddress());
                }
                pa.setPerson(staff);
                temp.add(pa);
            }
            if (staff.getAddress() == null) {
                staff.setAddress(new HashSet<>());
            }
            staff.getAddress().clear();
            staff.getAddress().addAll(temp);
        }
        staff = staffRepository.save(staff);
        return new GeneralInformationDto(staff);
    }

    @Override
    public GeneralInformationDto getGeneralInformation(UUID id) {
        if (id != null) {
            Staff staff = null;
            Optional<Staff> optional = staffRepository.findById(id);
            if (optional.isPresent()) {
                staff = optional.get();
            }
            if (staff != null) {
                return new GeneralInformationDto(staff);
            }
        }
        return null;
    }

    @Override
    public ProfileInformationDto saveOrUpdateProfileInformation(UUID id, ProfileInformationDto staffDto) {
        if (staffDto == null) {
            return null;
        }
        Staff staff = null;
        if (id != null) {
            if (staffDto.getId() != null && !staffDto.getId().equals(id)) {
                return null;
            }

        }

        if (staffDto.getId() != null) {
            Optional<Staff> optional = staffRepository.findById(staffDto.getId());
            if (optional.isPresent()) {
                staff = optional.get();
            }
        }

        if (staff == null && staffDto.getStaffCode() != null) {
            List<Staff> list = staffRepository.getByCode(staffDto.getStaffCode());
            if (list != null && !list.isEmpty()) {
                staff = list.get(0);
            }
        }
        if (staff == null) {
            staff = new Staff();
        }
        if (staffDto.getStaffCode() != null) {
            staff.setStaffCode(staffDto.getStaffCode());
        }
        staff.setCurrentWorkingStatus(staffDto.getCurrentWorkingStatus());
        staff.setSalaryCoefficient(staffDto.getSalaryCoefficient());
        staff.setSocialInsuranceNumber(staffDto.getSocialInsuranceNumber());
        staff.setJobTitle(staffDto.getJobTitle());
        staff.setHighestPosition(staffDto.getHighestPosition());
        staff.setDateOfReceivingPosition(staffDto.getDateOfReceivingPosition());
        staff.setDateOfReceivingAllowance(staffDto.getDateOfReceivingAllowance());
        staff.setProfessionalTitles(staffDto.getProfessionalTitles());
        staff.setAllowanceCoefficient(staffDto.getAllowanceCoefficient());
        staff.setSalaryLeve(staffDto.getSalaryLeve());
        staff.setPositionDecisionNumber(staffDto.getPositionDecisionNumber());

//        if (staffDto.getCivilServantCategory() != null) {
//            CivilServantCategory civilServantCategory = null;
//            Optional<CivilServantCategory> optional = civilServantCategoryRepository.findById(staffDto.getCivilServantCategory().getId());
//            if (optional.isPresent()) {
//                civilServantCategory = optional.get();
//            }
//            staff.setCivilServantCategory(civilServantCategory);
//        }
//
//        if (staffDto.getGrade() != null) {
//            CivilServantGrade grade = null;
//            Optional<CivilServantGrade> optional = civilServantGradeRepository.findById(staffDto.getGrade().getId());
//            if (optional.isPresent()) {
//                grade = optional.get();
//            }
//            staff.setGrade(grade);
//        }
//
//        if (staffDto.getProfession() != null) {
//            Profession profession = null;
//            Optional<Profession> optional = professionRepository.findById(staffDto.getProfession().getId());
//            if (optional.isPresent()) {
//                profession = optional.get();
//            }
//            staff.setProfession(profession);
//        }

        if (staffDto.getStatus() != null) {
            EmployeeStatus status = null;
            Optional<EmployeeStatus> optional = employeeStatusRepository.findById(staffDto.getStatus().getId());
            if (optional.isPresent()) {
                status = optional.get();
            }
            staff.setStatus(status);
        }
        if (staffDto.getDepartment() != null && staffDto.getDepartment().getId() != null) {
            HRDepartment department = null;
            Optional<HRDepartment> optional = hRDepartmentRepository.findById(staffDto.getDepartment().getId());
            if (optional.isPresent()) {
                department = optional.get();
            }
            staff.setDepartment(department);
        }
        if (staffDto.getLabourAgreementType() != null && staffDto.getLabourAgreementType().getId() != null) {
            LabourAgreementType labourAgreementType = null;
            Optional<LabourAgreementType> optional = labourAgreementTypeRepository.findById(staffDto.getLabourAgreementType().getId());
            if (optional.isPresent()) {
                labourAgreementType = optional.get();
            }
            staff.setLabourAgreementType(labourAgreementType);
        }
        if (staffDto.getContractDate() != null) {
            staff.setContractDate(staffDto.getContractDate());
        }
        if (staffDto.getJobTitle() != null) {
            staff.setJobTitle(staffDto.getJobTitle());
        }
        if (staffDto.getCivilServantType() != null) {
            CivilServantType type = null;
            Optional<CivilServantType> optional = civilServantTypeRepository.findById(staffDto.getCivilServantType().getId());
            if (optional.isPresent()) {
                type = optional.get();
            }
            staff.setCivilServantType(type);
        }
        staff.setStartDate(staffDto.getStartDate());
        staff.setRecruitmentDate(staffDto.getRecruitmentDate());
        staff.setSalaryStartDate(staffDto.getSalaryStartDate());
        staff = staffRepository.save(staff);
        return new ProfileInformationDto(staff);
    }

    @Override
    public ProfileInformationDto getProfileInformation(UUID id) {
        if (id != null) {
            Staff staff = null;
            Optional<Staff> optional = staffRepository.findById(id);
            if (optional.isPresent()) {
                staff = optional.get();
            }
            if (staff != null) {
                return new ProfileInformationDto(staff);
            }
        }
        return null;
    }


    private void handleAddPauseWorkingHistoryOfStaff(Staff staff, StaffLAImport importData) {
        List<StaffWorkingHistory> availableStaffWorkingHistories = staffWorkingHistoryRepository.findByTransferTypeStartDateOfStaff(HrConstants.StaffWorkingHistoryTransferType.PAUSE_TEMPORARY.getValue(),
                importData.getPauseDate(), staff.getId());

        StaffWorkingHistory pauseWorkingHistory = null;

        if (availableStaffWorkingHistories != null && !availableStaffWorkingHistories.isEmpty()) {
            pauseWorkingHistory = availableStaffWorkingHistories.get(0);
        } else {
            pauseWorkingHistory = new StaffWorkingHistory();
        }

        pauseWorkingHistory.setEndDate(importData.getReturnDate());
        pauseWorkingHistory.setStartDate(importData.getPauseDate());
        pauseWorkingHistory.setTransferType(HrConstants.StaffWorkingHistoryTransferType.PAUSE_TEMPORARY.getValue());
        pauseWorkingHistory.setNote(importData.getPauseReason());

        pauseWorkingHistory.setStaff(staff);

        if (staff.getStaffWorkingHistories() == null) {
            staff.setStaffWorkingHistories(new HashSet<>());
        }

        staff.getStaffWorkingHistories().add(pauseWorkingHistory);
    }

    private static void handleSetStaffPhase(Staff staff, StaffLAImport importData) {
        if (importData.getStaffPhase() == null) {
            staff.setStaffPhase(null);
            return;
        }

        switch (importData.getStaffPhase().trim()) {
            case "TV":
            case "Thử việc":
            case "Thử việc (TV)":
                staff.setStaffPhase(HrConstants.StaffPhase.PROBATION.getValue());
                break;

            case "HV":
            case "Học việc":
            case "Học việc (HV)":
                staff.setStaffPhase(HrConstants.StaffPhase.INTERN.getValue());
                break;

            case "CT":
            case "Chính thức":
            case "Chính thức (CT)":
                staff.setStaffPhase(HrConstants.StaffPhase.OFFICIAL.getValue());
                break;

            default:
                staff.setStaffPhase(null);
                break;
        }
    }

    private static void handleSetStaffGender(Staff staff, StaffLAImport importData) {
        if (importData.getGender() == null) {
            staff.setGender(null);
            return;
        }

        String genderInput = importData.getGender().trim().toLowerCase();

        switch (genderInput) {
            case "male":
            case "m":
            case "nam":
                staff.setGender("M");
                break;

            case "female":
            case "f":
            case "nu":
            case "nữ":
                staff.setGender("F");
                break;

            default:
                staff.setGender(null);
                break;
        }
    }

    public static void handleSetMaritalStatus(Staff staff, StaffLAImport importData) {
        if (importData.getMaritalStatus() == null) {
            staff.setMaritalStatus(null);
            return;
        }

        String maritalStatus = importData.getMaritalStatus().trim();

        switch (maritalStatus) {
            case "Độc thân":
                staff.setMaritalStatus(HrConstants.StaffMaritalStatus.SINGLE.getValue());
                break;
            case "Đính hôn":
                staff.setMaritalStatus(HrConstants.StaffMaritalStatus.ENGAGED.getValue());
                break;
            case "Đã kết hôn":
                staff.setMaritalStatus(HrConstants.StaffMaritalStatus.MARRIED.getValue());
                break;
            case "Ly thân":
                staff.setMaritalStatus(HrConstants.StaffMaritalStatus.SEPARATED.getValue());
                break;
            case "Đã ly hôn":
                staff.setMaritalStatus(HrConstants.StaffMaritalStatus.DIVORCED.getValue());
                break;
            case "Khác":
                staff.setMaritalStatus(HrConstants.StaffMaritalStatus.OTHERS.getValue());
                break;
            default:
                staff.setMaritalStatus(null);
                break;
        }
    }


    public static void handleSetDocumentStatus(Staff staff, StaffLAImport importData) {
        if (importData.getStaffDocumentStatus() == null) {
            staff.setStaffDocumentStatus(null);
            return;
        }

        String documentStatus = importData.getStaffDocumentStatus().trim();

        switch (documentStatus) {
            case "Chưa nộp hồ sơ":
            case "Chưa nộp":
                staff.setStaffDocumentStatus(HrConstants.StaffDocumentStatus.UNSUBMMITED.getValue());
                break;
            case "Thiếu hồ sơ":
            case "Thiếu":
                staff.setStaffDocumentStatus(HrConstants.StaffDocumentStatus.INCOMPLETED.getValue());
                break;
            case "Đủ hồ sơ":
            case "Đủ":
                staff.setStaffDocumentStatus(HrConstants.StaffDocumentStatus.COMPLETED.getValue());
                break;
            default:
                staff.setStaffDocumentStatus(null);
                break;
        }
    }


    public static void handleSetStaffWorkingFormat(Staff staff, StaffLAImport importData) {
        if (importData.getStaffWorkingFormat() == null) {
            staff.setStaffWorkingFormat(null);
            return;
        }

        String workingFormat = importData.getStaffWorkingFormat().trim();

        switch (workingFormat) {
            case "Cộng tác":
            case "cộng tác":
            case "CT":
                staff.setStaffWorkingFormat(HrConstants.StaffWorkingFormat.COLLABORATE.getValue());
                break;
            case "Bán thời gian (Part-time)":
            case "Part-time":
            case "part-time":
            case "parttime":
            case "Parttime":
            case "PT":
                staff.setStaffWorkingFormat(HrConstants.StaffWorkingFormat.PARTTIME.getValue());
                break;
            case "Toàn thời gian (Full-time)":
            case "Full-time":
            case "full-time":
            case "Fulltime":
            case "FT":
            case "ft":
                staff.setStaffWorkingFormat(HrConstants.StaffWorkingFormat.FULLTIME.getValue());
                break;
            default:
                staff.setStaffWorkingFormat(null);
                break;
        }
    }

    public void handleSetStaffWorkingLocation(Staff staff, StaffLAImport importData) {
        if (importData.getWorkingPlace() == null || !StringUtils.hasText(importData.getWorkingPlace())) {
            return;
        }

        StaffWorkingLocation workingLocation = null;

        List<StaffWorkingLocation> availableMainLocations = staffWorkingLocationRepository.findMainLocationByStaffId(staff.getId());
        if (availableMainLocations == null || availableMainLocations.isEmpty()) {
            workingLocation = new StaffWorkingLocation();
        } else {
            workingLocation = availableMainLocations.get(0);
        }

        workingLocation.setIsMainLocation(true);
        workingLocation.setWorkingLocation(importData.getWorkingPlace());
        workingLocation.setStaff(staff);


        if (staff.getStaffWorkingLocations() == null) {
            staff.setStaffWorkingLocations(new HashSet<>());
        }

        staff.getStaffWorkingLocations().add(workingLocation);

    }

    private HRDepartmentPosition handleGenerateBridgeDepartmentPositionTitle(HRDepartment department, PositionTitle positionTitle) {
        if (department == null || positionTitle == null) return null;

        List<HRDepartmentPosition> availableResults = hrDepartmentPositionRepository.findByDepartmentIdAndPositionTitleId(department.getId(), positionTitle.getId());

        if (availableResults != null && !availableResults.isEmpty()) return null;

        HRDepartmentPosition bridge = new HRDepartmentPosition();

        bridge.setDepartment(department);
        bridge.setPositionTitle(positionTitle);

        bridge = hrDepartmentPositionRepository.saveAndFlush(bridge);

        return bridge;
    }

    private boolean isHasDocumentTemplate(String message) {
        message = message.trim();

        switch (message) {
            case "Có":
            case "X":
            case "x":
            case "có":
            case "co":
            case "CO":
                return true;
        }

        return false;
    }

    private StaffDocumentItem getExistedStaffDocumentItem(Staff staff, HrDocumentItem documentItem) {
        if (staff == null || documentItem == null) return null;

        List<StaffDocumentItem> availableResults = staffDocumentItemRepository.findByStaffIdAndDocumentItemId(staff.getId(), documentItem.getId());

        if (availableResults != null && !availableResults.isEmpty())
            return availableResults.get(0);

        StaffDocumentItem staffDocumentItem = new StaffDocumentItem();

        staffDocumentItem.setDocumentItem(documentItem);
        staffDocumentItem.setStaff(staff);

        return staffDocumentItem;
    }


    private StaffWorkingHistory findPauseWorkingHistoryByStartDateOfStaff(Staff staff, Date pauseDate, Position position) {
        if (staff == null || staff.getStaffWorkingHistories() == null || pauseDate == null || staff.getStaffWorkingHistories().isEmpty())
            return null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String pauseDateStr = sdf.format(pauseDate);

        for (StaffWorkingHistory workingHistory : staff.getStaffWorkingHistories()) {
            if (workingHistory.getStartDate() != null) {
                if (workingHistory.getTransferType() == null || !workingHistory.getTransferType().equals(HrConstants.StaffWorkingHistoryTransferType.PAUSE_TEMPORARY.getValue()))
                    continue;

                // Normalize both dates to remove time
                String startDateStr = sdf.format(workingHistory.getStartDate());

                if (startDateStr.equals(pauseDateStr)
//                        && position == null
                ) {
                    return workingHistory;
                }
//                else if (startDateStr.equals(pauseDateStr) && position != null && workingHistory.getFromPosition() != null && workingHistory.getFromPosition().getId().equals(position.getId())) {
//                    return workingHistory;
//                }
            }
        }

        return null;

    }

    private void handleForPositionOrWorkingHistory(Staff staff, Position position, StaffLAImport importData) {
        if (position == null || staff == null) return;

        // Nếu trạng thái làm việc của nhân viên là đã nghỉ việc hoặc không nhận việc
        // => Vẫn tạo 1 staffWorkingHistory với transferType = 3 (tạm nghỉ)
        if (staff.getStatus() != null && (
                staff.getStatus().getCode().equals(HrConstants.EmployeeStatusCodeEnum.QUITED.getValue())
                        || staff.getStatus().getCode().equals(HrConstants.EmployeeStatusCodeEnum.NOT_RECEIVE_JOB.getValue())
        )) {

            StaffWorkingHistory workingHistory = this.findPauseWorkingHistoryByStartDateOfStaff(staff, importData.getPauseDate(), position);

            if (workingHistory == null) {
                // Chưa đủ dữ liệu tạo lịch sử làm việc của nhân viên
                workingHistory = new StaffWorkingHistory();
            }

            workingHistory.setNote(importData.getPauseReason());
            workingHistory.setStaff(staff);
            workingHistory.setTransferType(HrConstants.StaffWorkingHistoryTransferType.PAUSE_TEMPORARY.getValue());
            workingHistory.setStartDate(importData.getPauseDate());
            workingHistory.setEndDate(importData.getReturnDate());
            workingHistory.setFromPosition(position);


            if (staff.getStaffWorkingHistories() == null) {
                staff.setStaffWorkingHistories(new HashSet<>());
            }

            staff.getStaffWorkingHistories().add(workingHistory);


//            if (position.getFromPositionWorkingHistories() == null) {
//                position.setFromPositionWorkingHistories(new HashSet<>());
//            }
//
//            position.getFromPositionWorkingHistories().add(workingHistory);
            position.setStaff(null);

//            position = positionRepository.saveAndFlush(position);


            if (staff.getCurrentPositions() == null) {
                staff.setCurrentPositions(new HashSet<>());
            }

            if (staff.getCurrentPositions() != null && !staff.getCurrentPositions().isEmpty()) {
                for (Position other : staff.getCurrentPositions()) {
                    other.setIsMain(false);
                    other.setStaff(null);
                }
            }

            staff.getCurrentPositions().clear();


        }
        // Nếu nhân viên không phải 2 trường hợp trên => Tạo position và gán cho nhân viên, có chức vụ là chức vụ chính
        else {
            if (staff.getCurrentPositions() == null) {
                staff.setCurrentPositions(new HashSet<>());
            }

            if (staff.getCurrentPositions() != null && !staff.getCurrentPositions().isEmpty()) {
                for (Position other : staff.getCurrentPositions()) {
                    other.setIsMain(false);
                }
            }

            staff.getCurrentPositions().add(position);

            position.setStaff(staff);
            position.setIsMain(true);
        }
    }

    private PositionTitle handleCreateNewPositionTitleFromExcel(PositionTitle rankGroup, RankTitle rankTitle, HRDepartment department, PositionTitle positionTitle, StaffLAImport importData, List<String> errorMessages) {
        if (rankGroup == null) {
            errorMessages.add("Chưa có dữ liệu nhóm ngạch cho chức danh");
            return null;
        }

        if (rankTitle == null) {
            errorMessages.add("Chưa có dữ liệu cấp bậc cho chức danh");
            return null;
        }

        if (department == null) {
            errorMessages.add("Chưa có dữ liệu phòng ban cho chức danh");
            return null;
        }

        positionTitle = new PositionTitle();

        positionTitle.setParent(rankGroup);
        positionTitle.setRankTitle(rankTitle);

        positionTitle.setCode(importData.getPositionTitleCode());
        positionTitle.setName(importData.getPositionTitleName());
        positionTitle.setOtherName(importData.getPositionTitleName());
        positionTitle.setShortName(importData.getPositionTitleCode());

        positionTitle = positionTitleRepository.saveAndFlush(positionTitle);

        HRDepartmentPosition generatedBrigde = handleGenerateBridgeDepartmentPositionTitle(department, positionTitle);


        return positionTitle;
    }

    private void handleGenerateAdministrativeUnitErrorMessages(StaffLAImport importData, List<String> errorMessages) {
        // 36. Mã Tỉnh_Thường trú
        // 37. Tỉnh_Thường trú
        // 38. Mã Huyện_Thường trú
        // 39. Huyện_Thường trú
        // 40. Mã Xã_Thường trú
        // 41. Xã_Thường trú
//        if (importData.getWardName() != null && StringUtils.hasText(importData.getWardName())) {
//            if (importData.getWardCode() == null || !StringUtils.hasText(importData.getWardCode())) {
//                errorMessages.add("Chưa có mã phường");
//            }
//        }

        if (importData.getDistrictName() != null && StringUtils.hasText(importData.getDistrictName())) {
            if (importData.getDistrictCode() == null || !StringUtils.hasText(importData.getDistrictCode())) {
                errorMessages.add("Chưa có mã huyện");
            }
        }

        if (importData.getProvinceName() != null && StringUtils.hasText(importData.getProvinceName())) {
            if (importData.getProvinceCode() == null || !StringUtils.hasText(importData.getProvinceCode())) {
                errorMessages.add("Chưa có mã tỉnh");
            }
        }
    }

    @Override
    @Transactional
    public List<StaffLAImport> saveStaffLAImportFromExcel(List<StaffLAImport> staffImportData, boolean isCreateNew) {
        List<StaffLAImport> response = new ArrayList<>();

        HrDocumentTemplateDto commonTemplateDto = hrDocumentTemplateService.getByCode(HrConstants.COMMON_STAFF_PROFILE_TEMPLATE_CODE);

        List<HrDocumentItemDto> documentItemsDto = new ArrayList<>();
        if (commonTemplateDto.getDocumentItems() != null) {
            documentItemsDto = commonTemplateDto.getDocumentItems();
        }

        Integer wardLevel = HrConstants.AdministrativeLevel.COMMUNE.getValue();
        Integer districtLevel = HrConstants.AdministrativeLevel.DISTRICT.getValue();
        Integer provinceLevel = HrConstants.AdministrativeLevel.PROVINCE.getValue();

        for (StaffLAImport importData : staffImportData) {
            Staff staff = null;
            List<String> errorMessages = new ArrayList<>();

            if (importData.getStaffCode() == null || !StringUtils.hasText(importData.getStaffCode())) {
                errorMessages.add("Mã nhân viên là trường bắt buộc");
//                continue;
            }

            List<Staff> availableStaffs = staffRepository.findByCode(importData.getStaffCode());
            if (availableStaffs != null && !availableStaffs.isEmpty()) {
                if (isCreateNew) {
                    errorMessages.add("Đã tồn tại mã nhân viên");
                }
                staff = availableStaffs.get(0);
                staff.setVoided(null);
            }

            if (staff == null) {
                // Nếu không tìm thấy staff thì kiểm tra thêm tên
                boolean hasValidName = StringUtils.hasText(importData.getFirstName()) && StringUtils.hasText(importData.getLastName());

                if (hasValidName) {
                    staff = new Staff();
                }
            }

            if (staff == null) {
                errorMessages.add("Chưa nhập dữ liệu họ và tên nhân viên");
                String finalMessage = String.join(". ", errorMessages);
                importData.setErrorMessage(finalMessage);
                response.add(importData);
                continue;
            }

            // 1. Mã nhân viên
            // 2. Họ và tên nhân viên
            if (StringUtils.hasText(importData.getDisplayName())) {
                staff.setDisplayName(importData.getDisplayName());
            } else {
                errorMessages.add("Họ và tên là trường bắt buộc");
            }
            staff.setStaffCode(importData.getStaffCode());
            staff.setFirstName(importData.getFirstName());
            staff.setLastName(importData.getLastName());

            // 3. Mã trạng thái làm việc
            // 4. Trạng thái làm việc
            EmployeeStatus employeeStatus = null;
            if (StringUtils.hasText(importData.getEmployeeStatusCode())) {
                List<EmployeeStatus> employeeStatusList = employeeStatusRepository.findByCode(importData.getEmployeeStatusCode());

                if (employeeStatusList != null && !employeeStatusList.isEmpty()) {
                    employeeStatus = employeeStatusList.get(0);
                } else if (importData.getEmployeeStatusCode() != null && StringUtils.hasText(importData.getEmployeeStatusCode())
                        && importData.getEmployeeStatusName() != null && StringUtils.hasText(importData.getEmployeeStatusName())) {
                    employeeStatus = new EmployeeStatus();

                    employeeStatus.setCode(importData.getEmployeeStatusCode());
                    employeeStatus.setName(importData.getEmployeeStatusName());

                    employeeStatus = employeeStatusRepository.save(employeeStatus);
                } else {
                    errorMessages.add("Dữ liệu trạng thái nhân viên không hợp lệ");
//                    continue;
                }
            }
            staff.setStatus(employeeStatus);

            // 5. Ngày vào
            staff.setRecruitmentDate(importData.getRecruitmentDate());

            // 6. Ngày đi làm lại
            // 7. Ngày tạm dừng/nghỉ việc
            // 8. Lí do nghỉ việc
            if (importData.getReturnDate() != null || importData.getPauseDate() != null) {
                this.handleAddPauseWorkingHistoryOfStaff(staff, importData);
            } else if (importData.getPauseDate() == null && importData.getReturnDate() == null && StringUtils.hasText(importData.getPauseReason())) {
                errorMessages.add("Chưa có ngày nghỉ việc, ngày đi làm lại nhưng lại có lý do nghỉ việc");
//                continue;
            }

            // 9. Mã Ban/ Chi Nhánh
            // 10. Ban/ Chi Nhánh
            HRDepartment department = null;
            if (StringUtils.hasText(importData.getDepartmentCode())) {
                List<HRDepartment> departmentList = hRDepartmentRepository.findByCode(importData.getDepartmentCode());

                if (departmentList != null && !departmentList.isEmpty()) {
                    department = departmentList.get(0);
                } else if (importData.getDepartmentCode() != null && StringUtils.hasText(importData.getDepartmentCode())
                        && importData.getDepartmentName() != null && StringUtils.hasText(importData.getDepartmentName())) {
                    department = new HRDepartment();

                    department.setCode(importData.getDepartmentCode());
                    department.setName(importData.getDepartmentName());

                    department = hRDepartmentRepository.save(department);
                } else {
                    errorMessages.add("Dữ liệu phòng ban không hợp lệ");
//                    continue;
                }
            }
            staff.setDepartment(department);

            // 11. Mã nhóm ngạch
            // 12. Nhóm Ngạch
            PositionTitle rankGroup = null;
            if (StringUtils.hasText(importData.getRankGroupCode())) {
                List<PositionTitle> rankGroupList = positionTitleRepository.findByCode(importData.getRankGroupCode());

                if (rankGroupList != null && !rankGroupList.isEmpty()) {
                    rankGroup = rankGroupList.get(0);
                } else if (importData.getRankGroupCode() != null && StringUtils.hasText(importData.getRankGroupCode())
                        && importData.getRankGroupName() != null && StringUtils.hasText(importData.getRankGroupName())) {
                    rankGroup = new PositionTitle();

                    rankGroup.setCode(importData.getRankGroupCode());
                    rankGroup.setName(importData.getRankGroupName());

                    rankGroup = positionTitleRepository.save(rankGroup);
                } else {
                    errorMessages.add("Dữ liệu nhóm ngạch không hợp lệ");
//                    continue;
                }
            }

            // 13. Cấp bậc (Level)
            RankTitle rankTitle = null;
            if (StringUtils.hasText(importData.getRankTitleCode())) {
                List<RankTitle> rankTitleList = rankTitleRepository.findByShortName(importData.getRankTitleCode());

                if (rankTitleList != null && !rankTitleList.isEmpty()) {
                    rankTitle = rankTitleList.get(0);
                } else if (importData.getRankTitleCode() != null && StringUtils.hasText(importData.getRankTitleCode())) {
                    rankTitle = new RankTitle();

                    rankTitle.setShortName(importData.getRankTitleCode());
                    rankTitle.setName(importData.getRankTitleCode());
                    rankTitle.setOtherName(importData.getRankTitleCode());

                    rankTitle = rankTitleRepository.saveAndFlush(rankTitle);
                } else {
                    errorMessages.add("Dữ liệu cấp bậc không hợp lệ");
//                    continue;
                }
            }

            // 14. Mã Chức danh
            // 15. Chức danh
            PositionTitle positionTitle = null;
            if (StringUtils.hasText(importData.getPositionTitleCode())) {
                List<PositionTitle> positionTitleList = positionTitleRepository.findByCode(importData.getPositionTitleCode());

                if (positionTitleList != null && !positionTitleList.isEmpty()) {
                    positionTitle = positionTitleList.get(0);

                    if (department == null) {
                        errorMessages.add("Chưa có dữ liệu phòng ban cho chức danh");
//                        continue;
                    }

                    HRDepartmentPosition generatedBrigde = handleGenerateBridgeDepartmentPositionTitle(department, positionTitle);
                } else if (importData.getPositionTitleCode() != null && StringUtils.hasText(importData.getPositionTitleCode())
                        && importData.getPositionTitleName() != null && StringUtils.hasText(importData.getPositionTitleName())) {
                    positionTitle = this.handleCreateNewPositionTitleFromExcel(rankGroup, rankTitle, department, positionTitle, importData, errorMessages);

                } else {
                    errorMessages.add("Dữ liệu chức danh không hợp lệ");
//                    continue;
                }
            }
            // 16. Mã chức vụ
            // 17. Chức vụ
            Position position = null;
            if (StringUtils.hasText(importData.getPositionCode())) {
                List<Position> positionList = positionRepository.findByCode(importData.getPositionCode());

                if (positionList != null && !positionList.isEmpty()) {
                    position = positionList.get(0);

                    if (position.getStaff() != null && !position.getStaff().getStaffCode().equals(staff.getStaffCode())) {
                        errorMessages.add("Vị trí đã có nhân viên đảm nhiệm");
                    }

                    if (department == null) {
                        errorMessages.add("Chưa có dữ liệu phòng ban cho vị trí công tác");
//                        continue;
                    }

                    if (positionTitle == null) {
                        errorMessages.add("Chưa có dữ liệu chức vụ cho vị trí công tác");
//                        continue;
                    }

                    if (department != null && positionTitle != null) {
                        position.setTitle(positionTitle);
                        position.setDepartment(department);
                    }

                    position = positionRepository.save(position);

                } else if (importData.getPositionCode() != null && StringUtils.hasText(importData.getPositionCode())
                        && importData.getPositionName() != null && StringUtils.hasText(importData.getPositionName())) {
                    if (department == null) {
                        errorMessages.add("Chưa có dữ liệu phòng ban cho vị trí công tác");
//                        continue;
                    }

                    if (positionTitle == null) {
                        errorMessages.add("Chưa có dữ liệu chức vụ cho vị trí công tác");
//                        continue;
                    }

                    if (department != null && positionTitle != null) {
                        position = new Position();

                        position.setCode(importData.getPositionCode());
                        position.setName(importData.getPositionName());
                        position.setDescription(importData.getPositionName());


                        position.setTitle(positionTitle);
                        position.setDepartment(department);

                        position = positionRepository.save(position);
                    }

                } else {
                    errorMessages.add("Dữ liệu vị trí công tác chưa tồn tại, vui lòng tạo vị trí công tác trước");
//                    continue;
                }
            }

            staff = staffRepository.save(staff);
            if (position != null && position.getStaff() == null) {
                // Xử lý trường hợp tạo và gán chức vụ cho nhân viên hay chỉ tạo vị trí công tác cho nhân viên
                this.handleForPositionOrWorkingHistory(staff, position, importData);
            }

            // 18. Hình thức làm việc
            handleSetStaffWorkingFormat(staff, importData);

            // 19. Mã Khu vực/Phòng
//            private String areaCode;
            // 20. Khu vực/Phòng
//            private String area;

            // 21. Địa điểm làm việc
//            handleSetStaffWorkingLocation(staff, importData);

            // 22. Mã nhân viên Quản lý trực tiếp
            // 23. Quản lý trực tiếp
            Staff supervisor = null;
            if (importData.getSupervisorCode() != null && StringUtils.hasText(importData.getSupervisorCode())) {
                List<Staff> availableSupervisors = staffRepository.findByCode(importData.getSupervisorCode());

                if (availableSupervisors != null && !availableSupervisors.isEmpty()) {
                    supervisor = availableSupervisors.get(0);
                } else {
                    errorMessages.add("Chưa tồn tại nhân viên quản lý trực tiếp");
                }
            }

            if (staff.getStatus() != null && (
                    !staff.getStatus().getCode().equals(HrConstants.EmployeeStatusCodeEnum.QUITED.getValue())
                            && !staff.getStatus().getCode().equals(HrConstants.EmployeeStatusCodeEnum.NOT_RECEIVE_JOB.getValue())
            )) {
                // Tạo/cập nhật mối quan hệ quản lý trực tiếp
                if (supervisor != null && position != null) {
                    PositionRelationShip positionRelationShip = null;

                    List<PositionRelationShip> availableRelationships = positionRelationShipRepository.findByPositionIdSupervisorPositionStaffIdAndRelationshipType(position.getId(), supervisor.getId(), HrConstants.PositionRelationshipType.UNDER_DIRECT_MANAGEMENT.getValue());

                    if (availableRelationships == null || availableRelationships.isEmpty()) {
                        positionRelationShip = new PositionRelationShip();

                        Position supervisorPosition = null;
                        List<Position> supervisorPositions = positionRepository.findMainPositionByStaffId(supervisor.getId());
                        if (supervisorPositions != null && !supervisorPositions.isEmpty()) {
                            supervisorPosition = supervisorPositions.get(0);
                        }

                        if (supervisorPosition != null) {
                            positionRelationShip.setSupervisor(supervisorPosition);
                            positionRelationShip.setPosition(position);
                            positionRelationShip.setRelationshipType(HrConstants.PositionRelationshipType.UNDER_DIRECT_MANAGEMENT.getValue());

                            if (position.getRelationships() == null) {
                                position.setRelationships(new HashSet<>());
                            }

                            position.getRelationships().add(positionRelationShip);
                        }

                    }
                }
            }
// 24. Email công ty
            String companyEmail = importData.getCompanyEmail();
            staff.setCompanyEmail(companyEmail);

// 25. Email cá nhân
            String privateEmail = importData.getPrivateEmail();
            if (StringUtils.hasText(privateEmail)) {
                staff.setEmail(privateEmail); // Ưu tiên dùng email cá nhân làm email liên hệ chính
            } else if (StringUtils.hasText(companyEmail)) {
                staff.setEmail(companyEmail); // Nếu không có email cá nhân thì dùng email công ty
            } else {
                errorMessages.add("Cần nhập ít nhất một trong hai: Email cá nhân hoặc Email công ty.");
            }


            // 26. Tình trạng: TV-Thử việc, CT-Chính thức, HV-Học việc
            this.handleSetStaffPhase(staff, importData);

            // 28. Mã Công ty ký HĐ
            // 29. Công ty ký HĐ
            HrOrganization contractOrg = null;
            if (StringUtils.hasText(importData.getContractCompanyCode())) {
                List<HrOrganization> organizationList = hrOrganizationRepository.findByCode(importData.getContractCompanyCode());

                if (organizationList != null && !organizationList.isEmpty()) {
                    contractOrg = organizationList.get(0);
                } else if (importData.getContractCompanyCode() != null && StringUtils.hasText(importData.getContractCompanyCode())
                        && importData.getContractCompanyName() != null && StringUtils.hasText(importData.getContractCompanyName())) {
                    contractOrg = new HrOrganization();

                    contractOrg.setCode(importData.getContractCompanyCode());
                    contractOrg.setName(importData.getContractCompanyName());

                    contractOrg = hrOrganizationRepository.save(contractOrg);
                } else {
                    errorMessages.add("Đơn vị ký hợp đồng không hợp lệ");
//                    continue;
                }
            }
//            staff.setOrganization(contractOrg);

            // 31. Số ngày HV/TV
            if (importData.getLabourDays() != null) {
                staff.setApprenticeDays(importData.getLabourDays());
            } else {
                errorMessages.add("Số ngày HV/TV là bắt buộc");
            }

            // 27. Số HĐ (TV/HV)
            // 30. Ngày bắt đầu (HV/TV)
            // 32. Ngày kết thúc (HV/TV)
            if (StringUtils.hasText(importData.getLabourAgreementNumber())) {
                List<StaffLabourAgreement> availableAgreements = staffLabourAgreementRepository.findByStaffIdAndAgreementNumber(staff.getId(), importData.getLabourAgreementNumber().trim());

                StaffLabourAgreement staffLabourAgreement = null;

                if (availableAgreements != null && !availableAgreements.isEmpty()) {
                    staffLabourAgreement = availableAgreements.get(0);
                } else {
                    staffLabourAgreement = new StaffLabourAgreement();
                }

                if (importData.getRecruitmentDate() != null) {
                    // Xử lý ngày bắt đầu hợp đồng
                    if (importData.getLabourDays() != null) {
                        try {
                            // Tạo đối tượng Calendar để thêm ngày
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(importData.getRecruitmentDate());
                            calendar.add(Calendar.DAY_OF_MONTH, importData.getLabourDays());

                            Date calculateDate = calendar.getTime();
                            staff.setStartDate(calculateDate);
                        } catch (Exception e) {
                            // Xử lý lỗi nếu có (log lỗi, thêm vào errorMessages,...)
//                            errorMessages.add("Lỗi tính toán ngày bắt đầu chính thức");
                            staff.setStartDate(importData.getStartLabourAgreementDate());
                        }
                    } else {
                        // Nếu không có labourDays thì sử dụng ngày nhập vào
                        staff.setStartDate(importData.getStartLabourAgreementDate());
                    }
                }
                staffLabourAgreement.setStartDate(importData.getStartLabourAgreementDate());
                staffLabourAgreement.setEndDate(importData.getEndLabourAgreementDate());
                staffLabourAgreement.setContractOrganization(contractOrg);
                staffLabourAgreement.setLabourAgreementNumber(importData.getLabourAgreementNumber());

                staffLabourAgreement.setStaff(staff);

                if (staff.getAgreements() == null) {
                    staff.setAgreements(new HashSet<>());
                }

                staff.getAgreements().add(staffLabourAgreement);
            }

            // 33. SĐT
            if (importData.getPhoneNumber() != null) {
                staff.setPhoneNumber(importData.getPhoneNumber());
            } else {
                errorMessages.add("SĐT là thông tin bắt buộc");
            }

            // 34. Ngày sinh
            staff.setBirthDate(importData.getBirthDate());

            // 35. Giới tính
            this.handleSetStaffGender(staff, importData);

            // 36. Mã Tỉnh_Thường trú
            // 37. Tỉnh_Thường trú
            // 38. Mã Huyện_Thường trú
            // 39. Huyện_Thường trú
            // 40. Mã Xã_Thường trú
            // 41. Xã_Thường trú
            AdministrativeUnit administrativeUnit = null;

// Xã (không bắt buộc)
            if (importData.getWardCode() != null && StringUtils.hasText(importData.getWardCode())) {
                List<AdministrativeUnit> availableAdministratives = hrAdministrativeUnitService
                        .getAdministrativeUnitByCodeAndLevel(importData.getWardCode(), wardLevel);
                if (availableAdministratives != null && !availableAdministratives.isEmpty()) {
                    administrativeUnit = availableAdministratives.get(0);
                } else {
                    errorMessages.add("Không tồn tại xã tương ứng");
                }
            }

// Huyện (bắt buộc)
            if (administrativeUnit == null && StringUtils.hasText(importData.getDistrictCode())) {
                List<AdministrativeUnit> availableAdministratives = hrAdministrativeUnitService
                        .getAdministrativeUnitByCodeAndLevel(importData.getDistrictCode(), districtLevel);
                if (availableAdministratives != null && !availableAdministratives.isEmpty()) {
                    administrativeUnit = availableAdministratives.get(0);
                } else {
                    errorMessages.add("Không tồn tại huyện tương ứng");
                }
            }
//            else if (!StringUtils.hasText(importData.getDistrictCode())) {
//                errorMessages.add("Huyện thường trú là trường bắt buộc");
//            }

// Tỉnh (bắt buộc)
            if (administrativeUnit == null && StringUtils.hasText(importData.getProvinceCode())) {
                List<AdministrativeUnit> availableAdministratives = hrAdministrativeUnitService
                        .getAdministrativeUnitByCodeAndLevel(importData.getProvinceCode(), provinceLevel);
                if (availableAdministratives != null && !availableAdministratives.isEmpty()) {
                    administrativeUnit = availableAdministratives.get(0);
                } else {
                    errorMessages.add("Không tồn tại tỉnh tương ứng");
                }
            }
//            else if (!StringUtils.hasText(importData.getProvinceCode())) {
//                errorMessages.add("Tỉnh thường trú là trường bắt buộc");
//            }


            // Tạo message lỗi đơn vị hành chính
//            this.handleGenerateAdministrativeUnitErrorMessages(importData, errorMessages);

            staff.setAdministrativeUnit(administrativeUnit);

            // 42. Chi tiết_Thường trú
            staff.setPermanentResidence(importData.getPermanentResidence());

            // 43. Thường trú chi tiết

            // 44. Tạm trú
            staff.setCurrentResidence(importData.getCurrentResidence());

            // 45. CMND
            String cccd = importData.getIdNumber();
            Date cccdIssueDate = importData.getIdNumberIssueDate();
            String cccdIssuePlace = importData.getIdNumberIssueBy();

            String cmnd = importData.getPersonalIdentificationNumber();
            Date cmndIssueDate = importData.getIdNumberIssueDate();
            String cmndIssuePlace = importData.getPersonalIdentificationNumber();

            if ((cccd != null && !cccd.trim().isEmpty())) {
//                if (cccdIssueDate == null || cccdIssuePlace == null || cccdIssuePlace.trim().isEmpty()) {
//                    errorMessages.add("Phải nhập đầy đủ ngày cấp và nơi cấp cho CCCD.");
//                }
            } else if ((cmnd != null && !cmnd.trim().isEmpty())) {
//                if (cmndIssueDate == null || cmndIssuePlace == null || cmndIssuePlace.trim().isEmpty()) {
//                    errorMessages.add("Phải nhập đầy đủ ngày cấp và nơi cấp cho CMND.");
//                }
            } else {
                errorMessages.add("Cần nhập ít nhất CCCD hoặc CMND.");
            }

            // 46. (CMND 9/12)
//            if (importData.getIdNumber() != null && !importData.getIdNumber().isEmpty()) {
//                List<Staff> availableIdNumberStaffs = staffRepository.findByIdNumber(importData.getIdNumber());
//                if (availableIdNumberStaffs != null && !availableIdNumberStaffs.isEmpty()) {
//                    errorMessages.add("Đã tồn tại CMND");
//                }
//            }
            staff.setIdNumber(importData.getIdNumber());

            // 47. Ngày cấp
            staff.setIdNumberIssueDate(importData.getIdNumberIssueDate());

            // 48. Nơi cấp
            staff.setIdNumberIssueBy(importData.getIdNumberIssueBy());

            // 46+. (CCCD 9/12)
            if (importData.getPersonalIdentificationNumber() != null && !importData.getPersonalIdentificationNumber().isEmpty()) {
                List<Staff> availablePersonalIdStaffs = staffRepository.findByIdPersonalIdentificationNumber(importData.getPersonalIdentificationNumber());
                boolean isUsedByOther = false;

                for (Staff s : availablePersonalIdStaffs) {
                    if (!s.getStaffCode().equals(staff.getStaffCode())) {
                        isUsedByOther = true;
                        break;
                    }
                }

                if (availablePersonalIdStaffs != null && availablePersonalIdStaffs.size() > 0 && isCreateNew) {
                    errorMessages.add("Đã có nhân viên sử dụng CCCD");
                } else if (isUsedByOther) {
                    errorMessages.add("Đã tồn tại CCCD");
                }
            }

            staff.setPersonalIdentificationNumber(importData.getPersonalIdentificationNumber());

            // 47+. Ngày cấp CCCD
            staff.setPersonalIdentificationIssueDate(importData.getPersonalIdentificationIssueDate());


            // 48+. Nơi cấp CCCD
            staff.setPersonalIdentificationIssuePlace(importData.getPersonalIdentificationIssuePlace());


            // 49. Tình trạng hôn nhân
            this.handleSetMaritalStatus(staff, importData);

            // 50. Mã Dân tộc
            // 51. Dân tộc
            Ethnics ethnics = null;
            if (StringUtils.hasText(importData.getEthnicCode())) {
                List<Ethnics> ethnicsList = ethnicsRepository.findListByCode(importData.getEthnicCode());

                if (ethnicsList != null && !ethnicsList.isEmpty()) {
                    ethnics = ethnicsList.get(0);
                } else if (importData.getEthnicCode() != null && StringUtils.hasText(importData.getEthnicCode())
                        && importData.getEthnicStringName() != null && StringUtils.hasText(importData.getEthnicStringName())) {
                    ethnics = new Ethnics();

                    ethnics.setCode(importData.getEthnicCode());
                    ethnics.setName(importData.getEthnicStringName());

                    ethnics = ethnicsRepository.save(ethnics);
                } else {
                    errorMessages.add("Dữ liệu dân tộc nhân viên không hợp lệ");
                }
            }
            staff.setEthnics(ethnics);

            // 52. Mã Tôn giáo
            // 53. Tôn giáo
            Religion religion = null;
            if (StringUtils.hasText(importData.getReligionCode())) {
                List<Religion> religionList = religionRepository.findListByCode(importData.getReligionCode());

                if (religionList != null && !religionList.isEmpty()) {
                    religion = religionList.get(0);
                } else if (importData.getReligionCode() != null && StringUtils.hasText(importData.getReligionCode())
                        && importData.getReligionName() != null && StringUtils.hasText(importData.getReligionName())) {
                    religion = new Religion();

                    religion.setCode(importData.getReligionCode());
                    religion.setName(importData.getReligionName());

                    religion = religionRepository.save(religion);
                } else {
                    errorMessages.add("Dữ liệu tôn giáo nhân viên không hợp lệ");
//                    continue;
                }
            }
            staff.setReligion(religion);

            // 54. Mã Quốc tịch
            // 55. Quốc tịch
            Country country = null;
            if (StringUtils.hasText(importData.getCountryCode())) {
                List<Country> countryList = countryRepository.findListByCode(importData.getCountryCode());

                if (countryList != null && !countryList.isEmpty()) {
                    country = countryList.get(0);
                } else if (importData.getCountryCode() != null && StringUtils.hasText(importData.getCountryCode())
                        && importData.getCountryName() != null && StringUtils.hasText(importData.getCountryName())) {
                    country = new Country();

                    country.setCode(importData.getCountryCode());
                    country.setName(importData.getCountryName());

                    country = countryRepository.save(country);
                } else {
                    errorMessages.add("Dữ liệu quốc tịch nhân viên không hợp lệ");
//                    continue;
                }
            } else {
                errorMessages.add("Quốc tịch là trường bắt buộc");
            }
            staff.setNationality(country);

            // 56. Quê quán
            staff.setBirthPlace(importData.getHomeTown());

            // 57. Mã trình độ học vấn
            // 58. Trình độ học vấn
            EducationDegree educationDegree = null;
            if (StringUtils.hasText(importData.getEducationDegreeCode())) {
                List<EducationDegree> educationDegreeList = educationDegreeRepository.findByCode(importData.getEducationDegreeCode());

                if (educationDegreeList != null && !educationDegreeList.isEmpty()) {
                    educationDegree = educationDegreeList.get(0);
                } else if (importData.getEducationDegreeCode() != null && StringUtils.hasText(importData.getEducationDegreeCode())
                        && importData.getEducationDegreeName() != null && StringUtils.hasText(importData.getEducationDegreeName())) {
                    educationDegree = new EducationDegree();

                    educationDegree.setCode(importData.getEducationDegreeCode());
                    educationDegree.setName(importData.getEducationDegreeName());

                    educationDegree = educationDegreeRepository.save(educationDegree);
                } else {
                    errorMessages.add("Dữ liệu trình độ học vấn không hợp lệ");
//                    continue;
                }
            }
            staff.setEducationDegree(educationDegree);

            // 59. Thông tin người liên hệ
            staff.setContactPersonInfo(importData.getContactPersonInfo());

            // 60. Mã số Thuế
            if (StringUtils.hasText(importData.getTaxCode())) {

                staff.setTaxCode(importData.getTaxCode());
            }

            // 61. Số Người phụ thuộc đã đăng ký (nếu có)

            // 62. Mã số Bảo hiểm xã hội
            staff.setSocialInsuranceNumber(importData.getSocialInsuranceNumber());

            // 63. Mã số Bảo Hiểm Y tế
            staff.setHealthInsuranceNumber(importData.getHealthInsuranceNumber());

            // 64. Tình trạng Sổ Bảo hiểm xã hội
            staff.setSocialInsuranceNote(importData.getSocialInsuranceNote());

            // 65. Nơi mong muốn đăng ký khám chữa bệnh
            staff.setDesireRegistrationHealthCare(importData.getDesireRegistrationHealthCare());

            // 66. Mã nhân viên người giới thiệu
            // 67. Nhân viên giới thiệu
            Staff introducer = null;
            if (StringUtils.hasText(importData.getIntroducerCode())) {
                List<Staff> availableIntroducers = staffRepository.findByCode(importData.getIntroducerCode());

                if (availableIntroducers != null && !availableIntroducers.isEmpty()) {
                    introducer = availableIntroducers.get(0);
                } else {
                    errorMessages.add("Không tìm thấy người giới thiệu");
                }
            }
            if (introducer != null)
                staff.setIntroducer(introducer);


            // 68. Mã NV tuyển
            // 69. Nhân viên tuyển
            Staff recruiter = null;
            if (StringUtils.hasText(importData.getRecruiterCode())) {
                List<Staff> availableRecruiters = staffRepository.findByCode(importData.getRecruiterCode());

                if (availableRecruiters != null && !availableRecruiters.isEmpty()) {
                    recruiter = availableRecruiters.get(0);
                } else {
                    errorMessages.add("Không tìm thấy nhân viên tuyển");
                }
            }
            if (recruiter != null)
                staff.setRecruiter(recruiter);


//            // 70. Hồ sơ
            if (documentItemsDto.size() >= 14 && StringUtils.hasText(importData.getDocumentTemplate()) && isHasDocumentTemplate(importData.getDocumentTemplate())) {
                HrDocumentTemplate commonTemplate = this.getCommonDocumentTemplateEntity();
                List<HrDocumentItem> documentItems = this.getCommonDocumentItemEntites(commonTemplate);

                List<StaffDocumentItem> staffDocumentItems = new ArrayList<>();

                // 71. Ảnh 3x4
                StaffDocumentItem image34Item = this.getExistedStaffDocumentItem(staff, documentItems.get(0));
                if (isHasDocumentTemplate(importData.getImage34Check())) {
                    image34Item.setIsSubmitted(true);
                } else {
                    image34Item.setIsSubmitted(false);
                }
                staffDocumentItems.add(image34Item);

                // 72. CMND/CCCD
                StaffDocumentItem idNumberItem = this.getExistedStaffDocumentItem(staff, documentItems.get(1));
                if (isHasDocumentTemplate(importData.getIdNumberCheck())) {
                    idNumberItem.setIsSubmitted(true);
                } else {
                    idNumberItem.setIsSubmitted(false);
                }
                staffDocumentItems.add(idNumberItem);

                // 73. Đơn ứng tuyển
                StaffDocumentItem applicationFormItem = this.getExistedStaffDocumentItem(staff, documentItems.get(2));
                if (isHasDocumentTemplate(importData.getApplicationFormCheck())) {
                    applicationFormItem.setIsSubmitted(true);
                } else {
                    applicationFormItem.setIsSubmitted(false);
                }
                staffDocumentItems.add(applicationFormItem);

                // 74. Sơ yếu lý lịch
                StaffDocumentItem profileItem = this.getExistedStaffDocumentItem(staff, documentItems.get(3));
                if (isHasDocumentTemplate(importData.getProfileCheck())) {
                    profileItem.setIsSubmitted(true);
                } else {
                    profileItem.setIsSubmitted(false);
                }
                staffDocumentItems.add(profileItem);

                // 75. Bằng cấp cao nhất
                StaffDocumentItem degreeItem = this.getExistedStaffDocumentItem(staff, documentItems.get(4));
                if (isHasDocumentTemplate(importData.getHighestDegreeCheck())) {
                    degreeItem.setIsSubmitted(true);
                } else {
                    degreeItem.setIsSubmitted(false);
                }
                staffDocumentItems.add(degreeItem);

                // 76. Chứng chỉ liên quan
                StaffDocumentItem certificateItem = this.getExistedStaffDocumentItem(staff, documentItems.get(5));
                if (isHasDocumentTemplate(importData.getRelatedCertificateCheck())) {
                    certificateItem.setIsSubmitted(true);
                } else {
                    certificateItem.setIsSubmitted(false);
                }
                staffDocumentItems.add(certificateItem);

                // 77. Giấy khám SK
                StaffDocumentItem healthCheckItem = this.getExistedStaffDocumentItem(staff, documentItems.get(6));
                if (isHasDocumentTemplate(importData.getHeathCheck())) {
                    healthCheckItem.setIsSubmitted(true);
                } else {
                    healthCheckItem.setIsSubmitted(false);
                }
                staffDocumentItems.add(healthCheckItem);

                // 78. SHK
                StaffDocumentItem shkItem = this.getExistedStaffDocumentItem(staff, documentItems.get(7));
                if (isHasDocumentTemplate(importData.getShkCheck())) {
                    shkItem.setIsSubmitted(true);
                } else {
                    shkItem.setIsSubmitted(false);
                }
                staffDocumentItems.add(shkItem);

                // 79. Hồ sơ khác (ghi rõ)
                StaffDocumentItem otherFilesItem = this.getExistedStaffDocumentItem(staff, documentItems.get(8));
                if (isHasDocumentTemplate(importData.getOtherFilesCheck())) {
                    otherFilesItem.setIsSubmitted(true);
                } else {
                    otherFilesItem.setIsSubmitted(false);
                }
                staffDocumentItems.add(otherFilesItem);

                // 80. Phiếu thông tin cá nhân
                StaffDocumentItem personInfoItem = this.getExistedStaffDocumentItem(staff, documentItems.get(9));
                if (isHasDocumentTemplate(importData.getPersonInfoCheck())) {
                    personInfoItem.setIsSubmitted(true);
                } else {
                    personInfoItem.setIsSubmitted(false);
                }
                staffDocumentItems.add(personInfoItem);

                // 81. Cam kết bảo mật thông tin
                StaffDocumentItem secureInfoCommitmentItem = this.getExistedStaffDocumentItem(staff, documentItems.get(10));
                if (isHasDocumentTemplate(importData.getSecureInfocommitmentCheck())) {
                    secureInfoCommitmentItem.setIsSubmitted(true);
                } else {
                    secureInfoCommitmentItem.setIsSubmitted(false);
                }
                staffDocumentItems.add(secureInfoCommitmentItem);

                // 82. Cam kết bảo mật thông tin thu nhập
                StaffDocumentItem secureIncomeCommitmentItem = this.getExistedStaffDocumentItem(staff, documentItems.get(11));
                if (isHasDocumentTemplate(importData.getSecureIncomeCommitmentCheck())) {
                    secureIncomeCommitmentItem.setIsSubmitted(true);
                } else {
                    secureIncomeCommitmentItem.setIsSubmitted(false);
                }
                staffDocumentItems.add(secureIncomeCommitmentItem);

                // 83. Cam kết trách nhiệm
                StaffDocumentItem responsibilityCommitmentItem = this.getExistedStaffDocumentItem(staff, documentItems.get(12));
                if (isHasDocumentTemplate(importData.getResponsibilityCommitmentCheck())) {
                    responsibilityCommitmentItem.setIsSubmitted(true);
                } else {
                    responsibilityCommitmentItem.setIsSubmitted(false);
                }
                staffDocumentItems.add(responsibilityCommitmentItem);

                // 84. HĐ thử việc
                StaffDocumentItem probationLabourItem = this.getExistedStaffDocumentItem(staff, documentItems.get(13));
                if (isHasDocumentTemplate(importData.getProbationLabourCheck())) {
                    probationLabourItem.setIsSubmitted(true);
                } else {
                    probationLabourItem.setIsSubmitted(false);
                }
                staffDocumentItems.add(probationLabourItem);


                if (staff.getStaffDocumentItems() == null) {
                    staff.setStaffDocumentItems(new HashSet<>());
                }

                staff.getStaffDocumentItems().clear();
                staff.getStaffDocumentItems().addAll(staffDocumentItems);


                staff.setDocumentTemplate(commonTemplate);
            }


            // 85. Tình trạng hồ sơ
            this.handleSetDocumentStatus(staff, importData);

            // 86. Có đóng BHXH
            boolean hasSocialIns = this.getBooleanValueFromString(importData.getHasSocialIns());
            staff.setHasSocialIns(hasSocialIns);

            // 87. Bắt buộc chấm công
            boolean requiredAttendance = this.getBooleanValueFromString(importData.getRequireAttendance());
            staff.setRequireAttendance(requiredAttendance);

            // 88. Cho phép chấm công ngoài công ty
            boolean allowExternalTimekeeping = this.getBooleanValueFromString(importData.getAllowExternalIpTimekeeping());
            if (allowExternalTimekeeping) {
                staff.setAllowExternalIpTimekeeping(true);
                staff.setRequireAttendance(true);
            } else {
                staff.setAllowExternalIpTimekeeping(false);
            }

            // 89. Loại phân ca
            if (importData.getStaffWorkShiftType() != null) {
                if (importData.getStaffWorkShiftType().equals(HrConstants.StaffWorkShiftType.FIXED.getValue())) {
                    staff.setStaffWorkShiftType(HrConstants.StaffWorkShiftType.FIXED.getValue());
                } else if (importData.getStaffWorkShiftType().equals(HrConstants.StaffWorkShiftType.FLEXIBLE.getValue())) {
                    staff.setStaffWorkShiftType(HrConstants.StaffWorkShiftType.FLEXIBLE.getValue());
                } else {
                    errorMessages.add("Loại phân ca không hợp lệ");
                }
            } else {
                staff.setStaffWorkShiftType(null);
            }

            // 90. Mã ca làm việc cố định
            if (importData.getFixShiftWork() != null && StringUtils.hasText(importData.getFixShiftWork()) && staff.getStaffWorkShiftType() != null && importData.getStaffWorkShiftType().equals(HrConstants.StaffWorkShiftType.FIXED.getValue())) {
                ShiftWork fixShiftWorkEntity = null;
                List<ShiftWork> fixShiftWorkEntityList = shiftWorkRepository.findByCode(importData.getFixShiftWork());
                if (fixShiftWorkEntityList != null && !fixShiftWorkEntityList.isEmpty()) {
                    fixShiftWorkEntity = fixShiftWorkEntityList.get(0);
                }
                if (fixShiftWorkEntity != null) {
                    staff.setFixShiftWork(fixShiftWorkEntity);
                } else {
                    errorMessages.add("Mã ca làm việc cố định không hợp lệ");
                }
            } else {
                staff.setFixShiftWork(null);
            }

            // 91. Loại nghỉ trong tháng
            if (importData.getStaffLeaveShiftType() != null) {
                if (HrConstants.StaffLeaveShiftType.FIXED.getValue().equals(importData.getStaffLeaveShiftType())) {
                    staff.setStaffLeaveShiftType(HrConstants.StaffLeaveShiftType.FIXED.getValue());
                } else if (HrConstants.StaffLeaveShiftType.FLEXIBLE.getValue().equals(importData.getStaffLeaveShiftType())) {
                    staff.setStaffLeaveShiftType(HrConstants.StaffLeaveShiftType.FLEXIBLE.getValue());
                } else {
                    errorMessages.add("Loại nghỉ trong tháng không hợp lệ");
                }

            }

            // 92. Ngày nghỉ cố định
            if (importData.getFixLeaveWeekDay() != null && staff.getStaffLeaveShiftType() != null && staff.getStaffLeaveShiftType().equals(HrConstants.StaffLeaveShiftType.FIXED.getValue())) {
                Integer weekDayValue = importData.getFixLeaveWeekDay();

                if (weekDayValue.equals(HrConstants.WeekDays.MON.getValue())) {
                    staff.setFixLeaveWeekDay(weekDayValue);
                } else if (weekDayValue.equals(HrConstants.WeekDays.TUE.getValue())) {
                    staff.setFixLeaveWeekDay(weekDayValue);
                } else if (weekDayValue.equals(HrConstants.WeekDays.WED.getValue())) {
                    staff.setFixLeaveWeekDay(weekDayValue);
                } else if (weekDayValue.equals(HrConstants.WeekDays.THU.getValue())) {
                    staff.setFixLeaveWeekDay(weekDayValue);
                } else if (weekDayValue.equals(HrConstants.WeekDays.FRI.getValue())) {
                    staff.setFixLeaveWeekDay(weekDayValue);
                } else if (weekDayValue.equals(HrConstants.WeekDays.SAT.getValue())) {
                    staff.setFixLeaveWeekDay(weekDayValue);
                } else if (weekDayValue.equals(HrConstants.WeekDays.SUN.getValue())) {
                    staff.setFixLeaveWeekDay(weekDayValue);
                } else {
                    errorMessages.add("Ngày nghỉ cố định không hợp lệ");
                }
            }

            //93. Không tính đi muộn, về sớm
            boolean skipLateEarlyCount = this.getBooleanValueFromString(importData.getSkipLateEarlyCount());
            staff.setSkipLateEarlyCount(skipLateEarlyCount);

            // 94. Không tính làm thêm giờ
            boolean skipLateOvertimeCount = this.getBooleanValueFromString(importData.getSkipOvertimeCount());
            staff.setSkipOvertimeCount(skipLateOvertimeCount);

            if (!errorMessages.isEmpty()) {
                String finalMessage = String.join(". ", errorMessages);
                importData.setErrorMessage(finalMessage);
            }

            logger.info("Lưu TTNV " + staff.getDisplayName());

            staffRepository.saveAndFlush(staff);
            response.add(importData);


            entityManager.flush();
            entityManager.clear();
        }

        boolean hasErrors = response.stream()
                .anyMatch(importData -> importData.getErrorMessage() != null && !importData.getErrorMessage().isEmpty());

        if (hasErrors) {
            // Nếu có lỗi, trả về response mà không lưu dữ liệu
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return response;
        }

        return response;
    }

    private boolean getBooleanValueFromString(String value) {
        if (value == null) return false;
        if (!StringUtils.hasText(value)) return false;

        switch (value) {
            case "true":
            case "TRUE":
            case "x":
            case "X":
            case "Co":
            case "co":
            case "CO":
            case "CÓ":
            case "1":
                return true;

            default:
                return false;
        }
    }

    private HrDocumentTemplate getCommonDocumentTemplateEntity() {
        List<HrDocumentTemplate> availableTemplates = hrDocumentTemplateRepository.findByCode(HrConstants.COMMON_STAFF_PROFILE_TEMPLATE_CODE);
        if (availableTemplates != null && !availableTemplates.isEmpty()) {
            return availableTemplates.get(0);
        }

        return null;
    }

    private List<HrDocumentItem> getCommonDocumentItemEntites(HrDocumentTemplate commonTemplate) {
        List<HrDocumentItem> documentItems = new ArrayList<>();
        if (commonTemplate != null && !commonTemplate.getDocumentItems().isEmpty()) {
            documentItems.addAll(commonTemplate.getDocumentItems());

            // Sắp xếp danh sách theo displayOrder
            Collections.sort(documentItems, new Comparator<HrDocumentItem>() {
                @Override
                public int compare(HrDocumentItem o1, HrDocumentItem o2) {
                    // Kiểm tra null cho displayOrder
                    Integer order1 = o1.getDisplayOrder();
                    Integer order2 = o2.getDisplayOrder();

                    // Xử lý khi một hoặc cả hai displayOrder là null
                    if (order1 == null && order2 == null) {
                        return 0; // Cả hai null, coi như bằng nhau
                    } else if (order1 == null) {
                        return 1; // o1 null, đặt ở sau
                    } else if (order2 == null) {
                        return -1; // o2 null, đặt o1 ở trước
                    }

                    // Cả hai không null, so sánh bình thường
                    return Integer.compare(order1, order2);
                }
            });
        }

        return documentItems;
    }


    private static final String TEMPLATE_PATH = "ImportStaffLAReports.xlsx";

    @Override
    public Workbook exportImportStaffLAResults(ImportStaffDto importStaffDto) {
        List<StaffLAImport> staffImports = importStaffDto.getStaffImports();
        List<StaffFamilyRelationshipImport> staffFamilyRelationshipImports = importStaffDto.getStaffFamilyRelationshipImports();
        List<StaffBankAccountImport> staffBankAccountImports = importStaffDto.getStaffBankAccountImports();
        List<StaffWorkingLocationImport> staffWorkingLocationImports = importStaffDto.getStaffWorkingLocationImports();

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(TEMPLATE_PATH)) {
            long startTime = System.nanoTime();

            if (fileInputStream == null) {
                throw new IOException("File '" + TEMPLATE_PATH + "' không tìm thấy trong classpath");
            }

            Workbook workbook = new XSSFWorkbook(fileInputStream);

            Sheet staffSheet = workbook.getSheetAt(0);
            Sheet familySheet = workbook.getSheetAt(1);
            Sheet bankSheet = workbook.getSheetAt(2);
            Sheet staffWorkingLocationSheet = workbook.getSheetAt(3);

            CellStyle dataCellStyle = ExportExcelUtil.createDataCellStyle(workbook);

            // Kết quả sheet nhân viên
            this.handleExportStaffSheetReport(staffImports, staffSheet, dataCellStyle);

            // Kết quả sheet quan hệ thân nhân
            this.handleExportFamilyRelationshipSheetReport(staffFamilyRelationshipImports, familySheet, dataCellStyle);

            // Kết quả sheet tài khoản ngân hàng
            this.handleExportStaffBankAccountsSheetReport(staffBankAccountImports, bankSheet, dataCellStyle);

            // Kết quả sheet địa điểm làm việc
            this.handleExportStaffWorkingLocationsSheetReport(staffWorkingLocationImports, staffWorkingLocationSheet, dataCellStyle);


            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;

            logger.info("Xuất báo cáo nhân viên - Xử lý mất {} ms ", elapsedTimeMs);
            return workbook;

        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }

    private void handleExportStaffWorkingLocationsSheetReport(List<StaffWorkingLocationImport> staffWorkingLocationImports, Sheet sheet, CellStyle dataCellStyle) {
        int rowIndex = 1;

        for (StaffWorkingLocationImport importedData : staffWorkingLocationImports) {

            Row dataRow = sheet.createRow(rowIndex);
            int cellIndex = 0;

            // 0. STT
            String order = "";
            if (importedData.getStt() != null && StringUtils.hasText(importedData.getStt().toString())) {
                order = importedData.getStt().toString();
            }
            ExportExcelUtil.createCell(dataRow, cellIndex++, order, dataCellStyle);

            // 2. Lỗi khi nhập
            if (importedData.getErrorMessage() == null || !StringUtils.hasText(importedData.getErrorMessage())) {

                ExportExcelUtil.createCell(dataRow, cellIndex++, "Thông tin hợp lệ", dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, cellIndex++, importedData.getErrorMessage(), dataCellStyle);
            }

            // thêm dòng tiếp theo
            rowIndex++;
        }

        if (rowIndex == 1) {
            Row noErrorRow = sheet.createRow(rowIndex);
            ExportExcelUtil.createCell(noErrorRow, 0, "Không có lỗi được ghi nhận.", dataCellStyle);

            // Gộp từ cột 0 đến 3
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 3));

            // Căn giữa thông báo
            Cell cell = noErrorRow.getCell(0);
            CellStyle centeredStyle = sheet.getWorkbook().createCellStyle();
            centeredStyle.cloneStyleFrom(dataCellStyle); // giữ nguyên style gốc
            centeredStyle.setAlignment(HorizontalAlignment.CENTER);
            centeredStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cell.setCellStyle(centeredStyle);
        }
    }

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private String formatDate(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }

    private void handleExportStaffSheetReport(List<StaffLAImport> staffImports, Sheet sheet, CellStyle dataCellStyle) {
        if (staffImports == null || staffImports.isEmpty() || sheet == null) {
            return;
        }

        int rowIndex = 1;

        for (StaffLAImport importedData : staffImports) {

            Row dataRow = sheet.createRow(rowIndex);
            int cellIndex = 0;

            // 0. STT
            String order = "";
            if (importedData.getStt() != null && StringUtils.hasText(importedData.getStt().toString())) {
                order = importedData.getStt().toString();
            }
            ExportExcelUtil.createCell(dataRow, cellIndex++, order, dataCellStyle);

            // 1. Mã nhân viên
            ExportExcelUtil.createCell(dataRow, cellIndex++, importedData.getStaffCode(), dataCellStyle);

            // 2. Họ và tên
            ExportExcelUtil.createCell(dataRow, cellIndex++, importedData.getDisplayName(), dataCellStyle);

            // 3. Lỗi khi nhập
            if (importedData.getErrorMessage() == null || !StringUtils.hasText(importedData.getErrorMessage())) {

                ExportExcelUtil.createCell(dataRow, cellIndex++, "Thông tin hợp lệ", dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, cellIndex++, importedData.getErrorMessage(), dataCellStyle);
            }

            // thêm dòng tiếp theo
            rowIndex++;
        }

        if (rowIndex == 1) {
            Row noErrorRow = sheet.createRow(rowIndex);
            ExportExcelUtil.createCell(noErrorRow, 0, "Không có lỗi được ghi nhận.", dataCellStyle);

            // Gộp từ cột 0 đến 3
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 3));

            // Căn giữa thông báo
            Cell cell = noErrorRow.getCell(0);
            CellStyle centeredStyle = sheet.getWorkbook().createCellStyle();
            centeredStyle.cloneStyleFrom(dataCellStyle); // giữ nguyên style gốc
            centeredStyle.setAlignment(HorizontalAlignment.CENTER);
            centeredStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cell.setCellStyle(centeredStyle);
        }
    }

    private void handleExportFamilyRelationshipSheetReport(List<StaffFamilyRelationshipImport> staffFamilyRelationshipImports, Sheet sheet, CellStyle dataCellStyle) {
        int rowIndex = 1;

        for (StaffFamilyRelationshipImport importedData : staffFamilyRelationshipImports) {
            if (importedData.getErrorMessage() == null || !StringUtils.hasText(importedData.getErrorMessage()))
                continue;

            Row dataRow = sheet.createRow(rowIndex);

            // 0. STT
            int cellIndex = 0;
            ExportExcelUtil.createCell(dataRow, cellIndex++, importedData.getStt().toString(), dataCellStyle);

            // 1. Lỗi khi nhập
            ExportExcelUtil.createCell(dataRow, cellIndex++, importedData.getErrorMessage(), dataCellStyle);


            // thêm dòng tiếp theo
            rowIndex++;
        }

        if (rowIndex == 1) {
            Row noErrorRow = sheet.createRow(rowIndex);
            ExportExcelUtil.createCell(noErrorRow, 0, "Không có lỗi được ghi nhận.", dataCellStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 1));

            Cell cell = noErrorRow.getCell(0);
            CellStyle centeredStyle = sheet.getWorkbook().createCellStyle();
            centeredStyle.cloneStyleFrom(dataCellStyle);
            centeredStyle.setAlignment(HorizontalAlignment.CENTER);
            centeredStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cell.setCellStyle(centeredStyle);
        }
    }

    private void handleExportStaffBankAccountsSheetReport(List<StaffBankAccountImport> staffFamilyRelationshipImports, Sheet sheet, CellStyle dataCellStyle) {
        int rowIndex = 1;
        int orderNumber = 1;
        for (StaffBankAccountImport importedData : staffFamilyRelationshipImports) {
            if (importedData.getErrorMessage() == null || !StringUtils.hasText(importedData.getErrorMessage()))
                continue;

            Row dataRow = sheet.createRow(rowIndex);

            // 0. STT
            int cellIndex = 0;
            ExportExcelUtil.createCell(dataRow, cellIndex++, orderNumber, dataCellStyle);

            // 1. Lỗi khi nhập
            ExportExcelUtil.createCell(dataRow, cellIndex++, importedData.getErrorMessage(), dataCellStyle);


            // thêm dòng tiếp theo
            rowIndex++;
            orderNumber++;
        }

        if (rowIndex == 1) {
            Row noErrorRow = sheet.createRow(rowIndex);
            ExportExcelUtil.createCell(noErrorRow, 0, "Không có lỗi được ghi nhận.", dataCellStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 1));

            Cell cell = noErrorRow.getCell(0);
            CellStyle centeredStyle = sheet.getWorkbook().createCellStyle();
            centeredStyle.cloneStyleFrom(dataCellStyle);
            centeredStyle.setAlignment(HorizontalAlignment.CENTER);
            centeredStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cell.setCellStyle(centeredStyle);
        }
    }


    @Override
    public String generateNewStaffCode(SearchStaffDto dto) {
        // Chuyển đổi java.util.Date -> java.time.LocalDate
        Date date = dto.getRecruitmentDate();
        if (date == null) {
            // fallback nếu không có ngày: dùng ngày hôm nay
            date = new Date();
        }
        LocalDate joinDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        String year = String.format("%02d", joinDate.getYear() % 100); // 2 số cuối năm
        String month = String.format("%02d", joinDate.getMonthValue()); // 2 số tháng
        String prefix = "LA" + year + month + "_";

        // Tìm mã nhân viên lớn nhất hiện có bắt đầu với prefix
        String maxCode = staffRepository.findMaxStaffCodeStartingWith(prefix);

        int nextNumber = 1;
        if (maxCode != null) {
            // Tách số thứ tự ra từ LAyyMM_XXXXXX
            String[] parts = maxCode.split("_");
            if (parts.length == 2) {
                try {
                    nextNumber = Integer.parseInt(parts[1]) + 1;
                } catch (NumberFormatException ignored) {
                }
            }
        }

        String staffCode = prefix + String.format("%06d", nextNumber);

        Staff staff = new Staff();
        staff.setStaffCode(staffCode);

        return staffCode;
    }

    @Override
    public String generateNewStaffCodeV2(SearchStaffDto dto) {
        Date date = dto != null ? dto.getRecruitmentDate() : null;
        if (date == null) {
            date = new Date();
        }
        LocalDate joinDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        String year = String.format("%02d", joinDate.getYear() % 100);
        String month = String.format("%02d", joinDate.getMonthValue());
        String prefix = "LA" + year + month + "_";

        // Tìm mã lớn nhất trong toàn hệ thống
        String maxCode = staffRepository.findMaxValidStaffCode();

        int nextNumber = 1;
        if (maxCode != null) {
            try {
                // Tách phần số thứ tự từ LAyyMM_XXXXXX
                String[] parts = maxCode.split("_");
                if (parts.length == 2) {
                    nextNumber = Integer.parseInt(parts[1]) + 1;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        String staffCode = prefix + String.format("%06d", nextNumber);

        return staffCode;
    }


    @Override
    public List<UUID> generateFixScheduleForChosenStaffs(SearchStaffWorkScheduleDto dto) {
        if (dto == null || dto.getStaffIdList() == null
                || dto.getStaffIdList().isEmpty() || dto.getFromDate() == null || dto.getToDate() == null || dto.getFromDate().after(dto.getToDate()))
            return null;

        List<UUID> response = new ArrayList<>();

        for (UUID staffId : dto.getStaffIdList()) {
            List<StaffWorkScheduleDto> generatedSchedules = staffWorkScheduleService.generateFixSchedulesInRangeTimeForStaff(staffId, dto.getFromDate(), dto.getToDate());

            if (generatedSchedules != null && !generatedSchedules.isEmpty()) {
                response.add(staffId);
            }
        }

        return response;
    }


    @Override
    public Page<StaffLabourManagementDto> pagingStaffLabourManagement(SearchStaffDto dto) {
        if (dto == null) return null;

        HashMap<UUID, LabourAgreementDto> mapLabourAgreementLatest = staffLabourAgreementService.getLabourAgreementLatestMap();

        Page<Staff> staffPage = staffService.searchByPageEntity(dto);
        List<Staff> staffList = staffPage.getContent();

        List<StaffLabourManagementDto> response = new ArrayList<>();


        for (Staff staff : staffList) {
            StaffLabourManagementDto staffItem = new StaffLabourManagementDto(staff);

            response.add(staffItem);

            // 8. Cấp bậc
            String rankTitleJoinedString = this.getRankTitleJoinedStringOfStaff(staff);
            staffItem.setRankTitleJoined(rankTitleJoinedString);

            // 9. Vị trí làm việc
//            String titleJoinedString = this.getMultilineTitleStringOfStaffSortedByRankTitle(staff);
            String titleJoinedString = this.getMultilineTitleStringOfStaff(staff);
            staffItem.setTitleJoined(titleJoinedString);

            // 10. Loại HĐLĐ
            String contractType = "";
            if (mapLabourAgreementLatest.containsKey(staff.getId())) {
                contractType = mapLabourAgreementLatest.get(staff.getId()).getContactTypeName();
            }
            staffItem.setContractType(contractType);

            // 11. Thời điểm bắt đầu làm việc
            Date signDate = null;
            if (mapLabourAgreementLatest.containsKey(staff.getId())) {
                signDate = mapLabourAgreementLatest.get(staff.getId()).getSignDate();
            }
            String signDateString = (signDate != null) ? formatDate(signDate) : "";
            staffItem.setSignDateContract(signDateString);

            DecimalFormat df = new DecimalFormat("0.##");
            df.setMaximumFractionDigits(2);

            Double insuranceSalary = 0D;
            if (mapLabourAgreementLatest.containsKey(staff.getId())) {
                insuranceSalary = mapLabourAgreementLatest.get(staff.getId()).getSalary();
            }

            // 12. Tham gia bảo hiểm BHXH
            String bhxhSalary = CommissionPayrollServiceImpl.formatDecimalDouble(insuranceSalary * (0.08 + 0.175));
            staffItem.setBhxhSalary(bhxhSalary);

            // 13. Tham gia bảo hiểm BHYT
            String bhytSalary = CommissionPayrollServiceImpl.formatDecimalDouble(insuranceSalary * (0.015 + 0.03));
            staffItem.setBhytSalary(bhytSalary);

            // 14. Tham gia bảo hiểm BHTN
            String bhtnSalary = CommissionPayrollServiceImpl.formatDecimalDouble(insuranceSalary * (0.01 + 0.01));
            staffItem.setBhtnSalary(bhtnSalary);

            // 15. Tiền lương cơ bản
            String insuranceSalaryStr = CommissionPayrollServiceImpl.formatDecimalDouble(insuranceSalary);
            staffItem.setInsuranceSalaryStr(insuranceSalaryStr);

            // 17. Số ngày nghỉ trong năm
            String leaveDays = df.format(getLeaveDayInYearOfStaff(staff, dto));
            staffItem.setLeaveDays(leaveDays);

            // 18. Số giờ làm thêm
            String otHours = df.format(getTotalOTHoursInYearOfStaff(staff, dto));
            staffItem.setOtHours(otHours);

            // 19. Hưởng chế độ BHXH, BHYT, BHTN
            String insurancePolicy = this.getInsurancePolicyOfStaffInYear(staff, dto);
            staffItem.setSocialInsuranceBenefitEligible(insurancePolicy);

            // 23. Thời điểm chấm dứt HĐLĐ và lý do
            Date endDate = null;
            if (mapLabourAgreementLatest.containsKey(staff.getId())) {
                endDate = mapLabourAgreementLatest.get(staff.getId()).getEndDate();
            }
            String endDateString = (endDate != null) ? formatDate(endDate) : "";
            staffItem.setEndDateContract(endDateString);

        }

        Pageable pageable = PageRequest.of(dto.getPageIndex(), dto.getPageSize());

        entityManager.clear();

        return new PageImpl<>(response, pageable, staffPage.getTotalElements());
    }


    @Override
    public Page<StaffLabourUtilReportDto> pagingStaffLabourUtilReport(SearchStaffDto dto) {
        if (dto == null) return null;

        dto.setHasSocialIns(true);

        HashMap<UUID, LabourAgreementDto> mapLabourAgreementLatest = staffLabourAgreementService.getLabourAgreementLatestMap();

        Page<Staff> staffPage = staffService.searchByPageEntity(dto);
        List<Staff> staffList = staffPage.getContent();

        List<StaffLabourUtilReportDto> response = new ArrayList<>();


        for (Staff staff : staffList) {
            StaffLabourUtilReportDto staffItem = new StaffLabourUtilReportDto(staff);

            response.add(staffItem);

            // 6. Cấp bậc, chức vụ, chức danh nghề, nơi làm việc
            String rankTitleJoinedString = this.getMultilineTitleStringOfStaff(staff);
            staffItem.setRankTitleJoined(rankTitleJoinedString);


            // 7. Nhà quản lý
            if (isTitleNhaQuanLy(staff.getStaffPositionType())) {
                staffItem.setIsManager("x");
            }

            // 8. Chuyên môn kỹ thuật bậc cao
            if (isTitleChuyenMonKyThuatBacCao(staff.getStaffPositionType())) {
                staffItem.setHighTechQualification("x");
            }

            // 9. Chuyên môn kỹ thuật bậc trung
            if (isTitleChuyenMonKyThuatBacTrung(staff.getStaffPositionType())) {
                staffItem.setMidTechQualification("x");
            }

            // 10. Khác
            if (isTitleKhac(staff.getStaffPositionType())) {
                staffItem.setOtherQualification("x");
            }

            DecimalFormat df = new DecimalFormat("0.##");
            df.setMaximumFractionDigits(2);

            // 11. Hệ số/Mức lương
            if (mapLabourAgreementLatest.containsKey(staff.getId())) {
                String salaryStr = df.format(mapLabourAgreementLatest.get(staff.getId()).getSalary());
                staffItem.setSalaryCoefficient(CommissionPayrollServiceImpl.formatDecimalString(salaryStr));
            }

            // 12. Phụ cấp - Chức vụ
            String baseSalaryValue = df.format(this.getBaseSalaryValue(staff.getId()));
            staffItem.setPositionAllowance(baseSalaryValue);

            // 19. Ngày bắt đầu HĐLĐ không xác định thời hạn
            String indefiniteContractStartDate = this.getIndefiniteContractStartDate(staff.getId());
            staffItem.setIndefiniteContractStartDate(indefiniteContractStartDate);


            List<StaffLabourAgreement> definedLimitAgreements = staffLabourAgreementRepository.findLatestLabourByContractTypeCodeOfStaff(staff.getId(), HrConstants.ContractTypeCode.DETERMINE_THE_DEADLINE.getValue());

            if (definedLimitAgreements == null || definedLimitAgreements.isEmpty()) {
                // 20. Hiệu lực HĐLĐ xác định thời hạn - Ngày bắt đầu
                staffItem.setDefiniteContractStartDate("");

                // 21. Hiệu lực HĐLĐ xác định thời hạn - Ngày kết thúc
                staffItem.setDefiniteContractEndDate("");
            } else {
                StaffLabourAgreement latestAgreement = definedLimitAgreements.get(0);

                // 20. Hiệu lực HĐLĐ xác định thời hạn - Ngày bắt đầu
                String startDateStr = (latestAgreement.getStartDate() != null) ? formatDate(latestAgreement.getStartDate()) : "";
                staffItem.setDefiniteContractStartDate(startDateStr);

                // 21. Hiệu lực HĐLĐ xác định thời hạn - Ngày kết thúc
                String endDateStr = (latestAgreement.getEndDate() != null) ? formatDate(latestAgreement.getEndDate()) : "";
                staffItem.setDefiniteContractEndDate(endDateStr);
            }


            List<StaffLabourAgreement> probationAgreements =
                    staffLabourAgreementRepository.findLatestLabourByContractTypeCodeListOfStaff(
                            staff.getId(),
                            Arrays.asList(
                                    HrConstants.ContractTypeCode.PROBATION.getValue(),
                                    HrConstants.ContractTypeCode.OTHER.getValue()
                            )
                    );

            if (probationAgreements == null || probationAgreements.isEmpty()) {
                // 22. Hiệu lực HĐLĐ khác (dưới 1 tháng, thử việc) - Ngày bắt đầu
                staffItem.setOtherContractStartDate("");

                // 23. Hiệu lực HĐLĐ khác (dưới 1 tháng, thử việc) - Ngày kết thúc

                staffItem.setOtherContractEndDate("");
            } else {
                StaffLabourAgreement latestAgreement = probationAgreements.get(0);

                // 22. Hiệu lực HĐLĐ khác (dưới 1 tháng, thử việc) - Ngày bắt đầu
                String startDateStr = (latestAgreement.getStartDate() != null) ? formatDate(latestAgreement.getStartDate()) : "";
                staffItem.setOtherContractStartDate(startDateStr);

                // 23. Hiệu lực HĐLĐ khác (dưới 1 tháng, thử việc) - Ngày kết thúc
                String endDateStr = (latestAgreement.getEndDate() != null) ? formatDate(latestAgreement.getEndDate()) : "";
                staffItem.setOtherContractEndDate(endDateStr);
            }

            List<StaffInsuranceHistory> insuranceHistories =
                    staffInsuranceHistoryRepository.findByStaffId(staff.getId());

            if (insuranceHistories != null && !insuranceHistories.isEmpty()) {
                StaffInsuranceHistory insuranceHistory = insuranceHistories.get(0);

                // 24. Thời điểm đơn vị bắt đầu đóng BHXH
                String startDateStr = (insuranceHistory.getStartDate() != null) ? formatDate(insuranceHistory.getStartDate()) : "";
                staffItem.setStartSocialInsuranceDate(startDateStr);

                // 25. Thời điểm đơn vị kết thúc đóng BHXH
                String endDateStr = (insuranceHistory.getEndDate() != null) ? formatDate(insuranceHistory.getEndDate()) : "";
                staffItem.setEndSocialInsuranceDate(endDateStr);
            }

        }

        Pageable pageable = staffPage.getPageable();

        entityManager.clear();

        return new PageImpl<>(response, pageable, staffPage.getTotalElements());
    }

    private String getIndefiniteContractStartDate(UUID staffId) {
        List<StaffLabourAgreement> agreements = staffLabourAgreementRepository.findLatestLabourByContractTypeCodeOfStaff(staffId, HrConstants.ContractTypeCode.UNSPECIFIED_DEADLINE.getValue());

        if (agreements == null || agreements.isEmpty()) {
            return "";
        }

        Date startDate = agreements.get(0).getStartDate();

        String strValue = (startDate != null) ? formatDate(startDate) : "";
        return strValue;
    }

    private double getBaseSalaryValue(UUID staffId) {
        Double result = 0D;
        if (staffId == null) return result;

        try {
            SystemConfigDto systemConfig = systemConfigService.getByKeyCode(HrConstants.SystemConfigCode.BASE_WAGE_SALARYITEM_CODE.getCode());
            if (systemConfig == null || !StringUtils.hasText(systemConfig.getConfigValue())) {
                return result;
            }

            List<SalaryItem> availableSalaryItems = salaryItemRepository.findByCode(systemConfig.getConfigValue().trim());
            if (availableSalaryItems == null || availableSalaryItems.isEmpty()) {
                return result;
            }

            SalaryItem salaryItem = availableSalaryItems.get(0);

            List<StaffSalaryItemValue> availableSalaryValues = staffSalaryItemValueRepository.findCurrentByStaffIdAndSalaryItemId(staffId, salaryItem.getId());

            if (availableSalaryValues == null || availableSalaryValues.isEmpty()) {
                return result;
            }


            return (availableSalaryValues.get(0).getValue());

        } catch (Exception exception) {
            exception.printStackTrace();
            return result;
        }
    }

    private boolean isTitleNhaQuanLy(Integer staffPositionType) {
        if (staffPositionType == null) return false;

        if (staffPositionType.equals(HrConstants.StaffPositionType.NHA_QUAN_LY.getValue())) {
            return true;
        }

        return false;
    }

    private boolean isTitleChuyenMonKyThuatBacCao(Integer staffPositionType) {
        if (staffPositionType == null) return false;

        if (staffPositionType.equals(HrConstants.StaffPositionType.CHUYEN_MON_KY_THUAT_BAC_CAO.getValue())) {
            return true;
        }

        return false;
    }

    private boolean isTitleChuyenMonKyThuatBacTrung(Integer staffPositionType) {
        if (staffPositionType == null) return false;

        if (staffPositionType.equals(HrConstants.StaffPositionType.CHUYEN_MON_KY_THUAT_BAC_TRUNG.getValue())) {
            return true;
        }

        return false;
    }

    private boolean isTitleKhac(Integer staffPositionType) {
        if (staffPositionType == null) return false;

        if (staffPositionType.equals(HrConstants.StaffPositionType.KHAC.getValue())) {
            return true;
        }

        return false;
    }


    private double getLeaveDayInYearOfStaff(Staff staff, SearchStaffDto dto) {
        if (staff == null || dto == null) return 0D;

        double totalLeaveRatios = staffWorkScheduleRepository.getTotalLeaveRatioByStaffAndYear(staff.getId(), dto.getYear());

        // Gộp các chuỗi lại, phân cách bởi xuống dòng
        return totalLeaveRatios;
    }

    private double getTotalOTHoursInYearOfStaff(Staff staff, SearchStaffDto dto) {
        if (staff == null || dto == null) return 0D;

        double totalOTHours = staffWorkScheduleRepository.getTotalConfirmedOTHoursByStaffAndYear(staff.getId(), dto.getYear());

        // Gộp các chuỗi lại, phân cách bởi xuống dòng
        return totalOTHours;
    }


    private String getInsurancePolicyOfStaffInYear(Staff staff, SearchStaffDto dto) {
        if (staff == null || dto == null) return "";

        List<StaffMaternityHistory> maternityHistories = staffMaternityHistoryRepository
                .findMaternityHistoryInCurrentYear(staff.getId(), dto.getYear());

        if (maternityHistories == null || maternityHistories.isEmpty()) return "";

        List<String> response = new ArrayList<>();

        for (StaffMaternityHistory staffMaternityHistory : maternityHistories) {
            String responseItem = StaffMaternityHistoryServiceImpl.formatMaternityHistory(staffMaternityHistory);
            response.add(responseItem);
        }

        // Gộp các chuỗi lại, phân cách bởi xuống dòng
        return String.join("\n", response);
    }

    private String getRankTitleJoinedStringOfStaff(Staff staff) {
        if (staff == null) return "";

        List<Position> staffCurrentPositions = positionRepository.findByStaffId(staff.getId());

        if (staffCurrentPositions == null || staffCurrentPositions.isEmpty()) return "";

        Set<String> rankTitles = new HashSet<>();

        for (Position position : staffCurrentPositions) {
            if (position.getTitle() == null
                    || position.getTitle().getRankTitle() == null
                    || position.getTitle().getRankTitle().getName() == null)
                continue;

            rankTitles.add(position.getTitle().getRankTitle().getName());
        }

        List<String> sortedRankTitles = new ArrayList<>(rankTitles);
        Collections.sort(sortedRankTitles); // sắp xếp theo thứ tự chữ cái

        return String.join(", ", sortedRankTitles); // gộp lại bằng dấu phẩy
    }


    private String getMultilineTitleStringOfStaff(Staff staff) {
        if (staff == null) {
            return "";
        }

        List<Position> staffCurrentPositions = positionRepository.findByStaffId(staff.getId());

        if (staffCurrentPositions == null || staffCurrentPositions.isEmpty()) return "";

        List<Position> validPositions = new ArrayList<>();
        for (Position position : staffCurrentPositions) {
            if (position == null || position.getTitle() == null) continue;
            PositionTitle title = position.getTitle();
            if (title.getName() == null)
                continue;
            validPositions.add(position);
        }


        Set<String> seenTitles = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for (Position position : validPositions) {
            String titleName = position.getTitle().getName();
            if (!seenTitles.contains(titleName)) {
                seenTitles.add(titleName);
                if (sb.length() > 0) {
                    sb.append("\n"); // xuống dòng cho Excel
                }
                sb.append(titleName);
            }
        }

        return sb.toString();
    }

    private String getMultilineTitleStringOfStaffSortedByRankTitle(Staff staff) {
        if (staff == null) {
            return "";
        }

        List<Position> staffCurrentPositions = positionRepository.findByStaffId(staff.getId());

        if (staffCurrentPositions == null || staffCurrentPositions.isEmpty()) return "";

        List<Position> validPositions = new ArrayList<>();
        for (Position position : staffCurrentPositions) {
            if (position == null || position.getTitle() == null) continue;
            PositionTitle title = position.getTitle();
            if (title.getName() == null || title.getRankTitle() == null || title.getRankTitle().getName() == null)
                continue;
            validPositions.add(position);
        }

        Collections.sort(validPositions, new Comparator<Position>() {
            @Override
            public int compare(Position p1, Position p2) {
                String rank1 = p1.getTitle().getRankTitle().getName();
                String rank2 = p2.getTitle().getRankTitle().getName();
                return rank1.compareTo(rank2);
            }
        });

        Set<String> seenTitles = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for (Position position : validPositions) {
            String titleName = position.getTitle().getName();
            if (!seenTitles.contains(titleName)) {
                seenTitles.add(titleName);
                if (sb.length() > 0) {
                    sb.append("\n"); // xuống dòng cho Excel
                }
                sb.append(titleName);
            }
        }

        return sb.toString();
    }

    // Xuất sổ quản lý lao động
    @Override
    public Workbook exportLaborManagementBook(SearchStaffDto searchStaffDto) throws IOException {
        if (searchStaffDto == null) {
            return null;
        }

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("Excel/SO_QUAN_LY_LAO_DONG.xlsx")) {
            if (fileInputStream == null) {
                throw new IOException("File '" + TEMPLATE_PATH + "' không tìm thấy trong classpath");
            }

            Workbook workbook = new XSSFWorkbook(fileInputStream);

            Sheet staffSheet = workbook.getSheetAt(0);
            CellStyle dataCellStyle = ExportExcelUtil.createDataCellStyle(workbook);

            int pageIndex = 1;
            int rowIndex = 5;
            int orderNumber = 1;
            boolean hasNextPage = true;
            long startTime = System.nanoTime();

            // Địa điểm làm việc chính
            HashMap<UUID, LabourAgreementDto> mapLabourAgreementLatest = staffLabourAgreementService.getLabourAgreementLatestMap();

            while (hasNextPage) {
                // searchStaffDto = new SearchStaffDto();
                searchStaffDto.setPageIndex(pageIndex);
                searchStaffDto.setPageSize(50);

                Page<StaffLabourManagementDto> staffPage = this.pagingStaffLabourManagement(searchStaffDto);
                if (staffPage == null || staffPage.isEmpty()) {
                    break;
                }

                for (StaffLabourManagementDto staffItem : staffPage) {
                    if (staffItem == null) continue;

                    Row dataRow = staffSheet.createRow(rowIndex);
                    int cellIndex = 0;

                    // 0. STT
                    ExportExcelUtil.createCell(dataRow, 0, orderNumber, dataCellStyle);

                    // 1. Họ và tên
                    ExportExcelUtil.createCell(dataRow, 1, staffItem.getDisplayName(), dataCellStyle);

                    // 2. Giới tính
                    ExportExcelUtil.createCell(dataRow, 2, staffItem.getGender(), dataCellStyle);

                    // 3. Năm sinh
                    ExportExcelUtil.createCell(dataRow, 3, staffItem.getBirthDate(), dataCellStyle);

                    // 4. Quốc tịch
                    ExportExcelUtil.createCell(dataRow, 4, staffItem.getNationalityName(), dataCellStyle);

                    // 5. Địa chỉ
                    ExportExcelUtil.createCell(dataRow, 5, staffItem.getStaffAddress(), dataCellStyle);

                    // 6. CCCD (hoặc hộ chiếu)
                    ExportExcelUtil.createCell(dataRow, 6, staffItem.getStaffIndentity(), dataCellStyle);

                    // 7. Trình độ chuyên môn kỹ thuật
                    ExportExcelUtil.createCell(dataRow, 7, staffItem.getStaffEducationDegree(), dataCellStyle);

                    // 8. Cấp bậc
                    ExportExcelUtil.createCell(dataRow, 8, staffItem.getRankTitleJoined(), dataCellStyle);

                    // 9. Vị trí làm việc
                    ExportExcelUtil.createCell(dataRow, 9, staffItem.getTitleJoined(), dataCellStyle);

                    // 10. Loại HĐLĐ
                    ExportExcelUtil.createCell(dataRow, 10, staffItem.getContractType(), dataCellStyle);

                    // 11. Thời điểm bắt đầu làm việc
                    ExportExcelUtil.createCell(dataRow, 11, staffItem.getSignDateContract(), dataCellStyle);

                    // 12. Tham gia bảo hiểm BHXH
                    ExportExcelUtil.createCell(dataRow, 12, staffItem.getBhxhSalary(), dataCellStyle);

                    // 13. Tham gia bảo hiểm BHYT
                    ExportExcelUtil.createCell(dataRow, 13, staffItem.getBhytSalary(), dataCellStyle);

                    // 14. Tham gia bảo hiểm BHTN
                    ExportExcelUtil.createCell(dataRow, 14, staffItem.getBhtnSalary(), dataCellStyle);

                    // 15. Tiền lương cơ bản
                    ExportExcelUtil.createCell(dataRow, 15, staffItem.getInsuranceSalaryStr(), dataCellStyle);

                    // 16. Nâng bậc, nâng lương
                    ExportExcelUtil.createCell(dataRow, 16, staffItem.getUpSalaryInfo(), dataCellStyle);

                    // 17. Số ngày nghỉ trong năm
                    ExportExcelUtil.createCell(dataRow, 17, staffItem.getLeaveDays(), dataCellStyle);

                    // 18. Số giờ làm thêm
                    ExportExcelUtil.createCell(dataRow, 18, staffItem.getOtHours(), dataCellStyle);

                    // 19. Hưởng chế độ BHXH, BHYT, BHTN
                    ExportExcelUtil.createCell(dataRow, 19, staffItem.getSocialInsuranceBenefitEligible(), dataCellStyle);

                    // 20. Học nghề, đào tạo, bồi dưỡng, nâng cao trình độ kỹ năng nghề
                    ExportExcelUtil.createCell(dataRow, 20, staffItem.getStudyInfo(), dataCellStyle);

                    // 21. Kỷ luật lao động, trách nhiệm vật chất
                    ExportExcelUtil.createCell(dataRow, 21, staffItem.getDisciplineInfo(), dataCellStyle);

                    // 22. Tai nạn lao động, bệnh nghề nghiệp
                    ExportExcelUtil.createCell(dataRow, 22, staffItem.getOccupationalAccidentInfo(), dataCellStyle);

                    // 23. Thời điểm chấm dứt HĐLĐ và lý do
                    ExportExcelUtil.createCell(dataRow, 23, staffItem.getEndDateContract(), dataCellStyle);


                    // thêm dòng tiếp theo
                    rowIndex++;
                    ++orderNumber;

                }

                hasNextPage = staffPage.hasNext(); // Kiểm tra xem còn trang tiếp theo không
                pageIndex++; // Tăng pageIndex để lấy trang tiếp theo
            }
            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;

            logger.info("Xuất sổ quản lý lao động nhân viên - Xử lý mất {} ms ", elapsedTimeMs);
            return workbook;

        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }


    @Override
    public Workbook exportReportLabourUsage(SearchStaffDto searchDto) throws IOException {
        if (searchDto == null || searchDto.getContractOrganizationId() == null) return null;

        HrOrganization contractOrg = hrOrganizationRepository.findById(searchDto.getContractOrganizationId()).orElse(null);
        if (contractOrg == null) {
            return null;
        }

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("Excel/BAO_CAO_TINH_HINH_SU_DUNG_LAO_DONG.xlsx")) {
            if (fileInputStream == null) {
                throw new IOException("File '" + TEMPLATE_PATH + "' không tìm thấy trong classpath");
            }

            Workbook workbook = new XSSFWorkbook(fileInputStream);

            Sheet sheet = workbook.getSheetAt(0);

            // Tên pháp nhân
            handleSetContractOrgNameOnLabourUsageReport(sheet, contractOrg);
            // Ngày tháng năm lập báo cáo
            handleSetCurrentDateOnLabourUsageReport(sheet);
            // Tên doanh nghiệp, cơ quan, tổ chức
            handleReplaceCompanyNameSimple(sheet, contractOrg);
            // Địa chỉ chi tiết
            handleReplaceAddress(sheet, contractOrg);
            // Điện thoại, fax, email, website
            handleReplaceWebsiteSimple(sheet, contractOrg);
            // Mã số giấy chứng nhận đăng ký doanh nghiệp (mã số thuế)
            handleReplaceTaxCodeSimple(sheet, contractOrg);
            // Tính đến
            handleReplaceReportDateToday(sheet);
            // Người đại diện
            handleReplaceRepresentativeName(sheet, contractOrg);


            Font font = workbook.getFontAt((short) 0);
            font.setFontName("Times New Roman");

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            CellStyle cellStyleCenter = workbook.createCellStyle();
            cellStyleCenter.setBorderBottom(BorderStyle.THIN);
            cellStyleCenter.setBorderTop(BorderStyle.THIN);
            cellStyleCenter.setBorderLeft(BorderStyle.THIN);
            cellStyleCenter.setBorderRight(BorderStyle.THIN);
            cellStyleCenter.setAlignment(HorizontalAlignment.CENTER);
            cellStyleCenter.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleCenter.setWrapText(true);
            cellStyleCenter.setFont(font);

            CellStyle cellStyleLeft = workbook.createCellStyle();
            cellStyleLeft.setBorderBottom(BorderStyle.THIN);
            cellStyleLeft.setBorderTop(BorderStyle.THIN);
            cellStyleLeft.setBorderLeft(BorderStyle.THIN);
            cellStyleLeft.setBorderRight(BorderStyle.THIN);
            cellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
            cellStyleLeft.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleLeft.setWrapText(true);
            cellStyleLeft.setFont(font);

            CellStyle cellStyleRight = workbook.createCellStyle();
            cellStyleRight.setBorderBottom(BorderStyle.THIN);
            cellStyleRight.setBorderTop(BorderStyle.THIN);
            cellStyleRight.setBorderLeft(BorderStyle.THIN);
            cellStyleRight.setBorderRight(BorderStyle.THIN);
            cellStyleRight.setAlignment(HorizontalAlignment.RIGHT);
            cellStyleRight.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleRight.setWrapText(true);
            cellStyleRight.setFont(font);

            Row row = null;

            Cell cell = null;

            List<List<Object>> tempData = new ArrayList<>();
            List<CellRangeAddress> mergedRegionsInTemp = new ArrayList<>();

            // Lưu dữ liệu các ô
            for (int rowIndex = 18; rowIndex < 40; rowIndex++) {
                Row rowTemp = sheet.getRow(rowIndex);
                if (rowTemp == null) continue;

                List<Object> rowData = new ArrayList<>();
                for (int cn = 0; cn < rowTemp.getLastCellNum(); cn++) {
                    Cell cellTemp = rowTemp.getCell(cn);
                    Map<String, Object> cellInfo = new HashMap<>();

                    if (cellTemp != null) {
                        cellInfo.put("value", cellTemp.toString());
                        cellInfo.put("style", cellTemp.getCellStyle());
                    } else {
                        cellInfo.put("value", "");
                        cellInfo.put("style", null);
                    }

                    rowData.add(cellInfo);
                }
                tempData.add(rowData);
            }

            // Lưu thông tin các vùng merge liên quan
            for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                CellRangeAddress region = sheet.getMergedRegion(i);
                if (region.getFirstRow() >= 18 && region.getLastRow() < 40) {
                    mergedRegionsInTemp.add(region);
                }
            }

            for (int i = 18; i < 40; i++) {
                Row removingRow = sheet.getRow(i);
                if (removingRow != null) {
                    sheet.removeRow(removingRow);
                }
            }

            // Xóa các merged regions nằm trong các dòng từ 18 đến 39
            for (int i = sheet.getNumMergedRegions() - 1; i >= 0; i--) {
                CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
                if (mergedRegion.getFirstRow() >= 18 && mergedRegion.getLastRow() < 40) {
                    sheet.removeMergedRegion(i);
                }
            }

            // Sau đó mới xóa các hàng
            for (int i = 18; i < 40; i++) {
                Row removingRow = sheet.getRow(i);
                if (removingRow != null) {
                    sheet.removeRow(removingRow);
                }
            }


            //Điền dữ liệu vào bảng excel
            int rowIndex = 17;
            int index = 0;
            int manager = 0; // Nhà quản lý
            int highLevelTechnical = 0; // Chuyên môn kỹ thuật bậc cao
            int midLevelTechnical = 0; // Chuyên môn kỹ thuật bậc trung
            int other = 0; // Khác
            double salaryCoefficientOrLevel = 0.0; // Hệ số/ Mức lương


            int pageIndex = 1;
            int orderNumber = 1;
            boolean hasNextPage = true;
            long startTime = System.nanoTime();

            // Địa điểm làm việc chính
//        HashMap<UUID, LabourAgreementDto> mapLabourAgreementLatest = staffLabourAgreementService.getLabourAgreementLatestMap();

            long totalElements = 0;

            while (hasNextPage) {
                // searchStaffDto = new SearchStaffDto();
                searchDto.setPageIndex(pageIndex);
                searchDto.setPageSize(33);

                Page<StaffLabourUtilReportDto> staffPage = this.pagingStaffLabourUtilReport(searchDto);
                if (staffPage == null || staffPage.isEmpty()) {
                    break;
                }

                totalElements = staffPage.getTotalElements();

                for (StaffLabourUtilReportDto staffItem : staffPage.getContent()) {
                    if (index > 0) {
                        ++rowIndex;
                    }

                    ++index;
                    row = sheet.createRow(rowIndex);
                    sheet.getRow(rowIndex + 1);
                    if (row == null) {
                        continue;
                    }

                    // 0. STT
                    cell = row.createCell(0);
                    cell.setCellStyle(cellStyleCenter);
                    cell.setCellValue((double) index);


                    // 1. Họ tên
                    ExportExcelUtil.createCell(row, 1, staffItem.getDisplayName(), cellStyleLeft);

                    // 2. Mã số BHXH
                    ExportExcelUtil.createCell(row, 2, staffItem.getSocialInsuranceNumber(), cellStyleLeft);

                    // 3. Ngày sinh
                    ExportExcelUtil.createCell(row, 3, staffItem.getBirthDate(), cellStyleLeft);

                    // 4. Giới tính
                    ExportExcelUtil.createCell(row, 4, staffItem.getGender(), cellStyleLeft);

                    // 5. Số CCCD/ CMND/ Hộ chiếu
                    ExportExcelUtil.createCell(row, 5, staffItem.getStaffIndentity(), cellStyleLeft);

                    // 6. Cấp bậc, chức vụ, chức danh nghề, nơi làm việc
                    ExportExcelUtil.createCell(row, 6, staffItem.getRankTitleJoined(), cellStyleLeft);

                    // 7. Nhà quản lý
                    ExportExcelUtil.createCell(row, 7, staffItem.getIsManager(), cellStyleLeft);
                    if ("x".equals(staffItem.getIsManager())) {
                        manager++;
                    }

                    // 8. Chuyên môn kỹ thuật bậc cao
                    ExportExcelUtil.createCell(row, 8, staffItem.getHighTechQualification(), cellStyleLeft);
                    if ("x".equals(staffItem.getHighTechQualification())) {
                        highLevelTechnical++;
                    }

                    // 9. Chuyên môn kỹ thuật bậc trung
                    ExportExcelUtil.createCell(row, 9, staffItem.getMidTechQualification(), cellStyleLeft);
                    if ("x".equals(staffItem.getMidTechQualification())) {
                        midLevelTechnical++;
                    }

                    // 10. Khác
                    ExportExcelUtil.createCell(row, 10, staffItem.getOtherQualification(), cellStyleLeft);
                    if ("x".equals(staffItem.getOtherQualification())) {
                        other++;
                    }

                    // 11. Hệ số/ Mức lương
                    ExportExcelUtil.createCell(row, 11, staffItem.getSalaryCoefficient(), cellStyleLeft);

                    // 12. Phụ cấp - Chức vụ
                    ExportExcelUtil.createCell(row, 12, staffItem.getPositionAllowance(), cellStyleLeft);

                    // 13. Phụ cấp - Thâm niên VK (%)
                    ExportExcelUtil.createCell(row, 13, staffItem.getSeniorityAllowanceWork(), cellStyleLeft);

                    // 14. Phụ cấp - Thâm niên nghề (%)
                    ExportExcelUtil.createCell(row, 14, staffItem.getSeniorityAllowanceJob(), cellStyleLeft);

                    // 15. Phụ cấp - Phụ cấp lương
                    ExportExcelUtil.createCell(row, 15, staffItem.getSalaryAllowance(), cellStyleLeft);

                    // 16. Phụ cấp - Các khoản bổ sung
                    ExportExcelUtil.createCell(row, 16, staffItem.getOtherAllowance(), cellStyleLeft);

                    // 17. Ngày bắt đầu phụ cấp độc hại
                    ExportExcelUtil.createCell(row, 17, staffItem.getToxicAllowanceStartDate(), cellStyleLeft);

                    // 18. Ngày kết thúc phụ cấp độc hại
                    ExportExcelUtil.createCell(row, 18, staffItem.getToxicAllowanceEndDate(), cellStyleLeft);

                    // 19. Ngày bắt đầu HĐLĐ không xác định thời hạn
                    ExportExcelUtil.createCell(row, 19, staffItem.getIndefiniteContractStartDate(), cellStyleLeft);

                    // 20. Hiệu lực HĐLĐ xác định thời hạn - Ngày bắt đầu
                    ExportExcelUtil.createCell(row, 20, staffItem.getDefiniteContractStartDate(), cellStyleLeft);

                    // 21. Hiệu lực HĐLĐ xác định thời hạn - Ngày kết thúc
                    ExportExcelUtil.createCell(row, 21, staffItem.getDefiniteContractEndDate(), cellStyleLeft);

                    // 22. Hiệu lực HĐLĐ khác (dưới 1 tháng, thử việc) - Ngày bắt đầu
                    ExportExcelUtil.createCell(row, 22, staffItem.getOtherContractStartDate(), cellStyleLeft);

                    // 23. Hiệu lực HĐLĐ khác (dưới 1 tháng, thử việc) - Ngày kết thúc
                    ExportExcelUtil.createCell(row, 23, staffItem.getOtherContractEndDate(), cellStyleLeft);

                    // 24. Thời điểm đơn vị bắt đầu đóng BHXH
                    ExportExcelUtil.createCell(row, 24, staffItem.getStartSocialInsuranceDate(), cellStyleLeft);

                    // 25. Thời điểm đơn vị kết thúc đóng BHXH
                    ExportExcelUtil.createCell(row, 25, staffItem.getEndSocialInsuranceDate(), cellStyleLeft);

                    // 26. Ghi chú
                    ExportExcelUtil.createCell(row, 26, staffItem.getNote(), cellStyleLeft);


                }


                hasNextPage = staffPage.hasNext(); // Kiểm tra xem còn trang tiếp theo không
                pageIndex++; // Tăng pageIndex để lấy trang tiếp theo
            }


            row = sheet.createRow(++rowIndex);
            for (int i = 0; i <= 26; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(cellStyleLeft);
            }

            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 5));
            cell = row.getCell(0);
            cell.setCellStyle(cellStyleCenter);
            cell.setCellValue("Tổng");

            cell = row.getCell(6);
            cell.setCellStyle(cellStyleCenter);
            cell.setCellValue(totalElements);

            cell = row.getCell(7);
            cell.setCellStyle(cellStyleRight);
            cell.setCellValue(manager);

            cell = row.getCell(8);
            cell.setCellStyle(cellStyleRight);
            cell.setCellValue(highLevelTechnical);

            cell = row.getCell(9);
            cell.setCellStyle(cellStyleRight);
            cell.setCellValue(midLevelTechnical);

            cell = row.getCell(10);
            cell.setCellStyle(cellStyleRight);
            cell.setCellValue(other);

            cell = row.getCell(11);
            cell.setCellStyle(cellStyleRight);
            cell.setCellValue(salaryCoefficientOrLevel);


            // Xác định dòng bắt đầu để thêm dữ liệu xuống cuối
            int lastRowNum = sheet.getLastRowNum();
            int startRow = lastRowNum + 1;

            // Phục hồi dữ liệu
            for (int i = 0; i < tempData.size(); i++) {
                List<Object> rowData = tempData.get(i);
                Row newRow = sheet.createRow(startRow + i);

                for (int j = 0; j < rowData.size(); j++) {
                    Map<String, Object> cellInfo = (Map<String, Object>) rowData.get(j);
                    Cell newCell = newRow.createCell(j);

                    // Gán giá trị
                    newCell.setCellValue(cellInfo.get("value").toString());

                    // Gán style nếu có
                    if (cellInfo.get("style") != null) {
                        newCell.setCellStyle((CellStyle) cellInfo.get("style"));
                    }
                }
            }

            // Phục hồi merge
            for (CellRangeAddress region : mergedRegionsInTemp) {
                // Tính toán lại vị trí merge mới vì đã dán xuống cuối sheet
                int firstRow = region.getFirstRow() - 18 + startRow;
                int lastRow = region.getLastRow() - 18 + startRow;
                int firstCol = region.getFirstColumn();
                int lastCol = region.getLastColumn();

                CellRangeAddress newRegion = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
                sheet.addMergedRegion(newRegion);
            }

            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;
            logger.info("Xuất tất báo cáo tình hình sử dụng lao động - Xử lý mất {} ms ", elapsedTimeMs);

            return workbook;

        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }


    private void handleSetContractOrgNameOnLabourUsageReport(Sheet sheet, HrOrganization contractOrg) {
        int rowIndex = 1;
        int colIndex = 0;
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }

        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }

        // Lưu style cũ nếu có
        CellStyle originalStyle = cell.getCellStyle();

        // Gán giá trị mới
        cell.setCellValue(contractOrg.getName());

        // Giữ nguyên style
        if (originalStyle != null) {
            cell.setCellStyle(originalStyle);
        }
    }

    private void handleSetCurrentDateOnLabourUsageReport(Sheet sheet) {
        // Vị trí dòng 4 (index = 3), cột Z (index = 25)
        int rowIndex = 3;
        int colIndex = 14;

        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }

        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }

        // Lưu lại style cũ (nếu có) để giữ nguyên định dạng
        CellStyle originalStyle = cell.getCellStyle();

        // Lấy ngày hiện tại và định dạng theo yêu cầu
        LocalDate today = LocalDate.now();
        String formattedDate = String.format("HCM, ngày %d tháng %d năm %d",
                today.getDayOfMonth(), today.getMonthValue(), today.getYear());

        // Gán giá trị vào ô
        cell.setCellValue(formattedDate);

        // Áp dụng lại style cũ nếu có
        if (originalStyle != null) {
            cell.setCellStyle(originalStyle);
        }
    }

    private void handleReplaceAddress(Sheet sheet, HrOrganization contractOrg) {
        if (contractOrg == null) return;

        List<String> addressParts = new ArrayList<>();

        // Địa chỉ chi tiết luôn ở đầu
        if (StringUtils.hasText(contractOrg.getAddressDetail())) {
            addressParts.add(contractOrg.getAddressDetail().trim());
        }

        // Duyệt đơn vị hành chính từ cấp thấp lên cao
        AdministrativeUnit unit = contractOrg.getAdministrativeUnit();
        while (unit != null) {
            addressParts.add(unit.getName());
            unit = unit.getParent();
        }

        // Đảo ngược thứ tự các cấp hành chính để từ thấp đến cao
        if (addressParts.size() > 1) {
            List<String> administrativePart = addressParts.subList(1, addressParts.size());
            Collections.reverse(administrativePart);
        }

        // Ghép địa chỉ
        String fullAddress = String.join(", ", addressParts);

        // Vị trí ô cần thay thế
        String prefix = "- Địa chỉ:";
        int rowIndex = 11; // Cập nhật nếu dòng khác
        int colIndex = 0;

        Row row = sheet.getRow(rowIndex);
        if (row == null) return;

        Cell cell = row.getCell(colIndex);
        if (cell == null) return;

        String cellText = cell.getStringCellValue();
        if (!cellText.startsWith(prefix)) return;

        // Lưu style cũ
        CellStyle style = cell.getCellStyle();

        // Gán giá trị mới
        cell.setCellValue(prefix + " " + fullAddress);

        if (style != null) {
            cell.setCellStyle(style);
        }
    }


    private void handleReplaceWebsiteSimple(Sheet sheet, HrOrganization contractOrg) {
        String newWebsite = "";
        if (StringUtils.hasText(contractOrg.getWebsite())) {
            newWebsite = contractOrg.getWebsite();
        }
        String keyword = "website:";

        int rowIndex = 10; // Dòng tiếp theo sau tên công ty, bạn điều chỉnh nếu khác
        int colIndex = 0;  // Cột A

        Row row = sheet.getRow(rowIndex);
        if (row == null) return;

        Cell cell = row.getCell(colIndex);
        if (cell == null) return;

        String cellText = cell.getStringCellValue();
        int websiteIndex = cellText.toLowerCase().indexOf(keyword);
        if (websiteIndex == -1) return;

        // Cắt văn bản trước phần "website:"
        String updatedText = cellText.substring(0, websiteIndex + keyword.length()).trim() + " " + newWebsite;

        // Gán lại nội dung, giữ nguyên style
        cell.setCellValue(updatedText);
    }

    private void handleReplaceRepresentativeName(Sheet sheet, HrOrganization contractOrg) {
        if (contractOrg == null || contractOrg.getRepresentative() == null) return;

        String displayName = contractOrg.getRepresentative().getDisplayName();
        if (displayName == null || displayName.trim().isEmpty()) return;

        int rowIndex = 28;  // Dòng index 28
        int colIndex = 19;  // Cột T

        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }

        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }

        // Giữ nguyên style cũ
        CellStyle oldStyle = cell.getCellStyle();

        // Gán nội dung mới
        cell.setCellValue(displayName);

        // Gán lại style cũ nếu có
        if (oldStyle != null) {
            cell.setCellStyle(oldStyle);
        }
    }


    private void handleReplaceReportDateToday(Sheet sheet) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String todayFormatted = today.format(formatter);

        String keyword = "Tính đến";
        int rowIndex = 13; // Cập nhật đúng dòng chứa văn bản
        int colIndex = 0;  // Cột A

        Row row = sheet.getRow(rowIndex);
        if (row == null) return;

        Cell cell = row.getCell(colIndex);
        if (cell == null) return;

        String cellText = cell.getStringCellValue();
        int index = cellText.toLowerCase().indexOf(keyword.toLowerCase());
        if (index == -1) return;

        // Thay phần sau "Tính đến" bằng ngày hiện tại
        String updatedText = keyword + " " + todayFormatted;

        // Nếu văn bản còn nội dung khác phía trước hoặc sau, giữ lại
        if (index > 0) {
            updatedText = cellText.substring(0, index) + updatedText + ")";
        }

        cell.setCellValue(updatedText);
    }


    private void handleReplaceTaxCodeSimple(Sheet sheet, HrOrganization contractOrg) {
        String newTaxCode = "";
        if (StringUtils.hasText(contractOrg.getTaxCode())) {
            newTaxCode = contractOrg.getTaxCode();
        }
        String keyword = "mã số thuế):";

        int rowIndex = 11; // Dòng chứa thông tin mã số thuế, bạn điều chỉnh nếu khác
        int colIndex = 0;  // Cột A

        Row row = sheet.getRow(rowIndex);
        if (row == null) return;

        Cell cell = row.getCell(colIndex);
        if (cell == null) return;

        String cellText = cell.getStringCellValue();
        int taxCodeIndex = cellText.toLowerCase().indexOf(keyword);
        if (taxCodeIndex == -1) return;

        // Cắt văn bản trước phần "mã số thuế:"
        String updatedText = cellText.substring(0, taxCodeIndex + keyword.length()).trim() + " " + newTaxCode;

        // Gán lại nội dung, giữ nguyên style
        cell.setCellValue(updatedText);
    }


    private void handleReplaceCompanyNameSimple(Sheet sheet, HrOrganization contractOrg) {
        String newCompanyName = "";
        if (StringUtils.hasText(contractOrg.getName())) {
            newCompanyName = contractOrg.getName();
        }
        String oldCompanyName = "CÔNG TY TNHH THẨM MỸ LINH ANH REGION";

        int rowIndex = 8; // Dòng 10 (index 9)
        int colIndex = 0; // Cột A (index 0)

        Row row = sheet.getRow(rowIndex);
        if (row == null) return;

        Cell cell = row.getCell(colIndex);
        if (cell == null) return;

        String cellText = cell.getStringCellValue();
        if (!cellText.contains(oldCompanyName)) return;

        // Thay thế tên công ty cũ bằng tên mới
        String updatedText = cellText.replace(oldCompanyName, newCompanyName);

        // Gán lại nội dung cho ô, giữ nguyên style
        cell.setCellValue(updatedText);
    }


}

