package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.EvaluationForm;
import com.globits.hr.domain.PositionTitle;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EvaluationFormDto extends BaseObjectDto {
    // thông tin nhân viên
    private UUID staffId;
    private String staffName;
    private String staffCode;
    private UUID staffPositionId;
    private String position;
    private UUID staffDepartmentId;
    private String department;
    private UUID staffDivisionId;
    private String division;
    private UUID staffTeamId;
    private String team;
    private UUID directManagerId;
    private String directManagerName;
    private Date hireDate;
    private Date previousContractDuration;
    private UUID contractTypeId;
    private String contractTypeCode;
    private String contractTypeName;
    //A. NỘI DUNG ĐÁNH GIÁ:
    private List<StaffEvaluationDto> items;
    private String advantage;//Ưu điểm
    private String disadvantage;//Nhược điểm
    private String companyPolicyCompliance;//Chấp hành nội quy, quy định của công ty
    private String coworkerRelationship;//Mối quan hệ với đồng nghiệp
    private String senseOfResponsibility;//Tinh thần trách nhiệm
    //B. KẾT LUẬN:
    private Boolean contractRecommendation; //Đạt yêu cầu, đề xuất ký HĐLĐ = true / false = Không đạt yêu cầu
    //Đạt yêu cầu, đề xuất ký HĐLĐ
    private Date contractRecommendationDateFrom; //Đạt yêu cầu, đề xuất ký HĐLĐ từ ngày
    private Date contractRecommendationDateTo; //Đạt yêu cầu, đề xuất ký HĐLĐ đến ngày
    private PositionTitleDto positionTitle;//Chức danh
    private UUID positionTitleId;//Chức danh
    private String positionTitleName;
    private UUID rankTitleId;//Cấp bậc
    private String rankTitleName;
    private Double baseSalary;//Lương cứng
    private Double allowanceAmount;//Phụ cấp
    private Date effectiveFromDate; //Thời gian áp dụng: Từ ngày
    //Không đạt yêu cầu
    private Boolean cooperationStatus; //Ngừng hợp tác kể từ ngày = true / Bố trí sang vị trí khác = false
    //Ngừng hợp tác kể từ ngày
    private Date collaborationEndDate; //Ngừng hợp tác kể từ ngày
    //Bố trí sang vị trí khác

    private UUID newPositionId;//Bố trí sang vị trí khác
    private String newPositionName;
    private Date newPositionTransferDate;

    public EvaluationFormDto() {
    }

    public EvaluationFormDto(EvaluationForm entity, List<StaffEvaluationDto> items) {
        super(entity);
        if (entity != null) {
            this.staffId = entity.getStaff() != null ? entity.getStaff().getId() : null;
            this.staffName = entity.getStaffName();
            this.staffCode = entity.getStaffCode();
            this.staffPositionId = entity.getStaffPosition() != null ? entity.getStaffPosition().getId() : null;
            this.position = entity.getPosition();
            this.staffDepartmentId = entity.getStaffDepartment() != null ? entity.getStaffDepartment().getId() : null;
            this.department = entity.getDepartment();
            this.staffDivisionId = entity.getStaffDivision() != null ? entity.getStaffDivision().getId() : null;
            this.division = entity.getDivision();
            this.staffTeamId = entity.getStaffTeam() != null ? entity.getStaffTeam().getId() : null;
            this.team = entity.getTeam();
            this.directManagerId = entity.getDirectManager() != null ? entity.getDirectManager().getId() : null;
            this.directManagerName = entity.getDirectManagerName();
            //A. NỘI DUNG ĐÁNH GIÁ:
            this.hireDate = entity.getHireDate();
            this.previousContractDuration = entity.getPreviousContractDuration();
            if (entity.getContractType() != null) {
                this.contractTypeId = entity.getContractType().getId();
                this.contractTypeName = entity.getContractTypeName();
                this.contractTypeCode = entity.getContractType().getCode();
            }
            this.items = items;
            this.advantage = entity.getAdvantage();
            this.disadvantage = entity.getDisadvantage();
            this.companyPolicyCompliance = entity.getCompanyPolicyCompliance();
            this.coworkerRelationship = entity.getCoworkerRelationship();
            this.senseOfResponsibility = entity.getSenseOfResponsibility();
            //B. KẾT LUẬN:
            this.contractRecommendation = entity.getContractRecommendation();
            this.contractRecommendationDateFrom = entity.getContractRecommendationDateFrom();
            this.contractRecommendationDateTo = entity.getContractRecommendationDateTo();
            if (entity.getPositionTitle() != null) {
                this.positionTitleId = entity.getPositionTitle().getId();
                this.positionTitleName = entity.getPositionTitle().getName();
            }
            if (entity.getRankTitle() != null) {
                this.rankTitleId = entity.getRankTitle().getId();
                this.rankTitleName = entity.getRankTitle().getName();
            }
            if (entity.getBaseSalary() != null) {
                this.baseSalary = entity.getBaseSalary();
            }
            if (entity.getAllowanceAmount() != null) {
                this.allowanceAmount = entity.getAllowanceAmount();
            }
            if (entity.getEffectiveFromDate() != null) {
                this.effectiveFromDate = entity.getEffectiveFromDate();
            }
            if (entity.getCooperationStatus() != null) {
                this.cooperationStatus = entity.getCooperationStatus();
            }
            this.collaborationEndDate = entity.getCollaborationEndDate();
            if (entity.getNewPosition() != null) {
                this.newPositionId = entity.getNewPosition().getId();
                this.newPositionName = entity.getNewPosition().getName();
            }
            this.newPositionTransferDate = entity.getNewPositionTransferDate();
        }
    }

    public EvaluationFormDto(EvaluationForm entity) {
        super(entity);
        if (entity != null) {
            this.staffId = entity.getStaff() != null ? entity.getStaff().getId() : null;
            this.staffName = entity.getStaffName();
            this.staffCode = entity.getStaffCode();

            this.staffPositionId = entity.getStaffPosition() != null ? entity.getStaffPosition().getId() : null;
            this.position = entity.getPosition();

            this.staffDepartmentId = entity.getStaffDepartment() != null ? entity.getStaffDepartment().getId() : null;
            this.department = entity.getDepartment();

            this.staffDivisionId = entity.getStaffDivision() != null ? entity.getStaffDivision().getId() : null;
            this.division = entity.getDivision();

            this.staffTeamId = entity.getStaffTeam() != null ? entity.getStaffTeam().getId() : null;
            this.team = entity.getTeam();

            this.directManagerId = entity.getDirectManager() != null ? entity.getDirectManager().getId() : null;
            this.directManagerName = entity.getDirectManagerName();

            this.hireDate = entity.getHireDate();
            this.previousContractDuration = entity.getPreviousContractDuration();

            this.contractTypeId = entity.getContractType() != null ? entity.getContractType().getId() : null;
            this.contractTypeName = entity.getContractTypeName();
        }
    }


    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public UUID getStaffPositionId() {
        return staffPositionId;
    }

    public void setStaffPositionId(UUID staffPositionId) {
        this.staffPositionId = staffPositionId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public UUID getStaffDepartmentId() {
        return staffDepartmentId;
    }

    public void setStaffDepartmentId(UUID staffDepartmentId) {
        this.staffDepartmentId = staffDepartmentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public UUID getStaffDivisionId() {
        return staffDivisionId;
    }

    public void setStaffDivisionId(UUID staffDivisionId) {
        this.staffDivisionId = staffDivisionId;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public UUID getStaffTeamId() {
        return staffTeamId;
    }

    public void setStaffTeamId(UUID staffTeamId) {
        this.staffTeamId = staffTeamId;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public UUID getDirectManagerId() {
        return directManagerId;
    }

    public void setDirectManagerId(UUID directManagerId) {
        this.directManagerId = directManagerId;
    }

    public String getDirectManagerName() {
        return directManagerName;
    }

    public void setDirectManagerName(String directManagerName) {
        this.directManagerName = directManagerName;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public Date getPreviousContractDuration() {
        return previousContractDuration;
    }

    public void setPreviousContractDuration(Date previousContractDuration) {
        this.previousContractDuration = previousContractDuration;
    }

    public UUID getContractTypeId() {
        return contractTypeId;
    }

    public void setContractTypeId(UUID contractTypeId) {
        this.contractTypeId = contractTypeId;
    }

    public String getContractTypeName() {
        return contractTypeName;
    }

    public void setContractTypeName(String contractTypeName) {
        this.contractTypeName = contractTypeName;
    }

    public List<StaffEvaluationDto> getItems() {
        return items;
    }

    public void setItems(List<StaffEvaluationDto> items) {
        this.items = items;
    }

    public String getAdvantage() {
        return advantage;
    }

    public void setAdvantage(String advantage) {
        this.advantage = advantage;
    }

    public String getDisadvantage() {
        return disadvantage;
    }

    public void setDisadvantage(String disadvantage) {
        this.disadvantage = disadvantage;
    }

    public String getCompanyPolicyCompliance() {
        return companyPolicyCompliance;
    }

    public void setCompanyPolicyCompliance(String companyPolicyCompliance) {
        this.companyPolicyCompliance = companyPolicyCompliance;
    }

    public String getCoworkerRelationship() {
        return coworkerRelationship;
    }

    public void setCoworkerRelationship(String coworkerRelationship) {
        this.coworkerRelationship = coworkerRelationship;
    }

    public String getSenseOfResponsibility() {
        return senseOfResponsibility;
    }

    public void setSenseOfResponsibility(String senseOfResponsibility) {
        this.senseOfResponsibility = senseOfResponsibility;
    }

    public Boolean getContractRecommendation() {
        return contractRecommendation;
    }

    public void setContractRecommendation(Boolean contractRecommendation) {
        this.contractRecommendation = contractRecommendation;
    }

    public Date getContractRecommendationDateFrom() {
        return contractRecommendationDateFrom;
    }

    public void setContractRecommendationDateFrom(Date contractRecommendationDateFrom) {
        this.contractRecommendationDateFrom = contractRecommendationDateFrom;
    }

    public Date getContractRecommendationDateTo() {
        return contractRecommendationDateTo;
    }

    public void setContractRecommendationDateTo(Date contractRecommendationDateTo) {
        this.contractRecommendationDateTo = contractRecommendationDateTo;
    }

    public Date getCollaborationEndDate() {
        return collaborationEndDate;
    }

    public void setCollaborationEndDate(Date collaborationEndDate) {
        this.collaborationEndDate = collaborationEndDate;
    }

    public UUID getNewPositionId() {
        return newPositionId;
    }

    public void setNewPositionId(UUID newPositionId) {
        this.newPositionId = newPositionId;
    }

    public String getNewPositionName() {
        return newPositionName;
    }

    public void setNewPositionName(String newPositionName) {
        this.newPositionName = newPositionName;
    }

    public UUID getPositionTitleId() {
        return positionTitleId;
    }

    public void setPositionTitleId(UUID positionTitleId) {
        this.positionTitleId = positionTitleId;
    }

    public String getPositionTitleName() {
        return positionTitleName;
    }

    public void setPositionTitleName(String positionTitleName) {
        this.positionTitleName = positionTitleName;
    }

    public UUID getRankTitleId() {
        return rankTitleId;
    }

    public void setRankTitleId(UUID rankTitleId) {
        this.rankTitleId = rankTitleId;
    }

    public String getRankTitleName() {
        return rankTitleName;
    }

    public void setRankTitleName(String rankTitleName) {
        this.rankTitleName = rankTitleName;
    }

    public Double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(Double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public Double getAllowanceAmount() {
        return allowanceAmount;
    }

    public void setAllowanceAmount(Double allowanceAmount) {
        this.allowanceAmount = allowanceAmount;
    }

    public Date getEffectiveFromDate() {
        return effectiveFromDate;
    }

    public void setEffectiveFromDate(Date effectiveFromDate) {
        this.effectiveFromDate = effectiveFromDate;
    }

    public Boolean getCooperationStatus() {
        return cooperationStatus;
    }

    public void setCooperationStatus(Boolean cooperationStatus) {
        this.cooperationStatus = cooperationStatus;
    }

    public Date getNewPositionTransferDate() {
        return newPositionTransferDate;
    }

    public void setNewPositionTransferDate(Date newPositionTransferDate) {
        this.newPositionTransferDate = newPositionTransferDate;
    }

    public PositionTitleDto getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitleDto positionTitle) {
        this.positionTitle = positionTitle;
    }

    public String getContractTypeCode() {
        return contractTypeCode;
    }

    public void setContractTypeCode(String contractTypeCode) {
        this.contractTypeCode = contractTypeCode;
    }
}
