import React, { memo, useEffect } from "react";
import { observer } from "mobx-react";
import { Grid, Paper, TableHead, TableCell, TableRow, TableContainer, TableBody, Table, Radio, makeStyles } from "@material-ui/core";
import { formatDate } from "app/LocalFunction";
import { useFormikContext } from "formik";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";

const useStyles = makeStyles({
    root: {
        "& .MuiDialogContent-root": {
            overflow: "auto !important",
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
        border: "1.2px solid #4276a4 !important",
        borderCollapse: "collapse",

        "& .MuiTableCell-root": {
            border: "none",
        },

        "& .MuiTableRow-head": {
            backgroundColor: "#4276a4",
            border: "1px solid #4276a4",
        },

        "& .MuiTableCell-head": {
            border: "1px solid #4276a4",
            color: "#fff",
        },

        "& .MuiTableCell-body": {
            border: "1px solid #4276a4",
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

function TimesheetCheckFormChoosePeriodSection(props) {
    const classes = useStyles();
    const { t } = useTranslation();

    const {
        timeSheetDetailStore
    } = useStore();

    const {
        currentTimekeeping
    } = timeSheetDetailStore;

    const {
        values,
        setFieldValue,

    } = useFormikContext();

    function handleChooseTimePeriod(timePeriod) {
        setFieldValue("shiftWorkTimePeriod", timePeriod);
    }

    useEffect(function () {
        if (currentTimekeeping?.staff?.id != values?.staff?.id) {
            setFieldValue("staffWorkSchedule", null);
            setFieldValue("shiftWorkTimePeriod", null);
        }

    }, [values?.staff?.id]);

    return (
        <>
            {values?.staffWorkSchedule?.shiftWork?.timePeriods?.length > 0 && (
                <Grid item xs={12}>

                    <strong>
                        Giai đoạn làm việc trong ca
                    </strong>

                    <TableContainer component={Paper}>
                        <Table
                            className={`${classes.table} mb-12`}
                            aria-label="simple table"
                        >
                            <TableHead>
                                <TableRow>
                                    <TableCell
                                        align="center"
                                        className="py-4"
                                        style={{ width: "10%" }}
                                    >
                                        Chọn
                                    </TableCell>

                                    <TableCell
                                        align="center"
                                        className="py-4"
                                        style={{ width: "15%" }}
                                    >
                                        {t("timeKeeping.startTime")}
                                    </TableCell>

                                    <TableCell
                                        align="center"
                                        className="py-4"
                                        style={{ width: "15%" }}
                                    >
                                        {t("timeKeeping.endTime")}
                                    </TableCell>

                                    {/* <TableCell
                                        align="center"
                                        className="py-4"
                                        style={{ width: "20%" }}
                                    >
                                        Được phép đi muộn
                                    </TableCell> */}

                                    <TableCell
                                        align="center"
                                        className="py-4"
                                        style={{ width: "20%" }}
                                    >
                                        Tỷ lệ
                                    </TableCell>
                                </TableRow>
                            </TableHead>

                            <TableBody>
                                {values?.staffWorkSchedule?.shiftWork?.timePeriods.length > 0
                                    ? values?.staffWorkSchedule?.shiftWork?.timePeriods?.map(
                                        function (timePeriod, index) {
                                            const isRadioChecked = values?.shiftWorkTimePeriod?.id === timePeriod?.id;

                                            return (
                                                <TableRow key={index} className={classes.tableBody}>
                                                    <TableCell align="center">
                                                        <Radio
                                                            name="radSelected"
                                                            value={values?.shiftWorkTimePeriod?.id}
                                                            checked={values?.shiftWorkTimePeriod?.id === timePeriod?.id}
                                                            onClick={(event) => handleChooseTimePeriod(timePeriod)}
                                                        />
                                                    </TableCell>

                                                    <TableCell align="center">
                                                        {formatDate("HH:mm", timePeriod?.startTime)}
                                                    </TableCell>

                                                    <TableCell align="center">
                                                        {formatDate("HH:mm", timePeriod?.endTime)}
                                                    </TableCell>

                                                    {/* <TableCell align="center">
                                                        {`${timePeriod?.allowedLateMinutes || 0} phút`}
                                                    </TableCell> */}

                                                    <TableCell align="center">
                                                        {`${(timePeriod?.workRatio || 0) * 100}% ngày công`}
                                                    </TableCell>


                                                </TableRow>
                                            );
                                        }
                                    )
                                    : "Chưa có dữ liệu !"}
                            </TableBody>


                        </Table>
                    </TableContainer>
                </Grid>
            )}
        </>
    );
}

export default memo(observer(TimesheetCheckFormChoosePeriodSection));