package com.globits.hr.utils;

import org.apache.poi.ss.usermodel.*;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExcelUtils {
    public static Integer convertToInteger(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Cannot convert '" + s + "' to integer.");
            return null;
        }
    }

    public static <E extends Enum<E>> E getEnumValue(String value, Class<E> enumClass) {
        if (value == null || value.trim().isEmpty()) return null;

        String trimmedValue = value.trim();

        for (E enumConstant : enumClass.getEnumConstants()) {
            // Nếu enum có phương thức getCode, thử map theo code
            try {
                Object code = enumClass.getMethod("getCode").invoke(enumConstant);
                if (code != null && trimmedValue.equalsIgnoreCase(code.toString())) {
                    return enumConstant;
                }
            } catch (Exception ignored) {
            }

            // Fallback: so sánh theo name hoặc toString()
            if (enumConstant.name().equalsIgnoreCase(trimmedValue) ||
                    enumConstant.toString().equalsIgnoreCase(trimmedValue)) {
                return enumConstant;
            }
        }

        // Trả về null nếu không tìm thấy
        return null;
    }

    public static <T> T getCellValue(Cell cell, Class<T> desiredType) {
        if (cell == null) {
            return getDefaultValue(desiredType);
        }

        // Đảm bảo lấy kiểu dữ liệu đúng khi gặp công thức
        CellType cellType = cell.getCellTypeEnum();
        switch (cellType) {
            case STRING:
                return castValue(cell.getStringCellValue().trim(), desiredType);

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return castValue(cell.getDateCellValue(), desiredType);
                } else {
                    return castValue(cell.getNumericCellValue(), desiredType);
                }

            case BOOLEAN:
                return castValue(cell.getBooleanCellValue(), desiredType);

            case BLANK:
                return getDefaultValue(desiredType);

            default:
                throw new IllegalArgumentException("Unsupported cell type");
        }
    }

    private static <T> T getDefaultValue(Class<T> desiredType) {
        if (desiredType == int.class) return (T) Integer.valueOf(0);
        if (desiredType == double.class) return (T) Double.valueOf(0.0);
        if (desiredType == float.class) return (T) Float.valueOf(0.0f);
        if (desiredType == long.class) return (T) Long.valueOf(0L);
        if (desiredType == short.class) return (T) Short.valueOf((short) 0);
        if (desiredType == byte.class) return (T) Byte.valueOf((byte) 0);
        if (desiredType == boolean.class) return (T) Boolean.FALSE;
        return null;
    }

    private static <T> T castValue(Object value, Class<T> desiredType) {
        try {
            if (desiredType.isInstance(value)) {
                return desiredType.cast(value);
            } else if (desiredType == String.class) {
                return desiredType.cast(value.toString());
            } else if (desiredType == Integer.class && value instanceof Number) {
                return desiredType.cast(((Number) value).intValue());
            } else if (desiredType == Double.class && value instanceof Number) {
                return desiredType.cast(((Number) value).doubleValue());
            } else if (desiredType == Boolean.class && value instanceof Boolean) {
                return desiredType.cast(value);
            } else if (desiredType == java.util.Date.class) {
                if (value instanceof java.util.Date) {
                    return desiredType.cast(value);

                } else if (value instanceof Number) {
                    return desiredType.cast(DateUtil.getJavaDate(((Number) value).doubleValue()));

                } else if (value instanceof String) {
                    String stringValue = ((String) value).trim();
                    // Thử parse với một số định dạng phổ biến
                    String[] patterns = {"dd/MM/yyyy", "yyyy-MM-dd", "MM/dd/yyyy"};
                    for (String pattern : patterns) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                            sdf.setLenient(false); // Không cho phép ngày sai format
                            Date parsedDate = sdf.parse(stringValue);
                            return desiredType.cast(parsedDate);
                        } catch (ParseException ignored) {
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            return getDefaultValue(desiredType);
        }
        return getDefaultValue(desiredType);
    }


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

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
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
    
    public static Cell getCellSafe(Row row, int colIndex) {
        return (row == null) ? null : row.getCell(colIndex);
    }

    public static boolean getBooleanValueFromString(String value) {
        if (value == null) return false;
        if (!StringUtils.hasText(value)) return false;

        switch (value) {
            case "true":
            case "TRUE":
            case "x":
            case "X":
            case "Co":
            case "co":
            case "CO":
            case "CÓ":
            case "1":
                return true;

            default:
                return false;
        }
    }

    public static Double parseDoubleCellValue(Cell cell) {
        if (cell == null) return null;

        try {
            int cellType = cell.getCellType();

            // If it's a formula, get the cached result type
            if (cellType == Cell.CELL_TYPE_FORMULA) {
                try {
                    cellType = cell.getCachedFormulaResultType();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            switch (cellType) {
                case Cell.CELL_TYPE_NUMERIC:
                    return cell.getNumericCellValue();

                case Cell.CELL_TYPE_STRING:
                    String str = cell.getStringCellValue().trim();
                    if (str.isEmpty()) return null;
                    try {
                        return Double.parseDouble(str);
                    } catch (NumberFormatException e) {
                        return null; // Or throw if preferred
                    }

                case Cell.CELL_TYPE_BOOLEAN:
                    return cell.getBooleanCellValue() ? 1.0 : 0.0;

                case Cell.CELL_TYPE_BLANK:
                case Cell.CELL_TYPE_ERROR:
                    return null;

                default:
                    String fallback = cell.toString().trim();
                    if (fallback.isEmpty()) return null;
                    try {
                        return Double.parseDouble(fallback);
                    } catch (NumberFormatException e) {
                        return null;
                    }
            }
        } catch (Exception ex) {
            return null;
        }
    }


    public static String parseStringCellValue(Cell cell) {
        if (cell == null) return null;

        try {
            int cellType = cell.getCellType();

            // Nếu là công thức, lấy kiểu kết quả của công thức
            if (cellType == Cell.CELL_TYPE_FORMULA) {
                try {
                    cellType = cell.getCachedFormulaResultType();
                } catch (Exception e) {
                	e.printStackTrace();
                    //return "LỖI CÔNG THỨC";
                    return "";
                }
            }

            switch (cellType) {
                case Cell.CELL_TYPE_STRING:
                    return cell.getStringCellValue().trim();

                case Cell.CELL_TYPE_NUMERIC:
                    double val = cell.getNumericCellValue();
                    if (val == Math.floor(val)) {
                        return String.valueOf((long) val); // là số nguyên
                    } else {
                        return String.format("%.2f", val); // là số thực
                    }

                case Cell.CELL_TYPE_BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());

                case Cell.CELL_TYPE_BLANK:
                    return "";

                case Cell.CELL_TYPE_ERROR:
                    //return "LỖI CÔNG THỨC";
                    return "";

                default:
                    return cell.toString().trim();
            }
        } catch (Exception ex) {
        	//return "LỖI ĐỌC Ô";
            return "";
        }
    }

    public static Date parseDateCellValue(Cell cell, int rowIndex, int columnIndex, SimpleDateFormat dateFormat) {
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
                        System.err.println(
                                String.format("[RowIndex: %d] [ColumnIndex: %d] Sai định dạng năm, Giá trị: %s",
                                        rowIndex, columnIndex, strDate));
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
                    System.err.println(String.format("[RowIndex: %d] [ColumnIndex: %d] Sai định dạng năm, Giá trị: %s",
                            rowIndex, columnIndex, cell.getNumericCellValue()));
                    return null;
                }

            } else {
                System.err.println(String.format("[RowIndex: %d] [ColumnIndex: %d] Không hỗ trợ kiểu dữ liệu: %s",
                        rowIndex, columnIndex, cellType));
            }

        } catch (Exception ex) {
            System.err.println(String.format("[RowIndex: %d] [ColumnIndex: %d] Sai định dạng ngày tháng, Giá trị: %s",
                    rowIndex, columnIndex, cellType == CellType.STRING ? cell.getStringCellValue() : cell.toString()));
            // ex.printStackTrace();
        }

        return result;
    }

}
