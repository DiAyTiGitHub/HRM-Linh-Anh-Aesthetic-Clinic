package com.globits.hr.utils;

import com.globits.core.dto.*;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.EvaluationTemplate;
import com.globits.hr.domain.ShiftWork;
import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.dto.*;
import com.globits.hr.dto.function.ImportExcelMessageDto;
import com.globits.hr.dto.function.ImportStaffDto;
import com.globits.hr.dto.function.PositionTitleStaffDto;
import com.globits.hr.dto.function.StaffFamilyRelationshipFunctionDto;
import com.globits.hr.dto.importExcel.CandidateImport;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.security.dto.UserDto;
import com.globits.template.domain.ContentTemplate;
import com.globits.template.dto.ContentTemplateDto;
import com.globits.timesheet.dto.LeaveRequestDto;
import com.globits.timesheet.dto.LeaveTypeDto;
import com.globits.timesheet.dto.TimeSheetShiftWorkPeriodDto;
import com.globits.timesheet.dto.TimekeepingItemDto;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

@Component
public class ImportExportExcelUtil {
    private static final Logger logger = LoggerFactory.getLogger(ImportExportExcelUtil.class);
    private static Hashtable<String, Integer> hashStaffColumnConfig = new Hashtable<>();
    private static Hashtable<String, Integer> hashDepartmentColumnConfig = new Hashtable<>();
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static DecimalFormat numberFormatter = new DecimalFormat("######################");
    private static Hashtable<String, String> hashColumnPropertyConfig = new Hashtable<>();

    private static void scanStaffColumnExcelIndex(Sheet datatypeSheet, int scanRowIndex) {
        Row row = datatypeSheet.getRow(scanRowIndex);
        int numberCell = row.getPhysicalNumberOfCells();

        hashColumnPropertyConfig.put("staffCode".toLowerCase(), "staffCode");
        hashColumnPropertyConfig.put("firstName".toLowerCase(), "firstName");
        hashColumnPropertyConfig.put("lastName".toLowerCase(), "lastName");
        hashColumnPropertyConfig.put("displayName".toLowerCase(), "displayName");
        hashColumnPropertyConfig.put("birthdate".toLowerCase(), "birthdate");
        hashColumnPropertyConfig.put("birthdateMale".toLowerCase(), "birthdateMale");
        hashColumnPropertyConfig.put("birthdateFeMale".toLowerCase(), "birthdateFeMale");
        hashColumnPropertyConfig.put("gender".toLowerCase(), "gender");
        hashColumnPropertyConfig.put("address".toLowerCase(), "address");// Cái này cần xem lại
        hashColumnPropertyConfig.put("userName".toLowerCase(), "userName");
        hashColumnPropertyConfig.put("password".toLowerCase(), "password");
        hashColumnPropertyConfig.put("email".toLowerCase(), "email");
        hashColumnPropertyConfig.put("BirthPlace".toLowerCase(), "BirthPlace");

        hashColumnPropertyConfig.put("departmentCode".toLowerCase(), "departmentCode");
        hashColumnPropertyConfig.put("MaNgach".toLowerCase(), "MaNgach");
        hashColumnPropertyConfig.put("IDCard".toLowerCase(), "IDCard");

        for (int i = 0; i < numberCell; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellTypeEnum() == CellType.STRING) {
                String cellValue = cell.getStringCellValue();
                if (cellValue != null && cellValue.length() > 0) {
                    cellValue = cellValue.toLowerCase().trim();
                    String propertyName = hashColumnPropertyConfig.get(cellValue);
                    if (propertyName != null) {
                        hashStaffColumnConfig.put(propertyName, i);
                    }
                }
            }
        }
    }

    public static List<DepartmentDto> getListDepartmentFromInputStream(InputStream is) {
        try {
            List<DepartmentDto> ret = new ArrayList<>();
            @SuppressWarnings("resource")
            Workbook workbook = new XSSFWorkbook(is);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            int rowIndex = 4;

            hashDepartmentColumnConfig.put("code", 0);
            hashDepartmentColumnConfig.put("name", 1);

            int num = datatypeSheet.getLastRowNum();
            while (rowIndex <= num) {
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell = null;
                if (currentRow != null) {
                    DepartmentDto department = new DepartmentDto();
                    Integer index = hashDepartmentColumnConfig.get("code");
                    if (index != null) {
                        currentCell = currentRow.getCell(index);// code
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            double value = currentCell.getNumericCellValue();
                            String code = numberFormatter.format(value);
                            department.setCode(code);
                        } else if (currentCell != null && currentCell.getStringCellValue() != null) {
                            String code = currentCell.getStringCellValue();
                            department.setCode(code);
                        }
                    }
                    index = hashDepartmentColumnConfig.get("name");
                    if (index != null) {
                        currentCell = currentRow.getCell(index);// name
                        if (currentCell != null && currentCell.getStringCellValue() != null) {
                            String name = currentCell.getStringCellValue();
                            department.setName(name);
                        }
                    }
                    ret.add(department);
                }
                rowIndex++;
            }
            return ret;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<StaffDto> getListStaffFromInputStream(InputStream is) {
        try {
            List<StaffDto> ret = new ArrayList<>();
            @SuppressWarnings("resource")
            Workbook workbook = new XSSFWorkbook(is);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            int rowIndex = 1;
            int scanRowIndex = 0;
            scanStaffColumnExcelIndex(datatypeSheet, scanRowIndex);

            int num = datatypeSheet.getLastRowNum();
            while (rowIndex <= num) {
                System.out.println("rowIndex=" + rowIndex);
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell;
                if (currentRow != null) {
                    StaffDto staff = new StaffDto();
                    Integer index = hashStaffColumnConfig.get("staffCode");
                    if (index != null) {
                        currentCell = currentRow.getCell(index);// staffCode
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            double value = currentCell.getNumericCellValue();
                            String staffCode = numberFormatter.format(value);
                            staff.setStaffCode(staffCode);
                        } else if (currentCell != null && currentCell.getStringCellValue() != null) {
                            String staffCode = currentCell.getStringCellValue();
                            if (staffCode != null) {
                                staffCode = staffCode.trim();
                            }
                            staff.setStaffCode(staffCode);
                        }
                    }
                    if (staff.getStaffCode() != null && staff.getStaffCode().length() > 0) {
                        index = hashStaffColumnConfig.get("firstName");
                        if (index != null) {
                            currentCell = currentRow.getCell(index);// firstName
                            if (currentCell != null && currentCell.getStringCellValue() != null) {
                                String firstName = currentCell.getStringCellValue();
                                staff.setFirstName(firstName);
                            }
                        }
                        index = hashStaffColumnConfig.get("lastName");
                        if (index != null) {
                            currentCell = currentRow.getCell(index);// lastName
                            if (currentCell != null && currentCell.getStringCellValue() != null) {
                                String lastName = currentCell.getStringCellValue();
                                staff.setLastName(lastName);
                            }
                        }
                        index = hashStaffColumnConfig.get("displayName");
                        if (index != null) {
                            currentCell = currentRow.getCell(index);// lastName
                            if (currentCell != null && currentCell.getStringCellValue() != null) {
                                String displayName = currentCell.getStringCellValue();
                                staff.setDisplayName(displayName);
                                if (staff.getFirstName() == null) {
                                    int lastIndex = displayName.lastIndexOf(' ');
                                    if (lastIndex > 0 && lastIndex < displayName.length()) {
                                        int endIndex = displayName.length();
                                        String firstName = displayName.substring(lastIndex, endIndex);
                                        String lastName = displayName.substring(0, lastIndex);
                                        staff.setFirstName(firstName);
                                        staff.setLastName(lastName);
                                    }
                                }
                            }
                        }
                        index = hashStaffColumnConfig.get("birthdateMale");
                        if (index != null) {
                            currentCell = currentRow.getCell(index);// birthdate
                            if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                    && currentCell.getStringCellValue() != null) {
                                String strBirthdate = currentCell.getStringCellValue();
                                if (strBirthdate != null) {
                                    try {
                                        Date birthDate = dateFormat.parse(strBirthdate);
                                        staff.setBirthDate(birthDate);
                                        staff.setGender("M");
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                                double dateValue = currentCell.getNumericCellValue();
                                if (dateValue > 0) {
                                    try {
                                        Date birthDate = new Date(Math.round(dateValue));
                                        staff.setBirthDate(birthDate);
                                        staff.setGender("F");
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                        index = hashStaffColumnConfig.get("birthdateFeMale");
                        if (index != null) {
                            currentCell = currentRow.getCell(index);// birthdate
                            if (currentCell != null && currentCell.getStringCellValue() != null
                                    && currentCell.getCellTypeEnum() == CellType.STRING) {
                                String strBirthdate = currentCell.getStringCellValue();
                                if (strBirthdate != null) {
                                    try {
                                        Date birthDate = dateFormat.parse(strBirthdate);
                                        staff.setBirthDate(birthDate);
                                        staff.setGender("M");
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                                double dateValue = currentCell.getNumericCellValue();
                                if (dateValue > 0) {
                                    try {
                                        Date birthDate = new Date(Math.round(dateValue));
                                        staff.setBirthDate(birthDate);
                                        staff.setGender("M");
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                        index = hashStaffColumnConfig.get("birthdate");
                        if (index != null) {
                            currentCell = currentRow.getCell(index);// birthdate
                            if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                    && currentCell.getStringCellValue() != null) {
                                String strBirthdate = currentCell.getStringCellValue();
                                if (strBirthdate != null) {
                                    try {
                                        Date birthDate = dateFormat.parse(strBirthdate);
                                        staff.setBirthDate(birthDate);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                                double dateValue = currentCell.getNumericCellValue();
                                if (dateValue > 0) {
                                    try {
                                        Date birthDate = new Date(Math.round(dateValue));
                                        staff.setBirthDate(birthDate);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                        index = hashStaffColumnConfig.get("gender");
                        if (index != null) {
                            currentCell = currentRow.getCell(index);// gender
                            if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                    && currentCell.getStringCellValue() != null) {
                                String gender = currentCell.getStringCellValue();
                                if (gender.equals("0")) {
                                    staff.setGender("M");
                                } else if (gender.equals("1")) {
                                    staff.setGender("F");
                                } else {
                                    staff.setGender("U");
                                }
                            } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                                int dateValue = (int) currentCell.getNumericCellValue();
                                if (dateValue == 0) {
                                    staff.setGender("M");
                                } else if (dateValue == 1) {
                                    staff.setGender("F");
                                } else {
                                    staff.setGender("U");
                                }
                            }
                        }

                        index = hashStaffColumnConfig.get("IDCard");// cmnd
                        if (index != null) {
                            currentCell = currentRow.getCell(index);// cmnd

                            if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                    && currentCell.getStringCellValue() != null) {
                                String cmnd = currentCell.getStringCellValue();
                                if (cmnd != null) {
                                    staff.setIdNumber(cmnd);
                                }
                            } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                                double value = currentCell.getNumericCellValue();
                                String cmnd = numberFormatter.format(value);
                                if (cmnd != null) {
                                    staff.setIdNumber(cmnd);
                                }
                            }
                        }

                        index = hashStaffColumnConfig.get("userName"); // create userName nếu có
                        if (index != null) {
                            currentCell = currentRow.getCell(index);// userName
                            String userName = null;
                            if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING) {
                                userName = currentCell.getStringCellValue();
                            } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                                userName = "" + currentCell.getNumericCellValue();
                            }
                            if (userName != null) {
                                UserDto user = new UserDto();
                                user.setUsername(userName);
                                index = hashStaffColumnConfig.get("password");
                                if (index != null) {
                                    currentCell = currentRow.getCell(index);// password
                                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                                        double dPassword = currentCell.getNumericCellValue();
                                        String password = numberFormatter.format(dPassword);
                                        user.setPassword(password);
                                    } else if (currentCell != null && currentCell.getStringCellValue() != null) {
                                        String password = currentCell.getStringCellValue();
                                        user.setPassword(password);
                                    }
                                } else {
                                    user.setPassword("123456");
                                }
                                index = hashStaffColumnConfig.get("email");
                                if (index != null) {
                                    currentCell = currentRow.getCell(index);// email
                                    if (currentCell != null && currentCell.getStringCellValue() != null) {
                                        String email = currentCell.getStringCellValue();
                                        user.setEmail(email);
                                        staff.setUser(user);
                                    }
                                } else {
                                    String email = staff.getStaffCode() + "@tlu.edu.vn";
                                    user.setEmail(email);
                                    staff.setUser(user);
                                }
                            } else if (staff.getStaffCode() != null) {// lấy tạm userName là staffCode
                                UserDto user = new UserDto();
                                user.setUsername(staff.getStaffCode());
                                index = hashStaffColumnConfig.get("password");
                                if (index != null) {
                                    currentCell = currentRow.getCell(index);// password
                                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                                        double dPassword = currentCell.getNumericCellValue();
                                        String password = numberFormatter.format(dPassword);
                                        user.setPassword(password);
                                    } else if (currentCell != null && currentCell.getStringCellValue() != null) {
                                        String password = currentCell.getStringCellValue();
                                        user.setPassword(password);
                                    }
                                } else if (staff.getIdNumber() != null) {
                                    user.setPassword(staff.getIdNumber());
                                } else {
                                    user.setPassword("123456");
                                }
                                index = hashStaffColumnConfig.get("email");
                                if (index != null) {
                                    currentCell = currentRow.getCell(index);// email
                                    if (currentCell != null && currentCell.getStringCellValue() != null) {
                                        String email = currentCell.getStringCellValue();
                                        user.setEmail(email);
                                        staff.setUser(user);
                                    }
                                } else {
                                    String email = staff.getStaffCode() + "@tlu.edu.vn";
                                    user.setEmail(email);
                                    staff.setUser(user);
                                }
                            }
                        } else if (staff.getStaffCode() != null) {// lấy tạm userName là staffCode
                            UserDto user = new UserDto();
                            user.setUsername(staff.getStaffCode());
                            index = hashStaffColumnConfig.get("password");
                            if (index != null) {
                                currentCell = currentRow.getCell(index);// password
                                if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                                    double dPassword = currentCell.getNumericCellValue();
                                    String password = numberFormatter.format(dPassword);
                                    user.setPassword(password);
                                } else if (currentCell != null && currentCell.getStringCellValue() != null) {
                                    String password = currentCell.getStringCellValue();
                                    user.setPassword(password);
                                }
                            } else if (staff.getIdNumber() != null) {
                                user.setPassword(staff.getIdNumber());
                            } else {
                                user.setPassword("123456");
                            }
                            index = hashStaffColumnConfig.get("email");
                            if (index != null) {
                                currentCell = currentRow.getCell(index);// email
                                if (currentCell != null && currentCell.getStringCellValue() != null) {
                                    String email = currentCell.getStringCellValue();
                                    user.setEmail(email);
                                    staff.setUser(user);
                                }
                            } else {
                                String email = staff.getStaffCode() + "@tlu.edu.vn";
                                user.setEmail(email);
                                staff.setUser(user);
                            }
                        }
                        index = hashStaffColumnConfig.get("departmentCode");
                        if (index != null) {
                            currentCell = currentRow.getCell(index);// userName
                            if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING) {
                                String departmentCode = currentCell.getStringCellValue();
                                PositionStaffDto position = new PositionStaffDto();
                                position.setHrDepartment(new HRDepartmentDto());
                                position.getHrDepartment().setCode(departmentCode);
                                position.setPosition(new PositionDto());
                                staff.getPositions().add(position);
                            }
                        }

                        index = hashStaffColumnConfig.get("BirthPlace");
                        if (index != null) {
                            currentCell = currentRow.getCell(index);// birthPlace
                            if (currentCell != null && currentCell.getStringCellValue() != null) {
                                String birthPlace = currentCell.getStringCellValue();
                                staff.setBirthPlace(birthPlace);
                            }
                        }
                        index = hashStaffColumnConfig.get("ethnic");
                        if (index != null) {
                            currentCell = currentRow.getCell(index);// ethnic
                            if (currentCell != null && currentCell.getStringCellValue() != null) {
                                String ethnicCode = currentCell.getStringCellValue();
                                EthnicsDto ethnicsDto = new EthnicsDto();
                                ethnicsDto.setCode(ethnicCode);
                                staff.setEthnics(ethnicsDto);
                            }
                        }
                        index = hashStaffColumnConfig.get("religion");
                        if (index != null) {
                            currentCell = currentRow.getCell(index);// religion
                            if (currentCell != null && currentCell.getStringCellValue() != null) {
                                String religionCode = currentCell.getStringCellValue();
                                ReligionDto religionDto = new ReligionDto();
                                religionDto.setCode(religionCode);
                                staff.setReligion(religionDto);
                            }
                        }
                        index = hashStaffColumnConfig.get("phoneNumber");
                        if (index != null) {
                            currentCell = currentRow.getCell(index);// phoneNumber
                            if (currentCell != null && currentCell.getStringCellValue() != null) {
                                String phoneNumber = currentCell.getStringCellValue();
                                staff.setPhoneNumber(phoneNumber);
                            }
                        }
//						index = hashStaffColumnConfig.get("MaNgach");
//						if (index != null) {
//							currentCell = currentRow.getCell(index);// MaNgach
//							if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
//								double value = currentCell.getNumericCellValue();
//								String code = numberFormatter.format(value);
//								
//							} else if (currentCell != null && currentCell.getStringCellValue() != null) {
//								String code = currentCell.getStringCellValue();
//								if(code!=null) {
//									
//								}
//								
//							}
//						}

                        ret.add(staff);
                    }
                }
                rowIndex++;
            }
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ByteArrayResource exportStaffToExcelTable(List<StaffDto> dataList, Boolean isCheck)
            throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        /* Tạo font */
        XSSFFont fontBold = workbook.createFont();
        fontBold.setBold(true); // set bold
        fontBold.setFontHeight(10); // add font size

        XSSFFont fontBoldTitle = workbook.createFont();
        fontBoldTitle.setBold(true); // set bold
        fontBoldTitle.setFontHeight(15); // add font size

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
        cell.setCellValue("Mã nhân viên");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Họ và tên");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Ngày sinh");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Giới tính");
        cell.setCellStyle(tableHeadCellStyle);

//        cell = row.createCell(4);
//        cell.setCellValue("Trình độ");
//        cell.setCellStyle(tableHeadCellStyle);
//
//        cell = row.createCell(5);
//        cell.setCellValue("Loại viên chức");
//        cell.setCellStyle(tableHeadCellStyle);
//
//        cell = row.createCell(6);
//        cell.setCellValue("Mã nghạch ");
//        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Quê quán");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(5);
        cell.setCellValue("Phòng làm việc");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(6);
        cell.setCellValue("Trạng thái làm việc");
        cell.setCellStyle(tableHeadCellStyle);

//		if(isCheck) {
//			cell = row.createCell(4);
//			cell.setCellValue("Tên người nhận");
//			cell.setCellStyle(tableHeadCellStyle);
//			
//			cell = row.createCell(5);
//			cell.setCellValue("Số điện thoại");
//			cell.setCellStyle(tableHeadCellStyle);
//			
//			cell = row.createCell(6);
//			cell.setCellValue("Email");
//			cell.setCellStyle(tableHeadCellStyle);
//			
//			cell = row.createCell(7);
//			cell.setCellValue("Kết quả");
//			cell.setCellStyle(tableHeadCellStyle);
//			
//			cell = row.createCell(8);
//			cell.setCellValue("Trạng thái vận chuyển mẫu chính");
//			cell.setCellStyle(tableHeadCellStyle);
//			
//			cell = row.createCell(9);
//			cell.setCellValue("Bộ mấu đối chiếu");
//			cell.setCellStyle(tableHeadCellStyle);
//		}
        // Tạo các hàng cột dữ liệu
        XSSFRow tableDataRow;
        if (dataList != null && !dataList.isEmpty()) {
            for (int i = 0; i < dataList.size(); i++) {
//				  String domicile = ""; 
//                String qualification = "";
//                String civilServantCategory = "";
//                String civilServantType = "";
                String staffStatus = "", officeName = "", address = "";
                tableDataRow = sheet.createRow(i + 1);
                StaffDto data = dataList.get(i);

                // Dữ liệu kết quả
//                if (data != null) {
//                    if (data.getQualification() == null) {
//                        qualification += "";
//                    } else {
//                        qualification += data.getQualification();
//                    }
//
//                }
//                if (data != null) {
//                    if (data.getCivilServantType() == null) {
//                        civilServantType += "";
//                    } else {
//                        civilServantType += data.getCivilServantType().getName();
//                    }
//
//                }
//                if (data != null) {
//                    if (data.getCivilServantCategory() == null) {
//                        civilServantCategory += "";
//                    } else {
//                        civilServantCategory += data.getCivilServantCategory().getName();
//                    }
//
//                }
//                if (data != null) {
//                    if (data.getAddress() == null) {
//                        domicile += "";
//                    } else {
//                        if (data.getAddress().size() > 0) {
//                            boolean isCheckAdd = false;
//                            for (PersonAddressDto dto : data.getAddress()) {
//                                if (dto.getType() == 3) {
//                                    isCheckAdd = true;
//                                    domicile = dto.getAddress();
//                                }
//                            }
//                            if (!isCheckAdd) {
//                                domicile += "";
//                            }
//                        }
//                    }
//                    
//                }

                if (data != null) {
                    //
                    if (data.getProvince() != null && data.getProvince().getName() != null) {
                        address += data.getProvince().getName();
                    }
                    if (data.getDistrict() != null && data.getDistrict().getName() != null) {
                        if (address != "") {
                            address += ",";
                        }
                        address += data.getDistrict().getName();
                    }
                    if (data.getAdministrativeunit() != null && data.getAdministrativeunit().getName() != null) {
                        if (address != "") {
                            address += ",";
                        }
                        address += data.getAdministrativeunit().getName();
                    }
                    if (data.getPermanentResidence() != null) {
                        if (address != "") {
                            address += ",";
                        }
                        address += data.getPermanentResidence();
                    }
                    //
                    if (data.getDepartment() != null && data.getDepartment().getName() != null) {
                        officeName = data.getDepartment().getName();
                    }
                    if (data.getStatus() != null && data.getStatus().getName() != null) {
                        staffStatus = data.getStatus().getName();
                    }

                    tableDataRow.createCell(0).setCellValue(data.getStaffCode());
                    tableDataRow.createCell(1).setCellValue(data.getDisplayName());
                    tableDataRow.createCell(2).setCellValue(data.getBirthDate());
                    tableDataRow.createCell(3).setCellValue(data.getGender());
                    tableDataRow.createCell(4).setCellValue(address);
                    tableDataRow.createCell(5).setCellValue(officeName);
                    tableDataRow.createCell(6).setCellValue(staffStatus);
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

    public static ImportStaffDto importStaffFromInputStream(InputStream is) {
        ImportStaffDto importStaffDto = new ImportStaffDto();
        List<StaffDto> staff = new ArrayList<>();
        List<ImportExcelMessageDto> listMessage = new ArrayList<>();
        try {
            // cảnh báo
            @SuppressWarnings("resource")
            Workbook workbook = new XSSFWorkbook(is);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            int rowIndex = 1;
            int num = datatypeSheet.getLastRowNum();
            Calendar calendar = Calendar.getInstance();
            while (rowIndex <= num) {
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell;
                ImportExcelMessageDto message = new ImportExcelMessageDto();
                if (currentRow != null) {
                    StaffDto dto = new StaffDto();

                    // firstName
                    Integer index = 0;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String firstName = String.valueOf(currentCell.getNumericCellValue());
                        dto.setFirstName(firstName);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String firstName = currentCell.getStringCellValue().trim();
                        dto.setFirstName(firstName);
                    }
                    // lastname
                    index = 1;
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
                    // displayName
                    index = 2;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String displayName = String.valueOf(currentCell.getNumericCellValue());
                        dto.setDisplayName(displayName);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String displayName = currentCell.getStringCellValue().trim();
                        dto.setDisplayName(displayName);
                    }
                    // gender
                    index = 3;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String gender = String.valueOf((int) currentCell.getNumericCellValue());
                        if (gender.equals("1")) {
                            dto.setGender("F");
                        } else if (gender.equals("0")) {
                            dto.setGender("M");
                        } else {
                            dto.setGender("U");
                        }
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String gender = currentCell.getStringCellValue().trim();
                        if (gender.equals("1")) {
                            dto.setGender("F");
                        } else if (gender.equals("0")) {
                            dto.setGender("M");
                        } else {
                            dto.setGender("U");
                        }
                    }
                    //
                    index = 4;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                            calendar.setTime(currentCell.getDateCellValue());
                            dto.setBirthDate(calendar.getTime());
                        }
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                            calendar.setTime(currentCell.getDateCellValue());
                            dto.setBirthDate(calendar.getTime());
                        }
                    }
                    // birthPlace
                    index = 5;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String birthPlace = String.valueOf(currentCell.getNumericCellValue());
                        dto.setBirthPlace(birthPlace);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String birthPlace = currentCell.getStringCellValue().trim();
                        dto.setBirthPlace(birthPlace);
                        System.out.println(birthPlace);
                    }

                    Set<PersonAddressDto> listAdd = new HashSet<>();
                    index = 6;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String address = String.valueOf(currentCell.getNumericCellValue());
                        System.out.println(address);
                        PersonAddressDto personAddressDto = new PersonAddressDto();
                        personAddressDto.setAddress(address);
                        personAddressDto.setType(1);
                        listAdd.add(personAddressDto);
                        dto.setAddress(listAdd);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        PersonAddressDto personAddressDto = new PersonAddressDto();
                        String address = String.valueOf(currentCell.getStringCellValue());
                        System.out.println(address);
                        personAddressDto.setAddress(address);
                        personAddressDto.setType(1);
                        listAdd.add(personAddressDto);
                        dto.setAddress(listAdd);
                    }
                    // Address
                    index = 7;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String address = String.valueOf(currentCell.getNumericCellValue());
                        PersonAddressDto personAddressDto = new PersonAddressDto();
                        personAddressDto.setAddress(address);
                        personAddressDto.setType(2);
                        listAdd.add(personAddressDto);
                        dto.setAddress(listAdd);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String address = String.valueOf(currentCell.getStringCellValue());
                        PersonAddressDto personAddressDto = new PersonAddressDto();
                        personAddressDto.setAddress(address);
                        personAddressDto.setType(2);
                        listAdd.add(personAddressDto);
                        dto.setAddress(listAdd);
                    }
                    // NativePlace
                    index = 8;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String address = String.valueOf(currentCell.getNumericCellValue());
                        PersonAddressDto personAddressDto = new PersonAddressDto();
                        personAddressDto.setAddress(address);
                        personAddressDto.setType(3);
                        listAdd.add(personAddressDto);
                        dto.setAddress(listAdd);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String address = String.valueOf(currentCell.getStringCellValue());
                        PersonAddressDto personAddressDto = new PersonAddressDto();
                        personAddressDto.setAddress(address);
                        personAddressDto.setType(3);
                        listAdd.add(personAddressDto);
                        dto.setAddress(listAdd);
                    }
                    // IDCard
                    index = 9;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String iDCard = String.valueOf((long) currentCell.getNumericCellValue());
                        dto.setIdNumber(iDCard);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String iDCard = String.valueOf(currentCell.getStringCellValue());
                        dto.setIdNumber(iDCard);
                    }
                    // NgayCapCMT
                    index = 10;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                            calendar.setTime(currentCell.getDateCellValue());
                            dto.setIdNumberIssueDate(calendar.getTime());
                        }

                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                            calendar.setTime(currentCell.getDateCellValue());
                            dto.setIdNumberIssueDate(calendar.getTime());
                        }
                    }
                    // NoiCapCMT
                    index = 11;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String idNumberIssueBy = String.valueOf(currentCell.getNumericCellValue());
                        dto.setIdNumberIssueBy(idNumberIssueBy);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String idNumberIssueBy = String.valueOf(currentCell.getStringCellValue());
                        dto.setIdNumberIssueBy(idNumberIssueBy);
                    }
                    // nationality
                    index = 12;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String nationality = String.valueOf(currentCell.getNumericCellValue());
                        dto.setNationalityCode(nationality);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String nationality = String.valueOf(currentCell.getStringCellValue());
                        dto.setNationalityCode(nationality);
                    }
                    // ethnics
                    index = 13;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String ethnics = String.valueOf(currentCell.getNumericCellValue());
                        dto.setEthnicsCode(ethnics);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String ethnics = String.valueOf(currentCell.getStringCellValue());
                        dto.setEthnicsCode(ethnics);
                    }
                    // religion
                    index = 14;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String religion = String.valueOf(currentCell.getNumericCellValue());
                        dto.setReligionCode(religion);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String religion = String.valueOf(currentCell.getStringCellValue());
                        dto.setReligionCode(religion);
                    }
                    // Email
                    index = 15;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String email = String.valueOf(currentCell.getNumericCellValue());
                        dto.setEmail(email);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String email = String.valueOf(currentCell.getStringCellValue());
                        dto.setEmail(email);
                    }
                    // Mobile
                    index = 16;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String mobile = String.valueOf((long) currentCell.getNumericCellValue());
                        dto.setPhoneNumber(mobile);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String mobile = String.valueOf(currentCell.getStringCellValue());
                        dto.setPhoneNumber(mobile);
                    }
                    // tinhtranghonnhan
                    index = 17;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String maritalStatus = String.valueOf(currentCell.getNumericCellValue());
