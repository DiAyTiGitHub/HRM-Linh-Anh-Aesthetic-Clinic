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
import React, { memo } from "react";
import { useTranslation } from "react-i18next";
import CheckIcon from '@material-ui/icons/Check';
import CloseIcon from '@material-ui/icons/Close';
import { StaffWorkScheduleWorkingStatus } from "app/LocalConstants";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import { useFormikContext } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { pagingLeaveType } from "app/views/LeaveType/LeaveTypeService";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsVNDCurrencyInput from "../../common/form/GlobitsVNDCurrencyInput";
import GlobitsNumberInput from "../../common/form/GlobitsNumberInput";

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

function EditStaffWorkScheduleSummary() {
    const { t } = useTranslation();

    const classes = useStyles();

    const { staffWorkScheduleStore } = useStore();

    const { getTotalConfirmedOTHours, selectedStaffWorkSchedule } = staffWorkScheduleStore;

    function roundToTwo(num) {
        if (!num) return 0.00; // Handle null, undefined, or 0 cases explicitly

        return (Math.round(num * 100) / 100).toFixed(2);
    }

    const handleChooseTimePeriod = (selectedPeriod) => {
        setFieldValue("leavePeriod", selectedPeriod); // NOT leavePeriods

    };

    const { values, setFieldValue } = useFormikContext()
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


                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Loại nghỉ</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsPagingAutocompleteV2
                                name='leaveType'
                                // label={t("staffWorkSchedule.leaveType")}
                                api={pagingLeaveType}
                                onChange={(_, value) => {
                                    setFieldValue("leaveType", value);
                                }}
                            />
                        </TableCell>
                    </TableRow>

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
                                                                    onChange={() => setFieldValue("leavePeriod", timePeriod)}
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
                                <TableCell align='center'>Đi làm muộn(lần)</TableCell>
                                <TableCell align='center' className={classes.input}>
                                    <GlobitsVNDCurrencyInput
                                        name='lateArrivalCount'
                                        value={selectedStaffWorkSchedule?.lateArrivalCount || 0}
                                    />
                                </TableCell>
                            </TableRow>

                            <TableRow className={classes.tableBody}>
                                <TableCell align='center'>Về sớm(lần)</TableCell>
                                <TableCell align='center' className={classes.input}>
                                    <GlobitsVNDCurrencyInput
                                        name='earlyExitCount'
                                        value={selectedStaffWorkSchedule?.earlyExitCount || 0}
                                    />
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
                        <TableCell align='center'>Đi muộn(phút)</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsNumberInput
                                name='lateArrivalMinutes'
                                value={selectedStaffWorkSchedule?.lateArrivalMinutes || 0.00}
                            />
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Về sớm(phút)</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsNumberInput
                                name='earlyExitMinutes'
                                value={selectedStaffWorkSchedule?.earlyExitMinutes || 0}
                            />
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Đi sớm(phút)</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsNumberInput
                                name='earlyArrivalMinutes'
                                value={selectedStaffWorkSchedule?.earlyArrivalMinutes || 0}
                            />
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Về muộn(phút)</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsNumberInput
                                name='lateExitMinutes'
                                value={selectedStaffWorkSchedule?.lateExitMinutes || 0}
                            />
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell colSpan={2} align='center'>
                            <strong>Số giờ làm thêm được xác nhận</strong>
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Trước ca(giờ)</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsVNDCurrencyInput
                                name='confirmedOTHoursBeforeShift'
                                value={roundToTwo(selectedStaffWorkSchedule?.confirmedOTHoursBeforeShift) || 0}
                                onChange={(event) => {
                                    const value = event.target.value;
                                    setFieldValue("confirmedOTHoursBeforeShift", value);
                                    setFieldValue(
                                        "totalConfirmedOTHours",
                                        (Number(values.confirmedOTHoursAfterShift) || 0) + (Number(value) || 0)
                                    );
                                }}
                            />
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Sau ca(giờ)</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsVNDCurrencyInput
                                name='confirmedOTHoursAfterShift'
                                value={roundToTwo(selectedStaffWorkSchedule?.confirmedOTHoursAfterShift) || 0}
                                onChange={(event) => {
                                    const value = event.target.value;
                                    setFieldValue("confirmedOTHoursAfterShift", value);
                                    setFieldValue(
                                        "totalConfirmedOTHours",
                                        (Number(values.confirmedOTHoursBeforeShift) || 0) + (Number(value) || 0)
                                    );
                                }}
                            />
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
                        <TableCell align='center'>Đã làm việc(giờ)</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsVNDCurrencyInput
                                name='totalHours'
                                value={roundToTwo(selectedStaffWorkSchedule?.totalHours) || 0}
                            />
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Làm việc hợp lệ(giờ)</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsVNDCurrencyInput
                                name='totalValidHours'
                                value={roundToTwo(selectedStaffWorkSchedule?.totalValidHours) || 0}
                            />
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Nghỉ được tính lương(giờ)</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsVNDCurrencyInput
                                name='paidLeaveHours'
                                value={roundToTwo(selectedStaffWorkSchedule?.paidLeaveHours) || 0}
                            />
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Nghỉ không tính lương(giờ)</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsVNDCurrencyInput
                                name='unpaidLeaveHours'
                                value={roundToTwo(selectedStaffWorkSchedule?.unpaidLeaveHours) || 0}
                            />
                        </TableCell>
                    </TableRow>


                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Làm thêm được xác nhận(giờ)</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsTextField
                                name='totalConfirmedOTHours'
                                value={(values.confirmedOTHoursAfterShift || 0) + (values.confirmedOTHoursBeforeShift || 0)}
                                disabled
                            />
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Công quy đổi(giờ)</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsVNDCurrencyInput
                                name='convertedWorkingHours'
                                value={roundToTwo(selectedStaffWorkSchedule?.convertedWorkingHours) || 0}
                            />
                        </TableCell>
                    </TableRow>


                    <TableRow className={classes.tableBody}>
                        <TableCell colSpan={2} align='center'>
                            <strong>Tỷ lệ ngày công</strong>
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Được tính(ngày công)</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsVNDCurrencyInput
                                name='totalPaidWork'
                                value={roundToTwo(selectedStaffWorkSchedule?.totalPaidWork) || 0}
                            />
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Nghỉ phép hưởng lương(ngày công)</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsVNDCurrencyInput
                                name='paidLeaveWorkRatio'
                                value={roundToTwo(selectedStaffWorkSchedule?.paidLeaveWorkRatio) || 0}
                            />
                        </TableCell>
                    </TableRow>

                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Nghỉ phép không lương(ngày công)</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsVNDCurrencyInput
                                name='unpaidLeaveWorkRatio'
                                value={roundToTwo(selectedStaffWorkSchedule?.unpaidLeaveWorkRatio) || 0}
                            />
                        </TableCell>
                    </TableRow>


                    <TableRow className={classes.tableBody}>
                        <TableCell align='center'>Trạng thái làm việc</TableCell>
                        <TableCell align='center' className={classes.input}>
                            <GlobitsSelectInput
                                name="workingStatus"
                                keyValue="value"
                                displayvalue={"name"}
                                options={StaffWorkScheduleWorkingStatus.getListData()}
                            />
                        </TableCell>
                    </TableRow>
                </TableBody>
            </Table>
        </TableContainer>
    );
}

export default memo(observer(EditStaffWorkScheduleSummary));
