import { makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip } from "@material-ui/core";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import LocalConstants from "app/LocalConstants";
import { useStore } from "app/stores";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";


const useStyles = makeStyles({
    root: {
        "& .MuiDialogContent-root": {
            overflow: "unset !important",
        },
    },
    marginAuto: {
        display: "flex",
        "& label": {
            margin: "auto",
            marginRight: "10px",
            fontWeight: "500",
            fontSize: "16px",
        },
    },
    table: {
        width: "100%",
        border: "2px solid #2a80c8 !important",
        borderCollapse: "collapse",

        "& .MuiTableCell-root": {
            border: "none",
        },

        "& .MuiTableRow-head": {
            backgroundColor: "#2a80c8",
            border: "1px solid #2a80c8",
        },

        "& .MuiTableCell-head": {
            border: "1px solid #2a80c8",
            color: "#fff",
        },

        "& .MuiTableCell-body": {
            border: "1px solid #2a80c8",
        },

        "& .MuiFormGroup-root": {
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
        },
    },
    tableBody: {
        "& .MuiCheckbox-root": {
            margin: "auto",
        },
        "& .MuiTextField-root": {
            padding: "5px",
        },
    },
    headerDate: {
        fontSize: "22px",
        fontWeight: "700",
    },
    displayFlex: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
    },
});

function SalaryRecalPayslipUserFillSection(props) {
    const { t } = useTranslation();

    const {
        salaryStaffPayslipStore
    } = useStore();

    const { values, setFieldValue } = useFormikContext();


    const classes = useStyles();
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
                        {values?.salaryResultStaffItems?.length > 0 ? (
                            <TableContainer
                                component={Paper}
                                style={{
                                    overflowX: "unset !important",
                                }}>
                                <Table className={`${classes.table}`} aria-label='simple table'>
                                    <TableHead>
                                        <TableRow>
                                            <TableCell align="center">
                                                <span className="px-6">
                                                    {t("Thành phần lương")}
                                                </span>
                                            </TableCell>

                                            <TableCell align="center">
                                                <span className="px-6">
                                                    {t("Tham số")}
                                                </span>
                                            </TableCell>

                                            <TableCell align="center">
                                                <span className="px-6">
                                                    {t("Giá trị")}
                                                </span>
                                            </TableCell>

                                            <TableCell align="center">
                                                <span className="px-6">
                                                    {t("Loại giá trị")}
                                                </span>
                                            </TableCell>

                                        </TableRow>
                                    </TableHead>

                                    <TableBody>
                                        {values?.salaryResultStaffItems?.map((item, index) => {

                                            const referenceTemplateItem = values?.salaryTemplate?.templateItems[index];

                                            let tooltipContent = LocalConstants.SalaryItemCalculationType.getListData().find(i => i.value == referenceTemplateItem.calculationType)?.name;
                                            if (tooltipContent == "Dùng công thức") {
                                                tooltipContent = item?.salaryTemplateItem?.formula;
                                            }

                                            return (
                                                <TableRow key={index}>
                                                    <TableCell style={{
                                                        textAlign: "left",
                                                    }}>
                                                        <span className="px-6">
                                                            {item?.referenceName
                                                                ? item?.referenceName
                                                                : null}
                                                        </span>
                                                    </TableCell>

                                                    <TableCell>
                                                        <span className="px-6">
                                                            {item?.referenceCode
                                                                ? item?.referenceCode
                                                                : null}
                                                        </span>
                                                    </TableCell>

                                                    <Tooltip
                                                        arrow
                                                        placement="top"
                                                        title={tooltipContent}
                                                    >
                                                        <TableCell>

                                                            <GlobitsVNDCurrencyInput
                                                                name={`salaryResultStaffItems.${index}.value`}
                                                                readOnly={referenceTemplateItem.calculationType != LocalConstants.SalaryItemCalculationType.USER_FILL.value}
                                                            // disabled={referenceTemplateItem.calculationType != LocalConstants.SalaryItemCalculationType.USER_FILL.value}
                                                            />

                                                        </TableCell>
                                                    </Tooltip>

                                                    <TableCell>
                                                        <span className="px-6">
                                                            {LocalConstants.SalaryItemCalculationType.getListData().find(i => i.value == referenceTemplateItem.calculationType)?.name}
                                                        </span>
                                                    </TableCell>


                                                </TableRow>
                                            );
                                        })}
                                    </TableBody>
                                </Table>
                            </TableContainer>
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


export default memo(observer(SalaryRecalPayslipUserFillSection));