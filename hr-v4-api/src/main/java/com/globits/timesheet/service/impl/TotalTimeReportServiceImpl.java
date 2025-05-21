package com.globits.timesheet.service.impl;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import com.globits.hr.HrConstants;
import com.globits.hr.dto.HrGlobalPropertyDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.service.HrGlobalPropertyService;
import com.globits.hr.service.UserExtService;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.timesheet.dto.search.SearchTotalTimeReportDto;
import com.globits.timesheet.service.TotalTimeReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.globits.hr.dto.function.TotalTimeReportDto;

@Service
public class TotalTimeReportServiceImpl implements TotalTimeReportService {
    @PersistenceContext
    EntityManager manager;
    @Autowired
    private UserExtService userExtService;
    @Autowired
    private HrGlobalPropertyService globalPropertyService;

    @Override
    public Page<TotalTimeReportDto> totalTimeReport(SearchTotalTimeReportDto dto) {
        boolean isRoleUser = false;
        boolean isRoleAdmin = false;
        boolean isRoleManager = false;

        UserDto user = userExtService.getCurrentUser();
        if (user != null && user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (RoleDto item : user.getRoles()) {
                if (item.getName() != null && "ROLE_ADMIN".equals(item.getName())) {
                    isRoleAdmin = true;
                }
                if (item.getName() != null && "HR_MANAGER".equals(item.getName())) {
                    isRoleManager = true;
                }
                if (item.getName() != null && "HR_USER".equals(item.getName())) {
                    isRoleUser = true;
                }
            }
        }

        if (isRoleAdmin) {
            isRoleManager = false;
            isRoleUser = false;
        } else {
            if (isRoleManager) {
                isRoleUser = false;
            }
        }
        if (dto == null || isRoleUser) {
            return null;
        }
        Integer year = LocalDate.now().getYear();
        if (dto.getYearReport() != null) {
            year = dto.getYearReport();
        }
        Integer month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        if (dto.getMonthReport() != null) {
            month = dto.getMonthReport();
        }
        Integer week = 1;
        if (dto.getWeekReport() != null) {
            week = dto.getWeekReport();
        }
        if (dto.getTimeReport() == 1) {
            dto.setTimeReport(null);
            List<Date> startEndWeek = DateTimeUtil.getDateToDateOfWeek(week, month, year);
            dto.setFromDate(startEndWeek.get(0));
            dto.setToDate(startEndWeek.get(1));
        }

        if (dto.getFromDate() != null && dto.getToDate() != null && dto.getToDate().before(dto.getFromDate())) {
            return null;
        }
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();
        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        List<TotalTimeReportDto> result = new ArrayList<>();
        //thêm điều kiên lọc theo name status
//        String  whereCause ="And staff.status.name =:statusName";
        String sqlCount = "SELECT count(staff.id) FROM Staff staff ";
        String sqlStaff = "SELECT new com.globits.hr.dto.StaffDto(staff,true,true) FROM Staff staff ";
        String whereCause =" WHERE (1=1) ";

//        whereCause += " and size(staff.timeSheets) > 0 ";

        sqlCount+=whereCause;
        sqlStaff+=whereCause;
        HrGlobalPropertyDto activeStatusWorking = globalPropertyService
                .findGlobalProperty(HrConstants.GLOBAL_PROPERTY_ACTIVE_STATUS_WORKING);
        if (activeStatusWorking != null && activeStatusWorking.getPropertyValue() != null) {
            sqlCount += " and staff.status.code = :activeStatus ";
            sqlStaff += " and staff.status.code = :activeStatus ";
        }

//        sqlCount+=whereCause;
//        sqlStaff+=whereCause;
        Query query = manager.createQuery(sqlStaff);
        Query queryCount = manager.createQuery(sqlCount);

        if (activeStatusWorking != null && activeStatusWorking.getPropertyValue() != null) {
            query.setParameter("activeStatus", activeStatusWorking.getPropertyValue());
            queryCount.setParameter("activeStatus", activeStatusWorking.getPropertyValue());
        }
        //set param de loc nhung staff dang lam viec
//        query.setParameter("statusName", "Đang làm việc");
//        queryCount.setParameter("statusName", "Đang làm việc");
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        List<StaffDto> listStaff = query.getResultList();
        for (StaffDto staffDto : listStaff) {
            double totalHours = getTotalHoursProjectStaff(staffDto.getId(), dto);
            TotalTimeReportDto totalTimeReportDto = new TotalTimeReportDto();
            totalTimeReportDto.setStaffId(staffDto.getId());
            totalTimeReportDto.setTotalTime(totalHours);
            totalTimeReportDto.setStaffName(staffDto.getDisplayName());
            totalTimeReportDto.setMonth(month);
            totalTimeReportDto.setWeek(week);
            totalTimeReportDto.setYear(year);
            if (staffDto.getCivilServantType() != null) {
                totalTimeReportDto.setCivilServant(staffDto.getCivilServantType().getName());
            }
            if(staffDto.getDepartment() != null){
                totalTimeReportDto.setDepartment(staffDto.getDepartment().getName());
            }
            result.add(totalTimeReportDto);
            // result.add(new TotalTimeReportDto(staffDto.getId(),
            // staffDto.getDisplayName(),staffDto.getCivilServantCategory().getName(),
            // totalHours, week, month, year));
        }
        result.sort((o1, o2) -> o2.getTotalTime().compareTo(o1.getTotalTime()));

        long count = (long) queryCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(result, pageable, count);
    }

