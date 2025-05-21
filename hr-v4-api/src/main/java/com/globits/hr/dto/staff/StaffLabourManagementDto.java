package com.globits.hr.dto.staff;

import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.utils.ExportExcelUtil;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class StaffLabourManagementDto {
    private UUID staffId;

    // 0. STT
    private String orderNumber;
    // 1. Họ và tên
    private String displayName;
    // 2. Giới tính
    private String gender;
    // 3. Năm sinh
    private String birthDate;
    // 4. Quốc tịch
    private String nationalityName;
    // 5. Địa chỉ
    private String staffAddress;
    // 6. CCCD (hoặc hộ chiếu)
    private String staffIndentity;
    // 7. Trình độ chuyên môn kỹ thuật
    private String staffEducationDegree;
    // 8. Cấp bậc
    private String rankTitleJoined;
    // 9. Vị trí làm việc
    private String titleJoined;
    // 10. Loại HĐLĐ
    private String contractType;
    // 11. Thời điểm bắt đầu làm việc
    private String signDateContract;
    // 12. Tham gia bảo hiểm BHXH
    private String bhxhSalary;
    // 13. Tham gia bảo hiểm BHYT
    private String bhytSalary;
    // 14. Tham gia bảo hiểm BHTN
    private String bhtnSalary;
    // 15. Tiền lương cơ bản
    private String insuranceSalaryStr;
    // 16. Nâng bậc, nâng lương
    private String upSalaryInfo;
    // 17. Số ngày nghỉ trong năm
    private String leaveDays;
    // 18. Số giờ làm thêm
    private String otHours;
    // 19. Hưởng chế độ BHXH, BHYT, BHTN
    private String socialInsuranceBenefitEligible;
    // 20. Học nghề, đào tạo, bồi dưỡng, nâng cao trình độ kỹ năng nghề
    private String studyInfo;
    // 21. Kỷ luật lao động, trách nhiệm vật chất
    private String disciplineInfo;
    // 22. Tai nạn lao động, bệnh nghề nghiệp
    private String occupationalAccidentInfo;
    // 23. Thời điểm chấm dứt HĐLĐ và lý do
    private String endDateContract;

    public StaffLabourManagementDto() {
    }

    private String formatDate(Date date) {
        if (date == null)
            return "";
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public StaffLabourManagementDto(Staff entity) {
        this.staffId = entity.getId();

        // 1. Họ và tên
        this.displayName = entity.getDisplayName();

        // 2. Giới tính
        this.gender = ExportExcelUtil.getGenderText(entity.getGender());

        // 3. Năm sinh
        String birthDate = (entity.getBirthDate() != null) ? formatDate(entity.getBirthDate()) : "";
        this.birthDate = birthDate;

        // 4. Quốc tịch
        String nationalityNameValue = null;
        if (entity.getNationality() != null) {
            nationalityNameValue = (entity.getNationality().getName() != null) ? entity.getNationality().getName() : "";
        }
        this.nationalityName = nationalityNameValue;

        // 5. Địa chỉ
        String staffAddress = "";
        if ((entity.getPermanentResidence() != null && StringUtils.hasText(entity.getPermanentResidence()))
                || (entity.getCurrentResidence() != null && StringUtils.hasText(entity.getCurrentResidence()))) {

            if (entity.getPermanentResidence() != null && StringUtils.hasText(entity.getPermanentResidence())) {
                staffAddress = entity.getPermanentResidence();
            } else {
                staffAddress = entity.getCurrentResidence();
            }
        }
        this.staffAddress = staffAddress;

        // 6. CCCD (hoặc hộ chiếu)
        String staffIndentity = null;
        if (entity.getPersonalIdentificationNumber() != null
                && StringUtils.hasText(entity.getPersonalIdentificationNumber())) {
            staffIndentity = entity.getPersonalIdentificationNumber();
        }
        if (staffIndentity == null && entity.getIdNumber() != null
                && StringUtils.hasText(entity.getIdNumber())) {
            staffIndentity = entity.getIdNumber();
        }
        if (staffIndentity == null && entity.getPassportNumber() != null
                && StringUtils.hasText(entity.getPassportNumber())) {
            staffIndentity = entity.getPassportNumber();
        }
        if (staffIndentity == null)
            staffIndentity = "";
        this.staffIndentity = staffIndentity;

        // 7. Trình độ chuyên môn kỹ thuật
        String staffEducationDegree = null;
        if (entity.getEducationDegree() != null) {
            staffEducationDegree = entity.getEducationDegree().getName();
        }
        if (staffEducationDegree == null)
            staffEducationDegree = "";
        this.staffEducationDegree = staffEducationDegree;

        // 8. Cấp bậc
        this.rankTitleJoined = "";

        // 9. Vị trí làm việc
        this.titleJoined = "";

        // 10. Loại HĐLĐ
        this.contractType = "";

        // 11. Thời điểm bắt đầu làm việc
        this.signDateContract = "";

        // 12. Tham gia bảo hiểm BHXH
        this.bhxhSalary = "";

        // 13. Tham gia bảo hiểm BHYT
        this.bhytSalary = "";

        // 14. Tham gia bảo hiểm BHTN
        this.bhtnSalary = "";

        // 15. Tiền lương cơ bản
        this.insuranceSalaryStr = "";

        // 16. Nâng bậc, nâng lương
        this.upSalaryInfo = "";

        // 17. Số ngày nghỉ trong năm
        this.leaveDays = "";

        // 18. Số giờ làm thêm
        this.otHours = "";

        // 19. Hưởng chế độ BHXH, BHYT, BHTN
        this.socialInsuranceBenefitEligible = "";

        // 20. Học nghề, đào tạo, bồi dưỡng, nâng cao trình độ kỹ năng nghề
        this.studyInfo = "";

        // 21. Kỷ luật lao động, trách nhiệm vật chất
        this.disciplineInfo = "";

        // 22. Tai nạn lao động, bệnh nghề nghiệp
        this.occupationalAccidentInfo = "";

        // 23. Thời điểm chấm dứt HĐLĐ và lý do
        this.endDateContract = "";

    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getNationalityName() {
        return nationalityName;
    }

    public void setNationalityName(String nationalityName) {
        this.nationalityName = nationalityName;
    }

    public String getStaffAddress() {
        return staffAddress;
    }

    public void setStaffAddress(String staffAddress) {
        this.staffAddress = staffAddress;
    }

    public String getStaffIndentity() {
        return staffIndentity;
    }

    public void setStaffIndentity(String staffIndentity) {
        this.staffIndentity = staffIndentity;
    }

    public String getStaffEducationDegree() {
        return staffEducationDegree;
    }

    public void setStaffEducationDegree(String staffEducationDegree) {
        this.staffEducationDegree = staffEducationDegree;
    }

    public String getRankTitleJoined() {
        return rankTitleJoined;
    }

    public void setRankTitleJoined(String rankTitleJoined) {
        this.rankTitleJoined = rankTitleJoined;
    }

    public String getTitleJoined() {
        return titleJoined;
    }

    public void setTitleJoined(String titleJoined) {
        this.titleJoined = titleJoined;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getSignDateContract() {
        return signDateContract;
    }

    public void setSignDateContract(String signDateContract) {
        this.signDateContract = signDateContract;
    }

    public String getBhxhSalary() {
        return bhxhSalary;
    }

    public void setBhxhSalary(String bhxhSalary) {
        this.bhxhSalary = bhxhSalary;
    }

    public String getBhytSalary() {
        return bhytSalary;
    }

    public void setBhytSalary(String bhytSalary) {
        this.bhytSalary = bhytSalary;
    }

    public String getBhtnSalary() {
        return bhtnSalary;
    }

    public void setBhtnSalary(String bhtnSalary) {
        this.bhtnSalary = bhtnSalary;
    }

    public String getInsuranceSalaryStr() {
        return insuranceSalaryStr;
    }

    public void setInsuranceSalaryStr(String insuranceSalaryStr) {
        this.insuranceSalaryStr = insuranceSalaryStr;
    }

    public String getUpSalaryInfo() {
        return upSalaryInfo;
    }

    public void setUpSalaryInfo(String upSalaryInfo) {
        this.upSalaryInfo = upSalaryInfo;
    }

    public String getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(String leaveDays) {
        this.leaveDays = leaveDays;
    }

    public String getOtHours() {
        return otHours;
    }

    public void setOtHours(String otHours) {
        this.otHours = otHours;
    }

    public String getSocialInsuranceBenefitEligible() {
        return socialInsuranceBenefitEligible;
    }

    public void setSocialInsuranceBenefitEligible(String socialInsuranceBenefitEligible) {
        this.socialInsuranceBenefitEligible = socialInsuranceBenefitEligible;
    }

    public String getStudyInfo() {
        return studyInfo;
    }

    public void setStudyInfo(String studyInfo) {
        this.studyInfo = studyInfo;
    }

    public String getDisciplineInfo() {
        return disciplineInfo;
    }

    public void setDisciplineInfo(String disciplineInfo) {
        this.disciplineInfo = disciplineInfo;
    }

    public String getOccupationalAccidentInfo() {
        return occupationalAccidentInfo;
    }

    public void setOccupationalAccidentInfo(String occupationalAccidentInfo) {
        this.occupationalAccidentInfo = occupationalAccidentInfo;
    }

    public String getEndDateContract() {
        return endDateContract;
    }

    public void setEndDateContract(String endDateContract) {
        this.endDateContract = endDateContract;
    }


}
