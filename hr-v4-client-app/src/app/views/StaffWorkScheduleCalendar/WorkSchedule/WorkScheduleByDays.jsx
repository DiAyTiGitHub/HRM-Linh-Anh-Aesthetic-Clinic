import React, { memo } from "react";
import { observer } from "mobx-react";
import { toast } from "react-toastify";

import { useStore } from "app/stores";
import { formatDate, getShortVietnameseWeekday } from "app/LocalFunction";
import { useTranslation } from "react-i18next";
import WorkCalendarBody from "./WorkCalendarBody";

import "./WorkScheduleCalendarStyles.scss";
import GlobitsPagination from "app/common/GlobitsPagination";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

function WorkScheduleByDays(props) {
    const { t } = useTranslation();
    const {
        renderButtonNextDate,
        staffId,
    } = props;

    const {
        staffWorkScheduleCalendarStore
    } = useStore();

    const {
        listWorkSchedules,
        searchObject,
        getNeedRenderDates,
        totalPages,
        totalElements,
        handleChangePage,
        setPageSize
    } = staffWorkScheduleCalendarStore;


    return (
        <div id="calendar-weeks">
            <p className="text-to-date ">
                {formatDate("DD/MM/YYYY", searchObject?.fromDate)} - {formatDate("DD/MM/YYYY", searchObject?.toDate)}
            </p>

            <section className="commonTableContainer">
                <table className={`commonTable w-100`}>
                    <thead>
                        <tr className="tableHeader">
                            <th rowSpan={2} align="center" style={{ width: "40px" }}
                                className="stickyCell stickyHeader"
                            >
                                Nhân viên
                            </th>

                            {getNeedRenderDates().map(function (date, index) {

                                return (
                                    <React.Fragment key={index}>
                                        <th
                                            className="stickyHeader px-16"
                                            align="center"
                                        >
                                            <h4 className="m-0 pb-4">
                                                {getShortVietnameseWeekday(date)}
                                            </h4>
                                            {formatDate("DD/MM/YYYY", date)}
                                        </th>
                                    </React.Fragment>
                                );
                            })}
                        </tr>
                    </thead>

                    <WorkCalendarBody/>
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

export default memo(observer(WorkScheduleByDays));





