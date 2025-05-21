package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.PositionTitleSearchDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.PositionTitleService;
import com.globits.hr.service.SystemConfigService;
import com.globits.hr.utils.ExcelUtils;
import com.globits.hr.utils.SystemConfigUtil;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.security.domain.User;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Transactional
@Service
public class PositionTitleServiceImpl extends GenericServiceImpl<PositionTitle, UUID> implements PositionTitleService {
    private static final Logger logger = LoggerFactory.getLogger(PositionTitleServiceImpl.class);

    @Autowired
    private PositionTitleRepository positionTitleRepository;

    @Autowired
    private PositionRoleRepository positionRoleRepository;

    @Autowired
    private RankTitleRepository rankTitleRepository;
    @Autowired
    private HRDepartmentPositionRepository hrDepartmentPositionRepository;

    @Autowired
    private HRDepartmentRepository hrDepartmentRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public PositionTitleDto saveTitle(PositionTitleDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User modifiedUser;
        LocalDateTime currentDate = LocalDateTime.now();
        String currentUserName = "Unknown User";
        if (authentication != null) {
            modifiedUser = (User) authentication.getPrincipal();
            currentUserName = modifiedUser.getUsername();
        }

        PositionTitle title = null;
        if (dto.getId() != null) {
            title = this.findById(dto.getId());
        }
        if (title == null) {
            title = new PositionTitle();
            title.setCreateDate(currentDate);
            title.setCreatedBy(currentUserName);
        }
        title.setModifiedBy(currentUserName);
        title.setModifyDate(currentDate);
        if (dto.getCode() != null) {
            title.setCode(dto.getCode());
        }
        if (dto.getName() != null) {
            title.setName(dto.getName());
        }
        title.setDescription(dto.getDescription());
        title.setType(dto.getType());
        title.setPositionCoefficient(dto.getPositionCoefficient());
        title.setRecruitmentDays(dto.getRecruitmentDays());
        title.setEstimatedWorkingDays(dto.getEstimatedWorkingDays());
        title.setWorkDayCalculationType(dto.getWorkDayCalculationType());

        title = positionTitleRepository.save(title);

        return new PositionTitleDto(title);
    }

    @Override
    public PositionTitleDto getTitle(UUID id) {
        PositionTitle entity = this.findById(id);
        if (entity == null) {
            return null;
        } else {
            return new PositionTitleDto(entity);
        }
    }

