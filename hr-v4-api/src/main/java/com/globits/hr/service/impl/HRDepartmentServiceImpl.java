package com.globits.hr.service.impl;

import com.globits.core.domain.Department;
import com.globits.core.repository.DepartmentRepository;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.diagram.ResponseDiagram;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchHrDepartmentDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.*;
import com.globits.hr.utils.RoleUtils;
import com.globits.security.dto.UserDto;
import com.globits.security.service.RoleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Transactional
@Service
public class HRDepartmentServiceImpl implements HRDepartmentService {
    private static final Logger logger = LoggerFactory.getLogger(HrOrganizationDto.class);

    @PersistenceContext
    private EntityManager manager;

    @Autowired
    private HRDepartmentRepository repos;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentTypeRepository departmentTypeRepository;

    @Autowired
    private DepartmentGroupRepository departmentGroupRepository;

    @Autowired
    private PositionTitleRepository positionTitleRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private HRDepartmentPositionRepository hrDepartmentPositionRepository;

    @Autowired
    private HrOrganizationRepository hrOrganizationRepository;

    @Autowired
    private HrDepartmentShiftWorkService hrDepartmentShiftWorkService;
    @Autowired
    private HrDepartmentShiftWorkRepository hrDepartmentShiftWorkRepository;

    @Autowired
    private HRDepartmentRepository hrDepartmentRepository;

    @Autowired
    private ShiftWorkRepository shiftWorkRepository;
    @Autowired
    private UserExtService userExtService;
    @Autowired
    private StaffHierarchyService staffHierarchyService;
    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public Boolean isValidCode(HRDepartmentDto dto) {
        if (dto == null)
            return false;

        // ID of HRDepartment is null => Create new HRDepartment
        // => Assure that there's no other HRDepartments using this code of new HRDepartment
        // if there was any HRDepartment using new HRDepartment code, then this new code is
        // invalid => return False
        // else return true
        if (dto.getId() == null) {
            List<HRDepartment> entities = repos.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }

            return false;
        }
        // ID of HRDepartment is NOT null => HRDepartment is modified
        // => Assure that the modified code is not same to OTHER any HRDepartment's code
        // if there was any HRDepartment using new HRDepartment code, then this new code is
        // invalid => return False
        // else return true
        else {
            List<HRDepartment> entities = repos.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (HRDepartment entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }

        return true;
    }

    @Override
    public HRDepartmentDto saveOrUpdate(HRDepartmentDto dto) {
        if (dto == null) return null;

        HRDepartment hRDepartment = null;
        if (dto.getId() != null) {
            hRDepartment = repos.findById(dto.getId()).orElse(null);
            if (hRDepartment == null) return null;
        }

        if (hRDepartment == null) {
            hRDepartment = new HRDepartment();
        }

        hRDepartment.setCode(dto.getCode());
        hRDepartment.setName(dto.getName());
        hRDepartment.setShortName(dto.getShortName());
        hRDepartment.setSortNumber(dto.getSortNumber());
        hRDepartment.setDescription(dto.getDescription());
        hRDepartment.setFunction(dto.getFunc());
        hRDepartment.setIndustryBlock(dto.getIndustryBlock());
        hRDepartment.setFoundedDate(dto.getFoundedDate());
        hRDepartment.setFoundedNumber(dto.getFoundedNumber());
        hRDepartment.setDisplayOrder(dto.getDisplayOrder());
        hRDepartment.setDepartmentDisplayCode(dto.getDepartmentDisplayCode());
        hRDepartment.setEstablishDecisionCode(dto.getEstablishDecisionCode());
        hRDepartment.setEstablishDecisionDate(dto.getEstablishDecisionDate());
        hRDepartment.setTimezone(dto.getTimezone());

        // them loai phong ban
        if (dto.getHrDepartmentType() != null && dto.getHrDepartmentType().getId() != null) {
            DepartmentType departmentType = departmentTypeRepository
                    .findById(dto.getHrDepartmentType().getId()).orElse(null);
            if (departmentType == null) return null;
            hRDepartment.setHrdepartmentType(departmentType);
        } else {
            hRDepartment.setHrdepartmentType(null);
        }

        // them nhom phong ban
        if (dto.getDepartmentGroup() != null && dto.getDepartmentGroup().getId() != null) {
            DepartmentGroup departmentGroup = departmentGroupRepository.findById(dto.getDepartmentGroup().getId()).orElse(null);
            if (departmentGroup == null) return null;
            hRDepartment.setDepartmentGroup(departmentGroup);
        } else {
            hRDepartment.setDepartmentGroup(null);
        }

        //Thêm đơn vị
        //todo set tất cả HrOrganization các thằng con thành HrOrganization của phòng ban hiện tại, check đệ quy
        if (dto.getOrganization() != null && dto.getOrganization().getId() != null) {
            HrOrganization hrOrganization = hrOrganizationRepository.findById(dto.getOrganization().getId()).orElse(null);
            hRDepartment.setOrganization(hrOrganization);
            this.setHrOrganizationToSubDepartments(hRDepartment);
        }
        // them chuc danh quan ly
        if (dto.getPositionTitleManager() != null && dto.getPositionTitleManager().getId() != null) {
            PositionTitle positionTitleManager = positionTitleRepository
                    .findById(dto.getPositionTitleManager().getId()).orElse(null);
            if (positionTitleManager == null) return null;
            hRDepartment.setPositionTitleManager(positionTitleManager);

        } else {
            hRDepartment.setPositionTitleManager(null);
        }
        // vi tri quan ly
        if (dto.getPositionManager() != null && dto.getPositionManager().getId() != null) {
            Position positionManager = positionRepository
                    .findById(dto.getPositionManager().getId()).orElse(null);
            if (positionManager == null) return null;
            hRDepartment.setPositionManager(positionManager);
        } else {
            hRDepartment.setPositionManager(null);
        }
        //save multiple position title for this department
        Set<HRDepartmentPosition> departmentPositions = new HashSet<>();
        if (dto.getPositionTitles() != null && !dto.getPositionTitles().isEmpty()) {
            for (PositionTitleDto positionTitle : dto.getPositionTitles()) {
                if (positionTitle == null || positionTitle.getId() == null)
                    continue;

                //find whether the relationship of this positions title and this department is existed in database first
                List<HRDepartmentPosition> existedRelationships = hrDepartmentPositionRepository
                        .findByDepartmentIdAndPositionTitleId(dto.getId(), positionTitle.getId());
                HRDepartmentPosition positionRelaEntity = null;
                if (existedRelationships != null && existedRelationships.size() > 0) {
                    //in this section, relationship is already existed
                    positionRelaEntity = existedRelationships.get(0);
                }
                if (positionRelaEntity == null) {
                    //in this section, relationship is NOT EXISTED => CREATE NEW
                    positionRelaEntity = new HRDepartmentPosition();

                    PositionTitle positionTitleEntity = positionTitleRepository
                            .findById(positionTitle.getId()).orElse(null);
                    if (positionTitleEntity == null) continue;
                    positionRelaEntity.setDepartment(hRDepartment);
                    positionRelaEntity.setPositionTitle(positionTitleEntity);
                }

                departmentPositions.add(positionRelaEntity);
            }
        }
        if (hRDepartment.getDepartmentPositions() == null) hRDepartment.setDepartmentPositions(departmentPositions);
        else {
            hRDepartment.getDepartmentPositions().clear();
            hRDepartment.getDepartmentPositions().addAll(departmentPositions);
        }

        if (dto.getParentCode() != null) {
            List<HRDepartment> depart = repos.findByCode(dto.getParentCode());
            if (depart != null && depart.size() > 0) {
                Department department = depart.get(0);
                hRDepartment.setParent(department);
                checkCircularParent(hRDepartment, department);
            }
        } else if (dto.getParent() != null && dto.getParent().getId() != null) {
            Department department = null;
            Optional<Department> optional = departmentRepository.findById(dto.getParent().getId());
            if (optional.isPresent()) {
                department = optional.get();
            }
            hRDepartment.setParent(department);
            checkCircularParent(hRDepartment, department);
        } else {
            hRDepartment.setParent(null);
        }

        Set<Department> subDepartments = new HashSet<>();
        if (hRDepartment.getSubDepartments() != null)
            for (Department oldChild : hRDepartment.getSubDepartments()) {
                oldChild.setParent(null);
                departmentRepository.save(oldChild);
            }

        if (dto.getChildren() != null && dto.getChildren().size() > 0) {
            for (HRDepartmentDto child : dto.getChildren()) {
                if (child != null && child.getId() != null) {
                    HRDepartment childDep = repos.findById(child.getId()).orElse(null);
                    if (childDep == null) {
                        System.out.println("Child deparment: " + childDep.toString() + " is not existed");
                        continue;
                    }
                    childDep.setParent(hRDepartment);
                    subDepartments.add(childDep);
                }
            }
        }
        if (hRDepartment.getSubDepartments() == null) {
            hRDepartment.setSubDepartments(subDepartments);
        } else {
            hRDepartment.getSubDepartments().clear();
            hRDepartment.getSubDepartments().addAll(subDepartments);
        }

        //Thêm shiftWork cho phòng ban
        hrDepartmentShiftWorkService.generateHrDepartmentShiftWork(dto, hRDepartment);

        hRDepartment = repos.save(hRDepartment);

        return new HRDepartmentDto(hRDepartment);
    }

