package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.*;
import com.globits.hr.dto.PersonBankAccountDto;
import com.globits.hr.dto.StaffWorkingLocationDto;
import com.globits.hr.dto.importExcel.StaffBankAccountImport;
import com.globits.hr.dto.importExcel.StaffWorkingLocationImport;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.StaffWorkingLocationRepository;
import com.globits.hr.repository.WorkplaceRepository;
import com.globits.hr.service.StaffWorkingLocationService;
import com.globits.hr.service.UserExtService;
import com.globits.hr.utils.ExcelUtils;
import com.globits.security.dto.UserDto;

import jakarta.persistence.Query;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Transactional
@Service
public class StaffWorkingLocationServiceImpl extends GenericServiceImpl<StaffWorkingLocation, UUID> implements StaffWorkingLocationService {

    @Autowired
    private StaffWorkingLocationRepository repository;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UserExtService userExtService;

    @Override
    public HashMap<UUID, StaffWorkingLocationDto> getMainWorkingLocationMap() {
        HashMap<UUID, StaffWorkingLocationDto> map = new HashMap<>();

        try {
            List<Object[]> results = repository.findAllMainWorkingLocationNative();

            for (Object[] row : results) {
                try {
                    UUID staffId = row[0] != null ? UUID.fromString(row[0].toString()) : null;
                    String workingLocation = (String) row[2];
//                    Boolean isMainLocation = (Boolean) row[2];

                    if (staffId == null) {
                        continue;
                    }

                    StaffWorkingLocationDto dto = new StaffWorkingLocationDto();
                    dto.setIsMainLocation(true);
                    dto.setWorkingLocation(workingLocation);
                    dto.setStaffId(staffId);

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
    public Page<StaffWorkingLocationDto> searchByPage(SearchDto dto) {
        if (dto == null) {
            return null;
        }
        UserDto userDto = userExtService.getCurrentUser();
        UUID currentStaffId = null;
        if (userDto.getPerson() != null) {
            currentStaffId = userDto.getPerson().getId();
        }
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.createDate desc ";

        String sqlCount = "select count(distinct entity.id) from StaffWorkingLocation as entity ";
        String sql = "select distinct new com.globits.hr.dto.StaffWorkingLocationDto(entity) from StaffWorkingLocation as entity ";

        String leftJoin = " LEFT JOIN entity.staff staff ";
        leftJoin += " LEFT JOIN entity.workplace workplace ";

        sqlCount += leftJoin;
        sql += leftJoin;

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( workplace.name LIKE :text " + " OR workplace.code LIKE :text " + " OR staff.codeStaff LIKE :text " + " OR staff.displayName LIKE :text ) ";
        }
        if (dto.getStaffId() != null) {
            whereClause += " AND ( staff.id = :staffId ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, StaffWorkingLocationDto.class);
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

        int start = pageIndex * pageSize;
        query.setFirstResult(start);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<StaffWorkingLocationDto> entities = query.getResultList();
        Page<StaffWorkingLocationDto> result = new PageImpl<>(entities, pageable, count);

        return result;


    }

    @Override
    public StaffWorkingLocationDto getById(UUID id) {
        if (id == null) return null;
        StaffWorkingLocation entity = repository.findById(id).orElse(null);
        StaffWorkingLocationDto result = new StaffWorkingLocationDto(entity);
        return result;
    }

    @Override
    public StaffWorkingLocationDto saveOrUpdate(StaffWorkingLocationDto dto) {
        if (dto == null) return null;

        StaffWorkingLocation entity = null;
        if (dto.getId() != null) {
            entity = repository.findById(dto.getId()).orElse(null);
        }
        if (entity == null) {
            entity = new StaffWorkingLocation();
        }

        Staff staff = null;
        if (dto.getStaffId() != null) {
            staff = staffRepository.findById(dto.getStaffId()).orElse(null);
        }

        entity.setStaff(staff);
        //entity.setWorkingLocation(dto.getWorkingLocation());
        Workplace workplace = null;
        if (dto.getWorkplace() != null && dto.getWorkplace().getId() != null) {
            workplace = workplaceRepository.findById(dto.getWorkplace().getId()).orElse(null);
        }
        entity.setWorkplace(workplace);

        if (dto.getIsMainLocation() != null && dto.getIsMainLocation() && staff != null) {
            Set<StaffWorkingLocation> staffLocations = staff.getStaffWorkingLocations();
            if (staffLocations != null) {
                for (StaffWorkingLocation location : staffLocations) {
                    if (location.getIsMainLocation()) {
                        location.setIsMainLocation(false);
                        repository.save(location);
                    }
                }
            }
        }
        entity.setIsMainLocation(dto.getIsMainLocation());
        entity = repository.save(entity);
        return new StaffWorkingLocationDto(entity);
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id == null) return false;
        return repository.findById(id).map(entity -> {
            repository.delete(entity);
            return true;
        }).orElse(false);
    }

    @Override
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        boolean hasDeleted = false;
        for (UUID id : ids) {
            if (this.deleteById(id)) {
                hasDeleted = true;
            }
        }
        return hasDeleted;
    }

    @Override
    public List<StaffWorkingLocationDto> saveStaffWorkingLocationImportFromExcel(List<StaffWorkingLocationImport> importData) {
        List<StaffWorkingLocationDto> response = new ArrayList<>();

        if (importData == null || importData.isEmpty()) return response;

        for (StaffWorkingLocationImport importItem : importData) {
            StaffWorkingLocationDto responseItem = this.saveOneFromImportData(importItem);

            if (responseItem != null) {
                response.add(responseItem);
            }
        }

        return response;
    }

    @Override
    public List<StaffWorkingLocationImport> importExcelStaffWorkingLocation(Sheet datatypeSheet) {
        List<StaffWorkingLocationImport> response = new ArrayList<>();

        try {
            int rowIndex = 1;
            int num = datatypeSheet.getLastRowNum();

            while (rowIndex <= num) {
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell = null;

                if (currentRow != null) {
                    List<String> errorMessages = new ArrayList<>();

                    StaffWorkingLocationImport dto = new StaffWorkingLocationImport();

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

                    if (dto.getStt() == null && !StringUtils.hasText(dto.getStaffCode())) {
                        rowIndex++;
                        continue;
                    }

                    // 2. Họ và tên
                    index++;

                    // 3. Mã địa điểm làm việc
                    index++;
                    currentCell = currentRow.getCell(index);
                    String workplaceCode = this.getCellValueAsString(currentCell);
                    if (!StringUtils.hasText(workplaceCode)) {
                        errorMessages.add("Chưa có mã địa điểm làm việc");
                    }
                    dto.setWorkplaceCode(workplaceCode);

                    // 4.Là địa điểm làm việc chính
                    index++;
                    currentCell = currentRow.getCell(index);
                    String isMainLocation = this.getCellValueAsString(currentCell);
                    dto.setMainLocation(isTrue(isMainLocation));


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

    private StaffWorkingLocationDto saveOneFromImportData(StaffWorkingLocationImport importData) {
        List<String> errorMessages = new ArrayList<>();

        Staff staff = null;
        List<Staff> availableStaffs = staffRepository.findByCode(importData.getStaffCode());
        if (availableStaffs == null || availableStaffs.isEmpty()) {
            errorMessages.add("Không tìm thấy nhân viên hợp lệ");
        } else {
            staff = availableStaffs.get(0);
        }


        Workplace workplace = null;
        List<Workplace> availableWorkplaces = workplaceRepository.findByCode(importData.getWorkplaceCode());
        if (availableWorkplaces == null || availableWorkplaces.isEmpty()) {
            errorMessages.add("Không tìm thấy nơi làm việc hợp lệ");
        } else {
            workplace = availableWorkplaces.get(0);
        }

        // Đọc dữ liệu từ Excel không có lỗi
        if (!errorMessages.isEmpty()) {
            String errorMessage = String.join(". ", errorMessages);
            importData.setErrorMessage(errorMessage);

            return null;
        }

        List<StaffWorkingLocation> availableResults = repository.findLocationByStaffIdWorkplaceId(staff.getId(), workplace.getId());

        StaffWorkingLocation entity = null;
        if (availableResults != null && !availableResults.isEmpty()) {
            entity = availableResults.get(0);
        }
        if (entity == null) {
            entity = new StaffWorkingLocation();
        }

        entity.setStaff(staff);
        entity.setWorkplace(workplace);
        entity.setWorkingLocation(importData.getWorkingLocation());
        // Mặc định gán là false
        entity.setIsMainLocation(false);

        // Nếu nhân viên chưa có bản ghi nào => set là chính
        if (staff.getStaffWorkingLocations() == null || staff.getStaffWorkingLocations().isEmpty()) {
            entity.setIsMainLocation(true);
        } else {
            boolean hasMain = false;

            for (StaffWorkingLocation loc : staff.getStaffWorkingLocations()) {
                if (Boolean.TRUE.equals(loc.getIsMainLocation())) {
                    hasMain = true;
                    break;
                }
            }

            // Nếu chưa có nơi nào là chính => set bản ghi này làm chính
            if (!hasMain) {
                entity.setIsMainLocation(true);
            }
        }


        entity = repository.saveAndFlush(entity);

        // Nếu entity mới này là nơi làm việc chính => cập nhật các bản ghi khác
        if (Boolean.TRUE.equals(entity.getIsMainLocation())) {
            List<StaffWorkingLocation> otherLocations = repository.findLocationByStaffId(staff.getId());

            for (StaffWorkingLocation location : otherLocations) {
                if (!location.getId().equals(entity.getId()) && Boolean.TRUE.equals(location.getIsMainLocation())) {
                    location.setIsMainLocation(false);
                    repository.save(location);
                }
            }
        }


        return new StaffWorkingLocationDto(entity);
    }

    private boolean isTrue(String message) {
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

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING -> cell.getStringCellValue().trim();
            case Cell.CELL_TYPE_NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }
}
