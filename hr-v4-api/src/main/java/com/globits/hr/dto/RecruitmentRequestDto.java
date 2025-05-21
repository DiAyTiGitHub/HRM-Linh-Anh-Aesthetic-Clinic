package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.*;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RecruitmentRequestDto extends BaseObjectDto {
    private static final long serialVersionUID = 991992518344617174L;

    private String code; // ma yeu cau tuyen dung
    private String name; // ten yeu cau
    private String description; // mo ta cong viec
    private String request; // yeu cau
    private Integer status; // trang thái: HrConstants.RecruitmentRequestStatus
    private WorkplaceDto workPlace; // yeu cau
    private HrOrganizationDto organization; // cong ty/to chuc
    private HRDepartmentDto hrDepartment; // phong ban
    private HRDepartmentDto team; // phong ban
    private StaffDto personInCharge; // người chịu trách nhiệm
    private Set<RecruitmentRequestItemDto> recruitmentRequestItems; // Các vị trí cần tuyển trong yêu cầu
    private RecruitmentRequestItemDto recruitmentRequestItem; // Vị trí cần tuyển trong yêu cầu

    private UUID approvedPosition;
    private UUID nextApprovePosition;
    private Boolean isApprovePermission = false;
    private Boolean isEditPermission = false;
    private Boolean isSentPermission = false;
    private String errorMessage;

    private StaffDto proposer; // Người đề xuất
    private Date proposalDate; // Ngày đề xuất
    private Date proposalReceiptDate; // Ngày nhận đề xuất (ngày người phụ trách nhận đề xuất để thực hiện yêu cầu tuyển dụng)
    private Set<PositionRecruitmentRequestDto> positionRequests; // Các vị trí bị thay thế trong yêu cầu
    private PositionDto aPosition;
    private PositionDto nAPosition;
    private Date recruitingStartDate;
    private Date recruitingEndDate;

    public RecruitmentRequestDto() {
    }

    public RecruitmentRequestDto(RecruitmentRequest entity) {
        if (entity == null) return;
        this.id = entity.getId();
        this.name = entity.getName();
        this.code = entity.getCode();
        this.description = entity.getDescription();
        this.request = entity.getRequest();
        this.team = new HRDepartmentDto(entity.getTeam());
        this.recruitingStartDate = entity.getRecruitingStartDate();
        this.recruitingEndDate = entity.getRecruitingEndDate();
        if (entity.getHrDepartment() != null) {
            this.hrDepartment = new HRDepartmentDto();
            this.hrDepartment.setId(entity.getHrDepartment().getId());
            this.hrDepartment.setName(entity.getHrDepartment().getName());
            this.hrDepartment.setCode(entity.getHrDepartment().getCode());
        }
        if (entity.getHrOrganization() != null) {
            this.organization = new HrOrganizationDto();
            this.organization.setId(entity.getHrOrganization().getId());
            this.organization.setName(entity.getHrOrganization().getName());
            this.organization.setCode(entity.getHrOrganization().getCode());
        }
        if (entity.getPersonInCharge() != null) {
            this.personInCharge = new StaffDto(entity.getPersonInCharge(), false, false);
        }
        if (entity.getRecruitmentRequestItems() != null) {
            this.recruitmentRequestItems = new HashSet<RecruitmentRequestItemDto>();
            for (RecruitmentRequestItem item : entity.getRecruitmentRequestItems()) {
                RecruitmentRequestItemDto itemDto = new RecruitmentRequestItemDto(item);
                this.recruitmentRequestItems.add(itemDto);
            }
        }
        if (entity.getWorkPlace() != null) {
            this.workPlace = new WorkplaceDto();
            this.workPlace.setId(entity.getWorkPlace().getId());
            this.workPlace.setName(entity.getWorkPlace().getName());
            this.workPlace.setCode(entity.getWorkPlace().getCode());
        }
        this.status = entity.getStatus();
        if (entity.getNextApprovePosition() != null) {
            this.nextApprovePosition = entity.getNextApprovePosition().getId();
        }
        if (entity.getApprovedPosition() != null) {
            this.approvedPosition = entity.getApprovedPosition().getId();
        }

        this.proposalDate = entity.getProposalDate();
        this.proposalReceiptDate = entity.getProposalReceiptDate();

        if (entity.getProposer() != null) {
            this.proposer = new StaffDto();
            this.proposer.setStaffCode(entity.getProposer().getStaffCode());
            this.proposer.setId(entity.getProposer().getId());
            this.proposer.setDisplayName(entity.getProposer().getDisplayName());
        }

        if (entity.getPositionRequests() != null && !entity.getPositionRequests().isEmpty()) {
            this.positionRequests = new HashSet<>();
            for (PositionRecruitmentRequest prr : entity.getPositionRequests()) {
                this.positionRequests.add(new PositionRecruitmentRequestDto(prr));
            }
        }

        if (entity.getApprovedPosition() != null) {
            this.aPosition = new PositionDto();
            this.aPosition.setId(entity.getApprovedPosition().getId());
            this.aPosition.setName(entity.getApprovedPosition().getName());
            this.aPosition.setCode(entity.getApprovedPosition().getCode());
        }
        if (entity.getNextApprovePosition() != null) {
            this.nAPosition = new PositionDto();
            this.nAPosition.setId(entity.getNextApprovePosition().getId());
            this.nAPosition.setName(entity.getNextApprovePosition().getName());
            this.nAPosition.setCode(entity.getNextApprovePosition().getCode());
        }
    }

    public Set<PositionRecruitmentRequestDto> getPositionRequests() {
        return positionRequests;
    }

    public void setPositionRequests(Set<PositionRecruitmentRequestDto> positionRequests) {
        this.positionRequests = positionRequests;
    }

    public Set<RecruitmentRequestItemDto> getRecruitmentRequestItems() {
        return recruitmentRequestItems;
    }

    public void setRecruitmentRequestItems(Set<RecruitmentRequestItemDto> recruitmentRequestItems) {
        this.recruitmentRequestItems = recruitmentRequestItems;
    }

    public StaffDto getProposer() {
        return proposer;
    }

    public void setProposer(StaffDto proposer) {
        this.proposer = proposer;
    }

    public Date getProposalDate() {
        return proposalDate;
    }

    public void setProposalDate(Date proposalDate) {
        this.proposalDate = proposalDate;
    }

    public Date getProposalReceiptDate() {
        return proposalReceiptDate;
    }

    public void setProposalReceiptDate(Date proposalReceiptDate) {
        this.proposalReceiptDate = proposalReceiptDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HRDepartmentDto getHrDepartment() {
        return hrDepartment;
    }

    public void setHrDepartment(HRDepartmentDto hrDepartment) {
        this.hrDepartment = hrDepartment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public HrOrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganizationDto organization) {
        this.organization = organization;
    }

    public StaffDto getPersonInCharge() {
        return personInCharge;
    }

    public void setPersonInCharge(StaffDto personInCharge) {
        this.personInCharge = personInCharge;
    }

    public HRDepartmentDto getTeam() {
        return team;
    }

    public void setTeam(HRDepartmentDto team) {
        this.team = team;
    }

    public Boolean getApprovePermission() {
        return isApprovePermission;
    }

    public void setApprovePermission(Boolean approvePermission) {
        isApprovePermission = approvePermission;
    }

    public Boolean getEditPermission() {
        return isEditPermission;
    }

    public void setEditPermission(Boolean editPermission) {
        isEditPermission = editPermission;
    }

    public UUID getNextApprovePosition() {
        return nextApprovePosition;
    }

    public void setNextApprovePosition(UUID nextApprovePosition) {
        this.nextApprovePosition = nextApprovePosition;
    }

    public Boolean getSentPermission() {
        return this.isSentPermission;
    }

    public void setSentPermission(Boolean sentPermission) {
        this.isSentPermission = sentPermission;
    }

    public UUID getApprovedPosition() {
        return approvedPosition;
    }

    public void setApprovedPosition(UUID approvedPosition) {
        this.approvedPosition = approvedPosition;
    }

    public RecruitmentRequestItemDto getRecruitmentRequestItem() {
        return recruitmentRequestItem;
    }

    public void setRecruitmentRequestItem(RecruitmentRequestItemDto recruitmentRequestItem) {
        this.recruitmentRequestItem = recruitmentRequestItem;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public PositionDto getaPosition() {
        return aPosition;
    }

    public void setaPosition(PositionDto aPosition) {
        this.aPosition = aPosition;
    }

    public PositionDto getnAPosition() {
        return nAPosition;
    }

    public void setnAPosition(PositionDto nAPosition) {
        this.nAPosition = nAPosition;
    }

    public Date getRecruitingStartDate() {
        return recruitingStartDate;
    }

    public void setRecruitingStartDate(Date recruitingStartDate) {
        this.recruitingStartDate = recruitingStartDate;
    }

    public Date getRecruitingEndDate() {
        return recruitingEndDate;
    }

    public void setRecruitingEndDate(Date recruitingEndDate) {
        this.recruitingEndDate = recruitingEndDate;
    }

    public WorkplaceDto getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(WorkplaceDto workPlace) {
        this.workPlace = workPlace;
    }
}
