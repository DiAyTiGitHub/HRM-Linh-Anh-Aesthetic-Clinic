package com.globits.salary.dto.search;

import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.dto.SalaryResultStaffDto;

import java.util.UUID;

public class SalaryCalculatePayslipDto extends BaseObjectDto {
    private UUID changedCellId;
    private SalaryResultStaffDto reCalculatingRow;

    public SalaryCalculatePayslipDto() {

    }

    public UUID getChangedCellId() {
        return changedCellId;
    }

    public void setChangedCellId(UUID changedCellId) {
        this.changedCellId = changedCellId;
    }

    public SalaryResultStaffDto getReCalculatingRow() {
        return reCalculatingRow;
    }

    public void setReCalculatingRow(SalaryResultStaffDto reCalculatingRow) {
        this.reCalculatingRow = reCalculatingRow;
    }
}
