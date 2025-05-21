package com.globits.budget.utils;

public class Enums {
    public enum Currency {
        VND("VND", "Việt nam đồng"),
        USD("USD", "Dollar Mỹ"),
        EUR("EUR", "Euro"),
        CNY("CNY", "Nhân dân tệ"),
        JPY("JPY", "Yên Nhật");

        private String value;
        private String name;

        Currency(String value, String name) {
            this.name = name;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public enum BudgetType {
        INCOME(-1, "Ngân sách thu"),
        EXPENDITURE(1, "Ngân sách chi"),
        ;


        private int value;
        private String name;

        BudgetType(int value, String name) {
            this.name = name;
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public enum VoucherType {
        INCOME(-1, "Thu"),
        EXPENDITURE(1, "Chi"),
        ;


        private int value;
        private String name;

        VoucherType(int value, String name) {
            this.name = name;
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
