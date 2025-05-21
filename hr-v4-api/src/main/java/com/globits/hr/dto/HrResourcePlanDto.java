package com.globits.hr.dto;

import com.globits.budget.domain.BaseNameCodeObject;
import com.globits.budget.dto.BaseNameCodeObjectDto;
import com.globits.hr.domain.HrResourcePlan;
import com.globits.hr.domain.HrResourcePlanItem;
import org.springframework.util.StringUtils;

import java.util.*;

public class HrResourcePlanDto extends BaseNameCodeObjectDto {

    private Date planDate; // Ngày lập định biên

    private HRDepartmentDto department; // Phòng ban thực hiện định biên nhân sự

    private List<HrResourcePlanItemDto> resourcePlanItems; // Các vị trí định biên trong phòng ban

//    private HrResourcePlanDto parentPlan; // Thuộc kế hoạch định biên Tổng hợp nào

    private Set<HrResourcePlanDto> childrenPlans; // Các định biên con được tổng hợp để tạo thành định biên này (đối với định biên toàn Group)

    private StaffDto viceGeneralDirector; // phó tổng giám đốc duyệt

    private Integer viceGeneralDirectorStatus; // trạng thái phó tổng giám đốc duyệt

    private StaffDto generalDirector; // tổng giám đốc duyệt

    private Integer generalDirectorStatus; // trạng thái tổng giám đốc duyệt

    private StaffDto requester; // người yêu cầu định biên

    private Integer status; // trạng thái của yêu cầu

    private Boolean isTemporary; // Là tạm thời = tuyển lọc
    private Integer eliminatePlanNumber;

    public HrResourcePlanDto(HrResourcePlan entity) {
        super(entity);

        this.planDate = entity.getPlanDate();

        if (entity.getDepartment() != null) {
            this.department = new HRDepartmentDto(entity.getDepartment(), false);
        }

        if (entity.getResourcePlanItems() != null) {
            this.resourcePlanItems = new ArrayList<>();

            for (HrResourcePlanItem item : entity.getResourcePlanItems()) {
                if (item == null || item.getPositionTitle() == null || !StringUtils.hasText(item.getPositionTitle().getCode()))
                    continue;

                HrResourcePlanItemDto planItem = new HrResourcePlanItemDto(item);
                this.resourcePlanItems.add(planItem);
            }

// Sắp xếp theo positionTitle.name, sau đó positionTitle.code
            this.resourcePlanItems.sort(Comparator
                    .comparing((HrResourcePlanItemDto dto) -> dto.getPositionTitle().getCode(), Comparator.nullsLast(String::compareTo))
                    .thenComparing(dto -> dto.getPositionTitle().getName(), Comparator.nullsLast(String::compareTo)));
        }

        if (entity.getChildrenPlans() != null) {
            this.childrenPlans = new HashSet<>();
            for (HrResourcePlan child : entity.getChildrenPlans()) {
                this.childrenPlans.add(new HrResourcePlanDto(child));
            }
        }

        // phó tổng giám đốc duyệt
        if (entity.getViceGeneralDirector() != null) {
            this.viceGeneralDirector = new StaffDto();
            this.viceGeneralDirector.setId(entity.getViceGeneralDirector().getId());
            this.viceGeneralDirector.setStaffCode(entity.getViceGeneralDirector().getStaffCode());
            this.viceGeneralDirector.setDisplayName(entity.getViceGeneralDirector().getDisplayName());
        }

        this.viceGeneralDirectorStatus = entity.getViceGeneralDirectorStatus();

        if (entity.getGeneralDirector() != null) {
            this.generalDirector = new StaffDto();
            this.generalDirector.setId(entity.getGeneralDirector().getId());
            this.generalDirector.setStaffCode(entity.getGeneralDirector().getStaffCode());
            this.generalDirector.setDisplayName(entity.getGeneralDirector().getDisplayName());
        }

        this.generalDirectorStatus = entity.getGeneralDirectorStatus();

        if (entity.getRequester() != null) {
            this.requester = new StaffDto();
            this.requester.setId(entity.getRequester().getId());
            this.requester.setStaffCode(entity.getRequester().getStaffCode());
            this.requester.setDisplayName(entity.getRequester().getDisplayName());
        }
    }

    public HrResourcePlanDto() {
    }

    public StaffDto getViceGeneralDirector() {
        return viceGeneralDirector;
    }

    public void setViceGeneralDirector(StaffDto viceGeneralDirector) {
        this.viceGeneralDirector = viceGeneralDirector;
    }

    public Integer getViceGeneralDirectorStatus() {
        return viceGeneralDirectorStatus;
    }

    public void setViceGeneralDirectorStatus(Integer viceGeneralDirectorStatus) {
        this.viceGeneralDirectorStatus = viceGeneralDirectorStatus;
    }

    public StaffDto getGeneralDirector() {
        return generalDirector;
    }

    public void setGeneralDirector(StaffDto generalDirector) {
        this.generalDirector = generalDirector;
    }

    public Integer getGeneralDirectorStatus() {
        return generalDirectorStatus;
    }

    public void setGeneralDirectorStatus(Integer generalDirectorStatus) {
        this.generalDirectorStatus = generalDirectorStatus;
    }

    public StaffDto getRequester() {
        return requester;
    }

    public void setRequester(StaffDto requester) {
        this.requester = requester;
    }

    public Date getPlanDate() {
        return planDate;
    }

    public void setPlanDate(Date planDate) {
        this.planDate = planDate;
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    public List<HrResourcePlanItemDto> getResourcePlanItems() {
        return resourcePlanItems;
    }

    public void setResourcePlanItems(List<HrResourcePlanItemDto> resourcePlanItems) {
        this.resourcePlanItems = resourcePlanItems;
    }

    public Set<HrResourcePlanDto> getChildrenPlans() {
        return childrenPlans;
    }

    public void setChildrenPlans(Set<HrResourcePlanDto> childrenPlans) {
        this.childrenPlans = childrenPlans;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsTemporary() {
        return isTemporary;
    }

    public void setIsTemporary(Boolean temporary) {
        isTemporary = temporary;
    }

    public Integer getEliminatePlanNumber() {
        return eliminatePlanNumber;
    }

    public void setEliminatePlanNumber(Integer eliminatePlanNumber) {
        this.eliminatePlanNumber = eliminatePlanNumber;
    }
}
