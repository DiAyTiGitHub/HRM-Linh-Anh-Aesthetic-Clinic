package com.globits.timesheet.service.impl;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.globits.hr.dto.search.ProjectActivitySearchDto;
import com.globits.timesheet.domain.Project;
import com.globits.timesheet.domain.ProjectActivity;
import com.globits.timesheet.dto.ProjectActivityDto;
import com.globits.timesheet.dto.ProjectDto;
import com.globits.timesheet.repository.ProjectActivityRepository;
import com.globits.timesheet.repository.ProjectRepository;
import com.globits.timesheet.service.ProjectActivityService;

@Service
public class ProjectActivityServiceImpl implements ProjectActivityService {
    @PersistenceContext
    EntityManager manager;
    @Autowired
    ProjectActivityRepository repos;
    @Autowired
    ProjectRepository projectRepos;

    @Override
    public ProjectActivityDto saveOrUpdate(UUID id, ProjectActivityDto dto) {
        if (dto != null) {
            ProjectActivity entity = null;
            if (dto.getId() != null) {
                if (!dto.getId().equals(id)) {
                    return null;
                }
                Optional<ProjectActivity> projectActivityOptional = repos.findById(id);
                if (projectActivityOptional.isPresent()) {
                    entity = projectActivityOptional.get();
                }
                // if (entity != null) {
                // entity.setModifyDate(LocalDateTime.now());
                // }
            }
            if (entity == null) {
                entity = new ProjectActivity();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
            }
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity.setStartTime(dto.getStartTime());
            entity.setEndTime(dto.getEndTime());
            entity.setEstimateDuration(dto.getEstimateDuration());

            if (dto.getProject() != null && dto.getProject().getId() != null) {
                Optional<Project> projectOptional = projectRepos.findById(dto.getProject().getId());
                if (projectOptional.isPresent()) {
                    Project project = projectOptional.get();
                    entity.setProject(project);
                }
            }

            if (dto.getParent() != null && dto.getParent().getId() != null) {
                Optional<ProjectActivity> optionalParent = repos.findById(dto.getParent().getId());
                if (optionalParent.isPresent()) {
                    ProjectActivity activity = optionalParent.get();
                    entity.setParent(activity);
                }
            }

            // Child
            if (dto.getChild() != null && dto.getChild().size() > 0) {
                Iterator<ProjectActivityDto> iters = dto.getChild().iterator();
                HashSet<ProjectActivity> activityChilds = new HashSet<ProjectActivity>();
                while (iters.hasNext()) {
                    ProjectActivityDto activityDto = iters.next();
                    ProjectActivity activityChild = null;
                    if (activityDto.getId() != null) {
                        Optional<ProjectActivity> optionalParent = repos.findById(activityDto.getId());
                        if (optionalParent.isPresent()) {
                            activityChild = optionalParent.get();
                        }
                    }
                    if (activityChild == null) {
                        activityChild = new ProjectActivity();
                    }
                    activityChild.setCode(activityDto.getCode());
                    activityChild.setName(activityDto.getName());
                    activityChild.setDescription(activityDto.getDescription());
                    activityChild.setProject(entity.getProject());
                    if (activityDto.getStartTime() != null) {
                        activityChild.setStartTime(activityDto.getStartTime());
                    }
                    if (activityDto.getEndTime() != null) {
                        activityChild.setEndTime(activityDto.getEndTime());
                    }
                    if (activityDto.getEstimateDuration() != null) {
                        activityChild.setEstimateDuration(activityDto.getEstimateDuration());
                    }
                    activityChild.setParent(entity);
                    activityChilds.add(activityChild);
                }
                if (entity.getChild() != null) {
                    entity.getChild().clear();
                    entity.getChild().addAll(activityChilds);
                } else {
                    entity.setChild(activityChilds);
                }
            }
            entity = repos.save(entity);
            return new ProjectActivityDto(entity);
        }
        return null;
    }