    public double getTotalHoursProjectStaff(UUID staffId, SearchTotalTimeReportDto dto) {
        Set<UUID> checkContain = new HashSet<>();
        double totalHours = 0;
        List<TotalTimeReportDto> staffDetails = getStaffDetails(staffId, dto);
        for (TotalTimeReportDto reportDto : staffDetails) {
            totalHours += reportDto.getTotalTime();
            checkContain.add(reportDto.getTimeSheetId());
        }
        // List<TotalTimeReportDto> staffTimeSheets = getStaffTimeSheets(staffId, dto);
        // staffTimeSheets.removeIf(reportDto ->
        // checkContain.contains(reportDto.getTimeSheetId()));
        // for (TotalTimeReportDto reportDto : staffTimeSheets) {
        // totalHours += reportDto.getTotalTime();
        // }
        return totalHours;
    }

    public List<TotalTimeReportDto> getStaffDetails(UUID staffId, SearchTotalTimeReportDto dto) {
        String sql = "SELECT new com.globits.hr.dto.function.TotalTimeReportDto(entity.employee.id, entity.employee.displayName, entity.timeSheet.id, entity.duration, WEEK(entity.startTime), MONTH(entity.startTime), YEAR(entity.startTime)) "
                +
                "FROM TimeSheetDetail entity WHERE entity.employee.id = :staffId AND entity.duration > 0 ";
        String where = "";
        if (dto.getTimeReport() != null) {
            if (dto.getTimeReport() == 2) {
                where = "AND MONTH(entity.startTime) =: monthReport AND YEAR(entity.startTime) =: year ";
            }
            if (dto.getTimeReport() == 3) {
                where = "AND YEAR(entity.startTime) =: year ";
            }
        }
        if (dto.getFromDate() != null && dto.getToDate() != null) {
            where = " AND entity.startTime >= :fromDate AND entity.startTime <= :toDate ";
        }
        if (dto.getProjectId() != null) {
            where += "AND entity.project.id =: projectId ";
        }
        sql += where;
        Query query = manager.createQuery(sql);
        query.setParameter("staffId", staffId);
        if (dto.getProjectId() != null) {
            query.setParameter("projectId", dto.getProjectId());
        }
        setParameterToQuery(query, dto);
        return query.getResultList();
    }

    public List<TotalTimeReportDto> getStaffTimeSheets(UUID staffId, SearchTotalTimeReportDto dto) {
        String where = "";
        String sql = "SELECT new com.globits.hr.dto.function.TotalTimeReportDto(ts.staff.id, ts.staff.displayName, entity.id, entity.totalHours, WEEK(entity.startTime), MONTH(entity.startTime), YEAR(entity.startTime)) "
                +
                "FROM TimeSheet entity, TimeSheetStaff ts WHERE entity.id = ts.timesheet.id AND ts.staff.id = :staffId ";
        if (dto.getTimeReport() != null) {
            if (dto.getTimeReport() == 2) {
                where = "AND MONTH(entity.startTime) =: monthReport AND YEAR(entity.startTime) =: year ";
            }
            if (dto.getTimeReport() == 3) {
                where = "AND YEAR(entity.startTime) =: year ";
            }
        }
        if (dto.getFromDate() != null && dto.getToDate() != null) {
            where = " AND entity.startTime >= :fromDate AND entity.startTime <= :toDate ";
        }
        if (dto.getProjectId() != null) {
            where += "AND entity.project.id =: projectId ";
        }
        sql += where;
        Query query = manager.createQuery(sql);
        query.setParameter("staffId", staffId);
        if (dto.getProjectId() != null) {
            query.setParameter("projectId", dto.getProjectId());
        }
        setParameterToQuery(query, dto);
        return query.getResultList();
    }

    public void setParameterToQuery(Query query, SearchTotalTimeReportDto dto) {
        if (dto.getTimeReport() != null) {
            if (dto.getTimeReport() == 2 && dto.getMonthReport() != null && dto.getYearReport() != null) {
                if (dto.getYearReport() != null) {
                    query.setParameter("year", dto.getYearReport());
                } else {
                    query.setParameter("year", LocalDate.now().getYear());
                }
                query.setParameter("monthReport", dto.getMonthReport());
            }
            if (dto.getTimeReport() == 3) {
                if (dto.getYearReport() != null) {
                    query.setParameter("year", dto.getYearReport());
                } else {
                    query.setParameter("year", LocalDate.now().getYear());
                }
            }
            if (dto.getTimeReport() == 1 && dto.getWeekReport() == null) {
                query.setParameter("weekReport", LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
                if (dto.getYearReport() != null) {
                    query.setParameter("year", dto.getYearReport());
                } else {
                    query.setParameter("year", LocalDate.now().getYear());
                }
            }
            if (dto.getTimeReport() == 2 && dto.getMonthReport() == null) {
                query.setParameter("monthReport", Calendar.getInstance().get(Calendar.MONTH) + 1);
                if (dto.getYearReport() != null) {
                    query.setParameter("year", dto.getYearReport());
                } else {
                    query.setParameter("year", LocalDate.now().getYear());
                }
            }
        }
        if (dto.getToDate() != null && dto.getFromDate() != null) {
            query.setParameter("fromDate", dto.getFromDate());
            query.setParameter("toDate", dto.getToDate());
        }
    }
}
