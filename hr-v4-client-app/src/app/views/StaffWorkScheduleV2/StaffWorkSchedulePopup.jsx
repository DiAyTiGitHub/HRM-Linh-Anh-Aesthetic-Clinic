import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { Icon, IconButton, Grid, Tooltip } from "@material-ui/core";
import GlobitsColorfulThemePopup from "app/common/GlobitsColorfulThemePopup";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import GlobitsTable from "app/common/GlobitsTable";
import LocalConstants from "app/LocalConstants";
import { formatDate, getCheckInAndCheckOutTimeOfShiftWork, getDate, getDateTime } from "app/LocalFunction";
import GlobitsPopupV2 from "../../common/GlobitsPopupV2";
import { useFormikContext } from "formik";

function StaffWorkSchedulePopup({ open, handleClose, staff, fromDate, toDate }) {
    const { staffWorkScheduleStore } = useStore();
    const { t } = useTranslation();
    //const { values } = useFormikContext();

    const {
        listStaffWorkSchedules,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        pagingStaffWorkSchedule,
        setSearchObject,
    } = staffWorkScheduleStore;

    useEffect(() => {
        if (open && toDate && fromDate && staff) {
            const newSearchObject = {
                ...searchObject,
                staff: staff,
                pageIndex: 0,
                fromDate: fromDate || null,
                toDate: toDate || null,
            };
            setSearchObject(newSearchObject);
            pagingStaffWorkSchedule();
        }
    }, [open, staff, fromDate, toDate]);

    const columns = [
        {
            title: "Mã nhân viên",
            field: "staff.staffCode",
            align: "left",
        },
        {
            title: "Nhân viên",
            field: "staff.displayName",
            width: "20%",
            align: "left",
        },

        {
            title: "Ngày làm việc",
            field: "workingDate",
            width: "10%",
            align: "left",
            render: (row) => <span className='px-2'>{getDate(row?.workingDate)}</span>,
        },

        {
            title: "Ca làm việc",
            width: "30%",
            align: "left",
            field: "shiftWork.name",
            // render: row => <span className="px-2">{`${row?.shiftWork?.name} `}</span>

            render: (rowData) => {
                const { shiftWork } = rowData;
                const { name, code, totalHours, timePeriods } = shiftWork;

                const { checkInTime, checkOutTime } = getCheckInAndCheckOutTimeOfShiftWork(shiftWork);
                return (
                    <div className='px-4'>
                        {name && (
                            <p className='m-0 pb-4'>
                                <span>
                                    <strong>{`${name} - ${code}`} </strong>
                                </span>
                            </p>
                        )}

                        {/* {totalHours && <p className='m-0 no-wrap-text'>Quy định: {formatVNDMoney(totalHours)} giờ</p>} */}

                        {checkInTime && checkOutTime && (
                            <p className='m-0 no-wrap-text'>
                                {/* Thời gian:  */}
                                {`${formatDate("HH:mm", checkInTime)} - ${formatDate("HH:mm", checkOutTime)}`}
                            </p>
                        )}
                    </div>
                );
            },
        },

        {
            title: "Người phân ca",
            field: "coordinator",
            width: "18%",
            align: "center",
            render: (row) => (
                <span className='px-2'>
                    {`${row?.coordinator?.displayName || ""} - ${row?.coordinator?.staffCode || ""}`}
                </span>
            ),
        },
    ];

    return (
        <GlobitsPopupV2
            noDialogContent
            popupId={"staffWorkScheduleList"}
            title='Danh sách ca làm việc đã phân'
            size='md'
            open={open}
            scroll='body'
            onClosePopup={handleClose}>
            <Grid container className='p-12'>
                <Grid item xs={12}>
                    <GlobitsTable
                        selection={false}
                        data={listStaffWorkSchedules}
                        columns={columns}
                        totalPages={totalPages}
                        handleChangePage={handleChangePage}
                        setRowsPerPage={setPageSize}
                        pageSize={searchObject?.pageSize}
                        pageSizeOption={[10, 15, 25, 50, 100]}
                        totalElements={totalElements}
                        page={searchObject?.pageIndex || 1}
                    />
                </Grid>
            </Grid>
        </GlobitsPopupV2>
    );
}

export default memo(observer(StaffWorkSchedulePopup));
