import React, { memo, useEffect } from "react";
import { Grid, makeStyles, TableCell, Table, TableHead, TableRow, TableBody, } from "@material-ui/core";
import { useFormikContext, FieldArray, getIn } from "formik";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import { useStore } from "app/stores";
import LocalConstants from "app/LocalConstants";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import { pagingSalaryTemplates } from "app/views/Salary/SalaryTemplate/SalaryTemplateService";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";


const useStyles = makeStyles((theme) => ({
    root: {
        padding: "10px 15px",
        borderRadius: "5px",
    },
    groupContainer: {
        width: "100%",
        "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    },
    tableContainer: {
        maxHeight: "66vh",
        overflowY: "auto",
        "& .MuiTableCell-body": {
            border: "1px solid #e9ecef",
            textAlign: "center",
        },
        "& .MuiTableCell-head": {
            padding: "10px",
            minWidth: "150px",
            border: "1px solid #e9ecef",
            textAlign: "center",
        },
    },
    tableHeader: {
        width: "100%",
        borderBottom: "1px solid #E3F2FD",
        marginBottom: "8px",
        "& th": {
            width: "calc(100vw / 4)",
        },
    },
}));

function LabourAgreementSalaryItemValues(props) {
    const { t } = useTranslation();

    const {
        staffSalaryItemValueStore,
    } = useStore();

    const {
        getListSalaryTemplateItem
    } = staffSalaryItemValueStore;

    const {
        values,
        setFieldValue
    } = useFormikContext();

    const classes = useStyles();

    useEffect(function () {
        if (!values?.staff?.id || !values?.salaryTemplate?.id) {
            return;
        }

        async function handleGetTemplateItemValues() {

            const response = await getListSalaryTemplateItem(values);

            setFieldValue("staffSalaryItemValue", response?.staffSalaryItemValue);
        }

        handleGetTemplateItemValues();


    }, [values?.staff?.id, values?.salaryTemplate?.id]);



    return (
        <Grid container spacing={2}>
            <Grid item xs={12} sm={6} md={4}>
                <GlobitsPagingAutocompleteV2
                    name="salaryTemplate"
                    label={t("Mẫu bảng lương")}
                    api={pagingSalaryTemplates}
                />
            </Grid>

            <Grid item xs={12}>
                <FieldArray
                    name="staffSalaryItemValue"
                    render={(arrayHelpers) => (
                        <div className={classes.groupContainer}>
                            <div className={classes.tableContainer}>
                                {getIn(values, "staffSalaryItemValue")?.length > 0 ? (
                                    <Table style={{ tableLayout: "auto" }}>
                                        <TableHead>
                                            <TableRow>
                                                <TableCell colSpan={1} className="py-8">
                                                    {t("Thành phần lương")}
                                                </TableCell>
                                                <TableCell colSpan={1} className="py-8">
                                                    {t("Giá trị cố định")}
                                                </TableCell>
                                                <TableCell colSpan={1} className="py-8">
                                                    {t("Loại giá trị")}
                                                </TableCell>

                                            </TableRow>
                                        </TableHead>
                                        <TableBody>
                                            {getIn(values, "staffSalaryItemValue")?.map((item, index) => (
                                                <TableRow key={index}>
                                                    <TableCell colSpan={1} className="py-4">
                                                        {item?.salaryItem?.name
                                                            ? item?.salaryItem?.name
                                                            : null}
                                                    </TableCell>
                                                    <TableCell colSpan={1} className="py-4">
                                                        <div className="flex flex-center px-4">
                                                            <GlobitsVNDCurrencyInput
                                                                name={`staffSalaryItemValue.${index}.value`}
                                                            />
                                                        </div>
                                                    </TableCell>
                                                    <TableCell colSpan={1} className="py-4">
                                                        {LocalConstants.SalaryItemCalculationType.getListData().find(i => i.value == item?.calculationType)?.name}
                                                    </TableCell>
                                                </TableRow>
                                            ))}
                                        </TableBody>
                                    </Table>
                                ) : (
                                    <p className="flex justify-center">

                                    </p>
                                )}
                            </div>
                        </div>
                    )}
                />
            </Grid>


        </Grid>
    );
}

export default memo(observer(LabourAgreementSalaryItemValues));