//						dto.setMaritalStatus();
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String maritalStatus = String.valueOf(currentCell.getStringCellValue());
//						dto.setMaritalStatus(maritalStatus);
                    }
                    // trangThaiNhanVien
                    index = 18;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String status = String.valueOf(currentCell.getNumericCellValue());
                        dto.setStatusCode(status);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String status = String.valueOf(currentCell.getStringCellValue());
                        dto.setStatusCode(status);
                    }
                    // maPhongBan
                    index = 19;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String department = String.valueOf((int) currentCell.getNumericCellValue());
                        dto.setDepartmentCode(department);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String department = currentCell.getStringCellValue().trim();
                        dto.setDepartmentCode(department);
                    }
                    // maNhanVien
                    index = 20;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String staffCode = String.valueOf(currentCell.getNumericCellValue());
                        dto.setStaffCode(staffCode);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String staffCode = currentCell.getStringCellValue().trim();
                        dto.setStaffCode(staffCode);
                    } else if (currentCell == null) {
                        message.setIndex(rowIndex + "");
                        message.setMessage("Không có mã nhân viên");
                    }
                    // LoaiVienChuc
                    index = 21;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String civilServantTypeCode = String.valueOf(currentCell.getNumericCellValue());
                        dto.setCivilServantTypeCode(civilServantTypeCode);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String civilServantTypeCode = String.valueOf(currentCell.getStringCellValue());
                        dto.setCivilServantTypeCode(civilServantTypeCode);
                    }
                    // Ngachcongchuc
                    index = 22;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String civilServantCategoryCode = String.valueOf(currentCell.getNumericCellValue());
                        dto.setCivilServantCategoryCode(civilServantCategoryCode);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String civilServantCategoryCode = String.valueOf(currentCell.getStringCellValue());
                        dto.setCivilServantCategoryCode(civilServantCategoryCode);
                    }
                    // loaiHopDong
                    index = 23;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String labourAgreementTypeCode = String.valueOf(currentCell.getNumericCellValue());
                        dto.setLabourAgreementTypeCode(labourAgreementTypeCode);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String labourAgreementTypeCode = String.valueOf(currentCell.getStringCellValue());
                        dto.setLabourAgreementTypeCode(labourAgreementTypeCode);
                    }
                    // ngayhopdong
                    index = 24;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                            calendar.setTime(currentCell.getDateCellValue());
                            dto.setContractDate(calendar.getTime());
                        }
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                            calendar.setTime(currentCell.getDateCellValue());
                            dto.setContractDate(calendar.getTime());
                        }
                    }
                    // NgayTuyenDung
                    index = 25;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                            calendar.setTime(currentCell.getDateCellValue());
                            dto.setRecruitmentDate(calendar.getTime());
                        }

                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                            calendar.setTime(currentCell.getDateCellValue());
                            dto.setRecruitmentDate(calendar.getTime());
                        }
                    }
                    // chucDanhChuyenMon
                    index = 26;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String professionalTitles = String.valueOf(currentCell.getNumericCellValue());
                        dto.setProfessionalTitles(professionalTitles);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String professionalTitles = String.valueOf(currentCell.getStringCellValue());
                        dto.setProfessionalTitles(professionalTitles);
                    }
                    // congViecDuocGiao
                    index = 27;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String profession = String.valueOf(currentCell.getNumericCellValue());
                        dto.setProfessionCode(profession);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String profession = String.valueOf(currentCell.getStringCellValue());
                        dto.setProfessionCode(profession);
                    }
                    // BacLuong
                    index = 28;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String salaryLeve = String.valueOf(currentCell.getNumericCellValue());
                        dto.setSalaryLeve(salaryLeve);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String salaryLeve = String.valueOf(currentCell.getStringCellValue());
                        dto.setSalaryLeve(salaryLeve);
                    }
                    // HeSoLuong
                    index = 29;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String salaryCoefficient = String.valueOf(currentCell.getNumericCellValue());
                        dto.setSalaryCoefficient(salaryCoefficient);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String salaryCoefficient = String.valueOf(currentCell.getStringCellValue());
                        dto.setSalaryCoefficient(salaryCoefficient);
                    }
                    // NgayHuongBacLuong
                    index = 30;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                            calendar.setTime(currentCell.getDateCellValue());
                            dto.setSalaryStartDate(calendar.getTime());
                        }
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                            calendar.setTime(currentCell.getDateCellValue());
                            dto.setSalaryStartDate(calendar.getTime());
                        }
                    }
                    // SoSoBaoHiemXaHoi
                    index = 31;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String socialInsuranceNumber = String.valueOf(currentCell.getNumericCellValue());
                        dto.setSocialInsuranceNumber(socialInsuranceNumber);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String socialInsuranceNumber = String.valueOf(currentCell.getStringCellValue());
                        dto.setSocialInsuranceNumber(socialInsuranceNumber);
                    }
                    // Học hàm
                    index = 32;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String academicTitleCode = String.valueOf(currentCell.getNumericCellValue());
                        dto.setAcademicTitleCode(academicTitleCode);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String academicTitleCode = currentCell.getStringCellValue().trim();
                        dto.setAcademicTitleCode(academicTitleCode);
                    }
                    // Học vị
                    index = 33;
                    currentCell = currentRow.getCell(index);
                    if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        String educationDegreeCode = String.valueOf(currentCell.getNumericCellValue());
                        dto.setEducationDegreeCode(educationDegreeCode);
                    } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                            && currentCell.getStringCellValue() != null) {
                        String educationDegreeCode = currentCell.getStringCellValue().trim();
                        dto.setEducationDegreeCode(educationDegreeCode.trim());
                    }
                    staff.add(dto);
                    if (message.getIndex() != null) {
                        listMessage.add(message);
                    }
                }
                rowIndex++;
            }
            if (listMessage.size() > 0) {
                importStaffDto.setListMessage(listMessage);
            }
            importStaffDto.setListStaff(staff);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return importStaffDto;
    }

    public static List<PositionTitleStaffDto> importPositionProcessFromInputStream(InputStream is) {
        List<PositionTitleStaffDto> listData = new ArrayList<>();
        try {
            @SuppressWarnings("resource")
            Workbook workbook = new XSSFWorkbook(is);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            int rowIndex = 1;
            Calendar calendar = Calendar.getInstance();
            int num = datatypeSheet.getLastRowNum();
            while (rowIndex <= num) {
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell;
                if (currentRow != null) {
                    PositionTitleStaffDto dto = new PositionTitleStaffDto();
                    Integer index = 0;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String staffCode = String.valueOf(currentCell.getNumericCellValue());
                            dto.setStaffCode(staffCode);
                            System.out.println(staffCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String staffCode = currentCell.getStringCellValue().trim();
                            dto.setStaffCode(staffCode);
                            System.out.println(staffCode);
                        }
                    }

                    index = 1;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String potitionTitleCode = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setPositionTitleCode(potitionTitleCode);
                            System.out.println(potitionTitleCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String potitionTitleCode = currentCell.getStringCellValue().trim();
                            dto.setPositionTitleCode(potitionTitleCode);
                            System.out.println(potitionTitleCode);
                        }
                    }
                    index = 2;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String decisionCode = String.valueOf(currentCell.getNumericCellValue());
                            dto.setDecisionCode(decisionCode);
                            System.out.println(decisionCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String decisionCode = currentCell.getStringCellValue().trim();
                            dto.setDecisionCode(decisionCode);
                            System.out.println(decisionCode);
                        }
                    }
                    index = 3;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String allowanceCoefficient = String.valueOf(currentCell.getNumericCellValue());
                            dto.setAllowanceCoefficient(currentCell.getNumericCellValue());
                            System.out.println(allowanceCoefficient);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String allowanceCoefficient = currentCell.getStringCellValue().trim();
                            dto.setAllowanceCoefficient(Double.valueOf(allowanceCoefficient));
                            System.out.println(allowanceCoefficient);
                        }
                    }
                    index = 4;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String current = String.valueOf(currentCell.getNumericCellValue());
                            if (current.equals("1.0")) {
                                dto.setCurrent(true);
                            }
                            if (current.equals("0.0")) {
                                dto.setCurrent(false);
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String current = currentCell.getStringCellValue().trim();
                            if (current.equals("1")) {
                                dto.setCurrent(true);
                            }
                            if (current.equals("0")) {
                                dto.setCurrent(false);
                            }
                        }
                    }
                    index = 5;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String fromDateString;
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setFromDate(calendar.getTime());
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            fromDateString = String.valueOf(currentCell.getStringCellValue());
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(fromDateString);
                                dto.setFromDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }
                    index = 6;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String toDateString;
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setToDate(calendar.getTime());
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            toDateString = String.valueOf(currentCell.getStringCellValue());
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(toDateString);
                                dto.setToDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }

                    index = 7;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String departmentStr = String.valueOf(currentCell.getNumericCellValue());
                            dto.setDepartmentStr(departmentStr);
                            System.out.println(departmentStr);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String departmentStr = currentCell.getStringCellValue().trim();
                            dto.setDepartmentStr(departmentStr);
                            System.out.println(departmentStr);
                        }
                    }
                    index = 8;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String note = String.valueOf(currentCell.getNumericCellValue());
                            dto.setNote(note);
                            System.out.println(note);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String note = currentCell.getStringCellValue().trim();
                            dto.setNote(note);
                            System.out.println(note);
                        }
                    }
                    index = 9;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String decisionDateString;
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setDecisionDate(calendar.getTime());
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            decisionDateString = String.valueOf(currentCell.getStringCellValue());
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(decisionDateString);
                                dto.setDecisionDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }
                    index = 10;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String connectedAllowanceProcess = String.valueOf(currentCell.getNumericCellValue());
                            if (connectedAllowanceProcess.equals("1.0")) {
                                dto.setConnectedAllowanceProcess(true);
                            }
                            if (connectedAllowanceProcess.equals("0.0")) {
                                dto.setConnectedAllowanceProcess(false);
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String connectedAllowanceProcess = currentCell.getStringCellValue().trim();
                            if (connectedAllowanceProcess.equals("1")) {
                                dto.setConnectedAllowanceProcess(true);
                            }
                            if (connectedAllowanceProcess.equals("0")) {
                                dto.setConnectedAllowanceProcess(false);
                            }
                        }
                    }
                    index = 11;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String mainPosition = String.valueOf(currentCell.getNumericCellValue());
                            if (mainPosition.equals("1.0")) {
                                dto.setMainPosition(true);
                            }
                            if (mainPosition.equals("0.0")) {
                                dto.setMainPosition(false);
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String mainPosition = currentCell.getStringCellValue().trim();
                            if (mainPosition.equals("1")) {
                                dto.setMainPosition(true);
                            }
                            if (mainPosition.equals("0")) {
                                dto.setMainPosition(false);
                            }
                        }
                    }
                    index = 12;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String department = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setDepartmentCode(department);
                            System.out.println(department);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String department = currentCell.getStringCellValue().trim();
                            dto.setDepartmentCode(department);
                            System.out.println(department);
                        }
                    }
                    listData.add(dto);
                }
                rowIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listData;
    }

    public static List<StaffFamilyRelationshipFunctionDto> importStaffFamilyRelationshipProcessFromInputStream(
            InputStream is) {
        List<StaffFamilyRelationshipFunctionDto> listData = new ArrayList<>();
        try {
            @SuppressWarnings("resource")
            Workbook workbook = new XSSFWorkbook(is);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            int rowIndex = 1;
            Calendar calendar = Calendar.getInstance();
            int num = datatypeSheet.getLastRowNum();
            while (rowIndex <= num) {
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell = null;
                if (currentRow != null) {
                    StaffFamilyRelationshipFunctionDto dto = new StaffFamilyRelationshipFunctionDto();

                    Integer index = 0;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String staffCode = String.valueOf(currentCell.getNumericCellValue());
                            dto.setStaffCode(staffCode);
                            System.out.println(staffCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String staffCode = currentCell.getStringCellValue().trim();
                            dto.setStaffCode(staffCode);
                            System.out.println(staffCode);
                        }
                    }

                    index = 1;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String fullName = String.valueOf(currentCell.getNumericCellValue());
                            dto.setFullName(fullName);
                            System.out.println(fullName);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String fullName = currentCell.getStringCellValue().trim();
                            dto.setFullName(fullName);
                            System.out.println(fullName);
                        }
                    }
                    index = 2;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String address = String.valueOf(currentCell.getNumericCellValue());
                            dto.setAddress(address);
                            System.out.println(address);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String address = currentCell.getStringCellValue().trim();
                            dto.setAddress(address);
                            System.out.println(address);
                        }
                    }
                    index = 3;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setBirthDate(calendar.getTime());
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setBirthDate(calendar.getTime());
                            }
                        }
                    }
                    index = 4;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String familyRelationship = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setFamilyRelationship(familyRelationship);
                            System.out.println(familyRelationship);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String familyRelationship = currentCell.getStringCellValue().trim();
                            dto.setFamilyRelationship(familyRelationship);
                            System.out.println(familyRelationship);
                        }
                    }
                    listData.add(dto);
                }
                rowIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listData;
    }

    public static List<CountryDto> importCountryFromInputStream(InputStream is) {
        List<CountryDto> listData = new ArrayList<>();
        try {
            @SuppressWarnings("resource")
            Workbook workbook = new XSSFWorkbook(is);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            int rowIndex = 1;
            int num = datatypeSheet.getLastRowNum();
            while (rowIndex <= num) {
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell = null;
                if (currentRow != null) {
                    CountryDto dto = new CountryDto();

                    Integer index = 0;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String name = String.valueOf(currentCell.getNumericCellValue());
                            dto.setName(name);
                            System.out.println(name);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String name = currentCell.getStringCellValue().trim();
                            dto.setName(name);
                            System.out.println(name);
                        }
                    }
                    index = 1;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String code = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setCode(code);
                            System.out.println(code);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String code = currentCell.getStringCellValue().trim();
                            dto.setCode(code);
                            System.out.println(code);
                        }
                    }
                    listData.add(dto);
                }
                rowIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listData;
    }

    public static List<StaffSalaryHistoryDto> importStaffSalaryHistoryFromInputStream(InputStream is) {
        List<StaffSalaryHistoryDto> listData = new ArrayList<>();
        try {
            // cảnh báo
            @SuppressWarnings("resource")
            Workbook workbook = new XSSFWorkbook(is);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            int rowIndex = 1;
            Calendar calendar = Calendar.getInstance();
            int num = datatypeSheet.getLastRowNum();
            while (rowIndex <= num) {
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell;
                if (currentRow != null) {
                    StaffSalaryHistoryDto dto = new StaffSalaryHistoryDto();

                    Integer index = 0;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String staffCode = String.valueOf(currentCell.getNumericCellValue());
                            dto.setStaffCode(staffCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String staffCode = currentCell.getStringCellValue().trim();
                            dto.setStaffCode(staffCode);
                        }
                    }

                    index = 1;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            Double coefficient = Double.parseDouble(String.valueOf(currentCell.getNumericCellValue()));
                            dto.setCoefficient(coefficient);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String[] splits = currentCell.getStringCellValue().trim().split(",");
                            double coefficient;
                            if (splits.length > 1) {
                                String s = splits[0] + "." + splits[1];
                                coefficient = Double.parseDouble(s);
                            } else {
                                coefficient = Double.parseDouble(currentCell.getStringCellValue().trim());
                            }
                            dto.setCoefficient(coefficient);
                        }
                    }

                    index = 2;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String staffTypeCode = String.valueOf(currentCell.getNumericCellValue());
                            dto.setStaffTypeCode(staffTypeCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String staffTypeCode = currentCell.getStringCellValue().trim();
                            dto.setStaffTypeCode(staffTypeCode);
                        }
                    }

                    index = 3;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            Double coefficientOverLevel = Double
                                    .parseDouble(String.valueOf(currentCell.getNumericCellValue()));
                            dto.setCoefficientOverLevel(coefficientOverLevel);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            Double coefficientOverLevel = Double.parseDouble(currentCell.getStringCellValue().trim());
                            dto.setCoefficientOverLevel(coefficientOverLevel);
                        }
                    }

                    index = 4;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            Double percentage = Double.parseDouble(String.valueOf(currentCell.getNumericCellValue()));
                            dto.setPercentage(percentage);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            Double percentage = Double.parseDouble(currentCell.getStringCellValue().trim());
                            dto.setPercentage(percentage);
                        }
                    }

                    index = 5;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String decisionDateString;
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setDecisionDate(calendar.getTime());
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            decisionDateString = String.valueOf(currentCell.getStringCellValue());
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(decisionDateString);
                                dto.setDecisionDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }

                    index = 6;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String decisionCode = String.valueOf(currentCell.getNumericCellValue());
                            dto.setDecisionCode(decisionCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String decisionCode = currentCell.getStringCellValue().trim();
                            dto.setDecisionCode(decisionCode);
                        }
                    }

                    index = 7;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String salaryIncrementTypeCode = String.valueOf(currentCell.getNumericCellValue());
                            dto.setSalaryIncrementTypeCode(salaryIncrementTypeCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String salaryIncrementTypeCode = currentCell.getStringCellValue().trim();
                            dto.setSalaryIncrementTypeCode(salaryIncrementTypeCode);
                        }
                    }

                    index = 8;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String decisionDateString;
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setStartDate(calendar.getTime());
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            decisionDateString = currentCell.getStringCellValue();
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(decisionDateString);
                                dto.setStartDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }

                    index = 9;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String decisionDateString;

                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setStartDate(calendar.getTime());
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            decisionDateString = currentCell.getStringCellValue();
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(decisionDateString);
                                dto.setNextSalaryIncrementDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }

                    index = 10;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String note = String.valueOf(currentCell.getNumericCellValue());
                            dto.setNote(note);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String note = currentCell.getStringCellValue().trim();
                            dto.setNote(note);
                        }
                    }

                    index = 11;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String decisionDateString;
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setStartDate(calendar.getTime());
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            decisionDateString = currentCell.getStringCellValue();
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(decisionDateString);
                                dto.setStartStaffTypeCodeDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }
                    listData.add(dto);
                }
                rowIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listData;
    }

    public static List<StaffEducationHistoryDto> importStaffEducationHistoryFromInputStream(InputStream is) {
        List<StaffEducationHistoryDto> listData = new ArrayList<>();
        try {
            // cảnh báo
            @SuppressWarnings("resource")
            Workbook workbook = new XSSFWorkbook(is);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            int rowIndex = 1;
            Calendar calendar = Calendar.getInstance();
            int num = datatypeSheet.getLastRowNum();
            while (rowIndex <= num) {
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell;
                if (currentRow != null) {
                    StaffEducationHistoryDto dto = new StaffEducationHistoryDto();

                    Integer index = 0;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String staffCode = String.valueOf(currentCell.getNumericCellValue());
                            dto.setStaffCode(staffCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String staffCode = currentCell.getStringCellValue().trim();
                            dto.setStaffCode(staffCode);
                        }
                    }
                    index = 2;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String majorCode = String.valueOf(currentCell.getNumericCellValue());
                            dto.setMajorCode(majorCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String majorCode = currentCell.getStringCellValue().trim();
                            dto.setMajorCode(majorCode);
                        }
                    }
                    index = 3;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String educationTypeCode = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setEducationTypeCode(educationTypeCode);
                            System.out.println(educationTypeCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String educationTypeCode = currentCell.getStringCellValue().trim();
                            dto.setEducationTypeCode(educationTypeCode);
                            System.out.println(educationTypeCode);
                        }
                    }
                    index = 5;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String countryCode = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setCountryCode(countryCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String countryCode = String.valueOf(currentCell.getStringCellValue().trim());
                            dto.setCountryCode(countryCode);
                        }
                    }

                    index = 1;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String specialityCode = String.valueOf(currentCell.getNumericCellValue());
                            dto.setSpecialityCode(specialityCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String specialityCode = String.valueOf(currentCell.getStringCellValue().trim());
                            dto.setSpecialityCode(specialityCode);
                        }
                    }

                    index = 4;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String educationDegreeCode = String.valueOf(currentCell.getNumericCellValue());
                            dto.setEducationDegreeCode(educationDegreeCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String educationDegreeCode = String.valueOf(currentCell.getStringCellValue().trim());
                            dto.setEducationDegreeCode(educationDegreeCode);
                        }
                    }

                    index = 6;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String startDate;
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setStartDate(calendar.getTime());
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            startDate = String.valueOf(currentCell.getStringCellValue());
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(startDate);
                                dto.setStartDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }
                    //
                    index = 7;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String endDate;

                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setEndDate(calendar.getTime());
                            }

                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            endDate = String.valueOf(currentCell.getStringCellValue());
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(endDate);
                                dto.setEndDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }
                    // schoolName
                    index = 9;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String schoolName = String.valueOf(currentCell.getNumericCellValue());
                            dto.setSchoolName(schoolName);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String schoolName = currentCell.getStringCellValue().trim();
                            dto.setSchoolName(schoolName);
                        }
                    }
                    // Place
                    index = 8;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String place = String.valueOf(currentCell.getNumericCellValue());
                            dto.setPlace(place);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String place = currentCell.getStringCellValue().trim();
                            dto.setPlace(place);
                        }
                    }
                    //IsCurrent
                    index = 10;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String isCurrent = String.valueOf(currentCell.getNumericCellValue());
                            if (isCurrent.equals("1.0")) {
                                dto.setIsCurrent(true);
                            }
                            if (isCurrent.equals("0.0")) {
                                dto.setIsCurrent(false);
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String isCurrent = currentCell.getStringCellValue().trim();
                            if (isCurrent.equals("1")) {
                                dto.setIsCurrent(true);
                            }
                            if (isCurrent.equals("0")) {
                                dto.setIsCurrent(false);
                            }
                        }
                    }
                    //SoQuyetDinh
                    index = 11;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String decisionCode = String.valueOf(currentCell.getNumericCellValue());
                            dto.setDecisionCode(decisionCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String decisionCode = currentCell.getStringCellValue().trim();
                            dto.setDecisionCode(decisionCode);
                        }
                    }
                    // NguonKinhPhi
                    index = 12;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String fundingSource = String.valueOf(currentCell.getNumericCellValue());
                            dto.setFundingSource(fundingSource);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String fundingSource = currentCell.getStringCellValue().trim();
                            dto.setFundingSource(fundingSource);
                        }
                    }
                    //Ghi chú
                    index = 13;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String note = String.valueOf(currentCell.getNumericCellValue());
                            dto.setNote(note);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String note = currentCell.getStringCellValue().trim();
                            dto.setNote(note);
                        }
                    }
                    //Xác nhận
                    index = 14;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String isConfirmation = String.valueOf(currentCell.getNumericCellValue());
                            if (isConfirmation.equals("1.0")) {
                                dto.setIsConfirmation(true);
                            }
                            if (isConfirmation.equals("0.0")) {
                                dto.setIsConfirmation(false);
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String isConfirmation = currentCell.getStringCellValue().trim();
                            if (isConfirmation.equals("1")) {
                                dto.setIsConfirmation(true);
                            }
                            if (isConfirmation.equals("0")) {
                                dto.setIsConfirmation(false);
                            }
                        }
                    }
                    // Được tính thâm niên
                    index = 15;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String isCountedForSeniority = String.valueOf(currentCell.getNumericCellValue());
                            if (isCountedForSeniority.equals("1.0")) {
                                dto.setIsCountedForSeniority(true);
                            }
                            if (isCountedForSeniority.equals("0.0")) {
                                dto.setIsCountedForSeniority(false);
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String isCountedForSeniority = currentCell.getStringCellValue().trim();
                            if (isCountedForSeniority.equals("1")) {
                                dto.setIsCountedForSeniority(true);
                            }
                            if (isCountedForSeniority.equals("0")) {
                                dto.setIsCountedForSeniority(false);
                            }
                        }
                    }

                    //Căn cứ basis
                    index = 16;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String basis = String.valueOf(currentCell.getNumericCellValue());
                            dto.setBasis(basis);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String basis = currentCell.getStringCellValue().trim();
                            dto.setBasis(basis);
                        }
                    }
                    //NgayQuyetDinh
                    index = 17;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String decisionDate;
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setDecisionDate(calendar.getTime());
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            decisionDate = String.valueOf(currentCell.getStringCellValue());
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(decisionDate);
                                dto.setDecisionDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }
                    //NamTiepNhanVe
                    index = 18;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String returnDate;
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setReturnDate(calendar.getTime());
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            returnDate = String.valueOf(currentCell.getStringCellValue());
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(returnDate);
                                dto.setReturnDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }
                    //NotFinish
                    index = 19;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String notFinish = String.valueOf(currentCell.getNumericCellValue());
                            if (notFinish.equals("1.0")) {
                                dto.setNotFinish(true);
                            }
                            if (notFinish.equals("0.0")) {
                                dto.setNotFinish(false);
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String isCountedForSeniority = currentCell.getStringCellValue().trim();
                            if (isCountedForSeniority.equals("1")) {
                                dto.setNotFinish(true);
                            }
                            if (isCountedForSeniority.equals("0")) {
                                dto.setNotFinish(false);
                            }
                        }
                    }
                    //NgayKetThucTheoQD
                    index = 20;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String finishDateByDecision;
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setFinishDateByDecision(calendar.getTime());
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            finishDateByDecision = String.valueOf(currentCell.getStringCellValue());
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(finishDateByDecision);
                                dto.setFinishDateByDecision(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }
                    //GiaHan
                    index = 21;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String extendDateByDecision;

                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setExtendDateByDecision(calendar.getTime());
                            }

                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            extendDateByDecision = String.valueOf(currentCell.getStringCellValue());
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(extendDateByDecision);
                                dto.setExtendDateByDecision(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }
                    //SoQĐGiaHan
                    index = 22;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String extendDecisionCode = String.valueOf(currentCell.getNumericCellValue());
                            dto.setExtendDecisionCode(extendDecisionCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String extendDecisionCode = currentCell.getStringCellValue().trim();
                            dto.setExtendDecisionCode(extendDecisionCode);
                        }
                    }
                    //
                    index = 23;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String isExtended = String.valueOf(currentCell.getNumericCellValue());
                            if (isExtended.equals("1.0")) {
                                dto.setIsExtended(true);
                            }
                            if (isExtended.equals("0.0")) {
                                dto.setIsExtended(false);
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String isExtended = currentCell.getStringCellValue().trim();
                            if (isExtended.equals("1")) {
                                dto.setIsExtended(true);
                            }
                            if (isExtended.equals("0")) {
                                dto.setIsExtended(false);
                            }
                        }
                    }
                    //NgayQuyetDinhGiaHan
                    index = 24;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String extendDecisionDate;

                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setExtendDecisionDate(calendar.getTime());
                            }

                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            extendDecisionDate = String.valueOf(currentCell.getStringCellValue());
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(extendDecisionDate);
                                dto.setExtendDecisionDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }
                    listData.add(dto);
                }
                rowIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listData;
    }


    public static List<HrSpecialityDto> importHrSpecialityFromInputStream(InputStream is) {
        List<HrSpecialityDto> listData = new ArrayList<>();
        try {
            // cảnh báo
            @SuppressWarnings("resource")
            Workbook workbook = new XSSFWorkbook(is);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            int rowIndex = 1;
            int num = datatypeSheet.getLastRowNum();
            while (rowIndex <= num) {
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell;
                if (currentRow != null) {
                    HrSpecialityDto dto = new HrSpecialityDto();

                    Integer index = 1;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String name = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setName(name);
                            System.out.println(name);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String name = currentCell.getStringCellValue().trim();
                            dto.setName(name);
                            System.out.println(name);
                        }
                    }
                    index = 0;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String code = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setCode(code);
                            System.out.println(code);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String code = currentCell.getStringCellValue().trim();
                            dto.setCode(code);
                            System.out.println(code);
                        }
                    }
                    listData.add(dto);
                }
                rowIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listData;
    }

