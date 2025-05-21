package com.globits.hr.data.types;

public enum HrTaskStatus {
	CANCEL(-1,"CANCEL"),
	
	NEW(0,"NEW"),
	
	PICKED_UP(1,"PICKED_UP"),

    DOING(2,"DOING"),

    COMPLETE(3,"COMPLETE");

	private final int value;
    private final String description;

    private HrTaskStatus(int value,String description) {
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
