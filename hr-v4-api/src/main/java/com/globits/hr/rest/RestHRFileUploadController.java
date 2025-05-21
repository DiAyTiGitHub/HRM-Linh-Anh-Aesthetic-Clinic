package com.globits.hr.rest;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.tika.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import com.globits.core.Constants;
import com.globits.core.domain.Department;
import com.globits.core.domain.FileDescription;
import com.globits.core.dto.CountryDto;
import com.globits.core.dto.DepartmentDto;
import com.globits.core.dto.FileDescriptionDto;
import com.globits.core.repository.DepartmentRepository;
import com.globits.core.repository.ProfessionRepository;
import com.globits.core.service.CountryService;
import com.globits.core.service.DepartmentService;
import com.globits.core.service.EthnicsService;
import com.globits.core.service.FileDescriptionService;
import com.globits.core.service.ProfessionService;
import com.globits.core.service.ReligionService;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffSalaryHistory;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.HrSpecialityDto;
import com.globits.hr.dto.PersonCertificateDto;
import com.globits.hr.dto.PositionTitleDto;
//import com.globits.hr.dto.StaffInsuranceHistoryDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.StaffEducationHistoryDto;
import com.globits.hr.dto.StaffLabourAgreementDto;
import com.globits.hr.dto.StaffSalaryHistoryDto;
import com.globits.hr.dto.function.ImportStaffDto;
import com.globits.hr.dto.function.PositionTitleStaffDto;
import com.globits.hr.dto.function.StaffFamilyRelationshipFunctionDto;
import com.globits.hr.repository.CivilServantCategoryRepository;
import com.globits.hr.repository.CivilServantTypeRepository;
import com.globits.hr.repository.EmployeeStatusRepository;
import com.globits.hr.repository.HRDepartmentRepository;
import com.globits.hr.repository.LabourAgreementTypeRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.StaffSalaryHistoryRepository;
import com.globits.hr.service.HRDepartmentService;
import com.globits.hr.service.HrSpecialityService;
import com.globits.hr.service.PersonCertificateService;
import com.globits.hr.service.PositionStaffService;
import com.globits.hr.service.PositionTitleService;
//import com.globits.hr.service.StaffInsuranceHistoryService;
import com.globits.hr.service.StaffEducationHistoryService;
import com.globits.hr.service.StaffFamilyRelationshipService;
import com.globits.hr.service.StaffLabourAgreementService;
import com.globits.hr.service.StaffService;
import com.globits.hr.utils.ImportExportExcelUtil;
import com.globits.salary.domain.SalaryIncrementType;
import com.globits.salary.repository.SalaryIncrementTypeRepository;
import com.globits.security.domain.User;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api/hr/file")
public class RestHRFileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(RestHRFileUploadController.class);

    @Autowired
    private Environment env;
    @Autowired
    FileDescriptionService fileDescriptionService;
    @Autowired
    StaffService staffService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    CountryService countryService;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    EthnicsService ethnicsService;
    @Autowired
    ReligionService religionService;
    @Autowired
    EmployeeStatusRepository employeeStatusRepository;
    @Autowired
    HRDepartmentRepository hRDepartmentRepository;
    @Autowired
    CivilServantTypeRepository civilServantTypeRepository;
    @Autowired
    CivilServantCategoryRepository civilServantCategoryRepository;
    @Autowired
    LabourAgreementTypeRepository labourAgreementTypeRepository;
    @Autowired
    ProfessionRepository professionRepository;
    @Autowired
    ProfessionService professionService;
    @Autowired
    StaffFamilyRelationshipService staffFamilyRelationshipService;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private SalaryIncrementTypeRepository salaryIncrementTypeRepository;
    @Autowired
    private StaffSalaryHistoryRepository staffSalaryHistoryRepository;
    @Autowired
    private HrSpecialityService hrSpecialityService;
    //    @Autowired
