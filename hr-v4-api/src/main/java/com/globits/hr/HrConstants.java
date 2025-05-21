package com.globits.hr;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HrConstants {
    public static final String ROLE_HR_MANAGEMENT = "ROLE_HR_MANAGEMENT";
    //    public static final String ROLE_STUDENT_MANAGERMENT = "ROLE_STUDENT_MANAGERMENT";
//    public static final String ROLE_EDUCATION_MANAGERMENT = "ROLE_EDUCATION_MANAGERMENT";
//    //
    public static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String HR_USER = "HR_USER";
    public static final String HR_MANAGER = "HR_MANAGER";
    public static final String HR_RECRUITMENT = "HR_RECRUITMENT";
    public static final String HR_ASSIGNMENT_ROLE = "HR_ASSIGNMENT_ROLE";
    public static final String HR_INSURANCE_MANAGER = "HR_INSURANCE_MANAGER";
    public static final String HR_STAFF_VIEW = "HR_STAFF_VIEW";
    public static final String HR_VIEW_RECRUITMENT_REQUEST = "HR_VIEW_RECRUITMENT_REQUEST"; //
    public static final String SUPER_HR = "SUPER_HR"; //
    public static final String HR_APPROVAL_RECRUITMENT_REQUEST = "HR_APPROVAL_RECRUITMENT_REQUEST"; //
    public static final String HR_CREATE_RECRUITMENT_REQUEST = "HR_CREATE_RECRUITMENT_REQUEST"; //
    public static final String PB_HCNS = "PB_0011";
    public static final String HR_ASSIGNMENT = "HR_ASSIGNMENT";
    public static final String HR_FROMDATE_SYNC_TIMESHEET = "fromDateSyncTimeSheet";
    public static final String HR_TODATE_SYNC_TIMESHEET = "toDateSyncTimeSheet";
    public static final String HR_HOUR_SYNC_TIMESHEET = "hourTimeSheet";
    public static final String HR_SYNC_TIMESHEET = "syncTimeSheet";
    public static final String HR_URL_TIMESHEET = "urlApiTimeSheet";
    public static final String HR_URL_TIMESHEET_VALUE = "https://prod-61.southeastasia.logic.azure.com:443/workflows/4ebd4d253f894a2aad3944a7e60958b5/triggers/manual/paths/invoke?api-version=2016-06-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=sEsxMu-7IT0YGy3cFE9qgb9p6vsCNONlKFcOZagG6Vo";
    public static final String APP_CODE = "HR_APPLICATION";
    public static final String STRING_FORMAT_CANDIDATE_CODE = "%06d";
    public static final String HR_TIMESHEET_ONE_TIME_LOCK = "oneTimeLock";
    public static final String MAU_0003 = "MAU_0003";

    //    public static final String ROLE_STUDENT = "ROLE_STUDENT";
//    public static final String ROLE_CANDIDATE = "ROLE_CANDIDATE";
//
    public static final String HR_TESTER = "HR_TESTER";
//    public static final String ROLE_EXAM_MANAGERMENT = "ROLE_EXAM_MANAGERMENT";
//    public static final String ROLE_FINANCIAL_MANAGERMENT = "ROLE_FINANCIAL_MANAGERMENT";

    public static final String IS_POSITION_MANAGER = "IS_POSITION_MANAGER"; // là trưởng phòng
    public static final String IS_GENERAL_DIRECTOR = "IS_GENERAL_DIRECTOR"; // là TGĐ
    public static final String IS_DEPUTY_GENERAL_DIRECTOR = "IS_DEPUTY_GENERAL_DIRECTOR"; // là phó TGĐ

    // Danh sách vai trò hệ thống
    public enum SystemRole {
        ROLE_SUPER_ADMIN("ROLE_SUPER_ADMIN", "The highest administrative role with full system control and access"),
        ROLE_ADMIN("ROLE_ADMIN", "The general administrative role with high-level system access and management capabilities"),
        ROLE_USER("ROLE_USER", "A standard user role with limited access to basic system features"),
        HR_MANAGER("HR_MANAGER", "The role responsible for overseeing all HR functions, including employee management and HR policies"),
        HR_USER("HR_USER", "A general HR role with access to employee records and HR-related functionalities"),
        HR_RECRUITMENT("HR_RECRUITMENT", "The role responsible for handling recruitment processes, including job postings and candidate management"),
        HR_INSURANCE_MANAGER("HR_INSURANCE_MANAGER", "The role responsible for managing employee insurance policies and benefits"),
        HR_ASSIGNMENT_ROLE("HR_ASSIGNMENT_ROLE", "The role responsible for assigning employees to specific projects or departments"),
        HR_TESTER("HR_TESTER", "The role responsible for testing HR-related functionalities and ensuring system reliability");

        private final String value;
        private final String description;

        SystemRole(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static String getDescriptionByValue(String value) {
            if (value == null || value.isEmpty()) return "";
            for (SystemRole role : SystemRole.values()) {
                if (role.getValue().equals(value)) {
                    return role.getDescription();
                }
            }
            return "";
        }
    }


    // global property
    public static final String GLOBAL_PROPERTY_WORKING_STATUS_INPROCESS = "working-status-inprocess";
    public static final String GLOBAL_PROPERTY_WORKING_STATUS_COMPLETED = "working-status-completed";
    public static final String GLOBAL_PROPERTY_WORKING_STATUS_PAUSE = "working-status-pause";
    public static final String GLOBAL_PROPERTY_ACTIVE_STATUS_WORKING = "active-status-working";

    public static final String MAU_BANG_LUONG_THUE = "ML_02_V2";
    public static final String MAU_BANG_LUONG_THUC_TE = "ML_01_V2";

    // Số phút nhân viên mang thai được phép đi muộn/về sớm trong mỗi ca làm việc
    public static final Integer EARLY_LATE_MINS_FOR_PREGNANT_STAFF = 60;

    // % làm đủ ca
    public static final double WORK_ENOUGH_THRESHOLD = 0.9;

    // Giờ công quy đổi tiêu chuẩn của nhân viên
    public static final double STANDARD_CONVERTED_WORKING_HOURS = 8.0;

    // canh bao hop dong het han truoc ... ngay
    public static final int CONTRACT_PRE_EXPIRY_DAYS = 30;

    // Số ngày nghỉ phép ít nhất trong 1 năm
    public static final double BASE_ANNUAL_LEAVE_DAYS = 12.0;

    public static final String COMMON_STAFF_PROFILE_TEMPLATE_CODE = "HSNS_CHUNG";

    public enum EmployeeStatusCodeEnum {

        WAITING_RECEPTION("CHO_NHAN_VIEC", "Chờ nhận việc"),
        QUITED("DA_NGHI_VIEC", "Đã nghỉ việc"),
        WORKING("DANG_LAM_VIEC", "Đang làm việc"),
        NOT_RECEIVE_JOB("KHONG_NHAN_VIEC", "Không nhận việc"),
        BACK_TO_WORK("NGHI_VIEC_DI_LAM_LAI", "Nghỉ việc đi làm lại"),
        TEMPORARY_PAUSE("TAM_DUNG_CONG_VIEC", "Tạm dừng công việc"),
        SPECIAL_LEAVE("NGHI_CHE_DO", "Nghỉ chế độ"),
        UNPAID_LEAVE("NGHI_KHONG_LUONG", "Nghỉ không lương");

        private final String value;

        private final String description;

        EmployeeStatusCodeEnum(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return this.description;
        }
    }

    public enum CertificateTypeEnum {
        EnglishCertificate(1), // Chứng chỉ tiếng anh
        EnglishLevel(0), // Trình độ tiếng anh
        PoliticalTheoryLevel(2), // Trình độ lý luận chính trị;
        StateManagementQualifications(3), // Trình độ quản lý nhà nước;
        InformaticDegree(4), // Trình độ tin học
        EducationalManagementQualifications(5);// Trình độ quản lý giáo dục

        private int value;

        CertificateTypeEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum WorkingFormatEnum {
        off(-1), // Nghỉ làm
        onsite(0), // Đi làm bình thường tại văn phòng
        online(1), // Làm online từ xa
        out_office(2);// Đi công tác (gặp khách hàng, tập huấn, ...)

        private int value;

        WorkingFormatEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum RecruitmentRequestStatus {
        CREATED(0, "Chưa duyệt"),
        SENT(1, "Gửi cho cấp trên"),
        APPROVED(2, "Duyệt"),
        REJECTED(3, "Từ chối"),
        HR_LEADER(4, "Trưởng phòng nhân sự"),
        START_RECRUITING(5, "Bắt đầu tuyển dụng"),
        RECRUITING(6, "Đang tuyển dụng"),
        STOP(7, "Đang tuyển dụng");

        private final int value;
        private final String description;

        RecruitmentRequestStatus(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static String getDescriptionByValue(int value) {
            for (RecruitmentRequestStatus status : RecruitmentRequestStatus.values()) {
                if (status.getValue() == value) {
                    return status.getDescription();
                }
            }
            return null; // hoặc return "Không xác định"
        }
    }


    public enum RecruitmentPlanStatus {
        NOT_APPROVED_YET(0), // Chưa duyệt
        APPROVED(1), // Đã duyệt
        REJECTED(2), // Đã từ chối
        COMPLETED(3); // Đã hoàn thành

        private int value;

        RecruitmentPlanStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum CandidateStatus {
        NOT_APPROVED_YET(1, "Chờ duyệt"), // Chưa duyệt
        SCREENED_PASS(2, "Đã sơ lọc"), // Đã sơ lọc
        NOT_SCREENED(3, "Không qua sơ lọc"), // Không qua sơ lọc
        APPROVED(4, "Đã duyệt"), // Đã duyệt
        REJECTED(5, "Đã từ chối"), // Đã từ chối
        NOT_RESULT_YET(7, "Chưa có kết quả"), // Chưa có kết quả
        CV_NOT_APPROVED(8, "Không được duyệt CV"),// Không được duyệt CV
        PENDING_CANDIDATE_CONFIRMATION(9, "Chờ ứng viên xác nhận"), // Chờ ứng viên xác nhận
        PENDING_ASSIGNMENT(10, "Chờ nhận việc"),
        ACCEPTED_ASSIGNMENT(11, "Đã nhận việc"),
        DECLINED_ASSIGNMENT(12, "Từ chối nhận việc"),// = Từ chối offer
        APPROVE_CV(13, "Gửi duyệt CV"),
        REFUSE_OFFER(14, "Từ chối offer"), // th
        RESIGN(15, "Nghỉ việc trong lúc thử việc"),
        SEND_OFFER(16, "Gửi offer");


        private int value;
        private String description;

        CandidateStatus(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

    }

    public enum CandidateApprovalStatus {
        NOT_APPROVED_YET(1), // Chưa duyệt
        SCREENED_PASS(2), // Đã sơ lọc
        NOT_SCREENED(3), // Không qua sơ lọc
        APPROVED(4), // Đã duyệt
        REJECTED(5); // Đã từ chối

        private int value;

        CandidateApprovalStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // Loại tệp đính kèm trong hồ sơ ứng viên
    public enum CandidateAttachmentType {
        CV_RESUME(1, "CV/Resume"), // CV, Resume
        COVER_LETTER(2, "Thư ứng tuyển"), // Thư ứng tuyển
        PORTFOLIO(3, "Hồ sơ năng lực"), // Hồ sơ năng lực
        APPLICATION_FORM(4, "Đơn ứng tuyển (ứng viên điền theo mẫu công ty cung cấp)"), // Đơn ứng tuyển (ứng viên điền
        // theo mẫu công ty cung cấp)
        INTERVIEW_INVITATION(5, "Thư mời phỏng vấn"), CERTIFICATES_DEGREES(6, "Chứng chỉ, bẳng cấp"),
        OFFER_LETTER(7, "Thư mời nhận việc"), PERSONAL_DOCUMENTS(8, "Giấy tờ cá nhân"),
        REJECTION_LETTER(9, "Thư từ chối"), REFERENCE_LETTER(10, "Thư giới thiệu"), OTHERS(11, "Khác");

        private int value;
        private String name;

        CandidateAttachmentType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }

    public enum PreScreenStatus {
        PENDING(1, "Chờ sơ lọc"),
        PASSED(2, "Đạt"),
        FAILED(3, "Không đạt");

        private final int value;
        private final String description;

        PreScreenStatus(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static PreScreenStatus fromValue(int value) {
            for (PreScreenStatus status : PreScreenStatus.values()) {
                if (status.getValue() == value) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid PreScreenStatus value: " + value);
        }
    }

    // HrConstants.CandidateRecruitmentRoundResult
    // Kết quả của ứng viên trong từng vòng tuyển dụng
    public enum CandidateExamStatus {
        NOT_TESTED_YET(1), // chua thuc hien bai test
        PASSED(2), // ung vien Pass
        FAILED(3), // ung vien Fail
        REJECTED(4), // da tu choi (HR tu choi ung vien)
        RECRUITING(5); // Đang dự tuyển

        private int value;

        CandidateExamStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum InterviewScheduleStatus {
        PENDING_CANDIDATE_CONFIRMATION(0, "Chờ ứng viên xác nhận"),
        CANDIDATE_CONFIRMED(1, "Ứng viên đã xác nhận"),
        CANDIDATE_DECLINED(2, "Ứng viên từ chối tham gia"),
        INTERVIEW_COMPLETED(3, "Đã phỏng vấn"),
        CANCELLED(4, "Đã hủy");

        private final int value;
        private final String displayName;

        InterviewScheduleStatus(int value, String displayName) {
            this.value = value;
            this.displayName = displayName;
        }

        public int getValue() {
            return value;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static InterviewScheduleStatus fromValue(int value) {
            for (InterviewScheduleStatus status : values()) {
                if (status.value == value) return status;
            }
            return null;
        }
    }


    public enum CandidateReceptionStatus {
        NOT_RECEPTED_YET(1), // ứng viên chưa được TIẾP NHẬN
        RECEPTED(2), // ứng viên đã được TIẾP NHẬN
        REJECTED(3); // da từ chối TIẾP NHẬN (HR tu choi ung vien)

        private int value;

        CandidateReceptionStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum RelationshipType {
        DIRECT_MANAGER(1), //là quản lý trực tiếp của phòng ban hoặc nhân viên
        INDIRECT_MANAGER(2), //là quản lý gián tiếp của phòng ban hoặc nhân viên
        UNDER_DIRECT_MANAGEMENT(3), // chịu sự quản lý trực tiếp của phòng ban hoặc nhân viên
        UNDER_INDIRECT_MANAGEMENT(4); // chịu sự quản lý gián tiếp của phòng ban hoặc nhân viên

        private int value;

        RelationshipType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum CandidateOnboardStatus {
        WAITING(1), // ung vien ĐANG CHỜ NHẬN VIỆC
        NOT_COME(2), // ung vien khong den nhan viec = ung vien khong nhan viec
        ONBOARDED(3); // ung vien da nhan viec

        private int value;

        CandidateOnboardStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // Tính chất của thành phần lương
    public enum SalaryItemType {
        ADDITION(1), // 1. Thu nhập (+)
        DEDUCTION(2), // 2. Khấu trừ (-)
        INFORMATION(3), // 3. Có thể là thông tin nhân viên, cột hiển thị
        OTHERS(4); // 4. Khác (...)

        private int value;

        SalaryItemType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // Cách tính giá trị của thành phần lương
    public enum SalaryItemCalculationType {
        AUTO_SYSTEM(1), // 1. Hệ thống lấy dữ liệu
        USER_FILL(2), // 2. Tự nhập
        USING_FORMULA(3), // 3. Dùng công thức
        THRESHOLD(4), // 4. Mức ngưỡng
        FIX(5); // 5. Lương cố định

        private int value;

        SalaryItemCalculationType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum InterviewResultType {
        PASS_ACCEPTED(1, "Đạt yêu cầu, chuyển làm thủ tục nhận việc"),
        PASS_RESERVED(2, "Đạt yêu cầu, dự phòng"),
        CONSIDER_OTHER_POSITION(3, "Xem xét vị trí khác"),
        FAIL(4, "Không đạt yêu cầu");

        private final int value;
        private final String description;

        InterviewResultType(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        // Optional: hỗ trợ tìm enum theo value (để map từ DB ra enum)
        public static InterviewResultType fromValue(int value) {
            for (InterviewResultType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid InterviewResultType value: " + value);
        }
    }


    // Kiểu tính ngưỡng
    public enum ConfigType {
        FIX(1), // 1. Lương cố định
        USING_FORMULA(2); // 2. Dùng công thức

        private int value;

        ConfigType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // Các thành phần lương mặc định của hệ thống
    public enum SalaryItemCodeSystemDefault {
        LUONG_CO_BAN_SYSTEM("LUONG_CO_BAN_SYSTEM"), LUONG_DONG_BHXH_SYSTEM("LUONG_DONG_BHXH_SYSTEM"),
        SO_NGAY_CONG_SYSTEM("SO_NGAY_CONG_SYSTEM"), SO_NGAY_CONG_CHUAN_SYSTEM("SO_NGAY_CONG_CHUAN_SYSTEM"),
        LUONG_THEO_NGAY_CONG_SYSTEM("LUONG_THEO_NGAY_CONG_SYSTEM"),
        LUONG_THEO_GIO_CONG_SYSTEM("LUONG_THEO_GIO_CONG_SYSTEM"),
        // SO_CONG_CHUAN
        SO_GIO_CONG_SYSTEM("SO_GIO_CONG_SYSTEM"), SO_GIO_CONG_CHUAN_SYSTEM("SO_GIO_CONG_CHUAN_SYSTEM"),
        SO_GIO_CONG_OT_SYSTEM("SO_GIO_CONG_OT_SYSTEM"),
        MA_NV_SYSTEM("MA_NHAN_VIEN_SYSTEM"), HO_VA_TEN_NV_SYSTEM("HO_VA_TEN_NV_SYSTEM"), HO_NV_SYSTEM("HO_NV_SYSTEM"),
        TEN_NV_SYSTEM("TEN_NV_SYSTEM"), CHUC_VU_NV_SYSTEM("CHUC_VU_NV_SYSTEM"), DON_VI_NV_SYSTEM("DON_VI_NV_SYSTEM"),
        PHONG_BAN_NV_SYSTEM("PHONG_BAN_NV_SYSTEM"), SDT_NV_SYSTEM("SDT_NV_SYSTEM"), EMAIL_NV_SYSTEM("EMAIL_NV_SYSTEM"),
        GIOI_TINH_NV_SYSTEM("GIOI_TINH_NV_SYSTEM"), QUOC_TICH_NV_SYSTEM("QUOC_TICH_NV_SYSTEM"),
        NGUYEN_QUAN_NV_SYSTEM("NGUYEN_QUAN_NV_SYSTEM"),
        BH_XA_HOI_SYSTEM("BH_XA_HOI_SYSTEM"), BH_Y_TE_SYSTEM("BH_Y_TE_SYSTEM"),
        BH_THAT_NGHIEP_SYSTEM("BH_THAT_NGHIEP_SYSTEM"),
        BH_XA_HOI_CONG_TY_DONG_SYSTEM("BH_XA_HOI_CONG_TY_DONG_SYSTEM"),
        BH_Y_TE_CONG_TY_DONG_SYSTEM("BH_Y_TE_CONG_TY_DONG_SYSTEM"),
        BH_THAT_NGHIEP_CONG_TY_DONG_SYSTEM("BH_THAT_NGHIEP_CONG_TY_DONG_SYSTEM"),
        KHOAN_PHI_CONG_DOAN_CONG_TY_DONG_SYSTEM("KHOAN_PHI_CONG_DOAN_CONG_TY_DONG_SYSTEM"),
        SO_NGUOI_PHU_THUOC_SYSTEM("SO_NGUOI_PHU_THUOC_SYSTEM"), GIAM_TRU_BAN_THAN_SYSTEM("GIAM_TRU_BAN_THAN_SYSTEM"),
        GIAM_TRU_1_NGUOI_PHU_THUOC_SYSTEM("GIAM_TRU_1_NGUOI_PHU_THUOC_SYSTEM"),
        GIAM_TRU_NGUOI_PHU_THUOC_SYSTEM("GIAM_TRU_NGUOI_PHU_THUOC_SYSTEM"),
        GIAM_TRU_GIA_CANH_SYSTEM("GIAM_TRU_GIA_CANH_SYSTEM"),
        CAC_KHOAN_GIAM_TRU_SYSTEM("CAC_KHOAN_GIAM_TRU_SYSTEM"),
        THUE_TNCN_SYSTEM("THUE_TNCN_SYSTEM"), THU_NHAP_TINH_THUE_SYSTEM("THU_NHAP_TINH_THUE_SYSTEM"),
        THU_NHAP_CHIU_THUE_SYSTEM("THU_NHAP_CHIU_THUE_SYSTEM"), TONG_THU_NHAP_SYSTEM("TONG_THU_NHAP_SYSTEM"),
        TONG_THU_NHAP_MIEN_THUE_SYSTEM("TONG_THU_NHAP_MIEN_THUE_SYSTEM"), TONG_KHAU_TRU_SYSTEM("TONG_KHAU_TRU_SYSTEM"),
        STT_SYSTEM("STT_SYSTEM"), LUONG_KY_NAY_SYSTEM("LUONG_KY_NAY_SYSTEM"),
        TAM_UNG_SYSTEM("TAM_UNG_SYSTEM"),
        THUC_LINH_SYSTEM("THUC_LINH_SYSTEM"),

        //        LUONG_CO_BAN("LUONG_CO_BAN"),
//        BH_NHAN_VIEN_DONG("BAO_HIEM_NHAN_VIEN_DONG"),
//        BH_CONG_TY_DONG("BH_CONG_TY_DONG"),
//        THUC_LINH("THUC_LINH"),
//        THUE_TNCN("THUE_TNCN"),
//        THU_NHAP_TINH_THUE("THU_NHAP_TINH_THUE"),
//        THU_NHAP_CHIU_THUE("THU_NHAP_CHIU_THUE"),
//        TONG_THU_NHAP("TONG_THU_NHAP"),
//        SO_NGUOI_PHU_THUOC("SO_NGUOI_PHU_THUOC"),
//        MUC_GIAM_TRU_GIA_CANH("MUC_GIAM_TRU_GIA_CANH"),
//        MUC_GIAM_TRU_BAN_THAN("MUC_GIAM_TRU_BAN_THAN"),
        SO_GIO_LAM_VIEC_HC("SO_GIO_LAM_VIEC_HC"),
        SO_CA_NGHI_PHEP_CO_LUONG("SO_CA_NGHI_PHEP_CO_LUONG"),
        SO_CA_NGHI_PHEP_KHONG_LUONG("SO_CA_NGHI_PHEP_KHONG_LUONG"),
        SO_LAN_DI_LAM_MUON("SO_LAN_DI_LAM_MUON"),
        SO_NGAY_CONG("SO_NGAY_CONG"),
        ;

        private String value;

        SalaryItemCodeSystemDefault(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // Định nghĩa thêm các thành phần lương cần kết nối thêm tại đây
    public enum SalaryAutoMapField {
        // CHẤM CÔNG
        // Số giờ làm việc
        SO_GIO_LAM_VIEC_DUOC_PHAN("SO_GIO_LAM_VIEC_DUOC_PHAN", "Số giờ làm việc được phân"),
        SO_GIO_LAM_VIEC_THUC_TE("SO_GIO_LAM_VIEC_THUC_TE", "Số giờ làm việc thực tế"),
        SO_GIO_LAM_THEM_TRUOC_CA("SO_GIO_LAM_THEM_TRUOC_CA", "Số giờ làm thêm trước ca"),
        SO_GIO_LAM_THEM_SAU_CA("SO_GIO_LAM_THEM_SAU_CA", "Số giờ làm thêm sau ca"),
        SO_GIO_LAM_VIEC_CONG_QUY_DOI("SO_GIO_LAM_VIEC_CONG_QUY_DOI", "Số giờ làm việc công quy đổi"),

        // Số ca làm việc
        SO_CA_DUOC_PHAN("SO_CA_DUOC_PHAN", "Số ca làm việc được phân"),
        SO_CA_DI_LAM_DU("SO_CA_DI_LAM_DU", "Số ca làm việc đi làm đủ"),
        SO_CA_DI_LAM_THIEU("SO_CA_DI_LAM_THIEU", "Số ca làm việc đi làm thiếu"),
        SO_CA_KHONG_DI_LAM("SO_CA_KHONG_DI_LAM", "Số ca làm việc không đi làm"),
        SO_CA_CONG_TAC("SO_CA_CONG_TAC", "Số ca làm việc công tác"),
        SO_CA_NGHI_BU("SO_CA_NGHI_BU", "Số ca làm việc nghỉ bù"),
        SO_CA_NGHI_CHE_DO("SO_CA_NGHI_CHE_DO", "Số ca làm việc nghỉ chế độ"),
        SO_CA_NGHI_KHONG_LUONG("SO_CA_NGHI_KHONG_LUONG", "Số ca làm việc nghỉ không lương"),
        SO_CA_NGHI_LE("SO_CA_NGHI_LE", "Số ca làm việc nghỉ lễ"),
        SO_CA_NGHI_PHEP("SO_CA_NGHI_PHEP", "Số ca làm việc nghỉ phép"),

        // Số lần
        SO_LAN_DI_MUON("SO_LAN_DI_MUON", "Số lần đi muộn"),
        SO_LAN_VE_SOM("SO_LAN_VE_SOM", "Số lần về sớm"),

        // Số phút
        SO_PHUT_DI_MUON("SO_PHUT_DI_MUON", "Số phút đi muộn"),
        SO_PHUT_VE_SOM("SO_PHUT_VE_SOM", "Số phút về sớm"),
        SO_PHUT_DI_SOM("SO_PHUT_DI_SOM", "Số phút đi sớm"),
        SO_PHUT_VE_MUON("SO_PHUT_VE_MUON", "Số phút về muộn"),

        // Công được tính
        SO_CONG_DUOC_TINH("SO_CONG_DUOC_TINH", "Số công được tính"),


        // TÍNH LƯƠNG
        TAM_UNG("TAM_UNG", "Khoản tạm ứng trước tiền lương của nhân viên"),
        BHXH_NHAN_VIEN_DONG("BHXH_NHAN_VIEN_DONG", "Bảo hiểm xã hội nhân viên đóng"),
        BHYT_NHAN_VIEN_DONG("BHYT_NHAN_VIEN_DONG", "Bảo hiểm y tế nhân viên đóng"),
        BHTN_NHAN_VIEN_DONG("BHTN_NHAN_VIEN_DONG", "Bảo hiểm thất nghiệp nhân viên đóng"),
        KPCD_NHAN_VIEN_DONG("KPCD_NHAN_VIEN_DONG", "Khoản phí công đoàn nhân viên đóng"),
        BHXH_CONG_TY_DONG("BHXH_CONG_TY_DONG", "Bảo hiểm xã hội công ty đóng"),
        BHYT_CONG_TY_DONG("BHYT_CONG_TY_DONG", "Bảo hiểm y tế công ty đóng"),
        BHTN_CONG_TY_DONG("BHTN_CONG_TY_DONG", "Bảo hiểm thất nghiệp công ty đóng"),
        KPCD_CONG_TY_DONG("KPCD_CONG_TY_DONG", "Khoản phí công đoàn công ty đóng"),
        ;

        private String value;
        private String description;

        SalaryAutoMapField(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static List<SalaryAutoMapField> getList() {
            return Arrays.asList(SalaryAutoMapField.values());
        }

    }

    // Các thành phần lương khác được setup
    public enum SalaryItemCodeSetup {
        PHU_CAP_TRACH_NHIEM("PHU_CAP_TRACH_NHIEM"),
        QUA_LE_TET("QUA_LE_TET"),
        PHU_CAP_DIEN_THOAI("PHU_CAP_DIEN_THOAI"),
        CONG_TAC_PHI("CONG_TAC_PHI");

        private String value;

        SalaryItemCodeSetup(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // Kiểu giá trị (hiển thị cho kết quả của thành phần lương) của thành phần lương
    public enum SalaryItemValueType {
        TEXT(1), // 1. Chữ
        MONEY(2), // 2. Tiền tệ
        NUMBER(3), // 3. Số
        PERCENT(4), // 4. Phần trăm
        OTHERS(5); // 5. Khác

        private int value;

        SalaryItemValueType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum ShiftWorkCode {
        DAY_SHIFT("01", "Ca ngày"),
        NIGHT_SHIFT("02", "Ca tối");

        private String value;
        private String description;

        ShiftWorkCode(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ShiftWorkTimePeriodEnum {
        MORNING_SHIFT(UUID.fromString("075b2bf3-5cfb-42e4-b271-e44b49d2be23"), "SA"),
        AFTERNOON_SHIFT(UUID.fromString("d410ee13-b2cc-49fc-8159-c4a5f57ab6b8"), "CH"),
        EVENING_SHIFT(UUID.fromString("f5bcabc9-60ad-45d8-b229-05aa3a746b69"), "OT");

        private UUID value;
        private String codeValue;

        ShiftWorkTimePeriodEnum(UUID value, String codeValue) {
            this.value = value;
            this.codeValue = codeValue;
        }

        public UUID getValue() {
            return value;
        }

        public String getCodeValue() {
            return codeValue;
        }
    }

    public enum StaffSocialInsurancePaidStatus {
        PAID(1, "Đã đóng"),//
        UNPAID(2, "Chưa đóng");

        private Integer value;
        private String name;

        StaffSocialInsurancePaidStatus(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    // Trạng thái duyệt phiếu lương
    public enum SalaryResulStaffApprovalStatus {
        NOT_APPROVED_YET(1, "Chưa duyệt"), // Chưa duyệt
        APPROVED(2, "Đã duyệt"), // Đã duyệt
        NOT_APPROVED(3, "Không duyệt"),
        LOCKED(4, "Đã chốt");

        private Integer value;
        private String name;

        SalaryResulStaffApprovalStatus(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    // Trạng thái duyệt phiếu lương
    public enum SalaryResulStaffPaidStatus {
        PAID(1, "Đã chi trả"),
        UNPAID(2, "Chưa chi trả");

        private Integer value;
        private String name;

        SalaryResulStaffPaidStatus(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    // Trạng thái xác nhận tạm ứng tiền
    public enum StaffAdvancePaymentApprovalStatus {
        NOT_APPROVED_YET(1, "Chưa duyệt"), // Chưa duyệt
        APPROVED(2, "Đã duyệt"), // Đã duyệt
        NOT_APPROVED(3, "Không duyệt");

        private Integer value;
        private String name;

        StaffAdvancePaymentApprovalStatus(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    // Trạng thái xác nhận tạm ứng tiền
    public enum HrResourcePlanApprovalStatus {
        NOT_APPROVED_YET(1, "Chưa duyệt"), // Chưa duyệt
        APPROVED(2, "Đã duyệt"), // Đã duyệt
        NOT_APPROVED(3, "Không duyệt");

        private Integer value;
        private String name;

        HrResourcePlanApprovalStatus(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }


    public enum CandidateRecruitmentRoundEnum {
        NOT_RESULT_YET(0, "KHông đạt"), // Không đạt
        RESULT(1, "Đạt"); // đã đạt

        private Integer value;
        private String name;

        CandidateRecruitmentRoundEnum(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    public enum TypeTimeSheetDetail {
        START(1), // Vào làm - Check in
        END(2); // Rời làm việc - Check out

        private int value;

        TypeTimeSheetDetail(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // Loại tính thời gian chấm công
    public enum TimekeepingCalculationType {
        FIRST_IN_FIRST_OUT(1, "FIFO", "Vào đầu, Ra đầu"),
        FIRST_IN_LAST_OUT(2, "FILO", "Vào đầu, Ra cuối"),
        LAST_IN_LAST_OUT(3, "LILO", "Vào cuối, Ra cuối");

        private Integer value;
        private String code;
        private String description;

        TimekeepingCalculationType(Integer value, String code, String description) {
            this.value = value;
            this.code = code;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    // Trạng thái phê duyệt Yêu cầu ĐỔI CA làm việc
    public enum ShiftChangeRequestApprovalStatus {
        NOT_APPROVED_YET(1, "Chưa duyệt"), // Chưa duyệt
        APPROVED(2, "Đã duyệt"), // Đã duyệt
        NOT_APPROVED(3, "Không duyệt");

        private Integer value;
        private String name;

        ShiftChangeRequestApprovalStatus(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    // Trạng thái phê duyệt yêu cầu đăng ký làm việc
    public enum ShiftRegistrationApprovalStatus {
        NOT_APPROVED_YET(1, "Chưa duyệt"), // Chưa duyệt
        APPROVED(2, "Đã duyệt"), // Đã duyệt
        NOT_APPROVED(3, "Không duyệt");

        private Integer value;
        private String name;

        ShiftRegistrationApprovalStatus(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    // Trạng thái phê duyệt Lịch làm việc của nhân viên
    public enum StaffWorkScheduleApprovalStatus {
        NOT_APPROVED_YET(1, "Chưa duyệt"), // Chưa duyệt
        APPROVED(2, "Đã duyệt"), // Đã duyệt
        NOT_APPROVED(3, "Không duyệt");

        private Integer value;
        private String name;

        StaffWorkScheduleApprovalStatus(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    // Loại điều chuyển nhân viên
    public enum StaffWorkingHistoryTransferType {
        INTERNAL_ORG(1, "Điều chuyển nội bộ (trong cùng 1 đơn vị)"),
        EXTERNAL_ORG(2, "Điều chuyển sang đơn vị khác"),
        PAUSE_TEMPORARY(3, "Tạm nghỉ"),
        END_POSITION(4, "Ngừng công tác");

        private Integer value;
        private String name;

        StaffWorkingHistoryTransferType(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    public enum StaffHasSocialInsuranceExportType {
        INCREASE_2007(1, "Báo tăng lao động"),
        INCREASE_97_2003(2, "Báo tăng lao động"),
        DECREASE_2007(3, "Báo giảm lao động"),
        DECREASE_97_2003(4, "Báo giảm lao động"),
        MODIFY_2007(5, "Điều chỉnh đóng BHXH, BHYT, BHTN"),
        MODIFY_97_2003(6, "Điều chỉnh đóng BHXH, BHYT, BHTN");

        private final int value;
        private final String name;

        StaffHasSocialInsuranceExportType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    // Trạng thái hợp đồng lao động
    public enum StaffLabourAgreementStatus {
        UNSIGNED(1, "Hợp đồng chưa được ký"),
        SIGNED(2, "Hợp đồng đã được ký"),
        TERMINATED(3, "Đã chấm dứt");

        private final int value;
        private final String name;

        StaffLabourAgreementStatus(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public static StaffLabourAgreementStatus fromValue(int value) {
            for (StaffLabourAgreementStatus status : values()) {
                if (status.getValue() == value) return status;
            }
            return null;
        }

        public String getName() {
            return name;
        }
    }

    // Loại ngày nghỉ
    public enum HolidayLeaveType {
        WEEKEND(1, "Ngày nghỉ cuối tuần"),
        PULBIC_HOLIDAY(2, "Ngày nghỉ lễ chung"),
        OTHERS(3, "Khác");

        private final int value;
        private final String name;

        HolidayLeaveType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    // Trạng thái phê duyệt yêu cầu vắng mặt làm việc
    public enum AbsenceRequestApprovalStatus {
        NOT_APPROVED_YET(1, "Chưa duyệt"), // Chưa duyệt
        APPROVED(2, "Đã duyệt"), // Đã duyệt
        NOT_APPROVED(3, "Không duyệt");

        private final int value;
        private final String name;

        AbsenceRequestApprovalStatus(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    // Trạng thái phê duyệt yêu cầu vắng mặt làm việc
    public enum setApprovalStatus {
        NOT_APPROVED_YET(1, "Chưa duyệt"), // Chưa duyệt
        APPROVED(2, "Đã duyệt"), // Đã duyệt
        NOT_APPROVED(3, "Không duyệt");

        private final int value;
        private final String name;

        setApprovalStatus(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    // Loại nghỉ phép
    public enum AbsenceRequestType {
        PAID_LEAVE(1, "Nghỉ có lương"),
        UNPAID_LEAVE(2, "Nghỉ không lương");

        private final int value;
        private final String name;

        AbsenceRequestType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }


    // Trạng thái làm việc của nhân viên trong ca làm việc
    public enum StaffWorkScheduleWorkingStatus {
        FULL_ATTENDANCE(1, "Đi làm đủ ca"), // Đi làm đủ
        PARTIAL_ATTENDANCE(2, "Đi làm thiếu giờ"), // Đi làm thiếu giờ
        // 2 trạng thái này sẽ không dùng nữa
//        LEAVE_WITH_PERMISSION(3, "Nghỉ có phép"), // Nghỉ có phép
//        LEAVE_WITHOUT_PERMISSION(4, "Nghỉ không phép"), // Không đi làm/nghỉ không phép
        NOT_ATTENDANCE(5, "Không đi làm"); // Không đi làm

        private final int value;
        private final String name;

        StaffWorkScheduleWorkingStatus(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    // Trạng thái tính công của nhân viên trong ca làm việc
    public enum PaidWorkStatus {
        UNPAID(1, "Không tính công"), // Không tính công
        PAID(2, "Có tính công"); // Có tính công

        private final int value;
        private final String name;

        PaidWorkStatus(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }


    // Trạng thái làm việc của nhân viên trong ca làm việc
    public enum StaffWorkScheduleWorkingType {
        NORMAL_WORK(1, "Làm việc bình thường"), // Làm việc bình thường
        EXTENDED_OVERTIME(2, "Tăng ca kéo dài"); // Tăng ca kéo dài

        private final int value;
        private final String name;

        StaffWorkScheduleWorkingType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    // Trạng thái làm việc của nhân viên trong ca làm việc
    public enum ShiftWorkType {
        ADMINISTRATIVE(1, "Hành chính"), // Ca làm việc Hành chính
        OVERTIME(2, "Tăng ca"); // Ca làm việc Tăng ca

        private final int value;
        private final String name;

        ShiftWorkType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    // Loại mối quan hệ của các vị tri
    public enum PositionRelationshipType {
        UNDER_DIRECT_MANAGEMENT(3, "Chịu sự quản lý trực tiếp"), // chịu sự quản lý trực tiếp của phòng ban hoặc nhân viên
        UNDER_INDIRECT_MANAGEMENT(4, "Chịu sự quản lý gián tiếp"); // chịu sự quản lý gián tiếp của phòng ban hoặc nhân viên

        private int value;
        private String name;


        PositionRelationshipType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    // Trạng thái phê duyệt thời gian làm thêm của nhân viên
    public enum OvertimeRequestApprovalStatus {
        NOT_APPROVED_YET(1, "Chưa duyệt"), // Chưa duyệt
        APPROVED(2, "Đã duyệt"), // Đã duyệt
        NOT_APPROVED(3, "Không duyệt");

        private Integer value;
        private String name;

        OvertimeRequestApprovalStatus(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    public static final Map<String, String> SPECIAL_HOLIDAYS;

    static {
        Map<String, String> holidays = new HashMap<>();
        holidays.put("01-01", "Tết Dương Lịch");
        holidays.put("04-30", "Ngày Giải phóng miền Nam");
        holidays.put("05-01", "Ngày Quốc tế Lao động");
        holidays.put("09-02", "Ngày Quốc khánh");
        SPECIAL_HOLIDAYS = Collections.unmodifiableMap(holidays);
    }

    // Loại vị trí việc làm của nhân viên
    public enum StaffPositionType {
        NHA_QUAN_LY(1, "Nhà quản lý"),
        CHUYEN_MON_KY_THUAT_BAC_CAO(2, "Chuyên môn kỹ thuật bậc cao"),
        CHUYEN_MON_KY_THUAT_BAC_TRUNG(3, "Chuyên môn kỹ thuật bậc trung"),
        KHAC(4, "Khác");

        private Integer value;
        private String description;

        StaffPositionType(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

    }

    public enum ContractTypeCode {
        UNSPECIFIED_DEADLINE("HDLD_KXDTH", "Hợp đồng vô thời hạn"),
        DETERMINE_THE_DEADLINE("HDLD_XDTH", "Hợp đồng xác định thời hạn"),
        PROBATION("HDTV", "Hợp đồng thử việc"),
        OTHER("HDLD_KHAC", "Loại hợp đồng khác");

        private final String value;
        private final String description;

        ContractTypeCode(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }

    // Các toán tử so sánh trong cấu hình tính lương theo ngưỡng
    public enum SalaryTemplateItemConfigOperator {
        EQUALS(1, "Bằng (=)"),
        NOT_EQUALS(2, "Khác (!=)"),
        GREATER_THAN(3, "Lớn hơn (>)"),
        LESS_THAN(4, "Nhỏ hơn (<)"),
        GREATER_THAN_OR_EQUALS(5, "Lớn hơn bằng (>=)"),
        LESS_THAN_OR_EQUALS(6, "Nhỏ hơn bằng (<=)");

        private final Integer value;
        private final String description;

        SalaryTemplateItemConfigOperator(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }

    // Tình trạng nộp hồ sơ của nhân viên
    public enum StaffDocumentStatus {
        UNSUBMMITED(1, "Chưa nộp hồ sơ"),
        INCOMPLETED(2, "Thiếu hồ sơ"),
        COMPLETED(3, "Đủ hồ sơ");

        private final Integer value;
        private final String description;

        StaffDocumentStatus(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static String getDescriptionByValue(Integer value) {
            if (value == null) return "";
            for (StaffDocumentStatus format : StaffDocumentStatus.values()) {
                if (format.getValue().equals(value)) {
                    return format.getDescription();
                }
            }
            return "";
        }
    }

    // Hình thức làm việc của nhân viên
    public enum StaffWorkingFormat {
        COLLABORATE(1, "Cộng tác"),
        PARTTIME(2, "Bán thời gian (Part-time)"),
        FULLTIME(3, "Toàn thời gian (Full-time)");

        private final Integer value;
        private final String description;

        StaffWorkingFormat(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static String getDescriptionByValue(Integer value) {
            if (value == null) return "";
            for (StaffWorkingFormat format : StaffWorkingFormat.values()) {
                if (format.getValue().equals(value)) {
                    return format.getDescription();
                }
            }
            return "";
        }
    }

    // Tình trạng nhân viên
    public enum StaffPhase {
        INTERN(1, "Học việc (HV)"), // Học việc
        PROBATION(2, "Thử việc (TV)"), // Thử việc
        OFFICIAL(3, "Chính thức (CT)"); // Chính thức

        private final Integer value;
        private final String description;

        StaffPhase(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static String getDescriptionByValue(Integer value) {
            if (value == null) return "";
            for (StaffPhase format : StaffPhase.values()) {
                if (format.getValue().equals(value)) {
                    return format.getDescription();
                }
            }
            return "";
        }
    }

    // Tình trạng hôn nhân nhân viên
    public enum StaffMaritalStatus {
        SINGLE(0, "Độc thân"), // Single
        ENGAGED(1, "Đính hôn"), // Engaged
        MARRIED(2, "Đã kết hôn"), // Married
        SEPARATED(3, "Ly thân"), // Separated
        DIVORCED(4, "Đã ly hôn"), // Divorced
        OTHERS(5, "Khác");

        private final Integer value;
        private final String description;

        StaffMaritalStatus(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static String getDescriptionByValue(Integer value) {
            if (value == null) return "";
            for (StaffMaritalStatus format : StaffMaritalStatus.values()) {
                if (format.getValue().equals(value)) {
                    return format.getDescription();
                }
            }
            return "";
        }
    }

    // Cấp độ đơn vị hành chính
    public enum AdministrativeLevel {
        PROVINCE(3, "Cấp Tỉnh/Thành phố"),
        DISTRICT(2, "Cấp Quận/Huyện"),
        COMMUNE(1, "Cấp Xã/Phường");

        private final Integer value;
        private final String description;

        AdministrativeLevel(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static String getDescriptionByValue(Integer value) {
            if (value == null) return "";
            for (AdministrativeLevel format : AdministrativeLevel.values()) {
                if (format.getValue().equals(value)) {
                    return format.getDescription();
                }
            }
            return "";
        }
    }

    public enum DefaultDocumentTemplateItem {
        A34("A34", "Ảnh 3x4", 1),
        CCCD("CCCD", "CMND/CCCD", 2),
        DUT("DUT", "Đơn ứng tuyển", 3),
        SYLL("SYLL", "Sơ yếu lý lịch", 4),
        BC("BC", "Bằng cấp cao nhất", 5),
        CCLQ("CCLQ", "Chứng chỉ liên quan", 6),
        GKSK("GKSK", "Giấy khám SK", 7),
        SHK("SHK", "SHK", 8),
        HSK("HSK", "Hồ sơ khác", 9),
        PTTCN("PTTCN", "Phiếu thông tin cá nhân", 10),
        CKBMTT("CKBMTT", "Cam kết bảo mật thông tin", 11),
        CKBMTTTN("CKBMTTTN", "Cam kết bảo mật thông tin thu nhập", 12),
        CKTN("CKTN", "Cam kết trách nhiệm", 13),
        HDTV("HDTV", "HĐ thử việc", 14);

        private final String code;
        private final String description;
        private final Integer displayOrder;

        DefaultDocumentTemplateItem(String code, String description, Integer displayOrder) {
            this.code = code;
            this.description = description;
            this.displayOrder = displayOrder;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public Integer getDisplayOrder() {
            return displayOrder;
        }

        public static List<String> getListCode() {
            return Arrays.stream(DefaultDocumentTemplateItem.values())
                    .map(DefaultDocumentTemplateItem::getCode)
                    .collect(Collectors.toList());
        }
    }

    public enum LeaveTypeCode {
        ANNUAL_LEAVE("Nghỉ phép", "NGHI_PHEP"),
        HALF_ANNUAL_LEAVE("Nghỉ phép nửa ngày", "NGHI_PHEP_NUA_NGAY"),
        BUSINESS_TRIP("Công tác", "CONG_TAC"),
        HALF_BUSINESS_TRIP("Công tác nửa ngày", "CONG_TAC_NUA_NGAY"),
        UNPAID_LEAVE("Nghỉ không lương", "NGHI_KHONG_LUONG"),
        HALF_UNPAID_LEAVE("Nghỉ không lương nửa ngày", "NGHI_KHONG_LUONG_NUA_NGAY"),
        PUBLIC_HOLIDAY("Nghỉ lễ", "NGHI_LE"),
        HALF_PUBLIC_HOLIDAY("Nghỉ lễ nửa ngày", "NGHI_LE_NUA_NGAY"),
        COMPENSATORY_LEAVE("Nghỉ bù", "NGHI_BU"),
        HALF_COMPENSATORY_LEAVE("Nghỉ bù nửa ngày", "NGHI_BU_NUA_NGAY"),
        SPECIAL_LEAVE("Nghỉ chế độ", "NGHI_CHE_DO"),
        HALF_SPECIAL_LEAVE("Nghỉ chế độ nửa ngày", "NGHI_CHE_DO_NUA_NGAY"),

        YEAR_LEAVE("Nghỉ phép năm", "NGHI_PHEP_NAM"),
        HALF_YEAR_LEAVE("Nghỉ phép năm", "NGHI_PHEP_NAM");

        private final String name;
        private final String code;

        LeaveTypeCode(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }
    }

    public enum Gender {
        MALE("M", "Nam"),
        FEMALE("F", "Nữ"),
        OTHER("U", "Không rõ");

        private final String code;
        private final String name;

        Gender(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static Gender fromCode(String code) {
            for (Gender gender : Gender.values()) {
                if (gender.code.equalsIgnoreCase(code)) {
                    return gender;
                }
            }
            return OTHER;
        }
    }


    public enum AdminitractiveLevel {
        PROVINCE(3, "Tỉnh/Thành phố"),
        DISTRICT(2, "Quận/Huyện"),
        COMMUNE(1, "Xã/Phường");

        private final Integer value;
        private final String description;

        AdminitractiveLevel(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }


    // Định nghĩa thêm các thành phần lương cần kết nối thêm tại đây
    public enum SalaryItemAutoConnectCode {
        SO_GIO_CONG_TIEU_CHUAN("SO_GIO_CONG_TIEU_CHUAN", ""),
        SO_GIO_LAM_VIEC_HOP_LE("SO_GIO_LAM_VIEC_HC", ""),
        SO_GIO_TANG_CA_TV("SO_GIO_TANG_CA_TV", ""),
        SO_GIO_TANG_CA_CT("SO_GIO_TANG_CA_CT", ""),
        SO_GIO_OT_DUOC_XAC_NHAN("SO_GIO_OT_DUOC_XAC_NHAN", ""),
        SO_PHUT_TRE_SOM_TV("SO_PHUT_TRE_SOM_THU_VIEC", ""),
        SO_PHUT_TRE_SOM_CT("SO_PHUT_TRE_SOM_CHINH_THUC", ""),
        NGAY_CONG_CHUAN("NGAY_CONG_CHUAN", ""),
        NGAY_CONG_HUONG_LUONG_TV("NGAY_CONG_HUONG_LUONG_THU_VIEC", ""),
        NGAY_CONG_HUONG_LUONG_CT("NGAY_CONG_HUONG_LUONG_CHINH_THUC", ""),
        DA_TAM_UNG("DA_TAM_UNG", ""),
        SO_NGUOI_PHU_THUOC_THUE("SO_NGUOI_PHU_THUOC_THUE", ""),
        NGAY_CONG_TINH_LUONG_THUE("NGAY_CONG_TINH_LUONG_THUE", ""),
        NGAY_CONG_THUC_TE_DI_LAM("NGAY_CONG_THUC_TE_DI_LAM", ""),
        NGAY_VUOT_CONG_THU_VIEC("NGAY_VUOT_CONG_TV", ""),
        NGAY_VUOT_CONG_CHINH_THUC("NGAY_VUOT_CONG_CT", ""),

        TIEN_TRU_KHAC("TIEN_TRU_KHAC", ""),
        THU_NHAP_KHAC("THU_NHAP_KHAC", ""),

        // Tạo ra phiếu bảo hiểm
        MUC_LUONG_DONG_BAO_HIEM_XA_HOI("LUONG_THAM_GIA_BAO_HIEM_XA_HOI_THUE", ""),

        BAO_HIEM_XA_HOI_CONG_TY_DONG("BAO_HIEM_XA_HOI_CONG_TY_THUE", ""),
        BAO_HIEM_Y_TE_CONG_TY_DONG("BAO_HIEM_Y_TE_CONG_TY_THUE", ""),
        BAO_HIEM_THAT_NGHIEP_CONG_TY_DONG("BAO_HIEM_THAT_NGHIEP_CONG_TY_THUE", ""),

        BAO_HIEM_XA_HOI_NHAN_VIEN_DONG("BAO_HIEM_XA_HOI_NHAN_VIEN_THUE", ""),
        BAO_HIEM_Y_TE_NHAN_VIEN_DONG("BAO_HIEM_Y_TE_NHAN_VIEN_THUE", ""),
        BAO_HIEM_THAT_NGHIEP_NHAN_VIEN_DONG("BAO_HIEM_THAT_NGHIEP_NHAN_VIEN_THUE", ""),
        CO_DONG_BHXH("CO_DONG_BHXH", "");

        private String value;
        private String description;

        SalaryItemAutoConnectCode(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static List<SalaryAutoMapField> getList() {
            return Arrays.asList(SalaryAutoMapField.values());
        }

    }


    // Cách tính ngày công chuẩn trong tháng của chức danh
    public enum PositionTitleWorkdayCalculationType {
        FIXED(1, "Cố định"),
        CHANGE_BY_PERIOD(2, "Thay đổi theo kỳ lương"),
        ;

        private final Integer value;
        private final String description;

        PositionTitleWorkdayCalculationType(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

    }

    public enum ApproveStatus {
        Approve(2),
        Reject(1);
        private final Integer value;

        public Integer getValue() {
            return value;
        }

        ApproveStatus(Integer value) {
            this.value = value;
        }
    }

    public enum OrganizationType {
        LEGAL_ENTITY(1, "Pháp nhân"),
        OPERATION(2, "Vận hành");;
        private final Integer value;
        private final String description;

        OrganizationType(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

    }

    public enum OtherIncomeType {
        INCOME(1, "Thu nhập"),
        DEDUCTION(2, "Khấu trừ");

        private final Integer value;
        private final String description;

        OtherIncomeType(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }


    public enum LeaveShiftType {
        HALF_SHIFT_OFF(1, "Nghỉ nửa ca làm việc"),
        FULL_SHIFT_OFF(2, "Nghỉ cả ca làm việc");

        private final Integer value;
        private final String description;

        LeaveShiftType(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

    }

    public enum RecruitmentType {
        OFFLINE(1, "Phỏng vấn trực tiếp"),
        ONLINE(2, "Phỏng vấn gián tiếp");

        private final Integer value;
        private final String description;

        RecruitmentType(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static RecruitmentType fromValue(int value) {
            for (RecruitmentType status : values()) {
                if (status.value == value) {
                    return status;
                }
            }
            return null;
        }
    }

    public enum ResultStatus {
        PASS, FAIL
    }


    public enum CandidateRecruitmentRoundStatus {
        WAIT_RESPONSE(0, "Chờ xác nhận"),
        PARTICIPATED(1, "Tham gia"),
        NOT_PARTICIPATED(2, "Không tham gia"),
        REJECTED(3, "Bị từ chối");

        private final Integer value;
        private final String description;

        CandidateRecruitmentRoundStatus(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        // Optional: get enum by value
        public static CandidateRecruitmentRoundStatus fromValue(int value) {
            for (CandidateRecruitmentRoundStatus status : values()) {
                if (status.value == value) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid value: " + value);
        }
    }

    public enum EvaluationItemType {
        TECHNICAL_EXPERTISE(1, "Kiến thức Chuyên môn"),
        SKILL(2, "Kỹ năng"),
        ATTITUDE(3, "Thái độ/ tố chất"),
        OTHER(4, "Khác (nếu có)");
        private final Integer value;
        private final String description;

        EvaluationItemType(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        // Optional: get enum by value
        public static EvaluationItemType fromValue(int value) {
            for (EvaluationItemType status : values()) {
                if (status.value == value) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid value: " + value);
        }
    }

    public enum EvaluationTemplateItemContentType {
        TITLE(1, "Tiêu đề"),
        CONTENT(2, "Nội dung");
        private final Integer value;
        private final String description;

        EvaluationTemplateItemContentType(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        // Optional: get enum by value
        public static EvaluationTemplateItemContentType fromValue(int value) {
            for (EvaluationTemplateItemContentType status : values()) {
                if (status.value == value) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid value: " + value);
        }
    }


    public enum StaffWorkShiftType {
        FIXED(1, "Ca làm việc cố định"),
        FLEXIBLE(2, "Ca làm việc linh hoạt");

        private final Integer value;
        private final String description;

        StaffWorkShiftType(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        // Optional: get enum by value
        public static StaffWorkShiftType fromValue(int value) {
            for (StaffWorkShiftType status : values()) {
                if (status.value == value) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid value: " + value);
        }
    }


    public enum StaffLeaveShiftType {
        FIXED(1, "Nghỉ cố định"),
        FLEXIBLE(2, "Nghỉ linh hoạt theo tháng");

        private final Integer value;
        private final String description;

        StaffLeaveShiftType(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        // Optional: get enum by value
        public static StaffLeaveShiftType fromValue(int value) {
            for (StaffLeaveShiftType status : values()) {
                if (status.value == value) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid value: " + value);
        }
    }


    public enum WeekDays {
        MON(2, "Thứ Hai"),
        TUE(3, "Thứ Ba"),
        WED(4, "Thứ Tư"),
        THU(5, "Thứ Năm"),
        FRI(6, "Thứ Sáu"),
        SAT(7, "Thứ Bảy"),
        SUN(8, "Chủ Nhật");

        private final int value;
        private final String name;

        WeekDays(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

    }

    public enum SystemConfigCode {
        GEN_FIXED_SCHEDULES_DAY("GEN_FIXED_SCHEDULES_DAY", "Ngày tạo lịch làm việc cố định cho nhân viên trong tháng tiếp theo"),
        GEN_FIXED_SCHEDULES_HOUR_AND_MINUTE("GEN_FIXED_SCHEDULES_HOUR_AND_MINUTE", "Giờ và phút tạo lịch làm việc cố định cho nhân viên trong tháng tiếp theo"),
        TAX_SALARYTEMPLATE_CODE("TAX_SALARY_TEMPLATE_CODE", "Mã mẫu bảng lương thuế"),
        ACTUAL_SALARYTEMPLATE_CODE("ACTUAL_SALARY_TEMPLATE_CODE", "Mã mẫu bảng lương thực tế"),
        BASE_WAGE_SALARYITEM_CODE("BASE_WAGE_SALARYITEM_CODE", "Thành phần lương Lương cơ bản"),
        MIN_OT_MINUTES_TO_SHOW_CONFIRM("MIN_OT_MINUTES_TO_SHOW_CONFIRM", "Số phút làm thêm giờ tối thiểu để hiển thị cần được xác nhận làm thêm giờ"),
        INSURANCE_AMOUNT_SALARY_ITEM_CODE("INSURANCE_AMOUNT_SALARY_ITEM_CODE", "Mã thành phần lương Mức đóng BHXH của nhân viên");

        private final String code;
        private final String name;

        SystemConfigCode(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    public static enum CommonKeyCodeTypeEnum {// Dùng cùng với store nên > 2
        candidateCode(1) //ma nhan vien

        ;

        private int value;

        private CommonKeyCodeTypeEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum CodePrefix {
        NHOM_NGACH("NHOM_NGACH", "NG", 3),
        CHUC_DANH("CHUC_DANH", "CD", 3),
        CAP_BAC("CAP_BAC", "CB", 3),
        LOAI_PHONG_BAN("LOAI_PHONG_BAN", "LPB", 3),
        DANH_SACH_PHONG_BAN("DANH_SACH_PHONG_BAN", "DSPB", 3),
        DON_VI("DON_VI", "DV", 3),
        LOAI_CONG_CU_DUNG_CU("LOAI_CONG_CU_DUNG_CU", "LCDC", 3),
        CONG_CU_DUNG_CU("CONG_CU_DUNG_CU", "CCDC", 3),
        LOAI_KY_LUAT("LOAI_KY_LUAT", "LKL", 3),
        KY_LUONG("KY_LUONG", "KL", 3), // Có thể cần xử lý đặc biệt vì định dạng khác
        TAI_LIEU("TAI_LIEU", "TL", 3),
        MAU_TAI_LIEU("MAU_TAI_LIEU", "MTL", 3),
        TINH_TRANG_NV("TINH_TRANG_NV", "TTNV", 3),
        LOAI_DIEU_CHUYEN("LOAI_DIEU_CHUYEN", "LDC", 3),
        DIA_DIEM("DIA_DIEM", "DD", 3),
        LOAI_DAO_TAO("LOAI_DAO_TAO", "LDT", 3),
        TIEU_CHI_DANH_GIA("TIEU_CHI_DANH_GIA", "TCDG", 3),
        MAU_DANH_GIA("MAU_DANH_GIA", "MDG", 3),
        VI_TRI("VI_TRI", "VT", 3),
        YEU_CAU_TUYEN_DUNG("YEU_CAU_TUYEN_DUNG", "YCTD", 3),
        KE_HOACH_TUYEN_DUNG("KE_HOACH_TUYEN_DUNG", "KHTD", 3),
        UNG_VIEN("UNG_VIEN", "UV", 3),
        YEU_CAU_DINH_BIEN("YEU_CAU_DINH_BIEN", "YCDB", 3),
        CHUYEN_NGANH("CHUYEN_NGANH", "CN", 3),
        ;

        private final String configKey;
        private final String configValue;
        private final Integer zeroPadding;

        public String getConfigKey() {
            return configKey;
        }

        public String getConfigValue() {
            return configValue;
        }

        public Integer getZeroPadding() {
            return zeroPadding;
        }

        CodePrefix(String configKey, String configValue, Integer zeroPadding) {
            this.configKey = configKey;
            this.configValue = configValue;
            this.zeroPadding = zeroPadding;
        }

        // Optional: method to get enum by string
        public static CodePrefix fromValue(String configKey) {
            for (CodePrefix prefix : CodePrefix.values()) {
                if (prefix.configKey.equals(configKey)) {
                    return prefix;
                }
            }
            return null;
        }
    }

    // hard code trạng thái làm việc của nhân viên
    public enum DismissPositions {
        DA_NGHI_VIEC("DA_NGHI_VIEC"),
        NGHI_CHE_DO("NGHI_CHE_DO");

        private final String value;

        DismissPositions(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}