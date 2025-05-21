package com.globits.hr.dto.importExcel;

import java.util.Date;

public class StaffLAImport {
    // 0. STT
    private Integer stt;
    // 1. Mã nhân viên
    private String staffCode;
    // 2. Họ và tên
    private String firstName;
    private String lastName;
    private String displayName;
    // 3. Mã trạng thái làm việc
    private String employeeStatusCode;
    // 4. Trạng thái làm việc
    private String employeeStatusName;
    // 5. Ngày vào
    private Date recruitmentDate;
    // 6. Ngày đi làm lại
    private Date returnDate;
    // 7. Ngày tạm dừng/nghỉ việc
    private Date pauseDate;
    // 8. Lí do nghỉ việc
    private String pauseReason;
    // 9. Mã Ban/ Chi Nhánh
    private String departmentCode;
    // 10. Ban/ Chi Nhánh
    private String departmentName;
    // 11. Mã nhóm ngạch
    private String rankGroupCode;
    // 12. Nhóm Ngạch
    private String rankGroupName;
    // 13. Cấp bậc (Level)
    private String rankTitleCode;
    // 14. Mã Chức danh
    private String positionTitleCode;
    // 15. Chức danh
    private String positionTitleName;
    // 16. Mã chức vụ
    private String positionCode;
    // 17. Chức vụ
    private String positionName;
    // 18. Hình thức làm việc
    private String staffWorkingFormat;
    // 19. Mã Khu vực/Phòng
    private String areaCode;
    // 20. Khu vực/Phòng
    private String area;
    // 21. Địa điểm làm việc
    private String workingPlace;
    // 22. Mã nhân viên Quản lý trực tiếp
    private String supervisorCode;
    // 23. Quản lý trực tiếp
    private String supervisorName;
    // 24. Email công ty
    private String companyEmail;
    // 25. Email cá nhân
    private String privateEmail;
    // 26. Tình trạng: TV-Thử việc, CT-Chính thức, HV-Học việc
    private String staffPhase;

    // 27. Số HĐ (TV/HV)
    private String labourAgreementNumber;

    // 28. Mã Công ty ký HĐ
    private String contractCompanyCode;

    // 29. Công ty ký HĐ
    private String contractCompanyName;

    // 30. Ngày bắt đầu (HV/TV)
    private Date startLabourAgreementDate;
    // 31. Số ngày HV/TV
    private Integer labourDays;
    // 32. Ngày kết thúc (HV/TV)
    private Date endLabourAgreementDate;
    // 33. SĐT
    private String phoneNumber;
    // 34. Ngày sinh
    private Date birthDate;
    // 35. Giới tính
    private String gender;
    // 36. Mã Tỉnh_Thường trú
    private String provinceCode;
    // 37. Tỉnh_Thường trú
    private String provinceName;
    // 38. Mã Huyện_Thường trú
    private String districtCode;
    // 39. Huyện_Thường trú
    private String districtName;
    // 40. Mã Xã_Thường trú
    private String wardCode;
    // 41. Xã_Thường trú
    private String wardName;
    // 42. Chi tiết_Thường trú
    private String permanentResidence;
    // 43. Thường trú chi tiết
    private String detailResidence;
    // 44. Tạm trú
    private String currentResidence;
    // 45. CMND
    private String idNumber;
    // 47. Ngày cấp
    private Date idNumberIssueDate;
    // 48. Nơi cấp
    private String idNumberIssueBy;

    private String personalIdentificationNumber;
    private Date personalIdentificationIssueDate;
    private String personalIdentificationIssuePlace;

