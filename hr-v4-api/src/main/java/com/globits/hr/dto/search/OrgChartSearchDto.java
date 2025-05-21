package com.globits.hr.dto.search;

import java.util.UUID;

public class OrgChartSearchDto extends SearchDto {
	
	private UUID objectId;
	private UUID sourceId;
	private UUID targetId;
	public OrgChartSearchDto() {
		// TODO Auto-generated constructor stub
	}

	public UUID getObjectId() {
		return objectId;
	}

	public void setObjectId(UUID objectId) {
		this.objectId = objectId;
	}

	public UUID getSourceId() {
		return sourceId;
	}

	public void setSourceId(UUID sourceId) {
		this.sourceId = sourceId;
	}

	public UUID getTargetId() {
		return targetId;
	}

	public void setTargetId(UUID targetId) {
		this.targetId = targetId;
	}

}
