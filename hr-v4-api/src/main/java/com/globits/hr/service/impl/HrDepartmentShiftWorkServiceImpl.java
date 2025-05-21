package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.HRDepartment;
import com.globits.hr.domain.HrDepartmentShiftWork;
import com.globits.hr.domain.ShiftWork;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.repository.HRDepartmentRepository;
import com.globits.hr.repository.HrDepartmentShiftWorkRepository;
import com.globits.hr.repository.ShiftWorkRepository;
import com.globits.hr.service.HrDepartmentShiftWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HrDepartmentShiftWorkServiceImpl extends GenericServiceImpl<HrDepartmentShiftWork, UUID> implements HrDepartmentShiftWorkService {
    @Autowired
    private HRDepartmentRepository hrDepartmentRepository;

    @Autowired
    private ShiftWorkRepository shiftWorkRepository;

    @Autowired
    private HrDepartmentShiftWorkRepository hrDepartmentShiftWorkRepository;


    @Override
    public List<HRDepartmentDto> getDepartmentsHasShiftWork(UUID shiftWorkId) {
        if (shiftWorkId == null) return null;

        List<HrDepartmentShiftWork> hrDepartmentShiftWorks = hrDepartmentShiftWorkRepository.findByShiftWorkId(shiftWorkId);

        List<HRDepartmentDto> response = new ArrayList<>();

        if (hrDepartmentShiftWorks == null || hrDepartmentShiftWorks.isEmpty()) return response;

        for (HrDepartmentShiftWork hrDepartmentShiftWork : hrDepartmentShiftWorks) {
            HRDepartmentDto department = new HRDepartmentDto();

            department.setId(hrDepartmentShiftWork.getDepartment().getId());
            department.setName(hrDepartmentShiftWork.getDepartment().getName());
            department.setCode(hrDepartmentShiftWork.getDepartment().getCode());
            department.setSortNumber(hrDepartmentShiftWork.getDepartment().getSortNumber());

            response.add(department);
        }

        Collections.sort(response,
                Comparator.comparing(HRDepartmentDto::getSortNumber, Comparator.nullsLast(Comparator.naturalOrder())));

        return response;
    }

    @Override
    public List<ShiftWorkDto> getShiftWorksOfDepartment(UUID departmentId) {
        if (departmentId == null) return null;

        List<HrDepartmentShiftWork> hrDepartmentShiftWorks = hrDepartmentShiftWorkRepository.findByDepartmentId(departmentId);

        List<ShiftWorkDto> response = new ArrayList<>();

        if (hrDepartmentShiftWorks == null || hrDepartmentShiftWorks.isEmpty()) return response;

        for (HrDepartmentShiftWork hrDepartmentShiftWork : hrDepartmentShiftWorks) {
            ShiftWorkDto shiftWork = new ShiftWorkDto();

            shiftWork.setId(hrDepartmentShiftWork.getDepartment().getId());
            shiftWork.setName(hrDepartmentShiftWork.getDepartment().getName());
            shiftWork.setCode(hrDepartmentShiftWork.getDepartment().getCode());

            response.add(shiftWork);
        }

        return response;
    }

    @Override
    public void generateHrDepartmentShiftWork(HRDepartmentDto dto, HRDepartment entity) {
        if (entity.getDepartmentShiftWorks() == null) {
            entity.setDepartmentShiftWorks(new HashSet<>());
        }
        entity.getDepartmentShiftWorks().clear();

        if (dto.getShiftWorks() != null && !dto.getShiftWorks().isEmpty()) {
            for (ShiftWorkDto shiftWorkDto : dto.getShiftWorks()) {
                HrDepartmentShiftWork hrDepartmentShiftWork = null;
                ShiftWork shiftWork = null;


                if (shiftWorkDto != null && shiftWorkDto.getId() != null) {
                    shiftWork = shiftWorkRepository.findById(shiftWorkDto.getId()).orElse(null);

                }

                if (shiftWork == null) {
                    continue;
                }

                List<HrDepartmentShiftWork> hrDepartmentShiftWorkList = hrDepartmentShiftWorkRepository.findByDepartmentIdShiftWorkId(entity.getId(), shiftWorkDto.getId());

                if (hrDepartmentShiftWorkList != null && !hrDepartmentShiftWorkList.isEmpty()) {
                    hrDepartmentShiftWork = hrDepartmentShiftWorkList.get(0);
                } else {
                    hrDepartmentShiftWork = new HrDepartmentShiftWork();
                }
                hrDepartmentShiftWork.setDepartment(entity);
                hrDepartmentShiftWork.setShiftWork(shiftWork);

                entity.getDepartmentShiftWorks().add(hrDepartmentShiftWork);

            }
        }
    }

    @Override
    public void generateHrDepartmentShiftWork(ShiftWorkDto dto, ShiftWork entity) {
        if (entity.getDepartmentShiftWorks() == null) {
            entity.setDepartmentShiftWorks(new HashSet<>());
        }
        entity.getDepartmentShiftWorks().clear();

        if (dto.getDepartments() != null && !dto.getDepartments().isEmpty()) {
            for (HRDepartmentDto hrDepartmentDto : dto.getDepartments()) {
                HrDepartmentShiftWork hrDepartmentShiftWork = null;
                HRDepartment hrDepartment = null;

                if (hrDepartmentDto != null && hrDepartmentDto.getId() != null) {
                    hrDepartment = hrDepartmentRepository.findById(hrDepartmentDto.getId()).orElse(null);

                }
                if (hrDepartment == null) {
                    continue;
                }

                List<HrDepartmentShiftWork> hrDepartmentShiftWorkList = hrDepartmentShiftWorkRepository.findByDepartmentIdShiftWorkId(hrDepartment.getId(), entity.getId());

                if (hrDepartmentShiftWorkList != null && !hrDepartmentShiftWorkList.isEmpty()) {
                    hrDepartmentShiftWork = hrDepartmentShiftWorkList.get(0);
                } else {
                    hrDepartmentShiftWork = new HrDepartmentShiftWork();
                }
                hrDepartmentShiftWork.setDepartment(hrDepartment);
                hrDepartmentShiftWork.setShiftWork(entity);

                entity.getDepartmentShiftWorks().add(hrDepartmentShiftWork);

            }
        }

    }
}