//    public static List<StaffInsuranceHistoryDto> importExcelStaffInsuranceHistoryDto(InputStream is) {
//        List<StaffInsuranceHistoryDto> listData = new ArrayList<>();
//        try {
//            @SuppressWarnings("resource")
//            Workbook workbook = new XSSFWorkbook(is);
//            Sheet datatypeSheet = workbook.getSheetAt(0);
//            int rowIndex = 1;
//            Calendar calendar = Calendar.getInstance();
//            int num = datatypeSheet.getLastRowNum();
//            while (rowIndex <= num) {
//                Row currentRow = datatypeSheet.getRow(rowIndex);
//                Cell currentCell;
//                if (currentRow != null) {
//                    StaffInsuranceHistoryDto dto = new StaffInsuranceHistoryDto();
//
//                    Integer index = 0;
//                    if (index != null) {
//                        currentCell = currentRow.getCell(index);
//                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
//                            String staffCode = String.valueOf(currentCell.getNumericCellValue());
//                            StaffDto staffDto = new StaffDto();
//                            staffDto.setStaffCode(staffCode);
//                            dto.setStaff(staffDto);
//                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
//                                && currentCell.getStringCellValue() != null) {
//                            String staffCode = currentCell.getStringCellValue().trim();
//                            StaffDto staffDto = new StaffDto();
//                            staffDto.setStaffCode(staffCode);
//                            dto.setStaff(staffDto);
//                        }
//
//                    }
//
//                    index = 1;
//                    if (index != null) {
//                        currentCell = currentRow.getCell(index);
//                        String startDate;
//                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
//                                && currentCell.getNumericCellValue() > 0) {
//                            if (DateUtil.isCellDateFormatted(currentCell)) {
//                                calendar.setTime(currentCell.getDateCellValue());
//                                dto.setStartDate(calendar.getTime());
//                            }
//                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
//                                && currentCell.getStringCellValue() != null) {
//                            startDate = String.valueOf(currentCell.getStringCellValue());
//                            try {
//                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(startDate);
//                                dto.setStartDate(date);
//                            } catch (Exception ex) {
//                                System.out.print(ex.getMessage());
//                            }
//                        }
//                    }
//
//                    index = 2;
//                    if (index != null) {
//                        currentCell = currentRow.getCell(index);
//                        String endDate;
//                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
//                                && currentCell.getNumericCellValue() > 0) {
//                            if (DateUtil.isCellDateFormatted(currentCell)) {
//                                calendar.setTime(currentCell.getDateCellValue());
//                                dto.setEndDate(calendar.getTime());
//                            }
//
//                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
//                                && currentCell.getStringCellValue() != null) {
//                            endDate = String.valueOf(currentCell.getStringCellValue());
//                            try {
//                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(endDate);
//                                dto.setEndDate(date);
//                            } catch (Exception ex) {
//                                System.out.print(ex.getMessage());
//                            }
//                        }
//                    }
//
//                    index = 3;
//                    if (index != null) {
//                        currentCell = currentRow.getCell(index);
//                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
//                            String departmentName = String.valueOf(currentCell.getNumericCellValue());
//                            dto.setDepartmentName(departmentName);
//                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
//                                && currentCell.getStringCellValue() != null) {
//                            String departmentName = currentCell.getStringCellValue().trim();
//                            dto.setDepartmentName(departmentName);
//                        }
//                    }
//
//                    index = 4;
//                    if (index != null) {
//                        currentCell = currentRow.getCell(index);
//                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
//                            String professionName = String.valueOf(currentCell.getNumericCellValue());
//                            dto.setProfessionName(professionName);
//                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
//                                && currentCell.getStringCellValue() != null) {
//                            String professionName = currentCell.getStringCellValue().trim();
//                            dto.setProfessionName(professionName);
//                        }
//                    }
//
//                    index = 5;
//                    if (index != null) {
//                        currentCell = currentRow.getCell(index);
//                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
//                            Double salaryCofficient = Double.parseDouble(String.valueOf(currentCell.getNumericCellValue()));
//                            dto.setSalaryCofficient(salaryCofficient);
//                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
//                                && currentCell.getStringCellValue() != null) {
//                            Double salaryCofficient = Double.parseDouble(currentCell.getStringCellValue().trim().replace(",", "."));
//                            dto.setSalaryCofficient(salaryCofficient);
//                        }
//                    }
//
//                    index = 6;
//                    if (index != null) {
//                        currentCell = currentRow.getCell(index);
//                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
//                            Double allowanceCoefficient = Double.parseDouble(String.valueOf(currentCell.getNumericCellValue()));
//                            dto.setAllowanceCoefficient(allowanceCoefficient);
//                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
//                                && currentCell.getStringCellValue() != null) {
//                            Double allowanceCoefficient = Double.parseDouble(currentCell.getStringCellValue().trim().replace(",", "."));
//                            dto.setAllowanceCoefficient(allowanceCoefficient);
//                        }
//                    }
//
//                    index = 7;
//                    if (index != null) {
//                        currentCell = currentRow.getCell(index);
//                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
//                            String socialInsuranceBookCode = String.valueOf(currentCell.getNumericCellValue());
//                            dto.setSocialInsuranceBookCode(socialInsuranceBookCode);
//                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
//                                && currentCell.getStringCellValue() != null) {
//                            String socialInsuranceBookCode = currentCell.getStringCellValue().trim();
//                            dto.setSocialInsuranceBookCode(socialInsuranceBookCode);
//                        }
//                    }
//                    listData.add(dto);
//                }
//                rowIndex++;
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return listData;
//    }


    public static List<PositionTitleDto> importPositionTitleFromInputStream(InputStream is) {
        List<PositionTitleDto> listData = new ArrayList<>();
        try {
            // cảnh báo
            @SuppressWarnings("resource")
            Workbook workbook = new XSSFWorkbook(is);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            int rowIndex = 1;
            int num = datatypeSheet.getLastRowNum();
            while (rowIndex <= num) {
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell = null;
                if (currentRow != null) {
                    PositionTitleDto dto = new PositionTitleDto();

                    Integer index = 0;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String code = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setCode(code);
                            System.out.println(code);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String code = currentCell.getStringCellValue().trim();
                            dto.setCode(code);
                            System.out.println(code);
                        }
                    }
                    index = 1;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String name = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setName(name);
                            System.out.println(name);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String code = currentCell.getStringCellValue().trim();
                            dto.setName(code);
                            System.out.println(code);
                        }
                    }
                    index = 2;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String description = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setDescription(description);
                            System.out.println(description);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String description = currentCell.getStringCellValue().trim();
                            dto.setDescription(description);
                            System.out.println(description);
                        }
                    }
                    index = 3;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String positionCoefficient = String.valueOf(currentCell.getNumericCellValue());
                            dto.setPositionCoefficient(currentCell.getNumericCellValue());
                            System.out.println(positionCoefficient);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String positionCoefficient = currentCell.getStringCellValue().trim();
                            dto.setPositionCoefficient(Double.valueOf(positionCoefficient));
                            System.out.println(positionCoefficient);
                        }
                    }
                    listData.add(dto);
                }
                rowIndex++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return listData;
    }

    public static List<HRDepartmentDto> importDepartmentFromInputStream(InputStream is) {
        List<HRDepartmentDto> listData = new ArrayList<>();
        try {
            // cảnh báo
            @SuppressWarnings("resource")
            Workbook workbook = new XSSFWorkbook(is);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            int rowIndex = 1;
            Calendar calendar = Calendar.getInstance();
            int num = datatypeSheet.getLastRowNum();
            while (rowIndex <= num) {
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell;
                if (currentRow != null) {
                    HRDepartmentDto dto = new HRDepartmentDto();

                    Integer index = 0;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String code = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setCode(code);
                            System.out.println(code);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String code = currentCell.getStringCellValue().trim();
                            dto.setCode(code);
                            System.out.println(code);
                        }
                    }
                    index = 1;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String parentCode = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setParentCode(parentCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String parentCode = currentCell.getStringCellValue().trim();
                            dto.setParentCode(parentCode);
                        }
                    }
                    index = 2;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String name = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setName(name);
                            System.out.println(name);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String code = currentCell.getStringCellValue().trim();
                            dto.setName(code);
                            System.out.println(code);
                        }
                    }
                    index = 3;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String description = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setDescription(description);
                            System.out.println(description);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String description = currentCell.getStringCellValue().trim();
                            dto.setDescription(description);
                            System.out.println(description);
                        }
                    }
                    index = 4;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String displayOrder = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setDisplayOrder(displayOrder);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String displayOrder = currentCell.getStringCellValue().trim();
                            dto.setDisplayOrder(displayOrder);
                        }
                    }
                    index = 5;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String departmentType = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setDepartmentType((int) currentCell.getNumericCellValue());
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String departmentType = currentCell.getStringCellValue().trim();
                            dto.setDepartmentType(Integer.parseInt(departmentType));
                        }
                    }
                    index = 6;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String foundedDate;
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setFoundedDate(calendar.getTime());
                            }

                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            foundedDate = String.valueOf(currentCell.getStringCellValue());
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(foundedDate);
                                dto.setFoundedDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }
                    index = 7;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String foundedNumber = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setFoundedNumber(foundedNumber);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String foundedNumber = currentCell.getStringCellValue().trim();
                            dto.setFoundedNumber(foundedNumber);
                        }
                    }
                    index = 8;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String shortName = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setShortName(shortName);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String shortName = currentCell.getStringCellValue().trim();
                            dto.setShortName(shortName);
                        }
                    }
                    index = 9;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String departmentDisplayCode = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setDepartmentDisplayCode(departmentDisplayCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String departmentDisplayCode = currentCell.getStringCellValue().trim();
                            dto.setDepartmentDisplayCode(departmentDisplayCode);
                        }
                    }
                    index = 10;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String establishDecisionCode = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setEstablishDecisionCode(establishDecisionCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String establishDecisionCode = currentCell.getStringCellValue().trim();
                            dto.setEstablishDecisionCode(establishDecisionCode);
                        }
                    }
                    index = 11;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String establishDecisionDate = "";

                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setEstablishDecisionDate(calendar.getTime());
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            establishDecisionDate = String.valueOf(currentCell.getStringCellValue());
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(establishDecisionDate);
                                dto.setEstablishDecisionDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }
                    listData.add(dto);
                }
                rowIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listData;
    }

    public static List<StaffLabourAgreementDto> importStaffLabourAgreementFromInputStream(InputStream is) {
        List<StaffLabourAgreementDto> listData = new ArrayList<>();
        try {
            // cảnh báo
            @SuppressWarnings("resource")
            Workbook workbook = new XSSFWorkbook(is);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            int rowIndex = 1;
            Calendar calendar = Calendar.getInstance();
            int num = datatypeSheet.getLastRowNum();
            while (rowIndex <= num) {
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell;
                if (currentRow != null) {
                    StaffLabourAgreementDto dto = new StaffLabourAgreementDto();

                    Integer index = 0;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String staffCode = String.valueOf(currentCell.getNumericCellValue());
                            StaffDto staffDto = new StaffDto();
                            staffDto.setStaffCode(staffCode);
                            dto.setStaffCode(staffCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String staffCode = currentCell.getStringCellValue().trim();
                            StaffDto staffDto = new StaffDto();
                            staffDto.setStaffCode(staffCode);
                            dto.setStaffCode(staffCode);
                        }
                    }

                    index = 1;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String recruitmentDate;

                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setRecruitmentDate(calendar.getTime());
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            recruitmentDate = String.valueOf(currentCell.getStringCellValue());
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(recruitmentDate);
                                dto.setRecruitmentDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }
                    index = 2;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String contractDate;
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setContractDate(calendar.getTime());
                            }

                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            contractDate = String.valueOf(currentCell.getStringCellValue());
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(contractDate);
                                dto.setContractDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }
                    index = 3;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String contractTypeCode = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setContractTypeCode(contractTypeCode);
                            System.out.println(contractTypeCode);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String contractTypeCode = currentCell.getStringCellValue().trim();
                            dto.setContractTypeCode(contractTypeCode);
                            System.out.println(contractTypeCode);
                        }
                    }
                    listData.add(dto);
                }
                rowIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listData;
    }

    public static List<PersonCertificateDto> importPersonCertificateFromInputStream(InputStream is) {
        List<PersonCertificateDto> listData = new ArrayList<>();
        try {
            // cảnh báo
            @SuppressWarnings("resource")
            Workbook workbook = new XSSFWorkbook(is);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            int rowIndex = 1;
            Calendar calendar = Calendar.getInstance();
            int num = datatypeSheet.getLastRowNum();
            while (rowIndex <= num) {
                Row currentRow = datatypeSheet.getRow(rowIndex);
                Cell currentCell;
                if (currentRow != null) {
                    PersonCertificateDto dto = new PersonCertificateDto();

                    Integer index = 0;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String code = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setPersonCode(code);
                            System.out.println(code);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String code = currentCell.getStringCellValue().trim();
                            dto.setPersonCode(code);
                        }
                    }
                    index = 1;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String name = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setName(name);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String name = currentCell.getStringCellValue().trim();
                            dto.setName(name);
                        }
                    }
                    index = 2;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            String type = String.valueOf((int) currentCell.getNumericCellValue());
                            dto.setCertificateType(type);
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            String type = currentCell.getStringCellValue().trim();
                            dto.setCertificateType(type);
                        }
                    }
                    index = 3;
                    if (index != null) {
                        currentCell = currentRow.getCell(index);
                        String contractDate;
                        if (currentCell != null && currentCell.getCellTypeEnum() == CellType.NUMERIC
                                && currentCell.getNumericCellValue() > 0) {
                            if (DateUtil.isCellDateFormatted(currentCell)) {
                                calendar.setTime(currentCell.getDateCellValue());
                                dto.setIssueDate(calendar.getTime());
                            }
                        } else if (currentCell != null && currentCell.getCellTypeEnum() == CellType.STRING
                                && currentCell.getStringCellValue() != null) {
                            contractDate = String.valueOf(currentCell.getStringCellValue());
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(contractDate);
                                dto.setIssueDate(date);
                            } catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                        }
                    }
                    listData.add(dto);
                }
                rowIndex++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return listData;
    }

    public static List<TimekeepingItemDto> readTimeKeeppingFile(ByteArrayInputStream bis) {
        if (bis == null) {
            return null;
        }
        try {
            List<TimekeepingItemDto> list = new ArrayList<>();

            Workbook workbook = new XSSFWorkbook(bis);
            Sheet sheet = workbook.getSheetAt(0);

            int colIndex = 2;
            int rowIndex = 2;

            Row dateRow = sheet.getRow(0);
            Row shiftCodeRow = sheet.getRow(1);
            int numCols = dateRow.getLastCellNum();

            Date[] dates = new Date[numCols - 2];
            String[] shiftCodes = new String[numCols - 2];

            while (colIndex <= numCols) {
                Cell dateCell = dateRow.getCell(colIndex);
                Cell shiftCodeCell = shiftCodeRow.getCell(colIndex);

                colIndex++;

                if (dateCell == null || shiftCodeCell == null) {
                    continue;
                }

                Date date = dateCell.getDateCellValue();
                String code = shiftCodeCell.getStringCellValue();
                dates[colIndex - 3] = date;
                shiftCodes[colIndex - 3] = code;
            }

            int rowNum = sheet.getLastRowNum();

            while (rowIndex <= rowNum) {
                Row row = sheet.getRow(rowIndex);

                Cell staffCodeCell = row.getCell(1);
                String staffCode = staffCodeCell.getStringCellValue();
                if (staffCode == null)
                    continue;

                TimekeepingItemDto itemDto = null;

                for (int i = 2; i < numCols; i++) {
                    Date date = dates[i - 2];
                    if (date != null) {
                        itemDto = new TimekeepingItemDto();
                        itemDto.setStaffCode(staffCode);
                        itemDto.setWorkingDate(date);
                        itemDto.setTimeSheetShiftWorkPeriods(new ArrayList<>());
                        list.add(itemDto);
                    }

                    if (itemDto == null) continue;

                    TimeSheetShiftWorkPeriodDto periodDto = new TimeSheetShiftWorkPeriodDto();

                    int val;
                    Cell cell = row.getCell(i);
                    if (cell == null) {
                        val = HrConstants.WorkingFormatEnum.off.getValue();
                    } else {
                        String cellVal = cell.getStringCellValue();

                        if ("ON".equals(cellVal)) {
                            val = HrConstants.WorkingFormatEnum.onsite.getValue();
                        } else if ("ONLINE".equals(cellVal)) {
                            val = HrConstants.WorkingFormatEnum.online.getValue();
                        } else if ("TRIP".equals(cellVal)) {
                            val = HrConstants.WorkingFormatEnum.out_office.getValue();
                        } else {
                            val = HrConstants.WorkingFormatEnum.off.getValue();
                        }
                    }

                    periodDto.setWorkingFormat(val);
                    periodDto.setCode(shiftCodes[i - 2]);
                    itemDto.add(periodDto);

                }

                rowIndex++;
            }
            return list;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ByteArrayInputStream createByteArrayInputStreamFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            // Cách 1: Đọc toàn bộ file vào byte array (dùng cho file nhỏ)
            byte[] fileBytes = new byte[(int) file.length()]; // Ép kiểu long về int, cần cẩn thận với file quá lớn
            fileInputStream.read(fileBytes);
            return new ByteArrayInputStream(fileBytes);

            // Cách 2: Đọc file theo từng buffer (dùng cho file lớn, tránh OutOfMemoryError)
            /*
            byte[] buffer = new byte[8192]; // Buffer 8KB
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return new ByteArrayInputStream(outputStream.toByteArray());
            */
        }
    }

    public static void main(String[] args) throws IOException {
        ByteArrayInputStream bytes = createByteArrayInputStreamFromFile("C:\\Users\\91hai\\Documents\\Zalo Received Files\\ImportTimeSheet.xlsx");
        List<TimekeepingItemDto> list = readTimeKeeppingFile(bytes);
        for (TimekeepingItemDto timekeepingItemDto : list) {
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd");

            logger.info("----" + timekeepingItemDto.getStaffCode());
            logger.info(df.format(timekeepingItemDto.getWorkingDate()));
            if (timekeepingItemDto.getTimeSheetShiftWorkPeriods() != null && timekeepingItemDto.getTimeSheetShiftWorkPeriods().size() > 0) {
                for (TimeSheetShiftWorkPeriodDto period : timekeepingItemDto.getTimeSheetShiftWorkPeriods()) {
                    logger.info(period.getCode());
                    logger.info(period.getWorkingFormat().toString());
                }
            }
        }
    }

    public static void importTimekeeping(InputStream is, List<TimekeepingItemDto> list) throws IOException {
        Workbook workbook = new XSSFWorkbook(is);
        int numSheet = workbook.getNumberOfSheets();
        for (int a = 0; a < numSheet; a++) {
            Sheet sheet = workbook.getSheetAt(a);
            logger.info("SheetAt:" + a);
            int colIndex = 2;
            int rowIndex = 2;

            Row dateRow = sheet.getRow(0);
            Row shiftCodeRow = sheet.getRow(1);
            int numCols = dateRow.getLastCellNum();

            Date[] dates = new Date[numCols - 2];
            String[] shiftCodes = new String[numCols - 2];

            while (colIndex <= numCols) {
                Cell dateCell = dateRow.getCell(colIndex);
                Cell shiftCodeCell = shiftCodeRow.getCell(colIndex);

                colIndex++;

                if (dateCell == null || shiftCodeCell == null) {
                    continue;
                }

                Date date = dateCell.getDateCellValue();

                String code = shiftCodeCell.getStringCellValue();
                dates[colIndex - 3] = date;
                shiftCodes[colIndex - 3] = code;
            }

            int rowNum = sheet.getLastRowNum();
            logger.info("rowNum:" + rowNum);
            while (rowIndex <= rowNum) {
                Row row = sheet.getRow(rowIndex);
                logger.info("rowIndex:" + rowIndex);
                Cell staffCodeCell = row.getCell(1);
                String staffCode = staffCodeCell.getStringCellValue();
                if (staffCode == null)
                    continue;

                TimekeepingItemDto itemDto = null;

                for (int i = 2; i < numCols; i++) {
                    Date date = dates[i - 2];
                    if (date != null) {
                        itemDto = new TimekeepingItemDto();
                        itemDto.setStaffCode(staffCode);
                        itemDto.setWorkingDate(date);
                        itemDto.setTimeSheetShiftWorkPeriods(new ArrayList<>());
                        list.add(itemDto);
                    }

                    if (itemDto == null) continue;

                    TimeSheetShiftWorkPeriodDto periodDto = new TimeSheetShiftWorkPeriodDto();

                    int val;
                    Cell cell = row.getCell(i);
                    if (cell == null) {
                        val = HrConstants.WorkingFormatEnum.off.getValue();
                    } else {
                        String cellVal = cell.getStringCellValue();

                        if ("ON".equals(cellVal)) {
                            val = HrConstants.WorkingFormatEnum.onsite.getValue();
                        } else if ("ONLINE".equals(cellVal)) {
                            val = HrConstants.WorkingFormatEnum.online.getValue();
                        } else if ("TRIP".equals(cellVal)) {
                            val = HrConstants.WorkingFormatEnum.out_office.getValue();
                        } else {
                            val = HrConstants.WorkingFormatEnum.off.getValue();
                        }
                    }

                    periodDto.setWorkingFormat(val);
                    periodDto.setCode(shiftCodes[i - 2]);
                    itemDto.add(periodDto);

                }

                rowIndex++;
            }
        }

    }

    public static List<HrOrganizationDto> readHrOrganizationFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        List<HrOrganizationDto> organizationList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            Iterator<Row> rows = sheet.iterator();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // Bỏ qua dòng tiêu đề
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                HrOrganizationDto organizationDto = new HrOrganizationDto();

                // Lấy dữ liệu từ các ô trong hàng
                Cell codeCell = currentRow.getCell(0);// Mã tổ chức
                Cell nameCell = currentRow.getCell(1); // Tên tổ chức
                Cell websiteCell = currentRow.getCell(2); // Website
                Cell organizationTypeCell = currentRow.getCell(3); // Loại tổ chức
                Cell sortNumberCell = currentRow.getCell(4); // Số thứ tự

                if (nameCell != null) {
                    organizationDto.setName(nameCell.getStringCellValue());
                }
                if (codeCell != null) {
                    organizationDto.setCode(codeCell.getStringCellValue());
                }
                if (websiteCell != null) {
                    organizationDto.setWebsite(websiteCell.getStringCellValue());
                }
                if (organizationTypeCell != null) {
                    organizationDto.setOrganizationType((int) organizationTypeCell.getNumericCellValue());
                }
                if (sortNumberCell != null) {
                    organizationDto.setSortNumber((int) sortNumberCell.getNumericCellValue());
                }

                organizationList.add(organizationDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }

        return organizationList;
    }

    public static List<ImportFamilyRelationshipDto> readFamilyRelationshipFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        List<ImportFamilyRelationshipDto> organizationList = new ArrayList<>();
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            Iterator<Row> rows = sheet.iterator();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // Bỏ qua dòng tiêu đề
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                ImportFamilyRelationshipDto organizationDto = new ImportFamilyRelationshipDto();

                // Lấy dữ liệu từ các ô trong hàng
                Cell staffCode = currentRow.getCell(0); // Mã nhân viên
                Cell staffName = currentRow.getCell(1); // Họ tên nhân viên
                Cell name = currentRow.getCell(2); // Họ tên cha/mẹ
                Cell dob = currentRow.getCell(3); // Ngày tháng năm sinh
                Cell codeProfession = currentRow.getCell(4); // Mã nghề nghiệp
                Cell nameProfession = currentRow.getCell(5); // Nghề nghiệp
                Cell codeRelationship = currentRow.getCell(6); // Mã quan hệ
                Cell nameRelationship = currentRow.getCell(7); // Quan hệ

                if (staffCode != null) {
                    organizationDto.setStaffCode(getCellValueAsString(staffCode));
                }
                if (staffName != null) {
                    organizationDto.setStaffName(staffName.getStringCellValue().trim());
                }
                if (name != null) {
                    organizationDto.setName(getCellValueAsString(name));
                }
                if (dob != null) {
                    organizationDto.setDateOfBirth(parseDateCell(dob));
                }
                if (codeProfession != null) {
                    organizationDto.setCodeProfesstion(getCellValueAsString(codeProfession));
                }
                if (nameProfession != null) {
                    organizationDto.setNameProfesstion(getCellValueAsString(nameProfession));
                }
                if (codeRelationship != null) {
                    organizationDto.setCodeRelationship(getCellValueAsString(codeRelationship));
                }
                if (nameRelationship != null) {
                    organizationDto.setNameRelationship(getCellValueAsString(nameRelationship));
                }

                organizationList.add(organizationDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }

        return organizationList;
    }

    // Thêm phương thức helper này để lấy giá trị chuỗi an toàn từ bất kỳ loại ô nào
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue().trim();
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Nếu là ngày, định dạng lại thành chuỗi
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    return sdf.format(cell.getDateCellValue());
                } else {
                    // Với số thập phân, xử lý để tránh hiển thị dạng khoa học (.0 ở cuối)
                    double value = cell.getNumericCellValue();
                    if (value == Math.floor(value)) {
                        return String.format("%.0f", value);
                    } else {
                        return String.valueOf(value);
                    }
                }
            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception e2) {
                        return "";
                    }
                }
            case Cell.CELL_TYPE_BLANK:
                return "";
            default:
                return "";
        }
    }

    /**
     * Xử lý dữ liệu ngày tháng từ ô Excel
     * Nếu chỉ có năm → Đặt ngày 01/01/yyyy
     * Nếu có tháng/năm → Đặt ngày 01/MM/yyyy
     * Nếu đủ ngày/tháng/năm → Giữ nguyên
     */
    // Cách 1: Sửa phương thức parseDateCell để trả về Date thay vì String
    private static Date parseDateCell(Cell cell) {
        if (cell == null) return null;

        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        fullDateFormat.setLenient(false);

        try {
            if (cell.getCellType() == CellType.NUMERIC.getCode()) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                double numericValue = cell.getNumericCellValue();
                if (numericValue >= 1800 && numericValue <= 2100) {
                    return fullDateFormat.parse("01/01/" + (int) numericValue);
                }
                return DateUtil.getJavaDate(numericValue);
            }

            if (cell.getCellType() == CellType.STRING.getCode()) {
                String rawValue = cell.getStringCellValue().trim();

                if (rawValue.matches("^\\d{4}$")) {
                    return fullDateFormat.parse("01/01/" + rawValue);
                }

                if (rawValue.matches("^(0?[1-9]|1[0-2])/\\d{4}$")) {
                    String[] parts = rawValue.split("/");
                    return fullDateFormat.parse("01/" + parts[0] + "/" + parts[1]);
                }

                if (rawValue.matches("^(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[0-2])/\\d{4}$")) {
                    return fullDateFormat.parse(rawValue);
                }

                String[] formats = {"dd-MM-yyyy", "yyyy-MM-dd", "MM-dd-yyyy", "dd.MM.yyyy", "yyyy.MM.dd", "MM.dd.yyyy"};
                for (String format : formats) {
                    try {
                        return new SimpleDateFormat(format).parse(rawValue);
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi chuyển đổi ô ngày tháng: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


    public static List<RankTitleDto> readRankTitleFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<RankTitleDto> rankTitleList = new ArrayList<>();
            int lastRow = sheet.getLastRowNum();
            String errorMessage = null;

            for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue;

                RankTitleDto dto = new RankTitleDto();

                // Cột 0 - Tên cấp bậc (name) *
                String name = ExcelUtils.getCellValue(currentRow.getCell(0), String.class);
                // Cột 1 - Mã cấp bậc (code) *
                String code = ExcelUtils.getCellValue(currentRow.getCell(1), String.class);
                // Cột 2 - Level (Integer)
                Integer level = ExcelUtils.getCellValue(currentRow.getCell(2), Integer.class);
                // Cột 3 - Mô tả (description)
                String description = ExcelUtils.getCellValue(currentRow.getCell(3), String.class);
                // Cột 4 - Lương đóng BHXH (Double)
                Double salary = ExcelUtils.getCellValue(currentRow.getCell(4), Double.class);
                // Cột 5 - Mức hưởng phí giới thiệu (Double)
                Double referralFeeLevel = ExcelUtils.getCellValue(currentRow.getCell(5), Double.class);

                if (name == null && code == null && level == null && salary == null && description == null && referralFeeLevel == null) {
                    continue;
                }
                if (!StringUtils.hasText(name)) {
                    errorMessage = "Tên cấp bậc không được để trống, lỗi tại dòng " + rowIndex;
                    break;
                }
                dto.setName(name);


                if (!StringUtils.hasText(code)) {
                    errorMessage = "Mã cấp bậc không được để trống, lỗi tại dòng " + rowIndex;
                    break;
                }
                dto.setShortName(code);
                dto.setLevel(level);
                dto.setSocialInsuranceSalary(salary);
                dto.setDescription(description);
                dto.setReferralFeeLevel(referralFeeLevel);
                rankTitleList.add(dto);
            }

            if (errorMessage != null) {
                RankTitleDto response = new RankTitleDto();
                response.setErrorMessage(errorMessage);
                return List.of(response);
            }

            return rankTitleList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }


    public static List<PositionTitleDto> readPositionTitleFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        List<PositionTitleDto> positionTitleList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            int lastNumber = sheet.getLastRowNum();
            String errorMessage = null;
            for (int rowIndex = 2; rowIndex <= lastNumber; rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue; // Bỏ qua dòng trống

                PositionTitleDto positionTitle = new PositionTitleDto();

                // Lấy dữ liệu từ các ô trong hàng
                Cell name = currentRow.getCell(0); // Tên chức danh
                Cell code = currentRow.getCell(1); // Mã chức danh
                Cell otherName = currentRow.getCell(2);// Tên khác
                Cell shortName = currentRow.getCell(3); // Tên viết tắt
                Cell codeGroupPositionTitle = currentRow.getCell(5); //Mã nhóm ngạch
                Cell shortNameRankTitle = currentRow.getCell(6); //Tên viết tắt cấp bậc
                Cell recruitmentDays = currentRow.getCell(7);//Số ngày tuyển
//                Cell manager = currentRow.getCell(8);// Nhà quản lý
//                Cell highLevelTechnicalSpecialist = currentRow.getCell(9);// Chuyên môn kỹ thuật bậc cao
//                Cell midLevelTechnicalSpecialist = currentRow.getCell(10);// Chuyên môn kỹ thuật bậc trung
//                Cell other = currentRow.getCell(11);// Khác

                Integer positionTitleType = null;
                if (name != null) {
                    positionTitle.setName(name.getStringCellValue().trim());
                }
                if (code != null) {
                    positionTitle.setCode(code.getStringCellValue().trim());
                }
                if (shortName != null) {
                    positionTitle.setShortName(shortName.getStringCellValue().trim());
                }
                if (otherName != null) {
                    positionTitle.setOtherName(otherName.getStringCellValue().trim());
                }
                if (recruitmentDays != null) {
                    CellType cellType = CellType.forInt(recruitmentDays.getCellType());
                    if (cellType == CellType.NUMERIC) {
                        positionTitle.setRecruitmentDays((int) recruitmentDays.getNumericCellValue());
                    } else if (cellType == CellType.STRING) {
                        try {
                            positionTitle.setRecruitmentDays(Integer.parseInt(recruitmentDays.getStringCellValue().trim()));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (shortNameRankTitle != null && StringUtils.hasText(shortNameRankTitle.getStringCellValue().trim())) {
                    RankTitleDto rankTitleDto = new RankTitleDto();
                    rankTitleDto.setShortName(shortNameRankTitle.getStringCellValue().trim());
                    positionTitle.setRankTitle(rankTitleDto);
                }

                if (codeGroupPositionTitle != null && StringUtils.hasText(codeGroupPositionTitle.getStringCellValue().trim())) {
                    PositionTitleDto groupPositionTitle = new PositionTitleDto();
                    groupPositionTitle.setCode(codeGroupPositionTitle.getStringCellValue().trim());
                    positionTitle.setParent(groupPositionTitle);
                }
//                if (manager != null && StringUtils.hasText(String.valueOf(manager).trim())) {
//                    positionTitleType = HrConstants.PositionTitleType.NHA_QUAN_LY.getValue();
//                } else if (highLevelTechnicalSpecialist != null && StringUtils.hasText(String.valueOf(highLevelTechnicalSpecialist).trim())) {
//                    positionTitleType = HrConstants.PositionTitleType.CHUYEN_MON_KY_THUAT_BAC_CAO.getValue();
//                } else if (midLevelTechnicalSpecialist != null && StringUtils.hasText(String.valueOf(midLevelTechnicalSpecialist).trim())) {
//                    positionTitleType = HrConstants.PositionTitleType.CHUYEN_MON_KY_THUAT_BAC_TRUNG.getValue();
//                } else {
//                    positionTitleType = HrConstants.PositionTitleType.KHAC.getValue();
//                }
                if (positionTitleType != null) {
                    positionTitle.setPositionTitleType(positionTitleType);
                }

                //12. Mô tả
                Cell currentCell = currentRow.getCell(12);
                String description = ExcelUtils.getCellValue(currentCell, String.class);
                positionTitle.setDescription(description);

                //Bổ sung thêm trường
                //13. Cách tính ngày công chuẩn
                currentCell = currentRow.getCell(13);
                Integer workDayCalculationType = ExcelUtils.getCellValue(currentCell, Integer.class);
                if (workDayCalculationType != null) {
                    if (workDayCalculationType.equals(HrConstants.PositionTitleWorkdayCalculationType.FIXED.getValue()) || workDayCalculationType.equals(HrConstants.PositionTitleWorkdayCalculationType.CHANGE_BY_PERIOD.getValue())) {
                        positionTitle.setWorkDayCalculationType(workDayCalculationType);
                    } else {
                        errorMessage = "Cách tính ngày công chuẩn không hợp lệ tại dòng " + (rowIndex);
                        rowIndex = lastNumber + 1;
                        break;
                    }
                }

                //14. Số ngày làm việc ước tính (tháng)
                currentCell = currentRow.getCell(14);
                if (workDayCalculationType != null && workDayCalculationType.equals(HrConstants.PositionTitleWorkdayCalculationType.FIXED.getValue())) {
                    Double estimatedWorkingDays = ExcelUtils.getCellValue(currentCell, Double.class);
                    positionTitle.setEstimatedWorkingDays(estimatedWorkingDays);
                } else {
                    positionTitle.setEstimatedWorkingDays(null);
                }

                // 15. "Chức danh trực thuộc phòng ban (cách nhau bằng dấu ';')"
                currentCell = currentRow.getCell(15); // Ô chứa danh sách chức danh
                String positionsString = ExcelUtils.getCellValue(currentCell, String.class);

                if (StringUtils.hasText(positionsString)) {
                    // Tách danh sách theo dấu chấm phẩy
                    String[] positions = positionsString.split(";");

                    // Kiểm tra từng chức danh xem có hợp lệ không (ví dụ: không rỗng)
                    boolean allValid = true;
                    for (String position : positions) {
                        if (!StringUtils.hasText(position.trim())) {
                            allValid = false;
                            break;
                        }
                    }

                    if (!allValid) {
                        errorMessage = "Chức danh trực thuộc phòng ban phải nhập đúng quy tắc (cách nhau bằng dấu ';') tại dòng " + (rowIndex);
                        rowIndex = lastNumber + 1;
                        break;
                    }

                    for (int i = 0; i < positions.length; i++) {
                        HRDepartmentDto department = new HRDepartmentDto();
                        department.setCode(positions[i].trim());
                        if (positionTitle.getDepartments() == null) {
                            positionTitle.setDepartments(new ArrayList<>());
                        }
                        positionTitle.getDepartments().add(department);
                    }

                }

                positionTitleList.add(positionTitle);
            }
            if (errorMessage != null) {
                PositionTitleDto response = new PositionTitleDto();
                response.setErrorMessage(errorMessage);
                return List.of(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }

        return positionTitleList;
    }


    /*
     * Đã check Cell truyền vào
     */
    private static String getCellValueAsStringSimple(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING -> cell.getStringCellValue().trim();
            case Cell.CELL_TYPE_NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }


    private static Date parseDateCellValue(Cell cell, SimpleDateFormat dateFormat) {
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
//                        System.err.println(String.format(
//                                "[RowIndex: %d] [ColumnIndex: %d] Sai định dạng năm, Giá trị: %s",
//                                rowIndex, columnIndex, strDate
//                        ));
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
//                    System.err.println(String.format(
//                            "[RowIndex: %d] [ColumnIndex: %d] Sai định dạng năm, Giá trị: %s",
//                            rowIndex, columnIndex, cell.getNumericCellValue()
//                    ));
                    return null;
                }

            } else {
//                System.err.println(String.format(
//                        "[RowIndex: %d] [ColumnIndex: %d] Không hỗ trợ kiểu dữ liệu: %s",
//                        rowIndex, columnIndex, cellType
//                ));
            }

        } catch (Exception ex) {
//            System.err.println(String.format(
//                    "[RowIndex: %d] [ColumnIndex: %d] Sai định dạng ngày tháng, Giá trị: %s",
//                    rowIndex, columnIndex, cellType == CellType.STRING ? cell.getStringCellValue() : cell.toString()
//            ));
            //ex.printStackTrace();
        }

        return result;
    }


    public static List<HRDepartmentDto> readDepartmentFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataFormatter formatter = new DataFormatter();
        Map<String, HRDepartmentDto> departmentMap = new HashMap<>();
        List<HRDepartmentDto> departmentList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");

        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            String errorMessage = null;
            int lastNumber = sheet.getLastRowNum();
            for (int rowIndex = 1; rowIndex <= lastNumber; rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue;

                HRDepartmentDto department = new HRDepartmentDto();

                Cell stt = currentRow.getCell(0); // stt
                Cell code = currentRow.getCell(1); // Mã phòng ban
                Cell name = currentRow.getCell(2); // Tên phòng ban
                Cell shortName = currentRow.getCell(3); // Tên viết tắt
                Cell orgCode = currentRow.getCell(4); // Mã đơn vị trực thuộc
                Cell orgName = currentRow.getCell(5); // Đơn vị trực thuộc
                Cell parentCode = currentRow.getCell(6); // Mã phòng ban cha
                Cell parentName = currentRow.getCell(7); // Tên phòng ban cha
                Cell departmentTypeCode = currentRow.getCell(8); //  Mã loại phòng ban
                Cell departmentTypeName = currentRow.getCell(9); // Loại phòng ban
                Cell positionTitleManagerCode = currentRow.getCell(10); // Mã chức danh quản lý
                Cell positionTitleManagerName = currentRow.getCell(11); // Chức danh quản lý
                Cell foundedDate = currentRow.getCell(12); // Ngày thành lập
                Cell sortNumber = currentRow.getCell(13); // Thứ tự hiển thị
                Cell description = currentRow.getCell(14); // Mô tả


                String codeString = ExcelUtils.getCellValue(code, String.class);
                if (!StringUtils.hasText(codeString)) {
                    errorMessage = "Mã phòng ban không được để trống, lỗi tại dòng " + (rowIndex);
                    rowIndex = lastNumber + 1;
                    break;
                }
                department.setCode(codeString);

                if (parentCode != null) department.setParentCode(ExcelUtils.getCellValue(parentCode, String.class));
                if (name != null) department.setName(ExcelUtils.getCellValue(name, String.class));
                if (shortName != null) department.setShortName(ExcelUtils.getCellValue(shortName, String.class));
                if (foundedDate != null) {
                    Date foundedDateValue = ExcelUtils.getCellValue(foundedDate, Date.class);
                    department.setFoundedDate(foundedDateValue);
                }
                if (sortNumber != null) {
                    department.setSortNumber(ExcelUtils.getCellValue(sortNumber, Integer.class));
                }
                if (description != null) department.setDescription(ExcelUtils.getCellValue(description, String.class));
                String orgCodeValue = ExcelUtils.getCellValue(orgCode, String.class);
                if (orgCode != null && StringUtils.hasText(orgCodeValue)) {
                    HrOrganizationDto org = new HrOrganizationDto();

                    org.setCode(orgCodeValue);
                    org.setName(getCellValueAsStringSimple(orgName));

                    department.setOrganization(org);
                }
                String parentCodeValue = ExcelUtils.getCellValue(parentCode, String.class);
                if (parentCode != null && StringUtils.hasText(parentCodeValue)) {
                    HRDepartmentDto parent = new HRDepartmentDto();

                    parent.setCode(parentCodeValue);
                    parent.setName(getCellValueAsStringSimple(parentName));

                    department.setParent(parent);
                }
                String departmentTypeCodeValue = ExcelUtils.getCellValue(departmentTypeCode, String.class);
                if (departmentTypeCode != null && StringUtils.hasText(departmentTypeCodeValue)) {
                    DepartmentTypeDto departmentType = new DepartmentTypeDto();

                    departmentType.setCode(departmentTypeCodeValue);
                    departmentType.setName(getCellValueAsStringSimple(departmentTypeName));

                    department.setHrDepartmentType(departmentType);
                }
                String positionTitleManagerCodeValue = ExcelUtils.getCellValue(positionTitleManagerCode, String.class);
                if (positionTitleManagerCode != null && StringUtils.hasText(positionTitleManagerCodeValue)) {
                    PositionTitleDto positionTitleManager = new PositionTitleDto();

                    positionTitleManager.setCode(positionTitleManagerCodeValue);
                    positionTitleManager.setName(getCellValueAsStringSimple(positionTitleManagerName));

                    department.setPositionTitleManager(positionTitleManager);
                }

                // Thêm vào map để tiện tra cứu
                departmentMap.put(department.getCode(), department);

                // Xử lý gán cha-con
                if (department.getParentCode() != null && !department.getParentCode().isEmpty()) {
                    HRDepartmentDto parent = departmentMap.get(department.getParentCode());
                    if (parent != null) {
                        if (parent.getChildren() == null) {
                            parent.setChildren(new ArrayList<>());
                        }
                        parent.getChildren().add(department);
                        department.setParent(parent);
                    } else {
                        // Nếu cha chưa đọc đến thì cứ thêm vào danh sách gốc,
                        // lát nữa xử lý sau bằng parentCode
                        departmentList.add(department);
                    }
                } else {
                    departmentList.add(department);
                }

                //15. Mã chức danh thuộc phòng ban
                Cell currentCell = currentRow.getCell(15); // Mã chức danh thuộc phòng ban
                String positionTitlesString = ExcelUtils.getCellValue(currentCell, String.class);

                if (StringUtils.hasText(positionTitlesString)) {
                    // Tách danh sách theo dấu chấm phẩy
                    String[] positionTitles = positionTitlesString.split(";");

                    // Kiểm tra từng chức danh xem có hợp lệ không (ví dụ: không rỗng)
                    boolean allValid = true;
                    for (String positionTitle : positionTitles) {
                        if (!StringUtils.hasText(positionTitle.trim())) {
                            allValid = false;
                            break;
                        }
                    }

                    if (!allValid) {
                        errorMessage = "Chức danh trực thuộc phòng ban phải nhập đúng quy tắc (cách nhau bằng dấu ';') đang lỗi tại dòng " + (rowIndex);
                        rowIndex = lastNumber + 1;
                        break;
                    }

                    for (int i = 0; i < positionTitles.length; i++) {
                        PositionTitleDto positionTitleDto = new PositionTitleDto();
                        positionTitleDto.setCode(positionTitles[i].trim());
                        if (department.getPositionTitles() == null) {
                            department.setPositionTitles(new ArrayList<>());
                        }
                        department.getPositionTitles().add(positionTitleDto);
                    }

                }
                //16. Phòng ban trực thuộc
                currentCell = currentRow.getCell(16); // Mã chức danh thuộc phòng ban
                String departmentString = ExcelUtils.getCellValue(currentCell, String.class);

                if (StringUtils.hasText(departmentString)) {
                    // Tách danh sách theo dấu chấm phẩy
                    String[] departments = departmentString.split(";");

                    // Kiểm tra từng chức danh xem có hợp lệ không (ví dụ: không rỗng)
                    boolean allValid = true;
                    for (String item : departments) {
                        if (!StringUtils.hasText(item.trim())) {
                            allValid = false;
                            break;
                        }
                    }

                    if (!allValid) {
                        errorMessage = "Phòng ban trực thuộc phải nhập đúng quy tắc (cách nhau bằng dấu ';') đang lỗi tại dòng " + (rowIndex);
                        rowIndex = lastNumber + 1;
                        break;
                    }

                    for (int i = 0; i < departments.length; i++) {
                        HRDepartmentDto departmentDto = new HRDepartmentDto();
                        departmentDto.setCode(departments[i].trim());
                        departmentDto.setUpdate(true);
                        if (department.getChildren() == null) {
                            department.setChildren(new ArrayList<>());
                        }
                        department.getChildren().add(departmentDto);
                    }

                }
                //17. Mã ca làm việc

                currentCell = currentRow.getCell(17); // Mã chức danh thuộc phòng ban
                String shiftWorksString = ExcelUtils.getCellValue(currentCell, String.class);

                if (StringUtils.hasText(shiftWorksString)) {
                    // Tách danh sách theo dấu chấm phẩy
                    String[] shiftWorks = shiftWorksString.split(";");

                    // Kiểm tra từng chức danh xem có hợp lệ không (ví dụ: không rỗng)
                    boolean allValid = true;
                    for (String shiftWork : shiftWorks) {
                        if (!StringUtils.hasText(shiftWork.trim())) {
                            allValid = false;
                            break;
                        }
                    }

                    if (!allValid) {
                        errorMessage = "Ca làm việc thuộc phòng ban phải nhập đúng quy tắc (cách nhau bằng dấu ';') đang lỗi tại dòng " + (rowIndex);
                        rowIndex = lastNumber + 1;
                        break;
                    }

                    for (int i = 0; i < shiftWorks.length; i++) {
                        ShiftWorkDto shiftWork = new ShiftWorkDto();
                        shiftWork.setCode(shiftWorks[i].trim());
                        if (department.getShiftWorks() == null) {
                            department.setShiftWorks(new ArrayList<>());
                        }
                        department.getShiftWorks().add(shiftWork);
                    }

                }
            }
            if (errorMessage != null) {
                HRDepartmentDto response = new HRDepartmentDto();
                response.setErrorMessage(errorMessage);
                return List.of(response);
            }

            return departmentList;


        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }

    }

    public static List<PositionDto> readPositionFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        List<PositionDto> positionList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            int lastRow = sheet.getLastRowNum();
            for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue;

                List<String> errorMessages = new ArrayList<>();
                PositionDto position = new PositionDto();

                // [0] Mã vị trí
                Cell currentCell = currentRow.getCell(0);
                String code = ExcelUtils.parseStringCellValue(currentCell);
                if (!StringUtils.hasText(code)) {
                    errorMessages.add("Mã vị trí không được trống, đang lỗi tại dòng " + (rowIndex));
                }
                position.setCode(code);

                // [1] Tên vị trí
                currentCell = currentRow.getCell(1);
                String name = ExcelUtils.parseStringCellValue(currentCell);
                if (!StringUtils.hasText(name)) {
                    errorMessages.add("Tên vị trí không được trống, đang lỗi tại dòng " + (rowIndex));
                }
                position.setName(name);

                // [2] Mã phòng ban
                currentCell = currentRow.getCell(2);
                String departmentCode = ExcelUtils.parseStringCellValue(currentCell);
                HRDepartmentDto department = new HRDepartmentDto();
                department.setCode(departmentCode);
                position.setDepartment(department);

                // [4] Mã chức danh
                currentCell = currentRow.getCell(4);
                String titleCodeValue = ExcelUtils.parseStringCellValue(currentCell);
                if (!StringUtils.hasText(name)) {
                    errorMessages.add("Mã chức danh không được trống, đang lỗi tại dòng " + (rowIndex));
                }
                PositionTitleDto title = new PositionTitleDto();
                title.setCode(titleCodeValue);
                position.setTitle(title);

                // [6] Mã nhân viên
                currentCell = currentRow.getCell(6);
                String staffCodeValue = ExcelUtils.parseStringCellValue(currentCell);
                StaffDto staffDto = new StaffDto();
                staffDto.setStaffCode(staffCodeValue);
                position.setStaff(staffDto);

                // [8] Là chính
                currentCell = currentRow.getCell(8);
                String isMainValueStr = ExcelUtils.parseStringCellValue(currentCell);
                boolean isMainValue = ExcelUtils.getBooleanValueFromString(isMainValueStr);
                position.setIsMain(isMainValue);

                // [9] Là tạm thời/ tuyển lọc
                currentCell = currentRow.getCell(9);
                String isTemporaryValueStr = ExcelUtils.parseStringCellValue(currentCell);
                boolean isTemporaryValue = ExcelUtils.getBooleanValueFromString(isTemporaryValueStr);
                position.setIsTemporary(isTemporaryValue);

                // [10] Là vị trí kiêm nhiệm
                currentCell = currentRow.getCell(10);
                String isConcurrentValueStr = ExcelUtils.parseStringCellValue(currentCell);
                boolean isConcurrentValue = ExcelUtils.getBooleanValueFromString(isConcurrentValueStr);
                position.setIsConcurrent(isConcurrentValue);

                // [11] Mô tả
                currentCell = currentRow.getCell(11);
                String description = ExcelUtils.parseStringCellValue(currentCell);
                position.setDescription(description);

                // [12], [13] Chức vụ quản lý trực tiếp (mã + tên)
                currentCell = currentRow.getCell(12);
                String managePositionCodeValue = ExcelUtils.parseStringCellValue(currentCell);
                currentCell = currentRow.getCell(13);
                String managePositionNameValue = ExcelUtils.parseStringCellValue(currentCell);

                if (StringUtils.hasText(managePositionCodeValue)) {
                    PositionDto managePosition = new PositionDto();
                    managePosition.setCode(managePositionCodeValue);
                    managePosition.setName(managePositionNameValue);

                    PositionRelationshipDto relationshipDto = new PositionRelationshipDto();
                    relationshipDto.setSupervisor(managePosition);
                    relationshipDto.setRelationshipType(HrConstants.PositionRelationshipType.UNDER_DIRECT_MANAGEMENT.getValue());

                    if (position.getRelationships() == null) {
                        position.setRelationships(new ArrayList<>());
                    }

                    position.getRelationships().add(relationshipDto);
                }

                if (!StringUtils.hasText(position.getCode())
                        && !StringUtils.hasText(position.getName())
                        && !StringUtils.hasText(position.getTitle().getCode())) {
                    errorMessages.add("Dòng dữ liệu không hợp lệ");
                }

                if (!errorMessages.isEmpty()) {
//                    logger.info("Có l");
                    continue;
                }

                positionList.add(position);
            }

