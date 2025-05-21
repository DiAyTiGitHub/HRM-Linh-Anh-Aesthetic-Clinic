import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import LocalConstants from "app/LocalConstants";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { Add, Delete } from "@material-ui/icons";
import { useFormikContext } from "formik";
import { get } from "lodash";
import { Icon, IconButton, Tooltip } from "@material-ui/core";

function SalaryTemplateItemConfigRow(props) {
    const { index, remove, nameSpace, disabled } = props;
    const { setFieldValue, values } = useFormikContext();

    const withNameSpace = (field) => `${nameSpace}.${field}`;

    const currentConfigType = get(values, withNameSpace("configType"));
    const isItemValue = currentConfigType === LocalConstants.ConfigType.FIX.value;
    const isFormula = currentConfigType === LocalConstants.ConfigType.USING_FORMULA.value;

    useEffect(() => {
        if (isItemValue) setFieldValue(withNameSpace("formula"), null);
        if (isFormula) setFieldValue(withNameSpace("itemValue"), null);
    }, [currentConfigType]);

    return (
        <tr className='row-table-body'>
            <td>
                <GlobitsSelectInput
                    name={withNameSpace("operatorMinValue")}
                    options={LocalConstants.SalaryTemplateItemConfigOperator.getMinOperatorList()}
                    hideNullOption={false}
                    keyValue="value"
                />
            </td>

            <td>
                <GlobitsVNDCurrencyInput name={withNameSpace("minValue")} />
            </td>

            <td>
                <GlobitsSelectInput
                    name={withNameSpace("operatorMaxValue")}
                    options={LocalConstants.SalaryTemplateItemConfigOperator.getMaxOperatorList()}
                    hideNullOption={false}
                    keyValue="value"
                />
            </td>

            <td>
                <GlobitsVNDCurrencyInput name={withNameSpace("maxValue")} />
            </td>

            <td>
                <GlobitsSelectInput
                    name={withNameSpace("configType")}
                    options={LocalConstants.ConfigType.getListData()}
                    hideNullOption
                    keyValue="value"
                />
            </td>

            <td>
                <GlobitsVNDCurrencyInput
                    disabled={!isItemValue}
                    name={withNameSpace("itemValue")}
                />
            </td>

            <td>
                <GlobitsTextField
                    disabled={!isFormula}
                    name={withNameSpace("formula")}
                    multiline
                    minRows={1}
                    isTextArea
                />
            </td>

            {!disabled && (
                <td align='center'>
                    <Tooltip arrow title="Xóa mức ngưỡng">
                        <IconButton size="small" onClick={remove}>
                            <Icon fontSize="small" color="secondary">delete</Icon>
                        </IconButton>
                    </Tooltip>
                </td>
            )}
        </tr>
    );
}

export default memo(observer(SalaryTemplateItemConfigRow));