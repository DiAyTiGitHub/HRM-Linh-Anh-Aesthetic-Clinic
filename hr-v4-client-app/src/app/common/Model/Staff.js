import LocalConstants from "app/LocalConstants";

export class Staff {
    id = null;
    firstName = null;
    lastName = null;
    displayName = null;
    gender = null;
    birthDate = null
    birthPlace = null;
    permanentResidence = null;
    currentResidence = null;
    accommodationToday = null;
    idNumber = null;
    idNumberIssueDate = null
    idNumberIssueBy = null;
    nationality = null
    ethnics = null
    religion = null
    email = null;
    phoneNumber = null;
    maritalStatus = null;
    /* new */
    administrativeunit = null;
    district = null;
    province = null;
    familyComeFrom = null;
    familyPriority = null;
    familyYourself = null;
    //tab1 end
    username = null;
    password = null;
    confirmPassword = null;
    //tab2 start
    status = null;
    department = null;
    staffCode = null;
    civilServantType = null;
    civilServantCategory = null;
    grade = null;
    apprenticeDays = 0;
    personalIdentificationIssueDate = null;
    labourAgreementType = null;
    contractDate = null;
    recruitmentDate = null;
    professionalTitles = null;
    profession = null;
    highestPosition = null;
    dateOfReceivingPosition = null;
    positionDecisionNumber = null;
    currentWorkingStatus = null;
    startDate = null;
    jobTitle = null;
    allowanceCoefficient = null;
    dateOfReceivingAllowance = null;
    salaryLeve = null;
    salaryCoefficient = null;
    salaryStartDate = null;
    personCertificate = null;
    ethnicLanguage = false;
    physicalEducationTeacher = false;
    studying = null;
    highSchoolEducation = null;
    qualification = null;
    specializedName = null;
    formsOfTraining = null;
    trainingPlaces = null;
    graduationYear = null
    trainingCountry = null;
    academicRank = null;
    yearOfRecognitionAcademicRank = null;
    degree = null;
    yearOfRecognitionDegree = null;
    politicalTheoryLevel = null;
    stateManagementQualifications = null;
    educationalManagementQualifications = null;
    computerSkill = null;
    englishLevel = null;
    englishCertificate = null;
    certificationScore = null;
    yearOfCertification = null;
    note = null;
    otherLanguage = null;
    otherLanguageLevel = null;
    conferred = null;
    yearOfConferred = null;
    positions = null; //qua trinh chuc vu
    familyRelationships = null;
    educationHistory = null; // qua trinh dao tao
    agreements = null; // Hop dong
    stafInsuranceHistory = null; // Qua trinh dong BHXH
    salaryHistory = null; // Qua trinh luong
    overseasWorkHistory = null; // Qua trinh cong tac nuoc ngoai
    rewardHistory = null; // Qua trinh khen thuong
    maternityHistory = null; //Qua trinh thai san
    trainingHistory = null; // Qua trinh buoi duong
    workingHistory = null; //Qua trinh cong tac
    allowanceHistory = null; //Qua trinh phu cap
    allowanceSeniorityHistory = null; // Qua trinh phu cap tham nien nghe giao
    assets = [];
    approvalStatus = 1; //Trạng thái phê duyệt hồ sơ (đối với ứng viên)
    staffSocialInsurance = [];
    currentPosition = null;

    // hot doing
    hasSocialIns = false;
    startInsDate = null;

    insuranceSalary = null; // Mức lương đóng bảo hiểm xã hội
    staffPercentage = null; // Tỷ lệ cá nhân đóng bảo hiểm xã hội
    orgPercentage = null; // Tỷ lệ đơn vị đóng bảo hiểm xã hội
    staffInsuranceAmount = null; // Số tiền cá nhân đóng
    orgInsuranceAmount = null; // Số tiền đơn vị đóng
    unionDuesPercentage = null;  //  Tỷ lệ khoản phí công đoàn (công ty đóng)
    unionDuesAmount = null;  //  Số tiền đóng khoản phí công đoàn (công ty đóng)
    totalInsuranceAmount = null; // Tổng tiền bảo hiểm
    salaryPeriod = null; // kỳ lương nào
    salaryResult = null;
    paidStatus = null; // Bảo hiểm này của nhan vien da duoc tra (dong) hay chua. Chi tiet: HrConstants.StafSocialInsurancePaidStatus
    insuranceStartDate = null; // Ngày bắt đầu mức đóng
    insuranceEndDate = null; // Ngày kết thúc mức đóng
    staffType = null; // Loại nhân viên
    organization = null;

    taxCode = null; // Mã số thuế
    //Thông tin ngân hàng
    bankAccountName = null; // Tên tài khoản ngân hàng
    bankAccountNumber = null; // Số tài khoản ngân hàng
    bankName = null; // Tên ngân hàng
    bankBranch = null; // Chi nhánh ngân hàng

    //Nếu là người nước ngoài
    workPermitNumber = null; // Số giấy phép lao động (cho người nước ngoài)
    passportNumber = null; // Số hộ chiếu (cho người nước ngoài)

    introducer = null;
    recruiter = null;

    staffDocumentItems = []; // các tài liệu nhân viên đã nộp
    staffIntroduceCosts = []; // các chi phí giới thiệu của nhân viên

    staffWorkingFormat = null; // Hình thức làm việc của nhân viên. Chi tiết: HrConstants.StaffWorkingFormat
    companyEmail = null; // Email công ty
    staffPhase = null; // Tình trạng nhân viên. Chi tiết: HrConstants.StaffPhase
    contactPersonInfo = null; // Thông tin người liên hệ
    // Mã số thuế
    //     taxCode = null;
    socialInsuranceNumber = null;// Mã số bảo hiểm xã hội (BHXH)
    healthInsuranceNumber = null; // Mã số bảo hiểm y tế (BHYT)
    socialInsuranceNote = null; // Tình trạng sổ BHXH
    desireRegistrationHealthCare = null; // Nơi mong muốn đăng ký khám chữa bệnh
    allowExternalIpTimekeeping = false;
    // Nơi sinh = quê quán
    //     birthPlace = null;

    requireAttendance = true; //Nhân viên có cần chấm công hay không

    staffWorkShiftType = null; // Loại làm việc. HrConstants.StaffWorkShiftType
    staffLeaveShiftType = null; // Loại nghỉ làm việc. HrConstants.StaffLeaveShiftType

    skipLateEarlyCount = false; // nhân viên không bị tính đi muộn về sớm
    skipOvertimeCount = false; // nhân viên không được tính OT

    fixShiftWork = null; // Ca làm việc cố định nếu loại làm việc của nhân viên là cố định. HrConstants.StaffWorkShiftType.FIX
    fixLeaveWeekDay = null; // Ngày nghỉ cố định trong tuần, có giá trị khi loại nghỉ cửa nhân viên là nghỉ cố định. HrConstants.WeekDays
    fixLeaveWeekDay2 = null;
    staffPositionType = null;

    constructor() {

        this.recruitmentDate = new Date();
        this.staffPositionType = LocalConstants.StaffPositionType.KHAC.value;
    }
}