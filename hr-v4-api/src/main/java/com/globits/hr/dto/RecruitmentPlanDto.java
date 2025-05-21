package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.RecruitmentPlan;
import com.globits.hr.domain.RecruitmentRequestItem;
import com.globits.hr.domain.RecruitmentRound;
import com.globits.template.dto.ContentTemplateDto;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RecruitmentPlanDto extends BaseObjectDto {
    private static final long serialVersionUID = 991992518344617174L;

    private String code; // ma ke hoach
    private String name; // ten ke hoach
    private RecruitmentRequestDto recruitmentRequest; // yeu cau
    //    private Integer quantity; // so luong thuc te
    private Date estimatedTimeFrom; // thoi gian du kien tu
    private Date estimatedTimeTo; // thoi gian du kien den
    private String description; // mo ta ke hoach
    private Integer status; // trang thai

    private HrOrganizationDto organization; // cong ty/to chuc
    private HRDepartmentDto department; // phong ban
    private PositionDto position;// vi tri can tuyen
//    private PositionTitleDto positionTitle; // Vị trí cần tuyển

    private List<RecruitmentPlanItemDto> recruitmentPlanItems;
    private List<RecruitmentRoundDto> recruitmentRounds;
    private String errorMessage;
    private ContentTemplateDto passTemplate;
    private ContentTemplateDto failTemplate;

    public RecruitmentPlanDto() {
    }

    public RecruitmentPlanDto(RecruitmentPlan entity) {
        if (entity == null) return;
        this.id = entity.getId();
        this.name = entity.getName();
        this.code = entity.getCode();
        this.description = entity.getDescription();
        if (entity.getRecruitmentRequest() != null) {
            this.recruitmentRequest = new RecruitmentRequestDto();
            this.recruitmentRequest.setId(entity.getRecruitmentRequest().getId());
            this.recruitmentRequest.setCode(entity.getRecruitmentRequest().getCode());
            this.recruitmentRequest.setName(entity.getRecruitmentRequest().getName());
            if (entity.getRecruitmentRequest().getRecruitmentRequestItems() != null && !entity.getRecruitmentRequest().getRecruitmentRequestItems().isEmpty()) {
                RecruitmentRequestItem recruitmentRequestItem = entity.getRecruitmentRequest().getRecruitmentRequestItems().iterator().next();
                this.recruitmentRequest.setRecruitmentRequestItem(new RecruitmentRequestItemDto(recruitmentRequestItem));
            }

            if (entity.getRecruitmentRequest().getHrDepartment() != null) {
                HRDepartmentDto recruitingDepartment = new HRDepartmentDto();
                recruitingDepartment.setId(entity.getRecruitmentRequest().getHrDepartment().getId());
                recruitingDepartment.setName(entity.getRecruitmentRequest().getHrDepartment().getName());
                recruitingDepartment.setCode(entity.getRecruitmentRequest().getHrDepartment().getCode());
                this.recruitmentRequest.setHrDepartment(recruitingDepartment);
            }
            if (entity.getRecruitmentRequest().getHrOrganization() != null) {
                HrOrganizationDto organization = new HrOrganizationDto();
                organization.setId(entity.getRecruitmentRequest().getHrOrganization().getId());
                organization.setName(entity.getRecruitmentRequest().getHrOrganization().getName());
                organization.setCode(entity.getRecruitmentRequest().getHrOrganization().getCode());
                this.recruitmentRequest.setOrganization(organization);
            }
        }
        this.estimatedTimeFrom = entity.getEstimatedTimeFrom();
        this.estimatedTimeTo = entity.getEstimatedTimeTo();
        this.status = entity.getStatus();

    }

    public RecruitmentPlanDto(RecruitmentPlan entity, List<RecruitmentRoundDto> recruitmentRounds) {
        if (entity == null) return;
        this.id = entity.getId();
        this.name = entity.getName();
        this.code = entity.getCode();
        this.description = entity.getDescription();
        if (entity.getRecruitmentRequest() != null) {
            this.recruitmentRequest = new RecruitmentRequestDto();
            this.recruitmentRequest.setId(entity.getRecruitmentRequest().getId());
            this.recruitmentRequest.setCode(entity.getRecruitmentRequest().getCode());
            this.recruitmentRequest.setName(entity.getRecruitmentRequest().getName());
            if (entity.getRecruitmentRequest().getPersonInCharge() != null) {
                this.recruitmentRequest.setPersonInCharge(new StaffDto(entity.getRecruitmentRequest().getPersonInCharge()));
            }
            if (entity.getRecruitmentRequest().getHrDepartment() != null) {
                HRDepartmentDto recruitingDepartment = new HRDepartmentDto();
                recruitingDepartment.setId(entity.getRecruitmentRequest().getHrDepartment().getId());
                recruitingDepartment.setName(entity.getRecruitmentRequest().getHrDepartment().getName());
                recruitingDepartment.setCode(entity.getRecruitmentRequest().getHrDepartment().getCode());
                this.recruitmentRequest.setHrDepartment(recruitingDepartment);
            }
        }
        this.estimatedTimeFrom = entity.getEstimatedTimeFrom();
        this.estimatedTimeTo = entity.getEstimatedTimeTo();
        this.status = entity.getStatus();
        if (!CollectionUtils.isEmpty(recruitmentRounds)) {
            this.recruitmentRounds = recruitmentRounds.stream().sorted(Comparator.comparing(RecruitmentRoundDto::getRoundOrder,
                    Comparator.nullsFirst(Integer::compareTo)
            )).collect(Collectors.toList());
        }
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

    public RecruitmentRequestDto getRecruitmentRequest() {
        return recruitmentRequest;
    }

    public void setRecruitmentRequest(RecruitmentRequestDto recruitmentRequest) {
        this.recruitmentRequest = recruitmentRequest;
    }

    public Date getEstimatedTimeFrom() {
        return estimatedTimeFrom;
    }

    public void setEstimatedTimeFrom(Date estimatedTimeFrom) {
        this.estimatedTimeFrom = estimatedTimeFrom;
    }

    public Date getEstimatedTimeTo() {
        return estimatedTimeTo;
    }

    public void setEstimatedTimeTo(Date estimatedTimeTo) {
        this.estimatedTimeTo = estimatedTimeTo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public PositionDto getPosition() {
        return position;
    }

    public void setPosition(PositionDto position) {
        this.position = position;
    }

    public List<RecruitmentPlanItemDto> getRecruitmentPlanItems() {
        return recruitmentPlanItems;
    }

    public void setRecruitmentPlanItems(List<RecruitmentPlanItemDto> recruitmentPlanItems) {
        this.recruitmentPlanItems = recruitmentPlanItems;
    }

    public List<RecruitmentRoundDto> getRecruitmentRounds() {
        return recruitmentRounds;
    }

    public void setRecruitmentRounds(List<RecruitmentRoundDto> recruitmentRounds) {
        this.recruitmentRounds = recruitmentRounds;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ContentTemplateDto getPassTemplate() {
        return passTemplate;
    }

    public void setPassTemplate(ContentTemplateDto passTemplate) {
        this.passTemplate = passTemplate;
    }

    public ContentTemplateDto getFailTemplate() {
        return failTemplate;
    }

    public void setFailTemplate(ContentTemplateDto failTemplate) {
        this.failTemplate = failTemplate;
    }
}
