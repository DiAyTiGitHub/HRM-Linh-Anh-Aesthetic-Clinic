package com.globits.hr.service.impl;

import com.globits.core.domain.AdministrativeUnit;
import com.globits.core.domain.Department;
import com.globits.core.domain.Organization;
import com.globits.core.repository.AdministrativeUnitRepository;
import com.globits.core.repository.DepartmentRepository;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.function.ImportExcelMessageDto;
import com.globits.hr.dto.function.ImportStaffDto;
import com.globits.hr.dto.importExcel.*;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.repository.HRDepartmentRepository;
import com.globits.hr.repository.HrOrganizationRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.HRDepartmentService;
import com.globits.hr.service.HrOrganizationService;
import com.globits.hr.service.SystemConfigService;
import com.globits.hr.utils.ExcelUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HrOrganizationServiceImpl implements HrOrganizationService {
    private static final Logger logger = LoggerFactory.getLogger(HrOrganizationDto.class);


    @PersistenceContext
    EntityManager manager;

    @Autowired
    HrOrganizationRepository repos;

    @Autowired
    HRDepartmentRepository departmentRepository;

    @Autowired
    HRDepartmentService hRDepartmentService;

    @Autowired
    AdministrativeUnitRepository administrativeUnitRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public HrOrganizationDto saveOrUpdate(HrOrganizationDto dto) {
        if (dto != null && dto.getCode() != null && StringUtils.hasText(dto.getCode())) {
            HrOrganization hrOrganization = null;
            if (dto.getId() != null) {
                Optional<HrOrganization> optional = repos.findById(dto.getId());
                if (optional.isPresent()) {
                    hrOrganization = optional.get();
                }
            }
            if (hrOrganization == null) {
                hrOrganization = new HrOrganization();
            }
            hrOrganization.setCode(dto.getCode());
            hrOrganization.setName(dto.getName());
            hrOrganization.setSortNumber(dto.getSortNumber());
            hrOrganization.setOrganizationType(dto.getOrganizationType());
            hrOrganization.setLevel(dto.getLevel());
            hrOrganization.setWebsite(dto.getWebsite());

            hrOrganization.setTaxCode(dto.getTaxCode());
            hrOrganization.setAddressDetail(dto.getAddressDetail());
            hrOrganization.setFoundedDate(dto.getFoundedDate());

            if (dto.getRepresentative() != null) {
                Staff representative = staffRepository.findById(dto.getRepresentative().getId()).orElse(null);
                if (representative == null) return null;

                hrOrganization.setRepresentative(representative);
            } else {
                hrOrganization.setRepresentative(null);
            }

            if (dto.getParentId() != null) {
                Optional<HrOrganization> optional = repos.findById(dto.getParentId());
                if (optional.isPresent()) {
                    hrOrganization.setParent(optional.get());
                    checkCircularParent(hrOrganization, optional.get());
                }
            } else if (dto.getParent() != null && dto.getParent().getId() != null) {
                Optional<HrOrganization> optional = repos.findById(dto.getParent().getId());
                if (optional.isPresent()) {
                    hrOrganization.setParent(optional.get());
                    checkCircularParent(hrOrganization, optional.get());
                }
            } else {
                hrOrganization.setParent(null);
            }

            HrAdministrativeUnitDto administrativeUnit = dto.getAdministrativeUnit();
            HrAdministrativeUnitDto district = dto.getDistrict();
            HrAdministrativeUnitDto province = dto.getProvince();

            if (administrativeUnit != null && administrativeUnit.getId() != null) {
                administrativeUnitRepository.findById(administrativeUnit.getId())
                        .ifPresent(hrOrganization::setAdministrativeUnit);
            } else if (district != null && district.getId() != null) {
                administrativeUnitRepository.findById(district.getId())
                        .ifPresent(hrOrganization::setAdministrativeUnit);
            } else if (province != null && province.getId() != null) {
                administrativeUnitRepository.findById(province.getId())
                        .ifPresent(hrOrganization::setAdministrativeUnit);
            }

//            Set<HRDepartment> departments = new HashSet<>();
//            if (dto.getDepartments() != null && !dto.getDepartments().isEmpty()) {
//                for (HRDepartmentDto child : dto.getDepartments()) {
//                    if (child != null) {
//                        if (child.getId() != null) {
//                            HRDepartment hRDepartment = departmentRepository.findById(child.getId()).orElse(null);
//                            if (hRDepartment != null) {
//                                hRDepartment.setOrganization(hrOrganization);
//                                hRDepartment = hRDepartmentService.toHRDepartment(child, hRDepartment);
//                                departments.add(hRDepartment);
//                            }
//                        } else {
//                            HRDepartment hRDepartment = new HRDepartment();
//                            hRDepartment.setOrganization(hrOrganization);
//                            hRDepartmentService.toHRDepartment(child, hRDepartment);
//                            departments.add(hRDepartment);
//                        }
//                    }
//                }
//            }
//
//            if (hrOrganization.getDepartments() == null) {
//                hrOrganization.setDepartments(departments);
//            } else {
//                hrOrganization.getDepartments().clear();
//                hrOrganization.getDepartments().addAll(departments);
//            }

            hrOrganization = repos.save(hrOrganization);
            return new HrOrganizationDto(hrOrganization, false, false);
        }
        return null;
    }

    /**
     * Kiểm tra xem phòng ban mới được chọn làm cha có gây ra vòng lặp hay không.
     *
     * @param currentOrganization Phòng ban đang chỉnh sửa
     * @param selectedParent    Phòng ban được chọn làm cha
     */
    private void checkCircularParent(Organization currentOrganization, Organization selectedParent) {
        Organization parent = selectedParent;

        while (parent != null) {
            // Nếu một trong các cha là chính phòng ban hiện tại thì bị vòng lặp
            if (parent.getId().equals(currentOrganization.getId())) {
                throw new IllegalArgumentException("Không thể gán phòng ban cha gây ra vòng lặp.");
            }
            parent = parent.getParent(); // Lấy tiếp cha của cha (đi lên cây)
        }
    }


    @Override
    public Boolean deleteHrOrganization(UUID id) {
        if (id != null) {
            repos.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean deleteMultipleHrOrganizations(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return null;
        for (UUID id : ids) {
            boolean delete = deleteHrOrganization(id);
            if (!delete) return null;
        }
        return true;
    }

    @Override
    public HrOrganizationDto getHROrganization(UUID id) {
        if (id == null) return null;
        HrOrganization hrOrganization = repos.findById(id).orElse(null);
        if (hrOrganization == null) return null;

        HrOrganizationDto reponse = new HrOrganizationDto(hrOrganization, false, false);
        return reponse;
    }

    @Override
    public Page<HrOrganizationDto> searchByPage(SearchDto dto) {
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
        String orderBy = " ORDER BY entity.sortNumber DESC ";
        String sqlCount = "select count(entity.id) from HrOrganization as entity where entity.parent is null  ";
        String sql = "select new com.globits.hr.dto.HrOrganizationDto(entity,false,false) from HrOrganization as entity where entity.parent is null  ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text )";
        }
        if (dto.getOrganizationType() != null) {
            whereClause += " AND ( entity.organizationType = :organizationType )";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, HrOrganizationDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getOrganizationType() != null) {
            q.setParameter("organizationType", dto.getOrganizationType());
            qCount.setParameter("organizationType", dto.getOrganizationType());
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<HrOrganizationDto> entities = q.getResultList();
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
    public Page<HrOrganizationDto> pagingHrOrganizations(SearchDto dto) {
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
        String orderBy = " ORDER BY entity.sortNumber DESC ";
        String sqlCount = "select count(entity.id) from HrOrganization as entity where (1=1)  ";
        String sql = "select new com.globits.hr.dto.HrOrganizationDto(entity,false, false ) from HrOrganization as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text )";
        }
        if (dto.getOrganizationType() != null) {
            whereClause += " AND ( entity.organizationType = :organizationType )";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, HrOrganizationDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getOrganizationType() != null) {
            q.setParameter("organizationType", dto.getOrganizationType());
            qCount.setParameter("organizationType", dto.getOrganizationType());
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<HrOrganizationDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean isValidCode(HrOrganizationDto dto) {
        if (dto == null)
            return false;
        if (dto.getId() == null) {
            List<HrOrganization> entities = repos.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<HrOrganization> entities = repos.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (HrOrganization entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
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

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING -> cell.getStringCellValue().trim();
            case Cell.CELL_TYPE_NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }


    private Date parseDateCellValue(Cell cell, int rowIndex, int columnIndex, SimpleDateFormat dateFormat) {
        Date result = null;

        if (cell == null) {
            return null; // hoặc return default date nếu cần
        }

        CellType cellType = cell.getCellTypeEnum();

        try {
            if (cellType == CellType.STRING) {
                String strDate = cell.getStringCellValue().trim();

                if (!strDate.isEmpty()) {
                    Date parsedDate = dateFormat.parse(strDate);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(parsedDate);

                    int year = cal.get(Calendar.YEAR);
                    if (year < 1900 || year > 2100) {
                        System.err.println(String.format(
                                "[RowIndex: %d] [ColumnIndex: %d] Sai định dạng năm, Giá trị: %s",
                                rowIndex, columnIndex, strDate
                        ));
                        return null;
                    }

                    result = parsedDate;
                }

            } else if (cellType == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    result = cell.getDateCellValue();
                } else {
                    result = DateUtil.getJavaDate(cell.getNumericCellValue());
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(result);

                int year = cal.get(Calendar.YEAR);
                if (year < 1900 || year > 2100) {
                    System.err.println(String.format(
                            "[RowIndex: %d] [ColumnIndex: %d] Sai định dạng năm, Giá trị: %s",
                            rowIndex, columnIndex, cell.getNumericCellValue()
                    ));
                    return null;
                }

            } else {
                System.err.println(String.format(
                        "[RowIndex: %d] [ColumnIndex: %d] Không hỗ trợ kiểu dữ liệu: %s",
                        rowIndex, columnIndex, cellType
                ));
            }

        } catch (Exception ex) {
            System.err.println(String.format(
                    "[RowIndex: %d] [ColumnIndex: %d] Sai định dạng ngày tháng, Giá trị: %s",
                    rowIndex, columnIndex, cellType == CellType.STRING ? cell.getStringCellValue() : cell.toString()
            ));
            //ex.printStackTrace();
        }

        return result;
    }


    @Override
    public HrOrganizationImportResult readDataFromExcel(InputStream inputStream) {
        HrOrganizationImportResult importResult = new HrOrganizationImportResult();

        List<HrOrganizationImport> importSuccessRows = new ArrayList<>();
        List<HrOrganizationImport> importErrorRows = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            // Đọc dữ liệu import danh sách
            Sheet datatypeSheet = workbook.getSheetAt(0);

            try {
                int rowIndex = 1;
                int num = datatypeSheet.getLastRowNum();

                while (rowIndex <= num) {
                    Row currentRow = datatypeSheet.getRow(rowIndex);
                    Cell currentCell = null;

                    if (currentRow != null) {
                        List<String> errorMessages = new ArrayList<>();

                        HrOrganizationImport dto = new HrOrganizationImport();

                        // 0. Mã đơn vị
                        int index = 0;
                        currentCell = currentRow.getCell(index);
                        String code = this.getCellValueAsString(currentCell);
                        if (!StringUtils.hasText(code)) {
                            errorMessages.add("Chưa có mã đơn vị");
                        }
                        dto.setCode(code);

                        // 1. Tên đơn vị
                        index = 1;
                        currentCell = currentRow.getCell(index);
                        String name = this.getCellValueAsString(currentCell);
                        if (!StringUtils.hasText(name)) {
                            errorMessages.add("Chưa có tên đơn vị");
                        }
                        dto.setName(name);

                        // 2. Loai đơn vị
                        index = 2;
                        currentCell = currentRow.getCell(index);
                        Integer organizationType = ExcelUtils.getCellValue(currentCell, Integer.class);
                        dto.setOrganizationType(organizationType);

                        // 3. Website
                        index = 3;
                        currentCell = currentRow.getCell(index);
                        String website = this.getCellValueAsString(currentCell);

                        dto.setWebsite(website);

                        // 4. Mã đơn vị trực thuộc
                        index = 4;
                        currentCell = currentRow.getCell(index);
                        String parentOrgCode = this.getCellValueAsString(currentCell);
                        dto.setParentOrgCode(parentOrgCode);

                        // 5 Đơn vị trực thuộc
                        index = 5;
                        currentCell = currentRow.getCell(index);
                        String parentOrgName = this.getCellValueAsString(currentCell);
                        dto.setParentOrgName(parentOrgName);

                        // 6. Mã số thuế
                        index = 6;
                        currentCell = currentRow.getCell(index);
                        String taxCode = this.getCellValueAsString(currentCell);
                        dto.setTaxCode(taxCode);

                        // 7. Ngày thành lập
                        index = 7;
                        currentCell = currentRow.getCell(index);
                        dateFormat.setLenient(false);
                        Date foundDate = parseDateCellValue(currentCell, rowIndex, index, dateFormat);
                        dto.setFoundDate(foundDate);

                        // 8. Mã nhân viên đại diện
                        index = 8;
                        currentCell = currentRow.getCell(index);
                        String representativeCode = this.getCellValueAsString(currentCell);
                        dto.setRepresentativeCode(representativeCode);

                        // 9. Nhân viên đại diện
                        index = 9;
                        currentCell = currentRow.getCell(index);
                        String representativeName = this.getCellValueAsString(currentCell);
                        dto.setRepresentativeName(representativeName);

                        // 10. Mã tỉnh/thành phố
                        index = 10;
                        currentCell = currentRow.getCell(index);
                        String provinceCode = ExcelUtils.getCellValue(currentCell, String.class);
                        if (provinceCode != null && provinceCode.endsWith(".0")) {
                            provinceCode = provinceCode.substring(0, provinceCode.length() - 2);
                        }
                        dto.setProvinceCode(provinceCode);

                        // 11. Tên tỉnh/thành phố
                        index = 11;
                        currentCell = currentRow.getCell(index);
                        String provinceName = ExcelUtils.getCellValue(currentCell, String.class);
                        dto.setProvinceName(provinceName);

                        // 12. Mã quận/huyện
                        index = 12;
                        currentCell = currentRow.getCell(index);
                        String districtCode = ExcelUtils.getCellValue(currentCell, String.class);
                        if (districtCode != null && districtCode.endsWith(".0")) {
                            districtCode = districtCode.substring(0, districtCode.length() - 2);
                        }
                        dto.setDistrictCode(districtCode);

                        // 13. Tên quận/huyện
                        index = 13;
                        currentCell = currentRow.getCell(index);
                        String districtName = ExcelUtils.getCellValue(currentCell, String.class);
                        dto.setDistrictName(districtName);

                        // 14. Mã phường/xã
                        index = 14;
                        currentCell = currentRow.getCell(index);
                        String communeCode = ExcelUtils.getCellValue(currentCell, String.class);
                        if (communeCode != null && communeCode.endsWith(".0")) {
                            communeCode = communeCode.substring(0, communeCode.length() - 2);
                        }
                        dto.setCommuneCode(communeCode);

                        // 15. Tên phường/xã
                        index = 15;
                        currentCell = currentRow.getCell(index);
                        String communeName = ExcelUtils.getCellValue(currentCell, String.class);
                        dto.setCommuneName(communeName);

                        // 16. Địa chỉ chi tiết
                        index = 16;
                        currentCell = currentRow.getCell(index);
                        String addressDetail = ExcelUtils.getCellValue(currentCell, String.class);
                        dto.setAddressDetail(addressDetail);


                        // Đọc dữ liệu từ Excel không có lỗi
                        if (errorMessages.isEmpty()) {
                            importSuccessRows.add(dto);
                        }

                        // Có lỗi khi đọc dữ liệu Excel
                        else {
                            String errorMessage = String.join(", ", errorMessages);
                            dto.setErrorMessage(errorMessage);

                            importErrorRows.add(dto);
                        }
                    }
                    rowIndex++;
                }


            } catch (Exception e) {
                System.err.println("Error import excel: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Error import excel: " + e.getMessage());
        }

        importResult.setSuccessImportRows(importSuccessRows);
        importResult.setErrorImportRows(importErrorRows);

        return importResult;

    }


    @Override
    public HrOrganizationImportResult saveHrOrganizationImportFromExcel(HrOrganizationImportResult importResults) {
        if (importResults.getSuccessImportRows() == null) {
            importResults.setSuccessImportRows(new ArrayList<>());
        }
        if (importResults.getErrorImportRows() == null) {
            importResults.setErrorImportRows(new ArrayList<>());
        }

        for (HrOrganizationImport importItem : importResults.getSuccessImportRows()) {
            HrOrganizationImport responseItem = this.saveOneFromImportData(importItem);

            if (responseItem != null) {

//                importResults.getSuccessImportRows().add(importItem);
            } else {
                importResults.getErrorImportRows().add(importItem);
            }
        }

        return importResults;
    }

    private HrOrganizationImport saveOneFromImportData(HrOrganizationImport importData) {
        if (importData == null) return null;

        HrOrganization entity = null;

        List<HrOrganization> availableOrgs = repos.findByCode(importData.getCode().trim());
        if (availableOrgs != null && !availableOrgs.isEmpty()) {
            entity = availableOrgs.get(0);
        } else {
            entity = new HrOrganization();
        }

        Staff representative = null;
        List<Staff> availableStaffs = staffRepository.findByCode(importData.getRepresentativeCode());
        if (availableStaffs != null && !availableStaffs.isEmpty()) {
            representative = availableStaffs.get(0);
        }
        entity.setRepresentative(representative);

        HrOrganization parentOrg = null;
        List<HrOrganization> availableParents = repos.findByCode(importData.getParentOrgCode());
        if (availableParents != null && !availableParents.isEmpty()) {
            parentOrg = availableParents.get(0);
        }
        entity.setParent(parentOrg);

        entity.setCode(importData.getCode());
        entity.setName(importData.getName());
        entity.setFoundedDate(importData.getFoundDate());
        entity.setTaxCode(importData.getTaxCode());
        entity.setWebsite(importData.getWebsite());
        entity.setAddressDetail(importData.getAddressDetail());
        entity.setOrganizationType(importData.getOrganizationType());
        AdministrativeUnit commune = null;
        if (importData.getCommuneCode() != null) {
            commune = administrativeUnitRepository.findByCode(importData.getCommuneCode());
            if (commune != null) {
                entity.setAdministrativeUnit(commune);
            }
        }
        AdministrativeUnit district = null;
        if (commune == null && importData.getDistrictCode() != null) {
            district = administrativeUnitRepository.findByCode(importData.getDistrictCode());
            if (district != null) {
                entity.setAdministrativeUnit(district);
            }
        }

        AdministrativeUnit province = null;
        if (district == null && importData.getProvinceCode() != null) {
            province = administrativeUnitRepository.findByCode(importData.getDistrictCode());
            if (province != null) {
                entity.setAdministrativeUnit(province);
            }
        }


        repos.saveAndFlush(entity);

        return importData;
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
    public Workbook exportExcelHrOrganization(SearchDto dto) {
        if (dto == null) {
            return null;
        }

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("Excel/MAU_DON_VI.xlsx")) {
            if (fileInputStream == null) {
                throw new IOException("File '" + "Excel/MAU_DON_VI.xlsx" + "' không tìm thấy trong classpath");
            }

            Workbook workbook = new XSSFWorkbook(fileInputStream);

            Sheet staffSheet = workbook.getSheetAt(0);
            CellStyle dataCellStyle = createDataCellStyle(workbook);

            int pageIndex = 1;
            int rowIndex = 1;
            boolean hasNextPage = true;
            long startTime = System.nanoTime();

            while (hasNextPage) {
                // searchStaffDto = new SearchStaffDto();
                dto.setPageIndex(pageIndex);
                dto.setPageSize(100);

                Page<HrOrganizationDto> pageOrganization = this.pagingHrOrganizations(dto);
                if (pageOrganization == null || pageOrganization.isEmpty()) {
                    break;
                }

                for (HrOrganizationDto hrOrganizationDto : pageOrganization) {
                    if (hrOrganizationDto == null) continue;

                    Row dataRow = staffSheet.createRow(rowIndex);
                    int cellIndex = 0;

                    // 0. Mã đơn vị
                    this.createCell(dataRow, cellIndex++, hrOrganizationDto.getCode(), dataCellStyle);
                    // 1. Tên đơn vị
                    this.createCell(dataRow, cellIndex++, hrOrganizationDto.getName(), dataCellStyle);
                    // 2. Loại đơn vị
                    Integer organizationType = null;
                    if (hrOrganizationDto.getOrganizationType() != null) {
                        if (hrOrganizationDto.getOrganizationType().equals(HrConstants.OrganizationType.LEGAL_ENTITY.getValue())) {
                            organizationType = HrConstants.OrganizationType.LEGAL_ENTITY.getValue();
                        }
                        if (hrOrganizationDto.getOrganizationType().equals(HrConstants.OrganizationType.OPERATION.getValue())) {
                            organizationType = HrConstants.OrganizationType.OPERATION.getValue();
                        }
                    }

                    this.createCell(dataRow, cellIndex++, organizationType, dataCellStyle);
                    // 3. Website
                    this.createCell(dataRow, cellIndex++, hrOrganizationDto.getWebsite(), dataCellStyle);
                    // 4. Mã đơn vị cha
                    String parentCode = null;
                    String parentName = null;
                    if (hrOrganizationDto.getParent() != null) {
                        parentCode = hrOrganizationDto.getParent().getCode();
                        parentName = hrOrganizationDto.getParent().getName();
                    }
                    this.createCell(dataRow, cellIndex++, parentCode, dataCellStyle);
                    // 5. Đơn vị trực thuộc
                    this.createCell(dataRow, cellIndex++, parentName, dataCellStyle);
                    //  6. Mã số thuế
                    this.createCell(dataRow, cellIndex++, hrOrganizationDto.getTaxCode(), dataCellStyle);
                    // 7. Ngày thành lập
                    this.createCell(dataRow, cellIndex++, formatDate(hrOrganizationDto.getFoundedDate()), dataCellStyle);
                    // 8. Mã nhân viên đại diện
                    String presentCode = null;
                    String presentName = null;

                    String provinceCode = null;
                    String provinceName = null;
                    String districtCode = null;
                    String districtName = null;
                    String communeCode = null;
                    String communeName = null;

                    if (hrOrganizationDto.getRepresentative() != null) {
                        presentName = hrOrganizationDto.getRepresentative().getDisplayName();
                        presentCode = hrOrganizationDto.getRepresentative().getStaffCode();
                    }

                    if (hrOrganizationDto.getProvince() != null) {
                        provinceCode = hrOrganizationDto.getProvince().getCode();
                        provinceName = hrOrganizationDto.getProvince().getName();
                    }
                    if (hrOrganizationDto.getDistrict() != null) {
                        districtCode = hrOrganizationDto.getDistrict().getCode();
                        districtName = hrOrganizationDto.getDistrict().getName();
                    }

                    if (hrOrganizationDto.getAdministrativeUnit() != null) {
                        communeCode = hrOrganizationDto.getAdministrativeUnit().getCode();
                        communeName = hrOrganizationDto.getAdministrativeUnit().getName();
                    }

                    this.createCell(dataRow, cellIndex++, presentCode, dataCellStyle);
                    // 9. Nhân viên đại diện
                    this.createCell(dataRow, cellIndex++, presentName, dataCellStyle);
                    // 10. Mã tỉnh/thành phố
                    this.createCell(dataRow, cellIndex++, provinceCode, dataCellStyle);
                    // 11. Tên tỉnh/thành phố
                    this.createCell(dataRow, cellIndex++, provinceName, dataCellStyle);
                    // 12. Mã quận/huyện
                    this.createCell(dataRow, cellIndex++, districtCode, dataCellStyle);
                    // 13. Tên quận/huyện
                    this.createCell(dataRow, cellIndex++, districtName, dataCellStyle);
                    // 14. Mã phường/xã
                    this.createCell(dataRow, cellIndex++, communeCode, dataCellStyle);
                    // 15. Tên phường/xã
                    this.createCell(dataRow, cellIndex++, communeName, dataCellStyle);
                    // 16. Địa chỉ chi tiết
                    this.createCell(dataRow, cellIndex++, hrOrganizationDto.getAddressDetail(), dataCellStyle);

                    rowIndex++;
                }

                hasNextPage = pageOrganization.hasNext(); // Kiểm tra xem còn trang tiếp theo không
                pageIndex++; // Tăng pageIndex để lấy trang tiếp theo
            }
            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;

            logger.info("Xuất tất cả đơn vị - Xử lý mất {} ms ", elapsedTimeMs);
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
            String maxCode = repos.findMaxCodeByPrefix(systemConfig.getConfigValue(), systemConfig.getNumberOfZero());
            return systemConfigService.generateNextCode(systemConfig.getConfigValue(), systemConfig.getNumberOfZero(), maxCode);
        }
    }
}