    @Override
    public Boolean removeTitle(UUID id) {
        PositionTitle title = this.findById(id);
        if (title != null) {
            title.setVoided(true);
            positionTitleRepository.save(title);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Set<PositionTitle> getMainPositionByStaffId(UUID staffId) {
        if (staffId == null) {
            return null;
        }

        Staff staff = staffRepository.findById(staffId).orElse(null);
        if (staff == null) return null;

        List<Position> positions = positionRepository.findByStaffId(staffId);

        Set<PositionTitle> res = new HashSet<>();

        for (Position position : positions) {
            if (position.getTitle() != null) {
                res.add(position.getTitle());
            }
        }
        return res;
    }

    @Override
    public Boolean deleteMultiple(PositionTitleDto[] dtos) {
        boolean ret = true;

        if (dtos == null || dtos.length <= 0) {
            return ret;
        }
        ArrayList<PositionTitle> titles = new ArrayList<PositionTitle>();
        for (PositionTitleDto dto : dtos) {

            PositionTitle entity = this.findById(dto.getId());

            if (entity == null) {
                throw new RuntimeException();
            }
            titles.add(entity);
        }
        positionTitleRepository.deleteInBatch(titles);
        return ret;
    }

    @Override
    public Page<PositionTitleDto> searchByPage(PositionTitleSearchDto dto) {
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
        String whereClause = " WHERE (1=1) ";
        String joinHRDepartmentPosition = " LEFT JOIN HRDepartmentPosition dpt ON dpt.positionTitle.id = entity.id ";
        Boolean hasJoinHRDepartmentPosition = false;

        if (dto.getVoided() != null && dto.getVoided() == true) {
            whereClause += " AND ( entity.voided = true ) ";
        } else {
            whereClause += " AND ( entity.voided = false OR entity.voided IS NULL ) ";
        }
        String sqlCount = "select count(entity.id) from PositionTitle as entity ";
        String sql = "select new  com.globits.hr.dto.PositionTitleDto(entity) from PositionTitle as entity ";
        String leftJoin = " left join entity.rankTitle rankTitle ";
        String orderBy = "";
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text OR entity.shortName LIKE :text OR entity.description LIKE :text) ";
        }
        if (dto.getIsGroup() != null && dto.getIsGroup() == true) {
            // parent
            whereClause += " AND ( entity.parent.id IS NULL ) ";
            orderBy = " ORDER BY entity.code ";
        } else {
            // child
            sqlCount += leftJoin;
            sql += leftJoin;
            whereClause += " AND ( entity.parent.id IS NOT NULL ) ";
            orderBy = " ORDER BY rankTitle.name ";

        }
        if (dto.getRankTitleId() != null) {
            whereClause += " AND ( entity.rankTitle.id = :rankTitleId) ";
        }

        if (dto.getParentId() != null) {
            whereClause += " AND ( entity.parent.id = :parentId) ";
        }

        if (dto.getPositionRoleId() != null) {
            whereClause += " AND ( entity.positionRole.id = :positionRoleId) ";
        }

        if (dto.getType() != null) {
            whereClause += " AND ( entity.type = :positionTitleType) ";
        }

        // Lọc phòng ban qua HRDepartmentPosition
        List<String> departmentConditions = new ArrayList<>();
        if (dto.getDepartmentId() != null) {
            hasJoinHRDepartmentPosition = true;
            departmentConditions.add("dpt.department.id = :departmentId");
        }
        if (dto.getDepartmentCode() != null) {
            hasJoinHRDepartmentPosition = true;
            departmentConditions.add("dpt.department.code = :departmentCode");
        }
        if (!departmentConditions.isEmpty()) {
            whereClause += " AND ( " + String.join(" OR ", departmentConditions) + " ) ";
        }

        if (hasJoinHRDepartmentPosition) {
            sql += joinHRDepartmentPosition;
            sqlCount += joinHRDepartmentPosition;
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;
        Query q = manager.createQuery(sql, PositionTitleDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        if (dto.getDepartmentId() != null) {
            q.setParameter("departmentId", dto.getDepartmentId());
            qCount.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getDepartmentCode() != null) {
            q.setParameter("departmentCode", dto.getDepartmentCode());
            qCount.setParameter("departmentCode", dto.getDepartmentCode());
        }

        if (dto.getRankTitleId() != null) {
            q.setParameter("rankTitleId", dto.getRankTitleId());
            qCount.setParameter("rankTitleId", dto.getRankTitleId());
        }

        if (dto.getParentId() != null) {
            q.setParameter("parentId", dto.getParentId());
            qCount.setParameter("parentId", dto.getParentId());
        }

        if (dto.getPositionRoleId() != null) {
            q.setParameter("positionRoleId", dto.getPositionRoleId());
            qCount.setParameter("positionRoleId", dto.getPositionRoleId());
        }
        if (dto.getType() != null) {
            q.setParameter("positionTitleType", dto.getType());
            qCount.setParameter("positionTitleType", dto.getType());
        }
        if (dto.getIsExportExcel() != null && dto.getIsExportExcel()) {
            // Nếu xuất Excel thì không phân trang, lấy toàn bộ dữ liệu
            List<PositionTitleDto> entities = q.getResultList();
            long count = (long) qCount.getSingleResult();

            // Trả về tất cả dữ liệu, pageable = unpaged
            return new PageImpl<>(entities, Pageable.unpaged(), count);
        } else {
            // Phân trang như bình thường
            int startPosition = pageIndex * pageSize;
            q.setFirstResult(startPosition);
            q.setMaxResults(pageSize);
            List<PositionTitleDto> entities = q.getResultList();
            long count = (long) qCount.getSingleResult();

            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            return new PageImpl<>(entities, pageable, count);
        }
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Long count = positionTitleRepository.checkCode(code, id);
            return count != 0L;
        }
        return null;
    }

    @Override
    public Page<DepartmentsTreeDto> getByRoot(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        return positionTitleRepository.getByRoot(pageable);
    }

    @Override
    public Boolean isValidCode(PositionTitleDto dto) {
        if (dto == null)
            return false;

        // ID of PositionTitle is null => Create new PositionTitle
        // => Assure that there's no other PositionTitles using this code of new PositionTitle
        // if there was any PositionTitle using new PositionTitle code, then this new code is
        // invalid => return False
        // else return true
        if (dto.getId() == null) {
            List<PositionTitle> entities = positionTitleRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        }
        // ID of PositionTitle is NOT null => PositionTitle is modified
        // => Assure that the modified code is not same to OTHER any PositionTitle's code
        // if there was any PositionTitle using new PositionTitle code, then this new code is
        // invalid => return False
        // else return true
        else {
            List<PositionTitle> entities = positionTitleRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (PositionTitle entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public PositionTitleDto saveOrUpdate(PositionTitleDto dto) {
        if (dto == null) return null;
        PositionTitle entity = null;

        if (entity == null && dto.getId() != null) {
            entity = positionTitleRepository.findById(dto.getId()).orElse(null);
            if (entity == null) return null;
        }

        if (entity == null) entity = new PositionTitle();

        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setType(dto.getType());
        entity.setPositionCoefficient(dto.getPositionCoefficient());
        entity.setShortName(dto.getShortName());
        entity.setOtherName(dto.getOtherName());
        entity.setRecruitmentDays(dto.getRecruitmentDays());
//        entity.setPositionTitleType(dto.getPositionTitleType());
        entity.setWorkDayCalculationType(dto.getWorkDayCalculationType());
        entity.setEstimatedWorkingDays(dto.getEstimatedWorkingDays());
        entity.setCode(dto.getCode());
        // loại code đã sinh khỏi map
        if (dto.getPositionRole() != null && dto.getPositionRole().getId() != null) {
            PositionRole positionRole = positionRoleRepository.findById(dto.getPositionRole().getId()).orElse(null);
            if (positionRole == null) return null;
            entity.setPositionRole(positionRole);
        } else {
            entity.setPositionRole(null);
        }

        if (dto.getRankTitle() != null && dto.getRankTitle().getId() != null) {
            RankTitle rankTitle = rankTitleRepository.findById(dto.getRankTitle().getId()).orElse(null);
            if (rankTitle == null) return null;
            entity.setRankTitle(rankTitle);
        } else {
            entity.setRankTitle(null);
        }

        if (dto.getParent() != null && dto.getParent().getId() != null) {
            PositionTitle parent = positionTitleRepository.findById(dto.getParent().getId()).orElse(null);
            if (parent == null || parent.getId().equals(entity.getId())) return null;
            entity.setParent(parent);
        } else {
            entity.setParent(null);
        }
        //save multiple position title for this department
        if (entity.getDepartmentPositions() == null) {
            entity.setDepartmentPositions(new HashSet<>());
        }
        entity.getDepartmentPositions().clear();
        if (dto.getDepartments() != null && !dto.getDepartments().isEmpty()) {
            for (HRDepartmentDto item : dto.getDepartments()) {
                if (item == null || item.getId() == null)
                    continue;

                List<HRDepartmentPosition> existedRelationships = hrDepartmentPositionRepository
                        .findByDepartmentIdAndPositionTitleId(dto.getId(), entity.getId());
                HRDepartmentPosition positionRelaEntity = null;
                if (existedRelationships != null && !existedRelationships.isEmpty()) {
                    //in this section, relationship is already existed
                    positionRelaEntity = existedRelationships.get(0);
                }
                if (positionRelaEntity == null) {
                    //in this section, relationship is NOT EXISTED => CREATE NEW
                    positionRelaEntity = new HRDepartmentPosition();

                    HRDepartment hrDepartmentEntity = hrDepartmentRepository
                            .findById(item.getId()).orElse(null);
                    if (hrDepartmentEntity == null) continue;
                    positionRelaEntity.setDepartment(hrDepartmentEntity);
                    positionRelaEntity.setPositionTitle(entity);
                }

                entity.getDepartmentPositions().add(positionRelaEntity);
            }
        }

        entity = positionTitleRepository.save(entity);

        return new PositionTitleDto(entity);
    }

    @Override
    public Integer saveListPositionTitle(List<PositionTitleDto> dtos, Boolean isGroupPositionTitle) {
        int result = 0;
        if (dtos == null || dtos.isEmpty()) return result;
        for (PositionTitleDto dto : dtos) {
            PositionTitle entity = null;
            if (dto.getCode() != null) {
                List<PositionTitle> positionTitle = positionTitleRepository.findByCode(dto.getCode());
                if (positionTitle != null && !positionTitle.isEmpty()) {
                    entity = positionTitle.get(0);
                }
            }
            // tạo mới nếu chưa có hoặc đã bị voided
            if (entity == null) {
                entity = new PositionTitle();
            }
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setOtherName(dto.getOtherName());
            entity.setShortName(dto.getShortName());
            entity.setDescription(dto.getDescription());
            entity.setRecruitmentDays(dto.getRecruitmentDays());
//            entity.setPositionTitleType(dto.getPositionTitleType());
            entity.setVoided(false);

            if (!isGroupPositionTitle) {
                if (dto.getParent() == null || dto.getParent().getCode() == null) continue;
                List<PositionTitle> groupPositionTitle = positionTitleRepository.findByCode(dto.getParent().getCode());
                if (groupPositionTitle == null || groupPositionTitle.isEmpty()) continue;
                entity.setParent(groupPositionTitle.get(0));
            }
            if (dto.getRankTitle() != null && dto.getRankTitle().getShortName() != null) {
                List<RankTitle> rankTitles = rankTitleRepository.findByShortName(dto.getRankTitle().getShortName());
                if (rankTitles != null && !rankTitles.isEmpty()) {
                    entity.setRankTitle(rankTitles.get(0));
                }
            }
            entity.setWorkDayCalculationType(dto.getWorkDayCalculationType());
            entity.setEstimatedWorkingDays(dto.getEstimatedWorkingDays());
            if (entity.getDepartmentPositions() == null) {
                entity.setDepartmentPositions(new HashSet<>());
            }
            entity.getDepartmentPositions().clear();
            if (dto.getDepartments() != null && !dto.getDepartments().isEmpty()) {
                for (HRDepartmentDto item : dto.getDepartments()) {
                    if (item == null || item.getCode() == null)
                        continue;

                    List<HRDepartmentPosition> existedRelationships = hrDepartmentPositionRepository
                            .findByDepartmentCodeAndPositionTitleId(dto.getCode(), entity.getId());
                    HRDepartmentPosition positionRelaEntity = null;
                    if (existedRelationships != null && !existedRelationships.isEmpty()) {
                        //in this section, relationship is already existed
                        positionRelaEntity = existedRelationships.get(0);
                    }
                    if (positionRelaEntity == null) {
                        //in this section, relationship is NOT EXISTED => CREATE NEW
                        positionRelaEntity = new HRDepartmentPosition();

                        List<HRDepartment> hrDepartmentEntity = hrDepartmentRepository
                                .findByCode(item.getCode());
                        if (hrDepartmentEntity == null || hrDepartmentEntity.isEmpty()) continue;
                        positionRelaEntity.setDepartment(hrDepartmentEntity.get(0));
                        positionRelaEntity.setPositionTitle(entity);
                    }

                    entity.getDepartmentPositions().add(positionRelaEntity);
                }
            }
            positionTitleRepository.save(entity);
            result++;
        }
        return result;
    }

    @Override
    public Boolean deleteByShortName(String shortName) {
        List<PositionTitle> entities = positionTitleRepository.findByShortName(shortName);
        if (entities != null && !entities.isEmpty()) {
            positionTitleRepository.deleteAll(entities);
            return true;
        } else {
            return false;
        }
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


    @Override
    public Workbook exportExcelPositionTitleData(PositionTitleSearchDto dto) {
        if (dto == null) {
            return null;
        }

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("Excel/MAU_CHUC_DANH.xlsx")) {
            if (fileInputStream == null) {
                throw new IOException("File '" + "Excel/MAU_CHUC_DANH.xlsx" + "' không tìm thấy trong classpath");
            }

            Workbook workbook = new XSSFWorkbook(fileInputStream);

            Sheet staffSheet = workbook.getSheetAt(0);
            CellStyle dataCellStyle = createDataCellStyle(workbook);

            int pageIndex = 1;
            int rowIndex = 2;
            int orderNumber = 1;
            boolean hasNextPage = true;
            long startTime = System.nanoTime();

            while (hasNextPage) {
                // searchStaffDto = new SearchStaffDto();
                dto.setPageIndex(pageIndex);
                dto.setPageSize(100);

                Page<PositionTitleDto> pageDepartments = this.searchByPage(dto);
                if (pageDepartments == null || pageDepartments.isEmpty()) {
                    break;
                }

                for (PositionTitleDto positionTitleDto : pageDepartments) {
                    if (positionTitleDto == null) continue;

                    Row dataRow = staffSheet.createRow(rowIndex);

                    // 0. STT
                    int cellIndex = 0;
//                    createCell(dataRow, cellIndex++, orderNumber, dataCellStyle);
                    orderNumber++;

                    // 0. Tên chức danh
                    this.createCell(dataRow, cellIndex++, positionTitleDto.getName(), dataCellStyle);

                    // 1. Mã chức danh
                    this.createCell(dataRow, cellIndex++, positionTitleDto.getCode(), dataCellStyle);

                    // 2. Tên khác
                    this.createCell(dataRow, cellIndex++, positionTitleDto.getOtherName(), dataCellStyle);

                    // 3. Tên viết tắt
                    this.createCell(dataRow, cellIndex++, positionTitleDto.getShortName(), dataCellStyle);

                    // 4. Tên nhóm ngạch
                    // 5. Mã nhóm ngạch
                    String parentCode = null;
                    String parentName = null;
                    if (positionTitleDto.getParent() != null) {
                        parentName = positionTitleDto.getName();
                        parentCode = positionTitleDto.getCode();
                    }
                    this.createCell(dataRow, cellIndex++, parentName, dataCellStyle);
                    this.createCell(dataRow, cellIndex++, parentCode, dataCellStyle);

                    // 6. Mã cấp bậc
                    String rankTitleCode = null;
                    if (positionTitleDto.getRankTitle() != null) {
                        rankTitleCode = positionTitleDto.getCode();
                    }
                    this.createCell(dataRow, cellIndex++, rankTitleCode, dataCellStyle);

                    // 7. Số ngày tuyển
                    this.createCell(dataRow, cellIndex++, positionTitleDto.getRecruitmentDays(), dataCellStyle);

//                    // 8. Nhà quản lý
//                    if (positionTitleDto.getPositionTitleType() != null && positionTitleDto.getPositionTitleType().equals(HrConstants.PositionTitleType.NHA_QUAN_LY.getValue())) {
//                        this.createCell(dataRow, cellIndex++, "X", dataCellStyle);
//                    } else {
//                        this.createCell(dataRow, cellIndex++, "", dataCellStyle);
//                    }
//
//                    // 9. Chuyên môn kỹ thuật bậc cao
//                    if (positionTitleDto.getPositionTitleType() != null && positionTitleDto.getPositionTitleType().equals(HrConstants.PositionTitleType.CHUYEN_MON_KY_THUAT_BAC_CAO.getValue())) {
//                        this.createCell(dataRow, cellIndex++, "X", dataCellStyle);
//                    } else {
//                        this.createCell(dataRow, cellIndex++, "", dataCellStyle);
//                    }
//
//                    // 10. Chuyên muôn kỹ thuật bậc trung
//                    if (positionTitleDto.getPositionTitleType() != null && positionTitleDto.getPositionTitleType().equals(HrConstants.PositionTitleType.CHUYEN_MON_KY_THUAT_BAC_TRUNG.getValue())) {
//                        this.createCell(dataRow, cellIndex++, "X", dataCellStyle);
//                    } else {
//                        this.createCell(dataRow, cellIndex++, "", dataCellStyle);
//                    }
//
//                    // 11. Khác
//                    if (positionTitleDto.getPositionTitleType() != null && positionTitleDto.getPositionTitleType().equals(HrConstants.PositionTitleType.KHAC.getValue())) {
//                        this.createCell(dataRow, cellIndex++, "X", dataCellStyle);
//                    } else {
//                        this.createCell(dataRow, cellIndex++, "", dataCellStyle);
//                    }

                    // 12. Mô tả
                    this.createCell(dataRow, cellIndex++, positionTitleDto.getDescription(), dataCellStyle);


                    // 13. Cách tính ngày công chuẩn
                    this.createCell(dataRow, cellIndex++, positionTitleDto.getWorkDayCalculationType(), dataCellStyle);

                    // 14. Số ngày làm việc ước tính (tháng)
                    if (positionTitleDto.getWorkDayCalculationType() != null && positionTitleDto.getWorkDayCalculationType().equals(HrConstants.PositionTitleWorkdayCalculationType.FIXED.getValue())) {
                        this.createCell(dataRow, cellIndex++, positionTitleDto.getEstimatedWorkingDays(), dataCellStyle);
                    } else {
                        this.createCell(dataRow, cellIndex++, "", dataCellStyle);
                    }

                    // 15. "Chức danh trực thuộc phòng ban (cách nhau bằng dấu ';')"
                    if (positionTitleDto.getDepartments() != null && positionTitleDto.getDepartments().size() > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (HRDepartmentDto department : positionTitleDto.getDepartments()) {
                            if (department != null && department.getCode() != null) {
                                sb.append(department.getCode()).append(";");
                            }
                        }
                        if (sb.length() > 0) {
                            sb.setLength(sb.length() - 1); // Xóa dấu ';' cuối cùng
                        }
                        this.createCell(dataRow, cellIndex++, sb.toString(), dataCellStyle);
                    } else {
                        this.createCell(dataRow, cellIndex++, "", dataCellStyle);
                    }

                    rowIndex++;
                }

                hasNextPage = pageDepartments.hasNext(); // Kiểm tra xem còn trang tiếp theo không
                pageIndex++; // Tăng pageIndex để lấy trang tiếp theo
            }
            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;

            logger.info("Xuất dữ liệu chức danh - Xử lý mất {} ms ", elapsedTimeMs);
            return workbook;

        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String autoGenerateCode(String configKey) {
        SystemConfig systemConfig = systemConfigService.getConfigByConfigValue(configKey);
        if (systemConfig == null) {
            return "";
        } else {
            String maxCode = positionTitleRepository.findMaxCodeByPrefix(systemConfig.getConfigValue(), systemConfig.getNumberOfZero());
            return systemConfigService.generateNextCode(systemConfig.getConfigValue(), systemConfig.getNumberOfZero(), maxCode);
        }
    }
}
