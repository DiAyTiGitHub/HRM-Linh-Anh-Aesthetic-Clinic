package com.globits.timesheet.service.impl;

import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.function.ProjectTimeWorkChartDto;
import com.globits.hr.dto.function.ProjectTimeWorkTableDto;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.UserExtService;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.timesheet.dto.search.SearchTotalTimeReportDto;
import com.globits.timesheet.service.ProjectTimeWorkTableService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProjectTimeWorkTableServiceImpl implements ProjectTimeWorkTableService {
    @PersistenceContext
    private EntityManager manager;
    @Autowired
    private UserExtService userExtService;
    @Autowired
    private TotalTimeReportServiceImpl reportService;
    @Autowired
    private StaffRepository staffRepository;

    @Override
    public Page<ProjectTimeWorkTableDto> getPageAllStaff() {
        SearchTotalTimeReportDto dto = new SearchTotalTimeReportDto();
        String whereClause = " and (entity.isFinished is null or entity.isFinished = false) ";
        String sql = "select new com.globits.hr.dto.function.ProjectTimeWorkTableDto(entity) from Project entity where (1=1) ";
        sql += whereClause;
        Query query = manager.createQuery(sql);
        List<ProjectTimeWorkTableDto> tableProjectTimeWork = query.getResultList();
        if (tableProjectTimeWork == null) {
            return null;
        }
        for (ProjectTimeWorkTableDto timeWork : tableProjectTimeWork) {
            dto.setProjectId(timeWork.getProjectId());
            timeWork.setTotalHours(timeWorkProject(timeWork.getProjectStaff(), dto));
            timeWork.setProjectStaff(null);
        }
        return new PageImpl<>(tableProjectTimeWork);
    }

    private double timeWorkProject(List<StaffDto> listStaff, SearchTotalTimeReportDto dto) {
        double result = 0;
        if (listStaff.size() > 0) {
            for (StaffDto staffDto : listStaff) {
                result += reportService.getTotalHoursProjectStaff(staffDto.getId(), dto);
            }
        }
        return result;
    }

    @Override
    public Page<ProjectTimeWorkTableDto> getPageStaff(SearchTotalTimeReportDto dto) {
        boolean isRoleUser = false;
        boolean isRoleAdmin = false;
        boolean isRoleManager = false;

        UserDto user = userExtService.getCurrentUser();
        if (user != null && user.getRoles() != null && user.getRoles().size() > 0) {
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
        if (dto == null || !isRoleUser) {
            return null;
        }

        int year = LocalDate.now().getYear();
        if (dto.getYearReport() != null) {
            year = dto.getYearReport();
        }
        StaffDto staffDto = staffRepository.getByUsername(user.getUsername());

        int month;
        if (dto.getMonthReport() == null) {
            month = new Date(System.currentTimeMillis()).getMonth() + 1;
        } else {
            month = dto.getMonthReport();
        }
        if (dto.getTimeReport() == 1) {
            int week = 1;
            if (dto.getWeekReport() != null) {
                week = dto.getWeekReport();
            }
            List<Date> startEndWeekUser = DateTimeUtil.getDateToDateOfWeek(week, month, year);
            dto.setFromDate(startEndWeekUser.get(0));
            dto.setToDate(startEndWeekUser.get(1));
            dto.setTimeReport(null);
        }

        String sql = "select new com.globits.hr.dto.function.ProjectTimeWorkTableDto(entity) from ProjectStaff entity " +
                "where entity.staff.staffCode = :staffCode";
        Query query = manager.createQuery(sql);
        query.setParameter("staffCode", user.getUsername());
        List<ProjectTimeWorkTableDto> tableProjectTimeWork = query.getResultList();
        for (ProjectTimeWorkTableDto timeWork : tableProjectTimeWork) {
            dto.setProjectId(timeWork.getProjectId());
            timeWork.setTotalHours(reportService.getTotalHoursProjectStaff(staffDto.getId(), dto));
        }
        tableProjectTimeWork.removeIf(timeWork -> timeWork.getTotalHours() == 0);
        return new PageImpl<>(tableProjectTimeWork);
    }

    @Override
    public ProjectTimeWorkChartDto getChartTimeWork(SearchTotalTimeReportDto dto) {
        if (dto == null) {
            return null;
        }
        boolean isRoleUser = false;
        boolean isRoleAdmin = false;
        boolean isRoleManager = false;

        UserDto user = userExtService.getCurrentUser();
        if (user != null && user.getRoles() != null && user.getRoles().size() > 0) {
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

        int currentYear = LocalDate.now().getYear();
        if (dto.getYearReport() != null) {
            currentYear = dto.getYearReport();
        }

        int month;
        if (dto.getMonthReport() == null) {
            month = new Date(System.currentTimeMillis()).getMonth() + 1;
        } else {
            month = dto.getMonthReport();
        }

        List<String> horizontal = new ArrayList<>();
        Query query = null;
        String sql;

        if (isRoleAdmin || isRoleManager) {
            sql = "select new com.globits.hr.dto.function.ProjectTimeWorkTableDto(entity) from Project entity where YEAR(entity.createDate) = :yearReport ";
            query = manager.createQuery(sql);
            query.setParameter("yearReport", currentYear);
        }
        StaffDto staffDto = new StaffDto();
        if (isRoleUser) {
            sql = "select new com.globits.hr.dto.function.ProjectTimeWorkTableDto(entity) from ProjectStaff entity " +
                    "where entity.staff.staffCode = :staffCode and YEAR(entity.createDate) = :yearReport ";
            query = manager.createQuery(sql);
            query.setParameter("staffCode", user.getUsername());
            query.setParameter("yearReport", currentYear);
            staffDto = staffRepository.getByUsername(user.getUsername());
            dto.setStaffCode(user.getUsername());
        }
        if (query == null) {
            return null;
        }

        List<ProjectTimeWorkTableDto> tableProjectTimeWork = query.getResultList();
        if (tableProjectTimeWork == null) {
            return null;
        }

        if (dto.getTimeReport() == 2) {
            for (int i = 1; i <= 12; i++) {
                horizontal.add("Tháng " + i);
            }
            for (ProjectTimeWorkTableDto timeWork : tableProjectTimeWork) {
                dto.setProjectId(timeWork.getProjectId());
                List<Double> datas = new ArrayList<>();
                for (int i = 1; i <= 12; i++) {
                    dto.setMonthReport(i);
                    if (isRoleUser) {
                        datas.add(reportService.getTotalHoursProjectStaff(staffDto.getId(), dto));
                    } else {
                        datas.add(timeWorkProject(timeWork.getProjectStaff(), dto));
                    }
                }
                timeWork.setProjectStaff(null);
                timeWork.setData(datas);
            }
        }

        int maxNumberWeekOfMonth = DateTimeUtil.numberWeekOfMonth(month, currentYear);
        if (dto.getTimeReport() == 1) {
            dto.setTimeReport(null);
            for (int i = 1; i <= maxNumberWeekOfMonth; i++) {
                horizontal.add("Tuần " + i);
            }
            for (ProjectTimeWorkTableDto timeWork : tableProjectTimeWork) {
                dto.setProjectId(timeWork.getProjectId());
                List<Double> datas = new ArrayList<>();
                for (int i = 1; i <= maxNumberWeekOfMonth; i++) {
                    List<Date> startEndWeek = DateTimeUtil.getDateToDateOfWeek(i, month, currentYear);
                    dto.setFromDate(startEndWeek.get(0));
                    dto.setToDate(startEndWeek.get(1));
                    if (isRoleUser) {
                        datas.add(reportService.getTotalHoursProjectStaff(staffDto.getId(), dto));
                    } else {
                        datas.add(timeWorkProject(timeWork.getProjectStaff(), dto));
                    }
                }
                timeWork.setProjectStaff(null);
                timeWork.setData(datas);
            }
        }
        return new ProjectTimeWorkChartDto(horizontal, tableProjectTimeWork);
    }
}
