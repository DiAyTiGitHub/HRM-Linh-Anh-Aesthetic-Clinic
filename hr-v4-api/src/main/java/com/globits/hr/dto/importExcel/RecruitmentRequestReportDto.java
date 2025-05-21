package com.globits.hr.dto.importExcel;

import com.globits.hr.HrConstants;
import com.globits.hr.domain.Candidate;
import com.globits.hr.domain.RecruitmentPlan;
import com.globits.hr.domain.RecruitmentRequest;
import com.globits.hr.domain.RecruitmentRequestItem;
import com.globits.hr.utils.DateTimeUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RecruitmentRequestReportDto {
    // ID ứng viên
    private UUID recruitmentRequestId;

    // 0. STT
    private String index;

    // 1. ĐƠN VỊ
    private String unit;

    // 2. Ban
    private String division;

    // 3. PHÒNG/CƠ SỞ
    private String department;

    // 4. BỘ PHẬN
    private String subDepartment;

    // 5. NGƯỜI ĐỀ XUẤT
    private String proposer;

    // 6. VỊ TRÍ ĐỀ XUẤT TUYỂN DỤNG --> Chức danh tuyển dụng
    private String proposedPosition;

    // 7. NGÀY NHẬN ĐỀ XUẤT
    private String proposalDate;

    // 8. THỜI HẠN TUYỂN DỤNG ( Đơn vị ngày)
    private String recruitmentDurationDays;

    // 9. NGÀY HẾT HẠN TUYỂN DỤNG
    private String recruitmentDeadline;

    // 10. TÌNH TRẠNG
    private String status;

    // 11. TUYỂN MỚI
    private String isNewRecruitment;

    // 12. TUYỂN THAY THẾ
    private String isReplacementRecruitment;

    // 13. Tuyển trong định biên
    private String isWithinHeadcount;

    // 14. TUYỂN LỌC --> tuyển ngoài định biên
    private String isOutOfHeadcount;

    // 15. SỐ LƯỢNG YÊU CẦU TUYỂN DỤNG
    private String requestedQuantity;

    // 16. SỐ LƯỢNG NHÂN SỰ NHẬN VIỆC
    private String onboardedQuantity;

    // 17. SỐ LƯỢNG NHÂN SỰ CÒN LẠI CẦN TUYỂN DỤNG
    private String remainingQuantity;

    // 18. Số LƯỢNG NHÂN SỰ CHỜ NHẬN VIỆC
    private String pendingOnboardQuantity;

    // 19. Nguồn đăng tuyển
    private String recruitmentSource;

    // 20. NGÀY CHỐT OFFER VỚI ỨNG VIÊN
    private String offerClosedDate;

    // 21. NGÀY ỨNG VIÊN ONBOARD
    private String onboardDate;

    // 22. SỐ LƯỢNG NHÂN SỰ TỪ CHỐI OFFER
    private String offerDeclinedQuantity;

    // 23. SỐ LƯỢNG NHÂN SỰ NGHỈ VIỆC TRONG THỜI GIAN THỬ VIỆC
    private String probationQuitQuantity;

    // 24. HR PHỤ TRÁCH
    private String hrInCharge;

    // 25. NOTE
    private String note;

    public RecruitmentRequestReportDto() {
    }

    public RecruitmentRequestReportDto(RecruitmentRequest entity) {
        if (entity == null) return;

        this.recruitmentRequestId = entity.getId();

        // 0. STT
        this.index = "";

        // 1. ĐƠN VỊ
        this.unit = "";
        if (entity.getHrOrganization() != null) {
            this.unit = entity.getHrOrganization().getName();
        }

        // 3. PHÒNG/CƠ SỞ
        this.department = "";
        if (entity.getHrDepartment() != null) {
            this.department = entity.getHrDepartment().getName();
        }

        // 4. BỘ PHẬN
        this.subDepartment = "";
        if (entity.getTeam() != null) {
            this.subDepartment = entity.getTeam().getName();
        }

        // 2. Ban
        this.division = "";
        if (StringUtils.hasText(this.subDepartment)) {
            this.division = this.subDepartment;
        } else if (StringUtils.hasText(this.department)) {
            this.department = this.department;
        }

        // 5. NGƯỜI ĐỀ XUẤT
        this.proposer = "";
        if (entity.getProposer() != null && StringUtils.hasText(entity.getProposer().getDisplayName())) {
            this.proposer = entity.getProposer().getDisplayName();
        }

        RecruitmentRequestItem requestItem = null;
        if (entity.getRecruitmentRequestItems() != null && !entity.getRecruitmentRequestItems().isEmpty()) {
            requestItem = new ArrayList<>(entity.getRecruitmentRequestItems()).get(0);
        }

        // 6. VỊ TRÍ ĐỀ XUẤT TUYỂN DỤNG --> Chức danh tuyển dụng
        this.proposedPosition = "";
        if (requestItem != null && requestItem.getPositionTitle() != null) {
            this.proposedPosition = requestItem.getPositionTitle().getName();
        }

        // 7. NGÀY NHẬN ĐỀ XUẤT
        this.proposalDate = "";
        if (entity.getCreateDate() != null) {
            this.proposalDate = formatDate(entity.getProposalReceiptDate());
        }

        RecruitmentPlan recruitmentPlan = null;
        if (entity.getRecruitmentPlans() != null && !entity.getRecruitmentPlans().isEmpty()) {
            recruitmentPlan = entity.getRecruitmentPlans()
                    .stream().max(Comparator.comparing(RecruitmentPlan::getModifyDate))
                    .orElse(null);
        }

        // 8. THỜI HẠN TUYỂN DỤNG (Đơn vị ngày)
        this.recruitmentDurationDays = "";
        if (recruitmentPlan != null && recruitmentPlan.getEstimatedTimeFrom() != null && recruitmentPlan.getEstimatedTimeTo() != null && recruitmentPlan.getEstimatedTimeFrom().before(recruitmentPlan.getEstimatedTimeTo())) {
            int days = DateTimeUtil.getDaysBetweenDates(recruitmentPlan.getEstimatedTimeFrom(), recruitmentPlan.getEstimatedTimeTo()).size();
            this.recruitmentDurationDays = String.valueOf(days);
        }

        // 9. NGÀY HẾT HẠN TUYỂN DỤNG
        this.recruitmentDeadline = "";
        if (recruitmentPlan != null && recruitmentPlan.getEstimatedTimeTo() != null) {
            this.recruitmentDeadline = formatDate(recruitmentPlan.getEstimatedTimeTo());
        }

        // 10. TÌNH TRẠNG
        this.status = "";
        if (entity.getStatus() != null) {
            this.status = HrConstants.RecruitmentRequestStatus.getDescriptionByValue(entity.getStatus());
        }

        // 11. TUYỂN MỚI
        this.isNewRecruitment = "";

        // 12. TUYỂN THAY THẾ
        this.isReplacementRecruitment = "";

        // 13. Tuyển trong định biên
        this.isWithinHeadcount = "";
        if (requestItem != null && requestItem.getInPlanQuantity() != null) {
            this.isWithinHeadcount = String.valueOf(requestItem.getInPlanQuantity());
        }

        // 14. TUYỂN LỌC --> tuyển ngoài định biên
        this.isOutOfHeadcount = "";
        if (requestItem != null && requestItem.getExtraQuantity() != null) {
            this.isOutOfHeadcount = String.valueOf(requestItem.getExtraQuantity());
        }

        // 15. SỐ LƯỢNG YÊU CẦU TUYỂN DỤNG
        this.requestedQuantity = "";
        if (requestItem != null && requestItem.getAnnouncementQuantity() != null) {
            this.requestedQuantity = String.valueOf(requestItem.getAnnouncementQuantity());
        }


        // 16. SỐ LƯỢNG NHÂN SỰ NHẬN VIỆC
        this.onboardedQuantity = "";
        // 17. SỐ LƯỢNG NHÂN SỰ CÒN LẠI CẦN TUYỂN DỤNG
        this.remainingQuantity = "";
        // 18. Số LƯỢNG NHÂN SỰ CHỜ NHẬN VIỆC
        this.pendingOnboardQuantity = "";

        // 22. SỐ LƯỢNG NHÂN SỰ TỪ CHỐI OFFER
        this.offerDeclinedQuantity = "";
        // 23. SỐ LƯỢNG NHÂN SỰ NGHỈ VIỆC TRONG THỜI GIAN THỬ VIỆC
        this.probationQuitQuantity = "";
        if (recruitmentPlan != null) {
            if (!CollectionUtils.isEmpty(recruitmentPlan.getCandidates())) {
                int oQuantity = 0;
                int oDeclinedQuantity = 0;
                int pOnboardQuantity = 0;
                int pQuitQuantity = 0;
                for (Candidate candidate : recruitmentPlan.getCandidates()) {
                    if (candidate.getStatus() != null) {
                        if (candidate.getStatus().equals(HrConstants.CandidateStatus.ACCEPTED_ASSIGNMENT.getValue())) {
                            oQuantity++;
                        }else if(candidate.getStatus().equals(HrConstants.CandidateStatus.DECLINED_ASSIGNMENT.getValue())){
                            oDeclinedQuantity++;
                        }else if(candidate.getStatus().equals(HrConstants.CandidateStatus.PENDING_ASSIGNMENT.getValue())){
                            pOnboardQuantity++;
                        }else if(candidate.getStatus().equals(HrConstants.CandidateStatus.RESIGN.getValue())){
                            pQuitQuantity++;
                        }
                    }
                }
                this.onboardedQuantity = String.valueOf(oQuantity);
                this.offerDeclinedQuantity = String.valueOf(oDeclinedQuantity);
                this.pendingOnboardQuantity = String.valueOf(pOnboardQuantity);
                this.probationQuitQuantity = String.valueOf(pQuitQuantity);
                if (requestItem.getAnnouncementQuantity() != null) {
                    this.remainingQuantity = requestItem.getAnnouncementQuantity() - oQuantity + "";
                }
            }
        }
        // 11. TUYỂN MỚI
        this.isNewRecruitment = "";
        // 12. TUYỂN THAY THẾ
        this.isReplacementRecruitment = "";
        if(!CollectionUtils.isEmpty(entity.getPositionRequests())){
            this.isNewRecruitment =   requestItem.getAnnouncementQuantity() - entity.getPositionRequests().size() + "";
            this.isReplacementRecruitment = entity.getPositionRequests().size() + "";
        }else {
            this.isNewRecruitment = String.valueOf(requestItem.getAnnouncementQuantity());
        }
        // 19. Nguồn đăng tuyển
        this.recruitmentSource = "";

        // 20. NGÀY CHỐT OFFER VỚI ỨNG VIÊN
        this.offerClosedDate = "";

        // 21. NGÀY ỨNG VIÊN ONBOARD
        this.onboardDate = "";


        // 24. HR PHỤ TRÁCH
        this.hrInCharge = "";
        if (entity.getPersonInCharge() != null) {
            this.hrInCharge = entity.getPersonInCharge().getDisplayName();
        }

        // 25. NOTE
        // Lấy theo trường ghi chú ở kế hoạch tuyển dụng
        this.note = "";
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dateTime.format(formatter);
    }

    private String formatDate(Date date) {
        if (date == null)
            return "";
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public UUID getRecruitmentRequestId() {
        return recruitmentRequestId;
    }

    public void setRecruitmentRequestId(UUID recruitmentRequestId) {
        this.recruitmentRequestId = recruitmentRequestId;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSubDepartment() {
        return subDepartment;
    }

    public void setSubDepartment(String subDepartment) {
        this.subDepartment = subDepartment;
    }

    public String getProposer() {
        return proposer;
    }

    public void setProposer(String proposer) {
        this.proposer = proposer;
    }

    public String getProposedPosition() {
        return proposedPosition;
    }

    public void setProposedPosition(String proposedPosition) {
        this.proposedPosition = proposedPosition;
    }

    public String getProposalDate() {
        return proposalDate;
    }

    public void setProposalDate(String proposalDate) {
        this.proposalDate = proposalDate;
    }

    public String getRecruitmentDurationDays() {
        return recruitmentDurationDays;
    }

    public void setRecruitmentDurationDays(String recruitmentDurationDays) {
        this.recruitmentDurationDays = recruitmentDurationDays;
    }

    public String getRecruitmentDeadline() {
        return recruitmentDeadline;
    }

    public void setRecruitmentDeadline(String recruitmentDeadline) {
        this.recruitmentDeadline = recruitmentDeadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsNewRecruitment() {
        return isNewRecruitment;
    }

    public void setIsNewRecruitment(String isNewRecruitment) {
        this.isNewRecruitment = isNewRecruitment;
    }

    public String getIsReplacementRecruitment() {
        return isReplacementRecruitment;
    }

    public void setIsReplacementRecruitment(String isReplacementRecruitment) {
        this.isReplacementRecruitment = isReplacementRecruitment;
    }

    public String getIsWithinHeadcount() {
        return isWithinHeadcount;
    }

    public void setIsWithinHeadcount(String isWithinHeadcount) {
        this.isWithinHeadcount = isWithinHeadcount;
    }

    public String getIsOutOfHeadcount() {
        return isOutOfHeadcount;
    }

    public void setIsOutOfHeadcount(String isOutOfHeadcount) {
        this.isOutOfHeadcount = isOutOfHeadcount;
    }

    public String getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(String requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public String getOnboardedQuantity() {
        return onboardedQuantity;
    }

    public void setOnboardedQuantity(String onboardedQuantity) {
        this.onboardedQuantity = onboardedQuantity;
    }

    public String getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(String remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public String getPendingOnboardQuantity() {
        return pendingOnboardQuantity;
    }

    public void setPendingOnboardQuantity(String pendingOnboardQuantity) {
        this.pendingOnboardQuantity = pendingOnboardQuantity;
    }

    public String getRecruitmentSource() {
        return recruitmentSource;
    }

    public void setRecruitmentSource(String recruitmentSource) {
        this.recruitmentSource = recruitmentSource;
    }

    public String getOfferClosedDate() {
        return offerClosedDate;
    }

    public void setOfferClosedDate(String offerClosedDate) {
        this.offerClosedDate = offerClosedDate;
    }

    public String getOnboardDate() {
        return onboardDate;
    }

    public void setOnboardDate(String onboardDate) {
        this.onboardDate = onboardDate;
    }

    public String getOfferDeclinedQuantity() {
        return offerDeclinedQuantity;
    }

    public void setOfferDeclinedQuantity(String offerDeclinedQuantity) {
        this.offerDeclinedQuantity = offerDeclinedQuantity;
    }

    public String getProbationQuitQuantity() {
        return probationQuitQuantity;
    }

    public void setProbationQuitQuantity(String probationQuitQuantity) {
        this.probationQuitQuantity = probationQuitQuantity;
    }

    public String getHrInCharge() {
        return hrInCharge;
    }

    public void setHrInCharge(String hrInCharge) {
        this.hrInCharge = hrInCharge;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

//    public CandidateRecruitmentReportDto() {
//        if (entity == null) return;
//
//
//    }

}
