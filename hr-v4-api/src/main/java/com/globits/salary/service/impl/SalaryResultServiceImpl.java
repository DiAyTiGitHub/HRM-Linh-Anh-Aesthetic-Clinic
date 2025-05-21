package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.dto.search.SearchStaffSalaryTemplateDto;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.StaffService;
import com.globits.hr.service.StaffSocialInsuranceService;
import com.globits.hr.utils.ExportExcelUtil;
import com.globits.salary.domain.*;
import com.globits.salary.dto.*;
import com.globits.salary.dto.search.SearchSalaryResultDto;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import com.globits.salary.repository.*;
import com.globits.salary.service.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SalaryResultServiceImpl extends GenericServiceImpl<SalaryResult, UUID> implements SalaryResultService {
    private static final Logger logger = LoggerFactory.getLogger(SalaryResultServiceImpl.class);

    private static LocalDateTime logTime(String message) {
        LocalDateTime currentTime = LocalDateTime.now();
        String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info(message + " at " + formattedTime);

        return currentTime;
    }

    private static LocalDateTime logTime(String message, LocalDateTime previousTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String distance = previousTime == null ? "N/A" : Duration.between(previousTime, currentTime).toMillis() + " ms";

        logger.info(message + " at " + formattedTime + ", distance: " + distance);

        return currentTime;
    }

    @Autowired
    private SalaryPeriodRepository salaryPeriodRepository;

    @Autowired
    private SalaryResultRepository salaryResultRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SalaryResultStaffRepository salaryResultStaffRepository;

    @Autowired
    private SalaryResultStaffItemRepository salaryResultStaffItemRepository;

    @Autowired
    private SalaryTemplateRepository salaryTemplateRepository;

    @Autowired
    private SalaryTemplateItemRepository salaryTemplateItemRepository;

    @Autowired
    private SalaryResultStaffItemService salaryResultStaffItemService;

    @Autowired
    private SalaryResultItemRepository salaryResultItemRepository;

    @Autowired
    private SalaryResultItemGroupRepository salaryResultItemGroupRepository;

    @Autowired
    private SalaryResultItemGroupService salaryResultItemGroupService;

    @Autowired
    private SalaryResultItemService salaryResultItemService;

    @Autowired
    private SalaryItemRepository salaryItemRepository;

    @Autowired
    private SalaryItemService salaryItemService;

    @Autowired
    private StaffSocialInsuranceService staffSocialInsuranceService;

    @Autowired
    private SalaryPayslipService salaryPayslipService;

    @Autowired
    private SalaryResultStaffService salaryResultStaffService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private StaffSalaryTemplateService staffSalaryTemplateService;

    @Autowired
    private EntityManager entityManager;

    @Override
    public SalaryResultDto saveBoardConfigOfSalaryResultV2(SalaryResultDto dto) {
        if (dto == null || dto.getId() == null) {
            return null;
        }

        SalaryResult entity = null;
        entity = salaryResultRepository.findById(dto.getId()).orElse(null);
        if (entity == null)
            return null;

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());

        entity = salaryResultRepository.save(entity);

        // save column groups of salary board
        salaryResultItemGroupService.handleSetSalaryResultItemGroupsFromConfig(entity, dto);
        // save columns of salary board
//        salaryResultItemService.handleSetSalaryResultItemsFromConfig(entity, dto);
        salaryResultItemService.handleSetSalaryResultItemsFromConfigV2(entity, dto);
        entity = salaryResultRepository.save(entity);

        // some columns must have special usingFormula for it, this function will
        // generate its formula based on current context
        salaryResultItemService.autoGenerateSpecialFormulaForResultItem(entity);

        // generate table rows and cells of each row by result items
        // (which are generated from SalaryTemplate)
        salaryResultStaffService.generateSalaryResultStaffs(dto, entity);

        // auto calculate other cells values in each row that using formula
        salaryPayslipService.autoCalculateCellValueInEachRow(entity);

        entity = salaryResultRepository.save(entity);

        // auto generate staff social insurance from salary result
//        generateStaffSocialInsuranceFromSalaryResult(entity);

        return new SalaryResultDto(entity);
    }

    @Override
    @Modifying
    public SalaryResultDto saveOrUpdate(SalaryResultDto dto) {
        if (dto == null) {
            return null;
        }

        SalaryResult entity = new SalaryResult();
        if (dto.getId() != null)
            entity = salaryResultRepository.findById(dto.getId()).orElse(null);
        if (entity == null)
            entity = new SalaryResult();

        // Bảng lương đã bị khóa
        if (entity.getIsLocked() != null && entity.getIsLocked().equals(true)) {
            logger.info("Bảng lương đã bị khóa, không thể thao tác: ID: " + entity.getId());
            return null;
        }

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());

        if (dto.getSalaryPeriod() != null) {
            SalaryPeriod periodEntity = salaryPeriodRepository.findById(dto.getSalaryPeriod().getId()).orElse(null);
            if (periodEntity == null)
                return null;

            entity.setSalaryPeriod(periodEntity);
        } else {
            entity.setSalaryPeriod(null);
        }

        if (dto.getSalaryTemplate() != null) {
            SalaryTemplate templateEntity = salaryTemplateRepository.findById(dto.getSalaryTemplate().getId())
                    .orElse(null);
            if (templateEntity == null)
                return null;

            entity.setSalaryTemplate(templateEntity);
        } else {
            entity.setSalaryTemplate(null);
        }

        entity = salaryResultRepository.save(entity);

        // if chosen salary template is not null => copy initial items and group items
        // from chosen template
        entity = copySalaryResultItemsAndGroupsFromChosenTemplate(entity);

        // some columns must have special usingFormula for it, this function will
        // generate its formula based on current context
        salaryResultItemService.autoGenerateSpecialFormulaForResultItem(entity);

        // generate table rows and cells of each row by result items
        // (which are generated from SalaryTemplate)
        salaryResultStaffService.generateSalaryResultStaffs(dto, entity);

        // auto calculate other cells values in each row that using formula
        salaryPayslipService.autoCalculateCellValueInEachRow(entity);

        entity = salaryResultRepository.save(entity);

        // auto generate staff social insurance from salary result
