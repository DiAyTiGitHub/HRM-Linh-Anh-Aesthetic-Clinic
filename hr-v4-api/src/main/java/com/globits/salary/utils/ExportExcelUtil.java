package com.globits.salary.utils;

import com.globits.salary.dto.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.*;

public class ExportExcelUtil {
	public static ByteArrayResource exportExcelSalaryResult(SalaryResultDto datas) throws IOException {
	    // Khởi tạo workbook và sheet Excel
	    XSSFWorkbook workbook = new XSSFWorkbook();
	    Sheet sheet = workbook.createSheet("Bảng lương");

	    // Định dạng số theo kiểu Việt Nam (VD: 1,234,567)
	    NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

	    // Tạo font chữ Times New Roman, kích thước 11
	    Font font = workbook.createFont();
	    font.setFontName("Times New Roman");
	    font.setFontHeightInPoints((short) 11);

	    // Tạo font chữ đậm Times New Roman, kích thước 11
	    Font fontBold = workbook.createFont();
	    fontBold.setFontName("Times New Roman");
	    fontBold.setFontHeightInPoints((short) 11);
	    fontBold.setBold(true);

	    // Tạo style cho ô căn giữa, có viền
	    XSSFCellStyle cellStyleBorderCenter = workbook.createCellStyle();
	    cellStyleBorderCenter.setBorderBottom(BorderStyle.THIN);
	    cellStyleBorderCenter.setBorderTop(BorderStyle.THIN);
	    cellStyleBorderCenter.setBorderLeft(BorderStyle.THIN);
	    cellStyleBorderCenter.setBorderRight(BorderStyle.THIN);
	    cellStyleBorderCenter.setAlignment(HorizontalAlignment.CENTER);
	    cellStyleBorderCenter.setVerticalAlignment(VerticalAlignment.CENTER);
	    cellStyleBorderCenter.setWrapText(true);
	    cellStyleBorderCenter.setFont(font);

	    // Tạo style cho ô căn phải, có viền
	    XSSFCellStyle cellStyleBorderRight = workbook.createCellStyle();
	    cellStyleBorderRight.setBorderBottom(BorderStyle.THIN);
	    cellStyleBorderRight.setBorderTop(BorderStyle.THIN);
	    cellStyleBorderRight.setBorderLeft(BorderStyle.THIN);
	    cellStyleBorderRight.setBorderRight(BorderStyle.THIN);
	    cellStyleBorderRight.setAlignment(HorizontalAlignment.RIGHT);
	    cellStyleBorderRight.setVerticalAlignment(VerticalAlignment.CENTER);
	    cellStyleBorderRight.setWrapText(true);
	    cellStyleBorderRight.setFont(font);

	    // Tạo style cho ô căn trái, có viền
	    XSSFCellStyle cellStyleBorderLeft = workbook.createCellStyle();
	    cellStyleBorderLeft.setBorderBottom(BorderStyle.THIN);
	    cellStyleBorderLeft.setBorderTop(BorderStyle.THIN);
	    cellStyleBorderLeft.setBorderLeft(BorderStyle.THIN);
	    cellStyleBorderLeft.setBorderRight(BorderStyle.THIN);
	    cellStyleBorderLeft.setAlignment(HorizontalAlignment.LEFT);
	    cellStyleBorderLeft.setVerticalAlignment(VerticalAlignment.CENTER);
	    cellStyleBorderLeft.setWrapText(true);
	    cellStyleBorderLeft.setFont(font);

	    // Tạo style cho ô căn phải, chữ đậm, có viền
	    XSSFCellStyle cellStyleBorderBoldRight = workbook.createCellStyle();
	    cellStyleBorderBoldRight.setBorderBottom(BorderStyle.THIN);
	    cellStyleBorderBoldRight.setBorderTop(BorderStyle.THIN);
	    cellStyleBorderBoldRight.setBorderLeft(BorderStyle.THIN);
	    cellStyleBorderBoldRight.setBorderRight(BorderStyle.THIN);
	    cellStyleBorderBoldRight.setAlignment(HorizontalAlignment.RIGHT);
	    cellStyleBorderBoldRight.setVerticalAlignment(VerticalAlignment.CENTER);
	    cellStyleBorderBoldRight.setWrapText(true);
	    cellStyleBorderBoldRight.setFont(fontBold);

	    // Tạo style cho ô căn giữa, chữ đậm, có viền
	    XSSFCellStyle cellStyleBorderBoldCenter = workbook.createCellStyle();
	    cellStyleBorderBoldCenter.setBorderBottom(BorderStyle.THIN);
	    cellStyleBorderBoldCenter.setBorderTop(BorderStyle.THIN);
	    cellStyleBorderBoldCenter.setBorderLeft(BorderStyle.THIN);
	    cellStyleBorderBoldCenter.setBorderRight(BorderStyle.THIN);
	    cellStyleBorderBoldCenter.setAlignment(HorizontalAlignment.CENTER);
	    cellStyleBorderBoldCenter.setVerticalAlignment(VerticalAlignment.CENTER);
	    cellStyleBorderBoldCenter.setWrapText(true);
	    cellStyleBorderBoldCenter.setFont(fontBold);

	    // Danh sách các cột cố định (tương ứng với giao diện frontend)
	    List<Map<String, String>> hardCodeColumns = new ArrayList<>();
	    Map<String, String> col1 = new HashMap<>();
	    col1.put("displayName", "Mã NV");
	    col1.put("field", "staffCode");
	    hardCodeColumns.add(col1);

	    Map<String, String> col2 = new HashMap<>();
	    col2.put("displayName", "Họ và tên");
	    col2.put("field", "staffName");
	    hardCodeColumns.add(col2);

	    Map<String, String> col3 = new HashMap<>();
	    col3.put("displayName", "Đơn vị");
	    col3.put("field", "mainOrganization");
	    hardCodeColumns.add(col3);

	    Map<String, String> col4 = new HashMap<>();
	    col4.put("displayName", "Phòng ban");
	    col4.put("field", "mainDepartment");
	    hardCodeColumns.add(col4);

	    Map<String, String> col5 = new HashMap<>();
	    col5.put("displayName", "Chức danh");
	    col5.put("field", "mainPositionTitle");
	    hardCodeColumns.add(col5);

	    // Danh sách các cột và nhóm cột cho header
	    List<SalaryTemplateItemDto> columnGroups = new ArrayList<>();
	    List<SalaryTemplateItemDto> remainItems = new ArrayList<>();

	    // Xử lý các cột và nhóm cột dựa trên logic frontend
	    for (SalaryTemplateItemDto item : datas.getTemplateItems()) {
	        // Bỏ qua các mục bị ẩn trên bảng lương
	        if (item != null && item.getHiddenOnSalaryBoard() != null && item.getHiddenOnSalaryBoard()) {
	            continue;
	        }

	        if (item.getTemplateItemGroupId() == null) {
	            // Cột thông thường không thuộc nhóm (rowspan = 2)
	            SalaryTemplateItemDto columnItem = new SalaryTemplateItemDto();
	            columnItem.setId(item.getId());
	            columnItem.setDisplayName(item.getDisplayName());
	            columnItem.setItem(true);
	            columnItem.setValueType(item.getValueType());

	            columnGroups.add(columnItem);
	        } else {
	            // Kiểm tra xem nhóm đã tồn tại chưa
	            boolean groupExists = false;
	            if (!columnGroups.isEmpty()) {
	                SalaryTemplateItemDto lastGroup = columnGroups.get(columnGroups.size() - 1);
	                if (lastGroup.getId() != null && lastGroup.getId().equals(item.getTemplateItemGroupId())) {
	                    // Tăng colspan của nhóm hiện tại
	                    lastGroup.setColSpan(lastGroup.getColSpan() + 1);
	                    groupExists = true;
	                }
	            }

	            if (!groupExists) {
	                // Thêm nhóm mới
	                SalaryTemplateItemGroupDto groupInfo = null;
	                for (SalaryTemplateItemGroupDto group : datas.getTemplateItemGroups()) {
	                    if (group.getId().equals(item.getTemplateItemGroupId())) {
	                        groupInfo = group;
	                        break;
	                    }
	                }

	                if (groupInfo != null) {
	                    SalaryTemplateItemDto group = new SalaryTemplateItemDto();
	                    group.setId(groupInfo.getId());
	                    group.setDisplayName(groupInfo.getName());
	                    group.setColSpan(1);
	                    group.setItem(false);

	                    columnGroups.add(group);
	                }
	            }

	            remainItems.add(item);
	        }
	    }

	    // Tạo hai dòng header
	    Row rowHeader1 = sheet.createRow(0);
	    Row rowHeader2 = sheet.createRow(1);

	    // Thêm cột số thứ tự (STT)
	    Cell cell = rowHeader1.createCell(0);
	    cell.setCellValue("STT");
	    cell.setCellStyle(cellStyleBorderBoldCenter);
	    sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));

	    Cell cellRow2 = rowHeader2.createCell(0);
	    cellRow2.setCellStyle(cellStyleBorderBoldCenter);

	    int colIndex = 1;

	    // Thêm các cột cố định
	    for (Map<String, String> col : hardCodeColumns) {
	        cell = rowHeader1.createCell(colIndex);
	        cell.setCellValue(col.get("displayName"));
	        cell.setCellStyle(cellStyleBorderBoldCenter);
	        sheet.addMergedRegion(new CellRangeAddress(0, 1, colIndex, colIndex));

	        cellRow2 = rowHeader2.createCell(colIndex);
	        cellRow2.setCellStyle(cellStyleBorderBoldCenter);
	        colIndex++;
	    }

	    // Xử lý các nhóm cột và cột con
	    for (SalaryTemplateItemDto column : columnGroups) {
	        cell = rowHeader1.createCell(colIndex);

	        if (column.getItem()) {
	            // Cột đơn lẻ (rowspan=2)
	            cell.setCellValue(column.getDisplayName());
	            cell.setCellStyle(cellStyleBorderBoldCenter);
	            sheet.addMergedRegion(new CellRangeAddress(0, 1, colIndex, colIndex));

	            cellRow2 = rowHeader2.createCell(colIndex);
	            cellRow2.setCellStyle(cellStyleBorderBoldCenter);
	            colIndex++;
	        } else {
	            // Nhóm cột (có colspan)
	            cell.setCellValue(column.getDisplayName());
	            cell.setCellStyle(cellStyleBorderBoldCenter);

	            int colspan = column.getColSpan();
	            if (colspan > 1) {
	                sheet.addMergedRegion(new CellRangeAddress(0, 0, colIndex, colIndex + colspan - 1));

	                // Thêm các cột con vào dòng header thứ hai
	                for (int i = 0; i < colspan; i++) {
	                    Cell subCell = rowHeader2.createCell(colIndex + i);
	                    boolean itemFound = false;
	                    for (SalaryTemplateItemDto item : remainItems) {
	                        if (item.getTemplateItemGroupId() != null && item.getTemplateItemGroupId().equals(column.getId())) {
	                            subCell.setCellValue(item.getDisplayName());
	                            remainItems.remove(item); // Xóa mục đã sử dụng
	                            itemFound = true;
	                            break;
	                        }
	                    }
	                    if (!itemFound) {
	                        subCell.setCellValue(""); // Tạo ô trống nếu không có mục
	                    }
	                    subCell.setCellStyle(cellStyleBorderBoldCenter);
	                }

	                colIndex += colspan;
	            } else {
	                // Xử lý cột đơn trong nhóm
	                Cell subCell = rowHeader2.createCell(colIndex);
	                boolean itemFound = false;
	                for (SalaryTemplateItemDto item : remainItems) {
	                    if (item.getTemplateItemGroupId() != null && item.getTemplateItemGroupId().equals(column.getId())) {
	                        subCell.setCellValue(item.getDisplayName());
	                        remainItems.remove(item); // Xóa mục đã sử dụng
	                        itemFound = true;
	                        break;
	                    }
	                }
	                if (!itemFound) {
	                    subCell.setCellValue(""); // Tạo ô trống nếu không có mục
	                }
	                subCell.setCellStyle(cellStyleBorderBoldCenter);
	                colIndex++;
	            }
	        }
	    }

	    if (datas.getSalaryResultStaffs() != null && !datas.getSalaryResultStaffs().isEmpty()) {
	        // Thêm các dòng dữ liệu
	        int rowIndex = 2;
	        int index = 1;
	        for (SalaryResultStaffDto staff : datas.getSalaryResultStaffs()) {
	            Row dataRow = sheet.createRow(rowIndex++);

	            // Cột số thứ tự
	            cell = dataRow.createCell(0);
	            cell.setCellValue(index);
	            cell.setCellStyle(cellStyleBorderCenter);

	            int cellIndex = 1;

	            // Thêm dữ liệu cho các cột cố định
	            for (Map<String, String> col : hardCodeColumns) {
	                cell = dataRow.createCell(cellIndex++);
	                String fieldName = col.get("field");
	                String value = getFieldValue(staff, fieldName);
	                cell.setCellValue(value);
	                cell.setCellStyle(cellStyleBorderLeft);
	            }

	            // Thêm dữ liệu cho các mục lương
	            if (staff.getSalaryResultStaffItems() != null) {
	                // Đặt lại chỉ số cột cho các mục lương
	                cellIndex = hardCodeColumns.size() + 1;
	                // Theo dõi các cột đã được điền dữ liệu
	                Map<Integer, Boolean> filledColumns = new HashMap<>();
	                for (int i = cellIndex; i < colIndex; i++) {
	                    filledColumns.put(i, false);
	                }

	                for (SalaryResultStaffItemDto item : staff.getSalaryResultStaffItems()) {
	                    if (item != null && item.getSalaryTemplateItem() != null && item.getSalaryTemplateItem().getHiddenOnSalaryBoard() != null && item.getSalaryTemplateItem().getHiddenOnSalaryBoard()) {
	                        continue;
	                    }
	                    // Tìm vị trí cột đúng cho mục này
	                    int targetCol = hardCodeColumns.size() + 1;
	                    for (SalaryTemplateItemDto templateItem : datas.getTemplateItems()) {
	                        if (templateItem.getId().equals(item.getSalaryTemplateItem().getId())) {
	                            break;
	                        }
	                        if (templateItem.getHiddenOnSalaryBoard() != null && templateItem.getHiddenOnSalaryBoard()) {
	                            continue;
	                        }
	                        targetCol++;
	                    }

	                    cell = dataRow.createCell(targetCol);
	                    cell.setCellStyle(cellStyleBorderRight);
	                    filledColumns.put(targetCol, true);

	                    try {
	                        double value = Double.parseDouble(item.getValue());
	                        cell.setCellValue(value);
	                    } catch (Exception e) {
	                        cell.setCellValue(item.getValue());
	                    }
	                }

	                // Điền ô trống cho các cột chưa được điền dữ liệu
	                for (int i = cellIndex; i < colIndex; i++) {
	                    if (!filledColumns.getOrDefault(i, false)) {
	                        cell = dataRow.createCell(i);
	                        cell.setCellValue("");
	                        cell.setCellStyle(cellStyleBorderRight);
	                    }
	                }
	            }
	            index++;
	        }

	        // Tạo dòng tổng ở cuối
	        int lastRowIndex = sheet.getLastRowNum() + 1; // Tạo dòng mới sau dòng cuối cùng
	        Row totalRow = sheet.createRow(lastRowIndex);

	        for (int i = 0; i < colIndex; i++) {
	            if (i < 6) {
	                cell = totalRow.createCell(i);
	                cell.setCellStyle(cellStyleBorderBoldCenter);
	                if (i == 0) {
	                    // Gộp các ô từ 0 đến 5 và ghi "Tổng"
	                    sheet.addMergedRegion(new CellRangeAddress(lastRowIndex, lastRowIndex, 0, 5));
	                    cell.setCellValue("Tổng");
	                    cell.setCellStyle(cellStyleBorderBoldCenter);
	                }
	            } else {
	                // Tính tổng cho các cột số
	                double totalMoneyCurrent = 0.0;
	                for (int j = 2; j < lastRowIndex; j++) {
	                    Cell currentCell = sheet.getRow(j).getCell(i);
	                    if (currentCell != null) {
	                        try {
	                            if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
	                                totalMoneyCurrent += currentCell.getNumericCellValue();
	                            } else if (currentCell.getCellTypeEnum() == CellType.STRING) {
	                                totalMoneyCurrent += Double.parseDouble(currentCell.getStringCellValue());
	                            }
	                        } catch (Exception e) {
	                            // Bỏ qua giá trị không phải số
	                        }
	                    }
	                }
	                cell = totalRow.createCell(i);
	                cell.setCellStyle(cellStyleBorderBoldRight);
	                cell.setCellValue(formatter.format(totalMoneyCurrent));
	            }
	        }
	    }

	    // Đặt chiều rộng cột tối thiểu là 7000
	    for (int i = 1; i < colIndex; i++) {
	        int columnWidth = sheet.getColumnWidth(i);
	        if (columnWidth < 7000) {
	            sheet.setColumnWidth(i, 7000);
	        }
	    }

	    // Xuất workbook ra ByteArrayOutputStream
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    workbook.write(out);
	    workbook.close();
	    return new ByteArrayResource(out.toByteArray());
	}

	// Phương thức hỗ trợ lấy giá trị trường bằng reflection
	private static String getFieldValue(SalaryResultStaffDto staff, String fieldName) {
	    try {
	        Field field = staff.getClass().getDeclaredField(fieldName);
	        field.setAccessible(true);
	        Object value = field.get(staff);
	        return value != null ? value.toString() : "";
	    } catch (NoSuchFieldException | IllegalAccessException e) {
	        return "";
	    }
	}
    
    public static ByteArrayResource wirteSalaryTemplateItem(SalaryTemplateDto salaryTemplateDto, XSSFWorkbook workbook) {
        List<SalaryTemplateItemDto> listSalaryTemplateItem = salaryTemplateDto.getTemplateItems();
        if (listSalaryTemplateItem == null) {
            listSalaryTemplateItem = new ArrayList<>();
        }

        XSSFSheet sheet = workbook.getSheetAt(0);

        // Font và style
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 14);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);
        cellStyle.setFont(font);

