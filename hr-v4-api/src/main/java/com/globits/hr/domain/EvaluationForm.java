package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.Department;
import com.globits.hr.utils.Const;
import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "tbl_evaluation_form")
public class EvaluationForm extends BaseObject {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Staff staff;
    @Column(name = "staff_name")
    private String staffName; //Họ và tên:
    @Column(name = "staff_code")
    private String staffCode; //Mã nhân viên:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_position_id")
    private PositionTitle staffPosition;//Chức danh:
    @Column(name = "position")
    private String position;//Chức danh
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_department_id")
    private Department staffDepartment;//Ban
    @Column(name = "department")
    private String department;//Ban:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_division_id")
    private Department staffDivision;//Phòng/Cơ sở
    @Column(name = "division")
    private String division;//Phòng/Cơ sở
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_team_id")
    private Department staffTeam;//Bộ phận/Nhóm
    @Column(name = "team")
    private String team;//Bộ phận/Nhóm:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direct_manager_id")
    private Staff directManager;//Quản lý trực tiếp
    @Column(name = "direct_manager")
    private String directManagerName;//Quản lý trực tiếp:
    @Column(name = "hire_date")
    private Date hireDate;//Ngày nhận việc:
    @Column(name = "previous_contract_duration")
    private Date previousContractDuration;//Thời hạn HĐLĐ trước:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_type_id")
    private ContractType contractType;//Loại HĐLĐ:
    @Column(name = "contract_type_name")
    private String contractTypeName;//Loại HĐLĐ:
    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private Const.EVALUATION status;//  Ghi chú: Không kí HĐLĐ nếu cấp Quản lý trực tiếp có tỷ lệ đánh giá “Không đạt” chiếm trên 50%.
    @Column(name = "advantage")
    private String advantage;//Ưu điểm
    @Column(name = "disadvantage")
    private String disadvantage;//Nhược điểm
    @Column(name = "company_policy_compliance")
    private String companyPolicyCompliance;//Chấp hành nội quy, quy định của công ty
    @Column(name = "coworker_relationship")
    private String coworkerRelationship;//Mối quan hệ với đồng nghiệp
    @Column(name = "sense_of_responsibility")
    private String senseOfResponsibility;//Tinh thần trách nhiệm
    @Column(name = "contract_recommendation")
    private Boolean contractRecommendation; //Đạt yêu cầu, đề xuất ký HĐLĐ
    @Column(name = "contract_recommendation_date_from")
    private Date contractRecommendationDateFrom; //Đạt yêu cầu, đề xuất ký HĐLĐ từ ngày
    @Column(name = "contract_recommendation_date_tp")
    private Date contractRecommendationDateTo; //Đạt yêu cầu, đề xuất ký HĐLĐ đến ngày
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_title_id")
    private PositionTitle positionTitle;//Chức danh
    @Column(name = "position_tile_name")
    private String positionTitleName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rank_title_id")
    private RankTitle rankTitle;//Cấp bậc
    @Column(name = "rank_title_name")
    private String rankTitleName;
    @Column(name = "base_salary")
    private Double baseSalary;//Lương cứng
    @Column(name = "allowance_amount")
    private Double allowanceAmount;//Phụ cấp
    @Column(name = "effective_from_date")
    private Date effectiveFromDate; //Thời gian áp dụng: Từ ngày
    @Column(name = "cooperation_status")
    private Boolean cooperationStatus; //Ngừng hợp tác kể từ ngày = true / Bố trí sang vị trí khác = false
    @Column(name = "collaboration_end_date")
    private Date collaborationEndDate; //Ngừng hợp tác kể từ ngày
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_position_")
    private Position newPosition;//Bố trí sang vị trí khác
    @Column(name = "new_position")
    private String newPositionName;
    @Column(name = "new_position_transfer_date")
    private Date newPositionTransferDate;
    @Column(name = "evaluation_transfer_status")
    @Enumerated(value = EnumType.STRING)
    private Const.EVALUATION_TRANSFER_STATUS_ENUM evaluationTransferStatus;

    public EvaluationForm() {
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
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

    public PositionTitle getStaffPosition() {
        return staffPosition;
    }

    public void setStaffPosition(PositionTitle staffPosition) {
        this.staffPosition = staffPosition;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Department getStaffDepartment() {
        return staffDepartment;
    }

    public void setStaffDepartment(Department staffDepartment) {
        this.staffDepartment = staffDepartment;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Department getStaffDivision() {
        return staffDivision;
    }

    public void setStaffDivision(Department staffDivision) {
        this.staffDivision = staffDivision;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public Department getStaffTeam() {
        return staffTeam;
    }

    public void setStaffTeam(Department staffTeam) {
        this.staffTeam = staffTeam;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Staff getDirectManager() {
        return directManager;
    }

    public void setDirectManager(Staff directManager) {
        this.directManager = directManager;
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

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public String getContractTypeName() {
        return contractTypeName;
    }

    public void setContractTypeName(String contractTypeName) {
        this.contractTypeName = contractTypeName;
    }

    public Const.EVALUATION getStatus() {
        return status;
    }

    public void setStatus(Const.EVALUATION status) {
        this.status = status;
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

    public Position getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(Position newPosition) {
        this.newPosition = newPosition;
    }

    public String getNewPositionName() {
        return newPositionName;
    }

    public void setNewPositionName(String newPositionName) {
        this.newPositionName = newPositionName;
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

    public PositionTitle getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitle positionTitle) {
        this.positionTitle = positionTitle;
    }

    public String getPositionTitleName() {
        return positionTitleName;
    }

    public void setPositionTitleName(String positionTitleName) {
        this.positionTitleName = positionTitleName;
    }

    public RankTitle getRankTitle() {
        return rankTitle;
    }

    public void setRankTitle(RankTitle rankTitle) {
        this.rankTitle = rankTitle;
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

    public Const.EVALUATION_TRANSFER_STATUS_ENUM getEvaluationTransferStatus() {
        return evaluationTransferStatus;
    }

    public void setEvaluationTransferStatus(Const.EVALUATION_TRANSFER_STATUS_ENUM evaluationTransferStatus) {
        this.evaluationTransferStatus = evaluationTransferStatus;
    }
}