//            if (errorMessage != null) {
//                PositionDto response = new PositionDto();
//                response.setErrorMessage(errorMessage);
//                return List.of(response);
//            }

            return positionList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }


    public static List<PositionTitleDto> readGroupPositionTitleFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        List<PositionTitleDto> positionTitleList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            int lastNumber = sheet.getLastRowNum();
            for (int rowIndex = 2; rowIndex <= lastNumber; rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue; // Bỏ qua dòng trống

                PositionTitleDto positionTitle = new PositionTitleDto();

                // Lấy dữ liệu từ các ô trong hàng
                Cell name = currentRow.getCell(0); // Tên chức danh
                Cell code = currentRow.getCell(1); // Mã chức danh
                Cell otherName = currentRow.getCell(2);// Tên khác
                Cell shortName = currentRow.getCell(3); // Tên viết tắt
                Cell description = currentRow.getCell(4);// Mô tả

                if (name != null) {
                    positionTitle.setName(name.getStringCellValue());
                }
                if (code != null) {
                    positionTitle.setCode(code.getStringCellValue());
                } else {
                    continue;
                }
                if (otherName != null) {
                    positionTitle.setOtherName(otherName.getStringCellValue());
                }
                if (shortName != null) {
                    positionTitle.setShortName(shortName.getStringCellValue());
                }
                if (description != null) {
                    positionTitle.setDescription(description.getStringCellValue());
                }
                positionTitleList.add(positionTitle);
            }
            return positionTitleList;

        } catch (
                Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    public static List<DepartmentTypeDto> readDepartmentTypeFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataFormatter formatter = new DataFormatter();
        List<DepartmentTypeDto> departmentTypeList = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue;
                DepartmentTypeDto departmentTypeDto = new DepartmentTypeDto();
                Cell code = currentRow.getCell(0); // Mã loại phòng ban (*)
                Cell name = currentRow.getCell(1); // Tên loại phòng ban (*)
                Cell sortNumber = currentRow.getCell(2); // Trọng số (*)
                Cell shortName = currentRow.getCell(3); // Tên viết tắt
                Cell otherName = currentRow.getCell(4); // Tên khác
                Cell description = currentRow.getCell(5); // Mô tả
                if (code != null) departmentTypeDto.setCode(code.getStringCellValue());
                if (name != null) departmentTypeDto.setName(name.getStringCellValue());
                if (sortNumber != null) {
                    String sortNumberStr = formatter.formatCellValue(sortNumber);
                    if (!sortNumberStr.isEmpty()) {
                        try {
                            departmentTypeDto.setSortNumber(Integer.parseInt(sortNumberStr));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (shortName != null) departmentTypeDto.setShortName(shortName.getStringCellValue());
                if (otherName != null) departmentTypeDto.setOtherName(otherName.getStringCellValue());
                if (description != null) departmentTypeDto.setDescription(description.getStringCellValue());
                if (departmentTypeDto.getName() != null && departmentTypeDto.getCode() != null && departmentTypeDto.getSortNumber() != null) {
                    departmentTypeList.add(departmentTypeDto);
                }
            }
            return departmentTypeList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    public static List<HrAdministrativeUnitDto> readAdministrativeUnitFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataFormatter formatter = new DataFormatter();
        Map<String, HrAdministrativeUnitDto> administrativeUnitMap = new HashMap<>();

        List<HrAdministrativeUnitDto> administrativeUnitList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue;

                HrAdministrativeUnitDto hrAdministrativeUnitDto = new HrAdministrativeUnitDto();

                Cell code = currentRow.getCell(0); // Mã đơn vị
                Cell name = currentRow.getCell(1); // Tên đơn vị
                Cell level = currentRow.getCell(2); // Cấp độ
                Cell parentCode = currentRow.getCell(4); // Mã phòng ban cha
                Cell description = currentRow.getCell(6); // Mô tả

                if (code != null) {
                    String codeValue = formatter.formatCellValue(code).trim();
                    if (StringUtils.hasText(codeValue)) {
                        hrAdministrativeUnitDto.setCode(codeValue);
                    } else {
                        continue;
                    }
                }
                if (name != null) hrAdministrativeUnitDto.setName(name.getStringCellValue());
                if (level != null) {
                    String levelStr = formatter.formatCellValue(level);
                    if (!levelStr.isEmpty()) {
                        try {
                            hrAdministrativeUnitDto.setLevel(Integer.parseInt(levelStr));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (parentCode != null) hrAdministrativeUnitDto.setParentCode(parentCode.getStringCellValue());
                if (description != null) hrAdministrativeUnitDto.setDescription(description.getStringCellValue());

                // Thêm vào map để tiện tra cứu
                administrativeUnitMap.put(hrAdministrativeUnitDto.getCode(), hrAdministrativeUnitDto);

                // Xử lý gán cha-con
                if (hrAdministrativeUnitDto.getParentCode() != null && !hrAdministrativeUnitDto.getParentCode().isEmpty()) {
                    HrAdministrativeUnitDto parent = administrativeUnitMap.get(hrAdministrativeUnitDto.getParentCode());
                    if (parent != null) {
                        if (parent.getChildren() == null) {
                            parent.setChildren(new ArrayList<>());
                        }
                        parent.getChildren().add(hrAdministrativeUnitDto);
                        hrAdministrativeUnitDto.setParent(parent);
                    } else {
                        // Nếu cha chưa đọc đến thì cứ thêm vào danh sách gốc,
                        // lát nữa xử lý sau bằng parentCode
                        administrativeUnitList.add(hrAdministrativeUnitDto);
                    }
                } else {
                    administrativeUnitList.add(hrAdministrativeUnitDto);
                }
            }

            // Sau khi đọc xong, quét lại để gán cha-con nếu có bị sót do cha chưa tồn tại lúc đọc con
            List<HrAdministrativeUnitDto> finalizedList = new ArrayList<>();
            for (HrAdministrativeUnitDto dept : administrativeUnitMap.values()) {
                if (dept.getParentCode() == null || dept.getParentCode().isEmpty()) {
                    finalizedList.add(dept);
                }
            }

            return finalizedList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }

    }

    public static List<HrAdministrativeUnitDto> readAllAdministrativeUnitFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataFormatter formatter = new DataFormatter();
        List<HrAdministrativeUnitDto> administrativeUnitList = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue;

                HrAdministrativeUnitDto hrAdministrativeUnitDto = new HrAdministrativeUnitDto();


                // 0. STT
                // 1. Mã đơn vị hành chính(*)
                Cell code = currentRow.getCell(1); // Mã đơn vị
                // 2. Tên đơn vị hành chính
                Cell name = currentRow.getCell(2); // Tên đơn vị
                // 3. Mã cấp độ(*)
                Cell level = currentRow.getCell(3); // Cấp độ
                // 4. Cấp độ
                // 5. Mã đơn vị quản lý
                Cell parentCode = currentRow.getCell(5); // Mã đơn vị quản lý
                // 6. Tên đơn vị quản lý
                // 7. Mô tả
                Cell description = currentRow.getCell(7); // Mô tả


                if (code != null) {
                    String codeValue = formatter.formatCellValue(code).trim();
                    if (StringUtils.hasText(codeValue)) {
                        hrAdministrativeUnitDto.setCode(codeValue);
                    } else {
                        continue;
                    }
                }
                if (name != null) hrAdministrativeUnitDto.setName(name.getStringCellValue());
                if (level != null) {
                    String levelStr = formatter.formatCellValue(level);
                    if (!levelStr.isEmpty()) {
                        try {
                            hrAdministrativeUnitDto.setLevel(Integer.parseInt(levelStr));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (parentCode != null) {
                    hrAdministrativeUnitDto.setParentCode(ExcelUtils.getCellValue(parentCode, String.class));
                }
                if (description != null) {
                    hrAdministrativeUnitDto.setDescription(ExcelUtils.getCellValue(description, String.class));
                }
                administrativeUnitList.add(hrAdministrativeUnitDto);
            }
            return administrativeUnitList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }

    }


    public static List<ImportPositionRelationShipDto> readAllPositionRelationShipFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataFormatter formatter = new DataFormatter();
        List<ImportPositionRelationShipDto> res = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue;

                ImportPositionRelationShipDto item = new ImportPositionRelationShipDto();

                Cell codeSupervisor = currentRow.getCell(0); // Mã chức vụ quản lý
                Cell nameSupervisor = currentRow.getCell(1); // Tên chức vụ quản lý

                Cell code = currentRow.getCell(2); // Mã chức vụ
                Cell name = currentRow.getCell(3); // Tên chức vụ

                Cell relationshipType = currentRow.getCell(4); // Cấp độ

                Cell codeDepartment = currentRow.getCell(5); // Mã phòng ban
                Cell nameDepartment = currentRow.getCell(6); // Tên phòng ban

                if (codeSupervisor != null) {
                    String codeSupervisorValue = formatter.formatCellValue(codeSupervisor).trim();
                    if (StringUtils.hasText(codeSupervisorValue)) {
                        item.setSupervisorCode(codeSupervisorValue);
                    }
                }
                if (nameSupervisor != null) item.setSupervisorName(nameSupervisor.getStringCellValue());

                if (code != null) {
                    String codeValue = formatter.formatCellValue(code).trim();
                    if (StringUtils.hasText(codeValue)) {
                        item.setCode(codeValue);
                    }
                }
                if (name != null) item.setName(name.getStringCellValue());

                if (relationshipType != null) {
                    String relationshipTypeStr = formatter.formatCellValue(relationshipType);
                    if (!relationshipTypeStr.isEmpty()) {
                        try {
                            item.setRelationshipType(Integer.parseInt(relationshipTypeStr));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (codeDepartment != null) {
                    String codeDepartmentValue = formatter.formatCellValue(codeDepartment).trim();
                    if (StringUtils.hasText(codeDepartmentValue)) {
                        item.setDepartmentCode(codeDepartmentValue);
                    }
                }
                if (nameDepartment != null) item.setDepartmentName(nameDepartment.getStringCellValue());
                item.setIndex(rowIndex);

                res.add(item);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }


    public static List<HrAdministrativeUnitDto> readAllAdministrativeUnitFileV2(ByteArrayInputStream byteArrayInputStream) throws IOException {
        List<HrAdministrativeUnitDto> administrativeUnitList = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue;

                HrAdministrativeUnitDto hrAdministrativeUnitDto = new HrAdministrativeUnitDto();
                Cell code = currentRow.getCell(0); // Mã đơn vị
                Cell name = currentRow.getCell(1); // Tên đơn vị
                Cell parentCode = currentRow.getCell(3); // Mã phòng ban cha(Mã QH)
                Cell provinceCode = currentRow.getCell(5); //Mã Tỉnh/ Thành Phố
                if (code != null) {
                    Object cellValue = getCellValue(code, String.class);
                    String codeValue = (cellValue != null) ? cellValue.toString().strip() : "";
                    if (StringUtils.hasText(codeValue)) {
                        hrAdministrativeUnitDto.setCode(codeValue);
                    } else {
                        continue;
                    }
                }

                if (name != null) {
                    Object nameValue = getCellValue(name, String.class);
                    hrAdministrativeUnitDto.setName((nameValue != null) ? nameValue.toString().strip() : "");
                }

                String strParentCode = "";
                if (parentCode != null) {
                    Object parentValue = getCellValue(parentCode, String.class);
                    strParentCode = (parentValue != null) ? parentValue.toString().strip() : "";
                    hrAdministrativeUnitDto.setParentCode(strParentCode);
                }

                String strProvinceCode = "";
                if (provinceCode != null) {
                    Object provinceValue = getCellValue(provinceCode, String.class);
                    strProvinceCode = (provinceValue != null) ? provinceValue.toString().strip() : "";
                }

                if (!strParentCode.isEmpty() && !strProvinceCode.isEmpty()) {
                    hrAdministrativeUnitDto.setLevel(HrConstants.AdminitractiveLevel.PROVINCE.getValue());
                } else if (strParentCode.isEmpty() && !strProvinceCode.isEmpty()) {
                    hrAdministrativeUnitDto.setLevel(HrConstants.AdminitractiveLevel.DISTRICT.getValue());
                } else {
                    hrAdministrativeUnitDto.setLevel(HrConstants.AdminitractiveLevel.COMMUNE.getValue());
                }

                administrativeUnitList.add(hrAdministrativeUnitDto);

            }
            return administrativeUnitList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }

    }

    public static List<ShiftWorkDto> readShiftWorkDtoFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<ShiftWorkDto> shiftWorkList = new ArrayList<>();
            int lastRow = sheet.getLastRowNum();
            String errorMessage = null;
            Calendar calendar = Calendar.getInstance();
            for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                ShiftWorkDto shiftWorkDto = new ShiftWorkDto();
                Row currentRow = sheet.getRow(rowIndex);
                ShiftWorkTimePeriodDto timePeriodsDto = new ShiftWorkTimePeriodDto();
                if (shiftWorkDto.getTimePeriods() == null) {
                    shiftWorkDto.setTimePeriods(new ArrayList<>());
                }

                if (currentRow == null) continue;

                Cell currentCell = currentRow.getCell(0);
                // 0. Mã ca làm việc
                String codeShiftWork = ExcelUtils.getCellValue(currentCell, String.class);
                if (!StringUtils.hasText(codeShiftWork)) {
                    errorMessage = "Mã ca làm việc không được để trống, lỗi tại dòng " + (rowIndex);
                    rowIndex = lastRow + 1;
                    break;
                }
                shiftWorkDto.setCode(codeShiftWork);

                // 1. Tên ca làm việc
                currentCell = currentRow.getCell(1);
                String nameShiftWork = ExcelUtils.getCellValue(currentCell, String.class);
                shiftWorkDto.setName(nameShiftWork);
                // 2. Mã phòng ban
                currentCell = currentRow.getCell(2);
                String codeDepartmentString = ExcelUtils.getCellValue(currentCell, String.class);

                if (StringUtils.hasText(codeDepartmentString)) {
                    // Tách danh sách theo dấu chấm phẩy
                    String[] codeDepartments = codeDepartmentString.split(";");

                    // Kiểm tra từng chức danh xem có hợp lệ không (ví dụ: không rỗng)
                    boolean allValid = true;
                    for (String codeDepartment : codeDepartments) {
                        if (!StringUtils.hasText(codeDepartment.trim())) {
                            allValid = false;
                            break;
                        }
                    }

                    if (!allValid) {
                        errorMessage = "Mã phòng ban phải nhập đúng quy tắc (cách nhau bằng dấu ';') đang lỗi tại dòng " + (rowIndex);
                        rowIndex = lastRow + 1;
                        break;
                    }

                    for (int i = 0; i < codeDepartments.length; i++) {
                        HRDepartmentDto departmentDto = new HRDepartmentDto();
                        departmentDto.setCode(codeDepartments[i].trim());
                        if (shiftWorkDto.getDepartments() == null) {
                            shiftWorkDto.setDepartments(new ArrayList<>());
                        }
                        shiftWorkDto.getDepartments().add(departmentDto);
                    }

                }
                // 3. Tên phòng ban
                // 4. Mã giai đoạn
                currentCell = currentRow.getCell(4);
                String timePeriodsCode = ExcelUtils.getCellValue(currentCell, String.class);
                if (!StringUtils.hasText(timePeriodsCode)) {
                    errorMessage = "Mã giai đoạn không được để trống, lỗi tại dòng " + (rowIndex);
                    rowIndex = lastRow + 1;
                    break;
                }

                timePeriodsDto.setCode(timePeriodsCode);

                //5. Thời gian bắt đầu
                currentCell = currentRow.getCell(5);
                Date startDateTime = null;

                if (currentCell != null) {
                    if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(currentCell)) {
                        calendar.setTime(currentCell.getDateCellValue());
                        LocalTime startTime = LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                calendar.get(Calendar.SECOND));
                        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), startTime);
                        startDateTime = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                    } else if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                        String timeText = currentCell.getStringCellValue().trim();
                        String[] parts = timeText.split(":");
                        if (parts.length == 2) {
                            LocalTime startTime = LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), startTime);
                            startDateTime = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                        }
                    }
                }

                if (startDateTime == null) {
                    errorMessage = "Thời gian bắt đầu không được để trống, lỗi tại dòng " + (rowIndex);
                    rowIndex = lastRow + 1;
                    break;
                }

                timePeriodsDto.setStartTime(startDateTime);


                //6. Thời gian kết thúc
                currentCell = currentRow.getCell(6);
                Date endDateTime = null;

                if (currentCell != null) {
                    if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(currentCell)) {
                        calendar.setTime(currentCell.getDateCellValue());
                        LocalTime startTime = LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                calendar.get(Calendar.SECOND));
                        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), startTime);
                        endDateTime = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                    } else if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                        String timeText = currentCell.getStringCellValue().trim();
                        String[] parts = timeText.split(":");
                        if (parts.length == 2) {
                            LocalTime startTime = LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), startTime);
                            endDateTime = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                        }
                    }
                }
                if (endDateTime == null) {
                    errorMessage = "Thời gian kết thúc không được để trống, lỗi tại dòng " + (rowIndex);
                    rowIndex = lastRow + 1;
                    break;
                }
                timePeriodsDto.setEndTime(endDateTime);
                // Hoặc nếu bạn dùng Duration:
                Instant startInstant = startDateTime.toInstant();
                Instant endInstant = endDateTime.toInstant();

                LocalDateTime startLdt = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault());
                LocalDateTime endLdt = LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault());

                Duration duration = Duration.between(startLdt, endLdt);
                double totalHours = duration.toMinutes() / 60.0;

                // Lưu vào DTO
                shiftWorkDto.setTotalHours(totalHours);


                //7. Tỉ lệ ngày công
                currentCell = currentRow.getCell(7);
                Double timePeriodsWorkRatio = ExcelUtils.getCellValue(currentCell, Double.class);
                timePeriodsDto.setWorkRatio(timePeriodsWorkRatio);
                //8. Thời gian tối thiểu để tính đi làm (giờ)
                currentCell = currentRow.getCell(8);
                Double minWorkTimeHour = ExcelUtils.getCellValue(currentCell, Double.class);
                timePeriodsDto.setMinWorkTimeHour(minWorkTimeHour);

                shiftWorkDto.getTimePeriods().add(timePeriodsDto);

                shiftWorkList.add(shiftWorkDto);
            }
            if (errorMessage != null) {
                ShiftWorkDto response = new ShiftWorkDto();
                response.setErrorMessage(errorMessage);
                return List.of(response);
            }
            return shiftWorkList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    public static Object getCellValue(Cell cell, Class<?> type) {
        if (cell == null) return getDefaultValue(type);

        Object value;
        try {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    value = cell.getStringCellValue();
                    if (type == LocalDate.class) {
                        return handleDateString(cell.getStringCellValue(), type);
                    }
                    if (type == LocalDateTime.class) {
                        return handleDateString(cell.getStringCellValue(), type);
                    }
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) { // Kiểm tra Date
                        Date date = cell.getDateCellValue();
                        if (type == LocalDate.class)
                            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        if (type == LocalDateTime.class)
                            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        return date;
                    }
                    if (type == LocalDate.class) {
                        return handleDateString(String.valueOf(cell.getNumericCellValue()), type);
                    }
                    if (type == LocalDateTime.class) {
                        return handleDateString(String.valueOf(cell.getNumericCellValue()), type);
                    }
                    value = cell.getNumericCellValue();
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    value = cell.getBooleanCellValue();
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    try {
                        value = cell.getNumericCellValue();
                    } catch (IllegalStateException e) {
                        value = cell.getStringCellValue();
                    }
                    break;
                case Cell.CELL_TYPE_ERROR:
                    value = "ERROR_" + cell.getErrorCellValue();
                    break;
                case Cell.CELL_TYPE_BLANK:
                default:
                    return getDefaultValue(type);
            }
            return castToType(value, type);
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý ô Excel: {}", type.getSimpleName());
            return getDefaultValue(type); // Trả về giá trị mặc định khi lỗi
        }
    }

    private static Object castToType(Object value, Class<?> type) {
        if (value == null || !StringUtils.hasText(value.toString())) return getDefaultValue(type);

        try {
            if (type == String.class) return value.toString();
            if (type == Integer.class || type == int.class) return (int) Double.parseDouble(value.toString());
            if (type == Long.class || type == long.class)
                return (long) Double.parseDouble(value.toString()); // Fix lỗi ép kiểu
            if (type == Double.class || type == double.class) return Double.parseDouble(value.toString());
            if (type == Float.class || type == float.class) return Float.parseFloat(value.toString());
            if (type == Short.class || type == short.class) return (short) Double.parseDouble(value.toString());
            if (type == Byte.class || type == byte.class) return (byte) Double.parseDouble(value.toString());
            if (type == BigDecimal.class) return new BigDecimal(value.toString());
            if (type == Boolean.class || type == boolean.class) return Boolean.parseBoolean(value.toString());
            return value;
        } catch (Exception e) {
            logger.error("Lỗi ép kiểu: {} -> {}", value, type.getSimpleName());
            return getDefaultValue(type);
        }
    }

    private static Object handleDateString(String value, Class<?> type) {
        if (value == null || !StringUtils.hasText(value.toString())) return getDefaultValue(type);
        try {
            String strValue = value.toString().trim();

            // Kiểm tra nếu là số (dữ liệu từ Excel có thể bị chuyển thành số thực)
            if (strValue.matches("\\d+\\.\\d+")) {
                strValue = strValue.split("\\.")[0]; // Lấy phần nguyên nếu là số thực (e.g., "2000.0" -> "2000")
            }
            if (type == LocalDate.class || type == LocalDateTime.class) {
                String[] parts = strValue.split("/");
                int day = 1, month = 1, year = 1000;

                if (parts.length == 1) { // Chỉ có năm
                    year = Integer.parseInt(parts[0]);
                } else if (parts.length == 2) { // Chỉ có tháng và năm
                    month = Integer.parseInt(parts[0]);
                    year = Integer.parseInt(parts[1]);
                } else if (parts.length == 3) { // Đủ ngày/tháng/năm
                    day = Integer.parseInt(parts[0]);
                    month = Integer.parseInt(parts[1]);
                    year = Integer.parseInt(parts[2]);
                }
                LocalDate date = LocalDate.of(year, month, day);
                return type == LocalDate.class ? date : date.atStartOfDay();
            }
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý chuỗi ngày tháng: {}", value);
        }
        return value;
    }

    private static Object getDefaultValue(Class<?> type) {
        if (type == int.class) return 0;
        if (type == double.class) return 0.0;
        if (type == float.class) return 0.0f;
        if (type == long.class) return 0L;
        if (type == short.class) return (short) 0;
        if (type == byte.class) return (byte) 0;
        if (type == boolean.class) return false;
        return null; // Các kiểu Object mặc định là null
    }

    public static List<StaffWorkScheduleDto> readAllStaffWorkScheduleFile(ByteArrayInputStream bis) throws IOException {
        DataFormatter formatter = new DataFormatter();
        List<StaffWorkScheduleDto> staffWorkScheduleList = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(bis)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue;

                StaffWorkScheduleDto dto = new StaffWorkScheduleDto();
                StaffDto staff = new StaffDto();

                int index = 0;
                Cell staffCodeCell = currentRow.getCell(index); // Mã nhân viên
                // Mã nhân viên (*)
                if (staffCodeCell != null) {
                    String staffCode = ExcelUtils.getCellValue(staffCodeCell, String.class);
                    dto.setStaffCode(staffCode);
                    staff.setStaffCode(staffCode);
                } else {
                    dto.setErrorMessage("Mã nhân viên trong file import không được bỏ trống");
                    staffWorkScheduleList.clear();
                    staffWorkScheduleList.add(dto);
                    return staffWorkScheduleList;
                }
                index++;

                // Tên nhân viên
                Cell staffNameCell = currentRow.getCell(index);
                if (staffNameCell != null) {
//                    String staffName = ExcelUtils.getCellValue(staffNameCell, String.class);
//                    staff.setStaffCode(staffName);
                }
                index++;

                // Ngày làm việc  (*)
                Cell workingDateCell = currentRow.getCell(index);
                if (workingDateCell != null) {
                    Date workingDate = ExcelUtils.getCellValue(workingDateCell, Date.class);
                    dto.setWorkingDate(workingDate);
                } else {
                    dto.setErrorMessage("Ngày làm việc trong file import không được bỏ trống");
                    staffWorkScheduleList.clear();
                    staffWorkScheduleList.add(dto);
                    return staffWorkScheduleList;
                }
                index++;

                // Mã ca làm việc  (*)
                Cell shiftWorkCodeCell = currentRow.getCell(index);
                if (shiftWorkCodeCell != null) {
                    String shiftWorkCode = ExcelUtils.getCellValue(shiftWorkCodeCell, String.class);
                    if (!StringUtils.hasText(shiftWorkCode)) {
                        dto.setErrorMessage("Mã ca làm việc trong file import không được bỏ trống");
                        staffWorkScheduleList.clear();
                        staffWorkScheduleList.add(dto);
                        return staffWorkScheduleList;
                    }
                    dto.setShiftWorkCode(shiftWorkCode);
                } else {
                    dto.setErrorMessage("Mã ca làm việc trong file import không được bỏ trống");
                    staffWorkScheduleList.clear();
                    staffWorkScheduleList.add(dto);
                    return staffWorkScheduleList;
                }
                index++;

                // Ca làm việc
                Cell shiftWorkNameCell = currentRow.getCell(index);
                if (shiftWorkNameCell != null) {
                }
                index++;

                // Cách tinh thời gian
                Cell timeKeepingCalculationType = currentRow.getCell(index);
                if (timeKeepingCalculationType != null) {
                    String timeKeepingCalculationString = ExcelUtils.getCellValue(timeKeepingCalculationType, String.class);

                    // Try to get enum value
                    HrConstants.TimekeepingCalculationType calculationType = ExcelUtils.getEnumValue(timeKeepingCalculationString, HrConstants.TimekeepingCalculationType.class);

                    // Nếu không tìm thấy enum hợp lệ, set mặc định là 2 (hoặc giá trị bạn muốn)
                    if (calculationType != null) {
                        dto.setTimekeepingCalculationType(calculationType.getValue());
                    } else {
                        // Set giá trị mặc định (2) nếu không đọc được enum
                        dto.setTimekeepingCalculationType(2);
                    }
                }
                index++;

                // Chỉ chấm công 1 lần
                Cell allowOneEntryOnlyCell = currentRow.getCell(index);
                if (allowOneEntryOnlyCell != null) {
                    Boolean allowOneEntryOnly = ExcelUtils.getCellValue(allowOneEntryOnlyCell, Boolean.class);
                    dto.setAllowOneEntryOnly(allowOneEntryOnly);
                } else {
                    dto.setAllowOneEntryOnly(true);
                }
                index++;
                // Chỉ chấm công 1 lần
                Cell needManagerApproval = currentRow.getCell(index);
                if (allowOneEntryOnlyCell != null) {
                    Boolean isNeedManagerApproval = ExcelUtils.getCellValue(needManagerApproval, Boolean.class);
                    dto.setNeedManagerApproval(isNeedManagerApproval);
                } else {
                    dto.setNeedManagerApproval(false);
                }
                index++;

                staffWorkScheduleList.add(dto);
            }

            return staffWorkScheduleList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    public static List<EvaluationItemDto> readEvaluationItemDtoFile(ByteArrayInputStream bis) throws IOException {
        Map<String, EvaluationItemDto> evaluationItemMap = new LinkedHashMap<>();

        try (Workbook workbook = new XSSFWorkbook(bis)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue;

                // Lấy dữ liệu từ các ô trong hàng
                Cell currenCode = currentRow.getCell(1);
                Cell currenName = currentRow.getCell(2);
                Cell currenDescription = currentRow.getCell(3);

                String code = ExcelUtils.getCellValue(currenCode, String.class);
                String name = ExcelUtils.getCellValue(currenName, String.class);
                String description = ExcelUtils.getCellValue(currenDescription, String.class);

                // Bỏ qua dòng nếu không có mã (code)
                if (code == null || code.trim().isEmpty()) continue;

                // Kiểm tra trùng mã
                if (!evaluationItemMap.containsKey(code)) {
                    EvaluationItemDto dto = new EvaluationItemDto();
                    dto.setCode(code.trim());
                    dto.setName(name != null ? name.trim() : null);
                    dto.setDescription(description != null ? description.trim() : null);

                    evaluationItemMap.put(code, dto);
                }
            }

            return new ArrayList<>(evaluationItemMap.values());

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    public static List<RecruitmentPlanDto> readRecruitmentPlanFromFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<RecruitmentPlanDto> recruitmentPlanList = new ArrayList<>();
            int lastRow = sheet.getLastRowNum();
            String errorMessage = null;
            Calendar calendar = Calendar.getInstance();
            for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                RecruitmentPlanDto recruitmentPlan = new RecruitmentPlanDto();
                RecruitmentRequestDto recruitment = new RecruitmentRequestDto();
                Row currentRow = sheet.getRow(rowIndex);

                if (currentRow == null) continue;

                // 0. Mã kế hoạch *
                Cell currentCell = currentRow.getCell(0);
                String codeRecruitmentPlan = ExcelUtils.getCellValue(currentCell, String.class);
                if (!StringUtils.hasText(codeRecruitmentPlan)) {
                    errorMessage = "Mã kế hoạch không được để trống, lỗi tại dòng " + (rowIndex);
                    rowIndex = lastRow + 1;
                    break;
                }
                recruitmentPlan.setCode(codeRecruitmentPlan);

                // 1. Tên kế hoạch *
                currentCell = currentRow.getCell(1);
                String nameRecruitmentPlan = ExcelUtils.getCellValue(currentCell, String.class);
                if (!StringUtils.hasText(nameRecruitmentPlan)) {
                    errorMessage = "Tên kế hoạch không được để trống, lỗi tại dòng " + (rowIndex);
                    rowIndex = lastRow + 1;
                    break;
                }
                recruitmentPlan.setName(nameRecruitmentPlan);
                // 2. Mã yêu cầu tuyển dụng
                currentCell = currentRow.getCell(2);
                String codeRecruitment = ExcelUtils.getCellValue(currentCell, String.class);
                recruitment.setCode(codeRecruitment);
                // 3. Tên yêu cầu tuyển dụng
                currentCell = currentRow.getCell(3);
                String nameRecruitment = ExcelUtils.getCellValue(currentCell, String.class);
                recruitment.setName(nameRecruitment);

                recruitmentPlan.setRecruitmentRequest(recruitment);

                //4. Trạng thái
                currentCell = currentRow.getCell(4);
                Integer status = ExcelUtils.getCellValue(currentCell, Integer.class);
                if (status != null) {
                    if (status.equals(HrConstants.RecruitmentPlanStatus.NOT_APPROVED_YET.getValue())
                            || status.equals(HrConstants.RecruitmentPlanStatus.APPROVED.getValue())
                            || status.equals(HrConstants.RecruitmentPlanStatus.REJECTED.getValue())
                            || status.equals(HrConstants.RecruitmentPlanStatus.COMPLETED.getValue())
                    ) {
                        recruitmentPlan.setStatus(status);
                    } else {
                        errorMessage = "Trạng thái không hợp lệ, lỗi tại dòng " + (rowIndex);
                        rowIndex = lastRow + 1;
                        break;
                    }
                }
                // 5. Thời gian dự kế từ ngày
                currentCell = currentRow.getCell(5);
                Date estimatedTimeFrom = ExcelUtils.getCellValue(currentCell, Date.class);
                recruitmentPlan.setEstimatedTimeFrom(estimatedTimeFrom);
                // 6. Thời gian dự kiến đến ngày
                currentCell = currentRow.getCell(6);
                Date estimatedTimeTo = ExcelUtils.getCellValue(currentCell, Date.class);
                recruitmentPlan.setEstimatedTimeTo(estimatedTimeTo);
                //7. Nội dung kế hoạch
                currentCell = currentRow.getCell(7);
                String description = ExcelUtils.getCellValue(currentCell, String.class);
                recruitmentPlan.setDescription(description);

                recruitmentPlanList.add(recruitmentPlan);
            }
            if (errorMessage != null) {
                RecruitmentPlanDto response = new RecruitmentPlanDto();
                response.setErrorMessage(errorMessage);
                return List.of(response);
            }
            return recruitmentPlanList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    public static CandidateImport readCandidateFromFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            CandidateImport result = new CandidateImport();
            if (result.getCandidates() == null) {
                result.setCandidates(new ArrayList<>());
            }
            if (result.getErrors() == null) {
                result.setErrors(new ArrayList<>());
            }


            int lastRow = sheet.getLastRowNum();
            for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                CandidateDto candidate = new CandidateDto();
                Row currentRow = sheet.getRow(rowIndex);

                if (currentRow == null) continue;

                // 0. Mã ứng viên *
                Cell currentCell = currentRow.getCell(0);
                String codeCandidate = ExcelUtils.getCellValue(currentCell, String.class);
                if (!StringUtils.hasText(codeCandidate)) {
                    result.getErrors().add("Mã ứng viên không được để trống, lỗi tại dòng " + (rowIndex));
                }
                candidate.setCandidateCode(codeCandidate);

                //1. Họ vên ứng viên *
                currentCell = currentRow.getCell(1);
                String fullName = ExcelUtils.getCellValue(currentCell, String.class);
                if (!StringUtils.hasText(fullName)) {
                    result.getErrors().add("Họ vên ứng viên không được để trống, lỗi tại dòng " + (rowIndex));
                }
                String firstName = "";
                String lastName = "";

                if (StringUtils.hasText(fullName)) {
                    String[] parts = fullName.trim().split("\\s+");
                    if (parts.length > 1) {
                        firstName = parts[parts.length - 1];
                        lastName = String.join(" ", Arrays.copyOfRange(parts, 0, parts.length - 1)); // Họ và tên đệm
                    } else {
                        firstName = fullName;
                    }
                }

                candidate.setDisplayName(fullName);
                candidate.setFirstName(firstName);
                candidate.setLastName(lastName);
                //2. Giới tính
                currentCell = currentRow.getCell(2);
                String genderString = ExcelUtils.getCellValue(currentCell, String.class);
                if (genderString != null) {
                    if (genderString.equals(HrConstants.Gender.MALE.getCode())
                            || genderString.equals(HrConstants.Gender.FEMALE.getCode())
                            || genderString.equals(HrConstants.Gender.OTHER.getCode())
                    ) {
                        candidate.setGender(genderString);
                    } else {
                        result.getErrors().add("Giới tính không hợp lệ, lỗi tại dòng " + (rowIndex));
                    }
                }
                //3. Ngày sinh
                currentCell = currentRow.getCell(3);
                Date birthDate = ExcelUtils.getCellValue(currentCell, Date.class);
                candidate.setBirthDate(birthDate);
                //4. Tình trạng hôn nhân
                currentCell = currentRow.getCell(4);
                Integer maritalStatus = ExcelUtils.getCellValue(currentCell, Integer.class);
                if (maritalStatus != null) {
                    if (maritalStatus.equals(HrConstants.StaffMaritalStatus.SINGLE.getValue())
                            || maritalStatus.equals(HrConstants.StaffMaritalStatus.MARRIED.getValue())
                            || maritalStatus.equals(HrConstants.StaffMaritalStatus.SEPARATED.getValue())
                            || maritalStatus.equals(HrConstants.StaffMaritalStatus.DIVORCED.getValue())
                            || maritalStatus.equals(HrConstants.StaffMaritalStatus.OTHERS.getValue())
                    ) {
                        candidate.setMaritalStatus(maritalStatus);
                    } else {
                        result.getErrors().add("Tình trạng hôn nhân không hợp lệ, lỗi tại dòng " + (rowIndex));
                    }
                }
                //5. Ngyên quán/Nơi sinh
                currentCell = currentRow.getCell(5);
                String nativePlaceCode = ExcelUtils.getCellValue(currentCell, String.class);
                AdministrativeUnitDto nativePlace = new AdministrativeUnitDto();
                nativePlace.setCode(nativePlaceCode);
                candidate.setNativeVillage(nativePlace);
                //6. Chi tiết thường trú
                currentCell = currentRow.getCell(6);
                String permanentResidence = ExcelUtils.getCellValue(currentCell, String.class);
                candidate.setPermanentResidence(permanentResidence);
                //7. Tạm trú
                currentCell = currentRow.getCell(7);
                String currentResidence = ExcelUtils.getCellValue(currentRow.getCell(7), String.class);
                candidate.setCurrentResidence(currentResidence);
                //8. Mã quê quán tỉnh
                currentCell = currentRow.getCell(8);
                String provinceCode = ExcelUtils.getCellValue(currentCell, String.class);
                HrAdministrativeUnitDto province = new HrAdministrativeUnitDto();
                province.setCode(provinceCode);
                candidate.setProvince(province);

                //9. Mã quận huyện
                currentCell = currentRow.getCell(9);
                String districtCode = ExcelUtils.getCellValue(currentCell, String.class);
                HrAdministrativeUnitDto district = new HrAdministrativeUnitDto();
                district.setCode(districtCode);
                candidate.setDistrict(district);
                //10. Mã xã/phường
                currentCell = currentRow.getCell(10);
                String communeCode = ExcelUtils.getCellValue(currentCell, String.class);
                HrAdministrativeUnitDto commune = new HrAdministrativeUnitDto();
                commune.setCode(communeCode);
                candidate.setAdministrativeUnit(commune);
                //11. Số CMND
                currentCell = currentRow.getCell(11);
                String idNumber = ExcelUtils.getCellValue(currentCell, String.class);
                candidate.setIdNumber(idNumber);
                //12. Ngày cấp CMND
                currentCell = currentRow.getCell(12);
                Date idNumberIssueDate = ExcelUtils.getCellValue(currentCell, Date.class);
                candidate.setIdNumberIssueDate(idNumberIssueDate);
                //13. Nơi cấp CMND
                currentCell = currentRow.getCell(13);
                String idNumberIssueBy = ExcelUtils.getCellValue(currentCell, String.class);
                candidate.setIdNumberIssueBy(idNumberIssueBy);
                //14. Mã quốc tịch
                currentCell = currentRow.getCell(14);
                String nationalityCode = ExcelUtils.getCellValue(currentCell, String.class);
                CountryDto country = new CountryDto();
                country.setCode(nationalityCode);
                candidate.setNationality(country);
                //15. Mã dân tộc
                currentCell = currentRow.getCell(15);
                String ethnicsCode = ExcelUtils.getCellValue(currentCell, String.class);
                EthnicsDto ethnics = new EthnicsDto();
                ethnics.setCode(ethnicsCode);
                candidate.setEthnics(ethnics);
                //16. Mã tôn giáo
                currentCell = currentRow.getCell(16);
                String religionCode = ExcelUtils.getCellValue(currentCell, String.class);
                ReligionDto religion = new ReligionDto();
                religion.setCode(religionCode);
                candidate.setReligion(religion);
                //17. Số điện thoại
                currentCell = currentRow.getCell(17);
                String phoneNumber = ExcelUtils.getCellValue(currentCell, String.class);
                candidate.setPhoneNumber(phoneNumber);
                //18. Email
                currentCell = currentRow.getCell(18);
                String email = ExcelUtils.getCellValue(currentCell, String.class);
                candidate.setEmail(email);
                //19. Ngày nộp hồ sơ
                currentCell = currentRow.getCell(19);
                Date submissionDate = ExcelUtils.getCellValue(currentCell, Date.class);
                candidate.setSubmissionDate(submissionDate);
                //20. Mã kế hoạch tuyển dụng
                currentCell = currentRow.getCell(20);
                String recruitmentPlanCode = ExcelUtils.getCellValue(currentCell, String.class);
                RecruitmentPlanDto recruitmentPlan = new RecruitmentPlanDto();
                recruitmentPlan.setCode(recruitmentPlanCode);
                candidate.setRecruitmentPlan(recruitmentPlan);
                //21. Mã đơn vị
                currentCell = currentRow.getCell(21);
                String organizationCode = ExcelUtils.getCellValue(currentCell, String.class);
                HrOrganizationDto organization = new HrOrganizationDto();
                organization.setCode(organizationCode);
                candidate.setOrganization(organization);
                //22. Mã phòng ban
                currentCell = currentRow.getCell(22);
                String departmentCode = ExcelUtils.getCellValue(currentCell, String.class);
                HRDepartmentDto department = new HRDepartmentDto();
                department.setCode(departmentCode);
                candidate.setDepartment(department);
                //23. Mã chức danh cần tuyển  *
                currentCell = currentRow.getCell(23);
                String positionTitleCode = ExcelUtils.getCellValue(currentCell, String.class);
                if (positionTitleCode == null) {
                    result.getErrors().add("Mã chức danh cần tuyển không được để trống, lỗi tại dòng " + (rowIndex));
                }
                PositionTitleDto positionTitle = new PositionTitleDto();
                positionTitle.setCode(positionTitleCode);
                candidate.setPositionTitle(positionTitle);
                //24. Mức lương kỳ vọng (VNĐ)
                currentCell = currentRow.getCell(24);
                Double desired = ExcelUtils.getCellValue(currentCell, Double.class);
                candidate.setDesiredPay(desired);
                //25. Ngày có thể bắt đầu làm việc
                currentCell = currentRow.getCell(25);
                Date possibleWorkingDate = ExcelUtils.getCellValue(currentCell, Date.class);
                candidate.setPossibleWorkingDate(possibleWorkingDate);
                //26. Mã người giới thiệu
                currentCell = currentRow.getCell(26);
                String referrerCode = ExcelUtils.getCellValue(currentCell, String.class);
                StaffDto staff = new StaffDto();
                staff.setStaffCode(referrerCode);
                candidate.setStaff(staff);

                result.getCandidates().add(candidate);
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    public static List<RecruitmentRequestDto> readRecruitmentRequestFromFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<RecruitmentRequestDto> recruitmentRequestList = new ArrayList<>();
            int lastRow = sheet.getLastRowNum();
            String errorMessage = null;

            for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue;

                RecruitmentRequestDto dto = new RecruitmentRequestDto();
                RecruitmentRequestItemDto recruitmentRequestItem = new RecruitmentRequestItemDto();
                // Cột 0 - Mã yêu cầu *
                String code = ExcelUtils.getCellValue(currentRow.getCell(0), String.class);
                if (!StringUtils.hasText(code)) {
                    errorMessage = "Mã yêu cầu không được để trống, lỗi tại dòng " + (rowIndex);
                    rowIndex = lastRow + 1;
                    break;

                }
                dto.setCode(code);

                // Cột 1 - Tên yêu cầu *
                String name = ExcelUtils.getCellValue(currentRow.getCell(1), String.class);
                if (!StringUtils.hasText(name)) {
                    errorMessage = "Tên yêu cầu không được để trống, lỗi tại dòng " + (rowIndex);
                    rowIndex = lastRow + 1;
                    break;
                }
                dto.setName(name);

                // Cột 2 - Mã đơn vị
                String orgCode = ExcelUtils.getCellValue(currentRow.getCell(2), String.class);
                if (StringUtils.hasText(orgCode)) {
                    HrOrganizationDto org = new HrOrganizationDto();
                    org.setCode(orgCode);
                    dto.setOrganization(org);
                }

                // Cột 3 - Mã phòng/cơ sở
                String divisionCode = ExcelUtils.getCellValue(currentRow.getCell(3), String.class);
                if (StringUtils.hasText(divisionCode)) {
                    HRDepartmentDto hrDepartment = new HRDepartmentDto();
                    hrDepartment.setCode(divisionCode);
                    dto.setHrDepartment(hrDepartment);
                }

                // Cột 4 - Mã bộ phận/nhóm
                String teamCode = ExcelUtils.getCellValue(currentRow.getCell(4), String.class);
                if (StringUtils.hasText(teamCode)) {
                    HRDepartmentDto team = new HRDepartmentDto();
                    team.setCode(teamCode);
                    dto.setTeam(team);
                }

                // Cột 5 - Mã chức danh
                String positionCode = ExcelUtils.getCellValue(currentRow.getCell(5), String.class);
                if (StringUtils.hasText(positionCode)) {
                    PositionTitleDto position = new PositionTitleDto();
                    position.setCode(positionCode);
                    recruitmentRequestItem.setPositionTitle(position);
                }

                // Cột 6 - Số lượng cần tuyển
                Integer quantity = ExcelUtils.getCellValue(currentRow.getCell(6), Integer.class);
                recruitmentRequestItem.setAnnouncementQuantity(quantity);

                // Cột 7 - Nơi làm việc
                dto.setWorkPlace(new WorkplaceDto() {{
                    setCode(ExcelUtils.getCellValue(currentRow.getCell(7), String.class));
                }});

                // Cột 8 - Hình thức làm việc
                String workTypeStr = ExcelUtils.getCellValue(currentRow.getCell(8), String.class);
                if (StringUtils.hasText(workTypeStr)) {
                    if (workTypeStr.equals("FULL_TIME")) {
                        recruitmentRequestItem.setWorkType(Const.WorkType.FULL_TIME);
                    } else if (workTypeStr.equals("PART_TIME")) {
                        recruitmentRequestItem.setWorkType(Const.WorkType.PART_TIME);
                    } else if (workTypeStr.equals("INTERN")) {
                        recruitmentRequestItem.setWorkType(Const.WorkType.INTERN);
                    } else {
                        errorMessage = "Hình thức làm việc không hợp lệ tại dòng " + (rowIndex) + ": " + workTypeStr;
                        break;
                    }
                }


                // Cột 9 - Trình độ chuyên môn
                recruitmentRequestItem.setProfessionalLevel(ExcelUtils.getCellValue(currentRow.getCell(9), String.class));

                // Cột 10 - Kỹ năng chuyên môn
                recruitmentRequestItem.setProfessionalSkills(ExcelUtils.getCellValue(currentRow.getCell(10), String.class));

                // Cột 11-12: Từ/Đến độ tuổi
                recruitmentRequestItem.setMinimumAge(ExcelUtils.getCellValue(currentRow.getCell(11), Integer.class));
                recruitmentRequestItem.setMaximumAge(ExcelUtils.getCellValue(currentRow.getCell(12), Integer.class));

                // Cột 13 - Giới tính
                String gender = ExcelUtils.getCellValue(currentRow.getCell(13), String.class);
                if (gender != null) {
                    if (gender.equals(HrConstants.Gender.MALE.getCode())
                            || gender.equals(HrConstants.Gender.FEMALE.getCode())
                            || gender.equals(HrConstants.Gender.OTHER.getCode())
                    ) {
                        recruitmentRequestItem.setGender(gender);
                    } else {
                        errorMessage = "Giới tính không hợp lệ, lỗi tại dòng " + (rowIndex);
                        rowIndex = lastRow + 1;
                        break;
                    }
                }

                // Cột 14-15: Chiều cao / Cân nặng
                recruitmentRequestItem.setHeight(ExcelUtils.getCellValue(currentRow.getCell(14), Double.class));
                recruitmentRequestItem.setWeight(ExcelUtils.getCellValue(currentRow.getCell(15), Double.class));

                // Cột 16 - Số năm kinh nghiệm
                recruitmentRequestItem.setYearOfExperience(ExcelUtils.getCellValue(currentRow.getCell(16), Integer.class));

                // Cột 17-18: Thu nhập đề xuất từ / đến
                recruitmentRequestItem.setMinimumIncome(ExcelUtils.getCellValue(currentRow.getCell(17), Double.class));
                recruitmentRequestItem.setMaximumIncome(ExcelUtils.getCellValue(currentRow.getCell(18), Double.class));

                // Cột 19 - Yêu cầu khác
                recruitmentRequestItem.setOtherRequirements(ExcelUtils.getCellValue(currentRow.getCell(19), String.class));

                // Cột 20 - Mô tả công việc
                recruitmentRequestItem.setDescription(ExcelUtils.getCellValue(currentRow.getCell(20), String.class));

                // Cột 21 - Yêu cầu
                recruitmentRequestItem.setRequest(ExcelUtils.getCellValue(currentRow.getCell(21), String.class));

                // Cột 22 - Tuyển thay thế, // Cột 23 - Tuyển mới
                // true = tuyển thay thế, false = tuyển mới

                String replacedPersonCode = ExcelUtils.getCellValue(currentRow.getCell(22), String.class);
                if (StringUtils.hasText(replacedPersonCode)) {
                    recruitmentRequestItem.setIsReplacementRecruitment(true);
                    StaffDto staff = new StaffDto();
                    staff.setStaffCode(replacedPersonCode);
                    recruitmentRequestItem.setIsWithinHeadcount(true);
                    recruitmentRequestItem.setReplacedPerson(staff);
                }

                Boolean recruitmentNew = ExcelUtils.getCellValue(currentRow.getCell(23), Boolean.class);

                if (recruitmentNew != null && recruitmentNew) {
                    recruitmentRequestItem.setIsReplacementRecruitment(false);
                    recruitmentRequestItem.setIsWithinHeadcount(true);
                }

                // Cột 24 - Lý do tuyển ngoài định biên
                String reson = ExcelUtils.getCellValue(currentRow.getCell(24), String.class);
                if (StringUtils.hasText(reson)) {
                    recruitmentRequestItem.setReason(reson);
                    recruitmentRequestItem.setIsWithinHeadcount(false);
                }
                if (dto.getRecruitmentRequestItems() == null) {
                    dto.setRecruitmentRequestItems(new HashSet<>());
                }
                dto.getRecruitmentRequestItems().add(recruitmentRequestItem);
                recruitmentRequestList.add(dto);
            }

            if (errorMessage != null) {
                RecruitmentRequestDto response = new RecruitmentRequestDto();
                response.setErrorMessage(errorMessage);
                return List.of(response);
            }

            return recruitmentRequestList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    public static List<RecruitmentRoundDto> readRecruitmentRoundFromFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<RecruitmentRoundDto> recruitmentRoundList = new ArrayList<>();
            int lastRow = sheet.getLastRowNum();
            String errorMessage = null;

            for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue;

                RecruitmentRoundDto dto = new RecruitmentRoundDto();
                RecruitmentPlanDto recruitmentPlan = new RecruitmentPlanDto();
                EvaluationTemplateDto evaluationTemplate = new EvaluationTemplateDto();
                ContentTemplateDto passTemplate = new ContentTemplateDto();
                ContentTemplateDto failTemplate = new ContentTemplateDto();
