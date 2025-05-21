package com.globits.hr.service.impl;

import com.globits.core.domain.*;
import com.globits.core.dto.*;
import com.globits.core.repository.*;
import com.globits.core.service.CountryService;
import com.globits.core.service.EthnicsService;
import com.globits.core.service.ReligionService;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.core.utils.SecurityUtils;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.function.ImportExcelMessageDto;
import com.globits.hr.dto.function.ImportStaffDto;
import com.globits.hr.dto.function.PositionTitleStaffDto;
import com.globits.hr.dto.importExcel.*;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.dto.staff.UserWithStaffDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.*;
import com.globits.hr.utils.*;
import com.globits.salary.domain.SalaryIncrementType;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.domain.SalaryPeriod;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.dto.excel.ImportSalaryStaffItemValueDto;
import com.globits.salary.repository.SalaryIncrementTypeRepository;
import com.globits.salary.repository.SalaryPeriodRepository;
import com.globits.salary.repository.SalaryResultRepository;
import com.globits.security.domain.Role;
import com.globits.security.domain.User;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.security.repository.RoleRepository;
import com.globits.security.repository.UserRepository;
import com.globits.timesheet.domain.LeaveRequest;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import com.globits.timesheet.repository.LeaveRequestRepository;

import jakarta.annotation.Resource;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;

@Service
public class StaffServiceImpl extends GenericServiceImpl<Staff, UUID> implements StaffService {
    private static final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);
    private static final String TEMPLATE_PATH = "EmployeeList.xlsx";
    private static final int PAGE_SIZE = 500;
    private static final String DATE_FORMAT = "dd/MM/yyyy";

    @Resource
    private ResourceLoader resourceLoader;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private ShiftWorkRepository shiftWorkRepository;
    @Autowired
    private LanguageRepository otherLanguageRepository;
    @Autowired
    private StaffRewardHistoryRepository staffRewardHistoryRepository;
    @Autowired
    private EducationalManagementLevelRepository educationalManagementLevelRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private TitleConferredRepository titleConferredRepository;
    @Autowired
    private PositionStaffRepository positionStaffRepository;
    @Autowired
    PersonAddressRepository personAddressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StaffWorkingLocationService staffWorkingLocationService;
    @Autowired
    private HrDocumentTemplateService hrDocumentTemplateService;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ReligionRepository religionRepository;
    @Autowired
    private StaffWorkingHistoryRepository staffWorkingHistoryRepository;
    @Autowired
    private StaffWorkingHistoryService staffWorkingHistoryService;
    @Autowired
    private EthnicsRepository ethnicsRepository;
    @Autowired
    AdministrativeUnitRepository administrativeUnitRepository;
    @Autowired
    HrOrganizationRepository hrOrganizationRepository;
    @Autowired
    HRDepartmentRepository hRDepartmentRepository;
    @Autowired
    HRDepartmentRepository departmentRepository;
    @Autowired
    DepartmentRepository DepartmentRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AllowanceSeniorityHistoryRepository allowanceSeniorityHistoryRepository;
    @Autowired
    ContractTypeRepository contractTypeRepository;
    @Autowired
    StaffLabourAgreementService staffLabourAgreementService;
    @Autowired
    LabourAgreementTypeService labourAgreementTypeService;
    @Autowired
    LabourAgreementTypeRepository labourAgreementTypeRepository;
    @Autowired
    PositionRepository positionRepository;
    @Autowired
    PositionService positionService;
    @Autowired
    PositionRelationShipRepository positionRelationShipRepository;
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
    StaffSocialInsuranceRepository staffSocialInsuranceRepository;
    @Autowired
    SalaryResultRepository salaryResultRepository;
    @Autowired
    SalaryPeriodRepository salaryPeriodRepository;
    @Autowired
    StaffSignatureRepository staffSignatureRepository;
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
    StaffOverseasWorkHistoryRepository staffOverseasWorkHistoryRepository;
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
    SalaryIncrementTypeRepository salaryIncrementTypeRepository;
    @Autowired
    private UserExtService userExtService;
    @Autowired
    private RewardFormRepository rewardFormRepository;
    @Autowired
    private HrOrganizationRepository organizationRepository;
    @Autowired
    private StaffTrainingHistoryRepository staffTrainingHistoryRepository;
    @Autowired
    private StaffTypeRepository staffTypeRepository;
    @Autowired
    private PersonBankAccountService personBankAccountService;
    @Autowired
    private StaffFamilyRelationshipService staffFamilyRelationshipService;
    @Autowired
    private StaffHierarchyService staffHierarchyService;
    @Autowired
    private StaffWorkScheduleRepository staffWorkScheduleRepository;

    @Override
    public StaffDto getStaff(UUID staffId) {
        Staff entity = this.getEntityById(staffId);
        if (entity == null)
            return null;

        if (entity.getUser() == null) {
            User user = userRepository.findByUsername(entity.getStaffCode());
            entity.setUser(user);
        }
        StaffDto result = new StaffDto(entity);

        if (result != null && entity.getFamilyRelationships() != null) {
            result.setFamilyRelationships(new ArrayList<>());

            for (StaffFamilyRelationship familyRelationship : entity.getFamilyRelationships()) {
                result.getFamilyRelationships().add(new StaffFamilyRelationshipDto(familyRelationship));
            }

            Collections.sort(result.getFamilyRelationships(), new Comparator<StaffFamilyRelationshipDto>() {
                @Override
                public int compare(StaffFamilyRelationshipDto o1, StaffFamilyRelationshipDto o2) {
                    // First, compare by displayOrder
                    if (o1.getFullName() == null && o2.getFullName() == null)
                        return 0;
                    if (o1.getFullName() == null)
                        return 1;
                    if (o2.getFullName() == null)
                        return -1;

                    int orderComparison = o1.getFullName().compareTo(o2.getFullName());
                    if (orderComparison != 0) {
                        return orderComparison;
                    }

                    // If displayOrder is the same, compare by displayName (handling nulls)
                    if (o1.getBirthDate() == null && o2.getBirthDate() == null)
                        return 0;
                    if (o1.getBirthDate() == null)
                        return 1;
                    if (o2.getBirthDate() == null)
                        return -1;
                    return o1.getBirthDate().compareTo(o2.getBirthDate());
                }
            });
        }

        return result;
    }

    @Override
    public Staff getEntityById(UUID staffId) {
        Staff entity = null;
        Optional<Staff> optional = staffRepository.findById(staffId);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        return entity;
    }

    @Override
    public StaffDto dismissAllPositionsOfStaff(StaffDto dto) {
        if (dto == null || dto.getId() == null) {
            return null;
        }
        Staff entity = staffRepository.findById(dto.getId()).orElse(null);
        if (entity == null) {
            return null;
        }

        if (dto.getStatus() != null && dto.getStatus().getId() != null) {
            EmployeeStatus status = employeeStatusRepository.findById(dto.getStatus().getId()).orElse(null);
            if (status != null) {
                entity.setStatus(status);
            }
        } else {
            entity.setStatus(null);
        }
        entity.setHasSocialIns(false);
        entity.getCurrentPositions().clear();
        Staff response = staffRepository.save(entity);
        return new StaffDto(response, false, false);
    }

    @Override
    public Page<StaffDto> findByPageBasicInfo(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        return staffRepository.findByPageBasicInfo(pageable);
    }

    @Override
    @Transactional
    // tạo phương thức createStaffAndAccountByCode từ StaffDto và UUID bà đặt tên là
    // staffDto và id
    public StaffDto createStaffAndAccountByCode(StaffDto staffDto, UUID id) {
        // xét null cho staffDto
        if (staffDto == null) {
            return null;
        }
        // xét null và validateName
        // if (staffDto.getFirstName() != null &&
        // !validateStringName(staffDto.getFirstName())) {
        // return null;
        // }
        // if (staffDto.getLastName() != null &&
        // !validateStringName(staffDto.getLastName())) {
        // return null;
        // }
        // if (staffDto.getDisplayName() != null &&
        // !validateStringName(staffDto.getDisplayName())) {
        // return null;
        // }

        String currentUserName = "Unknown User";
        LocalDateTime currentDate = LocalDateTime.now();

        boolean isRoleUser = false;
        boolean isRoleAdmin = false;
        boolean isRoleManager = false;

        UserDto userDto = userExtService.getCurrentUser();
        if (userDto != null && userDto.getRoles() != null && userDto.getRoles().size() > 0) {
            for (RoleDto item : userDto.getRoles()) {
                // nếu name của item khác null và name của item giống ROLE_ADMIN thì isRoleAdmin
                // = true
                if (item.getName() != null && "ROLE_ADMIN".equals(item.getName())) {
                    isRoleAdmin = true;
                }
                if (item.getName() != null && "HR_MANAGER".equals(item.getName())) {
                    isRoleManager = true;
                }
                if (item.getName() != null && "HR_USER".equals(item.getName())) {
                    isRoleUser = true;
                }
            }
        }
        if (isRoleAdmin) {
            isRoleUser = false;
        } else {
            if (isRoleManager) {
                isRoleUser = false;
            }
        }

        Staff staff = null;
        if (id != null) {
            // nếu id của staffDto khác null và id của staffDto giống id (UUID)( truy)
            // nếu staffDto.getId() == null ---> thêm mới nhân viên
            if (staffDto.getId() != null && !staffDto.getId().equals(id)) {
                return null;
            }
        }
        // nếu staffDto khác null và id khác null thì tạo một optinal tìm và chứa id của
        // Staff
        if (staffDto.getId() != null && id != null) {
            Optional<Staff> optional = staffRepository.findById(id);
            // trả về true nếu có giá trị trong optinal k thì trả về false
            if (optional.isPresent()) {
                // gán dữ liệu id của optinal cho staff
                staff = optional.get();
                if (isRoleUser) {
                    // nếu userName của userDto khác null và userName của userDto so sánh với
                    // staffCode của staff thì trả về null
                    if (userDto.getUsername() != null && !userDto.getUsername().equals(staff.getStaffCode())) {
                        return null;
                    }
                }
            }
        }
        // nếu staff bằng null và staffCode của staffDto khác null
        if (staff == null && staffDto.getStaffCode() != null) {
            // tạo một list
            List<Staff> list = staffRepository.getByCode(staffDto.getStaffCode());
            if (list != null && list.size() > 0) {
                staff = list.get(0);
            }
        }
        if (staff == null) {
            staff = new Staff();
        }
        // set các trường trong class staff()
        staff.setStaffCode(staffDto.getStaffCode());
//        if (staffDto.getUsername() != null)
//            staff.setStaffCode(staffDto.getUsername());
        staff.setFirstName(normalize(staffDto.getFirstName()));
        staff.setLastName(normalize(staffDto.getLastName()));
        staff.setBirthDate(staffDto.getBirthDate());
        staff.setBirthPlace(staffDto.getBirthPlace());
        staff.setGender(staffDto.getGender());
        staff.setPhoto(staffDto.getPhoto());
        staff.setDisplayName(normalize(staffDto.getDisplayName()));
        staff.setPhoneNumber(staffDto.getPhoneNumber());
        staff.setMaritalStatus(staffDto.getMaritalStatus());
        staff.setCurrentWorkingStatus(staffDto.getCurrentWorkingStatus());
        staff.setSalaryCoefficient(staffDto.getSalaryCoefficient());
        staff.setSocialInsuranceNumber(staffDto.getSocialInsuranceNumber());
        staff.setJobTitle(staffDto.getJobTitle());
        staff.setHighestPosition(staffDto.getHighestPosition());
        staff.setDateOfReceivingPosition(staffDto.getDateOfReceivingPosition());
        staff.setPositionDecisionNumber(staffDto.getPositionDecisionNumber());
        staff.setDateOfReceivingAllowance(staffDto.getDateOfReceivingAllowance());
        staff.setProfessionalTitles(staffDto.getProfessionalTitles());
        staff.setAllowanceCoefficient(staffDto.getAllowanceCoefficient());
        staff.setSalaryLeve(staffDto.getSalaryLeve());
        staff.setEthnicLanguage(staffDto.getEthnicLanguage());
        staff.setPhysicalEducationTeacher(staffDto.getPhysicalEducationTeacher());
        staff.setSpecializedName(staffDto.getSpecializedName());
        staff.setFormsOfTraining(staffDto.getFormsOfTraining());
        staff.setTrainingCountry(staffDto.getTrainingCountry());
        staff.setTrainingPlaces(staffDto.getTrainingPlaces());
        staff.setHighSchoolEducation(staffDto.getHighSchoolEducation());
        staff.setQualification(staffDto.getQualification());
        staff.setCertificationScore(staffDto.getCertificationScore());
        staff.setYearOfCertification(staffDto.getYearOfCertification());
        staff.setNote(staffDto.getNote());
        staff.setYearOfRecognitionDegree(staffDto.getYearOfRecognitionDegree());
        staff.setYearOfRecognitionAcademicRank(staffDto.getYearOfRecognitionAcademicRank());
        staff.setImagePath(staffDto.getImagePath());
        staff.setPermanentResidence(staffDto.getPermanentResidence());
        staff.setCurrentResidence(staffDto.getCurrentResidence());
        staff.setWards(staffDto.getWards());
        staff.setYearOfConferred(staffDto.getYearOfConferred());
        staff.setFamilyComeFromString(staffDto.getFamilyComeFrom());
        staff.setFamilyPriority(staffDto.getFamilyPriority());
        staff.setFamilyYourself(staffDto.getFamilyYourself());

//        // set ngôn ngữ thông thạo
//        if (staffDto.getOtherLanguageLevel() != null) {
//            EducationDegree otherLanguageLevel = null;
//            Optional<EducationDegree> optional = educationDegreeRepository
//                    .findById(staffDto.getOtherLanguageLevel().getId());
//            if (optional.isPresent()) {
//                otherLanguageLevel = optional.get();
//            }
//            staff.setOtherLanguageLevel(otherLanguageLevel);
//        }
//        else{
//            staff.setOtherLanguageLevel(null);
//        }
//        // set đang học hiện tại
//        if (staffDto.getStudying() != null) {
//            EducationDegree studying = null;
//            Optional<EducationDegree> optional = educationDegreeRepository.findById(staffDto.getStudying().getId());
//            if (optional.isPresent()) {
//                studying = optional.get();
//            }
//            staff.setStudying(studying);
//        }
//        else{
//            staff.setStudying(null);
//        }
//        // set loại công chức
//        if (staffDto.getCivilServantCategory() != null) {
//            CivilServantCategory civilServantCategory = null;
//            Optional<CivilServantCategory> optional = civilServantCategoryRepository
//                    .findById(staffDto.getCivilServantCategory().getId());
//            if (optional.isPresent()) {
//                civilServantCategory = optional.get();
//            }
//            staff.setCivilServantCategory(civilServantCategory);
//        } else {
//            staff.setCivilServantCategory(null);
//        }

//        // set cấp công chức
//        if (staffDto.getGrade() != null) {
//            CivilServantGrade grade = null;
//            Optional<CivilServantGrade> optional = civilServantGradeRepository.findById(staffDto.getGrade().getId());
//            if (optional.isPresent()) {
//                grade = optional.get();
//            }
//            staff.setGrade(grade);
//        } else {
//            staff.setGrade(null);
//        }
//        // set nghề nghiệp
//        if (staffDto.getProfession() != null) {
//            Profession profession = null;
//            Optional<Profession> optional = professionRepository.findById(staffDto.getProfession().getId());
//            if (optional.isPresent()) {
//                profession = optional.get();
//            }
//            staff.setProfession(profession);
//        } else {
//            staff.setProfession(null);
//        }
        // set trạng thái
        if (staffDto.getStatus() != null) {
            EmployeeStatus status = null;
            Optional<EmployeeStatus> optional = employeeStatusRepository.findById(staffDto.getStatus().getId());
            if (optional.isPresent()) {
                status = optional.get();
            }
            staff.setStatus(status);
        } else {
            staff.setStatus(null);
        }
        // set dân tộc
        if (staffDto.getEthnics() != null) {
            Ethnics ethnics = null;
            Optional<Ethnics> optional = ethnicsRepository.findById(staffDto.getEthnics().getId());
            if (optional.isPresent()) {
                ethnics = optional.get();
            }
            staff.setEthnics(ethnics);
        } else {
            staff.setEthnics(null);
        }
        // set kỹ năng vi tính
        if (staffDto.getComputerSkill() != null) {
            EducationDegree computerSkill = null;
            Optional<EducationDegree> optional = educationDegreeRepository
                    .findById(staffDto.getComputerSkill().getId());
            if (optional.isPresent()) {
                computerSkill = optional.get();
            }
            staff.setComputerSkill(computerSkill);
        } else {
            staff.setComputerSkill(null);
        }
        // set cấp độ ngoại ngữ tiếng anh
        if (staffDto.getEnglishLevel() != null) {
            EducationDegree englishLevel = null;
            Optional<EducationDegree> optional = educationDegreeRepository.findById(staffDto.getEnglishLevel().getId());
            if (optional.isPresent()) {
                englishLevel = optional.get();
            }
            staff.setEnglishLevel(englishLevel);
        } else {
            staff.setEnglishLevel(null);
        }
        // set chứng chỉ tiếng anh
        if (staffDto.getEnglishCertificate() != null) {
            Certificate englishCertificate = null;
            Optional<Certificate> optional = certificateRepository.findById(staffDto.getEnglishCertificate().getId());
            if (optional.isPresent()) {
                englishCertificate = optional.get();
            }
            staff.setEnglishCertificate(englishCertificate);
        } else {
            staff.setEnglishCertificate(null);
        }
        // set xếp hạng học tập
        if (staffDto.getAcademicRank() != null) {
            AcademicTitle academicRank = null;
            Optional<AcademicTitle> optional = academicTitleRepository.findById(staffDto.getAcademicRank().getId());
            if (optional.isPresent()) {
                academicRank = optional.get();
            }
            staff.setAcademicRank(academicRank);
        } else {
            staff.setAcademicRank(null);
        }
        // set bằng cấp
        if (staffDto.getDegree() != null) {
            EducationDegree degree = null;
            Optional<EducationDegree> optional = educationDegreeRepository.findById(staffDto.getDegree().getId());
            if (optional.isPresent()) {
                degree = optional.get();
            }
            staff.setDegree(degree);
        } else {
            staff.setDegree(null);
        }
        if (staffDto.getOtherLanguage() != null) {
            Language otherLanguage = null;
            Optional<Language> optional = otherLanguageRepository.findById(staffDto.getOtherLanguage().getId());
            if (optional.isPresent()) {
                otherLanguage = optional.get();
            }
            staff.setOtherLanguage(otherLanguage);
        } else {
            staff.setOtherLanguage(null);
        }

        // set quốc tịch
        if (staffDto.getNationality() != null) {
            Country nationality = null;
            Optional<Country> optional = countryRepository.findById(staffDto.getNationality().getId());
            if (optional.isPresent()) {
                nationality = optional.get();
            }
            staff.setNationality(nationality);
        } else {
            staff.setNationality(null);
        }
        // set Email
        if (staffDto.getEmail() != null) {
            staff.setEmail(staffDto.getEmail());
        } else {
            staff.setEmail(null);
        }
        // set phòng ban
        if (staffDto.getDepartment() != null) {
            if (staffDto.getDepartment().getId() != null) {
                HRDepartment department = null;
                Optional<HRDepartment> optional = hRDepartmentRepository.findById(staffDto.getDepartment().getId());
                if (optional.isPresent()) {
                    department = optional.get();
                }
                staff.setDepartment(department);
            }
        } else {
            staff.setDepartment(null);
        }

        // set quê quán
        if (staffDto.getNativeVillage() != null) {
            AdministrativeUnit nativeVillage = null;
            Optional<AdministrativeUnit> optional = administrativeUnitRepository
                    .findById(staffDto.getNativeVillage().getId());
            if (optional.isPresent()) {
                nativeVillage = optional.get();
            }
            staff.setNativeVillage(nativeVillage);
        } else {
            staff.setNativeVillage(null);
        }

        // luu thong tin tinh/tp, quan/huyen, xa/phuong
        HrAdministrativeUnitDto administrativeUnit = staffDto.getAdministrativeunit();
        HrAdministrativeUnitDto district = staffDto.getDistrict();
        HrAdministrativeUnitDto province = staffDto.getProvince();
        if (administrativeUnit != null && administrativeUnit.getId() != null) {
            administrativeUnitRepository.findById(administrativeUnit.getId()).ifPresent(staff::setAdministrativeUnit);
        } else if (district != null && district.getId() != null) {
            administrativeUnitRepository.findById(district.getId()).ifPresent(staff::setAdministrativeUnit);
        } else if (province != null && province.getId() != null) {
            administrativeUnitRepository.findById(province.getId()).ifPresent(staff::setAdministrativeUnit);
        } else {
            staff.setAdministrativeUnit(null);
        }

        // set ton giáo
        if (staffDto.getReligion() != null) {
            if (staffDto.getReligion().getId() != null) {
                Religion religion = null;
                Optional<Religion> optional = religionRepository.findById(staffDto.getReligion().getId());
                if (optional.isPresent()) {
                    religion = optional.get();
                }
                staff.setReligion(religion);
            }
        } else {
            staff.setReligion(null);
        }
        // set bằng cấp chuyên nghiệp
        // if (staffDto.getProfessionalDegree() != null) {
        // ProfessionalDegree professionalDegree = null;
        // Optional<ProfessionalDegree> optional = professionalDegreeRepository
        // .findById(staffDto.getProfessionalDegree().getId());
        // if (optional.isPresent()) {
        // professionalDegree = optional.get();
        // }
        // staff.setProfessionalDegree(professionalDegree);
        // } else {
        // staff.setProfessionalDegree(null);
        // }
        // set thông tin bằng cấp
        if (staffDto.getInformaticDegree() != null) {
            InformaticDegree informaticDegree = null;
            Optional<InformaticDegree> optional = informaticDegreeRepository
                    .findById(staffDto.getInformaticDegree().getId());
            if (optional.isPresent()) {
                informaticDegree = optional.get();
            }
            staff.setInformaticDegree(informaticDegree);
        } else {
            staff.setInformaticDegree(null);
        }
        // set
        if (staffDto.getConferred() != null) {
            TitleConferred titleConferred = null;
            Optional<TitleConferred> optional = titleConferredRepository.findById(staffDto.getConferred().getId());
            if (optional.isPresent()) {
                titleConferred = optional.get();
            }
            staff.setConferred(titleConferred);
        } else {
            staff.setConferred(null);
        }
        // // set trình độ chính trị
        // if (staffDto.getPoliticalTheoryLevel() != null) {
        // PoliticalTheoryLevel politicalTheoryLevel = null;
        // Optional<PoliticalTheoryLevel> optional = politicalTheoryLevelRepository
        // .findById(staffDto.getPoliticalTheoryLevel().getId());
        // if (optional.isPresent()) {
        // politicalTheoryLevel = optional.get();
        // }
        // staff.setPoliticalTheoryLevel(politicalTheoryLevel);
        // } else {
        // staff.setPoliticalTheoryLevel(null);
        // }
        // set trình độ quản lý nhà nước
//	    if (staffDto.getStateManagementLevel() != null) {
//	        StateManagementLevel stateManagementLevel = null;
//	        Optional<StateManagementLevel> optional = stateManagementLevelRepository
//	                .findById(staffDto.getStateManagementLevel().getId());
//	        if (optional.isPresent()) {
//	            stateManagementLevel = optional.get();
//	        }
//	        staff.setStateManagementLevel(stateManagementLevel);
//	    } else {
//	        staff.setStateManagementLevel(null);
//	    }
        // set trình độ quản lý giáo dục
        if (staffDto.getEducationalManagementLevel() != null) {
            EducationalManagementLevel educationalManagementLevel = null;
            Optional<EducationalManagementLevel> optional = educationalManagementLevelRepository
                    .findById(staffDto.getEducationalManagementLevel().getId());
            if (optional.isPresent()) {
                educationalManagementLevel = optional.get();
            }
            staff.setEducationalManagementLevel(educationalManagementLevel);
        } else {
            staff.setEducationalManagementLevel(null);
        }
        // set thỏa thuận lao động
        if (staffDto.getLabourAgreementType() != null && staffDto.getLabourAgreementType().getId() != null) {

            LabourAgreementType labourAgreementType = null;
            Optional<LabourAgreementType> optional = labourAgreementTypeRepository
                    .findById(staffDto.getLabourAgreementType().getId());
            if (optional.isPresent()) {
                labourAgreementType = optional.get();
            }
            staff.setLabourAgreementType(labourAgreementType);
        } else {
            staff.setLabourAgreementType(null);
        }

        staff.setIdNumber(staffDto.getIdNumber());
        staff.setIdNumberIssueBy(staffDto.getIdNumberIssueBy());
        staff.setIdNumberIssueDate(staffDto.getIdNumberIssueDate());
        // set tình trạng hôn nhân
        if (staffDto.getMaritalStatus() != null) {
            staff.setMaritalStatus(staffDto.getMaritalStatus());
        } else {
            staff.setMaritalStatus(null);
        }
        // set thời hạn hợp đồng
        if (staffDto.getContractDate() != null) {
            staff.setContractDate(staffDto.getContractDate());
        } else {
            staff.setContractDate(null);
        }
        // set chức vụ
        if (staffDto.getJobTitle() != null) {
            staff.setJobTitle(staffDto.getJobTitle());
        } else {
            staff.setJobTitle(null);
        }
        // set loại công chức
        if (staffDto.getCivilServantType() != null) {
            CivilServantType type = null;
            Optional<CivilServantType> optional = civilServantTypeRepository
                    .findById(staffDto.getCivilServantType().getId());
            if (optional.isPresent()) {
                type = optional.get();
            }
            staff.setCivilServantType(type);
        } else {
            staff.setCivilServantType(null);
        }
        staff.setStartDate(staffDto.getStartDate()); // set ngày bắt đầu
        staff.setRecruitmentDate(staffDto.getRecruitmentDate());// set ngày yêu cầu
        staff.setSalaryStartDate(staffDto.getSalaryStartDate());// set ngày bắt đầu nhận lương
        staff.setGraduationYear(staffDto.getGraduationYear());// ngày tốt nghiệp
        staff.setForeignLanguageName(staffDto.getForeignLanguageName());// set tên ngoại ngữ

        // set địa chi
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
        } else {
            if (staff.getAddress() != null) {
                staff.getAddress().clear();
            }
        }
        HashSet<AllowanceSeniorityHistory> allowanceSeniorityHistories = new HashSet<>();
        if (staffDto.getAllowanceSeniorityHistory() != null && staffDto.getAllowanceSeniorityHistory().size() > 0) {
            for (AllowanceSeniorityHistoryDto allowanceSeniorityHistoryDto : staffDto.getAllowanceSeniorityHistory()) {
                if (allowanceSeniorityHistoryDto != null) {
                    AllowanceSeniorityHistory allowanceSeniorityHistory = null;
                    if (allowanceSeniorityHistoryDto.getId() != null) {
                        Optional<AllowanceSeniorityHistory> optional = allowanceSeniorityHistoryRepository
                                .findById(allowanceSeniorityHistoryDto.getId());
                        if (optional.isPresent()) {
                            allowanceSeniorityHistory = optional.get();
                        }
                    }
                    if (allowanceSeniorityHistory == null) {
                        allowanceSeniorityHistory = new AllowanceSeniorityHistory();
                    }
                    if (allowanceSeniorityHistoryDto.getQuotaCode() != null
                            && allowanceSeniorityHistoryDto.getQuotaCode().getId() != null) {
                        CivilServantCategory civilServantCategory = null;
                        Optional<CivilServantCategory> optional = civilServantCategoryRepository
                                .findById(allowanceSeniorityHistoryDto.getQuotaCode().getId());
                        if (optional.isPresent()) {
                            civilServantCategory = optional.get();
                        }
                        allowanceSeniorityHistory.setQuotaCode(civilServantCategory);
                    }
                    allowanceSeniorityHistory.setStartDate(allowanceSeniorityHistoryDto.getStartDate());
                    allowanceSeniorityHistory.setNote(allowanceSeniorityHistoryDto.getNote());
                    allowanceSeniorityHistory.setPercentReceived(allowanceSeniorityHistoryDto.getPercentReceived());
                    allowanceSeniorityHistory.setStaff(staff);
                    allowanceSeniorityHistories.add(allowanceSeniorityHistory);
                }
            }
        }
        if (allowanceSeniorityHistories.size() > 0) {
            if (staff.getAllowanceSeniorityHistories() == null) {
                staff.setAllowanceSeniorityHistories(allowanceSeniorityHistories);
            } else {
                staff.getAllowanceSeniorityHistories().clear();
                staff.getAllowanceSeniorityHistories().addAll(allowanceSeniorityHistories);
            }
        } else {
            if (staff.getAllowanceSeniorityHistories() != null) {
                staff.getAllowanceSeniorityHistories().clear();
            }
        }
        // set qua trinh chức vụ
        HashSet<PositionStaff> positions = new HashSet<>();
        if (staffDto.getPositions() != null && staffDto.getPositions().size() > 0) {

            boolean hasMainPosition = false;

            for (PositionStaffDto psDto : staffDto.getPositions()) {
                if (psDto != null) {
                    PositionStaff newPs = null;
                    if (psDto.getId() != null) {
                        Optional<PositionStaff> optional = positionStaffRepository.findById(psDto.getId());
                        if (optional.isPresent()) {
                            newPs = optional.get();
                        }
                    }
                    if (newPs == null) {
                        newPs = new PositionStaff();
                    }

                    // Đảm bảo chỉ có 1 thằng được set true
                    if (psDto.getMainPosition() && !hasMainPosition) {
                        hasMainPosition = true;
                        newPs.setMainPosition(true);
                    } else {
                        newPs.setMainPosition(false);
                    }

                    if (psDto.getPosition() != null && psDto.getPosition().getId() != null) {
                        Position position = null;
                        Optional<Position> optional = positionRepository.findById(psDto.getPosition().getId());
                        if (optional.isPresent()) {
                            position = optional.get();
                        }
                        newPs.setPosition(position);
                    }
                    // set phong ban
                    if (psDto.getHrDepartment() != null && psDto.getHrDepartment().getId() != null) {
                        HRDepartment department = null;
                        Optional<HRDepartment> optional = departmentRepository
                                .findById(psDto.getHrDepartment().getId());
                        if (optional.isPresent()) {
                            department = optional.get();
                        }
                        newPs.setHrDepartment(department);
                    }
                    newPs.setFromDate(psDto.getFromDate());
                    newPs.setToDate(psDto.getToDate());
                    newPs.setRelationshipType(psDto.getRelationshipType());
                    if (psDto.getSupervisor() != null && psDto.getSupervisor().getId() != null) {
                        Staff supervisor = staffRepository.findById(psDto.getSupervisor().getId()).orElse(null);
                        newPs.setSupervisor(supervisor);
                    }
                    newPs.setStaff(staff);
                    positions.add(newPs);
                }
            }
        }
        if (positions.size() > 0) {
            if (staff.getPositions() == null) {
                staff.setPositions(positions);
            } else {
                staff.getPositions().clear();
                staff.getPositions().addAll(positions);
            }
        } else {
            if (staff.getPositions() != null) {
                staff.getPositions().clear();
            }
        }
        // set quan hệ gia đình
        // tạo một listFamilyRelationship theo kiểu Hashset
        Set<StaffFamilyRelationship> listFamilyRelationship = new HashSet<>();
        if (staffDto.getFamilyRelationships() != null && staffDto.getFamilyRelationships().size() > 0) {
            // duyệt các phần tử từ familyRelationship vào
            // staffFamilyRelationshipDto(StaffFamilyRelationshipDto)
            for (StaffFamilyRelationshipDto staffFamilyRelationshipDto : staffDto.getFamilyRelationships()) {

                if (staffFamilyRelationshipDto != null) {
                    StaffFamilyRelationship staffFamilyRelationship = null;
                    if (staffFamilyRelationshipDto.getId() != null) {
                        Optional<StaffFamilyRelationship> optional = staffFamilyRelationshipRepository
                                .findById(staffFamilyRelationshipDto.getId());
                        if (optional.isPresent()) {
                            staffFamilyRelationship = optional.get();
                        }
                    }
                    if (staffFamilyRelationship == null) {
                        staffFamilyRelationship = new StaffFamilyRelationship();
                    }
                    if (staffFamilyRelationshipDto.getFamilyRelationship() != null
                            && staffFamilyRelationshipDto.getFamilyRelationship().getId() != null) {
                        FamilyRelationship familyRelationship = null;
                        Optional<FamilyRelationship> optional = familyRelationshipRepository
                                .findById(staffFamilyRelationshipDto.getFamilyRelationship().getId());
                        if (optional.isPresent()) {
                            familyRelationship = optional.get();
                        }
                        staffFamilyRelationship.setFamilyRelationship(familyRelationship);

                    }
                    if (staffFamilyRelationshipDto.getProfession() != null
                            && staffFamilyRelationshipDto.getProfession().getId() != null) {
                        Profession profession = null;
                        Optional<Profession> optional = professionRepository
                                .findById(staffFamilyRelationshipDto.getProfession().getId());
                        if (optional.isPresent()) {
                            profession = optional.get();
                        }
                        staffFamilyRelationship.setProfession(profession);

                    }
                    staffFamilyRelationship.setDescription(staffFamilyRelationshipDto.getDescription());
                    staffFamilyRelationship.setAddress(staffFamilyRelationshipDto.getAddress());
                    staffFamilyRelationship.setFullName(staffFamilyRelationshipDto.getFullName());
                    staffFamilyRelationship.setBirthDate(staffFamilyRelationshipDto.getBirthDate());
                    staffFamilyRelationship.setWorkingPlace(staffFamilyRelationshipDto.getWorkingPlace());
                    staffFamilyRelationship.setStaff(staff);
                    staffFamilyRelationship.setIsDependent(staffFamilyRelationshipDto.getIsDependent());

                    listFamilyRelationship.add(staffFamilyRelationship);
                }
            }
        }
        if (listFamilyRelationship.size() > 0) {
            if (staff.getFamilyRelationships() == null) {
                staff.setFamilyRelationships(listFamilyRelationship);
            } else {
                staff.getFamilyRelationships().clear();
                staff.getFamilyRelationships().addAll(listFamilyRelationship);
            }
        } else {
            if (staff.getFamilyRelationships() != null) {
                staff.getFamilyRelationships().clear();
            }
        }
        // set quá trình đào tạo