//        generateStaffSocialInsuranceFromSalaryResult(entity);

        return new SalaryResultDto(entity);
    }

    @Override
    public List<SalaryResultDto> updateStatus(List<UUID> ids, int status) {
        if (ids == null || ids.isEmpty())
            return null;

        List<SalaryResult> entities = salaryResultRepository.findAllById(ids);
        List<SalaryResultDto> result = new ArrayList<>();

        if (entities == null || entities.isEmpty())
            return result;
        for (SalaryResult entity : entities) {
            if (entity == null)
                continue;

            entity.setApprovalStatus(status);

            List<SalaryResultStaff> salaryResultStaffs = salaryResultStaffRepository
                    .getAllBySalaryResultId(entity.getId());
            if (salaryResultStaffs != null && !salaryResultStaffs.isEmpty()) {
                for (SalaryResultStaff salaryResultStaff : salaryResultStaffs) {
                    salaryResultStaff.setApprovalStatus(status);

//                    staffSocialInsuranceService.handleSocialInsuranceByChangingStatus(salaryResultStaff, status);
                }

                salaryResultStaffRepository.saveAll(salaryResultStaffs);
            }
        }

        salaryResultRepository.saveAll(entities);

        for (SalaryResult entity : entities) {
            result.add(new SalaryResultDto(entity));
        }

        return result;
    }