//                0.Mã kế hạch tuyển dụng *
                String recruitmentPlanCode = ExcelUtils.getCellValue(currentRow.getCell(0), String.class);
                if (!StringUtils.hasText(recruitmentPlanCode)) {
                    errorMessage = "Mã kế hạch tuyển dụng không được để trống, lỗi tại dòng " + (rowIndex);
                    rowIndex = lastRow + 1;
                    break;
                }
                recruitmentPlan.setCode(recruitmentPlanCode);
                dto.setRecruitmentPlan(recruitmentPlan);
//                1.Số thứ tự *
                Integer orderNumber = ExcelUtils.getCellValue(currentRow.getCell(1), Integer.class);
                if (orderNumber == null) {
                    errorMessage = "Số thứ tự vòng không được để trống, lỗi tại dòng " + (rowIndex);
                    rowIndex = lastRow + 1;
                    break;
                }
                dto.setRoundOrder(orderNumber);
//                2.Tên vòng *
                String name = ExcelUtils.getCellValue(currentRow.getCell(2), String.class);
                if (!StringUtils.hasText(name)) {
                    errorMessage = "Tên vòng tuyển dụng không được để trống, lỗi tại dòng " + (rowIndex);
                    rowIndex = lastRow + 1;
                    break;
                }
                dto.setName(name);
