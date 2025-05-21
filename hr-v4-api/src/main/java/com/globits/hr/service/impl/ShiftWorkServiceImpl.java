package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.HRDepartment;
import com.globits.hr.domain.HrDepartmentShiftWork;
import com.globits.hr.domain.ShiftWork;
import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.dto.ShiftWorkTimePeriodDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.HRDepartmentRepository;
import com.globits.hr.repository.HrDepartmentShiftWorkRepository;
import com.globits.hr.repository.ShiftWorkRepository;
import com.globits.hr.repository.ShiftWorkTimePeriodRepository;
import com.globits.hr.service.HrDepartmentShiftWorkService;
import com.globits.hr.service.ShiftWorkService;
import com.globits.hr.utils.DateTimeUtil;

import com.globits.salary.domain.SalaryItem;
import com.globits.salary.dto.SalaryItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.persistence.Query;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class ShiftWorkServiceImpl extends GenericServiceImpl<ShiftWork, UUID> implements ShiftWorkService {
    @Autowired
    private ShiftWorkRepository shiftWorkRepository;
    @Autowired
    private ShiftWorkTimePeriodRepository shiftWorkTimePeriodRepository;

    @Autowired
    private HrDepartmentShiftWorkService hrDepartmentShiftWorkService;

    @Autowired
    private HRDepartmentRepository hrDepartmentRepository;
    @Autowired
    private HrDepartmentShiftWorkRepository hrDepartmentShiftWorkRepository;

    @Override
    public Page<ShiftWorkDto> searchByPage(SearchDto dto) {
        if (dto == null) return null;

        int pageIndex = Math.max(dto.getPageIndex() - 1, 0);
        int pageSize = dto.getPageSize();

        StringBuilder whereClause = new StringBuilder(" WHERE (1=1) ");
        String orderBy = " ORDER BY entity.code ASC ";

        if (dto.getOrderBy() != null && StringUtils.hasLength(dto.getOrderBy().toString())) {
            orderBy = " ORDER BY entity." + dto.getOrderBy() + " ASC ";
        }

        StringBuilder sql = new StringBuilder("SELECT DISTINCT NEW com.globits.hr.dto.ShiftWorkDto(entity) FROM ShiftWork AS entity ");
        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(DISTINCT entity.id) FROM ShiftWork AS entity ");

        // Filter theo staffId bằng EXISTS để tránh JOIN trùng lặp
        if (dto.getStaffId() != null) {
            whereClause.append(" AND EXISTS (")
                    .append(" SELECT 1 FROM HrDepartmentShiftWork ds ")
                    .append(" JOIN ds.department d ")
                    .append(" JOIN Position pos ON pos.department.id = d.id ")
                    .append(" JOIN Staff staff ON pos.staff.id = staff.id ")
                    .append(" WHERE ds.shiftWork.id = entity.id AND staff.id = :staffId ")
                    .append(")");
        }

        // Tìm kiếm theo từ khóa
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause.append(" AND (UPPER(entity.name) LIKE UPPER(:text) OR UPPER(entity.code) LIKE UPPER(:text)) ");
        }

        // Filter theo ngày có phân ca
        if (dto.getDate() != null) {
            whereClause.append(" AND EXISTS (")
                    .append(" SELECT 1 FROM StaffWorkSchedule ss ")
                    .append(" WHERE ss.shiftWork.id = entity.id AND DATE(ss.workingDate) = DATE(:dateParam) ")
                    .append(")");
        }

        // Filter theo phòng ban
        if (dto.getDepartmentId() != null) {
            sql.append(" JOIN entity.departmentShiftWorks ds ");
            sqlCount.append(" JOIN entity.departmentShiftWorks ds ");
            whereClause.append(" AND ds.department.id = :departmentId ");
        }

        // Filter theo tổ chức
        if (dto.getOrganizationId() != null) {
            whereClause.append(" AND EXISTS (")
                    .append(" SELECT 1 FROM HrDepartmentShiftWork ds ")
                    .append(" WHERE ds.shiftWork.id = entity.id ")
                    .append(" AND ds.department.organization.id = :organizationId ")
                    .append(")");
        }

        sql.append(whereClause).append(orderBy);
        sqlCount.append(whereClause);

        Query q = manager.createQuery(sql.toString(), ShiftWorkDto.class);
        Query qCount = manager.createQuery(sqlCount.toString());

        // Set tham số
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getDepartmentId() != null) {
            q.setParameter("departmentId", dto.getDepartmentId());
            qCount.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getOrganizationId() != null) {
            q.setParameter("organizationId", dto.getOrganizationId());
            qCount.setParameter("organizationId", dto.getOrganizationId());
        }
        if (dto.getStaffId() != null) {
            q.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }
        if (dto.getDate() != null) {
            q.setParameter("dateParam", dto.getDate());
            qCount.setParameter("dateParam", dto.getDate());
        }

        // Phân trang
        q.setFirstResult(pageIndex * pageSize);
        q.setMaxResults(pageSize);

        List<ShiftWorkDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }



    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Long count = shiftWorkRepository.checkCode(code, id);
            return count != 0L;
        }
        return null;
    }

    @Override
    public void remove(UUID id) {
        ShiftWork entity = null;
        Optional<ShiftWork> optional = shiftWorkRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            shiftWorkRepository.delete(entity);
        }
    }

    @Override
    public ShiftWorkDto getById(UUID id) {
        if (id == null) {

            return null;
        }

        ShiftWork shiftWork = shiftWorkRepository.findById(id).orElse(null);

        if (shiftWork == null)
            return null;

        return new ShiftWorkDto(shiftWork, true);
    }


    @Override
    public List<ShiftWorkDto> getAll() {
        List<ShiftWork> allTimePeriod = shiftWorkRepository.findAll();
        List<ShiftWorkDto> models = new ArrayList<>();
        for (ShiftWork workTimePeriod : allTimePeriod) {
            models.add(new ShiftWorkDto(workTimePeriod));
        }
        return models;
    }

    @Override
    public ShiftWorkDto saveOrUpdate(UUID id, ShiftWorkDto dto) {
        if (dto != null && dto.getCode() != null && StringUtils.hasText(dto.getCode())) {
            LocalDateTime currentDate = LocalDateTime.now();
            String currentUserName = "Unknow User";
            ShiftWork entity = null;
            if (dto.getId() != null) {
                if (dto.getId() != null && !dto.getId().equals(id)) {
                    return null;
                }
                Optional<ShiftWork> shiftWorkOptional = shiftWorkRepository.findById(id);
                if (shiftWorkOptional.isPresent()) {
                    entity = shiftWorkOptional.get();

                }
                if (entity != null) {
                    entity.setModifyDate(LocalDateTime.now());
                }
            }
            if (entity == null) {
                entity = new ShiftWork();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
            }

            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setShiftWorkType(dto.getShiftWorkType());
            entity.setConvertedWorkingHours(dto.getConvertedWorkingHours());

            Set<ShiftWorkTimePeriod> timePeriods = new HashSet<>();
            if (dto.getTimePeriods() != null && !dto.getTimePeriods().isEmpty()) {
                for (ShiftWorkTimePeriodDto timePeriodDto : dto.getTimePeriods()) {
                    ShiftWorkTimePeriod timePeriod = null;
                    if (timePeriodDto != null && timePeriodDto.getId() != null) {
                        Optional<ShiftWorkTimePeriod> timePeriodOptional = shiftWorkTimePeriodRepository
                                .findById(timePeriodDto.getId());
                        if (timePeriodOptional.isPresent()) {
                            timePeriod = timePeriodOptional.get();
                        }
                    }
                    if (timePeriod == null) {
                        timePeriod = new ShiftWorkTimePeriod();
                        timePeriod.setShiftWork(entity);
                        timePeriod.setCreateDate(currentDate);
                        timePeriod.setCreatedBy(currentUserName);
                    }
                    if (timePeriodDto != null) {
                        timePeriod = timePeriodDto.toEntity(timePeriodDto, timePeriod);
                    }

                    timePeriods.add(timePeriod);
                }
            }

            if (entity.getTimePeriods() != null) {
                entity.getTimePeriods().clear();
                entity.getTimePeriods().addAll(timePeriods);
            } else {
                entity.setTimePeriods(timePeriods);
            }

            double totalHour = 0;
            for (ShiftWorkTimePeriod shiftWorkTimePeriod : entity.getTimePeriods()) {
                if (shiftWorkTimePeriod.getStartTime() != null && shiftWorkTimePeriod.getEndTime() != null) {
                    LocalTime start = shiftWorkTimePeriod.getStartTime().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalTime();
                    LocalTime end = shiftWorkTimePeriod.getEndTime().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalTime();

                    if (end.isAfter(start)) {
                        totalHour += Duration.between(start, end).toMinutes() / 60.0;
                    } else {
                        totalHour += Duration.between(start, LocalTime.MAX).toMinutes() / 60.0; // Từ start đến 23:59
                        totalHour += Duration.between(LocalTime.MIN, end).toMinutes() / 60.0;   // Từ 00:00 đến end
                    }
                }
            }
            entity.setTotalHours(totalHour);

            //thêm department thực hiện shiftwork này
            hrDepartmentShiftWorkService.generateHrDepartmentShiftWork(dto, entity);
            entity = shiftWorkRepository.save(entity);

            return new ShiftWorkDto(entity);
        }
        return null;
    }

    @Override
    public ShiftWorkDto findByCode(String code) {
        List<ShiftWork> entities = shiftWorkRepository.findByCode(code);
        if (entities == null || entities.size() == 0) {
            return null;
        }
        return new ShiftWorkDto(entities.get(0));
    }

    @Override
    public Integer saveListShiftWork(List<ShiftWorkDto> list) {
        try {
            if (list == null || list.isEmpty()) return null;

            List<ShiftWork> saveList = new ArrayList<>();
            for (ShiftWorkDto dto : list) {
                ShiftWork entity = null;
                if (dto.getCode() != null && StringUtils.hasText(dto.getCode())) {
                    List<ShiftWork> shiftWorkList = shiftWorkRepository.findByCode(dto.getCode());
                    if (shiftWorkList != null && !shiftWorkList.isEmpty()) {
                        entity = shiftWorkList.get(0);
                    }
                }
                if (entity == null) {
                    entity = new ShiftWork();
                    entity.setCode(dto.getCode());
                }
                entity.setName(dto.getName());
                entity.setTotalHours(dto.getTotalHours());
                if (dto.getConvertedWorkingHours() == null) {
                    entity.setConvertedWorkingHours(dto.getTotalHours());
                } else {
                    entity.setConvertedWorkingHours(dto.getConvertedWorkingHours());
                }
                if (entity.getDepartmentShiftWorks() == null) {
                    entity.setDepartmentShiftWorks(new HashSet<>());
                }
//                entity.getDepartmentShiftWorks().clear();
                if (dto.getDepartments() != null && !dto.getDepartments().isEmpty()) {
                    for (HRDepartmentDto departmentDto : dto.getDepartments()) {
                        HRDepartment department = null;
                        if (departmentDto.getCode() != null) {
                            List<HRDepartment> departmentList = hrDepartmentRepository.findByCode(departmentDto.getCode());
                            if (departmentList != null && !departmentList.isEmpty()) {
                                department = departmentList.get(0);
                            }
                        }
                        if (department == null) {
                            continue;
                        }
                        HrDepartmentShiftWork hrDepartmentShiftWork = null;
                        if (department.getId() != null && dto.getCode() != null) {
                            List<HrDepartmentShiftWork> hrDepartmentShiftWorkList = hrDepartmentShiftWorkRepository
                                    .findByDepartmentIdShiftWorkCode(department.getId(), dto.getCode());
                            if (hrDepartmentShiftWorkList != null && !hrDepartmentShiftWorkList.isEmpty()) {
                                hrDepartmentShiftWork = hrDepartmentShiftWorkList.get(0);
                            }
                        }
                        if (hrDepartmentShiftWork == null) {
                            hrDepartmentShiftWork = new HrDepartmentShiftWork();
                        }
                        hrDepartmentShiftWork.setDepartment(department);
                        hrDepartmentShiftWork.setShiftWork(entity);
                        entity.getDepartmentShiftWorks().add(hrDepartmentShiftWork);
                    }
                }
                if (entity.getTimePeriods() == null || entity.getTimePeriods().isEmpty()) {
                    entity.setTimePeriods(new HashSet<>());
                }
//                entity.getTimePeriods().clear();
                if (dto.getTimePeriods() != null && !dto.getTimePeriods().isEmpty()) {
                    for (ShiftWorkTimePeriodDto timePeriodDto : dto.getTimePeriods()) {
                        ShiftWorkTimePeriod timePeriod = null;
                        if (timePeriodDto.getCode() != null) {
                            timePeriod = shiftWorkTimePeriodRepository.findByCode(timePeriodDto.getCode());
                            if (timePeriod == null) {
                                timePeriod = new ShiftWorkTimePeriod();
                            }
                        }
                        if (timePeriod == null) {
                            timePeriod = new ShiftWorkTimePeriod();
                        }
                        timePeriod.setShiftWork(entity);
                        timePeriod.setStartTime(timePeriodDto.getStartTime());
                        timePeriod.setEndTime(timePeriodDto.getEndTime());
                        timePeriod.setCode(timePeriodDto.getCode());
                        timePeriod.setAllowedLateMinutes(timePeriodDto.getAllowedLateMinutes());
                        timePeriod.setMinTimekeepingHour(timePeriodDto.getMinTimekeepingHour());
                        timePeriod.setMinWorkTimeHour(timePeriodDto.getMinWorkTimeHour());
                        timePeriod.setWorkRatio(timePeriodDto.getWorkRatio());

                        entity.getTimePeriods().add(timePeriod);
                    }
                }
                saveList.add(entity);
            }
            shiftWorkRepository.saveAll(saveList);
            return saveList.size();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
