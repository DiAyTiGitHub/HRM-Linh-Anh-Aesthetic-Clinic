package com.globits.hr.utils;

import com.globits.hr.HrConstants;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffFamilyRelationship;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchStaffLabourAgreementDto;
import com.globits.task.dto.KanbanDto;
import com.globits.timesheet.dto.LeaveRequestDto;
import com.globits.timesheet.dto.TimeSheetDetailDto;
import com.globits.timesheet.dto.TimeSheetDto;
import com.globits.timesheet.dto.TimeSheetShiftWorkPeriodDto;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class ExportExcelUtil {
    private static final Logger log = LoggerFactory.getLogger(ExportExcelUtil.class);

    public static ByteArrayResource exportExcelTaskByFilter(List<KanbanDto> taskData) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        /* Tạo font */
        XSSFFont fontBold = workbook.createFont();
        fontBold.setBold(true); // set bold
        fontBold.setFontHeight(14); // add font size

        XSSFFont fontBoldTitle = workbook.createFont();
        fontBoldTitle.setBold(true); // set bold
        fontBoldTitle.setFontHeight(16); // add font size

        /* Tạo cell style */
        XSSFCellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setFont(fontBoldTitle);

        XSSFCellStyle tableHeadCellStyle = workbook.createCellStyle();
        tableHeadCellStyle.setFont(fontBold);
        tableHeadCellStyle.setBorderBottom(BorderStyle.THIN);
        tableHeadCellStyle.setBorderTop(BorderStyle.THIN);
        tableHeadCellStyle.setBorderLeft(BorderStyle.THIN);
        tableHeadCellStyle.setBorderRight(BorderStyle.THIN);

        XSSFRow row = sheet.createRow(0);
        XSSFCell cell;

        cell = row.createCell(0);
        cell.setCellValue("Mã dự án");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Tên dự án");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Mã công việc");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Tên công việc");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Hoạt động");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(5);
        cell.setCellValue("Thời gian tạo");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(6);
        cell.setCellValue("Người tạo");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(7);
        cell.setCellValue("Trạng thái");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(8);
        cell.setCellValue("Phụ trách");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(9);
        cell.setCellValue("Độ ưu tiên");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(10);
        cell.setCellValue("Ngày bắt đầu");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(11);
        cell.setCellValue("Ngày kết thúc");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(12);
        cell.setCellValue("Cập nhật cuối");
        cell.setCellStyle(tableHeadCellStyle);


        // Tạo các hàng cột dữ liệu
        XSSFRow tableDataRow;
        if (taskData != null && !taskData.isEmpty()) {
            for (int i = 0; i < taskData.size(); i++) {
                tableDataRow = sheet.createRow(i + 1);
                KanbanDto task = taskData.get(i);

                if (task != null) {
                    if (task.getProjectCode() != null) {
                        tableDataRow.createCell(0).setCellValue(task.getProjectCode());
                    } else {
                        tableDataRow.createCell(0).setCellValue("");
                    }

                    if (task.getProjectName() != null) {
                        tableDataRow.createCell(1).setCellValue(task.getProjectName());
                    } else {
                        tableDataRow.createCell(1).setCellValue("");
                    }

                    if (task.getCode() != null) {
                        String taskCode = "";
                        if (task.getProjectCode() != null) taskCode += task.getProjectCode();
                        taskCode += "#";
                        if (task.getCode() != null) taskCode += task.getCode();

                        tableDataRow.createCell(2).setCellValue(taskCode);
                    } else {
                        tableDataRow.createCell(2).setCellValue("");
                    }

                    if (task.getName() != null) {
                        tableDataRow.createCell(3).setCellValue(task.getName());
                    } else {
                        tableDataRow.createCell(3).setCellValue("");
                    }

                    if (task.getActivity() != null) {
                        tableDataRow.createCell(4).setCellValue(task.getActivity());
                    } else {
                        tableDataRow.createCell(4).setCellValue("");
                    }

                    if (task.getCreateDate() != null) {
                        tableDataRow.createCell(5).setCellValue(convertTimestampToDate(task.getCreateDate().getTime()));
                    } else {
                        tableDataRow.createCell(5).setCellValue("");
                    }

                    if (task.getCreatorName() != null) {
                        tableDataRow.createCell(6).setCellValue(task.getCreatorName());
                    } else {
                        tableDataRow.createCell(6).setCellValue("");
                    }

                    if (task.getStatusName() != null) {
                        tableDataRow.createCell(7).setCellValue(task.getStatusName());
                    } else {
                        tableDataRow.createCell(7).setCellValue("");
                    }

                    if (task.getAssignee() != null) {
                        tableDataRow.createCell(8).setCellValue(task.getAssignee());
                    } else {
                        tableDataRow.createCell(8).setCellValue("");
                    }

                    //priority need to map to its string value in client
                    if (task.getPriority() != null) {
                        tableDataRow.createCell(9).setCellValue(getPriorityNameByValue(task.getPriority()));
                    } else {
                        tableDataRow.createCell(9).setCellValue("");
                    }

                    if (task.getStartTime() != null) {
                        tableDataRow.createCell(10).setCellValue(convertTimestampToDate(task.getStartTime().getTime()));
                    } else {
                        tableDataRow.createCell(10).setCellValue("");
                    }

                    if (task.getEndTime() != null) {
                        tableDataRow.createCell(11).setCellValue(convertTimestampToDate(task.getEndTime().getTime()));
                    } else {
                        tableDataRow.createCell(11).setCellValue("");
                    }

                    if (task.getLastModifyDate() != null) {
                        tableDataRow.createCell(12).setCellValue(convertTimestampToDate(task.getLastModifyDate().getTime()));
                    } else {
                        tableDataRow.createCell(12).setCellValue("");
                    }
                }

                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            return new ByteArrayResource(out.toByteArray());
        }
        return null;
    }

    public static String convertTimestampToDate(long timestamp) {
        // Create a SimpleDateFormat object with desired date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Convert timestamp to Date object
        Date date = new Date(timestamp);

        // Format the Date object to a string
        String dateString = sdf.format(date);

        return dateString;
    }

    private static String getPriorityNameByValue(Integer value) {
        if (value == null) return "Chưa đặt";

        if (value == 1) return "Thấp";

        if (value == 2) return "Trung bình";

        if (value == 3) return "Cao";

        if (value == 4) return "Cấp bách";

        return "Chưa đặt";
    }


    public static ByteArrayResource exportTimeSheetToExcelTable(List<TimeSheetDto> dataList, Boolean isCheck) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        XSSFFont fontBold = workbook.createFont();
        fontBold.setBold(true);
        fontBold.setFontHeight(10);

        XSSFFont fontBoldTitle = workbook.createFont();
        fontBoldTitle.setBold(true);
        fontBoldTitle.setFontHeight(11);

        XSSFCellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setFont(fontBoldTitle);

        XSSFCellStyle tableHeadCellStyle = workbook.createCellStyle();
        tableHeadCellStyle.setFont(fontBoldTitle);
        tableHeadCellStyle.setAlignment(HorizontalAlignment.CENTER);
        tableHeadCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFRow row = sheet.createRow(0);
        XSSFCell cell;

        cell = row.createCell(0);
        cell.setCellValue("STT");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Dự án");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Ngày làm việc");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Công việc");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Mô tả");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(5);
        cell.setCellValue("Thời gian bắt đầu");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(6);
        cell.setCellValue("Thời gian kết thúc");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(7);
        cell.setCellValue("Tổng thời gian(giờ)");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(8);
        cell.setCellValue("Mức độ ưu tiên");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(9);
        cell.setCellValue("Người thực hiện");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(10);
        cell.setCellValue("Trạng thái");
        cell.setCellStyle(tableHeadCellStyle);
        for (int col = 0; col <= 10; col++) {
            sheet.addMergedRegion(new CellRangeAddress(0, 1, col, col));
        }

        cell = row.createCell(11);
        cell.setCellValue("Đầu việc");
        cell.setCellStyle(tableHeadCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 11, 15));

        row = sheet.createRow(1);
        cell = row.createCell(11);
        cell.setCellValue("Tiêu đề");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(12);
        cell.setCellValue("giờ bắt đầu");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(13);
        cell.setCellValue("giờ kết thúc");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(14);
        cell.setCellValue("Tổng giờ");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(15);
        cell.setCellValue("Nhân viên thực hiện");
        cell.setCellStyle(tableHeadCellStyle);

        XSSFCellStyle tableDataRowStyle = workbook.createCellStyle();
        tableDataRowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        tableDataRowStyle.setAlignment(HorizontalAlignment.CENTER);
        XSSFRow tableDataRow;

        if (dataList != null && !dataList.isEmpty()) {
            SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm dd-MM-yyyy");
            SimpleDateFormat formatDateWork = new SimpleDateFormat("dd-MM-yyyy");
            int increaseRow = 2;
            for (int i = 0; i < dataList.size(); i++) {
                XSSFCell cellData;
                TimeSheetDto data = dataList.get(i);
                tableDataRow = sheet.createRow(increaseRow);

                cellData = tableDataRow.createCell(0);
                cellData.setCellValue(i + 1);
                cellData.setCellStyle(tableDataRowStyle);

                if (data.getProject() != null) {
                    cellData = tableDataRow.createCell(1);
                    cellData.setCellValue(data.getProject().getName());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getWorkingDate() != null) {
                    cellData = tableDataRow.createCell(2);
                    cellData.setCellValue(formatDateWork.format(data.getWorkingDate()));
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getActivity() != null) {
                    cellData = tableDataRow.createCell(3);
                    cellData.setCellValue(data.getActivity().getName());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getDescription() != null) {
                    cellData = tableDataRow.createCell(4);
                    cellData.setCellValue(data.getDescription());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getStartTime() != null) {
                    cellData = tableDataRow.createCell(5);
                    cellData.setCellValue(formatDate.format(data.getStartTime()));
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getEndTime() != null) {
                    cellData = tableDataRow.createCell(6);
                    cellData.setCellValue(formatDate.format(data.getEndTime()));
                    cellData.setCellStyle(tableDataRowStyle);
                }

                cellData = tableDataRow.createCell(7);
                cellData.setCellValue(data.getTotalHours());
                cellData.setCellStyle(tableDataRowStyle);

                if (data.getPriority() != null) {
                    cellData = tableDataRow.createCell(8);
                    cellData.setCellValue(data.getPriority());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                List<StaffDto> staffDtoList = data.getTimeSheetStaff();
                StringBuilder strStaffs = new StringBuilder();
                if (staffDtoList != null && !staffDtoList.isEmpty()) {
                    for (StaffDto staffDto : staffDtoList) {
                        strStaffs.append(staffDto.getDisplayName()).append("\n");
                    }
                    cellData = tableDataRow.createCell(9);
                    cellData.setCellValue(strStaffs.toString());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getWorkingStatus() != null) {
                    cellData = tableDataRow.createCell(10);
                    cellData.setCellValue(data.getWorkingStatus().getName());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                int lastRow;
                if (data.getDetails() != null && data.getDetails().size() > 1) {
                    lastRow = data.getDetails().size();
                    for (int col = 0; col <= 10; col++) {
                        sheet.addMergedRegion(new CellRangeAddress(increaseRow, increaseRow + lastRow - 1, col, col));
                    }
                }

                List<TimeSheetDetailDto> detailDtoList = data.getDetails();
                if (detailDtoList != null && !detailDtoList.isEmpty()) {
                    for (int j = 0; j < detailDtoList.size(); j++) {
                        String nameStaff = "";
                        if (detailDtoList.get(j).getEmployee() != null && detailDtoList.get(j).getEmployee().getDisplayName() != null) {
                            nameStaff = detailDtoList.get(j).getEmployee().getDisplayName();
                        }
                        String start = "";
                        if (detailDtoList.get(j).getStartTime() != null) {
                            start = formatDate.format(detailDtoList.get(j).getStartTime());
                        }
                        String end = "";
                        if (detailDtoList.get(j).getStartTime() != null) {
                            end = formatDate.format(detailDtoList.get(j).getEndTime());
                        }
                        if (detailDtoList.get(j).getWorkingItemTitle() == null) {
                            detailDtoList.get(j).setWorkingItemTitle("");
                        }
                        cellData = tableDataRow.createCell(11);
                        cellData.setCellValue(detailDtoList.get(j).getWorkingItemTitle());
                        cellData.setCellStyle(tableDataRowStyle);

                        cellData = tableDataRow.createCell(12);
                        cellData.setCellValue(start);
                        cellData.setCellStyle(tableDataRowStyle);

                        cellData = tableDataRow.createCell(13);
                        cellData.setCellValue(end);
                        cellData.setCellStyle(tableDataRowStyle);

                        cellData = tableDataRow.createCell(14);
                        cellData.setCellValue(detailDtoList.get(j).getDuration());
                        cellData.setCellStyle(tableDataRowStyle);

                        cellData = tableDataRow.createCell(15);
                        cellData.setCellValue(nameStaff);
                        cellData.setCellStyle(tableDataRowStyle);

                        if (j == detailDtoList.size() - 1) {
                            break;
                        }
                        increaseRow++;
                        tableDataRow = sheet.createRow(increaseRow);
                    }
                }
                increaseRow++;
            }
            for (int i = 1; i <= 15; i++) {
//                sheet.setColumnWidth(i, 20);
                sheet.setColumnWidth(i, 22 * 256);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            return new ByteArrayResource(out.toByteArray());
        }
        return null;
    }

    public static ByteArrayResource exportTimeSheetDetailToExcelTable(List<TimeSheetDetailDto> dataList, Boolean isCheck) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        XSSFFont fontBold = workbook.createFont();
        fontBold.setBold(true);
        fontBold.setFontHeight(10);

        XSSFFont fontBoldTitle = workbook.createFont();
        fontBoldTitle.setBold(true);
        fontBoldTitle.setFontHeight(11);

        XSSFCellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setFont(fontBoldTitle);

        XSSFCellStyle tableHeadCellStyle = workbook.createCellStyle();
        tableHeadCellStyle.setFont(fontBoldTitle);
        tableHeadCellStyle.setAlignment(HorizontalAlignment.CENTER);
        tableHeadCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFRow row = sheet.createRow(0);
        XSSFCell cell;

        cell = row.createCell(0);
        cell.setCellValue("STT");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Dự án");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Công việc");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Ngày làm việc");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Thời gian bắt đầu");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(5);
        cell.setCellValue("Thời gian kết thúc");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(6);
        cell.setCellValue("Tổng thời gian");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(7);
        cell.setCellValue("Mức độ ưu tiên");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(8);
        cell.setCellValue("Người thực hiện");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(9);
        cell.setCellValue("Trạng thái");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(10);
        cell.setCellValue("Mô tả");
        cell.setCellStyle(tableHeadCellStyle);
        for (int col = 0; col <= 10; col++) {
            sheet.addMergedRegion(new CellRangeAddress(0, 1, col, col));
        }


        XSSFCellStyle tableDataRowStyle = workbook.createCellStyle();
        tableDataRowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        tableDataRowStyle.setAlignment(HorizontalAlignment.CENTER);
        XSSFRow tableDataRow;

        if (dataList != null && !dataList.isEmpty()) {
            SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm dd-MM-yyyy");
            SimpleDateFormat formatDateWork = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat formatDateWorkNew = new SimpleDateFormat("HH:mm");
            int increaseRow = 2;
            for (int i = 0; i < dataList.size(); i++) {
                XSSFCell cellData;
                TimeSheetDetailDto data = dataList.get(i);
                tableDataRow = sheet.createRow(increaseRow);

                Date workingDate = new Date();
                int hourStart = DateTimeUtil.getHours(data.getStartTime());
                int minuteStart = DateTimeUtil.getMinutes(data.getStartTime());
                int hourEnd = DateTimeUtil.getHours(data.getEndTime());
                int minuteEnd = DateTimeUtil.getMinutes(data.getEndTime());
                Date startTime = DateTimeUtil.setHourAndMinute(workingDate, hourStart, minuteStart);
                Date endTime = DateTimeUtil.setHourAndMinute(workingDate, hourEnd, minuteEnd);

                cellData = tableDataRow.createCell(0);
                cellData.setCellValue(i + 1);
                cellData.setCellStyle(tableDataRowStyle);

                if (data.getProject() != null) {
                    cellData = tableDataRow.createCell(1);
                    cellData.setCellValue(data.getProject().getName());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getProjectActivity() != null) {
                    cellData = tableDataRow.createCell(2);
                    cellData.setCellValue(data.getProjectActivity().getName());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getTimeSheet().getWorkingDate() != null) {
                    cellData = tableDataRow.createCell(3);
                    cellData.setCellValue(formatDateWork.format(data.getTimeSheet().getWorkingDate()));
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getStartTime() != null) {
                    cellData = tableDataRow.createCell(4);
                    cellData.setCellValue(formatDateWorkNew.format(startTime));
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getEndTime() != null) {
                    cellData = tableDataRow.createCell(5);
                    cellData.setCellValue(formatDateWorkNew.format(endTime));
                    cellData.setCellStyle(tableDataRowStyle);
                }


                if (data.getEndTime() != null && data.getStartTime() != null) {
                    cellData = tableDataRow.createCell(6);
                    Double TotalHour = (double) Math.round(DateTimeUtil.hoursDifference(startTime, endTime) * 10) / 10;
                    cellData.setCellValue(TotalHour);
                    cellData.setCellStyle(tableDataRowStyle);
                }


                if (data.getPriority() != null) {
                    if (data.getPriority() == 1) {
                        cellData = tableDataRow.createCell(7);
                        cellData.setCellValue("Thấp");
                        cellData.setCellStyle(tableDataRowStyle);
                    }
                    if (data.getPriority() == 2) {
                        cellData = tableDataRow.createCell(7);
                        cellData.setCellValue("Trung bình");
                        cellData.setCellStyle(tableDataRowStyle);
                    }
                    if (data.getPriority() == 3) {
                        cellData = tableDataRow.createCell(7);
                        cellData.setCellValue("Cao");
                        cellData.setCellStyle(tableDataRowStyle);
                    }
                    if (data.getPriority() == 4) {
                        cellData = tableDataRow.createCell(7);
                        cellData.setCellValue("Cấp bách");
                        cellData.setCellStyle(tableDataRowStyle);
                    }

                }
                if (data.getEmployee() != null) {
                    cellData = tableDataRow.createCell(8);
                    cellData.setCellValue(data.getEmployee().getDisplayName());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getWorkingStatus() != null) {
                    cellData = tableDataRow.createCell(9);
                    cellData.setCellValue(data.getWorkingStatus().getName());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getDescription() != null) {
                    cellData = tableDataRow.createCell(10);
                    cellData.setCellValue(data.getDescription());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                increaseRow++;
            }
            for (int i = 1; i <= 11; i++) {
//                sheet.setColumnWidth(i, 20);
                sheet.setColumnWidth(i, 22 * 256);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            return new ByteArrayResource(out.toByteArray());
        }
        return null;
    }

    public static ByteArrayResource exportStaffToExcelTable(List<StaffDto> dataList, Boolean isCheck) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        XSSFFont fontBold = workbook.createFont();
        fontBold.setBold(true);
        fontBold.setFontHeight(10);

        XSSFFont fontBoldTitle = workbook.createFont();
        fontBoldTitle.setBold(true);
        fontBoldTitle.setFontHeight(11);

        XSSFCellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setFont(fontBoldTitle);

        XSSFCellStyle tableHeadCellStyle = workbook.createCellStyle();
        tableHeadCellStyle.setFont(fontBoldTitle);
        tableHeadCellStyle.setAlignment(HorizontalAlignment.CENTER);
        tableHeadCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFRow row = sheet.createRow(0);
        XSSFCell cell;

        cell = row.createCell(0);
        cell.setCellValue("STT");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Mã nhân viên");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Tên nhân viên");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Ngày sinh");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Giới tính");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(5);
        cell.setCellValue("Số điện thoại");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(6);
        cell.setCellValue("Địa chỉ Email");
        cell.setCellStyle(tableHeadCellStyle);
        for (int col = 0; col <= 6; col++) {
            sheet.addMergedRegion(new CellRangeAddress(0, 1, col, col));
        }

        XSSFCellStyle tableDataRowStyle = workbook.createCellStyle();
        tableDataRowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        tableDataRowStyle.setAlignment(HorizontalAlignment.CENTER);
        XSSFRow tableDataRow;

        if (dataList != null && !dataList.isEmpty()) {
            SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm dd-MM-yyyy");
            SimpleDateFormat formatDateWork = new SimpleDateFormat("dd-MM-yyyy");
            int increaseRow = 2;
            for (int i = 0; i < dataList.size(); i++) {
                XSSFCell cellData;
                StaffDto data = dataList.get(i);
                tableDataRow = sheet.createRow(increaseRow);

                cellData = tableDataRow.createCell(0);
                cellData.setCellValue(i + 1);
                cellData.setCellStyle(tableDataRowStyle);

                if (data.getStaffCode() != null) {
                    cellData = tableDataRow.createCell(1);
                    cellData.setCellValue(data.getStaffCode());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getDisplayName() != null) {
                    cellData = tableDataRow.createCell(2);
                    cellData.setCellValue(data.getDisplayName());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getBirthDate() != null) {
                    cellData = tableDataRow.createCell(3);
                    cellData.setCellValue(formatDate.format(data.getBirthDate()));
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getGender() != null) {
                    cellData = tableDataRow.createCell(4);
                    cellData.setCellValue(data.getGender());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getPhoneNumber() != null) {
                    cellData = tableDataRow.createCell(5);
                    cellData.setCellValue(data.getPhoneNumber());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getEmail() != null) {
                    cellData = tableDataRow.createCell(6);
                    cellData.setCellValue(data.getEmail());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                increaseRow++;
            }
            for (int i = 1; i <= 6; i++) {
//                sheet.setColumnWidth(i, 20);
                sheet.setColumnWidth(i, 22 * 256);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            return new ByteArrayResource(out.toByteArray());
        }
        return null;
    }

    public static ByteArrayResource exportTimeSheetOfAllStaffToExcelTable(List<StaffDto> staffList, List<ShiftWorkTimePeriodDto> shiftWorkTimePeriodList, List<Date> dateList, List<TimeSheetDto> timeSheetList, Boolean isCheck) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        XSSFFont fontBold = workbook.createFont();
        fontBold.setBold(true);
        fontBold.setFontHeight(10);

        XSSFFont fontBoldTitle = workbook.createFont();
        fontBoldTitle.setBold(true);
        fontBoldTitle.setFontHeight(11);

        XSSFCellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setFont(fontBoldTitle);

        XSSFCellStyle tableHeadCellStyle = workbook.createCellStyle();
        tableHeadCellStyle.setFont(fontBoldTitle);
        tableHeadCellStyle.setAlignment(HorizontalAlignment.CENTER);
        tableHeadCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFRow row = sheet.createRow(0);
        XSSFCell cell;

        int colStaff = 2;
        if (staffList != null && staffList.size() > 0) {
            for (int i = 0; i < staffList.size(); i++) {
                cell = row.createCell(colStaff);
                cell.setCellValue(staffList.get(i).getFirstName() + " " + staffList.get(i).getLastName());
                cell.setCellStyle(tableHeadCellStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, colStaff, colStaff + shiftWorkTimePeriodList.size() - 1));
                colStaff += shiftWorkTimePeriodList.size();
            }
        }
        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("Date");
        cell.setCellStyle(tableHeadCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
        if (staffList != null && staffList.size() > 0 && shiftWorkTimePeriodList != null && shiftWorkTimePeriodList.size() > 0) {

            int colShiftWorkTimePeriod = 2;
            SimpleDateFormat formatHour = new SimpleDateFormat("hh:mm");
            for (int i = 0; i < staffList.size(); i++) {
                for (int j = 0; j < shiftWorkTimePeriodList.size(); j++) {
                    cell = row.createCell(colShiftWorkTimePeriod);
                    cell.setCellValue(shiftWorkTimePeriodList.get(j).getShiftWorkDto().getName() + " : " + formatHour.format(shiftWorkTimePeriodList.get(j).getStartTime()) + "-" + formatHour.format(shiftWorkTimePeriodList.get(j).getEndTime()));
                    cell.setCellStyle(tableHeadCellStyle);
                    colShiftWorkTimePeriod++;
                }
            }
        }

        XSSFCellStyle tableDataRowStyle = workbook.createCellStyle();
        tableDataRowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        tableDataRowStyle.setAlignment(HorizontalAlignment.CENTER);
        XSSFRow tableDataRow;

        if (staffList != null && !staffList.isEmpty() && shiftWorkTimePeriodList != null && !shiftWorkTimePeriodList.isEmpty() && dateList != null && !dateList.isEmpty() && timeSheetList != null && !timeSheetList.isEmpty()) {
            SimpleDateFormat formatDateWork = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat formatDay = new SimpleDateFormat("E");
            int increaseRow = 2;
            for (int i = 0; i < dateList.size(); i++) {
                int increaseCell = 2;

                XSSFCell cellData;
                Date dataDate = dateList.get(i);
                tableDataRow = sheet.createRow(increaseRow);

                cellData = tableDataRow.createCell(0);
                cellData.setCellValue(formatDateWork.format(dataDate));
                cellData.setCellStyle(tableDataRowStyle);

                cellData = tableDataRow.createCell(1);
                cellData.setCellValue(formatDay.format(dataDate));
                cellData.setCellStyle(tableDataRowStyle);

                if (dataDate != null) {
                    for (int j = 0; j < staffList.size(); j++) {
                        StaffDto staff = staffList.get(j);
                        if (staff != null) {
                            for (int h = 0; h < shiftWorkTimePeriodList.size(); h++) {
                                ShiftWorkTimePeriodDto shiftWorkTimePeriod = shiftWorkTimePeriodList.get(h);
                                if (shiftWorkTimePeriod != null) {
                                    for (int k = 0; k < timeSheetList.size(); k++) {
                                        TimeSheetDto timeSheet = timeSheetList.get(k);
                                        if (timeSheet != null && timeSheet.getStaff().getId() == staff.getId() && dataDate.getDate() == timeSheet.getWorkingDate().getDate() && timeSheet.getStaff().getId() == staffList.get(j).getId()) {
                                            List<TimeSheetShiftWorkPeriodDto> timeSheetShiftWorkPeriodList = timeSheet.getTimeSheetShiftWorkPeriod();
                                            if (timeSheetShiftWorkPeriodList != null && !timeSheetShiftWorkPeriodList.isEmpty()) {
                                                for (int l = 0; l < timeSheetShiftWorkPeriodList.size(); l++) {
                                                    if (shiftWorkTimePeriodList.get(l) != null && shiftWorkTimePeriodList.get(l).getId() != null && shiftWorkTimePeriodList.get(l).getId() == shiftWorkTimePeriod.getId()) {
                                                        if (timeSheetShiftWorkPeriodList.get(l).getWorkingFormat() == -1) {
                                                            cellData = tableDataRow.createCell(increaseCell);
                                                            cellData.setCellValue("Nghỉ");
                                                            cellData.setCellStyle(tableDataRowStyle);
                                                        }
                                                        if (timeSheetShiftWorkPeriodList.get(l).getWorkingFormat() == 0) {
                                                            cellData = tableDataRow.createCell(increaseCell);
                                                            cellData.setCellValue("Đi làm");
                                                            cellData.setCellStyle(tableDataRowStyle);
                                                        }
                                                        if (timeSheetShiftWorkPeriodList.get(l).getWorkingFormat() == 1) {
                                                            cellData = tableDataRow.createCell(increaseCell);
                                                            cellData.setCellValue("Làm online");
                                                            cellData.setCellStyle(tableDataRowStyle);
                                                        }
                                                        if (timeSheetShiftWorkPeriodList.get(l).getWorkingFormat() == 2) {
                                                            cellData = tableDataRow.createCell(increaseCell);
                                                            cellData.setCellValue("Đi công tác");
                                                            cellData.setCellStyle(tableDataRowStyle);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                increaseCell++;
                            }
                        }
                    }
                }
                increaseRow++;
            }
            for (int i = 0; i < dateList.size() + 2; i++) {
                sheet.setColumnWidth(i, 22 * 256);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            return new ByteArrayResource(out.toByteArray());
        }
        return null;
    }

    public static ByteArrayResource handleExcelStaffLabourAgreements(List<StaffLabourAgreementDto> datas, SearchStaffLabourAgreementDto searchDto, XSSFWorkbook workbook) throws IOException {
        if (datas == null || datas.isEmpty()) return null;
        XSSFSheet sheet = workbook.getSheetAt(0);
        Font font = workbook.getFontAt((short) 0);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        XSSFCellStyle stringStyle = workbook.createCellStyle();
        stringStyle.setFont(font);
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);
        cellStyle.setFont(font);
        XSSFRow row = sheet.getRow(3);
        if (row == null) {
            row = sheet.createRow(3); // Tạo mới nếu dòng không tồn tại
        }

        XSSFCell cell = row.getCell(2); // Cột C = index 2
        if (cell == null) {
            cell = row.createCell(2); // Tạo ô nếu chưa tồn tại
        }
        String exportType = "";
        if (searchDto == null || searchDto.getExportType() == null) return null;
        int type = searchDto.getExportType();
        if (type == HrConstants.StaffHasSocialInsuranceExportType.INCREASE_2007.getValue()) {
            exportType = HrConstants.StaffHasSocialInsuranceExportType.INCREASE_2007.getName();
        } else if (type == HrConstants.StaffHasSocialInsuranceExportType.INCREASE_97_2003.getValue()) {
            exportType = HrConstants.StaffHasSocialInsuranceExportType.INCREASE_97_2003.getName();
        } else if (type == HrConstants.StaffHasSocialInsuranceExportType.DECREASE_2007.getValue()) {
            exportType = HrConstants.StaffHasSocialInsuranceExportType.DECREASE_2007.getName();
        } else if (type == HrConstants.StaffHasSocialInsuranceExportType.DECREASE_97_2003.getValue()) {
            exportType = HrConstants.StaffHasSocialInsuranceExportType.DECREASE_97_2003.getName();
        } else if (type == HrConstants.StaffHasSocialInsuranceExportType.MODIFY_2007.getValue()) {
            exportType = HrConstants.StaffHasSocialInsuranceExportType.MODIFY_2007.getName();
        } else if (type == HrConstants.StaffHasSocialInsuranceExportType.MODIFY_97_2003.getValue()) {
            exportType = HrConstants.StaffHasSocialInsuranceExportType.MODIFY_97_2003.getName();
        }
        cell.setCellValue(exportType); // Đặt giá trị cho ô hợp nhất

        List<List<Object>> tempData = new ArrayList<>();
        for (int rowIndex = 17; rowIndex < 24; rowIndex++) {
            XSSFRow rowTemp = sheet.getRow(rowIndex);
            List<Object> rowData = new ArrayList<>();
            for (int cn = 0; cn < rowTemp.getLastCellNum(); cn++) {
                XSSFCell cellTemp = rowTemp.getCell(cn);
                Map<String, Object> cellInfo = new HashMap<>();
                cellInfo.put("value", cellTemp.toString());
                cellInfo.put("style", cellTemp.getCellStyle());
                rowData.add(cellInfo);
            }
            tempData.add(rowData);
        }

        for (int i = 17; i < 24; i++) {
            XSSFRow removingRow = sheet.getRow(i);
            if (removingRow != null) {
                sheet.removeRow(removingRow);
            }
        }

        int rowIndex = 6;
        int index = 0;

        for (StaffLabourAgreementDto item : datas) {
            if (index > 0) {
                ++rowIndex;
            }

            ++index;
            row = sheet.createRow(rowIndex);
            sheet.getRow(rowIndex + 1);
            if (row != null) {
                //STT
                cell = row.createCell(0);
                cell.setCellStyle(cellStyle);
                cell.setCellValue((double) index);
                //LoaiD02
                cell = row.createCell(1);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(exportType);
                //HoTen
                cell = row.createCell(2);
                cell.setCellStyle(cellStyle);
                if (item.getStaff() != null && item.getStaff().getDisplayName() != null) {
                    cell.setCellValue(item.getStaff().getDisplayName());
                } else {
                    cell.setCellValue("");
                }

//                MaSoBHXH
                cell = row.createCell(3);
                cell.setCellStyle(cellStyle);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
                if (item.getStaff() != null && item.getStaff().getContractNumber() != null) {
                    cell.setCellValue(item.getStaff().getContractNumber());
                } else {
                    cell.setCellValue("");
                }

//                SoSoBHXH
                cell = row.createCell(4);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
                if (item.getStaff() != null && item.getStaff().getSocialInsuranceNumber() != null) {
                    cell.setCellValue(item.getStaff().getSocialInsuranceNumber());
                } else {
                    cell.setCellValue("");
                }

//                SoLanKeKhai
                cell = row.createCell(5);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }

                //ThangNamKeKhai
                cell = row.createCell(6);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }

                //NguoiLapBieu
                cell = row.createCell(7);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //ThuTruongDonVi
                cell = row.createCell(8);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //TongSoBHXH
                cell = row.createCell(9);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //TongTheBHYT
                cell = row.createCell(10);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //ChucVu
                cell = row.createCell(11);
                cell.setCellStyle(cellStyle);
                if (item.getStaff() != null && item.getStaff().getCurrentPosition() != null && item.getStaff().getCurrentPosition().getName() != null) {
                    cell.setCellValue(item.getStaff().getCurrentPosition().getName());
                } else {
                    cell.setCellValue("");
                }
                //Muc/HeSoLuong
                cell = row.createCell(12);
                cell.setCellStyle(cellStyle);
                if (item.getStaff() != null && item.getStaff().getInsuranceSalaryCoefficient() != null) {
                    cell.setCellValue(item.getStaff().getInsuranceSalaryCoefficient());
                } else {
                    cell.setCellValue("");
                }
                //PCChucVu
                cell = row.createCell(13);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //PCThamNienVK
                cell = row.createCell(14);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //PCThamNienNghe
                cell = row.createCell(15);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //PCLuong
                cell = row.createCell(16);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //PCCacKhoanBS
                cell = row.createCell(17);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //TuThangNam
                cell = row.createCell(18);
                cell.setCellStyle(cellStyle);
                if (item.getStaff() != null && item.getStaff().getInsuranceStartDate() != null) {
                    cell.setCellValue(formatter.format(item.getStaff().getInsuranceStartDate()));
                } else {
                    cell.setCellValue("");
                }
                //DenThangNam
                cell = row.createCell(19);
                cell.setCellStyle(cellStyle);
                if (item.getStaff() != null && item.getStaff().getInsuranceEndDate() != null) {
                    cell.setCellValue(formatter.format(item.getStaff().getInsuranceEndDate()));
                } else {
                    cell.setCellValue("");
                }
                //PhuongAn
                cell = row.createCell(20);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //GhiChu
                cell = row.createCell(21);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //TyLeDong
                cell = row.createCell(22);
                cell.setCellStyle(cellStyle);
                if (item.getStaffSocialInsurancePercentage() != null) {
                    cell.setCellValue(item.getStaffSocialInsurancePercentage());
                } else {
                    cell.setCellValue("");
                }
                //PhongBanLamViec
                cell = row.createCell(23);
                cell.setCellStyle(cellStyle);
                if (item.getStaff() != null && item.getStaff().getDepartment() != null && item.getStaff().getDepartment().getName() != null) {
                    cell.setCellValue(item.getStaff().getDepartment().getName());
                } else {
                    cell.setCellValue("");
                }
                //NoiLamViec
                cell = row.createCell(24);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
                if (item.getWorkingPlace() != null) {
                    cell.setCellValue(item.getWorkingPlace());
                } else {
                    cell.setCellValue("");
                }
                //MaVungSS
                cell = row.createCell(25);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //MaLuongTT
                cell = row.createCell(26);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //LoaiVTLV
                cell = row.createCell(27);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //NgayBatDauVTLV
                cell = row.createCell(28);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //NgayKetThucVTLV
                cell = row.createCell(29);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }

                //LoaiHDLD
                cell = row.createCell(30);
                cell.setCellStyle(cellStyle);
                if (item.getLabourAgreementType() != null && item.getLabourAgreementType().getName() != null) {
                    cell.setCellValue(item.getLabourAgreementType().getName());
                } else {
                    cell.setCellValue("");
                }
                //NgayBatDauHDLD
                cell = row.createCell(31);
                cell.setCellStyle(cellStyle);
                if (item.getStartDate() != null) {
                    cell.setCellValue(formatter.format(item.getStartDate()));
                } else {
                    cell.setCellValue("");
                }
                //NgayKetThucHDLD
                cell = row.createCell(32);
                cell.setCellStyle(cellStyle);
                if (item.getEndDate() != null) {
                    cell.setCellValue(formatter.format(item.getEndDate()));
                } else {
                    cell.setCellValue("");
                }
                //NgayBatDauNNDH
                cell = row.createCell(33);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //NgayKetThucNNDH
                cell = row.createCell(34);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //NgaySinh
                cell = row.createCell(35);
                cell.setCellStyle(cellStyle);
                if (item.getStaff() != null && item.getStaff().getBirthDate() != null) {
                    cell.setCellValue(formatter.format(item.getStaff().getBirthDate()));
                } else {
                    cell.setCellValue("");
                }
                //GioiTinh
                cell = row.createCell(36);
                cell.setCellStyle(cellStyle);
                if (item.getStaff() != null && item.getStaff().getGender() != null) {
                    if (item.getStaff().getGender().equals(Const.GENDER_ENUM.FEMALE.getValue())) {
                        cell.setCellValue(Const.GENDER_ENUM.FEMALE.getDisplay());
                    } else {
                        cell.setCellValue(Const.GENDER_ENUM.MALE.getDisplay());
                    }
                } else {
                    cell.setCellValue("");
                }
                //QuocTich
                cell = row.createCell(37);
                cell.setCellStyle(cellStyle);
                if (item.getStaff() != null && item.getStaff().getNationality() != null && item.getStaff().getNationality().getName() != null) {
                    cell.setCellValue(item.getStaff().getNationality().getName());
                } else {
                    cell.setCellValue("");
                }

                //DanToc
                cell = row.createCell(38);
                cell.setCellStyle(cellStyle);
                if (item.getStaff() != null && item.getStaff().getEthnics() != null && item.getStaff().getEthnics().getName() != null) {
                    cell.setCellValue(item.getStaff().getEthnics().getName());
                } else {
                    cell.setCellValue("");
                }
                //GKSTinh/TP
                cell = row.createCell(39);
                cell.setCellStyle(cellStyle);
                if (item.getStaff() != null && item.getStaff().getBirthPlace() != null) {
                    cell.setCellValue(item.getStaff().getBirthPlace());
                } else {
                    cell.setCellValue("");
                }
                //GKSHuyen/Quan
                cell = row.createCell(40);
                cell.setCellStyle(cellStyle);
                if (item.getStaff() != null && item.getStaff().getBirthPlace() != null) {
                    cell.setCellValue(item.getStaff().getBirthPlace());
                } else {
                    cell.setCellValue("");
                }
                //GKSXa/Phuong
                cell = row.createCell(41);
                cell.setCellStyle(cellStyle);
                if (item.getStaff() != null && item.getStaff().getBirthPlace() != null) {
                    cell.setCellValue(item.getStaff().getBirthPlace());
                } else {
                    cell.setCellValue("");
                }
                //SoCMND
                cell = row.createCell(42);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
                if (item.getStaff() != null && item.getStaff().getIdNumber() != null) {
                    cell.setCellValue(item.getStaff().getIdNumber());
                } else {
                    cell.setCellValue("");
                }
                //LHSoNha
                cell = row.createCell(43);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //LHTinh/TP
                cell = row.createCell(44);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //LHHuyen/Quan
                cell = row.createCell(45);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //LHXa/Phuong
                cell = row.createCell(46);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //SoDienThoaiLH
                cell = row.createCell(47);
                cell.setCellStyle(cellStyle);
                if (item.getStaff() != null && item.getStaff().getPhoneNumber() != null) {
                    cell.setCellValue(item.getStaff().getPhoneNumber());
                } else {
                    cell.setCellValue("");
                }
                //MucTienDong
                cell = row.createCell(48);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
                if (item.getTotalInsuranceAmount() != null) {
                    cell.setCellValue(item.getTotalInsuranceAmount());
                } else {
                    cell.setCellValue("");
                }
                //PhuongThucDong
                cell = row.createCell(49);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //MaNoiKCB
                cell = row.createCell(50);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //NDThayDoiYeuCau
                cell = row.createCell(51);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //TLKemTheo
                cell = row.createCell(52);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //NDLapBangKe
                cell = row.createCell(53);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //TKKemTheo
                cell = row.createCell(54);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //TenLoaiVanBan
                cell = row.createCell(55);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //SoHieuVanBan
                cell = row.createCell(56);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //NgayBanHanh
                cell = row.createCell(57);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //NgayHieuLuc
                cell = row.createCell(58);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //CQBanHanhVanBan
                cell = row.createCell(59);
                cell.setCellStyle(cellStyle);
                if (item.getStaff() != null && item.getStaff().getDepartment() != null && item.getStaff().getDepartment().getOrganization() != null && item.getStaff().getDepartment().getOrganization().getName() != null) {
                    cell.setCellValue(item.getStaff().getDepartment().getOrganization().getName());
                } else {
                    cell.setCellValue("");
                }
                //TrichYeuVanBan
                cell = row.createCell(60);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("");
//                if () {
//                    cell.setCellValue();
//                } else {
//                    cell.setCellValue("");
//                }
                //TrichLuocNoiDungCanThamDinh
                cell = row.createCell(61);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(exportType);
            }
        }
        rowIndex += 1;
        for (List<Object> rowData : tempData) {
            row = sheet.createRow(++rowIndex);
            for (int i = 0; i < rowData.size(); i++) {
                Map<String, Object> cellInfo = (Map<String, Object>) rowData.get(i);
                cell = row.createCell(i);
                cell.setCellValue(cellInfo.get("value").toString());
                cell.setCellStyle((XSSFCellStyle) cellInfo.get("style"));
            }
        }


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayResource(out.toByteArray());
    }

    public static ByteArrayResource handleExportActualTimesheet(List<StaffWorkScheduleDto> datas, SearchStaffWorkScheduleDto dto, List<LeaveRequestDto> leaveRequests, XSSFWorkbook workbook) throws IOException {
        if (datas == null || datas.isEmpty() || dto.getFromDate() == null || dto.getToDate() == null) return null;
        XSSFSheet sheet = workbook.getSheetAt(0);
        Font font = workbook.getFontAt((short) 0);
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 11);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatterHourse = new SimpleDateFormat("HH:mm dd/MM/yyyy");

        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));

        XSSFCellStyle cellStyleCenter = workbook.createCellStyle();
        cellStyleCenter.setBorderBottom(BorderStyle.THIN);
        cellStyleCenter.setBorderTop(BorderStyle.THIN);
        cellStyleCenter.setBorderLeft(BorderStyle.THIN);
        cellStyleCenter.setBorderRight(BorderStyle.THIN);
        cellStyleCenter.setAlignment(HorizontalAlignment.CENTER);
        cellStyleCenter.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleCenter.setWrapText(true);
        cellStyleCenter.setFont(font);

        XSSFCellStyle cellStyleLeft = workbook.createCellStyle();
        cellStyleLeft.setBorderBottom(BorderStyle.THIN);
        cellStyleLeft.setBorderTop(BorderStyle.THIN);
        cellStyleLeft.setBorderLeft(BorderStyle.THIN);
        cellStyleLeft.setBorderRight(BorderStyle.THIN);
        cellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
        cellStyleLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleLeft.setWrapText(true);
        cellStyleLeft.setFont(font);

        XSSFCellStyle cellStyleRight = workbook.createCellStyle();
        cellStyleRight.setBorderBottom(BorderStyle.THIN);
        cellStyleRight.setBorderTop(BorderStyle.THIN);
        cellStyleRight.setBorderLeft(BorderStyle.THIN);
        cellStyleRight.setBorderRight(BorderStyle.THIN);
        cellStyleRight.setAlignment(HorizontalAlignment.RIGHT);
        cellStyleRight.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleRight.setWrapText(true);
        cellStyleRight.setFont(font);

        XSSFRow row = null;

        XSSFCell cell = null;

        //Điền dữ liệu vào bảng excel
        int rowIndex = 1;
        int index = 0;
        Map<UUID, Map<Date, List<StaffWorkScheduleDto>>> groupedData = datas.stream().collect(Collectors.groupingBy(s -> s.getStaff().getId(), Collectors.groupingBy(StaffWorkScheduleDto::getWorkingDate)));
        Map<Pair<UUID, String>, List<LeaveRequestDto>> groupedLeaveRequests = leaveRequests.stream()
                .filter(item -> item.getRequestStaff() != null && item.getRequestStaff().getId() != null
                        && item.getLeaveType() != null && item.getLeaveType().getCode() != null)
                .collect(Collectors.groupingBy(item ->
                        Pair.of(item.getRequestStaff().getId(), item.getLeaveType().getCode())
                ));


        for (Map.Entry<UUID, Map<Date, List<StaffWorkScheduleDto>>> staffEntry : groupedData.entrySet()) {
            Map<Date, List<StaffWorkScheduleDto>> dateMap = staffEntry.getValue();

            StaffDto staffDto = null;
            int lateArrivalMinutes = 0; // Số phút đi muộn
            int earlyExitMinutes = 0; // Số phút về sớm
            double overtimeHours = 0.0;  // Thời gian đăng ký làm thêm
            double totalPaidWork = 0.0; //số ngày công đi làm được tính
            double annualLeave = 0; // Nghỉ phép
            double businessTrip = 0; //Nghỉ công tác
            double unpaidLeave = 0; // Nghỉ không lương
            double publicHoliday = 0; // Nghỉ lễ
            double compensatoryLeave = 0; // Nghỉ bù
            double specialLeave = 0; // Nghỉ chế độ

            for (Map.Entry<Date, List<StaffWorkScheduleDto>> dateEntry : dateMap.entrySet()) {
                List<StaffWorkScheduleDto> scheduleList = dateEntry.getValue();
                if (!scheduleList.isEmpty() && staffDto == null) {
                    staffDto = scheduleList.get(0).getStaff();
                }

                for (StaffWorkScheduleDto item : scheduleList) {
                    if (item == null) {
                        continue;
                    }


                    if (item.getLateArrivalMinutes() != null) {
                        lateArrivalMinutes += item.getLateArrivalMinutes();
                    }
                    if (item.getEarlyExitMinutes() != null) {
                        earlyExitMinutes += item.getEarlyExitMinutes();
                    }

                    if (item.getTotalPaidWork() != null) {
                        totalPaidWork += item.getTotalPaidWork();
                    }
                    if (item.getShiftWorks() != null && !item.getShiftWorks().isEmpty()) {
                        for (ShiftWorkDto sw : item.getShiftWorks()) {
                            if (sw.getShiftWorkType() == HrConstants.ShiftWorkType.OVERTIME.getValue()) {
                                overtimeHours = sw.getTotalHours();
                            }
                        }
                    }

                    if (item.getLeaveType() != null && item.getLeaveType().getCode() != null) {
                        String leaveTypeCode = item.getLeaveType().getCode();
                        if (HrConstants.LeaveTypeCode.ANNUAL_LEAVE.getCode().equals(leaveTypeCode)) {
                            annualLeave += calculateLeaveDays(groupedLeaveRequests, staffDto.getId(), HrConstants.LeaveTypeCode.ANNUAL_LEAVE.getCode(), item.getWorkingDate());
                        } else if (HrConstants.LeaveTypeCode.BUSINESS_TRIP.getCode().equals(leaveTypeCode)) {
                            businessTrip += calculateLeaveDays(groupedLeaveRequests, staffDto.getId(), HrConstants.LeaveTypeCode.BUSINESS_TRIP.getCode(), item.getWorkingDate());
                        } else if (HrConstants.LeaveTypeCode.UNPAID_LEAVE.getCode().equals(leaveTypeCode)) {
                            unpaidLeave += calculateLeaveDays(groupedLeaveRequests, staffDto.getId(), HrConstants.LeaveTypeCode.UNPAID_LEAVE.getCode(), item.getWorkingDate());
                        } else if (HrConstants.LeaveTypeCode.PUBLIC_HOLIDAY.getCode().equals(leaveTypeCode)) {
                            publicHoliday += calculateLeaveDays(groupedLeaveRequests, staffDto.getId(), HrConstants.LeaveTypeCode.PUBLIC_HOLIDAY.getCode(), item.getWorkingDate());
                        } else if (HrConstants.LeaveTypeCode.COMPENSATORY_LEAVE.getCode().equals(leaveTypeCode)) {
                            compensatoryLeave += calculateLeaveDays(groupedLeaveRequests, staffDto.getId(), HrConstants.LeaveTypeCode.COMPENSATORY_LEAVE.getCode(), item.getWorkingDate());
                        } else if (HrConstants.LeaveTypeCode.SPECIAL_LEAVE.getCode().equals(leaveTypeCode)) {
                            specialLeave += calculateLeaveDays(groupedLeaveRequests, staffDto.getId(), HrConstants.LeaveTypeCode.SPECIAL_LEAVE.getCode(), item.getWorkingDate());
                        }
                    }
                }
            }
            if (index > 0) {
                ++rowIndex;
            }

            ++index;
            row = sheet.createRow(rowIndex);
            sheet.getRow(rowIndex + 1);
            if (row != null && staffDto != null) {
                //STT
                cell = row.createCell(0);
                cell.setCellStyle(cellStyleCenter);
                cell.setCellValue((double) index);

                //Mã nhân viên
                cell = row.createCell(1);
                cell.setCellStyle(cellStyleLeft);
                if (staffDto.getStaffCode() != null) {
                    cell.setCellValue(staffDto.getStaffCode());
                } else {
                    cell.setCellValue("");
                }

                //Tên nhân viên
                cell = row.createCell(2);
                cell.setCellStyle(cellStyleLeft);
                if (staffDto.getDisplayName() != null) {
                    cell.setCellValue(staffDto.getDisplayName());
                } else {
                    cell.setCellValue("");
                }

                //Phòng ban
                cell = row.createCell(3);
                cell.setCellStyle(cellStyleLeft);
                if (staffDto.getDepartment() != null && staffDto.getDepartment().getName() != null) {
                    cell.setCellValue(staffDto.getDepartment().getName());
                } else {
                    cell.setCellValue("");
                }

                //Chức danh
                cell = row.createCell(4);
                cell.setCellStyle(cellStyleLeft);
                if (staffDto.getPositionTitle() != null && staffDto.getPositionTitle().getName() != null) {
                    cell.setCellValue(staffDto.getPositionTitle().getName());
                } else {
                    cell.setCellValue("");
                }

                //Ngày công tính lương
                cell = row.createCell(5);
                cell.setCellStyle(cellStyleLeft);
                if (staffDto.getPositionTitle() != null && staffDto.getPositionTitle().getEstimatedWorkingDays() != null) {
                    cell.setCellValue(staffDto.getPositionTitle().getEstimatedWorkingDays());
                } else {
                    cell.setCellValue(0);
                }

                //Giờ vào
                cell = row.createCell(6);
                cell.setCellStyle(cellStyleLeft);
                if (true) {
                    cell.setCellValue("");
                } else {
                    cell.setCellValue("");
                }

                //Giờ ra
                cell = row.createCell(7);
                cell.setCellStyle(cellStyleLeft);
                if (true) {
                    cell.setCellValue("");
                } else {
                    cell.setCellValue("");
                }


                //Công thực tế
                cell = row.createCell(8);
                cell.setCellStyle(cellStyleLeft);
                cell.setCellValue(totalPaidWork);

                //Tăng ca
                cell = row.createCell(9);
                cell.setCellStyle(cellStyleLeft);
                cell.setCellValue(overtimeHours);


                //Trễ/Sớm
                cell = row.createCell(10);
                cell.setCellStyle(cellStyleLeft);
                cell.setCellValue(lateArrivalMinutes + "/" + earlyExitMinutes);

                //Ghi chú
                cell = row.createCell(11);
                cell.setCellStyle(cellStyleLeft);
                if (true) {
                    cell.setCellValue("");
                } else {
                    cell.setCellValue("");
                }
                //Nghỉ phép
                cell = row.createCell(12);
                cell.setCellStyle(cellStyleLeft);
                cell.setCellValue(annualLeave);

                //Công tác, Tự nhập
                cell = row.createCell(13);
                cell.setCellStyle(cellStyleLeft);
                cell.setCellValue(businessTrip);

                //Nghỉ không lương, Tự nhập
                cell = row.createCell(14);
                cell.setCellStyle(cellStyleLeft);
                cell.setCellValue(unpaidLeave);

                //Nghỉ lễ, Tự nhập
                cell = row.createCell(15);
                cell.setCellStyle(cellStyleLeft);
                cell.setCellValue(publicHoliday);

                //Nghỉ bù, Tự nhập
                cell = row.createCell(16);
                cell.setCellStyle(cellStyleLeft);
                cell.setCellValue(compensatoryLeave);

                //Nghỉ chế độ, Tự nhập
                cell = row.createCell(17);
                cell.setCellStyle(cellStyleLeft);
                cell.setCellValue(specialLeave);
            }
        }

        List<String> titles = Arrays.asList("STT", "Mã nhân viên", "Tên nhân viên", "Phòng ban", "Ngày công tính lương", "Thứ", "Giờ vào", "Giờ ra", "Đi làm", "Tăng ca", "Trễ/Sớm", "Ghi chú", "Nghỉ phép(ngày)", "Công tác(ngày)", "Nghỉ không lương(ngày)", "Nghỉ lễ(ngày)", "Nghỉ bù(ngày)", "Nghỉ chế độ(ngày)");

        rowIndex += 2;
        Row firstRow = sheet.getRow(0);
        row = sheet.createRow(rowIndex);
        if (firstRow != null) {
            for (int i = 0; i < titles.size(); i++) {
                Cell firstRowCell = firstRow.getCell(i);
                CellStyle style = null;
                if (firstRowCell != null) {
                    style = firstRowCell.getCellStyle();
                }
                cell = row.createCell(i);
                cell.setCellValue(titles.get(i));
                if (style != null) {
                    cell.setCellStyle(style);
                }
            }
        }

        index = 0;
        rowIndex += 1;

        Map<UUID, List<StaffWorkScheduleDto>> groupedStaff = datas.stream()
                .collect(Collectors.groupingBy(s -> s.getStaff().getId()));

        for (Map.Entry<UUID, List<StaffWorkScheduleDto>> staffEntry : groupedStaff.entrySet()) {
            List<StaffWorkScheduleDto> scheduleList = staffEntry.getValue();
            StaffDto staffDto = null;

            if (!scheduleList.isEmpty()) {
                staffDto = scheduleList.get(0).getStaff();
            }

            if (staffDto != null) {
                for (StaffWorkScheduleDto item : scheduleList) {
                    row = sheet.createRow(rowIndex++);

                    index++;

                    // STT
                    cell = row.createCell(0);
                    cell.setCellStyle(cellStyleCenter);
                    cell.setCellValue((double) (index));

                    // Mã nhân viên
                    cell = row.createCell(1);
                    cell.setCellStyle(cellStyleLeft);
                    cell.setCellValue(staffDto.getStaffCode() != null ? staffDto.getStaffCode() : "");

                    // Tên nhân viên
                    cell = row.createCell(2);
                    cell.setCellStyle(cellStyleLeft);
                    cell.setCellValue(staffDto.getDisplayName() != null ? staffDto.getDisplayName() : "");

                    // Phòng ban
                    cell = row.createCell(3);
                    cell.setCellStyle(cellStyleLeft);
                    cell.setCellValue(
                            staffDto.getDepartment() != null && staffDto.getDepartment().getName() != null
                                    ? staffDto.getDepartment().getName()
                                    : ""
                    );

                    // Ngày công tính lương
                    cell = row.createCell(4);
                    cell.setCellStyle(cellStyleLeft);
                    cell.setCellValue(formatter.format(item.getWorkingDate()));

                    // Thứ
                    cell = row.createCell(5);
                    cell.setCellStyle(cellStyleLeft);
                    cell.setCellValue(dayOfWeekFormat.format(item.getWorkingDate()));

                    if (item != null) {
                        List<TimeSheetDetailDto> timeSheetDetails = item.getTimeSheetDetails();
                        if (!CollectionUtils.isEmpty(timeSheetDetails)) {
                            // Sắp xếp theo giờ bắt đầu tăng dần
                            timeSheetDetails.sort(Comparator.comparing(TimeSheetDetailDto::getStartTime, Comparator.nullsFirst(Comparator.naturalOrder())));
                        }

                        // Giờ vào
                        cell = row.createCell(6);
                        cell.setCellStyle(cellStyleLeft);
                        if (!CollectionUtils.isEmpty(timeSheetDetails) && timeSheetDetails.get(0).getStartTime() != null) {
                            cell.setCellValue(formatterHourse.format(timeSheetDetails.get(0).getStartTime()));
                        } else {
                            cell.setCellValue("");
                        }

                        // Giờ ra
                        cell = row.createCell(7);
                        cell.setCellStyle(cellStyleLeft);
                        if (!CollectionUtils.isEmpty(timeSheetDetails) && timeSheetDetails.get(timeSheetDetails.size() - 1).getEndTime() != null) {
                            cell.setCellValue(formatterHourse.format(timeSheetDetails.get(timeSheetDetails.size() - 1).getEndTime()));
                        } else {
                            cell.setCellValue("");
                        }


                        // Đi làm
                        cell = row.createCell(8);
                        cell.setCellStyle(cellStyleLeft);
                        if (item.getWorkingStatus() != null && item.getWorkingStatus() != HrConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.getValue()) {
                            cell.setCellValue("X");
                        } else {
                            cell.setCellValue("");
                        }


                        // Giờ tăng ca
                        cell = row.createCell(9);
                        cell.setCellStyle(cellStyleLeft);
                        double overtimeHours = 0;
                        if (item.getShiftWorks() != null && !item.getShiftWorks().isEmpty()) {
                            for (ShiftWorkDto sw : item.getShiftWorks()) {
                                if (sw.getShiftWorkType() == HrConstants.ShiftWorkType.OVERTIME.getValue()) {
                                    overtimeHours = sw.getTotalHours();
                                }
                            }
                        }
                        cell.setCellValue(overtimeHours);

                        // Trễ / Sớm
                        cell = row.createCell(10);
                        cell.setCellStyle(cellStyleLeft);
                        int lateArrivalMinutes = item.getLateArrivalMinutes() != null ? item.getLateArrivalMinutes() : 0;
                        int earlyExitMinutes = item.getEarlyExitMinutes() != null ? item.getEarlyExitMinutes() : 0;
                        cell.setCellValue(lateArrivalMinutes + "/" + earlyExitMinutes);

                        // Ghi chú
                        cell = row.createCell(11);
                        cell.setCellStyle(cellStyleLeft);
                        cell.setCellValue("");

                        // Nghỉ phép
                        cell = row.createCell(12);
                        cell.setCellStyle(cellStyleLeft);
                        if (item.getLeaveType() != null && item.getLeaveType().getCode() != null) {
                            //Nghỉ phép
                            cell = row.createCell(12);
                            cell.setCellStyle(cellStyleLeft);
                            if (item.getLeaveType().getCode().equals(HrConstants.LeaveTypeCode.ANNUAL_LEAVE.getCode())) {
                                cell.setCellValue(calculateLeaveDays(groupedLeaveRequests, staffDto.getId(), HrConstants.LeaveTypeCode.ANNUAL_LEAVE.getCode(), item.getWorkingDate()));
                            } else {
                                cell.setCellValue("");
                            }
                            //Công tác, Tự nhập
                            cell = row.createCell(13);
                            cell.setCellStyle(cellStyleLeft);
                            if (item.getLeaveType().getCode().equals(HrConstants.LeaveTypeCode.BUSINESS_TRIP.getCode())) {
                                cell.setCellValue(calculateLeaveDays(groupedLeaveRequests, staffDto.getId(), HrConstants.LeaveTypeCode.BUSINESS_TRIP.getCode(), item.getWorkingDate()));
                            } else {
                                cell.setCellValue("");
                            }

                            //Nghỉ không lương, Tự nhập
                            cell = row.createCell(14);
                            cell.setCellStyle(cellStyleLeft);
                            if (item.getLeaveType().getCode().equals(HrConstants.LeaveTypeCode.UNPAID_LEAVE.getCode())) {
                                cell.setCellValue(calculateLeaveDays(groupedLeaveRequests, staffDto.getId(), HrConstants.LeaveTypeCode.UNPAID_LEAVE.getCode(), item.getWorkingDate()));
                            } else {
                                cell.setCellValue("");
                            }
                            //Nghỉ lễ, Tự nhập
                            cell = row.createCell(15);
                            cell.setCellStyle(cellStyleLeft);
                            if (item.getLeaveType().getCode().equals(HrConstants.LeaveTypeCode.PUBLIC_HOLIDAY.getCode())) {
                                cell.setCellValue(calculateLeaveDays(groupedLeaveRequests, staffDto.getId(), HrConstants.LeaveTypeCode.PUBLIC_HOLIDAY.getCode(), item.getWorkingDate()));
                            } else {
                                cell.setCellValue("");
                            }

                            //Nghỉ bù, Tự nhập
                            cell = row.createCell(16);
                            cell.setCellStyle(cellStyleLeft);
                            if (item.getLeaveType().getCode().equals(HrConstants.LeaveTypeCode.COMPENSATORY_LEAVE.getCode())) {
                                cell.setCellValue(calculateLeaveDays(groupedLeaveRequests, staffDto.getId(), HrConstants.LeaveTypeCode.COMPENSATORY_LEAVE.getCode(), item.getWorkingDate()));
                            } else {
                                cell.setCellValue("");
                            }

                            //Nghỉ chế độ, Tự nhập
                            cell = row.createCell(17);
                            cell.setCellStyle(cellStyleLeft);
                            if (item.getLeaveType().getCode().equals(HrConstants.LeaveTypeCode.SPECIAL_LEAVE.getCode())) {
                                cell.setCellValue(calculateLeaveDays(groupedLeaveRequests, staffDto.getId(), HrConstants.LeaveTypeCode.SPECIAL_LEAVE.getCode(), item.getWorkingDate()));
                            } else {
                                cell.setCellValue("");
                            }
                        } else {
                            for (int i = 12; i <= 17; i++) {
                                cell = row.createCell(i);
                                cell.setCellStyle(cellStyleLeft);
                                cell.setCellValue("");
                            }
                        }


                    } else {
                        // Không có dữ liệu chấm công → Để trống
                        for (int i = 6; i <= 17; i++) {
                            cell = row.createCell(i);
                            cell.setCellStyle(cellStyleLeft);
                            cell.setCellValue("");
                        }
                    }
                }
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayResource(out.toByteArray());
    }

    private static double calculateLeaveDays(Map<Pair<UUID, String>, List<LeaveRequestDto>> groupedLeaveRequests, UUID staffId, String leaveTypeCode, Date workingDate) {
        Pair<UUID, String> key = Pair.of(staffId, leaveTypeCode);
        List<LeaveRequestDto> leaveRequestDtoList = groupedLeaveRequests.getOrDefault(key, Collections.emptyList());

        for (LeaveRequestDto leaveRequestDto : leaveRequestDtoList) {
            Date fromDate = leaveRequestDto.getFromDate();
            Date toDate = leaveRequestDto.getToDate();

            if (fromDate != null && isSameDay(fromDate, workingDate)) {
                return leaveRequestDto.getHalfDayLeaveStart() != null && leaveRequestDto.getHalfDayLeaveStart() ? 0.5 : 1.0;
            }
            if (toDate != null && isSameDay(toDate, workingDate)) {
                return leaveRequestDto.getHalfDayLeaveEnd() != null && leaveRequestDto.getHalfDayLeaveEnd() ? 0.5 : 1.0;
            }
        }
        return 1.0; // Không tìm thấy cụ thể thì tính 1 ngày
    }

    private static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
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

    public static List<Date> getDateRange(Date fromDate, Date toDate) {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);

        while (!calendar.getTime().after(toDate)) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public static ByteArrayResource handleExportFamilyRelationship(List<Staff> datas, XSSFWorkbook workbook) throws IOException {
        if (datas == null || datas.isEmpty()) return null;
        XSSFSheet sheet = workbook.getSheetAt(0);

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");

        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 11);

        Font fontBold = workbook.createFont();
        fontBold.setFontName("Times New Roman");
        fontBold.setFontHeightInPoints((short) 11);
        fontBold.setBold(true);

        XSSFCellStyle cellStyleBorderLeft = workbook.createCellStyle();
        cellStyleBorderLeft.setBorderBottom(BorderStyle.THIN);
        cellStyleBorderLeft.setBorderTop(BorderStyle.THIN);
        cellStyleBorderLeft.setBorderLeft(BorderStyle.THIN);
        cellStyleBorderLeft.setBorderRight(BorderStyle.THIN);
        cellStyleBorderLeft.setAlignment(HorizontalAlignment.LEFT);
        cellStyleBorderLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleBorderLeft.setWrapText(true);
        cellStyleBorderLeft.setFont(font);

        //Điền dữ liệu vào bảng excel
        int rowIndex = 1;
        XSSFRow row = null;
        XSSFCell cell = null;
        //Vị trí của ô sau các đợt giới thiệu
        for (Staff item : datas) {
            if (item.getFamilyRelationships() != null && !item.getFamilyRelationships().isEmpty()) {
                String staffCode = "";
                String staffName = "";
                if (item.getStaffCode() != null) {
                    staffCode = item.getStaffCode();
                }
                if (item.getStaffCode() != null) {
                    staffName = item.getDisplayName();
                }
                for (StaffFamilyRelationship familyRelationship : item.getFamilyRelationships()) {
                    row = sheet.createRow(rowIndex);
                    //Mã nhân viên
                    cell = row.createCell(0);
                    cell.setCellStyle(cellStyleBorderLeft);
                    cell.setCellValue(staffCode);

                    //Họ tên nhân viên
                    cell = row.createCell(1);
                    cell.setCellStyle(cellStyleBorderLeft);
                    cell.setCellValue(staffName);

                    //Họ tên
                    cell = row.createCell(2);
                    cell.setCellStyle(cellStyleBorderLeft);
                    if (familyRelationship.getFullName() != null) {
                        cell.setCellValue(familyRelationship.getFullName());
                    } else {
                        cell.setCellValue("");
                    }
                    //Ngày tháng năm sinh
                    cell = row.createCell(3);
                    cell.setCellStyle(cellStyleBorderLeft);
                    if (familyRelationship.getBirthDate() != null) {
                        try {
                            cell.setCellValue(formatterDate.format(familyRelationship.getBirthDate()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            cell.setCellValue(familyRelationship.getBirthDate());
                        }
                    } else {
                        cell.setCellValue("");
                    }

                    //Mã Nghề nghiệp
                    cell = row.createCell(4);
                    cell.setCellStyle(cellStyleBorderLeft);
                    if (familyRelationship.getProfession() != null && familyRelationship.getProfession().getCode() != null) {
                        cell.setCellValue(familyRelationship.getProfession().getCode());
                    } else {
                        cell.setCellValue("");
                    }
                    //Nghề nghiệp
                    cell = row.createCell(5);
                    cell.setCellStyle(cellStyleBorderLeft);
                    if (familyRelationship.getProfession() != null && familyRelationship.getProfession().getName() != null) {
                        cell.setCellValue(familyRelationship.getProfession().getName());
                    } else {
                        cell.setCellValue("");
                    }

                    //Mã quan hệ
                    cell = row.createCell(6);
                    cell.setCellStyle(cellStyleBorderLeft);
                    if (familyRelationship.getFamilyRelationship() != null && familyRelationship.getFamilyRelationship().getCode() != null) {
                        cell.setCellValue(familyRelationship.getFamilyRelationship().getCode());
                    } else {
                        cell.setCellValue("");
                    }
                    //Quan hệ
                    cell = row.createCell(7);
                    cell.setCellStyle(cellStyleBorderLeft);
                    if (familyRelationship.getFamilyRelationship() != null && familyRelationship.getFamilyRelationship().getName() != null) {
                        cell.setCellValue(familyRelationship.getFamilyRelationship().getName());
                    } else {
                        cell.setCellValue("");
                    }
                    rowIndex++;
                }
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayResource(out.toByteArray());
    }

    public static ByteArrayResource handleExportIntroduceCost(List<StaffDto> datas) throws IOException {
        if (datas == null || datas.isEmpty()) return null;
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bảng lương");

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");

        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 11);

        Font fontBold = workbook.createFont();
        fontBold.setFontName("Times New Roman");
        fontBold.setFontHeightInPoints((short) 11);
        fontBold.setBold(true);

        // Tạo các style cho cell
        XSSFCellStyle cellStyleBorderCenter = workbook.createCellStyle();
        cellStyleBorderCenter.setBorderBottom(BorderStyle.THIN);
        cellStyleBorderCenter.setBorderTop(BorderStyle.THIN);
        cellStyleBorderCenter.setBorderLeft(BorderStyle.THIN);
        cellStyleBorderCenter.setBorderRight(BorderStyle.THIN);
        cellStyleBorderCenter.setAlignment(HorizontalAlignment.CENTER);
        cellStyleBorderCenter.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleBorderCenter.setWrapText(true);
        cellStyleBorderCenter.setFont(font);

        XSSFCellStyle cellStyleBorderRight = workbook.createCellStyle();
        cellStyleBorderRight.setBorderBottom(BorderStyle.THIN);
        cellStyleBorderRight.setBorderTop(BorderStyle.THIN);
        cellStyleBorderRight.setBorderLeft(BorderStyle.THIN);
        cellStyleBorderRight.setBorderRight(BorderStyle.THIN);
        cellStyleBorderRight.setAlignment(HorizontalAlignment.RIGHT);
        cellStyleBorderRight.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleBorderRight.setWrapText(true);
        cellStyleBorderRight.setFont(font);

        XSSFCellStyle cellStyleBorderLeft = workbook.createCellStyle();
        cellStyleBorderLeft.setBorderBottom(BorderStyle.THIN);
        cellStyleBorderLeft.setBorderTop(BorderStyle.THIN);
        cellStyleBorderLeft.setBorderLeft(BorderStyle.THIN);
        cellStyleBorderLeft.setBorderRight(BorderStyle.THIN);
        cellStyleBorderLeft.setAlignment(HorizontalAlignment.LEFT);
        cellStyleBorderLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleBorderLeft.setWrapText(true);
        cellStyleBorderLeft.setFont(font);


        XSSFCellStyle cellStyleBorderBoldLeft = workbook.createCellStyle();
        cellStyleBorderBoldLeft.setBorderBottom(BorderStyle.THIN);
        cellStyleBorderBoldLeft.setBorderTop(BorderStyle.THIN);
        cellStyleBorderBoldLeft.setBorderLeft(BorderStyle.THIN);
        cellStyleBorderBoldLeft.setBorderRight(BorderStyle.THIN);
        cellStyleBorderBoldLeft.setAlignment(HorizontalAlignment.LEFT);
        cellStyleBorderBoldLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleBorderBoldLeft.setWrapText(true);
        cellStyleBorderBoldLeft.setFont(fontBold);

        XSSFCellStyle cellStyleBoldCenter = workbook.createCellStyle();
        cellStyleBoldCenter.setAlignment(HorizontalAlignment.CENTER);
        cellStyleBoldCenter.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleBoldCenter.setWrapText(true);
        cellStyleBoldCenter.setFont(fontBold);

        // Lấy tất cả các đợt giới thiệu của danh sách nhân viên (không trùng)
        Set<Integer> periodOrderSet = new HashSet<>();
        for (StaffDto staff : datas) {
            if (staff == null || staff.getStaffIntroduceCosts().isEmpty()) continue;
            for (HrIntroduceCostDto item : staff.getStaffIntroduceCosts()) {
                if (item != null && item.getPeriodOrder() != null) {
                    periodOrderSet.add(item.getPeriodOrder());
                }
            }
        }

        // Chuyển Set thành List và sắp xếp tăng dần
        List<Integer> periodOrderList = new ArrayList<>(periodOrderSet);
        Collections.sort(periodOrderList);

        // Khởi tạo danh sách header
        List<String> headerLeft = new ArrayList<>(Arrays.asList("STT", "Mã Nhân viên", "Họ tên", "Trạng thái làm việc", "Ban/Chi nhánh", "Ngày vào", "Người giới thiệu", "Mã số người giới thiệu", "Người tuyển", "NGÀY PASS THỬ VIỆC", "THÁNG LÀM VIỆC", "LEVEL"));

        // Các đợt giới thiệu
        List<String> headerIntroduce = new ArrayList<>();
        if (!periodOrderList.isEmpty()) {
            for (Integer item : periodOrderList) { // Dùng Integer thay vì int
                headerIntroduce.add("THÁNG TÍNH GIỚI THIỆU ĐỢT " + item);
                headerIntroduce.add("CHI PHÍ");
            }
        }

        // Thêm các cột cuối vào header
        List<String> headerRight = new ArrayList<>(Arrays.asList("Note", "Chi phí"));
        List<String> header = new ArrayList<>();
        header.addAll(headerLeft);
        header.addAll(headerIntroduce);
        header.addAll(headerRight);

        Row row = sheet.createRow(0);
        Cell cell = null;
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, header.size() - 1));
        cell = row.createCell(0);
        cell.setCellValue("QUẢN LÝ GIỚI THIỆU HỒ SƠ MỚI");
        cell.setCellStyle(cellStyleBoldCenter);

        row = sheet.createRow(2);
        for (int i = 0; i < header.size(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(header.get(i));
            cell.setCellStyle(cellStyleBorderBoldLeft);
        }

        //Điền dữ liệu vào bảng excel
        int rowIndex = 3;
        int index = 0;

        //Vị trí của ô sau các đợt giới thiệu
        int indexBeforeNote = headerLeft.size() + headerIntroduce.size();
        for (StaffDto item : datas) {
            if (item.getIntroducer() == null || item.getIntroducer().getId() == null) continue;
            row = sheet.createRow(rowIndex);
            //STT
            cell = row.createCell(0);
            cell.setCellStyle(cellStyleBorderLeft);
            cell.setCellValue((double) index++);

            //Mã Nhân viên
            cell = row.createCell(1);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getStaffCode() != null) {
                cell.setCellValue(item.getStaffCode());
            } else {
                cell.setCellValue("");
            }
            //Họ tên
            cell = row.createCell(2);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getDisplayName() != null) {
                cell.setCellValue(item.getDisplayName());
            } else {
                cell.setCellValue("");
            }
            //Trạng thái làm việc
            cell = row.createCell(3);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getStatus() != null && item.getStatus().getName() != null) {
                cell.setCellValue(item.getStatus().getName());
            } else {
                cell.setCellValue("");
            }
            //Ban/Chi nhánh
            cell = row.createCell(4);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getDepartment() != null && item.getDepartment().getName() != null) {
                cell.setCellValue(item.getDepartment().getName());
            } else {
                cell.setCellValue("");
            }

            //Ngày vào
            cell = row.createCell(5);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getStartDate() != null) {
                try {
                    cell.setCellValue(formatterDate.format(item.getStartDate()));
                } catch (Exception e) {
                    e.printStackTrace();
                    cell.setCellValue(item.getStartDate());
                }
            } else {
                cell.setCellValue("");
            }
            //Người giới thiệu
            cell = row.createCell(6);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getIntroducer() != null && item.getIntroducer().getDisplayName() != null) {
                cell.setCellValue(item.getIntroducer().getDisplayName());
            } else {
                cell.setCellValue("");
            }

            //Mã số người giới thiệu
            cell = row.createCell(7);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getIntroducer() != null && item.getIntroducer().getStaffCode() != null) {
                cell.setCellValue(item.getIntroducer().getStaffCode());
            } else {
                cell.setCellValue("");
            }
            //Người tuyển
            cell = row.createCell(8);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getRecruiter() != null && item.getRecruiter().getDisplayName() != null) {
                cell.setCellValue(item.getRecruiter().getDisplayName());
            } else {
                cell.setCellValue("");
            }
            //NGÀY PASS THỬ VIỆC
            cell = row.createCell(9);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getContractDate() != null) {
                try {
                    cell.setCellValue(formatterDate.format(item.getContractDate()));
                } catch (Exception e) {
                    e.printStackTrace();
                    cell.setCellValue(item.getContractDate());
                }
            } else {
                cell.setCellValue("");
            }
            //6 THÁNG LÀM VIỆC
            cell = row.createCell(10);
            cell.setCellStyle(cellStyleBorderLeft);
            cell.setCellValue("");

            //LEVEL
            cell = row.createCell(11);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getCurrentPosition() != null && item.getCurrentPosition().getTitle() != null && item.getCurrentPosition().getTitle().getRankTitle() != null && item.getCurrentPosition().getTitle().getRankTitle().getShortName() != null) {
                cell.setCellValue(item.getCurrentPosition().getTitle().getRankTitle().getShortName());
            } else {
                cell.setCellValue("");
            }
            double cost = 0.0;
            //THÁNG TÍNH GIỚI THIỆU
            for (int i = 12; i < 12 + headerIntroduce.size(); i += 2) {
                //THÁNG TÍNH GIỚI THIỆU ĐỢT
                cell = row.createCell(i);
                cell.setCellStyle(cellStyleBorderLeft);
                cell.setCellValue("");

                //CHI PHÍ
                cell = row.createCell(i + 1);
                cell.setCellStyle(cellStyleBorderRight);
                cell.setCellValue("");

                if (!item.getStaffIntroduceCosts().isEmpty()) {
                    for (HrIntroduceCostDto itemCost : item.getStaffIntroduceCosts()) {
                        for (int periodOrder : periodOrderList) {
                            if (itemCost != null && itemCost.getPeriodOrder() == periodOrder) {
                                if (itemCost.getIntroducePeriod() != null) {
                                    cell = row.getCell(i);
                                    cell.setCellStyle(cellStyleBorderLeft);
                                    try {
                                        cell.setCellValue(formatterDate.format(itemCost.getIntroducePeriod()));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        cell.setCellValue(itemCost.getIntroducePeriod());
                                    }
                                }
                                if (itemCost.getCost() != null) {
                                    cell = row.getCell(i + 1);
                                    cell.setCellStyle(cellStyleBorderRight);
                                    try {
                                        cell.setCellValue(formatter.format(itemCost.getCost()));
                                        cost += itemCost.getCost();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        cell.setCellValue(itemCost.getCost());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //Note
            cell = row.createCell(indexBeforeNote);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getIntroducer().getStatus() != null && item.getIntroducer().getStatus().getName() != null) {
                cell.setCellValue("Người giới thiệu " + item.getIntroducer().getStatus().getName());
            } else {
                cell.setCellValue("");
            }
            //Chi phí
            cell = row.createCell(indexBeforeNote + 1);
            cell.setCellStyle(cellStyleBorderRight);
            try {
                cell.setCellValue(formatter.format(cost));
            } catch (Exception e) {
                e.printStackTrace();
                cell.setCellValue(cost);
            }
            rowIndex++;
        }
        for (int i = 0; i < header.size(); i++) {
            sheet.autoSizeColumn(i);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayResource(out.toByteArray());
    }

    public static ByteArrayResource handleExportAdministrativeUnit(List<HrAdministrativeUnitDto> datas, XSSFWorkbook workbook) throws IOException {
        if (datas == null || datas.isEmpty()) return null;

        // Đảm bảo danh sách có thể thay đổi
        List<HrAdministrativeUnitDto> sortDatas = new ArrayList<>(datas);

        // Sắp xếp danh sách
        Collections.sort(sortDatas, new Comparator<HrAdministrativeUnitDto>() {
            @Override
            public int compare(HrAdministrativeUnitDto o1, HrAdministrativeUnitDto o2) {
                // Kiểm tra null cho level
                Integer level1 = o1.getLevel();
                Integer level2 = o2.getLevel();

                if (level1 == null && level2 == null) {
                    return 0; // Cả hai null, coi như bằng nhau
                } else if (level1 == null) {
                    return 1; // level1 null, đặt ở sau
                } else if (level2 == null) {
                    return -1; // level2 null, đặt o1 ở trước
                }

                // So sánh theo level trước
                int levelComparison = Integer.compare(level2, level1);
                if (levelComparison != 0) {
                    return levelComparison; // Nếu level khác nhau, sắp xếp theo level
                }

                // Nếu level bằng nhau, sắp xếp theo code (theo thứ tự bảng chữ cái)
                String code1 = o1.getCode();
                String code2 = o2.getCode();

                if (code1 == null && code2 == null) {
                    return 0;
                } else if (code1 == null) {
                    return 1;
                } else if (code2 == null) {
                    return -1;
                }

                return code1.compareTo(code2); // So sánh theo bảng chữ cái
            }
        });

        XSSFSheet sheet = workbook.getSheetAt(0);

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");

        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 11);

        Font fontBold = workbook.createFont();
        fontBold.setFontName("Times New Roman");
        fontBold.setFontHeightInPoints((short) 11);
        fontBold.setBold(true);

        // Tạo các style cho cell
        XSSFCellStyle cellStyleBorderCenter = workbook.createCellStyle();
        cellStyleBorderCenter.setBorderBottom(BorderStyle.THIN);
        cellStyleBorderCenter.setBorderTop(BorderStyle.THIN);
        cellStyleBorderCenter.setBorderLeft(BorderStyle.THIN);
        cellStyleBorderCenter.setBorderRight(BorderStyle.THIN);
        cellStyleBorderCenter.setAlignment(HorizontalAlignment.CENTER);
        cellStyleBorderCenter.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleBorderCenter.setWrapText(true);
        cellStyleBorderCenter.setFont(font);

        XSSFCellStyle cellStyleBorderLeft = workbook.createCellStyle();
        cellStyleBorderLeft.setBorderBottom(BorderStyle.THIN);
        cellStyleBorderLeft.setBorderTop(BorderStyle.THIN);
        cellStyleBorderLeft.setBorderLeft(BorderStyle.THIN);
        cellStyleBorderLeft.setBorderRight(BorderStyle.THIN);
        cellStyleBorderLeft.setAlignment(HorizontalAlignment.LEFT);
        cellStyleBorderLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleBorderLeft.setWrapText(true);
        cellStyleBorderLeft.setFont(font);


        //Điền dữ liệu vào bảng excel
        int rowIndex = 1;
        int index = 0;
        XSSFRow row = null;
        XSSFCell cell = null;
        for (HrAdministrativeUnitDto item : sortDatas) {
            row = sheet.createRow(rowIndex);
            //0. STT
            cell = row.createCell(0);
            cell.setCellStyle(cellStyleBorderCenter);
            cell.setCellValue(index++);

            //1. Mã đơn vị hành chính(*)
            cell = row.createCell(1);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getCode() != null) {
                cell.setCellValue(item.getCode());
            } else {
                cell.setCellValue("");
            }

            //2. Tên đơn vị hành chính
            cell = row.createCell(2);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getName() != null) {
                cell.setCellValue(item.getName());
            } else {
                cell.setCellValue("");
            }
            //3. Mã cấp độ(*)
            cell = row.createCell(3);
            cell.setCellStyle(cellStyleBorderLeft);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getLevel() != null) {
                cell.setCellValue(item.getLevel());
            } else {
                cell.setCellValue("");
            }

            //4. Cấp độ
            cell = row.createCell(4);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getLevel() != null) {
                cell.setCellValue(HrConstants.AdministrativeLevel.getDescriptionByValue(item.getLevel()));
            } else {
                cell.setCellValue("");
            }
            //5. Mã đơn vị quản lý
            cell = row.createCell(5);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getParent() != null && item.getParent().getCode() != null) {
                cell.setCellValue(item.getParent().getCode());
            } else {
                cell.setCellValue("");
            }

            //6. Tên đơn vị quản lý
            cell = row.createCell(6);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getParent() != null && item.getParent().getName() != null) {
                cell.setCellValue(item.getParent().getName());
            } else {
                cell.setCellValue("");
            }
            //7. Mô tả
            cell = row.createCell(7);
            cell.setCellStyle(cellStyleBorderLeft);
            if (item.getDescription() != null) {
                cell.setCellValue(item.getDescription());
            } else {
                cell.setCellValue("");
            }
            rowIndex++;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayResource(out.toByteArray());
    }

    public static ByteArrayResource generatePaidLeaveXlsx(LeaveRequestDto dto, XSSFWorkbook workbook) throws IOException {
        if (dto == null) return null;

        XSSFSheet sheet = workbook.getSheetAt(0);

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");

        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 13);

        Font fontBold = workbook.createFont();
        fontBold.setFontName("Times New Roman");
        fontBold.setFontHeightInPoints((short) 13);
        fontBold.setBold(true);

        // Tạo các style cho cell
        XSSFCellStyle cellStyleBorderCenter = workbook.createCellStyle();
        cellStyleBorderCenter.setBorderBottom(BorderStyle.THIN);
        cellStyleBorderCenter.setBorderTop(BorderStyle.THIN);
        cellStyleBorderCenter.setBorderLeft(BorderStyle.THIN);
        cellStyleBorderCenter.setBorderRight(BorderStyle.THIN);
        cellStyleBorderCenter.setAlignment(HorizontalAlignment.CENTER);
        cellStyleBorderCenter.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleBorderCenter.setWrapText(true);
        cellStyleBorderCenter.setFont(font);

        XSSFCellStyle cellStyleBorderLeft = workbook.createCellStyle();
        cellStyleBorderLeft.setBorderBottom(BorderStyle.THIN);
        cellStyleBorderLeft.setBorderTop(BorderStyle.THIN);
        cellStyleBorderLeft.setBorderLeft(BorderStyle.THIN);
        cellStyleBorderLeft.setBorderRight(BorderStyle.THIN);
        cellStyleBorderLeft.setAlignment(HorizontalAlignment.LEFT);
        cellStyleBorderLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleBorderLeft.setWrapText(true);
        cellStyleBorderLeft.setFont(font);

        XSSFCellStyle cellStyleBoldLeft = workbook.createCellStyle();
        cellStyleBoldLeft.setAlignment(HorizontalAlignment.LEFT);
        cellStyleBoldLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleBoldLeft.setWrapText(true);
        cellStyleBoldLeft.setFont(fontBold);


        //Điền dữ liệu vào bảng excel
        int rowIndex = 1;
        XSSFRow row = null;
        XSSFCell cell = null;

        if (dto.getRequestStaff() != null) {
            StaffDto staff = dto.getRequestStaff();

            row = sheet.getRow(5);
            //Họ tên nhân viên
            cell = row.getCell(0);
            cell.setCellStyle(cellStyleBoldLeft);
            if (staff.getDisplayName() != null) {
                cell.setCellValue(("HỌ TÊN: " + staff.getDisplayName()).toUpperCase());
            } else {
                cell.setCellValue(("HỌ TÊN: ").toUpperCase());
            }

            //Mã nhân viên
            cell = row.getCell(5);
            cell.setCellStyle(cellStyleBoldLeft);
            if (staff.getStaffCode() != null) {
                cell.setCellValue(("MNV: " + staff.getStaffCode()).toUpperCase());
            } else {
                cell.setCellValue(("MNV: ").toUpperCase());
            }

            row = sheet.getRow(6);
            if (staff.getDepartment() != null) {
                //BAN:
                cell = row.getCell(0);
                cell.setCellStyle(cellStyleBoldLeft);
                if (staff.getDepartment().getHrDepartmentType() != null && staff.getDepartment().getHrDepartmentType().getName() != null) {
                    cell.setCellValue(("BAN: " + staff.getDepartment().getHrDepartmentType().getName()).toUpperCase());
                } else {
                    cell.setCellValue(("BAN: ").toUpperCase());
                }

                //PHÒNG/CƠ SỞ:
                cell = row.getCell(5);
                cell.setCellStyle(cellStyleBoldLeft);
                if (staff.getDepartment().getName() != null) {
                    cell.setCellValue(("PHÒNG/CƠ SỞ: " + staff.getDepartment().getName()).toUpperCase());
                } else {
                    cell.setCellValue(("PHÒNG/CƠ SỞ: ").toUpperCase());
                }
            }

            row = sheet.getRow(7);
            if (staff.getDepartment() != null) {
                //BỘ PHẬN/ NHÓM:
                cell = row.getCell(0);
                cell.setCellStyle(cellStyleBoldLeft);
                if (staff.getDepartment().getDepartmentGroup() != null && staff.getDepartment().getDepartmentGroup().getName() != null) {
                    cell.setCellValue(("BỘ PHẬN/ NHÓM: " + staff.getDepartment().getDepartmentGroup().getName()).toUpperCase());
                } else {
                    cell.setCellValue(("BỘ PHẬN/ NHÓM: ").toUpperCase());
                }


            }

            row = sheet.getRow(7);
            //NGÀY VÀO CÔNG TY:
            cell = row.getCell(5);
            cell.setCellStyle(cellStyleBoldLeft);
            if (staff.getStartDate() != null) {
                try {
                    cell.setCellValue(("NGÀY VÀO CÔNG TY: " + formatterDate.format(staff.getStartDate())).toUpperCase());
                } catch (Exception e) {
                    log.error("Lỗi khi định dạng hoặc gán giá trị ngày vào ô Excel: {}", e.getMessage(), e);
                    cell.setCellValue(("NGÀY VÀO CÔNG TY: ").toUpperCase());
                }
            } else {
                cell.setCellValue(("NGÀY VÀO CÔNG TY: ").toUpperCase());
            }
        }
        row = sheet.getRow(12);

        //Nghỉ phép năm
        if (HrConstants.LeaveTypeCode.YEAR_LEAVE.getCode().equals(dto.getLeaveType().getCode())) {
            if (dto.getFromDate() != null && dto.getToDate() != null) {
                try {
                    //Ngày nghỉ
                    cell = row.getCell(0);
                    cell.setCellStyle(cellStyleBorderLeft);
                    cell.setCellValue(formatterDate.format(dto.getFromDate()) + " - " + formatterDate.format(dto.getToDate()));

                    //Số ngày nghỉ
                    // Chuyển đổi từ Date sang LocalDate
                    LocalDate fromDate = dto.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate toDate = dto.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    // Tính tổng số ngày nghỉ
                    long totalDays = ChronoUnit.DAYS.between(fromDate, toDate) + 1; // Cộng 1 để bao gồm cả ngày bắt đầu

                    cell = row.getCell(1);
                    cell.setCellStyle(cellStyleBorderLeft);
                    cell.setCellValue(totalDays);


                } catch (Exception e) {
                    log.error("Lỗi khi định dạng hoặc gán giá trị ngày vào ô Excel: {}", e.getMessage(), e);
                }
            }
        }

        //Nghỉ phép không lương
        if (HrConstants.LeaveTypeCode.UNPAID_LEAVE.getCode().equals(dto.getLeaveType().getCode())) {
            if (dto.getFromDate() != null && dto.getToDate() != null) {
                try {
                    //Ngày nghỉ
                    cell = row.getCell(2);
                    cell.setCellStyle(cellStyleBorderLeft);
                    cell.setCellValue(formatterDate.format(dto.getFromDate()) + " - " + formatterDate.format(dto.getToDate()));

                    //Số ngày nghỉ
                    // Chuyển đổi từ Date sang LocalDate
                    LocalDate fromDate = dto.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate toDate = dto.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    // Tính tổng số ngày nghỉ
                    long totalDays = ChronoUnit.DAYS.between(fromDate, toDate) + 1; // Cộng 1 để bao gồm cả ngày bắt đầu

                    cell = row.getCell(3);
                    cell.setCellStyle(cellStyleBorderLeft);
                    cell.setCellValue(totalDays);


                } catch (Exception e) {
                    log.error("Lỗi khi định dạng hoặc gán giá trị ngày vào ô Excel: {}", e.getMessage(), e);
                }
            }
        }

        //Nghỉ chế độ
        if (HrConstants.LeaveTypeCode.SPECIAL_LEAVE.getCode().equals(dto.getLeaveType().getCode())) {
            if (dto.getFromDate() != null && dto.getToDate() != null) {
                try {
                    //Ngày nghỉ
                    cell = row.getCell(4);
                    cell.setCellStyle(cellStyleBorderLeft);
                    cell.setCellValue(formatterDate.format(dto.getFromDate()) + " - " + formatterDate.format(dto.getToDate()));

                    //Số ngày nghỉ
                    // Chuyển đổi từ Date sang LocalDate
                    LocalDate fromDate = dto.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate toDate = dto.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    // Tính tổng số ngày nghỉ
                    long totalDays = ChronoUnit.DAYS.between(fromDate, toDate) + 1; // Cộng 1 để bao gồm cả ngày bắt đầu

                    cell = row.getCell(5);
                    cell.setCellStyle(cellStyleBorderLeft);
                    cell.setCellValue(totalDays);


                } catch (Exception e) {
                    log.error("Lỗi khi định dạng hoặc gán giá trị ngày vào ô Excel: {}", e.getMessage(), e);
                }
            }
        }
        //Nghỉ bù
        if (HrConstants.LeaveTypeCode.COMPENSATORY_LEAVE.getCode().equals(dto.getLeaveType().getCode())) {
            if (dto.getFromDate() != null && dto.getToDate() != null) {
                try {
                    //Ngày nghỉ
                    cell = row.getCell(6);
                    cell.setCellStyle(cellStyleBorderLeft);
                    cell.setCellValue(formatterDate.format(dto.getFromDate()) + " - " + formatterDate.format(dto.getToDate()));

                    //Số ngày nghỉ
                    // Chuyển đổi từ Date sang LocalDate
                    LocalDate fromDate = dto.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate toDate = dto.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    // Tính tổng số ngày nghỉ
                    long totalDays = ChronoUnit.DAYS.between(fromDate, toDate) + 1; // Cộng 1 để bao gồm cả ngày bắt đầu

                    cell = row.getCell(7);
                    cell.setCellStyle(cellStyleBorderLeft);
                    cell.setCellValue(totalDays);


                } catch (Exception e) {
                    log.error("Lỗi khi định dạng hoặc gán giá trị ngày vào ô Excel: {}", e.getMessage(), e);
                }
            }
        }

        //Quản lý phê duyệt
        if (dto.getApprovalStaff() != null && dto.getApprovalStaff().getDisplayName() != null) {
            cell = row.getCell(8);
            cell.setCellStyle(cellStyleBorderLeft);
            cell.setCellValue(dto.getApprovalStaff().getDisplayName());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayResource(out.toByteArray());
    }

    /**
     * Tạo style cho cell: border và font Times New Roman
     */
    public static CellStyle createDataCellStyle(Workbook workbook) {
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

    public static CellStyle createDataCellStyle(Workbook workbook, int fontSize) {
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
        font.setFontHeightInPoints((short) fontSize);
        cellStyle.setFont(font);

        return cellStyle;
    }

    public static CellStyle createDataCellStyle(Workbook workbook, int fontSize, boolean isCenter) {
        CellStyle cellStyle = workbook.createCellStyle();

        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        if (isCenter == true) {
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        } else {
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        }


        // Tạo font Times New Roman
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) fontSize);
        cellStyle.setFont(font);

        return cellStyle;
    }

    public static CellStyle createDataCellStyleColor(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();

        // Set border
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);


        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Set background color (yellow)
        cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Set font
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);

        return cellStyle;
    }


    public static void createCell(Row row, int cellIndex, Object value, CellStyle style) {
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

    public static String getGenderText(String gender) {
        if (gender == null) return "";
        switch (gender.trim().toUpperCase()) {
            case "M":
                return "Nam";
            case "F":
                return "Nữ";
            default:
                return "Không xác định";
        }
    }

    public static void applyStyleToRow(Row row, CellStyle style, int numberOfColumns) {
        for (int i = 0; i < numberOfColumns; i++) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                cell = row.createCell(i);
            }
            cell.setCellStyle(style);
        }
    }


}