    // 49. Tình trạng hôn nhân
    private String maritalStatus;
    // 50. Mã Dân tộc
    private String ethnicCode;
    // 51. Dân tộc
    private String ethnicStringName;
    // 52. Mã Tôn giáo
    private String religionCode;
    // 53. Tôn giáo
    private String religionName;
    // 54. Mã Quốc tịch
    private String countryCode;
    // 55. Quốc tịch
    private String countryName;
    // 56. Quê quán
    private String homeTown;
    // 57. Mã trình độ học vấn
    private String educationDegreeCode;
    // 58. Trình độ học vấn
    private String educationDegreeName;
    // 59. Thông tin người liên hệ
    private String contactPersonInfo;
    // 60. Mã số Thuế
    private String taxCode;
    // 61. Số Người phụ thuộc đã đăng ký (nếu có)
    private String dependentPeople;
    // 62. Mã số Bảo hiểm xã hội
    private String socialInsuranceNumber;
    // 63. Mã số Bảo Hiểm Y tế
    private String healthInsuranceNumber; // Mã số bảo hiểm y tế (BHYT)
    // 64. Tình trạng Sổ Bảo hiểm xã hội
    private String socialInsuranceNote; // Tình trạng sổ BHXH
    // 65. Nơi mong muốn đăng ký khám chữa bệnh
    private String desireRegistrationHealthCare; // Nơi mong muốn đăng ký khám chữa bệnh
    // 66. Mã nhân viên người giới thiệu
    private String introducerCode; // Nhân viên giới thiệu nhân viên này vào làm
    // 67. Nhân viên giới thiệu
    private String introducerName;
    // 68. Mã NV tuyển
    private String recruiterCode;
    // 69. Nhân viên tuyển
    private String recruiterName;
    // 70. Hồ sơ
    private String documentTemplate;
    // 71. Ảnh 3x4
    private String image34Check;
    // 72. CMND/CCCD
    private String idNumberCheck;
    // 73. Đơn ứng tuyển
    private String applicationFormCheck;
    // 74. Sơ yếu lý lịch
    private String profileCheck;
    // 75. Bằng cấp cao nhất
    private String highestDegreeCheck;
    // 76. Chứng chỉ liên quan
    private String relatedCertificateCheck;
    // 77. Giấy khám SK
    private String heathCheck;
    // 78. SHK
    private String shkCheck;
    // 79. Hồ sơ khác (ghi rõ)
    private String otherFilesCheck;
    // 80. Phiếu thông tin cá nhân
    private String personInfoCheck;
    // 81. Cam kết bảo mật thông tin
    private String secureInfocommitmentCheck;
    // 82. Cam kết bảo mật thông tin thu nhập
    private String secureIncomeCommitmentCheck;
    // 83. Cam kết trách nhiệm
    private String responsibilityCommitmentCheck;
    // 84. HĐ thử việc
    private String probationLabourCheck;

    // 85. Tình trạng hồ sơ
    private String staffDocumentStatus;
    // 86. Có đóng BHXH
    private String hasSocialIns;
    // 87. Bắt buộc chấm công
    private String requireAttendance;
    // 88. Cho phép chấm công ngoài công ty
    private String allowExternalIpTimekeeping;
    // 89. Loại phân ca
    private Integer staffWorkShiftType;
    // 90. Mã ca làm việc cố định
    private String fixShiftWork;
    // 91. Loại nghỉ trong tháng
    private Integer staffLeaveShiftType;
    // 92. Ngày nghỉ cố định
    private Integer fixLeaveWeekDay;
    // 93. Không tính đi muộn, về sớm
    private String skipLateEarlyCount;
    // 94. Không tính làm thêm giờ
    private String skipOvertimeCount;

    private String errorMessage;


    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getRankGroupCode() {
        return rankGroupCode;
    }

    public void setRankGroupCode(String rankGroupCode) {
        this.rankGroupCode = rankGroupCode;
    }

    public String getRankGroupName() {
        return rankGroupName;
    }