    /**
     * Kiểm tra xem phòng ban mới được chọn làm cha có gây ra vòng lặp hay không.
     *
     * @param currentDepartment Phòng ban đang chỉnh sửa
     * @param selectedParent    Phòng ban được chọn làm cha
     */
    private void checkCircularParent(Department currentDepartment, Department selectedParent) {
        Department parent = selectedParent;

        while (parent != null) {
            // Nếu một trong các cha là chính phòng ban hiện tại thì bị vòng lặp
            if (parent.getId().equals(currentDepartment.getId())) {
                throw new IllegalArgumentException("Không thể gán phòng ban cha gây ra vòng lặp.");
            }
            parent = parent.getParent(); // Lấy tiếp cha của cha (đi lên cây)
        }
    }

    @Override
    public Integer saveListDepartment(List<HRDepartmentDto> departmentDtos) {
        if (departmentDtos == null || departmentDtos.isEmpty()) return 0;
        int totalSaved = 0;
        for (HRDepartmentDto departmentDto : departmentDtos) {
            totalSaved += saveDepartmentRecursive(departmentDto, null);
        }
        return totalSaved;
    }

    private int saveDepartmentRecursive(HRDepartmentDto dto, HRDepartment parent) {
        try {
            if (dto == null) return 0;

            HRDepartment department = null;
            if (dto.getCode() != null) {
                List<HRDepartment> listHRDepartment = repos.findByCode(dto.getCode());
                if (listHRDepartment != null && !listHRDepartment.isEmpty()) {
                    department = listHRDepartment.get(0);
                }
            }
            // Nếu không tồn tại thì tạo mới
            if (department == null) {
                department = new HRDepartment();
            }
            if (department.getCreateDate() == null) {
                department.setCreateDate(LocalDateTime.now());
            }
            department = repos.saveAndFlush(department); // lưu và lấy lại ID

            // Mapping các dữ liệu cơ bản
            department.setCode(dto.getCode());
            department.setName(dto.getName());
            department.setShortName(dto.getShortName());
            department.setSortNumber(dto.getSortNumber());
            department.setDescription(dto.getDescription());
            department.setFoundedDate(dto.getFoundedDate());

            // Chức danh quản lý
            PositionTitle positionTitleManager = null;
            if (dto.getPositionTitleManager() != null && StringUtils.hasText(dto.getPositionTitleManager().getCode())) {
                List<PositionTitle> positionTitleList = positionTitleRepository.findByCode(dto.getPositionTitleManager().getCode());

                if (positionTitleList != null && !positionTitleList.isEmpty()) {
                    positionTitleManager = positionTitleList.get(0);
                }

            }
            department.setPositionTitleManager(positionTitleManager);

            // Đơn vị trực thuộc
            HrOrganization hrOrganization = null;
            if (dto.getOrganization() != null && StringUtils.hasText(dto.getOrganization().getCode())) {
                List<HrOrganization> hrOrganizationList = hrOrganizationRepository.findByCode(dto.getOrganization().getCode());

                if (hrOrganizationList != null && !hrOrganizationList.isEmpty()) {
                    hrOrganization = hrOrganizationList.get(0);
                }

            }
            department.setOrganization(hrOrganization);

            // Loại phòng ban
            DepartmentType departmentType = null;
            if (dto.getHrDepartmentType() != null && StringUtils.hasText(dto.getHrDepartmentType().getCode())) {
                List<DepartmentType> departmentTypeList = departmentTypeRepository.findByCode(dto.getHrDepartmentType().getCode());

                if (departmentTypeList != null && !departmentTypeList.isEmpty()) {
                    departmentType = departmentTypeList.get(0);
                }

            }
            department.setHrdepartmentType(departmentType);

            // Set phòng ban cha
            HRDepartment parentDepartment = null;
            if (dto.getParent() != null && StringUtils.hasText(dto.getParent().getCode())) {
                List<HRDepartment> parentList = repos.findByCode(dto.getParent().getCode());

                if (parentList != null && !parentList.isEmpty()) {
                    parentDepartment = parentList.get(0);
                }

            }
            department.setParent(parentDepartment);
//        department.setParent(parent);

            //Mã chức danh thuộc phòng ban
            if (department.getDepartmentPositions() == null) {
                department.setDepartmentPositions(new HashSet<>());
            }
            department.getDepartmentPositions().clear();
            if (dto.getPositionTitles() != null && !dto.getPositionTitles().isEmpty()) {
                for (PositionTitleDto item : dto.getPositionTitles()) {
                    if (item == null || item.getCode() == null)
                        continue;

                    List<HRDepartmentPosition> existedRelationships = hrDepartmentPositionRepository
                            .findByDepartmentIdAndPositionTitleCode(department.getId(), item.getCode());
                    HRDepartmentPosition positionRelaEntity = null;
                    if (existedRelationships != null && !existedRelationships.isEmpty()) {
                        //in this section, relationship is already existed
                        positionRelaEntity = existedRelationships.get(0);
                    }
                    if (positionRelaEntity == null) {
                        //in this section, relationship is NOT EXISTED => CREATE NEW
                        positionRelaEntity = new HRDepartmentPosition();

                        List<PositionTitle> positionTitlesEntity = positionTitleRepository
                                .findByCode(item.getCode());
                        if (positionTitlesEntity == null) continue;
                        positionRelaEntity.setPositionTitle(positionTitlesEntity.get(0));
                        positionRelaEntity.setDepartment(department);
                    }

                    department.getDepartmentPositions().add(positionRelaEntity);
                }
            }
            //phòng ban trực thuộc
            if (department.getSubDepartments() != null)
                for (Department oldChild : department.getSubDepartments()) {
                    oldChild.setParent(null);
                    departmentRepository.save(oldChild);
                }

            if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
                if (department.getSubDepartments() == null) {
                    department.setSubDepartments(new HashSet<>());
                }
                for (HRDepartmentDto child : dto.getChildren()) {
                    if (child != null && child.getCode() != null && child.getUpdate()) {
                        List<HRDepartment> childDep = repos.findByCode(child.getCode());
                        if (childDep != null && childDep.size() > 0) {
                            childDep.get(0).setParent(department);
                            department.getSubDepartments().add(childDep.get(0));
                        }
                    }
                }
            }
            //Mã ca làm việc
            if (department.getDepartmentShiftWorks() == null) {
                department.setDepartmentShiftWorks(new HashSet<>());
            }
            department.getDepartmentShiftWorks().clear();
            if (dto.getShiftWorks() != null && !dto.getShiftWorks().isEmpty()) {
                for (ShiftWorkDto item : dto.getShiftWorks()) {
                    if (item == null || item.getCode() == null)
                        continue;

                    List<HrDepartmentShiftWork> existedRelationships = hrDepartmentShiftWorkRepository
                            .findByDepartmentIdShiftWorkCode(department.getId(), item.getCode());
                    HrDepartmentShiftWork hrDepartmentShiftWorkEntity = null;
                    if (existedRelationships != null && !existedRelationships.isEmpty()) {
                        //in this section, relationship is already existed
                        hrDepartmentShiftWorkEntity = existedRelationships.get(0);
                    }
                    if (hrDepartmentShiftWorkEntity == null) {
                        //in this section, relationship is NOT EXISTED => CREATE NEW
                        hrDepartmentShiftWorkEntity = new HrDepartmentShiftWork();

                        List<ShiftWork> shiftWorkEntity = shiftWorkRepository
                                .findByCode(item.getCode());
                        if (shiftWorkEntity == null) continue;
                        hrDepartmentShiftWorkEntity.setShiftWork(shiftWorkEntity.get(0));
                        hrDepartmentShiftWorkEntity.setDepartment(department);
                    }

                    department.getDepartmentShiftWorks().add(hrDepartmentShiftWorkEntity);
                }
            }

            // Lưu phòng ban hiện tại
            department = hrDepartmentRepository.saveAndFlush(department);

            int savedCount = 1; // Đã lưu 1 phòng ban

            // Đệ quy lưu các phòng ban con (nếu có)
            if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
                for (HRDepartmentDto childDto : dto.getChildren()) {
                    if (!childDto.getUpdate()) {
                        savedCount += saveDepartmentRecursive(childDto, department);
                    }
                }
            }

