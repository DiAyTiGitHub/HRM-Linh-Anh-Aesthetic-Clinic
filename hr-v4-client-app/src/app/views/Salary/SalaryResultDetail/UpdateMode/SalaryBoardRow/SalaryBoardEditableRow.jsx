import React, { memo, useEffect, useState } from "react";
import { observer } from "mobx-react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import SalaryEditableCell from "./SalaryEditableCell";
import { useStore } from "app/stores";
import { useFormikContext } from "formik";

function SalaryBoardEditableRow(props) {
    const {
        data,
        rowIndex
    } = props;

    const { salaryResultDetailStore } = useStore();
    const {
        reCalculateRowByChangingCellValue,
    } = salaryResultDetailStore;

    const [skipFirstRun, setSkipFirstRun] = useState(true);

    // async function handleChangeCellValue(event) {
    //     const newValue = event.target.value;

    //     // Update the Formik state
    //     const fieldName = `salaryResultStaffs[${rowIndex}].salaryResultStaffItems[${columnIndex}].value`;
    //     setFieldValue(fieldName, newValue);

    //     try {
    //         const needRecalculateRow = JSON.parse(JSON.stringify(values.salaryResultStaffs[rowIndex]));
    //         needRecalculateRow.salaryResultStaffItems[columnIndex].value = newValue;

    //         const recalculatedRowData = await reCalculateRowByChangingCellValue(cellData?.id, needRecalculateRow);

    //         console.log("recalculatedRowData", recalculatedRowData);
    //         const rowFieldName = `salaryResultStaffs[${rowIndex}]`;
    //         setFieldValue(rowFieldName, recalculatedRowData);

    //     }
    //     catch (error) {
    //         console.error(error);
    //     }

    // };

    const { setFieldValue } = useFormikContext();

    async function handleChangeCellValue(changeCellId) {
        try {
            const needRecalculateRow = JSON.parse(JSON.stringify(data));
            const recalculatedRowData = await reCalculateRowByChangingCellValue(changeCellId, needRecalculateRow);

            // console.log("recalculatedRowData", recalculatedRowData);
            const rowFieldName = `salaryResultStaffs[${rowIndex}]`;
            setFieldValue(rowFieldName, recalculatedRowData);

        }
        catch (error) {
            console.error(error);
        }

    };

    useEffect(() => {
        if (skipFirstRun) {
            setSkipFirstRun(false); // Prevent the next run from being skipped
            return; // Skip the first call
        }

        let changeCellId = null;
        for (let i = 0; i < data?.salaryResultStaffItems?.length; i++) {
            if (data?.salaryResultStaffItems[i]?.value !== data?.salaryResultStaffItems[i]?.oldValue) {
                changeCellId = data?.salaryResultStaffItems[i]?.id;
                break;
            }
        }

        if (changeCellId) {
            handleChangeCellValue(changeCellId);
            setSkipFirstRun(true);
        }

        // console.log("api will be called", changeCellId);
    }, [JSON.stringify(data)]);

    return (
        <>
            {
                data?.salaryResultStaffItems?.map(function (item, columnIndex) {
                    return (
                        <td key={item?.id}>
                            <SalaryEditableCell
                                rowIndex={rowIndex}
                                columnIndex={columnIndex}
                                cellData={item}
                            />
                        </td>
                    );
                })
            }
        </>
    );
}

export default memo(observer(SalaryBoardEditableRow));