    public void setRankGroupName(String rankGroupName) {
        this.rankGroupName = rankGroupName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmployeeStatusCode() {
        return employeeStatusCode;
    }

    public void setEmployeeStatusCode(String employeeStatusCode) {
        this.employeeStatusCode = employeeStatusCode;
    }

    public String getEmployeeStatusName() {
        return employeeStatusName;
    }

    public void setEmployeeStatusName(String employeeStatusName) {
        this.employeeStatusName = employeeStatusName;
    }

    public Date getRecruitmentDate() {
        return recruitmentDate;
    }

    public void setRecruitmentDate(Date recruitmentDate) {
        this.recruitmentDate = recruitmentDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public Date getPauseDate() {
        return pauseDate;
    }

    public void setPauseDate(Date pauseDate) {
        this.pauseDate = pauseDate;
    }

    public String getPauseReason() {
        return pauseReason;
    }

    public void setPauseReason(String pauseReason) {
        this.pauseReason = pauseReason;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getRankTitleCode() {
        return rankTitleCode;
    }

    public void setRankTitleCode(String rankTitleCode) {
        this.rankTitleCode = rankTitleCode;
    }

    public String getPositionTitleCode() {
        return positionTitleCode;
    }

    public void setPositionTitleCode(String positionTitleCode) {
        this.positionTitleCode = positionTitleCode;
    }

    public String getPositionTitleName() {
        return positionTitleName;
    }

    public void setPositionTitleName(String positionTitleName) {
        this.positionTitleName = positionTitleName;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getStaffWorkingFormat() {
        return staffWorkingFormat;
    }

    public void setStaffWorkingFormat(String staffWorkingFormat) {
        this.staffWorkingFormat = staffWorkingFormat;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getWorkingPlace() {
        return workingPlace;
    }

    public void setWorkingPlace(String workingPlace) {
        this.workingPlace = workingPlace;
    }

    public String getSupervisorCode() {
        return supervisorCode;
    }

    public void setSupervisorCode(String supervisorCode) {
        this.supervisorCode = supervisorCode;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getPrivateEmail() {
        return privateEmail;
    }

    public void setPrivateEmail(String privateEmail) {
        this.privateEmail = privateEmail;
    }

    public String getStaffPhase() {
        return staffPhase;
    }

    public void setStaffPhase(String staffPhase) {
        this.staffPhase = staffPhase;
    }

    public String getContractCompanyCode() {
        return contractCompanyCode;
    }

    public void setContractCompanyCode(String contractCompanyCode) {
        this.contractCompanyCode = contractCompanyCode;
    }

    public String getContractCompanyName() {
        return contractCompanyName;
    }

    public void setContractCompanyName(String contractCompanyName) {
        this.contractCompanyName = contractCompanyName;
    }

    public String getLabourAgreementNumber() {
        return labourAgreementNumber;
    }

    public void setLabourAgreementNumber(String labourAgreementNumber) {
        this.labourAgreementNumber = labourAgreementNumber;
    }

    public Date getStartLabourAgreementDate() {
        return startLabourAgreementDate;
    }

    public void setStartLabourAgreementDate(Date startLabourAgreementDate) {
        this.startLabourAgreementDate = startLabourAgreementDate;
    }

    public Integer getLabourDays() {
        return labourDays;
    }

    public void setLabourDays(Integer labourDays) {
        this.labourDays = labourDays;
    }

    public Date getEndLabourAgreementDate() {
        return endLabourAgreementDate;
    }

    public void setEndLabourAgreementDate(Date endLabourAgreementDate) {
        this.endLabourAgreementDate = endLabourAgreementDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getWardCode() {
        return wardCode;
    }

    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getPermanentResidence() {
        return permanentResidence;
    }

    public void setPermanentResidence(String permanentResidence) {
        this.permanentResidence = permanentResidence;
    }

    public String getDetailResidence() {
        return detailResidence;
    }

    public void setDetailResidence(String detailResidence) {
        this.detailResidence = detailResidence;
    }

    public String getCurrentResidence() {
        return currentResidence;
    }

    public void setCurrentResidence(String currentResidence) {
        this.currentResidence = currentResidence;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public Date getIdNumberIssueDate() {
        return idNumberIssueDate;
    }

    public void setIdNumberIssueDate(Date idNumberIssueDate) {
        this.idNumberIssueDate = idNumberIssueDate;
    }

    public String getIdNumberIssueBy() {
        return idNumberIssueBy;
    }

    public void setIdNumberIssueBy(String idNumberIssueBy) {
        this.idNumberIssueBy = idNumberIssueBy;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getEthnicCode() {
        return ethnicCode;
    }

    public void setEthnicCode(String ethnicCode) {
        this.ethnicCode = ethnicCode;
    }

    public String getEthnicStringName() {
        return ethnicStringName;
    }

    public void setEthnicStringName(String ethnicStringName) {
        this.ethnicStringName = ethnicStringName;
    }

    public String getReligionCode() {
        return religionCode;
    }

    public void setReligionCode(String religionCode) {
        this.religionCode = religionCode;
    }

    public String getReligionName() {
        return religionName;
    }

    public void setReligionName(String religionName) {
        this.religionName = religionName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public String getEducationDegreeCode() {
        return educationDegreeCode;
    }

    public void setEducationDegreeCode(String educationDegreeCode) {
        this.educationDegreeCode = educationDegreeCode;
    }

    public String getEducationDegreeName() {
        return educationDegreeName;
    }

    public void setEducationDegreeName(String educationDegreeName) {
        this.educationDegreeName = educationDegreeName;
    }

    public String getContactPersonInfo() {
        return contactPersonInfo;
    }

    public void setContactPersonInfo(String contactPersonInfo) {
        this.contactPersonInfo = contactPersonInfo;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getDependentPeople() {
        return dependentPeople;
    }

    public void setDependentPeople(String dependentPeople) {
        this.dependentPeople = dependentPeople;
    }

    public String getSocialInsuranceNumber() {
        return socialInsuranceNumber;
    }

    public void setSocialInsuranceNumber(String socialInsuranceNumber) {
        this.socialInsuranceNumber = socialInsuranceNumber;
    }

    public String getHealthInsuranceNumber() {
        return healthInsuranceNumber;
    }

    public void setHealthInsuranceNumber(String healthInsuranceNumber) {
        this.healthInsuranceNumber = healthInsuranceNumber;
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

    public String getIntroducerCode() {
        return introducerCode;
    }

    public void setIntroducerCode(String introducerCode) {
        this.introducerCode = introducerCode;
    }

    public String getIntroducerName() {
        return introducerName;
    }

    public void setIntroducerName(String introducerName) {
        this.introducerName = introducerName;
    }

    public String getRecruiterCode() {
        return recruiterCode;
    }

    public void setRecruiterCode(String recruiterCode) {
        this.recruiterCode = recruiterCode;
    }

    public String getRecruiterName() {
        return recruiterName;
    }

    public void setRecruiterName(String recruiterName) {
        this.recruiterName = recruiterName;
    }

    public String getDocumentTemplate() {
        return documentTemplate;
    }

    public void setDocumentTemplate(String documentTemplate) {
        this.documentTemplate = documentTemplate;
    }

    public String getStaffDocumentStatus() {
        return staffDocumentStatus;
    }

    public void setStaffDocumentStatus(String staffDocumentStatus) {
        this.staffDocumentStatus = staffDocumentStatus;
    }

    public String getImage34Check() {
        return image34Check;
    }

    public void setImage34Check(String image34Check) {
        this.image34Check = image34Check;
    }

    public String getIdNumberCheck() {
        return idNumberCheck;
    }

    public void setIdNumberCheck(String idNumberCheck) {
        this.idNumberCheck = idNumberCheck;
    }

    public String getApplicationFormCheck() {
        return applicationFormCheck;
    }

    public void setApplicationFormCheck(String applicationFormCheck) {
        this.applicationFormCheck = applicationFormCheck;
    }

    public String getProfileCheck() {
        return profileCheck;
    }

    public void setProfileCheck(String profileCheck) {
        this.profileCheck = profileCheck;
    }

    public String getHighestDegreeCheck() {
        return highestDegreeCheck;
    }

    public void setHighestDegreeCheck(String highestDegreeCheck) {
        this.highestDegreeCheck = highestDegreeCheck;
    }

    public String getRelatedCertificateCheck() {
        return relatedCertificateCheck;
    }

    public void setRelatedCertificateCheck(String relatedCertificateCheck) {
        this.relatedCertificateCheck = relatedCertificateCheck;
    }

    public String getHeathCheck() {
        return heathCheck;
    }

    public void setHeathCheck(String heathCheck) {
        this.heathCheck = heathCheck;
    }

    public String getShkCheck() {
        return shkCheck;
    }

    public void setShkCheck(String shkCheck) {
        this.shkCheck = shkCheck;
    }

    public String getOtherFilesCheck() {
        return otherFilesCheck;
    }

    public void setOtherFilesCheck(String otherFilesCheck) {
        this.otherFilesCheck = otherFilesCheck;
    }

    public String getPersonInfoCheck() {
        return personInfoCheck;
    }

    public void setPersonInfoCheck(String personInfoCheck) {
        this.personInfoCheck = personInfoCheck;
    }

    public String getSecureInfocommitmentCheck() {
        return secureInfocommitmentCheck;
    }

    public void setSecureInfocommitmentCheck(String secureInfocommitmentCheck) {
        this.secureInfocommitmentCheck = secureInfocommitmentCheck;
    }

    public String getSecureIncomeCommitmentCheck() {
        return secureIncomeCommitmentCheck;
    }

    public void setSecureIncomeCommitmentCheck(String secureIncomeCommitmentCheck) {
        this.secureIncomeCommitmentCheck = secureIncomeCommitmentCheck;
    }

    public String getResponsibilityCommitmentCheck() {
        return responsibilityCommitmentCheck;
    }

    public void setResponsibilityCommitmentCheck(String responsibilityCommitmentCheck) {
        this.responsibilityCommitmentCheck = responsibilityCommitmentCheck;
    }

    public String getProbationLabourCheck() {
        return probationLabourCheck;
    }

    public void setProbationLabourCheck(String probationLabourCheck) {
        this.probationLabourCheck = probationLabourCheck;
    }

    public Integer getStt() {
        return stt;
    }

    public void setStt(Integer stt) {
        this.stt = stt;
    }

    public String getHasSocialIns() {
        return hasSocialIns;
    }

    public void setHasSocialIns(String hasSocialIns) {
        this.hasSocialIns = hasSocialIns;
    }

    public String getRequireAttendance() {
        return requireAttendance;
    }

    public void setRequireAttendance(String requireAttendance) {
        this.requireAttendance = requireAttendance;
    }

    public String getAllowExternalIpTimekeeping() {
        return allowExternalIpTimekeeping;
    }

    public void setAllowExternalIpTimekeeping(String allowExternalIpTimekeeping) {
        this.allowExternalIpTimekeeping = allowExternalIpTimekeeping;
    }

    public Integer getStaffWorkShiftType() {
        return staffWorkShiftType;
    }

    public void setStaffWorkShiftType(Integer staffWorkShiftType) {
        this.staffWorkShiftType = staffWorkShiftType;
    }

    public String getFixShiftWork() {
        return fixShiftWork;
    }

    public void setFixShiftWork(String fixShiftWork) {
        this.fixShiftWork = fixShiftWork;
    }

    public Integer getStaffLeaveShiftType() {
        return staffLeaveShiftType;
    }

    public void setStaffLeaveShiftType(Integer staffLeaveShiftType) {
        this.staffLeaveShiftType = staffLeaveShiftType;
    }

    public Integer getFixLeaveWeekDay() {
        return fixLeaveWeekDay;
    }

    public void setFixLeaveWeekDay(Integer fixLeaveWeekDay) {
        this.fixLeaveWeekDay = fixLeaveWeekDay;
    }

    public String getSkipLateEarlyCount() {
        return skipLateEarlyCount;
    }

    public void setSkipLateEarlyCount(String skipLateEarlyCount) {
        this.skipLateEarlyCount = skipLateEarlyCount;
    }

    public String getSkipOvertimeCount() {
        return skipOvertimeCount;
    }

    public void setSkipOvertimeCount(String skipOvertimeCount) {
        this.skipOvertimeCount = skipOvertimeCount;
    }
    
}
