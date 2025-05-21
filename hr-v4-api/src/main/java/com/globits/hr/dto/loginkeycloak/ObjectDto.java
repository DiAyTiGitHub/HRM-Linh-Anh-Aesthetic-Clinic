package com.globits.hr.dto.loginkeycloak;

public class ObjectDto {
    private Boolean isFaled;
    private String code;// mã trả về
    private String note;// nội dung trả về

    public Boolean getIsFaled() {
        return isFaled;
    }

    public void setIsFaled(Boolean isFaled) {
        this.isFaled = isFaled;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
