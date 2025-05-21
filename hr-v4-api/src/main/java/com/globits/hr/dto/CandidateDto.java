package com.globits.hr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.domain.AdministrativeUnit;
import com.globits.core.domain.FileDescription;
import com.globits.core.domain.PersonAddress;
import com.globits.core.dto.*;
import com.globits.hr.domain.*;
import com.globits.security.dto.UserDto;
import com.globits.template.dto.ContentTemplateDto;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class CandidateDto extends PersonDto {
    // tab 1 - Thông tin ứng viên
    private String candidateCode; // ma ung vien

    private RecruitmentRoundDto currentRound;

    private  CandidateRecruitmentRoundDto currentCandidateRound;
    // Cac truong o duoi day da duoc luu trong Person
    // private String lastName; // ho
    // private String firstName; // ten ung vien
    // private String displayName; // ten hien thi
    // private Date birthDate; // ngay sinh
    // private String gender; // gioi tinh
    // private String idNumber; // so CMND/CCCD
    // private Date idNumberIssueDate; // ngay cap CMND/CCCD
    // private String idNumberIssueBy; // noi cap CMND/CCCD
    // private String phoneNumber; // dien thoai
    // private String email; // email
    // private CountryDto nationality; // quoc tich
    // private EthnicsDto ethnics; // dan toc
    // private ReligionDto religion; // ton giao
    // private AdministrativeUnitDto nativeVillage; // que quan = nguyen quan
    // private Set<PersonAddressDto> addresses; // danh sach dia chi noi o
    // private String imagePath; // anh ung vien
    // private Integer maritalStatus; // tinh trang hon nhan - tham khảo staff

    private String permanentResidence; // Hộ khẩu thường trú
    private String currentResidence; // Noi o hien tai
    private HrAdministrativeUnitDto administrativeUnit; // xa phuong, tinh thanh, que quan
    private HrAdministrativeUnitDto district;
    private HrAdministrativeUnitDto province;

    // tab 2 - Thông tin tuyển dụng
    private RecruitmentDto recruitment; // dot tuyen dung
    private RecruitmentPlanDto recruitmentPlan;
    private PositionTitleDto positionTitle; // vi tri ung tuyen

    private Date submissionDate; // Ngày nop ho so
    private Date interviewDate; // Ngày phong van = Ngày gap mat ung vien
    private Double desiredPay; // muc luong mong muon
    private Date possibleWorkingDate; // Ngày co the lam viec
    private Date onboardDate; // Ngày tiếp nhận nhân viên

    private List<CandidateRecruitmentRoundDto> recruitmentRoundResults;// ket qua

    // tab 3 - Trình độ học vấn/quá trình đào tạo
    private List<CandidateEducationHistoryDto> candidateEducationalHistories; // Quá trình đào tạo của ứng viên

    // tab 4 - Chứng chỉ (hiện có của nhân viên)
    private List<PersonCertificateDto> candidateCertificates; // Bang cap, Chứng chỉ (hiện có của nhân viên)

    // tab 5 - Kinh nghiệm làm việc
    private List<CandidateWorkingExperienceDto> candidateWorkingExperiences; // Kinh nghiệm làm việc

    // tab 6 - Tệp đính kèm
    private List<CandidateAttachmentDto> candidateAttachments; // Tệp đính kèm

    // Các trường khác
    private Integer preScreenStatus; //PreScreenStatus trạng thái sơ lọc
    // list status of candidate: Trang thai ho so ung vien
    private Integer approvalStatus; // trang thai ho so ung vien da duoc duyet hay chua
    // Xem status: HrConstants.CandidateApprovalStatus
    private Integer examStatus; // trang thai ung vien co PASS bai test cua dot phong van/thi tuyen hay khong
    // Xem status: HrConstants.CandidateExamStatus
    private Integer receptionStatus; // trạng thái của ứng viên sau khi đã PASS bài phỏng vấn/thi tuyển, trạng thái này chỉ ứng viên có được nhận việc hay không
    // Xem status: HrConstants.CandidateReceptionStatus
    private Integer onboardStatus; // trạng thái chỉ tình trạng nhận việc của ứng viên (không đến nhận việc, đã nhận việc,...)
    // Xem status: HrConstants.CandidateOnboardStatus
    private String refusalReason; // lý do từ chối

    private StaffDto staff; // nhan vien duoc tao sau khi ung vien da nhan viec thanh cong
    private StaffDto introducer; // nguoi gioi thieu

    private HrOrganizationDto organization;

    private HRDepartmentDto department;
    private Integer status; // trang thai ho so ung vien

    private String errorMessage;
    private Boolean isEnterdCandidateProfile;//la ho so ung vien tu nhap link public ngoai

    private Double probationIncome; // lương thử việc

    private Double basicIncome; // lương chính thức

    private Double positionBonus; // thưởng

    private Double allowance; //trợ cấp

    private Double otherBenefit; // khoản khác
    private String personalIdentificationNumber;
    private Date personalIdentificationIssueDate;
    private String personalIdentificationIssuePlace;
    private ContentTemplateDto template;
    private Boolean isEdit = false;
    private List<MultipartFile> files;
    public CandidateDto() {
    }

    public CandidateDto(Candidate entity) {
        if (entity == null) {
            return;
        }

        this.setMaritalStatus(entity.getMaritalStatus());
        this.setIdNumberIssueBy(entity.getIdNumberIssueBy());
        this.setCandidateCode(entity.getCandidateCode());
        this.setId(entity.getId());
        this.setBirthDate(entity.getBirthDate());
        this.setDisplayName(entity.getDisplayName());
        this.setEmail(entity.getEmail());
        this.setFirstName(entity.getFirstName());
        this.setLastName(entity.getLastName());
        this.setGender(entity.getGender());
        this.setIdNumber(entity.getIdNumber());
        this.setPhoneNumber(entity.getPhoneNumber());
        this.status = entity.getStatus();
        this.preScreenStatus = entity.getPreScreenStatus();
        this.isEnterdCandidateProfile=entity.getIsEnterdCandidateProfile();
        this.personalIdentificationNumber = entity.getPersonalIdentificationNumber();
        this.personalIdentificationIssueDate = entity.getPersonalIdentificationIssueDate();
        this.personalIdentificationIssuePlace = entity.getPersonalIdentificationIssuePlace();
        if (entity.getNativeVillage() != null) {
            this.setNativeVillage(new AdministrativeUnitDto(entity.getNativeVillage()));
        }
        // tab 2
        if (entity.getRecruitment() != null) {
            this.setRecruitment(new RecruitmentDto(entity.getRecruitment()));
        }

        if (entity.getRecruitmentPlan() != null) {
            this.recruitmentPlan = new RecruitmentPlanDto(entity.getRecruitmentPlan());
        }

        if (entity.getPositionTitle() != null) {
            this.setPositionTitle(new PositionTitleDto());
            this.getPositionTitle().setId(entity.getPositionTitle().getId());
            this.getPositionTitle().setCode(entity.getPositionTitle().getCode());
            this.getPositionTitle().setName(entity.getPositionTitle().getName());
            this.getPositionTitle().setDescription(entity.getPositionTitle().getDescription());
        }
        if (entity.getOrganization() != null) {
            this.setOrganization(new HrOrganizationDto());
            this.getOrganization().setId(entity.getOrganization().getId());
            this.getOrganization().setName(entity.getOrganization().getName());
            this.getOrganization().setCode(entity.getOrganization().getCode());
        }
        if (entity.getDepartment() != null) {
            this.setDepartment(new HRDepartmentDto());
            this.getDepartment().setId(entity.getDepartment().getId());
            this.getDepartment().setName(entity.getDepartment().getName());
            this.getDepartment().setCode(entity.getDepartment().getCode());
        }
        // que quan
        if (entity.getNativeVillage() != null) {
            this.setNativeVillage(new AdministrativeUnitDto());
            this.getNativeVillage().setId(entity.getNativeVillage().getId());
            this.getNativeVillage().setCode(entity.getNativeVillage().getCode());
            this.getNativeVillage().setName(entity.getNativeVillage().getName());
        }

        // ket qua ung tuyen
        if (entity.getCandidateRecruitmentRounds() != null && !entity.getCandidateRecruitmentRounds().isEmpty()) {
            List<CandidateRecruitmentRoundDto> recruitmentRounds = new ArrayList<CandidateRecruitmentRoundDto>();

            for (CandidateRecruitmentRound candidateRecruitmentRound : entity.getCandidateRecruitmentRounds()) {
                CandidateRecruitmentRoundDto candidateRecruitmentRoundDto = new CandidateRecruitmentRoundDto(candidateRecruitmentRound);

                recruitmentRounds.add(candidateRecruitmentRoundDto);
            }

            this.recruitmentRoundResults = recruitmentRounds;
        }

        // noi o hien tai
        this.setCurrentResidence(entity.getCurrentResidence());

        // list status of candidate: Trang thai ho so ung vien
        this.approvalStatus = entity.getApprovalStatus();
        this.examStatus = entity.getExamStatus();
        this.receptionStatus = entity.getReceptionStatus();
        this.onboardStatus = entity.getOnboardStatus();

        this.refusalReason = entity.getRefusalReason();
        this.submissionDate = (entity.getSubmissionDate());
        this.interviewDate = (entity.getInterviewDate());
        this.onboardDate = entity.getOnboardDate();

        if (entity.getStaff() != null) {
            StaffDto staff = new StaffDto(entity.getStaff(), false, false);
            this.setStaff(staff);
        }

        if (entity.getIntroducer() != null) {
            StaffDto introducer = new StaffDto(entity.getIntroducer(), false, false);
            this.setIntroducer(introducer);
        }
        this.probationIncome = entity.getProbationIncome();
        this.basicIncome = entity.getBasicIncome();
        this.positionBonus = entity.getPositionBonus();
        this.allowance = entity.getAllowance();
        this.otherBenefit = entity.getOtherBenefit();
    }

    public CandidateDto(Candidate entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || isDetail.equals(false))
            return;

        this.setInterviewDate(entity.getInterviewDate());
        this.setDesiredPay(entity.getDesiredPay());
        this.setPossibleWorkingDate(entity.getPossibleWorkingDate());
        this.setIsEnterdCandidateProfile(entity.getIsEnterdCandidateProfile());
        if (entity.getRecruitmentPlan() != null) {
            this.recruitmentPlan = new RecruitmentPlanDto();
            this.recruitmentPlan.setId(entity.getRecruitmentPlan().getId());
            this.recruitmentPlan.setName(entity.getRecruitmentPlan().getName());
            this.recruitmentPlan.setCode(entity.getRecruitmentPlan().getCode());
            this.recruitmentPlan.setDescription(entity.getRecruitmentPlan().getDescription());
        }
        if (entity.getNationality() != null) {
            this.setNationality(new CountryDto());
            this.getNationality().setId(entity.getNationality().getId());
            this.getNationality().setCode(entity.getNationality().getCode());
            this.getNationality().setName(entity.getNationality().getName());
        }

        if (entity.getEthnics() != null) {
            this.setEthnics(new EthnicsDto());
            this.getEthnics().setId(entity.getEthnics().getId());
            this.getEthnics().setCode(entity.getEthnics().getCode());
            this.getEthnics().setName(entity.getEthnics().getName());
        }

        if (entity.getReligion() != null) {
            this.setReligion(new ReligionDto());
            this.getReligion().setId(entity.getReligion().getId());
            this.getReligion().setCode(entity.getReligion().getCode());
            this.getReligion().setName(entity.getReligion().getName());
        }

        if (entity.getAdministrativeUnit() != null) {
            this.setAdministrativeUnit(new HrAdministrativeUnitDto());
            this.getAdministrativeUnit().setId(entity.getAdministrativeUnit().getId());
            this.getAdministrativeUnit().setName(entity.getAdministrativeUnit().getName());
            this.getAdministrativeUnit().setCode(entity.getAdministrativeUnit().getCode());

            if (entity.getAdministrativeUnit().getParent() != null) {
                AdministrativeUnit district = entity.getAdministrativeUnit().getParent();
                this.setDistrict(new HrAdministrativeUnitDto());
                this.getDistrict().setId(district.getId());
                this.getDistrict().setCode(district.getCode());
                this.getDistrict().setName(district.getName());

                if (district.getParent() != null) {
                    AdministrativeUnit province = district.getParent();
                    this.setProvince(new HrAdministrativeUnitDto());
                    this.getProvince().setId(province.getId());
                    this.getProvince().setCode(province.getCode());
                    this.getProvince().setName(province.getName());
                }
            }
        }

        this.setPermanentResidence(entity.getPermanentResidence());

        this.setIdCitizen(entity.getIdCitizen());
        this.setIdNumberIssueBy(entity.getIdNumberIssueBy());
        this.setIdNumberIssueDate(entity.getIdNumberIssueDate());
        this.setPhotoCropped(entity.getPhotoCropped());
        this.setShortName(entity.getShortName());
        this.setImagePath(entity.getImagePath());
        this.setBirthPlace(entity.getBirthPlace());

        // tab Quá trình đào tạo của ứng viên
        if (entity.getCandidateEducationHistory() != null && entity.getCandidateEducationHistory().size() > 0) {
            List<CandidateEducationHistoryDto> educationHistoryList = new ArrayList<CandidateEducationHistoryDto>();

            for (CandidateEducationHistory educationHistory : entity.getCandidateEducationHistory()) {
                CandidateEducationHistoryDto item = new CandidateEducationHistoryDto(educationHistory);
                educationHistoryList.add(item);
            }

            Collections.sort(educationHistoryList, new Comparator<CandidateEducationHistoryDto>() {
                @Override
                public int compare(CandidateEducationHistoryDto c1, CandidateEducationHistoryDto c2) {
                    // First, compare by startDate
                    if (c1.getStartDate() == null && c2.getStartDate() == null) {
                        return 0;
                    }
                    if (c1.getStartDate() == null) {
                        return 1;  // Null startDate should come last
                    }
                    if (c2.getStartDate() == null) {
                        return -1; // Null startDate should come last
                    }

                    int cmpRes = c1.getStartDate().compareTo(c2.getStartDate());
                    if (cmpRes != 0) {
                        return cmpRes;  // If startDate is different, return the comparison result
                    }

                    // If startDate is the same, compare by endDate
                    if (c1.getEndDate() == null && c2.getEndDate() == null) {
                        return 0;
                    }
                    if (c1.getEndDate() == null) {
                        return 1;  // Null endDate should come last
                    }
                    if (c2.getEndDate() == null) {
                        return -1; // Null endDate should come last
                    }

                    return c1.getEndDate().compareTo(c2.getEndDate());  // Compare by endDate if startDates are the same
                }
            });

            this.setCandidateEducationalHistories(educationHistoryList);
        }

        if (entity.getCandidateRecruitmentRounds() != null && !entity.getCandidateRecruitmentRounds().isEmpty()) {
            List<CandidateRecruitmentRoundDto> candidateRecruitmentRoundList = new ArrayList<>();

            for (CandidateRecruitmentRound candidateRecruitmentRound : entity.getCandidateRecruitmentRounds()) {
                CandidateRecruitmentRoundDto item = new CandidateRecruitmentRoundDto(candidateRecruitmentRound);
                candidateRecruitmentRoundList.add(item);
            }
//            candidateRecruitmentRoundList.sort(Comparator.comparing(CandidateRecruitmentRoundDto::getCreateDate).reversed());
            this.setRecruitmentRoundResults(candidateRecruitmentRoundList);
        }
        // tab Chung chi, bang cap
        if (entity.getCandidateCertificates() != null && entity.getCandidateCertificates().size() > 0) {
            List<PersonCertificateDto> candidateCertificates = new ArrayList<PersonCertificateDto>();

            for (PersonCertificate certificateItem : entity.getCandidateCertificates()) {
                PersonCertificateDto certificateDto = new PersonCertificateDto(certificateItem);
                candidateCertificates.add(certificateDto);
            }

            Collections.sort(candidateCertificates, new Comparator<PersonCertificateDto>() {
                @Override
                public int compare(PersonCertificateDto c1, PersonCertificateDto c2) {
                    if (c1.getIssueDate() == null && c2.getIssueDate() == null) {
                        return 0;
                    }
                    if (c1.getIssueDate() == null) {
                        return 1;
                    }
                    if (c2.getIssueDate() == null) {
                        return -1;
                    }
                    int cmpRes = c1.getIssueDate().compareTo(c2.getIssueDate());
                    if (cmpRes == -1) return 1;
                    if (cmpRes == 1) return -1;
                    return 0;
                }
            });

            this.setCandidateCertificates(candidateCertificates);
        }

        // tab Kinh nghiệm làm việc của ứng viên ở các công ty/tổ chức cũ
        if (entity.getCandidateWorkingExperience() != null && entity.getCandidateWorkingExperience().size() > 0) {
            List<CandidateWorkingExperienceDto> workingExperienceList = new ArrayList<>();

            for (CandidateWorkingExperience workingExperience : entity.getCandidateWorkingExperience()) {
                CandidateWorkingExperienceDto attachmentDto = new CandidateWorkingExperienceDto(workingExperience);
                workingExperienceList.add(attachmentDto);
            }

            Collections.sort(workingExperienceList, new Comparator<CandidateWorkingExperienceDto>() {
                @Override
                public int compare(CandidateWorkingExperienceDto c1, CandidateWorkingExperienceDto c2) {
                    // First, compare by startDate
                    if (c1.getStartDate() == null && c2.getStartDate() == null) {
                        return 0;
                    }
                    if (c1.getStartDate() == null) {
                        return 1;  // Null startDate should come last
                    }
                    if (c2.getStartDate() == null) {
                        return -1; // Null startDate should come last
                    }

                    int cmpRes = c1.getStartDate().compareTo(c2.getStartDate());
                    if (cmpRes != 0) {
                        return cmpRes;  // If startDate is different, return the comparison result
                    }

                    // If startDate is the same, compare by endDate
                    if (c1.getEndDate() == null && c2.getEndDate() == null) {
                        return 0;
                    }
                    if (c1.getEndDate() == null) {
                        return 1;  // Null endDate should come last
                    }
                    if (c2.getEndDate() == null) {
                        return -1; // Null endDate should come last
                    }

                    return c1.getEndDate().compareTo(c2.getEndDate());  // Compare by endDate if startDates are the same
                }
            });


            this.setCandidateWorkingExperiences(workingExperienceList);
        }

        // tab Các tệp đính kèm của ứng viên
        if (entity.getCandidateAttachments() != null && entity.getCandidateAttachments().size() > 0) {
            List<CandidateAttachmentDto> candidateAttachments = new ArrayList<>();

            for (CandidateAttachment attachment : entity.getCandidateAttachments()) {
                CandidateAttachmentDto attachmentDto = new CandidateAttachmentDto(attachment);
                candidateAttachments.add(attachmentDto);
            }

            Collections.sort(candidateAttachments, new Comparator<CandidateAttachmentDto>() {
                @Override
                public int compare(CandidateAttachmentDto c1, CandidateAttachmentDto c2) {
                    if (c1.getName() == null && c2.getName() == null) {
                        return 0;
                    }
                    if (c1.getName() == null) {
                        return 1;
                    }
                    if (c2.getName() == null) {
                        return -1;
                    }
                    int cmpRes = c1.getName().compareTo(c2.getName());
                    if (cmpRes == -1) return 1;
                    if (cmpRes == 1) return -1;
                    return 0;
                }
            });

            this.setCandidateAttachments(candidateAttachments);
        }
    }

    public CandidateRecruitmentRoundDto getCurrentCandidateRound() {
        return currentCandidateRound;
    }

    public void setCurrentCandidateRound(CandidateRecruitmentRoundDto currentCandidateRound) {
        this.currentCandidateRound = currentCandidateRound;
    }

    public Integer getPreScreenStatus() {
        return preScreenStatus;
    }

    public void setPreScreenStatus(Integer preScreenStatus) {
        this.preScreenStatus = preScreenStatus;
    }

    public RecruitmentRoundDto getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(RecruitmentRoundDto currentRound) {
        this.currentRound = currentRound;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCandidateCode() {
        return candidateCode;
    }

    public void setCandidateCode(String candidateCode) {
        this.candidateCode = candidateCode;
    }

    public RecruitmentDto getRecruitment() {
        return recruitment;
    }

    public void setRecruitment(RecruitmentDto recruitment) {
        this.recruitment = recruitment;
    }

    public PositionTitleDto getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitleDto positionTitle) {
        this.positionTitle = positionTitle;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Date getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(Date interviewDate) {
        this.interviewDate = interviewDate;
    }

    public Double getDesiredPay() {
        return desiredPay;
    }

    public void setDesiredPay(Double desiredPay) {
        this.desiredPay = desiredPay;
    }

    public Date getPossibleWorkingDate() {
        return possibleWorkingDate;
    }

    public void setPossibleWorkingDate(Date possibleWorkingDate) {
        this.possibleWorkingDate = possibleWorkingDate;
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

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Integer getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(Integer examStatus) {
        this.examStatus = examStatus;
    }

    public Integer getReceptionStatus() {
        return receptionStatus;
    }

    public void setReceptionStatus(Integer receptionStatus) {
        this.receptionStatus = receptionStatus;
    }

    public HrAdministrativeUnitDto getAdministrativeUnit() {
        return administrativeUnit;
    }

    public void setAdministrativeUnit(HrAdministrativeUnitDto administrativeUnit) {
        this.administrativeUnit = administrativeUnit;
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

    public List<PersonCertificateDto> getCandidateCertificates() {
        return candidateCertificates;
    }

    public void setCandidateCertificates(List<PersonCertificateDto> candidateCertificates) {
        this.candidateCertificates = candidateCertificates;
    }

    public String getRefusalReason() {
        return refusalReason;
    }

    public void setRefusalReason(String refusalReason) {
        this.refusalReason = refusalReason;
    }

    public Date getOnboardDate() {
        return onboardDate;
    }

    public void setOnboardDate(Date onboardDate) {
        this.onboardDate = onboardDate;
    }

    public Integer getOnboardStatus() {
        return onboardStatus;
    }

    public void setOnboardStatus(Integer onboardStatus) {
        this.onboardStatus = onboardStatus;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public List<CandidateEducationHistoryDto> getCandidateEducationalHistories() {
        return candidateEducationalHistories;
    }

    public void setCandidateEducationalHistories(List<CandidateEducationHistoryDto> candidateEducationalHistories) {
        this.candidateEducationalHistories = candidateEducationalHistories;
    }

    public List<CandidateWorkingExperienceDto> getCandidateWorkingExperiences() {
        return candidateWorkingExperiences;
    }

    public void setCandidateWorkingExperiences(List<CandidateWorkingExperienceDto> candidateWorkingExperiences) {
        this.candidateWorkingExperiences = candidateWorkingExperiences;
    }

    public List<CandidateAttachmentDto> getCandidateAttachments() {
        return candidateAttachments;
    }

    public void setCandidateAttachments(List<CandidateAttachmentDto> candidateAttachments) {
        this.candidateAttachments = candidateAttachments;
    }

    public List<CandidateRecruitmentRoundDto> getRecruitmentRoundResults() {
        return recruitmentRoundResults;
    }

    public void setRecruitmentRoundResults(List<CandidateRecruitmentRoundDto> recruitmentRoundResults) {
        this.recruitmentRoundResults = recruitmentRoundResults;
    }

    public HrOrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganizationDto organization) {
        this.organization = organization;
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    public StaffDto getIntroducer() {
        return introducer;
    }

    public void setIntroducer(StaffDto introducer) {
        this.introducer = introducer;
    }

    public RecruitmentPlanDto getRecruitmentPlan() {
        return recruitmentPlan;
    }

    public void setRecruitmentPlan(RecruitmentPlanDto recruitmentPlan) {
        this.recruitmentPlan = recruitmentPlan;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

	public Boolean getIsEnterdCandidateProfile() {
		return isEnterdCandidateProfile;
	}

	public void setIsEnterdCandidateProfile(Boolean isEnterdCandidateProfile) {
		this.isEnterdCandidateProfile = isEnterdCandidateProfile;
	}

    public Boolean getEnterdCandidateProfile() {
        return isEnterdCandidateProfile;
    }

    public void setEnterdCandidateProfile(Boolean enterdCandidateProfile) {
        isEnterdCandidateProfile = enterdCandidateProfile;
    }

    public Double getProbationIncome() {
        return probationIncome;
    }

    public void setProbationIncome(Double probationIncome) {
        this.probationIncome = probationIncome;
    }

    public Double getBasicIncome() {
        return basicIncome;
    }

    public void setBasicIncome(Double basicIncome) {
        this.basicIncome = basicIncome;
    }

    public Double getPositionBonus() {
        return positionBonus;
    }

    public void setPositionBonus(Double positionBonus) {
        this.positionBonus = positionBonus;
    }

    public Double getAllowance() {
        return allowance;
    }

    public void setAllowance(Double allowance) {
        this.allowance = allowance;
    }

    public Double getOtherBenefit() {
        return otherBenefit;
    }

    public void setOtherBenefit(Double otherBenefit) {
        this.otherBenefit = otherBenefit;
    }

    public String getPersonalIdentificationIssuePlace() {
        return personalIdentificationIssuePlace;
    }

    public void setPersonalIdentificationIssuePlace(String personalIdentificationIssuePlace) {
        this.personalIdentificationIssuePlace = personalIdentificationIssuePlace;
    }

    public Date getPersonalIdentificationIssueDate() {
        return personalIdentificationIssueDate;
    }

    public void setPersonalIdentificationIssueDate(Date personalIdentificationIssueDate) {
        this.personalIdentificationIssueDate = personalIdentificationIssueDate;
    }

    public String getPersonalIdentificationNumber() {
        return personalIdentificationNumber;
    }

    public void setPersonalIdentificationNumber(String personalIdentificationNumber) {
        this.personalIdentificationNumber = personalIdentificationNumber;
    }

    public ContentTemplateDto getTemplate() {
        return template;
    }

    public void setTemplate(ContentTemplateDto template) {
        this.template = template;
    }

    public Boolean getIsEdit() {
        return this.isEdit;
    }

    public void setIsEdit(Boolean isEdit) {
        this.isEdit = isEdit;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }
}