            return savedCount;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void setHrOrganizationToSubDepartments(HRDepartment department) {
        if (department.getSubDepartments() != null && !department.getSubDepartments().isEmpty() && department.getOrganization() != null) {
            for (Department subDepartment : department.getSubDepartments()) {
                if (subDepartment instanceof HRDepartment) {
                    ((HRDepartment) subDepartment).setOrganization(department.getOrganization());
                    setHrOrganizationToSubDepartments((HRDepartment) subDepartment);
                }
            }
        }
    }

    @Override
    public Boolean deleteHRDepartment(UUID id) {
        if (id != null) {
            repos.deleteById(id);
            return true;
        }
        return false;
    }


    @Override
    public HRDepartmentDto getHRDepartment(UUID id) {
        if (id == null) return null;
        HRDepartment hRDepartment = repos.findById(id).orElse(null);
        if (hRDepartment == null) return null;

        HRDepartmentDto reponse = new HRDepartmentDto(hRDepartment);
        return reponse;
    }

    @Override
    public Page<HRDepartmentDto> pagingTreeDepartments(SearchHrDepartmentDto dto) {
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
        String orderBy = " ORDER BY entity.name DESC ";
        String sqlCount = "select count(entity.id) from HRDepartment as entity where entity.parent is null  ";
        String sql = "select new com.globits.hr.dto.HRDepartmentDto(entity,true ) from HRDepartment as entity where (1=1)  ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text )";
        }

        if (dto.getDepartmentId() != null) {
            whereClause += " AND ( entity.id = :departmentId  )";
        } else {
            whereClause += " AND ( entity.parent is null ) ";
        }

