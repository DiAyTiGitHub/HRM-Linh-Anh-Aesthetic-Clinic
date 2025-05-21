import { useStore } from "app/stores";
import { observer } from "mobx-react";
import React, { memo, useMemo } from "react";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import SalaryOutcomeTotalCell from "./SalaryOutcomeTotalCell";

function SalaryOutcomeTotalRow(props) {
    const {
        getHardCodeColumns
    } = props;

    const { salaryOutcomeStore } = useStore();
    const {
        onViewSalaryResult
    } = salaryOutcomeStore;

    let renderRow = [];
    if (onViewSalaryResult?.salaryResultStaffs?.length > 0) {
        renderRow = onViewSalaryResult?.salaryResultStaffs[0];
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
                </div>
            </td>

            <td className="stickyCell lastRowCellStyle">

            </td>

            <td className="stickyCell lastRowCellStyle">

            </td>   

            <td className="stickyCell lastRowCellStyle">

            </td>

            {getHardCodeColumns()?.map(function (column, index) {
                return (
                    <td key={index} className={`px-6 center no-wrap-text  lastRowCellStyle`}>
                    </td>
                );
            })}

            {
                onViewSalaryResult?.templateItems?.map(function (item, jndex) {
                    if (!item?.hiddenOnSalaryBoard) {
                        return (
                            <SalaryOutcomeTotalCell
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

export default memo(observer(SalaryOutcomeTotalRow));