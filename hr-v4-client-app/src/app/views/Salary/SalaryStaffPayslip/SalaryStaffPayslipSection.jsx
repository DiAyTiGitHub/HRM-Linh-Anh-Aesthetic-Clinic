import React, { memo } from "react";
import { observer } from "mobx-react";
import { useFormikContext } from "formik";
import { formatValue } from "app/LocalFunction";

function VerticalSalaryStaffPayslipSection() {
    const { values } = useFormikContext();

    return (
        <section className='verticalTableContainer salaryPayslipPrintSection'>
            <div className='vertical-table'>
                <table className='print-table' style={{ width: "100%", tableLayout: "fixed" }}>
                    <tbody>
                        {values?.salaryResultStaffItems?.map((item, index) => {
                            if (item?.salaryTemplateItem?.hiddenOnPayslip) {
                                return null;
                            }
                            const displayValue = formatValue(item?.value, item?.valueType);

                            return (
                                <tr key={`row-${index}`}>
                                    <th
                                        className='column-header'
                                        style={{
                                            width: "50%",
                                            textAlign: "left",
                                            padding: "8px",
                                            fontWeight: "bold",
                                        }}>
                                        {item?.referenceName || item?.salaryResultItem?.displayName}
                                    </th>
                                    <td
                                        className='column-data'
                                        style={{
                                            width: "50%",
                                            textAlign: "left",
                                            padding: "8px",
                                        }}>
                                        {displayValue}
                                    </td>
                                </tr>
                            );
                        })}
                    </tbody>
                </table>
            </div>
        </section>
    );
}

export default memo(observer(VerticalSalaryStaffPayslipSection));
