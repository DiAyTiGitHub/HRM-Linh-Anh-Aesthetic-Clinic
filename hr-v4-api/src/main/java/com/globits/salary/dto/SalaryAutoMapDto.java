package com.globits.salary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.domain.SalaryAutoMap;
import com.globits.salary.domain.SalaryItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class SalaryAutoMapDto extends BaseObjectDto {
    private String salaryAutoMapField;
    private String description;
    private List<SalaryItemDto> salaryItems; // thành phần lương chính là các cột trong mẫu bảng lương

    public SalaryAutoMapDto() {

    }

    public SalaryAutoMapDto(SalaryAutoMap entity) {
        super(entity);

        this.salaryAutoMapField = entity.getSalaryAutoMapField();
        this.description = entity.getDescription();

        this.salaryItems = new ArrayList<>();

        if (entity.getSalaryItems() != null) {
            for (SalaryItem salaryItem : entity.getSalaryItems()) {
                SalaryItemDto responseItem = new SalaryItemDto();

                responseItem.setId(salaryItem.getId());
                responseItem.setName(salaryItem.getName());
                responseItem.setCode(salaryItem.getCode());

                this.salaryItems.add(responseItem);
            }

            // Sort after all items are added
            this.salaryItems.sort(Comparator
                    .comparing(SalaryItemDto::getName, Comparator.nullsLast(String::compareToIgnoreCase))
                    .thenComparing(SalaryItemDto::getCode, Comparator.nullsLast(String::compareToIgnoreCase)));

        }
    }

    public SalaryAutoMapDto(SalaryAutoMap entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || isDetail.equals(false)) return;


    }

    public String getSalaryAutoMapField() {
        return salaryAutoMapField;
    }

    public void setSalaryAutoMapField(String salaryAutoMapField) {
        this.salaryAutoMapField = salaryAutoMapField;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SalaryItemDto> getSalaryItems() {
        return salaryItems;
    }

    public void setSalaryItems(List<SalaryItemDto> salaryItems) {
        this.salaryItems = salaryItems;
    }
}
