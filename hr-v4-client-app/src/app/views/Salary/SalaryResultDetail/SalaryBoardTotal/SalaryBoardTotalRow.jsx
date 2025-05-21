import { useStore } from "app/stores";
import { observer } from "mobx-react";
import React, { memo } from "react";
import SalaryBoardTotalCell from "./SalaryBoardTotalCell";
import { IconButton, Icon, Tooltip } from "@material-ui/core";


function SalaryBoardTotalRow(props) {
    const {
        getHardCodeColumns
    } = props;

    const { salaryResultDetailStore, payrollStore } = useStore();

    const {
        onViewSalaryResult,
        listSalaryResultStaffs,
    } = salaryResultDetailStore;

    const { totalSalaryResultStaff } = payrollStore;

    //let renderRow = totalSalaryResultStaff;

    return (
        <tr className={`row-table-body row-table-no_data`}>
            <td className="stickyCell lastRowCellStyle">
                <div className="flex flex-middle justify-center">
                    <Tooltip title="Tổng tiền (VNĐ)" placement="top">
                        <IconButton className="bg-white" size="small"

                        >
                            <Icon fontSize="small" style={{ color: "#7c7c12" }}
                            >
                                {/* remove_red_eye */}
                                local_atm
                            </Icon>
                        </IconButton>
                    </Tooltip>
                </div>
            </td>

            {getHardCodeColumns()?.map(function (column, index) {

                return (
                    <td key={index} className={`px-6 text-align-right no-wrap-text lastRowCellStyle`}>
                    </td>
                );
            })}
            {
                totalSalaryResultStaff?.salaryResultStaffItems?.map(function (item, jndex) {
                    if (!item?.salaryTemplateItem?.hiddenOnSalaryBoard) {
                        return (
                            <SalaryBoardTotalCell
                                jndex={jndex}
                                item={item}
                            />
                        );
                    }
                })
            }
        </tr>
    );
}

export default memo(observer(SalaryBoardTotalRow));