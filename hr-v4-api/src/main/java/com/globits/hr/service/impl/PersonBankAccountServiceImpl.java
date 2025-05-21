package com.globits.hr.service.impl;

import com.globits.core.domain.Person;
import com.globits.core.domain.Profession;
import com.globits.core.repository.PersonRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.function.ImportExcelMessageDto;
import com.globits.hr.dto.function.ImportStaffDto;
import com.globits.hr.dto.importExcel.StaffBankAccountImport;
import com.globits.hr.dto.importExcel.StaffFamilyRelationshipImport;
import com.globits.hr.dto.importExcel.StaffLAImportResult;
import com.globits.hr.dto.search.PersonBankAccountSearchDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.repository.BankRepository;
import com.globits.hr.repository.HrIntroduceCostRepository;
import com.globits.hr.repository.PersonBankAccountRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.HrIntroduceCostService;
import com.globits.hr.service.PersonBankAccountService;
import com.globits.hr.utils.ExcelUtils;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PersonBankAccountServiceImpl extends GenericServiceImpl<PersonBankAccount, UUID> implements PersonBankAccountService {
    private static final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);

    @Autowired
    private PersonBankAccountRepository personBankAccountRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private StaffRepository staffRepository;


    @Override
    public Page<PersonBankAccountDto> searchByPage(PersonBankAccountSearchDto dto) {
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

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.bank.name ";

        String sqlCount = "select count( entity.id) from PersonBankAccount as entity ";
        String sql = "select  new com.globits.hr.dto.PersonBankAccountDto(entity) from PersonBankAccount as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.person.displayName LIKE :text  or entity.bankAccountName like :text or entity.bankAccountNumber like :text) ";
        }

        if (dto.getStaffId() != null) {
            whereClause += " and (entity.person.id = :staffId) ";
        }

        if (dto.getBankId() != null) {
            whereClause += " and (entity.bank.id = :bankId) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, PersonBankAccountDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        if (dto.getStaffId() != null) {
            query.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }

        long count = (long) qCount.getSingleResult();

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<PersonBankAccountDto> entities = query.getResultList();
        Page<PersonBankAccountDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public PersonBankAccountDto getById(UUID id) {
        if (id == null) return null;
        PersonBankAccount entity = personBankAccountRepository.findById(id).orElse(null);

        if (entity == null) return null;
        PersonBankAccountDto response = new PersonBankAccountDto(entity, true);

        return response;
    }

    @Override
    public PersonBankAccountDto saveOrUpdate(PersonBankAccountDto dto) {
        if (dto == null) {
            return null;
        }

        PersonBankAccount entity = new PersonBankAccount();
        if (dto.getId() != null) entity = personBankAccountRepository.findById(dto.getId()).orElse(null);
        if (entity == null) entity = new PersonBankAccount();

        entity.setIsMain(dto.getIsMain());
        if (dto.getPersonId() != null) {
            Staff person = staffRepository.findById(dto.getPersonId()).orElse(null);
            if (person == null) return null;
            entity.setPerson(person);

            if (person.getPersonBankAccounts() == null || person.getPersonBankAccounts().isEmpty()) {
                entity.setIsMain(true);
            }
        }

        if (dto.getBank() != null) {
            Bank bank = bankRepository.findById(dto.getBank().getId()).orElse(null);
            if (bank == null) return null;
            entity.setBank(bank);
        }

        entity.setBankAccountName(dto.getBankAccountName());
        entity.setBankAccountNumber(dto.getBankAccountNumber());
        entity.setBankBranch(dto.getBankBranch());


        entity = personBankAccountRepository.saveAndFlush(entity);

        this.handleOnlyOneMainPersonAccount(entity);

        return new PersonBankAccountDto(entity);
    }

    private void handleOnlyOneMainPersonAccount(PersonBankAccount entity) {
        // Chỉ có 1 tài khoản ngân hàng main của người này
        if (entity.getIsMain() != null && entity.getIsMain().equals(true)) {
            List<PersonBankAccount> bankAccounts = personBankAccountRepository.findByPersonId(entity.getPerson().getId());

            if (bankAccounts != null && !bankAccounts.isEmpty()) {
                for (PersonBankAccount bankAccount : bankAccounts) {
                    if (bankAccount.getId().equals(entity.getId())) continue;

                    bankAccount.setIsMain(false);
                }

                bankAccounts = personBankAccountRepository.saveAllAndFlush(bankAccounts);
            }
        }
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id == null) return false;

        PersonBankAccount entity = personBankAccountRepository.findById(id).orElse(null);
        if (entity == null) return false;

        personBankAccountRepository.delete(entity);
        return true;
    }

    @Override
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID itemId : ids) {
            boolean deleteRes = this.deleteById(itemId);
            if (!deleteRes) isValid = false;
        }
        return isValid;
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
    public int exportExcelStaffBankAccount(List<StaffBankAccountImport> exportData, Sheet sheet, int rowIndex) {
        if (exportData == null) {
            return rowIndex;
        }

        CellStyle dataCellStyle = createDataCellStyle(sheet.getWorkbook());


        for (StaffBankAccountImport exportRow : exportData) {
            if (exportRow == null) continue;

            Row dataRow = sheet.createRow(rowIndex);
            int cellIndex = 0;
            createCell(dataRow, cellIndex++, rowIndex, dataCellStyle);

            // 1. Mã nhân viên
            this.createCell(dataRow, cellIndex++, exportRow.getStaffCode(), dataCellStyle);
            // 2. Tên nhân viên
            this.createCell(dataRow, cellIndex++, exportRow.getStaffDisplayName(), dataCellStyle);
            // 3. Số tài khoản ngân hàng
            this.createCell(dataRow, cellIndex++, exportRow.getBankAccountNumber(), dataCellStyle);
            // 4. Mã Ngân hàng
            this.createCell(dataRow, cellIndex++, exportRow.getBankCode(), dataCellStyle);
            // 5. Tên Ngân hàng
            this.createCell(dataRow, cellIndex++, exportRow.getBankName(), dataCellStyle);
            // 6. Chi nhánh ngân hàng
            this.createCell(dataRow, cellIndex++, exportRow.getBankBranch(), dataCellStyle);
            // 7. Là tài khoản chính
            this.createCell(dataRow, cellIndex++, exportRow.getMain(), dataCellStyle);

            rowIndex++;
        }


        return rowIndex;
    }

    @Override
    public List<StaffBankAccountImport> getIEStaffBankAccounts(Staff staff) {
        if (staff == null || staff.getPersonBankAccounts() == null || staff.getPersonBankAccounts().isEmpty())
            return new ArrayList<>();

        List<StaffBankAccountImport> ieData = new ArrayList<>();

        for (PersonBankAccount personBankAccount : staff.getPersonBankAccounts()) {
            StaffBankAccountImport item = new StaffBankAccountImport(personBankAccount, staff);

            ieData.add(item);
        }

        ieData.sort(Comparator
                .comparing(StaffBankAccountImport::getBankName, Comparator.nullsLast(String::compareTo))
                .thenComparing(StaffBankAccountImport::getBankAccountNumber, Comparator.nullsLast(String::compareTo)));

        return ieData;
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

    @Override
    public List<StaffBankAccountImport> importExcelStaffBankAccount(Sheet datatypeSheet) {
        List<StaffBankAccountImport> response = new ArrayList<>();

        try {
            int rowIndex = 1;
            int num = datatypeSheet.getLastRowNum();

            while (rowIndex <= num) {
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell = null;

                if (currentRow != null) {
                    List<String> errorMessages = new ArrayList<>();

                    StaffBankAccountImport dto = new StaffBankAccountImport();

                    // 0. STT
                    currentCell = currentRow.getCell(0);
                    String rowOrderStr = ExcelUtils.parseStringCellValue(currentCell);
                    Integer rowOrder = ExcelUtils.convertToInteger(rowOrderStr);
                    dto.setStt(rowOrder);

                    // 1. Mã nhân viên
                    int index = 1;
                    currentCell = currentRow.getCell(index);
                    String staffCode = this.getCellValueAsString(currentCell);
                    if (!StringUtils.hasText(staffCode)) {
                        errorMessages.add("Không xác định được nhân viên");
                    }
                    dto.setStaffCode(staffCode);

                    if(dto.getStt() == null && !StringUtils.hasText(dto.getStaffCode())){
                        rowIndex++;
                        continue;
                    }

                    // 2. Họ và tên
                    index++;
                    currentCell = currentRow.getCell(index);
                    String fullName = this.getCellValueAsString(currentCell);

                    dto.setStaffDisplayName(fullName);

                    // 3. Số tài khoản ngân hàng
                    index++;
                    currentCell = currentRow.getCell(index);
                    String bankAccountNumber = this.getCellValueAsString(currentCell);
                    if (!StringUtils.hasText(bankAccountNumber)) {
                        errorMessages.add("Chưa có tài khoản ngân hàng");
                    }
                    dto.setBankAccountNumber(bankAccountNumber);

                    // 4. Mã ngân hàng
                    index++;
                    currentCell = currentRow.getCell(index);
                    String bankCode = this.getCellValueAsString(currentCell);
                    if (!StringUtils.hasText(bankCode)) {
                        errorMessages.add("Không xác định được ngân hàng nào");
                    }
                    dto.setBankCode(bankCode);

                    // 5. Ngân hàng
                    index++;
                    currentCell = currentRow.getCell(index);
                    String bankName = this.getCellValueAsString(currentCell);
                    dto.setBankName(bankName);

                    // 6. Chi nhánh ngân hàng
                    index++;
                    currentCell = currentRow.getCell(index);
                    String bankBranch = this.getCellValueAsString(currentCell);
                    dto.setBankBranch(bankBranch);

                    // 7. Là tài khoản chính
                    index++;
                    currentCell = currentRow.getCell(index);
                    String isMain = this.getCellValueAsString(currentCell);
                    if (StringUtils.hasText(isMain) && isMain.equalsIgnoreCase("true")) {
                        dto.setMain(true);
                    } else {
                        dto.setMain(false);
                    }

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


    @Override
    public List<PersonBankAccountDto> saveStaffBankAccountImportFromExcel(List<StaffBankAccountImport> importData) {
        List<PersonBankAccountDto> response = new ArrayList<>();

        if (importData == null || importData.isEmpty()) return response;

        for (StaffBankAccountImport importItem : importData) {
            PersonBankAccountDto responseItem = this.saveOneFromImportData(importItem);

            if (responseItem != null) {
                response.add(responseItem);
            }
        }

        return response;
    }

    private PersonBankAccountDto saveOneFromImportData(StaffBankAccountImport importData) {
        List<String> errorMessages = new ArrayList<>();

        Staff staff = null;
        List<Staff> availableStaffs = staffRepository.findByCode(importData.getStaffCode());
        if (availableStaffs == null || availableStaffs.isEmpty()) {
            errorMessages.add("Không tìm thấy nhân viên hợp lệ");
        } else {
            staff = availableStaffs.get(0);
        }


        Bank bank = null;
        List<Bank> availableBanks = bankRepository.findByCode(importData.getBankCode());
        if (availableBanks == null || availableBanks.isEmpty()) {
            if (importData.getBankCode() != null && importData.getBankName() != null) {
                bank = new Bank();
                bank.setCode(importData.getBankCode());
                bank.setName(importData.getBankName());

                bank = bankRepository.saveAndFlush(bank);
            } else {
                errorMessages.add("Không đủ dữ liệu để tạo Ngân hàng");
            }
        } else {
            bank = availableBanks.get(0);
        }

        // Đọc dữ liệu từ Excel không có lỗi
        if (!errorMessages.isEmpty()) {
            String errorMessage = String.join(". ", errorMessages);
            importData.setErrorMessage(errorMessage);

            return null;
        }

        List<PersonBankAccount> availableResults = personBankAccountRepository.findByPersonIdAndBankId(staff.getId(), bank.getId());

        PersonBankAccount entity = null;
        if (availableResults != null && !availableResults.isEmpty()) {
            entity = availableResults.get(0);
        }
        if (entity == null) {
            entity = new PersonBankAccount();
        }

        entity.setPerson(staff);
        entity.setBank(bank);
        entity.setBankAccountNumber(importData.getBankAccountNumber());
        entity.setBankBranch(importData.getBankBranch());
        if (staff.getPersonBankAccounts() == null || staff.getPersonBankAccounts().isEmpty()) {
            entity.setIsMain(true);
        } else {
            boolean hasMain = false;

            for (PersonBankAccount personBankAccount : staff.getPersonBankAccounts()) {
                if (personBankAccount.getIsMain() != null && personBankAccount.getIsMain().equals(true)) {
                    hasMain = true;
                    break;
                }
            }

            if (!hasMain) {
                entity.setIsMain(true);
            }
        }


        entity = personBankAccountRepository.saveAndFlush(entity);

        return new PersonBankAccountDto(entity);
    }

}
