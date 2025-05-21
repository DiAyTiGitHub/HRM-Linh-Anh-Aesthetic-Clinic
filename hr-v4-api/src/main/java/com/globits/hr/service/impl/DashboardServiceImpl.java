package com.globits.hr.service.impl;

import com.globits.hr.dto.function.DashboardDto;
import com.globits.hr.dto.search.DashboardSearchDto;
import com.globits.hr.service.DashboardService;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class DashboardServiceImpl implements DashboardService {

    @PersistenceContext
    EntityManager manager;

    @Override
    public DashboardDto getDashboard(DashboardSearchDto search) {
        DashboardDto dashboardDto = new DashboardDto();
        dashboardDto.setStaffNumber(this.getWorkingStaff());
        dashboardDto.setProjectNumber(this.getIncompleteProjects());
        dashboardDto.setMonthTaskNumber(this.getMonthTasks());

        return dashboardDto;
    }

    // lấy ra tất cả nhân viên đang làm việc
    public Long getWorkingStaff() {
        String whereClause = "and entity.status.name = :statusName";
        String sql = "select count(entity.id) from Staff as entity where (1=1) ";
        sql += whereClause;
        Query q = manager.createQuery(sql);
        q.setParameter("statusName", "Đang làm việc");
        return (Long) q.getSingleResult();
    }
    public Long getTotalStaff() {
        String whereClause = " ";
        String sql = "select count(entity.id) from Staff as entity where (1=1) ";
        sql += whereClause;
        Query q = manager.createQuery(sql);
        return (Long) q.getSingleResult();
    }

    public Long getTotalProject() {
        String whereClause = "";
        String sql = "select count(entity.id) from Project as entity where (1=1) ";
        sql += whereClause;
        Query q = manager.createQuery(sql);
        return (Long) q.getSingleResult();
    }
    //lay ra những project chưa hoàn thành
    public Long getIncompleteProjects() {
        String whereClause = "And entity.isFinished is null or entity.isFinished=false ";
        String sql = "select count(entity.id) from Project as entity where (1=1) ";
        sql += whereClause;
        Query q = manager.createQuery(sql);
        return (Long) q.getSingleResult();
    }

    public Long getMonthTasks() {
        String sql = "select count(entity.id) from TimeSheet as entity where (1=1)  ";
        String whereClause = "AND MONTH(entity.createDate) = MONTH(CURRENT_DATE)";
        sql += whereClause;
        Query q = manager.createQuery(sql);
        return (Long) q.getSingleResult();
    }

}
