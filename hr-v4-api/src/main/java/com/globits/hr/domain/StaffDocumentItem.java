package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.FileDescription;
import jakarta.persistence.*;

import java.util.Date;

// Tài liệu/hồ sơ nhân viên đã nộp
@Entity
@Table(name = "tbl_staff_document_item")
public class StaffDocumentItem extends BaseObject {
    private static final long serialVersionUID = -2208752009903206352L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Staff staff; // Nhân viên nộp tài liệu

    @ManyToOne
    @JoinColumn(name = "document_item_id")
    private HrDocumentItem documentItem; // Tài liệu

    @Column(name = "is_submitted")
    private Boolean isSubmitted; // Đã nộp hay chưa

    @Column(name = "submission_date")
    private Date submissionDate; // Ngày nộp

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileDescription file; // Tài liệu đã được lưu

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public HrDocumentItem getDocumentItem() {
        return documentItem;
    }

    public void setDocumentItem(HrDocumentItem documentItem) {
        this.documentItem = documentItem;
    }

    public FileDescription getFile() {
        return file;
    }

    public void setFile(FileDescription file) {
        this.file = file;
    }

	public Boolean getIsSubmitted() {
		return isSubmitted;
	}

	public void setIsSubmitted(Boolean isSubmitted) {
		this.isSubmitted = isSubmitted;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}
    
    
}