//                3.Ngày diễn ra *
                Date date = ExcelUtils.getCellValue(currentRow.getCell(3), Date.class);
                dto.setTakePlaceDate(date);
//                4.Địa điểm tổ chức
                String interviewLocation = ExcelUtils.getCellValue(currentRow.getCell(4), String.class);

                dto.setInterviewLocation(new WorkplaceDto(){{setCode(interviewLocation);}});
//                5.Hình thức
                Integer recruitmentType = ExcelUtils.getCellValue(currentRow.getCell(5), Integer.class);
                if (recruitmentType != null) {
                    if ((recruitmentType.equals(HrConstants.RecruitmentType.OFFLINE.getValue()) || recruitmentType.equals(HrConstants.RecruitmentType.ONLINE.getValue()))) {
                        dto.setRecruitmentType(recruitmentType);
                    } else {
                        errorMessage = "Giá trị hình thức vòng tuyển dụng không hợp lệ, lỗi tại dòng " + (rowIndex);
                        rowIndex = lastRow + 1;
                        break;
                    }
                }
//                6.Mã mẫu đánh giá ứng viên
                String evaluationTemplateCode = ExcelUtils.getCellValue(currentRow.getCell(6), String.class);
                evaluationTemplate.setCode(evaluationTemplateCode);
                dto.setEvaluationTemplate(evaluationTemplate);
