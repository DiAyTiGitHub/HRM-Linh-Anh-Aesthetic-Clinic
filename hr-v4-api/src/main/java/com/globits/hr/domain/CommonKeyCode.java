package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_common_key_code")
public class CommonKeyCode extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "type")
	private Integer type;// Loại mã (1.Mã đơn hàng nhập, 2.Mã đơn hàng xuất)

	@Column(name = "object_type")
	private String objectType;// Hard-code

	@Column(name = "object_id", nullable = true)
	private String objectId;// Hard-code theo object

	@Column(name = "current_index", nullable = false)

	private Integer currentIndex;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public Integer getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(Integer currentIndex) {
		this.currentIndex = currentIndex;
	}

}
