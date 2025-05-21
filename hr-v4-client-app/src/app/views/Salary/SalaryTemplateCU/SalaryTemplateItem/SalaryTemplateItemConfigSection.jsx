import {
    Button, ButtonGroup, Grid, makeStyles
} from "@material-ui/core";
import {Add, Delete} from "@material-ui/icons";
import {FieldArray, useFormikContext} from "formik";
import {observer} from "mobx-react";
import React, {memo, useState} from "react";
import {useTranslation} from 'react-i18next';
import GlobitsSelectInput from "../../../../common/form/GlobitsSelectInput";
import LocalConstants from "../../../../LocalConstants";
import GlobitsNumberInput from "../../../../common/form/GlobitsNumberInput";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import GlobitsTextField from "app/common/form/GlobitsTextField";

const useStyles = makeStyles(() => ({
    root: {
        background: "#E4f5fc", padding: "10px 15px", borderRadius: "5px",
    }, groupContainer: {
        width: "100%", "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    }, tableContainer: {
        marginTop: "16px", "& table": {
            border: "1px solid #ccc", borderCollapse: "collapse", "& td": {
                border: "1px solid #ccc",
            }
        }
    }, tableHeader: {
        width: "100%", borderBottom: "1px solid #ccc", marginBottom: "8px", "& th": {
            // width: "calc(100vw / 4)",
            border: "1px solid #ccc", padding: "8px 0 8px 4px"
        },
    },
}));

function SalaryTemplateItemConfigSection() {
    const {t} = useTranslation();
    const classes = useStyles();
    const {values, setFieldValue} = useFormikContext();

    function handleAddNewRow(push) {
        const newItem = {
            thresholdValue: null,
            itemValue: null,
            configType: LocalConstants.ConfigType.FIX.value,
            formula: null
        };
        push(newItem);
    }

    return (<Grid container spacing={2}>
        <Grid item xs={12}>
            <FieldArray name="templateItemConfigs">
                {({insert, remove, push}) => (<>
                    <Grid item xs={12}>
                        <Grid container spacing={1} className=''>
                            <Grid item xs={12} sm={4}>
                                <ButtonGroup
                                    color="container"
                                    aria-label="outlined primary button group"
                                >
                                    <Button
                                        onClick={() => handleAddNewRow(push)}
                                        fullWidth
                                        //  disabled={!hasEditPermission}
                                    >
                                        <Add color="white" className='mr-2 addIcon'/>
                                        Thêm ngưỡng
                                    </Button>
                                </ButtonGroup>
                            </Grid>
                        </Grid>
                    </Grid>

                    <Grid item xs={12}>
                        <section className={classes.tableContainer}>
                            <table className={`w-100 ${classes.table}`}>
                                <thead>
                                <tr className={classes.tableHeader}>
                                    <th align="center" style={{width: "20%"}}>Giá trị để so sánh</th>
                                    <th align="center" style={{width: "20%"}}>Giá trị tương ứng</th>
                                    <th align="center" style={{width: "20%"}}>Công thức</th>
                                    <th align="center" style={{width: "20%"}}>Loại cấu hình</th>
                                    <th align="center">Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                {values?.templateItemConfigs?.length > 0 ? (values?.templateItemConfigs?.map((order, index) => (
                                    <SalaryTemplateItemConfigSectionItem
                                        key={index}
                                        index={index}
                                        order={order}
                                        templateItemConfigs={values?.templateItemConfigs}
                                        nameSpace={`templateItemConfigs[${index}]`}
                                        remove={() => remove(index)}
                                        push={() => push(index)}
                                        //  disabled={!hasEditPermission}
                                    />))) : (<tr className='row-table-body row-table-no_data'>
                                    <td colSpan={4} align='center' className="py-8">Chưa có phần tử nào
                                    </td>
                                </tr>)}
                                </tbody>
                            </table>
                        </section>
                    </Grid>

                </>)}
            </FieldArray>
            {/* </Form>
                    )}
                </Formik> */}
        </Grid>
    </Grid>)
}

const SalaryTemplateItemConfigSectionItem = memo((props) => {
    const {
        index, templateItemConfigs, order, remove, push, nameSpace, disabled
    } = props;

    const {
        setFieldValue, values

    } = useFormikContext();

    const {t} = useTranslation();

    const handleTabKeyPress = (event) => {
        if (event.key === 'Tab') {
            if ((Number(index) === Number(templateItemConfigs?.length - 1))) {
                push();
            }
        }
    };

    const withNameSpace = (field) => {
        if (field) return `${nameSpace}.${field}`
        return nameSpace;
    }

    const [isItemValue, setItemValue] = useState(true);
    const [isFormula, setFormula] = useState(false);

    return (<tr className='row-table-body' key={index}>
        <td>
            <GlobitsVNDCurrencyInput name={withNameSpace("thresholdValue")}/>
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
            />
        </td>
        <td>
            <GlobitsSelectInput
                name={withNameSpace("configType")}
                options={LocalConstants.ConfigType.getListData()}
                hideNullOption={true}
                keyValue="value"
                handleChange={(evt) => {
                    const {value} = evt.target;
                    setFieldValue(withNameSpace("configType"), value);
                    if (value === LocalConstants.ConfigType.FIX.value) {
                        setFormula(false)
                        setItemValue(true)
                        setFieldValue(withNameSpace("formula"), "");
                    } else {
                        setFormula(true)
                        setItemValue(false)
                        setFieldValue(withNameSpace("itemValue"), "");
                    }
                }}
            />
        </td>

        {!disabled && <td align='center'>
                    <span
                        // tooltip={t("Xóa")}
                        className="pointer tooltip text-red"
                        style={{cursor: 'pointer'}}
                        onClick={remove}
                    >
                <Delete
                    className="text-red"
                />
            </span>
        </td>
        }

    </tr>)
})

export default memo(observer(SalaryTemplateItemConfigSection));
