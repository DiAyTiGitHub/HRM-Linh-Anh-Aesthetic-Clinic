package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchStaffSalaryTemplateDto;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.hr.utils.ExcelUtils;
import com.globits.salary.domain.SalaryPeriod;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.domain.SalaryTemplateItem;
import com.globits.salary.domain.StaffSalaryItemValue;
import com.globits.salary.domain.StaffSalaryTemplate;
import com.globits.salary.dto.*;
import com.globits.salary.dto.search.CalculateSalaryRequest;
import com.globits.salary.repository.SalaryPeriodRepository;
import com.globits.salary.repository.SalaryTemplateRepository;
import com.globits.salary.repository.StaffSalaryItemValueRepository;
import com.globits.salary.repository.StaffSalaryTemplateRepository;
import com.globits.salary.service.SalaryTemplateService;
import com.globits.salary.service.StaffSalaryTemplateService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StaffSalaryTemplateServiceImpl extends GenericServiceImpl<StaffSalaryTemplate, UUID> implements StaffSalaryTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(StaffSalaryTemplateDto.class);

    @Autowired
    private StaffSalaryTemplateRepository staffSalaryTemplateRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SalaryTemplateRepository salaryTemplateRepository;

    @Autowired
    private SalaryPeriodRepository salaryPeriodRepository;

    @Autowired
    private SalaryTemplateService salaryTemplateService;

    @Autowired
    private StaffSalaryItemValueRepository staffSalaryItemValueRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<SalaryTemplate> getListValidSalaryTemplatesOfStaffInPeriod(CalculateSalaryRequest dto) {
        if (dto == null || dto.getStaffId() == null || dto.getSalaryPeriodId() == null) return null;

        // Tính lương cho nhân viên nào
        Staff staff = staffRepository.findById(dto.getStaffId()).orElse(null);
        if (staff == null) return null;

        // Tính lương cho kỳ lương nào
        SalaryPeriod salaryPeriod = salaryPeriodRepository.findById(dto.getSalaryPeriodId()).orElse(null);
        if (salaryPeriod == null) return null;

        // Chuẩn hóa fromDate và toDate của
        if (salaryPeriod.getFromDate() != null) {
            salaryPeriod.setFromDate(DateTimeUtil.getStartOfDay(salaryPeriod.getFromDate()));
        }
        if (salaryPeriod.getToDate() != null) {
            salaryPeriod.setToDate(DateTimeUtil.getEndOfDay(salaryPeriod.getToDate()));
        }

        // Các mẫu tính được áp dụng của nhân viên
        List<SalaryTemplate> response = new ArrayList<>();

        //List<StaffSalaryTemplate> staffSalaryTemplates = staffSalaryTemplateRepository.findByStaffIdAndRangeTime(dto.getStaffId(), salaryPeriod.getFromDate(), salaryPeriod.getToDate());
        List<StaffSalaryTemplate> staffSalaryTemplates = staffSalaryTemplateRepository.findByStaffId(dto.getStaffId());

        for (StaffSalaryTemplate staffSalaryTemplate : staffSalaryTemplates) {
            SalaryTemplate salaryTemplate = staffSalaryTemplate.getSalaryTemplate();

            // Xử lý trường hợp chỉ import 1 cho 1 mẫu bảng lương, 1 kỳ lương
            if (dto.getSalaryTemplateId() != null && !salaryTemplate.getId().equals(dto.getSalaryTemplateId())) {
                continue;
            }

            response.add(salaryTemplate);
        }

        return response;
    }

    @Override
    public StaffSalaryTemplateDto saveOrUpdate(StaffSalaryTemplateDto dto) {
        StaffSalaryTemplateDto result = null;
        StaffSalaryTemplate entity = null;
        if (dto != null && dto.getStaff() != null && dto.getSalaryTemplate() != null) {
            if (dto.getId() != null) {
                entity = staffSalaryTemplateRepository.findById(dto.getId()).orElse(null);
            } else if (dto.getSalaryTemplate() != null && dto.getSalaryTemplate().getId() != null
                    && dto.getStaff() != null && dto.getStaff().getId() != null) {
                List<StaffSalaryTemplate> listEntity = staffSalaryTemplateRepository.findBySalaryTemplateIdAndStaffId(dto.getSalaryTemplate().getId(), dto.getStaff().getId());
                if (listEntity != null && listEntity.size() > 0) {
                    entity = listEntity.get(0);
                }
            }
            if (entity == null) {
                entity = new StaffSalaryTemplate();
            }
            Staff staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
            entity.setStaff(staff);
            SalaryTemplate salaryTemplate = salaryTemplateRepository.findById(dto.getSalaryTemplate().getId()).orElse(null);
            entity.setSalaryTemplate(salaryTemplate);
//            entity.setFromDate(dto.getFromDate());
//            entity.setToDate(dto.getToDate());
            entity = repository.save(entity);
        }
        result = new StaffSalaryTemplateDto(entity);
        return result;
    }

    @Override
    public StaffSalaryTemplateDto getById(UUID id) {
        StaffSalaryTemplateDto result = null;
        if (id != null) {
            StaffSalaryTemplate entity = repository.findById(id).orElse(null);
            result = new StaffSalaryTemplateDto(entity);
        }
        return result;
    }

    @Override
    public StaffSalaryTemplateDto findByStaffIdAndTemplateId(SearchStaffSalaryTemplateDto searchDto) {
        if (searchDto == null || searchDto.getStaffId() == null || searchDto.getSalaryTemplateId() == null) return null;

        List<StaffSalaryTemplate> availableResults = staffSalaryTemplateRepository.findBySalaryTemplateIdAndStaffId(searchDto.getSalaryTemplateId(), searchDto.getStaffId());

        if (availableResults == null || availableResults.isEmpty())
            return null;

        StaffSalaryTemplateDto response = new StaffSalaryTemplateDto(availableResults.get(0));

        return response;
    }


    @Override
    public UUID findStaffTemplateIdByStaffIdAndTemplateId(SearchStaffSalaryTemplateDto searchDto) {
        StaffSalaryTemplateDto staffTemplate = this.findByStaffIdAndTemplateId(searchDto);
        if (staffTemplate == null || staffTemplate.getId() == null) {
            return null;
        }

        return staffTemplate.getId();
    }

    @Override
    public Boolean deleteStaffSalaryTemplate(UUID id) {
        Boolean result = null;
        if (id != null) {
            repository.deleteById(id);
            result = true;
        }
        return result;
    }

    @Override
    public Page<StaffSalaryTemplateDto> searchByPage(SearchStaffSalaryTemplateDto searchDto) {
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

        String joinPositionStaff = "";
        boolean hasJoinMainPosition = false;
        if (searchDto.getPositionTitleId() != null || searchDto.getDepartmentId() != null || searchDto.getOrganizationId() != null) {
            joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.staff.id ";
            hasJoinMainPosition = true;
        }

        String orderBy = " ORDER BY entity.createDate DESC";

        String sqlCount = "select count(entity.id) from StaffSalaryTemplate as entity ";
        String sql = "select new com.globits.salary.dto.StaffSalaryTemplateDto(entity) from StaffSalaryTemplate entity ";

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword().toString())) {
            whereClause += " AND ( entity.staff.displayName LIKE :keyword OR entity.salaryTemplate.name LIKE :keyword ) ";
        }

        if (searchDto.getStaffSalaryTemplateId() != null) {
            whereClause += " AND ( entity.salaryTemplate.id = :salaryTemplateId ) ";
        } else if (searchDto.getSalaryTemplateId() != null) {
            whereClause += " AND ( entity.salaryTemplate.id = :salaryTemplateId ) ";
        }

        if (searchDto.getStaffId() != null && StringUtils.hasText(searchDto.getStaffId().toString())) {
            whereClause += " AND ( entity.staff.id  =: staffId ) ";
        }


        if (hasJoinMainPosition) {
            if (searchDto.getOrganizationId() != null && StringUtils.hasText(searchDto.getOrganizationId().toString())) {
                whereClause += " AND ( pos.department.organization.id  =: organizationId ) ";
            }
            if (searchDto.getDepartmentId() != null && StringUtils.hasText(searchDto.getDepartmentId().toString())) {
                whereClause += " AND ( pos.department.id  =: departmentId ) ";
            }
            if (searchDto.getPositionTitleId() != null && StringUtils.hasText(searchDto.getPositionTitleId().toString())) {
                whereClause += " AND ( pos.title.id  =: positionTitleId ) ";
            }
        }

        sql += joinPositionStaff + whereClause + orderBy;
        sqlCount += joinPositionStaff + whereClause;

        Query query = manager.createQuery(sql, StaffSalaryTemplateDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword().toString())) {
            query.setParameter("keyword", "%" + searchDto.getKeyword() + "%");
            qCount.setParameter("keyword", "%" + searchDto.getKeyword() + "%");
        }

        if (searchDto.getStaffSalaryTemplateId() != null) {
            query.setParameter("salaryTemplateId", searchDto.getStaffSalaryTemplateId());
            qCount.setParameter("salaryTemplateId", searchDto.getStaffSalaryTemplateId());
        } else if (searchDto.getSalaryTemplateId() != null) {
            query.setParameter("salaryTemplateId", searchDto.getSalaryTemplateId());
            qCount.setParameter("salaryTemplateId", searchDto.getSalaryTemplateId());
        }

        if (searchDto.getStaffId() != null && StringUtils.hasText(searchDto.getStaffId().toString())) {
            query.setParameter("staffId", searchDto.getStaffId());
            qCount.setParameter("staffId", searchDto.getStaffId());
        }

        if (hasJoinMainPosition) {
            if (searchDto.getOrganizationId() != null && StringUtils.hasText(searchDto.getOrganizationId().toString())) {
                query.setParameter("organizationId", searchDto.getOrganizationId());
                qCount.setParameter("organizationId", searchDto.getOrganizationId());
            }
            if (searchDto.getDepartmentId() != null && StringUtils.hasText(searchDto.getDepartmentId().toString())) {
                query.setParameter("departmentId", searchDto.getDepartmentId());
                qCount.setParameter("departmentId", searchDto.getDepartmentId());
            }
            if (searchDto.getPositionTitleId() != null && StringUtils.hasText(searchDto.getPositionTitleId().toString())) {
                query.setParameter("positionTitleId", searchDto.getPositionTitleId());
                qCount.setParameter("positionTitleId", searchDto.getPositionTitleId());
            }
        }

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        List<StaffSalaryTemplateDto> entities = query.getResultList();
        long count = (long) qCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public List<StaffSalaryTemplate> findBySalaryTemplateIdAndRangeTime(SearchStaffSalaryTemplateDto searchDto) {
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

        String joinPositionStaff = "";
        boolean hasJoinMainPosition = false;
        if (searchDto.getPositionTitleId() != null || searchDto.getDepartmentId() != null || searchDto.getOrganizationId() != null) {
            joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.staff.id ";
            hasJoinMainPosition = true;
        }

        String orderBy = " ORDER BY entity.fromDate DESC";

        String sql = "select entity from StaffSalaryTemplate entity  ";

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword().toString())) {
            whereClause += " AND ( entity.staff.displayName LIKE :keyword OR entity.salaryTemplate.name LIKE :keyword ) ";
        }

        if (searchDto.getStaffSalaryTemplateId() != null) {
            whereClause += " AND ( entity.salaryTemplate.id = :salaryTemplateId ) ";
        }

        if (searchDto.getStaffId() != null && StringUtils.hasText(searchDto.getStaffId().toString())) {
            whereClause += " AND ( entity.staff.id  =: staffId ) ";
        }
        if (searchDto.getSalaryTemplateId() != null) {
            whereClause += " AND (entity.salaryTemplate.id = :salaryTemplateId) ";
        }

        if (hasJoinMainPosition) {
            if (searchDto.getOrganizationId() != null && StringUtils.hasText(searchDto.getOrganizationId().toString())) {
                whereClause += " AND ( pos.department.organization.id  =: organizationId ) ";
            }
            if (searchDto.getDepartmentId() != null && StringUtils.hasText(searchDto.getDepartmentId().toString())) {
                whereClause += " AND ( pos.department.id  =: departmentId ) ";
            }
            if (searchDto.getPositionTitleId() != null && StringUtils.hasText(searchDto.getPositionTitleId().toString())) {
                whereClause += " AND ( pos.title.id  =: positionTitleId ) ";
            }
        }

        sql += joinPositionStaff + whereClause + orderBy;

        Query query = manager.createQuery(sql, StaffSalaryTemplateDto.class);

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword().toString())) {
            query.setParameter("keyword", "%" + searchDto.getKeyword() + "%");
        }
        if (searchDto.getStaffSalaryTemplateId() != null) {
            query.setParameter("salaryTemplateId", searchDto.getStaffSalaryTemplateId());
        }
        if (searchDto.getStaffId() != null && StringUtils.hasText(searchDto.getStaffId().toString())) {
            query.setParameter("staffId", searchDto.getStaffId());
        }
        if (searchDto.getSalaryTemplateId() != null) {
            query.setParameter("salaryTemplateId", searchDto.getSalaryTemplateId());
        }

        if (hasJoinMainPosition) {
            if (searchDto.getOrganizationId() != null && StringUtils.hasText(searchDto.getOrganizationId().toString())) {
                query.setParameter("organizationId", searchDto.getOrganizationId());
            }
            if (searchDto.getDepartmentId() != null && StringUtils.hasText(searchDto.getDepartmentId().toString())) {
                query.setParameter("departmentId", searchDto.getDepartmentId());
            }
            if (searchDto.getPositionTitleId() != null && StringUtils.hasText(searchDto.getPositionTitleId().toString())) {
                query.setParameter("positionTitleId", searchDto.getPositionTitleId());
            }
        }

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        List<StaffSalaryTemplate> entities = query.getResultList();

        return entities;
    }

    @Override
    public StaffSalaryTemplateDto updateStaffSalaryTemplate(StaffSalaryTemplateDto dto) {
        if (dto == null || dto.getId() == null || dto.getStaff() == null || dto.getStaff().getId() == null || dto.getSalaryTemplate() == null || dto.getSalaryTemplate().getId() == null)
            return null;

        StaffSalaryTemplate entity = staffSalaryTemplateRepository.findById(dto.getStaff().getId()).orElse(null);
        if (entity == null) return null;

        SalaryTemplate salaryTemplate = salaryTemplateRepository.findById(dto.getSalaryTemplate().getId()).orElse(null);
        if (salaryTemplate == null) return null;

        Staff staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        if (staff == null) return null;

        entity.setSalaryTemplate(salaryTemplate);
        entity.setStaff(staff);

        StaffSalaryTemplate response = staffSalaryTemplateRepository.save(entity);
        return new StaffSalaryTemplateDto(response);
    }

    @Override
    @Transactional
    public Integer saveListStaffSalaryTemplate(StaffSalaryTemplateDto dto) {
        if (dto == null || dto.getStaffs() == null || dto.getStaffs().isEmpty() || dto.getSalaryTemplate() == null || dto.getSalaryTemplate().getId() == null)
            return null;
        SalaryTemplate salaryTemplate = salaryTemplateRepository.findById(dto.getSalaryTemplate().getId()).orElse(null);
        if (salaryTemplate == null) return null;
        int result = 0;
        for (StaffDto item : dto.getStaffs()) {
            Staff staff = staffRepository.findById(item.getId()).orElse(null);
            if (staff == null) continue;
            StaffSalaryTemplate entity = new StaffSalaryTemplate();
            entity.setSalaryTemplate(salaryTemplate);
            entity.setStaff(staff);
            staffSalaryTemplateRepository.save(entity);
            result++;
        }
        return result;
    }

    @Override
    public Page<SalaryTemplateDto> getSalaryTemplatesOfStaff(CalculateSalaryRequest searchDto) {
        List<SalaryTemplateDto> response = new ArrayList<>();

        if (searchDto == null || searchDto.getSalaryPeriodId() == null || searchDto.getStaffId() == null)
            return null;

        Staff staff = staffRepository.findById(searchDto.getStaffId()).orElse(null);
        if (staff == null) return null;

        SalaryPeriod salaryPeriod = salaryPeriodRepository.findById(searchDto.getSalaryPeriodId()).orElse(null);
        if (salaryPeriod == null) return null;

        // Chuẩn hóa fromDate và toDate của
        if (salaryPeriod.getFromDate() != null) {
            salaryPeriod.setFromDate(DateTimeUtil.getStartOfDay(salaryPeriod.getFromDate()));
        }
        if (salaryPeriod.getToDate() != null) {
            salaryPeriod.setToDate(DateTimeUtil.getEndOfDay(salaryPeriod.getToDate()));
        }

        List<StaffSalaryTemplate> staffSalaryTemplates = staffSalaryTemplateRepository.findByStaffId(staff.getId());
        if (staffSalaryTemplates == null || staffSalaryTemplates.isEmpty()) {
            return null;
        }
        for (StaffSalaryTemplate staffSalaryTemplate : staffSalaryTemplates) {
            SalaryTemplateDto templateDto = new SalaryTemplateDto();
            templateDto.setId(staffSalaryTemplate.getSalaryTemplate().getId());
            templateDto.setCode(staffSalaryTemplate.getSalaryTemplate().getCode());
            templateDto.setName(staffSalaryTemplate.getSalaryTemplate().getName());

            response.add(templateDto);
        }
        return new PageImpl<>(response);
    }

    @Override
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null)
            return false;
        boolean isValid = true;

        for (UUID itemId : ids) {
            boolean deleteRes = this.deleteStaffSalaryTemplate(itemId);
            if (!deleteRes)
                isValid = false;
        }

        return isValid;
    }

    @Override
    public ByteArrayOutputStream exportImportResultStaffSalaryTemplate(MultipartFile file) throws IOException {
        // Đọc workbook gốc từ file đầu vào
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        // Lấy danh sách dữ liệu từ file Excel
        ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
        // Đọc file excel convert về ImportStaffSalaryTemplateDto
        List<ImportStaffSalaryTemplateDto> listData = this.readImportResultStaffSalaryTemplate(bis);
        // Tạo sheet mới để ghi kết quả xử lý
        // save ImportStaffSalaryTemplateDto
        XSSFSheet resultSheet = this.createUniqueSheet(workbook, "Kết quả xử lý");

        //Tạo dòng header ở dòng 0
        Row header = resultSheet.createRow(0);
        header.createCell(0).setCellValue("Dòng");
        header.createCell(1).setCellValue("Kết quả");

        //Nếu có dữ liệu thì ghi vào từ dòng 1 trở đi
        if (listData != null && !listData.isEmpty()) {
            for (int j = 0; j < listData.size(); j++) {
                ImportStaffSalaryTemplateDto dto = listData.get(j);
                String result = this.saveImportStaffSalaryTemplateDto(dto);

                // Ghi dữ liệu bắt đầu từ dòng 1 (i + 1)
                Row row = resultSheet.createRow(j + 1);
                row.createCell(0).setCellValue(dto.getIndex() + 1);
                row.createCell(1).setCellValue(result);
            }
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        return bos;
    }

    private String saveImportStaffSalaryTemplateDto(ImportStaffSalaryTemplateDto dto) {
        // Kiểm tra dữ liệu đầu vào
        if (dto == null) {
            return "Không có dữ liệu";
        }

        // Loại bỏ khoảng trắng thừa và kiểm tra mã mẫu bảng lương
        String salaryTemplateCode = StringUtils.trimWhitespace(dto.getSalaryTemplateCode());
        if (!StringUtils.hasText(salaryTemplateCode)) {
            return "Không xác định được mã mẫu bảng lương";
        }

        // Kiểm tra mã nhân viên
        String staffCode = StringUtils.trimWhitespace(dto.getStaffCode());
        if (!StringUtils.hasText(staffCode)) {
            return "Không xác định được mã nhân viên";
        }

        // Truy vấn mẫu bảng lương và nhân viên một lần
        List<SalaryTemplate> salaryTemplates = salaryTemplateRepository.findByCode(salaryTemplateCode);
        if (CollectionUtils.isEmpty(salaryTemplates)) {
            return "Không tìm thấy mẫu bảng lương với mã: " + salaryTemplateCode;
        }
        SalaryTemplate salaryTemplate = salaryTemplates.get(0);

        List<Staff> staffList = staffRepository.findByCode(staffCode);
        if (CollectionUtils.isEmpty(staffList)) {
            return "Không tìm thấy nhân viên với mã: " + staffCode;
        }
        Staff staff = staffList.get(0);

        logger.info("Cập nhật GTMBL cho NV: " + staff.getDisplayName() + " trong MBL " + salaryTemplate.getCode());

        try {
            // Theo dõi trạng thái tạo mới
            boolean hasNewRecord = false;

            // Tìm StaffSalaryTemplate
            Optional<StaffSalaryTemplate> optionalEntity = staffSalaryTemplateRepository
                    .findBySalaryTemplateIdAndStaffId(salaryTemplate.getId(), staff.getId())
                    .stream()
                    .findFirst();

            // Kiểm tra nếu bản ghi tồn tại, nếu không thì tạo mới
            StaffSalaryTemplate entity;
            if (optionalEntity.isPresent()) {
                entity = optionalEntity.get();
            } else {
                entity = new StaffSalaryTemplate();
                hasNewRecord = true; // Đánh dấu là tạo mới
            }

            entity.setSalaryTemplate(salaryTemplate);
            entity.setStaff(staff);
            staffSalaryTemplateRepository.save(entity);

            // Lấy danh sách template items và input values
            Set<SalaryTemplateItem> templateItems = salaryTemplate.getTemplateItems();
            Map<String, String> inputValues = dto.getMapSalaryTemplateItem();

            // Tạo Map để lưu StaffSalaryItemValue hiện có, giảm số lần truy vấn DB
            Map<UUID, StaffSalaryItemValue> existingValues = new HashMap<>();
            if (templateItems != null && !templateItems.isEmpty()) {
                List<UUID> salaryItemIds = templateItems.stream()
                        .filter(item -> item.getSalaryItem() != null)
                        .map(item -> item.getSalaryItem().getId())
                        .collect(Collectors.toList());

                // Truy vấn tất cả StaffSalaryItemValue một lần
                List<StaffSalaryItemValue> listValueEntities = staffSalaryItemValueRepository
                        .findCurrentByStaffIdAndSalaryItemIds(staff.getId(), salaryItemIds);

                // Chuyển thành Map để tra cứu nhanh
                existingValues = listValueEntities.stream()
                        .collect(Collectors.toMap(
                                value -> value.getSalaryItem().getId(),
                                value -> value,
                                (v1, v2) -> v1 // Giữ bản ghi đầu tiên nếu có trùng lặp
                        ));
            }

            // Xử lý và lưu StaffSalaryItemValue
            List<StaffSalaryItemValue> itemValues = new ArrayList<>();
            if (templateItems != null) {
                for (SalaryTemplateItem item : templateItems) {
                    if (item.getSalaryItem() == null) {
                        continue;
                    }

                    // Tìm hoặc tạo mới StaffSalaryItemValue
//	                StaffSalaryItemValue valueEntity = existingValues.getOrDefault(
//	                        item.getSalaryItem().getId(),
//	                        null
//	                );
//	                if (valueEntity == null) {
//	                    valueEntity = new StaffSalaryItemValue();
//	                    hasNewRecord = true; // Đánh dấu là tạo mới
//	                }

                    StaffSalaryItemValue valueEntity = existingValues.getOrDefault(
                            item.getSalaryItem().getId(),
                            null
                    );

                    boolean isCreateNew = false;

                    if (valueEntity == null) {
                        isCreateNew = true;
                        valueEntity = new StaffSalaryItemValue();
                    }

                    valueEntity.setSalaryItem(item.getSalaryItem());
                    valueEntity.setStaff(staff);
                    valueEntity.setCalculationType(item.getCalculationType());
                    double oldValue = 0.0;
                    if (valueEntity.getValue() != null) {
                        oldValue = valueEntity.getValue();
                    }
                    // Xử lý giá trị đầu vào
                    String strValue = inputValues.get(item.getCode());
                    Double finalValue = 0.0;
                    try {
                        if (StringUtils.hasText(strValue)) {
                            finalValue = Double.parseDouble(strValue);
                        } else if (item.getSalaryItem() != null
                                && StringUtils.hasText(item.getSalaryItem().getDefaultValue())) {
                            finalValue = Double.parseDouble(item.getSalaryItem().getDefaultValue());
                        }
                    } catch (NumberFormatException ex) {
                        logger.error("Import mẫu bảng lương: Sai định dạng số cho mã: " + item.getCode());
//                        return "Sai định dạng số cho mã: " + item.getCode();
                        finalValue = 0.0;
                    }
//                    valueEntity.setValue(finalValue);
                    double newValue = finalValue;


                    // Xử lý ta lịch sử cho thành phần lương cố định
                    // Nếu là tạo mới => Cập nhật thời gian bắt đầu áp dụng
                    if (isCreateNew) {
                        valueEntity.setValue(newValue);
                        valueEntity.setFromDate(new Date());
                        valueEntity.setIsCurrent(true);

                        itemValues.add(valueEntity);
                    }
                    // Nếu chưa là tạo mới => Kiểm tra giá trị có bị thay đổi không
                    else {
                        // Nếu giá trị không bị thay đổi => Bỏ qua
                        if (oldValue == newValue) {

                        }
                        // Nếu giá trị bị thay đổi => Tạo lịch sử, đồng thời tạo bản ghi mới lưu giá trị mới{
                        else {
                            // Kết thúc giá trị cũ
                            valueEntity.setIsCurrent(false);
                            valueEntity.setToDate(new Date());

                            itemValues.add(valueEntity);

                            // Tạo giá trị hiện thời mới
                            StaffSalaryItemValue newStartValue = new StaffSalaryItemValue();
                            newStartValue.setStaff(valueEntity.getStaff());
                            newStartValue.setValue(newValue);
                            newStartValue.setSalaryItem(valueEntity.getSalaryItem());
                            newStartValue.setFromDate(new Date());
                            newStartValue.setTemplateItem(valueEntity.getTemplateItem());
                            newStartValue.setIsCurrent(true);
                            newStartValue.setCalculationType(valueEntity.getCalculationType());

                            itemValues.add(newStartValue);
                        }
                    }


                }
            }

            // Lưu tất cả StaffSalaryItemValue một lần
            if (!itemValues.isEmpty()) {
                staffSalaryItemValueRepository.saveAllAndFlush(itemValues);
            }

            entityManager.flush();
            entityManager.clear();

            // Trả về thông báo dựa trên trạng thái
            return hasNewRecord ? "Tạo thành công" : "Cập nhật thành công";

        } catch (Exception e) {
            // Ghi log lỗi thay vì in stack trace
            logger.error("Lỗi khi lưu StaffSalaryTemplate: {}", e.getMessage(), e);
            return "Tạo không thành công";
        }
    }

    public List<ImportStaffSalaryTemplateDto> readImportResultStaffSalaryTemplate(InputStream inputStream) {
        // Khởi tạo danh sách kết quả chứa các DTO
        List<ImportStaffSalaryTemplateDto> resultList = new ArrayList<>();
        // Định dạng ngày để parse các cột ngày tháng
        SimpleDateFormat formatter = new SimpleDateFormat("d/M/yyyy");

        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            // Lấy sheet đầu tiên từ file Excel
            XSSFSheet sheet = workbook.getSheetAt(0);
            // Nếu sheet rỗng, trả về danh sách rỗng
            if (sheet == null) return resultList;

            // Đọc dòng header (dòng 1) để lấy các mã thành phần lương
            Row headerRow = sheet.getRow(1);
            // Nếu dòng header rỗng, trả về danh sách rỗng
            if (headerRow == null) return resultList;
            // Lấy số cột tối đa trong dòng header
            int columnCount = headerRow.getLastCellNum();

            // Lưu trữ ánh xạ từ chỉ số cột sang mã thành phần lương
            Map<Integer, String> keyMap = new HashMap<>();
            // Lưu trữ cột cuối cùng xuất hiện của mỗi mã thành phần lương để ghi đè trùng lặp
            Map<String, Integer> lastKeyColumn = new HashMap<>();

            // Duyệt các cột từ cột 5 để lấy mã thành phần lương
            for (int col = 5; col < columnCount; col++) {
                String key = ExcelUtils.parseStringCellValue(ExcelUtils.getCellSafe(headerRow, col));
                if (StringUtils.hasText(key)) {
                    // Lưu ánh xạ từ chỉ số cột sang mã thành phần lương
                    keyMap.put(col, key);
                    // Cập nhật cột cuối cùng cho mã thành phần lương này, ghi đè nếu trùng
                    lastKeyColumn.put(key, col);
                }
            }

            // Bắt đầu xử lý dữ liệu từ dòng index 2
            int dataStartRow = 2;
            // Duyệt qua từng dòng dữ liệu của nhân viên
            for (int i = dataStartRow; i <= sheet.getLastRowNum(); i++) {
                // Lấy dòng hiện tại của nhân viên
                Row row = sheet.getRow(i);
                // Tạo DTO mới cho nhân viên
                ImportStaffSalaryTemplateDto dto = new ImportStaffSalaryTemplateDto();

                String rowIndexStr = ExcelUtils.parseStringCellValue(ExcelUtils.getCellSafe(row, 0));
                Integer rowIndex = ExcelUtils.convertToInteger(rowIndexStr);
                dto.setIndex(rowIndex);
                dataStartRow++;

                // Lấy mã nhân viên từ cột 1
                dto.setStaffCode(ExcelUtils.parseStringCellValue(ExcelUtils.getCellSafe(row, 1)));
                // Lấy tên nhân viên từ cột 2
                dto.setStaffName(ExcelUtils.parseStringCellValue(ExcelUtils.getCellSafe(row, 2)));

                // Lấy thông tin mẫu lương từ dòng header
                // Mã mẫu lương từ cột 3
                dto.setSalaryTemplateCode(ExcelUtils.parseStringCellValue(ExcelUtils.getCellSafe(row, 3)));
                // Tên mẫu lương từ cột 4
                dto.setSalaryTemplateName(ExcelUtils.parseStringCellValue(ExcelUtils.getCellSafe(row, 4)));
//	                // Ngày bắt đầu từ cột 5
//	                dto.setStartDate(parseDateCellValue(getCellSafe(row, 5), 2, 5, formatter));
//	                // Ngày kết thúc từ cột 6
//	                dto.setEndDate(parseDateCellValue(getCellSafe(row, 6), 2, 6, formatter));

                if (dto.getIndex() == null && !StringUtils.hasText(dto.getStaffCode()) && !StringUtils.hasText(dto.getSalaryTemplateCode())) {
                    continue;
                }

                if (dto.getStaffCode() != null && StringUtils.hasText(dto.getStaffCode())
                        && dto.getSalaryTemplateCode() != null && StringUtils.hasText(dto.getSalaryTemplateCode())) {
                    // Tạo map lưu mã thành phần lương và giá trị tương ứng cho nhân viên
                    Map<String, String> values = new HashMap<>();
                    // Duyệt qua các mã thành phần lương từ lastKeyColumn
                    for (Map.Entry<String, Integer> entry : lastKeyColumn.entrySet()) {
                        // Lấy mã thành phần lương (key) từ header
                        String key = entry.getKey();
                        // Lấy chỉ số cột tương ứng của mã này (cột cuối cùng nếu mã trùng)
                        int col = entry.getValue();
                        // Lấy giá trị từ ô của nhân viên tại cột tương ứng
                        String value = ExcelUtils.parseStringCellValue(ExcelUtils.getCellSafe(row, col));
                        // Thêm cặp key-value vào map: mã thành phần lương -> giá trị tại cột tương ứng
                        values.put(key, value);
                    }
                    // Gán map giá trị thành phần lương vào DTO
                    dto.setMapSalaryTemplateItem(values);
                }
                // Thêm DTO vào danh sách kết quả
                resultList.add(dto);
            }
        } catch (IOException e) {
            // Ném ngoại lệ nếu lỗi đọc file Excel
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        }

        // Trả về danh sách DTO
        return resultList;
    }


    private XSSFSheet createUniqueSheet(XSSFWorkbook workbook, String baseName) {
        String sheetName = baseName;
        int index = 1;

        // Kiểm tra sheet đã tồn tại chưa
        while (workbook.getSheet(sheetName) != null) {
            sheetName = baseName + " (" + index + ")";
            index++;
        }

        return workbook.createSheet(sheetName);
    }

    @Override
    public SalaryTemplateDto getSalaryTemplate(SearchStaffSalaryTemplateDto dto) {
        if (dto.getSalaryTemplateId() == null) {
            return null;
        }

        SalaryTemplateDto salaryTemplateDto = salaryTemplateService.getSalaryTemplate(dto.getSalaryTemplateId());

        if (salaryTemplateDto != null && salaryTemplateDto.getTemplateItems() != null) {
            List<Integer> acceptedCalculationTypes = Arrays.asList(
                    HrConstants.SalaryItemCalculationType.FIX.getValue() // Giá trị cố định
                    // Có thể thêm kiểu khác
            );
            List<SalaryTemplateItemDto> filteredItems = salaryTemplateDto.getTemplateItems().stream()
                    .filter(item -> acceptedCalculationTypes.contains(item.getCalculationType()))
                    .collect(Collectors.toList());
            salaryTemplateDto.setTemplateItems(filteredItems);
        }

        return salaryTemplateDto;
    }

}


