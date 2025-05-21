package com.globits.hr.service.impl;

import com.globits.core.dto.SearchDto;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.DepartmentResourcePlanDto;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.HrResourcePlanDto;
import com.globits.hr.dto.HrResourcePlanItemDto;
import com.globits.hr.dto.PositionTitleResourcePlanDto;
import com.globits.hr.dto.RecruitmentRequestDto;
import com.globits.hr.dto.SearchHrResourcePlanDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchHrDepartmentDto;
import com.globits.hr.dto.search.SearchRecruitmentDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.*;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.hr.utils.RoleUtils;
import com.globits.salary.domain.SalaryResultStaff;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import com.globits.salary.service.impl.SalaryResultStaffServiceImpl;
import com.globits.security.domain.Role;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;

import jakarta.persistence.Query;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HrResourcePlanServiceImpl extends GenericServiceImpl<HrResourcePlan, UUID> implements HrResourcePlanService {
    private static final Logger logger = LoggerFactory.getLogger(SalaryResultStaffServiceImpl.class);

    @Autowired
    private HrResourcePlanRepository hrResourcePlanRepository;

    @Autowired
    private PositionTitleRepository positionTitleRepository;

    @Autowired
    private HRDepartmentRepository hrDepartmentRepository;

    @Autowired
    private HrResourcePlanItemRepository hrResourcePlanItemRepository;

    @Autowired
    private UserExtService userExtService;

    @Autowired
    private StaffHierarchyService staffHierarchyService;

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private HRDepartmentService hRDepartmentService;


    @Override
    public Page<HrResourcePlanDto> searchByPage(SearchHrResourcePlanDto dto) {
        if (dto == null) {
            return null;
        }

        formalizeSearchObject(dto);

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        UserDto userDto = userExtService.getCurrentUser();
        Staff staff = userExtService.getCurrentStaffEntity();
        boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
        boolean isManager = RoleUtils.hasRoleHrManager(userDto);
//        boolean isAssignment = RoleUtils.hasRoleHrAssignment(userDto);
//        boolean isShiftAssignment = RoleUtils.hasShiftAssignmentPermission(userDto, staff);
        boolean isStaffView = RoleUtils.hasRoleHrStaffView(userDto);
        boolean isPositionManager = RoleUtils.isHeadOfDepartment(staff);

        if (!(isAdmin || isManager || isStaffView)) {
            if (isPositionManager && staff != null) {
                List<UUID> departmentIdList = staffHierarchyService.getAllManagedAndSubDepartmentIdsByStaff(staff.getId());
                dto.setDepartmentIdList(departmentIdList);
            }
        }

        String whereClause = " where (1=1) and (entity.voided = false or entity.voided is null)  ";
        String orderBy = " ORDER BY entity.modifyDate desc ";

        String sqlCount = "select count(distinct entity.id) from HrResourcePlan as entity ";
        String sql = "select distinct new com.globits.hr.dto.HrResourcePlanDto(entity) from HrResourcePlan as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.code LIKE :text OR entity.name LIKE :text ) ";
        }

        if (dto.getOrganizationId() != null) {
            whereClause += " and entity.department.organization.id = :organizationId ";
        }

        if (dto.getDepartmentId() != null) {
            whereClause += " and entity.department.id = :departmentId ";
        }
        if (dto.getDepartmentIdList() != null && dto.getDepartmentIdList().size() > 0) {
            whereClause += " and entity.department.id IN (:departmentIdList) ";
        }

        if (dto.getFromDate() != null) {
            whereClause += " and entity.planDate >= :fromDate ";
        }

        if (dto.getToDate() != null) {
            whereClause += " and entity.planDate <= :toDate ";
        }

        // null la lay het
        // false lay co department
        // true lay khong co department
//        if (dto.isGetAggregate() != null && dto.isGetAggregate()) {
//            whereClause += " and entity.department is null ";
//        } else if (dto.isGetAggregate() != null && !dto.isGetAggregate()) {
//            whereClause += " and entity.department is not null ";
//        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, RecruitmentRequestDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        if (dto.getOrganizationId() != null) {
            query.setParameter("organizationId", dto.getOrganizationId());
            qCount.setParameter("organizationId", dto.getOrganizationId());
        }

        if (dto.getDepartmentId() != null) {
            query.setParameter("departmentId", dto.getDepartmentId());
            qCount.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getDepartmentIdList() != null && dto.getDepartmentIdList().size() > 0) {
            query.setParameter("departmentIdList", dto.getDepartmentIdList());
            qCount.setParameter("departmentIdList", dto.getDepartmentIdList());
        }

        if (dto.getFromDate() != null) {
            query.setParameter("fromDate", dto.getFromDate());
            qCount.setParameter("fromDate", dto.getFromDate());
        }

        if (dto.getToDate() != null) {
            query.setParameter("toDate", dto.getToDate());
            qCount.setParameter("toDate", dto.getToDate());
        }


        long count = (long) qCount.getSingleResult();

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<HrResourcePlanDto> entities = query.getResultList();
        Page<HrResourcePlanDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public Boolean isValidCode(HrResourcePlanDto dto) {
        if (dto == null) return false;

        // ID of HrResourcePlan is null => Create new HrResourcePlan
        // => Assure that there's no other HrResourcePlans using this code of new HrResourcePlan
        // if there was any HrResourcePlan using new HrResourcePlan's code, then this new code is
        // invalid => return False
        // else return true
        if (dto.getId() == null) {
            List<HrResourcePlan> entities = hrResourcePlanRepository.findByCode(dto.getCode());
            if (entities == null || entities.size() == 0) {
                return true;
            }
            return false;

        }
        // ID of HrResourcePlan is NOT null => HrResourcePlan is modified
        // => Assure that the modified code is not same to OTHER any HrResourcePlans' code
        // if there was any HrResourcePlan using new HrResourcePlan's code, then this new code is
        // invalid => return False
        // else return true
        else {
            List<HrResourcePlan> entities = hrResourcePlanRepository.findByCode(dto.getCode());
            if (entities == null || entities.size() == 0) {
                return true;
            }
            for (HrResourcePlan entity : entities) {
                if (!entity.getId().equals(dto.getId())) return false;
            }
        }
        return true;
    }

    @Override
    public HrResourcePlanDto getById(UUID id) {
        HrResourcePlan entity = hrResourcePlanRepository.findById(id).orElse(null);
        if (entity == null) return null;
        return new HrResourcePlanDto(entity);
    }

    @Override
    public HrResourcePlanDto saveOrUpdate(HrResourcePlanDto dto) {
        if (dto == null) {
            return null;
        }

        HrResourcePlan entity = null;
        if (dto.getId() != null) entity = hrResourcePlanRepository.findById(dto.getId()).orElse(null);
        if (entity == null) {
            entity = new HrResourcePlan();

            Staff currentStaff = userExtService.getCurrentStaffEntity();
            entity.setRequester(currentStaff);
        }

        if (dto.getDepartment() != null && dto.getDepartment().getId() != null) {
            HRDepartment department = hrDepartmentRepository.findById(dto.getDepartment().getId()).orElse(null);
            entity.setDepartment(department);
        } else {
            entity.setDepartment(null);
        }


        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCode(dto.getCode());
        entity.setPlanDate(dto.getPlanDate());

        Set<HrResourcePlanItem> items = new HashSet<>();
        if (dto.getResourcePlanItems() != null) {
            for (HrResourcePlanItemDto itemDto : dto.getResourcePlanItems()) {
                HrResourcePlanItem item = null;
                if (itemDto.getId() != null) {
                    item = hrResourcePlanItemRepository.findById(itemDto.getId()).orElse(null);
                }
                if (item == null) {
                    item = new HrResourcePlanItem();
                }

                item.setResourcePlan(entity);

                item.setCurrentPositionNumber(itemDto.getCurrentPositionNumber()); // số lượng định biên (tất cả position của chức danh và phòng ban đó)
                item.setCurrentStaffNumber(itemDto.getCurrentStaffNumber()); //số lượng thực tế (tất cả position có staff của chức danh và phòng ban đó)
                item.setEliminatePlanNumber(itemDto.getEliminatePlanNumber()); //số lượng cần lọc
                item.setAdditionalNumber(itemDto.getAdditionalNumber()); //số lượng cần bổ sung (số lượng định biên - số lượng thực tế)

                if (itemDto.getPositionTitle() != null && itemDto.getPositionTitle().getId() != null) {
                    PositionTitle positionTitle = positionTitleRepository.findById(itemDto.getPositionTitle().getId()).orElse(null);
                    item.setPositionTitle(positionTitle);
                } else {
                    return null;
                }

                items.add(item);
            }

        }

        if (entity.getResourcePlanItems() == null) entity.setResourcePlanItems(items);
        else {
            entity.getResourcePlanItems().clear();
            entity.getResourcePlanItems().addAll(items);
        }
        if (entity.getChildrenPlans() == null) {
            entity.setChildrenPlans(new HashSet<>());
        } else {
            entity.getChildrenPlans().clear();
        }
        if (dto.getChildrenPlans() != null) {
            for (HrResourcePlanDto item : dto.getChildrenPlans()) {
                if (item.getId() != null) {
                    HrResourcePlan child = hrResourcePlanRepository.findById(item.getId()).orElse(null);
                    if (child == null) {
                        continue;
                    }
                    child.setParentPlan(entity);
                    entity.getChildrenPlans().add(child);
                }
            }
        }

        entity = hrResourcePlanRepository.save(entity);

        return new HrResourcePlanDto(entity);
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteById(UUID id) {
        if (id == null) return false;

        HrResourcePlan hrResourcePlan = hrResourcePlanRepository.findById(id).orElse(null);
        if (hrResourcePlan == null) return false;

        hrResourcePlanRepository.delete(hrResourcePlan);

        return true;
    }

    @Override
    @Modifying
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID id : ids) {
            boolean deleteRes = this.deleteById(id);
            if (!deleteRes) isValid = false;
        }
        return isValid;
    }

    private void formalizeSearchObject(SearchHrResourcePlanDto dto) {
        if (dto.getFromDate() != null) {
            dto.setFromDate(DateTimeUtil.getStartOfDay(dto.getFromDate()));
        }
        if (dto.getToDate() != null) {
            dto.setToDate(DateTimeUtil.getEndOfDay(dto.getToDate()));
        }
    }



    private List<PositionTitleResourcePlanDto> getPositionTitleResourcePlan(SearchHrResourcePlanDto dto) {
        if (dto == null) {
            return null;
        }
        UserDto userDto = userExtService.getCurrentUser();
        Staff staff = userExtService.getCurrentStaffEntity();
        boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
        boolean isManager = RoleUtils.hasRoleHrManager(userDto);
        boolean isStaffView = RoleUtils.hasRoleHrStaffView(userDto);
        boolean isPositionManager = RoleUtils.isHeadOfDepartment(staff);
        if (!(isAdmin || isManager || isStaffView)) {
            if (isPositionManager && staff != null && dto.getDepartmentIdList() == null) {
                List<UUID> departmentIdList = staffHierarchyService.getAllManagedAndSubDepartmentIdsByStaff(staff.getId());
                dto.setDepartmentIdList(departmentIdList);
            }
        }
//        String sql = " SELECT \r\n"
//                + "    d.id AS departmentId,\r\n"
//                + "    d.name AS departmentName,\r\n"
//                + "    pt.id AS positionTitleId,\r\n"
//                + "    pt.name AS positionTitleName,\r\n"
//                + "    COUNT(p.id) AS nominalQuantity,\r\n"
//                + "    COUNT(CASE WHEN p.staff_id IS NOT NULL THEN 1 END) AS actualQuantity,\r\n"
//
//                + " CASE "
//                + "    WHEN COUNT(p.id) > COUNT(CASE WHEN p.staff_id IS NOT NULL THEN 1 END) "
//                + "    THEN COUNT(p.id) - COUNT(CASE WHEN p.staff_id IS NOT NULL THEN 1 END) "
//                + "    ELSE 0 "
//                + " END AS supplementaryQuantity "
//                + " FROM tbl_department d\r\n"
//                + " LEFT JOIN tbl_hr_department_position dp ON dp.department_id = d.id  AND (dp.voided IS NULL OR dp.voided=FALSE)\r\n"
//                + " LEFT JOIN tbl_position_title pt ON pt.id = dp.position_title_id AND (pt.voided IS NULL OR pt.voided=FALSE)\r\n"
//                + " LEFT JOIN tbl_position p ON p.title_id = pt.id  AND p.department_id = dp.department_id AND (p.voided IS NULL OR p.voided=FALSE) ";

//        String sql = "SELECT\n"
//                + "    d.id AS departmentId,\n"
//                + "    d.name AS departmentName,\n"
//                + "    pt.id AS positionTitleId,\n"
//                + "    pt.name AS positionTitleName,\n"
//                + "    COUNT(p.id) AS nominalQuantity,\n"
//                + "    COUNT(CASE WHEN p.staff_id IS NOT NULL THEN 1 END) AS actualQuantity,\n"
//                + "    CASE\n"
//                + "        WHEN COUNT(p.id) > COUNT(CASE WHEN p.staff_id IS NOT NULL THEN 1 END)\n"
//                + "        THEN COUNT(p.id) - COUNT(CASE WHEN p.staff_id IS NOT NULL THEN 1 END)\n"
//                + "        ELSE 0\n" + "    END AS supplementaryQuantity,\n"
//                + "    IFNULL(MAX(rpi.eliminate_plan_number), 0) AS filteredQuantity \n"
//                + "FROM tbl_department d\n" + "LEFT JOIN tbl_hr_department_position dp \n"
//                + "    ON dp.department_id = d.id \n"
//                + "    AND (dp.voided IS NULL OR dp.voided = FALSE)\n"
//                + "LEFT JOIN tbl_position_title pt \n"
//                + "    ON pt.id = dp.position_title_id \n"
//                + "    AND (pt.voided IS NULL OR pt.voided = FALSE)\n"
//                + "LEFT JOIN tbl_position p \n"
//                + "    ON p.title_id = pt.id \n"
//                + "    AND p.department_id = dp.department_id \n"
//                + "    AND (p.voided IS NULL OR p.voided = FALSE)\n"
//                + "LEFT JOIN tbl_hr_resource_plan rp \n"
//                + "    ON rp.department_id = d.id \n"
//                + "    AND (rp.voided IS NULL OR rp.voided = FALSE)\n"
//                + "    AND rp.plan_date = (\n"
//                + "        SELECT MAX(srp.plan_date)\n"
//                + "        FROM tbl_hr_resource_plan srp\n"
//                + "        INNER JOIN tbl_hr_resource_plan_item srpi ON srpi.resource_plan_id = srp.id\n"
//                + "        WHERE srp.department_id = d.id AND srpi.position_title_id = pt.id \n"
//                + "        AND srp.vice_general_director_status = 2 AND srp.general_director_status = 2 \n"
//                + "    )\n"
//                + "LEFT JOIN tbl_hr_resource_plan_item rpi \n"
//                + "    ON rpi.resource_plan_id = rp.id \n"
//                + "    AND rpi.position_title_id = dp.position_title_id\n";


        String sql = "SELECT\n" +
                "    d.id AS departmentId,\n" +
                "    d.name AS departmentName,\n" +
                "    pt.id AS positionTitleId,\n" +
                "    pt.name AS positionTitleName,\n" +
                "\n" +
                "    -- nominalQuantity = total - temporary\n" +
                "    GREATEST(\n" +
                "        COUNT(p.id) - COUNT(CASE WHEN p.is_temporary = TRUE THEN 1 END),\n" +
                "        0\n" +
                "    ) AS nominalQuantity,\n" +
                "\n" +
                "    -- actualQuantity = total staffed - concurrent\n" +
                "    GREATEST(\n" +
                "        COUNT(CASE WHEN p.staff_id IS NOT NULL THEN 1 END)\n" +
                "        - COUNT(CASE WHEN p.staff_id IS NOT NULL AND p.is_concurrent = TRUE THEN 1 END),\n" +
                "        0\n" +
                "    ) AS actualQuantity,\n" +
                "\n" +
                "    -- supplementaryQuantity = nominal - actual + concurrent\n" +
                "    GREATEST(\n" +
                "        (COUNT(p.id) - COUNT(CASE WHEN p.is_temporary = TRUE THEN 1 END))\n" +
                "        - (COUNT(CASE WHEN p.staff_id IS NOT NULL THEN 1 END)\n" +
                "        - COUNT(CASE WHEN p.staff_id IS NOT NULL AND p.is_concurrent = TRUE THEN 1 END)),\n" +
                "        0\n" +
                "    )  AS supplementaryQuantity,\n" +
                "\n" +
                "    -- filteredQuantity = temporary\n" +
                "    COUNT(CASE WHEN p.is_temporary = TRUE THEN 1 END) AS filteredQuantity\n" +
                "\n" +
                "FROM tbl_department d\n" +
                "LEFT JOIN tbl_hr_department_position dp \n" +
                "    ON dp.department_id = d.id \n" +
                "    AND (dp.voided IS NULL OR dp.voided = FALSE)\n" +
                "LEFT JOIN tbl_position_title pt \n" +
                "    ON pt.id = dp.position_title_id \n" +
                "    AND (pt.voided IS NULL OR pt.voided = FALSE)\n" +
                "LEFT JOIN tbl_position p \n" +
                "    ON p.title_id = pt.id \n" +
                "    AND p.department_id = dp.department_id \n" +
                "    AND (p.voided IS NULL OR p.voided = FALSE)";


        String whereClause = " WHERE (1=1) AND (d.voided IS NULL OR d.voided=FALSE)";
//        String groupBy = " GROUP BY d.id, d.name, pt.id, pt.name";
        String groupBy = "GROUP BY d.id, d.name, pt.id, pt.name ";
        String orderBy = " ORDER BY d.name, pt.name";

        if (dto.getPositionTitleId() != null) {
            whereClause += " AND pt.id= :positionTitleId ";
        }
        if (dto.getDepartmentId() != null) {
            whereClause += " AND d.id= :departmentId ";
        }
        if (dto.getDepartmentIdList() != null && !dto.getDepartmentIdList().isEmpty()) {
            whereClause += " AND d.id IN (:departmentIds) ";
        }
        if (dto.getOrganizationId() != null) {
            whereClause += " AND d.organization_id= :organizationId ";
        }

        String query = sql + whereClause + groupBy + orderBy;
        NativeQuery q = (NativeQuery) manager.createNativeQuery(query);

        if (dto.getPositionTitleId() != null) {
            q.setParameter("positionTitleId", dto.getPositionTitleId().toString());
        }

        if (dto.getDepartmentId() != null) {
            q.setParameter("departmentId", dto.getDepartmentId().toString());
        }

        if (dto.getDepartmentIdList() != null && !dto.getDepartmentIdList().isEmpty()) {
            List<String> ids = new ArrayList<String>();
            for (UUID id : dto.getDepartmentIdList()) {
                ids.add(id.toString());
            }
            q.setParameter("departmentIds", ids);
        }

        if (dto.getOrganizationId() != null) {
            q.setParameter("organizationId", dto.getOrganizationId().toString());
        }

        q.addScalar("departmentId", StandardBasicTypes.UUID_CHAR);
        q.addScalar("departmentName", StandardBasicTypes.STRING);
        q.addScalar("positionTitleId", StandardBasicTypes.UUID_CHAR);
        q.addScalar("positionTitleName", StandardBasicTypes.STRING);
        q.addScalar("nominalQuantity", StandardBasicTypes.INTEGER);
        q.addScalar("actualQuantity", StandardBasicTypes.INTEGER);
        q.addScalar("supplementaryQuantity", StandardBasicTypes.INTEGER);
        q.addScalar("filteredQuantity", StandardBasicTypes.INTEGER);

        q.setResultTransformer(Transformers.aliasToBean(PositionTitleResourcePlanDto.class));

        List<PositionTitleResourcePlanDto> results = q.getResultList();
        return results;
    }


    @Override
    public Boolean updateStatusByViceGeneralDirector(SearchHrResourcePlanDto dto) throws Exception {
        if (dto == null || CollectionUtils.isEmpty(dto.getChosenRecordIds()) || dto.getViceGeneralDirectorStatus() == null) {
            return false;
        }

        List<UUID> ids = dto.getChosenRecordIds();
        List<HrResourcePlan> entities = hrResourcePlanRepository.findAllById(ids);

        if (entities.size() != ids.size()) {
            //throw new Exception("Some records do not exist!");
            logger.error("Some records do not exist!");
            return false;
        }

        for (HrResourcePlan entity : entities) {
            entity.setViceGeneralDirectorStatus(dto.getViceGeneralDirectorStatus());
        }

        hrResourcePlanRepository.saveAll(entities);
        return true;
    }

    @Override
    public Boolean updateStatusByGeneralDirector(SearchHrResourcePlanDto dto) throws Exception {
        if (dto == null || CollectionUtils.isEmpty(dto.getChosenRecordIds()) || dto.getGeneralDirectorStatus() == null) {
            return false;
        }

        List<UUID> ids = dto.getChosenRecordIds();
        List<HrResourcePlan> entities = hrResourcePlanRepository.findAllById(ids);

        if (entities.size() != ids.size()) {
            //throw new Exception("Some records do not exist!");
            logger.error("Some records do not exist!");
            return false;
        }

        for (HrResourcePlan entity : entities) {
            entity.setGeneralDirectorStatus(dto.getGeneralDirectorStatus());
        }

        hrResourcePlanRepository.saveAll(entities);
        return true;
    }

    @Override
    public Boolean updateStatus(SearchHrResourcePlanDto dto) throws Exception {
        if (dto == null || CollectionUtils.isEmpty(dto.getChosenRecordIds()) || dto.getPlanApprovalStatus() == null) {
            return false;
        }

        List<UUID> ids = dto.getChosenRecordIds();
        List<HrResourcePlan> entities = hrResourcePlanRepository.findAllById(ids);

        if (entities.size() != ids.size()) {
            //throw new Exception("Some records do not exist!");
            logger.error("Some records do not exist!");
            return false;
        }
        UserDto userDto = userExtService.getCurrentUser();
        Staff staff = userExtService.getCurrentStaffEntity();
        boolean isGeneralDirector = false; // là TGĐ
        boolean isDeputyGeneralDirector = false; // là phó TGĐ
        boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
        boolean isManager = RoleUtils.hasRoleHrManager(userDto);

        StaffDto staffDto = new StaffDto(staff);
        userExtService.checkIsGeneralDirectorOrDeputyGeneralDirector(staffDto);

        if (staffDto.getUser() != null) {
            Set<RoleDto> roles = staffDto.getUser().getRoles();
            isGeneralDirector = roles.stream().anyMatch(r -> HrConstants.IS_GENERAL_DIRECTOR.equals(r.getName()));
            isDeputyGeneralDirector = roles.stream().anyMatch(r -> HrConstants.IS_DEPUTY_GENERAL_DIRECTOR.equals(r.getName()));
        }


        if (isGeneralDirector || isDeputyGeneralDirector || isAdmin || isManager) {
            for (HrResourcePlan entity : entities) {
                if (isGeneralDirector) {
                    entity.setGeneralDirector(staff);
                    entity.setGeneralDirectorStatus(dto.getPlanApprovalStatus());
                }
                if (isDeputyGeneralDirector) {
                    entity.setViceGeneralDirector(staff);
                    entity.setViceGeneralDirectorStatus(dto.getPlanApprovalStatus());
                }
                if (isAdmin || isManager) {
                    entity.setGeneralDirector(staff);
                    entity.setGeneralDirectorStatus(dto.getPlanApprovalStatus());
                    entity.setViceGeneralDirector(staff);
                    entity.setViceGeneralDirectorStatus(dto.getPlanApprovalStatus());
                }
            }

            hrResourcePlanRepository.saveAll(entities);
            return true;
        } else {
            logger.error("You do not have permission to perform this action!");
            return false;
        }
    }

    @Override
    public String autoGenerateCode(String configKey) {
        SystemConfig systemConfig = systemConfigService.getConfigByConfigValue(configKey);
        if (systemConfig == null) {
            return "";
        } else {
            String maxCode = hrResourcePlanRepository.findMaxCodeByPrefix(systemConfig.getConfigValue(), systemConfig.getNumberOfZero());
            return systemConfigService.generateNextCode(systemConfig.getConfigValue(), systemConfig.getNumberOfZero(), maxCode);
        }
    }


    @Override
    public List<DepartmentResourcePlanDto> getDepartmentResourcePlan(SearchHrResourcePlanDto dto) {
        List<PositionTitleResourcePlanDto> positionTitleList = getPositionTitleResourcePlan(dto);

        Map<UUID, DepartmentResourcePlanDto> departmentMap = new LinkedHashMap<>();

        for (PositionTitleResourcePlanDto p : positionTitleList) {
            if (p.getDepartmentId() == null) continue; // tránh lỗi null key

            DepartmentResourcePlanDto dept = departmentMap.computeIfAbsent(p.getDepartmentId(), id -> {
                DepartmentResourcePlanDto d = new DepartmentResourcePlanDto();
                d.setDepartmentId(id);
                d.setDepartmentName(p.getDepartmentName());
                d.setPositionTitles(new ArrayList<>());
                return d;
            });

//	        dept.getPositionTitles().add(p);
            // Nếu positionTitleId không null, thêm vào danh sách chi tiết
            if (p.getPositionTitleId() != null && p.getPositionTitleName() != null) {
                dept.getPositionTitles().add(p);
            }

            dept.setNominalQuantity(dept.getNominalQuantity() + (p.getNominalQuantity() != null ? p.getNominalQuantity() : 0));
            dept.setActualQuantity(dept.getActualQuantity() + (p.getActualQuantity() != null ? p.getActualQuantity() : 0));
            dept.setSupplementaryQuantity(dept.getSupplementaryQuantity() + (p.getSupplementaryQuantity() != null ? p.getSupplementaryQuantity() : 0));
            dept.setFilteredQuantity(dept.getFilteredQuantity() + (p.getFilteredQuantity() != null ? p.getFilteredQuantity() : 0));
        }

        return new ArrayList<>(departmentMap.values());
    }


    @Override
    public List<DepartmentResourcePlanDto> getDepartmentResourcePlanTree(SearchHrResourcePlanDto dto) {
        SearchHrDepartmentDto searchDto = new SearchHrDepartmentDto();
        searchDto.setPageIndex(1);
        searchDto.setPageSize(10);
        Page<HRDepartmentDto> pages = hRDepartmentService.pagingDepartmentHierarchy(searchDto);
        List<PositionTitleResourcePlanDto> positionTitleList = getPositionTitleResourcePlan(dto);
        // Nhóm các chức vụ theo departmentId
        Map<UUID, List<PositionTitleResourcePlanDto>> positionMap = positionTitleList.stream()
                .collect(Collectors.groupingBy(PositionTitleResourcePlanDto::getDepartmentId));

        List<DepartmentResourcePlanDto> result = new ArrayList<>();

        if (pages != null && pages.getContent() != null && !pages.getContent().isEmpty()) {
            for (HRDepartmentDto dept : pages.getContent()) {
                DepartmentResourcePlanDto dtoNode = buildDepartmentResourcePlanNode(dept, positionMap);
                result.add(dtoNode);
            }
        }

        return result;
    }

    private DepartmentResourcePlanDto buildDepartmentResourcePlanNode(HRDepartmentDto dept,
                                                                      Map<UUID, List<PositionTitleResourcePlanDto>> positionMap) {

        DepartmentResourcePlanDto node = new DepartmentResourcePlanDto();
        node.setDepartmentId(dept.getId());
        node.setDepartmentName(dept.getName());

        List<PositionTitleResourcePlanDto> positions = positionMap.getOrDefault(dept.getId(), new ArrayList<>())
                .stream()
                .filter(p -> p.getPositionTitleId() != null && p.getPositionTitleName() != null)
                .collect(Collectors.toList());
        node.setPositionTitles(positions);

        // Tổng số lượng nếu có logic cụ thể (ở đây ví dụ tính nominalQuantity theo tổng từng chức vụ)
        node.setNominalQuantity(positions.stream()
                .map(p -> Optional.ofNullable(p.getNominalQuantity()).orElse(0))
                .reduce(0, Integer::sum));

        node.setActualQuantity(positions.stream()
                .map(p -> Optional.ofNullable(p.getActualQuantity()).orElse(0))
                .reduce(0, Integer::sum));

        node.setSupplementaryQuantity(positions.stream()
                .map(p -> Optional.ofNullable(p.getSupplementaryQuantity()).orElse(0))
                .reduce(0, Integer::sum));

        node.setFilteredQuantity(positions.stream()
                .map(p -> Optional.ofNullable(p.getFilteredQuantity()).orElse(0))
                .reduce(0, Integer::sum));

        // Đệ quy xử lý con
        if (dept.getChildren() != null && !dept.getChildren().isEmpty()) {
            List<DepartmentResourcePlanDto> children = new ArrayList<>();
            for (HRDepartmentDto child : dept.getChildren()) {
                DepartmentResourcePlanDto childNode = buildDepartmentResourcePlanNode(child, positionMap);
                positions = positionMap.getOrDefault(child.getId(), new ArrayList<>())
                        .stream()
                        .filter(p -> p.getPositionTitleId() != null && p.getPositionTitleName() != null)
                        .collect(Collectors.toList());

                node.setPositionTitles(positions);
                children.add(childNode);
            }
            node.setChildren(children);
        }

        return node;
    }


    @Override
    public List<DepartmentResourcePlanDto> getDepartmentResourcePlanTreeBySpreadLevel(SearchHrResourcePlanDto dto) {
        SearchHrDepartmentDto searchDto = new SearchHrDepartmentDto();

        searchDto.setPageIndex(1);
        searchDto.setPageSize(10);

        Page<HRDepartmentDto> pages = hRDepartmentService.pagingDepartmentHierarchy(searchDto);
        List<PositionTitleResourcePlanDto> positionTitleList = getPositionTitleResourcePlan(dto);

        // Nhóm các chức vụ theo departmentId
        Map<UUID, List<PositionTitleResourcePlanDto>> positionMap = positionTitleList.stream()
                .collect(Collectors.groupingBy(PositionTitleResourcePlanDto::getDepartmentId));

        List<DepartmentResourcePlanDto> result = new ArrayList<>();

        if (pages != null && pages.getContent() != null && !pages.getContent().isEmpty()) {
            for (HRDepartmentDto dept : pages.getContent()) {
                buildDepartmentResourcePlanSpreadByLevel(dept, positionMap, result);
            }
        }

        return result;
    }

    private void buildDepartmentResourcePlanSpreadByLevel(HRDepartmentDto dept,
                                                          Map<UUID, List<PositionTitleResourcePlanDto>> positionMap,
                                                          List<DepartmentResourcePlanDto> result) {

        DepartmentResourcePlanDto node = new DepartmentResourcePlanDto();
        result.add(node);

        node.setDepartmentId(dept.getId());
        node.setDepartmentName(dept.getName());

        List<PositionTitleResourcePlanDto> positions = positionMap.getOrDefault(dept.getId(), new ArrayList<>())
                .stream()
                .filter(p -> p.getPositionTitleId() != null && p.getPositionTitleName() != null)
                .collect(Collectors.toList());
        node.setPositionTitles(positions);

        // Tổng số lượng nếu có logic cụ thể (ở đây ví dụ tính nominalQuantity theo tổng từng chức vụ)
        node.setNominalQuantity(positions.stream()
                .map(p -> Optional.ofNullable(p.getNominalQuantity()).orElse(0))
                .reduce(0, Integer::sum));

        node.setActualQuantity(positions.stream()
                .map(p -> Optional.ofNullable(p.getActualQuantity()).orElse(0))
                .reduce(0, Integer::sum));

        node.setSupplementaryQuantity(positions.stream()
                .map(p -> Optional.ofNullable(p.getSupplementaryQuantity()).orElse(0))
                .reduce(0, Integer::sum));

        node.setFilteredQuantity(positions.stream()
                .map(p -> Optional.ofNullable(p.getFilteredQuantity()).orElse(0))
                .reduce(0, Integer::sum));

        // Đệ quy xử lý con
        if (dept.getChildren() != null && !dept.getChildren().isEmpty()) {
            List<DepartmentResourcePlanDto> children = new ArrayList<>();

            for (HRDepartmentDto child : dept.getChildren()) {
                buildDepartmentResourcePlanSpreadByLevel(child, positionMap, result);

                positions = positionMap.getOrDefault(child.getId(), new ArrayList<>())
                        .stream()
                        .filter(p -> p.getPositionTitleId() != null && p.getPositionTitleName() != null)
                        .collect(Collectors.toList());

                node.setPositionTitles(positions);
            }

            node.setChildren(children);
        }

    }


}
