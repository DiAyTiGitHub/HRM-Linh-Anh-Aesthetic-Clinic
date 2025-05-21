import React, { memo } from "react";
import { observer } from "mobx-react";
import { formatVNDMoney } from "app/LocalFunction";
import LocalConstants from "app/LocalConstants";

function SalaryBoardViewRow(props) {
    const {
        data,
    } = props;

    return (
        <>
            {
                data?.salaryResultStaffItems?.map(function (item, jndex) {
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
                        <td key={item?.id} className={`px-6 ${align ? align : ""} no-wrap-text`}>
                            {displayValue}
                        </td>
                    );
                })
            }
        </>
    );
}

export default memo(observer(SalaryBoardViewRow));