import React, {useState, useEffect, memo, useMemo} from "react";
import {Grid, makeStyles} from "@material-ui/core";
import {useFormikContext} from "formik";
import {observer} from "mobx-react";
import {useTranslation} from "react-i18next";
import {useStore} from "app/stores";
import LocalConstants from "app/LocalConstants";
import {formatMoney, formatVNDMoney} from "app/LocalFunction";
import {IconButton, Icon, Tooltip} from "@material-ui/core";
import SalaryBoardTotalRow from "../SalaryBoardTotal/SalaryBoardTotalRow";

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

function SalaryBoardViewMode() {
    const {t} = useTranslation();

    const classes = useStyles();
    const {
        salaryResultDetailStore,
        salaryStaffPayslipStore
    } = useStore();

    const {
        onViewSalaryResult,
    } = salaryResultDetailStore;

    const {
        handleOpenCreateEdit,
        handleOpenRecalculatePayslip
    } = salaryStaffPayslipStore;

    function getHardCodeColumns() {
        return [
            {
                displayName: "Mã NV",
                field: "staffCode",
            },
            {
                displayName: "Họ và tên",
                field: "staffName",
            },
            {
                displayName: "Đơn vị",
                field: "mainOrganization",
            },
            {
                displayName: "Phòng ban",
                field: "mainDepartment",
            },
            {
                displayName: "Chức danh",
                field: "mainPositionTitle",
            },{
                displayName: "Vị trí",
                field: "mainPosition",
            },
        ];
    }

    const columnHeaders = useMemo(function () {
        const columnGroups = [];
        const remainItems = [];

        const data = JSON.parse(JSON.stringify(onViewSalaryResult));

        data?.templateItems?.forEach(function (item) {
            if (!item?.hiddenOnSalaryBoard) {
                if (item.templateItemGroupId == null) {
                    // this is a common column has rowspan = 2
                    let columnItem = JSON.parse(JSON.stringify(item));
                    columnItem.isItem = true;

                    columnGroups.push(columnItem);
                } else {
                    // these code below handle for column group and its item

                    // 1st case: the group existed right before => merge column with old consecutive before
                    if (columnGroups.length > 0 && columnGroups[columnGroups.length - 1].id == item.templateItemGroupId) {
                        columnGroups[columnGroups.length - 1].colSpan++;
                    }
                    // 2nd case: the group is not appear right before => add new group
                    else {
                        let group = data?.templateItemGroups.find(function (group) {
                            return group.id == item.templateItemGroupId;
                        });
                        group = JSON.parse(JSON.stringify(group));
                        group.colSpan = 1;

                        columnGroups.push(group);
                    }

                    remainItems.push(JSON.parse(JSON.stringify(item)));
                }
            }
        });

        return {
            columnGroups,
            remainItems
        };
    }, [
        onViewSalaryResult?.templateItemGroups,
        onViewSalaryResult?.templateItems
    ]);

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <section className="commonTableContainer">
                    <table className={`commonTable w-100`}>
                        <thead>
                        <tr className="tableHeader">
                            <th rowSpan={2} align="center" style={{width: "40px"}}
                                className="stickyCell stickyHeader"
                            >
                                Thao tác
                            </th>

                            {getHardCodeColumns()?.map(function (column, index) {
                                return (
                                    <React.Fragment key={index}>
                                        <th
                                            rowSpan={2}
                                            align="center"
                                            style={{minWidth: "120px"}}
                                            className="stickyHeader"
                                        >
                                            {column?.displayName}
                                        </th>
                                    </React.Fragment>
                                );
                            })}

                            {columnHeaders.columnGroups.map(function (column, index) {

                                return (
                                    <React.Fragment key={index}>
                                        {column?.isItem && (
                                            <th
                                                rowSpan={2}
                                                align="center"
                                                style={{minWidth: "120px"}}
                                                className="stickyHeader"
                                            >
                                                {column?.displayName}
                                            </th>
                                        )}

                                        {!column?.isItem && (
                                            <th
                                                colSpan={column?.colSpan}
                                                className="stickyHeader"
                                                align="center"
                                            >
                                                {column?.name}
                                            </th>
                                        )}
                                    </React.Fragment>
                                );
                            })}
                        </tr>

                        <tr className="tableHeader">
                            {columnHeaders.remainItems.map(function (column, index) {

                                return (
                                    <th key={index} align="center" style={{minWidth: "120px"}}>
                                        {column?.displayName}
                                    </th>
                                );
                            })}

                        </tr>
                        </thead>

                        <tbody>
                        {
                            onViewSalaryResult?.salaryResultStaffs?.map(function (resultStaff, index) {
                                return (
                                    <tr key={index} className={`row-table-body row-table-no_data`}>
                                        <td className="stickyCell">
                                            <div className="flex flex-middle justify-center">
                                                <Tooltip title="Xem phiếu lương nhân viên" placement="top">
                                                    <IconButton className="bg-white" size="small"
                                                                onClick={() => {
                                                                    handleOpenCreateEdit(resultStaff?.id);
                                                                }}
                                                    >
                                                        <Icon fontSize="small" style={{color: "gray"}}
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

                                        {getHardCodeColumns()?.map(function (column, index) {
                                            return (
                                                <td key={index} className={`px-6 center no-wrap-text`}>
                                                    {resultStaff[`${column?.field}`]}
                                                </td>
                                            );
                                        })}

                                        {
                                            resultStaff?.salaryResultStaffItems?.map(function (item, jndex) {
                                                if (!item?.salaryTemplateItem?.hiddenOnSalaryBoard) {
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

                                                    return (
                                                        <td key={index + "_" + jndex}
                                                            className={`px-6 ${align ? align : ""} no-wrap-text`}>
                                                            {displayValue}
                                                        </td>
                                                    );
                                                }
                                            })
                                        }
                                    </tr>
                                );
                            })
                        }

                        <SalaryBoardTotalRow
                            getHardCodeColumns={getHardCodeColumns}
                        />

                        </tbody>
                    </table>


                </section>
            </Grid>
        </Grid>
    );
}

export default memo(observer(SalaryBoardViewMode));

