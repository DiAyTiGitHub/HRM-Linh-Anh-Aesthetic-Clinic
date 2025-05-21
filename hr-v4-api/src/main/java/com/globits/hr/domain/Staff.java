package com.globits.hr.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import com.globits.core.domain.AdministrativeUnit;
import com.globits.core.domain.Person;
import com.globits.timesheet.domain.Journal;

@Entity
@Table(name = "tbl_staff")
@PrimaryKeyJoinColumn(name = "id", foreignKey = @ForeignKey(name = "staff_person", value = ConstraintMode.NO_CONSTRAINT))
@DiscriminatorValue("1")
public class Staff extends Person {
    private static final long serialVersionUID = 6014783475303579207L;

    @Column(name = "staff_code", nullable = true, unique = true)
    private String staffCode;// Mã nhân viên
    @Column(name = "contract_date")
    private Date contractDate;// Ngày hợp đồng

    @Column(name = "personal_identification_number", nullable = true)
    protected String personalIdentificationNumber;
    @Column(name = "personal_identification_issueDate", nullable = true)
    protected Date personalIdentificationIssueDate;
    @Column(name = "personal_identification_issuePlace", nullable = true)
    protected String personalIdentificationIssuePlace;

    //    @Column(name = "start_date")
//    private Date startDate; // Ngày bắt đầu công việc
    @Column(name = "recruitment_date")
    private Date recruitmentDate; // Ngày tuyển dụng
    @Column(name = "apprentice_days")
    private Integer apprenticeDays; // Số ngày học việc/thử việc

    @Column(name = "contract_number")
    private String contractNumber; // Số hợp đồng hiện thời

    @ManyToOne
    @JoinColumn(name = "civil_servant_type_id")
    private CivilServantType civilServantType;// Loại công chức

    @ManyToOne
    @JoinColumn(name = "staff_type_id")
    private StaffType staffType; // Loại nhân viên

    @Column(name = "current_working_status", nullable = true)
    private Integer currentWorkingStatus;// Trạng thái công việc hiện tại

    @ManyToOne
    @JoinColumn(name = "administrativeUnit_id")
    private AdministrativeUnit administrativeUnit;

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PositionStaff> positions = new HashSet<PositionStaff>();// Quá trình chức vụ

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffLabourAgreement> agreements;// Hợp đồng

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffSalaryHistory> salaryHistory;// Quá trình lương

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffSalaryProperty> staffSalaryProperties;// Thuộc tính lương

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffEducationHistory> educationHistory;// Quá trình đào tạo

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffOverseasWorkHistory> overseasWorkHistory;// quá trình công tác

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffFamilyRelationship> familyRelationships;// Quan hệ gia đình

//	@OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//	private Set<StaffInsuranceHistory> stafInsuranceHistory;// Quá trình bảo hiểm xh

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffSocialInsurance> staffSocialInsurances;// Quá trình bảo hiểm xh

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffAllowanceHistory> staffAllowanceHistories;

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffAllowance> staffAllowance;

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PersonCertificate> personCertificate;// Trinh do hoc van / chung chi cua nhan vien

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TimeSheetStaff> timeSheets;

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffRewardHistory> staffRewardHistories;// quá trình khen thưởng
    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffMaternityHistory> staffMaternityHistories; //
    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffTrainingHistory> staffTrainingHistories; // quá trình bồi dưỡng nhân viên
    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffWorkingHistory> staffWorkingHistories;
    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AllowanceSeniorityHistory> allowanceSeniorityHistories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_degree_id")
    private EducationDegree educationDegree;// Trình độ học vấn
    @ManyToOne
    @JoinColumn(name = "other_language_id")
    private Language otherLanguage;
    // @ManyToOne
//    @JoinColumn(name = "professional_degree_id")
//    private ProfessionalDegree professionalDegree;// Trình độ chuyên môn cao nhất
    @ManyToOne
    @JoinColumn(name = "informatic_degree_id")
    private InformaticDegree informaticDegree;// Bằng cấp
//    @ManyToOne
//    @JoinColumn(name = "political_theory_level")
//    private PoliticalTheoryLevel politicalTheoryLevel;// Trình độ lý luận chính trị
//    @ManyToOne
//    @JoinColumn(name = "state_management_level_id")
//    private StateManagementLevel stateManagementLevel;// Trình độ quản lý nhà nước

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "educational_management_level_id")
    private EducationalManagementLevel educationalManagementLevel;// Trình độ quản lý giáo dục

    @Column(name = "job_title")
    private String jobTitle;// Tên công việc hiện tại

    @Column(name = "salary_coefficient")
    private String salaryCoefficient;// Hệ số lương

    @Column(name = "salary_leve")
    private String salaryLeve;// Bậc lương

    @Column(name = "salary_start_date")
    private Date salaryStartDate;// Ngày hưởng bậc lương hiện thời

    @Column(name = "specializedName")
    private String specializedName;// Chuyên ngành đào tạo (chuyên ngành chính)

    @Column(name = "foreign_language_name")
    private String foreignLanguageName;// Ngữ thành thạo nhất

    @Column(name = "graduation_year")
    private Integer graduationYear;// Năm tốt nghiệp

    @Column(name = "permanent_residence")
    private String permanentResidence;// Hộ khẩu thường trú

    @Column(name = "highest_position")
    private String highestPosition;// Chức vụ cao nhất

    @Column(name = "date_of_receiving_position")
    private Date dateOfReceivingPosition;// Ngày nhận Chức vụ cao nhất

    @Column(name = "professional_titles")
    private String professionalTitles;// Chức danh chuyên môn

    @Column(name = "allowance_coefficient")
    private String allowanceCoefficient;// Hệ số phụ cấp

    @Column(name = "date_of_receiving_allowance")
    private Date dateOfReceivingAllowance;// Ngày hưởng phụ cấp

