package com.globits.hr.service.impl;

import com.globits.core.domain.Organization;
import com.globits.core.domain.OrganizationUser;
import com.globits.core.dto.CountryDto;
import com.globits.core.dto.EthnicsDto;
import com.globits.core.dto.PersonDto;
import com.globits.core.dto.ProfessionDto;
import com.globits.core.dto.ReligionDto;
import com.globits.core.repository.OrganizationRepository;
import com.globits.core.repository.OrganizationUserRepository;
import com.globits.core.utils.CommonUtils;
import com.globits.core.utils.SecurityUtils;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.HrEducationType;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffLabourAgreement;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.SystemConfigRepository;
import com.globits.hr.rest.RestHrEthnicsController;
import com.globits.hr.service.*;
import com.globits.salary.domain.*;
import com.globits.salary.dto.SalaryAutoMapDto;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.SalaryTemplateDto;
import com.globits.salary.repository.*;
import com.globits.salary.service.SalaryAutoMapService;
import com.globits.salary.service.SalaryItemService;
import com.globits.salary.service.SalaryTemplateService;
import com.globits.security.domain.Role;
import com.globits.security.domain.User;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.security.repository.UserRepository;
import com.globits.security.service.RoleService;
import com.globits.security.service.UserService;
import com.globits.task.domain.HrTask;
import com.globits.task.repository.HrTaskRepository;
import com.globits.task.service.HrTaskService;
import com.globits.timesheet.domain.Project;
import com.globits.timesheet.dto.LeaveTypeDto;
import com.globits.timesheet.repository.ProjectRepository;

import com.globits.timesheet.service.LeaveTypeService;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.format.CellFormatType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SetupDataServiceImpl implements SetupDataService {
    private static final Logger logger = LoggerFactory.getLogger(SetupDataServiceImpl.class);

    private static boolean eventFired = false;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment env;

    @Autowired
    private StaffService staffService;
    @Autowired
    private StaffRepository staffRepository;

    // @Autowired
//	private ResourceDefService resDefService;

    @Autowired
    private HrTaskRepository hrTaskRepository;
    @Autowired
    private HrTaskService hrTaskService;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CivilServantTypeService civilServantTypeService;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationUserService organizationUserService;

    @Autowired
    private OrganizationExtService organizationExtService;

    @Autowired
    private RecruitmentExamTypeService recruitmentExamTypeService;

    @Autowired
    private FamilyRelationshipService familyRelationshipService;

    @Autowired
    private DisciplineReasonService disciplineReasonService;

    @Autowired
    private RefusalReasonService refusalReasonService;

    @Autowired
    private DeferredTypeService deferredTypeService;

    @Autowired
    private TransferTypeService transferTypeService;

    @Autowired
    private RankTitleService rankTitleService;

    @Autowired
    private PositionTitleService positionTitleService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private DepartmentGroupService departmentGroupService;

    @Autowired
    private DepartmentTypeService departmentTypeService;

    @Autowired
    private HRDepartmentService departmentService;

    @Autowired
    private ContractTypeService contractTypeService;

    @Autowired
    private AddendumTypeService addendumTypeService;

    @Autowired
    private StaffTypeService staffTypeService;

    @Autowired
    private PositionRoleService positionRoleService;

    @Autowired
    private EmployeeStatusService employeeStatusService;

    @Autowired
    private RewardFormService rewardFormService;

    @Autowired
    private OrganizationUserRepository organizationUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SalaryAreaRepository salaryAreaRepository;

    @Autowired
    private SalaryUnitRepository salaryUnitRepository;

    @Autowired
    private SalaryTypeRepository salaryTypeRepository;

    @Autowired
    private SalaryConfigRepository salaryConfigRepository;

    @Autowired
    private HrSpecialityService hrSpecialityService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private HrCountryService hrCountryService;

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private HrEthinicsService hrEthinicsService;

    @Autowired
    private HrReligionService hrReligionService;

    @Autowired
    private EducationalInstitutionService educationalInstitutionService;

    @Autowired
    private HrEducationTypeService hrEducationTypeService;

    @Autowired
    private EducationDegreeService educationDegreeService;

    @Autowired
    private SalaryItemService salaryItemService;

    @Autowired
    private ShiftWorkService shiftWorkService;

    @Autowired
    private HrProfessionService hrProfessionService;

    @Autowired
    private SalaryTemplateService salaryTemplateService;

    @Autowired
    private AllowanceTypeService allowanceTypeService;

    @Autowired
    private SalaryAutoMapService salaryAutoMapService;
    @Autowired
    private LeaveTypeService leaveTypeService;

    @Override
    public void setupRoles() {
        try {
            createRoles();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void createRoles() throws XMLStreamException {
        List<String> roleNames = new ArrayList<>();

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        InputStream in = getClass().getClassLoader().getResourceAsStream("sys-roles.xml");
        XMLStreamReader streamReader = inputFactory.createXMLStreamReader(in, "UTF-8");

        streamReader.nextTag();
        streamReader.nextTag();

        while (streamReader.hasNext()) {
            if (streamReader.isStartElement()) {
                switch (streamReader.getLocalName()) {
                    case "name": {
                        roleNames.add(streamReader.getElementText());
                        break;
                    }
                }
            }
            streamReader.next();
        }

        streamReader.close();

        for (String roleName : roleNames) {
            createRoleIfNotExist(roleName);
        }
    }

    private void createRoleIfNotExist(String roleName) {

        if (CommonUtils.isEmpty(roleName)) {
            return;
        }

        Role role = roleService.findByName(roleName);

        if (CommonUtils.isNotNull(role)) {
            return;
        }

        if (role == null) {
            role = new Role();
            role.setName(roleName);
        }

        try {
            roleService.save(role);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setupAdminUser() {
        UserDto userDto = userService.findByUsername(com.globits.core.Constants.USER_ADMIN_USERNAME);

        if (CommonUtils.isNotNull(userDto)) {
            return;
        }

        userDto = new UserDto();
        userDto.setUsername(com.globits.core.Constants.USER_ADMIN_USERNAME);
        userDto.setPassword(SecurityUtils.getHashPassword("Admin2024@123"));
        userDto.setEmail("admin@globits.net");
        userDto.setActive(true);
        userDto.setDisplayName("Admin User");

        Role role = roleService.findByName(com.globits.core.Constants.ROLE_ADMIN);

        userDto.getRoles().addAll(Arrays.asList(new RoleDto(role)));

        PersonDto person = new PersonDto();
        person.setGender("M");
        person.setFirstName("Admin");
        person.setLastName("User");
        person.setDisplayName("Admin User");

        userDto.setPerson(person);

        try {
            userService.save(userDto);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setupStaffUser() {
        String userName = "lamnt21398";
        String password = "123456";
        String email = "lamnt21398@gmail.com";
        String name = "Nguyen Thanh Lam";
        UserDto userDto = userService.findByUsername(userName);

        if (CommonUtils.isNotNull(userDto)) {
            return;
        }

        userDto = new UserDto();
        userDto.setUsername(userName);
        userDto.setPassword(SecurityUtils.getHashPassword(password));
        userDto.setEmail(email);
        userDto.setActive(true);
        userDto.setDisplayName(name);

        Role role = roleService.findByName(com.globits.core.Constants.ROLE_ADMIN);

        userDto.getRoles().addAll(Arrays.asList(new RoleDto(role)));

        PersonDto person = new PersonDto();
        person.setGender("M");
        person.setFirstName("Admin");
        person.setLastName("User");
        person.setDisplayName("Admin User");

        userDto.setPerson(person);

        try {
            userService.save(userDto);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setupCivilServantType() {
//create civil servant type: Applicant (Ung vien)
        // define that PL04 IS APPLICANT
//        CivilServantTypeDto applicantType = civilServantTypeService.findByCode("PL04");
//        if (applicantType == null || applicantType.getId() == null) {
//            applicantType = new CivilServantTypeDto();
//            applicantType.setName("Ứng viên");
//            applicantType.setLanguageKey("Tiếng Việt");
//            applicantType.setCode("PL04");
//
//            applicantType = civilServantTypeService.update(applicantType, null);
//        }

        List<CivilServantTypeDto> defaultDtos = Arrays.asList(
                new CivilServantTypeDto("CSA", "Công chức loại A", "Tiếng Việt"),
                new CivilServantTypeDto("CSB", "Công chức loại B", "Tiếng Việt"),
                new CivilServantTypeDto("CSC", "Công chức loại C", "Tiếng Việt"),
                new CivilServantTypeDto("CSD", "Công chức loại D", "Tiếng Việt"));

        for (CivilServantTypeDto dto : defaultDtos) {
            CivilServantTypeDto existingDto = civilServantTypeService.findByCode(dto.getCode());
            if (existingDto == null || existingDto.getId() == null) {
                civilServantTypeService.saveOrUpdate(dto, null);
            }
        }

    }

    // Tạo mặc định 1 tổ chức, gắn tất cả user vào tổ chức đó(bảng
    // tbl_organization_user),
    // tạm thời mỗi user thì thuộc 1 công ty dù thiết kế là manytomany
    @Override
    public void setupDefaultOrganizationForAllCurrentUsers() {
        // generate default oranization is globits
        Organization globitsOrg = null;

        List<Organization> oldResult = organizationExtService.findEntityByCode("globits");
        if (oldResult != null && oldResult.size() > 0) {
            globitsOrg = oldResult.get(0);
        }
        if (globitsOrg == null) {
            globitsOrg = new Organization();
            globitsOrg.setCode("globits");
            globitsOrg.setName("Công ty TNHH Globits");
            globitsOrg.setWebsite("https://globits.net/");

            globitsOrg = organizationRepository.save(globitsOrg);
        }

        List<User> allCurrentUsers = userRepository.findAll();
        for (User user : allCurrentUsers) {
            OrganizationUser orgUser = null;

            if (user.getOrg() != null)
                continue;

            // set org or user field
            user.setOrg(globitsOrg);
            user = userRepository.save(user);

            List<OrganizationUser> allOrgUserRes = organizationUserService.findEntityByOrgIdAndUsrId(globitsOrg.getId(),
                    user.getId());
            // find old organization_user first
            if (allOrgUserRes != null && allOrgUserRes.size() > 0)
                orgUser = allOrgUserRes.get(0);

            // if there's no old organizationUser => create new
            if (orgUser == null) {
                orgUser = new OrganizationUser();
                orgUser.setUser(user);
                orgUser.setOrganization(globitsOrg);

                orgUser = organizationUserRepository.save(orgUser);
            }
        }

//        System.out.println("Generate default organization for all current users successfully!");
    }

    @Override
    public void setupDefaultSalaryAreas() {
        List<SalaryArea> defaultSalaryAreas = Arrays.asList(new SalaryArea("Vùng 1", "V1", 23000.0, 4960000.0),
                new SalaryArea("Vùng 2", "V2", 21200.0, 4410000.0), new SalaryArea("Vùng 3", "V3", 18600.0, 3860000.0),
                new SalaryArea("Vùng 4", "V4", 16500.0, 3450000.0));

        for (SalaryArea salaryArea : defaultSalaryAreas) {
            List<SalaryArea> existingSalaryArea = salaryAreaRepository.findByCode(salaryArea.getCode());
            if (existingSalaryArea == null || existingSalaryArea.size() == 0) {
                salaryAreaRepository.save(salaryArea);
//                logger.info("Created new Salary Area: " + salaryArea.getName());
            } else {
//                logger.info("Salary Area " + salaryArea.getName() + " already exists.");
            }
        }
    }

    // Setup data Các thành phần lương mặc định của hệ thống
    // Định nghĩa thêm ở HrConstants.SalaryItemCodeSystemDefault
    @Override
    public void setupDefaultSalaryItems() {
        List<SalaryItemDto> systemDefaults = Arrays.asList(
//                getSIBHXaHoi(),
//                getSIBHXaHoiCongTyDong(),
//                getSIBHThatNghiep(),
//                getSIBHThatNghiepCongTyDong(),
//                getSIBHYTe(),
//                getSIBHYTeCongTyDong(),
//                getSIChucVu(),
//                getSIDonVi(),
//                getSIGiamTruBanThan(),
//                getSIGiamTruGiaCanh(),
//                getSIGiamTru1NguoiPhuThuoc(),
//                getSIGiamTruNguoiPhuThuoc(),
//                getSIMaNhanVien(),
//                getSILuongCoBan(),
//                getSILuongDongBHXH(),
//                getSILuongTheoNgayCong(),
//                getSILuongTheoGioCong(),
//                getSIKhoanPhiCongDoanCongTyDong(),
//                getSISoGioCong(),
//                getSISoGioCongChuan(),
//                getSISoGioCongOT(),
//                getSISoNgayCong(),
//                getSISoNgayCongChuan(),
//                getSISoNguoiPhuThuoc(),
//                getSIPhongBan(),
//                getSITenNhanVien(),
//                getSIThueTNCN(),
//                getSITongKhauTru(),
//                getSITongThuNhap(),
//                getSITongThuNhapMienThue(),
//                getSIThuNhapChiuThue(),
//                getSIThuNhapTinhThue(),
//                getSIHoNhanVien(),
//                getSIHoVaTenNhanVien(),
//                getSIEmailNhanVien(),
//                getSISdtNhanVien(),
//                getSISTT(),
//                getSILuongKyNay(),
//                getSICacKhoanGiamTru(),
//                getSIThucLinh(),
//                getSITamUng(),
                getSoGioLamViecHC(),
                getSoLanDiLamMuon(),
                getSoCaNghiPhepKhongLuong(),
                getSoCaNghiPhepCoLuong(),
                getSoNgayCong()
        );

        for (SalaryItemDto dto : systemDefaults) {
            SalaryItemDto existedItem = salaryItemService.findByCode(dto.getCode());
//
//            // if calculation type of system's salary item is not set up => update this
//            if (existedItem != null && existedItem.getValueType() == null) {
//                existedItem.setValueType(dto.getValueType());
//
//                salaryItemService.saveSalaryItem(existedItem);
//                continue;
//            }
//
//            Boolean isValidItem = salaryItemService.isValidCode(dto);
//            if (isValidItem == null || isValidItem.equals(false)) {
//                continue;
//            }

            if (existedItem != null) {
                copyFromDefaultSetup(existedItem, dto);
            }

            salaryItemService.saveSalaryItem(dto);
        }
    }

    private SalaryItemDto getSoCaNghiPhepCoLuong() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.SO_CA_NGHI_PHEP_CO_LUONG.getValue(),
                "Số ca nghỉ phép có lương",
                "Số ca nghỉ phép có lương",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USER_FILL.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.NUMBER.getValue()   // valueType
        );
    }

    private SalaryItemDto getSoCaNghiPhepKhongLuong() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.SO_CA_NGHI_PHEP_KHONG_LUONG.getValue(),
                "Số ca nghỉ phép không lương",
                "Số ca nghỉ phép không lương",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USER_FILL.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.NUMBER.getValue()   // valueType
        );
    }

    private SalaryItemDto getSoLanDiLamMuon() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.SO_LAN_DI_LAM_MUON.getValue(),
                "Số lần đi làm muộn",
                "Số lần đi làm muộn",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USER_FILL.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.NUMBER.getValue()   // valueType
        );
    }

    private SalaryItemDto getSoNgayCong() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.SO_NGAY_CONG.getValue(),
                "Số ngày công",
                "Số ngày công",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USER_FILL.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.NUMBER.getValue()   // valueType
        );
    }

    private void copyFromDefaultSetup(SalaryItemDto existedItem, SalaryItemDto defaultItem) {
        defaultItem.setId(existedItem.getId());
    }

    private SalaryItemDto getSITamUng() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.TAM_UNG_SYSTEM.getValue(),
                "Tạm ứng",
                "Số tiền tạm ứng của nhân viên trong kì lương được chọn",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false,  // isTaxable
                false,  // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.MONEY.getValue()   // valueType
        );
    }

    private SalaryItemDto getSIThucLinh() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.THUC_LINH_SYSTEM.getValue(),
                "Thực lĩnh",
                "Thực lĩnh là số tiền thực nhận sau khi trừ các khoản khấu trừ và tạm ứng. Công thức cơ bản: THUC_LINH_SYSTEM = LUONG_KY_NAY_SYSTEM - TAM_UNG_SYSTEM",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false,  // isTaxable
                false,  // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue(),
                "LUONG_KY_NAY_SYSTEM - TAM_UNG_SYSTEM",   // formula
                HrConstants.SalaryItemValueType.MONEY.getValue()   // valueType
        );
    }

    private SalaryItemDto getSILuongCoBan() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.LUONG_CO_BAN_SYSTEM.getValue(),
                "Lương cơ bản",
                "Lương cơ bản được lấy theo hợp đồng đang có hiệu lực của nhân viên",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false,  // isTaxable
                false,  // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.MONEY.getValue()   // valueType
        );
    }

    private SalaryItemDto getSILuongDongBHXH() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.LUONG_DONG_BHXH_SYSTEM.getValue(),
                "Lương đóng BHXH",
                "Lương tính bảo hiểm xã hội",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false,  // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.MONEY.getValue()   // valueType
        );
    }

    private SalaryItemDto getSISoNgayCong() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.SO_NGAY_CONG_SYSTEM.getValue(),
                "Số ngày công",
                "Tổng số ngày công trong kì lương (đã được chọn/thiết lập) mà nhân viên đã làm việc",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.NUMBER.getValue()   // valueType
        );
    }

    private SalaryItemDto getSISoNgayCongChuan() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.SO_NGAY_CONG_CHUAN_SYSTEM.getValue(),
                "Số ngày công chuẩn",
                "Tổng số ngày công chuẩn trong kì lương đã được chọn/thiết lập",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue(),
                "24",   // formula
                HrConstants.SalaryItemValueType.NUMBER.getValue()   // valueType
        );
    }

    private SalaryItemDto getSISoGioCongChuan() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.SO_GIO_CONG_CHUAN_SYSTEM.getValue(),
                "Số giờ công chuẩn",
                "Tổng số giờ công chuẩn trong kì lương đã được chọn/thiết lập",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue(),
                "188.7",   // formula
                HrConstants.SalaryItemValueType.NUMBER.getValue()   // valueType
        );
    }

    private SalaryItemDto getSILuongTheoNgayCong() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.LUONG_THEO_NGAY_CONG_SYSTEM.getValue(),
                "Lương theo ngày công",
                "Tổng lương tính theo số ngày công.",
                HrConstants.SalaryItemType.ADDITION.getValue(),
                true,  // isTaxable
                true,  // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue(),
                "(SO_NGAY_CONG_SYSTEM * LUONG_CO_BAN_SYSTEM/SO_NGAY_CONG_CHUAN_SYSTEM)", // formula
                HrConstants.SalaryItemValueType.MONEY.getValue()   // valueType
        );
    }

    private SalaryItemDto getSILuongTheoGioCong() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.LUONG_THEO_GIO_CONG_SYSTEM.getValue(),
                "Lương theo giờ công",
                "Tổng lương tính theo số giờ công trong kì lương đã được thiết lập.",
                HrConstants.SalaryItemType.ADDITION.getValue(),
                true,  // isTaxable
                true,  // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue(),
                "(SO_GIO_CONG_SYSTEM * LUONG_CO_BAN_SYSTEM/SO_GIO_CONG_CHUAN_SYSTEM)", // formula
                HrConstants.SalaryItemValueType.MONEY.getValue()   // valueType
        );
    }


    private SalaryItemDto getSISoGioCong() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.SO_GIO_CONG_SYSTEM.getValue(),
                "Số giờ công",
                "Tổng số giờ công làm việc trong kì lương đã chọn",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.NUMBER.getValue()   // valueType
        );
    }

    private SalaryItemDto getSISoGioCongOT() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.SO_GIO_CONG_OT_SYSTEM.getValue(),
                "Số giờ công OT",
                "Tổng số giờ công làm thêm ngoài giờ",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.NUMBER.getValue()   // valueType
        );
    }

    private SalaryItemDto getSIMaNhanVien() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.MA_NV_SYSTEM.getValue(),
                "Mã nhân viên",
                "Mã định danh nhân viên trong hệ thống",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.TEXT.getValue()   // valueType
        );
    }

    private SalaryItemDto getSIHoVaTenNhanVien() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.HO_VA_TEN_NV_SYSTEM.getValue(),
                "Họ và tên nhân viên",
                "Họ và tên đầy đủ của nhân viên",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                false,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.TEXT.getValue()   // valueType
        );
    }

    private SalaryItemDto getSIHoNhanVien() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.HO_NV_SYSTEM.getValue(),
                "Họ",
                "Họ và tên đệm của nhân viên",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                false,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.TEXT.getValue()   // valueType
        );
    }

    private SalaryItemDto getSITenNhanVien() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.TEN_NV_SYSTEM.getValue(),
                "Tên nhân viên",
                "Tên của nhân viên",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                false,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.TEXT.getValue()   // valueType
        );
    }

    private SalaryItemDto getSIChucVu() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.CHUC_VU_NV_SYSTEM.getValue(),
                "Chức vụ",
                "Chức vụ hiện tại của nhân viên",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.TEXT.getValue()   // valueType
        );
    }

    private SalaryItemDto getSIDonVi() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.DON_VI_NV_SYSTEM.getValue(),
                "Đơn vị",
                "Đơn vị trực thuộc của nhân viên",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.TEXT.getValue()   // valueType
        );
    }

    private SalaryItemDto getSIPhongBan() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.PHONG_BAN_NV_SYSTEM.getValue(),
                "Phòng ban",
                "Phòng ban nơi nhân viên làm việc",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.TEXT.getValue()   // valueType
        );
    }

    private SalaryItemDto getSISdtNhanVien() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.SDT_NV_SYSTEM.getValue(),
                "SĐT",
                "Số điện thoại của nhân viên (thông tin liên hệ)",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.TEXT.getValue()   // valueType
        );
    }

    private SalaryItemDto getSIEmailNhanVien() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.EMAIL_NV_SYSTEM.getValue(),
                "Email",
                "Email của nhân viên (thông tin liên hệ)",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.TEXT.getValue()   // valueType
        );
    }

    private SalaryItemDto getSoGioLamViecHC() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.SO_GIO_LAM_VIEC_HC.getValue(),
                "Số giờ làm việc hành chính",
                "Số giờ làm việc hành chính",
                HrConstants.SalaryItemType.ADDITION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USER_FILL.getValue(),
                null,   // formula
                HrConstants.SalaryItemValueType.NUMBER.getValue()   // valueType
        );
    }

    private SalaryItemDto getSIBHXaHoi() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.BH_XA_HOI_SYSTEM.getValue(),
                "BHXH (8%)",
                "Bảo hiểm xã hội mà nhân viên phải đóng",
                HrConstants.SalaryItemType.DEDUCTION.getValue(),
                false, // isTaxable
                true,  // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue(),
                "(LUONG_DONG_BHXH_SYSTEM * 0.08)", // formula
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSIBHYTe() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.BH_Y_TE_SYSTEM.getValue(),
                "BHYT (1,5%)",
                "Bảo hiểm y tế mà nhân viên phải đóng",
                HrConstants.SalaryItemType.DEDUCTION.getValue(),
                false, // isTaxable
                true,  // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue(),
                "LUONG_DONG_BHXH_SYSTEM * 0.015", // formula
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSIBHThatNghiep() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.BH_THAT_NGHIEP_SYSTEM.getValue(),
                "BHTN (1%)",
                "Bảo hiểm thất nghiệp mà nhân viên phải đóng",
                HrConstants.SalaryItemType.DEDUCTION.getValue(),
                false, // isTaxable
                true,  // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue(),
                "LUONG_DONG_BHXH_SYSTEM * 0.01", // formula
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSIBHXaHoiCongTyDong() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.BH_XA_HOI_CONG_TY_DONG_SYSTEM.getValue(),
                "BHXH công ty đóng (17%)",
                "Bảo hiểm xã hội mà công ty đóng",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                true,  // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue(),
                "LUONG_DONG_BHXH_SYSTEM * 0.17", // formula
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSIBHYTeCongTyDong() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.BH_Y_TE_CONG_TY_DONG_SYSTEM.getValue(),
                "BHYT công ty đóng (3%)",
                "Bảo hiểm y tế mà công ty đóng",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                true,  // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue(),
                "LUONG_DONG_BHXH_SYSTEM * 0.03", // formula
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSIBHThatNghiepCongTyDong() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.BH_THAT_NGHIEP_CONG_TY_DONG_SYSTEM.getValue(),
                "BHTN công ty đóng (1%)",
                "Bảo hiểm thất nghiệp mà công ty đóng",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                true,  // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue(),
                "LUONG_DONG_BHXH_SYSTEM * 0.01", // formula
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSIKhoanPhiCongDoanCongTyDong() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.KHOAN_PHI_CONG_DOAN_CONG_TY_DONG_SYSTEM.getValue(),
                "KPCĐ công ty đóng (2%)",
                "Khoản phí công đoàn do công ty đóng cho nhân viên",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                true,  // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue(),
                "LUONG_DONG_BHXH_SYSTEM * 0.02", // formula
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSIThueTNCN() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.THUE_TNCN_SYSTEM.getValue(),
                "Thuế TNCN",
                "Thuế thu nhập cá nhân mà nhân viên phải nộp. Cách tính: Dựa vào các khoản thu nhập (SalaryItemType = ADDITION) và cách tính trong hợp đồng lao động",
                HrConstants.SalaryItemType.DEDUCTION.getValue(),
                false,  // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSIThuNhapTinhThue() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.THU_NHAP_TINH_THUE_SYSTEM.getValue(),
                "Thu nhập tính thuế",
                "Thu nhập của nhân viên sau khi trừ các khoản giảm trừ để tính thuế TNCN. Cách tính: MAX(THU_NHAP_CHIU_THUE_SYSTEM - GIAM_TRU_GIA_CANH_SYSTEM,0)",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false,  // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
