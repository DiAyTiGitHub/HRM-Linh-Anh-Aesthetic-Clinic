import { useStore } from "app/stores";
import { observer } from "mobx-react";
import React, { memo, useMemo } from "react";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import PayrollBoardTotalCell from "./PayrollBoardTotalCell";

function PayrollBoardTotalRow() {
    const { salaryStaffPayslipStore } = useStore();
    const {
        onViewSalaryBoard
    } = salaryStaffPayslipStore;

    let renderRow = [];
    if (onViewSalaryBoard?.salaryResultStaffs?.length > 0) {
        renderRow = onViewSalaryBoard?.salaryResultStaffs[0];
    }

    return (
        <tr className={`row-table-body row-table-no_data`}>
            <td className="stickyCell lastRowCellStyle">
                <div className="flex flex-middle justify-center">
                    <Tooltip title="Tổng tiền (VNĐ)" placement="top">
                        <IconButton className="bg-white" size="small"

                        >
                            <Icon fontSize="small" style={{ color: "#7c7c12" }}
                            >
                                local_atm
                            </Icon>
                        </IconButton>
                    </Tooltip>
                </div >
            </td>

            {
                onViewSalaryBoard?.templateItems?.map(function (item, jndex) {

                    return (
                        <PayrollBoardTotalCell
                            jndex={jndex}
                            item={item}
                        />
                    );
                })
            }
        </tr>
    );
}

export default memo(observer(PayrollBoardTotalRow));