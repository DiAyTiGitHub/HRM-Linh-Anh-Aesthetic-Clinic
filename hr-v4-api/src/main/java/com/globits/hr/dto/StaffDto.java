package com.globits.hr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.domain.AdministrativeUnit;
import com.globits.core.domain.Department;
import com.globits.core.dto.*;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.staff.StaffSocialInsuranceDto;
import com.globits.hr.utils.Const;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.security.dto.UserDto;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class StaffDto extends PersonDto {
    private static final Logger log = LoggerFactory.getLogger(StaffDto.class);
    private Boolean isManager = false;
    private UUID mainPositionId;
    private Boolean hasPosition = false;
    private String staffCode;
    private Date contractDate;
    private Date startDate;// Ngày bắt đầu công việc
    private Date recruitmentDate;// Ngày tuyển dụng
    private Integer apprenticeDays; // Số ngày học việc/thử việc
    private HrOrganizationDto organization;
    private HRDepartmentDto department;
    private String error; // Trường mới để lưu thông báo lỗi
    protected String personalIdentificationNumber;
    protected Date personalIdentificationIssueDate;
    protected String personalIdentificationIssuePlace;

    //commune
    private HrAdministrativeUnitDto administrativeunit;
    //district
    private HrAdministrativeUnitDto district;
    //province
    private HrAdministrativeUnitDto province;

    private EducationDegreeDto educationDegree; // Trình độ học vấn cao nhất
    private LanguageDto otherLanguage;
    private CivilServantTypeDto civilServantType; // Loại công chức
    private StaffTypeDto staffType; // Loại nhân viên
    private Set<PositionStaffDto> positions = new HashSet<>();
    private Integer currentWorkingStatus;// Trạng thái công việc hiện tại
    private String salaryCoefficient;// Hệ số lương
    private String jobTitle;// Tên công việc hiện tại
    //private ProfessionalDegreeDto professionalDegree;// Trình độ chuyên môn cao nhất
    private InformaticDegreeDto informaticDegree; // bằng cấp
    //private PoliticalTheoryLevelDto politicalTheoryLevel;// Trình độ lý luận chính trị
    //private StateManagementLevelDto stateManagementLevel; // trình độ quản lý nhà nước
    private EducationalManagementLevelDto educationalManagementLevel; // trình độ quản lý giáo dục
    private Date salaryStartDate;// Ngày hưởng bậc lương hiện thời
    private Set<StaffLabourAgreementDto> agreements = new HashSet<>();//hop dong lao dong
    private Set<StaffEducationHistoryDto> educationHistory;// Quá trình đào tạo
    private List<StaffFamilyRelationshipDto> familyRelationships;// Quan hệ gia đình
    private Set<StaffSalaryHistoryDto> salaryHistory;// Quá trình lương
    // private Set<StaffInsuranceHistoryDto> stafInsuranceHistory;// Quá trình bảo hiểm xh
    private Set<StaffSocialInsuranceDto> staffSocialInsurance;// Quá trình bảo hiểm xh
    private Set<PersonCertificateDto> personCertificate;// Trinh do hoc van / Chung chi hien co cua nhan vien
    private Set<StaffOverseasWorkHistoryDto> overseasWorkHistory; // quá trình công công tác
    private Set<StaffRewardHistoryDto> rewardHistory;// quá trình khen thưởng
    private Set<StaffMaternityHistoryDto> maternityHistory;// qua trinh thai san
    private Boolean isOnMaternityLeave;
    private Set<StaffAllowanceHistoryDto> allowanceHistory;
    private Set<StaffAllowanceDto> staffAllowance;
    private Set<StaffTrainingHistoryDto> trainingHistory;
    private Set<StaffWorkingHistoryDto> staffWorkingHistory;
    private Set<AllowanceSeniorityHistoryDto> allowanceSeniorityHistory;
    private Set<StaffSignatureDto> staffSignatures;
    private UserDto user;
    private String specializedName;// Chuyên ngành đào tạo (chuyên ngành chính)
    private String foreignLanguageName;// Ngữ thành thạo nhất
    private Integer graduationYear;// Năm tốt nghiệp
    //
    private String highestPosition;// Chức vụ cao nhất
    private Date dateOfReceivingPosition;// Ngày nhận Chức vụ cao nhất
    private String professionalTitles;// Chức danh chuyên môn
    private String allowanceCoefficient;// Hệ số phụ cấp
    private Date dateOfReceivingAllowance;// Ngày hưởng phụ cấp
    private ProfessionDto profession;// Công việc được giao
    private String salaryLeve;// Bậc lương
    private LabourAgreementTypeDto labourAgreementType; // Loại hợp đồng
    private EducationDegreeDto computerSkill; // Trình độ tin học
    private EducationDegreeDto englishLevel; // Trình độ tiếng anh
    private CertificateDto englishCertificate; // Chứng chỉ tiếng anh
    private Boolean ethnicLanguage;// Tiếng dân tộc
    private Boolean physicalEducationTeacher;// Giáo viên thể dục
    private String highSchoolEducation;// Giáo dục phổ thông
    private String formsOfTraining;// Hình thức đào tạo
    private String trainingPlaces;// Nơi đào tạo
    private String trainingCountry;// Nơi đào tạo
    private String qualification;// Trình độ chuyên môn
    private AcademicTitleDto academicRank; // Học vị
    private EducationDegreeDto degree; // học hàm
    private TitleConferredDto conferred; //
    private EmployeeStatusDto status; // Trạng thái nhân viên
    private String certificationScore;// Điểm chứng chỉ
    private String yearOfCertification;// Năm cấp chứng chỉ
    private String note;//
    private EducationDegreeDto otherLanguageLevel;// Trình độ ngoại ngữ khác
    private EducationDegreeDto studying;// Đang theo học
    private String yearOfRecognitionDegree;// Năm công nhận học vị
    private String yearOfRecognitionAcademicRank;// Năm công nhận học hàm
    private String yearOfConferred;
    private String currentCell; // for import excel
    private CivilServantCategoryDto civilServantCategory;// Ngạch công chức
    private CivilServantGradeDto grade;// Bậc công chức
    private String permanentResidence;// Hộ khẩu thường trú
    private String currentResidence;
    private String positionDecisionNumber;// Số quyết định chức vụ
    private PositionDto currentPosition; // Chuc vu hien tai
    private PositionTitleDto positionTitle; // Vị trí làm việc
    // import
    private String nationCode; // for import excel
    private String ethnicsCode; // for import excel
    private String religionCode; // for import excel
    private String statusCode; // for import excel
    private String departmentCode; // for import excel
    private String labourAgreementTypeCode; // for import excel
    private String civilServantCategoryCode; // for import excel
    private String civilServantTypeCode; // for import excel
    private String politicalTheoryLevelCode; // for import excel
    private String professionCode; // for import excel
    private String computerSkillCode; // for import excel
    private String englishLevelCode; // for import excel
    private String englishCertificateCode; // for import excel
    private String otherLanguageLevelCode; // for import excel
    private String academicRankCode; // for import excel
    private String degreeCode; // for import excel
    private String nationalityCode; // for import excel
    private String academicTitleCode; // for import excel
    private String educationDegreeCode; // for import excel
    private String wards; // nguyên quán
    private String familyComeFrom;// xuất thân gia đình
    private String familyPriority;// gia đình thuộc ưu tiên
    private String familyYourself;// ưu tiên bản thân
    private String username;
    private String password;

    private Boolean hasSocialIns;

    private Date startInsDate;

    private List<AssetDto> assets;

    //bhxh
    private Double insuranceSalary; // Mức lương đóng bảo hiểm xã hội
    private Double staffPercentage; // Tỷ lệ cá nhân đóng bảo hiểm xã hội
    private Double orgPercentage; // Tỷ lệ đơn vị đóng bảo hiểm xã hội
    private Double unionDuesPercentage; // Tỷ lệ khoản phí công đoàn (công ty đóng)
    private Double staffInsuranceAmount; // Số tiền cá nhân đóng
    private Double orgInsuranceAmount; // Số tiền đơn vị đóng
    private Double unionDuesAmount; // Số tiền đóng khoản phí công đoàn (công ty đóng)
    private Double totalInsuranceAmount; // Tổng tiền bảo hiểm
    private Date insuranceStartDate; // Ngày bắt đầu mức đóng
    private Date insuranceEndDate; // Ngày kết thúc mức đóng
    private Double insuranceSalaryCoefficient; // Hệ số lương đó bảo hiểm xã hội
    private String contractNumber; // Số hợp đồng hiện thời

    private Boolean allowExternalIpTimekeeping; // Cho phép chấm công ngoài

    //Thêm thông tin tài khoản ngân hàng
    private String bankAccountName; // Tên tài khoản ngân hàng
    private String bankAccountNumber; // Số tài khoản ngân hàng
    private String bankName; // Tên ngân hàng
    private String bankBranch; // Chi nhánh ngân hàng


    //Nếu là người nước ngoài
    private String workPermitNumber; // Số giấy phép lao động (cho người nước ngoài)
    private String passportNumber; // Số hộ chiếu (cho người nước ngoài)

    private String taxCode; // Mã số thuế

    private StaffDto introducer;
    private String introducerCode;
    private StaffDto recruiter;
    private String recruiterCode;

    private List<StaffDocumentItemDto> staffDocumentItems; // các tài liệu nhân viên đã nộp
    private List<HrIntroduceCostDto> staffIntroduceCosts; // các chi phí giới thiệu của nhân viên

    private Integer staffWorkingFormat; // Hình thức làm việc của nhân viên. Chi tiết: HrConstants.StaffWorkingFormat
    private String companyEmail; // Email công ty
    private Integer staffPhase; // Tình trạng nhân viên. Chi tiết: HrConstants.StaffPhase
    private String contactPersonInfo; // Thông tin người liên hệ
    // Mã số thuế
//    private String taxCode;
    private String socialInsuranceNumber;// Mã số bảo hiểm xã hội (BHXH)
    private String healthInsuranceNumber; // Mã số bảo hiểm y tế (BHYT)
    private String socialInsuranceNote; // Tình trạng sổ BHXH
    private String desireRegistrationHealthCare; // Nơi mong muốn đăng ký khám chữa bệnh
    // Nơi sinh = quê quán
//    private String birthPlace;

    // hồ sơ:
    private Integer staffDocumentStatus;
    private HrDocumentTemplateDto documentTemplate;
    private List<StaffWorkingLocationDto> staffWorkingLocations; // Địa điểm làm việc của nhân viên

    private Boolean requireAttendance; //Nhân viên có cần chấm công không không
    private HRDepartmentDto staffDepartment;//Ban
    private HRDepartmentDto staffDivision;//Phòng/Cơ sở
    private HRDepartmentDto staffTeam;//Bộ phận/Nhóm
    private List<PositionDto> positionList;
    private Boolean judgePerson = false;

    private Integer staffWorkShiftType; // Loại làm việc. HrConstants.StaffWorkShiftType
    private Integer staffLeaveShiftType; // Loại nghỉ làm việc. HrConstants.StaffLeaveShiftType

    private ShiftWorkDto fixShiftWork; // Ca làm việc cố định nếu loại làm việc của nhân viên là cố định. HrConstants.StaffWorkShiftType.FIX
    private Integer fixLeaveWeekDay; // Ngày nghỉ cố định trong tuần, có giá trị khi loại nghỉ cửa nhân viên là nghỉ cố định. HrConstants.WeekDays
    private Integer fixLeaveWeekDay2; // Ngày nghỉ cố định trong tuần, có giá trị khi loại nghỉ cửa nhân viên là nghỉ cố định. HrConstants.WeekDays

    private Boolean skipLateEarlyCount; // nhân viên không bị tính đi muộn về sớm
    private Boolean skipOvertimeCount; // nhân viên không được tính OT

    private Double annualLeaveDays;
    private Integer staffPositionType; // Loại vị trí việc làm. Chi tiết: HrConstants.StaffPositionType

    public StaffDto() {
    }

    public StaffDto(Double staffInsuranceAmount,
                    Double orgInsuranceAmount,
                    Double totalSalaryInsurance,
                    Double unionDuesAmount
    ) {
        this.staffInsuranceAmount = staffInsuranceAmount;
        this.orgInsuranceAmount = orgInsuranceAmount;
        this.unionDuesAmount = unionDuesAmount;

        if (totalSalaryInsurance != null) {

//            DecimalFormat df = new DecimalFormat("0.##"); // Keeps all significant digits
//            String stringFormat = df.format(totalSalaryInsurance);
//            cell.setValue(stringFormat);

            this.insuranceSalary = totalSalaryInsurance;
        }

        double total = 0.0;
        if (orgInsuranceAmount != null) {
            total += orgInsuranceAmount;
        }
        if (staffInsuranceAmount != null) {
            total += staffInsuranceAmount;
        }
        if (unionDuesAmount != null) {
            total += unionDuesAmount;
        }
        this.totalInsuranceAmount = total;
    }

    public StaffDto(UUID id, String staffCode, String displayName, String gender) {
        this.setId(id);
        this.setStaffCode(staffCode);
        this.setDisplayName(displayName);
        this.setGender(gender);

    }

    public StaffDto(Staff entity, boolean collapse, boolean simple) {
        if (entity == null) return;

        this.id = entity.getId();
        this.displayName = entity.getDisplayName();
        this.gender = entity.getGender();
        this.staffCode = entity.getStaffCode();
        apprenticeDays = entity.getApprenticeDays();
        this.startDate = entity.getStartDate();
        this.birthDate = entity.getBirthDate();

        if (entity.getCurrentPositions() != null && !entity.getCurrentPositions().isEmpty()) {
            hasPosition = true;
            for (Position p : entity.getCurrentPositions()) {
                if (p.getIsMain() != null && p.getIsMain()) {
                    mainPositionId = p.getId();
                    break;
                }
            }
        }
        if (entity.getCivilServantType() != null) {
            CivilServantTypeDto civilServantTypeDto = new CivilServantTypeDto();
            civilServantTypeDto.setName(entity.getCivilServantType().getName());
            this.civilServantType = civilServantTypeDto;
        }

        if (entity.getDepartment() != null) {
            HRDepartmentDto departmentDto = new HRDepartmentDto();
            departmentDto.setName(entity.getDepartment().getName());

            if (entity.getDepartment().getPositionManager() != null) {
                if (entity.getDepartment().getPositionManager().getStaff() != null) {
                    this.isManager = entity.getStaffCode().equals(entity.getDepartment().getPositionManager().getStaff().getStaffCode());
                }
            }

            this.department = departmentDto;
        }
        // lay thong tin vi tri chinh
        this.setMainPosition(entity.getCurrentPositions());

        //Lấy thông tin tài khoản
        if (entity.getUser() != null) {
            UserDto userDto = new UserDto();
            userDto.setId(entity.getUser().getId());
            userDto.setUsername(entity.getUser().getUsername());
            userDto.setEmail(entity.getUser().getEmail());
            this.user = userDto;
        }
    }

    public Boolean getIsManager() {
        return isManager;
    }

    public void setIsManager(Boolean manager) {
        isManager = manager;
    }

    public StaffDto(Staff entity) {
        super(entity);
        if (entity == null) {
            return;
        }
        id = entity.getId();
        if (entity.getCurrentPositions() != null && !entity.getCurrentPositions().isEmpty()) {
            hasPosition = true;
            for (Position p : entity.getCurrentPositions()) {
                if (p.getIsMain() != null && p.getIsMain()) {
                    mainPositionId = p.getId();
                    break;
                }
            }
        }

        if (entity.getCurrentPositions() != null && !entity.getCurrentPositions().isEmpty()) {
            for (Position position : entity.getCurrentPositions()) {
                HRDepartment department = position.getDepartment();
                if (department != null && department.getPositionManager() != null &&
                        department.getPositionManager().getStaff() != null && department.getPositionManager().getStaff().getStaffCode() != null) {
                    this.isManager = department.getPositionManager().getStaff().getStaffCode().equals(entity.getStaffCode());
                    if (isManager) {
                        break;
                    }
                }
            }
        }

        if (entity.getCurrentPositions() != null && !entity.getCurrentPositions().isEmpty()) {
            this.positionList = entity.getCurrentPositions()
                    .stream()
                    .map(PositionDto::new).toList();
        }
        this.hasSocialIns = entity.getHasSocialIns();
        staffCode = entity.getStaffCode();
        firstName = entity.getFirstName();
        lastName = entity.getLastName();
        displayName = entity.getDisplayName();
        gender = entity.getGender();
        birthDate = entity.getBirthDate();
        birthPlace = entity.getBirthPlace();
        contractDate = entity.getContractDate();
        recruitmentDate = entity.getRecruitmentDate();
        startDate = entity.getStartDate();
        apprenticeDays = entity.getApprenticeDays();
        personalIdentificationNumber = entity.getPersonalIdentificationNumber();
        personalIdentificationIssueDate = entity.getPersonalIdentificationIssueDate();
        personalIdentificationIssuePlace = entity.getPersonalIdentificationIssuePlace();
        this.startInsDate = entity.getStartInsDate();
        this.requireAttendance = entity.getRequireAttendance();
        setMaritalStatus(entity.getMaritalStatus());
        jobTitle = entity.getJobTitle();
        currentWorkingStatus = entity.getCurrentWorkingStatus();
        salaryCoefficient = entity.getSalaryCoefficient();
        salaryStartDate = entity.getSalaryStartDate();
        graduationYear = entity.getGraduationYear();
        foreignLanguageName = entity.getForeignLanguageName();
        specializedName = entity.getSpecializedName();
        highestPosition = entity.getHighestPosition();
        dateOfReceivingPosition = entity.getDateOfReceivingPosition();
        professionalTitles = entity.getProfessionalTitles();
        allowanceCoefficient = entity.getAllowanceCoefficient();
        dateOfReceivingAllowance = entity.getDateOfReceivingAllowance();
        salaryLeve = entity.getSalaryLeve();
        ethnicLanguage = entity.getEthnicLanguage();
        physicalEducationTeacher = entity.getPhysicalEducationTeacher();
        formsOfTraining = entity.getFormsOfTraining();
        trainingPlaces = entity.getTrainingCountry();
        trainingCountry = entity.getTrainingPlaces();
        highSchoolEducation = entity.getHighSchoolEducation();
        qualification = entity.getQualification();
        certificationScore = entity.getCertificationScore();
        yearOfCertification = entity.getYearOfCertification();
        yearOfRecognitionDegree = entity.getYearOfRecognitionDegree();
        yearOfRecognitionAcademicRank = entity.getYearOfRecognitionAcademicRank();
        yearOfConferred = entity.getYearOfConferred();
        permanentResidence = entity.getPermanentResidence();
        note = entity.getNote();
        staffPositionType = entity.getStaffPositionType();
        positionDecisionNumber = entity.getPositionDecisionNumber();
        currentResidence = entity.getCurrentResidence();
        wards = entity.getWards();
        familyComeFrom = entity.getFamilyComeFromString();
        familyPriority = entity.getFamilyPriority();
        familyYourself = entity.getFamilyYourself();
        staffDocumentStatus = entity.getStaffDocumentStatus();
        this.allowExternalIpTimekeeping = entity.getAllowExternalIpTimekeeping();
        this.insuranceSalaryCoefficient = entity.getInsuranceSalaryCoefficient();
        this.socialInsuranceNumber = entity.getSocialInsuranceNumber();// Số sổ bảo hiểm xã hội
        this.healthInsuranceNumber = entity.getHealthInsuranceNumber(); // Số bảo hiểm y tế (BHYT)
        this.bankAccountName = entity.getBankAccountName(); // Tên tài khoản ngân hàng
        this.bankAccountNumber = entity.getBankAccountNumber(); // Số tài khoản ngân hàng
        this.bankName = entity.getBankName(); // Tên ngân hàng
        this.bankBranch = entity.getBankBranch(); // Chi nhánh ngân hàng
        this.workPermitNumber = entity.getWorkPermitNumber(); // Số giấy phép lao động (cho người nước ngoài)
        this.passportNumber = entity.getPassportNumber(); // Số hộ chiếu (cho người nước ngoài)
        this.taxCode = entity.getTaxCode(); // Mã số thuế
        this.staffWorkingFormat = entity.getStaffWorkingFormat();
        this.companyEmail = entity.getCompanyEmail();
        this.staffPhase = entity.getStaffPhase();
        this.contactPersonInfo = entity.getContactPersonInfo();
        this.taxCode = entity.getTaxCode();
        this.socialInsuranceNote = entity.getSocialInsuranceNote();
        this.desireRegistrationHealthCare = entity.getDesireRegistrationHealthCare();
        this.staffWorkShiftType = entity.getStaffWorkShiftType(); // Loại làm việc. HrConstants.StaffWorkShiftType
        this.staffLeaveShiftType = entity.getStaffLeaveShiftType(); // Loại nghỉ làm việc. HrConstants.StaffLeaveShiftType

        this.skipOvertimeCount = entity.getSkipOvertimeCount();
        this.skipLateEarlyCount = entity.getSkipLateEarlyCount();
        this.fixLeaveWeekDay = entity.getFixLeaveWeekDay();
        this.fixLeaveWeekDay2 = entity.getFixLeaveWeekDay2();

        this.annualLeaveDays = entity.getAnnualLeaveDays();

        if (entity.getFixShiftWork() != null && entity.getStaffWorkShiftType() != null
                && entity.getStaffWorkShiftType().equals(HrConstants.StaffWorkShiftType.FIXED.getValue())) {
            this.fixShiftWork = new ShiftWorkDto();
            this.fixShiftWork.setId(entity.getFixShiftWork().getId());
            this.fixShiftWork.setCode(entity.getFixShiftWork().getCode());
            this.fixShiftWork.setName(entity.getFixShiftWork().getName());
            this.fixShiftWork.setTotalHours(entity.getFixShiftWork().getTotalHours());
        }

        if (entity.getAdministrativeUnit() != null) {
            AdministrativeUnit administrativeUnit = entity.getAdministrativeUnit();
            HrAdministrativeUnitDto administrativeUnitDto = new HrAdministrativeUnitDto(administrativeUnit, false);
            Integer level = administrativeUnitDto.getLevel();
            if (level != null) {
                if (level == 1) {
                    this.setAdministrativeunit(administrativeUnitDto);
                    if (administrativeUnit != null && administrativeUnit.getParent() != null) {
                        this.setDistrict(new HrAdministrativeUnitDto(administrativeUnit.getParent(), false));
                        if (administrativeUnit.getParent().getParent() != null) {
                            this.setProvince(new HrAdministrativeUnitDto(administrativeUnit.getParent().getParent(), false));
                        }
                    }
                } else if (level == 2) { // Cấp huyện/quận
                    this.setDistrict(administrativeUnitDto);
                    if (administrativeUnit != null && administrativeUnit.getParent() != null) {
                        this.setProvince(new HrAdministrativeUnitDto(administrativeUnit.getParent(), false));
                    }
                } else if (level == 3) { // Cấp tỉnh/thành phố
                    this.setProvince(administrativeUnitDto);
                }
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if (entity.getIdNumberIssueDate() != null && entity.getIdNumberIssueDate().before(sdf.parse("01-01-1900"))
                    || entity.getIdNumberIssueDate().after(sdf.parse("01-01-2100"))) {
                this.idNumberIssueDate = null;
            }
            if (entity.getBirthDate() != null && entity.getBirthDate().before(sdf.parse("01-01-1900"))
                    || entity.getBirthDate().after(sdf.parse("01-01-2100"))) {
                this.birthDate = null;
            } else {
                birthDate = entity.getBirthDate();
            }
            if (entity.getContractDate() != null && entity.getContractDate().before(sdf.parse("01-01-1900"))
                    || entity.getContractDate().after(sdf.parse("01-01-2100"))) {
                this.contractDate = null;
            } else {
                contractDate = entity.getContractDate();
            }
            if (entity.getRecruitmentDate() != null && entity.getRecruitmentDate().before(sdf.parse("01-01-1900"))
                    || entity.getRecruitmentDate().after(sdf.parse("01-01-2100"))) {
                this.recruitmentDate = null;
            } else {
                recruitmentDate = entity.getRecruitmentDate();
            }
            if (entity.getStartDate() != null && entity.getStartDate().before(sdf.parse("01-01-1900"))
                    || entity.getStartDate().after(sdf.parse("01-01-2100"))) {
                this.startDate = null;
            } else {
                startDate = entity.getStartDate();
            }
            if (entity.getSalaryStartDate() != null && entity.getSalaryStartDate().before(sdf.parse("01-01-1900"))
                    || entity.getSalaryStartDate().after(sdf.parse("01-01-2100"))) {
                this.salaryStartDate = null;
            } else {
                salaryStartDate = entity.getSalaryStartDate();
            }
            if (entity.getDateOfReceivingPosition() != null && entity.getDateOfReceivingPosition().before(sdf.parse("01-01-1900"))
                    || entity.getDateOfReceivingPosition().after(sdf.parse("01-01-2100"))) {
                this.dateOfReceivingPosition = null;
            } else {
                dateOfReceivingPosition = entity.getDateOfReceivingPosition();
            }
            if (entity.getDateOfReceivingAllowance().before(sdf.parse("01-01-1900"))
                    || entity.getDateOfReceivingAllowance().after(sdf.parse("01-01-2100"))) {
                this.dateOfReceivingAllowance = null;
            } else {
                dateOfReceivingAllowance = entity.getDateOfReceivingAllowance();
            }
        } catch (Exception e) {
        }
        if (entity.getStatus() != null) {
            status = new EmployeeStatusDto(entity.getStatus());
        }
        if (entity.getComputerSkill() != null) {
            computerSkill = new EducationDegreeDto(entity.getComputerSkill());
        }
        if (entity.getEnglishCertificate() != null) {
            englishCertificate = new CertificateDto(entity.getEnglishCertificate());
        }
        if (entity.getEnglishLevel() != null) {
            englishLevel = new EducationDegreeDto(entity.getEnglishLevel());
        }
        if (entity.getDegree() != null) {
            degree = new EducationDegreeDto(entity.getDegree());
        }
        if (entity.getAcademicRank() != null) {
            academicRank = new AcademicTitleDto(entity.getAcademicRank());
        }
        if (entity.getCivilServantType() != null) {
            civilServantType = new CivilServantTypeDto(entity.getCivilServantType());
        }
        if (entity.getDocumentTemplate() != null) {
            documentTemplate = new HrDocumentTemplateDto(entity.getDocumentTemplate());
        }
        if (entity.getEthnics() != null) {
            ethnics = new EthnicsDto(entity.getEthnics());
        }
        if (entity.getNationality() != null) {
            nationality = new CountryDto(entity.getNationality());
        }
        if (entity.getNativeVillage() != null) {
            nativeVillage = new AdministrativeUnitDto(entity.getNativeVillage());
        }
        if (entity.getReligion() != null) {
            religion = new ReligionDto(entity.getReligion());
        }
        if (entity.getEducationDegree() != null) {
            educationDegree = new EducationDegreeDto(entity.getEducationDegree());
        }
        if (entity.getInformaticDegree() != null) {
            informaticDegree = new InformaticDegreeDto(entity.getInformaticDegree());
        }
        if (entity.getEducationalManagementLevel() != null) {
            educationalManagementLevel = new EducationalManagementLevelDto(entity.getEducationalManagementLevel());
        }
        if (entity.getLabourAgreementType() != null) {
            labourAgreementType = new LabourAgreementTypeDto(entity.getLabourAgreementType());
        }
        if (entity.getConferred() != null) {
            conferred = new TitleConferredDto(entity.getConferred());
        }
        if (entity.getOtherLanguage() != null) {
            otherLanguage = new LanguageDto(entity.getOtherLanguage());
        }
        if (entity.getUser() != null) {
            user = new UserDto(entity.getUser());
            this.username = entity.getUser().getUsername();
        }
        if (entity.getStaffType() != null) {
            this.staffType = new StaffTypeDto(entity.getStaffType());
        }
        if (entity.getIntroducer() != null) {
            StaffDto introducer = new StaffDto(entity.getIntroducer(), false, false);
            introducer.setStatus(new EmployeeStatusDto(entity.getIntroducer().getStatus()));
            this.introducer = introducer;
        }
        if (entity.getRecruiter() != null) {
            StaffDto recruiter = new StaffDto(entity.getRecruiter(), false, false);
            recruiter.setStatus(new EmployeeStatusDto(entity.getRecruiter().getStatus()));
            this.recruiter = recruiter;
        }


        this.staffDocumentItems = new ArrayList<>();

        if (entity != null && entity.getStaffDocumentItems() != null && !entity.getStaffDocumentItems().isEmpty()) {
            for (StaffDocumentItem staffDocumentItem : entity.getStaffDocumentItems()) {
                if (staffDocumentItem != null) {
                    staffDocumentItems.add(new StaffDocumentItemDto(staffDocumentItem, false));
                }
            }

            if (this.staffDocumentItems != null) {
                Collections.sort(this.staffDocumentItems, new Comparator<StaffDocumentItemDto>() {
                    @Override
                    public int compare(StaffDocumentItemDto o1, StaffDocumentItemDto o2) {
                        // Check if o1 or o2 is null
                        if (o1 == null && o2 == null) return 0;
                        if (o1 == null) return 1;
                        if (o2 == null) return -1;

                        // Check if documentItem is null
                        if (o1.getDocumentItem() == null && o2.getDocumentItem() == null)
                            return 0;
                        if (o1.getDocumentItem() == null)
                            return 1;
                        if (o2.getDocumentItem() == null)
                            return -1;

                        Integer displayOrder1 = o1.getDocumentItem().getDisplayOrder();
                        Integer displayOrder2 = o2.getDocumentItem().getDisplayOrder();

                        // Handle null displayOrder
                        if (displayOrder1 == null && displayOrder2 == null) {
                            // If both displayOrder are null, compare by submissionDate
                            if (o1.getSubmissionDate() == null && o2.getSubmissionDate() == null)
                                return 0;
                            if (o1.getSubmissionDate() == null)
                                return 1;
                            if (o2.getSubmissionDate() == null)
                                return -1;
                            return o1.getSubmissionDate().compareTo(o2.getSubmissionDate());
                        }
                        if (displayOrder1 == null)
                            return 1;
                        if (displayOrder2 == null)
                            return -1;

                        // Compare by displayOrder first
                        int orderComparison = displayOrder1.compareTo(displayOrder2);
                        if (orderComparison != 0) {
                            return orderComparison;
                        }

                        // If displayOrder is the same, compare by submissionDate
                        if (o1.getSubmissionDate() == null && o2.getSubmissionDate() == null)
                            return 0;
                        if (o1.getSubmissionDate() == null)
                            return 1;
                        if (o2.getSubmissionDate() == null)
                            return -1;
                        return o1.getSubmissionDate().compareTo(o2.getSubmissionDate());
                    }
                });
            }
        }

        this.staffWorkingLocations = new ArrayList<>();
        if (entity.getStaffWorkingLocations() != null && !entity.getStaffWorkingLocations().isEmpty()) {
            for (StaffWorkingLocation staffWorkingLocation : entity.getStaffWorkingLocations()) {
                staffWorkingLocations.add(new StaffWorkingLocationDto(staffWorkingLocation));
            }

//            Collections.sort(this.staffWorkingLocations, new Comparator<StaffWorkingLocationDto>() {
//                @Override
//                public int compare(StaffWorkingLocationDto o1, StaffWorkingLocationDto o2) {
//                    // Check if documentItem is null
//                    if (o1.getWorkingLocation() == null && o2.getWorkingLocation() == null)
//                        return 0;
//                    if (o1.getWorkingLocation() == null)
//                        return 1;
//                    if (o2.getWorkingLocation() == null)
//                        return -1;
//
//                    int orderComparison = o1.getWorkingLocation().compareTo(o2.getWorkingLocation());
//                    if (orderComparison != 0) {
//                        return orderComparison;
//                    }
//
//                    return o1.getIsMainLocation().compareTo(o2.getIsMainLocation());
//                }
//            });
        }

        this.setMainPosition(entity.getCurrentPositions());

        if (entity.getDepartment() != null) {
            HRDepartmentDto departmentDto = new HRDepartmentDto(entity.getDepartment(), false, false);
            this.department = departmentDto;
        }
        if (entity.getOrganization() != null) {
            HrOrganizationDto org = new HrOrganizationDto(entity.getOrganization(), false, false);
            this.organization = org;
        }
    }

    public StaffDto(Staff entity, Boolean includeAll) {
        this(entity);

        id = entity.getId();
        staffCode = entity.getStaffCode();
        this.startDate = entity.getStartDate();
        firstName = entity.getFirstName();
        lastName = entity.getLastName();
        displayName = entity.getDisplayName();
        birthDate = entity.getBirthDate();
        gender = entity.getGender();
        currentResidence = entity.getCurrentResidence();
        apprenticeDays = entity.getApprenticeDays();
        this.setImagePath(entity.getImagePath());
        this.insuranceSalaryCoefficient = entity.getInsuranceSalaryCoefficient();
        this.insuranceStartDate = entity.getInsuranceStartDate();
        this.insuranceEndDate = entity.getInsuranceEndDate();
        this.email = entity.getEmail();
        this.phoneNumber = entity.getPhoneNumber();
        this.permanentResidence = entity.getPermanentResidence();
        this.allowExternalIpTimekeeping = entity.getAllowExternalIpTimekeeping();
        this.personalIdentificationNumber = entity.getPersonalIdentificationNumber();
        this.personalIdentificationIssueDate = entity.getPersonalIdentificationIssueDate();
        this.personalIdentificationIssuePlace = entity.getPersonalIdentificationIssuePlace();
        //bhxh
        this.insuranceSalary = entity.getInsuranceSalary();

        this.staffPercentage = entity.getStaffPercentage();
        this.orgPercentage = entity.getOrgPercentage();
        this.unionDuesPercentage = entity.getUnionDuesPercentage();

        this.staffInsuranceAmount = entity.getStaffInsuranceAmount();
        this.orgInsuranceAmount = entity.getOrgInsuranceAmount();
        this.unionDuesAmount = entity.getUnionDuesAmount();

        this.totalInsuranceAmount = entity.getTotalInsuranceAmount();
        this.requireAttendance = entity.getRequireAttendance();

        this.staffWorkShiftType = entity.getStaffWorkShiftType(); // Loại làm việc. HrConstants.StaffWorkShiftType
        this.staffLeaveShiftType = entity.getStaffLeaveShiftType(); // Loại nghỉ làm việc. HrConstants.StaffLeaveShiftType
        this.skipOvertimeCount = entity.getSkipOvertimeCount();
        this.skipLateEarlyCount = entity.getSkipLateEarlyCount();

        if (entity.getUser() != null) {
            username = entity.getUser().getUsername();
        }

        if (entity.getTotalInsuranceAmount() == null && entity.getStaffInsuranceAmount() != null && entity.getOrgInsuranceAmount() != null) {
            this.totalInsuranceAmount = entity.getStaffInsuranceAmount() + entity.getOrgInsuranceAmount();
        }
        if (entity.getStatus() != null) {
            this.status = new EmployeeStatusDto(entity.getStatus());
        }

        // lay thong tin vi tri chinh
        this.setMainPosition(entity.getCurrentPositions());

        if (entity.getAdministrativeUnit() != null) {
            AdministrativeUnit administrativeUnit = entity.getAdministrativeUnit();
            HrAdministrativeUnitDto administrativeUnitDto = new HrAdministrativeUnitDto(administrativeUnit, false);
            Integer level = administrativeUnitDto.getLevel();
            if (level != null) {
                if (level == HrConstants.AdministrativeLevel.COMMUNE.getValue()) {
                    this.setAdministrativeunit(administrativeUnitDto);
                    if (administrativeUnit != null && administrativeUnit.getParent() != null) {
                        this.setDistrict(new HrAdministrativeUnitDto(administrativeUnit.getParent(), false));
                        if (administrativeUnit.getParent().getParent() != null) {
                            this.setProvince(new HrAdministrativeUnitDto(administrativeUnit.getParent().getParent(), false));
                        }
                    }
                } else if (level == HrConstants.AdministrativeLevel.DISTRICT.getValue()) { // Cấp huyện/quận
                    this.setDistrict(administrativeUnitDto);
                    if (administrativeUnit != null && administrativeUnit.getParent() != null) {
                        this.setProvince(new HrAdministrativeUnitDto(administrativeUnit.getParent(), false));
                    }
                } else if (level == HrConstants.AdministrativeLevel.PROVINCE.getValue()) { // Cấp tỉnh/thành phố
                    this.setProvince(administrativeUnitDto);
                }
            }
        }
        if (entity.getStaffMaternityHistories() != null && !entity.getStaffMaternityHistories().isEmpty()) {
            this.isOnMaternityLeave = this.isCurrentlyOnMaternityLeave(entity.getStaffMaternityHistories());
        }

        if (includeAll) {
            birthPlace = entity.getBirthPlace();

            setMaritalStatus(entity.getMaritalStatus());
            jobTitle = entity.getJobTitle();
            currentWorkingStatus = entity.getCurrentWorkingStatus();
            salaryCoefficient = entity.getSalaryCoefficient();
            socialInsuranceNumber = entity.getSocialInsuranceNumber();

            graduationYear = entity.getGraduationYear();
            foreignLanguageName = entity.getForeignLanguageName();
            specializedName = entity.getSpecializedName();
            highestPosition = entity.getHighestPosition();

            professionalTitles = entity.getProfessionalTitles();
            allowanceCoefficient = entity.getAllowanceCoefficient();

            salaryLeve = entity.getSalaryLeve();
            formsOfTraining = entity.getFormsOfTraining();
            trainingPlaces = entity.getTrainingCountry();
            trainingCountry = entity.getTrainingPlaces();
            highSchoolEducation = entity.getHighSchoolEducation();
            qualification = entity.getQualification();
            yearOfRecognitionDegree = entity.getYearOfRecognitionDegree();
            yearOfRecognitionAcademicRank = entity.getYearOfRecognitionAcademicRank();
            positionDecisionNumber = entity.getPositionDecisionNumber();
            this.fixLeaveWeekDay = entity.getFixLeaveWeekDay();

            if (entity.getFixShiftWork() != null && entity.getStaffWorkShiftType() != null
                    && entity.getStaffWorkShiftType().equals(HrConstants.StaffWorkShiftType.FIXED.getValue())) {
                this.fixShiftWork = new ShiftWorkDto();
                this.fixShiftWork.setId(entity.getFixShiftWork().getId());
                this.fixShiftWork.setCode(entity.getFixShiftWork().getCode());
                this.fixShiftWork.setName(entity.getFixShiftWork().getName());
                this.fixShiftWork.setTotalHours(entity.getFixShiftWork().getTotalHours());
            }

            if (entity.getUser() != null) {
                user = new UserDto(entity.getUser());
            }
            if (entity.getLabourAgreementType() != null) {
                this.setLabourAgreementType(new LabourAgreementTypeDto(entity.getLabourAgreementType()));
            }
            if (entity.getIntroducer() != null) {
                StaffDto introducer = new StaffDto(entity.getIntroducer(), false, false);
                introducer.setStatus(new EmployeeStatusDto(entity.getIntroducer().getStatus()));
                this.introducer = introducer;
            }
            if (entity.getRecruiter() != null) {
                StaffDto recruiter = new StaffDto(entity.getRecruiter(), false, false);
                recruiter.setStatus(new EmployeeStatusDto(entity.getRecruiter().getStatus()));
                this.recruiter = recruiter;
            }

            if (entity.getStaffSocialInsurances() != null) {
                this.staffSocialInsurance = new HashSet<>();
                for (StaffSocialInsurance staffSocialInsurance : entity.getStaffSocialInsurances()) {
                    this.staffSocialInsurance.add(new StaffSocialInsuranceDto(staffSocialInsurance, false));
                }
            }

            if (entity.getStaffType() != null) {
                this.staffType = new StaffTypeDto(entity.getStaffType());
            }
            if (entity.getEducationHistory() != null) {
                this.educationHistory = new HashSet<>();
                for (StaffEducationHistory history : entity.getEducationHistory()) {
                    this.educationHistory.add(new StaffEducationHistoryDto(history));
                }
            }
            if (entity.getPositions() != null) {
                List<PositionStaffDto> list = new ArrayList<>();
                for (PositionStaff e : entity.getPositions()) {
                    list.add(new PositionStaffDto(e));
                }

                if (!list.isEmpty()) {
                    positions.addAll(list);
                }
            }
            if (entity.getAgreements() != null) {
                this.agreements = new HashSet<>();
                for (StaffLabourAgreement agreement : entity.getAgreements()) {
                    this.agreements.add(new StaffLabourAgreementDto(agreement, true));
                }
            }

            if (entity.getFamilyRelationships() != null) {
                this.familyRelationships = new ArrayList<>();
                for (StaffFamilyRelationship familyRelationship : entity.getFamilyRelationships()) {
                    this.familyRelationships.add(new StaffFamilyRelationshipDto(familyRelationship));
                }
            }


            if (entity.getSalaryHistory() != null && !entity.getSalaryHistory().isEmpty()) {
                this.salaryHistory = new HashSet<>();
                for (StaffSalaryHistory history : entity.getSalaryHistory()) {
                    this.salaryHistory.add(new StaffSalaryHistoryDto(history));
                }
            }

            if (entity.getPersonCertificate() != null) {
                this.personCertificate = new HashSet<>();
                for (PersonCertificate history : entity.getPersonCertificate()) {
                    this.personCertificate.add(new PersonCertificateDto(history));
                }
            }
            if (entity.getOverseasWorkHistory() != null) {
                this.overseasWorkHistory = new HashSet<>();
                for (StaffOverseasWorkHistory history : entity.getOverseasWorkHistory()) {
                    this.overseasWorkHistory.add(new StaffOverseasWorkHistoryDto(history));
                }
            }
            if (entity.getStaffRewardHistories() != null) {
                this.rewardHistory = new HashSet<>();
                for (StaffRewardHistory staffRewardHistory : entity.getStaffRewardHistories()) {
                    this.rewardHistory.add(new StaffRewardHistoryDto(staffRewardHistory));
                }
            }
            if (entity.getStaffMaternityHistories() != null) {
                this.maternityHistory = new HashSet<>();
                for (StaffMaternityHistory staffMaternityHistory : entity.getStaffMaternityHistories()) {
                    this.maternityHistory.add(new StaffMaternityHistoryDto(staffMaternityHistory));
                }
            }
            if (entity.getStaffAllowanceHistories() != null) {
                this.allowanceHistory = new HashSet<>();
                for (StaffAllowanceHistory staffAllowanceHistory : entity.getStaffAllowanceHistories()) {
                    this.allowanceHistory.add(new StaffAllowanceHistoryDto(staffAllowanceHistory));
                }
            }
            if (entity.getStaffSignatures() != null) {
                this.staffSignatures = new HashSet<>();
                for (StaffSignature staffSignature : entity.getStaffSignatures()) {
                    this.staffSignatures.add(new StaffSignatureDto(staffSignature, false));
                }
            }

            if (entity.getStaffTrainingHistories() != null) {
                this.trainingHistory = new HashSet<>();
                for (StaffTrainingHistory staffTrainingHistory : entity.getStaffTrainingHistories()) {
                    this.trainingHistory.add(new StaffTrainingHistoryDto(staffTrainingHistory));
                }
            }
            if (entity.getStaffWorkingHistories() != null) {
                this.staffWorkingHistory = new HashSet<>();
                for (StaffWorkingHistory staffWorkingHistory : entity.getStaffWorkingHistories()) {
                    this.staffWorkingHistory.add(new StaffWorkingHistoryDto(staffWorkingHistory));
                }
            }
            if (entity.getAllowanceSeniorityHistories() != null) {
                this.allowanceSeniorityHistory = new HashSet<>();
                for (AllowanceSeniorityHistory allowanceSeniorityHistory : entity.getAllowanceSeniorityHistories()) {
                    this.allowanceSeniorityHistory.add(new AllowanceSeniorityHistoryDto(allowanceSeniorityHistory));
                }
            }

            this.staffIntroduceCosts = new ArrayList<>();
            if (entity.getStaffIntroduceCosts() != null && !entity.getStaffIntroduceCosts().isEmpty()) {
                for (HrIntroduceCost introduceCost : entity.getStaffIntroduceCosts()) {
                    staffIntroduceCosts.add(new HrIntroduceCostDto(introduceCost));
                }

                Collections.sort(this.staffIntroduceCosts, new Comparator<HrIntroduceCostDto>() {
                    @Override
                    public int compare(HrIntroduceCostDto o1, HrIntroduceCostDto o2) {
                        // First, compare by displayOrder
                        if (o1.getPeriodOrder() == null && o2.getPeriodOrder() == null)
                            return 0;
                        if (o1.getPeriodOrder() == null)
                            return 1;
                        if (o2.getPeriodOrder() == null)
                            return -1;

                        int orderComparison = o1.getPeriodOrder().compareTo(o2.getPeriodOrder());
                        if (orderComparison != 0) {
                            return orderComparison;
                        }

                        // If displayOrder is the same, compare by displayName (handling nulls)
                        if (o1.getIntroducePeriod() == null && o2.getIntroducePeriod() == null)
                            return 0;
                        if (o1.getIntroducePeriod() == null)
                            return 1;
                        if (o2.getIntroducePeriod() == null)
                            return -1;
                        return o1.getIntroducePeriod().compareTo(o2.getIntroducePeriod());
                    }
                });
            }

            this.staffWorkingLocations = new ArrayList<>();
            if (entity.getStaffWorkingLocations() != null && !entity.getStaffWorkingLocations().isEmpty()) {
                for (StaffWorkingLocation staffWorkingLocation : entity.getStaffWorkingLocations()) {
                    staffWorkingLocations.add(new StaffWorkingLocationDto(staffWorkingLocation));
                }

//                Collections.sort(this.staffWorkingLocations, new Comparator<StaffWorkingLocationDto>() {
//                    @Override
//                    public int compare(StaffWorkingLocationDto o1, StaffWorkingLocationDto o2) {
//                        // Check if documentItem is null
//                        if (o1.getWorkingLocation() == null && o2.getWorkingLocation() == null)
//                            return 0;
//                        if (o1.getWorkingLocation() == null)
//                            return 1;
//                        if (o2.getWorkingLocation() == null)
//                            return -1;
//
//                        int orderComparison = o1.getWorkingLocation().compareTo(o2.getWorkingLocation());
//                        if (orderComparison != 0) {
//                            return orderComparison;
//                        }
//
//                        return o1.getIsMainLocation().compareTo(o2.getIsMainLocation());
//                    }
//                });
            }

            if (entity.getStaffAllowance() != null) {
                this.staffAllowance = new HashSet<>();
                for (StaffAllowance staffAllowance : entity.getStaffAllowance()) {
//            	staffAllowance.setStaff(null);
                    this.staffAllowance.add(new StaffAllowanceDto(staffAllowance));
                }
            }
        }

    }

    public static StaffDto getSimpleStaff(Staff staff) {
        StaffDto simpleStaff = new StaffDto();
        simpleStaff.setId(staff.getId());
        simpleStaff.setDisplayName(staff.getDisplayName());
        simpleStaff.setStaffCode(staff.getStaffCode());
        return simpleStaff;
    }

    // Lấy dữ liệu theo Organization - Department - CurrentPosition currentPositions (Position có isMain = true)
    public void setMainPosition(Set<Position> currentPositions) {
        if (currentPositions == null || currentPositions.isEmpty()) {
            return;
        }


        for (Position position : currentPositions) {
            if (position.getIsMain() == null || position.getIsMain().equals(false)) continue;
            //lấy vị trí hiện tại
            this.currentPosition = new PositionDto();
            this.currentPosition.setName(position.getName());
            this.currentPosition.setCode(position.getCode());
            this.currentPosition.setDescription(position.getDescription());
            if (position.getRelationships() != null && !position.getRelationships().isEmpty()) {
                this.currentPosition.setRelationships(new ArrayList<>());
                for (PositionRelationShip relationship : position.getRelationships()) {
                    PositionRelationshipDto positionRelationshipDto = new PositionRelationshipDto();
                    positionRelationshipDto.setRelationshipType(relationship.getRelationshipType());
                    if (relationship.getDepartment() != null) {
                        positionRelationshipDto.setDepartment(new HRDepartmentDto(relationship.getDepartment(), false));
                    }
                    if (relationship.getPosition() != null) {
                        positionRelationshipDto.setPosition(new PositionDto(relationship.getPosition(), false));
                    }
                    if (relationship.getSupervisor() != null) {
                        positionRelationshipDto.setSupervisor(new PositionDto(relationship.getSupervisor(), false));
                    }
                    this.currentPosition.getRelationships().add(positionRelationshipDto);
                }
            }
            if (position.getTitle() != null) {
                PositionTitle pt = position.getTitle();

                this.positionTitle = new PositionTitleDto();
                this.positionTitle.setId(pt.getId());
                this.positionTitle.setCode(pt.getCode());
                this.positionTitle.setName(pt.getName());

                if (pt.getRankTitle() != null) {
                    RankTitleDto rankTitle = new RankTitleDto();

                    rankTitle.setId(pt.getRankTitle().getId());
                    rankTitle.setName(pt.getRankTitle().getName());
                    rankTitle.setOtherName(pt.getRankTitle().getOtherName());
                    rankTitle.setShortName(pt.getRankTitle().getShortName());
                    rankTitle.setReferralFeeLevel(pt.getRankTitle().getReferralFeeLevel());

                    this.positionTitle.setRankTitle(rankTitle);
                }
            }

            if (position.getDepartment() != null) {
                this.department = new HRDepartmentDto();
                this.department.setId(position.getDepartment().getId());
                this.department.setCode(position.getDepartment().getCode());
                this.department.setName(position.getDepartment().getName());
                if (position.getDepartment().getHrdepartmentType() != null) {
                    DepartmentTypeDto departmentType = new DepartmentTypeDto();
                    departmentType.setCode(position.getDepartment().getHrdepartmentType().getCode());
                    departmentType.setName(position.getDepartment().getHrdepartmentType().getName());
                    this.department.setHrDepartmentType(departmentType);
                }
                if (position.getDepartment().getOrganization() != null) {
                    this.organization = new HrOrganizationDto();
                    this.organization.setId(position.getDepartment().getOrganization().getId());
                    this.organization.setCode(position.getDepartment().getOrganization().getCode());
                    this.organization.setName(position.getDepartment().getOrganization().getName());
                }
            }
            HRDepartment positionDepartment = position.getDepartment();
            while (positionDepartment != null) {
                if (positionDepartment.getHrdepartmentType() != null) {
                    if (positionDepartment.getHrdepartmentType().getCode() != null) {
                        if (positionDepartment.getHrdepartmentType().getCode().equals(Const.HR_DEPARTMENT_TYPE_ENUM.LPB_0004.getValue())) {
                            this.staffDepartment = new HRDepartmentDto(positionDepartment, false, false);
                        }
                        if (positionDepartment.getHrdepartmentType().getCode().equals(Const.HR_DEPARTMENT_TYPE_ENUM.LPB_0005.getValue())) {
                            this.staffDivision = new HRDepartmentDto(positionDepartment, false, false);
                        }
                        if (positionDepartment.getHrdepartmentType().getCode().equals(Const.HR_DEPARTMENT_TYPE_ENUM.LPB_0006.getValue())) {
                            this.staffDivision = new HRDepartmentDto(positionDepartment, false, false);
                        }
                        if (positionDepartment.getHrdepartmentType().getCode().equals(Const.HR_DEPARTMENT_TYPE_ENUM.LPB_0007.getValue())) {
                            this.staffTeam = new HRDepartmentDto(positionDepartment, false, false);
                        }
                        if (positionDepartment.getHrdepartmentType().getCode().equals(Const.HR_DEPARTMENT_TYPE_ENUM.LPB_0008.getValue())) {
                            this.staffTeam = new HRDepartmentDto(positionDepartment, false, false);
                        }
                    }
                }
                positionDepartment = (HRDepartment) positionDepartment.getParent();
            }

        }
    }

    private boolean isCurrentlyOnMaternityLeave(Set<StaffMaternityHistory> maternityHistory) {
        if (maternityHistory == null || maternityHistory.isEmpty()) {
            return false;
        }

        LocalDate today = LocalDate.now();

        for (StaffMaternityHistory history : maternityHistory) {
            LocalDate start = DateTimeUtil.convertDateToLocalDate(history.getMaternityLeaveStartDate());
            LocalDate end = DateTimeUtil.convertDateToLocalDate(history.getMaternityLeaveEndDate());

            if ((start != null && end != null) &&
                    (!today.isBefore(start) && !today.isAfter(end))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Staff toEntity() {
        Staff staff = new Staff();
        staff.setId(id);
        staff.setStaffCode(staffCode);
        staff.setFirstName(staffCode);
        staff.setLastName(staffCode);
        staff.setGender(gender);
        staff.setBirthDate(birthDate);
        staff.setBirthPlace(birthPlace);
        staff.setGender(gender);
        return staff;
    }


    public Boolean getSkipLateEarlyCount() {
        return skipLateEarlyCount;
    }

    public void setSkipLateEarlyCount(Boolean skipLateEarlyCount) {
        this.skipLateEarlyCount = skipLateEarlyCount;
    }

    public Boolean getSkipOvertimeCount() {
        return skipOvertimeCount;
    }

    public void setSkipOvertimeCount(Boolean skipOvertimeCount) {
        this.skipOvertimeCount = skipOvertimeCount;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getStaffDocumentStatus() {
        return staffDocumentStatus;
    }

    public void setStaffDocumentStatus(Integer staffDocumentStatus) {
        this.staffDocumentStatus = staffDocumentStatus;
    }

    public Set<StaffLabourAgreementDto> getAgreements() {
        return agreements;
    }

    public void setAgreements(Set<StaffLabourAgreementDto> agreements) {
        this.agreements = agreements;
    }

    public String getSocialInsuranceNumber() {
        return socialInsuranceNumber;
    }

    public void setSocialInsuranceNumber(String socialInsuranceNumber) {
        this.socialInsuranceNumber = socialInsuranceNumber;
    }

    public Set<StaffOverseasWorkHistoryDto> getOverseasWorkHistory() {
        return overseasWorkHistory;
    }

    public void setOverseasWorkHistory(Set<StaffOverseasWorkHistoryDto> overseasWorkHistory) {
        this.overseasWorkHistory = overseasWorkHistory;
    }

    public String getPersonalIdentificationNumber() {
        return personalIdentificationNumber;
    }

    public void setPersonalIdentificationNumber(String personalIdentificationNumber) {
        this.personalIdentificationNumber = personalIdentificationNumber;
    }

    public Date getPersonalIdentificationIssueDate() {
        return personalIdentificationIssueDate;
    }

    public void setPersonalIdentificationIssueDate(Date personalIdentificationIssueDate) {
        this.personalIdentificationIssueDate = personalIdentificationIssueDate;
    }

    public String getPersonalIdentificationIssuePlace() {
        return personalIdentificationIssuePlace;
    }

    public void setPersonalIdentificationIssuePlace(String personalIdentificationIssuePlace) {
        this.personalIdentificationIssuePlace = personalIdentificationIssuePlace;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public String getSalaryCoefficient() {
        return salaryCoefficient;
    }

    public void setSalaryCoefficient(String salaryCoefficient) {
        this.salaryCoefficient = salaryCoefficient;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public Set<PositionStaffDto> getPositions() {
        return positions;
    }

    public void setPositions(Set<PositionStaffDto> positions) {
        this.positions = positions;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String getCurrentCell() {
        return currentCell;
    }

    public void setCurrentCell(String currentCell) {
        this.currentCell = currentCell;
    }

    public Date getContractDate() {
        return contractDate;
    }

    public void setContractDate(Date contractDate) {
        this.contractDate = contractDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getRecruitmentDate() {
        return recruitmentDate;
    }

    public void setRecruitmentDate(Date recruitmentDate) {
        this.recruitmentDate = recruitmentDate;
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    public EducationDegreeDto getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(EducationDegreeDto educationDegree) {
        this.educationDegree = educationDegree;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public CivilServantTypeDto getCivilServantType() {
        return civilServantType;
    }

    public void setCivilServantType(CivilServantTypeDto civilServantType) {
        this.civilServantType = civilServantType;
    }

    public List<StaffFamilyRelationshipDto> getFamilyRelationships() {
        return familyRelationships;
    }

    public void setFamilyRelationships(List<StaffFamilyRelationshipDto> familyRelationships) {
        this.familyRelationships = familyRelationships;
    }

    public Set<StaffEducationHistoryDto> getEducationHistory() {
        return educationHistory;
    }

    public void setEducationHistory(Set<StaffEducationHistoryDto> educationHistory) {
        this.educationHistory = educationHistory;
    }

    public Set<StaffRewardHistoryDto> getRewardHistory() {
        return rewardHistory;
    }

    public void setRewardHistory(Set<StaffRewardHistoryDto> rewardHistory) {
        this.rewardHistory = rewardHistory;
    }

    public Set<StaffSalaryHistoryDto> getSalaryHistory() {
        return salaryHistory;
    }

    public void setSalaryHistory(Set<StaffSalaryHistoryDto> salaryHistory) {
        this.salaryHistory = salaryHistory;
    }

    public Integer getCurrentWorkingStatus() {
        return currentWorkingStatus;
    }

    public void setCurrentWorkingStatus(Integer currentWorkingStatus) {
        this.currentWorkingStatus = currentWorkingStatus;
    }

    // public ProfessionalDegreeDto getProfessionalDegree() {
    //     return professionalDegree;
    // }

    // public void setProfessionalDegree(ProfessionalDegreeDto professionalDegree) {
    //     this.professionalDegree = professionalDegree;
    // }

    public InformaticDegreeDto getInformaticDegree() {
        return informaticDegree;
    }

    public void setInformaticDegree(InformaticDegreeDto informaticDegree) {
        this.informaticDegree = informaticDegree;
    }

    // public PoliticalTheoryLevelDto getPoliticalTheoryLevel() {
    //     return politicalTheoryLevel;
    // }

    // public void setPoliticalTheoryLevel(PoliticalTheoryLevelDto politicalTheoryLevel) {
    //     this.politicalTheoryLevel = politicalTheoryLevel;
    // }

    public Date getSalaryStartDate() {
        return salaryStartDate;
    }

    public void setSalaryStartDate(Date salaryStartDate) {
        this.salaryStartDate = salaryStartDate;
    }

    public String getSpecializedName() {
        return specializedName;
    }

    public void setSpecializedName(String specializedName) {
        this.specializedName = specializedName;
    }

    public String getForeignLanguageName() {
        return foreignLanguageName;
    }

    public void setForeignLanguageName(String foreignLanguageName) {
        this.foreignLanguageName = foreignLanguageName;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getHighestPosition() {
        return highestPosition;
    }

    public void setHighestPosition(String highestPosition) {
        this.highestPosition = highestPosition;
    }

    public Date getDateOfReceivingPosition() {
        return dateOfReceivingPosition;
    }

    public void setDateOfReceivingPosition(Date dateOfReceivingPosition) {
        this.dateOfReceivingPosition = dateOfReceivingPosition;
    }

    public String getProfessionalTitles() {
        return professionalTitles;
    }

    public void setProfessionalTitles(String professionalTitles) {
        this.professionalTitles = professionalTitles;
    }

    public String getAllowanceCoefficient() {
        return allowanceCoefficient;
    }

    public void setAllowanceCoefficient(String allowanceCoefficient) {
        this.allowanceCoefficient = allowanceCoefficient;
    }

    public Date getDateOfReceivingAllowance() {
        return dateOfReceivingAllowance;
    }

    public void setDateOfReceivingAllowance(Date dateOfReceivingAllowance) {
        this.dateOfReceivingAllowance = dateOfReceivingAllowance;
    }

    public ProfessionDto getProfession() {
        return profession;
    }

    public void setProfession(ProfessionDto profession) {
        this.profession = profession;
    }

    public String getSalaryLeve() {
        return salaryLeve;
    }

    public void setSalaryLeve(String salaryLeve) {
        this.salaryLeve = salaryLeve;
    }

    public LabourAgreementTypeDto getLabourAgreementType() {
        return labourAgreementType;
    }

    public void setLabourAgreementType(LabourAgreementTypeDto labourAgreementType) {
        this.labourAgreementType = labourAgreementType;
    }

    public Set<StaffSocialInsuranceDto> getStaffSocialInsurance() {
        return staffSocialInsurance;
    }

    public void setStaffSocialInsurance(Set<StaffSocialInsuranceDto> staffSocialInsurance) {
        this.staffSocialInsurance = staffSocialInsurance;
    }

    public EducationDegreeDto getComputerSkill() {
        return computerSkill;
    }

    public void setComputerSkill(EducationDegreeDto computerSkill) {
        this.computerSkill = computerSkill;
    }

    public EducationDegreeDto getEnglishLevel() {
        return englishLevel;
    }

    public void setEnglishLevel(EducationDegreeDto englishLevel) {
        this.englishLevel = englishLevel;
    }

    public CertificateDto getEnglishCertificate() {
        return englishCertificate;
    }

    public void setEnglishCertificate(CertificateDto englishCertificate) {
        this.englishCertificate = englishCertificate;
    }

    public Boolean getEthnicLanguage() {
        return ethnicLanguage;
    }

    public void setEthnicLanguage(Boolean ethnicLanguage) {
        this.ethnicLanguage = ethnicLanguage;
    }

    public Boolean getPhysicalEducationTeacher() {
        return physicalEducationTeacher;
    }

    public void setPhysicalEducationTeacher(Boolean physicalEducationTeacher) {
        this.physicalEducationTeacher = physicalEducationTeacher;
    }

    public String getHighSchoolEducation() {
        return highSchoolEducation;
    }

    public void setHighSchoolEducation(String highSchoolEducation) {
        this.highSchoolEducation = highSchoolEducation;
    }

    public String getFormsOfTraining() {
        return formsOfTraining;
    }

    public void setFormsOfTraining(String formsOfTraining) {
        this.formsOfTraining = formsOfTraining;
    }

    public String getTrainingPlaces() {
        return trainingPlaces;
    }

    public void setTrainingPlaces(String trainingPlaces) {
        this.trainingPlaces = trainingPlaces;
    }

    public String getTrainingCountry() {
        return trainingCountry;
    }

    public void setTrainingCountry(String trainingCountry) {
        this.trainingCountry = trainingCountry;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public EducationDegreeDto getDegree() {
        return degree;
    }

    public void setDegree(EducationDegreeDto degree) {
        this.degree = degree;
    }

    public AcademicTitleDto getAcademicRank() {
        return academicRank;
    }

    public void setAcademicRank(AcademicTitleDto academicRank) {
        this.academicRank = academicRank;
    }

    public EmployeeStatusDto getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatusDto status) {
        this.status = status;
    }

    public String getCertificationScore() {
        return certificationScore;
    }

    public void setCertificationScore(String certificationScore) {
        this.certificationScore = certificationScore;
    }

    public String getYearOfCertification() {
        return yearOfCertification;
    }

    public void setYearOfCertification(String yearOfCertification) {
        this.yearOfCertification = yearOfCertification;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public EducationDegreeDto getOtherLanguageLevel() {
        return otherLanguageLevel;
    }

    public void setOtherLanguageProficiency(EducationDegreeDto otherLanguageLevel) {
        this.otherLanguageLevel = otherLanguageLevel;
    }

    public EducationDegreeDto getStudying() {
        return studying;
    }

    public void setStudying(EducationDegreeDto studying) {
        this.studying = studying;
    }

    public String getYearOfRecognitionDegree() {
        return yearOfRecognitionDegree;
    }

    public void setYearOfRecognitionDegree(String yearOfRecognitionDegree) {
        this.yearOfRecognitionDegree = yearOfRecognitionDegree;
    }

    public String getYearOfRecognitionAcademicRank() {
        return yearOfRecognitionAcademicRank;
    }

    public void setYearOfRecognitionAcademicRank(String yearOfRecognitionAcademicRank) {
        this.yearOfRecognitionAcademicRank = yearOfRecognitionAcademicRank;
    }

    public CivilServantCategoryDto getCivilServantCategory() {
        return civilServantCategory;
    }

    public void setCivilServantCategory(CivilServantCategoryDto civilServantCategory) {
        this.civilServantCategory = civilServantCategory;
    }

    public CivilServantGradeDto getGrade() {
        return grade;
    }

    public void setGrade(CivilServantGradeDto grade) {
        this.grade = grade;
    }

    public String getNationCode() {
        return nationCode;
    }

    public void setNationCode(String nationCode) {
        this.nationCode = nationCode;
    }

    public String getEthnicsCode() {
        return ethnicsCode;
    }

    public void setEthnicsCode(String ethnicsCode) {
        this.ethnicsCode = ethnicsCode;
    }

    public String getReligionCode() {
        return religionCode;
    }

    public void setReligionCode(String religionCode) {
        this.religionCode = religionCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getLabourAgreementTypeCode() {
        return labourAgreementTypeCode;
    }

    public void setLabourAgreementTypeCode(String labourAgreementTypeCode) {
        this.labourAgreementTypeCode = labourAgreementTypeCode;
    }

    public String getCivilServantCategoryCode() {
        return civilServantCategoryCode;
    }

    public void setCivilServantCategoryCode(String civilServantCategoryCode) {
        this.civilServantCategoryCode = civilServantCategoryCode;
    }

    public String getCivilServantTypeCode() {
        return civilServantTypeCode;
    }

    public void setCivilServantTypeCode(String civilServantTypeCode) {
        this.civilServantTypeCode = civilServantTypeCode;
    }

    public String getPoliticalTheoryLevelCode() {
        return politicalTheoryLevelCode;
    }

    public void setPoliticalTheoryLevelCode(String politicalTheoryLevelCode) {
        this.politicalTheoryLevelCode = politicalTheoryLevelCode;
    }

    public String getProfessionCode() {
        return professionCode;
    }

    public void setProfessionCode(String professionCode) {
        this.professionCode = professionCode;
    }

    public String getComputerSkillCode() {
        return computerSkillCode;
    }

    public void setComputerSkillCode(String computerSkillCode) {
        this.computerSkillCode = computerSkillCode;
    }

    public String getEnglishLevelCode() {
        return englishLevelCode;
    }

    public void setEnglishLevelCode(String englishLevelCode) {
        this.englishLevelCode = englishLevelCode;
    }

    public String getEnglishCertificateCode() {
        return englishCertificateCode;
    }

    public void setEnglishCertificateCode(String englishCertificateCode) {
        this.englishCertificateCode = englishCertificateCode;
    }

    public String getOtherLanguageLevelCode() {
        return otherLanguageLevelCode;
    }

    public void setOtherLanguageProficiencyCode(String otherLanguageProficiencyCode) {
        this.otherLanguageLevelCode = otherLanguageProficiencyCode;
    }

    public String getAcademicRankCode() {
        return academicRankCode;
    }

    public void setAcademicRankCode(String academicRankCode) {
        this.academicRankCode = academicRankCode;
    }

    public String getDegreeCode() {
        return degreeCode;
    }

    public void setDegreeCode(String degreeCode) {
        this.degreeCode = degreeCode;
    }

    public String getNationalityCode() {
        return nationalityCode;
    }

    public void setNationalityCode(String nationalityCode) {
        this.nationalityCode = nationalityCode;
    }

    public Set<PersonCertificateDto> getPersonCertificate() {
        return personCertificate;
    }

    public void setPersonCertificate(Set<PersonCertificateDto> personCertificate) {
        this.personCertificate = personCertificate;
    }

    public String getAcademicTitleCode() {
        return academicTitleCode;
    }

    public void setAcademicTitleCode(String academicTitleCode) {
        this.academicTitleCode = academicTitleCode;
    }

    public String getEducationDegreeCode() {
        return educationDegreeCode;
    }

    public void setEducationDegreeCode(String educationDegreeCode) {
        this.educationDegreeCode = educationDegreeCode;
    }

    public String getPermanentResidence() {
        return permanentResidence;
    }

    public void setPermanentResidence(String permanentResidence) {
        this.permanentResidence = permanentResidence;
    }

    public String getCurrentResidence() {
        return currentResidence;
    }

    public void setCurrentResidence(String currentResidence) {
        this.currentResidence = currentResidence;
    }

    public String getPositionDecisionNumber() {
        return positionDecisionNumber;
    }

    public void setPositionDecisionNumber(String positionDecisionNumber) {
        this.positionDecisionNumber = positionDecisionNumber;
    }

    public String getWards() {
        return wards;
    }

    public void setWards(String wards) {
        this.wards = wards;
    }

    public EducationalManagementLevelDto getEducationalManagementLevel() {
        return educationalManagementLevel;
    }

    public void setEducationalManagementLevel(
            EducationalManagementLevelDto educationalManagementLevel) {
        this.educationalManagementLevel = educationalManagementLevel;
    }

    public String getFamilyComeFrom() {
        return familyComeFrom;
    }

    public void setFamilyComeFrom(String familyComeFrom) {
        this.familyComeFrom = familyComeFrom;
    }

    public String getFamilyPriority() {
        return familyPriority;
    }

    public void setFamilyPriority(String familyPriority) {
        this.familyPriority = familyPriority;
    }

    public String getFamilyYourself() {
        return familyYourself;
    }

    public void setFamilyYourself(String familyYourself) {
        this.familyYourself = familyYourself;
    }

    public TitleConferredDto getConferred() {
        return conferred;
    }

    public void setConferred(TitleConferredDto conferred) {
        this.conferred = conferred;
    }

    public String getYearOfConferred() {
        return yearOfConferred;
    }

    public void setYearOfConferred(String yearOfConferred) {
        this.yearOfConferred = yearOfConferred;
    }

    public LanguageDto getOtherLanguage() {
        return otherLanguage;
    }

    public void setOtherLanguage(LanguageDto otherLanguage) {
        this.otherLanguage = otherLanguage;
    }

    public Set<StaffMaternityHistoryDto> getMaternityHistory() {
        return maternityHistory;
    }

    public void setMaternityHistory(Set<StaffMaternityHistoryDto> maternityHistory) {
        this.maternityHistory = maternityHistory;
    }

    public Set<StaffAllowanceHistoryDto> getAllowanceHistory() {
        return allowanceHistory;
    }

    public void setAllowanceHistory(Set<StaffAllowanceHistoryDto> allowanceHistory) {
        this.allowanceHistory = allowanceHistory;
    }

    public Set<StaffTrainingHistoryDto> getTrainingHistory() {
        return trainingHistory;
    }

    public void setTrainingHistory(Set<StaffTrainingHistoryDto> trainingHistory) {
        this.trainingHistory = trainingHistory;
    }

    public Set<StaffWorkingHistoryDto> getStaffWorkingHistory() {
        return staffWorkingHistory;
    }

    public void setStaffWorkingHistory(Set<StaffWorkingHistoryDto> staffWorkingHistory) {
        this.staffWorkingHistory = staffWorkingHistory;
    }

    public Set<AllowanceSeniorityHistoryDto> getAllowanceSeniorityHistory() {
        return allowanceSeniorityHistory;
    }

    public void setAllowanceSeniorityHistory(Set<AllowanceSeniorityHistoryDto> allowanceSeniorityHistory) {
        this.allowanceSeniorityHistory = allowanceSeniorityHistory;
    }

    public HrAdministrativeUnitDto getAdministrativeunit() {
        return administrativeunit;
    }

    public void setAdministrativeunit(HrAdministrativeUnitDto administrativeunit) {
        this.administrativeunit = administrativeunit;
    }

    public void setOtherLanguageLevel(EducationDegreeDto otherLanguageLevel) {
        this.otherLanguageLevel = otherLanguageLevel;
    }

    public void setOtherLanguageLevelCode(String otherLanguageLevelCode) {
        this.otherLanguageLevelCode = otherLanguageLevelCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<AssetDto> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetDto> assets) {
        this.assets = assets;
    }

    public HrAdministrativeUnitDto getDistrict() {
        return district;
    }

    public void setDistrict(HrAdministrativeUnitDto district) {
        this.district = district;
    }

    public HrAdministrativeUnitDto getProvince() {
        return province;
    }

    public void setProvince(HrAdministrativeUnitDto province) {
        this.province = province;
    }

    public PositionDto getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(PositionDto currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Boolean getHasSocialIns() {
        return hasSocialIns;
    }

    public void setHasSocialIns(Boolean hasSocialIns) {
        this.hasSocialIns = hasSocialIns;
    }

    public Date getStartInsDate() {
        return startInsDate;
    }

    public void setStartInsDate(Date startInsDate) {
        this.startInsDate = startInsDate;
    }

    public Double getInsuranceSalary() {
        return insuranceSalary;
    }

    public void setInsuranceSalary(Double insuranceSalary) {
        this.insuranceSalary = insuranceSalary;
    }

    public Double getStaffPercentage() {
        return staffPercentage;
    }

    public void setStaffPercentage(Double staffPercentage) {
        this.staffPercentage = staffPercentage;
    }

    public Double getOrgPercentage() {
        return orgPercentage;
    }

    public void setOrgPercentage(Double orgPercentage) {
        this.orgPercentage = orgPercentage;
    }

    public Double getStaffInsuranceAmount() {
        return staffInsuranceAmount;
    }

    public void setStaffInsuranceAmount(Double staffInsuranceAmount) {
        this.staffInsuranceAmount = staffInsuranceAmount;
    }

    public Double getOrgInsuranceAmount() {
        return orgInsuranceAmount;
    }

    public void setOrgInsuranceAmount(Double orgInsuranceAmount) {
        this.orgInsuranceAmount = orgInsuranceAmount;
    }

    public Double getTotalInsuranceAmount() {
        return totalInsuranceAmount;
    }

    public void setTotalInsuranceAmount(Double totalInsuranceAmount) {
        this.totalInsuranceAmount = totalInsuranceAmount;
    }

    public Date getInsuranceStartDate() {
        return insuranceStartDate;
    }

    public void setInsuranceStartDate(Date insuranceStartDate) {
        this.insuranceStartDate = insuranceStartDate;
    }

    public Date getInsuranceEndDate() {
        return insuranceEndDate;
    }

    public void setInsuranceEndDate(Date insuranceEndDate) {
        this.insuranceEndDate = insuranceEndDate;
    }

    public Double getInsuranceSalaryCoefficient() {
        return insuranceSalaryCoefficient;
    }

    public void setInsuranceSalaryCoefficient(Double insuranceSalaryCoefficient) {
        this.insuranceSalaryCoefficient = insuranceSalaryCoefficient;
    }

    public Double getUnionDuesPercentage() {
        return unionDuesPercentage;
    }

    public void setUnionDuesPercentage(Double unionDuesPercentage) {
        this.unionDuesPercentage = unionDuesPercentage;
    }

    public Double getUnionDuesAmount() {
        return unionDuesAmount;
    }

    public void setUnionDuesAmount(Double unionDuesAmount) {
        this.unionDuesAmount = unionDuesAmount;
    }

    public StaffTypeDto getStaffType() {
        return staffType;
    }

    public void setStaffType(StaffTypeDto staffType) {
        this.staffType = staffType;
    }

    public Set<StaffAllowanceDto> getStaffAllowance() {
        return staffAllowance;
    }

    public void setStaffAllowance(Set<StaffAllowanceDto> staffAllowance) {
        this.staffAllowance = staffAllowance;
    }

    public HrOrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganizationDto organization) {
        this.organization = organization;
    }

    public Boolean getHasPosition() {
        return hasPosition;
    }

    public void setHasPosition(Boolean hasPosition) {
        this.hasPosition = hasPosition;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public Set<StaffSignatureDto> getStaffSignatures() {
        return staffSignatures;
    }

    public void setStaffSignatures(Set<StaffSignatureDto> staffSignatures) {
        this.staffSignatures = staffSignatures;
    }

    public UUID getMainPositionId() {
        return mainPositionId;
    }

    public void setMainPositionId(UUID mainPositionId) {
        this.mainPositionId = mainPositionId;
    }

    public PositionTitleDto getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitleDto positionTitle) {
        this.positionTitle = positionTitle;
    }

    public Boolean getAllowExternalIpTimekeeping() {
        return allowExternalIpTimekeeping;
    }

    public void setAllowExternalIpTimekeeping(Boolean allowExternalIpTimekeeping) {
        this.allowExternalIpTimekeeping = allowExternalIpTimekeeping;
    }

    public String getHealthInsuranceNumber() {
        return healthInsuranceNumber;
    }

    public void setHealthInsuranceNumber(String healthInsuranceNumber) {
        this.healthInsuranceNumber = healthInsuranceNumber;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getWorkPermitNumber() {
        return workPermitNumber;
    }

    public void setWorkPermitNumber(String workPermitNumber) {
        this.workPermitNumber = workPermitNumber;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public StaffDto getIntroducer() {
        return introducer;
    }

    public void setIntroducer(StaffDto introducer) {
        this.introducer = introducer;
    }

    public StaffDto getRecruiter() {
        return recruiter;
    }

    public void setRecruiter(StaffDto recruiter) {
        this.recruiter = recruiter;
    }

    public List<HrIntroduceCostDto> getStaffIntroduceCosts() {
        return staffIntroduceCosts;
    }

    public void setStaffIntroduceCosts(List<HrIntroduceCostDto> staffIntroduceCosts) {
        this.staffIntroduceCosts = staffIntroduceCosts;
    }

    public List<StaffDocumentItemDto> getStaffDocumentItems() {
        return staffDocumentItems;
    }

    public void setStaffDocumentItems(List<StaffDocumentItemDto> staffDocumentItems) {
        this.staffDocumentItems = staffDocumentItems;
    }

    public HrDocumentTemplateDto getDocumentTemplate() {
        return documentTemplate;
    }

    public void setDocumentTemplate(HrDocumentTemplateDto documentTemplate) {
        this.documentTemplate = documentTemplate;
    }

    public String getIntroducerCode() {
        return introducerCode;
    }

    public void setIntroducerCode(String introducerCode) {
        this.introducerCode = introducerCode;
    }

    public String getRecruiterCode() {
        return recruiterCode;
    }

    public void setRecruiterCode(String recruiterCode) {
        this.recruiterCode = recruiterCode;
    }

    public Integer getStaffWorkingFormat() {
        return staffWorkingFormat;
    }

    public void setStaffWorkingFormat(Integer staffWorkingFormat) {
        this.staffWorkingFormat = staffWorkingFormat;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public Integer getStaffPhase() {
        return staffPhase;
    }

    public void setStaffPhase(Integer staffPhase) {
        this.staffPhase = staffPhase;
    }

    public String getContactPersonInfo() {
        return contactPersonInfo;
    }

    public void setContactPersonInfo(String contactPersonInfo) {
        this.contactPersonInfo = contactPersonInfo;
    }

    public String getSocialInsuranceNote() {
        return socialInsuranceNote;
    }

    public void setSocialInsuranceNote(String socialInsuranceNote) {
        this.socialInsuranceNote = socialInsuranceNote;
    }

    public String getDesireRegistrationHealthCare() {
        return desireRegistrationHealthCare;
    }

    public void setDesireRegistrationHealthCare(String desireRegistrationHealthCare) {
        this.desireRegistrationHealthCare = desireRegistrationHealthCare;
    }

    public Integer getApprenticeDays() {
        return apprenticeDays;
    }

    public void setApprenticeDays(Integer apprenticeDays) {
        this.apprenticeDays = apprenticeDays;
    }

    public List<StaffWorkingLocationDto> getStaffWorkingLocations() {
        return staffWorkingLocations;
    }

    public void setStaffWorkingLocations(List<StaffWorkingLocationDto> staffWorkingLocations) {
        this.staffWorkingLocations = staffWorkingLocations;
    }

    public Boolean getRequireAttendance() {
        return requireAttendance;
    }

    public HRDepartmentDto getStaffDepartment() {
        return staffDepartment;
    }

    public void setStaffDepartment(HRDepartmentDto staffDepartment) {
        this.staffDepartment = staffDepartment;
    }

    public HRDepartmentDto getStaffDivision() {
        return staffDivision;
    }

    public void setStaffDivision(HRDepartmentDto staffDivision) {
        this.staffDivision = staffDivision;
    }

    public HRDepartmentDto getStaffTeam() {
        return staffTeam;
    }

    public void setStaffTeam(HRDepartmentDto staffTeam) {
        this.staffTeam = staffTeam;
    }

    public List<PositionDto> getPositionList() {
        return positionList;
    }

    public void setPositionList(List<PositionDto> positionList) {
        this.positionList = positionList;
    }

    public Boolean getJudgePerson() {
        return judgePerson;
    }

    public void setJudgePerson(Boolean judgePerson) {
        this.judgePerson = judgePerson;
    }

    public void setRequireAttendance(Boolean requireAttendance) {
        this.requireAttendance = requireAttendance;
    }

    public Integer getStaffWorkShiftType() {
        return staffWorkShiftType;
    }

    public void setStaffWorkShiftType(Integer staffWorkShiftType) {
        this.staffWorkShiftType = staffWorkShiftType;
    }

    public Integer getStaffLeaveShiftType() {
        return staffLeaveShiftType;
    }

    public void setStaffLeaveShiftType(Integer staffLeaveShiftType) {
        this.staffLeaveShiftType = staffLeaveShiftType;
    }

    public ShiftWorkDto getFixShiftWork() {
        return fixShiftWork;
    }

    public void setFixShiftWork(ShiftWorkDto fixShiftWork) {
        this.fixShiftWork = fixShiftWork;
    }

    public Integer getFixLeaveWeekDay() {
        return fixLeaveWeekDay;
    }

    public void setFixLeaveWeekDay(Integer fixLeaveWeekDay) {
        this.fixLeaveWeekDay = fixLeaveWeekDay;
    }

    public Double getAnnualLeaveDays() {
        return annualLeaveDays;
    }

    public void setAnnualLeaveDays(Double annualLeaveDays) {
        this.annualLeaveDays = annualLeaveDays;
    }

    public Boolean getIsOnMaternityLeave() {
        return isOnMaternityLeave;
    }

    public void setIsOnMaternityLeave(Boolean onMaternityLeave) {
        isOnMaternityLeave = onMaternityLeave;
    }

    public Integer getFixLeaveWeekDay2() {
        return fixLeaveWeekDay2;
    }

    public void setFixLeaveWeekDay2(Integer fixLeaveWeekDay2) {
        this.fixLeaveWeekDay2 = fixLeaveWeekDay2;
    }

    public Integer getStaffPositionType() {
        return staffPositionType;
    }

    public void setStaffPositionType(Integer staffPositionType) {
        this.staffPositionType = staffPositionType;
    }
}
