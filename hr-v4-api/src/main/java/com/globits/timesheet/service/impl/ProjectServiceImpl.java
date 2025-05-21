package com.globits.timesheet.service.impl;

import com.globits.hr.HrConstants;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchProjectDto;
import com.globits.hr.service.StaffService;
import com.globits.hr.service.UserExtService;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.timesheet.domain.Label;
import com.globits.timesheet.domain.Project;
import com.globits.timesheet.domain.ProjectStaff;
import com.globits.timesheet.dto.LabelDto;
import com.globits.timesheet.dto.ProjectDto;
import com.globits.timesheet.repository.LabelRepository;
import com.globits.timesheet.repository.ProjectRepository;
import com.globits.timesheet.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProjectServiceImpl implements ProjectService {
    @PersistenceContext
    EntityManager manager;
    @Autowired
    ProjectRepository repos;
    @Autowired
    private UserExtService userExtService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private LabelRepository labelRepository;

    @Override
    @Transactional
    @Modifying
    public ProjectDto saveOrUpdate(UUID id, ProjectDto dto) {
        if (dto != null && dto.getCode() != null && StringUtils.hasText(dto.getCode())) {
            Project entity = null;

            if (id != null && dto.getId() != null && !dto.getId().equals(id)) {
                return null;
            }
            if (id != null) {
                entity = this.getEntityById(id);
            }
            if (entity == null && dto.getId() != null) {
                entity = this.getEntityById(dto.getId());
            }

            if (entity != null) {
                entity.setModifyDate(LocalDateTime.now());
            }

            if (entity == null) {
                entity = new Project();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
            }
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity.setWorkload(dto.getWorkload());
            entity.setActualWorkload(dto.getActualWorkload());
            entity.setStartDate(dto.getStartDate());
            entity.setEndDate(dto.getEndDate());
            entity.setFinished(dto.isFinished());

            // old logic to save staffs in project
            // HashMap<UUID, StaffDto> staffHashMap = new HashMap<UUID, StaffDto>();
            // if (dto.getProjectStaff() != null && dto.getProjectStaff().size() > 0) {
            // for (StaffDto newStaff : dto.getProjectStaff()) {
            // staffHashMap.put(newStaff.getId(), newStaff);
            // }
            // }
            // List<UUID> newStaffIdList =
            // staffHashMap.keySet().stream().collect(Collectors.toList());
            // Set<ProjectStaff> projectStaffList = new HashSet<>();
            // if (entity.getProjectStaff() != null && entity.getProjectStaff().size() > 0)
            // {
            // projectStaffList = entity.getProjectStaff();
            // for (ProjectStaff projectStaff : projectStaffList) {
            // Staff oldStaff = projectStaff.getStaff();
            // if (dto.getProjectStaff() != null && dto.getProjectStaff().size() > 0) {
            // for (StaffDto newStaff : dto.getProjectStaff()) {
            // if (oldStaff.getId().equals(newStaff.getId())) {
            // System.out.println("Nhan vien:" + newStaff.getId() + " da ton tai");
            // staffHashMap.remove(newStaff.getId());
            // continue;
            // }
            // }
            // // check xoa
            // if (!newStaffIdList.contains(oldStaff.getId())) {
            // // xoa oldStaff
            // System.out.println("xoa Nhan vien:" + oldStaff.getDisplayName() + " khoi
            // task");
            // entity.getProjectStaff().remove(projectStaff);
            // }
            // }
            // }
            // }
            //
            // if (!staffHashMap.isEmpty()) {
            // List<StaffDto> newStaffList =
            // staffHashMap.values().stream().collect(Collectors.toList());
            // if (newStaffList != null && newStaffList.size() > 0) {
            // Staff staff = null;
            //
            // Set<ProjectStaff> projectStaffSet = new HashSet<>();
            // if (entity.getProjectStaff() != null) {
            // projectStaffSet = entity.getProjectStaff();
            // }
            // for (StaffDto staffDto : newStaffList) {
            // Staff optional = staffService.getEntityById(staffDto.getId());
            // if (optional != null) {
            // staff = optional;
            // ProjectStaff projectStaff = new ProjectStaff();
            // projectStaff.setStaff(staff);
            // projectStaff.setProject(entity);
            // if (projectStaff != null) {
            // projectStaffSet.add(projectStaff);
            // }
            // entity.setProjectStaff(projectStaffSet);
            // }
            // }
            // }
            // }

            // new logic to handle project-staff written by diayti - time complexity O(n)
            if (dto.getProjectStaff() != null) {
                Set<UUID> joiningStaffIds = new HashSet<>();
                // first, get ids of staffs joining project
                for (StaffDto staff : dto.getProjectStaff()) {
                    joiningStaffIds.add(staff.getId());
                }
                // then initialize upcoming saving data for this field
                Set<ProjectStaff> newProjectStaffs = new HashSet<>();

                if (entity.getProjectStaff() != null && entity.getProjectStaff().size() != 0)
                    for (ProjectStaff projectStaff : entity.getProjectStaff()) {
                        if (joiningStaffIds.contains(projectStaff.getStaff().getId())) {
                            // add projectStaff existed into the new list
                            projectStaff.setVoided(null);
                            newProjectStaffs.add(projectStaff);
                            // remove id of staff which is handled
                            joiningStaffIds.remove(projectStaff.getStaff().getId());
                        } else {
                            // remove projectStaff isn't existed in the joining ids set => voided = true
                            projectStaff.setVoided(true);
                            newProjectStaffs.add(projectStaff);
                        }
                    }

                // in this section, the remain ids is new staff which isn't has projectStaff
                // record => create it
                for (UUID staffId : joiningStaffIds) {
                    Staff onJoiningStaff = staffService.getEntityById(staffId);
                    if (onJoiningStaff == null) {
                        System.out.println("Invalid staff id: " + staffId
                                + " is requested to add into this project: projectId: " + entity.getId());
                        continue;
                    }

                    ProjectStaff newProjectStaff = new ProjectStaff();
                    newProjectStaff.setProject(entity);
                    newProjectStaff.setStaff(onJoiningStaff);

                    // add new relationship to new collection of projectStaffSet
                    newProjectStaffs.add(newProjectStaff);

                }

                // final save this to project entity
                if (entity.getProjectStaff() == null)
                    entity.setProjectStaff(new HashSet<>());
                entity.getProjectStaff().clear();
                entity.getProjectStaff().addAll(newProjectStaffs);
            }

            Set<Label> labels = new HashSet<>();
            if (dto.getLabels() != null && !dto.getLabels().isEmpty()) {
                for (LabelDto labelDto : dto.getLabels()) {
                    Label label = null;
                    if (labelDto != null && labelDto.getId() != null) {
                        label = labelRepository.getOne(labelDto.getId());
                    }
                    if (label == null) {
                        label = new Label();
                        label.setProject(entity);
                    }
                    if (labelDto != null) {
                        label = labelDto.toEntity(labelDto, label);
                        label.setProject(entity);
                    }
                    labels.add(label);
                }
            }
            if (entity.getLabels() != null) {
                entity.getLabels().clear();
                entity.getLabels().addAll(labels);
            } else {
                entity.setLabels(labels);
            }
            entity = repos.save(entity);
            return new ProjectDto(entity);
        }
        return null;
    }

    @Override
    @Transactional
    @Modifying
    public Boolean delete(UUID projectId) {
        if (projectId == null)
            return false;
        Project onDeleteProject = repos.findById(projectId).orElse(null);
        if (onDeleteProject == null)
            return false;

        onDeleteProject.setVoided(true);
        repos.save(onDeleteProject);

        return true;
    }

    @Override
    public ProjectDto getProject(UUID id) {
        Project entity = this.getEntityById(id);
        if (entity != null) {
            return new ProjectDto(entity);
        }
        return null;
    }

    @Override
    public Project getEntityById(UUID id) {
        Project entity = null;
        Optional<Project> projectOptional = repos.findById(id);
        if (projectOptional.isPresent()) {
            entity = projectOptional.get();
        }
        return entity;
    }

    @Override
    public Page<ProjectDto> searchByPage(SearchProjectDto dto) {
        if (dto == null) {
            return null;
        }

        boolean isRoleUser = false;
        boolean isRoleAdmin = false;
        boolean isRoleManager = false;
        boolean isRoleTester = false;
        UserDto user = userExtService.getCurrentUser();
        if (user != null && user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (RoleDto item : user.getRoles()) {
                if (item.getName() != null && ("ROLE_ADMIN".equals(item.getName()))) {
                    isRoleAdmin = true;
                }
                if (item.getName() != null && "HR_MANAGER".equals(item.getName())) {
                    isRoleManager = true;
                }
                if (item.getName() != null
                        && (HrConstants.HR_USER.equals(item.getName()) || HrConstants.ROLE_USER.equals(item.getName()))) {
                    isRoleUser = true;
                }
                if (item.getName() != null && HrConstants.HR_TESTER.equals(item.getName())) {
                    isRoleTester = true;
                }
            }
        }
        StaffDto staffDto = userExtService.getCurrentStaff();
        if (isRoleAdmin) {
            isRoleManager = false;
            isRoleUser = false;
            isRoleTester = false;
        } else if (isRoleManager) {
            isRoleUser = false;
            isRoleTester = false;
        }

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String whereClause = "where (1=1) and (p.voided is null or p.voided = false) ";
        String orderBy = " ORDER BY p.createDate DESC";
        String sqlCount = "select count(p.id) from Project as p ";
        String sql = "select new com.globits.timesheet.dto.ProjectDto(p, true) from Project as p ";

        if (((isRoleUser || isRoleTester) && user != null && user.getId() != null)) {
            sql += "JOIN ProjectStaff as ps on ps.project.id = p.id ";
            sqlCount += "JOIN ProjectStaff as ps on ps.project.id = p.id ";
            whereClause += "AND ps.staff.id = :staffId and (ps.voided is null or ps.voided = false) ";
        }

        if (dto.getIsFinished() != null && dto.getIsFinished().equals(true)) {
            //filter projects are finished
            whereClause += " and (p.isFinished is not null and p.isFinished = true) ";
        } else {
            //filter projects are NOT finished
            whereClause += " and (p.isFinished is null or p.isFinished = false) ";
        }

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += "AND ( p.name LIKE :text OR p.code LIKE :text ) ";
        }

        if (dto.getStartDate() != null) {
            whereClause += " and p.startDate >= :startDate ";
        }

        if (dto.getEndDate() != null) {
            whereClause += " and p.endDate <= :endDate ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, ProjectDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if ((isRoleUser || isRoleTester) && staffDto != null) {
            q.setParameter("staffId", staffDto.getId());
            qCount.setParameter("staffId", staffDto.getId());
        }

        if (dto.getStartDate() != null) {
            q.setParameter("startDate", dto.getStartDate());
            qCount.setParameter("startDate", dto.getStartDate());
        }

        if (dto.getEndDate() != null) {
            q.setParameter("endDate", dto.getEndDate());
            qCount.setParameter("endDate", dto.getEndDate());
        }

        // if ((isRoleAdmin || isRoleManager) && dto.getStaffId() != null) {
        // q.setParameter("staffId", dto.getStaffId());
        // qCount.setParameter("staffId", dto.getStaffId());
        // }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<ProjectDto> entities = q.getResultList();
        if (entities == null || entities.size() == 0) {
            return null;
        }
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
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
        if (name != null) {
            Long count = repos.checkName(name, id);
            return count != 0L;
        }
        return null;
    }

}
