package com.globits.hr.dto;

import java.io.Serializable;
import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.FileDescriptionDto;

public class StaffLabourAgreementAttachmentDto extends BaseObjectDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private FileDescriptionDto file;
    private StaffLabourAgreementDto staffLabourAgreement;

    public FileDescriptionDto getFile() {
        return file;
    }

    public void setFile(FileDescriptionDto file) {
        this.file = file;
    }

    public StaffLabourAgreementDto getStaffLabourAgreement() {
        return staffLabourAgreement;
    }

    public void setStaffLabourAgreement(StaffLabourAgreementDto staffLabourAgreement) {
        this.staffLabourAgreement = staffLabourAgreement;
    }

    public StaffLabourAgreementAttachmentDto() {
    }
}
