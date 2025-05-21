package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

import java.util.Set;

// Bộ Tài liệu
@Entity
@Table(name = "tbl_hr_document_template")
public class HrDocumentTemplate extends BaseObject {
    private static final long serialVersionUID = -2208752009903206352L;

    @Column(name = "code")
    private String code; // mã bộ tài liệu

    @Column(name = "name")
    private String name; // tên bộ tài liệu

    @Column(name = "description")
    private String description; // mô tả tài liệu

	@OneToMany(mappedBy = "documentTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<HrDocumentItem> documentItems; // các tài liệu trong bộ hồ sơ/tài liệu


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<HrDocumentItem> getDocumentItems() {
		return documentItems;
	}

	public void setDocumentItems(Set<HrDocumentItem> documentItems) {
		this.documentItems = documentItems;
	}
}