//    private StaffInsuranceHistoryService stafInsuranceHistoryService;
    @Autowired
    private StaffEducationHistoryService staffEducationHistoryService;
    @Autowired
    private PositionTitleService positionTitleService;
    @Autowired
    private PositionStaffService positionStaffService;
    @Autowired
    HRDepartmentService hRDepartmentService;
    @Autowired
    private StaffLabourAgreementService staffLabourAgreementService;
    @Autowired
    PersonCertificateService personCertificateService;

    /**
     * POST /uploadFile -> receive and locally save a file.
     *
     * @param uploadfile The uploaded file as Multipart file parameter in the HTTP request.
     *                   The RequestParam name must be the same of the attribute "name" in
     *                   the input tag with type file.
     * @return An http OK status in case of success, an http 4xx status in case of errors.
     */
    @RequestMapping(value = "/image", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<FileDescriptionDto> uploadAttachment(@RequestParam("uploadfile") MultipartFile uploadfile) {
        FileDescriptionDto result;
        String path = "";
        if (env.getProperty("hrm.file.folder") != null) {
            path = env.getProperty("hrm.file.folder");
        }
        try {
            String extension = uploadfile.getOriginalFilename().split("\\.(?=[^\\.]+$)")[1];
            UUID randomCode = UUID.randomUUID();
            String filename = randomCode + "." + extension;
            try {
                File fileToBeSaved = new File(path, filename);
                uploadfile.transferTo(fileToBeSaved);
            } catch (Exception e) {
                logger.error("Error upload attachment file description: {}", e.getMessage(), e);
            }

            FileDescription file = new FileDescription();
            file.setContentSize(uploadfile.getSize());
            file.setContentType(uploadfile.getContentType());
            file.setName(filename);
            file.setFilePath(path);
            file = fileDescriptionService.save(file);

            result = new FileDescriptionDto(file);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(path = "/getImage/{filename}/{type}", method = RequestMethod.GET)
    public void getImage(HttpServletResponse response, @PathVariable String filename, @PathVariable String type) throws IOException {
        String path = "";
        if (env.getProperty("hrm.file.folder") != null) {
            path = env.getProperty("hrm.file.folder");
        }
        File file = new File(path + filename + "." + type);
        String contentType = "application/octet-stream";
        response.setContentType(contentType);
        OutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(file);
        IOUtils.copy(in, out);
        out.close();
        in.close();
    }

    @RequestMapping(path = "/image/{filename:.+}", method = RequestMethod.GET)
    public void getImageByName(HttpServletResponse response, @PathVariable(value = "filename") String filename) throws IOException {
        String path = "";
        if (env.getProperty("hrm.file.folder") != null) {
            path = env.getProperty("hrm.file.folder");
        }
        File file = new File(path + filename);
        if (file.exists()) {
            String contentType = "application/octet-stream";
            response.setContentType(contentType);
            OutputStream out = response.getOutputStream();
            FileInputStream in = new FileInputStream(file);
            IOUtils.copy(in, out);
            out.close();
            in.close();
        } else {
            throw new FileNotFoundException();
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/fileupload", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> uploadFile(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            String filename = uploadfile.getOriginalFilename();
            String directory = "C:\\temp";
            String filepath = Paths.get(directory, filename).toString();
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            stream.write(uploadfile.getBytes());
            stream.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/importStaff", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importStaffFile(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<StaffDto> list = ImportExportExcelUtil.getListStaffFromInputStream(bis);
            staffService.saveListStaff(list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/importDepartment", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importDepartmentFile(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<DepartmentDto> list = ImportExportExcelUtil.getListDepartmentFromInputStream(bis);
            if (list != null) {
                logger.info("Size of list Department: {}", list.size());
                for (DepartmentDto department : list) {
                    department = createDepartmentFromDto(department);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public DepartmentDto createDepartmentFromDto(DepartmentDto departmentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User modifiedUser = null;
        LocalDateTime currentDate = LocalDateTime.now();
        String currentUserName = "Unknown User";
        if (authentication != null) {
            modifiedUser = (User) authentication.getPrincipal();
            currentUserName = modifiedUser.getUsername();
        }
        Department department = null;
        if (departmentDto.getId() != null) {
            department = departmentRepository.getOne(departmentDto.getId());
        }
        if (department == null && departmentDto.getCode() != null) {
            department = findByCode(departmentDto.getCode());
        }
        if (department == null) {
            department = new Department();
            department.setCreateDate(currentDate);
            if (modifiedUser != null) {
                department.setCreatedBy(modifiedUser.getUsername());
            } else {
                department.setCreatedBy(currentUserName);
            }
        }
        if (departmentDto.getName() != null) {
            department.setName(departmentDto.getName());
        }
        if (departmentDto.getCode() != null) {
            department.setCode(departmentDto.getCode());
        }
        department = departmentRepository.save(department);
        return new DepartmentDto(department);
    }

    @Query("select u from Department u where u.code = ?1")
    public Department findByCode(String code) {
        return new Department();
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/fileuploaddep", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> uploadFileDep(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            String filename = uploadfile.getOriginalFilename();
            String directory = "C:\\temp";
            String filepath = Paths.get(directory, filename).toString();
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(filepath));
            stream.write(uploadfile.getBytes());
            stream.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/exportDepartment", method = {RequestMethod.POST})
    public void exportDepartment(WebRequest request, HttpServletResponse response,
                                 @RequestBody List<DepartmentDto> dtos) {
        List<DepartmentDto> list = new ArrayList<>();
        if (dtos != null && dtos.size() > 0) {
            Department department = null;
            for (DepartmentDto dto : dtos) {
                if (dto.getId() != null) {
                    department = departmentRepository.getOne(dto.getId());
                }
                if (dto.getCode() != null && department != null) {
                    department.setCode(dto.getCode());
                }
                if (dto.getName() != null && department != null) {
                    department.setName(dto.getName());
                }
                list.add(dto);
            }
        }

        String title = "Phòng Ban ";
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet worksheet = workbook.createSheet(title);

            HSSFCellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
            headerCellStyle.setFillBackgroundColor(HSSFColor.GREY_40_PERCENT.index);

            HSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setColor(HSSFFont.COLOR_NORMAL);
            font.setFontName("");

            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setFont(font);
            headerCellStyle.setWrapText(false);

            int rowCount = 1;
            int columnCount = 0;
            HSSFRow row;
            HSSFCell cell;

            row = worksheet.createRow(rowCount);
            cell = row.createCell(columnCount++);
            cell.setCellStyle(headerCellStyle);
            cell.setCellValue("STT");
            worksheet.setColumnWidth(columnCount - 1, 6 * 256);

            cell = row.createCell(columnCount++);
            cell.setCellStyle(headerCellStyle);
            cell.setCellValue("Mã Phòng Ban");
            worksheet.setColumnWidth(columnCount - 1, 20 * 256);

            cell = row.createCell(columnCount++);
            cell.setCellStyle(headerCellStyle);
            cell.setCellValue("Tên Phòng Ban");

            worksheet.setColumnWidth(columnCount - 1, 50 * 256);
            int titleRow = 0;
            row = worksheet.createRow(titleRow++);

            HSSFCellStyle style0 = workbook.createCellStyle();
            HSSFFont font0 = workbook.createFont();
            font0.setFontHeightInPoints((short) 13);
            style0.setFont(font0);

            cell = row.createCell(0);
            cell.setCellStyle(style0);
            cell.setCellValue(title.toUpperCase());
            worksheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

            HSSFCellStyle dateCellStyle = workbook.createCellStyle();
            short df = workbook.createDataFormat().getFormat("dd//MM/yyyy");
            dateCellStyle.setDataFormat(df);
            HSSFCellStyle normalCellStyle = workbook.createCellStyle();
            if (list.size() > 0) {
                for (DepartmentDto s : list) {
                    rowCount++;
                    columnCount = 0;
                    row = worksheet.createRow(rowCount);

                    cell = row.createCell(columnCount++);
                    cell.setCellStyle(normalCellStyle);
                    cell.setCellValue(rowCount - titleRow);

                    cell = row.createCell(columnCount++);
                    cell.setCellStyle(normalCellStyle);
                    if (s.getCode() != null)
                        cell.setCellValue(s.getCode());

                    cell = row.createCell(columnCount++);
                    cell.setCellStyle(normalCellStyle);
                    if (s.getName() != null)
                        cell.setCellValue(s.getName());
                }
            }
            workbook.write(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=BaoCaoHangThang.xls");
            response.flushBuffer();
        } catch (IOException e) {
            logger.error("Error exportDepartment: {}", e.getMessage(), e);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/importExcelStaff", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importStaff(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            ImportStaffDto dto = ImportExportExcelUtil.importStaffFromInputStream(bis);
            if (dto.getListMessage() != null && dto.getListMessage().size() > 0) {
                return new ResponseEntity<>(dto.getListMessage(), HttpStatus.OK);
            }
            if (dto.getListStaff() != null && dto.getListStaff().size() > 0) {
                List<StaffDto> list = dto.getListStaff();
                staffService.saveImportStaff(list);
                return new ResponseEntity<>("successful", HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/importPositionTitleStaff", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importPositionTitleStaff(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<PositionTitleStaffDto> list = ImportExportExcelUtil.importPositionProcessFromInputStream(bis);
            for (PositionTitleStaffDto staffDto : list) {

                staffService.savePositionStaff(staffDto);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/importStaffSalaryHistory", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importStaffSalaryHistory(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<StaffSalaryHistoryDto> list = ImportExportExcelUtil.importStaffSalaryHistoryFromInputStream(bis);
            for (StaffSalaryHistoryDto staffSalaryHistoryDto : list) {
                StaffSalaryHistory entity = new StaffSalaryHistory();
                Staff staff = null;
                if (staffSalaryHistoryDto.getStaffCode() != null && StringUtils.hasText(staffSalaryHistoryDto.getStaffCode())) {
                    List<Staff> listStaff = staffRepository.findByCode(staffSalaryHistoryDto.getStaffCode());
                    if (listStaff != null && listStaff.size() > 0) {
                        staff = listStaff.get(0);
                    }
                    if (staff != null) {
                        entity.setStaff(staff);
                    }
                }

                if (staffSalaryHistoryDto.getSalaryIncrementTypeCode() != null && StringUtils.hasText(staffSalaryHistoryDto.getSalaryIncrementTypeCode())) {
                    List<SalaryIncrementType> salaryIncrementTypes = salaryIncrementTypeRepository.findByCode(staffSalaryHistoryDto.getSalaryIncrementTypeCode());
                    if (salaryIncrementTypes != null && salaryIncrementTypes.size() > 0) {
                        entity.setSalaryIncrementType(salaryIncrementTypes.get(0));
                    }
                }

                entity.setCoefficient(staffSalaryHistoryDto.getCoefficient());
                entity.setStaffTypeCode(staffSalaryHistoryDto.getStaffTypeCode());
                entity.setCoefficientOverLevel(staffSalaryHistoryDto.getCoefficientOverLevel());
                entity.setPercentage(staffSalaryHistoryDto.getPercentage());
                entity.setDecisionDate(staffSalaryHistoryDto.getDecisionDate());
                entity.setDecisionCode(staffSalaryHistoryDto.getDecisionCode());
                entity.setStartDate(staffSalaryHistoryDto.getStartDate());
                entity.setNextSalaryIncrementDate(staffSalaryHistoryDto.getNextSalaryIncrementDate());
                entity.setNote(staffSalaryHistoryDto.getNote());
                entity.setStartStaffTypeCodeDate(staffSalaryHistoryDto.getStartStaffTypeCodeDate());
                if (entity.getStaff() != null && entity.getStaff().getId() != null) {
                    staffSalaryHistoryRepository.save(entity);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/importCountry", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importCountry(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<CountryDto> list = ImportExportExcelUtil.importCountryFromInputStream(bis);
            for (CountryDto dto : list) {

                countryService.saveCountry(dto);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/importSpeciality", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importSpeciality(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<HrSpecialityDto> list = ImportExportExcelUtil.importHrSpecialityFromInputStream(bis);
            for (HrSpecialityDto dto : list) {

                hrSpecialityService.saveSpeciality(dto, null);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
//    @RequestMapping(value = "/importStaffInsuranceHistory", method = RequestMethod.POST)
//    @ResponseBody
//    public ResponseEntity<?> importStaffInsuranceHistory(@RequestParam("uploadfile") MultipartFile uploadfile) {
//        try {
//            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
//            List<StaffInsuranceHistoryDto> list = ImportExportExcelUtil.importExcelStaffInsuranceHistoryDto(bis);
//            for (StaffInsuranceHistoryDto dto : list) {
//
//                stafInsuranceHistoryService.saveStaffInsuranceHistory(dto, null);
//            }
//
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/importStaffEducationHistory", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importStaffEducationHistory(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<StaffEducationHistoryDto> list = ImportExportExcelUtil.importStaffEducationHistoryFromInputStream(bis);
            for (StaffEducationHistoryDto dto : list) {

                staffEducationHistoryService.saveImportStaffEducationHistory(dto);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/importPositionTitle", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importPositionTitle(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<PositionTitleDto> list = ImportExportExcelUtil.importPositionTitleFromInputStream(bis);
            for (PositionTitleDto dto : list) {
                positionTitleService.saveTitle(dto);
            }
        } catch (Exception e) {
            logger.error("Error importPositionTitle: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/importPositionStaff", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importPositionStaff(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<PositionTitleStaffDto> list = ImportExportExcelUtil.importPositionProcessFromInputStream(bis);
            for (PositionTitleStaffDto dto : list) {
                positionStaffService.saveImportStaffEducationHistory(dto);
            }
        } catch (Exception e) {
            logger.error("Error importPositionStaff: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/importExcelDepartment", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importExcelDepartment(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<HRDepartmentDto> list = ImportExportExcelUtil.importDepartmentFromInputStream(bis);
            for (HRDepartmentDto dto : list) {
                HRDepartmentDto departmentDto = hRDepartmentService.saveOrUpdate(dto);
            }
        } catch (Exception e) {
            logger.error("Error importExcelDepartment: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/importExcelStaffLabourAgreement", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importExcelStaffLabourAgreement(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<StaffLabourAgreementDto> list = ImportExportExcelUtil.importStaffLabourAgreementFromInputStream(bis);
            for (StaffLabourAgreementDto dto : list) {
                StaffLabourAgreementDto ds = staffLabourAgreementService.saveAgreement(dto, null);
            }
        } catch (Exception e) {
            logger.error("Error importExcelStaffLabourAgreement: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/importExcelPersonCertificate", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importPersonCertificateFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<PersonCertificateDto> list = ImportExportExcelUtil.importPersonCertificateFromInputStream(bis);
            for (PersonCertificateDto dto : list) {
                PersonCertificateDto ds = personCertificateService.saveImportStaffEducationHistory(dto);
            }

        } catch (Exception e) {
            logger.error("Error importPersonCertificateFromInputStream: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/image/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<StaffDto> uploadAttachment(@RequestParam("uploadfile") MultipartFile uploadfile, @PathVariable UUID id) {
        if (id != null) {
            String path = "";
            if (env.getProperty("hrm.file.folder") != null) {
                path = env.getProperty("hrm.file.folder");
            }
            try {
                String extension = uploadfile.getOriginalFilename().split("\\.(?=[^\\.]+$)")[1];
                UUID randomCode = UUID.randomUUID();
                String filename = randomCode + "." + extension;
                try {
                    File fileToBeSaved = new File(path, filename);
                    uploadfile.transferTo(fileToBeSaved);
                } catch (Exception e) {
                    logger.error("Error: {}", e.getMessage(), e);
                }

                FileDescription file = new FileDescription();
                file.setContentSize(uploadfile.getSize());
                file.setContentType(uploadfile.getContentType());
                file.setName(filename);
                file.setFilePath(path);
                Staff entity = null;
                Optional<Staff> optional = staffRepository.findById(id);
                if (optional.isPresent()) {
                    entity = optional.get();
                }
                if (entity != null) {
                    file = fileDescriptionService.save(file);
                    entity.setImagePath(file.getName());
                    entity = staffRepository.save(entity);
                    return new ResponseEntity<>(new StaffDto(entity), HttpStatus.OK);
                }
            } catch (Exception e) {
                logger.error("Error uploadAttachment: {}", e.getMessage(), e);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
