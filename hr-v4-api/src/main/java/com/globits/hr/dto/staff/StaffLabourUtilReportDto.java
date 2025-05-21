package com.globits.hr.dto.staff;

import com.globits.hr.domain.Staff;
import com.globits.hr.utils.ExportExcelUtil;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class StaffLabourUtilReportDto {
    private UUID staffId;

    // 0. STT
    private String orderNumber;

    // 1. Họ tên
    private String displayName;

    // 2. Mã số BHXH
    private String socialInsuranceNumber; // <-- Chưa có, cần bổ sung

    // 3. Ngày sinh
    private String birthDate;

    // 4. Giới tính
    private String gender;

    // 5. Số CCCD/ CMND/ Hộ chiếu
    private String staffIndentity;

    // 6. Cấp bậc, chức vụ, chức danh nghề, nơi làm việc
    private String rankTitleJoined;

    // 7. Nhà quản lý
    private String isManager; // <-- Chưa có, cần bổ sung (boolean/String)

    // 8. Chuyên môn kỹ thuật bậc cao
    private String highTechQualification; // <-- Chưa có, cần bổ sung

    // 9. Chuyên môn kỹ thuật bậc trung
    private String midTechQualification; // <-- Chưa có, cần bổ sung

    // 10. Khác
    private String otherQualification; // <-- Chưa có, cần bổ sung

    // 11. Hệ số/ Mức lương
    private String salaryCoefficient; // <-- Chưa có, cần bổ sung

    // 12. Phụ cấp - Chức vụ
    private String positionAllowance; // <-- Chưa có, cần bổ sung

    // 13. Phụ cấp - Thâm niên VK (%)
    private String seniorityAllowanceWork; // <-- Chưa có, cần bổ sung

    // 14. Phụ cấp - Thâm niên nghề (%)
    private String seniorityAllowanceJob; // <-- Chưa có, cần bổ sung

    // 15. Phụ cấp - Phụ cấp lương
    private String salaryAllowance; // <-- Chưa có, cần bổ sung

    // 16. Phụ cấp - Các khoản bổ sung
    private String otherAllowance; // <-- Chưa có, cần bổ sung

    // 17. Ngày bắt đầu phụ cấp độc hại
    private String toxicAllowanceStartDate; // <-- Chưa có, cần bổ sung

    // 18. Ngày kết thúc phụ cấp độc hại
    private String toxicAllowanceEndDate; // <-- Chưa có, cần bổ sung

    // 19. Ngày bắt đầu HĐLĐ không xác định thời hạn
    private String indefiniteContractStartDate; // <-- Chưa có, cần bổ sung

    // 20. Hiệu lực HĐLĐ xác định thời hạn - Ngày bắt đầu
    private String definiteContractStartDate; // <-- Chưa có, cần bổ sung

    // 21. Hiệu lực HĐLĐ xác định thời hạn - Ngày kết thúc
    private String definiteContractEndDate; // <-- Chưa có, cần bổ sung

    // 22. Hiệu lực HĐLĐ khác (dưới 1 tháng, thử việc) - Ngày bắt đầu
    private String otherContractStartDate; // <-- Chưa có, cần bổ sung

    // 23. Hiệu lực HĐLĐ khác (dưới 1 tháng, thử việc) - Ngày kết thúc
    private String otherContractEndDate; // <-- Chưa có, cần bổ sung

    // 24. Thời điểm đơn vị bắt đầu đóng BHXH
    private String startSocialInsuranceDate; // <-- Chưa có, cần bổ sung

    // 25. Thời điểm đơn vị kết thúc đóng BHXH
    private String endSocialInsuranceDate; // <-- Chưa có, cần bổ sung

    // 26. Ghi chú
    private String note; // <-- Chưa có, cần bổ sung


    public StaffLabourUtilReportDto() {
    }

    private String formatDate(Date date) {
        if (date == null)
            return "";
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public StaffLabourUtilReportDto(Staff entity) {
        this.staffId = entity.getId();

        // 0. STT
        this.orderNumber = ""; // Chưa có

        // 1. Họ tên
        this.displayName = entity.getDisplayName();

        // 2. Mã số BHXH
        this.socialInsuranceNumber = entity.getSocialInsuranceNumber();

        // 3. Ngày sinh
        this.birthDate = (entity.getBirthDate() != null) ? formatDate(entity.getBirthDate()) : "";

        // 4. Giới tính
        this.gender = ExportExcelUtil.getGenderText(entity.getGender());

        // 5. Số CCCD/ CMND/ Hộ chiếu
        String staffIndentity = null;
        if (StringUtils.hasText(entity.getPersonalIdentificationNumber())) {
            staffIndentity = entity.getPersonalIdentificationNumber();
        } else if (StringUtils.hasText(entity.getIdNumber())) {
            staffIndentity = entity.getIdNumber();
        } else if (StringUtils.hasText(entity.getPassportNumber())) {
            staffIndentity = entity.getPassportNumber();
        } else {
            staffIndentity = "";
        }
        this.staffIndentity = staffIndentity;

        // 6. Cấp bậc, chức vụ, chức danh nghề, nơi làm việc
        this.rankTitleJoined = ""; // Chưa có

        // 7. Nhà quản lý
        this.isManager = ""; // Chưa có

        // 8. Chuyên môn kỹ thuật bậc cao
        this.highTechQualification = ""; // Chưa có

        // 9. Chuyên môn kỹ thuật bậc trung
        this.midTechQualification = ""; // Chưa có

        // 10. Khác
        this.otherQualification = ""; // Chưa có

        // 11. Hệ số/ Mức lương
        this.salaryCoefficient = ""; // Chưa có

        // 12. Phụ cấp - Chức vụ
        this.positionAllowance = ""; // Chưa có

        // 13. Phụ cấp - Thâm niên VK (%)
        this.seniorityAllowanceWork = ""; // Chưa có

        // 14. Phụ cấp - Thâm niên nghề (%)
        this.seniorityAllowanceJob = ""; // Chưa có

        // 15. Phụ cấp - Phụ cấp lương
        this.salaryAllowance = ""; // Chưa có

        // 16. Phụ cấp - Các khoản bổ sung
        this.otherAllowance = ""; // Chưa có

        // 17. Ngày bắt đầu phụ cấp độc hại
        this.toxicAllowanceStartDate = ""; // Chưa có

        // 18. Ngày kết thúc phụ cấp độc hại
        this.toxicAllowanceEndDate = ""; // Chưa có

        // 19. Ngày bắt đầu HĐLĐ không xác định thời hạn
        this.indefiniteContractStartDate = ""; // Chưa có

        // 20. Hiệu lực HĐLĐ xác định thời hạn - Ngày bắt đầu
        this.definiteContractStartDate = ""; // Chưa có

        // 21. Hiệu lực HĐLĐ xác định thời hạn - Ngày kết thúc
        this.definiteContractEndDate = ""; // Chưa có

        // 22. Hiệu lực HĐLĐ khác (dưới 1 tháng, thử việc) - Ngày bắt đầu
        this.otherContractStartDate = ""; // Chưa có

        // 23. Hiệu lực HĐLĐ khác (dưới 1 tháng, thử việc) - Ngày kết thúc
        this.otherContractEndDate = ""; // Chưa có

        // 24. Thời điểm đơn vị bắt đầu đóng BHXH
        this.startSocialInsuranceDate = ""; // Chưa có

        // 25. Thời điểm đơn vị kết thúc đóng BHXH
        this.endSocialInsuranceDate = ""; // Chưa có

        // 26. Ghi chú
        this.note = ""; // Chưa có

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

    public String getSocialInsuranceNumber() {
        return socialInsuranceNumber;
    }

    public void setSocialInsuranceNumber(String socialInsuranceNumber) {
        this.socialInsuranceNumber = socialInsuranceNumber;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStaffIndentity() {
        return staffIndentity;
    }

    public void setStaffIndentity(String staffIndentity) {
        this.staffIndentity = staffIndentity;
    }

    public String getRankTitleJoined() {
        return rankTitleJoined;
    }

    public void setRankTitleJoined(String rankTitleJoined) {
        this.rankTitleJoined = rankTitleJoined;
    }

    public String getIsManager() {
        return isManager;
    }

    public void setIsManager(String isManager) {
        this.isManager = isManager;
    }

    public String getHighTechQualification() {
        return highTechQualification;
    }

    public void setHighTechQualification(String highTechQualification) {
        this.highTechQualification = highTechQualification;
    }

    public String getMidTechQualification() {
        return midTechQualification;
    }

    public void setMidTechQualification(String midTechQualification) {
        this.midTechQualification = midTechQualification;
    }

    public String getOtherQualification() {
        return otherQualification;
    }

    public void setOtherQualification(String otherQualification) {
        this.otherQualification = otherQualification;
    }

    public String getSalaryCoefficient() {
        return salaryCoefficient;
    }

    public void setSalaryCoefficient(String salaryCoefficient) {
        this.salaryCoefficient = salaryCoefficient;
    }

    public String getPositionAllowance() {
        return positionAllowance;
    }

    public void setPositionAllowance(String positionAllowance) {
        this.positionAllowance = positionAllowance;
    }

    public String getSeniorityAllowanceWork() {
        return seniorityAllowanceWork;
    }

    public void setSeniorityAllowanceWork(String seniorityAllowanceWork) {
        this.seniorityAllowanceWork = seniorityAllowanceWork;
    }

    public String getSeniorityAllowanceJob() {
        return seniorityAllowanceJob;
    }

    public void setSeniorityAllowanceJob(String seniorityAllowanceJob) {
        this.seniorityAllowanceJob = seniorityAllowanceJob;
    }

    public String getSalaryAllowance() {
        return salaryAllowance;
    }

    public void setSalaryAllowance(String salaryAllowance) {
        this.salaryAllowance = salaryAllowance;
    }

    public String getOtherAllowance() {
        return otherAllowance;
    }

    public void setOtherAllowance(String otherAllowance) {
        this.otherAllowance = otherAllowance;
    }

    public String getToxicAllowanceStartDate() {
        return toxicAllowanceStartDate;
    }

    public void setToxicAllowanceStartDate(String toxicAllowanceStartDate) {
        this.toxicAllowanceStartDate = toxicAllowanceStartDate;
    }

    public String getToxicAllowanceEndDate() {
        return toxicAllowanceEndDate;
    }

    public void setToxicAllowanceEndDate(String toxicAllowanceEndDate) {
        this.toxicAllowanceEndDate = toxicAllowanceEndDate;
    }

    public String getIndefiniteContractStartDate() {
        return indefiniteContractStartDate;
    }

    public void setIndefiniteContractStartDate(String indefiniteContractStartDate) {
        this.indefiniteContractStartDate = indefiniteContractStartDate;
    }

    public String getDefiniteContractStartDate() {
        return definiteContractStartDate;
    }

    public void setDefiniteContractStartDate(String definiteContractStartDate) {
        this.definiteContractStartDate = definiteContractStartDate;
    }

    public String getDefiniteContractEndDate() {
        return definiteContractEndDate;
    }

    public void setDefiniteContractEndDate(String definiteContractEndDate) {
        this.definiteContractEndDate = definiteContractEndDate;
    }

    public String getOtherContractStartDate() {
        return otherContractStartDate;
    }

    public void setOtherContractStartDate(String otherContractStartDate) {
        this.otherContractStartDate = otherContractStartDate;
    }

    public String getOtherContractEndDate() {
        return otherContractEndDate;
    }

    public void setOtherContractEndDate(String otherContractEndDate) {
        this.otherContractEndDate = otherContractEndDate;
    }

    public String getStartSocialInsuranceDate() {
        return startSocialInsuranceDate;
    }

    public void setStartSocialInsuranceDate(String startSocialInsuranceDate) {
        this.startSocialInsuranceDate = startSocialInsuranceDate;
    }

    public String getEndSocialInsuranceDate() {
        return endSocialInsuranceDate;
    }

    public void setEndSocialInsuranceDate(String endSocialInsuranceDate) {
        this.endSocialInsuranceDate = endSocialInsuranceDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
