package com.globits.task.service.impl;

import com.globits.hr.domain.Staff;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.task.domain.HrTask;
import com.globits.task.domain.HrTaskStaff;
import com.globits.task.dto.HrTaskStaffDto;
import com.globits.task.repository.HrTaskRepository;
import com.globits.task.repository.HrTaskStaffRepository;
import com.globits.task.service.HrTaskStaffService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class HrTaskStaffServiceImpl implements HrTaskStaffService {
    @Autowired
    HrTaskStaffRepository hrTaskStaffRepository;
    @Autowired
    HrTaskRepository hrTaskRepository;
    @Autowired
    StaffRepository staffRepository;

    @Override
    public Boolean delete(UUID id) {
        try {
            hrTaskStaffRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public HrTaskStaffDto createOrUpdate(HrTaskStaffDto dto, UUID id) {
        boolean isAdd = false;
        boolean checkSave = true;
        if (dto == null) {
            return null;
        }
        HrTaskStaff taskStaff = null;
        if (id != null) {
            Optional<HrTaskStaff> optional = hrTaskStaffRepository.findById(id);
            if (optional.isPresent()) {
                taskStaff = optional.get();
            }
        }
        if(taskStaff==null && dto.getId()!=null) {
        	Optional<HrTaskStaff> optional = hrTaskStaffRepository.findById(dto.getId());
            if (optional.isPresent()) {
                taskStaff = optional.get();
            }
        }
        if (taskStaff == null) {
            taskStaff = new HrTaskStaff();
            isAdd = true;
        }
        HrTask hrTask = null;
        if (dto.getTaskDto() != null && dto.getTaskDto().getId() != null) {
            Optional<HrTask> optional = hrTaskRepository.findById(dto.getTaskDto().getId());
            if (optional.isPresent()) {
                hrTask = optional.get();
            }
            taskStaff.setTask(hrTask);
        }
        Staff staff = null;
        if (dto.getStaffDto() != null && dto.getStaffDto().getId() != null) {
            Optional<Staff> optional = staffRepository.findById(dto.getStaffDto().getId());
            if (optional.isPresent()) {
                staff = optional.get();
            }
            taskStaff.setStaff(staff);
        }
        if (dto.getStatus() != null) {
            taskStaff.setStatus(dto.getStatus());
        }
        if (dto.getDateAssign() != null) {
            dto.setDateAssign(DateTimeUtil.getStartOfDay(dto.getDateAssign()));
        }
        if (dto.getDateFinished() != null) {
            dto.setDateFinished(DateTimeUtil.getEndOfDay(dto.getDateFinished()));
        }
        taskStaff.setDateAssign(dto.getDateAssign());
        taskStaff.setDateFinished(dto.getDateFinished());
        taskStaff.setRole(dto.getRole());
        if (isAdd == true) {
            long count = 0;
            if (dto.getStaffDto() != null && dto.getStaffDto().getId() != null && dto.getTaskDto() != null && dto.getTaskDto().getId() != null) {
                count = hrTaskStaffRepository.countByStaffAndTask(dto.getStaffDto().getId(), dto.getTaskDto().getId());
            }
            if (count > 0) {
                checkSave = false;
            }
        }
        if (isAdd == false) {
            long count = 0;
            if (dto.getStaffDto() != null && dto.getStaffDto().getId() != null && dto.getTaskDto() != null && dto.getTaskDto().getId() != null && id != null) {
                count = hrTaskStaffRepository.countByStaffAndTaskUpdate(dto.getStaffDto().getId(), dto.getTaskDto().getId(), id);
            }
            if (count > 0) {
                checkSave = false;
            }
        }
        if (checkSave == true) {
            taskStaff = hrTaskStaffRepository.save(taskStaff);
        }
        return new HrTaskStaffDto(taskStaff);
    }

    @Override
    public HrTaskStaffDto createOrUpdateByUUID(UUID taskId, UUID staffId, UUID id) {
        boolean isAdd = false;
        boolean checkSave = true;
        HrTaskStaff taskStaff = null;
        if (id != null) {
            Optional<HrTaskStaff> optional = hrTaskStaffRepository.findById(id);
            if (optional.isPresent()) {
                taskStaff = optional.get();
            }
        }
        if (taskStaff == null) {
            taskStaff = new HrTaskStaff();
            isAdd = true;
        }
        HrTask hrTask = null;
        if (taskId != null) {
            Optional<HrTask> optional = hrTaskRepository.findById(taskId);
            if (optional.isPresent()) {
                hrTask = optional.get();
            }
            taskStaff.setTask(hrTask);
        }
        Staff staff = null;
        if (staffId != null) {
            Optional<Staff> optional = staffRepository.findById(staffId);
            if (optional.isPresent()) {
                staff = optional.get();
            }
            taskStaff.setStaff(staff);
        }
        if (isAdd == true) {
            long count = 0;
            if (staffId != null && taskId != null) {
                count = hrTaskStaffRepository.countByStaffAndTask(staffId, taskId);
            }
            if (count > 0) {
                checkSave = false;
            }
        }
        if (isAdd == false) {
            long count = 0;
            if (staffId != null && taskId != null && id != null) {
                count = hrTaskStaffRepository.countByStaffAndTaskUpdate(staffId, taskId, id);
            }
            if (count > 0) {
                checkSave = false;
            }
        }
        if (checkSave == true) {
            taskStaff = hrTaskStaffRepository.save(taskStaff);
        }
        return new HrTaskStaffDto(taskStaff);
    }
}