//    @ManyToOne
//    @JoinColumn(name = "profession_id")
//    private Profession profession;// Công việc được giao

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "labour_agreement_type_id")
    private LabourAgreementType labourAgreementType; // Loại hợp đồng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_degree_computer_skill")
    private EducationDegree computerSkill; // Trình độ tin học

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_degree_english_level")
    private EducationDegree englishLevel; // Trình độ tiếng anh

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "english_certificate")
    private Certificate englishCertificate; // Chứng chỉ tiếng anh

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "degree")
    private EducationDegree degree; // Học vị

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conferred")
    private TitleConferred conferred; //

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status")
    private EmployeeStatus status; // Trạng thái nhân viên

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_rank")
    private AcademicTitle academicRank; // học hàm

    @Column(name = "ethnic_language")
    private Boolean ethnicLanguage;// Tiếng dân tộc

    @Column(name = "physical_education_teacher")
    private Boolean physicalEducationTeacher;// Giáo viên thể dục

    @Column(name = "high_school_education")
    private String highSchoolEducation;// Giáo dục phổ thông

    @Column(name = "forms_of_training")
    private String formsOfTraining;// Hình thức đào tạo

    @Column(name = "training_places")
    private String trainingPlaces;// Nơi đào tạo

    @Column(name = "training_country")
    private String trainingCountry;// Nơi đào tạo

    @Column(name = "qualification")
    private String qualification;// Trình độ chuyên môn

    @Column(name = "certification_score")
    private String certificationScore;// Điểm chứng chỉ
    @Column(name = "year_of_certification")
    private String yearOfCertification;// Năm cấp chứng chỉ
    @Column(name = "year_of_recognition_degree")
    private String yearOfRecognitionDegree;// Năm công nhận học vị
    @Column(name = "year_of_conferred")
    private String yearOfConferred;
    @Column(name = "year_of_recognition_academicRank")
    private String yearOfRecognitionAcademicRank;// Năm công nhận học hàm
    @Column(name = "note")
    private String note;// Năm cấp chứng chỉ

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "Other_language_level")
//    private EducationDegree otherLanguageLevel; // Trình độ ngoại ngữ khác
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "studying")
//    private EducationDegree studying;// Đang theo học
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "civil_servant_category_id")
//    private CivilServantCategory civilServantCategory; // Ngạch công chức
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "grade_id")
//    private CivilServantGrade grade;// Bậc công chức

    @Column(name = "current_residence")
    private String currentResidence;// Nơi ở hiện tại

    @Column(name = "position_decision_number")
    private String positionDecisionNumber;// Số quyết định chức vụ

    @Column(name = "wards")
    private String wards;// Nguyên quán

    @Column(name = "family_come_from")
    private String familyComeFromString;

    @Column(name = "family_priority")
    private String familyPriority;

    @Column(name = "family_yourself")
    private String familyYourself;

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Journal> journals;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HrGroupStaff> hrGroupStaffs;

    // bhxh
    @Column(name = "has_social_ins")
    private Boolean hasSocialIns;

    @Column(name = "start_ins_date")
    private Date startInsDate;

    @Column(name = "insurance_salary")
    private Double insuranceSalary;// Mức lương đóng bảo hiểm xã hội

    @Column(name = "staff_percentage")
    private Double staffPercentage;// Tỷ lệ cá nhân đóng bảo hiểm xã hội

    @Column(name = "org_percentage")
    private Double orgPercentage;// Tỷ lệ đơn vị đóng bảo hiểm xã hội

    @Column(name = "union_dues_percentage")
    private Double unionDuesPercentage; // Tỷ lệ khoản phí công đoàn (công ty đóng)

    @Column(name = "staff_insurance_amount")
    private Double staffInsuranceAmount;// Số tiền cá nhân đóng

    @Column(name = "org_insurance_amount")
    private Double orgInsuranceAmount;// Số tiền đơn vị đóng

    @Column(name = "union_dues_amount")
    private Double unionDuesAmount; // Số tiền đóng khoản phí công đoàn (công ty đóng)

    @Column(name = "total_insurance_amount")
    private Double totalInsuranceAmount;// Tổng tiền bảo hiểm

    @Column(name = "insurance_start_date")
    private Date insuranceStartDate;// Ngày bắt đầu mức đóng

    @Column(name = "insurance_end_date")
    private Date insuranceEndDate; // Ngày kết thúc mức đóng

    @Column(name = "insurance_salary_coefficient")
    private Double insuranceSalaryCoefficient; // Hệ số lương đó bảo hiểm xã hội

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private HrOrganization organization; // Công ty/tổ chức hiện tại

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private HRDepartment department; // Phòng ban hiện tại

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_position_id")
    private Position currentPosition; // Chức vụ/vị trí hiện tại(Candidate can sua)

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffSignature> staffSignatures;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Position> currentPositions;

    @Column(name = "allow_external_ip_timekeeping")
    private Boolean allowExternalIpTimekeeping; // Cho phép chấm công ngoài

    // Thông tin số BHYT/BHXH


    // Thêm thông tin tài khoản ngân hàng
    @Column(name = "bank_account_name")
    private String bankAccountName; // Tên tài khoản ngân hàng

    @Column(name = "bank_account_number")
    private String bankAccountNumber; // Số tài khoản ngân hàng

    @Column(name = "bank_name")
    private String bankName; // Tên ngân hàng

    @Column(name = "bank_branch")
    private String bankBranch; // Chi nhánh ngân hàng

    // Nếu là người nước ngoài
    @Column(name = "work_permit_number")
    private String workPermitNumber; // Số giấy phép lao động (cho người nước ngoài)

    @Column(name = "passport_number")
    private String passportNumber; // Số hộ chiếu (cho người nước ngoài)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "introducer_id")
    private Staff introducer; // Nhân viên giới thiệu nhân viên này vào làm

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id")
    private Staff recruiter; // Nhân viên quyết định tuyển dụng nhân viên này vào làm

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HrIntroduceCost> staffIntroduceCosts; // các chi phí giới thiệu của nhân viên


    // Hồ sơ nhân sự
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_template_id")
    private HrDocumentTemplate documentTemplate;

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffDocumentItem> staffDocumentItems; // các tài liệu nhân viên đã nộp

    @Column(name = "staff_document_status")
    private Integer staffDocumentStatus; // Tình trạng hoàn thành hồ sơ của nhân viên. Chi tiết: HrConstants.StaffDocumentStatus


    // Tài khoản ngân hàng
    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PersonBankAccount> personBankAccounts; // các tài khoản ngân hàng của nhân viên

    @Column(name = "staff_working_format")
    private Integer staffWorkingFormat; // Hình thức làm việc của nhân viên. Chi tiết: HrConstants.StaffWorkingFormat

    @Column(name = "company_email")
    private String companyEmail; // Email công ty

    @Column(name = "staff_phase")
    private Integer staffPhase; // Tình trạng nhân viên. Chi tiết: HrConstants.StaffPhase

    @Column(name = "contact_person_info")
    private String contactPersonInfo; // Thông tin người liên hệ

    // Mã số thuế
