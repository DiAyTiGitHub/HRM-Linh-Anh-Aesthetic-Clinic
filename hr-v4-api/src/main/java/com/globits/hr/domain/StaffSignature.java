package com.globits.hr.domain;

import com.globits.budget.domain.BaseNameCodeObject;
import com.globits.core.domain.BaseObject;
import com.globits.core.domain.FileDescription;
import jakarta.persistence.*;

@Table(name = "tbl_staff_signature")
@Entity
public class StaffSignature extends BaseNameCodeObject {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id")
    private Staff staff;

//    @Column(name = "signature",columnDefinition="MEDIUMTEXT") //MEDIUMTEXT: Tối đa 16,777,215 bytes (khoảng 16 MB).
//    private String signature ;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileDescription file; // Tài liệu đã được lưu

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
//
//    public String getSignature() {
//        return signature;
//    }
//
//    public void setSignature(String signature) {
//        this.signature = signature;
//    }

    public FileDescription getFile() {
        return file;
    }

    public void setFile(FileDescription file) {
        this.file = file;
    }
}
