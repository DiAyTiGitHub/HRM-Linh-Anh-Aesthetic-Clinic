import React, { memo, useEffect } from "react";
import {
    Grid,
    makeStyles,
    TableCell,
    Table,
    TableHead,
    TableRow,
    TableBody,
    Tooltip,
    IconButton,
    Icon,
} from "@material-ui/core";
import { useFormikContext, FieldArray, getIn } from "formik";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import { useStore } from "app/stores";
import LocalConstants from "app/LocalConstants";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import { pagingSalaryTemplates } from "app/views/Salary/SalaryTemplate/SalaryTemplateService";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import RestoreIcon from '@material-ui/icons/Restore';
import SalaryValueHistoriesPopup from "./SalaryValueHistories/SalaryValueHistoriesPopup";
import { formatMoney, formatVNDMoney } from "app/LocalFunction";

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
    codeCell: {
        whiteSpace: "nowrap",
        overflow: "hidden",
        textOverflow: "ellipsis",
        maxWidth: "150px", // adjust this width to your layout
    },
    tableContainer: {
        maxHeight: "66vh",
        overflowX: "unset !important",

        "& .MuiTableCell-body": {
            border: "1px solid #e9ecef",
            textAlign: "center",
        },
        "& .MuiTableCell-head": {
            padding: "10px",
            // minWidth: "150px",
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

function StaffSalaryItemValue(props) {
    const {
        readOnly,
        displayValueEqualZero = true
    } = props;
    const { t } = useTranslation();

    const {
        staffSalaryItemValueStore,
    } = useStore();

    const {
        getListSalaryTemplateItem,
        handleOpenValueHitoriesPopup,
        openValueHitoriesPopup
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
        <Grid item xs={12}>
            <Grid container spacing={2}>
                {getIn(values, "staffSalaryItemValue")?.length > 0 && (
                    <>
                        <Grid item xs={12} className="pb-0">
                            <p className="m-0 p-0 borderThrough2">
                                Giá trị lương của nhân viên
                            </p>
                        </Grid>

                        <Grid item xs={12}>
                            <FieldArray
                                name="staffSalaryItemValue"
                                render={(arrayHelpers) => (
                                    <div className={classes.groupContainer}>
                                        <div className={classes.tableContainer}>
                                            <Table style={{ tableLayout: "auto" }}>
                                                <TableHead>
                                                    <TableRow>

                                                        <TableCell colSpan={1} className="py-8">
                                                            {t("STT")}
                                                        </TableCell>

                                                        <TableCell colSpan={1} className="py-8">
                                                            {t("Tham số TP")}
                                                        </TableCell>

                                                        <TableCell colSpan={1} className="py-8">
                                                            {t("Tên hiển thị")}
                                                        </TableCell>

                                                        <TableCell colSpan={1} className="py-8">
                                                            {t("Giá trị cố định")}
                                                        </TableCell>

                                                        <TableCell colSpan={1} className="py-8">
                                                            Thao tác
                                                        </TableCell>

                                                    </TableRow>
                                                </TableHead>
                                                <TableBody>
                                                    {getIn(values, "staffSalaryItemValue")
                                                        ?.filter(item => displayValueEqualZero || item?.value !== 0)
                                                        ?.map((item, index) => {
                                                            const code = item?.salaryItem?.code || null;
                                                            const displayName = item?.salaryItem?.name || null;

                                                            return (
                                                                <TableRow key={index}>
                                                                    <TableCell colSpan={1} className="p-4">
                                                                        {index + 1}
                                                                    </TableCell>

                                                                    <Tooltip arrow placement="top" title={code}>
                                                                        <TableCell colSpan={1}
                                                                            className={`px-6 ${classes.codeCell} p-4`}>
                                                                            {code}
                                                                        </TableCell>
                                                                    </Tooltip>

                                                                    <TableCell colSpan={1} className="p-4">
                                                                        {displayName}
                                                                    </TableCell>

                                                                    <TableCell colSpan={1} className="py-4">
                                                                        <div className="flex flex-center px-4">
                                                                            {readOnly ? (
                                                                                <span>{formatVNDMoney(item?.value)}</span>
                                                                            ) : (
                                                                                <GlobitsVNDCurrencyInput
                                                                                    name={`staffSalaryItemValue.${index}.value`}
                                                                                    readOnly={readOnly}
                                                                                />
                                                                            )}
                                                                        </div>
                                                                    </TableCell>

                                                                    <TableCell colSpan={1} className="py-4">
                                                                        {item?.id && (
                                                                            <div className="flex flex-center">
                                                                                <Tooltip arrow placement='top'
                                                                                    title={"Lịch sử giá trị lương"}>
                                                                                    <IconButton size='small'
                                                                                        onClick={() => handleOpenValueHitoriesPopup(item?.id)}>
                                                                                        <Icon fontSize='small'
                                                                                            style={{ color: "green" }}>restore</Icon>
                                                                                    </IconButton>
                                                                                </Tooltip>
                                                                            </div>
                                                                        )}
                                                                    </TableCell>
                                                                </TableRow>
                                                            );
                                                        })}
                                                </TableBody>
                                            </Table>
                                        </div>
                                    </div>
                                )}
                            />
                        </Grid>
                    </>
                )}
            </Grid>


        </Grid>
    );
}

export default memo(observer(StaffSalaryItemValue));






