import { Button, ButtonGroup, DialogActions, DialogContent, Grid, makeStyles, Table, TableBody, TableCell, TableHead, TableRow } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import TabAccordion from "app/common/Accordion/TabAccordion";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { FieldArray, Form, Formik, getIn, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import GlobitsPagingAutocomplete from "../../../common/form/GlobitsPagingAutocomplete";
import { pagingSalaryPeriod } from "../SalaryPeriod/SalaryPeriodService";
import TabResultStaffs from "../SalaryResultBoardConfig/SalaryResultConfigTabs/TabResultStaffs";
import ChooseUsingStaffSection from "app/views/User/UsingAccountStaff/ChooseUsingStaffSection";
import TableChartIcon from '@material-ui/icons/TableChart';
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import LocalConstants from "app/LocalConstants";
import { handleCalculateSalary, updateSalaryStaff } from "./SalaryStaffPayslipService";
import { Dialpad } from "@material-ui/icons";
import * as Yup from "yup";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { getSalaryTemplatesOfStaff } from "app/views/StaffSalaryTemplate/StaffSalaryTemplateService";


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
        overflowX: "auto",
        overflowY: "auto",
        maxHeight: "60vh",
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

function SalaryResultStaffItemSection(props) {
    const { t } = useTranslation();

    const {
        hrRoleUtilsStore
    } = useStore();

    const {
        isAdmin, isManager
    } = hrRoleUtilsStore;

    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();

    // useEffect(() => {
    //     console.log("catched");
    //     setFieldValue("salaryResultStaffItems", []);
    // }, [values?.staff?.id, values?.salaryPeriod?.id, values?.salaryTemplate?.id]);


    return (
        <FieldArray
            name="salaryResultStaffItems"
            render={(arrayHelpers) => (
                <div className={classes.groupContainer}>
                    <div className={classes.tableContainer}>
                        {getIn(values, "salaryResultStaffItems")?.length > 0 ? (
                            <Table style={{ tableLayout: "auto" }}>
                                <TableHead>
                                    <TableRow>
                                        <TableCell colSpan={1}>
                                            {t("Thành phần lương")}
                                        </TableCell>
                                        <TableCell colSpan={1}>
                                            {t("Giá trị")}
                                        </TableCell>
                                        <TableCell colSpan={1}>
                                            {t("Loại giá trị")}
                                        </TableCell>

                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {getIn(values, "salaryResultStaffItems")?.map((item, index) => (
                                        <TableRow key={index}>
                                            <TableCell colSpan={1} style={{
                                                textAlign: "left",
                                                marginLeft: "5px"
                                            }}>
                                                {item?.referenceName
                                                    ? item?.referenceName
                                                    : null}
                                            </TableCell>
                                            <TableCell colSpan={1}>
                                                <GlobitsVNDCurrencyInput
                                                    name={`salaryResultStaffItems.${index}.value`}
                                                    disabled={(!isAdmin && !isManager) || item?.salaryTemplateItem?.calculationType != LocalConstants.SalaryItemCalculationType.USER_FILL.value}
                                                // disabled={item?.salaryTemplateItem?.calculationType != LocalConstants.SalaryItemCalculationType.USER_FILL.value}
                                                />
                                            </TableCell>
                                            <TableCell colSpan={1}>
                                                {LocalConstants.SalaryItemCalculationType.getListData().find(i => i.value == item?.salaryTemplateItem?.calculationType)?.name}
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
    )
}


export default memo(observer(SalaryResultStaffItemSection));