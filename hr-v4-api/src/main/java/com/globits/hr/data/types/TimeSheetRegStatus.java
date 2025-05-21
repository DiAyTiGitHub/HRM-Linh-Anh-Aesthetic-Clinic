package com.globits.hr.data.types;

public enum TimeSheetRegStatus {
	PLAN(0,"PLAN"),
	
	REG(1,"REG");

	private final int value;
    private final String description;

    private TimeSheetRegStatus(int value,String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

	public String getDescription() {
		return description;
	}
}
