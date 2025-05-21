package com.globits.hr.service;

public interface SetupDataService {
    // setup roles
    public void setupRoles();

    public void setupAdminUser();

    public void setupStaffUser();

    // tao moi loai nhan vien/phan loai cong chuc
    public void setupCivilServantType();

    // Tạo mặc định 1 tổ chức, gắn tất cả user vào tổ chức đó(bảng
    // tbl_organization_user),
    // tạm thời mỗi user thì thuộc 1 công ty dù thiết kế là manytomany
    public void setupDefaultOrganizationForAllCurrentUsers();

    // handle for generating code of old tasks in projects
// public void generateCodeForAllTasksInAllProjects();

    // tạo vùng lương
    public void setupDefaultSalaryAreas();

    // Setup data Các thành phần lương mặc định của hệ thống
    // Định nghĩa thêm ở HrConstants.SalaryItemCodeSystemDefault
    public void setupDefaultSalaryItems();

    // Setup data Các thành phần lương khác (không phải mặc định)
    // Định nghĩa thêm ở HrConstants.SalaryItemCodeSetup
    public void setupDefaultOtherSalaryItems();

    // setup data for salaryUnit
    public void setupDefaultSalaryUnits();

    // setup data for salary Type
    public void setupDefaultSalaryTypes();

    // setup data for salary config - cau hinh luong thuong
    public void setupDefaultSalaryConfigs();

    // setup data for recruitment exam type - loai bai kiem tra tuyen dung
    public void setupDefaultRecruitmentExamType();

    // setup data for family relationship - loai quan he (nguoi than)
    public void setupDefaultFamilyRelationship();

    // setup data for discipline reason - ly do ky luat
    public void setupDefaultDisciplineReason();

    // setup data for refusal reason - ly do tu choi
    public void setupDefaultRefusalReason();

    // setup data for deferred type - loai tam hoan
    public void setupDefaultDeferredType();

    // setup data for deferred type - loai dieu chuyen
    public void setupDefaultTransferType();

    // setup data for rank title - cap bac
    public void setupDefaultRankTitle();

    // setup data for position title - chuc danh
    public void setupDefaultPositionTitle();

    // setup data for department group - nhom phong ban
    public void setupDefaultDepartmentGroup();

    // setup data for department type - loai phong ban
    public void setupDefaultDepartmentType();

    // setup data for department - phong ban
    public void setupDefaultDepartment();

    // setup data for contract type - loai hop dong
    public void setupDefaultContractType();

    // setup data for addendum type - loai phu luc
    public void setupDefaultAddendumType();

    // setup data for staff type - loai nhan vien
    public void setupDefaultStaffType();

    // setup data for position role - nhom quyen mac dinh
    public void setupDefaultPositionRole();

    // setup data for employee status - tinh trang nhan vien
    public void setupDefaultEmployeeStatus();

    // setup data for reward form - loai khen thuong
    public void setupDefaultRewardForm();

    // setup data for position - Chuc vu
    public void setupDefaultPosition();

    // setup data for hrSpeciality - Chuyen nganh dao tao
    public void setupDefaultHrSpeciality();

    // setup data for Certificate - Chung chi
    public void setupDefaultCertificate();

    // setup data for Country - Quoc gia
    public void setupDefaultCountry();

    // setup data for Ethnics - Dan toc
    public void setupDefaultEthnics();

    // setup data for Educational Institution - Co so dao tao
    public void setupDefaultEducationalInstitution();

    // setup data for Religion - Ton giao
    public void setupDefaultReligion();

    // setup data for HrEducationType - Loại hình đào tạo
    public void setupDefaultHrEducationType();

    // setup data for EducationDegree - Trình độ học vấn
    public void setupDefaultEducationDegree();

    // setup data for ShiftWork and ShiftWorkTimePeriod - Ca làm việc và giai đoạn giờ giấc trong ca làm việc
    public void setupDefaultShiftWorkAndShiftWorkTimePeriod();

    // setup data for Profession
    public void setupProfession();

    // setup MISA AMIS salaryTemplate - URL: https://helpamis.misa.vn/amis-tien-luong/kb/huong-dan-chung-luong-nghiep-vu-tinh-luong-tong-quan-tren-amis-tien-luong/
    public void setupMisaAmisSalaryTemplate();

    // setup salary insurance for staff by current active labour aggreements
    public void setupSalaryInsuranceForStaffByCurrentActiveAgreement();

    public void setupAllowanceType();

    public void setupLeaveType();

    public void setupSalaryAutoMap();

    // Cấu hình mặc định của hệ thống
    public void setupDefaultSystemConfig();
}
