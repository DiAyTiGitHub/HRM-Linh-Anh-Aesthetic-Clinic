import { observer } from "mobx-react";
import React, { memo } from "react";
import { useStore } from "app/stores";
import { useHistory } from "react-router-dom";
import TimekeepingReportBoardCell from "./TimekeepingReportBoardCell";
import { formatNumber } from "app/LocalFunction";

function TimekeepingReportBoardBody(props) {
    const {

    } = props;

    const {
        timekeepingReportStore
    } = useStore();

    const {
        listTimekeepingReport,
    } = timekeepingReportStore;

    const history = useHistory();



    const styles = {
        cell: {
            whiteSpace: 'nowrap',
            textAlign: "center",
            verticalAlign: "middle"
        },
        container: {
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            textAlign: 'center',
        },
        shiftWork: {
            padding: '1rem',
            border: '1px solid #ddd', // Border similar to default style
            borderRadius: '8px',
            boxShadow: '0px 4px 6px rgba(0, 0, 0, 0.1)',
        },
        totalAssignHours: {
            fontSize: '1rem',
            fontWeight: 'bold',
            color: '#4A4A4A', // Gray color
        },
    };

    return (

        <tbody>
            {/* {console.log("listTimekeepingReport", listTimekeepingReport)} */}
            {
                listTimekeepingReport?.map(function (staff, index) {
                    const {
                        displayOrder, // Thứ tự hiển thị = Cột STT trong bảng báo cáo chấm công

                        staffId, // Mã NV
                        displayName, // Họ tên NV
                        staffCode, // Mã nhân viên

                        currentPosition, // Vị trí chính
                        currentPositionTitle, // Chức danh chính
                        currentDepartment, // Phòng ban chính
                        currentOrganization, // Đơn vị chính
                        codeCurrentPosition, // Vị trí chính
                        codeCurrentPositionTitle, // Chức danh chính
                        codeCurrentDepartment, // Phòng ban chính
                        codeCurrentOrganization, // Đơn vị chính

                        workingSchedules, // list workingSchedule các ngày làm việc

                        totalAssignedShifts, // Tổng số ca làm việc được phân
                        totalAssignedHours, // Tổng số giờ làm việc được phân
                        totalWorkedHours, // Tổng số giờ làm việc thực tế

                        totalFullAttendanceShifts, // Tổng số ca làm việc nhân viên hoàn thành/đi làm đủ
                        totalPartialAttendanceShifts, // Tổng số ca đi làm thiếu
                        totalNotAttendenceShifts, //  Số ca làm việc nhân viên nghỉ có phép KHÔNG HƯỞNG LƯƠNG

                        convertedWorkingHours, // tổng Số giờ công quy đổi của nhân viên
                        lateArrivalCount,// tổng Số lần đi làm muộn
                        lateArrivalMinutes, //tổng  Số phút đi muộn
                        earlyExitCount, //tổng Số lần về sớm
                        earlyExitMinutes, // tổng Số phút về sớm
                        earlyArrivalMinutes, //tổng Số phút đến sớm
                        lateExitMinutes, // tổng Số phút về muộn
                        totalPaidWork, // tổng công được tính của nhân viên. VD: 0.5 ngày công, 1 ngày công
                        confirmedOTHoursBeforeShift, //tổng Số giờ làm thêm trước ca làm việc đã được xác nhận
                        confirmedOTHoursAfterShift, //tổng  Số giờ làm thêm sau ca làm việc đã được xác nhận
                        shiftLeaveTypes, // tổng các loại leaveType
                        estimatedWorkingHours, // tổng số giờ làm việc ước
                        paidLeaveHours, // tổng số giờ làm nghỉ có lương
                        unpaidLeaveHours, // tổng số giờ làm nghỉ không lương
                        totalPaidLeaveWorkRatio, // số công nghỉ có lương
                        totalUnpaidLeaveWorkRatio, // số công nghỉ không lương,
                        totalValidHours, // Số giờ làm việc hợp lệ

                    } = staff;

                    return (
                        <tr
                            key={staffId}
                            className={`row-table-body row-table-no_data`}
                        >
                            <td
                                className="stickyColumn displayOrder-column"
                                style={{ textAlign: "center", verticalAlign: "middle" }}
                            >
                                {displayOrder}
                            </td>

                            <td
                                className="stickyColumn staffCode-column"
                                style={{ textAlign: "center", verticalAlign: "middle" }}
                            >
                                {staffCode}
                            </td>

                            <td
                                className="stickyColumn displayName-column no-wrap-text"
                                style={{ textAlign: "center", verticalAlign: "middle" }}
                            >
                                {displayName}
                            </td>

                            <td className="stickyColumn">
                                <div>{currentOrganization}</div>
                            </td>

                            <td className="stickyColumn">
                                <div>{currentDepartment}</div>
                                {codeCurrentDepartment && <div>({codeCurrentDepartment})</div>}
                            </td>

                            <td className="stickyColumn">
                                <div>{currentPositionTitle}</div>
                                {codeCurrentPositionTitle && <div>({codeCurrentPositionTitle})</div>}
                            </td>

                            {/* <td className="stickyColumn">
                                <div>{currentPosition}</div>
                                {codeCurrentPosition && <div>({codeCurrentPosition})</div>}
                            </td> */}


                            {workingSchedules?.map((workingSchedule, index) => {
                                const {
                                    workingDate,
                                    shiftWorks,
                                    totalAssignHours,
                                    totalWorkingHours
                                } = workingSchedule;

                                return (
                                    <td
                                        key={workingDate + "_" + index}
                                        style={styles.cell}
                                    >
                                        <TimekeepingReportBoardCell
                                            workingSchedule={workingSchedule}
                                            staffId={staffId}
                                        />
                                    </td>
                                );
                            })}

                            {/* Start Số giờ */}
                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {formatNumber(totalAssignedHours)}
                            </td>

                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {formatNumber(totalWorkedHours)}
                            </td>

                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {formatNumber(totalValidHours)}
                            </td>

                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {formatNumber(convertedWorkingHours)}
                            </td>
                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {formatNumber(estimatedWorkingHours)}
                            </td>
                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {formatNumber(paidLeaveHours)}
                            </td>
                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {formatNumber(unpaidLeaveHours)}
                            </td>

                            {/* Số giờ làm thêm trước ca */}
                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {formatNumber(confirmedOTHoursBeforeShift)}
                            </td>

                            {/* Số giờ làm thêm sau ca */}
                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {formatNumber(confirmedOTHoursAfterShift)}
                            </td>

                            {/* End Số giờ */}

                            {/* Start Số Ca */}
                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {totalAssignedShifts}
                            </td>

                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {totalFullAttendanceShifts}
                            </td>

                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {totalPartialAttendanceShifts}
                            </td>

                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {totalNotAttendenceShifts}
                            </td>

                            {/* Các trạng thái nghỉ */}
                            {/* {shiftLeaveTypes?.map((leaveType, index) => {
                                const {
                                    nameLeaveType,
                                    codeLeaveType,
                                    totalShifts
                                } = leaveType;

                                return (
                                    <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                        {totalShifts}
                                    </td>
                                );
                            })} */}

                            {/* End Số ca  */}

                            {/* Start Số lần */}
                            {/* Số lần đi làm muộn */}
                            {/* <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {lateArrivalCount}
                            </td> */}

                            {/* Số lần về sớm */}
                            {/* <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {earlyExitCount}
                            </td> */}
                            {/* End Số lần */}

                            {/* Start Số phút */}
                            {/* Số phút đi muộn */}
                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {lateArrivalMinutes}
                            </td>

                            {/* Số phút về sớm */}
                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {earlyExitMinutes}
                            </td>

                            {/* Số phút đến sớm */}
                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {earlyArrivalMinutes}
                            </td>

                            {/* Số phút về muộn */}
                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {lateExitMinutes}
                            </td>
                            {/* End Số phút */}

                            {/* Số công được tính */}
                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {formatNumber(totalPaidWork)}
                            </td>
                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {formatNumber(totalPaidLeaveWorkRatio)}
                            </td>
                            <td className="" style={{ textAlign: "center", verticalAlign: "middle" }}>
                                {formatNumber(totalUnpaidLeaveWorkRatio)}
                            </td>

                        </tr>
                    );
                })
            }

        </tbody>

    );
}

export default memo(observer(TimekeepingReportBoardBody));