//                "MAX(THU_NHAP_CHIU_THUE_SYSTEM - GIAM_TRU_GIA_CANH_SYSTEM,0)", // formula
                null, // 17/12/24: hard code CalculationType is AutoSytem
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSIThuNhapChiuThue() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.THU_NHAP_CHIU_THUE_SYSTEM.getValue(),
                "Thu nhập chịu thuế",
                "Tổng thu nhập của nhân viên sau khi trừ đi các khoản giảm trừ thuế. Cách tính: TONG_THU_NHAP_SYSTEM - TONG_THU_NHAP_MIEN_THUE_SYSTEM",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false,  // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
//                "TONG_THU_NHAP_SYSTEM - TONG_THU_NHAP_MIEN_THUE_SYSTEM", // formula
                null, // 17/12/24: hard code CalculationType is AutoSytem
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSITongThuNhap() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.TONG_THU_NHAP_SYSTEM.getValue(),
                "Tổng thu nhập",
                "Tổng tất cả các khoản thu nhập (lương, phụ cấp, các khoản thu nhập khác, ...) của nhân viên trong kỳ lương được chọn. Cách tính: SUM(<các khoản có SalaryItemType = ADDITION>)",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false,  // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null, // formula
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSITongThuNhapMienThue() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.TONG_THU_NHAP_MIEN_THUE_SYSTEM.getValue(),
                "Tổng thu nhập miễn thuế",
                "Tổng tất cả các khoản thu nhập (lương, phụ cấp, các khoản thu nhập khác, ...) của nhân viên trong kỳ lương được chọn mà KHÔNG PHẢI CHỊU THUẾ. Cách tính: SUM(<các khoản có SalaryItemType = ADDITION> và isTaxable = false)",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSITongKhauTru() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.TONG_KHAU_TRU_SYSTEM.getValue(),
                "Tổng khấu trừ",
                "Tổng tất cả các khoản khấu trừ từ thu nhập của nhân viên. Cách tính: SUM(<các khoản có SalaryItemType = DEDUCTION>)",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null,
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSISoNguoiPhuThuoc() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.SO_NGUOI_PHU_THUOC_SYSTEM.getValue(),
                "Số người phụ thuộc",
                "Số lượng người phụ thuộc được khai báo để giảm trừ thuế. Cách tính: Hệ thống tự động lấy số người phụ thuộc của nhân viên",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null, // formula
                HrConstants.SalaryItemValueType.NUMBER.getValue() // valueType
        );
    }

    private SalaryItemDto getSIGiamTruBanThan() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.GIAM_TRU_BAN_THAN_SYSTEM.getValue(),
                "Giảm trừ bản thân",
                "Khoản giảm trừ thuế TNCN cho bản thân người lao động. Cách tính: Lấy theo giảm trừ bản thân được quy định theo luật hiện hành (khai báo trong thuộc tính chung của hệ thống)",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null, // formula (mặc định theo luật pháp)
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSIGiamTru1NguoiPhuThuoc() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.GIAM_TRU_1_NGUOI_PHU_THUOC_SYSTEM.getValue(),
                "Số tiền giảm trừ trên 1 người phụ thuộc",
                "Số tiền giảm trừ trên 1 người phụ thuộc. Cách tính: Lấy theo giảm trừ bản thân được quy định theo luật hiện hành (khai báo trong thuộc tính chung của hệ thống)",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null, // formula (mặc định theo luật pháp)
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSIGiamTruNguoiPhuThuoc() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.GIAM_TRU_NGUOI_PHU_THUOC_SYSTEM.getValue(),
                "Giảm trừ người phụ thuộc",
                "Khoản giảm trừ thuế TNCN cho nhân viên tính theo số người phụ thuộc. Cách tính: SO_NGUOI_PHU_THUOC_SYSTEM * 4400000",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null, // formula (mặc định theo luật pháp)
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSIGiamTruGiaCanh() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.GIAM_TRU_GIA_CANH_SYSTEM.getValue(),
                "Giảm trừ gia cảnh",
                "Tổng giảm trừ thuế TNCN bao gồm bản thân và người phụ thuộc",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue(),
                "11000000 + SO_NGUOI_PHU_THUOC_SYSTEM * 4400000", // formula
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSICacKhoanGiamTru() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.CAC_KHOAN_GIAM_TRU_SYSTEM.getValue(),
                "Các khoản giảm trừ",
                "Các khoản giảm trừ khi tính thu nhập tính thuế. Gồm: Giảm trừ gia cảnh, Các khoản BHXH bắt buộc (BHXH, BHYT, BHTN) và bảo hiểm trong một số lĩnh vực nghề nghiệp đặc biệt, Các khoản cá nhân đóng góp cho từ thiện, khuyến học hoặc nhân đạo: Mức giảm trừ tối đa không vượt quá thu nhập tính thuế và phải có tài liệu chứng minh",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null, // formula
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    private SalaryItemDto getSISTT() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.STT_SYSTEM.getValue(),
                "STT",
                "Số thứ tụ cho các dòng",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue(),
                null, // formula
                HrConstants.SalaryItemValueType.NUMBER.getValue() // valueType
        );
    }

    private SalaryItemDto getSILuongKyNay() {
        return new SalaryItemDto(
                HrConstants.SalaryItemCodeSystemDefault.LUONG_KY_NAY_SYSTEM.getValue(),
                "Lương kỳ này",
                "Lương thực tế mà người lao động nhận được trong kỳ lương sau khi trừ các khoản giảm trừ, thuế, ...",
                HrConstants.SalaryItemType.INFORMATION.getValue(),
                false, // isTaxable
                false, // isInsurable
                true,  // isActive
                null,  // maxValue
                HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue(),
                "TONG_THU_NHAP_SYSTEM - TONG_KHAU_TRU_SYSTEM", // formula
                HrConstants.SalaryItemValueType.MONEY.getValue() // valueType
        );
    }

    // Setup data Các thành phần lương khác (không phải mặc định)
    // Định nghĩa thêm ở HrConstants.SalaryItemCodeSetup
    @Override
    public void setupDefaultOtherSalaryItems() {
        // Các thành phần lương khác được setup
        List<SalaryItemDto> defaultDtos = Arrays.asList(
                new SalaryItemDto(HrConstants.SalaryItemCodeSetup.PHU_CAP_TRACH_NHIEM.getValue(), "Phụ cấp trách nhiệm",
                        "Phụ cấp dành cho các vị trí có trách nhiệm đặc thù", 1,
                        true, false, true, 5000000.0, 2, null, 2),
                new SalaryItemDto(HrConstants.SalaryItemCodeSetup.QUA_LE_TET.getValue(), "Quà lễ, tết",
                        "Khoản quà tặng nhân dịp lễ tết", 1,
                        true, false, true, 2000000.0, 2, null, 2),
                new SalaryItemDto(HrConstants.SalaryItemCodeSetup.PHU_CAP_DIEN_THOAI.getValue(), "Phụ cấp điện thoại",
                        "Hỗ trợ chi phí điện thoại liên lạc", 1,
                        false, false, true, 1000000.0, 2, null, 2),
                new SalaryItemDto(HrConstants.SalaryItemCodeSetup.CONG_TAC_PHI.getValue(), "Công tác phí",
                        "Khoản hỗ trợ chi phí khi đi công tác", 1,
                        false, false, true, 3000000.0, 2, null, 2)
        );

        for (SalaryItemDto dto : defaultDtos) {
            SalaryItemDto existingDto = salaryItemService.findByCode(dto.getCode());
            if (existingDto == null || existingDto.getId() == null) {
                salaryItemService.saveSalaryItem(dto);
            }
        }
    }


    @Override
    public void setupDefaultSalaryUnits() {
        List<SalaryUnit> defaultSalaryUnits = Arrays.asList(new SalaryUnit("Hàng ngày", "N", 1.0),
                new SalaryUnit("Hàng tuần", "T", 7.0), new SalaryUnit("Hàng tháng", "Th", 30.0),
                new SalaryUnit("Hàng năm", "N", 365.0));

        for (SalaryUnit salaryUnit : defaultSalaryUnits) {
            Long existingSalaryUnit = salaryUnitRepository.checkCode(salaryUnit.getCode(), null);
            if (existingSalaryUnit == 0) {
                salaryUnitRepository.save(salaryUnit);
            }
        }
    }

    @Override
    public void setupDefaultSalaryTypes() {
        List<SalaryType> defaultSalaryTypes = Arrays.asList(
                new SalaryType("Lương cơ bản", "Lương cơ bản", "Mô tả cho loại lương cơ bản"),
                new SalaryType("Lương làm thêm giờ", "Lương OT", "Mô tả cho loại lương làm thêm giờ"),
                new SalaryType("Thưởng", "Thưởng", "Mô tả cho loại thưởng"));

        for (SalaryType salaryType : defaultSalaryTypes) {
            // Kiểm tra xem loại lương đã tồn tại chưa dựa trên tên
            Long existingSalaryTypeCount = salaryTypeRepository.countByName(salaryType.getName());
            if (existingSalaryTypeCount == 0) {
                // Lưu nếu chưa tồn tại
                salaryTypeRepository.save(salaryType);
            }
        }
    }

    @Override
    public void setupDefaultSalaryConfigs() {
        List<SalaryConfig> defaultSalaryConfigs = Arrays.asList(
                new SalaryConfig("Lương cơ bản", "Cấu hình lương cơ bản", "LUONG_CO_BAN",
                        "Cấu hình lương cơ bản cho nhân viên", 100),
                new SalaryConfig("Tỷ lệ làm thêm giờ", "Cấu hình tỷ lệ làm thêm giờ", "TY_LE_LAM_THEM_GIO",
                        "Cấu hình tỷ lệ làm thêm giờ cho nhân viên", 200),
                new SalaryConfig("Thưởng", "Cấu hình thưởng", "THUONG", "Cấu hình thưởng cho nhân viên", 300));

        for (SalaryConfig salaryConfig : defaultSalaryConfigs) {
            Long existingSalaryConfig = salaryConfigRepository.checkCode(salaryConfig.getCode(), null);
            if (existingSalaryConfig == 0) {
                salaryConfigRepository.save(salaryConfig);
            }
        }
    }

    // @Override
    public void generateCodeForAllTasksInAllProjects() {
        // first get all projects
        List<Project> allProjects = projectRepository.findAll();
        for (Project project : allProjects) {
            // generate code for all task in each project
            List<HrTask> tasksInProject = hrTaskRepository.getTasksInProject(project.getId(),
                    PageRequest.of(0, 1000000));

            // handling for each task, generate for the oldest first, then for the newer
            // later
            int codeOrder = 0;
            for (int i = tasksInProject.size() - 1; i >= 0; i--) {
                HrTask currentTask = tasksInProject.get(i);
                codeOrder++;

                if (currentTask.getCode() == null || currentTask.getCode().length() == 0) {
                    currentTask.setCode(String.valueOf(codeOrder));

                    hrTaskRepository.save(currentTask);
                }
            }
        }

        System.out.println("generating code function completed");
    }

    @Override
    public void setupDefaultRecruitmentExamType() {
        List<RecruitmentExamTypeDto> defaultDtos = Arrays.asList(
                new RecruitmentExamTypeDto("Ngoại ngữ", "01", "Kiểm tra trình độ ngoại ngữ"),
                new RecruitmentExamTypeDto("Chuyên ngành", "02", "Thi viêt"),
                new RecruitmentExamTypeDto("Phỏng vấn", "03", "Phỏng vấn trực tiếp thí sinh"),
                new RecruitmentExamTypeDto("Sàng lọc", "04", "Sàng lọc ứng viên"));

        for (RecruitmentExamTypeDto dto : defaultDtos) {
            Boolean isExisting = recruitmentExamTypeService.checkCode(null, dto.getCode());
            if (!isExisting) {
                recruitmentExamTypeService.saveOne(dto, null);
            }
        }
    }

    @Override
    public void setupDefaultFamilyRelationship() {
        List<FamilyRelationshipDto> defaultDtos = Arrays.asList(new FamilyRelationshipDto("Vợ", "VO", null),
                new FamilyRelationshipDto("Chồng", "CHONG", null), new FamilyRelationshipDto("Con", "CON", null),
                new FamilyRelationshipDto("Ông ngoại", "ONG_NGOAI", null), new FamilyRelationshipDto("Bà Ngoại", "BA_NGOAI", null),
                new FamilyRelationshipDto("Ông nội", "ONG_NOI", null), new FamilyRelationshipDto("Bà Nội", "BA_NOI", null),
                new FamilyRelationshipDto("Bố ruột", "BO_RUOT", null), new FamilyRelationshipDto("Mẹ ruột", "ME_RUOT", null),
                new FamilyRelationshipDto("Anh trai", "ANH_TRAI", null), new FamilyRelationshipDto("Em trai", "EM_TRAI", null),
                new FamilyRelationshipDto("Chị gái", "CHI_GAI", null), new FamilyRelationshipDto("Em gái", "EM_GAI", null),
                new FamilyRelationshipDto("Bạn bè", "BAN_BE", null));

        for (FamilyRelationshipDto dto : defaultDtos) {
            Boolean isExisting = familyRelationshipService.checkCode(null, dto.getCode());
            if (!isExisting) {
                familyRelationshipService.saveOrUpdate(dto, null);
            }
        }
    }

    @Override
    public void setupDefaultDisciplineReason() {
        List<DisciplineReasonDto> defaultDtos = Arrays.asList(
                new DisciplineReasonDto("Vi phạm quy định", "01", "Không tuân thủ nội quy công ty."),
                new DisciplineReasonDto("Thái độ không đúng mực", "02",
                        "Có hành vi xúc phạm hoặc gây xích mích với đồng nghiệp."),
                new DisciplineReasonDto("Đi muộn, về sớm", "03", "Thường xuyên đến muộn mà không có lý do chính đáng."),
                new DisciplineReasonDto("Thiếu trách nhiệm", "04",
                        "Không hoàn thành công việc được giao hoặc đúng hạn."),
                new DisciplineReasonDto("Hiệu suất kém", "05", "Kết quả công việc không đạt yêu cầu đề ra."),
                new DisciplineReasonDto("Lạm dụng tài nguyên", "06",
                        "Sử dụng tài nguyên công ty cho mục đích cá nhân."),
                new DisciplineReasonDto("Vi phạm đạo đức", "07",
                        "Thực hiện hành vi không đúng đắn hoặc trái với quy tắc tổ chức."),
                new DisciplineReasonDto("Hành vi không an toàn", "08",
                        "Có hành vi gây nguy hiểm cho an toàn lao động."),
                new DisciplineReasonDto("Làm việc không trung thực", "09",
                        "Gian lận, không thành thật trong báo cáo công việc."),
                new DisciplineReasonDto("Vi phạm quyền riêng tư", "10", "Xâm phạm quyền riêng tư của đồng nghiệp."),
                new DisciplineReasonDto("Sử dụng ma túy hoặc rượu bia", "11",
                        "Sử dụng chất kích thích ảnh hưởng đến hiệu suất công việc."),
                new DisciplineReasonDto("Thiếu tuân thủ nội quy", "12", "Vi phạm các nội quy về an toàn và bảo mật."),
                new DisciplineReasonDto("Gây thiệt hại tài sản", "13",
                        "Làm hỏng hoặc gây thiệt hại cho tài sản của công ty."),
                new DisciplineReasonDto("Lạm dụng quyền hạn", "14", "Sử dụng quyền hạn vào mục đích tư lợi cá nhân."),
                new DisciplineReasonDto("Xung đột lợi ích", "15",
                        "Thực hiện hành vi gây mâu thuẫn lợi ích có thể ảnh hưởng đến công việc."),
                new DisciplineReasonDto("Thiếu sự hợp tác", "16",
                        "Không làm việc nhóm hoặc từ chối hỗ trợ đồng nghiệp."),
                new DisciplineReasonDto("Vi phạm quy tắc bảo mật thông tin", "17",
                        "Rò rỉ thông tin nhạy cảm của công ty."),
                new DisciplineReasonDto("Không chấp hành chỉ đạo của cấp trên", "18",
                        "Không tuân theo chỉ thị hoặc hướng dẫn từ quản lý."),
                new DisciplineReasonDto("Gây rối trong môi trường làm việc", "19",
                        "Tạo ra bầu không khí căng thẳng hoặc không chuyên nghiệp."),
                new DisciplineReasonDto("Lạm dụng thời gian làm việc", "20",
                        "Sử dụng thời gian làm việc cho việc riêng hoặc không liên quan."));

        for (DisciplineReasonDto dto : defaultDtos) {
            DisciplineReasonDto existingDto = disciplineReasonService.findByCode(dto.getCode());
            if (existingDto == null || existingDto.getId() == null) {
                disciplineReasonService.saveDisciplineReason(dto);
            }
        }
    }

    @Override
    public void setupDefaultRefusalReason() {
        List<RefusalReasonDto> defaultDtos = Arrays.asList(
                new RefusalReasonDto("Thiếu thời gian", "01", "Không đủ thời gian để thực hiện hoặc đánh giá đề xuất."),
                new RefusalReasonDto("Không phù hợp", "02",
                        "Đề xuất không phù hợp với nhu cầu hoặc tiêu chí hiện tại."),
                new RefusalReasonDto("Chi phí cao", "03", "Chi phí dự kiến vượt quá ngân sách đã định."),
                new RefusalReasonDto("Khả năng không đủ", "04", "Nguồn lực hiện tại không đủ để thực hiện dự án."),
                new RefusalReasonDto("Xung đột với kế hoạch hiện tại", "05",
                        "Dự án có thể gây ra xung đột với các kế hoạch đã được phê duyệt."),
                new RefusalReasonDto("Lo ngại về chất lượng", "06",
                        "Có những lo ngại về khả năng đạt tiêu chuẩn chất lượng mong muốn."),
                new RefusalReasonDto("Đã có sự lựa chọn khác", "07",
                        "Đã chọn một giải pháp khác hoặc nhà cung cấp khác."),
                new RefusalReasonDto("Yêu cầu quá gấp", "08", "Thời gian yêu cầu quá gấp, không thể đáp ứng được."),
                new RefusalReasonDto("Vấn đề về pháp lý và quy định", "09",
                        "Có các vấn đề pháp lý hoặc quy định liên quan đến dự án."),
                new RefusalReasonDto("Không đủ thông tin", "10", "Thiếu thông tin cần thiết để đưa ra quyết định."),
                new RefusalReasonDto("Rủi ro cao", "11", "Dự án tiềm ẩn nhiều rủi ro không thể chấp nhận được."),
                new RefusalReasonDto("Thiếu sự tin tưởng", "12",
                        "Thiếu niềm tin vào khả năng thực hiện của bên đề xuất."),
                new RefusalReasonDto("Chưa đủ thuyết phục", "13", "Đề xuất chưa đủ thuyết phục để được chấp nhận."),
                new RefusalReasonDto("Khả năng tương thích thấp", "14",
                        "Sự không tương thích với hệ thống hoặc quy trình hiện tại."),
                new RefusalReasonDto("Thiếu hỗ trợ từ cấp trên", "15",
                        "Không có sự ủng hộ từ các cấp quản lý hoặc lãnh đạo."),
                new RefusalReasonDto("Thiếu kinh nghiệm", "16",
                        "Bên đề xuất thiếu kinh nghiệm cần thiết trong lĩnh vực này."),
                new RefusalReasonDto("Thay đổi nhu cầu", "17", "Nhu cầu đã thay đổi, không còn phù hợp với đề xuất."),
                new RefusalReasonDto("Thời gian triển khai dài", "18",
                        "Thời gian cần để triển khai quá dài so với kế hoạch."),
                new RefusalReasonDto("Không đáp ứng tiêu chuẩn an toàn", "19",
                        "Đề xuất không đáp ứng các tiêu chuẩn an toàn cần thiết."),
                new RefusalReasonDto("Mâu thuẫn lợi ích", "20",
                        "Có khả năng xảy ra mâu thuẫn lợi ích giữa các bên liên quan."));

        for (RefusalReasonDto dto : defaultDtos) {
            RefusalReasonDto existingDto = refusalReasonService.findByCode(dto.getCode());
            if (existingDto == null || existingDto.getId() == null) {
                refusalReasonService.saveOrUpdate(dto);
            }
        }
    }

    @Override
    public void setupDefaultDeferredType() {
        List<DeferredTypeDto> defaultDtos = Arrays.asList(
                new DeferredTypeDto("Tạm giam", "01", "Bị tạm giam do các lý do pháp lý hoặc điều tra."),
                new DeferredTypeDto("Đi nghĩa vụ", "02",
                        "Đang thực hiện nghĩa vụ quân sự hoặc nghĩa vụ khác theo luật định."),
                new DeferredTypeDto("Bệnh tật", "03",
                        "Bị ảnh hưởng bởi bệnh tật, không thể tham gia hoặc thực hiện công việc."),
                new DeferredTypeDto("Thiếu nguồn lực", "04", "Không đủ nguồn lực cần thiết để tiếp tục thực hiện."),
                new DeferredTypeDto("Đi công tác", "05",
                        "Đang đi công tác xa, không thể tham gia trong thời gian này."),
                new DeferredTypeDto("Thay đổi kế hoạch", "06", "Kế hoạch đã thay đổi, cần thời gian để điều chỉnh."),
                new DeferredTypeDto("Chăm sóc gia đình", "07", "Cần tạm hoãn do có trách nhiệm chăm sóc gia đình."),
                new DeferredTypeDto("Học tập", "08", "Đang tham gia khóa học hoặc chương trình đào tạo."),
                new DeferredTypeDto("Tham gia sự kiện", "09", "Đang tham gia sự kiện quan trọng không thể hoãn lại."),
                new DeferredTypeDto("Mất khả năng làm việc", "10", "Mất khả năng làm việc tạm thời vì lý do cá nhân."));

        for (DeferredTypeDto dto : defaultDtos) {
            DeferredTypeDto existingDto = deferredTypeService.findByCode(dto.getCode());
            if (existingDto == null || existingDto.getId() == null) {
                deferredTypeService.saveOrUpdate(dto);
            }
        }
    }

    @Override
    public void setupDefaultTransferType() {
        List<TransferTypeDto> defaultDtos = Arrays.asList(
                new TransferTypeDto("Chuyển đổi vị trí", "01",
                        "Chuyển đổi nhân viên sang vị trí công việc khác trong cùng tổ chức."),
                new TransferTypeDto("Điều chuyển tạm thời", "02",
                        "Điều chuyển nhân viên đến vị trí tạm thời trong một khoảng thời gian nhất định."),
                new TransferTypeDto("Chuyển nhượng", "03", "Chuyển nhượng nhân viên từ bộ phận này sang bộ phận khác."),
                new TransferTypeDto("Chuyển giao dự án", "04",
                        "Chuyển giao trách nhiệm và quyền hạn cho một nhóm hoặc cá nhân khác."),
                new TransferTypeDto("Chuyển công tác", "05", "Chuyển nhân viên đến một địa điểm làm việc khác."),
                new TransferTypeDto("Thay đổi bộ phận", "06", "Thay đổi bộ phận làm việc của nhân viên."),
                new TransferTypeDto("Chuyển đến chi nhánh khác", "07",
                        "Chuyển nhân viên đến một chi nhánh khác của công ty."),
                new TransferTypeDto("Chuyển đổi sang dự án khác", "08", "Chuyển nhân viên sang một dự án mới."));

        for (TransferTypeDto dto : defaultDtos) {
            TransferTypeDto existingDto = transferTypeService.findByCode(dto.getCode());
            if (existingDto == null || existingDto.getId() == null) {
                transferTypeService.saveOrUpdate(dto);
            }
        }
    }

    @Override
    public void setupDefaultRankTitle() {
        List<RankTitleDto> defaultDtos = Arrays.asList(
                new RankTitleDto(1, 0, "Cấp bậc 1", "Rank Title 1", "RT1", "Cấp bậc cao nhất"),
                new RankTitleDto(2, 1, "Cấp bậc 2", "Rank Title 2", "RT2", "Cấp bậc cao"),
                new RankTitleDto(3, 2, "Cấp bậc 3", "Rank Title 3", "RT3", "Cấp bậc trung bình"),
                new RankTitleDto(4, 3, "Cấp bậc 4", "Rank Title 4", "RT4", "Cấp bậc thấp"),
                new RankTitleDto(5, 4, "Cấp bậc 5", "Rank Title 5", "RT5", "Cấp bậc thấp nhất"));

        for (RankTitleDto dto : defaultDtos) {
            RankTitleDto existingDto = rankTitleService.findByShortName(dto.getShortName());
            if (existingDto == null || existingDto.getId() == null) {
                rankTitleService.saveRankTitle(dto);
            }
        }
    }

    @Override
    public void setupDefaultPositionTitle() {
        // Tạo danh sách các chức danh (PositionTitle)
        PositionTitleDto internTitle = new PositionTitleDto("Thực tập sinh", "INTERN",
                "Thực tập sinh cho các phòng ban.");
        PositionTitleDto staffTitle = new PositionTitleDto("Nhân viên", "STAFF",
                "Nhân viên các phòng ban trong công ty.");
        PositionTitleDto seniorStaffTitle = new PositionTitleDto("Nhân viên cấp cao", "SENIOR_STAFF",
                "Nhân viên cấp cao các phòng ban.");
        PositionTitleDto teamLeaderTitle = new PositionTitleDto("Trưởng nhóm", "TEAM_LEADER",
                "Trưởng nhóm quản lý đội nhóm nhỏ.");
        PositionTitleDto departmentManagerTitle = new PositionTitleDto("Trưởng phòng", "DEPARTMENT_MANAGER",
                "Quản lý một phòng ban.");
        PositionTitleDto directorTitle = new PositionTitleDto("Giám đốc", "DIRECTOR",
                "Giám đốc quản lý một bộ phận hoặc công ty.");

        // Danh sách chức danh mặc định
        List<PositionTitleDto> defaultPositionTitles = Arrays.asList(internTitle, staffTitle, seniorStaffTitle,
                teamLeaderTitle, departmentManagerTitle, directorTitle);

        // Lưu các chức danh vào hệ thống nếu chưa tồn tại
        for (PositionTitleDto titleDto : defaultPositionTitles) {
            Boolean isExistingTitle = positionTitleService.checkCode(null, titleDto.getCode());
            if (isExistingTitle == null || !isExistingTitle) {
                positionTitleService.saveOrUpdate(titleDto);
            }
        }
    }

    @Override
    public void setupDefaultPosition() {
        // List of PositionDto for all departments
        List<PositionDto> positions = new ArrayList<>();

// Phòng Nhân Sự (HR001)
        positions.add(new PositionDto("HR001_INTERN", "Thực tập sinh nhân sự",
                "Thực tập các công việc liên quan đến nhân sự", 1, "INTERN", "HR001"));
        positions.add(new PositionDto("HR001_STAFF", "Nhân viên nhân sự", "Chịu trách nhiệm quản lý nhân sự", 1,
                "STAFF", "HR001"));
        positions.add(new PositionDto("HR001_SENIOR_STAFF", "Nhân viên cấp cao nhân sự",
                "Quản lý các chính sách nhân sự cao cấp", 1, "SENIOR_STAFF", "HR001"));
        positions.add(new PositionDto("HR001_TEAM_LEADER", "Trưởng nhóm nhân sự", "Quản lý đội ngũ nhân sự", 1,
                "TEAM_LEADER", "HR001"));
        positions.add(new PositionDto("HR001_DEPARTMENT_MANAGER", "Trưởng phòng nhân sự",
                "Lãnh đạo toàn bộ phòng nhân sự", 1, "DEPARTMENT_MANAGER", "HR001"));
        positions.add(new PositionDto("HR001_DIRECTOR", "Giám đốc nhân sự", "Quản lý chiến lược nhân sự", 1, "DIRECTOR",
                "HR001"));

// Phòng Sản Xuất (PROD006)
        positions.add(new PositionDto("PROD006_INTERN", "Thực tập sinh sản xuất", "Thực tập các công việc sản xuất", 1,
                "INTERN", "PROD006"));
        positions.add(new PositionDto("PROD006_STAFF", "Nhân viên sản xuất", "Thực hiện các công việc sản xuất", 1,
                "STAFF", "PROD006"));
        positions.add(new PositionDto("PROD006_SENIOR_STAFF", "Nhân viên cấp cao sản xuất",
                "Chịu trách nhiệm giám sát quy trình sản xuất", 1, "SENIOR_STAFF", "PROD006"));
        positions.add(new PositionDto("PROD006_TEAM_LEADER", "Trưởng nhóm sản xuất", "Quản lý nhóm sản xuất", 1,
                "TEAM_LEADER", "PROD006"));
        positions.add(new PositionDto("PROD006_DEPARTMENT_MANAGER", "Trưởng phòng sản xuất",
                "Điều hành toàn bộ phòng sản xuất", 1, "DEPARTMENT_MANAGER", "PROD006"));
        positions.add(new PositionDto("PROD006_DIRECTOR", "Giám đốc sản xuất", "Quản lý chiến lược sản xuất", 1,
                "DIRECTOR", "PROD006"));

// Phòng Công Nghệ Thông Tin (IT004)
        positions.add(new PositionDto("IT004_INTERN", "Thực tập sinh CNTT",
                "Thực tập phát triển phần mềm và quản lý hệ thống", 1, "INTERN", "IT004"));
        positions.add(new PositionDto("IT004_STAFF", "Nhân viên CNTT", "Phát triển và bảo trì phần mềm", 1, "STAFF",
                "IT004"));
        positions.add(new PositionDto("IT004_SENIOR_STAFF", "Nhân viên cấp cao CNTT", "Thiết kế và giám sát hệ thống",
                1, "SENIOR_STAFF", "IT004"));
        positions.add(new PositionDto("IT004_TEAM_LEADER", "Trưởng nhóm CNTT", "Quản lý nhóm phát triển phần mềm", 1,
                "TEAM_LEADER", "IT004"));
        positions.add(new PositionDto("IT004_DEPARTMENT_MANAGER", "Trưởng phòng CNTT", "Lãnh đạo toàn bộ phòng CNTT", 1,
                "DEPARTMENT_MANAGER", "IT004"));
        positions.add(
                new PositionDto("IT004_DIRECTOR", "Giám đốc CNTT", "Quản lý chiến lược CNTT", 1, "DIRECTOR", "IT004"));

// Các chức vụ chi tiết department IT004
        positions.add(new PositionDto("IT004_TESTER", "Kiểm thử viên",
                "Thực hiện kiểm thử phần mềm, đảm bảo chất lượng sản phẩm", 1, "STAFF", "IT004"));
        positions.add(new PositionDto("IT004_FRONTEND_DEV", "Lập trình viên Frontend",
                "Phát triển giao diện người dùng cho các ứng dụng web", 1, "STAFF", "IT004"));
        positions.add(new PositionDto("IT004_BACKEND_DEV", "Lập trình viên Backend",
                "Phát triển và duy trì các API và dịch vụ phía máy chủ", 1, "STAFF", "IT004"));
        positions.add(new PositionDto("IT004_FULLSTACK_DEV", "Lập trình viên Fullstack",
                "Thực hiện phát triển cả frontend và backend", 1, "SENIOR_STAFF", "IT004"));
        positions.add(new PositionDto("IT004_DEVOPS", "Kỹ sư DevOps",
                "Thiết lập và duy trì hạ tầng CI/CD, tối ưu hóa quá trình triển khai", 1, "SENIOR_STAFF", "IT004"));
        positions.add(new PositionDto("IT004_SYS_ADMIN", "Quản trị hệ thống",
                "Quản lý, giám sát và bảo trì hệ thống mạng và máy chủ", 1, "STAFF", "IT004"));
        positions.add(new PositionDto("IT004_SECURITY_ADMIN", "Quản trị viên bảo mật",
                "Quản lý bảo mật hệ thống và ứng phó sự cố bảo mật", 1, "SENIOR_STAFF", "IT004"));
        positions.add(new PositionDto("IT004_DATA_SCIENTIST", "Chuyên viên khoa học dữ liệu",
                "Phân tích và xử lý dữ liệu lớn, đưa ra các giải pháp dựa trên dữ liệu", 1, "SENIOR_STAFF", "IT004"));
        positions.add(new PositionDto("IT004_ML_ENGINEER", "Kỹ sư Machine Learning",
                "Thiết kế và triển khai các mô hình machine learning", 1, "SENIOR_STAFF", "IT004"));
        positions.add(new PositionDto("IT004_CLOUD_ENGINEER", "Kỹ sư đám mây",
                "Xây dựng và quản lý hạ tầng cloud, tối ưu hoá tài nguyên cloud", 1, "SENIOR_STAFF", "IT004"));
        positions.add(new PositionDto("IT004_IT_SUPPORT", "Nhân viên hỗ trợ CNTT",
                "Hỗ trợ người dùng và xử lý các sự cố kỹ thuật", 1, "STAFF", "IT004"));
        positions.add(new PositionDto("IT004_DATABASE_ADMIN", "Quản trị cơ sở dữ liệu",
                "Quản lý và tối ưu hóa cơ sở dữ liệu", 1, "STAFF", "IT004"));
        positions.add(new PositionDto("IT004_PROJECT_MANAGER", "Quản lý dự án CNTT",
                "Quản lý các dự án phát triển phần mềm, đảm bảo tiến độ và chất lượng", 1, "TEAM_LEADER", "IT004"));
        positions.add(new PositionDto("IT004_SCRUM_MASTER", "Scrum Master",
                "Hỗ trợ và thúc đẩy nhóm phát triển theo phương pháp Scrum", 1, "TEAM_LEADER", "IT004"));

// Phòng Marketing (MKT005)
        positions.add(new PositionDto("MKT005_INTERN", "Thực tập sinh marketing",
                "Thực tập các công việc liên quan đến marketing", 1, "INTERN", "MKT005"));
        positions.add(new PositionDto("MKT005_STAFF", "Nhân viên marketing", "Thực hiện các chiến dịch marketing", 1,
                "STAFF", "MKT005"));
        positions.add(new PositionDto("MKT005_SENIOR_STAFF", "Nhân viên cấp cao marketing",
                "Xây dựng chiến lược marketing", 1, "SENIOR_STAFF", "MKT005"));
        positions.add(new PositionDto("MKT005_TEAM_LEADER", "Trưởng nhóm marketing", "Quản lý các dự án marketing", 1,
                "TEAM_LEADER", "MKT005"));
        positions.add(new PositionDto("MKT005_DEPARTMENT_MANAGER", "Trưởng phòng marketing",
                "Lãnh đạo toàn bộ phòng marketing", 1, "DEPARTMENT_MANAGER", "MKT005"));
        positions.add(new PositionDto("MKT005_DIRECTOR", "Giám đốc marketing", "Quản lý chiến lược và thương hiệu", 1,
                "DIRECTOR", "MKT005"));

// Phòng Kinh Doanh (SALES003)
        positions.add(new PositionDto("SALES003_INTERN", "Thực tập sinh kinh doanh",
                "Thực tập các công việc kinh doanh", 1, "INTERN", "SALES003"));
        positions.add(new PositionDto("SALES003_STAFF", "Nhân viên kinh doanh", "Bán hàng và phát triển khách hàng", 1,
                "STAFF", "SALES003"));
        positions.add(new PositionDto("SALES003_SENIOR_STAFF", "Nhân viên cấp cao kinh doanh", "Phát triển thị trường",
                1, "SENIOR_STAFF", "SALES003"));
        positions.add(new PositionDto("SALES003_TEAM_LEADER", "Trưởng nhóm kinh doanh", "Quản lý nhóm bán hàng", 1,
                "TEAM_LEADER", "SALES003"));
        positions.add(new PositionDto("SALES003_DEPARTMENT_MANAGER", "Trưởng phòng kinh doanh",
                "Lãnh đạo phòng kinh doanh", 1, "DEPARTMENT_MANAGER", "SALES003"));
        positions.add(new PositionDto("SALES003_DIRECTOR", "Giám đốc kinh doanh", "Quản lý chiến lược kinh doanh", 1,
                "DIRECTOR", "SALES003"));

// Phòng Kế Toán (ACC002)
        positions.add(new PositionDto("ACC002_INTERN", "Thực tập sinh kế toán", "Thực tập các công việc kế toán", 1,
                "INTERN", "ACC002"));
        positions.add(new PositionDto("ACC002_STAFF", "Nhân viên kế toán",
                "Thực hiện các công việc liên quan đến sổ sách", 1, "STAFF", "ACC002"));
        positions.add(new PositionDto("ACC002_SENIOR_STAFF", "Nhân viên cấp cao kế toán", "Giám sát quy trình kế toán",
                1, "SENIOR_STAFF", "ACC002"));
        positions.add(new PositionDto("ACC002_TEAM_LEADER", "Trưởng nhóm kế toán", "Quản lý nhóm kế toán", 1,
                "TEAM_LEADER", "ACC002"));
        positions.add(new PositionDto("ACC002_DEPARTMENT_MANAGER", "Trưởng phòng kế toán", "Điều hành phòng kế toán", 1,
                "DEPARTMENT_MANAGER", "ACC002"));
        positions.add(new PositionDto("ACC002_DIRECTOR", "Giám đốc tài chính", "Quản lý chiến lược tài chính", 1,
                "DIRECTOR", "ACC002"));

        for (PositionDto dto : positions) {
            PositionDto existingDto = positionService.findByCode(dto.getCode());
            if (existingDto == null || existingDto.getId() == null) {
                positionService.setupDataPosition(dto);
            }
        }
    }

    @Override
    public void setupDefaultDepartmentGroup() {
        List<DepartmentGroupDto> defaultDtos = Arrays.asList(
                new DepartmentGroupDto("Nhóm Quản lý Nhân sự", "Human Resource Management", "HR", 1,
                        "Bao gồm Phòng Nhân sự (HR), Phòng Hành chính"),
                new DepartmentGroupDto("Nhóm Tài chính & Kế toán", "Finance & Accounting", "Finance", 2,
                        "Bao gồm Phòng Tài chính, Phòng Kế toán"),
                new DepartmentGroupDto("Nhóm Kinh doanh & Tiếp thị", "Sales & Marketing", "Sales", 3,
                        "Bao gồm Phòng Kinh doanh, Phòng Tiếp thị, Phòng Quan hệ công chúng, Phòng Phát triển kinh doanh"),
                new DepartmentGroupDto("Nhóm Công nghệ Thông tin", "Information Technology", "IT", 4,
                        "Bao gồm Phòng IT, Phòng Hỗ trợ kỹ thuật"),
                new DepartmentGroupDto("Nhóm Nghiên cứu & Phát triển", "Research & Development", "R&D", 5,
                        "Bao gồm Phòng Nghiên cứu và Phát triển, Phòng Kiểm soát Chất lượng, Phòng Phát triển sản phẩm"),
                new DepartmentGroupDto("Nhóm Vận hành", "Operations", "Operations", 6,
                        "Bao gồm Phòng Vận hành, Phòng Sản xuất, Phòng Cung ứng, Phòng Kế hoạch"),
                new DepartmentGroupDto("Nhóm Dịch vụ Khách hàng", "Customer Service", "CustomerService", 7,
                        "Bao gồm Phòng Dịch vụ Khách hàng, Phòng Hỗ trợ kỹ thuật"),
                new DepartmentGroupDto("Nhóm Pháp lý & Tuân thủ", "Legal & Compliance", "Legal", 8,
                        "Bao gồm Phòng Pháp lý, Phòng Kiểm toán nội bộ"),
                new DepartmentGroupDto("Nhóm Truyền thông & Quan hệ công chúng", "Communications & PR",
                        "Communications", 9, "Bao gồm Phòng Truyền thông, Phòng Quan hệ công chúng"),
                new DepartmentGroupDto("Nhóm An ninh & An toàn", "Security & Safety", "Security", 10,
                        "Bao gồm Phòng An ninh, Phòng An toàn lao động"));

        for (DepartmentGroupDto dto : defaultDtos) {
            DepartmentGroupDto existingDto = departmentGroupService.findByShortName(dto.getShortName());
            if (existingDto == null || existingDto.getId() == null) {
                departmentGroupService.saveOrUpdate(dto);
            }
        }
    }

    @Override
    public void setupDefaultDepartmentType() {
        List<DepartmentTypeDto> defaultDtos = Arrays.asList(
                new DepartmentTypeDto(1, "Nhân sự", "01", "Human Resources", "HR",
                        "Quản lý tuyển dụng, đào tạo, phúc lợi và các vấn đề liên quan đến nhân viên"),
                new DepartmentTypeDto(2, "Tài chính", "02", "Finance", "Finance",
                        "Quản lý tài chính, kế toán, ngân sách và báo cáo tài chính"),
                new DepartmentTypeDto(3, "Kế toán", "03", "Accounting", "Accounting",
                        "Quản lý sổ sách kế toán, thu chi và báo cáo tài chính chi tiết"),
                new DepartmentTypeDto(4, "Kinh doanh", "04", "Sales", "Sales",
                        "Phát triển khách hàng, bán hàng và đạt doanh thu"),
                new DepartmentTypeDto(5, "Tiếp thị", "05", "Marketing", "Marketing",
                        "Quản lý chiến lược tiếp thị, quảng cáo và thương hiệu"),
                new DepartmentTypeDto(6, "Công nghệ Thông tin", "06", "Information Technology", "IT",
                        "Phát triển và duy trì hệ thống công nghệ thông tin, hạ tầng mạng"),
                new DepartmentTypeDto(7, "Nghiên cứu & Phát triển", "07", "Research and Development", "R&D",
                        "Nghiên cứu, phát triển sản phẩm và cải tiến công nghệ"),
                new DepartmentTypeDto(8, "Vận hành", "08", "Operations", "Operations",
                        "Quản lý các hoạt động hàng ngày, đảm bảo hiệu suất hoạt động"),
                new DepartmentTypeDto(9, "Dịch vụ Khách hàng", "09", "Customer Service", "CustomerService",
                        "Hỗ trợ và chăm sóc khách hàng, giải quyết các khiếu nại"),
                new DepartmentTypeDto(10, "Pháp lý", "10", "Legal", "Legal",
                        "Quản lý các vấn đề pháp lý, hợp đồng, tuân thủ pháp luật"),
                new DepartmentTypeDto(11, "Cung ứng", "11", "Procurement", "Procurement",
                        "Quản lý việc mua sắm, cung ứng hàng hóa và dịch vụ"),
                new DepartmentTypeDto(12, "Hành chính", "12", "Administration", "Administration",
                        "Quản lý các công việc hành chính, văn phòng và hỗ trợ hoạt động chung"),
                new DepartmentTypeDto(13, "Sản xuất", "13", "Production", "Production",
                        "Chịu trách nhiệm sản xuất và chế tạo sản phẩm"),
                new DepartmentTypeDto(14, "Kiểm soát Chất lượng", "14", "Quality Control", "QC",
                        "Đảm bảo sản phẩm và dịch vụ đạt tiêu chuẩn chất lượng"),
                new DepartmentTypeDto(15, "Kế hoạch", "15", "Planning", "Planning",
                        "Quản lý kế hoạch sản xuất, phát triển chiến lược và lịch trình hoạt động"),
                new DepartmentTypeDto(16, "An ninh", "16", "Security", "Security",
                        "Đảm bảo an ninh, an toàn cho tổ chức và tài sản"),
                new DepartmentTypeDto(17, "Hỗ trợ kỹ thuật", "17", "Technical Support", "TechnicalSupport",
                        "Cung cấp hỗ trợ kỹ thuật cho khách hàng hoặc các phòng ban khác"),
                new DepartmentTypeDto(18, "Truyền thông", "18", "Public Relations/Communications", "Communications",
                        "Xây dựng và duy trì hình ảnh của công ty với công chúng, truyền thông đối ngoại"));

        for (DepartmentTypeDto dto : defaultDtos) {
            DepartmentTypeDto existingDto = departmentTypeService.findByShortName(dto.getShortName());
            if (existingDto == null || existingDto.getId() == null) {
                departmentTypeService.saveOrUpdate(dto);
            }
        }
    }

    @Override
    public void setupDefaultDepartment() {

        // Phòng nhân sự
        HRDepartmentDto hrMain = new HRDepartmentDto("Phòng Nhân Sự", "HR001", null, null, "1",
                null, new ArrayList<>(), "Quản lý nhân sự, tuyển dụng và đào tạo nhân viên, đảm bảo các chính sách lao động được thực thi.", "HR", "None",
                new Date(), null, "HR001", null, new Date(),
                "HR", 1, null, 1, null, departmentTypeService.findByShortName("HR"),
                departmentGroupService.findByShortName("HR"), new ArrayList<>(), "GMT+7");

        HRDepartmentDto recruitment = new HRDepartmentDto("Phòng Tuyển dụng", "HR001-1", hrMain, null, "1",
                null, new ArrayList<>(), "Chịu trách nhiệm tìm kiếm, phỏng vấn và tuyển chọn ứng viên phù hợp cho các vị trí trong công ty.", "HR", "None",
                new Date(), null, "HR001-1", null, new Date(),
                "REC", 1, "HR001", 1, null,
                departmentTypeService.findByShortName("HR"), departmentGroupService.findByShortName("HR"), new ArrayList<>(), "GMT+7");

        HRDepartmentDto training = new HRDepartmentDto("Phòng Đào tạo và Phát triển", "HR001-2", hrMain, null, "2",
                null, new ArrayList<>(), "Tổ chức các chương trình đào tạo để nâng cao kỹ năng và năng lực của nhân viên.", "HR", "None",
                new Date(), null, "HR001-2", null, new Date(),
                "TRN", 2, "HR001", 1, null, departmentTypeService.findByShortName("HR"),
                departmentGroupService.findByShortName("HR"), new ArrayList<>(), "GMT+7");

        HRDepartmentDto employeeManagement = new HRDepartmentDto("Phòng Quản lý nhân viên", "HR001-3", hrMain, null,
                "3", null, new ArrayList<>(), "Quản lý hồ sơ nhân viên, theo dõi sự phát triển nghề nghiệp và phúc lợi.", "HR", "None",
                new Date(), null, "HR001-3", null, new Date(),
                "EMG", 3, "HR001", 1, null, departmentTypeService.findByShortName("HR"),
                departmentGroupService.findByShortName("HR"), new ArrayList<>(), "GMT+7");

        hrMain.setSubDepartment(Arrays.asList(recruitment, training, employeeManagement));
        hrMain.setChildren(Arrays.asList(recruitment, training, employeeManagement));

        // Phòng Kế Toán
        HRDepartmentDto accountingMain = new HRDepartmentDto("Phòng Kế Toán", "ACC002", null, null, "2",
                null, new ArrayList<>(), "Quản lý tài chính, lập báo cáo tài chính, kiểm soát chi phí và thực hiện các nhiệm vụ kế toán.", "ACC", "None",
                new Date(), null, "ACC002", null, new Date(),
                "ACC", 2, null, 2, null, departmentTypeService.findByShortName("Accounting"),
                departmentGroupService.findByShortName("Accounting"), new ArrayList<>(), "GMT+7");

        HRDepartmentDto financialAccounting = new HRDepartmentDto("Phòng Kế toán tài chính", "ACC002-1", accountingMain, null, "1",
                null, new ArrayList<>(), "Lập báo cáo tài chính và kiểm soát các giao dịch tài chính hàng ngày.", "ACC", "None",
                new Date(), null, "ACC002-1", null, new Date(),
                "FAC", 1, "ACC002", 2, null, departmentTypeService.findByShortName("Accounting"),
                departmentGroupService.findByShortName("Accounting"), new ArrayList<>(), "GMT+7");

        HRDepartmentDto internalAudit = new HRDepartmentDto("Phòng Kiểm toán nội bộ", "ACC002-2", accountingMain, null, "2",
                null, new ArrayList<>(), "Đánh giá và kiểm tra tính chính xác của các báo cáo tài chính và quy trình nội bộ.", "ACC", "None",
                new Date(), null, "ACC002-2", null, new Date(),
                "IAU", 2, "ACC002", 2, null, departmentTypeService.findByShortName("Accounting"),
                departmentGroupService.findByShortName("Accounting"), new ArrayList<>(), "GMT+7");

        HRDepartmentDto managementAccounting = new HRDepartmentDto("Phòng Kế toán quản trị", "ACC002-3", accountingMain,
                null, "3", null, new ArrayList<>(),
                "Phân tích dữ liệu tài chính để hỗ trợ quyết định chiến lược cho công ty.", "ACC", "None", new Date(),
                null, "ACC002-3", null, new Date(), "MAC", 3, "ACC002", 2, null,
                departmentTypeService.findByShortName("Accounting"),
                departmentGroupService.findByShortName("Accounting"), new ArrayList<>(), "GMT+7");

        accountingMain.setSubDepartment(Arrays.asList(financialAccounting, internalAudit, managementAccounting));
        accountingMain.setChildren(Arrays.asList(financialAccounting, internalAudit, managementAccounting));

        // Phòng Kinh Doanh
        HRDepartmentDto salesMain = new HRDepartmentDto("Phòng Kinh Doanh", "SALES003", null, null, "3", null,
                new ArrayList<>(),
                "Phát triển và quản lý hoạt động kinh doanh, chăm sóc khách hàng, thúc đẩy doanh thu bán hàng.",
                "SALES", "None", new Date(), null, "SALES003", null, new Date(),
                "SAL", 3, null, 3, null, departmentTypeService.findByShortName("Sales"),
                departmentGroupService.findByShortName("Sales"), new ArrayList<>(), "GMT+7");

        HRDepartmentDto salesDepartment = new HRDepartmentDto("Phòng Bán hàng", "SALES003-1", salesMain, null, "1",
                null, new ArrayList<>(),
                "Tiến hành bán hàng và phát triển mối quan hệ với khách hàng để đạt được doanh số.", "SALES", "None",
                new Date(), null, "SALES003-1", null, new Date(), "SD", 1, "SALES003", 3, null,
                departmentTypeService.findByShortName("Sales"),
                departmentGroupService.findByShortName("Sales"), new ArrayList<>(), "GMT+7");

        HRDepartmentDto customerSupport = new HRDepartmentDto("Phòng Hỗ trợ khách hàng", "SALES003-2", salesMain, null,
                "2", null, new ArrayList<>(),
                "Cung cấp dịch vụ hỗ trợ và giải quyết vấn đề cho khách hàng, đảm bảo sự hài lòng.", "SALES", "None",
                new Date(), null, "SALES003-2", null, new Date(), "CS", 2, "SALES003", 3, null,
                departmentTypeService.findByShortName("Sales"), departmentGroupService.findByShortName("Sales"),
                new ArrayList<>(), "GMT+7");

        HRDepartmentDto marketDevelopment = new HRDepartmentDto("Phòng Phát triển thị trường", "SALES003-3", salesMain,
                null, "3", null, new ArrayList<>(),
                "Nghiên cứu và phát triển chiến lược để mở rộng thị trường và tăng trưởng doanh thu.", "SALES", "None",
                new Date(), null, "SALES003-3", null, new Date(), "MD", 3, "SALES003", 3, null,
                departmentTypeService.findByShortName("Sales"), departmentGroupService.findByShortName("Sales"),
                new ArrayList<>(), "GMT+7");

        salesMain.setSubDepartment(Arrays.asList(salesDepartment, customerSupport, marketDevelopment));
        salesMain.setChildren(Arrays.asList(salesDepartment, customerSupport, marketDevelopment));

        // Phòng Công Nghệ Thông Tin
        HRDepartmentDto itMain = new HRDepartmentDto("Phòng Công Nghệ Thông Tin", "IT004", null, null, "4", null,
                new ArrayList<>(),
                "Phát triển và bảo trì hệ thống phần mềm, quản lý hạ tầng CNTT, đảm bảo an ninh mạng.", "IT", "None",
                new Date(), null, "IT004", null, new Date(), "IT", 4, null, 4, null,
                departmentTypeService.findByShortName("IT"), departmentGroupService.findByShortName("IT"),
                new ArrayList<>(), "GMT+7");

        HRDepartmentDto softwareDevelopment = new HRDepartmentDto("Phòng Phát triển phần mềm", "IT004-1", itMain, null,
                "1", null, new ArrayList<>(),
                "Thiết kế, phát triển và bảo trì các ứng dụng phần mềm phục vụ nhu cầu của công ty.", "IT", "None",
                new Date(), null, "IT004-1", null, new Date(), "SWD", 1, "IT004", 4, null,
                departmentTypeService.findByShortName("IT"), departmentGroupService.findByShortName("IT"),
                new ArrayList<>(), "GMT+7");

        HRDepartmentDto systemAdministration = new HRDepartmentDto("Phòng Quản trị hệ thống", "IT004-2", itMain, null,
                "2", null, new ArrayList<>(), "Quản lý hạ tầng CNTT, bao gồm máy chủ, mạng và các thiết bị công nghệ.",
                "IT", "None", new Date(), null, "IT004-2", null, new Date(), "SA", 2, "IT004", 4, null,
                departmentTypeService.findByShortName("IT"), departmentGroupService.findByShortName("IT"),
                new ArrayList<>(), "GMT+7");

        HRDepartmentDto infoSecurity = new HRDepartmentDto("Phòng Bảo mật thông tin", "IT004-3", itMain, null, "3",
                null, new ArrayList<>(), "Đảm bảo an toàn và bảo mật cho dữ liệu và hệ thống thông tin của công ty.",
                "IT", "None", new Date(), null, "IT004-3", null, new Date(), "INFSEC", 3, "IT004", 4, null,
                departmentTypeService.findByShortName("IT"), departmentGroupService.findByShortName("IT"),
                new ArrayList<>(), "GMT+7");

        itMain.setSubDepartment(Arrays.asList(softwareDevelopment, systemAdministration, infoSecurity));
        itMain.setChildren(Arrays.asList(softwareDevelopment, systemAdministration, infoSecurity));

        // Phòng Marketing
        HRDepartmentDto marketingMain = new HRDepartmentDto("Phòng Marketing", "MKT005", null, null, "5", null,
                new ArrayList<>(),
                "Lên chiến lược quảng bá sản phẩm, quản lý quan hệ công chúng, nghiên cứu và phân tích thị trường.",
                "MKT", "None", new Date(), null, "MKT005", null, new Date(), "MKT", 5, null, 5, null,
                departmentTypeService.findByShortName("Marketing"), departmentGroupService.findByShortName("Marketing"),
                new ArrayList<>(), "GMT+7");

        HRDepartmentDto advertising = new HRDepartmentDto("Phòng Quảng cáo", "MKT005-1", marketingMain, null, "1", null,
                new ArrayList<>(), "Tạo và thực hiện các chiến dịch quảng cáo để tăng cường nhận thức về thương hiệu.",
                "MKT", "None", new Date(), null, "MKT005-1", null, new Date(), "ADV", 1, "MKT005", 5, null,
                departmentTypeService.findByShortName("Marketing"), departmentGroupService.findByShortName("Marketing"),
                new ArrayList<>(), "GMT+7");

        HRDepartmentDto publicRelations = new HRDepartmentDto("Phòng Quan hệ công chúng", "MKT005-2", marketingMain,
                null, "2", null, new ArrayList<>(),
                "Xây dựng và duy trì mối quan hệ tốt với báo chí và cộng đồng để thúc đẩy hình ảnh công ty.", "MKT",
                "None", new Date(), null, "MKT005-2", null, new Date(), "PR", 2, "MKT005", 5, null,
                departmentTypeService.findByShortName("Marketing"), departmentGroupService.findByShortName("Marketing"),
                new ArrayList<>(), "GMT+7");

        HRDepartmentDto marketResearch = new HRDepartmentDto("Phòng Nghiên cứu thị trường", "MKT005-3", marketingMain,
                null, "3", null, new ArrayList<>(),
                "Thực hiện nghiên cứu để hiểu rõ thị trường, nhu cầu khách hàng và xu hướng ngành.", "MKT", "None",
                new Date(), null, "MKT005-3", null, new Date(), "MR", 3, "MKT005", 5, null,
                departmentTypeService.findByShortName("Marketing"), departmentGroupService.findByShortName("Marketing"),
                new ArrayList<>(), "GMT+7");

        marketingMain.setSubDepartment(Arrays.asList(advertising, publicRelations, marketResearch));
        marketingMain.setChildren(Arrays.asList(advertising, publicRelations, marketResearch));

        // Phòng Sản Xuất
        HRDepartmentDto productionMain = new HRDepartmentDto("Phòng Sản Xuất", "PROD006", null, null, "6", null,
                new ArrayList<>(),
                "Quản lý sản xuất, giám sát quy trình sản xuất, đảm bảo sản phẩm đạt chất lượng yêu cầu.", "PROD",
                "None", new Date(), null, "PROD006", null, new Date(), "PROD", 6, null, 6, null,
                departmentTypeService.findByShortName("Production"),
                departmentGroupService.findByShortName("Production"), new ArrayList<>(), "GMT+7");

        HRDepartmentDto productionLineManagement = new HRDepartmentDto("Phòng Quản lý dây chuyền sản xuất", "PROD006-1",
                productionMain, null, "1", null, new ArrayList<>(),
                "Giám sát và tối ưu hóa quy trình sản xuất để đảm bảo hiệu suất và chất lượng.", "PROD", "None",
                new Date(), null, "PROD006-1", null, new Date(), "PLM", 1, "PROD006", 6, null,
                departmentTypeService.findByShortName("Production"),
                departmentGroupService.findByShortName("Production"), new ArrayList<>(), "GMT+7");

        HRDepartmentDto qualityControl = new HRDepartmentDto("Phòng Kiểm soát chất lượng", "PROD006-2", productionMain,
                null, "2", null, new ArrayList<>(),
                "Thực hiện kiểm tra và đánh giá sản phẩm để đảm bảo đáp ứng tiêu chuẩn chất lượng.", "PROD", "None",
                new Date(), null, "PROD006-2", null, new Date(), "QC", 2, "PROD006", 6, null,
                departmentTypeService.findByShortName("Production"),
                departmentGroupService.findByShortName("Production"), new ArrayList<>(), "GMT+7");

        HRDepartmentDto productDevelopment = new HRDepartmentDto("Phòng Phát triển sản phẩm", "PROD006-3",
                productionMain, null, "3", null, new ArrayList<>(),
                "Nghiên cứu và phát triển các sản phẩm mới để đáp ứng nhu cầu thị trường.", "PROD", "None", new Date(),
                null, "PROD006-3", null, new Date(), "PD", 3, "PROD006", 6, null,
                departmentTypeService.findByShortName("Production"),
                departmentGroupService.findByShortName("Production"), new ArrayList<>(), "GMT+7");

        productionMain.setSubDepartment(Arrays.asList(productionLineManagement, qualityControl, productDevelopment));
        productionMain.setChildren(Arrays.asList(productionLineManagement, qualityControl, productDevelopment));

        List<HRDepartmentDto> defaultDtos = Arrays.asList(hrMain, recruitment, training, employeeManagement,
                accountingMain, financialAccounting, internalAudit, managementAccounting, salesMain, salesDepartment,
                customerSupport, marketDevelopment, itMain, softwareDevelopment, systemAdministration, infoSecurity,
                marketingMain, advertising, publicRelations, marketResearch, productionMain, productionLineManagement,
                qualityControl, productDevelopment);

        for (HRDepartmentDto dto : defaultDtos) {
            Boolean isExisting = departmentService.checkCode(null, dto.getCode());
            if (isExisting == null || !isExisting) {
                departmentService.saveOrUpdate(dto);
            }
        }
    }

    @Override
    public void setupDefaultContractType() {
        List<ContractTypeDto> defaultDtos = Arrays.asList(
                new ContractTypeDto("Hợp đồng thử việc",
                        HrConstants.ContractTypeCode.PROBATION.getValue(), "vi",
                        "Hợp đồng thử việc", null),
                new ContractTypeDto("Hợp đồng lao động xác định thời hạn",
                        HrConstants.ContractTypeCode.DETERMINE_THE_DEADLINE.getValue(), "vi",
                        "Hợp đồng lao động xác định thời hạn", null),
                new ContractTypeDto("Hợp đồng lao động không xác định thời hạn",
                        HrConstants.ContractTypeCode.UNSPECIFIED_DEADLINE.getValue(), "vi",
                        "Hợp đồng lao động không xác định thời hạn", null),
                new ContractTypeDto("Hợp đồng lao động khác",
                        HrConstants.ContractTypeCode.OTHER.getValue(), "vi",
                        "Hợp đồng lao động khác", null)
        );

        for (ContractTypeDto dto : defaultDtos) {
            Boolean isValid = contractTypeService.isValidCode(dto);

            if (isValid != null && isValid) {
                contractTypeService.saveOrUpdate(dto, null);
            }
        }
    }

    @Override
    public void setupDefaultAddendumType() {
        List<AddendumTypeDto> defaultDtos = Arrays.asList(
                new AddendumTypeDto("Hình ảnh", "01", "Tài liệu hình ảnh liên quan đến nghiên cứu."),
                new AddendumTypeDto("Biểu đồ", "02", "Các biểu đồ thể hiện dữ liệu một cách trực quan."),
                new AddendumTypeDto("Đồ thị", "03", "Các đồ thị thể hiện mối quan hệ giữa các yếu tố."),
                new AddendumTypeDto("Bảng dữ liệu thô", "04", "Tập hợp dữ liệu thô sử dụng trong nghiên cứu."),
                new AddendumTypeDto("Ghi chú", "05", "Các ghi chú quan trọng liên quan đến nội dung."),
                new AddendumTypeDto("Phiếu câu hỏi khảo sát", "06", "Bảng câu hỏi được sử dụng trong khảo sát."),
                new AddendumTypeDto("Tác phẩm độc lập", "07", "Các tác phẩm không phụ thuộc vào nghiên cứu."),
                new AddendumTypeDto("Trích đoạn lớn từ sách", "08", "Các trích đoạn đáng chú ý từ sách liên quan."),
                new AddendumTypeDto("Video", "09", "Tài liệu hình ảnh động liên quan đến nghiên cứu."),
                new AddendumTypeDto("Âm thanh", "10", "Các bản ghi âm hoặc phỏng vấn trong nghiên cứu."),
                new AddendumTypeDto("Bản đồ", "11", "Các bản đồ hoặc hình ảnh địa lý có liên quan."),
                new AddendumTypeDto("Biên bản cuộc họp", "12", "Ghi chép các cuộc họp liên quan đến dự án."),
                new AddendumTypeDto("Tài liệu tham khảo", "13", "Danh sách các tài liệu đã sử dụng trong nghiên cứu."));

        for (AddendumTypeDto dto : defaultDtos) {
            AddendumTypeDto existingDto = addendumTypeService.findByCode(dto.getCode());
            if (existingDto == null || existingDto.getId() == null) {
                addendumTypeService.saveOrUpdate(dto);
            }
        }
    }

    @Override
    public void setupDefaultStaffType() {
        List<StaffTypeDto> defaultDtos = Arrays.asList(new StaffTypeDto("Nhân viên cao cấp", "ET01", "Cấp cao"),
                new StaffTypeDto("Nhân viên thời vụ", "ET02", "Thời vụ"),
                new StaffTypeDto("Nhân viên chính thức", "ET03", "Nhân viên có hợp đồng dài hạn."),
                new StaffTypeDto("Nhân viên thử việc", "ET04", "Nhân viên đang trong thời gian thử việc."),
                new StaffTypeDto("Nhân viên bán thời gian", "ET05", "Nhân viên làm việc không toàn thời gian."),
                new StaffTypeDto("Nhân viên hợp đồng", "ET06", "Nhân viên làm việc theo hợp đồng cụ thể."),
                new StaffTypeDto("Nhân viên tạm thời", "ET07", "Nhân viên làm việc ngắn hạn theo nhu cầu."));

        for (StaffTypeDto dto : defaultDtos) {
            StaffTypeDto existingDto = staffTypeService.findByCode(dto.getCode());
            if (existingDto == null || existingDto.getId() == null) {
                staffTypeService.saveOrUpdate(dto);
            }
        }
    }

    @Override
    public void setupDefaultPositionRole() {
        List<PositionRoleDto> defaultDtos = Arrays.asList(new PositionRoleDto("Chief Executive Officer",
                        "Giám đốc điều hành", "CEO",
                        "Người đứng đầu tổ chức, chịu trách nhiệm điều hành và ra quyết định chiến lược cho toàn bộ doanh nghiệp."),
                new PositionRoleDto("Chief Financial Officer", "Giám đốc tài chính", "CFO",
                        "Phụ trách tài chính, quản lý các hoạt động liên quan đến ngân sách, kế toán, kiểm toán, và chiến lược tài chính."),
                new PositionRoleDto("Chief Operating Officer", "Giám đốc vận hành", "COO",
                        "Quản lý các hoạt động hàng ngày của doanh nghiệp, đảm bảo rằng các bộ phận hoạt động trơn tru và hiệu quả."),
                new PositionRoleDto("Chief Technology Officer", "Giám đốc công nghệ", "CTO",
                        "Chịu trách nhiệm về công nghệ, chiến lược kỹ thuật, và sự đổi mới trong tổ chức."),
                new PositionRoleDto("Manager", "Quản lý", "MGR",
                        "Quản lý một bộ phận hoặc phòng ban cụ thể, chịu trách nhiệm giám sát, đào tạo và đánh giá hiệu suất nhân viên trong nhóm của mình."),
                new PositionRoleDto("Team Leader", "Trưởng nhóm", "TL",
                        "Lãnh đạo một nhóm nhỏ, thường phụ trách điều phối công việc hằng ngày, hỗ trợ các thành viên trong nhóm, và báo cáo với quản lý."),
                new PositionRoleDto("Supervisor", "Giám sát", "SV",
                        "Theo dõi và kiểm soát công việc của nhân viên cấp dưới, đảm bảo rằng các hoạt động được thực hiện theo kế hoạch và tiêu chuẩn."),
                new PositionRoleDto("Employee", "Nhân viên", "EMP",
                        "Nhân viên có quyền hạn và trách nhiệm ở cấp độ cơ bản nhất, thực hiện các nhiệm vụ và công việc hàng ngày theo chỉ đạo của cấp trên."),
                new PositionRoleDto("Intern", "Thực tập sinh", "INT",
                        "Thực tập sinh thường có quyền hạn hạn chế, chủ yếu tham gia hỗ trợ các công việc dưới sự giám sát của nhân viên hoặc quản lý."),
                new PositionRoleDto("Human Resources", "Nhân sự", "HR",
                        "Quản lý nhân sự, có quyền liên quan đến việc tuyển dụng, đào tạo, quản lý phúc lợi, và xử lý các vấn đề liên quan đến nhân viên."));

        for (PositionRoleDto dto : defaultDtos) {
            PositionRoleDto existingDto = positionRoleService.findByShortName(dto.getShortName());
            if (existingDto == null || existingDto.getId() == null) {
                positionRoleService.savePositionRole(dto);
            }
        }
    }

    @Override
    public void setupDefaultEmployeeStatus() {
        List<EmployeeStatusDto> defaultDtos = Arrays.asList(
                new EmployeeStatusDto(HrConstants.EmployeeStatusCodeEnum.WORKING.getValue(), HrConstants.EmployeeStatusCodeEnum.WORKING.getDescription(), "Tiếng Việt", true),
//                new EmployeeStatusDto(HrConstants.EmployeeStatusCodeEnum.WAITING_RECEPTION.getValue(), HrConstants.EmployeeStatusCodeEnum.WAITING_RECEPTION.getDescription(), "Tiếng Việt", true),
                new EmployeeStatusDto(HrConstants.EmployeeStatusCodeEnum.QUITED.getValue(), HrConstants.EmployeeStatusCodeEnum.QUITED.getDescription(), "Tiếng Việt", true),
//                new EmployeeStatusDto(HrConstants.EmployeeStatusCodeEnum.NOT_RECEIVE_JOB.getValue(), HrConstants.EmployeeStatusCodeEnum.NOT_RECEIVE_JOB.getDescription(), "Tiếng Việt", true),
//                new EmployeeStatusDto(HrConstants.EmployeeStatusCodeEnum.BACK_TO_WORK.getValue(), HrConstants.EmployeeStatusCodeEnum.BACK_TO_WORK.getDescription(), "Tiếng Việt", true),
//                new EmployeeStatusDto(HrConstants.EmployeeStatusCodeEnum.TEMPORARY_PAUSE.getValue(), HrConstants.EmployeeStatusCodeEnum.TEMPORARY_PAUSE.getDescription(), "Tiếng Việt", true),
                new EmployeeStatusDto(HrConstants.EmployeeStatusCodeEnum.SPECIAL_LEAVE.getValue(), HrConstants.EmployeeStatusCodeEnum.SPECIAL_LEAVE.getDescription(), "Tiếng Việt", true),
                new EmployeeStatusDto(HrConstants.EmployeeStatusCodeEnum.UNPAID_LEAVE.getValue(), HrConstants.EmployeeStatusCodeEnum.UNPAID_LEAVE.getDescription(), "Tiếng Việt", true)
        );

        for (EmployeeStatusDto dto : defaultDtos) {
            Boolean isExisting = employeeStatusService.checkCode(null, dto.getCode());
            if (isExisting == null || !isExisting) {
                employeeStatusService.saveOrUpdate(dto);
            }
        }
    }

    @Override
    public void setupDefaultRewardForm() {
        List<RewardFormDto> defaultDtos = Arrays.asList(
                new RewardFormDto("Thưởng KPI", "Tiếng Việt", "01", 1, 2, "Thưởng theo hiệu suất KPI hàng năm", null,
                        null),
                new RewardFormDto("Huân chương Lao động hạng Nhất", "Tiếng Việt", "02", 1, 1,
                        "Huân chương danh giá dành cho lao động xuất sắc", null, null),
                new RewardFormDto("Huân chương Lao động hạng Nhì", "Tiếng Việt", "03", 1, 1,
                        "Huân chương dành cho lao động có thành tích cao", null, null),
                new RewardFormDto("Huân chương Lao động hạng Ba", "Tiếng Việt", "04", 1, 1,
                        "Huân chương cho những cá nhân có đóng góp đáng kể", null, null),
                new RewardFormDto("Bằng khen Thủ tướng Chính phủ", "Tiếng Việt", "05", 2, 1,
                        "Bằng khen do Thủ tướng trao tặng", null, null),
                new RewardFormDto("Bằng khen Chủ tịch nước", "Tiếng Việt", "06", 2, 1,
                        "Bằng khen dành cho cá nhân có công trạng nổi bật", null, null),
                new RewardFormDto("Bằng khen Bộ trưởng Bộ Giáo dục", "Tiếng Việt", "07", 2, 1,
                        "Khen thưởng cho những cá nhân xuất sắc trong ngành giáo dục", null, null),
                new RewardFormDto("Chiến sĩ thi đua toàn quốc", "Tiếng Việt", "08", 1, 1,
                        "Danh hiệu cao quý cho chiến sĩ thi đua cấp quốc gia", null, null),
                new RewardFormDto("Chiến sĩ thi đua cấp Bộ", "Tiếng Việt", "09", 1, 2,
                        "Danh hiệu thi đua dành cho chiến sĩ xuất sắc cấp Bộ", null, null),
                new RewardFormDto("Huy chương Chiến công hạng Nhất", "Tiếng Việt", "10", 1, 1,
                        "Huy chương dành cho những cá nhân có chiến công nổi bật", null, null),
                new RewardFormDto("Huy chương Chiến công hạng Nhì", "Tiếng Việt", "11", 1, 1,
                        "Huy chương dành cho những cá nhân có chiến công xuất sắc", null, null),
                new RewardFormDto("Huy chương Chiến công hạng Ba", "Tiếng Việt", "12", 1, 1,
                        "Huy chương dành cho những cá nhân có chiến công tốt", null, null),
                new RewardFormDto("Huân chương Bảo vệ Tổ quốc", "Tiếng Việt", "13", 1, 1,
                        "Khen thưởng cho những cá nhân có đóng góp bảo vệ Tổ quốc", null, null),
                new RewardFormDto("Giải thưởng Sáng tạo Khoa học Kỹ thuật", "Tiếng Việt", "14", 2, 1,
                        "Khen thưởng cho những phát minh, sáng kiến khoa học kỹ thuật", null, null),
                new RewardFormDto("Thưởng Sáng kiến cải tiến", "Tiếng Việt", "15", 2, 2,
                        "Khen thưởng cho cá nhân, tập thể có sáng kiến cải tiến trong công việc", null, null),
                new RewardFormDto("Thưởng Năng suất lao động", "Tiếng Việt", "16", 2, 2,
                        "Khen thưởng cho người có năng suất lao động vượt trội", null, null));

        for (RewardFormDto dto : defaultDtos) {
            Boolean isExisting = rewardFormService.checkCode(null, dto.getCode());
            if (isExisting == null || !isExisting) {
                rewardFormService.saveOrUpdate(dto);
            }
        }
    }

    @Override
    public void setupDefaultHrSpeciality() {
        List<HrSpecialityDto> specialties = new ArrayList<>();

        specialties.add(new HrSpecialityDto("CNTT", "Công nghệ thông tin", "Information Technology"));
        specialties.add(new HrSpecialityDto("QTKD", "Quản trị kinh doanh", "Business Administration"));
        specialties.add(new HrSpecialityDto("KT", "Kế toán", "Accounting"));
        specialties.add(new HrSpecialityDto("TC", "Tài chính", "Finance"));
        specialties.add(new HrSpecialityDto("MKT", "Marketing", "Marketing"));
        specialties.add(new HrSpecialityDto("KT", "Kinh tế", "Economics"));
        specialties.add(new HrSpecialityDto("LT", "Luật", "Law"));
        specialties.add(new HrSpecialityDto("XD", "Xây dựng", "Civil Engineering"));
        specialties.add(new HrSpecialityDto("DTVT", "Điện tử viễn thông", "Telecommunications"));
        specialties.add(new HrSpecialityDto("CK", "Cơ khí", "Mechanical Engineering"));
        specialties.add(new HrSpecialityDto("HC", "Hóa học", "Chemistry"));
        specialties.add(new HrSpecialityDto("SH", "Sinh học", "Biology"));
        specialties.add(new HrSpecialityDto("VL", "Vật lý", "Physics"));
        specialties.add(new HrSpecialityDto("NV", "Ngữ văn", "Literature"));
        specialties.add(new HrSpecialityDto("LS", "Lịch sử", "History"));
        specialties.add(new HrSpecialityDto("DL", "Địa lý", "Geography"));
        specialties.add(new HrSpecialityDto("GDTC", "Giáo dục thể chất", "Physical Education"));
        specialties.add(new HrSpecialityDto("AN", "Âm nhạc", "Music"));
        specialties.add(new HrSpecialityDto("NT", "Nghệ thuật", "Art"));
        specialties.add(new HrSpecialityDto("TKDH", "Thiết kế đồ họa", "Graphic Design"));
        specialties.add(new HrSpecialityDto("KHMT", "Khoa học máy tính", "Computer Science"));
        specialties.add(new HrSpecialityDto("QLDA", "Quản lý dự án", "Project Management"));
        specialties.add(new HrSpecialityDto("QTNH", "Quản trị nhân sự", "Human Resource Management"));
        specialties.add(new HrSpecialityDto("KHDT", "Khoa học dữ liệu", "Data Science"));
        specialties.add(new HrSpecialityDto("ATT", "An toàn thông tin", "Information Security"));
        specialties.add(new HrSpecialityDto("CNSH", "Công nghệ sinh học", "Biotechnology"));
        specialties.add(new HrSpecialityDto("KTPM", "Kỹ thuật phần mềm", "Software Engineering"));
        specialties.add(new HrSpecialityDto("HTTT", "Hệ thống thông tin", "Information Systems"));
        specialties.add(new HrSpecialityDto("KTĐ", "Kỹ thuật điện", "Electrical Engineering"));
        specialties.add(new HrSpecialityDto("KTMT", "Kỹ thuật mạng", "Network Engineering"));
        specialties.add(new HrSpecialityDto("CBTP", "Chế biến thực phẩm", "Food Processing"));
        specialties.add(new HrSpecialityDto("DN", "Dinh dưỡng", "Nutrition"));
        specialties.add(new HrSpecialityDto("YTCC", "Y tế công cộng", "Public Health"));
        specialties.add(new HrSpecialityDto("D", "Dược", "Pharmacy"));
        specialties.add(new HrSpecialityDto("KSSH", "Khoa học sức khỏe", "Health Sciences"));
        specialties.add(new HrSpecialityDto("QLMT", "Quản lý môi trường", "Environmental Management"));
        specialties.add(new HrSpecialityDto("KTMT", "Kỹ thuật cơ điện tử", "Mechatronics Engineering"));
        specialties.add(new HrSpecialityDto("VT", "Vận tải", "Transport"));
        specialties.add(new HrSpecialityDto("LG", "Logistics", "Logistics"));
        specialties.add(new HrSpecialityDto("QLCC", "Quản lý chuỗi cung ứng", "Supply Chain Management"));
        specialties.add(new HrSpecialityDto("BĐS", "Bất động sản", "Real Estate"));
        specialties.add(new HrSpecialityDto("DU", "Du lịch", "Tourism"));
        specialties.add(new HrSpecialityDto("QTNN", "Quản trị nhà nước", "Public Administration"));
        specialties.add(new HrSpecialityDto("KDQT", "Kinh doanh quốc tế", "International Business"));
        specialties.add(new HrSpecialityDto("CNGT", "Công nghệ giao thông", "Traffic Engineering"));
        specialties.add(
                new HrSpecialityDto("MNSH", "Mạng máy tính và truyền thông", "Computer Networks and Communications"));
        specialties.add(new HrSpecialityDto("CNSH", "Công nghệ thông tin", "Information Technology"));
        specialties.add(new HrSpecialityDto("TKT", "Tài chính kế toán", "Financial Accounting"));
        specialties.add(new HrSpecialityDto("QTC", "Quản trị công nghệ", "Technology Management"));
        specialties.add(new HrSpecialityDto("KTPT", "Kỹ thuật chế tạo", "Manufacturing Engineering"));
        specialties.add(new HrSpecialityDto("KTNN", "Kỹ thuật môi trường", "Environmental Engineering"));
        specialties.add(new HrSpecialityDto("KTHT", "Kỹ thuật hệ thống", "Systems Engineering"));
        specialties.add(new HrSpecialityDto("MT", "Marketing kỹ thuật số", "Digital Marketing"));
        specialties.add(new HrSpecialityDto("HTQT", "Hệ thống thông tin quản lý", "Management Information Systems"));
        specialties.add(new HrSpecialityDto("KTL", "Kỹ thuật điện tử", "Electronics Engineering"));
        specialties.add(new HrSpecialityDto("CTCT", "Công nghệ chế tạo", "Manufacturing Technology"));
        specialties.add(new HrSpecialityDto("HTXH", "Khoa học xã hội", "Social Sciences"));
        specialties.add(new HrSpecialityDto("TKT", "Thiết kế thời trang", "Fashion Design"));
        specialties.add(new HrSpecialityDto("PCL", "Phân tích dữ liệu", "Data Analytics"));
        specialties.add(new HrSpecialityDto("GD", "Giáo dục", "Education"));
        specialties.add(new HrSpecialityDto("QHVN", "Quản lý văn hóa", "Cultural Management"));
        specialties.add(new HrSpecialityDto("NVQ", "Nghệ thuật ẩm thực", "Culinary Arts"));
        specialties.add(new HrSpecialityDto("MTQT", "Marketing quốc tế", "International Marketing"));
        specialties.add(new HrSpecialityDto("TT", "Truyền thông", "Communication"));
        specialties.add(new HrSpecialityDto("CSD", "Công nghệ xây dựng", "Construction Technology"));
        specialties.add(new HrSpecialityDto("TTSP", "Truyền thông đa phương tiện", "Multimedia Communication"));
        specialties.add(new HrSpecialityDto("QLCL", "Quản lý chất lượng", "Quality Management"));
        specialties.add(new HrSpecialityDto("CSKH", "Chăm sóc khách hàng", "Customer Care"));
        specialties.add(new HrSpecialityDto("TH", "Tâm lý học", "Psychology"));
        specialties.add(new HrSpecialityDto("KHMT", "Khoa học môi trường", "Environmental Science"));
        specialties.add(new HrSpecialityDto("KHKT", "Khoa học kỹ thuật", "Technical Science"));
        specialties.add(new HrSpecialityDto("QLTT", "Quản lý thương hiệu", "Brand Management"));
        specialties.add(new HrSpecialityDto("DGH", "Đồ họa", "Graphic Arts"));
        specialties.add(new HrSpecialityDto("PT", "Phát triển", "Development"));
        specialties.add(new HrSpecialityDto("TL", "Thư ký", "Secretarial Studies"));
        specialties.add(new HrSpecialityDto("TT", "Truyền thông xã hội", "Social Media Communication"));
        specialties.add(new HrSpecialityDto("QLCL", "Quản lý chuỗi cung ứng", "Supply Chain Management"));
        specialties.add(new HrSpecialityDto("KHCN", "Khoa học công nghệ", "Science and Technology"));
        specialties.add(new HrSpecialityDto("QLNN", "Quản lý nhà nước", "State Management"));
        specialties.add(new HrSpecialityDto("CCT", "Công nghệ chế tạo", "Manufacturing Technology"));

        for (HrSpecialityDto dto : specialties) {
            HrSpecialityDto existedItem = hrSpecialityService.findByCode(dto.getCode());
            if (existedItem == null) {
                hrSpecialityService.saveSpeciality(dto, null);
            }
        }
    }

    @Override
    public void setupDefaultCertificate() {
        List<CertificateDto> certificates = new ArrayList<>();

        // Các chứng chỉ học thuật (Loại khác hoặc chưa xác định)
        certificates.add(new CertificateDto("TH001", "Chứng chỉ đại học", 2));
        certificates.add(new CertificateDto("TH002", "Chứng chỉ thạc sĩ", 2));
        certificates.add(new CertificateDto("TH003", "Chứng chỉ tiến sĩ", 2));
        certificates.add(new CertificateDto("TH004", "Chứng chỉ cao đẳng", 2));
        certificates.add(new CertificateDto("TH005", "Chứng chỉ nghề", 2));

        // Các chứng chỉ tiếng Anh (Type 1 - Chứng chỉ tiếng Anh)
        certificates.add(new CertificateDto("EN001", "Chứng chỉ TOEFL", 1));
        certificates.add(new CertificateDto("EN002", "Chứng chỉ IELTS", 1));
        certificates.add(new CertificateDto("EN003", "Chứng chỉ Cambridge", 1));
        certificates.add(new CertificateDto("EN004", "Chứng chỉ TOEIC", 1));
        certificates.add(new CertificateDto("EN005", "Chứng chỉ tiếng Anh giao tiếp", 1));

        // Các chứng chỉ liên quan đến IT (Type 4 - Trình độ tin học)
        certificates.add(new CertificateDto("IT001", "Chứng chỉ Microsoft Certified", 4));
        certificates.add(new CertificateDto("IT002", "Chứng chỉ Cisco Certified", 4));
        certificates.add(new CertificateDto("IT003", "Chứng chỉ CompTIA A+", 4));
        certificates.add(new CertificateDto("IT004", "Chứng chỉ Java SE Programmer", 4));
        certificates.add(new CertificateDto("IT005", "Chứng chỉ AWS Certified Solutions Architect", 4));
        certificates.add(new CertificateDto("IT006", "Chứng chỉ Certified Ethical Hacker (CEH)", 4));
        certificates.add(new CertificateDto("IT007", "Chứng chỉ Cisco Certified Network Associate (CCNA)", 4));

        // Các chứng chỉ quản lý nhà nước (Type 3 - Trình độ quản lý nhà nước)
        certificates.add(new CertificateDto("IT008", "Chứng chỉ Project Management Professional (PMP)", 3));
        certificates.add(new CertificateDto("IT009", "Chứng chỉ Agile Certified Practitioner (PMI-ACP)", 3));
        certificates.add(new CertificateDto("IT010", "Chứng chỉ Scrum Master", 3));

        for (CertificateDto dto : certificates) {
            CertificateDto existedItem = certificateService.findByCode(dto.getCode());
            if (existedItem == null || existedItem.getId() == null) {
                certificateService.saveOrUpdate(null, dto);
            }
        }
    }


    @Override
    public void setupDefaultEducationalInstitution() {
        // Tạo danh sách các trường đại học, cao đẳng đào tạo CNTT tại miền Bắc
        List<EducationalInstitutionDto> institutions = new ArrayList<>();

        // Thêm các trường đại học, cao đẳng đào tạo CNTT với mã viết tắt ngắn gọn
        institutions.add(new EducationalInstitutionDto("HUST", "Đại học Bách khoa Hà Nội",
                "Hanoi University of Science and Technology",
                "Đào tạo mạnh về CNTT, với các chuyên ngành Khoa học Máy tính, Kỹ thuật phần mềm."));
        institutions.add(new EducationalInstitutionDto("UET", "Đại học Công nghệ - ĐHQG Hà Nội",
                "University of Engineering and Technology - VNU",
                "Trường đào tạo chất lượng cao về CNTT tại miền Bắc."));
        institutions.add(new EducationalInstitutionDto("PTIT", "Học viện Công nghệ Bưu chính Viễn thông",
                "Posts and Telecommunications Institute of Technology",
                "Đào tạo các ngành về CNTT và viễn thông, an toàn thông tin."));
        institutions.add(new EducationalInstitutionDto("FPT", "Đại học FPT", "FPT University",
                "Trường tư thục đào tạo CNTT uy tín với cơ sở tại Hà Nội (Hòa Lạc)."));
        institutions.add(
                new EducationalInstitutionDto("HUMG", "Đại học Mỏ - Địa chất", "Hanoi University of Mining and Geology",
                        "Có đào tạo chuyên ngành Công nghệ Thông tin với định hướng kỹ thuật ứng dụng."));
        institutions.add(
                new EducationalInstitutionDto("MTA", "Học viện Kỹ thuật Quân sự", "Le Quy Don Technical University",
                        "Trường đào tạo chuyên sâu về CNTT và kỹ thuật công nghệ trong lĩnh vực quốc phòng."));
        institutions.add(new EducationalInstitutionDto("EPU", "Đại học Điện lực", "Electric Power University",
                "Trường đào tạo về CNTT kết hợp với các ứng dụng công nghệ trong lĩnh vực điện và năng lượng."));
        institutions.add(new EducationalInstitutionDto("HNUE", "Đại học Sư phạm Hà Nội",
                "Hanoi National University of Education", "Có chuyên ngành đào tạo giáo viên về Công nghệ Thông tin."));
        institutions.add(new EducationalInstitutionDto("TNU", "Đại học Thái Nguyên", "Thai Nguyen University",
                "Trường thành viên đào tạo CNTT với các chương trình đa dạng."));
        institutions.add(new EducationalInstitutionDto("HANU", "Đại học Hà Nội", "Hanoi University",
                "Trường đào tạo chuyên ngành CNTT kết hợp ngoại ngữ."));
        institutions.add(new EducationalInstitutionDto("TLU", "Đại học Thủy lợi", "Thuyloi University",
                "Đào tạo CNTT với định hướng ứng dụng trong kỹ thuật thủy lợi và xây dựng."));
        institutions.add(new EducationalInstitutionDto("UTC", "Đại học Giao thông Vận tải",
                "University of Transport and Communications",
                "Trường đào tạo CNTT kết hợp ứng dụng trong giao thông vận tải."));
        institutions.add(new EducationalInstitutionDto("BUH", "Đại học Ngân hàng Hà Nội", "Banking University of Hanoi",
                "Đào tạo CNTT trong lĩnh vực tài chính ngân hàng."));
        institutions.add(new EducationalInstitutionDto("VIMARU", "Đại học Hàng hải Việt Nam",
                "Vietnam Maritime University", "Đào tạo CNTT với ứng dụng trong ngành hàng hải."));
        institutions.add(new EducationalInstitutionDto("BA", "Học viện Ngân hàng", "Banking Academy of Vietnam",
                "Đào tạo CNTT với định hướng trong tài chính và ngân hàng."));
        institutions.add(new EducationalInstitutionDto("NEU", "Đại học Kinh tế Quốc dân",
                "National Economics University", "Trường có ngành CNTT ứng dụng trong quản trị và kinh tế."));
        institutions.add(new EducationalInstitutionDto("HPU", "Đại học Dân lập Hải Phòng",
                "Haiphong Private University", "Trường đào tạo đa ngành, có chương trình CNTT."));
        institutions.add(new EducationalInstitutionDto("HPU2", "Đại học Hải Phòng", "Hai Phong University",
                "Đào tạo CNTT kết hợp với các ngành nghề kỹ thuật khác."));
        institutions.add(new EducationalInstitutionDto("SPKTV", "Đại học Sư phạm Kỹ thuật Vinh",
                "Vinh University of Technology Education", "Đào tạo CNTT cho giáo viên và kỹ sư công nghệ."));
        institutions.add(new EducationalInstitutionDto("ULIS", "Đại học Ngoại ngữ - ĐHQG Hà Nội",
                "University of Languages and International Studies", "Có đào tạo CNTT kết hợp ngôn ngữ."));
        institutions.add(new EducationalInstitutionDto("FPOLY", "Cao đẳng FPT Polytechnic", "FPT Polytechnic College",
                "Đào tạo hệ cao đẳng về CNTT với thời gian ngắn."));
        institutions.add(new EducationalInstitutionDto("BKN", "Cao đẳng nghề Bách khoa Hà Nội",
                "Hanoi Vocational College of Technology",
                "Đào tạo nghề CNTT với các chương trình thực hành chuyên sâu."));
        institutions.add(new EducationalInstitutionDto("CET", "Cao đẳng Kinh tế - Kỹ thuật Trung ương",
                "Central College of Economics and Technology",
                "Đào tạo ngành CNTT và các chuyên ngành kỹ thuật liên quan."));
        institutions.add(new EducationalInstitutionDto("HCT", "Cao đẳng Công nghệ Bách khoa Hà Nội",
                "Hanoi College of Technology and Business", "Đào tạo ngành CNTT tập trung vào thực hành kỹ thuật."));
        institutions.add(new EducationalInstitutionDto("CECE", "Cao đẳng Sư phạm Trung ương",
                "National College of Education", "Đào tạo giáo viên CNTT và ứng dụng công nghệ trong giảng dạy."));
        institutions.add(new EducationalInstitutionDto("TLU2", "Đại học Thành Đô", "Thang Long University",
                "Đào tạo ngành CNTT với nhiều chương trình thực hành."));
        institutions
                .add(new EducationalInstitutionDto("HUI", "Đại học Công nghiệp Hà Nội", "Hanoi University of Industry",
                        "Trường đào tạo ngành CNTT với các ngành liên quan đến kỹ thuật công nghiệp."));
        institutions.add(new EducationalInstitutionDto("HITC", "Cao đẳng Công nghệ Thông tin Hà Nội",
                "Hanoi College of Information Technology", "Trường chuyên đào tạo về công nghệ thông tin."));
        institutions.add(new EducationalInstitutionDto("CETU", "Cao đẳng Xây dựng Công trình đô thị",
                "College of Urban Construction", "Đào tạo CNTT với ứng dụng trong ngành xây dựng."));
        institutions.add(new EducationalInstitutionDto("HUTE", "Đại học Công nghệ và Quản lý Hữu Nghị",
                "Huu Nghi University of Technology and Management", "Đào tạo CNTT với ứng dụng trong quản lý."));
        institutions.add(new EducationalInstitutionDto("HITC2", "Cao đẳng Công thương Hà Nội",
                "Hanoi Industry and Trade College", "Đào tạo CNTT kết hợp với các ngành kinh tế và thương mại."));
        institutions.add(new EducationalInstitutionDto("HMU", "Đại học Kỹ thuật Y tế Hải Dương",
                "Hai Duong Medical Technical University", "Có đào tạo ngành CNTT ứng dụng trong y tế."));
        institutions.add(new EducationalInstitutionDto("SDU", "Đại học Sao Đỏ", "Sao Do University",
                "Trường đào tạo đa ngành, trong đó có CNTT."));
        institutions.add(new EducationalInstitutionDto("HUBT", "Đại học Kinh doanh và Công nghệ Hà Nội",
                "Hanoi University of Business and Technology", "Đào tạo CNTT kết hợp quản trị kinh doanh."));
        institutions.add(new EducationalInstitutionDto("PDU", "Đại học Phương Đông", "Phuong Dong University",
                "Trường tư thục có đào tạo ngành CNTT."));
        institutions.add(new EducationalInstitutionDto("HBU", "Đại học Hòa Bình", "Hoa Binh University",
                "Trường tư thục đào tạo ngành CNTT với các chương trình liên kết."));
        institutions.add(new EducationalInstitutionDto("VC", "Cao đẳng Công nghệ Viettronics", "Viettronics College",
                "Đào tạo CNTT ứng dụng trong điện tử và công nghệ thông tin."));
        institutions.add(new EducationalInstitutionDto("EP", "Đại học Điện lực", "Electric Power University",
                "Trường đào tạo ngành CNTT và các ngành kỹ thuật liên quan đến điện."));
        institutions.add(new EducationalInstitutionDto("UTC2", "Đại học Công nghệ Giao thông Vận tải",
                "University of Transport and Communications",
                "Đào tạo CNTT kết hợp với ứng dụng trong giao thông vận tải."));

        for (EducationalInstitutionDto dto : institutions) {
            EducationalInstitutionDto existedItem = educationalInstitutionService.findByCode(dto.getCode());
            if (existedItem == null || existedItem.getId() == null) {
                educationalInstitutionService.saveOrUpdate(dto, null);
            }
        }
    }

    @Override
    public void setupDefaultCountry() {
        List<CountryDto> countries = new ArrayList<>();

        // Tạo một số quốc gia nổi tiếng trên thế giới
        countries.add(createCountry("VN", "Việt Nam",
                "Quốc gia ở Đông Nam Á, nổi tiếng với văn hóa phong phú và cảnh đẹp thiên nhiên."));
        countries.add(createCountry("US", "Hoa Kỳ",
                "Quốc gia lớn nhất thế giới, nổi tiếng với nền kinh tế phát triển và đa dạng văn hóa."));
        countries.add(createCountry("JP", "Nhật Bản",
                "Quốc gia nổi tiếng với công nghệ tiên tiến và văn hóa truyền thống độc đáo."));
        countries.add(createCountry("FR", "Pháp", "Quốc gia nổi bật với nghệ thuật, ẩm thực và lịch sử phong phú."));
        countries.add(createCountry("DE", "Đức",
                "Quốc gia mạnh về công nghiệp và kỹ thuật, nổi tiếng với các thành phố lịch sử."));
        countries.add(createCountry("CN", "Trung Quốc",
                "Quốc gia đông dân nhất thế giới với nền văn hóa lâu đời và phong phú."));
        countries.add(createCountry("IN", "Ấn Độ", "Quốc gia đa dạng về văn hóa, tôn giáo và truyền thống."));
        countries.add(createCountry("BR", "Brazil",
                "Quốc gia lớn nhất Nam Mỹ, nổi tiếng với lễ hội Carnival và rừng Amazon."));
        countries.add(createCountry("GB", "Vương quốc Anh",
                "Quốc gia có lịch sử lâu dài, nổi tiếng với kiến trúc cổ điển và nền văn hóa ảnh hưởng toàn cầu."));
        countries.add(createCountry("CA", "Canada",
                "Quốc gia lớn thứ hai thế giới, nổi tiếng với thiên nhiên hoang sơ và đa dạng văn hóa."));
        countries.add(createCountry("AU", "Úc", "Quốc gia nổi tiếng với những cảnh đẹp tự nhiên và động vật độc đáo."));
        countries.add(createCountry("IT", "Ý", "Quốc gia nổi tiếng với nghệ thuật, ẩm thực và lịch sử phong phú."));
        countries.add(
                createCountry("ES", "Tây Ban Nha", "Quốc gia nổi tiếng với văn hóa đa dạng và các lễ hội đặc sắc."));
        countries.add(createCountry("RU", "Nga", "Quốc gia lớn nhất thế giới với văn hóa và lịch sử phong phú."));
        countries.add(createCountry("ZA", "Nam Phi", "Quốc gia nổi tiếng với vẻ đẹp thiên nhiên và đa dạng văn hóa."));
        countries.add(createCountry("KR", "Hàn Quốc", "Quốc gia nổi tiếng với công nghệ tiên tiến và văn hóa K-Pop."));
        countries.add(
                createCountry("NL", "Hà Lan", "Quốc gia nổi tiếng với cối xay gió, hoa tulip và văn hóa nghệ thuật."));
        countries.add(createCountry("SE", "Thụy Điển",
                "Quốc gia nổi tiếng với chất lượng cuộc sống cao và thiên nhiên tuyệt đẹp."));
        countries.add(createCountry("FI", "Phần Lan",
                "Quốc gia nổi tiếng với hệ thống giáo dục hàng đầu và vẻ đẹp tự nhiên."));
        countries.add(createCountry("NO", "Na Uy", "Quốc gia nổi tiếng với fjord và phong cảnh thiên nhiên hùng vĩ."));

        for (CountryDto country : countries) {
            CountryDto existedItem = hrCountryService.findByCode(country.getCode());
            if (existedItem == null || existedItem.getId() == null) {
                hrCountryService.saveOne(country, null);
            }
        }

    }

    private CountryDto createCountry(String code, String name, String description) {
        CountryDto countryDto = new CountryDto();
        countryDto.setCode(code);
        countryDto.setName(name);
        countryDto.setDescription(description);
        return countryDto;
    }

    @Override
    public void setupDefaultEthnics() {
        List<EthnicsDto> ethnics = new ArrayList<>();

        // Danh sách 54 dân tộc ở Việt Nam
        ethnics.add(createEthnic("Kinh", "VN-KH",
                "Dân tộc đông nhất Việt Nam, chủ yếu sinh sống ở đồng bằng Bắc Bộ và Nam Bộ."));
        ethnics.add(createEthnic("Tày", "VN-TY", "Dân tộc sống chủ yếu ở vùng núi phía Bắc."));
        ethnics.add(createEthnic("Thái", "VN-TH", "Dân tộc cư trú tại các tỉnh miền núi phía Tây Bắc."));
        ethnics.add(createEthnic("Mường", "VN-MU", "Dân tộc sống chủ yếu ở tỉnh Hòa Bình."));
        ethnics.add(createEthnic("Khmer", "VN-KH", "Dân tộc sinh sống chủ yếu ở miền Tây Nam Bộ."));
        ethnics.add(createEthnic("Nùng", "VN-NU", "Dân tộc sống chủ yếu ở vùng núi phía Bắc, đặc biệt là Lạng Sơn."));
        ethnics.add(createEthnic("H'Mông", "VN-HM", "Dân tộc sống chủ yếu ở các tỉnh miền núi phía Bắc."));
        ethnics.add(createEthnic("Dao", "VN-DA",
                "Dân tộc chủ yếu sinh sống ở các tỉnh phía Bắc, nổi bật với trang phục truyền thống."));
        ethnics.add(createEthnic("Gia Rai", "VN-GR", "Dân tộc sinh sống chủ yếu ở tỉnh Gia Lai."));
        ethnics.add(createEthnic("Ê Đê", "VN-EE", "Dân tộc sống chủ yếu ở tỉnh Đắk Lắk."));
        ethnics.add(createEthnic("Ba Na", "VN-BN", "Dân tộc sống chủ yếu ở các tỉnh Tây Nguyên."));
        ethnics.add(createEthnic("Co Tu", "VN-CT", "Dân tộc sinh sống chủ yếu ở tỉnh Quảng Nam và Đà Nẵng."));
        ethnics.add(createEthnic("Chăm", "VN-CH",
                "Dân tộc sống chủ yếu ở miền Trung, nổi tiếng với văn hóa và kiến trúc."));
        ethnics.add(createEthnic("Tà Ôi", "VN-TO", "Dân tộc sống chủ yếu ở tỉnh Thừa Thiên Huế."));
        ethnics.add(createEthnic("Xơ Đăng", "VN-XD", "Dân tộc sinh sống ở các tỉnh Tây Nguyên."));
        ethnics.add(createEthnic("Sê Đăng", "VN-SD", "Dân tộc sống ở vùng núi phía Trung Việt."));
        ethnics.add(createEthnic("Bru-Vân Kiều", "VN-BK", "Dân tộc sinh sống chủ yếu ở tỉnh Quảng Trị."));
        ethnics.add(createEthnic("Cơ Tu", "VN-CT", "Dân tộc sống chủ yếu ở tỉnh Quảng Nam và Đà Nẵng."));
        ethnics.add(createEthnic("Lào", "VN-LA", "Dân tộc sống ở khu vực biên giới phía Tây Bắc Việt Nam."));
        ethnics.add(createEthnic("Tà Ôi", "VN-TO", "Dân tộc sống chủ yếu ở Thừa Thiên Huế."));
        ethnics.add(createEthnic("Chứt", "VN-CHT", "Dân tộc sống chủ yếu ở vùng núi Hòa Bình và Quảng Bình."));
        ethnics.add(createEthnic("Kháng", "VN-KH", "Dân tộc sinh sống chủ yếu ở vùng núi phía Bắc Việt Nam."));
        ethnics.add(createEthnic("Mường", "VN-MU", "Dân tộc sống chủ yếu ở tỉnh Hòa Bình và một số tỉnh khác."));
        ethnics.add(createEthnic("Ngái", "VN-NG", "Dân tộc sống chủ yếu ở Lạng Sơn."));
        ethnics.add(createEthnic("Tày", "VN-TY", "Dân tộc chủ yếu sống ở Lạng Sơn, Cao Bằng."));
        ethnics.add(createEthnic("Thái", "VN-TH", "Dân tộc sống chủ yếu ở các tỉnh miền núi phía Tây Bắc."));
        ethnics.add(createEthnic("Lê", "VN-LE", "Dân tộc nhỏ ở một số tỉnh miền Bắc Việt Nam."));
        ethnics.add(createEthnic("H'Mông", "VN-HM", "Dân tộc nổi tiếng với trang phục sặc sỡ và văn hóa đặc sắc."));
        ethnics.add(createEthnic("Pà Thẻn", "VN-PTH", "Dân tộc sống chủ yếu ở tỉnh Hà Giang."));
        ethnics.add(createEthnic("La Chí", "VN-LC", "Dân tộc sống ở vùng núi phía Bắc Việt Nam."));
        ethnics.add(createEthnic("Lô Lô", "VN-LL", "Dân tộc sống chủ yếu ở các tỉnh phía Bắc."));
        ethnics.add(createEthnic("Rẻo", "VN-RE", "Dân tộc sống chủ yếu ở vùng núi phía Bắc."));
        ethnics.add(createEthnic("Giẻ Triêng", "VN-GT", "Dân tộc sống chủ yếu ở Kon Tum."));
        ethnics.add(createEthnic("Co", "VN-CO", "Dân tộc sinh sống chủ yếu ở các tỉnh miền Trung."));
        ethnics.add(createEthnic("Khơ Mú", "VN-KM", "Dân tộc sống chủ yếu ở Nghệ An và Thanh Hóa."));
        ethnics.add(createEthnic("Xinh Mun", "VN-XM", "Dân tộc sống chủ yếu ở tỉnh Sơn La."));
        ethnics.add(createEthnic("Dân Tộc Nước Ngoài", "VN-NGOAI",
                "Dân tộc sinh sống chủ yếu ở các tỉnh biên giới Việt Nam."));
        ethnics.add(createEthnic("Kơ Ho", "VN-KH", "Dân tộc sống chủ yếu ở Lâm Đồng."));
        ethnics.add(createEthnic("Chơ Ro", "VN-CR", "Dân tộc sinh sống chủ yếu ở Đồng Nai."));
        ethnics.add(createEthnic("Thổ", "VN-THO", "Dân tộc sinh sống chủ yếu ở tỉnh Nghệ An."));
        ethnics.add(createEthnic("Chăm", "VN-CH", "Dân tộc sống chủ yếu ở Ninh Thuận, Bình Thuận."));
        ethnics.add(createEthnic("Rơ Măm", "VN-RM", "Dân tộc sinh sống chủ yếu ở Đắk Lắk."));
        ethnics.add(createEthnic("Xê Đăng", "VN-XD", "Dân tộc sống chủ yếu ở các tỉnh Tây Nguyên."));
        ethnics.add(createEthnic("Ba Na", "VN-BN", "Dân tộc sinh sống chủ yếu ở Gia Lai, Kon Tum."));
        ethnics.add(createEthnic("Kơ Tu", "VN-KT", "Dân tộc sinh sống chủ yếu ở Quảng Nam, Đà Nẵng."));
        ethnics.add(createEthnic("Co Tu", "VN-CT", "Dân tộc sinh sống chủ yếu ở Quảng Nam."));
        ethnics.add(createEthnic("Mường", "VN-MU", "Dân tộc sống chủ yếu ở Hòa Bình, Thanh Hóa."));
        ethnics.add(createEthnic("Gia Rai", "VN-GR", "Dân tộc sống chủ yếu ở Gia Lai, Kon Tum."));
        ethnics.add(createEthnic("Ngái", "VN-NG", "Dân tộc sống chủ yếu ở Lạng Sơn."));
        ethnics.add(createEthnic("Chăm", "VN-CH", "Dân tộc sống chủ yếu ở Ninh Thuận, Bình Thuận."));

        for (EthnicsDto dto : ethnics) {
            EthnicsDto exitedItem = hrEthinicsService.findByCode(dto.getCode());
            if (exitedItem == null || exitedItem.getId() == null) {
                hrEthinicsService.saveOne(dto, null);
            }
        }
    }

    private EthnicsDto createEthnic(String name, String code, String description) {
        EthnicsDto ethnicDto = new EthnicsDto();
        ethnicDto.setName(name);
        ethnicDto.setCode(code);
        ethnicDto.setDescription(description);
        return ethnicDto;
    }

    @Override
    public void setupDefaultReligion() {
        List<ReligionDto> religions = new ArrayList<>();

        religions.add(
                createReligion("CR", "Cơ Đốc giáo", "Tôn giáo dựa trên sự giảng dạy và đời sống của Chúa Giê-su."));
        religions.add(createReligion("BUD", "Phật giáo",
                "Tôn giáo dựa trên giáo lý của Đức Phật, với nhiều nhánh khác nhau."));
        religions.add(createReligion("HIN", "Ấn Độ giáo", "Tôn giáo cổ xưa có nguồn gốc từ Ấn Độ, thờ nhiều thần."));
        religions.add(createReligion("ISL", "Hồi giáo",
                "Tôn giáo khởi nguồn từ Trung Đông, theo lời dạy của Tiên tri Muhammad."));
        religions.add(createReligion("SIK", "Sikh giáo",
                "Tôn giáo khởi nguồn từ Ấn Độ, kết hợp giữa Hindu giáo và Hồi giáo."));
        religions.add(createReligion("JUD", "Do Thái giáo",
                "Tôn giáo khởi nguồn từ dân tộc Do Thái, với truyền thống lâu đời."));
        religions.add(createReligion("TAO", "Đạo giáo",
                "Tôn giáo cổ xưa tại Trung Quốc, nhấn mạnh sự hòa hợp với tự nhiên."));
        religions.add(createReligion("SHA", "Thần giáo",
                "Tôn giáo bản địa của Nhật Bản, tôn thờ các vị thần và linh hồn tự nhiên."));
        religions.add(createReligion("CAO", "Cao Đài",
                "Tôn giáo tại Việt Nam, kết hợp giáo lý của nhiều tôn giáo khác nhau."));
        religions.add(createReligion("HOA", "Hòa Hảo",
                "Tôn giáo tại Việt Nam, phát triển từ Phật giáo, chú trọng lòng nhân ái."));
        religions.add(createReligion("PRO", "Tin Lành",
                "Nhánh của Cơ Đốc giáo, nhấn mạnh vào niềm tin cá nhân và sự cứu rỗi."));
        religions.add(createReligion("EVA", "Ngũ tuần",
                "Một nhánh của Tin Lành với các đặc điểm nổi bật về thánh linh và phép lạ."));
        religions.add(
                createReligion("JAI", "Kỳ Na giáo", "Tôn giáo Ấn Độ cổ xưa, nhấn mạnh vào sự từ bi và không bạo lực."));
        religions.add(createReligion("ZOR", "Ba Tư giáo",
                "Tôn giáo cổ đại khởi nguồn từ Iran, tôn thờ Ahura Mazda là vị thần tối cao."));
        religions.add(createReligion("ANIM", "Tín ngưỡng dân gian",
                "Niềm tin vào các linh hồn, vật linh trong thiên nhiên và tổ tiên."));
        religions.add(createReligion("BAH", "Bahá'í",
                "Tôn giáo nhấn mạnh sự thống nhất của tôn giáo và bình đẳng nhân loại."));
        religions.add(createReligion("CONF", "Khổng giáo",
                "Triết lý đạo đức và tôn giáo xuất phát từ các lời dạy của Khổng Tử."));
        religions.add(createReligion("SHI", "Shinto",
                "Tôn giáo bản địa của Nhật Bản, tôn thờ tổ tiên và các vị thần tự nhiên."));
        religions.add(createReligion("RVS", "Ravidassia",
                "Một nhánh của Ấn Độ giáo, phát triển từ các lời dạy của Guru Ravidass."));
        religions
                .add(createReligion("AGN", "Vô thần", "Quan điểm không tin vào sự tồn tại của các thần hay tôn giáo."));
        religions.add(createReligion("HUM", "Chủ nghĩa nhân văn",
                "Một quan điểm phi tôn giáo, nhấn mạnh giá trị con người và đạo đức."));
        religions.add(createReligion("NA", "Tôn giáo bản địa",
                "Niềm tin truyền thống và tôn giáo bản địa của các dân tộc thiểu số."));
        religions.add(createReligion("SC", "Khoa học giáo",
                "Tôn giáo mới phát triển từ niềm tin vào khoa học và tiến bộ con người."));
        religions.add(createReligion("UFO", "UFO giáo",
                "Tôn giáo hiện đại dựa trên niềm tin vào sự tồn tại của sinh vật ngoài hành tinh."));
        religions.add(createReligion("KHONG", "Không",
                "Người không theo hoặc không tin vào bất kỳ tôn giáo nào. Họ có thể sống theo triết lý cá nhân, đạo đức xã hội hoặc các hệ thống niềm tin không mang tính tôn giáo."));

        for (ReligionDto dto : religions) {
            ReligionDto existedItem = hrReligionService.findByCode(dto.getCode());
            if (existedItem == null || existedItem.getId() == null) {
                hrReligionService.saveOne(dto, null);
            }
        }

    }

    private ReligionDto createReligion(String code, String name, String description) {
        ReligionDto religion = new ReligionDto();
        religion.setCode(code);
        religion.setName(name);
        religion.setDescription(description);
        return religion;
    }

    @Override
    public void setupDefaultHrEducationType() {
        List<HrEducationTypeDto> hrEducationTypes = new ArrayList<>();
        hrEducationTypes.add(new HrEducationTypeDto("HrEduType01", "Đào tạo chính quy", "Fulltime education",
                "Hình thức giáo dục truyền thống, học sinh, sinh viên học toàn thời gian tại trường với thời khóa biểu cố định, theo chương trình chuẩn của Bộ Giáo dục."));
        hrEducationTypes.add(new HrEducationTypeDto("HrEduType02", "Đào tạo vừa học vừa làm", "Parttime education",
                "Kết hợp giữa học tập và làm việc. Người học có thể học bán thời gian và tham gia công việc thực tế để tích lũy kinh nghiệm."));
        hrEducationTypes.add(new HrEducationTypeDto("HrEduType03", "Đào tạo từ xa", "E-learning education",
                "Học viên học qua các phương tiện như internet, sách vở, hoặc bài giảng từ xa, không yêu cầu tham gia trực tiếp tại cơ sở giáo dục, phù hợp cho người học có nhu cầu linh hoạt về thời gian và địa điểm."));

        for (HrEducationTypeDto dto : hrEducationTypes) {
            HrEducationTypeDto existedItem = hrEducationTypeService.findByCode(dto.getCode());
            if (existedItem == null || existedItem.getId() == null) {
                hrEducationTypeService.saveOrUpdate(dto, null);
            }
        }
    }

    @Override
    public void setupDefaultEducationDegree() {
        List<EducationDegreeDto> educationDegrees = new ArrayList<>();

        // Thêm các trình độ học vấn phổ biến tại Việt Nam
        educationDegrees.add(new EducationDegreeDto("TS", "Tiến sĩ", 6)); // Doctorate
        educationDegrees.add(new EducationDegreeDto("ThS", "Thạc sĩ", 5)); // Master's degree
        educationDegrees.add(new EducationDegreeDto("CN", "Cử nhân", 4)); // Bachelor's degree
        educationDegrees.add(new EducationDegreeDto("KS", "Kỹ sư", 4)); // Engineer
        educationDegrees.add(new EducationDegreeDto("BS", "Bác sĩ", 4)); // Medical doctor
        educationDegrees.add(new EducationDegreeDto("CD", "Cao đẳng", 3)); // College degree
        educationDegrees.add(new EducationDegreeDto("CNCĐ", "Cử nhân cao đẳng", 3)); // College-level Bachelor
        educationDegrees.add(new EducationDegreeDto("TKN", "Trung cấp nghề", 2)); // Vocational Intermediate level
        educationDegrees.add(new EducationDegreeDto("SC", "Sơ cấp", 1)); // Elementary level
        educationDegrees.add(new EducationDegreeDto("TKV", "Kỹ thuật viên", 3)); // Technician
        educationDegrees.add(new EducationDegreeDto("TC", "Trung cấp", 2)); // Intermediate level
        educationDegrees.add(new EducationDegreeDto("LĐPT", "Lao động phổ thông", 1)); // General laborer
        educationDegrees.add(new EducationDegreeDto("CDN", "Cao đẳng nghề", 3)); // Vocational college degree
        educationDegrees.add(new EducationDegreeDto("TCKH", "Trung cấp khoa học", 2)); // Science Intermediate
        educationDegrees.add(new EducationDegreeDto("CDC", "Chứng chỉ đào tạo chuyên môn", 2)); // Professional
        // Certificate
        for (EducationDegreeDto dto : educationDegrees) {
            EducationDegreeDto existedItem = educationDegreeService.findByCode(dto.getCode());
            if (existedItem == null || existedItem.getId() == null) {
                educationDegreeService.saveOrUpdate(null, dto);
            }
        }

    }

    // setup data for ShiftWork and ShiftWorkTimePeriod - Ca làm việc và giai đoạn giờ giấc trong ca làm việc
    @Override
    public void setupDefaultShiftWorkAndShiftWorkTimePeriod() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // 01 - Ca ngày
        ShiftWorkDto dayShift = shiftWorkService.findByCode(HrConstants.ShiftWorkCode.DAY_SHIFT.getValue());
        if (dayShift == null) {
            dayShift = new ShiftWorkDto();
            dayShift.setCode(HrConstants.ShiftWorkCode.DAY_SHIFT.getValue());
            dayShift.setName("Ca ngày");

            List<ShiftWorkTimePeriodDto> timePeriods = new ArrayList<>();
            // Buổi sáng 9:00-12:00
            try {
                ShiftWorkTimePeriodDto morningPeriod = new ShiftWorkTimePeriodDto();

                String currentDate = "2024-12-14";
                Date startTime = dateFormat.parse(currentDate + " 09:00");
                Date endTime = dateFormat.parse(currentDate + " 12:00");

                morningPeriod.setStartTime(startTime);
                morningPeriod.setEndTime(endTime);

                timePeriods.add(morningPeriod);
            } catch (Exception ignored) {
            }


            // Buổi chiều 14:00-19:00
            try {
                ShiftWorkTimePeriodDto afternoonPeriod = new ShiftWorkTimePeriodDto();

                String currentDate = "2024-12-14";
                Date startTime = dateFormat.parse(currentDate + " 14:00");
                Date endTime = dateFormat.parse(currentDate + " 19:00");

                afternoonPeriod.setStartTime(startTime);
                afternoonPeriod.setEndTime(endTime);

                timePeriods.add(afternoonPeriod);
            } catch (Exception ignored) {
            }

            dayShift.setTimePeriods(timePeriods);

            shiftWorkService.saveOrUpdate(null, dayShift);
        }

        // 02 - Ca tối (OT)
        ShiftWorkDto nightShift = shiftWorkService.findByCode(HrConstants.ShiftWorkCode.NIGHT_SHIFT.getValue());
        if (nightShift == null) {
            nightShift = new ShiftWorkDto();
            nightShift.setCode(HrConstants.ShiftWorkCode.NIGHT_SHIFT.getValue());
            nightShift.setName("Ca tối (OT)");

            List<ShiftWorkTimePeriodDto> timePeriods = new ArrayList<>();
            // Buổi tối 19:00-23:30
            try {
                ShiftWorkTimePeriodDto nightPeriod = new ShiftWorkTimePeriodDto();

                String currentDate = "2024-12-14";
                Date startTime = dateFormat.parse(currentDate + " 19:00");
                Date endTime = dateFormat.parse(currentDate + " 23:30");

                nightPeriod.setStartTime(startTime);
                nightPeriod.setEndTime(endTime);

                timePeriods.add(nightPeriod);
            } catch (Exception ignored) {
            }

            nightShift.setTimePeriods(timePeriods);

            shiftWorkService.saveOrUpdate(null, nightShift);
        }

    }

    public void setupProfession() {
        List<ProfessionDto> listProfession = new ArrayList<>();

        // Đọc file Excel
        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("data_profession.xlsx")) {
            if (fileInputStream == null) {
                throw new IOException("File 'data_profession.xlsx' không tìm thấy trong classpath");
            }

            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    ProfessionDto professionDto = new ProfessionDto();
                    professionDto.setCode(getCellValue(row.getCell(0)).toString());
                    professionDto.setName(getCellValue(row.getCell(1)).toString());

                    Boolean existsCodeProfession = hrProfessionService.checkCode(null, professionDto.getCode());
                    if (!existsCodeProfession) {
                        listProfession.add(professionDto);
                    }
                }
            }

            workbook.close();

            if (!CollectionUtils.isEmpty(listProfession)) {
                for (ProfessionDto professionDto : listProfession) {
                    if (professionDto != null && professionDto.getCode() != null) {
                        Boolean existsCodeProfession = hrProfessionService.checkCode(null, professionDto.getCode());
                        if (!existsCodeProfession) {
                            hrProfessionService.saveOne(professionDto, null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
        }
    }

    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else if ((cell.getNumericCellValue()) % 1 == 0) {
                    return NumberToTextConverter.toText(cell.getNumericCellValue());
                }
                return String.valueOf(cell.getNumericCellValue());
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_ERROR:
                return cell.getErrorCellValue();
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
            case Cell.CELL_TYPE_BLANK:
                return null;
            default:
                return null;
        }
    }

    @Autowired
    private SalaryTemplateRepository salaryTemplateRepository;

    @Autowired
    private SalaryItemRepository salaryItemRepository;

    @Autowired
    private SalaryTemplateItemGroupRepository salaryTemplateItemGroupRepository;

    // setup MISA AMIS salaryTemplate - URL: https://helpamis.misa.vn/amis-tien-luong/kb/huong-dan-chung-luong-nghiep-vu-tinh-luong-tong-quan-tren-amis-tien-luong/
    @Override
    public void setupMisaAmisSalaryTemplate() {
        String misaAmisTemplateCode = "MBL_THONG_DUNG";

        SalaryTemplateDto amisTemplateDto = salaryTemplateService.findByCode(misaAmisTemplateCode);
        if (amisTemplateDto == null) {
            SalaryTemplate amisTemplate = new SalaryTemplate();
            amisTemplate.setCode(misaAmisTemplateCode);
            amisTemplate.setName("Mẫu bảng lương thông dụng");
            amisTemplate.setDescription("Mẫu bảng lương được sử dụng chung trong nhiều trường hợp tính lương");
            amisTemplate.setIsActive(true);

            if (amisTemplate.getTemplateItemGroups() == null)
                amisTemplate.setTemplateItemGroups(new HashSet<SalaryTemplateItemGroup>());

            Set<SalaryTemplateItemGroup> templateItemGroups = new HashSet<>();

            // Các khoản thu nhập chịu thuế
            SalaryTemplateItemGroup cacKhoanThuNhapChiuThue = new SalaryTemplateItemGroup();
            cacKhoanThuNhapChiuThue.setName("Các khoản thu nhập chịu thuế");
            cacKhoanThuNhapChiuThue.setDescription("Các khoản thu nhập chịu thuế là các nguồn thu nhập mà cá nhân hoặc tổ chức phải nộp thuế theo quy định của pháp luật");
            cacKhoanThuNhapChiuThue.setSalaryTemplate(amisTemplate);
            templateItemGroups.add(cacKhoanThuNhapChiuThue);

            // Các khoản thu nhập không chịu thuế
            SalaryTemplateItemGroup cacKhoanThuNhapKhongChiuThue = new SalaryTemplateItemGroup();
            cacKhoanThuNhapKhongChiuThue.setName("Các khoản thu nhập không chịu thuế");
            cacKhoanThuNhapKhongChiuThue.setDescription("Các khoản thu nhập không chịu thuế là những khoản thu nhập được miễn thuế theo quy định, bao gồm một số khoản trợ cấp, phụ cấp hoặc thu nhập từ các nguồn được pháp luật miễn trừ.");
            cacKhoanThuNhapKhongChiuThue.setSalaryTemplate(amisTemplate);
            templateItemGroups.add(cacKhoanThuNhapKhongChiuThue);

            // Các khoản khấu trừ
            SalaryTemplateItemGroup cacKhoanKhauTru = new SalaryTemplateItemGroup();
            cacKhoanKhauTru.setName("Các khoản khấu trừ");
            cacKhoanKhauTru.setDescription("Các khoản khấu trừ là những khoản được trừ vào thu nhập chịu thuế, bao gồm bảo hiểm xã hội, bảo hiểm y tế, bảo hiểm thất nghiệp và các khoản giảm trừ gia cảnh.");
            cacKhoanKhauTru.setSalaryTemplate(amisTemplate);
            templateItemGroups.add(cacKhoanKhauTru);

            // Công ty đóng
            SalaryTemplateItemGroup congTyDong = new SalaryTemplateItemGroup();
            congTyDong.setName("Công ty đóng");
            congTyDong.setDescription("Công ty đóng là các khoản mà doanh nghiệp phải đóng cho người lao động theo quy định, bao gồm bảo hiểm xã hội, bảo hiểm y tế, bảo hiểm thất nghiệp và các khoản đóng góp khác.");
            congTyDong.setSalaryTemplate(amisTemplate);
            templateItemGroups.add(congTyDong);


            amisTemplate.getTemplateItemGroups().clear();
            amisTemplate.getTemplateItemGroups().addAll(templateItemGroups);


            // TODO: setup các thành phần lương trong bảng lương này
            if (amisTemplate.getTemplateItems() == null)
                amisTemplate.setTemplateItems(new HashSet<>());

            Set<SalaryTemplateItem> templateItems = new HashSet<>();

            // STT
            SalaryTemplateItem sttItem = new SalaryTemplateItem();
            SalaryItem sttSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.STT_SYSTEM.getValue()).get(0);
            sttItem.setDisplayOrder(1);
            sttItem.setDisplayName("STT");
            sttItem.setDescription(sttSI.getDescription());
            sttItem.setSalaryItem(sttSI);
            sttItem.setHiddenOnPayslip(true);
            sttItem.setSalaryTemplate(amisTemplate);
            templateItems.add(sttItem);

            // Mã nhân viên
            SalaryTemplateItem maNVItem = new SalaryTemplateItem();
            SalaryItem maNVSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.MA_NV_SYSTEM.getValue()).get(0);
            maNVItem.setDisplayOrder(2);
            maNVItem.setDisplayName("Mã nhân viên");
            maNVItem.setDescription(maNVSI.getDescription());
            maNVItem.setSalaryItem(maNVSI);
            maNVItem.setSalaryTemplate(amisTemplate);
            templateItems.add(maNVItem);

            // Ho va ten
            SalaryTemplateItem hovatenItem = new SalaryTemplateItem();
            SalaryItem hovatenSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.HO_VA_TEN_NV_SYSTEM.getValue()).get(0);
            hovatenItem.setDisplayOrder(3);
            hovatenItem.setDisplayName("Họ và tên");
            hovatenItem.setDescription(hovatenSI.getDescription());
            hovatenItem.setSalaryItem(hovatenSI);
            hovatenItem.setSalaryTemplate(amisTemplate);
            templateItems.add(hovatenItem);

            // Don vi cong tac
            SalaryTemplateItem donViCongTacItem = new SalaryTemplateItem();
            SalaryItem donViCongTacSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.DON_VI_NV_SYSTEM.getValue()).get(0);
            donViCongTacItem.setDisplayOrder(4);
            donViCongTacItem.setDisplayName("Đơn vị công tác");
            donViCongTacItem.setDescription(donViCongTacSI.getDescription());
            donViCongTacItem.setSalaryItem(donViCongTacSI);
            donViCongTacItem.setSalaryTemplate(amisTemplate);
            templateItems.add(donViCongTacItem);

            // Phòng ban
            SalaryTemplateItem phongBanItem = new SalaryTemplateItem();
            SalaryItem phongBanSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.PHONG_BAN_NV_SYSTEM.getValue()).get(0);
            phongBanItem.setDisplayOrder(5);
            phongBanItem.setDisplayName("Phòng ban");
            phongBanItem.setDescription(phongBanSI.getDescription());
            phongBanItem.setSalaryItem(phongBanSI);
            phongBanItem.setSalaryTemplate(amisTemplate);
            templateItems.add(phongBanItem);

            // Lương cơ bản
            SalaryTemplateItem luongCoBanItem = new SalaryTemplateItem();
            SalaryItem luongCoBanSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.LUONG_CO_BAN_SYSTEM.getValue()).get(0);
            luongCoBanItem.setDisplayOrder(6);
            luongCoBanItem.setDisplayName("Lương cơ bản");
            luongCoBanItem.setDescription(luongCoBanSI.getDescription());
            luongCoBanItem.setSalaryItem(luongCoBanSI);
            luongCoBanItem.setSalaryTemplate(amisTemplate);
            templateItems.add(luongCoBanItem);

            // Lương đóng bảo hiểm
            SalaryTemplateItem luongDongBHItem = new SalaryTemplateItem();
            SalaryItem luongDongBHSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.LUONG_DONG_BHXH_SYSTEM.getValue()).get(0);
            luongDongBHItem.setDisplayOrder(7);
            luongDongBHItem.setDisplayName("Lương đóng bảo hiểm");
            luongDongBHItem.setDescription(luongDongBHSI.getDescription());
            luongDongBHItem.setSalaryItem(luongDongBHSI);
            luongDongBHItem.setSalaryTemplate(amisTemplate);
            templateItems.add(luongDongBHItem);

            // Số ngày công chuẩn
            SalaryTemplateItem soNgayCongChuanItem = new SalaryTemplateItem();
            SalaryItem soNgayCongChuanSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.SO_NGAY_CONG_CHUAN_SYSTEM.getValue()).get(0);
            soNgayCongChuanItem.setDisplayOrder(8);
            soNgayCongChuanItem.setDisplayName("Số ngày công chuẩn");
            soNgayCongChuanItem.setDescription(soNgayCongChuanSI.getDescription());
            soNgayCongChuanItem.setSalaryItem(soNgayCongChuanSI);
            soNgayCongChuanItem.setSalaryTemplate(amisTemplate);
            templateItems.add(soNgayCongChuanItem);

            // Số ngày công
            SalaryTemplateItem soNgayCongItem = new SalaryTemplateItem();
            SalaryItem soNgayCongSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.SO_NGAY_CONG_SYSTEM.getValue()).get(0);
            soNgayCongItem.setDisplayOrder(9);
            soNgayCongItem.setDisplayName("Số ngày công");
            soNgayCongItem.setDescription(soNgayCongSI.getDescription());
            soNgayCongItem.setSalaryItem(soNgayCongSI);
            soNgayCongItem.setSalaryTemplate(amisTemplate);
            templateItems.add(soNgayCongItem);

            // Lương theo ngày công
            SalaryTemplateItem luongTheoNgayCongItem = new SalaryTemplateItem();
            SalaryItem luongTheoNgayCongSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.LUONG_THEO_NGAY_CONG_SYSTEM.getValue()).get(0);
            luongTheoNgayCongItem.setDisplayOrder(10);
            luongTheoNgayCongItem.setDisplayName("Lương theo ngày công");
            luongTheoNgayCongItem.setDescription(luongTheoNgayCongSI.getDescription());
            luongTheoNgayCongItem.setSalaryItem(luongTheoNgayCongSI);
            luongTheoNgayCongItem.setSalaryTemplate(amisTemplate);
            luongTheoNgayCongItem.setTemplateItemGroup(cacKhoanThuNhapChiuThue);
            templateItems.add(luongTheoNgayCongItem);

            // Phụ cấp trách nhiệm
            SalaryTemplateItem phuCapTrachNhiemItem = new SalaryTemplateItem();
            SalaryItem phuCapTrachNhiemSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSetup.PHU_CAP_TRACH_NHIEM.getValue()).get(0);
            phuCapTrachNhiemItem.setDisplayOrder(11);
            phuCapTrachNhiemItem.setDisplayName("Phụ cấp trách nhiệm");
            phuCapTrachNhiemItem.setDescription(phuCapTrachNhiemSI.getDescription());
            phuCapTrachNhiemItem.setSalaryItem(phuCapTrachNhiemSI);
            phuCapTrachNhiemItem.setSalaryTemplate(amisTemplate);
            phuCapTrachNhiemItem.setTemplateItemGroup(cacKhoanThuNhapChiuThue);
            templateItems.add(phuCapTrachNhiemItem);

            // Quà lễ, tết
            SalaryTemplateItem quaLeTetItem = new SalaryTemplateItem();
            SalaryItem quaLeTetSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSetup.QUA_LE_TET.getValue()).get(0);
            quaLeTetItem.setDisplayOrder(12);
            quaLeTetItem.setDisplayName("Quà lễ, tết");
            quaLeTetItem.setDescription(quaLeTetSI.getDescription());
            quaLeTetItem.setSalaryItem(quaLeTetSI);
            quaLeTetItem.setSalaryTemplate(amisTemplate);
            quaLeTetItem.setTemplateItemGroup(cacKhoanThuNhapChiuThue);
            templateItems.add(quaLeTetItem);

            // Phụ cấp điện thoại
            SalaryTemplateItem phuCapDienThoaiItem = new SalaryTemplateItem();
            SalaryItem phuCapDienThoaiSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSetup.PHU_CAP_DIEN_THOAI.getValue()).get(0);
            phuCapDienThoaiItem.setDisplayOrder(13);
            phuCapDienThoaiItem.setDisplayName("Phụ cấp điện thoại");
            phuCapDienThoaiItem.setDescription(phuCapDienThoaiSI.getDescription());
            phuCapDienThoaiItem.setSalaryItem(phuCapDienThoaiSI);
            phuCapDienThoaiItem.setSalaryTemplate(amisTemplate);
            phuCapDienThoaiItem.setTemplateItemGroup(cacKhoanThuNhapKhongChiuThue);
            templateItems.add(phuCapDienThoaiItem);

            // Công tác phí
            SalaryTemplateItem congTacPhiItem = new SalaryTemplateItem();
            SalaryItem congTacPhiSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSetup.CONG_TAC_PHI.getValue()).get(0);
            congTacPhiItem.setDisplayOrder(14);
            congTacPhiItem.setDisplayName("Công tác phí");
            congTacPhiItem.setDescription(congTacPhiSI.getDescription());
            congTacPhiItem.setSalaryItem(congTacPhiSI);
            congTacPhiItem.setSalaryTemplate(amisTemplate);
            congTacPhiItem.setTemplateItemGroup(cacKhoanThuNhapKhongChiuThue);
            templateItems.add(congTacPhiItem);

            // Số người phụ thuộc
            SalaryTemplateItem soNguoiPhuThuocItem = new SalaryTemplateItem();
            SalaryItem soNguoiPhuThuocSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.SO_NGUOI_PHU_THUOC_SYSTEM.getValue()).get(0);
            soNguoiPhuThuocItem.setDisplayOrder(15);
            soNguoiPhuThuocItem.setDisplayName("Số người phụ thuộc");
            soNguoiPhuThuocItem.setDescription(soNguoiPhuThuocSI.getDescription());
            soNguoiPhuThuocItem.setSalaryItem(soNguoiPhuThuocSI);
            soNguoiPhuThuocItem.setSalaryTemplate(amisTemplate);
            templateItems.add(soNguoiPhuThuocItem);

            // Giảm trừ gia cảnh
            SalaryTemplateItem giamTruGiaCanhItem = new SalaryTemplateItem();
            SalaryItem giamTruGiaCanhSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.GIAM_TRU_GIA_CANH_SYSTEM.getValue()).get(0);
            giamTruGiaCanhItem.setDisplayOrder(16);
            giamTruGiaCanhItem.setDisplayName("Giảm trừ gia cảnh");
            giamTruGiaCanhItem.setDescription(giamTruGiaCanhSI.getDescription());
            giamTruGiaCanhItem.setSalaryItem(giamTruGiaCanhSI);
            giamTruGiaCanhItem.setSalaryTemplate(amisTemplate);
            templateItems.add(giamTruGiaCanhItem);

            // Thu nhập chịu thuế
            SalaryTemplateItem thuNhapChiuThueItem = new SalaryTemplateItem();
            SalaryItem thuNhapChiuThueSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.THU_NHAP_CHIU_THUE_SYSTEM.getValue()).get(0);
            thuNhapChiuThueItem.setDisplayOrder(17);
            thuNhapChiuThueItem.setDisplayName("Thu nhập chịu thuế");
            thuNhapChiuThueItem.setDescription(thuNhapChiuThueSI.getDescription());
            thuNhapChiuThueItem.setSalaryItem(thuNhapChiuThueSI);
            thuNhapChiuThueItem.setSalaryTemplate(amisTemplate);
            templateItems.add(thuNhapChiuThueItem);

            // Thu nhập tính thuế
            SalaryTemplateItem thuNhapTinhThueItem = new SalaryTemplateItem();
            SalaryItem thuNhapTinhThueSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.THU_NHAP_TINH_THUE_SYSTEM.getValue()).get(0);
            thuNhapTinhThueItem.setDisplayOrder(18);
            thuNhapTinhThueItem.setDisplayName("Thu nhập tính thuế");
            thuNhapTinhThueItem.setDescription(thuNhapTinhThueSI.getDescription());
            thuNhapTinhThueItem.setSalaryItem(thuNhapTinhThueSI);
            thuNhapTinhThueItem.setSalaryTemplate(amisTemplate);
            templateItems.add(thuNhapTinhThueItem);

            // BHXH (8%)
            SalaryTemplateItem bhxhItem = new SalaryTemplateItem();
            SalaryItem bhxhSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.BH_XA_HOI_SYSTEM.getValue()).get(0);
            bhxhItem.setDisplayOrder(19);
            bhxhItem.setDisplayName("BHXH (8%)");
            bhxhItem.setDescription(bhxhSI.getDescription());
            bhxhItem.setSalaryItem(bhxhSI);
            bhxhItem.setSalaryTemplate(amisTemplate);
            bhxhItem.setTemplateItemGroup(cacKhoanKhauTru);
            templateItems.add(bhxhItem);

            // BHYT (1.5%)
            SalaryTemplateItem bhytItem = new SalaryTemplateItem();
            SalaryItem bhytSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.BH_Y_TE_SYSTEM.getValue()).get(0);
            bhytItem.setDisplayOrder(20);
            bhytItem.setDisplayName("BHYT (1.5%)");
            bhytItem.setDescription(bhytSI.getDescription());
            bhytItem.setSalaryItem(bhytSI);
            bhytItem.setSalaryTemplate(amisTemplate);
            bhytItem.setTemplateItemGroup(cacKhoanKhauTru);
            templateItems.add(bhytItem);

            // BHTN (1%)
            SalaryTemplateItem bhtnItem = new SalaryTemplateItem();
            SalaryItem bhtnSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.BH_THAT_NGHIEP_SYSTEM.getValue()).get(0);
            bhtnItem.setDisplayOrder(21);
            bhtnItem.setDisplayName("BHTN (1%)");
            bhtnItem.setDescription(bhtnSI.getDescription());
            bhtnItem.setSalaryItem(bhtnSI);
            bhtnItem.setSalaryTemplate(amisTemplate);
            bhtnItem.setTemplateItemGroup(cacKhoanKhauTru);
            templateItems.add(bhtnItem);

            // Thuế TNCN
            SalaryTemplateItem thueTNCNItem = new SalaryTemplateItem();
            SalaryItem thueTNCNSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.THUE_TNCN_SYSTEM.getValue()).get(0);
            thueTNCNItem.setDisplayOrder(22);
            thueTNCNItem.setDisplayName("Thuế TNCN");
            thueTNCNItem.setDescription(thueTNCNSI.getDescription());
            thueTNCNItem.setSalaryItem(thueTNCNSI);
            thueTNCNItem.setSalaryTemplate(amisTemplate);
            thueTNCNItem.setTemplateItemGroup(cacKhoanKhauTru);
            templateItems.add(thueTNCNItem);

            // BHXH (17%)
            SalaryTemplateItem bhxhCTItem = new SalaryTemplateItem();
            SalaryItem bhxhCTSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.BH_XA_HOI_CONG_TY_DONG_SYSTEM.getValue()).get(0);
            bhxhCTItem.setDisplayOrder(23);
            bhxhCTItem.setDisplayName("BHXH (17%)");
            bhxhCTItem.setDescription(bhxhCTSI.getDescription());
            bhxhCTItem.setSalaryItem(bhxhCTSI);
            bhxhCTItem.setSalaryTemplate(amisTemplate);
            bhxhCTItem.setTemplateItemGroup(congTyDong);
            templateItems.add(bhxhCTItem);

            // BHYT (3%)
            SalaryTemplateItem bhytCTItem = new SalaryTemplateItem();
            SalaryItem bhytCTSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.BH_Y_TE_CONG_TY_DONG_SYSTEM.getValue()).get(0);
            bhytCTItem.setDisplayOrder(24);
            bhytCTItem.setDisplayName("BHYT (3%)");
            bhytCTItem.setDescription(bhytCTSI.getDescription());
            bhytCTItem.setSalaryItem(bhytCTSI);
            bhytCTItem.setSalaryTemplate(amisTemplate);
            bhytCTItem.setTemplateItemGroup(congTyDong);
            templateItems.add(bhytCTItem);

            // BHTN (1%)
            SalaryTemplateItem bhtnCTItem = new SalaryTemplateItem();
            SalaryItem bhtnCTSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.BH_THAT_NGHIEP_CONG_TY_DONG_SYSTEM.getValue()).get(0);
            bhtnCTItem.setDisplayOrder(25);
            bhtnCTItem.setDisplayName("BHTN (1%)");
            bhtnCTItem.setDescription(bhtnCTSI.getDescription());
            bhtnCTItem.setSalaryItem(bhtnCTSI);
            bhtnCTItem.setSalaryTemplate(amisTemplate);
            bhtnCTItem.setTemplateItemGroup(congTyDong);
            templateItems.add(bhtnCTItem);

            // KPCĐ (2%)
            SalaryTemplateItem kpcdItem = new SalaryTemplateItem();
            SalaryItem kpcdSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.KHOAN_PHI_CONG_DOAN_CONG_TY_DONG_SYSTEM.getValue()).get(0);
            kpcdItem.setDisplayOrder(26);
            kpcdItem.setDisplayName("KPCĐ (2%)");
            kpcdItem.setDescription(kpcdSI.getDescription());
            kpcdItem.setSalaryItem(kpcdSI);
            kpcdItem.setSalaryTemplate(amisTemplate);
            kpcdItem.setTemplateItemGroup(congTyDong);
            templateItems.add(kpcdItem);

            // Tổng thu nhập
            SalaryTemplateItem tongThuNhapItem = new SalaryTemplateItem();
            SalaryItem tongThuNhapSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.TONG_THU_NHAP_SYSTEM.getValue()).get(0);
            tongThuNhapItem.setDisplayOrder(27);
            tongThuNhapItem.setDisplayName("Tổng thu nhập");
            tongThuNhapItem.setDescription(tongThuNhapSI.getDescription());
            tongThuNhapItem.setSalaryItem(tongThuNhapSI);
            tongThuNhapItem.setSalaryTemplate(amisTemplate);
            templateItems.add(tongThuNhapItem);

            // Tổng khấu trừ
            SalaryTemplateItem tongKhauTruItem = new SalaryTemplateItem();
            SalaryItem tongKhauTruSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.TONG_KHAU_TRU_SYSTEM.getValue()).get(0);
            tongKhauTruItem.setDisplayOrder(28);
            tongKhauTruItem.setDisplayName("Tổng khấu trừ");
            tongKhauTruItem.setDescription(tongKhauTruSI.getDescription());
            tongKhauTruItem.setSalaryItem(tongKhauTruSI);
            tongKhauTruItem.setSalaryTemplate(amisTemplate);
            templateItems.add(tongKhauTruItem);

            // Lương kỳ này
            SalaryTemplateItem luongKyNayItem = new SalaryTemplateItem();
            SalaryItem luongKyNaySI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.LUONG_KY_NAY_SYSTEM.getValue()).get(0);
            luongKyNayItem.setDisplayOrder(29);
            luongKyNayItem.setDisplayName("Lương kỳ này");
            luongKyNayItem.setDescription(luongKyNaySI.getDescription());
            luongKyNayItem.setSalaryItem(luongKyNaySI);
            luongKyNayItem.setSalaryTemplate(amisTemplate);
            templateItems.add(luongKyNayItem);

            // Tạm ứng
            SalaryTemplateItem tamUngItem = new SalaryTemplateItem();
            SalaryItem tamUngSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.TAM_UNG_SYSTEM.getValue()).get(0);
            tamUngItem.setDisplayOrder(30);
            tamUngItem.setDisplayName("Tạm ứng");
            tamUngItem.setDescription(tamUngSI.getDescription());
            tamUngItem.setSalaryItem(tamUngSI);
            tamUngItem.setSalaryTemplate(amisTemplate);
            templateItems.add(tamUngItem);

            // Thực lĩnh
            SalaryTemplateItem thucLinhItem = new SalaryTemplateItem();
            SalaryItem thucLinhSI = salaryItemRepository.findByCode(HrConstants.SalaryItemCodeSystemDefault.THUC_LINH_SYSTEM.getValue()).get(0);
            thucLinhItem.setDisplayOrder(31);
            thucLinhItem.setDisplayName("Thực lĩnh");
            thucLinhItem.setDescription(thucLinhSI.getDescription());
            thucLinhItem.setSalaryItem(thucLinhSI);
            thucLinhItem.setSalaryTemplate(amisTemplate);
            templateItems.add(thucLinhItem);

            amisTemplate.getTemplateItems().clear();
            amisTemplate.getTemplateItems().addAll(templateItems);

            SalaryTemplate response = salaryTemplateRepository.save(amisTemplate);
        }
    }

    @Override
    public void setupSalaryInsuranceForStaffByCurrentActiveAgreement() {
        SearchStaffDto searchStaffDto = new SearchStaffDto();
        searchStaffDto.setHasSocialIns(true);
        List<StaffDto> availableStaffs = staffService.getListStaff(searchStaffDto);

        if (availableStaffs != null && !availableStaffs.isEmpty()) {
            for (StaffDto staff : availableStaffs) {
                staffService.handleSetValueForCurrentInsuranceSalary(staff.getId());
            }
        }
    }

    @Override
    public void setupAllowanceType() {
        this.createAllowanceType("Phụ cấp kiêm nhiệm", "01");
        this.createAllowanceType("Phụ cấp thâm niên vượt khung", "02");
        this.createAllowanceType("Phụ cấp khu vực", "03");
        this.createAllowanceType("Phụ cấp trách nhiệm công việc", "04");
        this.createAllowanceType("Phụ cấp lưu động", "05");
        this.createAllowanceType("Phụ cấp ưu đãi theo nghề", "06");
        this.createAllowanceType("Phụ cấp công tác ở vùng có điều kiện kinh tế - xã hội đặc biệt khó khăn", "07");
        this.createAllowanceType("Phụ cấp theo phân loại đơn vị hành chính và theo phân hạng đơn vị sự nghiệp công lập", "08");
        this.createAllowanceType("Phụ cấp áp dụng riêng đối với lực lượng vũ trang", "09");
    }


    public void createAllowanceType(String name, String code) {
        Boolean isExisting = allowanceTypeService.checkCode(null, code);
        if (!isExisting) {
            AllowanceTypeDto result = new AllowanceTypeDto();
            result.setName(name);
            result.setCode(code);
            allowanceTypeService.saveOrUpdate(result, null);
        }
    }

    @Override
    public void setupLeaveType() {
        this.createLeaveType(
                HrConstants.LeaveTypeCode.ANNUAL_LEAVE.getName(),
                HrConstants.LeaveTypeCode.ANNUAL_LEAVE.getCode(),
                "Nhân viên có thể sử dụng ngày nghỉ phép để nghỉ ngơi, " +
                        "du lịch, giải quyết công việc cá nhân hoặc chăm sóc gia đình.",
                true, true);
        this.createLeaveType(
                HrConstants.LeaveTypeCode.HALF_ANNUAL_LEAVE.getName(), HrConstants.LeaveTypeCode.HALF_ANNUAL_LEAVE.getCode(),
                "Nhân viên có thể sử dụng ngày nghỉ phép để nghỉ ngơi, du lịch, giải quyết công việc cá nhân hoặc chăm sóc gia đình.",
                true, false);
        this.createLeaveType(
                HrConstants.LeaveTypeCode.BUSINESS_TRIP.getName(), HrConstants.LeaveTypeCode.BUSINESS_TRIP.getCode(),
                "Nhân viên tạm thời rời khỏi nơi làm việc để thực hiện nhiệm vụ, công việc theo yêu cầu của công ty.",
                true, true);
        this.createLeaveType(
                HrConstants.LeaveTypeCode.HALF_BUSINESS_TRIP.getName(), HrConstants.LeaveTypeCode.HALF_BUSINESS_TRIP.getCode(),
                "Nhân viên tạm thời rời khỏi nơi làm việc để thực hiện nhiệm vụ, công việc theo yêu cầu của công ty.",
                true, false);
        this.createLeaveType(
                HrConstants.LeaveTypeCode.UNPAID_LEAVE.getName(), HrConstants.LeaveTypeCode.UNPAID_LEAVE.getCode(),
                "Nhân viên có thể xin nghỉ không lương vì lý do cá nhân, gia đình hoặc các trường hợp đặc biệt khác khi đã sử dụng hết ngày phép.",
                false, true);
        this.createLeaveType(
                HrConstants.LeaveTypeCode.HALF_UNPAID_LEAVE.getName(),
                HrConstants.LeaveTypeCode.HALF_UNPAID_LEAVE.getCode(),
                "Nhân viên có thể xin nghỉ không lương vì lý do cá nhân, gia đình hoặc các trường hợp đặc biệt khác khi đã sử dụng hết ngày phép.",
                false, false);
        this.createLeaveType(
                HrConstants.LeaveTypeCode.PUBLIC_HOLIDAY.getName(),
                HrConstants.LeaveTypeCode.PUBLIC_HOLIDAY.getCode(),
                "Nhân viên được nghỉ theo các ngày lễ, Tết theo quy định của pháp luật (ví dụ: Tết Nguyên Đán, Quốc khánh, Giỗ tổ Hùng Vương,...).",
                true, true);
        this.createLeaveType(
                HrConstants.LeaveTypeCode.HALF_PUBLIC_HOLIDAY.getName(),
                HrConstants.LeaveTypeCode.HALF_PUBLIC_HOLIDAY.getCode(),
                "Nhân viên được nghỉ theo các ngày lễ, Tết theo quy định của pháp luật (ví dụ: Tết Nguyên Đán, Quốc khánh, Giỗ tổ Hùng Vương,...).",
                true, false);
        this.createLeaveType(
                HrConstants.LeaveTypeCode.COMPENSATORY_LEAVE.getName(),
                HrConstants.LeaveTypeCode.COMPENSATORY_LEAVE.getCode(),
                "Áp dụng cho nhân viên làm việc vào các ngày nghỉ lễ hoặc ngoài giờ theo yêu cầu của công ty.",
                true, true);
        this.createLeaveType(
                HrConstants.LeaveTypeCode.HALF_COMPENSATORY_LEAVE.getName(),
                HrConstants.LeaveTypeCode.HALF_COMPENSATORY_LEAVE.getCode(),
                "Áp dụng cho nhân viên làm việc vào các ngày nghỉ lễ hoặc ngoài giờ theo yêu cầu của công ty.",
                true, false);
        this.createLeaveType(
                HrConstants.LeaveTypeCode.SPECIAL_LEAVE.getName(),
                HrConstants.LeaveTypeCode.SPECIAL_LEAVE.getCode(),
                "Là loại nghỉ dành cho nhân viên theo quy định của luật lao động, như nghỉ thai sản, nghỉ ốm đau, nghỉ chăm sóc con nhỏ,...",
                true, true);
        this.createLeaveType(
                HrConstants.LeaveTypeCode.HALF_SPECIAL_LEAVE.getName(),
                HrConstants.LeaveTypeCode.HALF_SPECIAL_LEAVE.getCode(),
                "Là loại nghỉ dành cho nhân viên theo quy định của luật lao động, như nghỉ thai sản, nghỉ ốm đau, nghỉ chăm sóc con nhỏ,...",
                true, false);
        this.createLeaveType(
                HrConstants.LeaveTypeCode.YEAR_LEAVE.getName(),
                HrConstants.LeaveTypeCode.YEAR_LEAVE.getCode(),
                "Loại nghỉ phép năm được áp dụng theo quy định của luật lao động, bao gồm các trường hợp như nghỉ ốm đau, nghỉ thai sản, hoặc nghỉ chăm sóc con nhỏ.",
                true, true);
    }

    public void createLeaveType(String name, String code, String description, Boolean isPaid, Boolean usedForRequest) {
        LeaveTypeDto leaveType = new LeaveTypeDto(name, code, description, isPaid, usedForRequest);

//        Boolean isValid = leaveTypeService.isValidCode(leaveType);

//        if (isValid) {
        leaveTypeService.saveOrUpdate(leaveType);
//        }
    }

    // Tự render thêm các Kết nối thành pần lương tương ứng trong HrConstants.SalaryAutoMapField
    @Override
    public void setupSalaryAutoMap() {
        List<HrConstants.SalaryAutoMapField> salaryFields = HrConstants.SalaryAutoMapField.getList();
        if (salaryFields != null && salaryFields.size() > 0) {
            salaryFields.forEach(field -> {
                List<SalaryAutoMap> listSalaryAutoMap = salaryAutoMapService.getBySalaryAutoMapField(field);
                if (listSalaryAutoMap == null || CollectionUtils.isEmpty(listSalaryAutoMap)) {
                    SalaryAutoMapDto salaryAutoMapDto = new SalaryAutoMapDto();
                    salaryAutoMapDto.setSalaryAutoMapField(field.getValue());
                    salaryAutoMapDto.setDescription(field.getDescription());
                    salaryAutoMapService.saveOrUpdate(salaryAutoMapDto);
                }
            });
        }

    }


    @Override
    public void setupDefaultSystemConfig() {
        List<SystemConfigDto> defaultSystemConfigs = new ArrayList<>();

        // Thêm các cấu hình mặc định
        // Các cấu hình liên quan đến chạy job sinh tự động lịch làm việc cố định của nhân viên trong tháng
        defaultSystemConfigs.add(new SystemConfigDto(HrConstants.SystemConfigCode.GEN_FIXED_SCHEDULES_DAY.getCode(),
                "28", HrConstants.SystemConfigCode.GEN_FIXED_SCHEDULES_DAY.getName()));
        defaultSystemConfigs.add(new SystemConfigDto(HrConstants.SystemConfigCode.GEN_FIXED_SCHEDULES_HOUR_AND_MINUTE.getCode(),
                "00:00", HrConstants.SystemConfigCode.GEN_FIXED_SCHEDULES_HOUR_AND_MINUTE.getName()));

        defaultSystemConfigs.add(new SystemConfigDto(HrConstants.SystemConfigCode.TAX_SALARYTEMPLATE_CODE.getCode(),
                HrConstants.MAU_BANG_LUONG_THUE, HrConstants.SystemConfigCode.TAX_SALARYTEMPLATE_CODE.getName()));

        defaultSystemConfigs.add(new SystemConfigDto(HrConstants.SystemConfigCode.ACTUAL_SALARYTEMPLATE_CODE.getCode(),

                HrConstants.MAU_BANG_LUONG_THUC_TE, HrConstants.SystemConfigCode.ACTUAL_SALARYTEMPLATE_CODE.getName()));

        defaultSystemConfigs.add(new SystemConfigDto(HrConstants.SystemConfigCode.BASE_WAGE_SALARYITEM_CODE.getCode(),
                "LUONG_CO_BAN", HrConstants.SystemConfigCode.BASE_WAGE_SALARYITEM_CODE.getName()));

        defaultSystemConfigs.add(new SystemConfigDto(HrConstants.SystemConfigCode.MIN_OT_MINUTES_TO_SHOW_CONFIRM.getCode(),
                "30", HrConstants.SystemConfigCode.MIN_OT_MINUTES_TO_SHOW_CONFIRM.getName()));

        defaultSystemConfigs.add(new SystemConfigDto(HrConstants.SystemConfigCode.INSURANCE_AMOUNT_SALARY_ITEM_CODE.getCode(),
                "LUONG_THAM_GIA_BAO_HIEM_XA_HOI_THUE", HrConstants.SystemConfigCode.INSURANCE_AMOUNT_SALARY_ITEM_CODE.getName()));




        for (SystemConfigDto dto : defaultSystemConfigs) {
            SystemConfigDto existedItem = systemConfigService.getByKeyCode(dto.getConfigKey());

            if (existedItem == null || existedItem.getId() == null) {
                systemConfigService.saveOrUpdate(dto);
            }
        }
    }


}
