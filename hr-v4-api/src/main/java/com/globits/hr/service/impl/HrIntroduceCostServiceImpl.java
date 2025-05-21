package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.HrIntroduceCostDto;
import com.globits.hr.dto.PositionDto;
import com.globits.hr.dto.PositionRelationshipDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchHrIntroduceCostDto;
import com.globits.hr.repository.HrIntroduceCostRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.HrIntroduceCostService;
import com.globits.hr.service.UserExtService;
import com.globits.hr.utils.ExportExcelUtil;
import jakarta.persistence.Query;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HrIntroduceCostServiceImpl extends GenericServiceImpl<HrIntroduceCost, UUID>
        implements HrIntroduceCostService {
    private static final Logger logger = LoggerFactory.getLogger(HrIntroduceCostServiceImpl.class);
    private static final String EXPORT_POSITIONS_TEMPLATE_PATH = "Excel/MAU_DANH_SACH_THUONG_GIOI_THIEU_NHAN_SU.xlsx";
    private static final int EXPORT_PAGE_SIZE = 100;
    @Autowired
    private HrIntroduceCostRepository hrIntroduceCostRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UserExtService userExtService;

    @Override
    public Page<HrIntroduceCostDto> searchByPage(SearchHrIntroduceCostDto dto) {
        if (dto == null) {
            return null;
        }

        int pageIndex = Math.max(dto.getPageIndex() - 1, 0);
        int pageSize = dto.getPageSize();

        String whereClause = " WHERE (1=1) ";
        String orderBy = " ORDER BY entity.periodOrder ";

        String sqlCount = "SELECT COUNT(DISTINCT entity.id) FROM HrIntroduceCost entity ";
        String sql = "SELECT DISTINCT new com.globits.hr.dto.HrIntroduceCostDto(entity) FROM HrIntroduceCost entity ";

        String joinClause = "";

        // JOIN cho staff
        if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null || dto.getOrganizationId() != null) {
            joinClause += " JOIN Position posStaff ON posStaff.isMain = true AND posStaff.staff.id = entity.staff.id ";
            if (dto.getOrganizationId() != null) {
                whereClause += " AND posStaff.department.organization.id = :organizationId ";
            }
            if (dto.getDepartmentId() != null) {
                whereClause += " AND posStaff.department.id = :departmentId ";
            }
            if (dto.getPositionTitleId() != null) {
                whereClause += " AND posStaff.title.id = :positionTitleId ";
            }
        }

        // JOIN cho introducedStaff
        if (dto.getIntroStaffPositionTitleId() != null || dto.getIntroStaffDepartmentId() != null || dto.getIntroStaffOrganizationId() != null) {
            joinClause += " JOIN Position posIntroStaff ON posIntroStaff.isMain = true AND posIntroStaff.staff.id = entity.introducedStaff.id ";
            if (dto.getIntroStaffOrganizationId() != null) {
                whereClause += " AND posIntroStaff.department.organization.id = :introStaffOrganizationId ";
            }
            if (dto.getIntroStaffDepartmentId() != null) {
                whereClause += " AND posIntroStaff.department.id = :introStaffDepartmentId ";
            }
            if (dto.getIntroStaffPositionTitleId() != null) {
                whereClause += " AND posIntroStaff.title.id = :introStaffPositionTitleId ";
            }
        }

        if (StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND entity.staff.displayName LIKE :text ";
        }

        if (dto.getStaffId() != null || (dto.getStaff() != null && dto.getStaff().getId() != null)) {
            whereClause += " AND entity.staff.id = :staffId ";
        }

        if (dto.getIntroducedStaffId() != null || (dto.getIntroducedStaff() != null && dto.getIntroducedStaff().getId() != null)) {
            whereClause += " AND entity.introducedStaff.id = :introducedStaffId ";
        }

        if (dto.getMonth() != null && dto.getYear() != null) {
            whereClause += " AND ( " +
                    " (MONTH(entity.introducePeriod) = :month AND YEAR(entity.introducePeriod) = :year) OR " +
                    " (MONTH(entity.introducePeriod2) = :month AND YEAR(entity.introducePeriod2) = :year) OR " +
                    " (MONTH(entity.introducePeriod3) = :month AND YEAR(entity.introducePeriod3) = :year) " +
                    " ) ";
        }

        sql += joinClause + whereClause + orderBy;
        sqlCount += joinClause + whereClause;

        Query query = manager.createQuery(sql, HrIntroduceCostDto.class);
        Query qCount = manager.createQuery(sqlCount);

        // Set parameters
        if (StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", "%" + dto.getKeyword() + "%");
            qCount.setParameter("text", "%" + dto.getKeyword() + "%");
        }

        if (dto.getStaffId() != null || (dto.getStaff() != null && dto.getStaff().getId() != null)) {
            UUID staffId = dto.getStaffId() != null ? dto.getStaffId() : dto.getStaff().getId();
            query.setParameter("staffId", staffId);
            qCount.setParameter("staffId", staffId);
        }

        if (dto.getIntroducedStaffId() != null || (dto.getIntroducedStaff() != null && dto.getIntroducedStaff().getId() != null)) {
            UUID introducedStaffId = dto.getIntroducedStaffId() != null ? dto.getIntroducedStaffId() : dto.getIntroducedStaff().getId();
            query.setParameter("introducedStaffId", introducedStaffId);
            qCount.setParameter("introducedStaffId", introducedStaffId);
        }

        if (dto.getMonth() != null && dto.getYear() != null) {
            query.setParameter("month", dto.getMonth());
            query.setParameter("year", dto.getYear());
            qCount.setParameter("month", dto.getMonth());
            qCount.setParameter("year", dto.getYear());
        }

        if (dto.getOrganizationId() != null) {
            query.setParameter("organizationId", dto.getOrganizationId());
            qCount.setParameter("organizationId", dto.getOrganizationId());
        }

        if (dto.getDepartmentId() != null) {
            query.setParameter("departmentId", dto.getDepartmentId());
            qCount.setParameter("departmentId", dto.getDepartmentId());
        }

        if (dto.getPositionTitleId() != null) {
            query.setParameter("positionTitleId", dto.getPositionTitleId());
            qCount.setParameter("positionTitleId", dto.getPositionTitleId());
        }

        if (dto.getIntroStaffOrganizationId() != null) {
            query.setParameter("introStaffOrganizationId", dto.getIntroStaffOrganizationId());
            qCount.setParameter("introStaffOrganizationId", dto.getIntroStaffOrganizationId());
        }

        if (dto.getIntroStaffDepartmentId() != null) {
            query.setParameter("introStaffDepartmentId", dto.getIntroStaffDepartmentId());
            qCount.setParameter("introStaffDepartmentId", dto.getIntroStaffDepartmentId());
        }

        if (dto.getIntroStaffPositionTitleId() != null) {
            query.setParameter("introStaffPositionTitleId", dto.getIntroStaffPositionTitleId());
            qCount.setParameter("introStaffPositionTitleId", dto.getIntroStaffPositionTitleId());
        }

        long count = (long) qCount.getSingleResult();

        query.setFirstResult(pageIndex * pageSize);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<HrIntroduceCostDto> entities = query.getResultList();

        return new PageImpl<>(entities, pageable, count);
    }


    @Override
    public HrIntroduceCostDto getById(UUID id) {
        if (id == null) return null;
        HrIntroduceCost entity = hrIntroduceCostRepository.findById(id).orElse(null);

        if (entity == null) return null;
        HrIntroduceCostDto response = new HrIntroduceCostDto(entity, true);

        return response;
    }

    @Override
    public HrIntroduceCostDto saveOrUpdate(HrIntroduceCostDto dto) {
        if (dto == null) {
            return null;
        }

        HrIntroduceCost entity = new HrIntroduceCost();

        if (dto.getId() != null) entity = hrIntroduceCostRepository.findById(dto.getId()).orElse(null);

        if (entity == null) entity = new HrIntroduceCost();

        entity.setPeriodOrder(dto.getPeriodOrder());

        Staff staff = null;
        if (dto.getStaff() != null) {
            staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        }
        entity.setStaff(staff);

        Staff introducedStaff = null;
        if (dto.getIntroducedStaff() != null) {
            introducedStaff = staffRepository.findById(dto.getIntroducedStaff().getId()).orElse(null);

            if (staff != null && introducedStaff != null) {
                introducedStaff.setIntroducer(staff);

                introducedStaff = staffRepository.saveAndFlush(introducedStaff);
            }
        }
        entity.setIntroducedStaff(introducedStaff);

        entity.setIntroducePeriod(dto.getIntroducePeriod());
        entity.setCost(dto.getCost());

        entity.setIntroducePeriod2(dto.getIntroducePeriod2());
        entity.setCost2(dto.getCost2());

        entity.setIntroducePeriod3(dto.getIntroducePeriod3());
        entity.setCost3(dto.getCost3());


        entity.setNote(dto.getNote());

        entity = hrIntroduceCostRepository.saveAndFlush(entity);

        return new HrIntroduceCostDto(entity);
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id == null) return false;

        HrIntroduceCost entity = hrIntroduceCostRepository.findById(id).orElse(null);
        if (entity == null) return false;

        hrIntroduceCostRepository.delete(entity);
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

    @Override
    public Workbook exportExcelIntroduceCost(SearchHrIntroduceCostDto dto) {
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
            CellStyle dataCellStyleFont14 = ExportExcelUtil.createDataCellStyle(workbook, 14, true);
            CellStyle dataCellStyleColor = ExportExcelUtil.createDataCellStyleColor(workbook);


            Staff staff = userExtService.getCurrentStaffEntity();

            String title = "DANH SÁCH CHI TIẾT VỀ THƯỞNG GIỚI THIỆU NHÂN SỰ";
            String preparedBy = "Người lập:";

            if (dto.getMonth() != null && dto.getYear() != null) {
                title += " " + "T" + dto.getMonth() + "." + dto.getYear();
            }
            if (staff != null && staff.getDisplayName() != null) {
                preparedBy += " " + staff.getDisplayName();
            }
            Row dataRow = staffSheet.createRow(0);
            ExportExcelUtil.createCell(dataRow, 0, title, dataCellStyleFont14);
            dataRow = staffSheet.createRow(2);
            ExportExcelUtil.createCell(dataRow, 0, preparedBy, dataCellStyle);

            int pageIndex = 1;
            int rowIndex = 4;
            boolean hasNextPage = true;
            long startTime = System.nanoTime();

            while (hasNextPage) {
                dto.setPageIndex(pageIndex);
                dto.setPageSize(EXPORT_PAGE_SIZE);

                Page<HrIntroduceCostDto> introduceList = this.searchByPage(dto);
                if (introduceList == null || introduceList.isEmpty()) {
                    break;
                }
                int orderNumber = 0;
                for (HrIntroduceCostDto introduceCostDto : introduceList.getContent()) {
                    if (introduceCostDto == null) continue;
                    dataRow = staffSheet.createRow(rowIndex);
                    int cellIndex = 0;
                    orderNumber++;

                    // 0. STT
                    ExportExcelUtil.createCell(dataRow, cellIndex++, orderNumber, dataCellStyle);
                    String introducedStaffCode = "";
                    String introducedStaffDisplayName = "";
                    String introducedStaffDepartmentName = "";

                    if (introduceCostDto.getIntroducedStaff() != null) {
                        introducedStaffCode = introduceCostDto.getIntroducedStaff().getStaffCode();
                        introducedStaffDisplayName = introduceCostDto.getIntroducedStaff().getDisplayName();
                        if (introduceCostDto.getIntroducedStaff().getDepartment() != null) {
                            introducedStaffDepartmentName = introduceCostDto.getIntroducedStaff().getDepartment().getName();
                        }
                    }
                    // 1. Mã Nhân viên
                    ExportExcelUtil.createCell(dataRow, cellIndex++, introducedStaffCode, dataCellStyle);
                    // 2. Họ tên
                    ExportExcelUtil.createCell(dataRow, cellIndex++, introducedStaffDisplayName, dataCellStyle);
                    // 3. Ban/Chi nhánh
                    ExportExcelUtil.createCell(dataRow, cellIndex++, introducedStaffDepartmentName, dataCellStyle);
                    // 4. Ngày vào
                    ExportExcelUtil.createCell(dataRow, cellIndex++, formatDateToMMDDYYYY(introduceCostDto.getIntroStaffStartDate()), dataCellStyle);

                    String staffDisplayName = "";
                    String staffCode = "";
                    if (introduceCostDto.getStaff() != null && introduceCostDto.getStaff().getDisplayName() != null) {
                        staffDisplayName = introduceCostDto.getStaff().getDisplayName();
                    }
                    if (introduceCostDto.getStaff() != null && introduceCostDto.getStaff().getStaffCode() != null) {
                        staffCode = introduceCostDto.getStaff().getStaffCode();
                    }
                    // 5. Người giới thiệu
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staffDisplayName, dataCellStyle);
                    // 6. Mã người giới thiệu
                    ExportExcelUtil.createCell(dataRow, cellIndex++, staffCode, dataCellStyle);

                    // 7. NGÀY PASS THỬ VIỆC
                    ExportExcelUtil.createCell(dataRow, cellIndex++, formatDateToMMDDYYYY(introduceCostDto.getOfficialDate()), dataCellStyle);

                    // 8. Sáu tháng làm việc
                    ExportExcelUtil.createCell(dataRow, cellIndex++, formatDateToMMDDYYYY(introduceCostDto.getSixMonthsWorking()), dataCellStyle);
                    // 9. Level
                    String rankTitleName = "";
                    if (introduceCostDto.getRankTitle() != null && introduceCostDto.getRankTitle().getName() != null) {
                        rankTitleName = introduceCostDto.getRankTitle().getName();
                    }
                    ExportExcelUtil.createCell(dataRow, cellIndex++, rankTitleName, dataCellStyle);
                    // 10. Tổng thưởng theo cấp bậc
                    ExportExcelUtil.createCell(dataRow, cellIndex++, formatCurrencyVND(introduceCostDto.getReferralFeeLevel()), dataCellStyle);
                    // 11. Ngày tính giới thiệu đợt 1
                    // 12. Chi phí giới thiệu đợt 1
                    if (introduceCostDto.getIntroducePeriod() != null
                            && dto.getMonth() != null
                            && dto.getYear() != null
                            && isInSelectedMonthYear(introduceCostDto.getIntroducePeriod(), dto.getMonth(), dto.getYear())) {
                        ExportExcelUtil.createCell(dataRow, cellIndex++, formatDateToMMDDYYYY(introduceCostDto.getIntroducePeriod()), dataCellStyleColor);
                        ExportExcelUtil.createCell(dataRow, cellIndex++, formatCurrencyVND(introduceCostDto.getCost()), dataCellStyleColor);
                    } else {
                        ExportExcelUtil.createCell(dataRow, cellIndex++, formatDateToMMDDYYYY(introduceCostDto.getIntroducePeriod()), dataCellStyle);
                        ExportExcelUtil.createCell(dataRow, cellIndex++, formatCurrencyVND(introduceCostDto.getCost()), dataCellStyle);
                    }

                    // 13. Ngày tính giới thiệu đợt 2
                    // 14. Chi phí giới thiệu đợt 2
                    if (introduceCostDto.getIntroducePeriod2() != null
                            && dto.getMonth() != null
                            && dto.getYear() != null
                            && isInSelectedMonthYear(introduceCostDto.getIntroducePeriod2(), dto.getMonth(), dto.getYear())) {
                        ExportExcelUtil.createCell(dataRow, cellIndex++, formatDateToMMDDYYYY(introduceCostDto.getIntroducePeriod2()), dataCellStyleColor);
                        ExportExcelUtil.createCell(dataRow, cellIndex++, formatCurrencyVND(introduceCostDto.getCost2()), dataCellStyleColor);
                    } else {
                        ExportExcelUtil.createCell(dataRow, cellIndex++, formatDateToMMDDYYYY(introduceCostDto.getIntroducePeriod2()), dataCellStyle);
                        ExportExcelUtil.createCell(dataRow, cellIndex++, formatCurrencyVND(introduceCostDto.getCost2()), dataCellStyle);
                    }
                    // 15. Ngày tính giới thiệu đợt 3
                    // 16. Chi phí giới thiệu đợt 3
                    if (introduceCostDto.getIntroducePeriod3() != null
                            && dto.getMonth() != null
                            && dto.getYear() != null
                            && isInSelectedMonthYear(introduceCostDto.getIntroducePeriod3(), dto.getMonth(), dto.getYear())) {
                        ExportExcelUtil.createCell(dataRow, cellIndex++, formatDateToMMDDYYYY(introduceCostDto.getIntroducePeriod3()), dataCellStyleColor);
                        ExportExcelUtil.createCell(dataRow, cellIndex++, formatCurrencyVND(introduceCostDto.getCost3()), dataCellStyleColor);
                    } else {
                        ExportExcelUtil.createCell(dataRow, cellIndex++, formatDateToMMDDYYYY(introduceCostDto.getIntroducePeriod3()), dataCellStyle);
                        ExportExcelUtil.createCell(dataRow, cellIndex++, formatCurrencyVND(introduceCostDto.getCost3()), dataCellStyle);
                    }
                    // 17. Ghi chú
                    ExportExcelUtil.createCell(dataRow, cellIndex++, introduceCostDto.getNote(), dataCellStyle);

                    // thêm dòng tiếp theo
                    rowIndex++;
                }

                hasNextPage = introduceList.hasNext(); // Kiểm tra xem còn trang tiếp theo không
                pageIndex++; // Tăng pageIndex để lấy trang tiếp theo
            }
            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;

            logger.info("Xuất danh danh sách thưởng chi phí giới thiệu - Xử lý mất {} ms ", elapsedTimeMs);
            return workbook;

        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }

    public static String formatDateToMMDDYYYY(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    public static String formatCurrencyVND(Double amount) {
        if (amount == null) return "";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount); // Ví dụ: 1.000.000 ₫
    }

    public boolean isInSelectedMonthYear(Date date, Integer selectedMonth, Integer selectedYear) {
        if (date == null || selectedMonth == null || selectedYear == null) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int month = calendar.get(Calendar.MONTH) + 1; // Lưu ý: MONTH bắt đầu từ 0
        int year = calendar.get(Calendar.YEAR);

        return month == selectedMonth && year == selectedYear;
    }

}