//        XSSFCellStyle cellStyleLeft = workbook.createCellStyle();
//        cellStyleLeft.cloneStyleFrom(cellStyle);
//        cellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
//
//        XSSFCellStyle cellStyleRight = workbook.createCellStyle();
//        cellStyleRight.cloneStyleFrom(cellStyle);
//        cellStyleRight.setAlignment(HorizontalAlignment.RIGHT);

        // Dòng 0 và 1: Header - merge và set label cố định
        Row row0 = sheet.createRow(0);
        Row row1 = sheet.createRow(1);

        String[] fixedHeaders = {"STT", "Mã nhân viên", "Tên nhân viên", "Mã bảng lương", "Tên bảng lương"};
        for (int i = 0; i < fixedHeaders.length; i++) {
            Cell cell0 = row0.createCell(i);
            cell0.setCellValue(fixedHeaders[i]);
            cell0.setCellStyle(cellStyle);

            Cell cell1 = row1.createCell(i);
            cell1.setCellStyle(cellStyle);

            // Merge ô giữa dòng 0 và dòng 1
            sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i));
        }

        // Dòng 2: Mã bảng lương & Tên bảng lương
        Row row2 = sheet.createRow(2);
        row2.createCell(3).setCellValue(salaryTemplateDto.getCode());  // Mã bảng lương
        row2.createCell(4).setCellValue(salaryTemplateDto.getName());  // Tên bảng lương
        row2.getCell(3).setCellStyle(cellStyle);
        row2.getCell(4).setCellStyle(cellStyle);

        // Từ cột thứ 5: theo listSalaryTemplateItem
        int startCol = 5;
        for (int i = 0; i < listSalaryTemplateItem.size(); i++) {
            SalaryTemplateItemDto item = listSalaryTemplateItem.get(i);
            int colIndex = startCol + i;

            // Dòng 0: displayName 
            Cell cellDisplayName = row0.createCell(colIndex);
            cellDisplayName.setCellValue(item.getDisplayName());
            cellDisplayName.setCellStyle(cellStyle);

            // Dòng 1: code
            Cell cellCode = row1.createCell(colIndex);
            cellCode.setCellValue(item.getCode());
            cellCode.setCellStyle(cellStyle);
        }

        // Auto size columns nếu cần
        for (int i = 0; i < startCol + listSalaryTemplateItem.size(); i++) {
            sheet.autoSizeColumn(i);

            // Tăng thêm độ rộng một chút để tránh bị cột quá hẹp
            int currentWidth = sheet.getColumnWidth(i);
            int extraWidth = 1500; // Tăng thêm (tuỳ chỉnh)
            sheet.setColumnWidth(i, currentWidth + extraWidth);
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            workbook.close();
            return new ByteArrayResource(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

	
	
}