//    private void generateStaffSocialInsuranceFromSalaryResult(SalaryResult entity) {
//        if (entity == null || entity.getSalaryResultStaffs() == null || entity.getSalaryResultStaffs().isEmpty())
//            return;
//
//        for (SalaryResultStaff resultStaff : entity.getSalaryResultStaffs()) {
//            staffSocialInsuranceService.generateFromResultStaff(resultStaff);
//        }
//    }

    private SalaryResult copySalaryResultItemsAndGroupsFromChosenTemplate(SalaryResult result) {
        if (result == null || result.getSalaryTemplate() == null)
            return result;

        // copy column groups from salary template to salary result first
        salaryResultItemGroupService.copyFromSalaryTemplateItemGroup(result);

        // then, copy columns from salary template item to salary result item
        salaryResultItemService.copyFromSalaryTemplateItem(result);

        result = salaryResultRepository.save(result);
        return result;
    }

    @Override
    public Page<SalaryResultDto> searchByPage(SearchSalaryResultDto searchDto) {
        if (searchDto == null) {
            return null;
        }

        int pageIndex = searchDto.getPageIndex();
        int pageSize = searchDto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.modifyDate desc ";

        String sqlCount = "select count(distinct entity.id) from SalaryResult as entity ";
        String sql = "select distinct new com.globits.salary.dto.SalaryResultDto(entity) from SalaryResult as entity ";

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text OR entity.description LIKE :text ) ";
        }

        if (searchDto.getStatus() != null && searchDto.getStatus() > 0) {
            whereClause += " AND entity.approvalStatus = :status ";
        }

        if (searchDto.getFromDate() != null) {
            whereClause += " AND entity.fromDate >= :fromDate ";
        }

        if (searchDto.getToDate() != null) {
            whereClause += " AND entity.toDate <= :toDate ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, SalaryResultDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            query.setParameter("text", '%' + searchDto.getKeyword() + '%');
            qCount.setParameter("text", '%' + searchDto.getKeyword() + '%');
        }

        if (searchDto.getFromDate() != null) {
            query.setParameter("fromDate", searchDto.getFromDate());
            qCount.setParameter("fromDate", searchDto.getFromDate());
        }

        if (searchDto.getToDate() != null) {
            query.setParameter("toDate", searchDto.getToDate());
            qCount.setParameter("toDate", searchDto.getToDate());
        }
        if (searchDto.getStatus() != null && searchDto.getStatus() > 0) {
            query.setParameter("status", searchDto.getStatus());
            qCount.setParameter("status", searchDto.getStatus());
        }
        long count = (long) qCount.getSingleResult();
        int startPosition = pageIndex * pageSize;

        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        List<SalaryResultDto> entities = query.getResultList();
        Page<SalaryResultDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public SalaryResultDto getById(UUID id) {
        SalaryResult entity = salaryResultRepository.findById(id).orElse(null);
        if (entity == null)
            return null;

        return new SalaryResultDto(entity, true);
    }

    @Override
    public SalaryResultDto getBasicInfoById(UUID id) {
        SalaryResult entity = salaryResultRepository.findById(id).orElse(null);
        if (entity == null)
            return null;

        SalaryResultDto response = new SalaryResultDto(entity);

        SalaryTemplate salaryTemplate = entity.getSalaryTemplate();

        if (salaryTemplate.getTemplateItemGroups() != null && !salaryTemplate.getTemplateItemGroups().isEmpty()) {
            if (response.getTemplateItemGroups() == null) {
                response.setTemplateItemGroups(new ArrayList<>());
            }

            List<SalaryTemplateItemGroupDto> groupColumns = new ArrayList<>();

            for (SalaryTemplateItemGroup groupColumnEntity : salaryTemplate.getTemplateItemGroups()) {
                SalaryTemplateItemGroupDto groupColumn = new SalaryTemplateItemGroupDto(groupColumnEntity, true);

                groupColumns.add(groupColumn);
            }

            Collections.sort(groupColumns, new Comparator<SalaryTemplateItemGroupDto>() {
                @Override
                public int compare(SalaryTemplateItemGroupDto o1, SalaryTemplateItemGroupDto o2) {
                    if (o1.getName() == null && o2.getName() == null)
                        return 0;
                    if (o1.getName() == null)
                        return 1;
                    if (o2.getName() == null)
                        return -1;

                    return o1.getName().compareTo(o2.getName());
                }
            });

            response.getTemplateItemGroups().clear();
            response.getTemplateItemGroups().addAll(groupColumns);
        } else {
            response.setResultItemGroups(new ArrayList<>());
        }

        if (salaryTemplate.getTemplateItems() != null && !salaryTemplate.getTemplateItems().isEmpty()) {
            if (response.getTemplateItems() == null) {
                response.setTemplateItems(new ArrayList<>());
            }

            List<SalaryTemplateItemDto> columns = new ArrayList<>();

            for (SalaryTemplateItem columnEntity : salaryTemplate.getTemplateItems()) {
                SalaryTemplateItemDto groupColumn = new SalaryTemplateItemDto(columnEntity, true);

                columns.add(groupColumn);
            }

            Collections.sort(columns, new Comparator<SalaryTemplateItemDto>() {
                @Override
                public int compare(SalaryTemplateItemDto o1, SalaryTemplateItemDto o2) {
                    // First, compare by displayOrder
                    if (o1.getDisplayOrder() == null && o2.getDisplayOrder() == null)
                        return 0;
                    if (o1.getDisplayOrder() == null)
                        return 1;
                    if (o2.getDisplayOrder() == null)
                        return -1;

                    int orderComparison = o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
                    if (orderComparison != 0) {
                        return orderComparison;
                    }

                    // If displayOrder is the same, compare by displayName (handling nulls)
                    if (o1.getDisplayName() == null && o2.getDisplayName() == null)
                        return 0;
                    if (o1.getDisplayName() == null)
                        return 1;
                    if (o2.getDisplayName() == null)
                        return -1;
                    return o1.getDisplayName().compareTo(o2.getDisplayName());
                }
            });

            response.getTemplateItems().clear();
            response.getTemplateItems().addAll(columns);
        } else {
            response.setTemplateItems(new ArrayList<>());
        }

        return response;
    }

    @Override
    public SalaryResultDto getSalaryResultBoard(UUID id) {
        SalaryResultDto response = this.getConfigSalaryResult(id);

        if (response == null)
            return null;
        SalaryResult entity = salaryResultRepository.findById(id).orElse(null);

        if (entity != null && entity.getSalaryResultStaffs() != null && !entity.getSalaryResultStaffs().isEmpty()) {
            if (response.getSalaryResultStaffs() == null) {
                response.setSalaryResultStaffs(new ArrayList<>());
            }

            List<SalaryResultStaff> staffData = new ArrayList<>(entity.getSalaryResultStaffs());

            Collections.sort(staffData, new Comparator<SalaryResultStaff>() {
                @Override
                public int compare(SalaryResultStaff o1, SalaryResultStaff o2) {
                    // First, compare by displayOrder
                    if (o1.getStaff() == null && o2.getStaff() == null)
                        return 0;
                    if (o1.getStaff().getStaffCode() == null)
                        return 1;
                    if (o2.getStaff().getStaffCode() == null)
                        return -1;

                    int orderComparison = o1.getStaff().getStaffCode().compareTo(o2.getStaff().getStaffCode());
                    if (orderComparison != 0) {
                        return orderComparison;
                    }

                    // If displayOrder is the same, compare by displayName (handling nulls)
                    if (o1.getStaff().getDisplayName() == null && o2.getStaff().getDisplayName() == null)
                        return 0;
                    if (o1.getStaff().getDisplayName() == null)
                        return 1;
                    if (o2.getStaff().getDisplayName() == null)
                        return -1;
                    return o1.getStaff().getDisplayName().compareTo(o2.getStaff().getDisplayName());
                }
            });

            List<SalaryResultStaffDto> rows = new ArrayList<>();
            for (SalaryResultStaff tableRow : staffData) {
                SalaryResultStaffDto row = new SalaryResultStaffDto(tableRow, true);

                row.setStaff(null);

                rows.add(row);
            }

            response.getSalaryResultStaffs().clear();
            response.getSalaryResultStaffs().addAll(rows);
        } else {
            response.setSalaryResultStaffs(new ArrayList<>());
        }

        return response;
    }

    @Override
    public SalaryResultDto getConfigSalaryResult(UUID id) {
        SalaryResult entity = salaryResultRepository.findById(id).orElse(null);
        if (entity == null)
            return null;

        SalaryResultDto response = new SalaryResultDto(entity, true);

        SalaryTemplate salaryTemplate = entity.getSalaryTemplate();

        if (salaryTemplate.getTemplateItemGroups() != null && !salaryTemplate.getTemplateItemGroups().isEmpty()) {
            if (response.getTemplateItemGroups() == null) {
                response.setTemplateItemGroups(new ArrayList<>());
            }

            List<SalaryTemplateItemGroupDto> groupColumns = new ArrayList<>();

            for (SalaryTemplateItemGroup groupColumnEntity : salaryTemplate.getTemplateItemGroups()) {
                SalaryTemplateItemGroupDto groupColumn = new SalaryTemplateItemGroupDto(groupColumnEntity, true);

                groupColumns.add(groupColumn);
            }

            Collections.sort(groupColumns, new Comparator<SalaryTemplateItemGroupDto>() {
                @Override
                public int compare(SalaryTemplateItemGroupDto o1, SalaryTemplateItemGroupDto o2) {
                    if (o1.getName() == null && o2.getName() == null)
                        return 0;
                    if (o1.getName() == null)
                        return 1;
                    if (o2.getName() == null)
                        return -1;

                    return o1.getName().compareTo(o2.getName());
                }
            });

            response.getTemplateItemGroups().clear();
            response.getTemplateItemGroups().addAll(groupColumns);
        } else {
            response.setResultItemGroups(new ArrayList<>());
        }

        if (salaryTemplate.getTemplateItems() != null && !salaryTemplate.getTemplateItems().isEmpty()) {
            if (response.getTemplateItems() == null) {
                response.setTemplateItems(new ArrayList<>());
            }

            List<SalaryTemplateItemDto> columns = new ArrayList<>();

            for (SalaryTemplateItem columnEntity : salaryTemplate.getTemplateItems()) {
                SalaryTemplateItemDto groupColumn = new SalaryTemplateItemDto(columnEntity, true);

                columns.add(groupColumn);
            }

            Collections.sort(columns, new Comparator<SalaryTemplateItemDto>() {
                @Override
                public int compare(SalaryTemplateItemDto o1, SalaryTemplateItemDto o2) {
                    // First, compare by displayOrder
                    if (o1.getDisplayOrder() == null && o2.getDisplayOrder() == null)
                        return 0;
                    if (o1.getDisplayOrder() == null)
                        return 1;
                    if (o2.getDisplayOrder() == null)
                        return -1;

                    int orderComparison = o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
                    if (orderComparison != 0) {
                        return orderComparison;
                    }

                    // If displayOrder is the same, compare by displayName (handling nulls)
                    if (o1.getDisplayName() == null && o2.getDisplayName() == null)
                        return 0;
                    if (o1.getDisplayName() == null)
                        return 1;
                    if (o2.getDisplayName() == null)
                        return -1;
                    return o1.getDisplayName().compareTo(o2.getDisplayName());
                }
            });

            response.getTemplateItems().clear();
            response.getTemplateItems().addAll(columns);
        } else {
            response.setTemplateItems(new ArrayList<>());
        }

        return response;
    }

    @Override
    @Modifying
    public Boolean remove(UUID id) {
        if (id == null)
            return false;

        SalaryResult entity = salaryResultRepository.findById(id).orElse(null);

        if (entity == null)
            return false;

        // Bảng lương đã bị khóa
        if (entity.getIsLocked() != null && entity.getIsLocked().equals(true)) {
            logger.info("Bảng lương đã bị khóa, không thể thao tác: ID: " + entity.getId());
            return false;
        }

//        List<SalaryResultStaff> payslips = new ArrayList<>(entity.getSalaryResultStaffs());
//        if (!payslips.isEmpty()) {
//            for (SalaryResultStaff payslip : payslips) {
//                payslip.setSalaryResult(null);
//            }
//        }

//        salaryResultStaffRepository.saveAllAndFlush(payslips);

        salaryResultRepository.delete(entity);

        entityManager.flush();
        entityManager.clear();

        return true;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean removeMultiple(List<UUID> ids) {
        if (ids == null)
            return false;
        boolean isValid = true;
        for (UUID id : ids) {
            boolean deleteRes = this.remove(id);
            if (!deleteRes)
                isValid = false;
        }
        return isValid;
    }

    @Override
    public SalaryResultDto findByCode(String code) {
        if (code == null || code.isEmpty())
            return null;
        List<SalaryResult> entities = salaryResultRepository.findByCode(code);
        if (entities != null && !entities.isEmpty()) {
            return new SalaryResultDto(entities.get(0));
        }
        return null;
    }

    @Override
    public Boolean isValidCode(SalaryResultDto dto) {
        if (dto == null)
            return false;

        // ID of SalaryResult is null => Create new SalaryResult
        // => Assure that there's no other SalaryResults using this code of new
        // SalaryResult
        // if there was any SalaryPeriod using new SalaryResult code, then this new code
        // is
        // invalid => return False
        // else return true
        if (dto.getId() == null) {
            List<SalaryResult> entities = salaryResultRepository.findByCode(dto.getCode());
            if (entities == null || entities.size() == 0) {
                return true;
            }
            return false;

        }
        // ID of SalaryResult is NOT null => SalaryResult is modified
        // => Assure that the modified code is not same to OTHER any SalaryResult code
        // if there was any SalaryResult using new SalaryResult code, then this new code
        // is
        // invalid => return False
        // else return true
        else {
            List<SalaryResult> entities = salaryResultRepository.findByCode(dto.getCode());
            if (entities == null || entities.size() == 0) {
                return true;
            }
            for (SalaryResult entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }

    public Workbook handleExcel(UUID id) {
        if (id == null) {
            return null;
        }
        String templatePath = "Empty.xlsx";
        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(templatePath)) {
            if (fileInputStream == null) {
                throw new IOException("File '" + templatePath + "' không tìm thấy trong classpath");
            }
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            configExcelStyle(workbook);
            SalaryResultDto salaryResultDto = this.getSalaryResultBoard(id);
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.createRow(0);
            List<String> headers = new ArrayList<>();
            if (salaryResultDto != null && salaryResultDto.getResultItems() != null) {
                for (SalaryResultItemDto salaryResultItem : salaryResultDto.getResultItems()) {
                    if (salaryResultItem != null) {
                        headers.add(salaryResultItem.getDisplayName());
                    }
                }

                for (int i = 0; i < headers.size(); i++) {
                    Cell headerCell = headerRow.createCell(i);
                    headerCell.setCellValue(headers.get(i));
                }
            }

            int rowIndex = 1;
            if (salaryResultDto.getSalaryResultStaffs() != null && salaryResultDto.getSalaryResultStaffs().size() > 0) {
                for (SalaryResultStaffDto salaryResultStaff : salaryResultDto.getSalaryResultStaffs()) {
                    Row dataRow = sheet.createRow(rowIndex++);
                    if (salaryResultStaff != null && salaryResultStaff.getSalaryResultStaffItems() != null
                            && salaryResultStaff.getSalaryResultStaffItems().size() > 0) {
                        List<String> data = new ArrayList<>();
                        for (SalaryResultStaffItemDto salaryResultStaffItem : salaryResultStaff
                                .getSalaryResultStaffItems()) {
                            data.add(salaryResultStaffItem.getValue());
                        }

                        for (int i = 0; i < data.size(); i++) {
                            Cell dataCell = dataRow.createCell(i);
                            dataCell.setCellValue(data.get(i));
                        }
                    }
                }
            }

            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            return workbook;
        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }

    public static void configExcelStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Times New Roman");
        font.setBold(true);

        cellStyle.setWrapText(true);

        Font fontNoBorder = workbook.createFont();
        fontNoBorder.setFontHeightInPoints((short) 10);
        fontNoBorder.setBold(true);
        fontNoBorder.setFontName("Times New Roman");

        CellStyle cellStyleNoBoder = workbook.createCellStyle();
        cellStyleNoBoder.setWrapText(true);
        cellStyleNoBoder.setFont(fontNoBorder);

        CellStyle cellStyleBoldTable = workbook.createCellStyle();
        cellStyleBoldTable.setWrapText(true);
        cellStyleBoldTable.setFont(font);
    }

    // Có thể tạo bảng lương mới hay không
    @Override
    public Boolean isValidToCreateSalaryBoard(SalaryResultDto dto) {
        if (dto == null || dto.getSalaryPeriod() == null || dto.getSalaryPeriod().getId() == null
                || dto.getSalaryTemplate() == null || dto.getSalaryTemplate().getId() == null)
            return false;

        SalaryPeriod salaryPeriod = salaryPeriodRepository.findById(dto.getSalaryPeriod().getId()).orElse(null);
        if (salaryPeriod == null)
            return false;

        SalaryTemplate salaryTemplate = salaryTemplateRepository.findById(dto.getSalaryTemplate().getId()).orElse(null);
        if (salaryTemplate == null)
            return false;

        List<SalaryResult> existedResults = salaryResultRepository.findByTemplateIdAndPeriodId(salaryTemplate.getId(),
                salaryPeriod.getId());

        if (existedResults != null && !existedResults.isEmpty()) {
            // Đã tạo bảng lương với kỳ lương và mẫu bảng lương được chọn
            return false;
        }

        return true;
    }

    @Override
    public SalaryResultDto createSalaryBoardByPeriodAndTemplate(SalaryResultDto dto) {
        LocalDateTime time = logTime("Bắt đầu tạo bảng lương");

        if (dto == null) {
            return null;
        }

        SalaryResult entity = new SalaryResult();

        if (dto.getId() != null){
            entity = salaryResultRepository.findById(dto.getId()).orElse(null);
            // Bảng lương đã bị khóa
            if (entity != null && entity.getIsLocked() != null && entity.getIsLocked().equals(true)) {
                return null;
            }
        }
        if (entity == null)
            entity = new SalaryResult();

        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());
        entity.setApprovalStatus(HrConstants.SalaryResulStaffApprovalStatus.NOT_APPROVED_YET.getValue());

        SalaryPeriod periodEntity = null;
        if (dto.getSalaryPeriod() != null) {
            periodEntity = salaryPeriodRepository.findById(dto.getSalaryPeriod().getId()).orElse(null);
            if (periodEntity == null)
                return null;

            entity.setSalaryPeriod(periodEntity);
        } else {
            entity.setSalaryPeriod(null);
        }

        SalaryTemplate templateEntity = null;
        if (dto.getSalaryTemplate() != null) {
            templateEntity = salaryTemplateRepository.findById(dto.getSalaryTemplate().getId())
                    .orElse(null);
            if (templateEntity == null)
                return null;

            entity.setSalaryTemplate(templateEntity);
        } else {
            entity.setSalaryTemplate(null);
        }

        entity = salaryResultRepository.saveAndFlush(entity);

        time = logTime("Tạo salaryResult thành công", time);

        // Lấy danh sách tất cả nhân viên được tính lương theo bảng lương
        SearchStaffDto searchStaffDto = new SearchStaffDto();

        SalaryTemplateDto salaryTemplateDto = new SalaryTemplateDto();
        if (templateEntity != null)
            salaryTemplateDto.setId(templateEntity.getId());

        SalaryPeriodDto salaryPeriodDto = new SalaryPeriodDto();
        if (periodEntity != null)
            salaryPeriodDto.setId(periodEntity.getId());

        searchStaffDto.setSalaryPeriod(salaryPeriodDto);
        searchStaffDto.setSalaryTemplateId(salaryTemplateDto.getId());
//        searchStaffDto.setIsExportExcel(true);
        int pageIndex = 1;
        int pageSize = 50;
        searchStaffDto.setPageSize(pageSize);
        searchStaffDto.setPageIndex(pageIndex);

        boolean hasNextPage = true;

        while (hasNextPage) {
            Page<StaffDto> calculateStaffs = staffService.searchByPage(searchStaffDto);
            time = logTime("Lấy trang bảng lương để tạo, trang " + pageIndex, time);

//            logger.info("New page: " + calculateStaffs.getContent().size() + " , pageIndex: " + pageIndex);

            hasNextPage = calculateStaffs.hasNext();
            pageIndex++;
            searchStaffDto.setPageIndex(pageIndex);

            for (StaffDto calStaff : calculateStaffs) {
                time = logTime("Bắt đầu tạo phiếu lương: " + calStaff.getDisplayName(), time);

//                logger.info("Generating payslip for staff: " + calStaff.getDisplayName());
                SalaryResultStaffDto payslipRequest = new SalaryResultStaffDto();

                payslipRequest.setSalaryPeriod(salaryPeriodDto);
                payslipRequest.setSalaryTemplate(salaryTemplateDto);
                payslipRequest.setStaff(calStaff);
                payslipRequest.setSalaryResultId(entity.getId());

                SalaryResultStaffDto createdPayslip = salaryResultStaffService.calculateSalaryStaff(payslipRequest);
                time = logTime("Tạo thành công phiếu lương: " + calStaff.getDisplayName(), time);
            }

            entityManager.flush();
            entityManager.clear();
        }

        time = logTime("Tạo thành công bảng lương: ", time);

        return new SalaryResultDto(entity);
    }

    @Override
    public List<SalaryTemplateItemDto> getListTemplateItem(UUID salaryResultId) {
        if (salaryResultId == null) {
            return null;
        }

        SalaryResult entity = salaryResultRepository.findById(salaryResultId).orElse(null);

        if (entity == null)
            return null;

        SalaryTemplate salaryTemplate = entity.getSalaryTemplate();
        if (salaryTemplate == null)
            return null;

        Set<SalaryTemplateItem> templateItems = salaryTemplate.getTemplateItems();

        List<SalaryTemplateItemDto> result = new ArrayList<>();

        if (templateItems != null && !templateItems.isEmpty()) {
            for (SalaryTemplateItem item : templateItems) {
                result.add(new SalaryTemplateItemDto(item));
            }
            // Sắp xếp theo displayOrder
            result.sort(Comparator.comparing(SalaryTemplateItemDto::getDisplayOrder));
        }

        return result;
    }

    private List<SalaryTemplateItemDto> getMannualTypeSalaryValue(SalaryResultDto salaryResult) {
        List<SalaryTemplateItemDto> response = new ArrayList<>();

        if (salaryResult == null || salaryResult.getTemplateItems() == null
                || salaryResult.getTemplateItems().isEmpty()) {
            return response;
        }

        for (SalaryTemplateItemDto templateItem : salaryResult.getTemplateItems()) {
            if (templateItem.getCalculationType() != null && (templateItem.getCalculationType()
                    .equals(HrConstants.SalaryItemCalculationType.USER_FILL.getValue())
                    || templateItem.getCalculationType()
                    .equals(HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue()))) {
                response.add(templateItem);
            }
        }

        return response;
    }

    private void printMannuallyTypeSalaryColumns(List<SalaryTemplateItemDto> mannuallyTypeSalaryValueColumns,
                                                 Sheet sheet) {
        int rowIndex = 0;
        int cellIndex = 4;

        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }

        for (SalaryTemplateItemDto column : mannuallyTypeSalaryValueColumns) {
            String value = column.getCode();

            Cell cell = row.getCell(cellIndex);
            if (cell == null) {
                cell = row.createCell(cellIndex);
            }
            cell.setCellValue(value != null ? value : "");

            cellIndex++;
        }
    }

    private static final String TEMPLATE_PATH = "ImportSalaryResultStaffItems.xlsx";

    @Override
    public Workbook exportFileImportSalaryValueByFilter(SearchSalaryResultStaffDto dto) {
        if (dto == null) {
            return null;
        }

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(TEMPLATE_PATH)) {
            if (fileInputStream == null) {
                throw new IOException("File '" + TEMPLATE_PATH + "' không tìm thấy trong classpath");
            }

            SalaryResultDto salaryResult = this.searchSalaryResultBoard(dto);
            if (salaryResult == null)
                return null;

            List<SalaryResultStaffDto> printData = salaryResult.getSalaryResultStaffs();
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet importSheet = workbook.getSheetAt(0);

            // Dòng mẫu để clone style
            Row templateRow = importSheet.getRow(1); // Giả sử dòng 1 là dòng mẫu
            if (templateRow == null) {
                throw new IllegalStateException("Dòng template (row 1) không tồn tại trong file Excel.");
            }

            // Các trường cần nhập tay
            List<SalaryTemplateItemDto> mannuallyTypeSalaryValueColumns = this.getMannualTypeSalaryValue(salaryResult);
            this.printMannuallyTypeSalaryColumns(mannuallyTypeSalaryValueColumns, importSheet);

            int rowIndex = 2; // Bắt đầu từ dòng sau dòng template
            int orderNumber = 1;
            long startTime = System.nanoTime();

            for (SalaryResultStaffDto exportRow : printData) {
                if (exportRow == null)
                    continue;

                Row dataRow = importSheet.createRow(rowIndex);
                cloneRowStyle(templateRow, dataRow); // Clone style từ dòng mẫu

                int cellIndex = 0;

                // 0. STT
                setCellValueKeepStyle(dataRow, cellIndex++, orderNumber++);

                // 1. Mã NV
                setCellValueKeepStyle(dataRow, cellIndex++, exportRow.getStaffCode());

                // 2. Họ tên NV
                setCellValueKeepStyle(dataRow, cellIndex++, exportRow.getStaffName());

                // 3. Mã kỳ lương
                setCellValueKeepStyle(dataRow, cellIndex++, exportRow.getSalaryPeriod().getCode());

                // 4+. Các cột nhập tay hoặc hệ thống tính
                for (SalaryResultStaffItemDto salaryValue : exportRow.getSalaryResultStaffItems()) {
                    if (salaryValue.getSalaryTemplateItem() != null
                            && salaryValue.getSalaryTemplateItem().getCalculationType() != null
                            && (salaryValue.getSalaryTemplateItem().getCalculationType()
                            .equals(HrConstants.SalaryItemCalculationType.USER_FILL.getValue())
                            || salaryValue.getSalaryTemplateItem().getCalculationType()
                            .equals(HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue()))) {

                        String cellSalaryValue = salaryValue.getValue();
                        setCellValueKeepStyle(dataRow, cellIndex++, cellSalaryValue);
                    }
                }

                rowIndex++;
            }

            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;
            logger.info("Xuất mẫu dữ liệu nhập - Xử lý mất {} ms ", elapsedTimeMs);
            return workbook;

        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }

    private void cloneRowStyle(Row sourceRow, Row targetRow) {
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell sourceCell = sourceRow.getCell(i);
            if (sourceCell == null) continue;

            Cell targetCell = targetRow.createCell(i);
            targetCell.setCellStyle(sourceCell.getCellStyle());
            targetCell.setCellType(sourceCell.getCellType());
        }
    }

    private void setCellValueKeepStyle(Row row, int cellIndex, Object value) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            cell = row.createCell(cellIndex);
        }

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value != null) {
            cell.setCellValue(value.toString());
        } else {
            cell.setCellValue("");
        }
    }


    /**
     * Hàm hỗ trợ lấy style của cell mẫu theo index
     */
    private CellStyle getTemplateCellStyle(Row templateRow, int cellIndex) {
        if (templateRow == null) return null;
        Cell templateCell = templateRow.getCell(cellIndex);
        return (templateCell != null) ? templateCell.getCellStyle() : null;
    }


    private SearchStaffSalaryTemplateDto convertToSearchStaffSalaryTemplateDto(SearchSalaryResultDto dto) {
        SearchStaffSalaryTemplateDto response = new SearchStaffSalaryTemplateDto();

        response.setStaffId(dto.getStaffId());
        response.setDepartmentId(dto.getDepartmentId());
        response.setOrganizationId(dto.getOrganizationId());
        response.setPositionTitleId(dto.getPositionTitleId());
        response.setPositionId(dto.getPositionId());

        return response;
    }

    @Override
    public SalaryResultDto searchSalaryResultBoard(SearchSalaryResultStaffDto dto) {
        SalaryResult entity = salaryResultRepository.findById(dto.getSalaryResultId()).orElse(null);
        if (entity == null) {
            return null;
        }

        SalaryResultDto result = new SalaryResultDto(entity, true);

        dto.setSalaryResult(result);
        dto.setSalaryResultId(result.getId());
        dto.setSalaryPeriod(result.getSalaryPeriod());
        dto.setSalaryPeriodId(result.getSalaryPeriod().getId());
        dto.setSalaryTemplateId(result.getSalaryTemplate().getId());

        SalaryTemplate salaryTemplate = entity.getSalaryTemplate();

        if (salaryTemplate.getTemplateItemGroups() != null && !salaryTemplate.getTemplateItemGroups().isEmpty()) {
            if (result.getTemplateItemGroups() == null) {
                result.setTemplateItemGroups(new ArrayList<>());
            }

            List<SalaryTemplateItemGroupDto> groupColumns = new ArrayList<>();

            for (SalaryTemplateItemGroup groupColumnEntity : salaryTemplate.getTemplateItemGroups()) {
                SalaryTemplateItemGroupDto groupColumn = new SalaryTemplateItemGroupDto(groupColumnEntity, true);

                groupColumns.add(groupColumn);
            }

            Collections.sort(groupColumns, new Comparator<SalaryTemplateItemGroupDto>() {
                @Override
                public int compare(SalaryTemplateItemGroupDto o1, SalaryTemplateItemGroupDto o2) {
                    if (o1.getName() == null && o2.getName() == null)
                        return 0;
                    if (o1.getName() == null)
                        return 1;
                    if (o2.getName() == null)
                        return -1;

                    return o1.getName().compareTo(o2.getName());
                }
            });

            result.getTemplateItemGroups().clear();
            result.getTemplateItemGroups().addAll(groupColumns);
        } else {
            result.setResultItemGroups(new ArrayList<>());
        }

        if (salaryTemplate.getTemplateItems() != null && !salaryTemplate.getTemplateItems().isEmpty()) {
            if (result.getTemplateItems() == null) {
                result.setTemplateItems(new ArrayList<>());
            }

            List<SalaryTemplateItemDto> columns = new ArrayList<>();

            for (SalaryTemplateItem columnEntity : salaryTemplate.getTemplateItems()) {
                SalaryTemplateItemDto groupColumn = new SalaryTemplateItemDto(columnEntity, true);

                columns.add(groupColumn);
            }

            Collections.sort(columns, new Comparator<SalaryTemplateItemDto>() {
                @Override
                public int compare(SalaryTemplateItemDto o1, SalaryTemplateItemDto o2) {
                    // First, compare by displayOrder
                    if (o1.getDisplayOrder() == null && o2.getDisplayOrder() == null)
                        return 0;
                    if (o1.getDisplayOrder() == null)
                        return 1;
                    if (o2.getDisplayOrder() == null)
                        return -1;

                    int orderComparison = o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
                    if (orderComparison != 0) {
                        return orderComparison;
                    }

                    // If displayOrder is the same, compare by displayName (handling nulls)
                    if (o1.getDisplayName() == null && o2.getDisplayName() == null)
                        return 0;
                    if (o1.getDisplayName() == null)
                        return 1;
                    if (o2.getDisplayName() == null)
                        return -1;
                    return o1.getDisplayName().compareTo(o2.getDisplayName());
                }
            });

            result.getTemplateItems().clear();
            result.getTemplateItems().addAll(columns);
        } else {
            result.setTemplateItems(new ArrayList<>());
        }

        Page<SalaryResultStaffDto> salaryResultStaffPage = salaryResultStaffService.pagingSalaryResultStaff(dto);

        result.setSalaryResultStaffs(salaryResultStaffPage.getContent());

        return result;
    }

    @Override
    public SalaryResultDto recalculateSalaryBoard(UUID salaryResultId) {
        if (salaryResultId == null)
            return null;

        SalaryResultDto salaryResultDto = this.getBasicInfoById(salaryResultId);

        // Bảng lương đã bị khóa
        if (salaryResultDto.getIsLocked() != null && salaryResultDto.getIsLocked().equals(true)) {
            return null;
        }

        // Xoá bảng lương cũ
        this.remove(salaryResultId);

        // tạo bảng lương mới
        SalaryResultDto response = this.createSalaryBoardByPeriodAndTemplate(salaryResultDto);

        return response;
    }

    // Khóa bảng lương
    @Override
    public Boolean lockPayroll(UUID salaryResultId) {
        if (salaryResultId == null) {
            return false;
        }

        SalaryResult salaryResult = salaryResultRepository.findById(salaryResultId).orElse(null);
        if (salaryResult == null) {
            return false;
        }

        salaryResult.setIsLocked(true);
        salaryResult = salaryResultRepository.saveAndFlush(salaryResult);

        return true;
    }

    // Hủy khóa bảng lương
    @Override
    public Boolean unlockPayroll(UUID salaryResultId) {
        if (salaryResultId == null) {
            return false;
        }

        SalaryResult salaryResult = salaryResultRepository.findById(salaryResultId).orElse(null);
        if (salaryResult == null) {
            return false;
        }

        salaryResult.setIsLocked(false);
        salaryResult = salaryResultRepository.saveAndFlush(salaryResult);

        return true;
    }

















}
