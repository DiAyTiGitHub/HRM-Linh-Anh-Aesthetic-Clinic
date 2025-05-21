import React, { memo, useEffect } from "react";
import { Button, ButtonGroup, Grid, Icon, IconButton, makeStyles, Tooltip } from "@material-ui/core";
import { ErrorMessage, FieldArray, useFormikContext } from "formik";
import { useTranslation } from "react-i18next";
import AddIcon from "@material-ui/icons/Add";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { ShiftWorkTimePeriod } from "app/common/Model/Timekeeping/ShiftWorkTimePeriod";

const useStyles = makeStyles((readOnly) => ({
    root: {
        background: "#E4f5fc", padding: "10px 15px", borderRadius: "5px",
    }, groupContainer: {
        width: "100%", "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    }, tableContainer: {
        marginTop: "2px", overflowX: "auto", overflowY: "hidden", "& table": {
            border: "1px solid #ccc", borderCollapse: "collapse", "& td": {
                border: "1px solid #ccc",
            },
        },
    },
    tableHeader: (readOnly) => ({
        width: "100%",
        borderBottom: "1px solid #ccc",
        marginBottom: "8px",
        "& th": {
            width: readOnly ? "calc(100vw / 6)" : "calc(100vw / 7)",
            border: "1px solid #ccc",
            padding: "8px 0 8px 4px",
        },
    }),
}));

const DetailTimePeriod = (props) => {
    const { readOnly = false } = props;

    const { t } = useTranslation();
    const classes = useStyles(readOnly);
    const { values, setFieldValue, errors, touched } = useFormikContext();
    
    const calculateTotalHours = (timePeriods = []) => {
        let total = 0;
    
        for (let i = 0; i < timePeriods.length; i++) {
            const period = timePeriods[i];
    
            if (period.startTime && period.endTime) {
                const startDate = new Date(period.startTime);
                const endDate = new Date(period.endTime);
    
                const startMinutes = startDate.getHours() * 60 + startDate.getMinutes();
                const endMinutes = endDate.getHours() * 60 + endDate.getMinutes();
    
                const diffMinutes = endMinutes - startMinutes;
                const diffHours = diffMinutes / 60;
    
                if (!isNaN(diffHours) && diffHours > 0) {
                    total += diffHours;
                }
            }
        }
    
        return Math.round(total * 100) / 100;
    };
    

    useEffect(() => {
        const total = calculateTotalHours(values.timePeriods);

        setFieldValue("totalHours", total);
        setFieldValue("convertedWorkingHours", total);

    }, [JSON.stringify(values.timePeriods)]);

    return (<FieldArray name="timePeriods">
        {({ push, remove }) => (<div>
            <Grid item xs={12}>
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <ButtonGroup color='container' aria-label='outlined primary button group'>
                            <Button
                                startIcon={<AddIcon />}
                                type="button"
                                onClick={() => push(new ShiftWorkTimePeriod())}
                                disabled={readOnly}
                            >
                                {t("shiftWork.timePeriods.buttonAdd")}
                            </Button>
                        </ButtonGroup>
                    </Grid>
                    {touched?.timePeriods && errors?.timePeriods && (
                        <Grid item xs={12}>
                            <ErrorMessage name="timePeriods" className="text-danger" component="span" />
                        </Grid>
                    )}
                    <Grid item xs={12}>
                        <section className={classes.tableContainer}>
                            <table className={classes.table}>
                                <thead>
                                    <tr className={classes.tableHeader}>
                                        {!readOnly && <th>{t("general.action")}</th>}
                                        <th>{t("shiftWork.timePeriods.code")}</th>
                                        <th>{t("shiftWork.timePeriods.startTime")}</th>
                                        <th>{t("shiftWork.timePeriods.endTime")}</th>
                                        {/* <th>{t("shiftWork.timePeriods.allowedLateMinutes")}</th> */}
                                        <th>{t("shiftWork.timePeriods.workRatio")}</th>
                                        <th>{t("shiftWork.timePeriods.minWorkTimeHour")}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {values?.timePeriods?.length > 0 ? (values.timePeriods.map((_, index) => (
                                        <tr key={index}>
                                            {!readOnly &&
                                                <td style={{ textAlign: "center" }}>
                                                    <Tooltip title="Xóa giai đoạn" placement="top" arrow>
                                                        <IconButton size="small" onClick={() => remove(index)}>
                                                            <Icon fontSize="small" color="secondary">delete</Icon>
                                                        </IconButton>
                                                    </Tooltip>
                                                </td>}
                                            <td>
                                                <GlobitsTextField
                                                    name={`timePeriods[${index}].code`}
                                                    readOnly={readOnly}
                                                />
                                            </td>
                                            <td>
                                                <GlobitsDateTimePicker
                                                    name={`timePeriods[${index}].startTime`}
                                                    format="HH:mm"
                                                    isTimePicker
                                                    readOnly={readOnly}
                                                />
                                            </td>
                                            <td>
                                                <GlobitsDateTimePicker
                                                    name={`timePeriods[${index}].endTime`}
                                                    format="HH:mm"
                                                    isTimePicker
                                                    readOnly={readOnly}
                                                />
                                            </td>
                                            {/* <td>
                                                <GlobitsVNDCurrencyInput
                                                    name={`timePeriods[${index}].allowedLateMinutes`}
                                                    readOnly={readOnly}
                                                />
                                            </td> */}
                                            <td>
                                                <GlobitsVNDCurrencyInput
                                                    name={`timePeriods[${index}].workRatio`}
                                                    readOnly={readOnly}
                                                />
                                            </td>
                                            <td>
                                                <GlobitsVNDCurrencyInput
                                                    name={`timePeriods[${index}].minWorkTimeHour`}
                                                    readOnly={readOnly}
                                                />
                                            </td>
                                        </tr>))) : (
                                        <tr>
                                            <td colSpan={7} align='center' className="py-8">
                                                Chưa có giai đoạn nào
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </section>
                    </Grid>
                </Grid>
            </Grid>
        </div>)}
    </FieldArray>);
};

export default memo(DetailTimePeriod);
