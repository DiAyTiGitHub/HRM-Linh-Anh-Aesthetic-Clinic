import LocalConstants from "app/LocalConstants";
import { formatMoney, formatVNDMoney } from "app/LocalFunction";
import { useStore } from "app/stores";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import React, { memo, useMemo } from "react";

function PayrollBoardTotalCell(props) {
    const { salaryStaffPayslipStore } = useStore();
    const {
        onViewSalaryBoard
    } = salaryStaffPayslipStore;

    const {
        jndex,
        item
    } = props;

    function getDisplayValue() {

        if (item?.valueType == LocalConstants.SalaryItemValueType.MONEY.value
            // || item?.valueType == LocalConstants.SalaryItemValueType.NUMBER.value
        ) {
            let currentSum = 0;

            for (let i = 0; i < onViewSalaryBoard?.salaryResultStaffs?.length; i++) {
                let staff = onViewSalaryBoard?.salaryResultStaffs[i];
                if (staff.salaryResultStaffItems && staff.salaryResultStaffItems[jndex]) {
                    currentSum += Number.parseFloat(staff.salaryResultStaffItems[jndex].value);
                }
            }

            return formatVNDMoney(currentSum)
            // + " VNĐ";
        }

        return "";
    }

    let displayValue = getDisplayValue();

    // console.log("displayValue", displayValue);

    return (
        <td key={item?.id} className={`px-6 text-align-right no-wrap-text lastRowCellStyle`}>
            {displayValue}
        </td>
    );
}

export default memo(observer(PayrollBoardTotalCell));