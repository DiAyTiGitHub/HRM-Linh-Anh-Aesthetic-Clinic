import React, { memo, useEffect, useState } from "react";
import { Button, DialogActions, DialogContent, Grid, makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from "@material-ui/core";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { Form, Formik, useFormikContext } from "formik";
import { useStore } from "app/stores";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { observer } from "mobx-react";
import { useParams } from "react-router-dom";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import moment from "moment";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingStaff } from "../HumanResourcesInformation/StaffService";
import { LIST_YEAR } from "app/LocalConstants";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import { FormControlLabel, Switch } from "@material-ui/core";




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
    input: {
        padding: "0px",
        "& .MuiFormControl-root": {
            padding: "0px !important",
        }
    }
});



function StaffAnnualLeaveHistoryStatistics(props) {
    const {
        readOnly
    } = props;

    const { staffAnnualLeaveHistoryStore } = useStore();
    const { t } = useTranslation();
    const { id } = useParams();

    const classes = useStyles();

    const { values } = useFormikContext();

    const [showMonthlyDetail, setShowMonthlyDetail] = useState(true);
    // console.log("values", values);

    return (

        <Grid container spacing={2}>
            <Grid item xs={12} >
                <p className='m-0 p-0 borderThrough2'>Thống kê</p>
            </Grid>

            <Grid item xs={12}>
                <TableContainer
                    component={Paper}
                    style={{
                        overflowX: "unset !important",
                    }}>
                    <Table className={`${classes.table}`} aria-label='simple table'>
                        <TableHead>
                            <TableRow>
                                <TableCell align='center' className='py-4' style={{ width: "40%" }}>
                                    Thống kê
                                </TableCell>

                                <TableCell align='center' className='py-4' style={{ width: "20%" }}>
                                    Giá trị
                                </TableCell>

                                <TableCell align='center' className='py-4' style={{ width: "40%" }}>
                                    Ghi chú
                                </TableCell>

                            </TableRow>
                        </TableHead>

                        <TableBody>
                            <TableRow className={classes.tableBody}>
                                <TableCell align='center'>SN nghỉ phép quy định</TableCell>

                                <TableCell align='center' className={classes.input}>
                                    <GlobitsVNDCurrencyInput
                                        readOnly={readOnly}
                                        name='grantedLeaveDays'
                                    />
                                </TableCell>

                                <TableCell align='center' className={classes.input}>
                                    <GlobitsTextField
                                        readOnly={readOnly}
                                        name='grantedLeaveDaysNote'
                                    />
                                </TableCell>
                            </TableRow>

                            <TableRow className={classes.tableBody}>
                                <TableCell align='center'>SN nghỉ phép tồn năm trước</TableCell>

                                <TableCell align='center' className={classes.input}>
                                    <GlobitsVNDCurrencyInput
                                        readOnly={readOnly}
                                        name='carriedOverLeaveDays'
                                    />
                                </TableCell>

                                <TableCell align='center' className={classes.input}>
                                    <GlobitsTextField
                                        readOnly={readOnly}
                                        name='carriedOverLeaveDaysNote'
                                    />
                                </TableCell>
                            </TableRow>

                            <TableRow className={classes.tableBody}>
                                <TableCell align='center'>SN nghỉ phép tăng theo thâm niên</TableCell>

                                <TableCell align='center' className={classes.input}>
                                    <GlobitsVNDCurrencyInput
                                        readOnly={readOnly}
                                        name='seniorityLeaveDays'
                                    />
                                </TableCell>

                                <TableCell align='center' className={classes.input}>
                                    <GlobitsTextField
                                        readOnly={readOnly}
                                        name='seniorityLeaveDaysNote'
                                    />
                                </TableCell>
                            </TableRow>

                            <TableRow className={classes.tableBody}>
                                <TableCell align='center'>SN nghỉ phép được thưởng</TableCell>

                                <TableCell align='center' className={classes.input}>
                                    <GlobitsVNDCurrencyInput
                                        readOnly={readOnly}
                                        name='bonusLeaveDays'
                                    />
                                </TableCell>

                                <TableCell align='center' className={classes.input}>
                                    <GlobitsTextField
                                        readOnly={readOnly}
                                        name='bonusLeaveDaysNote'
                                    />
                                </TableCell>
                            </TableRow>

                            <TableRow className={classes.tableBody}>
                                <TableCell align='center'>SN nghỉ phép bị hủy</TableCell>

                                <TableCell align='center' className={classes.input}>
                                    <GlobitsVNDCurrencyInput
                                        readOnly={readOnly}
                                        name='cancelledLeaveDays'
                                    />
                                </TableCell>

                                <TableCell align='center' className={classes.input}>
                                    <GlobitsTextField
                                        readOnly={readOnly}
                                        name='cancelledLeaveDaysNote'
                                    />
                                </TableCell>
                            </TableRow>

                        </TableBody>
                    </Table>
                </TableContainer>
            </Grid>

            <Grid item xs={12}>
                <FormControlLabel
                    control={
                        <Switch
                            checked={showMonthlyDetail}
                            onChange={() => setShowMonthlyDetail(prev => !prev)}
                            // color="primary"
                            style={{ color: "#2a80c8" }}
                        />
                    }
                    label="Chi tiết theo tháng"
                />
            </Grid>


            {showMonthlyDetail && (
                <Grid item xs={12}>
                    <TableContainer
                        component={Paper}
                        style={{
                            overflowX: "unset !important",
                        }}
                    >
                        <Table className={`${classes.table}`} aria-label='simple table'>


                            <TableHead>
                                <TableRow className={classes.tableBody} >
                                    <TableCell align="center">Tháng</TableCell>
                                    {[...Array(12)].map((_, idx) => (
                                        <TableCell align="center" key={idx}>
                                            {idx + 1}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            </TableHead>

                            <TableBody>
                                <TableRow className={classes.tableBody}>
                                    <TableCell align="center">Số ngày NP</TableCell>
                                    {[...Array(12)].map((_, idx) => {
                                        const month = idx + 1;
                                        let matched = values?.monthlyLeaveHistories?.find(m => m.month === month);
                                        // if(matched == null){
                                        //     matched = new 
                                        // }
                                        return (
                                            <TableCell align="center" key={idx} className={classes.input}>
                                                {/* {matched?.leaveDays ?? 0} */}

                                                <GlobitsVNDCurrencyInput
                                                    name={`monthlyLeaveHistories[${idx}].leaveDays`}
                                                    readOnly={readOnly}
                                                />

                                            </TableCell>
                                        );
                                    })}
                                </TableRow>

                            </TableBody>
                        </Table>
                    </TableContainer>
                </Grid>
            )}


        </Grid>
    )
}

export default memo(observer(StaffAnnualLeaveHistoryStatistics));