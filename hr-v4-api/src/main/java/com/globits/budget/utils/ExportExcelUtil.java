package com.globits.budget.utils;


import java.awt.*;
import java.awt.Color;
import java.text.NumberFormat;
import java.util.Locale;

import com.globits.budget.domain.Voucher;
import com.globits.budget.domain.VoucherItem;
import com.globits.budget.dto.VoucherDto;
import com.globits.budget.dto.VoucherItemDto;
import com.globits.budget.dto.budget.BudgetSummaryBalanceDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExportExcelUtil {
    public static ByteArrayResource exportVoucher(List<VoucherDto> datas, BudgetSummaryBalanceDto summary, XSSFWorkbook workbook) throws IOException {
        XSSFSheet sheet = workbook.getSheetAt(0);
        Font font = workbook.createFont();
        font.setFontName("Times New Roman"); // Chỉnh font về Times New Roman
        font.setFontHeightInPoints((short) 14); // Cỡ chữ 14

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);
        cellStyle.setFont(font);

        XSSFCellStyle cellStyleRight = workbook.createCellStyle();
        cellStyleRight.setBorderBottom(BorderStyle.THIN);
        cellStyleRight.setBorderTop(BorderStyle.THIN);
        cellStyleRight.setBorderLeft(BorderStyle.THIN);
        cellStyleRight.setBorderRight(BorderStyle.THIN);
        cellStyleRight.setAlignment(HorizontalAlignment.RIGHT);
        cellStyleRight.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleRight.setWrapText(true);
        cellStyleRight.setFont(font);

        XSSFCellStyle cellStyleLeft = workbook.createCellStyle();
        cellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
        cellStyleLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleLeft.setFont(font);

        if (datas != null && summary != null) {
            XSSFRow row;
            XSSFCell cell;
            String budgetName = "Số dư của quỹ: ";
            if (summary.getBudget().getName() != null) {
                budgetName += summary.getBudget().getName();
            } else {
                budgetName += "Không xác định!";
            }
            // Dòng 2: Tên ngân sách
            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellValue(budgetName);
            cell.setCellStyle(cellStyleLeft);

            // Dòng 3: Số dư hiện tại
            row = sheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellValue("Đang có: " + currencyFormatter.format(summary.getSummaryUntilToDate().getTotalAmount()));
            cell.setCellStyle(cellStyleLeft);

            // Dòng 4: Ngày tính tới
            row = sheet.createRow(3);
            cell = row.createCell(0);
            cell.setCellValue("Tính tới ngày: " + formatter.format(summary.getSummaryUntilToDate().getToDate()));
            cell.setCellStyle(cellStyleLeft);


            double totalIncome = summary.getSummaryFromDateToDate().stream()
                    .filter(item -> item.getVoucherType() > 0)
                    .mapToDouble(item -> item.getTotalAmount() * item.getVoucherType())
                    .sum();
            // Dòng 5: Tổng thu
            row = sheet.createRow(4);
            cell = row.createCell(0);
            cell.setCellValue("Tổng thu: " + currencyFormatter.format(totalIncome));
            cell.setCellStyle(cellStyleLeft);


            double totalExpense = summary.getSummaryFromDateToDate().stream()
                    .filter(item -> item.getVoucherType() < 0)
                    .mapToDouble(item -> item.getTotalAmount() * item.getVoucherType())
                    .sum();

            // Dòng 6: Tổng chi
            row = sheet.createRow(5);
            cell = row.createCell(0);
            cell.setCellValue("Tổng chi: " + currencyFormatter.format(totalExpense));
            cell.setCellStyle(cellStyleLeft);

            // Dòng 7: Khoảng thời gian

            String fromDate = formatter.format(summary.getSummaryFromDateToDate().get(0).getFromDate());
            String toDate = formatter.format(summary.getSummaryFromDateToDate().get(0).getToDate());
            row = sheet.createRow(6);
            cell = row.createCell(0);
            cell.setCellValue("Khoảng thời gian: " + fromDate + " đến " + toDate);
            cell.setCellStyle(cellStyleLeft);

            int rowIndex = 9;
            if (datas != null && datas.size() > 0) {
                int index = 0;

                for (VoucherDto item : datas) {
                    if (index > 0) {
                        ++rowIndex;
                    }

                    ++index;
                    row = sheet.createRow(rowIndex);
                    sheet.getRow(rowIndex + 1);
                    if (row != null) {
                        cell = row.createCell(0);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue((double) index);

                        cell = row.createCell(1);
                        cell.setCellStyle(cellStyle);
                        if (item.getVoucherDate() != null) {
                            cell.setCellValue(formatter.format(item.getVoucherDate()));
                        } else {
                            cell.setCellValue("");
                        }

                        cell = row.createCell(2);
                        cell.setCellStyle(cellStyle);
                        if (item.getVoucherType() != null) {
                            if (Enums.VoucherType.INCOME.getValue() == item.getVoucherType()) {
                                cell.setCellValue(Enums.VoucherType.INCOME.getName());
                            } else {
                                cell.setCellValue(Enums.VoucherType.EXPENDITURE.getName());
                            }
                        } else {
                            cell.setCellValue("");
                        }


                        cell = row.createCell(3);
                        cell.setCellStyle(cellStyle);
                        if (item.getVoucherItems() != null && !item.getVoucherItems().isEmpty()) {
                            StringBuilder budgetCategoryName = new StringBuilder();

                            for (VoucherItemDto voucherItem : item.getVoucherItems()) {
                                if (voucherItem.getBudgetCategory() != null && voucherItem.getBudgetCategory().getName() != null) {
                                    if (budgetCategoryName.length() > 0) {
                                        budgetCategoryName.append(", ");
                                    }
                                    budgetCategoryName.append(voucherItem.getBudgetCategory().getName());
                                }
                            }

                            cell.setCellValue(budgetCategoryName.toString()); // Chuyển StringBuilder thành String
                        } else {
                            cell.setCellValue("");
                        }

                        cell = row.createCell(4);
                        cell.setCellStyle(cellStyleRight);
                        if (item.getTotalAmount() != null) {
                            cell.setCellValue(formatCurrency(item.getTotalAmount()));
                        } else {
                            cell.setCellValue("");
                        }

                        cell = row.createCell(5);
                        cell.setCellStyle(cellStyle);
                        if (item.getBudget() != null && item.getBudget().getName() != null) {
                            cell.setCellValue(item.getBudget().getName());
                        } else {
                            cell.setCellValue("");
                        }

                        cell = row.createCell(6);
                        cell.setCellStyle(cellStyle);
                        if (item.getVoucherCode() != null) {
                            cell.setCellValue(item.getVoucherCode());
                        } else {
                            cell.setCellValue("");
                        }
                    }
                }
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new

                ByteArrayResource(out.toByteArray());
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

    public static String formatDate(Date date) {
        // Tạo đối tượng SimpleDateFormat với định dạng mong muốn
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        // Định dạng đối tượng Date thành chuỗi
        return formatter.format(date);
    }

    public static String formatDateTime(Date date) {
        // Tạo đối tượng SimpleDateFormat với định dạng ngày và giờ
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // Định dạng đối tượng Date thành chuỗi
        return formatter.format(date);
    }

    public static String formatCurrency(double amount) {
        // Định dạng số theo kiểu tiền tệ Việt Nam với hai chữ số thập phân
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }

}
