package com.globits.hr.dto;

import java.util.UUID;

public class DefaultDocumentTemplateItemDto {
	private UUID staffId;
	private String hasEmployeeProfile;
    private String hasA34;
    private String hasCCCD;
    private String hasDUT;
    private String hasSYLL;
    private String hasBC;
    private String hasCCLQ;
    private String hasGKSK;
    private String hasSHK;
    private String hasHSK;
    private String hasPTTCN;
    private String hasCKBMTT;
    private String hasCKBMTTTN;
    private String hasCKTN;
    private String hasHDTV;

    public DefaultDocumentTemplateItemDto(UUID staffId,
    									  String hasEmployeeProfile,
                                          String hasA34,
                                          String hasCCCD,
                                          String hasDUT,
                                          String hasSYLL,
                                          String hasBC,
                                          String hasCCLQ,
                                          String hasGKSK,
                                          String hasSHK,
                                          String hasHSK,
                                          String hasPTTCN,
                                          String hasCKBMTT,
                                          String hasCKBMTTTN,
                                          String hasCKTN,
                                          String hasHDTV) {
        this.staffId = staffId;
        this.hasEmployeeProfile = hasEmployeeProfile;
        this.hasA34 = hasA34;
        this.hasCCCD = hasCCCD;
        this.hasDUT = hasDUT;
        this.hasSYLL = hasSYLL;
        this.hasBC = hasBC;
        this.hasCCLQ = hasCCLQ;
        this.hasGKSK = hasGKSK;
        this.hasSHK = hasSHK;
        this.hasHSK = hasHSK;
        this.hasPTTCN = hasPTTCN;
        this.hasCKBMTT = hasCKBMTT;
        this.hasCKBMTTTN = hasCKBMTTTN;
        this.hasCKTN = hasCKTN;
        this.hasHDTV = hasHDTV;
    }

    public UUID getStaffId() {
        return staffId;
    }

	public String getHasA34() {
		return hasA34;
	}

	public void setHasA34(String hasA34) {
		this.hasA34 = hasA34;
	}

	public String getHasCCCD() {
		return hasCCCD;
	}

	public void setHasCCCD(String hasCCCD) {
		this.hasCCCD = hasCCCD;
	}

	public String getHasDUT() {
		return hasDUT;
	}

	public void setHasDUT(String hasDUT) {
		this.hasDUT = hasDUT;
	}

	public String getHasSYLL() {
		return hasSYLL;
	}

	public void setHasSYLL(String hasSYLL) {
		this.hasSYLL = hasSYLL;
	}

	public String getHasBC() {
		return hasBC;
	}

	public void setHasBC(String hasBC) {
		this.hasBC = hasBC;
	}

	public String getHasCCLQ() {
		return hasCCLQ;
	}

	public void setHasCCLQ(String hasCCLQ) {
		this.hasCCLQ = hasCCLQ;
	}

	public String getHasGKSK() {
		return hasGKSK;
	}

	public void setHasGKSK(String hasGKSK) {
		this.hasGKSK = hasGKSK;
	}

	public String getHasSHK() {
		return hasSHK;
	}

	public void setHasSHK(String hasSHK) {
		this.hasSHK = hasSHK;
	}

	public String getHasHSK() {
		return hasHSK;
	}

	public void setHasHSK(String hasHSK) {
		this.hasHSK = hasHSK;
	}

	public String getHasPTTCN() {
		return hasPTTCN;
	}

	public void setHasPTTCN(String hasPTTCN) {
		this.hasPTTCN = hasPTTCN;
	}

	public String getHasCKBMTT() {
		return hasCKBMTT;
	}

	public void setHasCKBMTT(String hasCKBMTT) {
		this.hasCKBMTT = hasCKBMTT;
	}

	public String getHasCKBMTTTN() {
		return hasCKBMTTTN;
	}

	public void setHasCKBMTTTN(String hasCKBMTTTN) {
		this.hasCKBMTTTN = hasCKBMTTTN;
	}

	public String getHasCKTN() {
		return hasCKTN;
	}

	public void setHasCKTN(String hasCKTN) {
		this.hasCKTN = hasCKTN;
	}

	public String getHasHDTV() {
		return hasHDTV;
	}

	public void setHasHDTV(String hasHDTV) {
		this.hasHDTV = hasHDTV;
	}

	public void setStaffId(UUID staffId) {
		this.staffId = staffId;
	}

	public String getHasEmployeeProfile() {
		return hasEmployeeProfile;
	}

	public void setHasEmployeeProfile(String hasEmployeeProfile) {
		this.hasEmployeeProfile = hasEmployeeProfile;
	}

    
}
