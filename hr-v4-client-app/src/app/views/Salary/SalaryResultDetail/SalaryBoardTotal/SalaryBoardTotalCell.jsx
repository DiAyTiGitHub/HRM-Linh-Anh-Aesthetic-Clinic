import LocalConstants from "app/LocalConstants";
import { formatMoney, formatVNDMoney } from "app/LocalFunction";
import { useStore } from "app/stores";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import React, { memo, useMemo } from "react";

function SalaryBoardTotalCell(props) {
    const { salaryResultDetailStore } = useStore();

    const {
        onViewSalaryResult,
    } = salaryResultDetailStore;

    const {
        jndex,
        item
    } = props;

    function getDisplayValue() {
        // if (item?.valueType == LocalConstants.SalaryItemValueType.MONEY.value
        //     // || item?.valueType == LocalConstants.SalaryItemValueType.NUMBER.value
        // ) {
        //     let currentSum = 0;

        //     for (let i = 0; i < onViewSalaryResult?.salaryResultStaffs?.length; i++) {
        //         let staff = onViewSalaryResult?.salaryResultStaffs[i];
        //         if (staff.salaryResultStaffItems && staff.salaryResultStaffItems[jndex]) {
        //             // console.log("checking: ", staff.salaryResultStaffItems[jndex].value);
        //             currentSum += Number.parseFloat(staff.salaryResultStaffItems[jndex].value);
        //         }
        //     }
        //     // console.log("currentSum: ", currentSum);

        //     return formatVNDMoney(currentSum) 
        //     // + " VNĐ";
        // }

        // return "";

        let displayValue = item?.value;
        if (item?.valueType === LocalConstants.SalaryItemValueType.MONEY.value
            || item?.valueType === LocalConstants.SalaryItemValueType.NUMBER.value
            || item?.valueType === LocalConstants.SalaryItemValueType.PERCENT.value
        ) {
            if (displayValue) {
                displayValue = formatVNDMoney(displayValue);
            }
        }
        return displayValue;
    }

    let displayValue = getDisplayValue();

    return (
        <td key={item?.id} className={`px-6 text-align-right no-wrap-text lastRowCellStyle`}>
            {displayValue}
        </td>
    );
}

export default memo(observer(SalaryBoardTotalCell));