//        Set<StaffEducationHistory> ListEducationHistory = new HashSet<>();
//        if (staffDto.getEducationHistory() != null && staffDto.getEducationHistory().size() > 0) {
//            for (StaffEducationHistoryDto staffEducationHistoryDto : staffDto.getEducationHistory()) {
//                if (staffEducationHistoryDto != null) {
//                    StaffEducationHistory staffEducationHistory = null;
//                    if (staffEducationHistoryDto.getId() != null) {
//                        Optional<StaffEducationHistory> optional = staffEducationHistoryRepository
//                                .findById(staffEducationHistoryDto.getId());
//                        if (optional.isPresent()) {
//                            staffEducationHistory = optional.get();
//                        }
//                    }
//                    if (staffEducationHistory == null) {
//                        staffEducationHistory = new StaffEducationHistory();
//                    }
//                    if (staffEducationHistoryDto.getCountry() != null
//                            && staffEducationHistoryDto.getCountry().getId() != null) {
//                        Country country = null;
//                        Optional<Country> optional = countryRepository
//                                .findById(staffEducationHistoryDto.getCountry().getId());
//                        if (optional.isPresent()) {
//                            country = optional.get();
//                        }
//                        staffEducationHistory.setCountry(country);
//                    }
//                    if (staffEducationHistoryDto.getSpeciality() != null
//                            && staffEducationHistoryDto.getSpeciality().getId() != null) {
//                        HrSpeciality hrSpeciality = null;
//                        Optional<HrSpeciality> optional = hrSpecialityRepository
//                                .findById(staffEducationHistoryDto.getSpeciality().getId());
//                        if (optional.isPresent()) {
//                            hrSpeciality = optional.get();
//                        }
//                        staffEducationHistory.setSpeciality(hrSpeciality);
//                    }
//                    if (staffEducationHistoryDto.getMajor() != null
//                            && staffEducationHistoryDto.getMajor().getId() != null) {
//                        HrSpeciality hrSpeciality = null;
//                        Optional<HrSpeciality> optional = hrSpecialityRepository
//                                .findById(staffEducationHistoryDto.getMajor().getId());
//                        if (optional.isPresent()) {
//                            hrSpeciality = optional.get();
//                        }
//                        staffEducationHistory.setMajor(hrSpeciality);
//                    }
//                    if (staffEducationHistoryDto.getEducationType() != null
//                            && staffEducationHistoryDto.getEducationType().getId() != null) {
//                        HrEducationType hrEducationType = null;
//                        Optional<HrEducationType> optional = hrEducationTypeRepository
//                                .findById(staffEducationHistoryDto.getEducationType().getId());
//                        if (optional.isPresent()) {
//                            hrEducationType = optional.get();
//                        }
//                        staffEducationHistory.setEducationType(hrEducationType);
//                    }
//                    if (staffEducationHistoryDto.getEducationalInstitution() != null
//                            && staffEducationHistoryDto.getEducationalInstitution().getId() != null) {
//                        EducationalInstitution educationalInstitution = null;
//                        Optional<EducationalInstitution> optional = educationalInstitutionRepository
//                                .findById(staffEducationHistoryDto.getEducationalInstitution().getId());
//                        if (optional.isPresent()) {
//                            educationalInstitution = optional.get();
//                        }
//                        staffEducationHistory.setEducationalInstitution(educationalInstitution);
//                    }
//                    if (staffEducationHistoryDto.getEducationDegree() != null
//                            && staffEducationHistoryDto.getEducationDegree().getId() != null) {
//                        EducationDegree educationDegree = null;
//                        Optional<EducationDegree> optional = educationDegreeRepository
//                                .findById(staffEducationHistoryDto.getEducationDegree().getId());
//                        if (optional.isPresent()) {
//                            educationDegree = optional.get();
//                        }
//                        staffEducationHistory.setEducationDegree(educationDegree);
//                    }
//                    staffEducationHistory.setStartDate(staffEducationHistoryDto.getStartDate());
//                    staffEducationHistory.setEndDate(staffEducationHistoryDto.getEndDate());
//                    staffEducationHistory.setStatus(staffEducationHistoryDto.getStatus());
//                    staffEducationHistory.setSchoolName(staffEducationHistoryDto.getSchoolName());
//                    staffEducationHistory.setActualGraduationYear(staffEducationHistoryDto.getActualGraduationYear());
//                    staffEducationHistory.setReturnDate(staffEducationHistoryDto.getReturnDate());
//                    staffEducationHistory.setDecisionCode(staffEducationHistoryDto.getDecisionCode());
//                    staffEducationHistory.setDecisionDate(staffEducationHistoryDto.getDecisionDate());
//                    staffEducationHistory.setFundingSource(staffEducationHistoryDto.getFundingSource());
//                    staffEducationHistory.setExtendDateByDecision(staffEducationHistoryDto.getExtendDateByDecision());
//                    staffEducationHistory.setExtendDecisionDate(staffEducationHistoryDto.getExtendDecisionDate());
//                    staffEducationHistory.setExtendDecisionCode(staffEducationHistoryDto.getExtendDecisionCode());
//                    staffEducationHistory.setBasis(staffEducationHistoryDto.getBasis());
//                    staffEducationHistory.setDescription(staffEducationHistoryDto.getDescription());
//                    staffEducationHistory.setIsConfirmation(staffEducationHistoryDto.getIsConfirmation());
//                    staffEducationHistory.setIsCurrent(staffEducationHistoryDto.getIsCurrent());
//                    staffEducationHistory.setIsCountedForSeniority(staffEducationHistoryDto.getIsCountedForSeniority());
//                    staffEducationHistory.setIsExtended(staffEducationHistoryDto.getIsExtended());
//                    staffEducationHistory.setNotFinish(staffEducationHistoryDto.getNotFinish());
//                    staffEducationHistory.setStaff(staff);
//                    ListEducationHistory.add(staffEducationHistory);
//                }
//            }
//        }
//        if (ListEducationHistory.size() > 0) {
//            if (staff.getEducationHistory() == null) {
//                staff.setEducationHistory(ListEducationHistory);
//            } else {
//                staff.getEducationHistory().clear();
//                staff.getEducationHistory().addAll(ListEducationHistory);
//            }
//        } else {
//            if (staff.getEducationHistory() != null) {
//                staff.getEducationHistory().clear();
//            }
//        }

        Set<StaffOverseasWorkHistory> ListStaffOverseasWorkHistory = new HashSet<>();
        if (staffDto.getOverseasWorkHistory() != null && staffDto.getOverseasWorkHistory().size() > 0) {
            for (StaffOverseasWorkHistoryDto staffOverseasWorkHistoryDto : staffDto.getOverseasWorkHistory()) {
                if (staffOverseasWorkHistoryDto != null) {
                    StaffOverseasWorkHistory staffOverseasWorkHistory = null;
                    if (staffOverseasWorkHistoryDto.getId() != null) {
                        Optional<StaffOverseasWorkHistory> optional = staffOverseasWorkHistoryRepository
                                .findById(staffOverseasWorkHistoryDto.getId());
                        if (optional.isPresent()) {
                            staffOverseasWorkHistory = optional.get();
                        }
                    }
                    if (staffOverseasWorkHistory == null) {
                        staffOverseasWorkHistory = new StaffOverseasWorkHistory();
                    }
                    if (staffOverseasWorkHistoryDto.getCountry() != null
                            && staffOverseasWorkHistoryDto.getCountry().getId() != null) {
                        Country country = null;
                        Optional<Country> optional = countryRepository
                                .findById(staffOverseasWorkHistoryDto.getCountry().getId());
                        if (optional.isPresent()) {
                            country = optional.get();
                        }
                        staffOverseasWorkHistory.setCountry(country);
                    }
                    staffOverseasWorkHistory.setStartDate(staffOverseasWorkHistoryDto.getStartDate());
                    staffOverseasWorkHistory.setEndDate(staffOverseasWorkHistoryDto.getEndDate());
                    staffOverseasWorkHistory.setDecisionDate(staffOverseasWorkHistoryDto.getDecisionDate());
                    staffOverseasWorkHistory.setPurpose(staffOverseasWorkHistoryDto.getPurpose());
                    staffOverseasWorkHistory.setCompanyName(staffOverseasWorkHistoryDto.getCompanyName());
                    staffOverseasWorkHistory.setDecisionNumber(staffOverseasWorkHistoryDto.getDecisionNumber());
                    staffOverseasWorkHistory.setStaff(staff);
                    ListStaffOverseasWorkHistory.add(staffOverseasWorkHistory);
                }
            }
        }
        if (ListStaffOverseasWorkHistory.size() > 0) {
            if (staff.getOverseasWorkHistory() == null) {
                staff.setOverseasWorkHistory(ListStaffOverseasWorkHistory);
            } else {
                staff.getOverseasWorkHistory().clear();
                staff.getOverseasWorkHistory().addAll(ListStaffOverseasWorkHistory);
            }
        } else {
            if (staff.getOverseasWorkHistory() != null) {
                staff.getOverseasWorkHistory().clear();
            }
        }
        // set quá trình khen thưởng
        Set<StaffRewardHistory> ListStaffRewardHistory = new HashSet<>();
        if (staffDto.getRewardHistory() != null && staffDto.getRewardHistory().size() > 0) {
            for (StaffRewardHistoryDto staffRewardHistoryDto : staffDto.getRewardHistory()) {
                if (staffRewardHistoryDto != null) {
                    StaffRewardHistory staffRewardHistory = null;
                    if (staffRewardHistoryDto.getId() != null) {
                        Optional<StaffRewardHistory> optional = staffRewardHistoryRepository
                                .findById(staffRewardHistoryDto.getId());
                        if (optional.isPresent()) {
                            staffRewardHistory = optional.get();
                        }
                    }
                    if (staffRewardHistory == null) {
                        staffRewardHistory = new StaffRewardHistory();
                    }
                    if (staffRewardHistoryDto.getRewardType() != null
                            && staffRewardHistoryDto.getRewardType().getId() != null) {
                        RewardForm rewardType = null;
                        Optional<RewardForm> optional = rewardFormRepository
                                .findById(staffRewardHistoryDto.getRewardType().getId());
                        if (optional.isPresent()) {
                            rewardType = optional.get();
                        }
                        staffRewardHistory.setRewardType(rewardType);
                    }
                    if (staffRewardHistoryDto.getOrganization() != null
                            && staffRewardHistoryDto.getOrganization().getId() != null) {
                        HrOrganization organization = null;
                        Optional<HrOrganization> optional = organizationRepository
                                .findById(staffRewardHistoryDto.getOrganization().getId());
                        if (optional.isPresent()) {
                            organization = optional.get();
                        }
                        staffRewardHistory.setOrganization(organization);
                    }
                    staffRewardHistory.setOrganizationName(staffRewardHistoryDto.getOrganizationName());
                    staffRewardHistory.setRewardDate(staffRewardHistoryDto.getRewardDate());
                    staffRewardHistory.setStaff(staff);
                    ListStaffRewardHistory.add(staffRewardHistory);
                }
            }
        }
        if (ListStaffRewardHistory != null && !ListStaffRewardHistory.isEmpty()) {
            if (staff.getStaffRewardHistories() == null) {
                staff.setStaffRewardHistories(ListStaffRewardHistory);
            } else {
                staff.getStaffRewardHistories().clear();
                staff.getStaffRewardHistories().addAll(ListStaffRewardHistory);
            }
        } else {
            if (staff.getStaffRewardHistories() != null) {
                staff.getStaffRewardHistories().clear();
            }
        }
        // set qua trinh thai san
//        Set<StaffMaternityHistory> ListStaffMaternityHistory = new HashSet<>();
//        if (staffDto.getMaternityHistory() != null && staffDto.getMaternityHistory().size() > 0) {
//            for (StaffMaternityHistoryDto staffMaternityHistoryDto : staffDto.getMaternityHistory()) {
//                if (staffMaternityHistoryDto != null) {
//                    StaffMaternityHistory staffMaternityHistory = null;
//                    if (staffMaternityHistoryDto.getId() != null) {
//                        Optional<StaffMaternityHistory> optional = staffMaternityHistoryRepository
//                                .findById(staffMaternityHistoryDto.getId());
//                        if (optional.isPresent()) {
//                            staffMaternityHistory = optional.get();
//                        }
//                    }
//                    if (staffMaternityHistory == null) {
//                        staffMaternityHistory = new StaffMaternityHistory();
//                    }
//                    staffMaternityHistory.setStartDate(staffMaternityHistoryDto.getStartDate());
//                    staffMaternityHistory.setEndDate(staffMaternityHistoryDto.getEndDate());
//                    staffMaternityHistory.setBirthNumber(staffMaternityHistoryDto.getBirthNumber());
//                    staffMaternityHistory.setNote(staffMaternityHistoryDto.getNote());
//                    staffMaternityHistory.setStaff(staff);
//                    ListStaffMaternityHistory.add(staffMaternityHistory);
//                }
//            }
//        }
//        if (ListStaffMaternityHistory.size() > 0) {
//            if (staff.getStaffMaternityHistories() == null) {
//                staff.setStaffMaternityHistories(ListStaffMaternityHistory);
//            } else {
//                staff.getStaffMaternityHistories().clear();
//                staff.getStaffMaternityHistories().addAll(ListStaffMaternityHistory);
//            }
//        } else {
//            if (staff.getStaffMaternityHistories() != null) {
//                staff.getStaffMaternityHistories().clear();
//            }
//        }

        /// chữ ký
//        HashSet<StaffSignature> staffSignatures = new HashSet<>();
//        if (staffDto.getStaffSignatures() != null && staffDto.getStaffSignatures().size() > 0) {
//            for (StaffSignatureDto staffSignatureDto : staffDto.getStaffSignatures()) {
//                StaffSignature staffSignature = null;
//                if (staffSignatureDto == null) {
//                    return null;
//                }
//
//                if (staffSignatureDto.getId() != null) {
//                    staffSignature = staffSignatureRepository.findById(staffSignatureDto.getId()).orElse(null);
//                }
//
//                if (staffSignature == null) {
//                    staffSignature = new StaffSignature();
//                }
//
//                staffSignature.setStaff(staff);
//                staffSignature.setName(staffSignatureDto.getName());
//                staffSignature.setCode(staffSignatureDto.getCode());
//                staffSignature.setDescription(staffSignatureDto.getDescription());
//                staffSignature.setSignature(staffSignatureDto.getSignature());
//
//                staffSignatures.add(staffSignature);
//            }
//        }
//        if (staffSignatures.size() > 0) {
//            if (staff.getStaffSignatures() == null) {
//                staff.setStaffSignatures(staffSignatures);
//            } else {
//                staff.getStaffSignatures().clear();
//                staff.getStaffSignatures().addAll(staffSignatures);
//            }
//        } else {
//            if (staff.getStaffSignatures() != null) {
//                staff.getStaffSignatures().clear();
//            }
//        }

        // quá trình lương
        Set<StaffSalaryHistory> ListStaffSalaryHistory = new HashSet<>();
        if (staffDto.getSalaryHistory() != null && staffDto.getSalaryHistory().size() > 0) {
            for (StaffSalaryHistoryDto staffSalaryHistoryDto : staffDto.getSalaryHistory()) {
                if (staffSalaryHistoryDto != null) {
                    StaffSalaryHistory staffSalaryHistory = null;
                    if (staffSalaryHistoryDto.getId() != null) {
                        Optional<StaffSalaryHistory> optional = staffSalaryHistoryRepository
                                .findById(staffSalaryHistoryDto.getId());
                        if (optional.isPresent()) {
                            staffSalaryHistory = optional.get();
                        }
                    }
                    if (staffSalaryHistory == null) {
                        staffSalaryHistory = new StaffSalaryHistory();
                    }
                    staffSalaryHistory.setDecisionCode(staffSalaryHistoryDto.getDecisionCode());
                    staffSalaryHistory.setDecisionDate(staffSalaryHistoryDto.getDecisionDate());
                    staffSalaryHistory.setCoefficient(staffSalaryHistoryDto.getCoefficient());
                    staffSalaryHistory.setCoefficientOverLevel(staffSalaryHistoryDto.getCoefficientOverLevel());
                    staffSalaryHistory.setPercentage(staffSalaryHistoryDto.getPercentage());
                    staffSalaryHistory.setStaffTypeCode(staffSalaryHistoryDto.getStaffTypeCode());
                    if (staffSalaryHistoryDto.getSalaryIncrementType() != null) {
                        SalaryIncrementType salaryType = null;
                        Optional<SalaryIncrementType> optional = salaryIncrementTypeRepository
                                .findById(staffSalaryHistoryDto.getSalaryIncrementType().getId());
                        if (optional.isPresent()) {
                            salaryType = optional.get();
                        }
                        staffSalaryHistory.setSalaryIncrementType(salaryType);

                    }

                    staffSalaryHistory.setStaff(staff);
                    ListStaffSalaryHistory.add(staffSalaryHistory);
                }
            }
        }
        if (ListStaffSalaryHistory.size() > 0) {
            if (staff.getSalaryHistory() == null) {
                staff.setSalaryHistory(ListStaffSalaryHistory);
            } else {
                staff.getSalaryHistory().clear();
                staff.getSalaryHistory().addAll(ListStaffSalaryHistory);
            }
        } else {
            if (staff.getSalaryHistory() != null) {
                staff.getSalaryHistory().clear();
            }
        }

//        // quá trình đóng bảo hiểm xã hội
//        Set<StaffInsuranceHistory> ListStaffInsuranceHistory = new HashSet<>();
//        if (staffDto.getStafInsuranceHistory() != null && staffDto.getStafInsuranceHistory().size() > 0) {
//            for (StaffInsuranceHistoryDto staffInsuranceHistoryDto : staffDto.getStafInsuranceHistory()) {
//                if (staffInsuranceHistoryDto != null) {
//                    StaffInsuranceHistory staffInsuranceHistory = null;
//                    if (staffInsuranceHistoryDto.getId() != null) {
//                        Optional<StaffInsuranceHistory> optional = staffInsuranceHistoryRepository
//                                .findById(staffInsuranceHistoryDto.getId());
//                        if (optional.isPresent()) {
//                            staffInsuranceHistory = optional.get();
//                        }
//                    }
//                    if (staffInsuranceHistory == null) {
//                        staffInsuranceHistory = new StaffInsuranceHistory();
//                    }
//                    staffInsuranceHistory.setStartDate(staffInsuranceHistoryDto.getStartDate());
//                    staffInsuranceHistory.setEndDate(staffInsuranceHistoryDto.getEndDate());
//                    staffInsuranceHistory.setNote(staffInsuranceHistoryDto.getNote());
//                    staffInsuranceHistory.setSalaryCofficient(staffInsuranceHistoryDto.getSalaryCofficient());
//                    staffInsuranceHistory.setInsuranceSalary(staffInsuranceHistoryDto.getInsuranceSalary());
//                    staffInsuranceHistory.setStaffPercentage(staffInsuranceHistoryDto.getStaffPercentage());
//                    staffInsuranceHistory.setOrgPercentage(staffInsuranceHistoryDto.getOrgPercentage());
//                    staffInsuranceHistory.setStaffInsuranceAmount(staffInsuranceHistoryDto.getStaffInsuranceAmount());
//                    staffInsuranceHistory.setOrgInsuranceAmount(staffInsuranceHistoryDto.getOrgInsuranceAmount());
//                    staffInsuranceHistory.setStaff(staff);
//                    ListStaffInsuranceHistory.add(staffInsuranceHistory);
//                }
//            }
//        }
//        if (ListStaffInsuranceHistory.size() > 0) {
//            if (staff.getStafInsuranceHistory() == null) {
//                staff.setStafInsuranceHistory(ListStaffInsuranceHistory);
//            } else {
//                staff.getStafInsuranceHistory().clear();
//                staff.getStafInsuranceHistory().addAll(ListStaffInsuranceHistory);
//            }
//        } else {
//            if (staff.getStafInsuranceHistory() != null) {
//                staff.getStafInsuranceHistory().clear();
//            }
//        }
        // quá trình đóng bảo hiểm xã hội
//        Set<StaffSocialInsurance> ListStaffSocialInsurance = new HashSet<>();
//        if (staffDto.getStaffSocialInsurance() != null && staffDto.getStaffSocialInsurance().size() > 0) {
//            for (StaffSocialInsuranceDto staffSocialInsuranceDto : staffDto.getStaffSocialInsurance()) {
//                if (staffSocialInsuranceDto != null) {
//                    StaffSocialInsurance staffSocialInsurance = null;
//                    if (staffSocialInsuranceDto.getId() != null) {
//                        Optional<StaffSocialInsurance> optional = staffSocialInsuranceRepository
//                                .findById(staffSocialInsuranceDto.getId());
//                        if (optional.isPresent()) {
//                            staffSocialInsurance = optional.get();
//                        }
//                    }
//                    if (staffSocialInsurance == null) {
//                        staffSocialInsurance = new StaffSocialInsurance();
//                    }
//                    staffSocialInsurance.setStaff(staff);
//                    staffSocialInsurance.setInsuranceSalary(staffSocialInsuranceDto.getInsuranceSalary());
//                    staffSocialInsurance.setStaffPercentage(staffSocialInsuranceDto.getStaffPercentage());
//                    staffSocialInsurance.setStaffInsuranceAmount(staffSocialInsuranceDto.getStaffInsuranceAmount());
//                    staffSocialInsurance.setOrgPercentage(staffSocialInsuranceDto.getOrgInsuranceAmount());
//                    staffSocialInsurance.setTotalInsuranceAmount(staffSocialInsuranceDto.getTotalInsuranceAmount());
//                    staffSocialInsurance.setOrgInsuranceAmount(staffSocialInsuranceDto.getOrgInsuranceAmount());
//                    staffSocialInsurance.setPaidStatus(staffSocialInsuranceDto.getPaidStatus());
//                    staffSocialInsurance.setNote(staffSocialInsuranceDto.getNote());
//                    staffSocialInsurance.setSalaryCoefficient(staffSocialInsuranceDto.getSalaryCoefficient());
//                    staffSocialInsurance.setStartDate(staffSocialInsuranceDto.getStartDate());
//                    staffSocialInsurance.setEndDate(staffSocialInsuranceDto.getEndDate());
//                    if (staffSocialInsuranceDto.getSalaryResult() != null) {
//                        SalaryResult salaryResult = null;
//                        if (staffSocialInsuranceDto.getSalaryResult().getId() != null) {
//                            Optional<SalaryResult> optional = salaryResultRepository
//                                    .findById(staffSocialInsuranceDto.getSalaryResult().getId());
//                            if (optional.isPresent()) {
//                                salaryResult = optional.get();
//                            }
//                        }
//                        staffSocialInsurance.setSalaryResult(salaryResult);
//                    }
//                    if (staffSocialInsuranceDto.getSalaryPeriod() != null) {
//                        SalaryPeriod salaryPeriod = null;
//                        if (staffSocialInsuranceDto.getSalaryPeriod().getId() != null) {
//                            Optional<SalaryPeriod> optional = salaryPeriodRepository
//                                    .findById(staffSocialInsuranceDto.getSalaryPeriod().getId());
//                            if (optional.isPresent()) {
//                                salaryPeriod = optional.get();
//                            }
//                        }
//                        staffSocialInsurance.setSalaryPeriod(salaryPeriod);
//                    }
//                    ListStaffSocialInsurance.add(staffSocialInsurance);
//                }
//            }
//        }
//        if (ListStaffSocialInsurance.size() > 0) {
//            if (staff.getStaffSocialInsurances() == null) {
//                staff.setStaffSocialInsurances(ListStaffSocialInsurance);
//            } else {
//                staff.getStaffSocialInsurances().clear();
//                staff.getStaffSocialInsurances().addAll(ListStaffSocialInsurance);
//            }
//        } else {
//            if (staff.getStaffSocialInsurances() != null) {
//                staff.getStaffSocialInsurances().clear();
//            }
//        }
        // trình độ học vấn
        Set<PersonCertificate> personCertificates = new HashSet<>();
        if (staffDto.getPersonCertificate() != null && staffDto.getPersonCertificate().size() > 0) {
            for (PersonCertificateDto personCertificateDto : staffDto.getPersonCertificate()) {
                if (personCertificateDto != null) {
                    PersonCertificate personCertificate = null;
                    if (personCertificateDto.getId() != null) {
                        Optional<PersonCertificate> optional = personCertificateRepository
                                .findById(personCertificateDto.getId());
                        if (optional.isPresent()) {
                            personCertificate = optional.get();
                        }
                    }
                    if (personCertificate == null) {
                        personCertificate = new PersonCertificate();
                    }
                    if (personCertificateDto.getCertificate() != null
                            && personCertificateDto.getCertificate().getId() != null) {
                        Certificate certificate = null;
                        Optional<Certificate> optional = certificateRepository
                                .findById(personCertificateDto.getCertificate().getId());
                        if (optional.isPresent()) {
                            certificate = optional.get();
                        }
                        personCertificate.setCertificate(certificate);
                    }
                    personCertificate.setLevel(personCertificateDto.getLevel());
                    personCertificate.setIssueDate(personCertificateDto.getIssueDate());
                    personCertificate.setName(personCertificateDto.getName());
                    personCertificate.setPerson(staff);
                    personCertificates.add(personCertificate);
                }
            }
        }
        if (personCertificates.size() > 0) {
            if (staff.getPersonCertificate() == null) {
                staff.setPersonCertificate(personCertificates);
            } else {
                staff.getPersonCertificate().clear();
                staff.getPersonCertificate().addAll(personCertificates);
            }
        } else {
            if (staff.getPersonCertificate() != null) {
                staff.getPersonCertificate().clear();
            }
        }
        // set qua trinh phu cap
//        Set<StaffAllowanceHistory> staffAllowanceHistories = new HashSet<>();
//        if (staffDto.getAllowanceHistory() != null && staffDto.getAllowanceHistory().size() > 0) {
//            for (StaffAllowanceHistoryDto staffAllowanceHistoryDto : staffDto.getAllowanceHistory()) {
//                if (staffAllowanceHistoryDto != null) {
//                    StaffAllowanceHistory staffAllowanceHistory = null;
//                    if (staffAllowanceHistoryDto.getId() != null) {
//                        Optional<StaffAllowanceHistory> optional = staffAllowanceHistoryRepository
//                                .findById(staffAllowanceHistoryDto.getId());
//                        if (optional.isPresent()) {
//                            staffAllowanceHistory = optional.get();
//                        }
//                    }
//                    if (staffAllowanceHistory == null) {
//                        staffAllowanceHistory = new StaffAllowanceHistory();
//                    }
//                    if (staffAllowanceHistoryDto.getAllowanceType() != null
//                            && staffAllowanceHistoryDto.getAllowanceType().getId() != null) {
//                        AllowanceType allowanceType = null;
//                        Optional<AllowanceType> optional = allowanceTypeRepository
//                                .findById(staffAllowanceHistoryDto.getAllowanceType().getId());
//                        if (optional.isPresent()) {
//                            allowanceType = optional.get();
//                        }
//                        staffAllowanceHistory.setAllowanceType(allowanceType);
//                    }
//                    staffAllowanceHistory.setStartDate(staffAllowanceHistoryDto.getStartDate());
//                    staffAllowanceHistory.setEndDate(staffAllowanceHistoryDto.getEndDate());
//                    staffAllowanceHistory.setNote(staffAllowanceHistoryDto.getNote());
//                    staffAllowanceHistory.setCoefficient(staffAllowanceHistoryDto.getCoefficient());
//                    staffAllowanceHistory.setStaff(staff);
//                    staffAllowanceHistories.add(staffAllowanceHistory);
//                }
//            }
//        }
//        if (staffAllowanceHistories.size() > 0) {
//            if (staff.getStaffAllowanceHistories() == null) {
//                staff.setStaffAllowanceHistories(staffAllowanceHistories);
//            } else {
//                staff.getStaffAllowanceHistories().clear();
//                staff.getStaffAllowanceHistories().addAll(staffAllowanceHistories);
//            }
//        } else {
//            if (staff.getStaffAllowanceHistories() != null) {
//                staff.getStaffAllowanceHistories().clear();
//            }
//        }
        // set qua trinh boi duong
        Set<StaffTrainingHistory> staffTrainingHistories = new HashSet<>();
        if (staffDto.getTrainingHistory() != null && staffDto.getTrainingHistory().size() > 0) {
            for (StaffTrainingHistoryDto staffTrainingHistoryDto : staffDto.getTrainingHistory()) {
                if (staffTrainingHistoryDto != null) {
                    StaffTrainingHistory staffTrainingHistory = null;
                    if (staffTrainingHistoryDto.getId() != null) {
                        Optional<StaffTrainingHistory> optional = staffTrainingHistoryRepository
                                .findById(staffTrainingHistoryDto.getId());
                        if (optional.isPresent()) {
                            staffTrainingHistory = optional.get();
                        }
                    }
                    if (staffTrainingHistory == null) {
                        staffTrainingHistory = new StaffTrainingHistory();
                    }
                    if (staffTrainingHistoryDto.getTrainingCountry() != null
                            && staffTrainingHistoryDto.getTrainingCountry().getId() != null) {
                        Country country = null;
                        Optional<Country> optional = countryRepository
                                .findById(staffTrainingHistoryDto.getTrainingCountry().getId());
                        if (optional.isPresent()) {
                            country = optional.get();
                        }
                        staffTrainingHistory.setTrainingCountry(country);
                    }
                    if (staffTrainingHistoryDto.getCertificate() != null
                            && staffTrainingHistoryDto.getCertificate().getId() != null) {
                        Certificate certificate = null;
                        Optional<Certificate> optional = certificateRepository
                                .findById(staffTrainingHistoryDto.getCertificate().getId());
                        if (optional.isPresent()) {
                            certificate = optional.get();
                        }
                        staffTrainingHistory.setCertificate(certificate);
                    }
                    staffTrainingHistory.setStartDate(staffTrainingHistoryDto.getStartDate());
                    staffTrainingHistory.setEndDate(staffTrainingHistoryDto.getEndDate());
                    staffTrainingHistory.setTrainingPlace(staffTrainingHistoryDto.getTrainingPlace());
                    staffTrainingHistory.setTrainingContent(staffTrainingHistoryDto.getTrainingContent());
                    staffTrainingHistory.setStaff(staff);
                    staffTrainingHistories.add(staffTrainingHistory);
                }
            }
        }
        if (staffTrainingHistories.size() > 0) {
            if (staff.getStaffTrainingHistories() == null) {
                staff.setStaffTrainingHistories(staffTrainingHistories);
            } else {
                staff.getStaffTrainingHistories().clear();
                staff.getStaffTrainingHistories().addAll(staffTrainingHistories);
            }
        } else {
            if (staff.getStaffTrainingHistories() != null) {
                staff.getStaffTrainingHistories().clear();
            }
        }
        // Add Asset
//        List<AssetDto> assets = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(staffDto.getAssets())) {
//            for (AssetDto assetDto : staffDto.getAssets()) {
//                StaffDto staffAsset = new StaffDto();
//                staffAsset.setId(id);
//                assetDto.setStaff(staffAsset);
//                assetDto = assetService.saveAsset(assetDto);
//                assets.add(assetDto);
//            }
//        } else {
//            List<AssetDto> assetByStaff = assetService.getListByStaff(id);
//            for (AssetDto assetDto : assetByStaff) {
//                assetService.deleteAsset(assetDto.getId());
//            }
//        }
        staff = staffRepository.save(staff);
        if (staff != null && staffDto.getUsername() != null) {
            User user = userRepository.findByUsername(staffDto.getUsername());

            if (user == null) {
                user = new User();
                user.setUsername(staffDto.getUsername());
                user.setPassword(staffDto.getPassword());
                user.setCreateDate(currentDate);
                user.setCreatedBy(currentUserName);

                HashSet<Role> roles = new HashSet<>();
                Role role;
                role = roleRepository.findByName(HrConstants.HR_USER);
                if (role != null)
                    roles.add(role);

                user.getRoles().clear();
                user.getRoles().addAll(roles);
                user.setEmail(staffDto.getEmail());
                user.setPerson(staff);
                UserDto dto = new UserDto(user);
                // set id = null because id in contructor is generate
                dto.setId(null);
                userExtService.saveUser(dto);
            }
        }
        StaffDto result = new StaffDto(staff);
