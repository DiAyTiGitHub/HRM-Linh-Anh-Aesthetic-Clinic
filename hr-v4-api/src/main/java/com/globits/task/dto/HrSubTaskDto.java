package com.globits.task.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.comparator.HrSubTaskItemComparator;
import com.globits.task.domain.HrSubTask;
import com.globits.task.domain.HrSubTaskItem;

import java.util.*;

public class HrSubTaskDto extends BaseObjectDto {
    private UUID taskId;

    private String name;

    private String code;

    private String description;

    private List<HrSubTaskItemDto> items;

    public HrSubTaskDto() {

    }

    public HrSubTaskDto(HrSubTask entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.code = entity.getCode();
            this.description = entity.getDescription();
            if (entity.getTask() != null) {
                this.taskId = entity.getTask().getId();
            }
            if (entity.getSubTaskItems() != null && entity.getSubTaskItems().size() > 0) {
                this.items = new ArrayList<>();
                List<HrSubTaskItemDto> subTaskItemList = new ArrayList<>();
                for (HrSubTaskItem item : entity.getSubTaskItems()) {
                    subTaskItemList.add(new HrSubTaskItemDto(item));
                }
                if (subTaskItemList != null && subTaskItemList.size() > 0) {
//                    subTaskItemList.sort(Comparator.comparing(HrSubTaskItemDto::getName));

                    //new comparator, sort by value (is done yet) first, then sort by name
                    Collections.sort(subTaskItemList, new HrSubTaskItemComparator());
                    this.items.addAll(subTaskItemList);
                }
            }

        }
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HrSubTaskItemDto> getItems() {
        return items;
    }

    public void setItems(List<HrSubTaskItemDto> items) {
        this.items = items;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
