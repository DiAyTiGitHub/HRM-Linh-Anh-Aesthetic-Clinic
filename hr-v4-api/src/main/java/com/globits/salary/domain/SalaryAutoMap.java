package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.HrConstants;
import jakarta.persistence.*;

import java.util.Set;

@Table(name = "tbl_salary_auto_map")
@Entity
public class SalaryAutoMap extends BaseObject {

    // Định nghĩa thêm các thành phần lương cần kết nối tại HrConstants.SalaryAutoMapField
    // => Khi chạy lại server đã có hàm tự tạo ra các SalaryAutoMapField từ enum
    @Column(name = "salary_auto_field")
    private String salaryAutoMapField; // khai báo tên trường được map với thành phần lương

//    @ManyToOne
//    @JoinColumn(name = "salary_item_id")
//    private SalaryItem salaryItem; // map với thành phần lương nào

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;


    // Các thành phần lương được kết nối với cấu hình này
    @OneToMany(mappedBy = "salaryAutoMap", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SalaryItem> salaryItems;



    public Set<SalaryItem> getSalaryItems() {
        return salaryItems;
    }

    public void setSalaryItems(Set<SalaryItem> salaryItems) {
        this.salaryItems = salaryItems;
    }

    public String getSalaryAutoMapField() {
        return salaryAutoMapField;
    }

    public void setSalaryAutoMapField(String salaryAutoMapField) {
        this.salaryAutoMapField = salaryAutoMapField;
    }

//    public SalaryItem getSalaryItem() {
//        return salaryItem;
//    }
//
//    public void setSalaryItem(SalaryItem salaryItem) {
//        this.salaryItem = salaryItem;
//    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
