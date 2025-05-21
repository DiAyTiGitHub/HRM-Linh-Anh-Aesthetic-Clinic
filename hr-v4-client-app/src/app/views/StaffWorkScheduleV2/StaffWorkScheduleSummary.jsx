import { Grid, makeStyles, Radio } from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import { formatDate } from "app/LocalFunction";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import CheckIcon from '@material-ui/icons/Check';
import CloseIcon from '@material-ui/icons/Close';
import { useFormikContext } from "formik";

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

function getApprovalStatusName(status) {
    switch (status) {
        case 1:
            return "Chưa duyệt";
        case 2:
            return "Đã duyệt";
        case 3:
            return "Không duyệt";
        default:
            return "Không xác định";
    }
}

function StaffWorkScheduleSummary(props) {
    const { t } = useTranslation();

    const classes = useStyles();


    const { staffWorkScheduleStore } = useStore();

    const {
        getTotalConfirmedOTHours,
        getStaffWorkScheduleWorkingStatusName,
        selectedStaffWorkSchedule
    } = staffWorkScheduleStore;

    function roundToTwo(num) {
        if (!num) return "0.00"; // Handle null, undefined, or 0 cases explicitly

        return (Math.round(num * 100) / 100).toFixed(2);
    }

    const { values, setFieldValue } = useFormikContext();

    return (
        <TableContainer
            component={Paper}
            style={{
                overflowX: "unset !important",
            }}>
            <Table className={`${classes.table}`} aria-label='simple table'>
                <TableHead>
                    <TableRow className={classes.tableBody}>
                        <TableCell
                            colSpan={2}
                            align='center'
                            className='py-4'
                            style={{ width: "40%" }}
                        >

                            <strong>Thông tin chung</strong>
                        </TableCell>
                    </TableRow>
                </TableHead>

                <TableBody>
                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Ngày làm việc</TableCell>
                        <TableCell align='center'>
                            <strong>
                                {formatDate("DD/MM/YYYY", selectedStaffWorkSchedule?.workingDate)}
                            </strong>
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Ca làm việc</TableCell>
                        <TableCell align='center'>{selectedStaffWorkSchedule?.shiftWork?.name}</TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Người phân ca</TableCell>
                        <TableCell
                            align='center'>{selectedStaffWorkSchedule?.coordinator?.displayName || "Không xác định"}</TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Cho phép chấm công vào ra nhiều lần</TableCell>
                        <TableCell align='center'>
                            {!selectedStaffWorkSchedule?.allowOneEntryOnly && (
                                <CheckIcon fontSize="small" style={{ color: "green" }} />
                            )}

                            {selectedStaffWorkSchedule?.allowOneEntryOnly && (
                                <CloseIcon fontSize="small" style={{ color: "red" }} />
                            )}

                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Cần quản lý phê duyệt</TableCell>
                        <TableCell align='center'>
                            {selectedStaffWorkSchedule?.needManagerApproval && (
                                <CheckIcon fontSize="small" style={{ color: "green" }} />
                            )}


                            {!selectedStaffWorkSchedule?.needManagerApproval && (
                                <CloseIcon fontSize="small" style={{ color: "red" }} />
                            )}
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Số giờ làm việc ước tính</TableCell>
                        <TableCell align='center'>
                            <strong>
                                {`${roundToTwo(selectedStaffWorkSchedule?.estimatedWorkingHours) || "0.00"} `}
                            </strong>
                            giờ
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell colSpan={2} align='center'>
                            <strong>Cấu hình hiện tại từ nhân viên</strong>
                        </TableCell>
                    </TableRow>


                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Trong quá trình thai sản</TableCell>
                        <TableCell align='center'>
                            {selectedStaffWorkSchedule?.duringPregnancy && (
                                <CheckIcon fontSize="small" style={{ color: "green" }} />
                            )}


                            {!selectedStaffWorkSchedule?.duringPregnancy && (
                                <CloseIcon fontSize="small" style={{ color: "red" }} />
                            )}
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Không tính đi muộn về sớm</TableCell>
                        <TableCell align='center'>
                            {selectedStaffWorkSchedule?.staff?.skipLateEarlyCount && (
                                <CheckIcon fontSize="small" style={{ color: "green" }} />
                            )}


                            {!selectedStaffWorkSchedule?.staff?.skipLateEarlyCount && (
                                <CloseIcon fontSize="small" style={{ color: "red" }} />
                            )}
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Không tính làm thêm giờ</TableCell>
                        <TableCell align='center'>
                            {selectedStaffWorkSchedule?.staff.skipOvertimeCount && (
                                <CheckIcon fontSize="small" style={{ color: "green" }} />
                            )}


                            {!selectedStaffWorkSchedule?.staff.skipOvertimeCount && (
                                <CloseIcon fontSize="small" style={{ color: "red" }} />
                            )}
                        </TableCell>
                    </TableRow>

                    {
                        selectedStaffWorkSchedule?.needManagerApproval && (
                            <TableRow className={classes.tableBody}>
                                <TableCell align='center'>Trạng thái phê duyệt</TableCell>
                                <TableCell
                                    align='center'>{getApprovalStatusName(selectedStaffWorkSchedule?.approvalStatus)}</TableCell>
                            </TableRow>
                        )
                    }




                    {selectedStaffWorkSchedule?.leaveType && (
                        <TableRow className={classes.tableBody}>
                            <TableCell align='center'>Loại nghỉ</TableCell>
                            <TableCell align='center' className='p-0'>
                                <strong>{selectedStaffWorkSchedule?.leaveType?.name}</strong>
                            </TableCell>
                        </TableRow>
                    )}


                </TableBody>
            </Table>

            {values?.leaveType?.code?.includes("NUA_NGAY") && (
                <TableBody>
                    {values?.shiftWork?.timePeriods.length > 0
                        ? <Grid item xs={12}>

                            <strong style={{ paddingTop: "10px" }}>
                                Giai đoạn nghỉ trong ca
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
                                        {values?.shiftWork?.timePeriods.length > 0
                                            ? values?.shiftWork?.timePeriods?.map(
                                                function (timePeriod, index) {
                                                    const isRadioChecked = values?.leavePeriod?.id === timePeriod?.id;

                                                    return (
                                                        <TableRow key={index} className={classes.tableBody}>
                                                            <TableCell align="center">
                                                                <Radio
                                                                    name="radSelected"
                                                                    value={timePeriod?.id}
                                                                    checked={values?.leavePeriod?.id === timePeriod?.id}
                                                                    // onChange={() => setFieldValue("leavePeriod", timePeriod)}
                                                                    disabled
                                                                />

                                                            </TableCell>

                                                            <TableCell align="center">
                                                                {formatDate("HH:mm", timePeriod?.startTime)}
                                                            </TableCell>

                                                            <TableCell align="center">
                                                                {formatDate("HH:mm", timePeriod?.endTime)}
                                                            </TableCell>
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
                        : "Chưa có dữ liệu !"}
                </TableBody>
            )}

            <Table className={`${classes.table}`} aria-label='simple table'>
                <TableHead>
                    <TableRow>
                        <TableCell align='center' className='py-4' style={{ width: "40%" }}>
                            Thống kê
                        </TableCell>

                        <TableCell align='center' className='py-4' style={{ width: "40%" }}>
                            Kết quả
                        </TableCell>
                    </TableRow>
                </TableHead>

                <TableBody>
                    {!selectedStaffWorkSchedule?.allowOneEntryOnly && (
                        <>
                            <TableRow className={classes.tableBody}>
                                <TableCell colSpan={2} align='center'>
                                    <strong>Số lần</strong>
                                </TableCell>
                            </TableRow>

                            <TableRow className={classes.tableBody}>
                                <TableCell align='center'>Đi làm muộn</TableCell>
                                <TableCell align='center'>
                                    <strong>{`${selectedStaffWorkSchedule?.lateArrivalCount || "0"} `}</strong>
                                    lần
                                </TableCell>
                            </TableRow>

                            <TableRow className={classes.tableBody}>
                                <TableCell align='center'>Về sớm</TableCell>
                                <TableCell align='center'>
                                    <strong>{`${selectedStaffWorkSchedule?.earlyExitCount || "0"} `}</strong>
                                    lần
                                </TableCell>
                            </TableRow>
                        </>
                    )}

                    <TableRow className={classes.tableBody}>
                        <TableCell colSpan={2} align='center'>
                            <strong>Số phút</strong>
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Đi muộn</TableCell>
                        <TableCell align='center'>
                            <strong>{`${selectedStaffWorkSchedule?.lateArrivalMinutes || "0"} `}</strong>
                            phút
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Về sớm</TableCell>
                        <TableCell align='center'>
                            <strong>{`${selectedStaffWorkSchedule?.earlyExitMinutes || "0"} `}</strong>
                            phút
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Đi sớm</TableCell>
                        <TableCell align='center'>
                            <strong>{`${selectedStaffWorkSchedule?.earlyArrivalMinutes || "0"} `}</strong>
                            phút
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Về muộn</TableCell>
                        <TableCell align='center'>
                            <strong>{`${selectedStaffWorkSchedule?.lateExitMinutes || "0"} `}</strong>
                            phút
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell colSpan={2} align='center'>
                            <strong>Số giờ làm thêm được xác nhận</strong>
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Trước ca</TableCell>
                        <TableCell align='center'>
                            <strong>
                                {`${roundToTwo(selectedStaffWorkSchedule?.confirmedOTHoursBeforeShift) || "0.00"} `}
                            </strong>
                            giờ
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Sau ca</TableCell>
                        <TableCell align='center'>
                            <strong>{`${roundToTwo(selectedStaffWorkSchedule?.confirmedOTHoursAfterShift) || "0.00"} `}</strong>
                            giờ
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Người xác nhận OT</TableCell>
                        <TableCell align='center'>
                            <strong>{selectedStaffWorkSchedule?.otEndorser?.displayName}</strong>-
                            {selectedStaffWorkSchedule?.otEndorser?.staffCode}
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell colSpan={2} align='center'>
                            <strong>Tổng kết</strong>
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Đã làm việc</TableCell>
                        <TableCell align='center'>
                            <strong>{`${roundToTwo(selectedStaffWorkSchedule?.totalHours) || "0.00"} `}</strong>
                            giờ
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Làm việc hợp lệ</TableCell>
                        <TableCell align='center'>
                            <strong>{`${roundToTwo(selectedStaffWorkSchedule?.totalValidHours) || "0.00"} `}</strong>
                            giờ
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Nghỉ được tính lương</TableCell>
                        <TableCell align='center'>
                            <strong>{`${roundToTwo(selectedStaffWorkSchedule?.paidLeaveHours) || "0.00"} `}</strong>
                            giờ
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Nghỉ không tính lương</TableCell>
                        <TableCell align='center'>
                            <strong>{`${roundToTwo(selectedStaffWorkSchedule?.unpaidLeaveHours) || "0.00"} `}</strong>
                            giờ
                        </TableCell>
                    </TableRow>


                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Làm thêm được xác nhận</TableCell>
                        <TableCell align='center'>
                            <strong>{`${roundToTwo(getTotalConfirmedOTHours(selectedStaffWorkSchedule)) || "0.00"} `}</strong>
                            giờ
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Công quy đổi</TableCell>
                        <TableCell align='center'>
                            <strong>{`${roundToTwo(selectedStaffWorkSchedule?.convertedWorkingHours) || "0.00"} `}</strong>
                            giờ
                        </TableCell>
                    </TableRow>


                    <TableRow className={classes.tableBody}>
                        <TableCell colSpan={2} align='center'>
                            <strong>Tỷ lệ ngày công</strong>
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Được tính</TableCell>
                        <TableCell align='center'>
                            <strong>{`${roundToTwo(selectedStaffWorkSchedule?.totalPaidWork) || "0"} `}</strong>
                            ngày công
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Nghỉ phép hưởng lương</TableCell>
                        <TableCell align='center'>
                            <strong>{`${roundToTwo(selectedStaffWorkSchedule?.paidLeaveWorkRatio) || "0"} `}</strong>
                            ngày công
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Nghỉ phép không lương</TableCell>
                        <TableCell align='center'>
                            <strong>{`${roundToTwo(selectedStaffWorkSchedule?.unpaidLeaveWorkRatio) || "0"} `}</strong>
                            ngày công
                        </TableCell>
                    </TableRow>


                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Trạng thái làm việc</TableCell>
                        <TableCell align='center' className='p-0'>
                            <strong>{getStaffWorkScheduleWorkingStatusName(selectedStaffWorkSchedule)}</strong>
                        </TableCell>
                    </TableRow>

                </TableBody>
            </Table>
        </TableContainer>
    );
}

export default memo(observer(StaffWorkScheduleSummary));
