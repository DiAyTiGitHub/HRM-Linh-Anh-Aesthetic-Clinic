package com.globits.task.service.impl;

import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.service.StaffService;
import com.globits.task.domain.HrSubTask;
import com.globits.task.domain.HrSubTaskItem;
import com.globits.task.domain.HrSubTaskItemStaff;
import com.globits.task.domain.HrTask;
import com.globits.task.dto.HrSubTaskDto;
import com.globits.task.dto.HrSubTaskItemDto;
import com.globits.task.dto.SearchSubTaskDto;
import com.globits.task.repository.HrSubTaskRepository;
import com.globits.task.service.HrSubTaskItemService;
import com.globits.task.service.HrSubTaskService;
import com.globits.task.service.HrTaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HrSubTaskServiceImpl implements HrSubTaskService {
    @PersistenceContext
    EntityManager manager;
    @Autowired
    HrSubTaskRepository hrSubTaskRepository;
    @Autowired
    HrTaskService hrTaskService;
    @Autowired
    StaffService staffService;
    @Autowired
    HrSubTaskItemService hrSubTaskItemService;

    @Override
    public Boolean delete(UUID id) {
        try{
            if(id!=null){
                hrSubTaskRepository.deleteById(id);
            }
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    @Override
    public HrSubTaskDto createOrUpdate(HrSubTaskDto dto, UUID id) {
        if(dto!=null && dto.getCode() != null) {
            if(id!=null && dto.getId()!=null && !dto.getId().equals(id)){
                return null;
            }
            HrSubTask entity = null;
            if (id != null) {
                entity = this.getEntityById(id);
            }
            if(entity == null){
                entity = new HrSubTask();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
            }
            if(dto.getName()!=null){
                entity.setName(dto.getName());
            }
            if(dto.getCode()!=null){
                entity.setCode(dto.getCode());
            }
            if(dto.getDescription()!=null){
                entity.setDescription(dto.getDescription());
            }
            if(dto.getTaskId()!=null){
                HrTask hrTask = hrTaskService.getEntityById(dto.getTaskId());
                if(hrTask!=null){
                    entity.setTask(hrTask);
                }
            }
            if (dto.getItems() != null && dto.getItems().size() > 0) {
                Set<HrSubTaskItem> subTaskItems = new HashSet<>();
                for (HrSubTaskItemDto hrSubTaskItem : dto.getItems()) {
                    HrSubTaskItem item = null;
                    if (hrSubTaskItem.getId() != null) {
                        item = hrSubTaskItemService.getEntityById(hrSubTaskItem.getId());
                    }
                    if (item == null) {
                        item = new HrSubTaskItem();
                    }
                    item.setCode(hrSubTaskItem.getCode());
                    item.setName(hrSubTaskItem.getName());
                    item.setStartTime(hrSubTaskItem.getStartTime());
                    item.setEndTime(hrSubTaskItem.getEndTime());
                    item.setDescription(hrSubTaskItem.getDescription());
                    item.setValue(hrSubTaskItem.isValue());
                    item.setSubTask(entity);
                    HashMap<UUID, StaffDto> staffItemHashMap = new HashMap<UUID, StaffDto>();
                    if (hrSubTaskItem.getStaffs() != null && hrSubTaskItem.getStaffs().size() > 0) {
                        for (StaffDto newStaff : hrSubTaskItem.getStaffs()) {
                            staffItemHashMap.put(newStaff.getId(), newStaff);
                        }
                    }
                    if (item.getStaffs() != null && item.getStaffs().size() > 0) {
                        List<UUID> newStaffItemIdList = staffItemHashMap.keySet().stream().collect(Collectors.toList());
                        Set<HrSubTaskItemStaff> subTaskItemStaffList = new HashSet<>();
                        if (item.getStaffs() != null && item.getStaffs().size() > 0) {
                            subTaskItemStaffList = item.getStaffs();
                            for (HrSubTaskItemStaff subTaskItemStaff : subTaskItemStaffList) {
                                Staff oldStaff = subTaskItemStaff.getStaff();
                                if (hrSubTaskItem.getStaffs() != null && hrSubTaskItem.getStaffs().size() > 0) {
                                    for (StaffDto newStaff : hrSubTaskItem.getStaffs()) {
                                        if (oldStaff.getId().equals(newStaff.getId())) {
                                            System.out.println("Nhan vien:" + newStaff.getId() + " da ton tai trong item");
                                            staffItemHashMap.remove(newStaff.getId());
                                            continue;
                                        }
                                    }
                                    //check xoa
                                    if (!newStaffItemIdList.contains(oldStaff.getId())) {
                                        //xoa oldStaff
                                        System.out.println("xoa Nhan vien:" + oldStaff.getDisplayName() + " khoi item");
                                        item.getStaffs().remove(subTaskItemStaff);
                                    }
                                }
                            }
                        }
                    }
                    if (!staffItemHashMap.isEmpty()) {
                        List<StaffDto> newStaffList = staffItemHashMap.values().stream().collect(Collectors.toList());
                        if (newStaffList != null && newStaffList.size() > 0) {
                            Staff staff = null;

                            Set<HrSubTaskItemStaff> subTaskItemStaffSet = new HashSet<>();
                            if(item.getStaffs()!=null){
                                subTaskItemStaffSet = item.getStaffs();
                            }
                            for (StaffDto staffDto : newStaffList) {
                                Staff optional = staffService.getEntityById(staffDto.getId());
                                if (optional != null) {
                                    staff = optional;
                                    HrSubTaskItemStaff subTaskItemStaff = new HrSubTaskItemStaff();
                                    subTaskItemStaff.setStaff(staff);
                                    subTaskItemStaff.setSubTaskItem(item);
                                    subTaskItemStaffSet.add(subTaskItemStaff);
                                }
                            }
                            if(subTaskItemStaffSet.size()>0){
                                item.setStaffs(subTaskItemStaffSet);
                            }
                        }
                    }
                    subTaskItems.add(item);
                }
                if(entity.getSubTaskItems()!=null){
                    entity.getSubTaskItems().clear();
                    entity.getSubTaskItems().addAll(subTaskItems);
                }
            }

            entity = hrSubTaskRepository.save(entity);
            return new HrSubTaskDto(entity);
        }
        return null;
    }

    @Override
    public HrSubTaskDto getById(UUID id) {
        HrSubTask entity = this.getEntityById(id);
        if (entity != null) {
            return new HrSubTaskDto(entity);
        }
        return null;
    }

    @Override
    public HrSubTask getEntityById(UUID id) {
        HrSubTask entity = null;
        Optional<HrSubTask> optional = hrSubTaskRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }

        return entity;
    }

    @Override
    public List<HrSubTaskDto> getListSubTask(SearchSubTaskDto dto) {
        if (dto == null) {
            return null;
        }
        String whereClause = " where (1=1)";
        String sql = "SELECT new com.globits.task.dto.HrSubTaskDto(entity) FROM HrSubTask as entity";
        String orderBy = " order by entity.name ASC";
        if (dto.getTaskId()!=null) {
            whereClause += " AND entity.task.id = :taskId";
        }
        sql += whereClause + orderBy;
        Query query = manager.createQuery(sql, HrSubTaskDto.class);
        if (dto.getTaskId()!=null) {
            query.setParameter("taskId", dto.getTaskId());
        }
        List<HrSubTaskDto> entities = query.getResultList();
        return entities;
    }
}
