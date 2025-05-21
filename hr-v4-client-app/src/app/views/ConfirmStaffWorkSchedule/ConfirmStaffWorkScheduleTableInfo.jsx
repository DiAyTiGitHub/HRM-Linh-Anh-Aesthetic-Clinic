import React , { useEffect , memo } from "react";
import { useFormikContext } from "formik";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { formatNumber , getCheckInAndCheckOutTimeOfShiftWork } from "app/LocalFunction";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import {
    Grid ,
    Paper ,
    TableHead ,
    TableCell ,
    TableRow ,
    TableContainer ,
    TableBody ,
    Table ,
    makeStyles
} from "@material-ui/core";
import { formatDate } from "app/LocalFunction";
import LocalConstants from "app/LocalConstants";

const useStyles = makeStyles({
    root:{
        "& .MuiDialogContent-root":{
            overflow:"auto !important" ,
        } ,
    } ,
    marginAuto:{
        display:"flex" ,
        "& label":{
            margin:"auto" ,
            marginRight:"10px" ,
            fontWeight:"500" ,
            fontSize:"16px" ,
        } ,
    } ,
    table:{
        border:"1.2px solid #4276a4 !important" ,
        borderCollapse:"collapse" ,

        "& .MuiTableCell-root":{
            border:"none" ,
        } ,

        "& .MuiTableRow-head":{
            backgroundColor:"#4276a4" ,
            border:"1px solid #4276a4" ,
        } ,

        "& .MuiTableCell-head":{
            border:"1px solid #4276a4" ,
            color:"#fff" ,
        } ,

        "& .MuiTableCell-body":{
            border:"1px solid #4276a4" ,
        } ,

        "& .MuiFormGroup-root":{
            display:"flex" ,
            justifyContent:"center" ,
            alignItems:"center" ,
        } ,
    } ,
    tableBody:{
        "& .MuiCheckbox-root":{
            margin:"auto" ,
        } ,
        "& .MuiTextField-root":{
            padding:"5px" ,
        } ,
    } ,
    headerDate:{
        fontSize:"22px" ,
        fontWeight:"700" ,
    } ,
    displayFlex:{
        display:"flex" ,
        justifyContent:"center" ,
        alignItems:"center" ,
    } ,
});

