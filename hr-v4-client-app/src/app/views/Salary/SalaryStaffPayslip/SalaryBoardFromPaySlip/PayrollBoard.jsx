import React, { useState, useEffect, memo, useMemo } from "react";
import { Grid, makeStyles } from "@material-ui/core";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import LocalConstants from "app/LocalConstants";
import { formatMoney, formatVNDMoney } from "app/LocalFunction";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import PayrollBoardTotalRow from "./PayrollBoardTotalRow";

const useStyles = makeStyles(() => ({
    root: {
        background: "#E4f5fc",
        padding: "10px 15px",
        borderRadius: "5px",
    },
    groupContainer: {
        width: "100%",
        "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    },
}));

function PayrollBoard() {
    const { t } = useTranslation();

    const { salaryStaffPayslipStore } = useStore();
    const {
        handleClose,
        openViewSalaryBoard,
        viewSalaryBoard,
        onViewSalaryBoard
    } = salaryStaffPayslipStore;

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <section className="commonTableContainer">
                    <table className={`commonTable w-100`}>
                        <thead>
                            {/* <tr className="tableHeader">
                                <th rowSpan={2} align="center" style={{ width: "40px" }}
                                    className="stickyCell stickyHeader"
                                >
                                    Thao tác
                                </th>

                               
                            </tr> */}


                            <tr className="tableHeader">
                                <th align="center"
                                    className="stickyCell stickyHeader no-wrap-text pr-8"
                                >
                                    Họ và tên
                                </th>

                                {onViewSalaryBoard?.templateItems?.map(function (column, index) {

                                    return (
                                        <th key={index} align="center" style={{ minWidth: "120px" }}>
                                            {column?.displayName}
                                        </th>
                                    );
                                })}

                            </tr>
                        </thead>

                        <tbody>
                            {
                                onViewSalaryBoard?.salaryResultStaffs?.map(function (resultStaff, index) {

                                    return (
                                        <tr key={index} className={`row-table-body row-table-no_data`}>
                                            {/* <td className="stickyCell">
                                                <div className="flex flex-middle justify-center">
                                                    <Tooltip title="Phiếu lương nhân viên" placement="top">
                                                        <IconButton className="bg-white" size="small"
                                                            onClick={() => {
                                                                handleOpenCreateEdit(resultStaff?.id);
                                                            }}
                                                        >
                                                            <Icon fontSize="small" color="primary"
                                                            >
                                                                monetization_on
                                                            </Icon>
                                                        </IconButton>
                                                    </Tooltip>
                                                </div >
                                            </td> */}

                                            <td className="stickyCell text-center no-wrap-text">
                                                {resultStaff?.staffName}
                                            </td>

                                            {
                                                resultStaff?.salaryResultStaffItems?.map(function (item, jndex) {
                                                    let align = "";
                                                    let displayValue = item?.value;

                                                    if (item?.referenceCode == LocalConstants.SalaryItemCodeSystemDefault.STT_SYSTEM.value) {
                                                        align = "text-center";
                                                    }
                                                    else if (item?.valueType == LocalConstants.SalaryItemValueType.MONEY.value
                                                        || item?.valueType == LocalConstants.SalaryItemValueType.NUMBER.value
                                                        || item?.valueType == LocalConstants.SalaryItemValueType.PERCENT.value
                                                    ) {
                                                        align = "text-align-right";
                                                        if (displayValue) {
                                                            displayValue = formatVNDMoney(displayValue);
                                                        }
                                                    }

                                                    return (
                                                        <td key={index + "_" + jndex} className={`px-6 ${align ? align : ""} no-wrap-text`}>
                                                            {displayValue}
                                                        </td>
                                                    );
                                                })
                                            }
                                        </tr>
                                    );
                                })
                            }

                            <PayrollBoardTotalRow />

                        </tbody>
                    </table>


                </section>
            </Grid>
        </Grid >
    );
}

export default memo(observer(PayrollBoard));

