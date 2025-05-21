package com.globits.salary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.core.utils.CoreDateTimeUtil;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.domain.SalaryTemplateItem;
import com.globits.salary.domain.SalaryTemplateItemGroup;

import jakarta.persistence.Column;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class SalaryTemplateDto extends BaseObjectDto {
	private String code;
	private String name;
	private String description;
	private Date modifiedDate;

	private Boolean isActive; // Đang còn được sử dụng hay không. VD: = false => Không thể chọn sử dụng mẫu
	// bảng lương này cho bảng lương mới nữa
	private Boolean isCreatePayslip; // Có tạo phiếu lương cho bảng lương sử dụng mẫu bảng lương này hay không
	private Integer numberOfItems; // Số thành phần lương được sử dụng trong mẫu

	private List<SalaryTemplateItemGroupDto> templateItemGroups; // các nhóm cột trong mẫu bảng lương
	private List<SalaryTemplateItemDto> templateItems; // thành phần lương chính là các cột trong mẫu bảng lương

	public SalaryTemplateDto() {

	}

	public SalaryTemplateDto(SalaryTemplate entity) {
		super();

		this.id = entity.getId();
		this.code = entity.getCode();
		this.name = entity.getName();
		this.isActive = entity.getIsActive();
		this.description = entity.getDescription();
		this.isCreatePayslip = entity.getIsCreatePayslip();

		if (entity.getModifyDate() != null) {
			this.modifiedDate = CoreDateTimeUtil.convertToDateViaInstant(entity.getModifyDate());
		}

		this.numberOfItems = 0;
		if (entity.getTemplateItems() != null && !entity.getTemplateItems().isEmpty()) {
			this.numberOfItems = entity.getTemplateItems().size();
		}

	}

	public SalaryTemplateDto(SalaryTemplate entity, Boolean isDetail) {
		this(entity);

		if (isDetail == null || isDetail.equals(false))
			return;

		if (entity.getTemplateItemGroups() != null && !entity.getTemplateItemGroups().isEmpty()) {
			List<SalaryTemplateItemGroupDto> groups = new ArrayList<>();
			for (SalaryTemplateItemGroup group : entity.getTemplateItemGroups()) {
				SalaryTemplateItemGroupDto groupDto = new SalaryTemplateItemGroupDto(group);
				groups.add(groupDto);
			}

			Collections.sort(groups, new Comparator<SalaryTemplateItemGroupDto>() {
				@Override
				public int compare(SalaryTemplateItemGroupDto o1, SalaryTemplateItemGroupDto o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});

			this.templateItemGroups = groups;
		} else {
			this.templateItemGroups = new ArrayList<>();
		}

		if (entity.getTemplateItems() != null && !entity.getTemplateItems().isEmpty()) {
			List<SalaryTemplateItemDto> items = new ArrayList<>();
			for (SalaryTemplateItem item : entity.getTemplateItems()) {
				SalaryTemplateItemDto itemDto = new SalaryTemplateItemDto(item, true);
				items.add(itemDto);
			}

			Collections.sort(items, new Comparator<SalaryTemplateItemDto>() {
				@Override
				public int compare(SalaryTemplateItemDto o1, SalaryTemplateItemDto o2) {
					// First, compare by displayOrder
					if (o1.getDisplayOrder() == null && o2.getDisplayOrder() == null)
						return 0;
					if (o1.getDisplayOrder() == null)
						return 1;
					if (o2.getDisplayOrder() == null)
						return -1;

					int orderComparison = o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
					if (orderComparison != 0) {
						return orderComparison;
					}

					// If displayOrder is the same, compare by displayName (handling nulls)
					if (o1.getDisplayName() == null && o2.getDisplayName() == null)
						return 0;
					if (o1.getDisplayName() == null)
						return 1;
					if (o2.getDisplayName() == null)
						return -1;
					return o1.getDisplayName().compareTo(o2.getDisplayName());
				}
			});

			this.templateItems = items;
		} else {
			this.templateItems = new ArrayList<>();
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getActive() {
		return isActive;
	}

	public void setActive(Boolean active) {
		isActive = active;
	}

	public Integer getNumberOfItems() {
		return numberOfItems;
	}

	public void setNumberOfItems(Integer numberOfItems) {
		this.numberOfItems = numberOfItems;
	}

	public List<SalaryTemplateItemGroupDto> getTemplateItemGroups() {
		return templateItemGroups;
	}

	public void setTemplateItemGroups(List<SalaryTemplateItemGroupDto> templateItemGroups) {
		this.templateItemGroups = templateItemGroups;
	}

	public List<SalaryTemplateItemDto> getTemplateItems() {
		return templateItems;
	}

	public void setTemplateItems(List<SalaryTemplateItemDto> templateItems) {
		this.templateItems = templateItems;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Boolean getIsCreatePayslip() {
		return isCreatePayslip;
	}

	public void setIsCreatePayslip(Boolean isCreatePayslip) {
		this.isCreatePayslip = isCreatePayslip;
	}

}