function ConfirmStaffWorkScheduleTableInfo(props) {
    const {t} = useTranslation();
    const classes = useStyles();

    const {readOnly} = props;

    const {
        values ,
        setFieldValue
    } = useFormikContext();

    useEffect(function () {
        let convertedHours = 0;
        if (values?.staffWorkSchedule?.earlyArrivalMinutes > 0) {
            convertedHours = formatNumber(values?.staffWorkSchedule?.earlyArrivalMinutes / 60);
        }

        setFieldValue("staffWorkSchedule.earlyArrivalHours" , convertedHours);
    } , [values?.staffWorkSchedule?.earlyArrivalMinutes]);

    useEffect(function () {
        let convertedHours = 0;
        if (values?.staffWorkSchedule?.lateExitMinutes > 0) {
            convertedHours = formatNumber(values?.staffWorkSchedule?.lateExitMinutes / 60);
        }

        setFieldValue("staffWorkSchedule.lateExitHours" , convertedHours);
    } , [values?.staffWorkSchedule?.lateExitMinutes]);


    const checkInOutObject = getCheckInAndCheckOutTimeOfShiftWork(values?.staffWorkSchedule?.shiftWork);

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>

                {/* <strong>
    Giai đoạn làm việc trong ca
</strong> */}

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
                                    style={{width:"10%"}}
                                >

                                </TableCell>

                                <TableCell
                                    align="center"
                                    className="py-4"
                                    style={{width:"15%"}}
                                >
                                    Giờ vào quy định
                                </TableCell>

                                <TableCell
                                    align="center"
                                    className="py-4"
                                    style={{width:"15%"}}
                                >
                                    Thời gian chấm công
                                </TableCell>

                                <TableCell
                                    align="center"
                                    className="py-4"
                                    style={{width:"15%"}}
                                >
                                    Số phút sớm/muộn
                                </TableCell>

                                <TableCell
                                    align="center"
                                    className="py-4"
                                    style={{width:"15%"}}
                                >
                                    Số giờ quy đổi
                                </TableCell>

                                <TableCell
                                    align="center"
                                    className="py-4"
                                    style={{width:"18%"}}
                                >
                                    Số giờ yêu cầu tính làm thêm
                                </TableCell>

                                {values?.approvalStatus == LocalConstants.OvertimeRequestApprovalStatus.APPROVED.value && (
                                    <TableCell
                                        align="center"
                                        className="py-4"
                                        style={{width:"18%"}}
                                    >
                                        Số giờ được phê duyệt
                                    </TableCell>
                                )}

                            </TableRow>
                        </TableHead>

                        <TableBody>
                            <TableRow className={classes.tableBody}>
                                <TableCell align="center" className="p-0">
                                    Đi sớm
                                </TableCell>

                                <TableCell align="center" className="p-0">
                                    {formatDate("HH:mm" , checkInOutObject?.checkInTime)}
                                </TableCell>

                                <TableCell align="center" className="p-0">
                                    {formatDate("HH:mm" , values?.staffWorkSchedule?.firstCheckIn)}
                                </TableCell>

                                <TableCell align="center" className="p-0">
                                    {`${formatNumber(values?.staffWorkSchedule?.earlyArrivalMinutes)} phút`}
                                </TableCell>

                                <TableCell align="center" className="p-0">
                                    {`${formatNumber(values?.staffWorkSchedule?.earlyArrivalHours)} giờ`}
                                </TableCell>

                                <TableCell align="center" className="p-0">
                                    <GlobitsVNDCurrencyInput
                                        // label={"Xác nhận số làm thêm trước ca"}
                                        name='requestOTHoursBeforeShift'
                                        disabled={values?.approvalStatus == LocalConstants.OvertimeRequestApprovalStatus.APPROVED.value}
                                        readOnly={readOnly}
                                    />
                                </TableCell>

                                {values?.approvalStatus == LocalConstants.OvertimeRequestApprovalStatus.APPROVED.value && (
                                    <TableCell align="center" className="p-0">
                                        <GlobitsVNDCurrencyInput
                                            // label={"Xác nhận số làm thêm trước ca"}
                                            name='staffWorkSchedule.confirmedOTHoursBeforeShift'
                                            disabled
                                            readOnly={readOnly}
                                        />
                                    </TableCell>
                                )}
                            </TableRow>


                            <TableRow className={classes.tableBody}>
                                <TableCell align="center" className="p-0">
                                    Về muộn
                                </TableCell>

                                <TableCell align="center" className="p-0">
                                    {formatDate("HH:mm" , checkInOutObject?.checkOutTime)}
                                </TableCell>

                                <TableCell align="center" className="p-0">
                                    {formatDate("HH:mm" , values?.staffWorkSchedule?.lastCheckout)}
                                </TableCell>

                                <TableCell align="center" className="p-0">
                                    {`${formatNumber(values?.staffWorkSchedule?.lateExitMinutes)} phút`}
                                </TableCell>

                                <TableCell align="center" className="p-0">
                                    {`${formatNumber(values?.staffWorkSchedule?.lateExitHours)} giờ`}
                                </TableCell>

                                <TableCell align="center" className="p-0">
                                    <GlobitsVNDCurrencyInput
                                        // label={"Xác nhận số làm thêm trước ca"}
                                        name='requestOTHoursAfterShift'
                                        disabled={values?.approvalStatus == LocalConstants.OvertimeRequestApprovalStatus.APPROVED.value}
                                        readOnly={readOnly}
                                    />
                                </TableCell>

                                {values?.approvalStatus == LocalConstants.OvertimeRequestApprovalStatus.APPROVED.value && (
                                    <TableCell align="center" className="p-0">
                                        <GlobitsVNDCurrencyInput
                                            // label={"Xác nhận số làm thêm trước ca"}
                                            name='staffWorkSchedule.confirmedOTHoursAfterShift'
                                            disabled
                                            readOnly={readOnly}
                                        />
                                    </TableCell>
                                )}
                            </TableRow>
                        </TableBody>
                    </Table>
                </TableContainer>
            </Grid>
        </Grid>

    );
}

export default memo(observer(ConfirmStaffWorkScheduleTableInfo));