    @Override
    public Boolean delete(UUID id) {
        if (id != null) {
            repos.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Boolean voidProjectActivity(UUID id) {
        if (id == null) {
            return false;
        }
        ProjectActivity entity = this.getEntityById(id);
        if (entity == null) {
            return false;
        }
        entity.setVoided(true);
        try {
            repos.save(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ProjectActivityDto getProjectActivity(UUID id) {
        ProjectActivity entity = this.getEntityById(id);
        if (entity != null) {
            return new ProjectActivityDto(entity);
        }
        return null;
    }

    @Override
    public ProjectActivity getEntityById(UUID id) {
        ProjectActivity entity = null;
        Optional<ProjectActivity> projectOptional = repos.findById(id);
        if (projectOptional.isPresent()) {
            entity = projectOptional.get();
        }
        return entity;
    }

    @Override
    public Page<ProjectActivityDto> searchByPage(ProjectActivitySearchDto dto) {
        if (dto == null) {
            return null;
        }

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String whereClause = "";
        String orderBy = " ORDER BY entity.code";
        String sqlCount = "select count(entity.id) from ProjectActivity as entity where (1=1)   ";
        String sql = "select new com.globits.timesheet.dto.ProjectActivityDto(entity, false) from ProjectActivity as entity where (1=1)  ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text )";
        }
        if (dto.getProjectId() != null) {
            whereClause += " AND (entity.project.id =: projectId)";
        }

        if (dto.getIncludeVoided() != null) {
            whereClause += " AND ( entity.voided IS NULL OR entity.voided =:voided) ";
        }
        if (dto.getIncludeAll() != null && dto.getIncludeAll()) {
            //
        } else {
            whereClause += " AND ( entity.endTime IS NULL OR DATE(entity.endTime) >= DATE(:currentDate) ) ";
        }

        //handle for searching in range [fromDate, toDate]
        if (dto.getFromDate() != null) {
            whereClause += " AND DATE(entity.startTime) >= DATE(:fromDate) ";
        }
        if (dto.getToDate() != null) {
            whereClause += " and DATE(entity.endDTime) <= DATE(:toDate) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, ProjectActivityDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getProjectId() != null) {
            q.setParameter("projectId", dto.getProjectId());
            qCount.setParameter("projectId", dto.getProjectId());
        }
        if (dto.getIncludeVoided() != null) {
            q.setParameter("voided", dto.getIncludeVoided());
            qCount.setParameter("voided", dto.getIncludeVoided());
        }
        if (dto.getIncludeAll() != null && dto.getIncludeAll()) {
            //
        } else {
            Date today = new Date();
            q.setParameter("currentDate", today);
            qCount.setParameter("currentDate", today);
        }

        //handle for searching in range [fromDate, toDate]
        if (dto.getFromDate() != null) {
            q.setParameter("fromDate", dto.getFromDate());
            qCount.setParameter("fromDate", dto.getFromDate());
        }
        if (dto.getToDate() != null) {
            q.setParameter("toDate", dto.getToDate());
            qCount.setParameter("toDate", dto.getToDate());
        }

        Long count = (long) qCount.getSingleResult();
        int startPosition = 0;
        List<ProjectActivityDto> entities = null;
        Pageable pageable = null;
        if (dto.isDisablePaging()) {
            startPosition = 0;
            q.setFirstResult(startPosition);
            q.setMaxResults(count.intValue());
            entities = q.getResultList();
            pageable = PageRequest.of(0, count.intValue());
        } else {
            startPosition = pageIndex * pageSize;
            q.setFirstResult(startPosition);
            q.setMaxResults(pageSize);
            entities = q.getResultList();
            pageable = PageRequest.of(pageIndex, pageSize);
        }
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Long count = repos.checkCode(code.strip(), id);
            return count != 0L;
        }
        return null;
    }

    @Override
    public Boolean checkName(UUID id, String name) {
        if (name != "") {
            Long count = repos.checkName(id, name);
            return count != 0L;
        }
        return null;
    }

    @Override
    public List<ProjectActivityDto> getList(ProjectActivitySearchDto dto) {
        if (dto == null) {
            return null;
        }

        // String whereClause = "";
        String orderBy = " ORDER BY entity.code ASC";
        String sql = "select new com.globits.timesheet.dto.ProjectActivityDto(entity) from ProjectActivity as entity ";
        // String whereClause = "WHERE entity.parent is null";
        String whereClause = "WHERE (1=1)";
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text )";
        }
        if (dto.getProjectId() != null) {
            whereClause += " AND (entity.project.id =: projectId)";
        }
        if (dto.getIncludeVoided() != null && dto.getIncludeVoided().equals(true)) {
            whereClause += " ";
        } else {
            whereClause += " AND ( entity.voided IS NULL OR entity.voided = false) ";
        }
        if (dto.getIncludeAll() != null && dto.getIncludeAll()) {
            //
        } else {
            whereClause += " AND ( entity.endTime IS NULL OR DATE(entity.endTime) >= DATE(:currentDate) ) ";
        }

        //handle for searching in range [fromDate, toDate]
        if (dto.getFromDate() != null) {
            whereClause += " AND DATE(entity.startTime) >= DATE(:fromDate) ";
        }
        if (dto.getToDate() != null) {
            whereClause += " and DATE(entity.endTime) <= DATE(:toDate) ";
        }

        sql += whereClause + orderBy;

        Query q = manager.createQuery(sql, ProjectActivityDto.class);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getProjectId() != null) {
            q.setParameter("projectId", dto.getProjectId());
        }

        if (dto.getIncludeVoided() != null && dto.getIncludeVoided().equals(true)) {
            whereClause += " ";
        } else {
            whereClause += " AND ( entity.voided IS NULL OR entity.voided = false) ";
        }

        if (dto.getIncludeAll() != null && dto.getIncludeAll()) {
            //
        } else {
            Date today = new Date();
            q.setParameter("currentDate", today);
        }

        //handle for searching in range [fromDate, toDate]
        if (dto.getFromDate() != null) {
            q.setParameter("fromDate", dto.getFromDate());
        }
        if (dto.getToDate() != null) {
            q.setParameter("toDate", dto.getToDate());
        }

        List<ProjectActivityDto> entities = q.getResultList();
        return entities;
    }

