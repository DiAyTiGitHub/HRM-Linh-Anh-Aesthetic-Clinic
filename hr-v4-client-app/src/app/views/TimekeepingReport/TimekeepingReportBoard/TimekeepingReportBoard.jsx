import React, { memo } from "react";
import { observer } from "mobx-react";
import { toast } from "react-toastify";
import { useStore } from "app/stores";
import { formatDate, getShortVietnameseWeekday } from "app/LocalFunction";
import { useTranslation } from "react-i18next";
import TimekeepingReportBoardBody from "./TimekeepingReportBoardBody";
import GlobitsPagination from "app/common/GlobitsPagination";
import "../../StaffWorkScheduleCalendar/WorkSchedule/WorkScheduleCalendarStyles.scss";
import "./TimekeepingReportBoardStyles.scss";
import GlobitsCheckBox from "../../../common/form/GlobitsCheckBox";
import { makeStyles } from "@material-ui/core";

const useStyles = makeStyles((theme) => ({
    widthTh: {
        // "& th":{
        //     minWidth:"50px" ,
        // }
    }
}));

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

function TimekeepingReportBoard(props) {
    const classes = useStyles();
    const { t } = useTranslation();
    const { } = props;

    const {
        timekeepingReportStore
    } = useStore();

    const {
        searchObject,
        getNeedRenderDates,
        totalPages,
        totalElements,
        handleChangePage,
        setPageSize,
        listLeaveType
    } = timekeepingReportStore;

    return (
        <div id="calendar-weeks">
            <p className="text-to-date ">
                {formatDate("DD/MM/YYYY", searchObject?.fromDate)} - {formatDate("DD/MM/YYYY", searchObject?.toDate)}
            </p>
            <section className="commonTableContainer">
                <table className={`commonTable w-100`}>
                    <thead>
                        <tr className={`${classes.widthTh} tableHeader`}>

                            <th
                                rowSpan={2}
                                align="center"
                                className="stickyColumn stickyHeader displayOrder-column"
                            >
                                STT
                            </th>

                            <th
                                rowSpan={2}
                                align="center"
                                className="stickyColumn stickyHeader staffCode-column no-wrap-text"
                            >
                                Mã nhân viên
                            </th>

                            <th
                                rowSpan={2}
                                align="center"
                                className="stickyColumn stickyHeader displayName-column no-wrap-text"
                            >
                                Họ và tên
                            </th>

                            <th
                                rowSpan={2}
                                align="center"
                                className="stickyHeader minWidthColumn"
                            >
                                Đơn vị
                            </th>

                            <th
                                rowSpan={2}
                                align="center"
                                className="stickyHeader minWidthColumn"
                            >
                                Phòng ban
                            </th>

                            <th
                                rowSpan={2}
                                align="center"
                                className="stickyHeader minWidthColumn"
                            >
                                Chức danh
                            </th>

                            {/* <th
                                rowSpan={2}
                                align="center"
                                className="stickyColumn stickyHeader minWidthColumn"
                            >
                                Vị trí
                            </th> */}


                            {getNeedRenderDates().map(function (date, index) {

                                return (
                                    <React.Fragment key={index}>
                                        <th
                                            className="stickyHeader px-4"
                                            align="center"
                                        >
                                            <p className="m-0">
                                                {getShortVietnameseWeekday(date)}
                                            </p>
                                        </th>
                                    </React.Fragment>
                                );
                            })}

                            <th
                                align="center"
                                colSpan={9}
                                className="stickyHeader"
                            >
                                Số giờ
                            </th>

                            <th
                                align="center"
                                //colSpan={4 + listLeaveType.length}
                                colSpan={4}
                                className="stickyHeader"
                            >
                                Số ca làm việc
                            </th>

                            {/* <th
                            align="center"
                            colSpan={2}

                            className="stickyHeader"
                            >
                                Số lần
                            </th> */}

                            <th
                                align="center"
                                colSpan={4}
                                className="stickyHeader"
                            >
                                Số phút
                            </th>

                            <th
                                align="center"
                                colSpan={3}
                                className="stickyHeader"
                            >
                                Số công
                            </th>

                            {/* <th
                                rowSpan={2}
                                align="center"
                                className="stickyHeader"
                            >
                                Số công được tính lương
                            </th> */}

                        </tr>

                        <tr className={`${classes.widthTh} tableHeader`}>
                            {getNeedRenderDates().map(function (date, index) {

                                return (
                                    <React.Fragment key={index}>
                                        <th
                                            className="stickyHeader px-4"
                                            align="center"
                                        >
                                            {/* <div className="rotateVertical"> */}
                                            {formatDate("DD/MM", date)}

                                            {/* </div> */}
                                        </th>
                                    </React.Fragment>
                                );
                            })}

                            {/* Start Số giờ */}
                            <th
                                align="center"
                                className="stickyHeader"
                            >
                                Làm việc được phân
                            </th>

                            <th
                                align="center"
                                className="stickyHeader"
                            >
                                Làm việc thực tế
                            </th>
                            <th
                                align="center"
                                className="stickyHeader"
                            >
                                Làm việc hợp lệ
                            </th>
                            <th
                                align="center"
                                className="stickyHeader"
                            >

                                Công quy đổi
                            </th>
                            <th
                                align="center"
                                className="stickyHeader"
                            >

                                Làm việc ước tính
                            </th>
                            <th
                                align="center"
                                className="stickyHeader"
                            >

                                Nghỉ làm có lương
                            </th>
                            <th
                                align="center"
                                className="stickyHeader"
                            >

                                Nghỉ làm không lương
                            </th>
                            <th
                                align="center"
                                className="stickyHeader"
                            >

                                Làm thêm trước ca
                            </th>
                            <th
                                align="center"
                                className="stickyHeader"
                            >

                                Làm thêm sau ca
                            </th>
                            {/* End Số giờ  */}

                            {/* Start Số ca làm việc */}
                            <th
                                align="center"
                                className="stickyHeader"
                            >
                                Được phân
                            </th>

                            <th
                                align="center"
                                className="stickyHeader"
                            >
                                Đi làm đủ
                            </th>

                            <th
                                align="center"
                                className="stickyHeader"
                            >
                                Đi làm thiếu
                            </th>

                            <th
                                align="center"
                                className="stickyHeader"
                            >
                                Không đi làm
                            </th>

                            {/* Các trạng thái nghỉ */}
                            {/* {Array.isArray(listLeaveType) && listLeaveType.map((leaveType , index) => {
                            const {name , code} = leaveType;
                            return (
                                <th
                                    key={index} // Thêm key để React nhận diện các phần tử trong danh sách
                                    align="center"
                                    className="stickyHeader"
                                >
                                    {name}
                                </th>
                            );
                            })} */}

                            {/* End Số ca làm việc */}

                            {/* Start Số lần */}
                            {/* <th
                            rowSpan={2}
                            align="center"
                            className="stickyHeader"
                            >
                                Đi muộn
                            </th>

                            <th
                                rowSpan={2}
                                align="center"
                                className="stickyHeader"
                            >
                                Về sớm
                            </th> */}
                            {/* End Số lần */}

                            {/* Start Số phút */}
                            <th
                                rowSpan={2}
                                align="center"
                                className="stickyHeader"
                            >
                                Đi muộn
                            </th>

                            <th
                                rowSpan={2}
                                align="center"
                                className="stickyHeader"
                            >
                                Về sớm
                            </th>

                            <th
                                rowSpan={2}
                                align="center"
                                className="stickyHeader"
                            >
                                Đi sớm
                            </th>

                            <th
                                rowSpan={2}
                                align="center"
                                className="stickyHeader"
                            >
                                Về muộn
                            </th>
                            {/* End Số phút */}

                            {/* Start Số công */}
                            <th
                                rowSpan={2}
                                align="center"
                                className="stickyHeader"
                            >
                                Đi làm
                            </th>
                            <th
                                rowSpan={2}
                                align="center"
                                className="stickyHeader"
                            >
                                Nghỉ có lương
                            </th>
                            <th
                                rowSpan={2}
                                align="center"
                                className="stickyHeader"
                            >
                                Nghỉ không lương
                            </th>
                            {/* End Số công */}

                        </tr>

                    </thead>

                    <TimekeepingReportBoardBody />
                </table>

            </section>

            <GlobitsPagination
                totalPages={totalPages}
                handleChangePage={handleChangePage}
                setRowsPerPage={setPageSize}
                pageSize={searchObject.pageSize}
                pageSizeOption={[10, 25, 50, 100]}
                totalElements={totalElements}
                page={searchObject.pageIndex}
            />
        </div>


    );
}

export default memo(observer(TimekeepingReportBoard));
