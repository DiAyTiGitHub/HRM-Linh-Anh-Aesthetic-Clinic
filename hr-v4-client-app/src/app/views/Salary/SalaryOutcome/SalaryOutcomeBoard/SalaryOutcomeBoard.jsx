import React from "react";
import LocalConstants from "app/LocalConstants";
import { observer } from "mobx-react";
import { memo, useMemo } from "react";
import { formatVNDMoney } from "app/LocalFunction";
import { useStore } from "app/stores";
import SalaryOutcomeTotalRow from "./SalaryOutcomeTotalRow";
import { Checkbox, Icon, IconButton, Tooltip } from "@material-ui/core";
import LockIcon from '@material-ui/icons/Lock';
import SalaryOutcomeRow from "./SalaryOutcomeRow";

function SalaryOutcomeBoard() {
    const {
        salaryOutcomeStore
    } = useStore();

    const {
        onViewSalaryResult,
        listChosenPayslip,
        toggleSelectAll
    } = salaryOutcomeStore;


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
                        let group = data?.templateItemGroups?.find(function (group) {
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
            }, {
                displayName: "Vị trí",
                field: "mainPosition",
            },
        ];
    }

    const isAllChecked = listChosenPayslip?.length === onViewSalaryResult?.salaryResultStaffs?.length;

    return (
        <>
            {
                onViewSalaryResult?.salaryResultStaffs?.length >= 1 && (
                    <section className="commonTableContainer">
                        <table className={`commonTable w-100`}>
                            <thead>
                                <tr className="tableHeader">
                                    <th
                                        rowSpan={2}
                                        align="center"
                                        style={{ width: "40px" }}
                                        className="stickyCell stickyHeader"
                                    >
                                        <Tooltip title={isAllChecked ? "Bỏ chọn tất cả" : "Chọn tất cả"} placement="top" arrow>
                                            <Checkbox
                                                id="radioAll"
                                                name="radSelectAll"
                                                checked={isAllChecked}
                                                onClick={toggleSelectAll}  // Gọi hàm chọn tất cả
                                            />
                                        </Tooltip>
                                    </th>

                                    <th
                                        rowSpan={2}
                                        align="center"
                                        className="stickyCell stickyHeader no-wrap-text"
                                    >
                                        <Tooltip
                                            placement="top"
                                            title="Trạng thái phê duyệt phiếu lương"
                                        >
                                            <span>

                                            </span>
                                        </Tooltip>
                                    </th>

                                    <th rowSpan={2} align="center" style={{ width: "40px" }}
                                        className="stickyCell stickyHeader"
                                    >
                                        Thao tác
                                    </th>

                                    <th
                                        align="center"
                                        rowSpan={2}
                                        className="stickyCell stickyHeader no-wrap-text"
                                    >
                                        STT
                                    </th>

                                    {getHardCodeColumns()?.map(function (column, index) {
                                        return (
                                            <React.Fragment key={index}>
                                                <th
                                                    rowSpan={2}
                                                    align="center"
                                                    style={{ minWidth: "120px" }}
                                                    className="  no-wrap-text"
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
                                                        style={{ minWidth: "120px" }}
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
                                            <th key={index} align="center" style={{ minWidth: "120px" }}>
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
                                            <SalaryOutcomeRow
                                                getHardCodeColumns={getHardCodeColumns}
                                                key={resultStaff?.id}
                                                resultStaff={resultStaff}
                                                rowIndex={index}
                                            />
                                        );
                                    })
                                }

                                <SalaryOutcomeTotalRow
                                    getHardCodeColumns={getHardCodeColumns}
                                />

                            </tbody>
                        </table>


                    </section>
                )
            }

            {
                onViewSalaryResult?.salaryResultStaffs?.length <= 0 && (
                    <div className="w-100 flex flex-center align-center">
                        <p className="m-0 p-0">
                            Chưa có nhân viên được tính lương
                        </p>
                    </div>
                )
            }
        </>
    );
}

export default memo(observer(SalaryOutcomeBoard));