//    @Column(name = "tax_code")
//    private String taxCode;

    @Column(name = "social_insurance_number", nullable = true)
    private String socialInsuranceNumber;// Mã số bảo hiểm xã hội (BHXH)

    @Column(name = "health_insurance_number", nullable = true)
    private String healthInsuranceNumber; // Mã số bảo hiểm y tế (BHYT)

    @Column(name = "social_insurance_note", nullable = true)
    private String socialInsuranceNote; // Tình trạng sổ BHXH

    @Column(name = "desire_registration_health_care", length = 1000)
    private String desireRegistrationHealthCare; // Nơi mong muốn đăng ký khám chữa bệnh


    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffWorkingLocation> staffWorkingLocations; // Địa điểm làm việc của nhân viên


    // Nơi sinh = quê quán
//    private String birthPlace;

    @Column(name = "require_attendance")
    private Boolean requireAttendance; //Nhân viên có cần chấm công không không

    @Column(name = "work_shift_type")
    private Integer staffWorkShiftType; // Loại làm việc. HrConstants.StaffWorkShiftType

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fix_shift_work_id")
    private ShiftWork fixShiftWork; // Ca làm việc cố định nếu loại làm việc của nhân viên là cố định. HrConstants.StaffWorkShiftType.FIX

    @Column(name = "staff_leave_shift_type")
    private Integer staffLeaveShiftType; // Loại nghỉ làm việc. HrConstants.StaffLeaveShiftType

    @Column(name = "fix_leave_week_day")
    private Integer fixLeaveWeekDay; // Ngày nghỉ cố định trong tuần, có giá trị khi loại nghỉ cửa nhân viên là nghỉ cố định. HrConstants.WeekDays

    @Column(name = "fix_leave_week_day_2")
    private Integer fixLeaveWeekDay2; // Ngày nghỉ cố định trong tuần, có giá trị khi loại nghỉ cửa nhân viên là nghỉ cố định. HrConstants.WeekDays


    @Column(name = "skip_late_early_count")
    private Boolean skipLateEarlyCount; // nhân viên không bị tính đi muộn về sớm

    @Column(name = "skip_overtime_count")
    private Boolean skipOvertimeCount; // nhân viên không được tính OT
    
    @Column(name = "annual_leave_days")
    private Double annualLeaveDays;

    @Column(name = "staff_position_type")
    private Integer staffPositionType; // Loại vị trí việc làm. Chi tiết: HrConstants.StaffPositionType

    @OneToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    public Integer getStaffPositionType() {
        return staffPositionType;
    }

    public void setStaffPositionType(Integer staffPositionType) {
        this.staffPositionType = staffPositionType;
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

    public ShiftWork getFixShiftWork() {
        return fixShiftWork;
    }

    public void setFixShiftWork(ShiftWork fixShiftWork) {
        this.fixShiftWork = fixShiftWork;
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

    public Set<StaffWorkingLocation> getStaffWorkingLocations() {
        return staffWorkingLocations;
    }

    public void setStaffWorkingLocations(Set<StaffWorkingLocation> staffWorkingLocations) {
        this.staffWorkingLocations = staffWorkingLocations;
    }

    public Set<PersonBankAccount> getPersonBankAccounts() {
        return personBankAccounts;
    }

    public void setPersonBankAccounts(Set<PersonBankAccount> personBankAccounts) {
        this.personBankAccounts = personBankAccounts;
    }

    public HrDocumentTemplate getDocumentTemplate() {
        return documentTemplate;
    }

    public void setDocumentTemplate(HrDocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }

    public Integer getStaffDocumentStatus() {
        return staffDocumentStatus;
    }

    public void setStaffDocumentStatus(Integer staffDocumentStatus) {
        this.staffDocumentStatus = staffDocumentStatus;
    }

    public Set<StaffDocumentItem> getStaffDocumentItems() {
        return staffDocumentItems;
    }

    public void setStaffDocumentItems(Set<StaffDocumentItem> staffDocumentItems) {
        this.staffDocumentItems = staffDocumentItems;
    }

    public Set<HrIntroduceCost> getStaffIntroduceCosts() {
        return staffIntroduceCosts;
    }

    public void setStaffIntroduceCosts(Set<HrIntroduceCost> staffIntroduceCosts) {
        this.staffIntroduceCosts = staffIntroduceCosts;
    }

    public Set<Position> getCurrentPositions() {
        return currentPositions;
    }

    public void setCurrentPositions(Set<Position> currentPositions) {
        this.currentPositions = currentPositions;
    }

    public Set<HrGroupStaff> getHrGroupStaffs() {
        return hrGroupStaffs;
    }

    public void setHrGroupStaffs(Set<HrGroupStaff> hrGroupStaffs) {
        this.hrGroupStaffs = hrGroupStaffs;
    }

    public Integer getCurrentWorkingStatus() {
        return currentWorkingStatus;
    }

    public void setCurrentWorkingStatus(Integer currentWorkingStatus) {
        this.currentWorkingStatus = currentWorkingStatus;
    }

    public Date getContractDate() {
        return contractDate;
    }

    public void setContractDate(Date contractDate) {
        this.contractDate = contractDate;
    }

    public Date getRecruitmentDate() {
        return recruitmentDate;
    }

    public void setRecruitmentDate(Date recruitmentDate) {
        this.recruitmentDate = recruitmentDate;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public CivilServantType getCivilServantType() {
        return civilServantType;
    }

    public void setCivilServantType(CivilServantType civilServantType) {
        this.civilServantType = civilServantType;
    }

    public String getSocialInsuranceNumber() {
        return socialInsuranceNumber;
    }

    public void setSocialInsuranceNumber(String socialInsuranceNumber) {
        this.socialInsuranceNumber = socialInsuranceNumber;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getSalaryCoefficient() {
        return salaryCoefficient;
    }

    public void setSalaryCoefficient(String salaryCoefficient) {
        this.salaryCoefficient = salaryCoefficient;
    }

    public Date getSalaryStartDate() {
        return salaryStartDate;
    }

    public void setSalaryStartDate(Date salaryStartDate) {
        this.salaryStartDate = salaryStartDate;
    }

    public Set<PositionStaff> getPositions() {
        return positions;
    }

    public void setPositions(Set<PositionStaff> positions) {
        this.positions = positions;
    }

    public Set<StaffLabourAgreement> getAgreements() {
        return agreements;
    }

    public void setAgreements(Set<StaffLabourAgreement> agreements) {
        this.agreements = agreements;
    }

    public Set<StaffSalaryHistory> getSalaryHistory() {
        return salaryHistory;
    }

    public void setSalaryHistory(Set<StaffSalaryHistory> salaryHistory) {
        this.salaryHistory = salaryHistory;
    }

    public Set<StaffSalaryProperty> getStaffSalaryProperties() {
        return staffSalaryProperties;
    }

    public void setStaffSalaryProperties(Set<StaffSalaryProperty> staffSalaryProperties) {
        this.staffSalaryProperties = staffSalaryProperties;
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

    public Set<StaffEducationHistory> getEducationHistory() {
        return educationHistory;
    }

    public void setEducationHistory(Set<StaffEducationHistory> educationHistory) {
        this.educationHistory = educationHistory;
    }

    public Set<StaffFamilyRelationship> getFamilyRelationships() {
        return familyRelationships;
    }

    public void setFamilyRelationships(Set<StaffFamilyRelationship> familyRelationships) {
        this.familyRelationships = familyRelationships;
    }

    public Set<StaffRewardHistory> getStaffRewardHistories() {
        return staffRewardHistories;
    }

    public void setStaffRewardHistories(Set<StaffRewardHistory> staffRewardHistories) {
        this.staffRewardHistories = staffRewardHistories;
    }

    public HRDepartment getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartment department) {
        this.department = department;
    }

    public EducationDegree getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(EducationDegree educationDegree) {
        this.educationDegree = educationDegree;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getPermanentResidence() {
        return permanentResidence;
    }

    public void setPermanentResidence(String permanentResidence) {
        this.permanentResidence = permanentResidence;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
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

    public InformaticDegree getInformaticDegree() {
        return informaticDegree;
    }

    public void setInformaticDegree(InformaticDegree informaticDegree) {
        this.informaticDegree = informaticDegree;
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

//    public Profession getProfession() {
//        return profession;
//    }
//
//    public void setProfession(Profession profession) {
//        this.profession = profession;
//    }

    public String getSalaryLeve() {
        return salaryLeve;
    }

    public void setSalaryLeve(String salaryLeve) {
        this.salaryLeve = salaryLeve;
    }

    public LabourAgreementType getLabourAgreementType() {
        return labourAgreementType;
    }

    public void setLabourAgreementType(LabourAgreementType labourAgreementType) {
        this.labourAgreementType = labourAgreementType;
    }

//	public Set<StaffInsuranceHistory> getStafInsuranceHistory() {
//		return stafInsuranceHistory;
//	}
//
//	public void setStafInsuranceHistory(Set<StaffInsuranceHistory> stafInsuranceHistory) {
//		this.stafInsuranceHistory = stafInsuranceHistory;
//	}

    public Set<StaffSocialInsurance> getStaffSocialInsurances() {
        return staffSocialInsurances;
    }

    public void setStaffSocialInsurances(Set<StaffSocialInsurance> staffSocialInsurances) {
        this.staffSocialInsurances = staffSocialInsurances;
    }

    public EducationDegree getComputerSkill() {
        return computerSkill;
    }

    public void setComputerSkill(EducationDegree computerSkill) {
        this.computerSkill = computerSkill;
    }

    public EducationDegree getEnglishLevel() {
        return englishLevel;
    }

    public void setEnglishLevel(EducationDegree englishLevel) {
        this.englishLevel = englishLevel;
    }

    public Certificate getEnglishCertificate() {
        return englishCertificate;
    }

    public void setEnglishCertificate(Certificate englishCertificate) {
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

    public EducationDegree getDegree() {
        return degree;
    }

    public void setDegree(EducationDegree degree) {
        this.degree = degree;
    }

    public AcademicTitle getAcademicRank() {
        return academicRank;
    }

    public void setAcademicRank(AcademicTitle academicRank) {
        this.academicRank = academicRank;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
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

//    public EducationDegree getOtherLanguageLevel() {
//        return otherLanguageLevel;
//    }
//
//    public void setOtherLanguageLevel(EducationDegree otherLanguageLevel) {
//        this.otherLanguageLevel = otherLanguageLevel;
//    }
//
//    public EducationDegree getStudying() {
//        return studying;
//    }
//
//    public void setStudying(EducationDegree studying) {
//        this.studying = studying;
//    }

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

//    public CivilServantCategory getCivilServantCategory() {
//        return civilServantCategory;
//    }
//
//    public void setCivilServantCategory(CivilServantCategory civilServantCategory) {
//        this.civilServantCategory = civilServantCategory;
//    }
//
//    public CivilServantGrade getGrade() {
//        return grade;
//    }
//
//    public void setGrade(CivilServantGrade grade) {
//        this.grade = grade;
//    }

    public Set<PersonCertificate> getPersonCertificate() {
        return personCertificate;
    }

    public void setPersonCertificate(Set<PersonCertificate> personCertificate) {
        this.personCertificate = personCertificate;
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

    public Set<TimeSheetStaff> getTimeSheets() {
        return timeSheets;
    }

    public void setTimeSheets(Set<TimeSheetStaff> timeSheetStaffSet) {
        this.timeSheets = timeSheetStaffSet;
    }

    public EducationalManagementLevel getEducationalManagementLevel() {
        return educationalManagementLevel;
    }

    public void setEducationalManagementLevel(EducationalManagementLevel educationalManagementLevel) {
        this.educationalManagementLevel = educationalManagementLevel;
    }

    public String getFamilyComeFromString() {
        return familyComeFromString;
    }

    public void setFamilyComeFromString(String familyComeFromString) {
        this.familyComeFromString = familyComeFromString;
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

    public TitleConferred getConferred() {
        return conferred;
    }

    public void setConferred(TitleConferred conferred) {
        this.conferred = conferred;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getYearOfConferred() {
        return yearOfConferred;
    }

    public void setYearOfConferred(String yearOfConferred) {
        this.yearOfConferred = yearOfConferred;
    }

    public Language getOtherLanguage() {
        return otherLanguage;
    }

    public void setOtherLanguage(Language otherLanguage) {
        this.otherLanguage = otherLanguage;
    }

    public Set<StaffOverseasWorkHistory> getOverseasWorkHistory() {
        return overseasWorkHistory;
    }

    public void setOverseasWorkHistory(Set<StaffOverseasWorkHistory> overseasWorkHistory) {
        this.overseasWorkHistory = overseasWorkHistory;
    }

    public Set<StaffMaternityHistory> getStaffMaternityHistories() {
        return staffMaternityHistories;
    }

    public void setStaffMaternityHistories(Set<StaffMaternityHistory> staffMaternityHistories) {
        this.staffMaternityHistories = staffMaternityHistories;
    }

    public Set<StaffAllowanceHistory> getStaffAllowanceHistories() {
        return staffAllowanceHistories;
    }

    public void setStaffAllowanceHistories(Set<StaffAllowanceHistory> staffAllowanceHistories) {
        this.staffAllowanceHistories = staffAllowanceHistories;
    }

    public Set<StaffAllowance> getStaffAllowance() {
        return staffAllowance;
    }

    public void setStaffAllowance(Set<StaffAllowance> staffAllowance) {
        this.staffAllowance = staffAllowance;
    }

    public Set<StaffTrainingHistory> getStaffTrainingHistories() {
        return staffTrainingHistories;
    }

    public void setStaffTrainingHistories(Set<StaffTrainingHistory> staffTrainingHistories) {
        this.staffTrainingHistories = staffTrainingHistories;
    }

    public Set<StaffWorkingHistory> getStaffWorkingHistories() {
        return staffWorkingHistories;
    }

    public void setStaffWorkingHistories(Set<StaffWorkingHistory> staffWorkingHistories) {
        this.staffWorkingHistories = staffWorkingHistories;
    }

    public Set<AllowanceSeniorityHistory> getAllowanceSeniorityHistories() {
        return allowanceSeniorityHistories;
    }

    public void setAllowanceSeniorityHistories(Set<AllowanceSeniorityHistory> allowanceSeniorityHistories) {
        this.allowanceSeniorityHistories = allowanceSeniorityHistories;
    }

    public AdministrativeUnit getAdministrativeUnit() {
        return administrativeUnit;
    }

    public void setAdministrativeUnit(AdministrativeUnit administrativeUnit) {
        this.administrativeUnit = administrativeUnit;
    }

    public Set<Journal> getJournals() {
        return journals;
    }

    public void setJournals(Set<Journal> journals) {
        this.journals = journals;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Position currentPosition) {
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

    public StaffType getStaffType() {
        return staffType;
    }

    public void setStaffType(StaffType staffType) {
        this.staffType = staffType;
    }

    public HrOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganization organization) {
        this.organization = organization;
    }

    public Set<StaffSignature> getStaffSignatures() {
        return staffSignatures;
    }

    public void setStaffSignatures(Set<StaffSignature> staffSignatures) {
        this.staffSignatures = staffSignatures;
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

    public Staff getIntroducer() {
        return introducer;
    }

    public void setIntroducer(Staff introducer) {
        this.introducer = introducer;
    }

    public Staff getRecruiter() {
        return recruiter;
    }

    public void setRecruiter(Staff recruiter) {
        this.recruiter = recruiter;
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

    public Boolean getRequireAttendance() {
        return requireAttendance;
    }

    public void setRequireAttendance(Boolean requireAttendance) {
        this.requireAttendance = requireAttendance;
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

    public Integer getFixLeaveWeekDay2() {
        return fixLeaveWeekDay2;
    }

    public void setFixLeaveWeekDay2(Integer fixLeaveWeekDay2) {
        this.fixLeaveWeekDay2 = fixLeaveWeekDay2;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }
}