//                7.Mã mẫu Email duyệt
                String passTemplateCode = ExcelUtils.getCellValue(currentRow.getCell(7), String.class);
                passTemplate.setCode(passTemplateCode);
                dto.setPassTemplate(passTemplate);
//                8.Mã mẫu Email từ chối
                String failTemplateCode = ExcelUtils.getCellValue(currentRow.getCell(8), String.class);
                failTemplate.setCode(failTemplateCode);
                dto.setFailTemplate(failTemplate);

                recruitmentRoundList.add(dto);
            }

            if (errorMessage != null) {
                RecruitmentRoundDto response = new RecruitmentRoundDto();
                response.setErrorMessage(errorMessage);
                return List.of(response);
            }

            return recruitmentRoundList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    public static List<LeaveRequestDto> readLeaveRequestFromFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<LeaveRequestDto> leaveRequestList = new ArrayList<>();
            int lastRow = sheet.getLastRowNum();
            String errorMessage = null;

            for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue;

                LeaveRequestDto dto = new LeaveRequestDto();
                StaffDto staff = new StaffDto();
                LeaveTypeDto leaveType = new LeaveTypeDto();
                ShiftWorkDto shiftWorkStart = new ShiftWorkDto();
                ShiftWorkDto shiftWorkEnd = new ShiftWorkDto();
                ShiftWorkTimePeriodDto timePeriodStart = new ShiftWorkTimePeriodDto();
                ShiftWorkTimePeriodDto timePeriodEnd = new ShiftWorkTimePeriodDto();
                //0. Mã nhân viên *
                String staffCode = ExcelUtils.getCellValue(currentRow.getCell(0), String.class);
                if (!StringUtils.hasText(staffCode)) {
                    errorMessage = "Mã nhân viên không được để trống, lỗi tại dòng " + (rowIndex);
                    rowIndex = lastRow + 1;
                    break;
                }
                staff.setStaffCode(staffCode);
                dto.setRequestStaff(staff);

                //1. Ngày yêu cầu
                Date requestDate = ExcelUtils.getCellValue(currentRow.getCell(1), Date.class);
                dto.setRequestDate(requestDate);
                //2. Thời điểm bắt đầu nghỉ *
                Date fromDate = ExcelUtils.getCellValue(currentRow.getCell(2), Date.class);
                dto.setFromDate(fromDate);
                //3. Thời điểm kết thúc *
                Date toDate = ExcelUtils.getCellValue(currentRow.getCell(3), Date.class);
                dto.setToDate(toDate);
                //4. Nghỉ nửa ngày
                String halfDayLeave = ExcelUtils.getCellValue(currentRow.getCell(4), String.class);
                if (halfDayLeave != null && ("true".equalsIgnoreCase(halfDayLeave) || "yes".equalsIgnoreCase(halfDayLeave))) {
                    dto.setHalfDayLeave(true);
                } else {
                    dto.setHalfDayLeave(false);
                }
                //5. Nghỉ nửa ngày đầu
                String halfDayLeaveStart = ExcelUtils.getCellValue(currentRow.getCell(5), String.class);
                if (halfDayLeaveStart != null && ("true".equalsIgnoreCase(halfDayLeaveStart) || "yes".equalsIgnoreCase(halfDayLeaveStart))) {
                    dto.setHalfDayLeaveStart(true);
                    dto.setHalfDayLeave(true);
                } else {
                    dto.setHalfDayLeaveStart(false);
                }
                //6. Mã ca làm việc
                String shiftWorkStartCode = ExcelUtils.getCellValue(currentRow.getCell(6), String.class);
                shiftWorkStart.setCode(shiftWorkStartCode);
                dto.setShiftWorkStart(shiftWorkStart);
                //7. Mã giai đoạn
                String timePeriodStartCode = ExcelUtils.getCellValue(currentRow.getCell(7), String.class);
                if (!StringUtils.hasText(shiftWorkStartCode) && StringUtils.hasText(timePeriodStartCode)) {
                    errorMessage = "Lỗi tại dòng " + (rowIndex) + ": Phải nhập Mã ca làm việc trước khi nhập Mã giai đoạn.";
                    rowIndex = lastRow + 1;
                    break;
                }
                timePeriodStart.setCode(timePeriodStartCode);
                dto.setTimePeriodStart(timePeriodStart);

                //8. Nghỉ nửa ngày cuối
                String halfDayLeaveEnd = ExcelUtils.getCellValue(currentRow.getCell(8), String.class);
                if (halfDayLeaveEnd != null && ("true".equalsIgnoreCase(halfDayLeaveEnd) || "yes".equalsIgnoreCase(halfDayLeaveEnd))) {
                    dto.setHalfDayLeaveEnd(true);
                    dto.setHalfDayLeave(true);
                } else {
                    dto.setHalfDayLeaveStart(false);
                }
                //9. Mã ca làm việc
                String shiftWorkEndCode = ExcelUtils.getCellValue(currentRow.getCell(9), String.class);
                shiftWorkEnd.setCode(shiftWorkEndCode);
                dto.setShiftWorkEnd(shiftWorkEnd);
                //10. Mã giai đoạn
                String timePeriodEndCode = ExcelUtils.getCellValue(currentRow.getCell(10), String.class);
                if (!StringUtils.hasText(shiftWorkStartCode) && StringUtils.hasText(timePeriodStartCode)) {
                    errorMessage = "Lỗi tại dòng " + (rowIndex) + ": Phải nhập Mã ca làm việc trước khi nhập Mã giai đoạn.";
                    rowIndex = lastRow + 1;
                    break;
                }
                timePeriodEnd.setCode(timePeriodEndCode);
                dto.setTimePeriodEnd(timePeriodEnd);

                //11. Mã loại nghỉ
                String leaveTypeCode = ExcelUtils.getCellValue(currentRow.getCell(11), String.class);
                leaveType.setCode(leaveTypeCode);
                dto.setLeaveType(leaveType);

                //12. Lý do nghỉ
                String requestReason = ExcelUtils.getCellValue(currentRow.getCell(12), String.class);
                dto.setRequestReason(requestReason);
                //13. Duyệt (True/False)
                String approvalStatus = ExcelUtils.getCellValue(currentRow.getCell(13), String.class);
                if (approvalStatus != null) {
                    if ("true".equalsIgnoreCase(approvalStatus) || "yes".equalsIgnoreCase(approvalStatus)) {
                        dto.setApprovalStatus(HrConstants.AbsenceRequestApprovalStatus.APPROVED.getValue());
                    } else if ("no".equalsIgnoreCase(approvalStatus) || "false".equalsIgnoreCase(approvalStatus))
                        dto.setApprovalStatus(HrConstants.AbsenceRequestApprovalStatus.NOT_APPROVED.getValue());
                    else {
                        errorMessage = "Giá trị duyệt không hợp lệ, lỗi tại dòng " + (rowIndex);
                        rowIndex = lastRow + 1;
                        break;
                    }
                } else {
                    dto.setApprovalStatus(HrConstants.AbsenceRequestApprovalStatus.NOT_APPROVED_YET.getValue());
                }
                leaveRequestList.add(dto);
            }

            if (errorMessage != null) {
                LeaveRequestDto response = new LeaveRequestDto();
                response.setErrorMessage(errorMessage);
                return List.of(response);
            }

            return leaveRequestList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    public static List<SalaryItemDto> readSalaryItemFromFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<SalaryItemDto> salaryItemList = new ArrayList<>();
            int lastRow = sheet.getLastRowNum();

            for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue;

                SalaryItemDto dto = new SalaryItemDto();
                //0. STT Nếu không có số thứ tự thì sẽ không import dòng đó
                Integer orderNumber = ExcelUtils.getCellValue(currentRow.getCell(0), Integer.class);
                if (orderNumber == null) {
                    continue;
                }
                // 1. Tên thành phần (*)
                String name = ExcelUtils.getCellValue(currentRow.getCell(1), String.class);
                if (!StringUtils.hasText(name)) {
                    SalaryItemDto response = new SalaryItemDto();
                    response.setErrorMessage("Dòng " + (rowIndex) + ": Tên thành phần không được để trống");
                    return List.of(response);
                }
                dto.setName(name);

                // 2. Mã thành phần (*)
                String code = ExcelUtils.getCellValue(currentRow.getCell(2), String.class);
                if (!StringUtils.hasText(code)) {
                    SalaryItemDto response = new SalaryItemDto();
                    response.setErrorMessage("Dòng " + (rowIndex) + ": Mã thành phần không được để trống");
                    return List.of(response);
                }
                dto.setCode(code);

                // 3. Tính chất thành phần
                Integer type = ExcelUtils.getCellValue(currentRow.getCell(3), Integer.class);
                if (type != null) {
                    if (type.equals(HrConstants.SalaryItemType.ADDITION.getValue())
                            || type.equals(HrConstants.SalaryItemType.DEDUCTION.getValue())
                            || type.equals(HrConstants.SalaryItemType.INFORMATION.getValue())
                            || type.equals(HrConstants.SalaryItemType.OTHERS.getValue())
                    ) {
                        dto.setType(type);
                    } else {
                        SalaryItemDto response = new SalaryItemDto();
                        response.setErrorMessage("Dòng " + (rowIndex) + ": Tính chất thành phần không hợp lệ");
                        return List.of(response);
                    }
                }

                // 4. Cách tính giá trị
                Integer calculationType = ExcelUtils.getCellValue(currentRow.getCell(4), Integer.class);
                if (calculationType != null) {
                    if (calculationType.equals(HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue())
                            || calculationType.equals(HrConstants.SalaryItemCalculationType.USER_FILL.getValue())
                            || calculationType.equals(HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue())
                            || calculationType.equals(HrConstants.SalaryItemCalculationType.THRESHOLD.getValue())
                            || calculationType.equals(HrConstants.SalaryItemCalculationType.FIX.getValue())) {

                        dto.setCalculationType(calculationType);
                    } else {
                        SalaryItemDto response = new SalaryItemDto();
                        response.setErrorMessage("Dòng " + (rowIndex) + ": Cách tính giá trị không hợp lệ");
                        return List.of(response);
                    }
                }

                // 5. Kiểu giá trị
                Integer valueType = ExcelUtils.getCellValue(currentRow.getCell(5), Integer.class);
                if (valueType != null) {
                    if (valueType.equals(HrConstants.SalaryItemValueType.TEXT.getValue())
                            || valueType.equals(HrConstants.SalaryItemValueType.MONEY.getValue())
                            || valueType.equals(HrConstants.SalaryItemValueType.NUMBER.getValue())
                            || valueType.equals(HrConstants.SalaryItemValueType.PERCENT.getValue())
                            || valueType.equals(HrConstants.SalaryItemValueType.OTHERS.getValue())) {

                        dto.setValueType(valueType);
                    } else {
                        SalaryItemDto response = new SalaryItemDto();
                        response.setErrorMessage("Dòng " + (rowIndex) + ": Kiểu giá trị không hợp lệ");
                        return List.of(response);
                    }
                }

                // 6. Giá trị mặc định
                dto.setDefaultValue(ExcelUtils.getCellValue(currentRow.getCell(6), String.class));

                // 7. Mô tả
                dto.setDescription(ExcelUtils.getCellValue(currentRow.getCell(7), String.class));
                salaryItemList.add(dto);
            }

            return salaryItemList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    public static List<AssetDto> readAssetDtoFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<AssetDto> assetList = new ArrayList<>();
            int lastRow = sheet.getLastRowNum();

            for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) continue;

                AssetDto dto = new AssetDto();
                ProductDto product = new ProductDto();
                StaffDto staff = new StaffDto();
                // Cột 0: Mã công cụ, dụng cụ (*)
                String productCode = ExcelUtils.getCellValue(currentRow.getCell(0), String.class);
                if (!StringUtils.hasText(productCode)) {
                    dto.setErrorMessage("Dòng " + (rowIndex) + ": Mã công cụ, dụng cụ không được để trống");
                    return List.of(dto);
                }
                product.setCode(productCode);
                dto.setProduct(product);
                // Cột 1: Mã nhân viên (*)
                String staffCode = ExcelUtils.getCellValue(currentRow.getCell(1), String.class);
                if (!StringUtils.hasText(staffCode)) {
                    dto.setErrorMessage("Dòng " + (rowIndex) + ": Mã nhân viên không được để trống");
                    return List.of(dto);
                }
                staff.setStaffCode(staffCode);
                dto.setStaff(staff);

                // Cột 2: Ngày bắt đầu (MM/dd/yyyy)
                Date startDate = ExcelUtils.getCellValue(currentRow.getCell(2), Date.class);
                dto.setStartDate(startDate);
                // Cột 3: Ngày kết thúc (MM/dd/yyyy)
                Date endDate = ExcelUtils.getCellValue(currentRow.getCell(3), Date.class);
                dto.setEndDate(endDate);

                // Cột 4: Ghi chú (tùy chọn)
                String note = ExcelUtils.getCellValue(currentRow.getCell(4), String.class);
                dto.setNote(note);

                assetList.add(dto);
            }

            return assetList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }
}
