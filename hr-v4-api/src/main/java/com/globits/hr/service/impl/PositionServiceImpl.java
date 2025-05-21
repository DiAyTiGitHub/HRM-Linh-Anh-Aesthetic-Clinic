package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchPositionDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.PositionRelationshipService;
import com.globits.hr.service.PositionService;
import com.globits.hr.service.SystemConfigService;
import com.globits.hr.service.UserExtService;
import com.globits.hr.utils.ExportExcelUtil;
import com.globits.security.dto.UserDto;
import jakarta.persistence.Query;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.DtoInstantiatingConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PositionServiceImpl extends GenericServiceImpl<Position, UUID> implements PositionService {
    private static final Logger logger = LoggerFactory.getLogger(PositionDto.class);
    private static final String EXPORT_POSITIONS_TEMPLATE_PATH = "Excel/MAU_CHUC_VU.xlsx";
    private static final int EXPORT_PAGE_SIZE = 100;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private PositionTitleRepository positionTitleRepository;

    @Autowired
    private HRDepartmentRepository hrDepartmentRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PositionRelationshipService positionRelationshipService;

    @Autowired
    private RecruitmentRequestRepository recruitmentRequestRepository;

    @Autowired
    private StaffWorkingHistoryRepository staffWorkingHistoryRepository;

    @Autowired
    private UserExtService userExtService;

    @Autowired
    private PositionRelationShipRepository positionRelationShipRepository;

    @Autowired
    private SystemConfigService systemConfigService;

    public PositionDto getById(UUID id) {
        if (id == null) return null;
        Position entity = positionRepository.findById(id).orElse(null);

        if (entity == null) return null;
        PositionDto response = new PositionDto(entity, true);

        return response;
    }


    @Override
    @Modifying
    public PositionDto savePosition(PositionDto dto) {
        if (dto == null) {
            return null;
        }

        Position entity = new Position();
        if (dto.getId() != null) entity = positionRepository.findById(dto.getId()).orElse(null);
        if (entity == null) entity = new Position();

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());

        if (dto.getTitle() != null && dto.getTitle().getId() != null) {
            PositionTitle positionTitle = positionTitleRepository.findById(dto.getTitle().getId()).orElse(null);
            if (positionTitle == null) return null;
            entity.setTitle(positionTitle);
        } else {
            entity.setTitle(null);
        }

        if (dto.getDepartment() != null && dto.getDepartment().getId() != null) {
            HRDepartment department = hrDepartmentRepository.findById(dto.getDepartment().getId()).orElse(null);
            if (department == null) return null;
            entity.setDepartment(department);
        } else {
            entity.setDepartment(null);
        }

        Staff staff = null;
        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
            if (staff == null) return null;
            entity.setStaff(staff);
        } else {
            entity.setStaff(null);
        }

        // 1 nhân viên chỉ có 1 vị chính duy nhất
        if (Boolean.TRUE.equals(dto.getIsMain()) && staff != null) {
            List<Position> existingMainPositions = positionRepository.findMainPositionByStaffId(staff.getId());
            if (!existingMainPositions.isEmpty()) {
                for (Position existingMainPosition : existingMainPositions) {
                    existingMainPosition.setIsMain(false);
                    positionRepository.save(existingMainPosition);
                }
            }
        }
        entity.setIsMain(dto.getIsMain());
        entity.setIsConcurrent(dto.getIsConcurrent());
        entity.setIsTemporary(dto.getIsTemporary());


        // Mối quan hệ trong chức vụ
        positionRelationshipService.handleSetRelationshipsInPotion(dto, entity);

        entity = positionRepository.save(entity);

        return new PositionDto(entity);
    }

    @Override
    public Integer saveListPosition(List<PositionDto> dtos) {
        int result = 0;

        if (dtos == null || dtos.isEmpty()) return result;

        for (PositionDto dto : dtos) {
            Position entity = null;
            if (dto.getCode() != null && !dto.getCode().isEmpty()) {
                List<Position> positions = positionRepository.findByCode(dto.getCode());
                if (positions != null && !positions.isEmpty()) {
                    entity = positions.get(0);
                }
            } else {
                continue;
            }
            if (entity == null) {
                entity = new Position();
                entity.setCode(dto.getCode());
            }
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity.setIsMain(dto.getIsMain());
            entity.setIsTemporary(dto.getIsTemporary());
            entity.setIsConcurrent(dto.getIsConcurrent());

            if (dto.getDepartment() != null && dto.getDepartment().getCode() != null) {
                List<HRDepartment> departments = hrDepartmentRepository.findByCode(dto.getDepartment().getCode());
                if (!departments.isEmpty()) {
                    HRDepartment department = departments.get(0);
                    entity.setDepartment(department);
                }
            }

            if (dto.getTitle() != null && dto.getTitle().getCode() != null) {
                List<PositionTitle> positionTitles = positionTitleRepository.findByCode(dto.getTitle().getCode());
                if (!positionTitles.isEmpty()) {
                    PositionTitle positionTitle = positionTitles.get(0);
                    entity.setTitle(positionTitle);
                }
            }

            if (dto.getStaff() != null && dto.getStaff().getStaffCode() != null) {
                List<Staff> staffs = staffRepository.getByCode(dto.getStaff().getStaffCode());
                if (!staffs.isEmpty()) {
                    Staff staff = staffs.get(0);
                    entity.setStaff(staff);
                }
            }

            if (entity.getRelationships() == null) {
                entity.setRelationships(new HashSet<>());
            }
            entity.getRelationships().clear();
            if (dto.getRelationships() != null && !dto.getRelationships().isEmpty()) {
                for (PositionRelationshipDto relationshipDto : dto.getRelationships()) {
                    PositionRelationShip relationShip = new PositionRelationShip();
                    relationShip.setPosition(entity);
                    if (relationshipDto.getRelationshipType() != null) {
                        if (relationshipDto.getSupervisor() != null && relationshipDto.getSupervisor().getCode() != null) {
                            List<Position> positionList = positionRepository.findByCode(relationshipDto.getSupervisor().getCode());
                            if (positionList != null && !positionList.isEmpty()) {
                                Position supervisor = positionList.get(0);
                                relationShip.setSupervisor(supervisor);
                            } else {
                                continue;
                            }
                        }
                    }

                    relationShip.setRelationshipType(relationshipDto.getRelationshipType());

                    entity.getRelationships().add(relationShip);
                }
            }

            positionRepository.save(entity);

            result++;
        }
        return result;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deletePosition(UUID id) {
        if (id == null) return false;

        Position entity = positionRepository.findById(id).orElse(null);
        if (entity == null) return false;

        positionRepository.delete(entity);
        return true;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteMultiplePositions(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID itemId : ids) {
            boolean deleteRes = this.deletePosition(itemId);
            if (!deleteRes) isValid = false;
        }
        return isValid;
    }

    @Override
    public Page<PositionDto> pagingPosition(SearchPositionDto dto) {
        if (dto == null) {
            return null;
        }
        UUID currentStaffId = null;
        if (dto.getIsPublic() == null || !dto.getIsPublic()) {
            StaffDto staffDto = userExtService.getCurrentStaff();
            if (staffDto != null && staffDto.getId() != null) {
                currentStaffId = staffDto.getId();
            }
        }


        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String whereClause = " WHERE (1=1) ";
        String sqlCount = "SELECT COUNT(DISTINCT entity.id) FROM Position AS entity ";
        String sql = "";
        if (dto.getExportExcel() != null && dto.getExportExcel()) {
            sql = "SELECT DISTINCT new com.globits.hr.dto.PositionDto(entity, true) FROM Position AS entity ";
        } else {
            sql = "SELECT DISTINCT new com.globits.hr.dto.PositionDto(entity) FROM Position AS entity ";
        }
        String leftJoin = " LEFT JOIN entity.staff staff " +
                " LEFT JOIN entity.title title " +
                " LEFT JOIN entity.department department ";
        sqlCount += leftJoin;
        sql += leftJoin;

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.name LIKE :text " +
                    "OR entity.code LIKE :text " +
                    "OR entity.description LIKE :text " +
                    "OR staff.displayName LIKE :text " +
                    "OR title.name LIKE :text) ";
        }

        if (dto.getOrganizationId() != null) {
            whereClause += " AND (entity.department.organization.id = :organizationId) ";
        }
        if (dto.getPositionTitleId() != null) {
            whereClause += " AND (title.id = :positionTitleId) ";
        }
        if (dto.getRankTitleId() != null) {
            whereClause += " AND (title.rankTitle.id = :rankTitleId) ";
        }
        if (dto.getStaffId() != null) {
            whereClause += " AND (staff.id = :staffId) ";
        }
        if (dto.getDepartmentId() != null || dto.getDepartmentCode() != null) {
            List<String> conditions = new ArrayList<>();

            if (dto.getDepartmentId() != null) {
                conditions.add("entity.department.id = :departmentId");
            }
            if (dto.getDepartmentCode() != null) {
                conditions.add("entity.department.code = :departmentCode");
            }

            if (!conditions.isEmpty()) {
                whereClause += " AND ( " + String.join(" OR ", conditions) + " ) ";
            }
        }

        if (dto.isGetOwn() && currentStaffId != null) {
            whereClause += " AND (staff.id = :currentStaffId) ";
        }
        if (dto.getVacant() != null) {
            if (dto.getVacant()) {
                whereClause += " AND (staff.id IS NULL) ";
            } else {
                whereClause += " AND (staff.id IS NOT NULL) ";
            }
        }
        if (dto.getOldPosition() != null && dto.getOldPosition()) {
            whereClause += " AND (entity.staff.id IS NOT NULL OR entity.previousStaff.id IS NOT NULL) ";
        }

        sql += whereClause;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, PositionDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getDepartmentId() != null) {
            query.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getDepartmentCode() != null) {
            query.setParameter("departmentCode", dto.getDepartmentCode());
        }

        if (dto.getOrganizationId() != null) {
            query.setParameter("organizationId", dto.getOrganizationId());
            qCount.setParameter("organizationId", dto.getOrganizationId());
        }
        if (dto.getPositionTitleId() != null) {
            query.setParameter("positionTitleId", dto.getPositionTitleId());
            qCount.setParameter("positionTitleId", dto.getPositionTitleId());
        }
        if (dto.getRankTitleId() != null) {
            query.setParameter("rankTitleId", dto.getRankTitleId());
            qCount.setParameter("rankTitleId", dto.getRankTitleId());
        }
        if (dto.getStaffId() != null) {
            query.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }
        if (dto.isGetOwn() && currentStaffId != null) {
            query.setParameter("currentStaffId", currentStaffId);
            qCount.setParameter("currentStaffId", currentStaffId);
        }
        // Get all departments
        List<PositionDto> allPosition = query.getResultList();
        Collections.sort(allPosition, new Comparator<PositionDto>() {
            @Override
            public int compare(PositionDto o1, PositionDto o2) {
                // Check if either position is a department manager
                boolean o1IsManager = o1.isDepartmentManager() != null && o1.isDepartmentManager();
                boolean o2IsManager = o2.isDepartmentManager() != null && o2.isDepartmentManager();

                if (o1IsManager && !o2IsManager) {
                    // o1 is a manager but o2 isn't, so o1 comes first
                    return -1;
                } else if (!o1IsManager && o2IsManager) {
                    // o2 is a manager but o1 isn't, so o2 comes first
                    return 1;
                } else {
                    // Cả hai đều là manager hoặc đều không là manager
                    // Sắp xếp theo createDate (điều kiện thứ 2)
                    if (o1.getCreateDate() != null && o2.getCreateDate() != null) {
                        // Sắp xếp từ mới đến cũ (giảm dần)
                        return o2.getCreateDate().compareTo(o1.getCreateDate());
                    } else if (o1.getCreateDate() != null) {
                        return -1; // o1 có ngày tạo, o2 không
                    } else if (o2.getCreateDate() != null) {
                        return 1;  // o2 có ngày tạo, o1 không
                    }
                    return 0;
                }
            }
        });
        // Get total count after filtering
        long totalCount = allPosition.size();

        // Manually paginate the result
        int fromIndex = Math.min(pageIndex * pageSize, allPosition.size());
        int toIndex = Math.min(fromIndex + pageSize, allPosition.size());

        List<PositionDto> pagedDepartments = allPosition.subList(fromIndex, toIndex);


        // Create page object
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(pagedDepartments, pageable, totalCount);
    }


    @Override
    public PositionDto findByCode(String code) {
        List<Position> entities = positionRepository.findByCode(code);
        if (entities != null && entities.size() > 0) {
            return new PositionDto(entities.get(0));
        }
        return null;
    }

    @Override
    public PositionDto setupDataPosition(PositionDto dto) {
        if (dto == null || dto.getCode() == null) return null;
        PositionDto existedDuplicateCode = this.findByCode(dto.getCode());

        Position entity = null;
        if (existedDuplicateCode != null) {
            entity = positionRepository.findById(existedDuplicateCode.getId()).orElse(null);
        } else {
            entity = new Position();
        }

        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setName(dto.getName());

        if (dto.getDepartment() != null && dto.getDepartment().getCode() != null) {
            List<HRDepartment> availableDepartments = hrDepartmentRepository.findByCode(dto.getDepartment().getCode());
            if (availableDepartments != null && availableDepartments.size() > 0) {
                HRDepartment department = availableDepartments.get(0);
                entity.setDepartment(department);
            }
        } else {
            entity.setDepartment(null);
        }

        if (dto.getTitle() != null && dto.getTitle().getCode() != null) {
            List<PositionTitle> availableTitles = positionTitleRepository.findByCode(dto.getTitle().getCode());
            if (availableTitles != null && !availableTitles.isEmpty()) {
                PositionTitle title = availableTitles.get(0);
                entity.setTitle(title);
            }
        } else {
            entity.setTitle(null);
        }

        Position response = positionRepository.save(entity);
        return new PositionDto(response);
    }

    @Override
    public List<Position> createTemporaryPositionsFromRecruitmentRequest(UUID recruitmentRequestId) {
        if (recruitmentRequestId == null) return null;
        RecruitmentRequest recruitmentRequest = recruitmentRequestRepository.findById(recruitmentRequestId).orElse(null);

        if (recruitmentRequest == null || recruitmentRequest.getRecruitmentRequestItems() == null || recruitmentRequest.getRecruitmentRequestItems().size() <= 0)
            return null;

        List<Position> needSaveEntities = new ArrayList<>();
        for (RecruitmentRequestItem item : recruitmentRequest.getRecruitmentRequestItems()) {
            Integer extraQuantity = item.getExtraQuantity();
            if (extraQuantity == null || extraQuantity == 0) return null;


            for (int i = 1; i <= extraQuantity; i++) {
                Position position = new Position();

                position.setIsTemporary(true);
                position.setDepartment(recruitmentRequest.getHrDepartment());
                position.setTitle(item.getPositionTitle());

                if (item.getPositionTitle() != null) {
                    position.setCode(item.getPositionTitle().getCode());
                    position.setName(item.getPositionTitle().getName());
                    position.setDescription(item.getPositionTitle().getDescription());

                }

                needSaveEntities.add(position);
            }
        }

        List<Position> response = positionRepository.saveAll(needSaveEntities);

        return response;
    }


    @Override
    public Boolean isValidCode(PositionDto dto) {
        if (dto == null) return false;

        // ID of Position is null => Create new Position
        // => Assure that there's no other Positions using this code of new Position
        // if there was any Position using new Position code, then this new code is
        // invalid => return False
        // else return true
        if (dto.getId() == null) {
            List<Position> entities = positionRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        }
        // ID of Position is NOT null => SalaryItem is modified
        // => Assure that the modified code is not same to OTHER any Position's code
        // if there was any Position using new Position code, then this new code is
        // invalid => return False
        // else return true
        else {
            List<Position> entities = positionRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (Position entity : entities) {
                if (!entity.getId().equals(dto.getId())) return false;
            }
        }
        return true;
    }

    @Override
    @Transactional
    public PositionDto removeStaffFromPosition(UUID positionId) {
        if (positionId == null) return null;

        Position position = positionRepository.findById(positionId).orElse(null);
        if (position == null) return null;

        Staff staff = position.getStaff();
        if (staff == null) return null;

        findHistoryAndUpdateFromPosition(position);
        // Update position
        position.setIsMain(false);
        position.setPreviousStaff(staff);
        position.setStaff(null);
        position = positionRepository.save(position);

        return new PositionDto(position);
    }

    @Override
    public StaffWorkingHistory findHistoryAndUpdateFromPosition(Position position) {
        if (position == null) return null;
        Staff staff = position.getStaff();
        if (staff == null) return null;
        // Find existing active StaffWorkingHistory record (where endDate is null)
        List<StaffWorkingHistory> existingHistory = staffWorkingHistoryRepository
                .findByStaffAndToPositionAndEndDateIsNull(staff.getId(), position.getId());
        StaffWorkingHistory history = null;

        if (existingHistory != null && !existingHistory.isEmpty()) {
            // If found, update the endDate to today
            history = existingHistory.get(0);
            history.setEndDate(new Date());
        } else {
            history = new StaffWorkingHistory();
            history.setStaff(staff);
            history.setToPosition(position);
            history.setEndDate(new Date());
        }

        if (position.getDepartment() != null) {
            history.setToDepartment(position.getDepartment());
            if (position.getDepartment().getOrganization() != null) {
                history.setToOrganization(position.getDepartment().getOrganization());
            }
        }
        staffWorkingHistoryRepository.save(history);
        return history;
    }

    @Override
    public String autoGenerateCode(String configKey) {
        SystemConfig systemConfig = systemConfigService.getConfigByConfigValue(configKey);
        if (systemConfig == null) {
            return "";
        } else {
            String maxCode = positionRepository.findMaxCodeByPrefix(systemConfig.getConfigValue(), systemConfig.getNumberOfZero());
            return systemConfigService.generateNextCode(systemConfig.getConfigValue(), systemConfig.getNumberOfZero(), maxCode);
        }
    }

    @Override
    @Transactional
    public List<PositionDto> assignPositionsForStaff(SearchPositionDto dto) {
        if (dto == null || dto.getStaffId() == null || dto.getChosenIds() == null) return null;

        Staff staff = staffRepository.findById(dto.getStaffId()).orElse(null);
        if (staff == null) return null;

        Set<UUID> chosenIdsSet = new HashSet<>(dto.getChosenIds());
        boolean hasMain = false;

        // Khởi tạo danh sách lịch sử và currentPositions
        Set<StaffWorkingHistory> listHistory = new HashSet<>();
        Set<Position> listPosition = new HashSet<>();
        // Xử lý các vị trí hiện tại không có trong chosenIds
        Set<Position> currentPositions = staff.getCurrentPositions() != null ? staff.getCurrentPositions() : new HashSet<>();
        for (Position oldPosition : currentPositions) {
            if (!chosenIdsSet.contains(oldPosition.getId())) {
                findHistoryAndUpdateFromPosition(oldPosition);
                oldPosition.setPreviousStaff(staff);
                oldPosition.setStaff(null);
                listPosition.add(oldPosition);
            }
        }

        // Xử lý các vị trí mới trong chosenIds
        for (UUID positionId : dto.getChosenIds()) {
            Position position = positionRepository.findById(positionId).orElse(null);
            if (position == null) continue;

            if (position.getStaff() == null) {
                StaffWorkingHistory history = createHistory(staff, position, null, HrConstants.StaffWorkingHistoryTransferType.EXTERNAL_ORG.getValue());
                listHistory.add(history);
            } else if (!position.getStaff().getId().equals(staff.getId())) {
                Staff oldStaff = position.getStaff();
                StaffWorkingHistory historyNewStaff = createHistory(staff, position, null, HrConstants.StaffWorkingHistoryTransferType.EXTERNAL_ORG.getValue());
                StaffWorkingHistory historyOldStaff = createHistory(oldStaff, null, position, HrConstants.StaffWorkingHistoryTransferType.END_POSITION.getValue());
                //găn ng tiền nhiệm
                position.setPreviousStaff(oldStaff);

                listHistory.add(historyNewStaff);
                listHistory.add(historyOldStaff);
            }


            position.setStaff(staff);
            if (position.getIsMain() != null && position.getIsMain()) {
                if (hasMain) position.setIsMain(false);
                else hasMain = true;
            }
            listPosition.add(position);
        }

        // If only one position, set it as main
        if (listPosition.size() == 1) {
            listPosition.iterator().next().setIsMain(true);
        }

        // Lưu danh sách lịch sử trước khi cập nhật staff
        staffWorkingHistoryRepository.saveAll(listHistory);
        positionRepository.saveAll(listPosition);

        // Convert to DTO if needed
        return listPosition.stream()
                .map(PositionDto::new)
                .collect(Collectors.toList());
    }

    // Phương thức hỗ trợ tạo lịch sử
    private StaffWorkingHistory createHistory(Staff staff, Position toPosition, Position fromPosition, int transferType) {
        StaffWorkingHistory history = new StaffWorkingHistory();
        history.setStaff(staff);
        history.setToPosition(toPosition);
        history.setFromPosition(fromPosition);
        if (toPosition != null && toPosition.getDepartment() != null) {
            history.setToDepartment(toPosition.getDepartment());
            if (toPosition.getDepartment().getOrganization() != null) {
                history.setToOrganization(toPosition.getDepartment().getOrganization());
            }
        }
        if (fromPosition != null && fromPosition.getDepartment() != null) {
            history.setFromDepartment(fromPosition.getDepartment());
            if (fromPosition.getDepartment().getOrganization() != null) {
                history.setFromOrganization(fromPosition.getDepartment().getOrganization());
            }
        }
        history.setStartDate(new Date());
        history.setTransferType(transferType);
        return history;
    }

    @Override
    public Long countNumberOfPositionInDepartmentWithPositionTitle(SearchPositionDto searchDto) {
        if (searchDto.getDepartmentId() == null || searchDto.getPositionTitleId() == null) return null;

        Long count = positionRepository.countNumberOfPositionInDepartmentWithPositionTitle(searchDto.getDepartmentId(), searchDto.getPositionTitleId());

        if (count != null) return count;
        return null;
    }

    @Override
    public Set<HrResourcePlanItemDto> getResourcePlanItem(UUID departmentId) {
        if (departmentId == null) return null;

        HRDepartment department = hrDepartmentRepository.findById(departmentId).orElse(null);
        if (department == null) return null;
        HRDepartmentDto departmentDto = new HRDepartmentDto(department);

        Set<HrResourcePlanItemDto> res = new HashSet<>();
        for (PositionTitleDto positionTitleDto : departmentDto.getPositionTitles()) {
            HrResourcePlanItemDto item = new HrResourcePlanItemDto();
            item.setPositionTitle(positionTitleDto);

            // số lượng định bin
            List<Position> listPosition = positionRepository.findByDepartmentIdAndPositionTitleId(departmentId, positionTitleDto.getId());
            if (listPosition != null && !listPosition.isEmpty()) {
                int currentNumber = 0;

                for (Position position : listPosition) {
                    if (position.getStaff() != null) {
                        currentNumber++;
                    }
                }

                // số lượng định biên (tất cả position của chức danh và phòng ban đó)
                item.setCurrentPositionNumber(listPosition.size());

                //số lượng thực tế (tất cả position có staff của chức danh và phòng ban đó)
                item.setCurrentStaffNumber(currentNumber);
            } else {
                // số lượng định biên (tất cả position của chức danh và phòng ban đó)
                item.setCurrentPositionNumber(0);

                //số lượng thực tế (tất cả position có staff của chức danh và phòng ban đó)
                item.setCurrentStaffNumber(0);
            }

            res.add(item);
        }
        return res;
    }

    @Override
    public HashMap<UUID, PositionMainDto> getPositionMainMap() {
        HashMap<UUID, PositionMainDto> map = new HashMap<>();

        try {
            List<Object[]> results = positionRepository.findAllPositionMainNative(
                    HrConstants.PositionRelationshipType.UNDER_DIRECT_MANAGEMENT.getValue()
            );

            for (Object[] row : results) {
                try {
                    UUID staffId = row[0] != null ? UUID.fromString(row[0].toString()) : null;
                    UUID positionId = row[1] != null ? UUID.fromString(row[1].toString()) : null;
                    String positionName = (String) row[2];
                    String positionCode = (String) row[3];
                    String departmentName = (String) row[4];
                    String departmentCode = (String) row[5];
                    String positionTitleName = (String) row[6];
                    String positionTitleCode = (String) row[7];
                    String rankTitleName = (String) row[8];
                    String supervisorName = (String) row[9];
                    String supervisorCode = (String) row[10];
                    String supervisorStaffCode = (String) row[11];
                    String supervisorStaffDisplayName = (String) row[12];
                    String positionTitleGroupCode = (String) row[13];
                    String positionTitleGroupName = (String) row[14];

                    if (staffId == null) {
                        continue;
                    }

                    PositionMainDto dto = new PositionMainDto(
                            staffId,
                            positionId,
                            positionName,
                            positionCode,
                            departmentName,
                            departmentCode,
                            positionTitleName,
                            positionTitleCode,
                            rankTitleName,
                            supervisorName,
                            supervisorCode,
                            supervisorStaffCode,
                            supervisorStaffDisplayName,
                            positionTitleGroupCode,
                            positionTitleGroupName
                    );

                    map.put(staffId, dto);
                } catch (Exception rowEx) {
                    System.err.println("Error processing row in getPositionMainMap: " + rowEx.getMessage());
                    //rowEx.printStackTrace();
                    return null;
                }
            }

        } catch (Exception ex) {
            System.err.println("Error executing getPositionMainMap: " + ex.getMessage());
            //ex.printStackTrace();
            return null;
        }

        return map;
    }


    @Override
    public List<PositionDto> transferPositions(TransferPositionsDto dto) {
        if (dto == null) return null;

        if (dto.getDepartment() == null || dto.getDepartment().getId() == null) return null;
        if (dto.getPositions() == null || dto.getPositions().isEmpty()) return null;
        HRDepartment department = hrDepartmentRepository.findById(dto.getDepartment().getId()).orElse(null);
        if (department == null) return null;

        List<PositionDto> res = new ArrayList<>();
        for (PositionDto positionDto : dto.getPositions()) {
            Position position = positionRepository.findById(positionDto.getId()).orElse(null);
            if (position == null) continue;

            position.setDepartment(department);

            position = positionRepository.save(position);

            res.add(new PositionDto(position));
        }
        return res;
    }

    @Override
    @Transactional
    public List<PositionDto> transferStaff(TransferStaffDto dto) {
        if (dto == null) return null;
        if (dto.getToPosition() == null || dto.getToPosition().getId() == null) return null;
        if (dto.getFromPosition() == null || dto.getFromPosition().getId() == null) return null;

        Position fromPosition = positionRepository.findById(dto.getFromPosition().getId()).orElse(null);
        Position toPosition = positionRepository.findById(dto.getToPosition().getId()).orElse(null);

        if (fromPosition == null || toPosition == null) return null;
        if (fromPosition.getStaff() == null) return null;

        Staff staff = fromPosition.getStaff();
        // Cập nhật thông tin position
        findHistoryAndUpdateFromPosition(fromPosition);
        if (toPosition.getStaff() != null) {
            findHistoryAndUpdateFromPosition(toPosition);
        }
        fromPosition.setStaff(null);
        toPosition.setStaff(staff);

        // Thêm quá trình công tác
        StaffWorkingHistory history = new StaffWorkingHistory();
        history.setStaff(staff);
        history.setFromPosition(fromPosition);
        history.setToPosition(toPosition);
        history.setStartDate(new Date());
        history.setNote(dto.getNote());

        HrOrganization fromOrganization = null;
        if (fromPosition.getDepartment() != null) {
            history.setFromDepartment(fromPosition.getDepartment());
            if (fromPosition.getDepartment().getOrganization() != null) {
                fromOrganization = fromPosition.getDepartment().getOrganization();
                history.setFromOrganization(fromOrganization);
            }
        }

        HrOrganization toOrganization = null;
        if (toPosition.getDepartment() != null) {
            history.setToDepartment(toPosition.getDepartment());
            if (toPosition.getDepartment().getOrganization() != null) {
                toOrganization = toPosition.getDepartment().getOrganization();
                history.setToOrganization(toOrganization);
            }
        }

        // Xác định loại điều chuyển
        if (fromOrganization != null && toOrganization != null) {
            if (fromOrganization.getId().equals(toOrganization.getId())) {
                history.setTransferType(HrConstants.StaffWorkingHistoryTransferType.INTERNAL_ORG.getValue());
            } else {
                history.setTransferType(HrConstants.StaffWorkingHistoryTransferType.EXTERNAL_ORG.getValue());
            }
        } else if (toOrganization == null) {
            history.setTransferType(HrConstants.StaffWorkingHistoryTransferType.PAUSE_TEMPORARY.getValue());
        } else {
            // Trường hợp không xác định được (mặc định INTERNAL_ORG nếu cần)
            history.setTransferType(HrConstants.StaffWorkingHistoryTransferType.INTERNAL_ORG.getValue());
        }
        history = staffWorkingHistoryRepository.save(history);

        // Lưu các position đã cập nhật
        fromPosition = positionRepository.save(fromPosition);
        toPosition = positionRepository.save(toPosition);

        // Trả về danh sách PositionDto
        List<PositionDto> result = new ArrayList<>();
        result.add(new PositionDto(fromPosition));
        result.add(new PositionDto(toPosition));

        return result;
    }


    @Override
    public Workbook exportExcelPosition(SearchPositionDto dto) {
        if (dto == null) {
            return null;
        }

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(EXPORT_POSITIONS_TEMPLATE_PATH)) {
            if (fileInputStream == null) {
                throw new IOException("File '" + EXPORT_POSITIONS_TEMPLATE_PATH + "' không tìm thấy trong classpath");
            }

            Workbook workbook = new XSSFWorkbook(fileInputStream);

            Sheet staffSheet = workbook.getSheetAt(0);
            CellStyle dataCellStyle = ExportExcelUtil.createDataCellStyle(workbook);

            int pageIndex = 1;
            int rowIndex = 1;
            //int orderNumber = 1; // STT
            boolean hasNextPage = true;
            long startTime = System.nanoTime();

            while (hasNextPage) {
                dto.setPageIndex(pageIndex);
                dto.setPageSize(EXPORT_PAGE_SIZE);

                dto.setExportExcel(true);
                Page<PositionDto> pagePosition = this.pagingPosition(dto);
                if (pagePosition == null || pagePosition.isEmpty()) {
                    break;
                }

                for (PositionDto positionDto : pagePosition) {
                    if (positionDto == null) continue;

                    Row dataRow = staffSheet.createRow(rowIndex);
                    int cellIndex = 0;

                    // 1. Mã Tên chức vụ
                    ExportExcelUtil.createCell(dataRow, cellIndex++, positionDto.getCode(), dataCellStyle);
                    // 2. Tên chức vụ
                    ExportExcelUtil.createCell(dataRow, cellIndex++, positionDto.getName(), dataCellStyle);

                    // 3. Mã phòng ban, 4. Tên phòng ban
                    String departmentCode = null;
                    String departmentName = null;
                    if (positionDto.getDepartment() != null) {
                        departmentCode = positionDto.getDepartment().getCode();
                        departmentName = positionDto.getDepartment().getName();
                    }
                    ExportExcelUtil.createCell(dataRow, cellIndex++, departmentCode, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, departmentName, dataCellStyle);

                    // 5.6. Chức danh
                    String positionTitleCode = null;
                    String positionTitleName = null;
                    if (positionDto.getTitle() != null) {
                        positionTitleCode = positionDto.getTitle().getCode();
                        positionTitleName = positionDto.getTitle().getName();
                    }
                    ExportExcelUtil.createCell(dataRow, cellIndex++, positionTitleCode, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, positionTitleName, dataCellStyle);

                    // 7.8. Nhân viên
                    String staffCode = null;
                    String staffName = null;
                    if (positionDto.getStaff() != null) {
                        staffCode = positionDto.getStaff().getStaffCode();
                        staffName = positionDto.getStaff().getDisplayName();
                    }
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staffCode, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staffName, dataCellStyle);

                    // 9. 10. vị trí
                    String isMainStr = null;
                    String isTemporaryStr = null;
                    String isConcurrentStr = null;
                    if (positionDto.getIsMain() != null && positionDto.getIsMain() == true) {
                        isMainStr = "X";
                    }
                    if (positionDto.getIsTemporary() != null && positionDto.getIsTemporary() == true) {
                        isTemporaryStr = "X";
                    }
                    if (positionDto.getIsConcurrent() != null && positionDto.getIsConcurrent() == true) {
                        isConcurrentStr = "X";
                    }
                    ExportExcelUtil.createCell(dataRow, cellIndex++, isMainStr, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, isTemporaryStr, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, isConcurrentStr, dataCellStyle);

                    // 12. Mô tả
                    ExportExcelUtil.createCell(dataRow, cellIndex++, positionDto.getDescription(), dataCellStyle);

                    // 13.Chức vụ quản lý trực tiếp
                    String underDirectManagementPositionCode = null;
                    String underDirectManagementPositionName = null;
                    if (positionDto.getRelationships() != null && !positionDto.getRelationships().isEmpty()) {
                        for (PositionRelationshipDto positionRelationshipDto : positionDto.getRelationships()) {
                            if (positionRelationshipDto != null
                                    && positionRelationshipDto.getRelationshipType().equals(HrConstants.PositionRelationshipType.UNDER_DIRECT_MANAGEMENT.getValue())) {
                                if (positionRelationshipDto.getSupervisor() != null) {
                                    underDirectManagementPositionCode = positionRelationshipDto.getSupervisor().getCode();
                                    underDirectManagementPositionName = positionRelationshipDto.getSupervisor().getName();
                                    break;
                                }

                            }
                        }
                    }
                    ExportExcelUtil.createCell(dataRow, cellIndex++, underDirectManagementPositionCode, dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, underDirectManagementPositionName, dataCellStyle);

                    // thêm dòng tiếp theo
                    rowIndex++;
                }

                hasNextPage = pagePosition.hasNext(); // Kiểm tra xem còn trang tiếp theo không
                pageIndex++; // Tăng pageIndex để lấy trang tiếp theo
            }
            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;

            logger.info("Xuất danh danh sách chức vụ - Xử lý mất {} ms ", elapsedTimeMs);
            return workbook;

        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }


    @Override
    public int saveListImportExcel(List<ImportPositionRelationShipDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return 0;
        }

        // Lấy tất cả position và department hiện có trước để dùng lại
        Map<String, Position> existingPositions = new HashMap<String, Position>();
        List<Position> positions = positionRepository.findAll();
        for (Position position : positions) {
            if (position.getCode() != null && !position.getCode().isEmpty()) {
                existingPositions.put(position.getCode(), position);
            }
        }

        Map<String, HRDepartment> existingDepartments = new HashMap<String, HRDepartment>();
        List<HRDepartment> departments = hrDepartmentRepository.findAll();
        for (HRDepartment department : departments) {
            if (department.getCode() != null && !department.getCode().isEmpty()) {
                existingDepartments.put(department.getCode(), department);
            }
        }

        // Lọc các DTO hợp lệ
        List<ImportPositionRelationShipDto> validDtos = new ArrayList<ImportPositionRelationShipDto>();
        for (ImportPositionRelationShipDto dto : dtos) {
            if (isValidDto(dto, existingPositions, existingDepartments)) {
                validDtos.add(dto);
            }
        }

        if (validDtos.isEmpty()) {
            return 0;
        }

        // Lấy tất cả relationship hiện có để check trùng
        List<PositionRelationShip> existingRelationships = positionRelationShipRepository.findAll();
        Map<String, PositionRelationShip> relationshipMap = new HashMap<String, PositionRelationShip>();
        for (PositionRelationShip relationship : existingRelationships) {
            // Kiểm tra các code trước khi tạo key
            String positionCode = (relationship.getPosition() != null && isValidCode(relationship.getPosition().getCode()))
                    ? relationship.getPosition().getCode() : null;
            String supervisorCode = (relationship.getSupervisor() != null && isValidCode(relationship.getSupervisor().getCode()))
                    ? relationship.getSupervisor().getCode() : null;
            String departmentCode = (relationship.getDepartment() != null && isValidCode(relationship.getDepartment().getCode()))
                    ? relationship.getDepartment().getCode() : null;

            // Chỉ tạo key và thêm vào map nếu ít nhất 2 code hợp lệ
            int nonNullValidCount = 0;
            if (positionCode != null) nonNullValidCount++;
            if (supervisorCode != null) nonNullValidCount++;
            if (departmentCode != null) nonNullValidCount++;

            if (nonNullValidCount >= 2) {
                String key = generateRelationshipKey(positionCode, supervisorCode, departmentCode);
                if (!relationshipMap.containsKey(key)) { // Chỉ thêm nếu chưa có key
                    relationshipMap.put(key, relationship);
                }
            }
        }
        List<PositionRelationShip> entitiesToSave = new ArrayList<PositionRelationShip>();

        // Xử lý từng DTO
        for (ImportPositionRelationShipDto dto : validDtos) {
            int nonNullCount = getNonNullCodeCount(dto);

            if (nonNullCount == 2) {
                // Trường hợp chỉ có 2 code khác null
                String relationshipKey = generateRelationshipKey(dto.getCode(), dto.getSupervisorCode(), dto.getDepartmentCode());
                PositionRelationShip entity = getOrCreateEntity(relationshipKey, relationshipMap);

                // Xử lý relationshipType
                if (dto.getSupervisorCode() != null && dto.getCode() != null && dto.getDepartmentCode() == null) {
                    // Mối quan hệ giữa Position và Supervisor
                    if (dto.getRelationshipType() == null || (dto.getRelationshipType() != 3 && dto.getRelationshipType() != 4)) {
                        dto.setRelationshipType(3); // Gán mặc định là trực tiếp (Supervisor-Position)
                    }
                } else if (dto.getCode() != null && dto.getDepartmentCode() != null && dto.getSupervisorCode() == null) {
                    // Mối quan hệ giữa Position và Department
                    if (dto.getRelationshipType() == null || (dto.getRelationshipType() != 1 && dto.getRelationshipType() != 2)) {
                        dto.setRelationshipType(1); // Gán mặc định là trực tiếp (Position-Department)
                    }
                }

                setEntityValues(entity, dto, existingPositions, existingDepartments);
                entitiesToSave.add(entity);
            } else if (nonNullCount == 3) {
                // Trường hợp cả 3 code khác null: tạo bản ghi dựa trên relationshipType
                if (dto.getRelationshipType() != null) {
                    switch (dto.getRelationshipType()) {
                        case 1: // Position quản lý trực tiếp phòng ban
                        case 2: // Position quản lý gián tiếp phòng ban
                            addRelationshipForPositionDepartment(dto, existingPositions, existingDepartments, entitiesToSave, relationshipMap);
                            break;
                        case 3: // Supervisor quản lý trực tiếp position
                        case 4: // Supervisor quản lý gián tiếp position
                            addRelationshipForSupervisorPosition(dto, existingPositions, existingDepartments, entitiesToSave, relationshipMap);
                            break;
                        default:
                            // Nếu relationshipType không hợp lệ, bỏ qua
                            break;
                    }
                }
            }
        }

        // Lưu tất cả entities
        positionRelationShipRepository.saveAll(entitiesToSave);
        return entitiesToSave.size();
    }

    @Override
    public PositionDto getByStaffId(UUID staffId) {
        if (staffId == null) return null;

        Staff staff = staffRepository.findById(staffId).orElse(null);
        if (staff == null) return null;
        Set<Position> currentPositions = staff.getCurrentPositions();
        PositionDto positionDto = null;
        for (Position p : currentPositions) {
            positionDto = new PositionDto(p);
            if (p.getIsMain()) {
                return positionDto;
            }
        }
        return positionDto;
    }

    // Kiểm tra code không null, không empty, không blank
    private boolean isValidCode(String code) {
        return code != null && !code.trim().isEmpty();
    }

    // Tạo key unique cho relationship (không bao gồm relationshipType)
    private String generateRelationshipKey(String positionCode, String supervisorCode, String departmentCode) {
        return String.format("%s|%s|%s",
                positionCode != null ? positionCode : "",
                supervisorCode != null ? supervisorCode : "",
                departmentCode != null ? departmentCode : "");
    }

    // Đếm số lượng code không null
    private int getNonNullCodeCount(ImportPositionRelationShipDto dto) {
        int nonNullCount = 0;
        if (dto.getCode() != null) nonNullCount++;
        if (dto.getSupervisorCode() != null) nonNullCount++;
        if (dto.getDepartmentCode() != null) nonNullCount++;
        return nonNullCount;
    }

    // Lấy hoặc tạo entity mới
    private PositionRelationShip getOrCreateEntity(String relationshipKey, Map<String, PositionRelationShip> relationshipMap) {
        PositionRelationShip entity;
        if (relationshipMap.containsKey(relationshipKey)) {
            entity = relationshipMap.get(relationshipKey);
        } else {
            entity = new PositionRelationShip();
        }
        return entity;
    }

    // Set giá trị cho entity
    private void setEntityValues(PositionRelationShip entity, ImportPositionRelationShipDto dto,
                                 Map<String, Position> existingPositions, Map<String, HRDepartment> existingDepartments) {
        entity.setPosition(existingPositions.get(dto.getCode()));
        entity.setSupervisor(existingPositions.get(dto.getSupervisorCode()));
        entity.setDepartment(existingDepartments.get(dto.getDepartmentCode()));
        entity.setRelationshipType(dto.getRelationshipType());
    }

    // Thêm relationship cho Position và Department
    private void addRelationshipForPositionDepartment(ImportPositionRelationShipDto dto,
                                                      Map<String, Position> existingPositions,
                                                      Map<String, HRDepartment> existingDepartments,
                                                      List<PositionRelationShip> entitiesToSave,
                                                      Map<String, PositionRelationShip> relationshipMap) {
        String key = generateRelationshipKey(dto.getCode(), null, dto.getDepartmentCode());
        PositionRelationShip entity = getOrCreateEntity(key, relationshipMap);
        entity.setPosition(existingPositions.get(dto.getCode()));
        entity.setSupervisor(null);
        entity.setDepartment(existingDepartments.get(dto.getDepartmentCode()));
        entity.setRelationshipType(dto.getRelationshipType());
        entitiesToSave.add(entity);
    }

    // Thêm relationship cho Supervisor và Position
    private void addRelationshipForSupervisorPosition(ImportPositionRelationShipDto dto,
                                                      Map<String, Position> existingPositions,
                                                      Map<String, HRDepartment> existingDepartments,
                                                      List<PositionRelationShip> entitiesToSave,
                                                      Map<String, PositionRelationShip> relationshipMap) {
        String key = generateRelationshipKey(dto.getCode(), dto.getSupervisorCode(), null);
        PositionRelationShip entity = getOrCreateEntity(key, relationshipMap);
        entity.setPosition(existingPositions.get(dto.getCode()));
        entity.setSupervisor(existingPositions.get(dto.getSupervisorCode()));
        entity.setDepartment(null);
        entity.setRelationshipType(dto.getRelationshipType());
        entitiesToSave.add(entity);
    }

    // Validate DTO
    private boolean isValidDto(ImportPositionRelationShipDto dto,
                               Map<String, Position> positionMap,
                               Map<String, HRDepartment> departmentMap) {
        if (dto == null) {
            return false;
        }

        // Đếm số lượng code không null
        int nonNullCount = getNonNullCodeCount(dto);

        // Yêu cầu ít nhất 2 trong 3 code phải tồn tại
        if (nonNullCount < 2) {
            return false;
        }

        // Validate position code
        if (dto.getCode() != null && !positionMap.containsKey(dto.getCode())) {
            return false;
        }

        // Validate supervisor code
        if (dto.getSupervisorCode() != null && !positionMap.containsKey(dto.getSupervisorCode())) {
            return false;
        }

        // Validate department code
        if (dto.getDepartmentCode() != null && !departmentMap.containsKey(dto.getDepartmentCode())) {
            return false;
        }

        return true;
    }
}
