package com.globits.hr.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.service.StaffService;

public class ImportStaffExcelUtil {
    @Autowired
    public static StaffService service;

    public static List<StaffDto> importAssetFromInputStream(InputStream is) {
        List<StaffDto> staff = new ArrayList<>();
        try {
            @SuppressWarnings("resource")
            Workbook workbook = new XSSFWorkbook(is);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            int rowIndex = 0;
            int num = datatypeSheet.getLastRowNum();
            while (rowIndex <= num) {
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell;
                if (currentRow != null) {
                    StaffDto dto = new StaffDto();
                    Integer index = 1;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String firstName = String.valueOf(currentCell.getNumericCellValue());
                            dto.setFirstName(firstName);
                            System.out.println(firstName);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String firstName = currentCell.getStringCellValue().trim();
                            dto.setFirstName(firstName);
                            System.out.println(firstName);
                        }
                    }
                    index = 2;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String lastname = String.valueOf(currentCell.getNumericCellValue());
                            dto.setLastName(lastname);
                            System.out.println(lastname);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String lastname = currentCell.getStringCellValue().trim();
                            dto.setLastName(lastname);
                            System.out.println(lastname);
                        }
                    }
                    index = 3;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String displayName = String.valueOf(currentCell.getNumericCellValue());
                            dto.setDisplayName(displayName);
                            System.out.println(displayName);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String displayName = currentCell.getStringCellValue().trim();
                            dto.setDisplayName(displayName);
                            System.out.println(displayName);
                        }
                    }

                    index = 4;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String gender = String.valueOf(currentCell.getNumericCellValue());
                            dto.setGender(gender);
                            System.out.println(gender);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String gender = currentCell.getStringCellValue().trim();
                            dto.setGender(gender);
                            System.out.println(gender);
                        }
                    }
                    index = 5;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String gender = String.valueOf(currentCell.getNumericCellValue());
                            System.out.println(gender);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String gender = currentCell.getStringCellValue().trim();
                            System.out.println(gender);
                        }
                    }
                    index = 6;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String birthPlace = String.valueOf(currentCell.getNumericCellValue());
                            dto.setBirthPlace(birthPlace);
                            System.out.println(birthPlace);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String birthPlace = currentCell.getStringCellValue().trim();
                            dto.setBirthPlace(birthPlace);
                            System.out.println(birthPlace);
                        }
                    }
                    staff.add(dto);
                }
                rowIndex++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    public static void main(String[] args) {
        List<StaffDto> lst;
        try {
            FileInputStream fileIn = new FileInputStream(new File("C:\\Users\\viet\\Documents\\hr.xlsx"));
            lst = importAssetFromInputStream(fileIn);
            for (StaffDto sd : lst) {
                StaffDto dto = new StaffDto();
                dto.setFirstName(sd.getFirstName());
                dto.setLastName(sd.getLastName());
                dto.setDisplayName(sd.getDisplayName());
            }
            System.out.println(lst.size());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
