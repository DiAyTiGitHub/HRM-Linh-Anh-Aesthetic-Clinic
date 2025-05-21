import React from "react";
import LocalConstants from "app/LocalConstants";
import { observer } from "mobx-react";
import { memo, useMemo } from "react";
import { formatVNDMoney } from "app/LocalFunction";
import { useStore } from "app/stores";
import { Checkbox, Icon, IconButton, Tooltip } from "@material-ui/core";
import SalaryOutcomePayslipStatusSection from "./SalaryOutcomePayslipStatusSection";

function SalaryOutcomeRow(props) {
    const {
        resultStaff,
        rowIndex
    } = props;

    const {
        getHardCodeColumns
    } = props;

    const {
        salaryOutcomeStore,
        salaryStaffPayslipStore
    } = useStore();

    const {
        handleOpenCreateEdit,
        handleOpenRecalculatePayslip
    } = salaryStaffPayslipStore;

    const {
        listChosenPayslip,
        handleChosenItem
    } = salaryOutcomeStore;

    const isChecked = listChosenPayslip?.some((item) => item?.id === resultStaff?.id);

    return (
        <tr className={`row-table-body row-table-no_data`}>
            <td className="stickyCell text-center no-wrap-text">
                <Tooltip
                    title={isChecked ? "Bỏ chọn" : "Chọn"}
                    placement="top"
                    arrow

                >
                    <Checkbox
                        // className="pr-16"
                        id={`radio${resultStaff?.id}`}
                        name="radSelected"
                        value={resultStaff.id}
                        checked={isChecked}
                        onClick={() => handleChosenItem(resultStaff)}
                    />
                </Tooltip>
            </td>

            <td className="stickyCell text-center no-wrap-text">
                <SalaryOutcomePayslipStatusSection
                    resultStaff={resultStaff}
                />
            </td>

            <td className="stickyCell">
                <div className="flex flex-middle justify-center">
                    <Tooltip title="Xem phiếu lương nhân viên" placement="top">
                        <IconButton className="bg-white" size="small"
                            onClick={() => {
                                handleOpenCreateEdit(resultStaff?.id);
                            }}
                        >
                            <Icon fontSize="small" style={{ color: "gray" }}
                            >
                                remove_red_eye
                            </Icon>
                        </IconButton>
                    </Tooltip>

                    {
                        resultStaff?.approvalStatus != LocalConstants.SalaryStaffPayslipApprovalStatus?.LOCKED.value && (
                            <Tooltip title="Tính lại phiếu lương" placement="top">
                                <IconButton className="bg-white" size="small"
                                    onClick={() => {
                                        handleOpenRecalculatePayslip(resultStaff?.id);
                                    }}
                                >
                                    <Icon fontSize="small" color="primary"
                                    >
                                        monetization_on
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        )
                    }

                </div>
            </td>

            <td className="stickyCell text-center no-wrap-text">
                {resultStaff?.displayOrder || rowIndex + 1}
            </td>

            {getHardCodeColumns()?.map(function (column, index) {
                return (
                    <td key={index} className={`px-6  text-center no-wrap-text`}>
                        {resultStaff[`${column?.field}`]}
                    </td>
                );
            })}

            {
                resultStaff?.salaryResultStaffItems?.map(function (item, jndex) {
                    let align = "";
                    let displayValue = item?.value;

                    if (item?.referenceCode == LocalConstants.SalaryItemCodeSystemDefault.STT_SYSTEM.value) {
                        align = "text-center";
                    } else if (item?.valueType == LocalConstants.SalaryItemValueType.MONEY.value
                        || item?.valueType == LocalConstants.SalaryItemValueType.NUMBER.value
                        || item?.valueType == LocalConstants.SalaryItemValueType.PERCENT.value
                    ) {
                        align = "text-align-right";
                        if (displayValue) {
                            displayValue = formatVNDMoney(displayValue);
                        }
                    }
                    if (!item?.salaryTemplateItem?.hiddenOnSalaryBoard) {
                        return (
                            <td key={jndex} className={`px-6 ${align ? align : ""} no-wrap-text`}>
                                {displayValue}
                            </td>
                        );
                    }
                })
            }
        </tr>
    );
}


export default memo(observer(SalaryOutcomeRow));