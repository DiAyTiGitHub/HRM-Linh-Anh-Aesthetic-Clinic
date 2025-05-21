package com.globits.hr.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import com.globits.core.domain.Profession;
import com.globits.core.dto.ProfessionDto;
import com.globits.core.repository.ProfessionRepository;
import com.globits.hr.domain.FamilyRelationship;
import com.globits.hr.domain.PersonBankAccount;
import com.globits.hr.dto.PersonBankAccountDto;
import com.globits.hr.dto.importExcel.StaffBankAccountImport;
import com.globits.hr.dto.importExcel.StaffFamilyRelationshipImport;
import com.globits.hr.dto.importExcel.StaffLAImportResult;
import com.globits.hr.repository.FamilyRelationshipRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffFamilyRelationship;
import com.globits.hr.dto.StaffFamilyRelationshipDto;
import com.globits.hr.dto.function.StaffFamilyRelationshipFunctionDto;
import com.globits.hr.repository.StaffFamilyRelationshipRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.StaffFamilyRelationshipService;
import com.globits.security.domain.User;
import org.springframework.util.StringUtils;

import static org.json.XMLTokener.entity;

@Transactional
@Service
public class StaffFamilyRelationshipServiceImpl extends GenericServiceImpl<StaffFamilyRelationship, UUID>
        implements StaffFamilyRelationshipService {
    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffFamilyRelationshipRepository staffFamilyRelationshipRepository;

    @Autowired
    private FamilyRelationshipRepository familyRelationshipRepository;

    @Autowired
    private ProfessionRepository professionRepository;

    @Override
    public Page<StaffFamilyRelationshipDto> getPages(int pageIndex, int pageSize) {
        if (pageIndex > 1) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return this.staffFamilyRelationshipRepository.getPages(pageable);
    }

    @Override
    public List<StaffFamilyRelationshipDto> getAll(UUID id) {
        return this.staffFamilyRelationshipRepository.getAll(id);
    }

    @Override
    public StaffFamilyRelationshipDto getFamilyById(UUID id) {
        return this.staffFamilyRelationshipRepository.getFamilyById(id);
    }

    @Override
    public StaffFamilyRelationshipDto saveFamily(StaffFamilyRelationshipDto familyDto, UUID id) {
        if (familyDto == null) {
            return null;
        }
        Staff staff = null;
        if (familyDto.getStaff() != null && familyDto.getStaff().getId() != null) {
            staff = this.staffRepository.getOne(familyDto.getStaff().getId());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User modifiedUser;
        LocalDateTime currentDate = LocalDateTime.now();
        String currentUserName = "Unknown User";
        if (authentication != null) {
            try {
                modifiedUser = (User) authentication.getPrincipal();
                currentUserName = modifiedUser.getUsername();
            } catch (Exception e) {
            }
        }
        StaffFamilyRelationship entity = null;
        if (id != null) {
            entity = this.findById(id);
        }
        if (entity == null && familyDto.getId() != null) {
            entity = this.findById(familyDto.getId());
        }

        if (entity == null) {// trường hợp thêm mới
            entity = new StaffFamilyRelationship();
            entity.setCreateDate(currentDate);
            entity.setCreatedBy(currentUserName);
        }
        entity.setModifyDate(currentDate);
        entity.setModifiedBy(currentUserName);

        if (familyDto.getFullName() != null) {
            entity.setFullName(familyDto.getFullName());
        }
        if (familyDto.getAddress() != null) {
            entity.setAddress(familyDto.getAddress());
        }
        if (familyDto.getBirthDate() != null) {
            entity.setBirthDate(familyDto.getBirthDate());
        }
        if (familyDto.getDescription() != null) {
            entity.setDescription(familyDto.getDescription());
        }
        if (familyDto.getWorkingPlace() != null) {
            entity.setWorkingPlace(familyDto.getWorkingPlace());
        }
        FamilyRelationship familyRelationship = null;
        if (familyDto.getFamilyRelationship() != null && familyDto.getFamilyRelationship().getId() != null) {
            familyRelationship = this.familyRelationshipRepository.getOne(familyDto.getFamilyRelationship().getId());
        }
        entity.setFamilyRelationship(familyRelationship);

        Profession profession = null;
        if (familyDto.getProfession() != null && familyDto.getProfession().getId() != null) {
            profession = professionRepository.findById(familyDto.getProfession().getId()).orElse(null);
        }
        entity.setProfession(profession);

        entity.setTaxCode(familyDto.getTaxCode());
        entity.setStaff(staff);
        entity.setIsDependent(familyDto.getIsDependent());
        entity.setDependentDeductionFromDate(familyDto.getDependentDeductionFromDate());
        entity.setDependentDeductionToDate(familyDto.getDependentDeductionToDate());

        entity = this.staffFamilyRelationshipRepository.save(entity);
        familyDto.setId(entity.getId());
        return new StaffFamilyRelationshipDto(entity);
    }

    @Override
    public StaffFamilyRelationshipDto removeFamily(UUID id) {
        StaffFamilyRelationship familyRelationship = this.findById(id);
        if (staffFamilyRelationshipRepository != null && familyRelationship != null) {
            this.staffFamilyRelationshipRepository.deleteById(id);
        }
        return new StaffFamilyRelationshipDto(familyRelationship);
    }

    @Override
    public Boolean removeLists(List<UUID> ids) {
        if (ids != null && ids.size() > 0) {
            for (UUID id : ids) {
                this.staffFamilyRelationshipRepository.deleteById(id);
            }
        }
        return false;
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


    public int exportExcelStaffFamilyRelationship(List<StaffFamilyRelationshipImport> exportData, Sheet sheet, int rowIndex) {
        if (exportData == null) {
            return rowIndex;
        }

        CellStyle dataCellStyle = createDataCellStyle(sheet.getWorkbook());


        for (StaffFamilyRelationshipImport exportRow : exportData) {
            if (exportRow == null) continue;

            Row dataRow = sheet.createRow(rowIndex);
            int cellIndex = 0;

            // STT
            this.createCell(dataRow, cellIndex++, rowIndex, dataCellStyle);

            // 1. Mã nhân viên
            this.createCell(dataRow, cellIndex++, exportRow.getStaffCode(), dataCellStyle);
            // 2. Họ tên nhân viên
            this.createCell(dataRow, cellIndex++, exportRow.getStaffDisplayName(), dataCellStyle);
            // 3. Họ tên người có quan hệ
            this.createCell(dataRow, cellIndex++, exportRow.getRelationPersonName(), dataCellStyle);
            // 4. Ngày tháng năm sinh
            String recruitmentDate = (exportRow.getRelationPersonBirthDate() != null)
                    ? formatDate(exportRow.getRelationPersonBirthDate()) : "";
            this.createCell(dataRow, cellIndex++, recruitmentDate, dataCellStyle);
            // 5. Mã Nghề nghiệp
            this.createCell(dataRow, cellIndex++, exportRow.getProfessionCode(), dataCellStyle);
            // 6. Nghề nghiệp
            this.createCell(dataRow, cellIndex++, exportRow.getProfessionName(), dataCellStyle);
            // 7. Mã quan hệ
            this.createCell(dataRow, cellIndex++, exportRow.getRelationshipCode(), dataCellStyle);
            // 8. Quan hệ
            this.createCell(dataRow, cellIndex++, exportRow.getRelationshipName(), dataCellStyle);
            // 9. Là người phụ thuộc
            String isDependent = null;
            if (exportRow.getIsDependent() != null && exportRow.getIsDependent()) {
                isDependent = "Có";
            }
            this.createCell(dataRow, cellIndex++, isDependent, dataCellStyle);

            // 10. Nơi ở hiện tại
            this.createCell(dataRow, cellIndex++, exportRow.getAddress(), dataCellStyle);
            // 11. Tên và địa chỉ cơ quan nơi làm việc
            this.createCell(dataRow, cellIndex++, exportRow.getWorkingPlace(), dataCellStyle);
            // 12. Mã số thuế
            this.createCell(dataRow, cellIndex++, exportRow.getTaxCode(), dataCellStyle);

            // 13. Ngày bắt đầu phụ thuộc
            String dependentDeductionFromDate = "";
            if (exportRow.getIsDependent() != null && exportRow.getIsDependent()) {
                dependentDeductionFromDate = (exportRow.getDependentDeductionFromDate() != null)
                        ? formatDate(exportRow.getDependentDeductionFromDate()) : "";
            }

            this.createCell(dataRow, cellIndex++, dependentDeductionFromDate, dataCellStyle);
            // 14. Ngày kết thúc phụ thuộc
            String dependentDeductionToDate = "";
            if (exportRow.getIsDependent() != null && exportRow.getIsDependent()) {
                dependentDeductionToDate = (exportRow.getDependentDeductionToDate() != null)
                        ? formatDate(exportRow.getDependentDeductionToDate()) : "";
            }
            this.createCell(dataRow, cellIndex++, dependentDeductionToDate, dataCellStyle);

            rowIndex++;
        }


        return rowIndex;
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
    public List<StaffFamilyRelationshipDto> saveStaffFamilyRelationshipImportFromExcel(List<StaffFamilyRelationshipImport> importData) {
        List<StaffFamilyRelationshipDto> response = new ArrayList<>();

        if (importData == null || importData.isEmpty()) return response;

        for (StaffFamilyRelationshipImport importItem : importData) {
            StaffFamilyRelationshipDto responseItem = this.saveOneFromImportData(importItem);

            if (responseItem != null) {
                response.add(responseItem);
            }
        }

        return response;
    }

    private StaffFamilyRelationshipDto saveOneFromImportData(StaffFamilyRelationshipImport importData) {
        List<String> errorMessages = new ArrayList<>();

        Staff staff = null;
        List<Staff> availableStaffs = staffRepository.findByCode(importData.getStaffCode());
        if (availableStaffs == null || availableStaffs.isEmpty()) {
            errorMessages.add("Không tìm thấy nhân viên hợp lệ");
        } else {
            staff = availableStaffs.get(0);
        }

        FamilyRelationship familyRelationship = null;
        List<FamilyRelationship> availableFamilyRelationships = familyRelationshipRepository.findByCode(importData.getRelationshipCode());
        if (availableFamilyRelationships == null || availableFamilyRelationships.isEmpty()) {
            if (importData.getRelationshipCode() != null && importData.getRelationshipName() != null) {
                familyRelationship = new FamilyRelationship();
                familyRelationship.setCode(importData.getRelationshipCode());
                familyRelationship.setName(importData.getRelationshipName());

                familyRelationship = familyRelationshipRepository.saveAndFlush(familyRelationship);
            } else {
                errorMessages.add("Không đủ dữ liệu để tạo Quan hệ thân nhân");
            }
        } else {
            familyRelationship = availableFamilyRelationships.get(0);
        }

        Profession profession = null;
        List<Profession> availableProfessions = professionRepository.findListByCode(importData.getProfessionCode());
        if (availableProfessions != null && !availableProfessions.isEmpty()) {
            profession = availableProfessions.get(0);
        } else if (importData.getProfessionCode() != null && importData.getProfessionName() != null) {
            profession = new Profession();

            profession.setCode(importData.getProfessionCode());
            profession.setName(importData.getProfessionName());

            profession = professionRepository.saveAndFlush(profession);
        } else {
            errorMessages.add("Không đủ dữ liệu để tạo nghề nghiệp");
        }

        // Đọc dữ liệu từ Excel không có lỗi
        if (!errorMessages.isEmpty()) {
            String errorMessage = String.join(". ", errorMessages);
            importData.setErrorMessage(errorMessage);

            return null;
        }

        List<StaffFamilyRelationship> availableResults = staffFamilyRelationshipRepository.findByStaffIdAndRelationshipId(staff.getId(), familyRelationship.getId());

        StaffFamilyRelationship entity = null;
        if (availableResults != null && !availableResults.isEmpty()) {
            entity = availableResults.get(0);
        }

        if (entity == null) {
            entity = new StaffFamilyRelationship();
        }

        entity.setStaff(staff);
        entity.setFamilyRelationship(familyRelationship);

        entity.setProfession(profession);

        entity.setBirthDate(importData.getRelationPersonBirthDate());
        entity.setFullName(importData.getRelationPersonName());
        entity.setIsDependent(importData.getIsDependent());
        entity.setAddress(importData.getAddress());
        entity.setWorkingPlace(importData.getWorkingPlace());
        entity.setTaxCode(importData.getTaxCode());
        if (importData.getIsDependent() != null && importData.getIsDependent()) {
            entity.setDependentDeductionFromDate(importData.getDependentDeductionFromDate());
            entity.setDependentDeductionToDate(importData.getDependentDeductionToDate());
        } else {
            entity.setDependentDeductionFromDate(null);
            entity.setDependentDeductionToDate(null);
        }

        entity = staffFamilyRelationshipRepository.saveAndFlush(entity);

        return new StaffFamilyRelationshipDto(entity);
    }

    @Override
    public List<StaffFamilyRelationshipImport> importExcelStaffFamilyRelationship(Sheet datatypeSheet) {
        List<StaffFamilyRelationshipImport> response = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");

        try {
            int rowIndex = 1;
            int num = datatypeSheet.getLastRowNum();

            while (rowIndex <= num) {
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell = null;

                if (currentRow != null) {
                    List<String> errorMessages = new ArrayList<>();

                    StaffFamilyRelationshipImport dto = new StaffFamilyRelationshipImport();

                    // 0. STT
                    currentCell = currentRow.getCell(0);
                    Integer rowOrder = null;
                    if (currentCell != null) {
                        try {
                            if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                rowOrder = (int) currentCell.getNumericCellValue();
                            } else if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                rowOrder = Integer.valueOf(currentCell.getStringCellValue());
                            }
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }
                    dto.setStt(rowOrder);

                    // 1. Mã nhân viên
                    int index = 1;
                    currentCell = currentRow.getCell(index);
                    String staffCode = this.getCellValueAsString(currentCell);
                    if (!StringUtils.hasText(staffCode)) {
                        errorMessages.add("Không xác định được nhân viên");
                    }
                    dto.setStaffCode(staffCode);

                    // 2. Họ và tên
                    index++;
                    currentCell = currentRow.getCell(index);
                    String fullName = this.getCellValueAsString(currentCell);

                    dto.setStaffDisplayName(fullName);

                    // 3. Họ tên người có quan hệ
                    index++;
                    currentCell = currentRow.getCell(index);
                    String relationPersonName = this.getCellValueAsString(currentCell);
                    if (!StringUtils.hasText(relationPersonName)) {
                        errorMessages.add("Không xác định được Họ tên người có quan hệ");
                    }
                    dto.setRelationPersonName(relationPersonName);

                    // 4. Ngày tháng năm sinh
                    index++;
                    currentCell = currentRow.getCell(index);
                    Date relationPersonBirthDate = parseDateCellValue(currentCell, rowIndex, index, dateFormat);
                    dto.setRelationPersonBirthDate(relationPersonBirthDate);

                    // 5. Mã Nghề nghiệp
                    index++;
                    currentCell = currentRow.getCell(index);
                    String professionCode = this.getCellValueAsString(currentCell);
                    if (!StringUtils.hasText(professionCode)) {
                        errorMessages.add("Không xác định được Nghề nghiệp thân nhân");
                    }
                    dto.setProfessionCode(professionCode);


                    // 6. Nghề nghiệp
                    index++;
                    currentCell = currentRow.getCell(index);
                    String professionName = this.getCellValueAsString(currentCell);

                    dto.setProfessionName(professionName);

                    // 7. Mã quan hệ
                    index++;
                    currentCell = currentRow.getCell(index);
                    String relationshipCode = this.getCellValueAsString(currentCell);
                    if (!StringUtils.hasText(relationshipCode)) {
                        errorMessages.add("Không xác định được Quan hệ thân nhân");
                    }
                    dto.setRelationshipCode(relationshipCode);

                    // 8. Quan hệ
                    index++;
                    currentCell = currentRow.getCell(index);
                    String relationshipName = this.getCellValueAsString(currentCell);
                    dto.setRelationshipName(relationshipName);

                    // 9. Là người phụ thuộc
                    index++;
                    currentCell = currentRow.getCell(index);
                    String dependantString = this.getCellValueAsString(currentCell);
                    dto.setIsDependent(isDependant(dependantString));

                    // 10. Nơi ở hiện tại
                    index++;
                    currentCell = currentRow.getCell(index);
                    String address = this.getCellValueAsString(currentCell);
                    dto.setAddress(address);

                    // 11. Tên và địa chỉ cơ quan nơi làm việc
                    index++;
                    currentCell = currentRow.getCell(index);
                    String workingPlace = this.getCellValueAsString(currentCell);
                    dto.setWorkingPlace(workingPlace);


                    // 12. Mã số thuế
                    index++;
                    currentCell = currentRow.getCell(index);
                    String taxCode = this.getCellValueAsString(currentCell);
                    dto.setTaxCode(taxCode);

                    // 13. Ngày bắt đầu phụ thuộc
                    index++;
                    currentCell = currentRow.getCell(index);
                    Date dependentDeductionFromDate = this.parseDateCellValue(currentCell, rowIndex, index, dateFormat);
                    dto.setDependentDeductionFromDate(dependentDeductionFromDate);

                    // 14. Ngày kết thúc phụ thuộc
                    index++;
                    currentCell = currentRow.getCell(index);
                    Date dependentDeductionToDate = this.parseDateCellValue(currentCell, rowIndex, index, dateFormat);
                    dto.setDependentDeductionToDate(dependentDeductionToDate);

                    // Đọc dữ liệu từ Excel không có lỗi
                    if (!errorMessages.isEmpty()) {
                        String errorMessage = String.join(", ", errorMessages);
                        dto.setErrorMessage(errorMessage);
                    }

                    response.add(dto);
                }

                rowIndex++;
            }


        } catch (Exception e) {
            System.err.println("Error import excel: " + e.getMessage());
        }

        return response;
    }

    private boolean isDependant(String message) {
        message = message.trim();

        switch (message) {
            case "Có":
            case "X":
            case "x":
            case "True":
            case "TRUE":
            case "true":
            case "Yes":
            case "YES":
            case "yes":
            case "có":
            case "co":
            case "CO":
                return true;
        }

        return false;
    }

    @Override
    public List<StaffFamilyRelationshipImport> getIEStaffFamilyRelationship(Staff staff) {
        if (staff == null || staff.getFamilyRelationships() == null || staff.getFamilyRelationships().isEmpty())
            return new ArrayList<>();

        List<StaffFamilyRelationshipImport> ieData = new ArrayList<>();

        for (StaffFamilyRelationship staffFamilyRelationship : staff.getFamilyRelationships()) {
            StaffFamilyRelationshipImport item = new StaffFamilyRelationshipImport(staffFamilyRelationship);

            ieData.add(item);
        }

        // Sorting by relationPersonName first, then relationPersonBirthDate (earlier dates first)
        ieData.sort(Comparator
                .comparing(StaffFamilyRelationshipImport::getRelationPersonName, Comparator.nullsLast(String::compareTo))
                .thenComparing(StaffFamilyRelationshipImport::getRelationPersonBirthDate, Comparator.nullsLast(Date::compareTo)));

        return ieData;
    }
}