//        result.setAssets(assets);
        return result;
    }

    private boolean validateStringName(String strName) {
        for (int i = 0; i < strName.length(); i++) {
            if (!Character.isLetter(strName.charAt(i)) && !Character.isWhitespace(strName.charAt(i))) {
                return false;
            }
        }
        return true;
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
    public StaffDto createStaffSimple(StaffDto staffDto) {
        if (staffDto == null) {
            return null;
        }
        String currentUserName = "Unknown User";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LocalDateTime currentDate = LocalDateTime.now();
        User modifiedUser;
        if (authentication != null) {
            modifiedUser = (User) authentication.getPrincipal();
            currentUserName = modifiedUser.getUsername();
        }
        User user = new User();
        Staff staff = null;
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy");
        String dateInString = "01-01-1900";
        try {
            date = formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (staffDto.getId() != null) {
            Optional<Staff> optional = staffRepository.findById(staffDto.getId());
            if (optional.isPresent()) {
                staff = optional.get();
            }
        }

        if (staff == null && staffDto.getStaffCode() != null) {
            List<Staff> list = staffRepository.getByCode(staffDto.getStaffCode());
            if (list != null && list.size() > 0) {
                staff = list.get(0);
            }
        }
        if (staff == null) {
            staff = new Staff();
        }
        if (staffDto.getStaffCode() != null)
            staff.setStaffCode(staffDto.getStaffCode());
        if (staffDto.getFirstName() != null)
            staff.setFirstName(staffDto.getFirstName());
        if (staffDto.getLastName() != null)
            staff.setLastName(staffDto.getLastName());
        if (staffDto.getBirthDate() != null && !staffDto.getBirthDate().before(date)) {
            staff.setBirthDate(staffDto.getBirthDate());
        }
        if (staffDto.getBirthPlace() != null)
            staff.setBirthPlace(staffDto.getBirthPlace());
        if (staffDto.getGender() != null)
            staff.setGender(staffDto.getGender());
        if (staffDto.getPhoto() != null)
            staff.setPhoto(staffDto.getPhoto());
        if (staffDto.getDisplayName() != null)
            staff.setDisplayName(staffDto.getDisplayName());
        if (staffDto.getPhoneNumber() != null)
            staff.setPhoneNumber(staffDto.getPhoneNumber());
        if (staffDto.getMaritalStatus() != null) {
            staff.setMaritalStatus(staffDto.getMaritalStatus());
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
        staff.setEthnicLanguage(staffDto.getEthnicLanguage());
        staff.setPhysicalEducationTeacher(staffDto.getPhysicalEducationTeacher());
        staff.setSpecializedName(staffDto.getSpecializedName());
        staff.setFormsOfTraining(staffDto.getFormsOfTraining());
        staff.setTrainingCountry(staffDto.getTrainingCountry());
        staff.setTrainingPlaces(staffDto.getTrainingPlaces());
        staff.setHighSchoolEducation(staffDto.getHighSchoolEducation());
        staff.setQualification(staffDto.getQualification());
        staff.setCertificationScore(staffDto.getCertificationScore());
        staff.setYearOfCertification(staffDto.getYearOfCertification());
        staff.setNote(staffDto.getNote());
        staff.setYearOfRecognitionDegree(staffDto.getYearOfRecognitionDegree());
        staff.setYearOfRecognitionAcademicRank(staffDto.getYearOfRecognitionAcademicRank());
//        if (staffDto.getOtherLanguageLevel() != null) {
//            EducationDegree otherLanguageProficiency = null;
//            Optional<EducationDegree> optional = educationDegreeRepository
//                    .findById(staffDto.getOtherLanguageLevel().getId());
//            if (optional.isPresent()) {
//                otherLanguageProficiency = optional.get();
//            }
//            staff.setOtherLanguageLevel(otherLanguageProficiency);
//        }
//        if (staffDto.getStudying() != null) {
//            EducationDegree studying = null;
//            Optional<EducationDegree> optional = educationDegreeRepository.findById(staffDto.getStudying().getId());
//            if (optional.isPresent()) {
//                studying = optional.get();
//            }
//            staff.setStudying(studying);
//        }
//        if (staffDto.getCivilServantCategory() != null) {
//            CivilServantCategory civilServantCategory = null;
//            Optional<CivilServantCategory> optional = civilServantCategoryRepository
//                    .findById(staffDto.getCivilServantCategory().getId());
//            if (optional.isPresent()) {
//                civilServantCategory = optional.get();
//            }
//            staff.setCivilServantCategory(civilServantCategory);
//        }
//        if (staffDto.getGrade() != null) {
//            CivilServantGrade grade = null;
//            Optional<CivilServantGrade> optional = civilServantGradeRepository.findById(staffDto.getGrade().getId());
//            if (optional.isPresent()) {
//                grade = optional.get();
//            }
//            staff.setGrade(grade);
//        }
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

        if (staffDto.getEthnics() != null) {
            Ethnics ethnics = null;
            Optional<Ethnics> optional = ethnicsRepository.findById(staffDto.getEthnics().getId());
            if (optional.isPresent()) {
                ethnics = optional.get();
            }
            staff.setEthnics(ethnics);
        }
        if (staffDto.getComputerSkill() != null) {
            EducationDegree computerSkill = null;
            Optional<EducationDegree> optional = educationDegreeRepository
                    .findById(staffDto.getComputerSkill().getId());
            if (optional.isPresent()) {
                computerSkill = optional.get();
            }
            staff.setComputerSkill(computerSkill);
        }
        if (staffDto.getEnglishLevel() != null) {
            EducationDegree englishLevel = null;
            Optional<EducationDegree> optional = educationDegreeRepository.findById(staffDto.getEnglishLevel().getId());
            if (optional.isPresent()) {
                englishLevel = optional.get();
            }
            staff.setEnglishLevel(englishLevel);
        }
        if (staffDto.getEnglishCertificate() != null) {
            Certificate englishCertificate = null;
            Optional<Certificate> optional = certificateRepository.findById(staffDto.getEnglishCertificate().getId());
            if (optional.isPresent()) {
                englishCertificate = optional.get();
            }
            staff.setEnglishCertificate(englishCertificate);
        }
        if (staffDto.getAcademicRank() != null) {
            AcademicTitle academicRank = null;
            Optional<AcademicTitle> optional = academicTitleRepository.findById(staffDto.getAcademicRank().getId());
            if (optional.isPresent()) {
                academicRank = optional.get();
            }
            staff.setAcademicRank(academicRank);
        }
        if (staffDto.getDegree() != null) {
            EducationDegree degree = null;
            Optional<EducationDegree> optional = educationDegreeRepository.findById(staffDto.getDegree().getId());
            if (optional.isPresent()) {
                degree = optional.get();
            }
            staff.setDegree(degree);
        }

        if (staffDto.getNationality() != null) {
            Country nationality = null;
            Optional<Country> optional = countryRepository.findById(staffDto.getNationality().getId());
            if (optional.isPresent()) {
                nationality = optional.get();
            }
            staff.setNationality(nationality);
        }
        if (staffDto.getEmail() != null)
            staff.setEmail(staffDto.getEmail());

        if (staffDto.getDepartment() != null && staffDto.getDepartment().getId() != null) {
            HRDepartment department = null;
            Optional<HRDepartment> optional = hRDepartmentRepository.findById(staffDto.getDepartment().getId());
            if (optional.isPresent()) {
                department = optional.get();
            }
            staff.setDepartment(department);
        }
        if (staffDto.getNativeVillage() != null) {
            AdministrativeUnit nativeVillage = null;
            Optional<AdministrativeUnit> optional = administrativeUnitRepository
                    .findById(staffDto.getNativeVillage().getId());
            if (optional.isPresent()) {
                nativeVillage = optional.get();
            }
            staff.setNativeVillage(nativeVillage);
        }

        if (staffDto.getReligion() != null) {
            Religion religion = null;
            Optional<Religion> optional = religionRepository.findById(staffDto.getReligion().getId());
            if (optional.isPresent()) {
                religion = optional.get();
            }
            staff.setReligion(religion);
        }

        // if (staffDto.getProfessionalDegree() != null) {
        // ProfessionalDegree professionalDegree = null;
        // Optional<ProfessionalDegree> optional = professionalDegreeRepository
        // .findById(staffDto.getProfessionalDegree().getId());
        // if (optional.isPresent()) {
        // professionalDegree = optional.get();
        // }
        // staff.setProfessionalDegree(professionalDegree);
        // }

        if (staffDto.getInformaticDegree() != null) {
            InformaticDegree informaticDegree = null;
            Optional<InformaticDegree> optional = informaticDegreeRepository
                    .findById(staffDto.getInformaticDegree().getId());
            if (optional.isPresent()) {
                informaticDegree = optional.get();
            }
            staff.setInformaticDegree(informaticDegree);
        }

        // if (staffDto.getPoliticalTheoryLevel() != null) {
        // PoliticalTheoryLevel politicalTheoryLevel = null;
        // Optional<PoliticalTheoryLevel> optional = politicalTheoryLevelRepository
        // .findById(staffDto.getPoliticalTheoryLevel().getId());
        // if (optional.isPresent()) {
        // politicalTheoryLevel = optional.get();
        // }
        // staff.setPoliticalTheoryLevel(politicalTheoryLevel);
        // }
//        if (staffDto.getStateManagementLevel() != null) {
//            StateManagementLevel stateManagementLevel = null;
//            Optional<StateManagementLevel> optional = stateManagementLevelRepository
//                    .findById(staffDto.getStateManagementLevel().getId());
//            if (optional.isPresent()) {
//                stateManagementLevel = optional.get();
//            }
//            staff.setStateManagementLevel(stateManagementLevel);
//        } else {
//            staff.setStateManagementLevel(null);
//        }
        if (staffDto.getEducationalManagementLevel() != null) {
            EducationalManagementLevel educationalManagementLevel = null;
            Optional<EducationalManagementLevel> optional = educationalManagementLevelRepository
                    .findById(staffDto.getEducationalManagementLevel().getId());
            if (optional.isPresent()) {
                educationalManagementLevel = optional.get();
            }
            staff.setEducationalManagementLevel(educationalManagementLevel);
        } else {
            staff.setEducationalManagementLevel(null);
        }

        if (staffDto.getLabourAgreementType() != null) {
            LabourAgreementType labourAgreementType = null;
            Optional<LabourAgreementType> optional = labourAgreementTypeRepository
                    .findById(staffDto.getLabourAgreementType().getId());
            if (optional.isPresent()) {
                labourAgreementType = optional.get();
            }
            staff.setLabourAgreementType(labourAgreementType);
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
        if (staffDto.getContractDate() != null) {
            staff.setContractDate(staffDto.getContractDate());
        }
        if (staffDto.getJobTitle() != null) {
            staff.setJobTitle(staffDto.getJobTitle());
        }
        if (staffDto.getCivilServantType() != null) {
            CivilServantType type = null;
            Optional<CivilServantType> optional = civilServantTypeRepository
                    .findById(staffDto.getCivilServantType().getId());
            if (optional.isPresent()) {
                type = optional.get();
            }
            staff.setCivilServantType(type);
        }
        staff.setStartDate(staffDto.getStartDate());
        staff.setRecruitmentDate(staffDto.getRecruitmentDate());
        staff.setSalaryStartDate(staffDto.getSalaryStartDate());
        staff.setGraduationYear(staffDto.getGraduationYear());
        staff.setForeignLanguageName(staffDto.getForeignLanguageName());

        if (staffDto.getUser() != null) {
            user.setUsername(staffDto.getUser().getUsername());
            String password = SecurityUtils.getHashPassword(staffDto.getUser().getPassword());
            user.setPassword(password);
            // user.setPassword(password);
            user.setCreateDate(currentDate);
            user.setCreatedBy(currentUserName);
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
        staff = staffRepository.save(staff);
        return new StaffDto(staff);
    }

    @Override
    public List<StaffDto> saveImportStaff(List<StaffDto> list) {
        List<StaffDto> ret = new ArrayList<>();
        for (StaffDto staffDto : list) {
            if (staffDto.getNationalityCode() != null && StringUtils.hasText(staffDto.getNationalityCode())) {
                staffDto.setNationality(new CountryDto(countryService.findByCode(staffDto.getNationalityCode())));
            }
            if (staffDto.getEthnicsCode() != null && StringUtils.hasText(staffDto.getEthnicsCode())) {
                Ethnics entity = ethnicsService.findByCode(staffDto.getEthnicsCode());
                if (entity != null) {
                    staffDto.setEthnics(new EthnicsDto(entity));
                }
            }
            if (staffDto.getReligionCode() != null && StringUtils.hasText(staffDto.getReligionCode())) {
                Religion entity = religionService.findByCode(staffDto.getReligionCode());
                if (entity != null) {
                    staffDto.setReligion(new ReligionDto(entity));
                }
            }
            if (staffDto.getStatusCode() != null && StringUtils.hasText(staffDto.getStatusCode())) {
                List<EmployeeStatusDto> listEmployeeStatusDto = employeeStatusRepository
                        .getByName(staffDto.getStatusCode());
                if (listEmployeeStatusDto != null && listEmployeeStatusDto.size() > 0) {
                    staffDto.setStatus(listEmployeeStatusDto.get(0));
                } else {
                    staffDto.setStatus(null);
                }

            }
            if (staffDto.getDepartmentCode() != null && StringUtils.hasText(staffDto.getDepartmentCode())) {
                List<HRDepartment> listHRDepartment = hRDepartmentRepository.findByCode(staffDto.getDepartmentCode());
                if (listHRDepartment != null && listHRDepartment.size() > 0) {
                    staffDto.setDepartment(new HRDepartmentDto(listHRDepartment.get(0)));
                } else {
                    staffDto.setDepartment(null);
                }
            }
            if (staffDto.getCivilServantTypeCode() != null && StringUtils.hasText(staffDto.getCivilServantTypeCode())) {
                List<CivilServantType> listCivilServantType = civilServantTypeRepository
                        .findByCode(staffDto.getCivilServantTypeCode());
                if (listCivilServantType != null && listCivilServantType.size() > 0) {
                    staffDto.setCivilServantType(new CivilServantTypeDto(listCivilServantType.get(0)));
                } else {
                    staffDto.setCivilServantType(null);
                }
            }
            if (staffDto.getCivilServantCategoryCode() != null
                    && StringUtils.hasText(staffDto.getCivilServantCategoryCode())) {
                List<CivilServantCategory> listCivilServantCategory = civilServantCategoryRepository
                        .findByCode(staffDto.getCivilServantCategoryCode());
                if (listCivilServantCategory != null && listCivilServantCategory.size() > 0) {
                    staffDto.setCivilServantCategory(new CivilServantCategoryDto(listCivilServantCategory.get(0)));
                } else {
                    staffDto.setCivilServantCategory(null);
                }
            }
            if (staffDto.getLabourAgreementTypeCode() != null
                    && StringUtils.hasText(staffDto.getLabourAgreementTypeCode())) {
                List<LabourAgreementType> listLabourAgreementType = labourAgreementTypeRepository
                        .findByCode(staffDto.getLabourAgreementTypeCode());
                if (listLabourAgreementType != null && listLabourAgreementType.size() > 0) {
                    staffDto.setLabourAgreementType(new LabourAgreementTypeDto(listLabourAgreementType.get(0)));
                } else {
                    staffDto.setLabourAgreementType(null);
                }
            }
            if (staffDto.getProfessionCode() != null && StringUtils.hasText(staffDto.getProfessionCode())) {
                List<Profession> listProfession = professionRepository.findListByCode(staffDto.getProfessionCode());

                if (listProfession != null && listProfession.size() > 0) {
                    staffDto.setProfession(new ProfessionDto(listProfession.get(0)));
                } else {
                    staffDto.setProfession(null);
                }
            }
            if (staffDto.getAcademicTitleCode() != null && StringUtils.hasText(staffDto.getAcademicTitleCode())) {
                List<AcademicTitle> listAcademic = academicTitleRepository.findByCode(staffDto.getAcademicTitleCode());

                if (listAcademic != null && listAcademic.size() > 0) {
                    staffDto.setAcademicRank(new AcademicTitleDto(listAcademic.get(0)));
                } else {
                    staffDto.setAcademicRank(null);
                }
            }
            if (staffDto.getEducationDegreeCode() != null && StringUtils.hasText(staffDto.getEducationDegreeCode())) {
                List<EducationDegree> listEducation = educationDegreeRepository
                        .findByCode(staffDto.getEducationDegreeCode());

                if (listEducation != null && listEducation.size() > 0) {
                    staffDto.setDegree(new EducationDegreeDto(listEducation.get(0)));
                } else {
                    staffDto.setDegree(null);
                }
            }

            staffDto = this.createStaffSimple(staffDto);
            ret.add(staffDto);
        }
        return ret;
    }

    public Page<PositionStaffDto> findTeacherByDepartment(UUID departmentId, int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        return positionStaffRepository.findTeacherByDepartment(departmentId, pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StaffDto deleteStaff(UUID id) {
        if (id == null) {
            throw new RuntimeException("Invalid argument!");
        }
        Staff entity = this.findById(id);
        if (entity == null) {
            throw new RuntimeException("Invalid argument!");
        }

        entity.setVoided(true);

        return new StaffDto(entity);
    }

    @Override
    @Transactional
    public Boolean deleteMultiple(Staff[] staffs) {
        boolean ret = true;
        if (staffs == null || staffs.length <= 0) {
            return ret;
        }
        ArrayList<Staff> lstStaffs = new ArrayList<>();
        for (Staff st : staffs) {
            Staff entity = this.findById(st.getId());
            Person person = null;
            Optional<Person> optional = personRepository.findById(st.getId());
            if (optional.isPresent()) {
                person = optional.get();
            }
            if (entity == null || person == null) {
                throw new RuntimeException();
            }
            lstStaffs.add(entity);
        }
        staffRepository.deleteAllInBatch(lstStaffs);
        return ret;
    }

    @Override
    public List<StaffDto> getAll() {
        Iterator<Staff> itr = staffRepository.findAll().iterator();
        List<StaffDto> list = new ArrayList<>();
        while (itr.hasNext()) {
            list.add(new StaffDto(itr.next()));
        }
        return list;
    }

    @Override
    public Page<StaffDto> searchStaff(StaffSearchDto dto, int pageSize, int pageIndex) {
        LocalDateTime date = LocalDateTime.now();
        String sqlCount = "select count(distinct ps.staff.id) from PositionStaff ps where (1=1)";
        String sql = "select distinct ps.staff as s from PositionStaff ps where (1=1)";

        Department dep = null;
        if (dto.getDepartment() != null) {
            dep = departmentRepository.getReferenceById(dto.getDepartment().getId());
            sql += " and ps.department.linePath like :linePath";
            sqlCount += " and ps.department.linePath like :linePath";
        }
        if (dto.getIsMainPosition() != null) {
            sql += " and ps.mainPosition=:mainPosition";
            sqlCount += " and ps.mainPosition=:mainPosition";
        }

        if (dto.getKeyword() != null) {
            sql += " and ps.staff.displayName like :keyword";
            sqlCount += " and ps.staff.displayName like :keyword";
        }
        if (dto.getApprovalStatus() != null) {
            sql += " and ps.staff.approvalStatus = :approvalStatus";
            sqlCount += " and ps.staff.approvalStatus = :approvalStatus";
        }
        Query query = manager.createQuery(sql);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getDepartment() != null) {
            if (dep != null) {
                String linePath = dep.getLinePath() + "%";
                query.setParameter("linePath", linePath);
                qCount.setParameter("linePath", linePath);
            }
        }
        if (dto.getKeyword() != null) {
            String keyword = "%" + dto.getKeyword() + "%";
            query.setParameter("keyword", keyword);
            qCount.setParameter("keyword", keyword);
        }

        if (dto.getIsMainPosition() != null) {
            query.setParameter("mainPosition", dto.getIsMainPosition());
            qCount.setParameter("mainPosition", dto.getIsMainPosition());
        }

        int startPosition = (pageIndex - 1) * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        List<Staff> entities = query.getResultList();
        LocalDateTime endDate = LocalDateTime.now();
        long diffInMillis = endDate.getNano() - date.getNano();
        logger.info("{}", diffInMillis);
        List<StaffDto> content = new ArrayList<>();
        for (Staff entity : entities) {
            content.add(new StaffDto(entity));
        }
        long count = (long) qCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        return new PageImpl<>(content, pageable, count);
    }

    @Transactional
    @Override
    public StaffDto createStaffFromDto(StaffDto staffDto) {
        LocalDateTime currentDate = LocalDateTime.now();
        String currentUserName = "Unknown User";

        Staff staff = null;
        if (staffDto.getId() != null) {
            staff = this.findById(staffDto.getId());
        }
        if (staff == null && staffDto.getStaffCode() != null) {
            List<Staff> listStaff = staffRepository.findByCode(staffDto.getStaffCode());
            if (listStaff != null && listStaff.size() > 0) {
                staff = listStaff.get(0);
            }
        }
        if (staff == null) {
            staff = new Staff();
            staff.setCreateDate(currentDate);
        }
        String displayName = null;
        if (staffDto.getLastName() != null) {
            staff.setLastName(staffDto.getLastName());
            displayName = staffDto.getLastName();
        }
        if (staffDto.getFirstName() != null) {
            staff.setFirstName(staffDto.getFirstName());
            displayName = displayName + " " + staffDto.getFirstName();
        }
        if (displayName != null) {
            staff.setDisplayName(displayName);
        }
        if (staffDto.getBirthDate() != null) {
            staff.setBirthDate(staffDto.getBirthDate());
        }
        if (staffDto.getGender() != null) {
            staff.setGender(staffDto.getGender());
        }
        if (staffDto.getBirthPlace() != null)
            staff.setBirthPlace(staffDto.getBirthPlace());
        if (staffDto.getEmail() != null) {
            staff.setEmail(staffDto.getEmail());
        }
        if (staffDto.getStaffCode() != null) {
            staff.setStaffCode(staffDto.getStaffCode());
        }
        if (staffDto.getPhoneNumber() != null) {
            staff.setPhoneNumber(staffDto.getPhoneNumber());
        }
        if (staffDto.getIdNumber() != null) {
            staff.setIdNumber(staffDto.getIdNumber());
        }
        if (staffDto.getIdNumberIssueBy() != null) {
            staff.setIdNumberIssueBy(staffDto.getIdNumberIssueBy());
        }
        if (staffDto.getIdNumberIssueDate() != null) {
            staff.setIdNumberIssueDate(staffDto.getIdNumberIssueDate());
        }

        if (staffDto.getNationality() != null) {
            Country country = null;
            if (staffDto.getNationality().getId() != null) {
                Optional<Country> optional = countryRepository.findById(staffDto.getNationality().getId());
                if (optional.isPresent()) {
                    country = optional.get();
                }
            }
            if (country == null) {
                if (staffDto.getNationality().getCode() != null) {
                    country = countryRepository.findByCode(staffDto.getNationality().getCode());
                }
            }
            staff.setNationality(country);
        }
        if (staffDto.getReligion() != null) {
            Religion religion = null;
            if (staffDto.getReligion().getId() != null) {
                Optional<Religion> optional = religionRepository.findById(staffDto.getReligion().getId());
                if (optional.isPresent()) {
                    religion = optional.get();
                }
            }
            if (religion == null) {
                if (staffDto.getReligion().getCode() != null) {
                    religion = religionRepository.findByCode(staffDto.getReligion().getCode());
                }
            }
            staff.setReligion(religion);
        }
        if (staffDto.getEthnics() != null) {
            Ethnics ethnics = null;
            if (staffDto.getEthnics().getId() != null) {
                Optional<Ethnics> optional = ethnicsRepository.findById(staffDto.getEthnics().getId());
                if (optional.isPresent()) {
                    ethnics = optional.get();
                }
            }
            if (ethnics == null) {
                if (staffDto.getEthnics().getCode() != null) {
                    ethnics = ethnicsRepository.findByCode(staffDto.getEthnics().getCode());
                }
            }
            staff.setEthnics(ethnics);
        }
        if (staffDto.getUser() != null) {
            User user = staff.getUser();
            if (user == null) {
                user = new User();
                user.setUsername(staffDto.getUser().getUsername());
            }
            String password = SecurityUtils.getHashPassword(staffDto.getUser().getPassword());
            if (password != null && password.length() > 0) {
                user.setPassword(password);
            }
            if (staffDto.getUser().getEmail() != null)
                user.setEmail(staffDto.getUser().getEmail());
            user.setPerson(staff);
            staff.setUser(user);
        }

        logger.info("Display name - staff code: {}", staff.getDisplayName() + ", " + staff.getStaffCode());
        staff = staffRepository.save(staff);
        return new StaffDto(staff);
    }

    @Override
    public int saveListStaff(List<StaffDto> staffDtoList) {
        for (StaffDto dto : staffDtoList) {
            createStaffFromDto(dto);
        }
        return staffDtoList.size();
    }

    @Override
    public Page<StaffDto> findPageByCode(String textSearch, int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        return staffRepository.findPageByCodeOrName(textSearch, pageable);
    }

    @Override
    public Boolean validateStaffCode(StaffDto dto) {
        if (dto == null)
            return false;

        // ID of Staff is null => Create new Staff
        // => Assure that there's no other Staffs using this code of new Staff
        // if there was any Staff using new Staff code, then this new code is
        // invalid => return False
        // else return true
        if (dto.getId() == null) {
            List<Staff> entities = staffRepository.findByCode(dto.getStaffCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        }
        // ID of Staff is NOT null => Staff is modified
        // => Assure that the modified code is not same to OTHER any Staff's code
        // if there was any Staff using new Staff code, then this new code is
        // invalid => return False
        // else return true
        else {
            List<Staff> entities = staffRepository.findByCode(dto.getStaffCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (Staff entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public Boolean validateCccd(StaffDto dto) {
        if (dto == null)
            return false;

        String personalId = dto.getPersonalIdentificationNumber();
        if (personalId == null || personalId.strip().isEmpty()) {
            return true; // Cho phép null hoặc rỗng
        }

        List<Staff> entities = staffRepository.findByIdPersonalIdentificationNumber(personalId.strip());
        if (dto.getId() == null) {
            // Tạo mới nhân sự
            return entities == null || entities.isEmpty();
        } else {
            // Chỉnh sửa nhân sự
            if (entities == null || entities.isEmpty())
                return true;
            for (Staff entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
            return true;
        }
    }

    @Override
    public Boolean validateCmnd(StaffDto dto) {
        if (dto == null)
            return false;

        String idNumber = dto.getIdNumber();
        if (idNumber == null || (idNumber != null && idNumber.strip().isEmpty())) {
            return true; // Cho phép null hoặc rỗng
        }

        List<Staff> entities = staffRepository.findByIdNumber(idNumber.strip());
        if (dto.getId() == null) {
            // Tạo mới nhân sự
            return entities == null || entities.isEmpty();
        } else {
            // Chỉnh sửa nhân sự
            if (entities == null || entities.isEmpty())
                return true;
            for (Staff entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
            return true;
        }
    }

    @Override
    public Boolean validateTaxCode(StaffDto dto) {
        if (dto == null)
            return false;
        if (dto.getId() == null) {
            List<Staff> entities = staffRepository.findByTaxCode(dto.getTaxCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<Staff> entities = staffRepository.findByTaxCode(dto.getTaxCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (Staff entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public Boolean validateSocialInsuranceNumber(StaffDto dto) {
        if (dto == null)
            return false;
        if (dto.getId() == null) {
            List<Staff> entities = staffRepository.findBySocialInsuranceNumber(dto.getSocialInsuranceNumber().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<Staff> entities = staffRepository.findBySocialInsuranceNumber(dto.getSocialInsuranceNumber().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (Staff entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public Boolean validateHealthInsuranceNumber(StaffDto dto) {
        if (dto == null)
            return false;
        if (dto.getId() == null) {
            List<Staff> entities = staffRepository.findByHealthInsuranceNumber(dto.getHealthInsuranceNumber().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<Staff> entities = staffRepository.findByHealthInsuranceNumber(dto.getHealthInsuranceNumber().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (Staff entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public Boolean validateUserName(String userName, UUID userId) {
        if (userName != null) {
            List<Staff> staffs = staffRepository.findAll();
            for (Staff staff : staffs) {
                if (staff != null) {
                    if (staff.getUser() != null) {
                        if (staff.getUser().getUsername() != null
                                && staff.getUser().getUsername().equalsIgnoreCase(userName)) {
                            if (userId != null && staff.getUser() != null && staff.getUser().getId() != null) {
                                staff.getUser().getId();
                            }
                            return Boolean.FALSE;
                        }
                    }
                }
            }
        }
        return Boolean.TRUE;
    }


    @Override
    public SearchStaffDto getInitialFilter() {
        SearchStaffDto response = new SearchStaffDto();

        response.setPageIndex(1);
        response.setPageSize(10);

        List<EmployeeStatus> onWorkingStatusResults = employeeStatusRepository.findByCode(HrConstants.EmployeeStatusCodeEnum.WORKING.getValue());
        if (onWorkingStatusResults != null && !onWorkingStatusResults.isEmpty()) {
            EmployeeStatusDto initialStatus = new EmployeeStatusDto(onWorkingStatusResults.get(0));
            response.setEmployeeStatus(initialStatus);
            response.setEmployeeStatusId(initialStatus.getId());
        }


        return response;
    }

    @Override
    public Page<StaffDto> searchByPage(SearchStaffDto dto) {
        if (dto == null) {
            return null;
        }

        // check role
        UserDto userDto = userExtService.getCurrentUser();
        boolean isUser = RoleUtils.hasRoleUser(userDto);
        boolean isRecruitment = RoleUtils.hasRoleHrRecruitment(userDto);
        boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
        boolean isAssignment = RoleUtils.hasRoleHrAssignment(userDto);
        Staff staff = userExtService.getCurrentStaffEntity();
        boolean isShiftAssignment = RoleUtils.hasShiftAssignmentPermission(userDto, staff);

        // setStaffId or setListStaffId
        StaffDto currentStaff = userExtService.getCurrentStaff();

        List<UUID> managedStaffIds = null;

        if (isAdmin) {

        } else if (isAssignment && isShiftAssignment) {
            List<UUID> listStaffId = Collections.singletonList(currentStaff.getId());

            if (dto.getLevelNumber() == null) {
                managedStaffIds = staffHierarchyService.getAllManagedStaff(currentStaff.getId(), listStaffId);
            } else {
                managedStaffIds = staffHierarchyService.getManagedStaffByLevel(currentStaff.getId(), listStaffId,
                        dto.getLevelNumber(), dto.getCollectInEachLevel());
            }

            dto.setListStaffId(managedStaffIds);
        }

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.firstName ASC, entity.lastName ASC ";

        String sql = "";
        String sqlCount = "select count(distinct entity.id) from Staff as entity ";
        if (dto.getIsExportExcel() && dto.getIsExportExcel() != null) {
            sql = "select new com.globits.hr.dto.StaffDto(entity,true) from Staff as entity ";
        } else {
            if (dto.getIsBasic() != null && dto.getIsBasic()) {
                sql = "select new com.globits.hr.dto.StaffDto(entity,false, false) from Staff as entity ";
            } else {
                sql = "select new com.globits.hr.dto.StaffDto(entity,false) from Staff as entity ";
            }
        }

        String joinPositionStaff = "";
        Boolean hasJoinMainPosition = false;
        if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null
                || (dto.getDepartmentIds() != null && !dto.getDepartmentIds().isEmpty())
                || dto.getOrganizationId() != null || dto.getRankTitleId() != null || dto.getPositionId() != null) {
            hasJoinMainPosition = true;
            joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.id ";
        }
        String joinMaternityHistories = "";
        if (dto.getFromMaternityLeave() != null || dto.getToMaternityLeave() != null) {
            joinMaternityHistories = " LEFT JOIN entity.staffMaternityHistories hist ";
        }

        // Sửa lại code: 1 nhân viên có nhiều mẫu bảng lương
        SalaryPeriod salaryPeriod = null;
        if (dto.getSalaryPeriod() != null) {
            salaryPeriod = salaryPeriodRepository.findById(dto.getSalaryPeriod().getId()).orElse(null);
        } else if (dto.getSalaryPeriodId() != null) {
            salaryPeriod = salaryPeriodRepository.findById(dto.getSalaryPeriodId()).orElse(null);
        }

        if (salaryPeriod != null) {
            salaryPeriod.setFromDate(DateTimeUtil.getStartOfDay(salaryPeriod.getFromDate()));
            salaryPeriod.setToDate(DateTimeUtil.getEndOfDay(salaryPeriod.getToDate()));
        }

        if (salaryPeriod != null && dto.getSalaryTemplateId() != null) {
            // whereClause += " and EXISTS (select 1 from StaffSalaryTemplate sst " + "where
            // sst.salaryTemplate.id = :salaryTemplateId and sst.staff.id = entity.id " + "
            // and (date(sst.fromDate) <= date(:toDate) and ( date(sst.toDate) is null or
            // date(sst.toDate) >= date(:fromDate) ) ) ) ";
            whereClause += " and EXISTS (select 1 from StaffSalaryTemplate sst "
                    + "where sst.salaryTemplate.id = :salaryTemplateId and sst.staff.id = entity.id ) ";
        }

        if (dto.getIncludeVoided() == null || dto.getIncludeVoided().equals(false)) {
            whereClause += " and (entity.voided is null or entity.voided = false) ";
        }

        // keyword không phân biệt hoa thường
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( " + "LOWER(entity.displayName) LIKE LOWER(CONCAT('%', :text, '%')) "
                    + "OR LOWER(entity.staffCode) LIKE LOWER(CONCAT('%', :text, '%')) "
                    + "OR LOWER(entity.Email) LIKE LOWER(CONCAT('%', :text, '%')) "
                    + "OR LOWER(entity.phoneNumber) LIKE LOWER(CONCAT('%', :text, '%')) "
                    + "OR LOWER(CONCAT(entity.displayName, ' - ', entity.staffCode)) LIKE LOWER(CONCAT('%', :text, '%')) "
                    + ")";
        }

        if (dto.getFromBirthDate() != null) {
            whereClause += " AND ( entity.birthDate  >= :fromBirthDate ) ";
        }
        if (dto.getToBirthDate() != null) {
            whereClause += " AND ( entity.birthDate  <= :toBirthDate ) ";
        }

        if (dto.getBirthMonths() != null && !dto.getBirthMonths().isEmpty()) {
            whereClause += " AND MONTH(entity.birthDate) IN (:birthMonths) ";
        }

        // ContractOrganization - WorkOrganization theo hop dong gan nhat
        // Đơn vị ký hợp đồng
        if (dto.getContractOrganizationId() != null) {
            whereClause += " and (entity.id in ( SELECT sa.staff.id FROM StaffLabourAgreement sa"
                    + " where sa.contractOrganization.id =: contractOrganizationId "
                    + " and sa.startDate = (SELECT MAX(sa2.startDate) FROM StaffLabourAgreement sa2 WHERE sa2.staff.id = sa.staff.id ) )) ";
        }
        // Đơn vị làm việc
        if (dto.getWorkOrganizationId() != null) {
            whereClause += " and (entity.id in ( SELECT sa.staff.id FROM StaffLabourAgreement sa"
                    + " where sa.workOrganization.id =: workOrganizationId "
                    + " and sa.startDate = (SELECT MAX(sa2.startDate) FROM StaffLabourAgreement sa2 WHERE sa2.staff.id = sa.staff.id ) )) ";
        }
        // Trạng thái nhân viên
        if (dto.getEmployeeStatusId() != null && StringUtils.hasText(dto.getEmployeeStatusId().toString())) {
            whereClause += " AND ( entity.status.id  =: employeeStatusId ) ";
        }
        // Ngày bắt đầu chính thức
        if (dto.getFromStartDate() != null) {
            whereClause += " AND ( entity.startDate  >= :fromStartDate ) ";
        }

        if (dto.getToStartDate() != null) {
            whereClause += " AND ( entity.startDate  <= :toStartDate ) ";
        }

        // Ngày bắt đầu tuyển dụng
        if (dto.getFromRecruitmentDate() != null) {
            whereClause += " AND ( entity.recruitmentDate  >= :fromRecruitmentDate ) ";
        }

        if (dto.getToRecruitmentDate() != null) {
            whereClause += " AND ( entity.recruitmentDate  <= :toRecruitmentDate ) ";
        }

        // Địa điểm làm việc
        // Địa điểm làm việc chính - AND swl.isMainLocation = true
        if (dto.getWorkplaceId() != null && StringUtils.hasText(dto.getWorkplaceId().toString())) {
            whereClause += " AND entity.id IN (SELECT swl.staff.id FROM StaffWorkingLocation swl WHERE swl.workplace.id = :workplaceId  ) ";
        }
        // Tình trạng nhân viên. Chi tiết: HrConstants.StaffPhase
        if (dto.getStaffPhase() != null) {
            whereClause += " AND ( entity.staffPhase  =: staffPhase ) ";
        }

        // Số hợp đồng
        if (dto.getContractNumber() != null && StringUtils.hasText(dto.getContractNumber().toString())) {
            whereClause += " AND entity.id IN (SELECT sla.staff.id FROM StaffLabourAgreement sla WHERE LOWER(sla.labourAgreementNumber) LIKE LOWER(:contractNumber)) ";
        }
        // Giới tính
        if (dto.getGender() != null && StringUtils.hasText(dto.getGender())) {
            whereClause += " AND ( entity.gender  =: gender ) ";
        }

        // Tỉnh thường trú
        if (dto.getProvinceId() != null && StringUtils.hasText(dto.getProvinceId().toString())
                && (dto.getDistrictId() == null || !StringUtils.hasText(dto.getDistrictId().toString()))
                && (dto.getCommuneId() == null || !StringUtils.hasText(dto.getCommuneId().toString()))) {
            whereClause += " AND (entity.administrativeUnit.id = :provinceId "
                    + " OR (entity.administrativeUnit.parent IS NOT NULL AND entity.administrativeUnit.parent.id = :provinceId) "
                    + " OR (entity.administrativeUnit.parent.parent IS NOT NULL AND entity.administrativeUnit.parent.parent.id = :provinceId) )";
        }

        // Huyện thường trú
        if (dto.getDistrictId() != null && StringUtils.hasText(dto.getDistrictId().toString())
                && dto.getProvinceId() != null && StringUtils.hasText(dto.getProvinceId().toString())
                && (dto.getCommuneId() == null || !StringUtils.hasText(dto.getCommuneId().toString()))) {
            whereClause += " AND (entity.administrativeUnit.id = :districtId "
                    + " OR (entity.administrativeUnit.parent IS NOT NULL AND entity.administrativeUnit.parent.id = :districtId) )";
        }

        // Xã thường trú
        if (dto.getCommuneId() != null && StringUtils.hasText(dto.getCommuneId().toString())
                && dto.getDistrictId() != null && StringUtils.hasText(dto.getDistrictId().toString())
                && dto.getProvinceId() != null && StringUtils.hasText(dto.getProvinceId().toString())) {
            whereClause += " AND entity.administrativeUnit.id = :communeId ";
        }

        // tạm trú (nơi ở hiện tại)
        if (dto.getCurrentResidence() != null && StringUtils.hasText(dto.getCurrentResidence())) {
            whereClause += " AND ( LOWER(entity.currentResidence) LIKE LOWER(:currentResidence) ) ";
        }
        // Quê quán
        if (dto.getBirthPlace() != null && StringUtils.hasText(dto.getBirthPlace())) {
            whereClause += " AND ( LOWER(entity.birthPlace) LIKE LOWER(:birthPlace) ) ";
        }
        // CMND/CCCD
        if (dto.getIdNumber() != null && StringUtils.hasText(dto.getIdNumber())) {
            whereClause += " AND ( LOWER(entity.idNumber) LIKE LOWER(:idNumber) OR LOWER(entity.personalIdentificationNumber) LIKE LOWER(:idNumber) ) ";
        }

        // Tình trạng hôn nhân
        if (dto.getMaritalStatus() != null) {
            whereClause += " AND ( entity.maritalStatus = :maritalStatus ) ";
        }

        // Mã số thuế
        if (dto.getTaxCode() != null && StringUtils.hasText(dto.getTaxCode())) {
            whereClause += " AND ( LOWER(entity.taxCode) LIKE LOWER(:taxCode) ) ";
        }

        // Mã số bảo hiểm y tế (BHYT)
        if (dto.getHealthInsuranceNumber() != null && StringUtils.hasText(dto.getHealthInsuranceNumber())) {
            whereClause += " AND ( LOWER(entity.healthInsuranceNumber) LIKE LOWER(:healthInsuranceNumber) ) ";
        }

        // Mã số BHXH
        if (dto.getSocialInsuranceNumber() != null && StringUtils.hasText(dto.getSocialInsuranceNumber())) {
            whereClause += " AND ( LOWER(entity.socialInsuranceNumber) LIKE LOWER(:socialInsuranceNumber) ) ";
        }

        // Tình trạng sổ BHXH
        if (dto.getSocialInsuranceNote() != null && StringUtils.hasText(dto.getSocialInsuranceNote())) {
            whereClause += " AND ( LOWER(entity.socialInsuranceNote) LIKE LOWER(:socialInsuranceNote) ) ";
        }

        // Có tham gia BHXH
        if (dto.getHasSocialInsuranceNumber() != null && dto.getHasSocialInsuranceNumber() == true) {
            whereClause += " AND ( entity.socialInsuranceNumber IS NOT NULL AND entity.socialInsuranceNumber <> '') ";
        }

        // Nhân viên giới thiệu nhân viên này vào làm
        if (dto.getIntroducerId() != null && StringUtils.hasText(dto.getIntroducerId().toString())) {
            whereClause += " AND ( entity.introducer.id = :introducerId ) ";
        }
        // Nhân viên quyết định tuyển dụng nhân viên này vào làm
        if (dto.getRecruiterId() != null && StringUtils.hasText(dto.getRecruiterId().toString())) {
            whereClause += " AND ( entity.recruiter.id = :recruiterId ) ";
        }
        // Tình trạng hoàn thành hồ sơ của nhân viên. Chi tiết:
        // HrConstants.StaffDocumentStatus
        if (dto.getStaffDocumentStatus() != null) {
            whereClause += " AND ( entity.staffDocumentStatus = :staffDocumentStatus ) ";
        }

        if (dto.getAcademicTitleLevel() != null) {
            whereClause += " AND ( entity.academicRank.id IN (select a.id from AcademicTitle a where a.level >= :academicTitleLevel ) ) ";
        }
        if (dto.getEducationDegreeLevel() != null) {
            whereClause += " AND ( entity.degree.id IN (select a.id from EducationDegree a where a.level >= :educationDegreeLevel ) ) ";
        }

        if (dto.getCivilServantTypeId() != null && StringUtils.hasText(dto.getCivilServantTypeId().toString())) {
            whereClause += " AND ( entity.civilServantType.id  =: civilServantTypeId ) ";
        }

        if (dto.getApprovalStatus() != null) {
            whereClause += " AND ( entity.approvalStatus = :approvalStatus ) ";
        }
        if (dto.getStaffTypeId() != null) {
            whereClause += " AND ( entity.staffType.id = :staffTypeId ) ";
        }

        if (dto.getHasSocialIns() != null && dto.getHasSocialIns()) {
            whereClause += " AND ( entity.hasSocialIns is not null and entity.hasSocialIns = true ) ";
        } else if (dto.getHasSocialIns() != null && !dto.getHasSocialIns()) {
            whereClause += " AND ( entity.hasSocialIns is null or entity.hasSocialIns = false ) ";
        }

        if (dto.getAllowExternalIpTimekeeping() != null && dto.getAllowExternalIpTimekeeping()) {
            whereClause += " AND ( entity.allowExternalIpTimekeeping = true ) ";
        }

        // handling for choosing multiple project
        if (dto.getProjectIdList() != null) {
            sql += " INNER JOIN ProjectStaff ps on entity.id = ps.staff.id ";
            sqlCount += " INNER JOIN ProjectStaff ps on entity.id = ps.staff.id ";
            whereClause += " AND (ps.project.id in :projectIdList) ";

            if (dto.getIncludeVoidedInProject() != null && dto.getIncludeVoidedInProject()) {
                whereClause += "";
            } else {
                whereClause += " and (ps.voided = false or ps.voided is null) ";
            }
        }
        // just choose a single project
        else if (dto.getProjectId() != null) {
            sql += " INNER JOIN ProjectStaff ps on entity.id = ps.staff.id ";
            sqlCount += " INNER JOIN ProjectStaff ps on entity.id = ps.staff.id ";
            whereClause += " AND (ps.project.id = :projectId)";

            if (dto.getIncludeVoidedInProject() != null && dto.getIncludeVoidedInProject()) {
                whereClause += "";
            } else {
                whereClause += " and (ps.voided = false or ps.voided is null) ";
            }
        }

        if (hasJoinMainPosition != null && hasJoinMainPosition) {
            sql += joinPositionStaff;
            sqlCount += joinPositionStaff;

            // Chức vụ (vị trí làm việc)
            if (dto.getPositionId() != null && StringUtils.hasText(dto.getPositionId().toString())) {
                whereClause += " AND ( pos.id  =: positionId ) ";
            }

            // Cấp bậc
            if (dto.getRankTitleId() != null && StringUtils.hasText(dto.getRankTitleId().toString())) {
                whereClause += " AND ( pos.title.rankTitle.id  =: rankTitleId ) ";
            }

            if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
                whereClause += " AND ( pos.department.organization.id  =: organizationId ) ";
            }
            if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
                whereClause += " AND ( pos.department.id  =: departmentId ) ";
            }
            if (dto.getDepartmentIds() != null && !dto.getDepartmentIds().isEmpty()) {
                whereClause += " AND pos.department.id IN :departmentIds ";
            }
            if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
                whereClause += " AND ( pos.title.id  =: positionTitleId ) ";
            }
        }
        if (dto.getDirectManagerId() != null && StringUtils.hasText(dto.getDirectManagerId().toString())) {
            whereClause += " AND entity.id IN ( SELECT pr.position.staff.id FROM Position p JOIN PositionRelationShip pr ON pr.supervisor.id = p.id JOIN p.staff s WHERE pr.relationshipType = 3 AND s.id = :directManagerId ) ";
        }

        if (dto.getStaffWorkShiftType() != null) {
            whereClause += " AND (entity.staffWorkShiftType = :staffWorkShiftType) ";
        }

        if (dto.getFixShiftWorkId() != null) {
            whereClause += " AND (entity.fixShiftWork.id = :fixShiftWorkId) ";
        }

        if (dto.getStaffLeaveShiftType() != null) {
            whereClause += " AND (entity.staffLeaveShiftType = :staffLeaveShiftType) ";
        }

        if (dto.getFixLeaveWeekDay() != null) {
            whereClause += " AND (entity.fixLeaveWeekDay = :fixLeaveWeekDay) ";
        }

        // list nhan vien
        if (dto.getListStaffId() != null && !dto.getListStaffId().isEmpty()) {
            whereClause += " AND entity.id IN ( :listStaffId ) ";
        }

        if (dto.getFromMaternityLeave() != null) {
            whereClause += " AND hist.startDate >= :fromMaternityLeave";

        }
        if (dto.getToMaternityLeave() != null) {
            whereClause += " AND hist.endDate <= :toMaternityLeave ";
        }

        //hết nghỉ chế độ thai sản là trạng thái nhân viên là nghỉ chế độ và thời gian nghỉ thai sản đã kế thúc
        if (dto.getMaternityLeaveEnded() != null && dto.getMaternityLeaveEnded()) {
            whereClause += " AND entity.status.code = :employeeStatusCode ";
            whereClause += " AND (entity.id in ( SELECT sm.staff.id FROM StaffMaternityHistory sm where sm.maternityLeaveEndDate <:dateNow ))";
        }
        sql += joinMaternityHistories + whereClause + orderBy;
        sqlCount += joinMaternityHistories + whereClause;

        Query query = manager.createQuery(sql, StaffDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword().strip() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword().strip() + '%');
        }
        if (dto.getFromBirthDate() != null) {
            query.setParameter("fromBirthDate", dto.getFromBirthDate());
            qCount.setParameter("fromBirthDate", dto.getFromBirthDate());
        }
        if (dto.getToBirthDate() != null) {
            query.setParameter("toBirthDate", dto.getToBirthDate());
            qCount.setParameter("toBirthDate", dto.getToBirthDate());
        }
        if (dto.getBirthMonths() != null && !dto.getBirthMonths().isEmpty()) {
            query.setParameter("birthMonths", dto.getBirthMonths());
            qCount.setParameter("birthMonths", dto.getBirthMonths());
        }

        if (dto.getContractOrganizationId() != null) {
            query.setParameter("contractOrganizationId", dto.getContractOrganizationId());
            qCount.setParameter("contractOrganizationId", dto.getContractOrganizationId());
        }
        if (dto.getWorkOrganizationId() != null) {
            query.setParameter("workOrganizationId", dto.getWorkOrganizationId());
            qCount.setParameter("workOrganizationId", dto.getWorkOrganizationId());
        }
        if (dto.getFromMaternityLeave() != null) {
            query.setParameter("fromMaternityLeave", dto.getFromMaternityLeave());
            qCount.setParameter("fromMaternityLeave", dto.getFromMaternityLeave());
        }
        if (dto.getToMaternityLeave() != null) {
            whereClause += " AND hist.endDate <= :toMaternityLeave ";
            query.setParameter("toMaternityLeave", dto.getToMaternityLeave());
            qCount.setParameter("toMaternityLeave", dto.getToMaternityLeave());
        }
        // organization - department - positionTitle
        if (hasJoinMainPosition != null && hasJoinMainPosition == true) {

            // Chức vụ
            if (dto.getPositionId() != null && StringUtils.hasText(dto.getPositionId().toString())) {
                query.setParameter("positionId", dto.getPositionId());
                qCount.setParameter("positionId", dto.getPositionId());
            }

            // Cấp bậc
            if (dto.getRankTitleId() != null && StringUtils.hasText(dto.getRankTitleId().toString())) {
                query.setParameter("rankTitleId", dto.getRankTitleId());
                qCount.setParameter("rankTitleId", dto.getRankTitleId());
            }

            if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
                query.setParameter("organizationId", dto.getOrganizationId());
                qCount.setParameter("organizationId", dto.getOrganizationId());
            }
            if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
                query.setParameter("departmentId", dto.getDepartmentId());
                qCount.setParameter("departmentId", dto.getDepartmentId());
            }
            if (dto.getDepartmentIds() != null && !dto.getDepartmentIds().isEmpty()) {
                query.setParameter("departmentIds", dto.getDepartmentIds());
                qCount.setParameter("departmentIds", dto.getDepartmentIds());
            }
            if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
                query.setParameter("positionTitleId", dto.getPositionTitleId());
                qCount.setParameter("positionTitleId", dto.getPositionTitleId());
            }
        }
        if (dto.getDirectManagerId() != null && StringUtils.hasText(dto.getDirectManagerId().toString())) {
            query.setParameter("directManagerId", dto.getDirectManagerId());
            qCount.setParameter("directManagerId", dto.getDirectManagerId());
        }
        if (dto.getApprovalStatus() != null) {
            query.setParameter("approvalStatus", dto.getApprovalStatus());
            qCount.setParameter("approvalStatus", dto.getApprovalStatus());
        }

        if (dto.getCivilServantTypeId() != null && StringUtils.hasText(dto.getCivilServantTypeId().toString())) {
            query.setParameter("civilServantTypeId", dto.getCivilServantTypeId());
            qCount.setParameter("civilServantTypeId", dto.getCivilServantTypeId());
        }
        if (dto.getAcademicTitleLevel() != null) {
            query.setParameter("academicTitleLevel", dto.getAcademicTitleLevel());
            qCount.setParameter("academicTitleLevel", dto.getAcademicTitleLevel());
        }
        if (dto.getEducationDegreeLevel() != null) {
            query.setParameter("educationDegreeLevel", dto.getEducationDegreeLevel());
            qCount.setParameter("educationDegreeLevel", dto.getEducationDegreeLevel());
        }
        if (dto.getEmployeeStatusId() != null && StringUtils.hasText(dto.getEmployeeStatusId().toString())) {
            query.setParameter("employeeStatusId", dto.getEmployeeStatusId());
            qCount.setParameter("employeeStatusId", dto.getEmployeeStatusId());
        }


        // Ngày bắt đầu chính thức
        if (dto.getFromStartDate() != null) {
            query.setParameter("fromStartDate", dto.getFromStartDate());
            qCount.setParameter("fromStartDate", dto.getFromStartDate());
        }

        if (dto.getToStartDate() != null) {
            query.setParameter("toStartDate", dto.getToStartDate());
            qCount.setParameter("toStartDate", dto.getToStartDate());
        }

        // Ngày bắt đầu tuyển dụng
        if (dto.getFromRecruitmentDate() != null) {
            query.setParameter("fromRecruitmentDate", dto.getFromRecruitmentDate());
            qCount.setParameter("fromRecruitmentDate", dto.getFromRecruitmentDate());
        }

        if (dto.getToRecruitmentDate() != null) {
            query.setParameter("toRecruitmentDate", dto.getToRecruitmentDate());
            qCount.setParameter("toRecruitmentDate", dto.getToRecruitmentDate());
        }
        // Địa điểm làm việc
        if (dto.getWorkplaceId() != null && StringUtils.hasText(dto.getWorkplaceId().toString())) {
            query.setParameter("workplaceId", dto.getWorkplaceId());
            qCount.setParameter("workplaceId", dto.getWorkplaceId());

        }
        // Tình trạng nhân viên. Chi tiết: HrConstants.StaffPhase
        if (dto.getStaffPhase() != null) {
            query.setParameter("staffPhase", dto.getStaffPhase());
            qCount.setParameter("staffPhase", dto.getStaffPhase());
        }

        // Số hợp đồng
        if (dto.getContractNumber() != null && StringUtils.hasText(dto.getContractNumber().toString())) {
            query.setParameter("contractNumber", "%" + dto.getContractNumber() + "%");
            qCount.setParameter("contractNumber", "%" + dto.getContractNumber() + "%");
        }
        // Giới tính
        if (dto.getGender() != null && StringUtils.hasText(dto.getGender())) {
            query.setParameter("gender", dto.getGender());
            qCount.setParameter("gender", dto.getGender());
        }
//        Tỉnh thường trú
        if (dto.getProvinceId() != null && StringUtils.hasText(dto.getProvinceId().toString())
                && (dto.getDistrictId() == null || !StringUtils.hasText(dto.getDistrictId().toString()))
                && (dto.getCommuneId() == null || !StringUtils.hasText(dto.getCommuneId().toString()))) {
            query.setParameter("provinceId", dto.getProvinceId());
            qCount.setParameter("provinceId", dto.getProvinceId());
        }
        // Huyện thường trú
        if (dto.getDistrictId() != null && StringUtils.hasText(dto.getDistrictId().toString())
                && dto.getProvinceId() != null && StringUtils.hasText(dto.getProvinceId().toString())
                && (dto.getCommuneId() == null || !StringUtils.hasText(dto.getCommuneId().toString()))) {
            query.setParameter("districtId", dto.getDistrictId());
            qCount.setParameter("districtId", dto.getDistrictId());
        }
        // Xã thường trú
        if (dto.getCommuneId() != null && StringUtils.hasText(dto.getCommuneId().toString())
                && dto.getDistrictId() != null && StringUtils.hasText(dto.getDistrictId().toString())
                && dto.getProvinceId() != null && StringUtils.hasText(dto.getProvinceId().toString())) {
            query.setParameter("communeId", dto.getCommuneId());
            qCount.setParameter("communeId", dto.getCommuneId());
        }
        // tạm trú (nơi ở hiện tại)
        if (dto.getCurrentResidence() != null && StringUtils.hasText(dto.getCurrentResidence())) {
            query.setParameter("currentResidence", "%" + dto.getCurrentResidence() + "%");
            qCount.setParameter("currentResidence", "%" + dto.getCurrentResidence() + "%");
        }
        // Quê quán
        if (dto.getBirthPlace() != null && StringUtils.hasText(dto.getBirthPlace())) {
            query.setParameter("birthPlace", "%" + dto.getBirthPlace() + "%");
            qCount.setParameter("birthPlace", "%" + dto.getBirthPlace() + "%");
        }
        // CMND/CCCD
        if (dto.getIdNumber() != null && StringUtils.hasText(dto.getIdNumber())) {
            query.setParameter("idNumber", "%" + dto.getIdNumber() + "%");
            qCount.setParameter("idNumber", "%" + dto.getIdNumber() + "%");
        }

        // Tình trạng hôn nhân
        if (dto.getMaritalStatus() != null) {
            query.setParameter("maritalStatus", dto.getMaritalStatus());
            qCount.setParameter("maritalStatus", dto.getMaritalStatus());
        }

        // Mã số thuế
        if (dto.getTaxCode() != null && StringUtils.hasText(dto.getTaxCode())) {
            query.setParameter("taxCode", "%" + dto.getTaxCode() + "%");
            qCount.setParameter("taxCode", "%" + dto.getTaxCode() + "%");
        }

        // Mã số bảo hiểm y tế (BHYT)
        if (dto.getHealthInsuranceNumber() != null && StringUtils.hasText(dto.getHealthInsuranceNumber())) {
            query.setParameter("healthInsuranceNumber", "%" + dto.getHealthInsuranceNumber() + "%");
            qCount.setParameter("healthInsuranceNumber", "%" + dto.getHealthInsuranceNumber() + "%");
        }

        // Mã số BHXH
        if (dto.getSocialInsuranceNumber() != null && StringUtils.hasText(dto.getSocialInsuranceNumber())) {
            query.setParameter("socialInsuranceNumber", "%" + dto.getSocialInsuranceNumber() + "%");
            qCount.setParameter("socialInsuranceNumber", "%" + dto.getSocialInsuranceNumber() + "%");
        }

        // Tình trạng sổ BHXH
        if (dto.getSocialInsuranceNote() != null && StringUtils.hasText(dto.getSocialInsuranceNote())) {
            query.setParameter("socialInsuranceNote", "%" + dto.getSocialInsuranceNote() + "%");
            qCount.setParameter("socialInsuranceNote", "%" + dto.getSocialInsuranceNote() + "%");
        }

        // Nhân viên giới thiệu nhân viên này vào làm
        if (dto.getIntroducerId() != null && StringUtils.hasText(dto.getIntroducerId().toString())) {
            query.setParameter("introducerId", dto.getIntroducerId());
            qCount.setParameter("introducerId", dto.getIntroducerId());
        }
        // Nhân viên quyết định tuyển dụng nhân viên này vào làm
        if (dto.getRecruiterId() != null && StringUtils.hasText(dto.getRecruiterId().toString())) {
            query.setParameter("recruiterId", dto.getRecruiterId());
            qCount.setParameter("recruiterId", dto.getRecruiterId());
        }
        // Tình trạng hoàn thành hồ sơ của nhân viên. Chi tiết:
        // HrConstants.StaffDocumentStatus
        if (dto.getStaffDocumentStatus() != null) {
            query.setParameter("staffDocumentStatus", dto.getStaffDocumentStatus());
            qCount.setParameter("staffDocumentStatus", dto.getStaffDocumentStatus());
        }

        if (dto.getStaffTypeId() != null) {
            query.setParameter("staffTypeId", dto.getStaffTypeId());
            qCount.setParameter("staffTypeId", dto.getStaffTypeId());
        }

        // handling for choosing multiple project
        if (dto.getProjectIdList() != null) {
            query.setParameter("projectIdList", dto.getProjectIdList());
            qCount.setParameter("projectIdList", dto.getProjectIdList());
        }
        // just choose a single project
        else if (dto.getProjectId() != null) {
            query.setParameter("projectId", dto.getProjectId());
            qCount.setParameter("projectId", dto.getProjectId());
        }

        if (salaryPeriod != null && dto.getSalaryTemplateId() != null) {
            // Chuẩn hóa fromDate và toDate
            // Date fromDate = DateTimeUtil.getStartOfDay(salaryPeriod.getFromDate());
            // Date toDate = DateTimeUtil.getEndOfDay(salaryPeriod.getToDate());

            query.setParameter("salaryTemplateId", dto.getSalaryTemplateId());
            // query.setParameter("fromDate", fromDate);
            // query.setParameter("toDate", toDate);

            qCount.setParameter("salaryTemplateId", dto.getSalaryTemplateId());
            // qCount.setParameter("fromDate", fromDate);
            // qCount.setParameter("toDate", toDate);
        }

        if (dto.getListStaffId() != null && !dto.getListStaffId().isEmpty()) {
            query.setParameter("listStaffId", dto.getListStaffId());
            qCount.setParameter("listStaffId", dto.getListStaffId());
        }

        if (dto.getStaffWorkShiftType() != null) {
            query.setParameter("staffWorkShiftType", dto.getStaffWorkShiftType());
            qCount.setParameter("staffWorkShiftType", dto.getStaffWorkShiftType());
        }

        if (dto.getFixShiftWorkId() != null) {
            query.setParameter("fixShiftWorkId", dto.getFixShiftWorkId());
            qCount.setParameter("fixShiftWorkId", dto.getFixShiftWorkId());
        }

        if (dto.getStaffLeaveShiftType() != null) {
            query.setParameter("staffLeaveShiftType", dto.getStaffLeaveShiftType());
            qCount.setParameter("staffLeaveShiftType", dto.getStaffLeaveShiftType());
        }

        if (dto.getFixLeaveWeekDay() != null) {
            query.setParameter("fixLeaveWeekDay", dto.getFixLeaveWeekDay());
            qCount.setParameter("fixLeaveWeekDay", dto.getFixLeaveWeekDay());
        }
        if (dto.getMaternityLeaveEnded() != null && dto.getMaternityLeaveEnded()) {
            query.setParameter("employeeStatusCode", HrConstants.DismissPositions.NGHI_CHE_DO.getValue());
            qCount.setParameter("employeeStatusCode", HrConstants.DismissPositions.NGHI_CHE_DO.getValue());

            Date dateNow = new Date();
            query.setParameter("dateNow", dateNow);
            qCount.setParameter("dateNow", dateNow);
        }
        // List<StaffDto> entities;
        List<StaffDto> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();

        Page<StaffDto> result;
        if (dto.getIsExportExcel() && dto.getIsExportExcel() != null) {
            entities = query.getResultList();
            result = new PageImpl<>(entities);
        } else {
            int startPosition = pageIndex * pageSize;
            query.setFirstResult(startPosition);
            query.setMaxResults(pageSize);
            entities = query.getResultList();

            Pageable pageable = PageRequest.of(pageIndex, pageSize);

            result = new PageImpl<>(entities, pageable, count);
        }

        manager.flush();
        manager.clear();

        return result;
    }

    @Override
    public StaffDto getTotalHasSocialIns(SearchStaffDto dto) {
        if (dto.getHasSocialIns() != null && dto.getHasSocialIns()) {
            StaffDto sumIns = staffRepository.getTotalInsuranceAmounts();
            return sumIns;
        }
        return new StaffDto();
    }

    private void addChildren(Department parent, List<Department> children) {
        if (null != parent.getSubDepartments()) {
            for (Department child : parent.getSubDepartments()) {
                children.add(child);
                addChildren(child, children);
            }
        }
    }

    @Override
    public List<UUID> getAllDepartmentIdByParentId(UUID parentId) {
        Department parent = departmentRepository.getOne(parentId);
        if (parent.getId() != null) {
            List<UUID> ret = new ArrayList<>();
            List<Department> children = new ArrayList<>();
            ret.add(parent.getId());
            this.addChildren(parent, children);
            if (children.size() > 0) {
                for (Department dp : children) {
                    ret.add(dp.getId());
                }
            }
            return ret;
        }
        return null;
    }

    @Override
    public StaffDto savePositionStaff(PositionTitleStaffDto dto) {
        if (dto != null) {
            Staff entity = null;
            PositionStaff positionStaff = new PositionStaff();
            Set<PositionStaff> listFamilyRelationship = new HashSet<>();
            PositionTitle positionTitle = null;
            if (dto.getStaffCode() != null) {
                List<Staff> listStaff = staffRepository.getByCode(dto.getStaffCode());
                if (listStaff != null && listStaff.size() > 0) {
                    entity = listStaff.get(0);
                    positionStaff.setStaff(entity);
                    listFamilyRelationship = entity.getPositions();
                }
            }
            if (entity == null) {
                return null;
            }
//            if (dto.getPositionTitleCode() != null) {
//                List<PositionTitle> listPositionTitle = positionTitleRepository.findByCode(dto.getPositionTitleCode());
//                if (listPositionTitle != null && listPositionTitle.size() > 0) {
//                    positionTitle = listPositionTitle.get(0);
//                    positionStaff.setPosition(positionTitle);
//                }
//            }
            if (positionTitle == null) {
                return null;
            }
            if (dto.getFromDate() != null) {
                positionStaff.setFromDate(dto.getFromDate());
            }
            if (dto.getToDate() != null) {
                positionStaff.setToDate(dto.getToDate());
            }
            if (dto.getDepartmentCode() != null) {
                List<HRDepartment> depart = hRDepartmentRepository.findByName(dto.getDepartmentCode());
                if (depart != null && depart.size() > 0) {
                    positionStaff.setHrDepartment(depart.get(0));
                }
            }
            positionStaff = positionStaffRepository.save(positionStaff);
            listFamilyRelationship.add(positionStaff);
            entity.setPositions(listFamilyRelationship);
            entity = staffRepository.save(entity);
            return new StaffDto(entity);
        }
        return null;
    }

    @Override
    public Staff getByCode(String code) {
        List<Staff> lst = this.staffRepository.getByCode(code);
        if (lst != null && lst.size() > 0) {
            return lst.get(0);
        }
        return null;
    }

    @Override
    public Boolean checkIdNumber(StaffDto dto) {
        Long count = staffRepository.countByIdNumber(dto.getId(), dto.getIdNumber());
        return count != 0L;
    }

    @Override
    public StaffDto updateStaffImage(UUID id, String imagePath) {
        if (id != null) {
            Staff entity = null;
            Optional<Staff> optional = staffRepository.findById(id);
            if (optional.isPresent()) {
                entity = optional.get();
            }
            if (entity != null) {
                entity.setImagePath(imagePath);
                entity = staffRepository.save(entity);
                return new StaffDto(entity);
            }
        }
        return null;
    }

    // create/update staff WITHOUT create and update staff's account
    @Override
    @Transactional
    public StaffDto saveStaffWithoutAccount(StaffDto staffDto) {
        // xét null cho staffDto
        if (staffDto == null) {
            return null;
        }
        String currentUserName = "Unknown User";
        LocalDateTime currentDate = LocalDateTime.now();

        boolean isRoleUser = false;
        boolean isRoleAdmin = false;
        boolean isRoleManager = false;

        UserDto userDto = userExtService.getCurrentUser();
        if (userDto != null && userDto.getRoles() != null && userDto.getRoles().size() > 0) {
            for (RoleDto item : userDto.getRoles()) {
                // nếu name của item khác null và name của item giống ROLE_ADMIN thì isRoleAdmin
                // = true
                if (item.getName() != null && "ROLE_ADMIN".equals(item.getName())) {
                    isRoleAdmin = true;
                }
                if (item.getName() != null && "HR_MANAGER".equals(item.getName())) {
                    isRoleManager = true;
                }
                if (item.getName() != null && "HR_USER".equals(item.getName())) {
                    isRoleUser = true;
                }
            }
        }
        if (isRoleAdmin) {
            isRoleUser = false;
        } else {
            if (isRoleManager) {
                isRoleUser = false;
            }
        }

        Staff staff = new Staff();
        if (staffDto != null && staffDto.getId() != null) {
            staff = staffRepository.findById(staffDto.getId()).orElse(null);

            if (staff == null)
                staff = new Staff();

            if (isRoleUser) {
                // nếu userName của userDto khác null và userName của userDto so sánh với
                // staffCode của staff thì trả về null
                if (userDto.getUsername() != null && !userDto.getUsername().equals(staff.getStaffCode())) {
                    return null;
                }
            }
        }

        // nếu staff bằng null và staffCode của staffDto khác null
        if (staff == null && staffDto.getStaffCode() != null) {
            // tạo một list
            List<Staff> list = staffRepository.getByCode(staffDto.getStaffCode());
            if (list != null && !list.isEmpty()) {
                staff = list.get(0);
            }
        }

        if (staff == null) {
            staff = new Staff();
        }

        // set các trường trong class staff()
        staff.setStaffCode(staffDto.getStaffCode());
        staff.setApprenticeDays(staffDto.getApprenticeDays());
        staff.setFirstName(normalize(staffDto.getFirstName()));
        staff.setLastName(normalize(staffDto.getLastName()));
        staff.setStaffPositionType(staffDto.getStaffPositionType());
        staff.setBirthDate(staffDto.getBirthDate());
        staff.setBirthPlace(staffDto.getBirthPlace());
        staff.setGender(staffDto.getGender());
        staff.setPhoto(staffDto.getPhoto());
        staff.setDisplayName(normalize(staffDto.getDisplayName()));
        staff.setPhoneNumber(staffDto.getPhoneNumber());
        staff.setMaritalStatus(staffDto.getMaritalStatus());
        staff.setCurrentWorkingStatus(staffDto.getCurrentWorkingStatus());
        staff.setSalaryCoefficient(staffDto.getSalaryCoefficient());
        staff.setJobTitle(staffDto.getJobTitle());
        staff.setHighestPosition(staffDto.getHighestPosition());
        staff.setDateOfReceivingPosition(staffDto.getDateOfReceivingPosition());
        staff.setPositionDecisionNumber(staffDto.getPositionDecisionNumber());
        staff.setDateOfReceivingAllowance(staffDto.getDateOfReceivingAllowance());
        staff.setProfessionalTitles(staffDto.getProfessionalTitles());
        staff.setAllowanceCoefficient(staffDto.getAllowanceCoefficient());
        staff.setSalaryLeve(staffDto.getSalaryLeve());
        staff.setEthnicLanguage(staffDto.getEthnicLanguage());
        staff.setPhysicalEducationTeacher(staffDto.getPhysicalEducationTeacher());
        staff.setSpecializedName(staffDto.getSpecializedName());
        staff.setFormsOfTraining(staffDto.getFormsOfTraining());
        staff.setTrainingCountry(staffDto.getTrainingCountry());
        staff.setTrainingPlaces(staffDto.getTrainingPlaces());
        staff.setHighSchoolEducation(staffDto.getHighSchoolEducation());
        staff.setQualification(staffDto.getQualification());
        staff.setCertificationScore(staffDto.getCertificationScore());
        staff.setYearOfCertification(staffDto.getYearOfCertification());
        staff.setNote(staffDto.getNote());
        staff.setYearOfRecognitionDegree(staffDto.getYearOfRecognitionDegree());
        staff.setYearOfRecognitionAcademicRank(staffDto.getYearOfRecognitionAcademicRank());
        staff.setImagePath(staffDto.getImagePath());
        staff.setPermanentResidence(staffDto.getPermanentResidence());
        staff.setCurrentResidence(staffDto.getCurrentResidence());
        staff.setWards(staffDto.getWards());
        staff.setYearOfConferred(staffDto.getYearOfConferred());
        staff.setFamilyComeFromString(staffDto.getFamilyComeFrom());
        staff.setFamilyPriority(staffDto.getFamilyPriority());
        staff.setFamilyYourself(staffDto.getFamilyYourself());
        staff.setHasSocialIns(staffDto.getHasSocialIns());
        staff.setStartInsDate(staffDto.getStartInsDate());
        staff.setStaffWorkShiftType(staffDto.getStaffWorkShiftType());
        staff.setStaffLeaveShiftType(staffDto.getStaffLeaveShiftType());
        staff.setSkipLateEarlyCount(staffDto.getSkipLateEarlyCount());
        staff.setSkipOvertimeCount(staffDto.getSkipOvertimeCount());
        staff.setFixLeaveWeekDay(staffDto.getFixLeaveWeekDay());
        staff.setFixLeaveWeekDay2(staffDto.getFixLeaveWeekDay2());

        if (staffDto.getFixShiftWork() != null && staffDto.getStaffWorkShiftType() != null
                && staffDto.getStaffWorkShiftType().equals(HrConstants.StaffWorkShiftType.FIXED.getValue())) {
            ShiftWork shiftWork = shiftWorkRepository.findById(staffDto.getFixShiftWork().getId()).orElse(null);
            staff.setFixShiftWork(shiftWork);
        } else {
            staff.setFixShiftWork(null);
        }

        if (staffDto.getPersonalIdentificationNumber() != null
                && staffDto.getPersonalIdentificationNumber().length() == 12) {
            staff.setPersonalIdentificationNumber(staffDto.getPersonalIdentificationNumber());
        } else {
            staff.setPersonalIdentificationNumber(null);
        }
        staff.setPersonalIdentificationIssueDate(staffDto.getPersonalIdentificationIssueDate());
        staff.setPersonalIdentificationIssuePlace(staffDto.getPersonalIdentificationIssuePlace());

        // bhxh
        staff.setSocialInsuranceNumber(staffDto.getSocialInsuranceNumber());
        staff.setInsuranceSalary(staffDto.getInsuranceSalary());
        staff.setStaffPercentage(staffDto.getStaffPercentage());
        staff.setStaffInsuranceAmount(staffDto.getStaffInsuranceAmount());
        staff.setUnionDuesPercentage(staffDto.getUnionDuesPercentage());
        staff.setOrgPercentage(staffDto.getOrgPercentage());
        staff.setOrgInsuranceAmount(staffDto.getOrgInsuranceAmount());
        staff.setUnionDuesAmount(staffDto.getUnionDuesAmount());
        staff.setInsuranceStartDate(staffDto.getInsuranceStartDate());
        staff.setInsuranceEndDate(staffDto.getInsuranceEndDate());
        staff.setInsuranceSalaryCoefficient(staffDto.getInsuranceSalaryCoefficient());

        // cho phép chấm công ngoài công ty
        staff.setAllowExternalIpTimekeeping(staffDto.getAllowExternalIpTimekeeping());

        // Số bảo hiểm y tế (BHYT)
        staff.setHealthInsuranceNumber(staffDto.getHealthInsuranceNumber());

        // Thêm thông tin tài khoản ngân hàng
        staff.setBankAccountName(staffDto.getBankAccountName());
        staff.setBankAccountNumber(staffDto.getBankAccountNumber());
        staff.setBankName(staffDto.getBankName());
        staff.setBankBranch(staffDto.getBankBranch());

        // Nếu là người nước ngoài
        staff.setWorkPermitNumber(staffDto.getWorkPermitNumber());
        staff.setPassportNumber(staffDto.getPassportNumber());

        staff.setTaxCode(staffDto.getTaxCode());
        if (staffDto.getStaffInsuranceAmount() != null && staffDto.getOrgInsuranceAmount() != null) {
            staff.setTotalInsuranceAmount(staffDto.getStaffInsuranceAmount() + staffDto.getOrgInsuranceAmount());
        }

        // loại nhân viên
        if (staffDto.getStaffType() != null) {
            StaffType staffType = staffTypeRepository.findById(staffDto.getStaffType().getId()).orElse(null);
            staff.setStaffType(staffType);
        } else {
            staff.setStaffType(null);
        }

        HrAdministrativeUnitDto administrativeUnit = staffDto.getAdministrativeunit();
        HrAdministrativeUnitDto district = staffDto.getDistrict();
        HrAdministrativeUnitDto province = staffDto.getProvince();
        if (administrativeUnit != null && administrativeUnit.getId() != null) {
            administrativeUnitRepository.findById(administrativeUnit.getId()).ifPresent(staff::setAdministrativeUnit);
        } else if (district != null && district.getId() != null) {
            administrativeUnitRepository.findById(district.getId()).ifPresent(staff::setAdministrativeUnit);
        } else if (province != null && province.getId() != null) {
            administrativeUnitRepository.findById(province.getId()).ifPresent(staff::setAdministrativeUnit);
        } else {
            staff.setAdministrativeUnit(null);
        }
        // set trạng thái
        if (staffDto.getStatus() != null) {
            EmployeeStatus status = null;
            Optional<EmployeeStatus> optional = employeeStatusRepository.findById(staffDto.getStatus().getId());
            if (optional.isPresent()) {
                status = optional.get();
            }
            staff.setStatus(status);
        } else {
            staff.setStatus(null);
        }
        // set dân tộc
        if (staffDto.getEthnics() != null) {
            Ethnics ethnics = null;
            Optional<Ethnics> optional = ethnicsRepository.findById(staffDto.getEthnics().getId());
            if (optional.isPresent()) {
                ethnics = optional.get();
            }
            staff.setEthnics(ethnics);
        } else {
            staff.setEthnics(null);
        }
        // set kỹ năng vi tính
        if (staffDto.getComputerSkill() != null) {
            EducationDegree computerSkill = null;
            Optional<EducationDegree> optional = educationDegreeRepository
                    .findById(staffDto.getComputerSkill().getId());
            if (optional.isPresent()) {
                computerSkill = optional.get();
            }
            staff.setComputerSkill(computerSkill);
        } else {
            staff.setComputerSkill(null);
        }
        // set cấp độ ngoại ngữ tiếng anh
        if (staffDto.getEnglishLevel() != null) {
            EducationDegree englishLevel = null;
            Optional<EducationDegree> optional = educationDegreeRepository.findById(staffDto.getEnglishLevel().getId());
            if (optional.isPresent()) {
                englishLevel = optional.get();
            }
            staff.setEnglishLevel(englishLevel);
        } else {
            staff.setEnglishLevel(null);
        }
        // set chứng chỉ tiếng anh
        if (staffDto.getEnglishCertificate() != null) {
            Certificate englishCertificate = null;
            Optional<Certificate> optional = certificateRepository.findById(staffDto.getEnglishCertificate().getId());
            if (optional.isPresent()) {
                englishCertificate = optional.get();
            }
            staff.setEnglishCertificate(englishCertificate);
        } else {
            staff.setEnglishCertificate(null);
        }
        // set xếp hạng học tập
        if (staffDto.getAcademicRank() != null) {
            AcademicTitle academicRank = null;
            Optional<AcademicTitle> optional = academicTitleRepository.findById(staffDto.getAcademicRank().getId());
            if (optional.isPresent()) {
                academicRank = optional.get();
            }
            staff.setAcademicRank(academicRank);
        } else {
            staff.setAcademicRank(null);
        }
        // set bằng cấp
        if (staffDto.getDegree() != null) {
            EducationDegree degree = null;
            Optional<EducationDegree> optional = educationDegreeRepository.findById(staffDto.getDegree().getId());
            if (optional.isPresent()) {
                degree = optional.get();
            }
            staff.setDegree(degree);
        } else {
            staff.setDegree(null);
        }

        // Trình độ học vấn
        if (staffDto.getEducationDegree() != null) {
            EducationDegree educationDegree = educationDegreeRepository.findById(staffDto.getEducationDegree().getId())
                    .orElse(null);
            if (educationDegree == null)
                return null;

            staff.setEducationDegree(educationDegree);
        } else {
            staff.setEducationDegree(null);
        }

        if (staffDto.getOtherLanguage() != null) {
            Language otherLanguage = null;
            Optional<Language> optional = otherLanguageRepository.findById(staffDto.getOtherLanguage().getId());
            if (optional.isPresent()) {
                otherLanguage = optional.get();
            }
            staff.setOtherLanguage(otherLanguage);
        } else {
            staff.setOtherLanguage(null);
        }

        // set quốc tịch
        if (staffDto.getNationality() != null) {
            Country nationality = null;
            Optional<Country> optional = countryRepository.findById(staffDto.getNationality().getId());
            if (optional.isPresent()) {
                nationality = optional.get();
            }
            staff.setNationality(nationality);
        } else {
            staff.setNationality(null);
        }
        // set Email
        if (staffDto.getEmail() != null) {
            staff.setEmail(staffDto.getEmail());
        } else {
            staff.setEmail(null);
        }
        // set công ty/tổ chức
        if (staffDto.getOrganization() != null && staffDto.getOrganization().getId() != null) {
            HrOrganization organization = hrOrganizationRepository.findById(staffDto.getOrganization().getId())
                    .orElse(null);
            if (organization != null)
                staff.setOrganization(organization);
        } else {
            staff.setOrganization(null);
        }
        // set phòng ban
        if (staffDto.getDepartment() != null && staffDto.getDepartment().getId() != null) {
            HRDepartment department = hRDepartmentRepository.findById(staffDto.getDepartment().getId()).orElse(null);
            if (department != null)
                staff.setDepartment(department);
        } else {
            staff.setDepartment(null);
        }
        // vị trí hiện tại
//        if (staffDto.getCurrentPosition() != null && staffDto.getCurrentPosition().getId() != null) {
//            Position currentPosition = positionRepository.findById(staffDto.getCurrentPosition().getId()).orElse(null);
//            if (currentPosition != null) staff.setCurrentPosition(currentPosition);
//        } else {
//            staff.setCurrentPosition(null);
//        }

        // set quê quán
        if (staffDto.getNativeVillage() != null && staffDto.getNativeVillage().getId() != null) {
            AdministrativeUnit nativeVillage = null;
            Optional<AdministrativeUnit> optional = administrativeUnitRepository
                    .findById(staffDto.getNativeVillage().getId());
            if (optional.isPresent()) {
                nativeVillage = optional.get();
            }
            staff.setNativeVillage(nativeVillage);
        } else {
            staff.setNativeVillage(null);
        }

        // set ton giáo
        if (staffDto.getReligion() != null) {
            if (staffDto.getReligion().getId() != null) {
                Religion religion = null;
                Optional<Religion> optional = religionRepository.findById(staffDto.getReligion().getId());
                if (optional.isPresent()) {
                    religion = optional.get();
                }
                staff.setReligion(religion);
            }
        } else {
            staff.setReligion(null);
        }
        // set thông tin bằng cấp
        if (staffDto.getInformaticDegree() != null) {
            InformaticDegree informaticDegree = null;
            Optional<InformaticDegree> optional = informaticDegreeRepository
                    .findById(staffDto.getInformaticDegree().getId());
            if (optional.isPresent()) {
                informaticDegree = optional.get();
            }
            staff.setInformaticDegree(informaticDegree);
        } else {
            staff.setInformaticDegree(null);
        }
        // set
        if (staffDto.getConferred() != null) {
            TitleConferred titleConferred = null;
            Optional<TitleConferred> optional = titleConferredRepository.findById(staffDto.getConferred().getId());
            if (optional.isPresent()) {
                titleConferred = optional.get();
            }
            staff.setConferred(titleConferred);
        } else {
            staff.setConferred(null);
        }
        // set trình độ quản lý giáo dục
        if (staffDto.getEducationalManagementLevel() != null) {
            EducationalManagementLevel educationalManagementLevel = null;
            Optional<EducationalManagementLevel> optional = educationalManagementLevelRepository
                    .findById(staffDto.getEducationalManagementLevel().getId());
            if (optional.isPresent()) {
                educationalManagementLevel = optional.get();
            }
            staff.setEducationalManagementLevel(educationalManagementLevel);
        } else {
            staff.setEducationalManagementLevel(null);
        }
        // set thỏa thuận lao động
        if (staffDto.getLabourAgreementType() != null && staffDto.getLabourAgreementType().getId() != null) {

            LabourAgreementType labourAgreementType = null;
            Optional<LabourAgreementType> optional = labourAgreementTypeRepository
                    .findById(staffDto.getLabourAgreementType().getId());
            if (optional.isPresent()) {
                labourAgreementType = optional.get();
            }
            staff.setLabourAgreementType(labourAgreementType);
        } else {
            staff.setLabourAgreementType(null);
        }

        staff.setIdNumber(staffDto.getIdNumber());
        staff.setIdNumberIssueBy(staffDto.getIdNumberIssueBy());
        staff.setIdNumberIssueDate(staffDto.getIdNumberIssueDate());
        // Hồ sơ nhân sự

        // set tình trạng hôn nhân
        if (staffDto.getMaritalStatus() != null) {
            staff.setMaritalStatus(staffDto.getMaritalStatus());
        } else {
            staff.setMaritalStatus(null);
        }
        // set thời hạn hợp đồng
        if (staffDto.getContractDate() != null) {
            staff.setContractDate(staffDto.getContractDate());
        } else {
            staff.setContractDate(null);
        }
        // set chức vụ
        if (staffDto.getJobTitle() != null) {
            staff.setJobTitle(staffDto.getJobTitle());
        } else {
            staff.setJobTitle(null);
        }
        // set loại công chức
        if (staffDto.getCivilServantType() != null) {
            CivilServantType type = null;
            Optional<CivilServantType> optional = civilServantTypeRepository
                    .findById(staffDto.getCivilServantType().getId());
            if (optional.isPresent()) {
                type = optional.get();
            }
            staff.setCivilServantType(type);
        } else {
            staff.setCivilServantType(null);
        }
        staff.setRecruitmentDate(staffDto.getRecruitmentDate());// set ngày yêu cầu
        staff.setSalaryStartDate(staffDto.getSalaryStartDate());// set ngày bắt đầu nhận lương
        staff.setGraduationYear(staffDto.getGraduationYear());// ngày tốt nghiệp
        staff.setForeignLanguageName(staffDto.getForeignLanguageName());// set tên ngoại ngữ

        // set địa chi
        if (staffDto.getAddress() != null && !staffDto.getAddress().isEmpty()) {
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
        } else {
            if (staff.getAddress() != null) {
                staff.getAddress().clear();
            }
        }
        HashSet<AllowanceSeniorityHistory> allowanceSeniorityHistories = new HashSet<>();
        if (staffDto.getAllowanceSeniorityHistory() != null && !staffDto.getAllowanceSeniorityHistory().isEmpty()) {
            for (AllowanceSeniorityHistoryDto allowanceSeniorityHistoryDto : staffDto.getAllowanceSeniorityHistory()) {
                if (allowanceSeniorityHistoryDto != null) {
                    AllowanceSeniorityHistory allowanceSeniorityHistory = null;
                    if (allowanceSeniorityHistoryDto.getId() != null) {
                        Optional<AllowanceSeniorityHistory> optional = allowanceSeniorityHistoryRepository
                                .findById(allowanceSeniorityHistoryDto.getId());
                        if (optional.isPresent()) {
                            allowanceSeniorityHistory = optional.get();
                        }
                    }
                    if (allowanceSeniorityHistory == null) {
                        allowanceSeniorityHistory = new AllowanceSeniorityHistory();
                    }
                    if (allowanceSeniorityHistoryDto.getQuotaCode() != null
                            && allowanceSeniorityHistoryDto.getQuotaCode().getId() != null) {
                        CivilServantCategory civilServantCategory = null;
                        Optional<CivilServantCategory> optional = civilServantCategoryRepository
                                .findById(allowanceSeniorityHistoryDto.getQuotaCode().getId());
                        if (optional.isPresent()) {
                            civilServantCategory = optional.get();
                        }
                        allowanceSeniorityHistory.setQuotaCode(civilServantCategory);
                    }
                    allowanceSeniorityHistory.setStartDate(allowanceSeniorityHistoryDto.getStartDate());
                    allowanceSeniorityHistory.setNote(allowanceSeniorityHistoryDto.getNote());
                    allowanceSeniorityHistory.setPercentReceived(allowanceSeniorityHistoryDto.getPercentReceived());
                    allowanceSeniorityHistory.setStaff(staff);
                    allowanceSeniorityHistories.add(allowanceSeniorityHistory);
                }
            }
        }
        if (!allowanceSeniorityHistories.isEmpty()) {
            if (staff.getAllowanceSeniorityHistories() == null) {
                staff.setAllowanceSeniorityHistories(allowanceSeniorityHistories);
            } else {
                staff.getAllowanceSeniorityHistories().clear();
                staff.getAllowanceSeniorityHistories().addAll(allowanceSeniorityHistories);
            }
        } else {
            if (staff.getAllowanceSeniorityHistories() != null) {
                staff.getAllowanceSeniorityHistories().clear();
            }
        }

        // set qua trinh chức vụ
        HashSet<PositionStaff> positions = new HashSet<>();
        if (staffDto.getPositions() != null && !staffDto.getPositions().isEmpty()) {
            boolean hasMainPosition = false;
            for (PositionStaffDto psDto : staffDto.getPositions()) {
                if (psDto != null) {
                    PositionStaff newPs = null;
                    if (psDto.getId() != null) {
                        Optional<PositionStaff> optional = positionStaffRepository.findById(psDto.getId());
                        if (optional.isPresent()) {
                            newPs = optional.get();
                        }
                    }
                    if (newPs == null) {
                        newPs = new PositionStaff();
                    }

                    // Đảm bảo chỉ có 1 thằng được set true
                    if (psDto.getMainPosition() != null && psDto.getMainPosition() && !hasMainPosition) {
                        hasMainPosition = true;
                        newPs.setMainPosition(true);
                    } else {
                        newPs.setMainPosition(false);
                    }

                    if (psDto.getPosition() != null && psDto.getPosition().getId() != null) {
                        Position position = null;
                        Optional<Position> optional = positionRepository.findById(psDto.getPosition().getId());
                        if (optional.isPresent()) {
                            position = optional.get();
                        }
                        newPs.setPosition(position);
                    }
                    // set phong ban
                    if (psDto.getHrDepartment() != null && psDto.getHrDepartment().getId() != null) {
                        HRDepartment department = null;
                        Optional<HRDepartment> optional = departmentRepository
                                .findById(psDto.getHrDepartment().getId());
                        if (optional.isPresent()) {
                            department = optional.get();
                        }
                        newPs.setHrDepartment(department);
                    }
                    newPs.setFromDate(psDto.getFromDate());
                    newPs.setToDate(psDto.getToDate());
                    newPs.setRelationshipType(psDto.getRelationshipType());
                    if (psDto.getSupervisor() != null && psDto.getSupervisor().getId() != null) {
                        Staff supervisor = staffRepository.findById(psDto.getSupervisor().getId()).orElse(null);
                        newPs.setSupervisor(supervisor);
                    }
                    newPs.setStaff(staff);
                    positions.add(newPs);
                }
            }
        }
        if (!positions.isEmpty()) {
            if (staff.getPositions() == null) {
                staff.setPositions(positions);
            } else {
                staff.getPositions().clear();
                staff.getPositions().addAll(positions);
            }
        } else {
            if (staff.getPositions() != null) {
                staff.getPositions().clear();
            }
        }


        //Hiện tại đang lưu riêng
        // set quan hệ gia đình
        // tạo một listFamilyRelationship theo kiểu Hashset
//        Set<StaffFamilyRelationship> listFamilyRelationship = new HashSet<>();
//        if (staffDto.getFamilyRelationships() != null && !staffDto.getFamilyRelationships().isEmpty()) {
//            // duyệt các phần tử từ familyRelationship vào
//            // staffFamilyRelationshipDto(StaffFamilyRelationshipDto)
//            for (StaffFamilyRelationshipDto staffFamilyRelationshipDto : staffDto.getFamilyRelationships()) {
//
//                if (staffFamilyRelationshipDto != null) {
//                    StaffFamilyRelationship staffFamilyRelationship = null;
//                    if (staffFamilyRelationshipDto.getId() != null) {
//                        Optional<StaffFamilyRelationship> optional = staffFamilyRelationshipRepository
//                                .findById(staffFamilyRelationshipDto.getId());
//                        if (optional.isPresent()) {
//                            staffFamilyRelationship = optional.get();
//                        }
//                    }
//                    if (staffFamilyRelationship == null) {
//                        staffFamilyRelationship = new StaffFamilyRelationship();
//                    }
//                    if (staffFamilyRelationshipDto.getFamilyRelationship() != null
//                            && staffFamilyRelationshipDto.getFamilyRelationship().getId() != null) {
//                        FamilyRelationship familyRelationship = null;
//                        Optional<FamilyRelationship> optional = familyRelationshipRepository
//                                .findById(staffFamilyRelationshipDto.getFamilyRelationship().getId());
//                        if (optional.isPresent()) {
//                            familyRelationship = optional.get();
//                        }
//                        staffFamilyRelationship.setFamilyRelationship(familyRelationship);
//
//                    }
//                    if (staffFamilyRelationshipDto.getProfession() != null
//                            && staffFamilyRelationshipDto.getProfession().getId() != null) {
//                        Profession profession = null;
//                        Optional<Profession> optional = professionRepository
//                                .findById(staffFamilyRelationshipDto.getProfession().getId());
//                        if (optional.isPresent()) {
//                            profession = optional.get();
//                        }
//                        staffFamilyRelationship.setProfession(profession);
//
//                    }
//                    staffFamilyRelationship.setDescription(staffFamilyRelationshipDto.getDescription());
//                    staffFamilyRelationship.setAddress(staffFamilyRelationshipDto.getAddress());
//                    staffFamilyRelationship.setFullName(staffFamilyRelationshipDto.getFullName());
//                    staffFamilyRelationship.setBirthDate(staffFamilyRelationshipDto.getBirthDate());
//                    staffFamilyRelationship.setWorkingPlace(staffFamilyRelationshipDto.getWorkingPlace());
//                    staffFamilyRelationship.setStaff(staff);
//                    staffFamilyRelationship.setIsDependent(staffFamilyRelationshipDto.getIsDependent());
//                    staffFamilyRelationship.setTaxCode(staffFamilyRelationshipDto.getTaxCode());
//                    listFamilyRelationship.add(staffFamilyRelationship);
//                }
//            }
//        }
//        if (!listFamilyRelationship.isEmpty()) {
//            if (staff.getFamilyRelationships() == null) {
//                staff.setFamilyRelationships(listFamilyRelationship);
//            } else {
//                staff.getFamilyRelationships().clear();
//                staff.getFamilyRelationships().addAll(listFamilyRelationship);
//            }
//        } else {
//            if (staff.getFamilyRelationships() != null) {
//                staff.getFamilyRelationships().clear();
//            }
//        }

        Set<StaffOverseasWorkHistory> ListStaffOverseasWorkHistory = new HashSet<>();
        if (staffDto.getOverseasWorkHistory() != null && !staffDto.getOverseasWorkHistory().isEmpty()) {
            for (StaffOverseasWorkHistoryDto staffOverseasWorkHistoryDto : staffDto.getOverseasWorkHistory()) {
                if (staffOverseasWorkHistoryDto != null) {
                    StaffOverseasWorkHistory staffOverseasWorkHistory = null;
                    if (staffOverseasWorkHistoryDto.getId() != null) {
                        Optional<StaffOverseasWorkHistory> optional = staffOverseasWorkHistoryRepository
                                .findById(staffOverseasWorkHistoryDto.getId());
                        if (optional.isPresent()) {
                            staffOverseasWorkHistory = optional.get();
                        }
                    }
                    if (staffOverseasWorkHistory == null) {
                        staffOverseasWorkHistory = new StaffOverseasWorkHistory();
                    }
                    if (staffOverseasWorkHistoryDto.getCountry() != null
                            && staffOverseasWorkHistoryDto.getCountry().getId() != null) {
                        Country country = null;
                        Optional<Country> optional = countryRepository
                                .findById(staffOverseasWorkHistoryDto.getCountry().getId());
                        if (optional.isPresent()) {
                            country = optional.get();
                        }
                        staffOverseasWorkHistory.setCountry(country);
                    }
                    staffOverseasWorkHistory.setStartDate(staffOverseasWorkHistoryDto.getStartDate());
                    staffOverseasWorkHistory.setEndDate(staffOverseasWorkHistoryDto.getEndDate());
                    staffOverseasWorkHistory.setDecisionDate(staffOverseasWorkHistoryDto.getDecisionDate());
                    staffOverseasWorkHistory.setPurpose(staffOverseasWorkHistoryDto.getPurpose());
                    staffOverseasWorkHistory.setCompanyName(staffOverseasWorkHistoryDto.getCompanyName());
                    staffOverseasWorkHistory.setDecisionNumber(staffOverseasWorkHistoryDto.getDecisionNumber());
                    staffOverseasWorkHistory.setStaff(staff);
                    ListStaffOverseasWorkHistory.add(staffOverseasWorkHistory);
                }
            }
        }
        if (!ListStaffOverseasWorkHistory.isEmpty()) {
            if (staff.getOverseasWorkHistory() == null) {
                staff.setOverseasWorkHistory(ListStaffOverseasWorkHistory);
            } else {
                staff.getOverseasWorkHistory().clear();
                staff.getOverseasWorkHistory().addAll(ListStaffOverseasWorkHistory);
            }
        } else {
            if (staff.getOverseasWorkHistory() != null) {
                staff.getOverseasWorkHistory().clear();
            }
        }
        // set quá trình khen thưởng
        Set<StaffRewardHistory> ListStaffRewardHistory = new HashSet<>();
        if (staffDto.getRewardHistory() != null && !staffDto.getRewardHistory().isEmpty()) {
            for (StaffRewardHistoryDto staffRewardHistoryDto : staffDto.getRewardHistory()) {
                if (staffRewardHistoryDto != null) {
                    StaffRewardHistory staffRewardHistory = null;
                    if (staffRewardHistoryDto.getId() != null) {
                        Optional<StaffRewardHistory> optional = staffRewardHistoryRepository
                                .findById(staffRewardHistoryDto.getId());
                        if (optional.isPresent()) {
                            staffRewardHistory = optional.get();
                        }
                    }
                    if (staffRewardHistory == null) {
                        staffRewardHistory = new StaffRewardHistory();
                    }
                    if (staffRewardHistoryDto.getRewardType() != null
                            && staffRewardHistoryDto.getRewardType().getId() != null) {
                        RewardForm rewardType = null;
                        Optional<RewardForm> optional = rewardFormRepository
                                .findById(staffRewardHistoryDto.getRewardType().getId());
                        if (optional.isPresent()) {
                            rewardType = optional.get();
                        }
                        staffRewardHistory.setRewardType(rewardType);
                    }
                    if (staffRewardHistoryDto.getOrganization() != null
                            && staffRewardHistoryDto.getOrganization().getId() != null) {
                        HrOrganization organization = null;
                        Optional<HrOrganization> optional = organizationRepository
                                .findById(staffRewardHistoryDto.getOrganization().getId());
                        if (optional.isPresent()) {
                            organization = optional.get();
                        }
                        staffRewardHistory.setOrganization(organization);
                    }
                    staffRewardHistory.setOrganizationName(staffRewardHistoryDto.getOrganizationName());
                    staffRewardHistory.setRewardDate(staffRewardHistoryDto.getRewardDate());
                    staffRewardHistory.setStaff(staff);
                    ListStaffRewardHistory.add(staffRewardHistory);
                }
            }
        }
        if (!ListStaffRewardHistory.isEmpty()) {
            if (staff.getStaffRewardHistories() == null) {
                staff.setStaffRewardHistories(ListStaffRewardHistory);
            } else {
                staff.getStaffRewardHistories().clear();
                staff.getStaffRewardHistories().addAll(ListStaffRewardHistory);
            }
        } else {
            if (staff.getStaffRewardHistories() != null) {
                staff.getStaffRewardHistories().clear();
            }
        }
        // set qua trinh thai san
//        Set<StaffMaternityHistory> ListStaffMaternityHistory = new HashSet<>();
//        if (staffDto.getMaternityHistory() != null && staffDto.getMaternityHistory().size() > 0) {
//            for (StaffMaternityHistoryDto staffMaternityHistoryDto : staffDto.getMaternityHistory()) {
//                if (staffMaternityHistoryDto != null) {
//                    StaffMaternityHistory staffMaternityHistory = null;
//                    if (staffMaternityHistoryDto.getId() != null) {
//                        Optional<StaffMaternityHistory> optional = staffMaternityHistoryRepository
//                                .findById(staffMaternityHistoryDto.getId());
//                        if (optional.isPresent()) {
//                            staffMaternityHistory = optional.get();
//                        }
//                    }
//                    if (staffMaternityHistory == null) {
//                        staffMaternityHistory = new StaffMaternityHistory();
//                    }
//                    staffMaternityHistory.setStartDate(staffMaternityHistoryDto.getStartDate());
//                    staffMaternityHistory.setEndDate(staffMaternityHistoryDto.getEndDate());
//                    staffMaternityHistory.setBirthNumber(staffMaternityHistoryDto.getBirthNumber());
//                    staffMaternityHistory.setNote(staffMaternityHistoryDto.getNote());
//                    staffMaternityHistory.setStaff(staff);
//                    ListStaffMaternityHistory.add(staffMaternityHistory);
//                }
//            }
//        }
//        if (ListStaffMaternityHistory.size() > 0) {
//            if (staff.getStaffMaternityHistories() == null) {
//                staff.setStaffMaternityHistories(ListStaffMaternityHistory);
//            } else {
//                staff.getStaffMaternityHistories().clear();
//                staff.getStaffMaternityHistories().addAll(ListStaffMaternityHistory);
//            }
//        } else {
//            if (staff.getStaffMaternityHistories() != null) {
//                staff.getStaffMaternityHistories().clear();
//            }
//        }
        // quá trình lương
        Set<StaffSalaryHistory> ListStaffSalaryHistory = new HashSet<>();
        if (staffDto.getSalaryHistory() != null && staffDto.getSalaryHistory().size() > 0) {
            for (StaffSalaryHistoryDto staffSalaryHistoryDto : staffDto.getSalaryHistory()) {
                if (staffSalaryHistoryDto != null) {
                    StaffSalaryHistory staffSalaryHistory = null;
                    if (staffSalaryHistoryDto.getId() != null) {
                        Optional<StaffSalaryHistory> optional = staffSalaryHistoryRepository
                                .findById(staffSalaryHistoryDto.getId());
                        if (optional.isPresent()) {
                            staffSalaryHistory = optional.get();
                        }
                    }
                    if (staffSalaryHistory == null) {
                        staffSalaryHistory = new StaffSalaryHistory();
                    }
                    staffSalaryHistory.setDecisionCode(staffSalaryHistoryDto.getDecisionCode());
                    staffSalaryHistory.setDecisionDate(staffSalaryHistoryDto.getDecisionDate());
                    staffSalaryHistory.setCoefficient(staffSalaryHistoryDto.getCoefficient());
                    staffSalaryHistory.setCoefficientOverLevel(staffSalaryHistoryDto.getCoefficientOverLevel());
                    staffSalaryHistory.setPercentage(staffSalaryHistoryDto.getPercentage());
                    staffSalaryHistory.setStaffTypeCode(staffSalaryHistoryDto.getStaffTypeCode());
                    if (staffSalaryHistoryDto.getSalaryIncrementType() != null) {
                        SalaryIncrementType salaryType = null;
                        Optional<SalaryIncrementType> optional = salaryIncrementTypeRepository
                                .findById(staffSalaryHistoryDto.getSalaryIncrementType().getId());
                        if (optional.isPresent()) {
                            salaryType = optional.get();
                        }
                        staffSalaryHistory.setSalaryIncrementType(salaryType);

                    }

                    staffSalaryHistory.setStaff(staff);
                    ListStaffSalaryHistory.add(staffSalaryHistory);
                }
            }
        }
        if (!ListStaffSalaryHistory.isEmpty()) {
            if (staff.getSalaryHistory() == null) {
                staff.setSalaryHistory(ListStaffSalaryHistory);
            } else {
                staff.getSalaryHistory().clear();
                staff.getSalaryHistory().addAll(ListStaffSalaryHistory);
            }
        } else {
            if (staff.getSalaryHistory() != null) {
                staff.getSalaryHistory().clear();
            }
        }
//        // quá trình đóng bảo hiểm xã hội
//        Set<StaffInsuranceHistory> ListStaffInsuranceHistory = new HashSet<>();
//        if (staffDto.getStafInsuranceHistory() != null && staffDto.getStafInsuranceHistory().size() > 0) {
//            for (StaffInsuranceHistoryDto staffInsuranceHistoryDto : staffDto.getStafInsuranceHistory()) {
//                if (staffInsuranceHistoryDto != null) {
//                    StaffInsuranceHistory staffInsuranceHistory = null;
//                    if (staffInsuranceHistoryDto.getId() != null) {
//                        Optional<StaffInsuranceHistory> optional = staffInsuranceHistoryRepository
//                                .findById(staffInsuranceHistoryDto.getId());
//                        if (optional.isPresent()) {
//                            staffInsuranceHistory = optional.get();
//                        }
//                    }
//                    if (staffInsuranceHistory == null) {
//                        staffInsuranceHistory = new StaffInsuranceHistory();
//                    }
//                    staffInsuranceHistory.setStartDate(staffInsuranceHistoryDto.getStartDate());
//                    staffInsuranceHistory.setEndDate(staffInsuranceHistoryDto.getEndDate());
//                    staffInsuranceHistory.setNote(staffInsuranceHistoryDto.getNote());
//                    staffInsuranceHistory.setSalaryCofficient(staffInsuranceHistoryDto.getSalaryCofficient());
//                    staffInsuranceHistory.setInsuranceSalary(staffInsuranceHistoryDto.getInsuranceSalary());
//                    staffInsuranceHistory.setStaffPercentage(staffInsuranceHistoryDto.getStaffPercentage());
//                    staffInsuranceHistory.setOrgPercentage(staffInsuranceHistoryDto.getOrgPercentage());
//                    staffInsuranceHistory.setStaffInsuranceAmount(staffInsuranceHistoryDto.getStaffInsuranceAmount());
//                    staffInsuranceHistory.setOrgInsuranceAmount(staffInsuranceHistoryDto.getOrgInsuranceAmount());
//                    staffInsuranceHistory.setStaff(staff);
//                    ListStaffInsuranceHistory.add(staffInsuranceHistory);
//                }
//            }
//        }
//        if (ListStaffInsuranceHistory.size() > 0) {
//            if (staff.getStafInsuranceHistory() == null) {
//                staff.setStafInsuranceHistory(ListStaffInsuranceHistory);
//            } else {
//                staff.getStafInsuranceHistory().clear();
//                staff.getStafInsuranceHistory().addAll(ListStaffInsuranceHistory);
//            }
//        } else {
//            if (staff.getStafInsuranceHistory() != null) {
//                staff.getStafInsuranceHistory().clear();
//            }
//        }
        // quá trình đóng bảo hiểm xã hội
//        Set<StaffSocialInsurance> staffSocialInsuranceSet = new HashSet<>();
//
//        if (staffDto.getStaffSocialInsurance() != null && !staffDto.getStaffSocialInsurance().isEmpty()) {
//            for (StaffSocialInsuranceDto dto : staffDto.getStaffSocialInsurance()) {
//                if (dto == null) continue;
//
//                StaffSocialInsurance staffSocialInsurance;
//
//                if (dto.getId() != null) {
//                    staffSocialInsurance = staffSocialInsuranceRepository.findById(dto.getId()).orElse(new StaffSocialInsurance());
//                } else {
//                    staffSocialInsurance = new StaffSocialInsurance();
//                }
//
//                staffSocialInsurance.setStaff(staff);
//                staffSocialInsurance.setInsuranceSalary(dto.getInsuranceSalary());
//                staffSocialInsurance.setStaffPercentage(dto.getStaffPercentage());
//                staffSocialInsurance.setStaffInsuranceAmount(dto.getStaffInsuranceAmount());
//                staffSocialInsurance.setOrgPercentage(dto.getOrgPercentage());
//                staffSocialInsurance.setTotalInsuranceAmount(dto.getTotalInsuranceAmount());
//                staffSocialInsurance.setOrgInsuranceAmount(dto.getOrgInsuranceAmount());
//                staffSocialInsurance.setPaidStatus(dto.getPaidStatus());
//                staffSocialInsurance.setNote(dto.getNote());
//                staffSocialInsurance.setSalaryCoefficient(dto.getSalaryCoefficient());
//                staffSocialInsurance.setStartDate(dto.getStartDate());
//                staffSocialInsurance.setEndDate(dto.getEndDate());
//
//                // Set SalaryResult if available
//                if (dto.getSalaryResult() != null && dto.getSalaryResult().getId() != null) {
//                    staffSocialInsurance.setSalaryResult(
//                            salaryResultRepository.findById(dto.getSalaryResult().getId()).orElse(null)
//                    );
//                }
//
//                // Set SalaryPeriod if available
//                if (dto.getSalaryPeriod() != null && dto.getSalaryPeriod().getId() != null) {
//                    staffSocialInsurance.setSalaryPeriod(
//                            salaryPeriodRepository.findById(dto.getSalaryPeriod().getId()).orElse(null)
//                    );
//                }
//
//                staffSocialInsuranceSet.add(staffSocialInsurance);
//            }
//        }
//
//        if (!staffSocialInsuranceSet.isEmpty()) {
//            if (staff.getStaffSocialInsurances() == null) {
//                staff.setStaffSocialInsurances(staffSocialInsuranceSet);
//            } else {
//                staff.getStaffSocialInsurances().clear();
//                staff.getStaffSocialInsurances().addAll(staffSocialInsuranceSet);
//            }
//        } else if (staff.getStaffSocialInsurances() != null) {
//            staff.getStaffSocialInsurances().clear();
//        }

        //Hiện tại lưu riêng api
        // trình độ học vấn
//        Set<PersonCertificate> personCertificates = new HashSet<>();
//        if (staffDto.getPersonCertificate() != null && staffDto.getPersonCertificate().size() > 0) {
//            for (PersonCertificateDto personCertificateDto : staffDto.getPersonCertificate()) {
//                if (personCertificateDto != null) {
//                    PersonCertificate personCertificate = null;
//                    if (personCertificateDto.getId() != null) {
//                        Optional<PersonCertificate> optional = personCertificateRepository
//                                .findById(personCertificateDto.getId());
//                        if (optional.isPresent()) {
//                            personCertificate = optional.get();
//                        }
//                    }
//                    if (personCertificate == null) {
//                        personCertificate = new PersonCertificate();
//                    }
//                    if (personCertificateDto.getCertificate() != null
//                            && personCertificateDto.getCertificate().getId() != null) {
//                        Certificate certificate = null;
//                        Optional<Certificate> optional = certificateRepository
//                                .findById(personCertificateDto.getCertificate().getId());
//                        if (optional.isPresent()) {
//                            certificate = optional.get();
//                        }
//                        personCertificate.setCertificate(certificate);
//                    }
//                    personCertificate.setLevel(personCertificateDto.getLevel());
//                    personCertificate.setIssueDate(personCertificateDto.getIssueDate());
//                    personCertificate.setName(personCertificateDto.getName());
//                    personCertificate.setPerson(staff);
//                    personCertificates.add(personCertificate);
//                }
//            }
//        }
//        if (!personCertificates.isEmpty()) {
//            if (staff.getPersonCertificate() == null) {
//                staff.setPersonCertificate(personCertificates);
//            } else {
//                staff.getPersonCertificate().clear();
//                staff.getPersonCertificate().addAll(personCertificates);
//            }
//        } else {
//            if (staff.getPersonCertificate() != null) {
//                staff.getPersonCertificate().clear();
//            }
//        }
//
        // set qua trinh phu cap
//        Set<StaffAllowanceHistory> staffAllowanceHistories = new HashSet<>();
//        if (staffDto.getAllowanceHistory() != null && staffDto.getAllowanceHistory().size() > 0) {
//            for (StaffAllowanceHistoryDto staffAllowanceHistoryDto : staffDto.getAllowanceHistory()) {
//                if (staffAllowanceHistoryDto != null) {
//                    StaffAllowanceHistory staffAllowanceHistory = null;
//                    if (staffAllowanceHistoryDto.getId() != null) {
//                        Optional<StaffAllowanceHistory> optional = staffAllowanceHistoryRepository
//                                .findById(staffAllowanceHistoryDto.getId());
//                        if (optional.isPresent()) {
//                            staffAllowanceHistory = optional.get();
//                        }
//                    }
//                    if (staffAllowanceHistory == null) {
//                        staffAllowanceHistory = new StaffAllowanceHistory();
//                    }
//                    if (staffAllowanceHistoryDto.getAllowanceType() != null
//                            && staffAllowanceHistoryDto.getAllowanceType().getId() != null) {
//                        AllowanceType allowanceType = null;
//                        Optional<AllowanceType> optional = allowanceTypeRepository
//                                .findById(staffAllowanceHistoryDto.getAllowanceType().getId());
//                        if (optional.isPresent()) {
//                            allowanceType = optional.get();
//                        }
//                        staffAllowanceHistory.setAllowanceType(allowanceType);
//                    }
//                    staffAllowanceHistory.setStartDate(staffAllowanceHistoryDto.getStartDate());
//                    staffAllowanceHistory.setEndDate(staffAllowanceHistoryDto.getEndDate());
//                    staffAllowanceHistory.setNote(staffAllowanceHistoryDto.getNote());
//                    staffAllowanceHistory.setCoefficient(staffAllowanceHistoryDto.getCoefficient());
//                    staffAllowanceHistory.setStaff(staff);
//                    staffAllowanceHistories.add(staffAllowanceHistory);
//                }
//            }
//        }
//        if (staffAllowanceHistories.size() > 0) {
//            if (staff.getStaffAllowanceHistories() == null) {
//                staff.setStaffAllowanceHistories(staffAllowanceHistories);
//            } else {
//                staff.getStaffAllowanceHistories().clear();
//                staff.getStaffAllowanceHistories().addAll(staffAllowanceHistories);
//            }
//        } else {
//            if (staff.getStaffAllowanceHistories() != null) {
//                staff.getStaffAllowanceHistories().clear();
//            }
//        }
        // set qua trinh boi duong
        Set<StaffTrainingHistory> staffTrainingHistories = new HashSet<>();
        if (staffDto.getTrainingHistory() != null && staffDto.getTrainingHistory().size() > 0) {
            for (StaffTrainingHistoryDto staffTrainingHistoryDto : staffDto.getTrainingHistory()) {
                if (staffTrainingHistoryDto != null) {
                    StaffTrainingHistory staffTrainingHistory = null;
                    if (staffTrainingHistoryDto.getId() != null) {
                        Optional<StaffTrainingHistory> optional = staffTrainingHistoryRepository
                                .findById(staffTrainingHistoryDto.getId());
                        if (optional.isPresent()) {
                            staffTrainingHistory = optional.get();
                        }
                    }
                    if (staffTrainingHistory == null) {
                        staffTrainingHistory = new StaffTrainingHistory();
                    }
                    if (staffTrainingHistoryDto.getTrainingCountry() != null
                            && staffTrainingHistoryDto.getTrainingCountry().getId() != null) {
                        Country country = null;
                        Optional<Country> optional = countryRepository
                                .findById(staffTrainingHistoryDto.getTrainingCountry().getId());
                        if (optional.isPresent()) {
                            country = optional.get();
                        }
                        staffTrainingHistory.setTrainingCountry(country);
                    }
                    if (staffTrainingHistoryDto.getCertificate() != null
                            && staffTrainingHistoryDto.getCertificate().getId() != null) {
                        Certificate certificate = null;
                        Optional<Certificate> optional = certificateRepository
                                .findById(staffTrainingHistoryDto.getCertificate().getId());
                        if (optional.isPresent()) {
                            certificate = optional.get();
                        }
                        staffTrainingHistory.setCertificate(certificate);
                    }
                    staffTrainingHistory.setStartDate(staffTrainingHistoryDto.getStartDate());
                    staffTrainingHistory.setEndDate(staffTrainingHistoryDto.getEndDate());
                    staffTrainingHistory.setTrainingPlace(staffTrainingHistoryDto.getTrainingPlace());
                    staffTrainingHistory.setTrainingContent(staffTrainingHistoryDto.getTrainingContent());
                    staffTrainingHistory.setStaff(staff);
                    staffTrainingHistories.add(staffTrainingHistory);
                }
            }
        }
        if (!staffTrainingHistories.isEmpty()) {
            if (staff.getStaffTrainingHistories() == null) {
                staff.setStaffTrainingHistories(staffTrainingHistories);
            } else {
                staff.getStaffTrainingHistories().clear();
                staff.getStaffTrainingHistories().addAll(staffTrainingHistories);
            }
        } else {
            if (staff.getStaffTrainingHistories() != null) {
                staff.getStaffTrainingHistories().clear();
            }
        }
        // Add Asset
//        List<AssetDto> assets = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(staffDto.getAssets())) {
//            for (AssetDto assetDto : staffDto.getAssets()) {
//                StaffDto staffAsset = new StaffDto();
//                staffAsset.setId(staff.getId());
//                assetDto.setStaff(staffAsset);
//                assetDto = assetService.saveAsset(assetDto);
//                assets.add(assetDto);
//            }
//        } else {
//            List<AssetDto> assetByStaff = assetService.getListByStaff(staff.getId());
//            for (AssetDto assetDto : assetByStaff) {
//                assetService.deleteAsset(assetDto.getId());
//            }
//        }

        // TODO: save staffLabourAgreement V2
//        Set<StaffLabourAgreement> savedLabourAgreements = new HashSet<>();
//        if (staffDto.getAgreements() != null && !staffDto.getAgreements().isEmpty()) {
//            for (StaffLabourAgreementDto agreementDto : staffDto.getAgreements()) {
//                //FE doesn't submit field staff
//                if (agreementDto.getStaff() == null || agreementDto.getStaff().getId() == null) {
//                    StaffDto createForStaff = new StaffDto();
//                    createForStaff.setId(staff.getId());
//
//                    agreementDto.setStaff(createForStaff);
//                }
//                StaffLabourAgreementDto savedItem = staffLabourAgreementService.saveOrUpdate(agreementDto);
//                if (savedItem == null) {
//                    System.out.println("Error when save labour agreement");
//                    continue;
//                }
//                StaffLabourAgreement agreementItem = staffLabourAgreementRepository.findById(savedItem.getId()).orElse(null);
//                savedLabourAgreements.add(agreementItem);
//            }
//            if (staff.getAgreements() != null) {
//                staff.getAgreements().clear();
//            } else {
//                staff.setAgreements(new HashSet<StaffLabourAgreement>());
//            }
//            staff.getAgreements().addAll(savedLabourAgreements);
//        } else if (staff.getAgreements() != null) {
//            staff.getAgreements().clear();
//        }

        // nguoi gioi thieu lam viec
        if (staffDto.getIntroducer() != null && staffDto.getIntroducer().getId() != null) {
            Staff introducer = repository.findById(staffDto.getIntroducer().getId()).orElse(null);
            staff.setIntroducer(introducer);
        } else {
            staff.setIntroducer(null);
        }
        // nguoi quyet dinh tuyen dung
        if (staffDto.getRecruiter() != null && staffDto.getRecruiter().getId() != null) {
            Staff recruiter = repository.findById(staffDto.getRecruiter().getId()).orElse(null);
            staff.setRecruiter(recruiter);
        } else {
            staff.setRecruiter(null);
        }

        staff.setStaffWorkingFormat(staffDto.getStaffWorkingFormat());
        staff.setCompanyEmail(staffDto.getCompanyEmail());
        staff.setStaffPhase(staffDto.getStaffPhase());
        staff.setContactPersonInfo(staffDto.getContactPersonInfo());
        staff.setSocialInsuranceNote(staffDto.getSocialInsuranceNote());
        staff.setDesireRegistrationHealthCare(staffDto.getDesireRegistrationHealthCare());
        staff.setStartDate(staffDto.getStartDate()); // set ngày bắt đầu
        if (staffDto.getRequireAttendance() == null || !staffDto.getRequireAttendance()) {
            staff.setRequireAttendance(false);
        } else {
            staff.setRequireAttendance(true);
        }
        staff.setAnnualLeaveDays(HrConstants.BASE_ANNUAL_LEAVE_DAYS);
        staff = staffRepository.save(staff);

        StaffDto result = new StaffDto(staff);
//        result.setAssets(assets);

        // handle set field current insurance salary for display
        this.handleSetValueForCurrentInsuranceSalary(staff.getId());

        return result;
    }

    @Override
    public void handleSetValueForCurrentInsuranceSalary(UUID staffId) {
        if (staffId == null)
            return;

        Staff staff = staffRepository.findById(staffId).orElse(null);
        if (staff == null || staff.getAgreements() == null || staff.getAgreements().isEmpty()) {
            return;
        }

        Date currentDate = new Date();
        StaffLabourAgreement currentValidAgreement = staff.getAgreements().stream()
                .filter(agreement -> agreement.getStartDate() != null && agreement.getEndDate() != null
                        && !currentDate.before(agreement.getStartDate()) // currentDate >= startDate
                        && !currentDate.after(agreement.getEndDate())) // currentDate <= endDate
                .max(Comparator.comparing(StaffLabourAgreement::getEndDate)) // Get the one with the longest endDate
                .orElse(null);

        if (currentValidAgreement == null) {
            staff.setInsuranceSalary(0.0);
        } else {
            staff.setInsuranceSalary(currentValidAgreement.getInsuranceSalary());

            if (staff.getInsuranceSalary() == null) {
                staff.setInsuranceSalary(0.0);
            }
        }

        Double staffInsuranceAmount = 0.0;
        if (staff.getStaffPercentage() == null) {
            staff.setStaffPercentage(10.5);
        }
        staffInsuranceAmount = (staff.getStaffPercentage() * staff.getInsuranceSalary()) / 100;
        staff.setStaffInsuranceAmount(staffInsuranceAmount);

        Double orgInsuranceAmount = 0.0;
        if (staff.getOrgPercentage() == null) {
            staff.setOrgPercentage(21.5);
        }
        orgInsuranceAmount = (staff.getOrgPercentage() * staff.getInsuranceSalary()) / 100;
        staff.setOrgInsuranceAmount(orgInsuranceAmount);

        staff.setTotalInsuranceAmount(staffInsuranceAmount + orgInsuranceAmount);

        staffRepository.save(staff);

    }

    // lấy nhân viên theo tháng / năm
    @Override
    public List<StaffDto> findStaffsHaveBirthDayByMonth(int month) {
        List<StaffDto> staffList = staffRepository.findStaffsHaveBirthDayByMonth(month);

        return staffList;
    }

    @Override
    public List<StaffDto> findBySalaryTemplatePeriod(SearchStaffDto searchDto) {
        if (searchDto == null) {
            return null;
        }
        if (searchDto.getSalaryTemplateId() == null || searchDto.getSalaryPeriodId() == null) {
            return null;
        }
        List<StaffDto> staffList = staffRepository.findBySalaryTemplatePeriod(searchDto.getSalaryTemplateId(),
                searchDto.getSalaryPeriodId());

        return staffList;
    }

    @Override
    public void exportHICInfoToWord(HttpServletResponse response, UUID staffId) throws IOException {
        // Mở tài liệu Word hiện có
        org.springframework.core.io.Resource resource = resourceLoader
                .getResource("classpath:TK1_TS2018-09-25-10-07-23-AM.docx");
        InputStream inputStream = resource.getInputStream();
        XWPFDocument document = new XWPFDocument(inputStream);
        List<XWPFParagraph> paragraphList = document.getParagraphs();
        StaffDto staff = this.getStaff(staffId);
        if (staff == null)
            return;
        String[] targetText = new String[]{};
        String[] replacements = new String[]{};
        if (!StringUtils.hasText(staff.getSocialInsuranceNumber())) {
            targetText = new String[]{"[01]. Họ và tên (viết chữ in hoa): …………………………………………………………",
                    "[02]. Ngày, tháng, năm sinh: ………/………/…………… [03]. Giới tính: …………………",
                    "[04]. Quốc tịch ………………………… [05]. Dân tộc ………………………………………",
                    "[06]. Nơi đăng ký giấy khai sinh: [06.1]. Xã (phường, thị trấn): ………………………………",
                    "[06.2]. Huyện (quận, thị xã, Tp thuộc tỉnh): …………………… [06.3]. Tỉnh (Tp): ……………",
                    "[07]. Địa chỉ nhận kết quả: [07.1]. Số nhà, đường phố, thôn xóm: .…….…………………….",
                    "[07.2]. Xã (phường, thị trấn): ..……..………………. [07.3] Huyện (quận, thị xã, Tp thuộc tỉnh): .………..……………………………… [07.4].Tỉnh (Tp): …………….……………………",
                    "[08]. Số CMND/ Hộ chiếu/ Thẻ căn cước: ………….[09]. Số điện thoại liên hệ:……………",
                    "[10]. Họ tên cha/ mẹ/ người giám hộ (đối với trẻ em dưới 6 tuổi): …………………………..",
                    "[11]. Mức tiền đóng: …………….…… [12]. Phương thức đóng: …..…….…………………",
                    "[13]. Nơi đăng ký khám bệnh, chữa bệnh ban đầu (không áp dụng đối với người tham gia BHXH tự nguyện): …………………..……………………….",};
            replacements = new String[]{
                    "[01]. Họ và tên (viết chữ in hoa): " + (StringUtils.hasText(staff.getDisplayName())
                            ? staff.getDisplayName().toUpperCase(Locale.ROOT)
                            : WordUtil.REPLACE_EMPTY_STRING),
                    "[02]. Ngày, tháng, năm sinh: " + DateTimeUtil.customFormatDate(staff.getBirthDate(), "dd/MM/yyyy")
                            + " [03]. Giới tính: " + Const.GENDER_ENUM.getDisplay(staff.getGender()),
                    "[04]. Quốc tịch "
                            + (staff.getNationality() != null ? staff.getNationality().getName()
                            : WordUtil.REPLACE_EMPTY_STRING)
                            + " [05]. Dân tộc "
                            + (staff.getEthnics() != null ? staff.getEthnics().getName()
                            : WordUtil.REPLACE_EMPTY_STRING),
                    "[06]. Nơi đăng ký giấy khai sinh: " + Const.checkString(staff.getBirthPlace())
                            + " [06.1]. Xã (phường, thị trấn): "
                            + (staff.getAdministrativeunit() != null ? staff.getAdministrativeunit().getName()
                            : WordUtil.REPLACE_EMPTY_STRING),
                    "[06.2]. Huyện (quận, thị xã, Tp thuộc tỉnh): "
                            + (staff.getDistrict() != null ? staff.getDistrict().getName()
                            : WordUtil.REPLACE_EMPTY_STRING)
                            + " [06.3]. Tỉnh (Tp): "
                            + (staff.getProvince() != null ? staff.getProvince().getName()
                            : WordUtil.REPLACE_EMPTY_STRING),
                    "[07]. Địa chỉ nhận kết quả: [07.1]. Số nhà, đường phố, thôn xóm: .…….…………………….",
                    "[07.2]. Xã (phường, thị trấn): ..……..………………. [07.3] Huyện (quận, thị xã, Tp thuộc tỉnh): .………..……………………………… [07.4].Tỉnh (Tp): …………….……………………",
                    "[08]. Số CMND/ Hộ chiếu/ Thẻ căn cước: "
                            + (StringUtils.hasText(staff.getIdNumber()) ? staff.getIdNumber()
                            : WordUtil.REPLACE_EMPTY_STRING)
                            + " [09]. Số điện thoại liên hệ: "
                            + (StringUtils.hasText(staff.getPhoneNumber()) ? staff.getPhoneNumber()
                            : WordUtil.REPLACE_EMPTY_STRING),
                    "[10]. Họ tên cha/ mẹ/ người giám hộ (đối với trẻ em dưới 6 tuổi): …………………………..",
                    "[11]. Mức tiền đóng: …………….…… [12]. Phương thức đóng: …..…….…………………",
                    "[13]. Nơi đăng ký khám bệnh, chữa bệnh ban đầu (không áp dụng đối với người tham gia BHXH tự nguyện): …………………..………………………."};
        } else {
            targetText = new String[]{"[01]. Họ và tên (viết chữ in hoa): …………………………………………………………….",
                    "[02]. Ngày, tháng, năm sinh: ……/……/……… [03]. Mã số BHXH: ……………………….",
                    "[04]. Nội dung thay đổi, yêu cầu: ……………………………………………………………..",
                    "[05]. Hồ sơ kèm theo (nếu có): ……………………………………………..…………………"};
            replacements = new String[]{
                    "[01]. Họ và tên (viết chữ in hoa): " + (StringUtils.hasText(staff.getDisplayName())
                            ? staff.getDisplayName().toUpperCase(Locale.ROOT)
                            : WordUtil.REPLACE_EMPTY_STRING),
                    "[02]. Ngày, tháng, năm sinh: " + DateTimeUtil.customFormatDate(staff.getBirthDate(), "dd/MM/yyyy")
                            + " [03]. Mã số BHXH: "
                            + (StringUtils.hasText(staff.getSocialInsuranceNumber()) ? staff.getSocialInsuranceNumber()
                            : WordUtil.REPLACE_EMPTY_STRING),
                    "[04]. Nội dung thay đổi, yêu cầu: ……………………………………………………………..",
                    "[05]. Hồ sơ kèm theo (nếu có): ……………………………………………..…………………"};
        }
        WordUtil.replaceParagraph(paragraphList, targetText, replacements);
        XWPFTable table = document.getTableArray(2); // Get the first table
        // Modify the first row (after header)
        int indexRow = 2;
        int no = 1;
        for (StaffFamilyRelationshipDto familyMember : staff.getFamilyRelationships()) { // Dùng iterator đã lưu trữ để
            // kiểm tra phần tử tiếp
            // theo
            XWPFTableRow row = table.getRow(indexRow++);
            if (row == null)
                row = table.createRow(); // Tạo dòng mới nếu không có dòng nào
            int columnIndex = 0;
            // Thêm nội dung và set style cho ô
            // STT
            XWPFTableCell cellNo = row.getCell(columnIndex++);
            WordUtil.setCellTextAndStyle(cellNo, String.valueOf(no++));
            // Họ và tên
            XWPFTableCell cellFullName = row.getCell(columnIndex++);
            WordUtil.setCellTextAndStyle(cellFullName, familyMember.getFullName());
            // Số bảo hiểm xã hội
            XWPFTableCell cellSocialInsuranceNumber = row.getCell(columnIndex++);
            WordUtil.setCellTextAndStyle(cellSocialInsuranceNumber,
                    familyMember.getStaff() != null ? familyMember.getStaff().getSocialInsuranceNumber() : "");
            // Ngày sinh
            XWPFTableCell cellBirthDate = row.getCell(columnIndex++);
            WordUtil.setCellTextAndStyle(cellBirthDate,
                    DateTimeUtil.customFormatDate(familyMember.getBirthDate(), "dd/MM/yyyy"));
            // Giới tính
            XWPFTableCell cellGender = row.getCell(columnIndex++);
            WordUtil.setCellTextAndStyle(cellGender,
                    familyMember.getStaff() != null ? Const.GENDER_ENUM.getDisplay(familyMember.getStaff().getGender())
                            : "");
            // Địa chỉ
            XWPFTableCell cellAddress = row.getCell(columnIndex++);
            WordUtil.setCellTextAndStyle(cellAddress, familyMember.getAddress());
            // Quan hệ gia đình
            XWPFTableCell cellFamilyRelationship = row.getCell(columnIndex++);
            WordUtil.setCellTextAndStyle(cellFamilyRelationship,
                    familyMember.getFamilyRelationship() != null ? familyMember.getFamilyRelationship().getName() : "");
            // Số CMND
            XWPFTableCell cellIdNumber = row.getCell(columnIndex);
            WordUtil.setCellTextAndStyle(cellIdNumber,
                    familyMember.getStaff() != null ? Const.checkString(familyMember.getStaff().getIdNumber()) : "");
        }

        // Lưu tài liệu đã chỉnh sửa ra file mới
        // Cài đặt các header để người dùng tải file về dưới dạng .docx
        // Ghi file Word vào output stream của response
        document.write(response.getOutputStream());
        // Đóng tài liệu
        document.close();
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=sample.docx");
        response.flushBuffer();
    }

    @Override
    public Workbook handleExcel(SearchStaffDto dto) {
        if (dto == null) {
            return null;
        }
        String templatePath = "Empty.xlsx";
        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(templatePath)) {
            if (fileInputStream == null) {
                throw new IOException("File '" + templatePath + "' không tìm thấy trong classpath");
            }
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            configExcelStyle(workbook);

            List<StaffDto> listStaff = this.getListStaff(dto);

            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.createRow(0);
            List<String> headers = new ArrayList<>();
            headers.add("Mã nhân viên");
            headers.add("Tên nhân viên");
            headers.add("Phòng ban");
            headers.add("Mức lương đóng BHXH");
            headers.add("Tỷ lệ cá nhân đóng (%)");
            headers.add("Số tiền cá nhân đóng (VNĐ)");
            headers.add("Tỷ lệ đơn vị đóng (%)");
            headers.add("Số tiền đơn vị đóng (VNĐ)");
            headers.add("Tổng (VNĐ)");

            for (int i = 0; i < headers.size(); i++) {
                Cell headerCell = headerRow.createCell(i);
                headerCell.setCellValue(headers.get(i));
            }

            int rowIndex = 1;

            if (listStaff != null && !listStaff.isEmpty()) {
                for (StaffDto staffDto : listStaff) {
                    if (staffDto != null) {
                        Row dataRow = sheet.createRow(rowIndex); // Tạo một hàng mới
                        if (staffDto.getStaffCode() != null) {
                            int indexCell = 0;
                            Cell staffCodeCell1 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell2 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell3 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell4 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell5 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell6 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell7 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell8 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell9 = dataRow.createCell(indexCell++);

                            staffCodeCell1.setCellValue(staffDto.getStaffCode());
                            staffCodeCell2.setCellValue(staffDto.getDisplayName());

                            if (staffDto.getDepartment() != null && staffDto.getDepartment().getName() != null) {
                                staffCodeCell3.setCellValue(staffDto.getDepartment().getName());
                            }
                            if (staffDto.getInsuranceSalary() != null) {
                                staffCodeCell4.setCellValue(staffDto.getInsuranceSalary());
                            }
                            if (staffDto.getStaffPercentage() != null) {
                                staffCodeCell5.setCellValue(staffDto.getStaffPercentage());
                            }
                            if (staffDto.getStaffInsuranceAmount() != null) {
                                staffCodeCell6.setCellValue(staffDto.getStaffInsuranceAmount());
                            }
                            if (staffDto.getOrgPercentage() != null) {
                                staffCodeCell7.setCellValue(staffDto.getOrgPercentage());
                            }
                            if (staffDto.getOrgInsuranceAmount() != null) {
                                staffCodeCell8.setCellValue(staffDto.getOrgInsuranceAmount());
                            }
                            if (staffDto.getTotalInsuranceAmount() != null) {
                                staffCodeCell9.setCellValue(staffDto.getTotalInsuranceAmount());
                            }
                        }
                        rowIndex++;
                    }
                }
                // hàng tổng cộng cuối
                rowIndex++;
                StaffDto sumIns = staffRepository.getTotalInsuranceAmounts();
                if (sumIns != null) {
                    Row dataRow = sheet.createRow(rowIndex);
                    int indexCell = 0;
                    Cell staffCodeCell1 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell2 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell3 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell4 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell5 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell6 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell7 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell8 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell9 = dataRow.createCell(indexCell++);

                    staffCodeCell2.setCellValue("Tổng cộng");

                    if (sumIns.getInsuranceSalary() != null) {
                        staffCodeCell4.setCellValue(sumIns.getInsuranceSalary());
                    }
                    if (sumIns.getStaffPercentage() != null) {
                        staffCodeCell5.setCellValue(sumIns.getStaffPercentage());
                    }
                    if (sumIns.getStaffInsuranceAmount() != null) {
                        staffCodeCell6.setCellValue(sumIns.getStaffInsuranceAmount());
                    }
                    if (sumIns.getOrgPercentage() != null) {
                        staffCodeCell7.setCellValue(sumIns.getOrgPercentage());
                    }
                    if (sumIns.getOrgInsuranceAmount() != null) {
                        staffCodeCell8.setCellValue(sumIns.getOrgInsuranceAmount());
                    }
                    if (sumIns.getTotalInsuranceAmount() != null) {
                        staffCodeCell9.setCellValue(sumIns.getTotalInsuranceAmount());
                    }
                }
            }

            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            return workbook;
        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }

    public static void configExcelStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Times New Roman");
        font.setBold(true);

        cellStyle.setWrapText(true);

        Font fontNoBorder = workbook.createFont();
        fontNoBorder.setFontHeightInPoints((short) 10);
        fontNoBorder.setBold(true);
        fontNoBorder.setFontName("Times New Roman");

        CellStyle cellStyleNoBoder = workbook.createCellStyle();
        cellStyleNoBoder.setWrapText(true);
        cellStyleNoBoder.setFont(fontNoBorder);

        CellStyle cellStyleBoldTable = workbook.createCellStyle();
        cellStyleBoldTable.setWrapText(true);
        cellStyleBoldTable.setFont(font);
    }

    @Override
    public List<StaffDto> getListStaff(SearchStaffDto dto) {
        if (dto == null) {
            return null;
        }

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.staffCode ASC";

        String sql = "select distinct new com.globits.hr.dto.StaffDto(entity) from Staff as entity ";

        List<UUID> listDepId = new ArrayList<>();
        if (dto.getDepartmentId() != null) {
            listDepId = this.getAllDepartmentIdByParentId(dto.getDepartmentId());
            if (listDepId != null && listDepId.size() > 0) {
                whereClause += " AND entity.department.id IN (:listDepId) ";
            } else {
                whereClause += " AND entity.department.id = :departmentId ";
            }
        }

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.displayName LIKE :text OR entity.staffCode LIKE :text ) ";
        }
        if (dto.getAcademicTitleLevel() != null) {
            whereClause += " AND ( entity.academicRank.id IN (select a.id from AcademicTitle a where a.level >= :academicTitleLevel ) ) ";
        }
        if (dto.getEducationDegreeLevel() != null) {
            whereClause += " AND ( entity.degree.id IN (select a.id from EducationDegree a where a.level >= :educationDegreeLevel ) ) ";
        }
        if (dto.getEmployeeStatusId() != null && StringUtils.hasText(dto.getEmployeeStatusId().toString())) {
            whereClause += " AND ( entity.status.id  =: employeeStatusId ) ";
        }
        if (dto.getCivilServantTypeId() != null && StringUtils.hasText(dto.getCivilServantTypeId().toString())) {
            whereClause += " AND ( entity.civilServantType.id  =: civilServantTypeId ) ";
        }
        if (dto.getGender() != null && StringUtils.hasText(dto.getGender())) {
            whereClause += " AND ( entity.gender  =: gender ) ";
        }
        if (dto.getApprovalStatus() != null) {
            whereClause += " AND ( entity.approvalStatus = :approvalStatus ) ";
        }
//        if (dto.getHasSocialIns() != null && dto.getHasSocialIns()) {
//            whereClause += " AND ( entity.hasSocialIns is not null and entity.hasSocialIns = true ) ";
//        } else if (dto.getHasSocialIns() != null && !dto.getHasSocialIns()) {
//            whereClause += " AND ( entity.hasSocialIns is null or entity.hasSocialIns = false ) ";
//        }
        if (dto.getProjectIdList() != null) {
            sql += " INNER JOIN ProjectStaff ps on entity.id = ps.staff.id ";
            whereClause += " AND (ps.project.id in :projectIdList) ";
            if (dto.getIncludeVoidedInProject() == null || !dto.getIncludeVoidedInProject()) {
                whereClause += " and (ps.voided = false or ps.voided is null) ";
            }
        } else if (dto.getProjectId() != null) {
            sql += " INNER JOIN ProjectStaff ps on entity.id = ps.staff.id ";
            whereClause += " AND (ps.project.id = :projectId)";
            if (dto.getIncludeVoidedInProject() == null || !dto.getIncludeVoidedInProject()) {
                whereClause += " and (ps.voided = false or ps.voided is null) ";
            }
        }

        sql += whereClause + orderBy;

        Query query = manager.createQuery(sql, StaffDto.class);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getApprovalStatus() != null) {
            query.setParameter("approvalStatus", dto.getApprovalStatus());
        }
        if (dto.getCivilServantTypeId() != null && StringUtils.hasText(dto.getCivilServantTypeId().toString())) {
            query.setParameter("civilServantTypeId", dto.getCivilServantTypeId());
        }
        if (dto.getAcademicTitleLevel() != null) {
            query.setParameter("academicTitleLevel", dto.getAcademicTitleLevel());
        }
        if (dto.getEducationDegreeLevel() != null) {
            query.setParameter("educationDegreeLevel", dto.getEducationDegreeLevel());
        }
        if (dto.getEmployeeStatusId() != null && StringUtils.hasText(dto.getEmployeeStatusId().toString())) {
            query.setParameter("employeeStatusId", dto.getEmployeeStatusId());
        }
        if (dto.getGender() != null && StringUtils.hasText(dto.getGender())) {
            query.setParameter("gender", dto.getGender());
        }
        if (dto.getProjectIdList() != null) {
            query.setParameter("projectIdList", dto.getProjectIdList());
        } else if (dto.getProjectId() != null) {
            query.setParameter("projectId", dto.getProjectId());
        }

        if (listDepId != null && listDepId.size() > 0) {
            query.setParameter("listDepId", listDepId);
        } else {
            if (dto.getDepartmentId() != null) {
                query.setParameter("departmentId", dto.getDepartmentId());
            }
        }

        return query.getResultList();
    }

    @Override
    public UserExtRoleDto getCurrentRoleUser() {
        User modifiedUser = userExtService.getCurrentUserEntity();
        if (modifiedUser != null && modifiedUser.getRoles() != null && modifiedUser.getRoles().size() > 0) {
            UserExtRoleDto dto = new UserExtRoleDto();
            for (Role role : modifiedUser.getRoles()) {
                if (role != null && role.getName() != null) {
                    if (role.getName().trim().equals(HrConstants.ROLE_SUPER_ADMIN.trim())) {
                        dto.setRoleSuperAdmin(true);
                        continue;
                    } else if (role.getName().trim().equals(HrConstants.ROLE_ADMIN.trim())) {
                        dto.setRoleAdmin(true);
                        continue;
                    } else if (role.getName().trim().equals(HrConstants.ROLE_USER.trim())
                            || role.getName().trim().equals(HrConstants.HR_USER.trim())) {
                        dto.setRoleAdmin(true);
                        continue;
                    } else if (role.getName().trim().equals(HrConstants.HR_MANAGER.trim())) {
                        dto.setRoleHrManager(true);
                        continue;
                    } else if (role.getName().trim().equals(HrConstants.HR_RECRUITMENT.trim())) {
                        dto.setRoleRecruitment(true);
                        continue;
                    } else if (role.getName().trim().equals(HrConstants.HR_INSURANCE_MANAGER.trim())) {
                        dto.setRoleRecruitment(true);
                        continue;
                    }
                }
            }
            return dto;
        }
        return null;
    }

    @Override
    public List<StaffDto> createUsersForStaff(List<StaffDto> staffs, boolean allowCreate) {
        int count = 0;
        List<StaffDto> listCreateAble = new ArrayList<>();
        for (StaffDto staffDto : staffs) {
            if (staffDto == null || staffDto.getId() == null) {
                continue;
            }

            Staff staff = staffRepository.findById(staffDto.getId()).orElse(null);
            if (staff == null || staff.getUser() != null) {
                continue;
            }

            if (allowCreate == true) {
                UserWithStaffDto userWithStaffDto = new UserWithStaffDto();
                userWithStaffDto.setStaff(new StaffDto(staff));
                userWithStaffDto.setPassword("123456");
                userWithStaffDto.setUsername(staff.getStaffCode());
                userWithStaffDto.setEmail(staff.getEmail());

                Role roleUser = roleRepository.findByName("ROLE_USER");
                if (roleUser != null) {
                    userWithStaffDto.getRoles().add(new RoleDto(roleUser));
                }

                userExtService.saveUserAndChooseUsingStaff(userWithStaffDto);
            }
            listCreateAble.add(new StaffDto(staff));
        }
        return listCreateAble;
    }

    @Override
    public Integer updateAllowExternalIpTimekeeping(List<StaffDto> staffs, boolean status) {
        if (staffs == null || staffs.isEmpty())
            return null;
        int result = 0;
        for (StaffDto staffDto : staffs) {
            if (staffDto == null || staffDto.getId() == null)
                continue;
            Staff entity = staffRepository.findById(staffDto.getId()).orElse(null);
            if (entity == null)
                continue;
            entity.setAllowExternalIpTimekeeping(status);
            staffRepository.save(entity);
            result++;
        }
        return result;
    }

    @Override
    public Workbook exportExcelListStaff(SearchStaffDto searchStaffDto) {
        if (searchStaffDto == null) {
            return null;
        }

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(TEMPLATE_PATH)) {
            if (fileInputStream == null) {
                throw new IOException("File '" + TEMPLATE_PATH + "' không tìm thấy trong classpath");
            }

            Workbook workbook = new XSSFWorkbook(fileInputStream);

            Sheet staffSheet = workbook.getSheetAt(0);
            Sheet familySheet = workbook.getSheetAt(1);
            Sheet bankSheet = workbook.getSheetAt(2);
            CellStyle dataCellStyle = ExportExcelUtil.createDataCellStyle(workbook);

            int pageIndex = 1;
            int rowIndex = 1;
            int orderNumber = 1;
            int familyRowIndex = 1;
            int bankRowIndex = 1;
            boolean hasNextPage = true;
            long startTime = System.nanoTime();

            // Địa điểm làm việc chính
            HashMap<UUID, StaffWorkingLocationDto> mapWorkingLocation = staffWorkingLocationService
                    .getMainWorkingLocationMap();
            HashMap<UUID, PositionMainDto> mapMainPosition = positionService.getPositionMainMap();
            HashMap<UUID, LabourAgreementDto> mapLabourAgreementLatest = staffLabourAgreementService
                    .getLabourAgreementLatestMap();
            HashMap<UUID, LeaveHistoryDto> mapLeaveHistory = staffWorkingHistoryService.getLeaveHistoryMap();
            HashMap<UUID, PermanentAddressDto> mapPermanentAddress = this.getPermanentAddressMap();
            HashMap<UUID, DefaultDocumentTemplateItemDto> mapDefaultDocumentTemplateItem = hrDocumentTemplateService
                    .getDefaultDocumentTemplateItemMap();
            HashMap<UUID, Integer> numberOfDependentsMap = this.getNumberOfDependentsMap();

            while (hasNextPage) {
                // searchStaffDto = new SearchStaffDto();
                searchStaffDto.setPageIndex(pageIndex);
                searchStaffDto.setPageSize(PAGE_SIZE);

                Page<Staff> staffPage = searchByPageEntity(searchStaffDto);
                if (staffPage == null || staffPage.isEmpty()) {
                    break;
                }

                for (Staff staff : staffPage) {
                    if (staff == null)
                        continue;

                    // Xuất dữ liệu quan hệ thân nhân của nhân viên
                    List<StaffFamilyRelationshipImport> exportStaffFamilyRelationship = staffFamilyRelationshipService
                            .getIEStaffFamilyRelationship(staff);
                    familyRowIndex = staffFamilyRelationshipService.exportExcelStaffFamilyRelationship(
                            exportStaffFamilyRelationship, familySheet, familyRowIndex);

                    // Xuất dữ liệu tài khoản ngân hàng của nhân viên
                    List<StaffBankAccountImport> exportStaffBankAccount = personBankAccountService
                            .getIEStaffBankAccounts(staff);
                    bankRowIndex = personBankAccountService.exportExcelStaffBankAccount(exportStaffBankAccount,
                            bankSheet, bankRowIndex);

                    Row dataRow = staffSheet.createRow(rowIndex);
                    int cellIndex = 0;
                    ExportExcelUtil.createCell(dataRow, cellIndex++, orderNumber, dataCellStyle);
                    orderNumber++;

                    // 1. Mã nhân viên
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getStaffCode(), dataCellStyle);
                    // 2. Tên nhân viên
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getDisplayName(), dataCellStyle);
                    // 3. Mã Trạng thái làm việc
                    String statusCode = (staff.getStatus() != null && staff.getStatus().getCode() != null)
                            ? staff.getStatus().getCode()
                            : "";
                    ExportExcelUtil.createCell(dataRow, cellIndex++, statusCode, dataCellStyle);
                    // 4. Trạng thái làm việc
                    String statusName = (staff.getStatus() != null && staff.getStatus().getName() != null)
                            ? staff.getStatus().getName()
                            : "";
                    ExportExcelUtil.createCell(dataRow, cellIndex++, statusName, dataCellStyle);
                    // 5. Ngày vào (ngày tuyển dụng)
                    String recruitmentDate = (staff.getRecruitmentDate() != null)
                            ? formatDate(staff.getRecruitmentDate())
                            : "";
                    ExportExcelUtil.createCell(dataRow, cellIndex++, recruitmentDate, dataCellStyle);

                    // 6. 7. 8.
                    LeaveHistoryDto leaveHistory = null;
                    if (mapLeaveHistory != null) {
                        leaveHistory = mapLeaveHistory.get(staff.getId());
                    }
                    String startDateLeave = leaveHistory != null ? formatDate(leaveHistory.getStartDate()) : "";
                    String endDateLeave = leaveHistory != null ? formatDate(leaveHistory.getEndDate()) : "";
                    String reasonLeave = leaveHistory != null ? leaveHistory.getReason() : "";
                    ExportExcelUtil.createCell(dataRow, cellIndex++, startDateLeave, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, endDateLeave, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, reasonLeave, dataCellStyle);

                    // 9-17
                    PositionMainDto mainPosition = null;
                    if (mapMainPosition != null) {
                        mainPosition = mapMainPosition.get(staff.getId());
                    }
                    String departmentCode = mainPosition != null ? mainPosition.getDepartmentCode() : "";
                    String departmentName = mainPosition != null ? mainPosition.getDepartmentName() : "";
                    String positionCode = mainPosition != null ? mainPosition.getPositionCode() : "";
                    String positionName = mainPosition != null ? mainPosition.getPositionName() : "";
                    String positionTitleName = mainPosition != null ? mainPosition.getPositionTitleName() : "";
                    String positionTitleCode = mainPosition != null ? mainPosition.getPositionTitleCode() : "";
                    String rankTitleName = mainPosition != null ? mainPosition.getRankTitleName() : "";
                    String supervisorStaffCode = mainPosition != null ? mainPosition.getSupervisorStaffCode() : "";
                    String supervisorStaffDisplayName = mainPosition != null
                            ? mainPosition.getSupervisorStaffDisplayName()
                            : "";
                    String positionTitleGroupCode = mainPosition != null ? mainPosition.getPositionTitleGroupCode()
                            : "";
                    String positionTitleGroupName = mainPosition != null ? mainPosition.getPositionTitleGroupName()
                            : "";

                    // 9. Mã Ban/Chi nhánh
                    ExportExcelUtil.createCell(dataRow, cellIndex++, departmentCode, dataCellStyle);
                    // 10. Tên Ban/Chi nhánh
                    ExportExcelUtil.createCell(dataRow, cellIndex++, departmentName, dataCellStyle);
                    // 11. 12.
                    ExportExcelUtil.createCell(dataRow, cellIndex++, positionTitleGroupCode, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, positionTitleGroupName, dataCellStyle);
                    // 13. Level (Chỉ có Tên)
                    ExportExcelUtil.createCell(dataRow, cellIndex++, rankTitleName, dataCellStyle);
                    // 14. Mã Chức danh
                    ExportExcelUtil.createCell(dataRow, cellIndex++, positionTitleCode, dataCellStyle);
                    // 15. Tên Chức danh
                    ExportExcelUtil.createCell(dataRow, cellIndex++, positionTitleName, dataCellStyle);
                    // 16. Mã chức vụ
                    ExportExcelUtil.createCell(dataRow, cellIndex++, positionCode, dataCellStyle);
                    // 17. Tên chức vụ
                    ExportExcelUtil.createCell(dataRow, cellIndex++, positionName, dataCellStyle);
                    // 18. Hình thức làm việc
                    Integer workingFormatValue = staff.getStaffWorkingFormat(); // giả sử đây là Integer
                    String workingFormatDesc = HrConstants.StaffWorkingFormat.getDescriptionByValue(workingFormatValue);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, workingFormatDesc, dataCellStyle);
                    // this.createCell(dataRow, cellIndex++, staff.getStaffWorkingFormat(),
                    // dataCellStyle);
                    // 19. Địa điểm làm việc
                    String mainWorkingPlace = "";
                    if (mapWorkingLocation != null && mapWorkingLocation.containsKey(staff.getId())) {
                        mainWorkingPlace = mapWorkingLocation.get(staff.getId()).getWorkingLocation();
                    }
                    ExportExcelUtil.createCell(dataRow, cellIndex++, mainWorkingPlace, dataCellStyle);
                    // 20. Mã Quản lý trực tiếp
                    ExportExcelUtil.createCell(dataRow, cellIndex++, supervisorStaffCode, dataCellStyle);
                    // 21. Tên Quản lý trực tiếp
                    ExportExcelUtil.createCell(dataRow, cellIndex++, supervisorStaffDisplayName, dataCellStyle);
                    // 22. Email công ty
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getCompanyEmail(), dataCellStyle);
                    // 23. Email cá nhân
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getEmail(), dataCellStyle);
                    // 24. Tình trạng
                    Integer staffPhaseValue = staff.getStaffPhase();
                    String staffPhaseDesc = HrConstants.StaffPhase.getDescriptionByValue(staffPhaseValue);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staffPhaseDesc, dataCellStyle);
                    // this.createCell(dataRow, cellIndex++, staff.getStaffPhase(), dataCellStyle);
                    // 25-30. Hợp đồng gần nhất
                    LabourAgreementDto labourAgreementLatest = null;
                    if (mapLabourAgreementLatest != null) {
                        labourAgreementLatest = mapLabourAgreementLatest.get(staff.getId());
                    }
                    String codeOrg = labourAgreementLatest != null ? labourAgreementLatest.getCodeOrg() : "";
                    String nameOrg = labourAgreementLatest != null ? labourAgreementLatest.getNameOrg() : "";
                    String labourAgreementNumber = labourAgreementLatest != null
                            ? labourAgreementLatest.getLabourAgreementNumber()
                            : "";
                    String startDate = labourAgreementLatest != null ? formatDate(labourAgreementLatest.getStartDate())
                            : "";
                    String endDate = labourAgreementLatest != null ? formatDate(labourAgreementLatest.getEndDate())
                            : "";
                    String totalDays = labourAgreementLatest != null ? labourAgreementLatest.getTotalDays().toString()
                            : "";
                    // 25.Số HĐ (TV/HV)
                    ExportExcelUtil.createCell(dataRow, cellIndex++, labourAgreementNumber, dataCellStyle);
                    // 26. Mã công ty ký HĐ
                    ExportExcelUtil.createCell(dataRow, cellIndex++, codeOrg, dataCellStyle);
                    // 27. Công ty ký HĐ
                    ExportExcelUtil.createCell(dataRow, cellIndex++, nameOrg, dataCellStyle);
                    // 28. Ngày bắt đầu (HV/TV)
                    ExportExcelUtil.createCell(dataRow, cellIndex++, startDate, dataCellStyle);
                    // 29. Số ngày HV/TV
                    ExportExcelUtil.createCell(dataRow, cellIndex++, totalDays, dataCellStyle);
                    if (staff.getApprenticeDays() != null) {
                        ExportExcelUtil.createCell(dataRow, 29, staff.getApprenticeDays().toString(), dataCellStyle);
                    } else {
                        ExportExcelUtil.createCell(dataRow, 29, "0", dataCellStyle);
                    }
                    // 30.Ngày kết thúc (HV/TV)
                    ExportExcelUtil.createCell(dataRow, cellIndex++, endDate, dataCellStyle);

                    // 31. SĐT
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getPhoneNumber(), dataCellStyle);
                    // 32. Ngày sinh
                    ExportExcelUtil.createCell(dataRow, cellIndex++, formatDate(staff.getBirthDate()), dataCellStyle);
                    // 33. Giới tính
                    ExportExcelUtil.createCell(dataRow, cellIndex++, ExportExcelUtil.getGenderText(staff.getGender()),
                            dataCellStyle);
                    // 34. - 39.
                    PermanentAddressDto permanentAddress = null;
                    if (mapPermanentAddress != null) {
                        permanentAddress = mapPermanentAddress.get(staff.getId());
                    }
                    String administrativeUnitCode = permanentAddress != null
                            ? permanentAddress.getAdministrativeUnitCode()
                            : "";
                    String administrativeUnitValue = permanentAddress != null
                            ? permanentAddress.getAdministrativeUnitValue()
                            : "";
                    String districtCode = permanentAddress != null ? permanentAddress.getDistrictCode() : "";
                    String districtValue = permanentAddress != null ? permanentAddress.getDistrictValue() : "";
                    String provinceCode = permanentAddress != null ? permanentAddress.getProvinceCode() : "";
                    String provinceValue = permanentAddress != null ? permanentAddress.getProvinceValue() : "";
                    // 34. Mã tỉnh/thành phố
                    ExportExcelUtil.createCell(dataRow, cellIndex++, provinceCode, dataCellStyle);
                    // 35. Tên tỉnh/thành phố
                    ExportExcelUtil.createCell(dataRow, cellIndex++, provinceValue, dataCellStyle);
                    // 36. Mã huyện/quận
                    ExportExcelUtil.createCell(dataRow, cellIndex++, districtCode, dataCellStyle);
                    // 37. Tên huyện/quận
                    ExportExcelUtil.createCell(dataRow, cellIndex++, districtValue, dataCellStyle);
                    // 38. Mã xã/phường
                    ExportExcelUtil.createCell(dataRow, cellIndex++, administrativeUnitCode, dataCellStyle);
                    // 39. Tên xã/phường
                    ExportExcelUtil.createCell(dataRow, cellIndex++, administrativeUnitValue, dataCellStyle);
                    // 40. Chi tiết thường trú
                    String permanentResidenceValue = staff.getPermanentResidence();
                    ExportExcelUtil.createCell(dataRow, cellIndex++, permanentResidenceValue, dataCellStyle);
                    // 41. Thường trú chi tiết (gộp cả xã, huyện, tỉnh)
                    StringBuilder addressBuilder = new StringBuilder();
                    if (permanentResidenceValue != null && !permanentResidenceValue.isEmpty()) {
                        addressBuilder.append(permanentResidenceValue);
                    }
                    if (administrativeUnitValue != null && !administrativeUnitValue.isEmpty()) {
                        if (!addressBuilder.isEmpty())
                            addressBuilder.append(", ");
                        addressBuilder.append(administrativeUnitValue);
                    }
                    if (districtValue != null && !districtValue.isEmpty()) {
                        if (!addressBuilder.isEmpty())
                            addressBuilder.append(", ");
                        addressBuilder.append(districtValue);
                    }
                    if (provinceValue != null && !provinceValue.isEmpty()) {
                        if (!addressBuilder.isEmpty())
                            addressBuilder.append(", ");
                        addressBuilder.append(provinceValue);
                    }
                    ExportExcelUtil.createCell(dataRow, cellIndex++, addressBuilder.toString(), dataCellStyle);
                    // 42. Tạm trú
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getCurrentResidence(), dataCellStyle);
                    // 43. CMND
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getIdNumber(), dataCellStyle);
                    // 45. Ngày cấp
                    ExportExcelUtil.createCell(dataRow, cellIndex++, formatDate(staff.getIdNumberIssueDate()),
                            dataCellStyle);
                    // 46. Nơi cấp
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getIdNumberIssueBy(), dataCellStyle);

                    // 43+. CCCD
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getPersonalIdentificationNumber(),
                            dataCellStyle);
                    // 45+. Ngày cấp CCCD
                    ExportExcelUtil.createCell(dataRow, cellIndex++,
                            formatDate(staff.getPersonalIdentificationIssueDate()), dataCellStyle);
                    // 46+. Nơi cấp CCCD
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getPersonalIdentificationIssuePlace(),
                            dataCellStyle);

                    // 47. Tình trạng hôn nhân
                    Integer staffMaritalStatusValue = staff.getMaritalStatus(); // giả sử đây là Integer
                    String staffMaritalStatusDesc = HrConstants.StaffMaritalStatus
                            .getDescriptionByValue(staffMaritalStatusValue);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staffMaritalStatusDesc, dataCellStyle);
                    // this.createCell(dataRow, cellIndex++, staff.getMaritalStatus(),
                    // dataCellStyle);

                    String ethnicsCodeValue = null;
                    String ethnicsNameValue = null;
                    if (staff.getEthnics() != null) {
                        ethnicsCodeValue = (staff.getEthnics().getCode() != null) ? staff.getEthnics().getCode() : "";
                        ethnicsNameValue = (staff.getEthnics().getName() != null) ? staff.getEthnics().getName() : "";
                    }
                    // 48. Mã Dân tộc
                    ExportExcelUtil.createCell(dataRow, cellIndex++, ethnicsCodeValue, dataCellStyle);
                    // 49. Dân tộc
                    ExportExcelUtil.createCell(dataRow, cellIndex++, ethnicsNameValue, dataCellStyle);

                    String religionCodeValue = null;
                    String religionNameValue = null;
                    if (staff.getReligion() != null) {
                        religionCodeValue = (staff.getReligion().getCode() != null) ? staff.getReligion().getCode()
                                : "";
                        religionNameValue = (staff.getReligion().getName() != null) ? staff.getReligion().getName()
                                : "";
                    }
                    // 50. Mã Tôn giáo
                    ExportExcelUtil.createCell(dataRow, cellIndex++, religionCodeValue, dataCellStyle);
                    // 51. Tôn giáo
                    ExportExcelUtil.createCell(dataRow, cellIndex++, religionNameValue, dataCellStyle);

                    String nationalityCodeValue = null;
                    String nationalityNameValue = null;
                    if (staff.getNationality() != null) {
                        nationalityCodeValue = (staff.getNationality().getCode() != null)
                                ? staff.getNationality().getCode()
                                : "";
                        nationalityNameValue = (staff.getNationality().getName() != null)
                                ? staff.getNationality().getName()
                                : "";
                    }
                    // 52. Mã Quốc tịch
                    ExportExcelUtil.createCell(dataRow, cellIndex++, nationalityCodeValue, dataCellStyle);
                    // 53. Quốc tịch
                    ExportExcelUtil.createCell(dataRow, cellIndex++, nationalityNameValue, dataCellStyle);
                    // 54. Quê quán
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getBirthPlace(), dataCellStyle);

                    String educationDegreeCode = null;
                    String educationDegreeName = null;
                    if (staff.getEducationDegree() != null) {
                        educationDegreeCode = staff.getEducationDegree().getCode();
                        educationDegreeName = staff.getEducationDegree().getName();
                    }
                    // 55. Mã Trình độ học vấn
                    ExportExcelUtil.createCell(dataRow, cellIndex++, educationDegreeCode, dataCellStyle);
                    // 56. Trình độ học vấn
                    ExportExcelUtil.createCell(dataRow, cellIndex++, educationDegreeName, dataCellStyle);
                    // 57. Thông tin người liên hệ
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getContactPersonInfo(), dataCellStyle);
                    // 58. Mã số thuế
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getTaxCode(), dataCellStyle);
                    // 59. Số Người phụ thuộc đã đăng ký
                    String numberOfDependents = null;
                    if (numberOfDependentsMap != null && staff.getId() != null) {
                        Integer value = numberOfDependentsMap.get(staff.getId());
                        if (value != null) {
                            numberOfDependents = String.valueOf(value);
                        }
                    }
                    ExportExcelUtil.createCell(dataRow, cellIndex++, numberOfDependents, dataCellStyle);
                    // 60. Mã số Bảo hiểm xã hội
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getSocialInsuranceNumber(), dataCellStyle);
                    // 61. Mã số Bảo Hiểm Y tế
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getHealthInsuranceNumber(), dataCellStyle);
                    // 62. Tình trạng Sổ Bảo hiểm xã hội
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getSocialInsuranceNote(), dataCellStyle);
                    // 63. Nơi mong muốn đăng ký khám chữa bệnh
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staff.getDesireRegistrationHealthCare(),
                            dataCellStyle);

                    String introducerNameValue = null;
                    String introducerCodeValue = null;
                    if (staff.getIntroducer() != null) {
                        introducerNameValue = (staff.getIntroducer().getDisplayName() != null)
                                ? staff.getIntroducer().getDisplayName()
                                : "";
                        introducerCodeValue = (staff.getIntroducer().getStaffCode() != null)
                                ? staff.getIntroducer().getStaffCode()
                                : "";
                    }
                    // 64. Mã người giới thiệu
                    ExportExcelUtil.createCell(dataRow, cellIndex++, introducerCodeValue, dataCellStyle);
                    // 65. Tên người giới thiệu
                    ExportExcelUtil.createCell(dataRow, cellIndex++, introducerNameValue, dataCellStyle);

                    String recruiterNameValue = null;
                    String recruiterCodeValue = null;
                    if (staff.getRecruiter() != null) {
                        recruiterNameValue = (staff.getRecruiter().getDisplayName() != null)
                                ? staff.getRecruiter().getDisplayName()
                                : "";
                        recruiterCodeValue = (staff.getRecruiter().getStaffCode() != null)
                                ? staff.getRecruiter().getStaffCode()
                                : "";
                    }
                    // 66. Mã người tuyển dụng
                    ExportExcelUtil.createCell(dataRow, cellIndex++, recruiterCodeValue, dataCellStyle);
                    // 67. Tên người tuyển dụng
                    ExportExcelUtil.createCell(dataRow, cellIndex++, recruiterNameValue, dataCellStyle);

                    // 68-82.
                    DefaultDocumentTemplateItemDto defaultDocumentTemplateItem = null;
                    if (mapPermanentAddress != null) {
                        defaultDocumentTemplateItem = mapDefaultDocumentTemplateItem.get(staff.getId());
                    }
                    String hasEmployeeProfile = null;
                    String hasA34 = null;
                    String hasCCCD = null;
                    String hasDUT = null;
                    String hasSYLL = null;
                    String hasBC = null;
                    String hasCCLQ = null;
                    String hasGKSK = null;
                    String hasSHK = null;
                    String hasHSK = null;
                    String hasPTTCN = null;
                    String hasCKBMTT = null;
                    String hasCKBMTTTN = null;
                    String hasCKTN = null;
                    String hasHDTV = null;
                    if (defaultDocumentTemplateItem != null) {
                        hasEmployeeProfile = defaultDocumentTemplateItem.getHasEmployeeProfile();
                        hasA34 = defaultDocumentTemplateItem.getHasA34();
                        hasCCCD = defaultDocumentTemplateItem.getHasCCCD();
                        hasDUT = defaultDocumentTemplateItem.getHasDUT();
                        hasSYLL = defaultDocumentTemplateItem.getHasSYLL();
                        hasBC = defaultDocumentTemplateItem.getHasBC();
                        hasCCLQ = defaultDocumentTemplateItem.getHasCCLQ();
                        hasGKSK = defaultDocumentTemplateItem.getHasGKSK();
                        hasSHK = defaultDocumentTemplateItem.getHasSHK();
                        hasHSK = defaultDocumentTemplateItem.getHasHSK();
                        hasPTTCN = defaultDocumentTemplateItem.getHasPTTCN();
                        hasCKBMTT = defaultDocumentTemplateItem.getHasCKBMTT();
                        hasCKBMTTTN = defaultDocumentTemplateItem.getHasCKBMTTTN();
                        hasCKTN = defaultDocumentTemplateItem.getHasCKTN();
                        hasHDTV = defaultDocumentTemplateItem.getHasHDTV();
                    }
                    ExportExcelUtil.createCell(dataRow, cellIndex++, hasEmployeeProfile, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, hasA34, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, hasCCCD, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, hasDUT, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, hasSYLL, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, hasBC, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, hasCCLQ, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, hasGKSK, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, hasSHK, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, hasHSK, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, hasPTTCN, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, hasCKBMTT, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, hasCKBMTTTN, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, hasCKTN, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, hasHDTV, dataCellStyle);
                    // 83.
                    Integer staffDocumentStatusValue = staff.getStaffDocumentStatus();
                    String staffDocumentStatusDesc = HrConstants.StaffDocumentStatus
                            .getDescriptionByValue(staffDocumentStatusValue);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staffDocumentStatusDesc, dataCellStyle);

                    // thêm dòng tiếp theo
                    rowIndex++;
                }

                hasNextPage = staffPage.hasNext(); // Kiểm tra xem còn trang tiếp theo không
                pageIndex++; // Tăng pageIndex để lấy trang tiếp theo
            }
            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;

            logger.info("Xuất tất cả nhân viên - Xử lý mất {} ms ", elapsedTimeMs);
            return workbook;

        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }

    private String formatDate(Date date) {
        if (date == null)
            return "";
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }

    @Override
    public ImportStaffDto importExcelListStaff(InputStream inputStream) {
        ImportStaffDto importStaffDto = new ImportStaffDto();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            // Đọc dữ liệu import danh sách nhân viên
            Sheet staffSheet = workbook.getSheetAt(0);
            Sheet familySheet = workbook.getSheetAt(1);
            Sheet bankSheet = workbook.getSheetAt(2);
            Sheet staffWorkingLocationSheet = workbook.getSheetAt(3);

            List<StaffDto> listStaff = new ArrayList<>();
            // Hàm import mới
            List<StaffLAImport> staffImports = this.importExcelDataStaffSheet(staffSheet);
            // Quan hệ thân nhân
            List<StaffFamilyRelationshipImport> staffFamilyRelationshipImports = staffFamilyRelationshipService
                    .importExcelStaffFamilyRelationship(familySheet);
            // Tài khoản ngân hàng
            List<StaffBankAccountImport> staffBankAccountImports = personBankAccountService
                    .importExcelStaffBankAccount(bankSheet);

            // Địa điểm làm việc
            List<StaffWorkingLocationImport> staffWorkingLocationImports = staffWorkingLocationService
                    .importExcelStaffWorkingLocation(staffWorkingLocationSheet);

            importStaffDto.setListStaff(listStaff);
            importStaffDto.setStaffImports(staffImports);
            importStaffDto.setStaffBankAccountImports(staffBankAccountImports);
            importStaffDto.setStaffFamilyRelationshipImports(staffFamilyRelationshipImports);
            importStaffDto.setStaffWorkingLocationImports(staffWorkingLocationImports);
        } catch (Exception e) {
            System.err.println("Error import excel: " + e.getMessage());
            e.printStackTrace();
        }

        return importStaffDto;
    }

    private List<StaffLAImport> importExcelDataStaffSheet(Sheet datatypeSheet) {
        List<StaffLAImport> response = new ArrayList<>();

        int rowIndex = 1;
        int num = datatypeSheet.getLastRowNum();

        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
        while (rowIndex <= num) {
//            logger.info("Import with rowIndex = " + rowIndex);

            Row currentRow = datatypeSheet.getRow(rowIndex);
            Cell currentCell = null;
            if (currentRow != null) {
                StaffLAImport dto = new StaffLAImport();
                List<String> errorMessages = new ArrayList<>();

                // 0. STT
                currentCell = currentRow.getCell(0);
                Integer rowOrder = null;
                if (currentCell != null) {
                    try {
                        if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                            rowOrder = (int) currentCell.getNumericCellValue();
                        } else if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                            rowOrder = Integer.valueOf(currentCell.getStringCellValue());
                        }
                    } catch (Exception e) {
                        // e.printStackTrace();
//                        errorMessages.add("Không đọc được dữ liệu số ngày học việc, thử việc");
                    }
                } else {
                    rowOrder = rowIndex;
                }
                dto.setStt(rowOrder);

                // 1. Mã nhân viên
                int index = 1;
                currentCell = currentRow.getCell(index);
                String staffCode = this.parseStringCellValue(currentCell);
                if (!StringUtils.hasText(staffCode)) {
                    errorMessages.add("Không có mã nhân viên");
                }
                dto.setStaffCode(staffCode);

                // 2. Họ và tên
                index++;
                currentCell = currentRow.getCell(index);
                String fullName = this.parseStringCellValue(currentCell);
                if (StringUtils.hasText(fullName)) {
                    String[] parts = fullName.split("\\s+");

                    if (parts.length >= 2) {
                        // Có ít nhất 2 phần => tách được họ và tên
                        // Lấy phần cuối làm firstName
                        String firstName = parts[parts.length - 1];

                        // Ghép các phần từ đầu đến áp cuối làm lastName
                        StringBuilder lastNameBuilder = new StringBuilder();
                        for (int i = 0; i < parts.length - 1; i++) {
                            lastNameBuilder.append(parts[i]);
                            if (i < parts.length - 2) { // Thêm khoảng trắng giữa các phần, trừ phần cuối
                                lastNameBuilder.append(" ");
                            }
                        }

                        String lastName = lastNameBuilder.toString();

                        dto.setLastName(lastName);
                        dto.setFirstName(firstName);

                    } else {
                        // Nếu chỉ có 1 phần, không tách được
                        dto.setLastName(null);
                        dto.setFirstName(null);
                    }
                } else {
                    dto.setLastName(null);
                    dto.setFirstName(null);
                }
                if (!StringUtils.hasText(fullName)) {
                    errorMessages.add("Không có tên nhân viên");
                }
                dto.setDisplayName(fullName);

                if (rowOrder == null && !StringUtils.hasText(staffCode) && !StringUtils.hasText(fullName)) {
                    rowIndex++;
                    continue;
                }

                // 3. Mã Trạng thái làm việc
                index++;
                currentCell = currentRow.getCell(index);
                String employeeStatusCode = this.parseStringCellValue(currentCell);
                dto.setEmployeeStatusCode(employeeStatusCode);

                // 4. Trạng thái làm việc
                index++;
                currentCell = currentRow.getCell(index);
                String employeeStatusName = this.parseStringCellValue(currentCell);
                dto.setEmployeeStatusName(employeeStatusName);
                // 5. Ngày vào
                index++;
                currentCell = currentRow.getCell(index);

                dateFormat.setLenient(false);
                Date recruitmentDate = parseDateCellValue(currentCell, rowIndex, index, dateFormat);
                dto.setRecruitmentDate(recruitmentDate);

                // 6. Ngày đi làm lại
                index++;
                currentCell = currentRow.getCell(index);
                dateFormat.setLenient(false);
                Date returnDate = parseDateCellValue(currentCell, rowIndex, index, dateFormat);
                dto.setReturnDate(returnDate);

                // 7. Ngày tạm dừng/nghỉ việc
                index++;
                currentCell = currentRow.getCell(index);
                dateFormat.setLenient(false);
                Date pauseDate = parseDateCellValue(currentCell, rowIndex, index, dateFormat);
                dto.setPauseDate(pauseDate);

                // 8. Lí do nghỉ việc
                index++;
                currentCell = currentRow.getCell(index);
                String pauseReason = this.parseStringCellValue(currentCell);
                dto.setPauseReason(pauseReason);

                // 9. Mã Ban/ Chi Nhánh
                index++;
                currentCell = currentRow.getCell(index);
                String departmentCode = this.parseStringCellValue(currentCell);
                dto.setDepartmentCode(departmentCode);

                // 10. Ban/ Chi Nhánh
                index++;
                currentCell = currentRow.getCell(index);
                String departmentName = this.parseStringCellValue(currentCell);
                dto.setDepartmentName(departmentName);

                // 11. Mã nhóm ngạch
                index++;
                currentCell = currentRow.getCell(index);
                String rankGroupCode = this.parseStringCellValue(currentCell);
                dto.setRankGroupCode(rankGroupCode);

                // 12. Nhóm Ngạch
                index++;
                currentCell = currentRow.getCell(index);
                String rankGroupName = this.parseStringCellValue(currentCell);
                dto.setRankGroupName(rankGroupName);

                // 13. Cấp bậc (Level)
                index++;
                currentCell = currentRow.getCell(index);
                String rankTitleCode = this.parseStringCellValue(currentCell);
                dto.setRankTitleCode(rankTitleCode);

                // 14. Mã Chức danh
                index++;
                currentCell = currentRow.getCell(index);
                String positionTitleCode = this.parseStringCellValue(currentCell);
                dto.setPositionTitleCode(positionTitleCode);

                // 15. Chức danh
                index++;
                currentCell = currentRow.getCell(index);
                String positionTitleName = this.parseStringCellValue(currentCell);
                dto.setPositionTitleName(positionTitleName);

                // 16. Mã chức vụ
                index++;
                currentCell = currentRow.getCell(index);
                String positionCode = this.parseStringCellValue(currentCell);
                dto.setPositionCode(positionCode);

                // 17. Chức vụ
                index++;
                currentCell = currentRow.getCell(index);
                String positionName = this.parseStringCellValue(currentCell);
                dto.setPositionName(positionName);

                // 18. Hình thức làm việc
                index++;
                currentCell = currentRow.getCell(index);
                String staffWorkingFormat = this.parseStringCellValue(currentCell);
                dto.setStaffWorkingFormat(staffWorkingFormat);

//                // 19. Địa điểm làm việc
//                index++;
//                currentCell = currentRow.getCell(index);
//                String workingPlace = this.parseStringCellValue(currentCell);
//                dto.setWorkingPlace(workingPlace);

                // 20. Mã nhân viên Quản lý trực tiếp
                index++;
                currentCell = currentRow.getCell(index);
                String supervisorCode = this.parseStringCellValue(currentCell);
                dto.setSupervisorCode(supervisorCode);

                // 21. Quản lý trực tiếp
                index++;
                currentCell = currentRow.getCell(index);
                String supervisorName = this.parseStringCellValue(currentCell);
                dto.setSupervisorName(supervisorName);

                // 22. Email công ty
                index++;
                currentCell = currentRow.getCell(index);
                String companyEmail = this.parseStringCellValue(currentCell);
                dto.setCompanyEmail(companyEmail);

                // 23. Email cá nhân
                index++;
                currentCell = currentRow.getCell(index);
                String privateEmail = this.parseStringCellValue(currentCell);
                dto.setPrivateEmail(privateEmail);

                // 24. Tình trạng: TV-Thử việc, CT-Chính thức, HV-Học việc
                index++;
                currentCell = currentRow.getCell(index);
                String staffPhase = this.parseStringCellValue(currentCell);
                dto.setStaffPhase(staffPhase);

                // 25. Số HĐ (TV/HV)
                index++;
                currentCell = currentRow.getCell(index);
                String labourAgreementNumber = this.parseStringCellValue(currentCell);
                dto.setLabourAgreementNumber(labourAgreementNumber);

                // 26. Mã Công ty ký HĐ
                index++;
                currentCell = currentRow.getCell(index);
                String contractCompanyCode = this.parseStringCellValue(currentCell);
                dto.setContractCompanyCode(contractCompanyCode);

                // 27. Công ty ký HĐ
                index++;
                currentCell = currentRow.getCell(index);
                String contractCompanyName = this.parseStringCellValue(currentCell);
                dto.setContractCompanyName(contractCompanyName);

                // 28. Ngày bắt đầu (HV/TV)
                index++;
                currentCell = currentRow.getCell(index);
                dateFormat.setLenient(false);
                Date startLabourAgreementDate = parseDateCellValue(currentCell, rowIndex, index, dateFormat);
                dto.setStartLabourAgreementDate(startLabourAgreementDate);

                // 29. Số ngày HV/TV
                index++;
                currentCell = currentRow.getCell(index);
                Integer labourDays = null;
                if (currentCell != null) {
                    try {
                        if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                            labourDays = (int) currentCell.getNumericCellValue();
                        } else if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                            labourDays = Integer.valueOf(currentCell.getStringCellValue());
                        }
                    } catch (Exception e) {
                        // e.printStackTrace();
                        errorMessages.add("Không đọc được dữ liệu số ngày học việc, thử việc");
                    }
                }
                dto.setLabourDays(labourDays);

                // 30. Ngày kết thúc (HV/TV)
                index++;
                currentCell = currentRow.getCell(index);
                dateFormat.setLenient(false);
                Date endLabourAgreementDate = parseDateCellValue(currentCell, rowIndex, index, dateFormat);
                dto.setEndLabourAgreementDate(endLabourAgreementDate);

                // 31. SĐT
                index++;
                currentCell = currentRow.getCell(index);
                String phoneNumber = this.parseStringCellValue(currentCell);
                dto.setPhoneNumber(phoneNumber);

                // 32. Ngày sinh
                index++;
                currentCell = currentRow.getCell(index);
                dateFormat.setLenient(false);
                Date birthDate = parseDateCellValue(currentCell, rowIndex, index, dateFormat);
                dto.setBirthDate(birthDate);

                // 33. Giới tính
                index++;
                currentCell = currentRow.getCell(index);
                String gender = this.parseStringCellValue(currentCell);
                dto.setGender(gender);

                // 34. Mã Tỉnh_Thường trú
                index++;
                currentCell = currentRow.getCell(index);
                String provinceCode = this.parseStringCellValue(currentCell);
                dto.setProvinceCode(provinceCode);

                // 35. Tỉnh_Thường trú
                index++;
                currentCell = currentRow.getCell(index);
                String provinceName = this.parseStringCellValue(currentCell);
                dto.setProvinceName(provinceName);

                // 36. Mã Huyện_Thường trú
                index++;
                currentCell = currentRow.getCell(index);
                String districtCode = this.parseStringCellValue(currentCell);
                dto.setDistrictCode(districtCode);

                // 37. Huyện_Thường trú
                index++;
                currentCell = currentRow.getCell(index);
                String districtName = this.parseStringCellValue(currentCell);
                dto.setDistrictName(districtName);

                // 38. Mã Xã_Thường trú
                index++;
                currentCell = currentRow.getCell(index);
                String wardCode = this.parseStringCellValue(currentCell);
                dto.setWardCode(wardCode);

                // 39. Xã_Thường trú
                index++;
                currentCell = currentRow.getCell(index);
                String wardName = this.parseStringCellValue(currentCell);
                dto.setWardName(wardName);

                // 40. Chi tiết_Thường trú
                index++;
                currentCell = currentRow.getCell(index);
                String permanentResidence = this.parseStringCellValue(currentCell);
                dto.setPermanentResidence(permanentResidence);

                // 41. Thường trú chi tiết
                index++;
                currentCell = currentRow.getCell(index);
                String detailResidence = this.parseStringCellValue(currentCell);
                dto.setDetailResidence(detailResidence);

                // 42. Tạm trú
                index++;
                currentCell = currentRow.getCell(index);
                String currentResidence = this.parseStringCellValue(currentCell);
                dto.setCurrentResidence(currentResidence);

                // 43. CMND
                index++;
                currentCell = currentRow.getCell(index);
                String idNumber = this.parseStringCellValue(currentCell);
                dto.setIdNumber(idNumber);

                // 45. Ngày cấp
                index++;
                currentCell = currentRow.getCell(index);
                dateFormat.setLenient(false);
                Date idNumberIssueDate = parseDateCellValue(currentCell, rowIndex, index, dateFormat);
                dto.setIdNumberIssueDate(idNumberIssueDate);

                // 46. Nơi cấp
                index++;
                currentCell = currentRow.getCell(index);
                String idNumberIssueBy = this.parseStringCellValue(currentCell);
                dto.setIdNumberIssueBy(idNumberIssueBy);

                // 43++. CMND
                index++;
                currentCell = currentRow.getCell(index);
                String personalIdentificationNumber = this.parseStringCellValue(currentCell);
                dto.setPersonalIdentificationNumber(personalIdentificationNumber);

                // 45++. Ngày cấp
                index++;
                currentCell = currentRow.getCell(index);
                dateFormat.setLenient(false);
                Date personalIdentificationIssueDate = parseDateCellValue(currentCell, rowIndex, index, dateFormat);
                dto.setPersonalIdentificationIssueDate(personalIdentificationIssueDate);

                // 46. Nơi cấp
                index++;
                currentCell = currentRow.getCell(index);
                String personalIdentificationIssuePlace = this.parseStringCellValue(currentCell);
                dto.setPersonalIdentificationIssuePlace(personalIdentificationIssuePlace);

                // 47. Tình trạng hôn nhân
                index++;
                currentCell = currentRow.getCell(index);
                String maritalStatus = this.parseStringCellValue(currentCell);
                dto.setMaritalStatus(maritalStatus);

                // 48. Mã Dân tộc
                index++;
                currentCell = currentRow.getCell(index);
                String ethnicCode = this.parseStringCellValue(currentCell);
                dto.setEthnicCode(ethnicCode);

                // 49. Dân tộc
                index++;
                currentCell = currentRow.getCell(index);
                String ethnicStringName = this.parseStringCellValue(currentCell);
                dto.setEthnicStringName(ethnicStringName);

                // 50. Mã Tôn giáo
                index++;
                currentCell = currentRow.getCell(index);
                String religionCode = this.parseStringCellValue(currentCell);
                dto.setReligionCode(religionCode);

                // 51. Tôn giáo
                index++;
                currentCell = currentRow.getCell(index);
                String religionName = this.parseStringCellValue(currentCell);
                dto.setReligionName(religionName);

                // 52. Mã Quốc tịch
                index++;
                currentCell = currentRow.getCell(index);
                String countryCode = this.parseStringCellValue(currentCell);
                dto.setCountryCode(countryCode);

                // 53. Quốc tịch
                index++;
                currentCell = currentRow.getCell(index);
                String countryName = this.parseStringCellValue(currentCell);
                dto.setCountryName(countryName);

                // 54. Quê quán
                index++;
                currentCell = currentRow.getCell(index);
                String homeTown = this.parseStringCellValue(currentCell);
                dto.setHomeTown(homeTown);

                // 55. Mã trình độ học vấn
                index++;
                currentCell = currentRow.getCell(index);
                String educationDegreeCode = this.parseStringCellValue(currentCell);
                dto.setEducationDegreeCode(educationDegreeCode);

                // 56. Trình độ học vấn
                index++;
                currentCell = currentRow.getCell(index);
                String educationDegreeName = this.parseStringCellValue(currentCell);
                dto.setEducationDegreeName(educationDegreeName);

                // 57. Thông tin người liên hệ
                index++;
                currentCell = currentRow.getCell(index);
                String contactPersonInfo = this.parseStringCellValue(currentCell);
                dto.setContactPersonInfo(contactPersonInfo);

                // 58. Mã số Thuế
                index++;
                currentCell = currentRow.getCell(index);
                String taxCode = this.parseStringCellValue(currentCell);
                dto.setTaxCode(taxCode);

                // 59. Số Người phụ thuộc đã đăng ký (nếu có)
                index++;
                currentCell = currentRow.getCell(index);
                String dependentPeople = this.parseStringCellValue(currentCell);
                dto.setDependentPeople(dependentPeople);

                // 60. Mã số Bảo hiểm xã hội
                index++;
                currentCell = currentRow.getCell(index);
                String socialInsuranceNumber = this.parseStringCellValue(currentCell);
                dto.setSocialInsuranceNumber(socialInsuranceNumber);

                // 61. Mã số Bảo Hiểm Y tế (BHYT)
                index++;
                currentCell = currentRow.getCell(index);
                String healthInsuranceNumber = this.parseStringCellValue(currentCell);
                dto.setHealthInsuranceNumber(healthInsuranceNumber);

                // 62. Tình trạng Sổ Bảo hiểm xã hội
                index++;
                currentCell = currentRow.getCell(index);
                String socialInsuranceNote = this.parseStringCellValue(currentCell);
                dto.setSocialInsuranceNote(socialInsuranceNote);

                // 63. Nơi mong muốn đăng ký khám chữa bệnh
                index++;
                currentCell = currentRow.getCell(index);
                String desireRegistrationHealthCare = this.parseStringCellValue(currentCell);
                dto.setDesireRegistrationHealthCare(desireRegistrationHealthCare);

                // 64. Mã nhân viên người giới thiệu
                index++;
                currentCell = currentRow.getCell(index);
                String introducerCode = this.parseStringCellValue(currentCell);
                dto.setIntroducerCode(introducerCode);

                // 65. Nhân viên giới thiệu
                index++;
                currentCell = currentRow.getCell(index);
                String introducerName = this.parseStringCellValue(currentCell);
                dto.setIntroducerName(introducerName);

                // 66. Mã NV tuyển
                index++;
                currentCell = currentRow.getCell(index);
                String recruiterCode = this.parseStringCellValue(currentCell);
                dto.setRecruiterCode(recruiterCode);

                // 67. Nhân viên tuyển
                index++;
                currentCell = currentRow.getCell(index);
                String recruiterName = this.parseStringCellValue(currentCell);
                dto.setRecruiterName(recruiterName);

                // 68. Hồ sơ
                index++;
                currentCell = currentRow.getCell(index);
                String documentTemplate = this.parseStringCellValue(currentCell);
                dto.setDocumentTemplate(documentTemplate);

                // 69. Ảnh 3x4
                index++;
                currentCell = currentRow.getCell(index);
                String image34Check = this.parseStringCellValue(currentCell);
                dto.setImage34Check(image34Check);

                // 70. CMND/CCCD
                index++;
                currentCell = currentRow.getCell(index);
                String idNumberCheck = this.parseStringCellValue(currentCell);
                dto.setIdNumberCheck(idNumberCheck);

                // 71. Đơn ứng tuyển
                index++;
                currentCell = currentRow.getCell(index);
                String applicationFormCheck = this.parseStringCellValue(currentCell);
                dto.setApplicationFormCheck(applicationFormCheck);

                // 72. Sơ yếu lý lịch
                index++;
                currentCell = currentRow.getCell(index);
                String profileCheck = this.parseStringCellValue(currentCell);
                dto.setProfileCheck(profileCheck);

                // 73. Bằng cấp cao nhất
                index++;
                currentCell = currentRow.getCell(index);
                String highestDegreeCheck = this.parseStringCellValue(currentCell);
                dto.setHighestDegreeCheck(highestDegreeCheck);

                // 74. Chứng chỉ liên quan
                index++;
                currentCell = currentRow.getCell(index);
                String relatedCertificateCheck = this.parseStringCellValue(currentCell);
                dto.setRelatedCertificateCheck(relatedCertificateCheck);

                // 75. Giấy khám SK
                index++;
                currentCell = currentRow.getCell(index);
                String heathCheck = this.parseStringCellValue(currentCell);
                dto.setHeathCheck(heathCheck);

                // 76. SHK
                index++;
                currentCell = currentRow.getCell(index);
                String shkCheck = this.parseStringCellValue(currentCell);
                dto.setShkCheck(shkCheck);

                // 77. Hồ sơ khác (ghi rõ)
                index++;
                currentCell = currentRow.getCell(index);
                String otherFilesCheck = this.parseStringCellValue(currentCell);
                dto.setOtherFilesCheck(otherFilesCheck);

                // 78. Phiếu thông tin cá nhân
                index++;
                currentCell = currentRow.getCell(index);
                String personInfoCheck = this.parseStringCellValue(currentCell);
                dto.setPersonInfoCheck(personInfoCheck);

                // 79. Cam kết bảo mật thông tin
                index++;
                currentCell = currentRow.getCell(index);
                String secureInfocommitmentCheck = this.parseStringCellValue(currentCell);
                dto.setSecureInfocommitmentCheck(secureInfocommitmentCheck);

                // 80. Cam kết bảo mật thông tin thu nhập
                index++;
                currentCell = currentRow.getCell(index);
                String secureIncomeCommitmentCheck = this.parseStringCellValue(currentCell);
                dto.setSecureIncomeCommitmentCheck(secureIncomeCommitmentCheck);

                // 81. Cam kết trách nhiệm
                index++;
                currentCell = currentRow.getCell(index);
                String responsibilityCommitmentCheck = this.parseStringCellValue(currentCell);
                dto.setResponsibilityCommitmentCheck(responsibilityCommitmentCheck);

                // 82. HĐ thử việc
                index++;
                currentCell = currentRow.getCell(index);
                String probationLabourCheck = this.parseStringCellValue(currentCell);
                dto.setProbationLabourCheck(probationLabourCheck);

                // 83. Tình trạng hồ sơ
                index++;
                currentCell = currentRow.getCell(index);
                String staffDocumentStatus = this.parseStringCellValue(currentCell);
                dto.setStaffDocumentStatus(staffDocumentStatus);

                // 86. Có đóng BHXH
                index++;
                currentCell = currentRow.getCell(index);
                String hasSocialIns = this.parseStringCellValue(currentCell);
                dto.setHasSocialIns(hasSocialIns);

                // 87. Bắt buộc chấm công
                index++;
                currentCell = currentRow.getCell(index);
                String requireAttendance = this.parseStringCellValue(currentCell);
                dto.setRequireAttendance(requireAttendance);
                // 88. Cho phép chấm công ngoài công ty
                index++;
                currentCell = currentRow.getCell(index);
                String allowExternalIpTimekeeping = this.parseStringCellValue(currentCell);
                dto.setAllowExternalIpTimekeeping(allowExternalIpTimekeeping);

                // 89. Loại phân ca
                index++;
                currentCell = currentRow.getCell(index);
                try {
                    String staffWorkShiftTypeStr = parseStringCellValue(currentCell);
                    Integer staffWorkShiftType = ExcelUtils.convertToInteger(staffWorkShiftTypeStr);
                    dto.setStaffWorkShiftType(staffWorkShiftType);

                } catch (Exception exception) {

                }

                // 90. Mã ca làm việc cố định
                index++;
                currentCell = currentRow.getCell(index);
                String fixShiftWork = parseStringCellValue(currentCell);
                dto.setFixShiftWork(fixShiftWork);

                // 91. Loại nghỉ trong tháng
                index++;
                currentCell = currentRow.getCell(index);
                try {
                    String staffLeaveShiftTypeStr = parseStringCellValue(currentCell);
                    Integer staffLeaveShiftType = ExcelUtils.convertToInteger(staffLeaveShiftTypeStr);
                    dto.setStaffLeaveShiftType(staffLeaveShiftType);

                } catch (Exception exception) {

                }

                // 92. Ngày nghỉ cố định
                index++;
                currentCell = currentRow.getCell(index);
                try {
                    String fixLeaveWeekDayStr = parseStringCellValue(currentCell);
                    Integer fixLeaveWeekDay = ExcelUtils.convertToInteger(fixLeaveWeekDayStr);
                    dto.setFixLeaveWeekDay(fixLeaveWeekDay);
                } catch (Exception exception) {

                }

                // 93. Không tính đi muộn, về sớm
                index++;
                currentCell = currentRow.getCell(index);
                String skipLateEarlyCount = parseStringCellValue(currentCell);
                dto.setSkipLateEarlyCount(skipLateEarlyCount);
                // 94. Không tính làm thêm giờ
                index++;
                currentCell = currentRow.getCell(index);
                String skipOvertimeCount = parseStringCellValue(currentCell);
                dto.setSkipOvertimeCount(skipOvertimeCount);

                response.add(dto);
            }
            rowIndex++;
        }

        return response;
    }

    private Integer parseIntegerCellValue(Row currentRow, int index, int rowIndex, String columnName) {
        Cell currentCell = currentRow.getCell(index);
        String cellValue = this.parseStringCellValue(currentCell);

        if (cellValue != null && StringUtils.hasText(cellValue)) {
            try {
                return Integer.parseInt(cellValue.trim());
            } catch (NumberFormatException e) {
                System.err.println(String.format(
                        "[RowIndex: %d] [ColumnIndex: %d - %s] Lỗi chuyển đổi chuỗi sang số nguyên, Giá trị: %s",
                        rowIndex, index, columnName, cellValue));
            }
        }
        return null;
    }

    @Override
    public List<StaffDto> saveListStaffFromExcel(List<StaffDto> listStaffDto) {
        List<StaffDto> result = new ArrayList<>();

        for (StaffDto staffDto : listStaffDto) {
            // Kiểm tra mã nhân viên
            if (staffDto.getStaffCode() == null) {
                continue; // Bỏ qua nếu không có mã nhân viên
            }

            Staff staff = this.getByCode(staffDto.getStaffCode());

            // Nếu không tìm thấy staff thì kiểm tra thêm tên
            boolean hasValidName = StringUtils.hasText(staffDto.getFirstName())
                    && StringUtils.hasText(staffDto.getLastName());

            if (staff == null && hasValidName) {
                staff = new Staff();
            }

            if (staff == null) {
                continue; // Bỏ qua nếu không đủ điều kiện tạo mới staff
            }

            // 1. Mã nhân viên
            // 2. Họ và tên nhân viên
            staff.setStaffCode(staffDto.getStaffCode());
            staff.setFirstName(staffDto.getFirstName());
            staff.setLastName(staffDto.getLastName());
            staff.setDisplayName(staffDto.getDisplayName());
            // 4. Trạng thái làm việc
            if (StringUtils.hasText(staffDto.getStatusCode())) {
                List<EmployeeStatus> employeeStatusList = employeeStatusRepository.findByCode(staffDto.getStatusCode());

                if (employeeStatusList != null && !employeeStatusList.isEmpty()) {
                    staff.setStatus(employeeStatusList.get(0));
                }
            }

            if (staffDto.getRecruitmentDate() != null) {
                staff.setRecruitmentDate(staffDto.getRecruitmentDate());
            }

            if (staffDto.getEmail() != null) {
                staff.setEmail(staffDto.getEmail());
            }
            if (staffDto.getEmail() != null) {
                staff.setEmail(staffDto.getEmail());
            }
            if (staffDto.getBirthDate() != null) {
                staff.setBirthDate(staffDto.getBirthDate());
            }
            if (staffDto.getGender() != null) {
                String gender = staffDto.getGender().trim().toUpperCase();
                if ("M".equals(gender) || "F".equals(gender)) {
                    staff.setGender(gender);
                }
            }
            if (staffDto.getPermanentResidence() != null) {
                staff.setPermanentResidence(staffDto.getPermanentResidence());
            }
            if (staffDto.getCurrentResidence() != null) {
                staff.setCurrentResidence(staffDto.getCurrentResidence());
            }
            if (staffDto.getIdNumber() != null) {
                staff.setIdNumber(staffDto.getIdNumber());
            }
            if (staffDto.getIdNumberIssueDate() != null) {
                staff.setIdNumberIssueDate(staffDto.getIdNumberIssueDate());
            }
            if (staffDto.getIdNumberIssueBy() != null) {
                staff.setIdNumberIssueBy(staffDto.getIdNumberIssueBy());
            }

            if (staffDto.getMaritalStatus() != null) {
                staff.setMaritalStatus(staffDto.getMaritalStatus());
            }

            if (staffDto.getBankAccountNumber() != null) {
                staff.setBankAccountNumber(staffDto.getBankAccountNumber());
            }

            if (staffDto.getBankName() != null) {
                staff.setBankName(staffDto.getBankName());
            }

            if (staffDto.getEthnicsCode() != null) {
                Ethnics ethnics = ethnicsRepository.findByCode(staffDto.getEthnicsCode());
                if (ethnics != null) {
                    staff.setEthnics(ethnics);
                }
            }

            if (staffDto.getReligionCode() != null) {
                Religion religion = religionRepository.findByCode(staffDto.getReligionCode());
                if (religion != null) {
                    staff.setReligion(religion);
                }
            }

            if (staffDto.getNationalityCode() != null) {
                Country nationality = countryRepository.findByCode(staffDto.getNationalityCode());
                if (nationality != null) {
                    staff.setNationality(nationality);
                }
            }

            if (staffDto.getTaxCode() != null) {
                staff.setTaxCode(staffDto.getTaxCode());
            }

            if (staffDto.getHealthInsuranceNumber() != null) {
                staff.setHealthInsuranceNumber(staffDto.getHealthInsuranceNumber());
            }

            if (staffDto.getIntroducerCode() != null) {
                Staff introducer = this.getByCode(staffDto.getIntroducerCode());
                if (introducer != null) {
                    staff.setIntroducer(introducer);
                }
            }

            if (staffDto.getRecruiterCode() != null) {
                Staff recruiter = this.getByCode(staffDto.getRecruiterCode());
                if (recruiter != null) {
                    staff.setRecruiter(recruiter);
                }
            }

            if (staffDto.getStaffDocumentStatus() != null) {
                staff.setStaffDocumentStatus(staffDto.getStaffDocumentStatus());
            } else {
                staff.setStaffDocumentStatus(HrConstants.StaffDocumentStatus.UNSUBMMITED.getValue());
            }

            staff = repository.saveAndFlush(staff);

            result.add(new StaffDto(staff, false));
        }

        return result;
    }

    @Override
    public List<Staff> getListStaffByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty())
            return null;
        List<Staff> result = new ArrayList<>();
        for (UUID id : ids) {
            Staff entity = staffRepository.findById(id).orElse(null);
            if (entity != null) {
                result.add(entity);
            }
        }
        return result;
    }

    private String parseStringCellValue(Cell cell) {
        if (cell == null)
            return null;
        return switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING -> cell.getStringCellValue().trim();
            case Cell.CELL_TYPE_NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }

    private Date parseDateCellValue(Cell cell, int rowIndex, int columnIndex, SimpleDateFormat dateFormat) {
        Date result = null;

        if (cell == null) {
            return null; // hoặc return default date nếu cần
        }

        CellType cellType = cell.getCellTypeEnum();

        try {
            if (cellType == CellType.STRING) {
                String strDate = cell.getStringCellValue().trim();

                if (!strDate.isEmpty()) {
                    Date parsedDate = dateFormat.parse(strDate);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(parsedDate);

                    int year = cal.get(Calendar.YEAR);
                    if (year < 1900 || year > 2100) {
                        System.err.println(
                                String.format("[RowIndex: %d] [ColumnIndex: %d] Sai định dạng năm, Giá trị: %s",
                                        rowIndex, columnIndex, strDate));
                        return null;
                    }

                    result = parsedDate;
                }

            } else if (cellType == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    result = cell.getDateCellValue();
                } else {
                    result = DateUtil.getJavaDate(cell.getNumericCellValue());
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(result);

                int year = cal.get(Calendar.YEAR);
                if (year < 1900 || year > 2100) {
                    System.err.println(String.format("[RowIndex: %d] [ColumnIndex: %d] Sai định dạng năm, Giá trị: %s",
                            rowIndex, columnIndex, cell.getNumericCellValue()));
                    return null;
                }

            } else {
                System.err.println(String.format("[RowIndex: %d] [ColumnIndex: %d] Không hỗ trợ kiểu dữ liệu: %s",
                        rowIndex, columnIndex, cellType));
            }

        } catch (Exception ex) {
            System.err.println(String.format("[RowIndex: %d] [ColumnIndex: %d] Sai định dạng ngày tháng, Giá trị: %s",
                    rowIndex, columnIndex, cellType == CellType.STRING ? cell.getStringCellValue() : cell.toString()));
            // ex.printStackTrace();
        }

        return result;
    }

    @Override
    public Page<Staff> searchByPageEntity(SearchStaffDto dto) {
        if (dto == null) {
            return null;
        }

        // check role
        UserDto userDto = userExtService.getCurrentUser();
        boolean isUser = RoleUtils.hasRoleUser(userDto);
        boolean isRecruitment = RoleUtils.hasRoleHrRecruitment(userDto);
        boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
        boolean isAssignment = RoleUtils.hasRoleHrAssignment(userDto);
        Staff staff = userExtService.getCurrentStaffEntity();
        boolean isShiftAssignment = RoleUtils.hasShiftAssignmentPermission(userDto, staff);

        // setStaffId or setListStaffId
        StaffDto currentStaff = userExtService.getCurrentStaff();

        List<UUID> managedStaffIds = null;

        if (isAdmin) {

        } else if (isAssignment && isShiftAssignment) {
            List<UUID> listStaffId = Collections.singletonList(currentStaff.getId());

            if (dto.getLevelNumber() == null) {
                managedStaffIds = staffHierarchyService.getAllManagedStaff(currentStaff.getId(), listStaffId);
            } else {
                managedStaffIds = staffHierarchyService.getManagedStaffByLevel(currentStaff.getId(), listStaffId,
                        dto.getLevelNumber(), dto.getCollectInEachLevel());
            }

            dto.setListStaffId(managedStaffIds);
        }

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.firstName ASC, entity.lastName ASC ";

        String sql = "";
        String sqlCount = "select count(distinct entity.id) from Staff as entity ";
        if (dto.getIsExportExcel() && dto.getIsExportExcel() != null) {
            sql = "select entity from Staff as entity ";
        } else {
            sql = "select entity from Staff as entity ";
        }

        String joinPositionStaff = "";
        Boolean hasJoinMainPosition = false;
        if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null
                || (dto.getDepartmentIds() != null && !dto.getDepartmentIds().isEmpty())
                || dto.getOrganizationId() != null || dto.getRankTitleId() != null || dto.getPositionId() != null) {
            hasJoinMainPosition = true;
            joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.id ";
        }

        // Sửa lại code: 1 nhân viên có nhiều mẫu bảng lương
        SalaryPeriod salaryPeriod = null;
        if (dto.getSalaryPeriod() != null) {
            salaryPeriod = salaryPeriodRepository.findById(dto.getSalaryPeriod().getId()).orElse(null);
        } else if (dto.getSalaryPeriodId() != null) {
            salaryPeriod = salaryPeriodRepository.findById(dto.getSalaryPeriodId()).orElse(null);
        }

        if (salaryPeriod != null) {
            salaryPeriod.setFromDate(DateTimeUtil.getStartOfDay(salaryPeriod.getFromDate()));
            salaryPeriod.setToDate(DateTimeUtil.getEndOfDay(salaryPeriod.getToDate()));
        }

        if (salaryPeriod != null && dto.getSalaryTemplateId() != null) {
            // whereClause += " and EXISTS (select 1 from StaffSalaryTemplate sst " + "where
            // sst.salaryTemplate.id = :salaryTemplateId and sst.staff.id = entity.id " + "
            // and (date(sst.fromDate) <= date(:toDate) and ( date(sst.toDate) is null or
            // date(sst.toDate) >= date(:fromDate) ) ) ) ";
            whereClause += " and EXISTS (select 1 from StaffSalaryTemplate sst "
                    + "where sst.salaryTemplate.id = :salaryTemplateId and sst.staff.id = entity.id ) ";
        }

        if (dto.getIncludeVoided() == null || dto.getIncludeVoided().equals(false)) {
            whereClause += " and (entity.voided is null or entity.voided = false) ";
        }

        // keyword không phân biệt hoa thường
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( " + "LOWER(entity.displayName) LIKE LOWER(CONCAT('%', :text, '%')) "
                    + "OR LOWER(entity.staffCode) LIKE LOWER(CONCAT('%', :text, '%')) "
                    + "OR LOWER(entity.Email) LIKE LOWER(CONCAT('%', :text, '%')) "
                    + "OR LOWER(entity.phoneNumber) LIKE LOWER(CONCAT('%', :text, '%')) "
                    + "OR LOWER(CONCAT(entity.displayName, ' - ', entity.staffCode)) LIKE LOWER(CONCAT('%', :text, '%')) "
                    + ")";
        }

        if (dto.getFromBirthDate() != null) {
            whereClause += " AND ( entity.birthDate  >= :fromBirthDate ) ";
        }
        if (dto.getToBirthDate() != null) {
            whereClause += " AND ( entity.birthDate  <= :toBirthDate ) ";
        }

        if (dto.getBirthMonths() != null && !dto.getBirthMonths().isEmpty()) {
            whereClause += " AND MONTH(entity.birthDate) IN (:birthMonths) ";
        }

        if (dto.getStaffPositionType() != null) {
            whereClause += " AND entity.staffPositionType = :staffPositionType ";
        }

        // ContractOrganization - WorkOrganization theo hop dong gan nhat
        // Đơn vị ký hợp đồng
        if (dto.getContractOrganizationId() != null) {
            whereClause += " and (entity.id in ( SELECT sa.staff.id FROM StaffLabourAgreement sa"
                    + " where sa.contractOrganization.id =: contractOrganizationId "
                    + " and sa.startDate = (SELECT MAX(sa2.startDate) FROM StaffLabourAgreement sa2 WHERE sa2.staff.id = sa.staff.id ) )) ";
        }
        // Đơn vị làm việc
        if (dto.getWorkOrganizationId() != null) {
            whereClause += " and (entity.id in ( SELECT sa.staff.id FROM StaffLabourAgreement sa"
                    + " where sa.workOrganization.id =: workOrganizationId "
                    + " and sa.startDate = (SELECT MAX(sa2.startDate) FROM StaffLabourAgreement sa2 WHERE sa2.staff.id = sa.staff.id ) )) ";
        }
        // Trạng thái nhân viên
        if (dto.getEmployeeStatusId() != null && StringUtils.hasText(dto.getEmployeeStatusId().toString())) {
            whereClause += " AND ( entity.status.id  =: employeeStatusId ) ";
        }
        // Ngày bắt đầu công việc (Ngày vào làm)
        if (dto.getFromStartDate() != null) {
            whereClause += " AND ( entity.recruitmentDate  >= :fromStartDate ) ";
        }

        if (dto.getToStartDate() != null) {
            whereClause += " AND ( entity.recruitmentDate  <= :toStartDate ) ";
        }

        // Địa điểm làm việc
        // Địa điểm làm việc chính - AND swl.isMainLocation = true
        if (dto.getWorkplaceId() != null && StringUtils.hasText(dto.getWorkplaceId().toString())) {
            whereClause += " AND entity.id IN (SELECT swl.staff.id FROM StaffWorkingLocation swl WHERE swl.workplace.id = :workplaceId  ) ";
        }
        // Tình trạng nhân viên. Chi tiết: HrConstants.StaffPhase
        if (dto.getStaffPhase() != null) {
            whereClause += " AND ( entity.staffPhase  =: staffPhase ) ";
        }

        // Số hợp đồng
        if (dto.getContractNumber() != null && StringUtils.hasText(dto.getContractNumber().toString())) {
            whereClause += " AND entity.id IN (SELECT sla.staff.id FROM StaffLabourAgreement sla WHERE LOWER(sla.labourAgreementNumber) LIKE LOWER(:contractNumber)) ";
        }
        // Giới tính
        if (dto.getGender() != null && StringUtils.hasText(dto.getGender())) {
            whereClause += " AND ( entity.gender  =: gender ) ";
        }

        // Tỉnh thường trú
        if (dto.getProvinceId() != null && StringUtils.hasText(dto.getProvinceId().toString())
                && (dto.getDistrictId() == null || !StringUtils.hasText(dto.getDistrictId().toString()))
                && (dto.getCommuneId() == null || !StringUtils.hasText(dto.getCommuneId().toString()))) {
            whereClause += " AND (entity.administrativeUnit.id = :provinceId "
                    + " OR (entity.administrativeUnit.parent IS NOT NULL AND entity.administrativeUnit.parent.id = :provinceId) "
                    + " OR (entity.administrativeUnit.parent.parent IS NOT NULL AND entity.administrativeUnit.parent.parent.id = :provinceId) )";
        }

        // Huyện thường trú
        if (dto.getDistrictId() != null && StringUtils.hasText(dto.getDistrictId().toString())
                && dto.getProvinceId() != null && StringUtils.hasText(dto.getProvinceId().toString())
                && (dto.getCommuneId() == null || !StringUtils.hasText(dto.getCommuneId().toString()))) {
            whereClause += " AND (entity.administrativeUnit.id = :districtId "
                    + " OR (entity.administrativeUnit.parent IS NOT NULL AND entity.administrativeUnit.parent.id = :districtId) )";
        }

        // Xã thường trú
        if (dto.getCommuneId() != null && StringUtils.hasText(dto.getCommuneId().toString())
                && dto.getDistrictId() != null && StringUtils.hasText(dto.getDistrictId().toString())
                && dto.getProvinceId() != null && StringUtils.hasText(dto.getProvinceId().toString())) {
            whereClause += " AND entity.administrativeUnit.id = :communeId ";
        }

        // tạm trú (nơi ở hiện tại)
        if (dto.getCurrentResidence() != null && StringUtils.hasText(dto.getCurrentResidence())) {
            whereClause += " AND ( LOWER(entity.currentResidence) LIKE LOWER(:currentResidence) ) ";
        }
        // Quê quán
        if (dto.getBirthPlace() != null && StringUtils.hasText(dto.getBirthPlace())) {
            whereClause += " AND ( LOWER(entity.birthPlace) LIKE LOWER(:birthPlace) ) ";
        }
        // CMND/CCCD
        if (dto.getIdNumber() != null && StringUtils.hasText(dto.getIdNumber())) {
            whereClause += " AND ( LOWER(entity.idNumber) LIKE LOWER(:idNumber) OR LOWER(entity.personalIdentificationNumber) LIKE LOWER(:idNumber) ) ";
        }

        // Tình trạng hôn nhân
        if (dto.getMaritalStatus() != null) {
            whereClause += " AND ( entity.maritalStatus = :maritalStatus ) ";
        }

        // Mã số thuế
        if (dto.getTaxCode() != null && StringUtils.hasText(dto.getTaxCode())) {
            whereClause += " AND ( LOWER(entity.taxCode) LIKE LOWER(:taxCode) ) ";
        }

        // Mã số bảo hiểm y tế (BHYT)
        if (dto.getHealthInsuranceNumber() != null && StringUtils.hasText(dto.getHealthInsuranceNumber())) {
            whereClause += " AND ( LOWER(entity.healthInsuranceNumber) LIKE LOWER(:healthInsuranceNumber) ) ";
        }

        // Mã số BHXH
        if (dto.getSocialInsuranceNumber() != null && StringUtils.hasText(dto.getSocialInsuranceNumber())) {
            whereClause += " AND ( LOWER(entity.socialInsuranceNumber) LIKE LOWER(:socialInsuranceNumber) ) ";
        }

        // Tình trạng sổ BHXH
        if (dto.getSocialInsuranceNote() != null && StringUtils.hasText(dto.getSocialInsuranceNote())) {
            whereClause += " AND ( LOWER(entity.socialInsuranceNote) LIKE LOWER(:socialInsuranceNote) ) ";
        }

        // Có tham gia BHXH
        if (dto.getHasSocialInsuranceNumber() != null && dto.getHasSocialInsuranceNumber() == true) {
            whereClause += " AND ( entity.socialInsuranceNumber IS NOT NULL AND entity.socialInsuranceNumber <> '') ";
        }

        // Nhân viên giới thiệu nhân viên này vào làm
        if (dto.getIntroducerId() != null && StringUtils.hasText(dto.getIntroducerId().toString())) {
            whereClause += " AND ( entity.introducer.id = :introducerId ) ";
        }
        // Nhân viên quyết định tuyển dụng nhân viên này vào làm
        if (dto.getRecruiterId() != null && StringUtils.hasText(dto.getRecruiterId().toString())) {
            whereClause += " AND ( entity.recruiter.id = :recruiterId ) ";
        }
        // Tình trạng hoàn thành hồ sơ của nhân viên. Chi tiết:
        // HrConstants.StaffDocumentStatus
        if (dto.getStaffDocumentStatus() != null) {
            whereClause += " AND ( entity.staffDocumentStatus = :staffDocumentStatus ) ";
        }

        if (dto.getAcademicTitleLevel() != null) {
            whereClause += " AND ( entity.academicRank.id IN (select a.id from AcademicTitle a where a.level >= :academicTitleLevel ) ) ";
        }
        if (dto.getEducationDegreeLevel() != null) {
            whereClause += " AND ( entity.degree.id IN (select a.id from EducationDegree a where a.level >= :educationDegreeLevel ) ) ";
        }

        if (dto.getCivilServantTypeId() != null && StringUtils.hasText(dto.getCivilServantTypeId().toString())) {
            whereClause += " AND ( entity.civilServantType.id  =: civilServantTypeId ) ";
        }

        if (dto.getApprovalStatus() != null) {
            whereClause += " AND ( entity.approvalStatus = :approvalStatus ) ";
        }
        if (dto.getStaffTypeId() != null) {
            whereClause += " AND ( entity.staffType.id = :staffTypeId ) ";
        }

        if (dto.getHasSocialIns() != null && dto.getHasSocialIns()) {
            whereClause += " AND ( entity.hasSocialIns is not null and entity.hasSocialIns = true ) ";
        } else if (dto.getHasSocialIns() != null && !dto.getHasSocialIns()) {
            whereClause += " AND ( entity.hasSocialIns is null or entity.hasSocialIns = false ) ";
        }

        if (dto.getAllowExternalIpTimekeeping() != null && dto.getAllowExternalIpTimekeeping()) {
            whereClause += " AND ( entity.allowExternalIpTimekeeping = true ) ";
        }

        // handling for choosing multiple project
        if (dto.getProjectIdList() != null) {
            sql += " INNER JOIN ProjectStaff ps on entity.id = ps.staff.id ";
            sqlCount += " INNER JOIN ProjectStaff ps on entity.id = ps.staff.id ";
            whereClause += " AND (ps.project.id in :projectIdList) ";

            if (dto.getIncludeVoidedInProject() != null && dto.getIncludeVoidedInProject()) {
                whereClause += "";
            } else {
                whereClause += " and (ps.voided = false or ps.voided is null) ";
            }
        }
        // just choose a single project
        else if (dto.getProjectId() != null) {
            sql += " INNER JOIN ProjectStaff ps on entity.id = ps.staff.id ";
            sqlCount += " INNER JOIN ProjectStaff ps on entity.id = ps.staff.id ";
            whereClause += " AND (ps.project.id = :projectId)";

            if (dto.getIncludeVoidedInProject() != null && dto.getIncludeVoidedInProject()) {
                whereClause += "";
            } else {
                whereClause += " and (ps.voided = false or ps.voided is null) ";
            }
        }

        if (hasJoinMainPosition != null && hasJoinMainPosition) {
            sql += joinPositionStaff;
            sqlCount += joinPositionStaff;

            // Chức vụ (vị trí làm việc)
            if (dto.getPositionId() != null && StringUtils.hasText(dto.getPositionId().toString())) {
                whereClause += " AND ( pos.id  =: positionId ) ";
            }

            // Cấp bậc
            if (dto.getRankTitleId() != null && StringUtils.hasText(dto.getRankTitleId().toString())) {
                whereClause += " AND ( pos.title.rankTitle.id  =: rankTitleId ) ";
            }

            if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
                whereClause += " AND ( pos.department.organization.id  =: organizationId ) ";
            }
            if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
                whereClause += " AND ( pos.department.id  =: departmentId ) ";
            }
            if (dto.getDepartmentIds() != null && !dto.getDepartmentIds().isEmpty()) {
                whereClause += " AND pos.department.id IN :departmentIds ";
            }
            if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
                whereClause += " AND ( pos.title.id  =: positionTitleId ) ";
            }
        }
        if (dto.getDirectManagerId() != null && StringUtils.hasText(dto.getDirectManagerId().toString())) {
            whereClause += " AND entity.id IN ( SELECT pr.position.staff.id FROM Position p JOIN PositionRelationShip pr ON pr.supervisor.id = p.id JOIN p.staff s WHERE pr.relationshipType = 3 AND s.id = :directManagerId ) ";
        }

        if (dto.getStaffWorkShiftType() != null) {
            whereClause += " AND (entity.staffWorkShiftType = :staffWorkShiftType) ";
        }

        if (dto.getFixShiftWorkId() != null) {
            whereClause += " AND (entity.fixShiftWork.id = :fixShiftWorkId) ";
        }

        if (dto.getStaffLeaveShiftType() != null) {
            whereClause += " AND (entity.staffLeaveShiftType = :staffLeaveShiftType) ";
        }

        if (dto.getFixLeaveWeekDay() != null) {
            whereClause += " AND (entity.fixLeaveWeekDay = :fixLeaveWeekDay) ";
        }

        // list nhan vien
        if (dto.getListStaffId() != null && !dto.getListStaffId().isEmpty()) {
            whereClause += " AND entity.id IN ( :listStaffId ) ";
        }
        if (dto.getStaffId() != null) {
            whereClause += " AND (entity.id= :staffId ) ";
        }
        if (dto.getEndSocialInsFromDate() != null || dto.getEndSocialInsToDate() != null) {
            String subQueryEndSocilInsDate = "select 1 from StaffInsuranceHistory sih where sih.staff.id = entity.id ";

            if (dto.getEndSocialInsFromDate() != null) {
                subQueryEndSocilInsDate += " and date(sih.endDate) >= date(:endSocialInsFromDate) ";
            }
            if (dto.getEndSocialInsToDate() != null) {
                subQueryEndSocilInsDate += " and date(sih.endDate) <= date(:endSocialInsToDate) ";
            }

            whereClause += " and EXISTS (" + subQueryEndSocilInsDate + ")";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, Staff.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword().strip() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword().strip() + '%');
        }
        if (dto.getFromBirthDate() != null) {
            query.setParameter("fromBirthDate", dto.getFromBirthDate());
            qCount.setParameter("fromBirthDate", dto.getFromBirthDate());
        }
        if (dto.getToBirthDate() != null) {
            query.setParameter("toBirthDate", dto.getToBirthDate());
            qCount.setParameter("toBirthDate", dto.getToBirthDate());
        }
        if (dto.getBirthMonths() != null && !dto.getBirthMonths().isEmpty()) {
            query.setParameter("birthMonths", dto.getBirthMonths());
            qCount.setParameter("birthMonths", dto.getBirthMonths());
        }

        if (dto.getStaffPositionType() != null) {
            query.setParameter("staffPositionType", dto.getStaffPositionType());
            qCount.setParameter("staffPositionType", dto.getStaffPositionType());
        }

        if (dto.getContractOrganizationId() != null) {
            query.setParameter("contractOrganizationId", dto.getContractOrganizationId());
            qCount.setParameter("contractOrganizationId", dto.getContractOrganizationId());
        }
        if (dto.getWorkOrganizationId() != null) {
            query.setParameter("workOrganizationId", dto.getWorkOrganizationId());
            qCount.setParameter("workOrganizationId", dto.getWorkOrganizationId());
        }

        // organization - department - positionTitle
        if (hasJoinMainPosition != null && hasJoinMainPosition == true) {

            // Chức vụ
            if (dto.getPositionId() != null && StringUtils.hasText(dto.getPositionId().toString())) {
                query.setParameter("positionId", dto.getPositionId());
                qCount.setParameter("positionId", dto.getPositionId());
            }

            // Cấp bậc
            if (dto.getRankTitleId() != null && StringUtils.hasText(dto.getRankTitleId().toString())) {
                query.setParameter("rankTitleId", dto.getRankTitleId());
                qCount.setParameter("rankTitleId", dto.getRankTitleId());
            }

            if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
                query.setParameter("organizationId", dto.getOrganizationId());
                qCount.setParameter("organizationId", dto.getOrganizationId());
            }
            if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
                query.setParameter("departmentId", dto.getDepartmentId());
                qCount.setParameter("departmentId", dto.getDepartmentId());
            }
            if (dto.getDepartmentIds() != null && !dto.getDepartmentIds().isEmpty()) {
                query.setParameter("departmentIds", dto.getDepartmentIds());
                qCount.setParameter("departmentIds", dto.getDepartmentIds());
            }
            if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
                query.setParameter("positionTitleId", dto.getPositionTitleId());
                qCount.setParameter("positionTitleId", dto.getPositionTitleId());
            }
        }
        if (dto.getDirectManagerId() != null && StringUtils.hasText(dto.getDirectManagerId().toString())) {
            query.setParameter("directManagerId", dto.getDirectManagerId());
            qCount.setParameter("directManagerId", dto.getDirectManagerId());
        }
        if (dto.getApprovalStatus() != null) {
            query.setParameter("approvalStatus", dto.getApprovalStatus());
            qCount.setParameter("approvalStatus", dto.getApprovalStatus());
        }

        if (dto.getCivilServantTypeId() != null && StringUtils.hasText(dto.getCivilServantTypeId().toString())) {
            query.setParameter("civilServantTypeId", dto.getCivilServantTypeId());
            qCount.setParameter("civilServantTypeId", dto.getCivilServantTypeId());
        }
        if (dto.getAcademicTitleLevel() != null) {
            query.setParameter("academicTitleLevel", dto.getAcademicTitleLevel());
            qCount.setParameter("academicTitleLevel", dto.getAcademicTitleLevel());
        }
        if (dto.getEducationDegreeLevel() != null) {
            query.setParameter("educationDegreeLevel", dto.getEducationDegreeLevel());
            qCount.setParameter("educationDegreeLevel", dto.getEducationDegreeLevel());
        }
        if (dto.getEmployeeStatusId() != null && StringUtils.hasText(dto.getEmployeeStatusId().toString())) {
            query.setParameter("employeeStatusId", dto.getEmployeeStatusId());
            qCount.setParameter("employeeStatusId", dto.getEmployeeStatusId());
        }

        // Ngày bắt đầu công việc (Ngày vào làm)
        if (dto.getFromStartDate() != null) {
            query.setParameter("fromStartDate", dto.getFromStartDate());
            qCount.setParameter("fromStartDate", dto.getFromStartDate());
        }

        if (dto.getToStartDate() != null) {
            query.setParameter("toStartDate", dto.getToStartDate());
            qCount.setParameter("toStartDate", dto.getToStartDate());
        }
        // Địa điểm làm việc
        if (dto.getWorkplaceId() != null && StringUtils.hasText(dto.getWorkplaceId().toString())) {
            query.setParameter("workplaceId", dto.getWorkplaceId());
            qCount.setParameter("workplaceId", dto.getWorkplaceId());

        }
        // Tình trạng nhân viên. Chi tiết: HrConstants.StaffPhase
        if (dto.getStaffPhase() != null) {
            query.setParameter("staffPhase", dto.getStaffPhase());
            qCount.setParameter("staffPhase", dto.getStaffPhase());
        }

        // Số hợp đồng
        if (dto.getContractNumber() != null && StringUtils.hasText(dto.getContractNumber().toString())) {
            query.setParameter("contractNumber", "%" + dto.getContractNumber() + "%");
            qCount.setParameter("contractNumber", "%" + dto.getContractNumber() + "%");
        }
        // Giới tính
        if (dto.getGender() != null && StringUtils.hasText(dto.getGender())) {
            query.setParameter("gender", dto.getGender());
            qCount.setParameter("gender", dto.getGender());
        }
