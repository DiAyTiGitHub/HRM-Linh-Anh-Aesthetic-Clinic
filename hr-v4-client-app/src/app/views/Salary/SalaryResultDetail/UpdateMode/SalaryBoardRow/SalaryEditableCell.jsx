import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useFormikContext } from "formik";
import { trim } from "lodash";
import { observer } from "mobx-react";
import React, { memo, useEffect, useMemo } from "react";
import { convertDataToVariableValues, evaluateExpression, extractVariables, getSpecialCellItemFormula, isSpecialCellItem } from "./CellFunctions";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import LocalConstants from "app/LocalConstants";
import { useStore } from "app/stores";

function SalaryEditableCell(props) {
    // function getUsingFormula(columnIndex) {
    //     const findingList = Array.from(values.resultItems);

    //     let foundItem = findingList[columnIndex];

    //     return foundItem?.usingFormula;
    // }

    // const usingFormula = useMemo(function () {
    //     // console.log(columnIndex, "catched cell data: ", cellData);
    //     // THIS CHECK SPECIAL FORMULA HAS BEEN PROCESSED IN BACKEND
    //     // if (isSpecialCellItem(cellData?.referenceCode)) {
    //     //     const inputCellFormula = {
    //     //         code: cellData?.referenceCode,
    //     //         items: values.resultItems,
    //     //     };
    //     //     const specialFormula = getSpecialCellItemFormula(inputCellFormula);
    //     //     // console.log("code", cellData?.referenceCode, "specialFormula", specialFormula);
    //     //     return specialFormula;
    //     // }

    //     return getUsingFormula(columnIndex);
    // }, [columnIndex]);


    // const needToFillVariables = useMemo(function () {
    //     if (usingFormula && usingFormula.length > 0)
    //         return extractVariables(usingFormula);
    //     return [];
    // }, [usingFormula]);

    // const dependenciesCalculation = useMemo(function () {
    //     if (!needToFillVariables || needToFillVariables.length <= 0) return [];

    //     const dependenciesList = [];

    //     const findingList = Array.from(values.salaryResultStaffs[rowIndex].salaryResultStaffItems);
    //     for (let i = 0; i < findingList.length; i++) {
    //         const onLoopItem = findingList[i];

    //         if (needToFillVariables.includes(trim(onLoopItem.referenceCode))) {
    //             dependenciesList.push(`salaryResultStaffs[${rowIndex}].salaryResultStaffItems[${i}]`);
    //         }
    //     }

    //     return dependenciesList;

    // }, [...needToFillVariables]);

    // const computedDependencies = useMemo(() => {
    //     if (!dependenciesCalculation || dependenciesCalculation.length <= 0) return [];

    //     return dependenciesCalculation.map(dep => {
    //         const pathSegments = dep.split(/[\[\].]+/).filter(Boolean);
    //         let current = values;
    //         for (const segment of pathSegments) {
    //             if (current && current[segment] !== undefined) {
    //                 current = current[segment];
    //             } else {
    //                 return undefined;
    //             }
    //         }
    //         return current;
    //     });
    // }, [dependenciesCalculation, values]);

    // useEffect(() => {
    //     if (!computedDependencies || computedDependencies.length <= 0 || !usingFormula) return;

    //     // console.log("computedDependencies", computedDependencies);

    //     const expression = usingFormula;
    //     const variableValues = convertDataToVariableValues(computedDependencies);

    //     const cellValue = evaluateExpression(expression, variableValues);

    //     setFieldValue(`salaryResultStaffs[${rowIndex}].salaryResultStaffItems[${columnIndex}].value`, cellValue);
    // }, [...computedDependencies]);

    const {
        rowIndex,
        columnIndex,
        cellData
    } = props;

    let useNumberInput = false;
    if (cellData?.valueType == LocalConstants.SalaryItemValueType.MONEY.value
        || cellData?.valueType == LocalConstants.SalaryItemValueType.NUMBER.value
        || cellData?.valueType == LocalConstants.SalaryItemValueType.PERCENT.value
    )
        useNumberInput = true;

    return (
        <>
            {useNumberInput && (
                <GlobitsVNDCurrencyInput
                    textAlignRight
                    name={`salaryResultStaffs[${rowIndex}].salaryResultStaffItems[${columnIndex}].value`}
                    // onChange={handleChangeCellValue}
                />
            )}

            {!useNumberInput && (
                <GlobitsTextField
                    name={`salaryResultStaffs[${rowIndex}].salaryResultStaffItems[${columnIndex}].value`}
                />
            )}
        </>
    );
}

export default memo(observer(SalaryEditableCell));