        if (dto.getOrganizationId() != null) {
            whereClause += " AND ( entity.organization.id = :organizationId  )";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, HRDepartmentDto.class);
        Query qCount = manager.createQuery(sqlCount);

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
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<HRDepartmentDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Long count = repos.checkCode(code, id);
            return count != 0L;
        }
        return null;
    }

    @Override
    public Page<HRDepartmentDto> pagingDepartments(SearchHrDepartmentDto dto) {
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
        UserDto userDto = userExtService.getCurrentUser();
        Staff staff = userExtService.getCurrentStaffEntity();
        boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
        boolean isManager = RoleUtils.hasRoleHrManager(userDto);
        boolean isStaffView = RoleUtils.hasRoleHrStaffView(userDto);
        boolean isPositionManager = RoleUtils.isHeadOfDepartment(staff);

        if (!(isAdmin || isManager || isStaffView)) {
            StaffDto staffDto = userExtService.getCurrentStaff();
            boolean hasPBHCNS = staffDto.getPositionList().stream()
                    .anyMatch(p -> p.getDepartment() != null && HrConstants.PB_HCNS.equals(p.getDepartment().getCode()));
            if (!hasPBHCNS) {
                staffDto.getPositionList().stream().filter(Objects::nonNull)
                        .forEach(positionDto -> {
                            if (positionDto.getDepartment() != null) {
                                dto.getDepartmentIdList().add(positionDto.getDepartment().getId());
                            }
                        });
                if (isPositionManager && staff != null) {
                    List<UUID> departmentIdList = staffHierarchyService.getAllManagedAndSubDepartmentIdsByStaff(staff.getId());
                    dto.setDepartmentIdList(departmentIdList);
                }
            }
        }
        String whereClause = "";
        String orderBy = " ORDER BY entity.displayOrder DESC, entity.name ASC, entity.code ASC ";
        String sqlCount = "select count(entity.id) from HRDepartment as entity where  (1=1)  ";
        String sql = "select new com.globits.hr.dto.HRDepartmentDto(entity, true, false ) from HRDepartment as entity where (1=1)  ";
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }
        if (dto.getOrganizationId() != null) {
            whereClause += " AND ( entity.organization.id = :organizationId) ";
        }
        if (dto.getDepartmentId() != null) {
            whereClause += " AND ( entity.id = :departmentId) ";
        }
        if (dto.getDepartmentIdList() != null && !dto.getDepartmentIdList().isEmpty()) {
            whereClause += " AND ( entity.id IN (:departmentIdList)) ";
        }
        if (dto.getParentId() != null) {
            whereClause += " AND ( entity.parent.id = :parentId ) ";
        }
        if (!CollectionUtils.isEmpty(dto.getDepartmentTypeCode())) {
            whereClause += " AND ( entity.hrdepartmentType.code in :departmentTypeCode ) ";
        }
        if (dto.getHrDepartmentTypeId() != null) {
            whereClause += " AND entity.hrdepartmentType.id = :hrDepartmentTypeId ";
        } else if (dto.getHrDepartmentTypeId() == null && StringUtils.hasText(dto.getHrDepartmentTypeCode())) {
            whereClause += " AND TRIM(entity.hrdepartmentType.code) LIKE CONCAT('%', :hrDepartmentTypeCode, '%') ";
        }


        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, HRDepartmentDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getOrganizationId() != null) {
            q.setParameter("organizationId", dto.getOrganizationId());
            qCount.setParameter("organizationId", dto.getOrganizationId());
        }
        if (dto.getDepartmentId() != null) {
            q.setParameter("departmentId", dto.getDepartmentId());
            qCount.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getDepartmentIdList() != null && !dto.getDepartmentIdList().isEmpty()) {
            q.setParameter("departmentIdList", dto.getDepartmentIdList());
            qCount.setParameter("departmentIdList", dto.getDepartmentIdList());
        }
        if (dto.getParentId() != null) {
            q.setParameter("parentId", dto.getParentId());
            qCount.setParameter("parentId", dto.getParentId());
        }
        if (!CollectionUtils.isEmpty(dto.getDepartmentTypeCode())) {
            q.setParameter("departmentTypeCode", dto.getDepartmentTypeCode());
            qCount.setParameter("departmentTypeCode", dto.getDepartmentTypeCode());
        }
        if (dto.getHrDepartmentTypeId() != null) {
            q.setParameter("hrDepartmentTypeId", dto.getHrDepartmentTypeId());
            qCount.setParameter("hrDepartmentTypeId", dto.getHrDepartmentTypeId());
        } else if (dto.getHrDepartmentTypeId() == null && StringUtils.hasText(dto.getHrDepartmentTypeCode())) {
            q.setParameter("hrDepartmentTypeCode", dto.getHrDepartmentTypeCode().trim());
            qCount.setParameter("hrDepartmentTypeCode", dto.getHrDepartmentTypeCode().trim());
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<HRDepartmentDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public HRDepartment toHRDepartment(HRDepartmentDto dto, HRDepartment hRDepartment) {
        if (dto != null && dto.getCode() != null && StringUtils.hasText(dto.getCode())) {
            if (dto.getId() != null) {
                Optional<HRDepartment> optional = repos.findById(dto.getId());
                if (optional.isPresent()) {
                    hRDepartment = optional.get();
                }
            }
            if (hRDepartment == null) {
                hRDepartment = new HRDepartment();
            }
            hRDepartment.setCode(dto.getCode());
            hRDepartment.setName(dto.getName());
            hRDepartment.setShortName(dto.getShortName());
            hRDepartment.setSortNumber(dto.getSortNumber());
            hRDepartment.setDescription(dto.getDescription());
            hRDepartment.setFunction(dto.getFunc());
            hRDepartment.setIndustryBlock(dto.getIndustryBlock());
            hRDepartment.setFoundedDate(dto.getFoundedDate());
            hRDepartment.setFoundedNumber(dto.getFoundedNumber());
            hRDepartment.setDisplayOrder(dto.getDisplayOrder());
            hRDepartment.setDepartmentDisplayCode(dto.getDepartmentDisplayCode());
            hRDepartment.setEstablishDecisionCode(dto.getEstablishDecisionCode());
            hRDepartment.setEstablishDecisionDate(dto.getEstablishDecisionDate());
            hRDepartment.setTimezone(dto.getTimezone());

            // them loai phong ban
            if (dto.getHrDepartmentType() != null && dto.getHrDepartmentType().getId() != null) {
                DepartmentType departmentType = departmentTypeRepository
                        .findById(dto.getHrDepartmentType().getId()).orElse(null);
                if (departmentType == null) return null;
                hRDepartment.setHrdepartmentType(departmentType);
            } else {
                hRDepartment.setHrdepartmentType(null);
            }

            // them nhom phong ban
            if (dto.getDepartmentGroup() != null && dto.getDepartmentGroup().getId() != null) {
                DepartmentGroup departmentGroup = departmentGroupRepository.findById(dto.getDepartmentGroup().getId()).orElse(null);
                if (departmentGroup == null) return null;
                hRDepartment.setDepartmentGroup(departmentGroup);
            } else {
                hRDepartment.setDepartmentGroup(null);
            }

            // them chuc danh quan ly
            if (dto.getPositionTitleManager() != null && dto.getPositionTitleManager().getId() != null) {
                PositionTitle positionTitleManager = positionTitleRepository
                        .findById(dto.getPositionTitleManager().getId()).orElse(null);
                if (positionTitleManager == null) return null;
                hRDepartment.setPositionTitleManager(positionTitleManager);

            } else {
                hRDepartment.setPositionTitleManager(null);
            }

            //save multiple position title for this department
            Set<HRDepartmentPosition> departmentPositions = new HashSet<>();
            if (dto.getPositionTitles() != null && !dto.getPositionTitles().isEmpty()) {
                for (PositionTitleDto positionTitle : dto.getPositionTitles()) {
                    if (positionTitle == null || positionTitle.getId() == null)
                        continue;

                    //find whether the relationship of this positions title and this department is existed in database first
                    List<HRDepartmentPosition> existedRelationships = hrDepartmentPositionRepository
                            .findByDepartmentIdAndPositionTitleId(dto.getId(), positionTitle.getId());
                    HRDepartmentPosition positionRelaEntity = null;
                    if (existedRelationships != null && existedRelationships.size() > 0) {
                        //in this section, relationship is already existed
                        positionRelaEntity = existedRelationships.get(0);
                    }
                    if (positionRelaEntity == null) {
                        //in this section, relationship is NOT EXISTED => CREATE NEW
                        positionRelaEntity = new HRDepartmentPosition();

                        PositionTitle positionTitleEntity = positionTitleRepository
                                .findById(positionTitle.getId()).orElse(null);
                        if (positionTitleEntity == null) continue;
                        positionRelaEntity.setDepartment(hRDepartment);
                        positionRelaEntity.setPositionTitle(positionTitleEntity);
                    }

                    departmentPositions.add(positionRelaEntity);
                }
            }
            if (hRDepartment.getDepartmentPositions() == null) hRDepartment.setDepartmentPositions(departmentPositions);
            else {
                hRDepartment.getDepartmentPositions().clear();
                hRDepartment.getDepartmentPositions().addAll(departmentPositions);
            }

            if (dto.getParentCode() != null) {
                List<HRDepartment> depart = repos.findByCode(dto.getParentCode());
                if (depart != null && depart.size() > 0) {
                    Department department = depart.get(0);
                    hRDepartment.setParent(department);
                }
            } else if (dto.getParent() != null && dto.getParent().getId() != null) {
                Department department = null;
                Optional<Department> optional = departmentRepository.findById(dto.getParent().getId());
                if (optional.isPresent()) {
                    department = optional.get();
                }
                hRDepartment.setParent(department);
            } else {
                hRDepartment.setParent(null);
            }

            Set<Department> subDepartments = new HashSet<>();
            if (hRDepartment.getSubDepartments() != null)
                for (Department oldChild : hRDepartment.getSubDepartments()) {
                    oldChild.setParent(null);
                    departmentRepository.save(oldChild);
                }

            if (dto.getChildren() != null && dto.getChildren().size() > 0) {
                for (HRDepartmentDto child : dto.getChildren()) {
                    if (child != null && child.getId() != null) {
                        HRDepartment childDep = repos.findById(child.getId()).orElse(null);
                        if (childDep == null) {
                            System.out.println("Child deparment: " + childDep.toString() + " is not existed");
                            continue;
                        }
                        childDep.setParent(hRDepartment);
                        subDepartments.add(childDep);
                    }
                }
            }
            if (hRDepartment.getSubDepartments() == null) {
                hRDepartment.setSubDepartments(subDepartments);
            } else {
                hRDepartment.getSubDepartments().clear();
                hRDepartment.getSubDepartments().addAll(subDepartments);
            }
            return hRDepartment;
        }
        return hRDepartment;
    }

    @Override
    public List<ResponseDiagram> getHRDepartmentDiagram(UUID id) {
        return List.of();
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null)
            return false;
        boolean isValid = true;
        for (UUID itemId : ids) {
            boolean deleteRes = this.deleteHRDepartment(itemId);
            if (!deleteRes)
                isValid = false;
        }
        return isValid;
    }

    @Override
    public List<HRDepartmentDto> pagingDepartmentHierarchySpreadByLevel(SearchHrDepartmentDto dto) {
        if (dto == null) {
            return null;
        }

        Page<HRDepartmentDto> departmentPage = this.pagingDepartmentHierarchy(dto);
        if (departmentPage == null) {
            return null;
        }

        List<HRDepartmentDto> response = new ArrayList<>();

        handleAddToResponse(departmentPage.getContent(), response);

        return response;

    }

    private void handleAddToResponse(List<HRDepartmentDto> departmentData, List<HRDepartmentDto> result) {
        if(departmentData == null || departmentData.isEmpty()){
            return;
        }

        for(HRDepartmentDto department : departmentData){
            result.add(department);

            if(department.getChildren() != null && !department.getChildren().isEmpty()){
                handleAddToResponse(department.getChildren(), result);
            }
        }
    }

    @Override
    public Page<HRDepartmentDto> pagingDepartmentHierarchy(SearchHrDepartmentDto dto) {
        if (dto == null) {
            return null;
        }

        int pageIndex = Math.max(dto.getPageIndex() - 1, 0);
        int pageSize = dto.getPageSize();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        List<HRDepartmentDto> rootDepartments = new ArrayList<>();

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
                // Lấy danh sách phòng ban mà nhân viên làm trưởng phòng
                List<HRDepartment> departmentMain = hrDepartmentRepository.findByStaffId(staff.getId());
                if (departmentMain != null && departmentMain.size() > 0 && departmentMain.get(0) != null) {
                    dto.setDepartmentId(departmentMain.get(0).getId());
                } else {
                    return null;
                }

            }
        }
        if (dto.getDepartmentId() != null) {
            // Trường hợp truyền vào 1 phòng ban cụ thể
            HRDepartment rootEntity = manager.find(HRDepartment.class, dto.getDepartmentId());
            if (rootEntity != null) {
                HRDepartmentDto rootDto = new HRDepartmentDto(rootEntity);
                loadChildren(rootDto);  // đệ quy load cây con
                rootDepartments.add(rootDto);
            }
        } else {
            // Trường hợp mặc định: load các phòng ban gốc (parent IS NULL)
            String whereClause = " WHERE entity.parent IS NULL ";
            String orderBy = " ORDER BY entity.sortNumber, entity.displayOrder ASC ";

            if (dto.getOrganizationId() != null) {
                whereClause += " AND entity.organization.id = :organizationId ";
            }

            String sql = "SELECT new com.globits.hr.dto.HRDepartmentDto(entity) " +
                    "FROM HRDepartment entity " + whereClause + orderBy;
            String sqlCount = "SELECT count(entity.id) FROM HRDepartment entity " + whereClause;

            Query q = manager.createQuery(sql, HRDepartmentDto.class);
            Query qCount = manager.createQuery(sqlCount);

            if (dto.getOrganizationId() != null) {
                q.setParameter("organizationId", dto.getOrganizationId());
                qCount.setParameter("organizationId", dto.getOrganizationId());
            }

            rootDepartments = q.getResultList();

            for (HRDepartmentDto parent : rootDepartments) {
                loadChildren(parent);
            }
        }


        // phân trang danh sách gốc
        if (dto.getHrDepartmentTypeId() == null && !StringUtils.hasText(dto.getHrDepartmentTypeCode()) && !StringUtils.hasText(dto.getKeyword())) {
            int fromIndex = Math.min(pageIndex * pageSize, rootDepartments.size());
            int toIndex = Math.min(fromIndex + pageSize, rootDepartments.size());
            return new PageImpl<>(rootDepartments.subList(fromIndex, toIndex), pageable, rootDepartments.size());
        }

        List<HRDepartmentDto> matchedTrees = new ArrayList<>();

        for (HRDepartmentDto root : rootDepartments) {
            if (searchAndKeepMatchingBranch(root, dto)) {
                matchedTrees.add(root);
            }
        }

        int fromIndex = Math.min(pageIndex * pageSize, matchedTrees.size());
        int toIndex = Math.min(fromIndex + pageSize, matchedTrees.size());
        return new PageImpl<>(matchedTrees.subList(fromIndex, toIndex), pageable, matchedTrees.size());

    }


    private boolean loadChildrenOnlyNullManager(HRDepartmentDto parent) {
        String hql = "SELECT new com.globits.hr.dto.HRDepartmentDto(entity, false) " +
                "FROM HRDepartment entity WHERE entity.parent.id = :parentId " +
                "ORDER BY entity.sortNumber, entity.displayOrder ASC";

        List<HRDepartmentDto> children = manager.createQuery(hql, HRDepartmentDto.class)
                .setParameter("parentId", parent.getId())
                .getResultList();

        List<HRDepartmentDto> filteredChildren = new ArrayList<>();
        boolean hasValidChild = false;

        for (HRDepartmentDto child : children) {
            boolean childHasValidSub = loadChildrenOnlyNullManager(child);

            // Chỉ lấy những đứa con không có manager hoặc có con hợp lệ
            if (child.getPositionManager() == null || childHasValidSub) {
                filteredChildren.add(child);
                hasValidChild = true;
            }
        }

        parent.setChildren(filteredChildren);
        return hasValidChild;
    }

    private void loadChildren(HRDepartmentDto parent) {
        String hql = "SELECT new com.globits.hr.dto.HRDepartmentDto(entity, false) " +
                "FROM HRDepartment entity WHERE entity.parent.id = :parentId " +
                "ORDER BY entity.sortNumber, entity.displayOrder ASC";
        List<HRDepartmentDto> children = manager.createQuery(hql, HRDepartmentDto.class)
                .setParameter("parentId", parent.getId())
                .getResultList();

        for (HRDepartmentDto child : children) {
            loadChildren(child);
        }
    }

    // Hàm chuẩn hóa chuỗi: loại bỏ khoảng trắng thừa, chuyển về chữ thường, loại bỏ dấu
    private String normalizeString(String input) {
        if (input == null) {
            return "";
        }
        // Chuyển về chữ thường và loại bỏ khoảng trắng thừa
        String normalized = input.toLowerCase().trim();
        // Loại bỏ dấu (nếu cần, tùy thuộc vào yêu cầu)
        normalized = normalized.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("đ", "d");
        return normalized;
    }

    private boolean searchAndKeepMatchingBranch(HRDepartmentDto node, SearchHrDepartmentDto dto) {
        if (dto == null) return false;

        // Kiểm tra node hiện tại có khớp không, có hiển thị dữ liệu không, mặc định là có
        boolean matched = true;

        if (StringUtils.hasText(dto.getKeyword())) {
            // Chuẩn hóa name và code trước khi so sánh
            String nodeName = normalizeString(node.getName());
            String nodeCode = normalizeString(node.getCode());

            String keyword = normalizeString(dto.getKeyword());

            matched = nodeName.contains(keyword) || nodeCode.contains(keyword);
        }

        if (dto.getHrDepartmentTypeId() != null) {
            matched = matched && (node.getHrDepartmentType() != null && node.getHrDepartmentType().getId().equals(dto.getHrDepartmentTypeId()));
        } else if (dto.getHrDepartmentTypeId() == null && StringUtils.hasText(dto.getHrDepartmentTypeCode())) {
            matched = matched && (node.getHrDepartmentType() != null && node.getHrDepartmentType().getCode().trim().toLowerCase().contains(dto.getHrDepartmentTypeCode().toLowerCase().trim()));
        }


        // Thêm log để debug
        // System.out.println("Node: " + node.getName() + " | Normalized Name: " + nodeName + " | Code: " + nodeCode + " | Keyword: " + keyword + " | Matched: " + matched);

        // Nếu không có children, trả về kết quả khớp của node hiện tại
        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            return matched;
        }

        // Lọc các children để chỉ giữ lại nhánh khớp
        List<HRDepartmentDto> filteredChildren = new ArrayList<>();
        boolean hasMatchingDescendant = false;

        for (HRDepartmentDto child : node.getChildren()) {
            if (searchAndKeepMatchingBranch(child, dto)) {
                filteredChildren.add(child);
                hasMatchingDescendant = true;
            }
        }

        // Gán lại danh sách children đã lọc
        node.setChildren(filteredChildren);

        // Giữ node này nếu nó khớp keyword hoặc có con/cháu khớp
        return matched || hasMatchingDescendant;
    }


    @Override
    public Workbook exportExcelDepartment(SearchHrDepartmentDto dto) {
        if (dto == null) {
            return null;
        }

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("Excel/MAU_PHONG_BAN.xlsx")) {
            if (fileInputStream == null) {
                throw new IOException("File '" + "Excel/MAU_PHONG_BAN.xlsx" + "' không tìm thấy trong classpath");
            }

            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet staffSheet = workbook.getSheetAt(0);
            CellStyle dataCellStyle = createDataCellStyle(workbook);

            // Start with root level departments
            dto.setPageIndex(1);
            dto.setPageSize(Integer.MAX_VALUE); // Lấy tất cả để xử lý đệ quy

            Page<HRDepartmentDto> pageDepartments = this.pagingDepartmentHierarchy(dto);
            if (pageDepartments == null || pageDepartments.isEmpty()) {
                return workbook; // Trả về workbook trống nếu không có dữ liệu
            }

            // Start writing from row 1 (after header)
            int[] rowIndex = {1};
            int[] orderNumber = {1};

            long startTime = System.nanoTime();

            // Lấy danh sách phòng ban đã phân cấp
            List<HRDepartmentDto> hierarchyDepartments = pageDepartments.getContent();

            // Xử lý danh sách phòng ban theo đệ quy
            processHierarchicalDepartments(hierarchyDepartments, staffSheet, dataCellStyle, rowIndex, orderNumber);

            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;

            logger.info("Xuất tất cả phòng ban - Xử lý mất {} ms ", elapsedTimeMs);
            return workbook;

        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }

    /**
     * Xử lý danh sách phòng ban đã phân cấp và ghi vào Excel
     */
    private void processHierarchicalDepartments(
            List<HRDepartmentDto> departments,
            Sheet sheet,
            CellStyle dataCellStyle,
            int[] rowIndex,
            int[] orderNumber) {

        if (departments == null || departments.isEmpty()) {
            return;
        }

        for (HRDepartmentDto department : departments) {
            // Ghi dữ liệu phòng ban hiện tại
            writeDepartmentToExcel(department, sheet, dataCellStyle, rowIndex, orderNumber);

            // Kiểm tra và xử lý các phòng ban con (nếu có)
            if (department.getChildren() != null && !department.getChildren().isEmpty()) {
                processHierarchicalDepartments(department.getChildren(), sheet, dataCellStyle, rowIndex, orderNumber);
            }
        }
    }

    /**
     * Ghi thông tin một phòng ban vào Excel
     */
    private void writeDepartmentToExcel(
            HRDepartmentDto departmentDto,
            Sheet sheet,
            CellStyle dataCellStyle,
            int[] rowIndex,
            int[] orderNumber) {

        if (departmentDto == null) return;

        Row dataRow = sheet.createRow(rowIndex[0]);

        // 0. STT
        int cellIndex = 0;
        createCell(dataRow, cellIndex++, orderNumber[0], dataCellStyle);
        orderNumber[0]++;

        // 1. Mã phòng ban
        this.createCell(dataRow, cellIndex++, departmentDto.getCode(), dataCellStyle);
        // 2. Tên phòng ban
        this.createCell(dataRow, cellIndex++, departmentDto.getName(), dataCellStyle);
        // 3. Tên viết tắt
        this.createCell(dataRow, cellIndex++, departmentDto.getShortName(), dataCellStyle);

        // 4. Mã đơn vị trực thuộc
        // 5. Đơn vị trực thuộc
        String orgCode = null;
        String orgName = null;
        if (departmentDto.getOrganization() != null) {
            orgCode = departmentDto.getOrganization().getCode();
            orgName = departmentDto.getOrganization().getName();
        }
        this.createCell(dataRow, cellIndex++, orgCode, dataCellStyle);
        this.createCell(dataRow, cellIndex++, orgName, dataCellStyle);

        // 6. Mã phòng ban cha
        // 7. Tên phòng ban cha
        String parentCode = null;
        String parentName = null;
        if (departmentDto.getParent() != null) {
            parentCode = departmentDto.getParent().getCode();
            parentName = departmentDto.getParent().getName();
        }
        this.createCell(dataRow, cellIndex++, parentCode, dataCellStyle);
        this.createCell(dataRow, cellIndex++, parentName, dataCellStyle);

        // 8. Mã loại phòng ban
        // 9. Loại phòng ban
        String departmentTypeCode = null;
        String departmentTypeName = null;
        if (departmentDto.getHrDepartmentType() != null) {
            departmentTypeCode = departmentDto.getHrDepartmentType().getCode();
            departmentTypeName = departmentDto.getHrDepartmentType().getName();
        }
        this.createCell(dataRow, cellIndex++, departmentTypeCode, dataCellStyle);
        this.createCell(dataRow, cellIndex++, departmentTypeName, dataCellStyle);

        // 10. Mã chức danh quản lý
        // 11. Chức danh quản lý
        String positionTitleManagerCode = null;
        String positionTitleManagerName = null;
        if (departmentDto.getPositionTitleManager() != null) {
            positionTitleManagerCode = departmentDto.getPositionTitleManager().getCode();
            positionTitleManagerName = departmentDto.getPositionTitleManager().getName();
        }
        this.createCell(dataRow, cellIndex++, positionTitleManagerCode, dataCellStyle);
        this.createCell(dataRow, cellIndex++, positionTitleManagerName, dataCellStyle);

        // 12. Ngày thành lập
        this.createCell(dataRow, cellIndex++, formatDate(departmentDto.getFoundedDate()), dataCellStyle);
        // 13. Thứ tự hiển thị
        this.createCell(dataRow, cellIndex++, departmentDto.getSortNumber(), dataCellStyle);
        // 14. Mô tả
        this.createCell(dataRow, cellIndex++, departmentDto.getDescription(), dataCellStyle);
        // 15.Mã chức danh thuộc phòng ban
        if (departmentDto.getPositionTitles() != null && !departmentDto.getPositionTitles().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (PositionTitleDto item : departmentDto.getPositionTitles()) {
                if (item != null && item.getCode() != null) {
                    sb.append(item.getCode()).append(";");
                }
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1); // Xóa dấu ';' cuối cùng
            }
            this.createCell(dataRow, cellIndex++, sb.toString(), dataCellStyle);
        } else {
            this.createCell(dataRow, cellIndex++, "", dataCellStyle);
        }

        // 16.Chức danh thuộc phòng ban
        if (departmentDto.getChildren() != null && !departmentDto.getChildren().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (HRDepartmentDto item : departmentDto.getChildren()) {
                if (item != null && item.getCode() != null) {
                    sb.append(item.getCode()).append(";");
                }
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1); // Xóa dấu ';' cuối cùng
            }
            this.createCell(dataRow, cellIndex++, sb.toString(), dataCellStyle);
        } else {
            this.createCell(dataRow, cellIndex++, "", dataCellStyle);
        }

        // 17.Mã ca làm việc
        if (departmentDto.getShiftWorks() != null && !departmentDto.getShiftWorks().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ShiftWorkDto item : departmentDto.getShiftWorks()) {
                if (item != null && item.getCode() != null) {
                    sb.append(item.getCode()).append(";");
                }
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1); // Xóa dấu ';' cuối cùng
            }
            this.createCell(dataRow, cellIndex++, sb.toString(), dataCellStyle);
        } else {
            this.createCell(dataRow, cellIndex++, "", dataCellStyle);
        }
        // Tăng chỉ số hàng cho phòng ban tiếp theo
        rowIndex[0]++;
    }


    private void createCell(Row row, int cellIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }
        cell.setCellStyle(style);
    }

    /**
     * Tạo style cho cell: border và font Times New Roman
     */
    private CellStyle createDataCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();

        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Tạo font Times New Roman
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);

        return cellStyle;
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    @Override
    public Page<HRDepartmentDto> pagingDepartmentPositionTreeView(SearchDto dto) {
        if (dto == null) {
            return null;
        }

        int pageIndex = Math.max(dto.getPageIndex() - 1, 0);
        int pageSize = dto.getPageSize();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        String whereClause = " WHERE entity.parent IS NULL ";
        String orderBy = " ORDER BY entity.sortNumber, entity.displayOrder ASC ";

        if (dto.getOrganizationId() != null) {
            whereClause += " AND entity.organization.id = :organizationId ";
        }

        String sql = "SELECT entity FROM HRDepartment entity " + whereClause + orderBy;
        String sqlCount = "SELECT count(entity.id) FROM HRDepartment entity " + whereClause;

        // Query chính
        Query query = manager.createQuery(sql, HRDepartment.class);
        Query countQuery = manager.createQuery(sqlCount);

        if (dto.getOrganizationId() != null) {
            query.setParameter("organizationId", dto.getOrganizationId());
            countQuery.setParameter("organizationId", dto.getOrganizationId());
        }

        // Set phân trang
        query.setFirstResult(pageIndex * pageSize);
        query.setMaxResults(pageSize);

        // Lấy dữ liệu phân trang
        List<HRDepartment> departments = query.getResultList();
        long totalCount = (long) countQuery.getSingleResult();

        // Lấy tất cả departments để xây dựng cây
        List<HRDepartment> allDepartments = hrDepartmentRepository.findAll();
        Map<UUID, HRDepartmentDto> dtoMap = new HashMap<>();

        // Lấy tất cả positions
        List<Position> positions = positionRepository.findByKeyword(dto.getKeyword());
        Map<UUID, Set<PositionDto>> positionsByDeptId = positions
                .stream()
                .filter(p -> p.getDepartment() != null) // Filter bỏ các position không có department
                .collect(Collectors.groupingBy(
                        p -> p.getDepartment().getId(),
                        Collectors.mapping(
                                p -> new PositionDto(p, false),
                                Collectors.toSet()
                        )
                ));

        // Tạo map DTO
        for (HRDepartment dept : allDepartments) {
            HRDepartmentDto departmentDto = new HRDepartmentDto();
            departmentDto.setId(dept.getId());
            departmentDto.setName(dept.getName());
            departmentDto.setCode(dept.getCode());
            if (dept.getParent() != null) {
                departmentDto.setParentId(dept.getParent().getId());
            }
            Set<PositionDto> positionDtos = positionsByDeptId.getOrDefault(dept.getId(), new HashSet<>());
            departmentDto.setPositions(positionDtos);
            departmentDto.setNumberOfPositions(departmentDto.getPositions().size());
            dtoMap.put(dept.getId(), departmentDto);
        }

        // Xây dựng cây
        List<HRDepartmentDto> rootDtos = new ArrayList<>();
        for (HRDepartmentDto deptDto : dtoMap.values()) {
            if (deptDto.getParentId() == null) {
                rootDtos.add(deptDto);
            } else {
                HRDepartmentDto parent = dtoMap.get(deptDto.getParentId());
                if (parent != null) {
                    parent.getChildren().add(deptDto);
                }
            }
        }

        // Áp dụng phân trang trên các root departments
        int fromIndex = (int) pageable.getOffset();
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), rootDtos.size());
        List<HRDepartmentDto> pagedRootDtos = rootDtos.subList(fromIndex, toIndex);

        return new PageImpl<>(pagedRootDtos, pageable, totalCount);
    }

    @Override
    public Boolean checkValidParent(SearchDto dto) {
        // If no parent is selected, it's valid (root department case)
        if (dto.getParentId() == null || dto.getTypeId() == null) {
            return true;
        }

        HRDepartment parent = hrDepartmentRepository.findById(dto.getParentId()).orElse(null);
        if (parent == null) {
            return false; // Invalid if parent doesn't exist
        }

        DepartmentType currentType = departmentTypeRepository.findById(dto.getTypeId()).orElse(null);
        if (currentType == null) {
            return false; // Invalid if department type doesn't exist
        }

        // If either department has no type/sortNumber, skip validation
        if (parent.getHrdepartmentType() == null || parent.getHrdepartmentType().getSortNumber() == null) {
            return true;
        }
        if (currentType.getSortNumber() == null) {
            return true;
        }

        Integer parentSortNumber = parent.getHrdepartmentType().getSortNumber();
        Integer currentSortNumber = currentType.getSortNumber();

        // Parent must have a HIGHER or EQUAL level (LOWER or EQUAL sortNumber)
        return parentSortNumber <= currentSortNumber;
    }

    @Override
    public String autoGenerateCode(String configKey) {
        SystemConfig systemConfig = systemConfigService.getConfigByConfigValue(configKey);
        if (systemConfig == null) {
            return "";
        } else {
            String maxCode = hrDepartmentRepository.findMaxCodeByPrefix(systemConfig.getConfigValue(), systemConfig.getNumberOfZero());
            return systemConfigService.generateNextCode(systemConfig.getConfigValue(), systemConfig.getNumberOfZero(), maxCode);
        }
    }


    // Lấy phòng ban quản lý
    @Override
    public List<HRDepartment> findAllDescendantsOfDepartment(UUID departmentId, String departmentCode) {
        if (departmentId == null && (departmentCode == null || departmentCode.isEmpty())) {
            return Collections.emptyList();
        }

        // Load all departments to build the tree
        List<HRDepartment> allDepartments = hrDepartmentRepository.findAll();

        // Map Parent ID -> List of children
        Map<UUID, List<HRDepartment>> childMap = allDepartments.stream()
                .filter(dept -> dept.getParent() != null && dept.getParent().getId() != null)
                .collect(Collectors.groupingBy(dept -> dept.getParent().getId()));

        // Determine root department
        HRDepartment root = null;
        if (departmentId != null) {
            root = hrDepartmentRepository.findById(departmentId).orElse(null);
        } else if (departmentCode != null && !departmentCode.isEmpty()) {
            List<HRDepartment> roots = hrDepartmentRepository.findByCode(departmentCode);
            if (roots != null && !roots.isEmpty()) {
                root = roots.get(0);
            }
        }

        if (root == null) {
            return Collections.emptyList();
        }

        List<HRDepartment> result = new ArrayList<>();
        result.add(root); // lấy cả chính nó
        collectDescendants(root, childMap, result);

        return result;
    }

    private void collectDescendants(HRDepartment parent, Map<UUID, List<HRDepartment>> childMap, List<HRDepartment> result) {
        List<HRDepartment> children = childMap.get(parent.getId());
        if (children != null) {
            result.addAll(children);
            for (HRDepartment child : children) {
                collectDescendants(child, childMap, result);
            }
        }
    }
}