    @Override
    public Boolean exportProject(UUID id, HttpServletResponse response) {
        List<ProjectActivityDto> list = new ArrayList<ProjectActivityDto>();
        list = repos.getListByProjectId(id);
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("Export_ProjectActivity.xlsx");
            Workbook workbook = new XSSFWorkbook(is);
            XSSFSheet worksheet = (XSSFSheet) workbook.getSheet("Project_Activity");
            int rowCount = 2;
            int columnCount = 0;
            XSSFRow row;
            XSSFCell cell;

            XSSFCellStyle styleCell = (XSSFCellStyle) workbook.getCellStyleAt(2);
            if (list != null) {
                for (ProjectActivityDto activityDto : list) {
                    rowCount++;
                    columnCount = 0;
                    row = worksheet.createRow(rowCount);

                    // Mã dự án
                    cell = row.createCell(columnCount++);
                    cell.setCellStyle(styleCell);
                    if (activityDto.getCode() != null) {
                        cell.setCellValue(activityDto.getCode());
                    }

                    // Tên dự án
                    cell = row.createCell(columnCount++);
                    cell.setCellStyle(styleCell);
                    if (activityDto.getName() != null) {
                        cell.setCellValue(activityDto.getName());
                    }

                    // Mô tả
                    cell = row.createCell(columnCount++);
                    cell.setCellStyle(styleCell);
                    if (activityDto.getDescription() != null) {
                        cell.setCellValue(activityDto.getDescription());
                    }
                }
            }

            workbook.write(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=Project_Activity.xlsx");
            response.flushBuffer();
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getCreate(String projectId, String parentId) {
        String projectCode = "";
        ProjectActivityDto projectActivityDto = new ProjectActivityDto();
        Optional<Project> projectOptional = projectRepos.findById(UUID.fromString(projectId));
        if (projectOptional.isPresent()) {
            projectCode = projectOptional.get().getCode();
            String sqlCount = "select count(entity.id) from ProjectActivity as entity where entity.project.id =:projectId ";
            if (parentId != null) {
                sqlCount += " and entity.parent.id =:parentId";
            } else {
                sqlCount += " and entity.parent.id is null";
            }
            Query q = manager.createQuery(sqlCount);
            if (parentId != null) {
                q.setParameter("projectId", UUID.fromString(projectId));
                q.setParameter("parentId", UUID.fromString(parentId));
            } else {
                q.setParameter("projectId", UUID.fromString(projectId));
            }
            long count = (long) q.getSingleResult() + 1;
            String projectCodeTmp = projectCode + "." + count;
            Long checkCode = repos.checkCodeByParentNull(projectCodeTmp, projectOptional.get().getId());
            while (true) {
                count += 1;
                projectCodeTmp = projectCode + "." + count;
                checkCode = repos.checkCodeByParentNull(projectCodeTmp, projectOptional.get().getId());
                if (checkCode == 0) {
                    break;
                }
            }
            projectCode = projectCodeTmp;
            projectActivityDto.setProject(new ProjectDto(projectOptional.get(), true));
            if (parentId != null) {
                Optional<ProjectActivity> projectActivity = repos.findById(UUID.fromString(parentId));
                if (projectActivity.isPresent()) {
                    projectCode = projectActivity.get().getCode();
                    projectCodeTmp = projectCode + "." + count;
                    checkCode = repos.checkCodeByParentNotNull(projectCodeTmp, projectActivity.get().getId());
                    while (true) {
                        count += 1;
                        projectCodeTmp = projectCode + "." + count;
                        checkCode = repos.checkCodeByParentNotNull(projectCodeTmp, projectActivity.get().getId());
                        if (checkCode == 0) {
                            break;
                        }
                    }

                    projectCode = projectCodeTmp;
                    ProjectActivity entity = projectActivity.get();
                    projectActivityDto.setParent(new ProjectActivityDto(entity, false));
                    projectActivityDto.setParentId(entity.getId());
                }
            }
        }
        projectActivityDto.setCode(projectCode);
        return projectActivityDto.getCode();
    }

    public String AutoGenerateProjectActivityCode(String projectCode) {
        String codeGenerated = projectCode;
        return codeGenerated;
    }

    @Override
    public Set<UUID> getChildrenActivityIdsByParentActivityId(UUID parentId) {
        Set<UUID> result = new HashSet<>();

        if (parentId == null) return null;
        List<UUID> childrenIds = repos.getListChildrenActivityIdsByParentId(parentId);
        result.addAll(childrenIds);

        //get children ids of children
        for (UUID childId : childrenIds) {
            Set<UUID> childrenOfChild = this.getChildrenActivityIdsByParentActivityId(childId);
            result.addAll(childrenOfChild);
        }

        return result;
    }
}
