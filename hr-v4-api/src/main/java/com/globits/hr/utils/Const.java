package com.globits.hr.utils;

import org.springframework.util.StringUtils;

public class Const {
    public enum GENDER_ENUM {
        MALE("M", "Nam"),
        FEMALE("F", "Nữ");

        private final String value;
        private final String display;

        GENDER_ENUM(String value, String display) {
            this.value = value;
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }

        public String getValue() {
            return value;
        }

        public static String getDisplay(String gender) {
            if (gender == null) return "";
            if (gender.equals(MALE.getValue())) return MALE.getDisplay();
            if (gender.equals(FEMALE.getValue())) return FEMALE.getDisplay();
            return "";
        }
    }

    public enum HR_DEPARTMENT_TYPE_ENUM {
        LPB_0004("LPB_0004", "Ban"),
        LPB_0005("LPB_0005", "Phòng"),
        LPB_0006("LPB_0006", "Cơ sở kinh doanh"),
        LPB_0007("LPB_0007", "Bộ phận"),
        LPB_0008("LPB_0008", "Nhóm");
        private final String value;
        private final String display;

        HR_DEPARTMENT_TYPE_ENUM(String value, String display) {
            this.value = value;
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }

        public String getValue() {
            return value;
        }

    }
    public static String concatenate(String... values) {
        // Sử dụng StringBuilder để nối chuỗi
        StringBuilder result = new StringBuilder();
        for (String value : values) {
            if (value != null) {  // Kiểm tra nếu phần tử không phải là null
                if (!result.isEmpty()) {
                    result.append(", "); // Thêm dấu phân cách nếu không phải phần tử đầu tiên
                }
                result.append(value); // Nối chuỗi
            }
        }
        return result.toString(); // Trả về chuỗi đã nối
    }

    public static String checkString(String str) {
        if (StringUtils.hasText(str)) return str;
        return " ";
    }

    public enum EVALUATION {
        PASS, FAIL
    }
    public enum EVALUATION_TRANSFER_STATUS_ENUM {
        STAFF, // chuyển cho nhân viên tự đánh giá
        DIRECT_MANAGER, // chuyển cho quản lý trực tiếp đánh giá
        POSITION_MANAGER // chuyển cho quản lý phòng đánh giá
    }
    public enum WorkType {
        FULL_TIME("Nhân viên chính thức"),
        PART_TIME("Cộng tác viên"),
        INTERN("Thực tập sinh");

        private final String label;

        WorkType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
    public enum FORWARD_STATUS {
        PASS, FAIL
    }
    public enum FORWARD_PERSON {
        CREATOR, DEPARTMENT_LEADER, DGD, CEO
    }
}
