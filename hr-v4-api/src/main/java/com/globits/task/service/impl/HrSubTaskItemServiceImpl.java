package com.globits.task.service.impl;

import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.service.StaffService;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.task.domain.HrSubTask;
import com.globits.task.domain.HrSubTaskItem;
import com.globits.task.domain.HrSubTaskItemStaff;
import com.globits.task.dto.HrSubTaskItemDto;
import com.globits.task.dto.SearchSubTaskDto;
import com.globits.task.repository.HrSubTaskItemRepository;
import com.globits.task.service.HrSubTaskItemService;
import com.globits.task.service.HrSubTaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HrSubTaskItemServiceImpl implements HrSubTaskItemService {
    @PersistenceContext
    EntityManager manager;
    @Autowired
    HrSubTaskItemRepository hrSubTaskItemRepository;
    @Autowired
    HrSubTaskService hrSubTaskService;
    @Autowired
    StaffService staffService;

    @Override
    public Boolean delete(UUID id) {
        try{
            if(id!=null){
                hrSubTaskItemRepository.deleteById(id);
            }
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    @Override
    public HrSubTaskItemDto createOrUpdate(HrSubTaskItemDto dto, UUID id) {
        if(dto!=null && dto.getCode() != null) {
            if(id!=null && dto.getId()!=null && !dto.getId().equals(id)){
                return null;
            }
            if (dto.getEndTime().before(dto.getStartTime())) {
                return null;
            }
            HrSubTaskItem entity = null;
            if (id != null) {
                entity = this.getEntityById(id);
            }
            if(entity == null){
                entity = new HrSubTaskItem();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
            }
            if(dto.getName()!=null){
                entity.setName(dto.getName());
            }
            entity.setCode(dto.getCode());
            if(dto.getDescription()!=null){
                entity.setDescription(dto.getDescription());
            }
            entity.setValue(dto.isValue());
            if(dto.getStartTime()!=null){
                entity.setStartTime(DateTimeUtil.getStartOfDay(dto.getStartTime()));
            }
            if(dto.getEndTime()!=null){
                entity.setEndTime(DateTimeUtil.getEndOfDay(dto.getEndTime()));
            }
            if(dto.getSubTaskId()!=null){
                HrSubTask hrSubTask = hrSubTaskService.getEntityById(dto.getSubTaskId());
                if(hrSubTask!=null){
                    entity.setSubTask(hrSubTask);
                }
            }
            HashMap<UUID, StaffDto> staffHashMap = new HashMap<UUID, StaffDto>();
            if (dto.getStaffs() != null && dto.getStaffs().size() > 0) {
                for (StaffDto newStaff : dto.getStaffs()) {
                    staffHashMap.put(newStaff.getId(), newStaff);
                }
            }
            List<UUID> newStaffIdList = staffHashMap.keySet().stream().collect(Collectors.toList());
            Set<HrSubTaskItemStaff> subTaskItemStaffList = new HashSet<>();
            if (entity.getStaffs() != null && entity.getStaffs().size() > 0) {
                subTaskItemStaffList = entity.getStaffs();
                for (HrSubTaskItemStaff subTaskItemStaff : subTaskItemStaffList) {
                    Staff oldStaff = subTaskItemStaff.getStaff();
                    if (dto.getStaffs() != null && dto.getStaffs().size() > 0) {
                        for (StaffDto newStaff : dto.getStaffs()) {
                            if (oldStaff.getId().equals(newStaff.getId())) {
                                System.out.println("Nhan vien:" + newStaff.getId() + " da ton tai");
                                staffHashMap.remove(newStaff.getId());
                                continue;
                            }
                        }
                        //check xoa
                        if (!newStaffIdList.contains(oldStaff.getId())) {
                            //xoa oldStaff
                            System.out.println("xoa Nhan vien:" + oldStaff.getDisplayName() + " khoi task");
                            entity.getStaffs().remove(subTaskItemStaff);
                        }
                    }
                }
            }
            if (!staffHashMap.isEmpty()) {
                List<StaffDto> newStaffList = staffHashMap.values().stream().collect(Collectors.toList());
                if (newStaffList != null && newStaffList.size() > 0) {
                    Staff staff = null;

                    Set<HrSubTaskItemStaff> subTaskItemStaffSet = new HashSet<>();
                    if(entity.getStaffs()!=null){
                        subTaskItemStaffSet = entity.getStaffs();
                    }
                    for (StaffDto staffDto : newStaffList) {
                        Staff optional = staffService.getEntityById(staffDto.getId());
                        if (optional != null) {
                            staff = optional;
                            HrSubTaskItemStaff subTaskItemStaff = new HrSubTaskItemStaff();
                            subTaskItemStaff.setStaff(staff);
                            subTaskItemStaff.setSubTaskItem(entity);
                            subTaskItemStaffSet.add(subTaskItemStaff);
                        }
                    }
                    if(subTaskItemStaffSet.size()>0){
                        entity.setStaffs(subTaskItemStaffSet);
                    }
                }
            }
            entity = hrSubTaskItemRepository.save(entity);
            return new HrSubTaskItemDto(entity);
        }
        return null;
    }

    @Override
    public HrSubTaskItemDto getById(UUID id) {
        HrSubTaskItem entity = this.getEntityById(id);
        if (entity != null) {
            return new HrSubTaskItemDto(entity);
        }
        return null;
    }

    @Override
    public HrSubTaskItem getEntityById(UUID id) {
        HrSubTaskItem entity = null;
        Optional<HrSubTaskItem> optional = hrSubTaskItemRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        return entity;
    }

    @Override
    public List<HrSubTaskItemDto> getListSubTaskItem(SearchSubTaskDto dto) {
        if (dto == null) {
            return null;
        }
        String whereClause = " where (1=1)";
        String sql = "SELECT new com.globits.task.dto.HrSubTaskItemDto(entity) FROM HrSubTaskItem as entity";
        String orderBy = " order by entity.name ASC";
        if (dto.getSubTaskId()!=null) {
            whereClause += " AND entity.subTask.id = :subTaskId";
        }
        sql += whereClause + orderBy;
        Query query = manager.createQuery(sql, HrSubTaskItemDto.class);
        if (dto.getSubTaskId()!=null) {
            query.setParameter("subTaskId", dto.getSubTaskId());
        }
        List<HrSubTaskItemDto> entities = query.getResultList();
        return entities;
    }
}