//        Tỉnh thường trú
        if (dto.getProvinceId() != null && StringUtils.hasText(dto.getProvinceId().toString())
                && (dto.getDistrictId() == null || !StringUtils.hasText(dto.getDistrictId().toString()))
                && (dto.getCommuneId() == null || !StringUtils.hasText(dto.getCommuneId().toString()))) {
            query.setParameter("provinceId", dto.getProvinceId());
            qCount.setParameter("provinceId", dto.getProvinceId());
        }
        // Huyện thường trú
        if (dto.getDistrictId() != null && StringUtils.hasText(dto.getDistrictId().toString())
                && dto.getProvinceId() != null && StringUtils.hasText(dto.getProvinceId().toString())
                && (dto.getCommuneId() == null || !StringUtils.hasText(dto.getCommuneId().toString()))) {
            query.setParameter("districtId", dto.getDistrictId());
            qCount.setParameter("districtId", dto.getDistrictId());
        }
        // Xã thường trú
        if (dto.getCommuneId() != null && StringUtils.hasText(dto.getCommuneId().toString())
                && dto.getDistrictId() != null && StringUtils.hasText(dto.getDistrictId().toString())
                && dto.getProvinceId() != null && StringUtils.hasText(dto.getProvinceId().toString())) {
            query.setParameter("communeId", dto.getCommuneId());
            qCount.setParameter("communeId", dto.getCommuneId());
        }
        // tạm trú (nơi ở hiện tại)
        if (dto.getCurrentResidence() != null && StringUtils.hasText(dto.getCurrentResidence())) {
            query.setParameter("currentResidence", "%" + dto.getCurrentResidence() + "%");
            qCount.setParameter("currentResidence", "%" + dto.getCurrentResidence() + "%");
        }
        // Quê quán
        if (dto.getBirthPlace() != null && StringUtils.hasText(dto.getBirthPlace())) {
            query.setParameter("birthPlace", "%" + dto.getBirthPlace() + "%");
            qCount.setParameter("birthPlace", "%" + dto.getBirthPlace() + "%");
        }
        // CMND/CCCD
        if (dto.getIdNumber() != null && StringUtils.hasText(dto.getIdNumber())) {
            query.setParameter("idNumber", "%" + dto.getIdNumber() + "%");
            qCount.setParameter("idNumber", "%" + dto.getIdNumber() + "%");
        }

        // Tình trạng hôn nhân
        if (dto.getMaritalStatus() != null) {
            query.setParameter("maritalStatus", dto.getMaritalStatus());
            qCount.setParameter("maritalStatus", dto.getMaritalStatus());
        }

        // Mã số thuế
        if (dto.getTaxCode() != null && StringUtils.hasText(dto.getTaxCode())) {
            query.setParameter("taxCode", "%" + dto.getTaxCode() + "%");
            qCount.setParameter("taxCode", "%" + dto.getTaxCode() + "%");
        }

        // Mã số bảo hiểm y tế (BHYT)
        if (dto.getHealthInsuranceNumber() != null && StringUtils.hasText(dto.getHealthInsuranceNumber())) {
            query.setParameter("healthInsuranceNumber", "%" + dto.getHealthInsuranceNumber() + "%");
            qCount.setParameter("healthInsuranceNumber", "%" + dto.getHealthInsuranceNumber() + "%");
        }

        // Mã số BHXH
        if (dto.getSocialInsuranceNumber() != null && StringUtils.hasText(dto.getSocialInsuranceNumber())) {
            query.setParameter("socialInsuranceNumber", "%" + dto.getSocialInsuranceNumber() + "%");
            qCount.setParameter("socialInsuranceNumber", "%" + dto.getSocialInsuranceNumber() + "%");
        }

        // Tình trạng sổ BHXH
        if (dto.getSocialInsuranceNote() != null && StringUtils.hasText(dto.getSocialInsuranceNote())) {
            query.setParameter("socialInsuranceNote", "%" + dto.getSocialInsuranceNote() + "%");
            qCount.setParameter("socialInsuranceNote", "%" + dto.getSocialInsuranceNote() + "%");
        }

        // Nhân viên giới thiệu nhân viên này vào làm
        if (dto.getIntroducerId() != null && StringUtils.hasText(dto.getIntroducerId().toString())) {
            query.setParameter("introducerId", dto.getIntroducerId());
            qCount.setParameter("introducerId", dto.getIntroducerId());
        }
        // Nhân viên quyết định tuyển dụng nhân viên này vào làm
        if (dto.getRecruiterId() != null && StringUtils.hasText(dto.getRecruiterId().toString())) {
            query.setParameter("recruiterId", dto.getRecruiterId());
            qCount.setParameter("recruiterId", dto.getRecruiterId());
        }
        // Tình trạng hoàn thành hồ sơ của nhân viên. Chi tiết:
        // HrConstants.StaffDocumentStatus
        if (dto.getStaffDocumentStatus() != null) {
            query.setParameter("staffDocumentStatus", dto.getStaffDocumentStatus());
            qCount.setParameter("staffDocumentStatus", dto.getStaffDocumentStatus());
        }

        if (dto.getStaffTypeId() != null) {
            query.setParameter("staffTypeId", dto.getStaffTypeId());
            qCount.setParameter("staffTypeId", dto.getStaffTypeId());
        }

        // handling for choosing multiple project
        if (dto.getProjectIdList() != null) {
            query.setParameter("projectIdList", dto.getProjectIdList());
            qCount.setParameter("projectIdList", dto.getProjectIdList());
        }
        // just choose a single project
        else if (dto.getProjectId() != null) {
            query.setParameter("projectId", dto.getProjectId());
            qCount.setParameter("projectId", dto.getProjectId());
        }

        if (salaryPeriod != null && dto.getSalaryTemplateId() != null) {
            // Chuẩn hóa fromDate và toDate
            // Date fromDate = DateTimeUtil.getStartOfDay(salaryPeriod.getFromDate());
            // Date toDate = DateTimeUtil.getEndOfDay(salaryPeriod.getToDate());

            query.setParameter("salaryTemplateId", dto.getSalaryTemplateId());
            // query.setParameter("fromDate", fromDate);
            // query.setParameter("toDate", toDate);

            qCount.setParameter("salaryTemplateId", dto.getSalaryTemplateId());
            // qCount.setParameter("fromDate", fromDate);
            // qCount.setParameter("toDate", toDate);
        }

        if (dto.getListStaffId() != null && !dto.getListStaffId().isEmpty()) {
            query.setParameter("listStaffId", dto.getListStaffId());
            qCount.setParameter("listStaffId", dto.getListStaffId());
        }
        if (dto.getStaffId() != null) {
            query.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }

        if (dto.getStaffWorkShiftType() != null) {
            query.setParameter("staffWorkShiftType", dto.getStaffWorkShiftType());
            qCount.setParameter("staffWorkShiftType", dto.getStaffWorkShiftType());
        }

        if (dto.getFixShiftWorkId() != null) {
            query.setParameter("fixShiftWorkId", dto.getFixShiftWorkId());
            qCount.setParameter("fixShiftWorkId", dto.getFixShiftWorkId());
        }

        if (dto.getStaffLeaveShiftType() != null) {
            query.setParameter("staffLeaveShiftType", dto.getStaffLeaveShiftType());
            qCount.setParameter("staffLeaveShiftType", dto.getStaffLeaveShiftType());
        }

        if (dto.getFixLeaveWeekDay() != null) {
            query.setParameter("fixLeaveWeekDay", dto.getFixLeaveWeekDay());
            qCount.setParameter("fixLeaveWeekDay", dto.getFixLeaveWeekDay());
        }

        if (dto.getEndSocialInsFromDate() != null || dto.getEndSocialInsToDate() != null) {
            if (dto.getEndSocialInsFromDate() != null) {
                query.setParameter("endSocialInsFromDate", dto.getEndSocialInsFromDate());
                qCount.setParameter("endSocialInsFromDate", dto.getEndSocialInsFromDate());
            }
            if (dto.getEndSocialInsToDate() != null) {
                query.setParameter("endSocialInsToDate", dto.getEndSocialInsToDate());
                qCount.setParameter("endSocialInsToDate", dto.getEndSocialInsToDate());
            }
        }

        List<Staff> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();

        Page<Staff> result;
        if (dto.getIsExportExcel() && dto.getIsExportExcel() != null) {
            entities = query.getResultList();
            result = new PageImpl<>(entities);
        } else {
            int startPosition = pageIndex * pageSize;
            query.setFirstResult(startPosition);
            query.setMaxResults(pageSize);
            entities = query.getResultList();

            Pageable pageable = PageRequest.of(pageIndex, pageSize);

            result = new PageImpl<>(entities, pageable, count);
        }

        return result;
    }

    @Override
    public HashMap<UUID, PermanentAddressDto> getPermanentAddressMap() {
        HashMap<UUID, PermanentAddressDto> result = new HashMap<>();

        try {
            List<Object[]> queryResults = staffRepository.findListPermanentAddress(
                    HrConstants.AdministrativeLevel.COMMUNE.getValue(),
                    HrConstants.AdministrativeLevel.DISTRICT.getValue(),
                    HrConstants.AdministrativeLevel.PROVINCE.getValue());
            for (Object[] row : queryResults) {
                try {
                    UUID staffId = row[0] != null ? UUID.fromString(row[0].toString()) : null;
                    if (staffId == null) {
                        continue;
                    }
                    String administrativeUnitCode = (String) row[1];
                    String administrativeUnitValue = (String) row[2];
                    String districtCode = (String) row[3];
                    String districtValue = (String) row[4];
                    String provinceCode = (String) row[5];
                    String provinceValue = (String) row[6];
                    PermanentAddressDto dto = new PermanentAddressDto(staffId, administrativeUnitCode,
                            administrativeUnitValue, districtCode, districtValue, provinceCode, provinceValue);
                    result.put(staffId, dto);
                } catch (Exception rowEx) {
                    System.err.println("Error processing row in getPermanentAddressMap: " + rowEx.getMessage());
                    // rowEx.printStackTrace();
                    return null;
                }
            }

        } catch (Exception ex) {
            System.err.println("Error executing getPermanentAddressMap: " + ex.getMessage());
            // ex.printStackTrace();
            return null;
        }

        return result;
    }

    @Override
    public HashMap<UUID, Integer> getNumberOfDependentsMap() {
        HashMap<UUID, Integer> result = new HashMap<>();
        try {
            List<Object[]> queryResults = staffRepository.findListNumberOfDependents();
            for (Object[] row : queryResults) {
                try {
                    UUID staffId = row[0] != null ? UUID.fromString(row[0].toString()) : null;
                    if (staffId == null) {
                        continue;
                    }
                    Long number = row[1] != null ? ((Number) row[1]).longValue() : 0L;
                    int numberOfDependents = number.intValue();
                    result.put(staffId, numberOfDependents);
                } catch (Exception rowEx) {
                    System.err.println("Error processing row in getNumberOfDependentsMap: " + rowEx.getMessage());
                    rowEx.printStackTrace();
                }
            }
        } catch (Exception ex) {
            System.err.println("Error executing getNumberOfDependentsMap: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public ApiResponse<StaffLabourAgreementDto> getLastLabourAgreement(UUID staff) {
        if (staff == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Mã nhân viên không hợp lệ!", null);
        }
        List<StaffLabourAgreementDto> list = labourAgreementRepository.getLastLabourAgreement(staff);
        if (!CollectionUtils.isEmpty(list)) {
            return new ApiResponse<>(HttpStatus.SC_OK, "OK", list.get(0));
        }
        return new ApiResponse<>(HttpStatus.SC_OK, "OK", null);
    }

    /**
     * Tính toán và cập nhật số ngày nghỉ phép năm còn lại của nhân viên.
     *
     * @param staff
     */
    @Override
    public StaffDto calculateRemainingAnnualLeave(SearchStaffDto dto) {
        if (dto == null || dto.getStaffId() == null) {
            return null; // Trả về null nếu dto hoặc staffId là null
        }

        Staff staff = staffRepository.findById(dto.getStaffId()).orElse(null);
        if (staff == null) {
            return null; // Trả về null nếu không tìm thấy nhân viên
        }

        StaffDto result = new StaffDto();
        result.setId(staff.getId());
        result.setDisplayName(staff.getDisplayName());
        result.setStaffCode(staff.getStaffCode());

        Double annualLeaveDays = HrConstants.BASE_ANNUAL_LEAVE_DAYS;

        // Kiểm tra năm của dto
        if (dto.getYear() == null) {
            return null; // Trả về null nếu năm trong dto là null
        }

        // Ngày bắt đầu của năm
        LocalDate startDateOfYear = LocalDate.of(dto.getYear(), Month.JANUARY, 1);

        // Ngày kết thúc của năm
        LocalDate endDateOfYear = LocalDate.of(dto.getYear(), Month.DECEMBER, 31);

        // Chuyển đổi sang Date nếu cần
        Date startDate = Date.from(startDateOfYear.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDateOfYear.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Kiểm tra nếu không có startDate hoặc endDate
        if (startDate == null || endDate == null) {
            return null; // Trả về null nếu startDate hoặc endDate không hợp lệ
        }

        String leaveCode = "NGHI_PHEP";
        List<StaffWorkSchedule> leaveDays = staffWorkScheduleRepository.getApprovedAnnualLeaveDays(staff.getId(),
                leaveCode, startDate, endDate);

        // Kiểm tra nếu danh sách leaveDays là null
        if (leaveDays == null) {
            leaveDays = new ArrayList<>(); // Gán một danh sách trống nếu leaveDays là null
        }

        String leaveCodeHalfDay = "NGHI_PHEP_NUA_NGAY";
        List<StaffWorkSchedule> leaveHalfDays = staffWorkScheduleRepository.getApprovedAnnualLeaveDays(staff.getId(),
                leaveCodeHalfDay, startDate, endDate);
        // Kiểm tra nếu danh sách leaveHalfDays là null
        if (leaveHalfDays == null) {
            leaveHalfDays = new ArrayList<>(); // Gán một danh sách trống nếu leaveHalfDays là null
        }
        // Lấy số lượng phần tử trong mỗi danh sách
        int totalLeaveDaysCount = leaveDays.size(); // Số lượng ngày nghỉ phép
        int totalHalfDaysCount = leaveHalfDays.size(); // Số lượng nửa ngày nghỉ phép

        // Tính tổng số ngày nghỉ phép
        Double total = totalLeaveDaysCount + 0.5 * totalHalfDaysCount;

        // Trừ vào số ngày nghỉ phép đã sử dụng
        annualLeaveDays -= total;

        // Trường hợp số ngày nghỉ phép còn lại < 0
        if (annualLeaveDays < 0) {
            annualLeaveDays = 0.0;
        }

        result.setAnnualLeaveDays(annualLeaveDays);
        return result;
    }
}
